package net.eithon.plugin.docs;

import java.io.File;
import java.util.HashMap;

import net.eithon.library.extensions.EithonPlayer;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.file.FileMisc;
import net.eithon.library.plugin.CommandParser;
import net.eithon.library.plugin.ConfigurableMessage;
import net.eithon.library.plugin.Configuration;
import net.eithon.library.plugin.ICommandHandler;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements ICommandHandler {
	private ConfigurableMessage _pageOfMessage;
	private EithonPlugin _eithonPlugin;
	private HashMap<String, PagedDocument> _docs;
	private int _chatBoxWidth;
	private String _lastReadCommand;
	private int _nextPageNumber;

	public CommandHandler(EithonPlugin plugin){
		this._eithonPlugin = plugin;
		Configuration configuration = this._eithonPlugin.getConfiguration();
		this._chatBoxWidth = configuration.getInt("ChatBoxWidthInPixels", 320);
		this._pageOfMessage = this._eithonPlugin.getConfigurableMessage("PageOfMessage", 2, "Page %d of %d");
		this._docs = new HashMap<String, PagedDocument>();
		this._lastReadCommand = null;
		this._nextPageNumber = 1;
	}

	public boolean onCommand(CommandParser commandParser) {
		EithonPlayer eithonPlayer = commandParser.getEithonPlayerOrInformSender();
		if (eithonPlayer == null) return true;

		if (!commandParser.hasCorrectNumberOfArgumentsOrShowSyntax(1,1)) return true;

		String command = commandParser.getArgumentStringAsLowercase(0);

		if (command.equals("reload")) {
			reloadCommand(eithonPlayer);
		} else {
			int defaultPageNumber = updateCurrentPageNumber(command);
			int pageNumber = commandParser.getArgumentInteger(1, defaultPageNumber);
			this._nextPageNumber = pageNumber+1;
			showPageCommand(eithonPlayer, command, pageNumber);
		}
		return true;
	}

	private int updateCurrentPageNumber(String command) {
		if (!command.equalsIgnoreCase(this._lastReadCommand)) {
			this._lastReadCommand = command;
			this._nextPageNumber = 1;
		}
		return this._nextPageNumber;
	}

	private void reloadCommand(EithonPlayer eithonPlayer) {
		if (!eithonPlayer.hasPermissionOrInformPlayer("edocs.reload")) return;
		for (PagedDocument doc : this._docs.values()) {
			doc.reloadRules();
		}
		this._lastReadCommand = null;
	}

	private void showPageCommand(EithonPlayer eithonPlayer, String fileName, int page) {
		if (!eithonPlayer.hasPermissionOrInformPlayer("edocs.read")) return;
		File file = new File(this._eithonPlugin.getDataFolder(),fileName + ".txt");
		if (!file.exists()) {
			showCommandSyntax(eithonPlayer.getPlayer(), null);
			return;
		}
		showFile(eithonPlayer, fileName, file, page);
	}

	public void showCommandSyntax(CommandSender sender, String command) {
		File folder = this._eithonPlugin.getDataFolder();
		String[] nameArray = FileMisc.getFileNames(folder, ".txt");
		String names = String.join("|", nameArray);
		sender.sendMessage(String.format("/edocs reload|(%s) [<page>]", names));
	}

	void showFile(EithonPlayer eithonPlayer, String command, File file, int page)
	{
		if (!file.exists()) {
			showCommandSyntax(eithonPlayer.getPlayer(), null);
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
