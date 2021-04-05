package net.cyberflame.rankpicker.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import net.cyberflame.rankpicker.RankPicker;
import me.clip.placeholderapi.PlaceholderAPI;

public class Menu implements InventoryHolder {
	private Player p;
	private String name;
	private int slots;
		
	public Menu(RankPicker plugin, Player p) {
		this.p = p;
		this.name = PlaceholderAPI.setPlaceholders(p, plugin.getSettings().getMenuName());
		this.slots = plugin.getSettings().getMenuSize();
	}

	@Override
	public Inventory getInventory() {
		if (name.length() > 32) {
			name = name.substring(0, 31);
		}
		
		Inventory inv = Bukkit.createInventory(this, slots, name);
		
		for (Item i : Item.getMenuItems()) {
			org.bukkit.inventory.ItemStack iStack = i.getItemStack(p);
			inv.setItem(i.getSlot(), iStack);
		}
		return inv;
	}
}
