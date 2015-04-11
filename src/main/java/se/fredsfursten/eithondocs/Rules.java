package se.fredsfursten.eithondocs;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;
import se.fredsfursten.textwrap.ChatPage;
import se.fredsfursten.textwrap.Paginator;

import org.bukkit.ChatColor;

class Rules {
	private static Rules singleton = null;

	private static ArrayList<ChatPage> chatPages = null;
	private static Stack<String> colorStack = null;
	private static boolean isBold = false;
	private static boolean isStrikeThrough = false;
	private static boolean isUnderline = false;
	private static boolean isItalic = false;
	private static boolean isMagic = false;

	static Rules get()
	{ 
		if (singleton == null) {
			singleton = new Rules();
		}
		return singleton;
	}

	public int getNumberOfPages(){
		return chatPages.size();
	}

	public String[] getPage(int pageNumber){
		if ((pageNumber < 1) || (pageNumber > getNumberOfPages())) return null;
		return chatPages.get(pageNumber-1).getLines();
	}

	public void reloadRules() {
		parseFile();
	}

	private Rules(){
		reloadRules();
	}

	private static void parseFile() {
		File fileToParse = new File("plugins" + File.separator +"EithonDocs" + File.separator + "rules.txt");
		String rules = "";
		boolean firstLine = true;
		try {
			FileInputStream fis = new FileInputStream(fileToParse);

			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			String line = null;
			colorStack = new Stack<String>();
			String code = convertToColorCode("grey");
			colorStack.push(code);
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
			rules += String.format("Failed to read the rules from \"%s\".", fileToParse.toString());
		}
		
		chatPages = new ArrayList<ChatPage>();
		ChatPage chatPage = null;
		int i = 1;
		do {
			chatPage = Paginator.paginate(rules, i);
			chatPages.add(chatPage);
			i++;
		} while (i <= chatPage.getTotalPages());
	}

	private static String parseLine(String line, boolean firstLine) {
		StringTokenizer st = new StringTokenizer(line, "[]");
		String newLine = "";
		while (st.hasMoreElements()) {
			String token = st.nextToken();
			boolean isCode = true;
			if (token.startsWith("color="))
			{
				String color = token.substring(6);
				String code = convertToColorCode(color);
				colorStack.push(code);
			} else if (token.equalsIgnoreCase("/color")) {
				if (colorStack.size() > 1) {
					colorStack.pop();
				}
			} else if (token.equalsIgnoreCase("b")) {
				isBold = true;
			} else if (token.equalsIgnoreCase("s")) {
				isStrikeThrough = true;
			} else if (token.equalsIgnoreCase("u")) {
				isUnderline = true;
			} else if (token.equalsIgnoreCase("i")) {
				isItalic = true;
			} else if (token.equalsIgnoreCase("m")) {
				isMagic = true;
			} else if (token.equalsIgnoreCase("/b")) {
				isBold = false;
			} else if (token.equalsIgnoreCase("/s")) {
				isStrikeThrough = false;
			} else if (token.equalsIgnoreCase("/u")) {
				isUnderline = false;
			} else if (token.equalsIgnoreCase("/i")) {
				isItalic = false;
			} else if (token.equalsIgnoreCase("/m")) {
				isMagic = false;
			} else {
				isCode = false;
			}

			if (firstLine || isCode) newLine += activeCodes();
			if (!isCode) newLine += token;
		}

		return newLine;
	}

	private static String activeCodes() {
		String activeCodes = colorStack.peek();
		if (isBold) activeCodes += ChatColor.BOLD;
		if (isStrikeThrough) activeCodes += ChatColor.STRIKETHROUGH;
		if (isUnderline) activeCodes += ChatColor.UNDERLINE;
		if (isItalic) activeCodes += ChatColor.ITALIC;
		if (isMagic) activeCodes += ChatColor.MAGIC;
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
