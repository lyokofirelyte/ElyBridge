package com.github.lyokofirelyte.ElyBridge;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class BridgeAPI {

	private ElyBridge main;
	
	@Getter @Setter 
	private Map<String, ElyPortal> portals = new HashMap<String, ElyPortal>();
	private String dir = "./plugins/ElyBridge/portals";
	
	public BridgeAPI(ElyBridge i){
		main = i;
	}
	
	public ElyPortal getPortal(String name){
		return portals.containsKey(name) ? portals.get(name) : null;
	}
	
	@SneakyThrows
	public void load(){
		
		if (Bukkit.getPluginManager().getPlugin("WorldEdit") != null){
			main.setWe((WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit"));
		}
		
		main.setCommandManager(new ElyCommand(main));
		
		File folder = new File(dir);
		folder.mkdirs();
		
		portals = new HashMap<String, ElyPortal>();
		
		for (String file : folder.list()){
			
			 JSONObject obj = (JSONObject) new JSONParser().parse(new FileReader(dir + "/" + file));
			 ElyPortal portal = new ElyPortal(file.replace(".json", ""));
			 obj.remove("name");
			 
			 for (Object o : obj.keySet()){
				 String header = (String) o;
				 try {
					 portal.put(header, obj.get(o));
				 } catch (Exception e){
					 e.printStackTrace();
				 }
			 }
			 
			 portal.finalize();
			 portals.put(portal.getName(), portal);
		}
		
		main.getLogger().log(Level.INFO, portals.size() + " portals loaded!");
	}
	
	@SneakyThrows
	public void save(){
		
		for (ElyPortal portal : portals.values()){
			for (Field field : portal.getClass().getDeclaredFields()){
				field.setAccessible(true);
				try {
					 portal.put(field.getName(), field.get(portal));
				} catch (Exception e){
					e.printStackTrace();
				}
			}
			saveJSON(dir + "/" + portal.getName(), portal);
		}
	}
	
	@SneakyThrows
	private void saveJSON(String name, JSONObject obj){

		FileWriter writer = new FileWriter(name + ".json");
		writer.write(obj.toJSONString());
		writer.flush();
		writer.close();
	}

	public BridgeAPI start(){
		
		load();
		
		for (String cmd : new String[]{ "ely", "sv", "x" }){
			main.getCommand(cmd).setExecutor(main.getCommandManager());
		}
		
		Bukkit.getPluginManager().registerEvents(new ElyMoveEvent(main), main);
		
		return this;
	}

	public void updateServerName(){
		if (Bukkit.getOnlinePlayers().size() > 0){
			try {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("GetServer");
				Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(main, "BungeeCord", out.toByteArray());
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void requestServerList(String player){
		if (Bukkit.getOnlinePlayers().size() > 0){
			try {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("GetServers");
				Bukkit.getPlayer(player).sendPluginMessage(main, "BungeeCord", out.toByteArray());
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void requestIP(String player){
		
		if (Bukkit.getOnlinePlayers().size() > 0){
			try {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("IP");
				Bukkit.getPlayer(player).sendPluginMessage(main, "BungeeCord", out.toByteArray());
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void sendAllToServer(final String server){
		
		final Player holder = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		
		for (Player player : Bukkit.getOnlinePlayers()){
			if (!holder.getName().equals(player.getName())){
				sendToServer(player.getName(), server);
			}
		}
		
		sendToServer(holder.getName(), server);
	}
	
	public void sendToServer(String player, String server){
		
		if (Bukkit.getOnlinePlayers().size() > 0){
			try {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("ConnectOther");
				out.writeUTF(player);
				out.writeUTF(server);
				Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(main, "BungeeCord", out.toByteArray());
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void sendPluginMessageAll(String subchannel, String message){
		sendPluginMessage("ALL", subchannel, message);
	}
	
	public void sendPluginMessage(String server, String subchannel, String message){
		
		if (Bukkit.getOnlinePlayers().size() > 0){
			try {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Forward");
				out.writeUTF(server);
				out.writeUTF(subchannel);
		
				ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
				DataOutputStream msgout = new DataOutputStream(msgbytes);
				msgout.writeUTF(message);
				msgout.writeShort(message.getBytes().length);
		
				out.writeShort(msgbytes.toByteArray().length);
				out.write(msgbytes.toByteArray());
	            Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(main, "BungeeCord", out.toByteArray());
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}