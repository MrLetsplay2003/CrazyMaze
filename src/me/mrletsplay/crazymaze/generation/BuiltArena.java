package me.mrletsplay.crazymaze.generation;

import org.bukkit.Location;

import me.mrletsplay.crazymaze.maze.Maze3D;

public class BuiltArena {

	private MazeBuilderProperties builderProperties;
	private Maze3D maze;
	private Location arenaLocation;

	public BuiltArena(MazeBuilderProperties builderProperties, Maze3D maze, Location arenaLocation) {
		this.builderProperties = builderProperties;
		this.maze = maze;
		this.arenaLocation = arenaLocation;
	}
	
	public MazeBuilderProperties getBuilderProperties() {
		return builderProperties;
	}
	
	public Maze3D getMaze() {
		return maze;
	}
	
	public Location getArenaLocation() {
		return arenaLocation;
	}

}
