package se.fredsfursten.eithondocs;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import se.fredsfursten.plugintools.ConfigurableFormat;
import se.fredsfursten.plugintools.PluginConfig;

public class Commands {
	private static PluginConfig configuration;
	private static Commands singleton = null;
	private static final String RULES_COMMAND = "/edocs rules [<page>]";
	private ConfigurableFormat pageOf;

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
		this.pageOf = new ConfigurableFormat("PageOfMessage", 2, "Page %d of %d");
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
		try {
			if (args.length > 1) displayPage = Integer.parseInt(args[1]);
		} catch (Exception e) {}
		if (displayPage > pageCount) displayPage = pageCount;
		String[] pageLines = Rules.get().getPage(displayPage);
		this.pageOf.sendMessage(player, displayPage, pageCount);
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
