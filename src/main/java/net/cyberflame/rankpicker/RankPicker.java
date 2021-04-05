package net.cyberflame.rankpicker;

import org.bukkit.plugin.java.JavaPlugin;

import net.cyberflame.rankpicker.menu.Item;

public class RankPicker extends JavaPlugin {

	protected Settings settings;
	
	@Override
	public void onEnable() {
		settings = new Settings(this);
		settings.loadConfig();	
		Item.setMenuItems(settings.getMenuItems());
		new PlayerListener(this);
		new Commands(this);
	}
	
	@Override
	public void onDisable() {
		Item.unload();
	}
	
	public Settings getSettings() {
		return settings;
	}
	
}
