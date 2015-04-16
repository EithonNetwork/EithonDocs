package se.fredsfursten.eithondocs;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import se.fredsfursten.plugintools.ConfigurableFormat;
import se.fredsfursten.plugintools.Misc;
import se.fredsfursten.plugintools.PluginConfig;

public class Commands {
	private static Commands singleton = null;
	private static final String RULES_COMMAND = "/edocs <doc file name> [<page>]";
	private ConfigurableFormat pageOf;
	private JavaPlugin _plugin;
	private HashMap<String, Doc> _docs;

	private Commands() {
	}	

	static Commands get()
	{
		if (singleton == null) {
			singleton = new Commands();
		}
		return singleton;
	}

	void enable(JavaPlugin plugin){
		this._plugin = plugin;
		this._docs = new HashMap<String, Doc>();
		PluginConfig config = PluginConfig.get(plugin);
		this.pageOf = new ConfigurableFormat(config, "PageOfMessage", 2, "Page %d of %d");
	}

	void disable() {
	}

	boolean onCommand(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player!");
			return false;
		}
		
		Player player = (Player) sender;
		if (args.length < 1) {
			showCommandSyntax(player);
			return true;
		}

		String command = args[0].toLowerCase();
		if (command.equals("reload")) {
			Commands.get().reloadCommand(player, args);
		} else {
			File file = new File(this._plugin.getDataFolder(),command + ".txt");
			if (!file.exists()) {
				showCommandSyntax(player);
				return true;
			}
			int page = 1;
			if (args.length > 1) {
				try {
				page = Integer.parseInt(args[1]);
				} catch (Exception ex) {
					showCommandSyntax(player);
					return true;
				}
			}
			showFile(player, command, file, page);
		}
		return true;
	}

	private void showCommandSyntax(Player player) {
		File folder = this._plugin.getDataFolder();
		String[] nameArray = Misc.fileNames(folder, ".txt");
		String names = String.join("|", nameArray);
		player.sendMessage(String.format("/edocs (%s) [<page>]", names));
	}
	
	void showFile(Player player, String command, File file, int page)
	{
		if (!Misc.verifyPermission(player, "edocs.reads")) return;
		if (!file.exists()) {
			showCommandSyntax(player);
			return;
		}
		
		Doc doc = _docs.get(command);
		if (doc == null) {
			doc = new Doc(file);
			this._docs.put(command, doc);
		}

		int pageCount = doc.getNumberOfPages();
		if (page > pageCount) page = pageCount;
		String[] pageLines = doc.getPage(page);
		this.pageOf.sendMessage(player, page, pageCount);
		for (String line : pageLines) {
			Misc.debugInfo("line: \"%s\"", line);
		}
		player.sendMessage(pageLines);
	}

	public void reloadCommand(Player player, String[] args) {
		if (!Misc.verifyPermission(player, "edocs.reload")) return;

		for (Doc doc : this._docs.values()) {
			doc.reloadRules();
		}
	}
}
