package me.mrletsplay.crazymaze.arena;

import org.bukkit.Material;

public class ArenaLayout {

	private String name, displayName;
	private Material iconType;
	private short iconData;
	private Material walls, floor, between;
	private byte wallsD, floorD, betweenD;
	
	public ArenaLayout(String name, String displayName, Material iconType, short iconData, Material walls, byte wallsD, Material floor, byte floorD, Material between, byte betweenD) {
		this.name = name;
		this.displayName = displayName;
		this.iconType = iconType;
		this.iconData = iconData;
		this.walls = walls;
		this.wallsD = wallsD;
		this.floor = floor;
		this.floorD = floorD;
		this.between = between;
		this.betweenD = betweenD;
	}
	
	public String getName() {
		return name;
	}
	
	public Material getWalls() {
		return walls;
	}
	
	public byte getWallsD() {
		return wallsD;
	}
	
	public Material getFloor() {
		return floor;
	}
	
	public byte getFloorD() {
		return floorD;
	}
	
	public Material getBetween() {
		return between;
	}
	
	public byte getBetweenD() {
		return betweenD;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public Material getIconType() {
		return iconType;
	}
	
	public short getIconData() {
		return iconData;
	}
	
}
