package net.eithon.plugin.docs;

import java.io.File;
import java.util.HashMap;

import net.eithon.library.extensions.EithonPlayer;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.file.FileMisc;
import net.eithon.library.plugin.CommandParser;
import net.eithon.library.plugin.ICommandHandler;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements ICommandHandler {
	private EithonPlugin _eithonPlugin;
	private HashMap<String, PagedDocument> _docs;
	private String _lastReadCommand;
	private int _nextPageNumber;

	public CommandHandler(EithonPlugin plugin){
		this._eithonPlugin = plugin;
		this._docs = new HashMap<String, PagedDocument>();
		this._lastReadCommand = null;
		this._nextPageNumber = 1;
	}

	public boolean onCommand(CommandParser commandParser) {
		EithonPlayer eithonPlayer = commandParser.getEithonPlayerOrInformSender();
		if (eithonPlayer == null) return true;
		if (!commandParser.hasCorrectNumberOfArgumentsOrShowSyntax(1,2)) return true;

		String command = commandParser.getArgumentCommand();

		if (command.equals("reload")) {
			if (!commandParser.hasCorrectNumberOfArgumentsOrShowSyntax(1,1)) return true;
			reloadCommand(eithonPlayer);
		} else if (command.equals("next")) {
			if (!commandParser.hasCorrectNumberOfArgumentsOrShowSyntax(1,1)) return true;
			showPageCommand(eithonPlayer, this._lastReadCommand, this._nextPageNumber++);
		} else {
			int defaultPageNumber = updateCurrentPageNumber(command);
			int pageNumber = commandParser.getArgumentInteger(defaultPageNumber);
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
			doc.reload();
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
		sender.sendMessage(String.format("/edocs reload | next | (%s) [<page>]", names));
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
			int linesOnPage = Config.V.chatBoxHeightInLines;
			if (Config.M.pageFooter.hasContent()) linesOnPage--;
			if (Config.M.pageHeader.hasContent()) linesOnPage--;
			doc = new PagedDocument(file, Config.V.chatBoxWidthInPixels, linesOnPage);
			this._docs.put(command, doc);
		}

		int totalPages = doc.getNumberOfPages();
		if (page > totalPages) {
			page = 1;
			this._nextPageNumber = page+1;
		}
		String title = firstCharacterUpperCase(command);
		
		String[] pageLines = doc.getPage(page);
		HashMap<String,String> namedArguments = new HashMap<String, String>();
		namedArguments.put("TITLE", title);
		namedArguments.put("CURRENT_PAGE", Integer.toString(page));
		namedArguments.put("TOTAL_PAGES", Integer.toString(totalPages));

		if (Config.M.pageHeader.hasContent()) Config.M.pageHeader.sendMessage(player, namedArguments);
		player.sendMessage(pageLines);
		if (Config.M.pageFooter.hasContent()) Config.M.pageFooter.sendMessage(player, namedArguments);
	}

	private String firstCharacterUpperCase(String command) {
		char[] charArray = command.toCharArray();
		charArray[0] = Character.toUpperCase(charArray[0]);
		return new String(charArray);
	}
}
