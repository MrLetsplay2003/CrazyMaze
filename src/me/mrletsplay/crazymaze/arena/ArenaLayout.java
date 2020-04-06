package me.mrletsplay.crazymaze.arena;

import me.mrletsplay.crazymaze.main.MaterialWithData;

public class ArenaLayout {

	private String name, displayName;
	private MaterialWithData icon;
	private MaterialWithData walls, floor, between;
	
	public ArenaLayout(String name, String displayName, MaterialWithData icon, MaterialWithData walls, MaterialWithData floor, MaterialWithData between) {
		this.name = name;
		this.displayName = displayName;
		this.icon = icon;
		this.walls = walls;
		this.floor = floor;
		this.between = between;
	}
	
	public String getName() {
		return name;
	}
	
	public MaterialWithData getWalls() {
		return walls;
	}
	
	public MaterialWithData getFloor() {
		return floor;
	}
	
	public MaterialWithData getBetween() {
		return between;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public MaterialWithData getIcon() {
		return icon;
	}
	
}
