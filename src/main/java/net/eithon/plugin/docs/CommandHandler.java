package net.eithon.plugin.docs;

import java.io.File;
import java.util.HashMap;

import net.eithon.library.extensions.EithonPlayer;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.file.FileMisc;
import net.eithon.library.plugin.Configuration;
import net.eithon.library.plugin.PluginMisc;
import net.eithon.library.plugin.ConfigurableMessage;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler {
	private ConfigurableMessage _pageOfMessage;
	private EithonPlugin _eithonPlugin;
	private HashMap<String, PagedDocument> _docs;
	private int _chatBoxWidth;
	private String _lastReadCommand;
	private int _currentPageNumber;

	public CommandHandler(EithonPlugin plugin){
		this._eithonPlugin = plugin;
		Configuration configuration = this._eithonPlugin.getConfiguration();
		this._chatBoxWidth = configuration.getInt("ChatBoxWidthInPixels", 320);
		this._pageOfMessage = this._eithonPlugin.getConfigurableMessage("PageOfMessage", 2, "Page %d of %d");
		this._docs = new HashMap<String, PagedDocument>();
		this._lastReadCommand = null;
		this._currentPageNumber = 1;
	}

	boolean onCommand(CommandSender sender, String[] args) {
		if (!PluginMisc.isPlayerOrWarn(sender)) return false;
		Player player = (Player) sender;
		EithonPlayer eithonPlayer = new EithonPlayer(player);
		if (args.length < 1) {
			showCommandSyntax(player);
			return true;
		}

		String command = args[0].toLowerCase();
		if (command.equals("reload")) {
			reloadCommand(eithonPlayer, args);
		} else {
			int page = parsePageNumber(args, 1, command);
			showPageCommand(eithonPlayer, command, page);
		}
		return true;
	}

	private void reloadCommand(EithonPlayer eithonPlayer, String[] args) {
		if (!eithonPlayer.hasPermissionOrWarn("edocs.reload")) return;
		for (PagedDocument doc : this._docs.values()) {
			doc.reloadRules();
		}
		this._lastReadCommand = null;
	}

	private void showPageCommand(EithonPlayer eithonPlayer, String fileName, int page) {
		if (!eithonPlayer.hasPermissionOrWarn("edocs.read")) return;
		File file = new File(this._eithonPlugin.getJavaPlugin().getDataFolder(),fileName + ".txt");
		if (!file.exists()) {
			showCommandSyntax(eithonPlayer);
			return;
		}
		showFile(eithonPlayer, fileName, file, page);
	}

	private int parsePageNumber(String[] args, int index, String fileName) {
		if (!fileName.equalsIgnoreCase(this._lastReadCommand)) {
			this._lastReadCommand = fileName;
			this._currentPageNumber = 1;
		}
		
		int page = this._currentPageNumber;
		if (args.length > index) {
			try { page = Integer.parseInt(args[index]); } catch (Exception ex) {}
		}
		return page;
	}
	
	private void showCommandSyntax(EithonPlayer eithonPlayer) {
		showCommandSyntax(eithonPlayer.getPlayer());
	}

	private void showCommandSyntax(Player player) {
		File folder = this._eithonPlugin.getJavaPlugin().getDataFolder();
		String[] nameArray = FileMisc.getFileNames(folder, ".txt");
		String names = String.join("|", nameArray);
		player.sendMessage(String.format("/edocs (%s) [<page>]", names));
	}

	void showFile(EithonPlayer eithonPlayer, String command, File file, int page)
	{
		if (!file.exists()) {
			showCommandSyntax(eithonPlayer);
			return;
		}

		Player player = eithonPlayer.getPlayer();
		PagedDocument doc = this._docs.get(command);
		if (doc == null) {
			doc = new PagedDocument(file, this._chatBoxWidth);
			this._docs.put(command, doc);
		}

		int pageCount = doc.getNumberOfPages();
		if (page > pageCount) page = pageCount;
		String[] pageLines = doc.getPage(page);
		this._pageOfMessage.sendMessage(player, page, pageCount);
		player.sendMessage(pageLines);
	}
}
