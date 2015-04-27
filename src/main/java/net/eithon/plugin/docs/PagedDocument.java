package net.eithon.plugin.docs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;

import net.eithon.library.textwrap.ChatPage;
import net.eithon.library.textwrap.Paginator;

import org.bukkit.ChatColor;
import org.hamcrest.core.IsInstanceOf;

class PagedDocument {
	private ArrayList<ChatPage> _chatPages = null;
	private Stack<String> _colorStack = null;
	private boolean _isBold = false;
	private boolean _isStrikeThrough = false;
	private boolean _isUnderline = false;
	private boolean _isItalic = false;
	private boolean _isMagic = false;
	private File _file;
	private int _widthInPixels = 320;

	public PagedDocument(File file, int widthInPixels) {
		this._file = file;
		reloadRules();
	}

	public int getNumberOfPages(){
		return this._chatPages.size();
	}

	public String[] getPage(int pageNumber){
		if ((pageNumber < 1) || (pageNumber > getNumberOfPages())) return null;
		return this._chatPages.get(pageNumber-1).getLines();
	}

	public void reloadRules() {
		parseFile();
	}

	private void parseFile() {
		String rules = "";
		boolean firstLine = true;
		try {
			FileInputStream fis = new FileInputStream(this._file);

			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			String line = null;
			this._colorStack = new Stack<String>();
			String code = convertToColorCode("grey");
			this._colorStack.push(code);
			while ((line = br.readLine()) != null) {
				String parsedLine = parseLine(line, firstLine);
				if (firstLine) firstLine = false;
				else rules += "\n";
				rules+=parsedLine;
			}
			br.close();
		} catch (IOException e) {
			if (firstLine) firstLine = false;
			else rules += "\n";
			rules += String.format("Failed to read the documentation from \"%s\".", this._file.toString());
		}

		this._chatPages = new ArrayList<ChatPage>();
		ChatPage chatPage = null;
		int i = 1;
		do {
			chatPage = Paginator.paginate(rules, i, "", Character.toString(ChatColor.COLOR_CHAR), this._widthInPixels, 9);
			this._chatPages.add(chatPage);
			i++;
		} while (i <= chatPage.getTotalPages());
	}

	private String parseLine(String line, boolean firstLine) {
		StringTokenizer st = new StringTokenizer(line, "[]", true);
		String newLine = "";
		boolean isCode = false;
		boolean firstToken = true;
		while (st.hasMoreElements()) {
			String token = st.nextToken();
			if (token.equalsIgnoreCase("[")) {
				isCode = true;
				continue;
			} else if (token.equalsIgnoreCase("]")) {
				if (isCode) {
					isCode = false;
					continue;
				}
			}

			if (isCode) {
				if (token.startsWith("color="))
				{
					String color = token.substring(6);
					String code = convertToColorCode(color);
					this._colorStack.push(code);
				} else if (token.equalsIgnoreCase("/color")) {
					if (this._colorStack.size() > 1) {
						this._colorStack.pop();
					}
				} else if (token.equalsIgnoreCase("b")) {
					this._isBold = true;
				} else if (token.equalsIgnoreCase("s")) {
					this._isStrikeThrough = true;
				} else if (token.equalsIgnoreCase("u")) {
					this._isUnderline = true;
				} else if (token.equalsIgnoreCase("i")) {
					this._isItalic = true;
				} else if (token.equalsIgnoreCase("m")) {
					this._isMagic = true;
				} else if (token.equalsIgnoreCase("/b")) {
					this._isBold = false;
				} else if (token.equalsIgnoreCase("/s")) {
					this._isStrikeThrough = false;
				} else if (token.equalsIgnoreCase("/u")) {
					this._isUnderline = false;
				} else if (token.equalsIgnoreCase("/i")) {
					this._isItalic = false;
				} else if (token.equalsIgnoreCase("/m")) {
					this._isMagic = false;
				} else {
					isCode = false;
					token = "[" + token;
				}
			}

			if (firstLine || firstToken || isCode) newLine += activeCodes();
			if (!isCode) newLine += token;
			firstToken = false;
		}

		return newLine;
	}

	private String activeCodes() {
		String activeCodes = this._colorStack.peek();
		if (this._isBold) activeCodes += ChatColor.BOLD;
		if (this._isStrikeThrough) activeCodes += ChatColor.STRIKETHROUGH;
		if (this._isUnderline) activeCodes += ChatColor.UNDERLINE;
		if (this._isItalic) activeCodes += ChatColor.ITALIC;
		if (this._isMagic) activeCodes += ChatColor.MAGIC;
		return activeCodes;
	}

	private static String convertToColorCode(String color) {
		String result = "";
		if (color.equalsIgnoreCase("black"))
		{
			result = ChatColor.BLACK + "";
		}
		else if (color.equalsIgnoreCase("yellow"))
		{
			result = ChatColor.YELLOW + "";
		}
		else if (color.equalsIgnoreCase("blue"))
		{
			result = ChatColor.BLUE + "";
		}
		else if (color.equalsIgnoreCase("green"))
		{
			result = ChatColor.GREEN + "";
		}
		else if (color.equalsIgnoreCase("red"))
		{
			result = ChatColor.RED + "";
		}
		else if (color.equalsIgnoreCase("aqua"))
		{
			result = ChatColor.AQUA + "";
		}
		else if (color.equalsIgnoreCase("darkaqua"))
		{
			result = ChatColor.DARK_AQUA + "";
		}
		else if (color.equalsIgnoreCase("darkblue"))
		{
			result = ChatColor.DARK_BLUE + "";
		}
		else if (color.equalsIgnoreCase("darkgrey"))
		{
			result = ChatColor.DARK_GRAY + "";
		}
		else if (color.equalsIgnoreCase("darkpurple"))
		{
			result = ChatColor.DARK_PURPLE + "";
		}
		else if (color.equalsIgnoreCase("darkgreen"))
		{
			result = ChatColor.DARK_GREEN + "";
		}
		else if (color.equalsIgnoreCase("darkred"))
		{
			result = ChatColor.DARK_RED + "";
		}
		else if (color.equalsIgnoreCase("gold"))
		{
			result = ChatColor.GOLD + "";
		}
		else if (color.equalsIgnoreCase("gray") || color.equalsIgnoreCase("grey"))
		{
			result = ChatColor.GRAY + "";
		}
		else if (color.equalsIgnoreCase("lightpurple"))
		{
			result = ChatColor.LIGHT_PURPLE + "";
		}	
		else if (color.equalsIgnoreCase("white"))
		{
			result = ChatColor.WHITE + "";
		}
		else {
			result = "[Unkown color: " + color + "]";
		}
		return result;
	}

	private static String convertToFontCode(String docsCode) {
		String result = "";
		if (docsCode.equalsIgnoreCase("b"))
		{
			result = ChatColor.BOLD + "";
		}
		else if (docsCode.equalsIgnoreCase("s"))
		{
			result = ChatColor.STRIKETHROUGH + "";
		}
		else if (docsCode.equalsIgnoreCase("u"))
		{
			result = ChatColor.UNDERLINE + "";
		}
		else if (docsCode.equalsIgnoreCase("i"))
		{
			result = ChatColor.ITALIC + "";
		}
		else if (docsCode.equalsIgnoreCase("m"))
		{
			result = ChatColor.MAGIC + "";
		}
		else {
			result = "[Unkown code: " + docsCode + "]";
		}
		return result;
	}
}
