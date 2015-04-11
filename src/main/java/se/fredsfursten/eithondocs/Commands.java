package se.fredsfursten.eithondocs;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import se.fredsfursten.plugintools.PluginConfig;

public class Commands {
	private static PluginConfig configuration;
	private static Commands singleton = null;
	private static final String RULES_COMMAND = "/edocs rules [<page>]";
	private static final int ROWS_TO_SHOW = 8;

	private JavaPlugin plugin = null;

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
		this.plugin = plugin;
		configuration = new PluginConfig(plugin, "config.yml");
	}

	void disable() {
	}

	void rulesCommand(Player player, String[] args)
	{
		if (!verifyPermission(player, "edocs.rules")) return;
		if (!arrayLengthIsWithinInterval(args, 1, 2)) {
			player.sendMessage(RULES_COMMAND);
			return;
		}
		
		int pageCount = Rules.get().getNumberOfPages();
		
		int displayPage = 1;
		if (args.length > 1) displayPage = Integer.parseInt(args[1]);
		if (displayPage > pageCount) displayPage = pageCount;
		String[] pageLines = Rules.get().getPage(displayPage);
		player.sendMessage(String.format("Page %d of %d", displayPage, pageCount));
		for (String line : pageLines) {
			configuration.debugInfo("line: \"%s\"", line);
		}
		player.sendMessage(pageLines);
	}

	public void reloadCommand(Player player, String[] args) {
		if (!verifyPermission(player, "edocs.reload")) return;
		
		Rules.get().reloadRules();
	}

	void helpCommand(Player player, String[] args)
	{
		if (!verifyPermission(player, "edocs.help")) return;
		
		player.sendMessage("HELP");
	}

	private boolean verifyPermission(Player player, String permission)
	{
		if (player.hasPermission(permission)) return true;
		player.sendMessage("You must have permission " + permission);
		return false;
	}

	private boolean arrayLengthIsWithinInterval(Object[] args, int min, int max) {
		return (args.length >= min) && (args.length <= max);
	}
}
