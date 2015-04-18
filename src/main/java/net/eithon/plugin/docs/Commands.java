package net.eithon.plugin.docs;

import java.io.File;
import java.util.HashMap;

import net.eithon.library.extensions.EithonPlayer;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.file.FileMisc;
import net.eithon.library.misc.Debug.DebugPrintLevel;
import net.eithon.library.plugin.ConfigurableMessage;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands {
	private static Commands singleton = null;
	private ConfigurableMessage _pageOf;
	private EithonPlugin _eithonPlugin;
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

	void enable(EithonPlugin plugin){
		this._eithonPlugin = plugin;
		this._docs = new HashMap<String, Doc>();
		this._pageOf = this._eithonPlugin.getConfigurableMessage("PageOfMessage", 2, "Page %d of %d");
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
		EithonPlayer eithonPlayer = new EithonPlayer(player);
		if (command.equals("reload")) {
			if (!eithonPlayer.hasPermissionOrWarn("edocs.reload")) return true;
			Commands.get().reloadCommand(player, args);
		} else {
			if (!eithonPlayer.hasPermissionOrWarn("edocs.read")) return true;
			File file = new File(this._eithonPlugin.getJavaPlugin().getDataFolder(),command + ".txt");
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
		File folder = this._eithonPlugin.getJavaPlugin().getDataFolder();
		String[] nameArray = FileMisc.getFileNames(folder, ".txt");
		String names = String.join("|", nameArray);
		player.sendMessage(String.format("/edocs (%s) [<page>]", names));
	}

	void showFile(Player player, String command, File file, int page)
	{
		if (!file.exists()) {
			showCommandSyntax(player);
			return;
		}

		Doc doc = this._docs.get(command);
		if (doc == null) {
			doc = new Doc(file);
			this._docs.put(command, doc);
		}

		int pageCount = doc.getNumberOfPages();
		if (page > pageCount) page = pageCount;
		String[] pageLines = doc.getPage(page);
		this._pageOf.sendMessage(player, page, pageCount);
		if (this._eithonPlugin.getDebug().shouldDebug(DebugPrintLevel.VERBOSE)) {
			for (String line : pageLines) {
				this._eithonPlugin.getDebug().debug(DebugPrintLevel.VERBOSE, "line: \"%s\"", line);
			}
		}
		player.sendMessage(pageLines);
	}

	public void reloadCommand(Player player, String[] args) {
		for (Doc doc : this._docs.values()) {
			doc.reloadRules();
		}
	}
}
