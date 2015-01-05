package se.fredsfursten.eithondocs;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Commands {
	private static Commands singleton = null;

	private JavaPlugin plugin = null;

	private Commands() {
	}

	static Commands get()
	{
		if (singleton == null) {
			singleton = new Commands();
		}
		return singleton;
	}

	void enable(JavaPlugin plugin){
		this.plugin = plugin;
	}

	void disable() {
	}

	void rulesCommand(Player player, String[] args)
	{
		if (!verifyPermission(player, "edocs.rules")) return;
		
		ArrayList<String> rules = Rules.get().getRules();
		for (String message : rules) {
			player.sendMessage(message);
		}
	}

	public void reloadCommand(Player player, String[] args) {
		if (!verifyPermission(player, "edocs.reload")) return;
		
		Rules.get().reloadRules();
	}

	void helpCommand(Player player, String[] args)
	{
		if (!verifyPermission(player, "edocs.help")) return;
		
		player.sendMessage("HELP");
	}

	private boolean verifyPermission(Player player, String permission)
	{
		if (player.hasPermission(permission)) return true;
		player.sendMessage("You must have permission " + permission);
		return false;
	}
}
