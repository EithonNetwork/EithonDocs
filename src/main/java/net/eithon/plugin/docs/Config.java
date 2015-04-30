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
		public static int chatBoxWidthInPixels;
		public static int chatBoxHeightInLines;

		static void load(Configuration config) {
			chatBoxWidthInPixels = config.getInt("ChatBoxWidthInPixels", 320);
			chatBoxHeightInLines = config.getInt("ChatBoxHeightInLines", 10);
		}
	}
	public static class C {

		static void load(Configuration config) {
		}

	}
	public static class M {
		public static ConfigurableMessage pageHeader;
		public static ConfigurableMessage pageFooter;

		static void load(Configuration config) {
			String[] parameterNames = {"TITLE", "CURRENT_PAGE", "TOTAL_PAGES"};
			pageHeader = config.getConfigurableMessage("messages.PageHeader", 0,
					"-----[%TITLE%]-----", parameterNames);
			pageFooter = config.getConfigurableMessage("messages.PageFooter", 0,
					"Page %CURRENT_PAGE% of %TOTAL_PAGES%", parameterNames);
		}		
	}

}
