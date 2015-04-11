package se.fredsfursten.eithondocs;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import se.fredsfursten.plugintools.ConfigurableFormat;
import se.fredsfursten.plugintools.PluginConfig;

public final class EithonDocsPlugin extends JavaPlugin implements Listener {
	private static PluginConfig configuration;

	@Override
	public void onEnable() {
		if (configuration == null) {
			configuration = new PluginConfig(this, "config.yml");
		} else {
			configuration.load();
		}
		getServer().getPluginManager().registerEvents(this, this);		
		ConfigurableFormat.enable(configuration.getFileConfiguration());
		Commands.get().enable(this);
		Rules.get().enable(this);
	}

	@Override
	public void onDisable() {
		Commands.get().disable();
		Rules.get().disable();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player!");
			return false;
		}
		if (args.length < 1) {
			sender.sendMessage("Incomplete command...");
			return false;
		}

		Player player = (Player) sender;

		String command = args[0].toLowerCase();
		if (command.equals("rules")) {
			Commands.get().rulesCommand(player, args);
		} else if (command.equals("reload")) {
			Commands.get().reloadCommand(player, args);
		} else if (command.equals("help")) {
			Commands.get().helpCommand(player, args);
		} else {
			sender.sendMessage("Could not understand command.");
			return false;
		}
		return true;
	}
}
