package com.github.lyokofirelyte.ElyBridge;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.ChatColor;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class ElyCommand implements CommandExecutor {

	private ElyBridge main;
	
	public ElyCommand(ElyBridge i){
		main = i;
	}
	
	private String[] elyLogo = new String[]{
		"&b. . . . .&f(  &3E  l  y  s  i  a  n &f  )&b. . . . .",	
		"",
		"&7&oA MC Operating System by Hugs",
		"&7&oDivinityAPI not installed - running basics!",
		"&6&o/ely help"
	};
	
	private String[] elyHelp = new String[]{
		"&6Elysian Help Core",
		"&7.................",
		"/ely portal create <server>",
		"  &7> Creates a portal with your WE selection",
		"/ely portal remove <server>",
		"  &7> Removes a named portal",
		"/ely portal info <server>",
		"  &7> Displays information about the portal",
		"/ely portal list",
		"  &7> A list of all defined portals",
		"/ely save",
		"  &7> Saves data to file",
		"/ely load",
		"  &7> Overrides data with data from file"
	};
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	
    	if (sender instanceof Player){
    		
    		Player p = (Player) sender;
    		
    		switch(label.toLowerCase()){
    		
    			case "ely":
    				
    				if (args.length == 0 || !sender.hasPermission("ely.use")){
    					
    					for (String ely : elyLogo){
    						main.s(sender, ely);
    					}
    					
    				} else {
    					
    					switch (args[0]){
    					
    						case "help": case "?":
    							
    							for (String str : elyHelp){
    								main.s(p, str);
    							}
    							
    						break;
    					
	    					case "load":
	    						
	    						main.getApi().load();
	    						main.s(p, "Loaded all files!");
	    						
	    					break;
	    					
	    					case "save":
	    						
	    						main.getApi().save();
	    						main.s(p, "Saved all files!");
	    						
	    					break;
	    					
	    					case "portal":
	    						
	    						if (args.length >= 2){
	    						
		        					switch (args[1]){
		        					
			    						case "create":
			    							
			    							if (args.length == 3){
			    							
				    							if (main.getWe().getSelection(p) != null && main.getWe().getSelection(p) instanceof CuboidSelection){
				    								
				    								Selection sel = main.getWe().getSelection(p);
				    								Vector max = sel.getMaximumPoint().toVector();
				    								Vector min = sel.getMinimumPoint().toVector();
				    								
				    								if (!main.getApi().getPortals().containsKey(args[2])){
				    									ElyPortal portal = new ElyPortal(args[2]);
				    									portal.setWorld(p.getWorld().getName());
				    									portal.setMax(max.getBlockX() + " " + max.getBlockY() + " " + max.getBlockZ());
				    									portal.setMin(min.getBlockX() + " " + min.getBlockY() + " " + min.getBlockZ());
				    									main.getApi().getPortals().put(args[2], portal);
				    									main.s(p, "Added the portal to server &6" + args[2] + "&b!");
				    								} else {
				    									main.s(p, "&c&oThat server already has a portal! :3");
				    								}
				    								
				    							} else {
				    								main.s(p, "&c&oYour WE selection must be cuboid!");
				    							}
				    							
			    							} else {
			    								main.s(p, "&c&o/ely portal create <server>");
			    							}
			    							
			    						break;
			    						
			    						case "remove": case "delete":
			    							
			    							if (args.length == 3){
			    								
			    								if (main.getApi().getPortals().containsKey(args[2])){
			    									main.getApi().getPortals().remove(args[2]);
			    									main.s(p, "Removed the portal to server &6" + args[2] + "&b!");
			    								} else {
			    									main.s(p, "&c&oThat portal does not exist!");
			    								}
			    								
			    							} else {
			    								main.s(p, "&c&o/ely portal remove <server>");
			    							}
			    							
			    						break;
			    						
			    						case "info":
			    							
			    							if (args.length == 3){
			    								
			    								if (main.getApi().getPortals().containsKey(args[2])){
			    									
			    									ElyPortal portal = main.getApi().getPortals().get(args[2]);
			    									
			    									for (String str : new String[]{
			    										"&6Portal Information : " + args[2],
			    										"> World: " + portal.getWorld(),
			    										"> Max Bounds: " + portal.getMax(),
			    										"> Min Bounds: " + portal.getMin(),
			    										"> Server: " + portal.getName()
			    									}){
			    										main.s(p, str);
			    									}
			    									
			    								} else {
			    									main.s(p, "&c&oThat portal does not exist!");
			    								}
			    								
			    							} else {
			    								main.s(p, "&c&o/ely portal info <server>");
			    							}
			    							
			    						break;
			    						
			    						case "list":
			    							
			    							if (main.getApi().getPortals().size() <= 0){
			    								
			    								main.s(p, "&c&oThere are no portals defined.");
			    								
			    							} else {
			    							
				    							String portalList = "";
				    							
				    							for (String portalName : main.getApi().getPortals().keySet()){
				    								portalList += "&6" + portalName + "&b, ";
				    							}
				    							
				    							main.s(p, portalList.substring(0, portalList.length() - 4));
			    							}
				    							
			    						break;
		        					}
		        					
	    						} else {
	    							main.s(sender, "&c&oInvalid syntax! See /ely help");
	    						}
	    						
	    					break;
    					}
    				}
    				
    			break;
    		
    			case "sv":
    				
    				if (args.length == 1 && args[0].equals("list")){
    					main.getApi().requestServerList(p.getName());
    				} else if (args.length == 2 && args[0].equals("all")){
    					main.getApi().sendAllToServer(args[1]);
    				} else if (args.length >= 1){
    					String send = args.length == 1 ? p.getName() : args[1];
    					main.getApi().sendToServer(send, args[0]);
    				} else {
    					main.s(p, "&c&o/sv list, /sv server, /sv server <player>, /sv all <server>");
    				}
    				
    			break;
    			
    			case "x":
    				
    				if (args.length >= 1){
    				
	    				String send = "";
	    				
	    				for (String s : args){
	    					send += s + " ";
	    				}
	    				
	    				main.getApi().sendPluginMessageAll("x", ChatColor.stripColor("&7" + p.getDisplayName() + "&f: &c" + send));
    				
    				} else {
    					main.s(p, "&c&o/x <message>");
    				}
    				
    			break;
    		}
    	} else {
    		main.s(sender, "&c&oSorry console, you can't run this.");
    	}
    	
    	return true;
    }
}
