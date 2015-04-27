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
			doc = new PagedDocument(file, Config.V.chatBoxWidth);
			this._docs.put(command, doc);
		}

		int pageCount = doc.getNumberOfPages();
		if (page > pageCount) {
			page = 1;
			this._nextPageNumber = page+1;
		}
		String[] pageLines = doc.getPage(page);
		Config.M.pageOf.sendMessage(player, page, pageCount);
		player.sendMessage(pageLines);
	}
}
