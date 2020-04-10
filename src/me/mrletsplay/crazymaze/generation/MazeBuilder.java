package me.mrletsplay.crazymaze.generation;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import me.mrletsplay.crazymaze.main.MaterialWithData;
import me.mrletsplay.crazymaze.maze.Maze3D;
import me.mrletsplay.crazymaze.maze.MazeCell;
import me.mrletsplay.crazymaze.maze.MazeDirection;
import me.mrletsplay.crazymaze.maze.MazeLayer;
import me.mrletsplay.mrcore.bukkitimpl.BlockUtils;

public class MazeBuilder {
	
	public static BuildTask buildMaze(Location location, Maze3D maze, MazeBuilderProperties properties) {
		List<Runnable> tasks = new ArrayList<>();
		
		for(int lI = 0; lI < maze.getNumLayers(); lI++) {
			buildLayer(new Location(location.getWorld(), location.getBlockX(), location.getBlockY() + lI * (properties.getWallHeight() + 1), location.getBlockZ()), maze.getLayer(lI), properties);
		}
		
		return new BuildTask(tasks);
	}
	
	public static BuildTask buildLayer(Location location, MazeLayer layer, MazeBuilderProperties properties) {
		List<Runnable> tasks = new ArrayList<>();
		
		World w = location.getWorld();
		
		int cellSize = properties.getFieldSize() + properties.getWallWidth();
		
		for(int x = 0; x < layer.getSizeX(); x++) {
			for(int y = 0; y < layer.getSizeY(); y++) {
				final int fX = x, fY = y;
				MazeCell c = layer.getCell(x, y);
				tasks.add(() -> {
					fill(w, location.getBlockX() + fX * cellSize, location.getBlockY(), location.getBlockZ() + fY * cellSize, cellSize, 1, cellSize, properties.getFieldMaterial());
					
					if(c.hasWall(MazeDirection.UP)) {
						fill(w, location.getBlockX() + fX * cellSize, location.getBlockY() + 1, location.getBlockZ() + fY * cellSize, cellSize + 1, properties.getWallHeight(), 1, properties.getWallMaterial());
					}
					
					if(c.hasWall(MazeDirection.DOWN)) {
						fill(w, location.getBlockX() + fX * cellSize, location.getBlockY() + 1, location.getBlockZ() + fY * cellSize + cellSize, cellSize + 1, properties.getWallHeight(), 1, properties.getWallMaterial());
					}
					
					if(c.hasWall(MazeDirection.LEFT)) {
						fill(w, location.getBlockX() + fX * cellSize, location.getBlockY() + 1, location.getBlockZ() + fY * cellSize, 1, properties.getWallHeight(), cellSize + 1, properties.getWallMaterial());
					}
					
					if(c.hasWall(MazeDirection.RIGHT)) {
						fill(w, location.getBlockX() + fX * cellSize + cellSize, location.getBlockY() + 1, location.getBlockZ() + fY * cellSize, 1, properties.getWallHeight(), cellSize + 1, properties.getWallMaterial());
					}
				});
			}
		}
		
		for(int x = 0; x <= layer.getSizeX(); x++) {
			final int fX = x;
			tasks.add(() -> fill(w, location.getBlockX() + fX * cellSize, location.getBlockY(), location.getBlockZ(), 1, 1, layer.getSizeY() * cellSize + 1, properties.getSubWallMaterial()));
		}
		
		for(int y = 0; y <= layer.getSizeX(); y++) {
			final int fY = y;
			tasks.add(() -> fill(w, location.getBlockX(), location.getBlockY(), location.getBlockZ() + fY * cellSize, layer.getSizeX() * cellSize + 1, 1, 1, properties.getSubWallMaterial()));
		}
		
		return new BuildTask(tasks);
	}
	
	private static void fill(World world, int x, int y, int z, int w, int h, int d, MaterialWithData type) {
		for(int cX = x; cX < x + w; cX++) {
			for(int cY = y; cY < y + h; cY++) {
				for(int cZ = z; cZ < z + d; cZ++) {
					BlockUtils.placeBlock(new Location(world, cX, cY, cZ), type.getMaterial(), (byte) type.getData());
				}
			}
		}
	}
	
	public static BuildTask resetMaze(Location location, Maze3D maze, MazeBuilderProperties properties) {
		List<Runnable> tasks = new ArrayList<>();
		
		int cellSize = properties.getFieldSize() + properties.getWallWidth();
		
		for(int lI = maze.getNumLayers() - 1; lI >= 0; lI--) {
			for(int x = 0; x < maze.getSizeX(); x++) {
				for(int y = 0; y < maze.getSizeY(); y++) {
					final int fLI = lI, fX = x, fY = y;
					tasks.add(() -> fill(location.getWorld(), location.getBlockX() + fX * cellSize, location.getBlockY() + fLI * (properties.getWallHeight() + 1), location.getBlockZ() + fY * cellSize, cellSize, properties.getWallHeight() + 1, cellSize, new MaterialWithData(Material.AIR)));
				}
			}
		}
		
		return new BuildTask(tasks);
	}

}
