package se.fredsfursten.eithondocs;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import se.fredsfursten.plugintools.Misc;
import se.fredsfursten.plugintools.PluginConfig;

public final class EithonDocsPlugin extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		Misc.enable(this);
		PluginConfig.get(this);
		Commands.get().enable(this);
		Doc.initialize(this);
		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
		Commands.get().disable();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return Commands.get().onCommand(sender, args);
	}
}
