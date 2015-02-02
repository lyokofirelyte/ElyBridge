package com.github.lyokofirelyte.ElyBridge;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ElyMoveEvent implements Listener {

	private ElyBridge main;
	private Map<String, Long> cooldowns = new HashMap<String, Long>();
	
	public ElyMoveEvent(ElyBridge i){
		main = i;
	}
	
	@EventHandler(ignoreCancelled = false)
	public void onMove(PlayerMoveEvent e){
		
		Player p = e.getPlayer();
		String world = p.getWorld().getName();
		Location loc = p.getLocation();
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();

		for (ElyPortal portal : main.getApi().getPortals().values()){
			if (portal.compare(world, x, y, z)){
				if (!cooldowns.containsKey(p.getName())){
					cooldowns.put(p.getName(), 0L);
				}
				if (cooldowns.get(p.getName()) <= System.currentTimeMillis()){
					cooldowns.put(p.getName(), System.currentTimeMillis() + 10000L);
					main.getApi().sendToServer(p.getName(), portal.getName());
				}
				break;
			}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		cooldowns.put(e.getPlayer().getName(), System.currentTimeMillis() + 10000L);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if (cooldowns.containsKey(e.getPlayer().getName())){
			cooldowns.remove(e.getPlayer().getName());
		}
	}
}