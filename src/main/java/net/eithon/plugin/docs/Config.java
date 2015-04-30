package net.eithon.plugin.docs;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.ConfigurableMessage;
import net.eithon.library.plugin.Configuration;

public class Config {
	public static void load(EithonPlugin plugin)
	{
		Configuration config = plugin.getConfiguration();
		V.load(config);
		C.load(config);
		M.load(config);

	}
	public static class V {
		public static int chatBoxWidth;
		public static boolean displayPageOfMessageAbove;

		static void load(Configuration config) {
			chatBoxWidth = config.getInt("ChatBoxWidthInPixels", 320);
			int above = config.getInt("DisplayPageOfMessageAbove", 1);
			displayPageOfMessageAbove = above > 0;
		}
	}
	public static class C {

		static void load(Configuration config) {
		}

	}
	public static class M {
		public static ConfigurableMessage pageOf;

		static void load(Configuration config) {
			pageOf = config.getConfigurableMessage("messages.PageOf", 2,
					"Page %d of %d");
		}		
	}

}
