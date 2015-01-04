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

class Rules {
	private static Rules singleton = null;

	private static ArrayList<String> rules = null;
	private static Stack<String> colorStack = null;

	static Rules get()
	{ 
		if (singleton == null) {
			singleton = new Rules();
		}
		return singleton;
	}

	public ArrayList<String> getRules(){
		return rules;
	}

	private Rules(){
		parseFile();
	}

	private static void parseFile() {
		File fileToParse = new File("plugins" + File.separator +"EithonDocs" + File.separator + "rules.txt");
		rules = new ArrayList<String>();
		try {
			FileInputStream fis = new FileInputStream(fileToParse);

			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			String line = null;
			colorStack = new Stack<String>();
			String code = convertToColorCode("grey");
			colorStack.push(code);
			while ((line = br.readLine()) != null) {
				String parsedLine = parseLine(line);
				rules.add(parsedLine);
			}
			br.close();
		} catch (IOException e) {
			rules.add(String.format("Failed to read the rules from \"%s\".", fileToParse.toString()));
		}
	}

	private static String parseLine(String line) {
		StringTokenizer st = new StringTokenizer(line, "[]");
		String newLine = "";
		while (st.hasMoreElements()) {
			String token = st.nextToken();
			if (token.startsWith("color="))
			{
				String color = token.substring(6);
				String code = convertToColorCode(color);
				colorStack.push(code);
			} else if (token.equalsIgnoreCase("/color")) {
				colorStack.pop();
			} else {
				newLine += colorStack.peek() + token;
			}
		}

		return newLine;
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
}
