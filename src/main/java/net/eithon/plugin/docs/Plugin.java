package net.eithon.plugin.docs;

import net.eithon.library.extensions.EithonPlugin;

public final class Plugin extends EithonPlugin {

	@Override
	public void onEnable() {
		super.enable(new CommandHandler(this), null);
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}
}
