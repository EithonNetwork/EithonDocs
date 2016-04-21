package net.eithon.plugin.docs;

import net.eithon.library.extensions.EithonPlugin;

public final class Plugin extends EithonPlugin {

	@Override
	public void onEnable() {
		super.onEnable();
		Config.load(this);
		CommandHandler commandHandler = new CommandHandler(this);
		super.activate(commandHandler.getCommandSyntax());
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}
}
