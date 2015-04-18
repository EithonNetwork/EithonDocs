package net.eithon.plugin.eithondocs;

import net.eithon.library.extensions.EithonPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin {

	@Override
	public void onEnable() {
		EithonPlugin eithonPlugin = EithonPlugin.get(this);
		eithonPlugin.enable();
		Commands.get().disable();
		Doc.initialize(eithonPlugin);
		//getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
		Commands.get().disable();
		EithonPlugin eithonPlugin = EithonPlugin.get(this);
		eithonPlugin.disable();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return Commands.get().onCommand(sender, args);
	}
}
