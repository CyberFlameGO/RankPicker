package net.cyberflame.rankpicker.menu;

import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.clip.placeholderapi.PlaceholderAPI;

public class Item {
	
	private static Set<Item> menuItems;

	private Material material;
	
	private int amount;
	
	private String displayName;
	
	private List<String> lore;
	
	private ClickHandler clickHandler;
	
	private int slot;
	
	public Item(Material mat, int amount) {
		this.setMaterial(mat);
		this.setAmount(amount);
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public List<String> getLore() {
		return lore;
	}

	public void setLore(List<String> lore) {
		this.lore = lore;
	}

	public ClickHandler getClickHandler() {
		return clickHandler;
	}

	public void setClickHandler(ClickHandler clickHandler) {
		this.clickHandler = clickHandler;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}
	
	public ItemStack getItemStack(Player p) {
		
		ItemStack i = new ItemStack(material, amount);
		ItemMeta meta = i.getItemMeta();
		
		if (displayName != null) {
			meta.setDisplayName(PlaceholderAPI.setPlaceholders(p, displayName));
		}
		
		if (lore != null) {
			meta.setLore(PlaceholderAPI.setPlaceholders(p, lore));
		}
		
		i.setItemMeta(meta);
		return i;
	}

	public static Set<Item> getMenuItems() {
		return menuItems;
	}

	public static void setMenuItems(Set<Item> items) {
		Item.menuItems = items;
	}
	
	//get an item for a specific slot
	public static Item getMenuItem(int slot) {
		if (menuItems == null) {
			return null;
		}
		
		for (Item i : menuItems) {
			if (i.getSlot() == slot) {
				return i;
			}
		}
		return null;
	}
	
	public static void unload() {
		menuItems = null;
	}
}
