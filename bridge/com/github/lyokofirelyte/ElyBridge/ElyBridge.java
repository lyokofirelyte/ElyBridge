package com.github.lyokofirelyte.ElyBridge;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class ElyBridge extends JavaPlugin implements PluginMessageListener {
	
	@Getter @Setter
	private BridgeAPI api;
	
	@Getter @Setter
	private WorldEditPlugin we;
	
	@Getter @Setter
	private ElyCommand commandManager;
	
	@Override
	public void onEnable(){
		
		getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		setApi(new BridgeAPI(this).start());
	}
	
	@Override
	public void onDisable(){
		getApi().save();
	}
	
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message){
		
		if (channel.equals("BungeeCord")){
			
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
		    String subchannel = in.readUTF();
		    
		    switch (subchannel.toLowerCase()){
		    	
		    	case "getservers":
		    		s(player, in.readUTF());
		    	break;
		    	
		    	case "x":
		    		
		    		String msg = in.readUTF();
		    		
		    		for (Player staff : Bukkit.getOnlinePlayers()){
		    			if (staff.hasPermission("ely.staff")){
		    				staff.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4) -X- ( &c" +  msg));
		    			}
		    		}
		    		
		    	break;

		    }
		}
	}
	
    public void s(CommandSender s, String msg){
    	s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3\u2744 &b" + msg));
    }
}