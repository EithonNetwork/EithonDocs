package net.eithon.plugin.docs;

import net.eithon.library.extensions.EithonPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin {
	
	private CommandHandler _commandHandler = null;

	@Override
	public void onEnable() {
		EithonPlugin eithonPlugin = EithonPlugin.get(this);
		this._commandHandler = new CommandHandler(eithonPlugin);
	}

	@Override
	public void onDisable() {
		this._commandHandler = null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return this._commandHandler.onCommand(sender, args);
	}
}
