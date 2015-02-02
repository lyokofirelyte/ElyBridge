package com.github.lyokofirelyte.ElyBridge;

import org.json.simple.JSONObject;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ElyPortal extends JSONObject {
	
	@Getter
	private final String name;
	
	@Getter @Setter 
	private String world;
	
	@Getter @Setter 
	private String max;
	
	@Getter @Setter 
	private String min;
	
	public boolean compare(String world, int x, int y, int z){
		
		String[] maxSplit = max.split(" ");
		int xMax = Integer.parseInt(maxSplit[0]);
		int yMax = Integer.parseInt(maxSplit[1]);
		int zMax = Integer.parseInt(maxSplit[2]);
		
		String[] minSplit = min.split(" ");
		int xMin = Integer.parseInt(minSplit[0]);
		int yMin = Integer.parseInt(minSplit[1]);
		int zMin = Integer.parseInt(minSplit[2]);
		
		if (world.equals(world)){
			if (x <= xMax && x >= xMin){
				if (y <= yMax && y >= yMin){
					if (z <= zMax && z >= zMin){
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public void finalize(){
		for (Object o : keySet()){
			try {
				getClass().getDeclaredField((String) o).set(this, get(o));
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}