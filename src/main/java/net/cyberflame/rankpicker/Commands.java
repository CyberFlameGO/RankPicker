package net.cyberflame.rankpicker;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.cyberflame.rankpicker.menu.Menu;
import net.cyberflame.rankpicker.menu.Item;

public class Commands implements CommandExecutor {

	private RankPicker plugin;
	
	public Commands(RankPicker i) {
		plugin = i;
		plugin.getCommand("rankpicker").setExecutor(this);
	}
	
	private void sms(CommandSender s, String msg) {
		s.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}

	@Override
	public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
		
		if (s instanceof Player) {
			Player p = (Player) s;
			
			if (!p.hasPermission("rankpicker.admin")) {
				sms(s, "&cYou don't have permission to do that!");
				return true;
			}
		}
		
		if (args.length == 0 || args.length > 0 && args[0].equalsIgnoreCase("help")) {
			sms(s, "&bRankPicker &f" + plugin.getDescription().getVersion() + " &bHelp");
			sms(s, "&bcreated by: &cCyberFlame");
			sms(s, "/rankpicker open <player> &8- &7Set a player in the rank picker menu");
			sms(s, "/rankpicker reload &8- &7Reload the plugin");
			return true;
		} else if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("open")) {
			
			if (args.length != 2) {
				sms(s, "&cInvalid arguments! &7/rankpicker open <player>");
				return true;
			}
			
			Player target = Bukkit.getPlayer(args[1]);
			
			if (target == null) {
				sms(s, args[1] + " &cis not online!");
				return true;
			}
			
			if (target.getOpenInventory() != null && target.getOpenInventory().getTopInventory() instanceof Menu) {
				sms(s, target.getName() + " &cis already in the rank picking menu!");
				return true;
			}
			
			target.openInventory(new Menu(plugin, target).getInventory());
			sms(s, target.getName() + " &ahas been set in the rank picking menu!");
			return true;
		} else if (args[0].equalsIgnoreCase("reload")) {
			
			final Set<String> playersInMenu = new HashSet<>();
			
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.getOpenInventory() != null && p.getOpenInventory().getTopInventory() instanceof Menu) {
					playersInMenu.add(p.getName());
					p.closeInventory();
					p.updateInventory();
				}
			}
	
			Item.unload();
			plugin.reloadConfig();
			plugin.saveConfig();	
			Item.setMenuItems(plugin.getSettings().getMenuItems());
			int cLoaded = Item.getMenuItems() != null ? Item.getMenuItems().size() : 0;
			sms(s, "&bRankPicker &fconfiguration successfully reloaded!");
			sms(s, cLoaded + " &7menu item(s) loaded!");
			
			if (playersInMenu != null) {
				for (String p : playersInMenu) {
					Player in = Bukkit.getPlayer(p);
					if (in != null) {
						in.openInventory(new Menu(plugin, in).getInventory());
					}
				}
			}
			return true;
			
		} else {
			sms(s, "&cInvalid command! &fUse &7/rankpicker help");
			return true;
		}
	}
}
