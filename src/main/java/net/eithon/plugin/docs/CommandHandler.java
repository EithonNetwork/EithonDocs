package net.eithon.plugin.docs;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import net.eithon.library.command.CommandSyntaxException;
import net.eithon.library.command.EithonCommand;
import net.eithon.library.command.EithonCommandUtilities;
import net.eithon.library.command.ICommandSyntax;
import net.eithon.library.extensions.EithonPlayer;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.file.FileMisc;
import net.eithon.library.plugin.Logger.DebugPrintLevel;

import org.bukkit.entity.Player;

public class CommandHandler {
	private EithonPlugin _eithonPlugin;
	private HashMap<String, PagedDocument> _docs;
	private File _textFileFolder;
	private ICommandSyntax _commandSyntax;

	public CommandHandler(EithonPlugin plugin){
		this._eithonPlugin = plugin;
		this._commandSyntax = null;
		this._docs = new HashMap<String, PagedDocument>();
		this._textFileFolder = getTextFilesFolder();
	}

	public ICommandSyntax getCommandSyntax() {
		if (this._commandSyntax != null) return this._commandSyntax;

		ICommandSyntax commandSyntax = EithonCommand.createRootCommand("edocs");
		commandSyntax.setPermissionsAutomatically();

		try {
			commandSyntax.parseCommandSyntax("reload")
			.setCommandExecutor(ec -> reloadCommand(ec));
			commandSyntax.parseCommandSyntax("<document-name> <page-number : INTEGER>")
			.setCommandExecutor(ec -> showPageCommand(ec));
			commandSyntax.getParameterSyntax("document-name")
			.setMandatoryValues(ec -> EithonCommandUtilities.getFileNames(this._textFileFolder, ".txt"));
			commandSyntax.getParameterSyntax("page-number")
			.setMandatoryValues(ec -> getPageRange(ec))
			.setDefault(1);
		} catch (CommandSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		this._commandSyntax = commandSyntax;
		return this._commandSyntax;
	}

	private List<String> getPageRange(EithonCommand command) {
		String documentName = command.getArgument("document-name").asString();
		return EithonCommandUtilities.getRange(1, getNumberOfPages(documentName));
	}

	private int getNumberOfPages(String documentName) {
		PagedDocument document = getDocument(documentName);
		if (document == null) return 1;
		return document.getNumberOfPages();
	}

	private void reloadCommand(EithonCommand command) {
		for (PagedDocument doc : this._docs.values()) {
			doc.reload();
		}
	}

	private void showPageCommand(EithonCommand command) {
		EithonPlayer eithonPlayer = command.getEithonPlayerOrInformSender();
		if (eithonPlayer == null) return;
		if (!eithonPlayer.hasPermissionOrInformPlayer("edocs.read")) return;
		
		String documentName = command.getArgument("document-name").asString();
		
		int page = command.getArgument("page-number").asInteger();
		showFile(eithonPlayer, documentName, page);
	}

	void showFile(EithonPlayer eithonPlayer, String documentName, int page)
	{

		Player player = eithonPlayer.getPlayer();

		PagedDocument doc = getDocument(documentName);

		int totalPages = doc.getNumberOfPages();
		if (page > totalPages) {
			page = 1;
		}
		String title = firstCharacterUpperCase(documentName);
		
		String[] pageLines = doc.getPage(page);
		HashMap<String,String> namedArguments = new HashMap<String, String>();
		namedArguments.put("TITLE", title);
		namedArguments.put("CURRENT_PAGE", Integer.toString(page));
		namedArguments.put("TOTAL_PAGES", Integer.toString(totalPages));

		if (Config.M.pageHeader.hasContent()) Config.M.pageHeader.sendMessage(player, namedArguments);
		player.sendMessage(pageLines);
		if (Config.M.pageFooter.hasContent()) Config.M.pageFooter.sendMessage(player, namedArguments);
	}

	private PagedDocument getDocument(String documentName) {

		File file = new File(this._textFileFolder,documentName + ".txt");
		if (!file.exists()) return null;
		
		PagedDocument doc = this._docs.get(documentName);
		if (doc == null) {
			int linesOnPage = Config.V.chatBoxHeightInLines;
			if (Config.M.pageFooter.hasContent()) linesOnPage--;
			if (Config.M.pageHeader.hasContent()) linesOnPage--;
			doc = new PagedDocument(file, Config.V.chatBoxWidthInPixels, linesOnPage);
			this._docs.put(documentName, doc);
		}
		return doc;
	}

	private String firstCharacterUpperCase(String command) {
		char[] charArray = command.toCharArray();
		charArray[0] = Character.toUpperCase(charArray[0]);
		return new String(charArray);
	}
	
	private File getTextFilesFolder() {
		File dataFolder = this._eithonPlugin.getDataFolder();
		verbose("getDocumentFolder", "dataFolder = %s", dataFolder.getPath());
		File txtFolder = new File(dataFolder, "txt-files");
		verbose("getDocumentFolder", "txtFolder = %s", txtFolder.getPath());
		FileMisc.makeSureParentDirectoryExists(txtFolder);
		if (!txtFolder.exists()) txtFolder.mkdir();
		return txtFolder;
	}
	
	void verbose(String method, String format, Object... args) {
		String message = String.format(format,  args);
		this._eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE, "CommandHandler.%s: %s", method, message);
	}
}
