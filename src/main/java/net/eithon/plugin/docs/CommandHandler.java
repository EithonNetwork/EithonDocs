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

	public CommandHandler(EithonPlugin plugin){
		this._eithonPlugin = plugin;
		this._docs = new HashMap<String, PagedDocument>();
	}

	public boolean onCommand(CommandParser commandParser) {
		EithonPlayer eithonPlayer = commandParser.getEithonPlayerOrInformSender();
		if (eithonPlayer == null) return true;

		String command = commandParser.getArgumentCommand();
		if (command == null) return false;

		if (command.equals("reload")) {
			if (!commandParser.hasCorrectNumberOfArgumentsOrShowSyntax(1,1)) return true;
			reloadCommand(eithonPlayer);
		} else {
			if (!commandParser.hasCorrectNumberOfArgumentsOrShowSyntax(1,2)) return true;
			int pageNumber = commandParser.getArgumentInteger(1);
			showPageCommand(eithonPlayer, command, pageNumber);
		}
		return true;
	}

	private void reloadCommand(EithonPlayer eithonPlayer) {
		if (!eithonPlayer.hasPermissionOrInformPlayer("edocs.reload")) return;
		for (PagedDocument doc : this._docs.values()) {
			doc.reload();
		}
	}

	private void showPageCommand(EithonPlayer eithonPlayer, String fileName, int page) {
		if (!eithonPlayer.hasPermissionOrInformPlayer("edocs.read")) return;
		File file = new File(getDocumentFolder(),fileName + ".txt");
		if (!file.exists()) {
			showCommandSyntax(eithonPlayer.getPlayer(), null);
			return;
		}
		showFile(eithonPlayer, fileName, file, page);
	}

	public void showCommandSyntax(CommandSender sender, String command) {
		String[] nameArray = FileMisc.getFileNames(getDocumentFolder(), ".txt");
		String names = String.join("|", nameArray);
		sender.sendMessage(String.format("/edocs reload | (%s) [<page>]", names));
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
	
	private File getDocumentFolder() {
		File dataFolder = this._eithonPlugin.getDataFolder();
		File folder = new File(dataFolder, "txt-files");
		FileMisc.makeSureDirectoriesExists(folder);
		return folder;
	}
}
