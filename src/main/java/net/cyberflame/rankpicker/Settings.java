package net.cyberflame.rankpicker;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.cyberflame.rankpicker.menu.ClickHandler;
import net.cyberflame.rankpicker.menu.Item;


public class Settings {

	private RankPicker plugin;
	
	public Settings(RankPicker i) {
		plugin = i;
	}
	
	public void loadConfig() {
		
		FileConfiguration c = plugin.getConfig();
		
		c.options().header("RankPicker" + plugin.getDescription().getVersion() + " main configuration file"
				+ "\n"
				+ "\nmenu_name max length is 32 characters!"
				+ "\n"
				+ "\nmenu_size must be a number in multiples of 9, max size of an inventory is 54"
				+ "\n"
				+ "\nMenu slots start at 0 for the first slot!"
				+ "\n"
				+ "\nYou can not specify a slot greater than the menu_size-1 as the slot does not exist"
				+ "\n"
				+ "\nEvery menu_item must have a unique identifier otherwise duplicates will not be loaded!"
				+ "\n"
				+ "\nEach menu_item must contain a valid material name and slot for it to be loaded!"
				+ "\nany additional option specified is optional"
				+ "\nmenu_items format:"
				+ "\n"
				+ "\nmenu_items:"
				+ "\n  <item identifier>:"
				+ "\n    material: <Material name>"
				+ "\n    amount: <int amount to display>"
				+ "\n    slot: <int slot to display the item in>"
				+ "\n    display_name: <display name, color codes can be used>"
				+ "\n    lore:"
				+ "\n    - 'lore line 1'"
				+ "\n    - 'lore line 2'"
				+ "\n    click_commands:"
				+ "\n    - 'command 1 with no /'"
				+ "\n    - 'spawn %player_name%'"
				+ "\n"
				+ "\nYou can use any PlaceholderAPI placeholder in any String - display_name, lore, or click_commands"
				+ "\n"
				+ "\nYou must grant the permission node rankpicker.close in the click commands or the player will never be able to close the menu!"
				);
		
		c.addDefault("menu_name", "&aPick a rank");
		
		c.addDefault("menu_size", 54);

		
		if (!c.contains("menu_items")) {
			
			c.set("menu_items.1.material", "DIAMOND_BLOCK");
			c.set("menu_items.1.amount", 1);
			c.set("menu_items.1.slot", 1);
			c.set("menu_items.1.display_name", "&aPick a rank!");
			c.set("menu_items.1.lore", Arrays.asList(new String[] {
					"Hey %player_name%, before you start playing", "you need to pick a rank!"
			}));
			c.set("menu_items.1.click_commands", Arrays.asList(new String[] {
					"spawn %player_name%"
			}));
		}

		
		c.options().copyDefaults(true);
		plugin.saveConfig();
		plugin.reloadConfig();
	}
	
	public String getMenuName() {
		return plugin.getConfig().getString("menu_name");
	}
	
	public int getMenuSize() {
		int size = plugin.getConfig().getInt("menu_size");
		
		int remainder = size % 9;
		
		if (remainder > 0) {
			size = size + remainder;
		}
		
		return size <= 54 ? size : 54;
	}
	
	public Set<Item> getMenuItems() {
		
		FileConfiguration c = plugin.getConfig();
		
		if (!c.contains("menu_items")) {
			return null;	
		}
		
		if (!c.isConfigurationSection("menu_items")) {
			return null;
		}
		
		Set<String> keys = c.getConfigurationSection("menu_items").getKeys(false);
		
		if (keys == null || keys.isEmpty()) {
			return null;
		}
		
		Set<Item> items = new HashSet<Item>();
		
		for (String key : keys) {
			
			String pre = "menu_items." + key + ".";
			
			if (!c.contains(pre + "material")) {
				plugin.getLogger().warning("Material for menu item: " + key + " is not present!");
				plugin.getLogger().warning("Skipping menu item: " + key);
				continue;
			}
			
			Material m = Material.getMaterial(c.getString(pre + "material").toUpperCase());
			
			if (m == null) {
				plugin.getLogger().warning("Material for menu item: " + key + " is not a valid material!");
				plugin.getLogger().warning("Skipping menu item: " + key);
				continue;
			}
			
			int slot = 1;
			
			if (!c.contains(pre + "slot")) {
				plugin.getLogger().warning("Slot for menu item: " + key + " is not defined!");
				plugin.getLogger().warning("Skipping menu item: " + key);
				continue;
			}
			
			slot = c.getInt(pre + "slot");
			
			if (slot > getMenuSize()-1) {
				plugin.getLogger().warning("Slot for menu item: " + key + " is greater than the inventory size!");
				plugin.getLogger().warning("Skipping menu item: " + key);
				continue;
			}
			
			boolean valid = true;
			
			if (!items.isEmpty()) {
				for (Item i : items) {
					if (i.getSlot() == slot) {
						valid = false;
						break;
					}
				}
			}
			
			if (!valid) {
				plugin.getLogger().warning("Slot for menu item: " + key + " is already taken!");
				plugin.getLogger().warning("Skipping menu item: " + key);
				continue;
			}
			
			int amount = 1;
			
			if (c.contains(pre + "amount")) {
				amount = (short) c.getInt(pre + "amount");
			}
			
			String name = null;
			
			if (c.contains(pre + "display_name")) {
				name = c.getString(pre + "display_name");
			}
			
			List<String> lore = null;
			
			if (c.contains(pre + "lore")) {
				lore = c.getStringList(pre + "lore");
			}
			
			ClickHandler handler = null;
			
			if (c.contains(pre + "click_commands")) {
				
				final List<String> commands = c.getStringList(pre + "click_commands");
				
				handler = new ClickHandler() {

					@Override
					public void onClick(Player p) {
						for (String cmd : commands) {
							if (cmd.equalsIgnoreCase("[close]")) {
								p.closeInventory();
								p.updateInventory();
							}
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(p, cmd));
						}
					}
				};
			}
			
			Item i = new Item(m, amount);
			i.setSlot(slot);
			i.setDisplayName(name);
			i.setLore(lore);
			i.setClickHandler(handler);
			items.add(i);
		}
		
		return items;
	}
}
