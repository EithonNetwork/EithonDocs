package se.fredsfursten.eithondocs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import se.fredsfursten.plugintools.PluginConfig;
import se.fredsfursten.textwrap.ChatPage;
import se.fredsfursten.textwrap.Paginator;

class Doc {
	private ArrayList<ChatPage> chatPages = null;
	private Stack<String> colorStack = null;
	private boolean isBold = false;
	private boolean isStrikeThrough = false;
	private boolean isUnderline = false;
	private boolean isItalic = false;
	private boolean isMagic = false;
	private File file;
	private static int chatBoxWidth = 320;
	private static JavaPlugin _plugin;

	public static void initialize(JavaPlugin plugin) {
		_plugin = plugin;
		chatBoxWidth = PluginConfig.get(_plugin).getInt("ChatBoxWidth", 320);
	}
	
	public Doc(File file) {
		this.file = file;
		reloadRules();
	}
	public int getNumberOfPages(){
		return this.chatPages.size();
	}

	public String[] getPage(int pageNumber){
		if ((pageNumber < 1) || (pageNumber > getNumberOfPages())) return null;
		return this.chatPages.get(pageNumber-1).getLines();
	}

	public void reloadRules() {
		parseFile();
	}
	
	private void parseFile() {
		String rules = "";
		boolean firstLine = true;
		try {
			FileInputStream fis = new FileInputStream(this.file);

			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			String line = null;
			this.colorStack = new Stack<String>();
			String code = convertToColorCode("grey");
			this.colorStack.push(code);
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
			rules += String.format("Failed to read the rules from \"%s\".", this.file.toString());
		}
		
		this.chatPages = new ArrayList<ChatPage>();
		ChatPage chatPage = null;
		int i = 1;
		do {
			chatPage = Paginator.paginate(rules, i, "", Character.toString(ChatColor.COLOR_CHAR), chatBoxWidth, 9);
			this.chatPages.add(chatPage);
			i++;
		} while (i <= chatPage.getTotalPages());
	}

	private String parseLine(String line, boolean firstLine) {
		StringTokenizer st = new StringTokenizer(line, "[]");
		String newLine = "";
		boolean firstToken = true;
		while (st.hasMoreElements()) {
			String token = st.nextToken();
			boolean isCode = true;
			if (token.startsWith("color="))
			{
				String color = token.substring(6);
				String code = convertToColorCode(color);
				this.colorStack.push(code);
			} else if (token.equalsIgnoreCase("/color")) {
				if (this.colorStack.size() > 1) {
					this.colorStack.pop();
				}
			} else if (token.equalsIgnoreCase("b")) {
				this.isBold = true;
			} else if (token.equalsIgnoreCase("s")) {
				this.isStrikeThrough = true;
			} else if (token.equalsIgnoreCase("u")) {
				this.isUnderline = true;
			} else if (token.equalsIgnoreCase("i")) {
				this.isItalic = true;
			} else if (token.equalsIgnoreCase("m")) {
				this.isMagic = true;
			} else if (token.equalsIgnoreCase("/b")) {
				this.isBold = false;
			} else if (token.equalsIgnoreCase("/s")) {
				this.isStrikeThrough = false;
			} else if (token.equalsIgnoreCase("/u")) {
				this.isUnderline = false;
			} else if (token.equalsIgnoreCase("/i")) {
				this.isItalic = false;
			} else if (token.equalsIgnoreCase("/m")) {
				this.isMagic = false;
			} else {
				isCode = false;
			}

			if (firstLine || firstToken || isCode) newLine += activeCodes();
			if (!isCode) newLine += token;
			firstToken = false;
		}

		return newLine;
	}

	private String activeCodes() {
		String activeCodes = this.colorStack.peek();
		if (this.isBold) activeCodes += ChatColor.BOLD;
		if (this.isStrikeThrough) activeCodes += ChatColor.STRIKETHROUGH;
		if (this.isUnderline) activeCodes += ChatColor.UNDERLINE;
		if (this.isItalic) activeCodes += ChatColor.ITALIC;
		if (this.isMagic) activeCodes += ChatColor.MAGIC;
		return activeCodes;
	}

	private static String convertToColorCode(String color) {
		String result = "";
		if (color.equalsIgnoreCase("black"))
		{
			result = ChatColor.BLACK + "";
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
