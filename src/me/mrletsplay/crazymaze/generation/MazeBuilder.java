package me.mrletsplay.crazymaze.generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Entity;

import me.mrletsplay.crazymaze.arena.ArenaGameMode;
import me.mrletsplay.crazymaze.main.Config;
import me.mrletsplay.crazymaze.main.CrazyMaze;
import me.mrletsplay.crazymaze.main.MaterialWithData;
import me.mrletsplay.crazymaze.main.Message;
import me.mrletsplay.crazymaze.main.PowerupField;
import me.mrletsplay.crazymaze.main.Tools;
import me.mrletsplay.crazymaze.maze.Maze3D;
import me.mrletsplay.crazymaze.maze.MazeCell;
import me.mrletsplay.crazymaze.maze.MazeDirection;
import me.mrletsplay.crazymaze.maze.MazeLayer;
import me.mrletsplay.mrcore.bukkitimpl.BlockUtils;
import me.mrletsplay.mrcore.bukkitimpl.versioned.VersionedMaterial;

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
		
		Random r = new Random();
		
		World w = location.getWorld();
		
		int cellSize = properties.getCellSize();
		
		for(int x = 0; x < layer.getSizeX(); x++) {
			for(int y = 0; y < layer.getSizeY(); y++) {
				final int fX = x, fY = y;
				MazeCell c = layer.getCell(x, y);
				tasks.add(() -> {
					MaterialWithData field = properties.getFieldMaterial();
					
					if(properties.isPowerupFields() && r.nextDouble() < properties.getPowerupFieldChance()) {
						field = PowerupField.values()[r.nextInt(PowerupField.values().length)].materialWithData;
					}
					
					if(c.equals(properties.getRedCell())) {
						field = new MaterialWithData(Material.RED_CONCRETE);
						placeStampSign(location, layer, c, properties, ChatColor.RED);
					}
					
					if(c.equals(properties.getBlueCell())) {
						field = new MaterialWithData(Material.BLUE_CONCRETE);
						placeStampSign(location, layer, c, properties, ChatColor.BLUE);
					}
					
					if(c.equals(properties.getGreenCell())) {
						field = new MaterialWithData(Material.GREEN_CONCRETE);
						placeStampSign(location, layer, c, properties, ChatColor.GREEN);
					}
					
					if(c.equals(properties.getYellowCell())) {
						field = new MaterialWithData(Material.YELLOW_CONCRETE);
						placeStampSign(location, layer, c, properties, ChatColor.YELLOW);
					}
					
					fill(w, location.getBlockX() + fX * cellSize, location.getBlockY(), location.getBlockZ() + fY * cellSize, cellSize, 1, cellSize, field);
					
					for(MazeDirection dir : c.getWalls()) {
						buildWall(Tools.getCellLocation(location, properties, c), dir, cellSize, properties.getWallHeight(), properties.getWallMaterial()).run();
					}
					
					fill(w, location.getBlockX() + fX * cellSize, location.getBlockY() + properties.getWallHeight() + 1, location.getBlockZ() + fY * cellSize, cellSize + properties.getWallWidth(), 1, cellSize + properties.getWallWidth(), new MaterialWithData(Material.BARRIER));
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
		
		if(properties.getFinishSignCell() != null) {
			tasks.add(() -> {
				Location loc = Tools.getCellLocation(location, properties, properties.getFinishSignCell()).add(properties.getFieldSize(), 1, properties.getFieldSize());
				BlockUtils.placeBlock(loc, VersionedMaterial.OAK_SIGN_BLOCK);
				
				Sign sign = (Sign) loc.getBlock().getState();
				sign.setLine(0, Config.getMessage(Message.SIGN_FINISH_LINE_1));
				sign.setLine(1, Config.getMessage(Message.SIGN_FINISH_LINE_2));
				sign.update();
				
				Rotatable rot = (Rotatable) loc.getBlock().getBlockData();
				rot.setRotation(BlockFace.NORTH_WEST);
				loc.getBlock().setBlockData(rot);
			});
		}
		
		if(properties.getMode() == ArenaGameMode.FOUR_STAMPS) {
			tasks.add(() -> {
				Location loc = Tools.getCellLocation(location, properties, layer.getCell(0, 0)).add(properties.getWallWidth(), 1, properties.getWallWidth());
				BlockUtils.placeBlock(loc, VersionedMaterial.OAK_SIGN_BLOCK);
				
				Sign sign = (Sign) loc.getBlock().getState();
				sign.setLine(0, Config.getMessage(Message.SIGN_FINISH_LINE_1));
				sign.setLine(1, Config.getMessage(Message.SIGN_FINISH_LINE_2));
				sign.update();
				
				Rotatable rot = (Rotatable) loc.getBlock().getBlockData();
				rot.setRotation(BlockFace.SOUTH_EAST);
				loc.getBlock().setBlockData(rot);
			});
		}
		
		tasks.add(() -> {
			Location loc = Tools.getCellLocation(location, properties, layer.getCell(layer.getSizeX() - 1, 0));
			fill(w, loc.getBlockX(), loc.getBlockY() + properties.getWallHeight() + 1, loc.getBlockZ(), properties.getCellSize() + properties.getWallWidth(), 1, properties.getCellSize() + properties.getWallWidth(), properties.getFieldMaterial());
			
			fillDirectional(w, loc.getBlockX() + properties.getFieldSize(), loc.getBlockY() + 1, loc.getBlockZ() + 1, 1, properties.getWallHeight() + 1, 1, new MaterialWithData(Material.LADDER), BlockFace.WEST);
			fillSquare(w, loc.getBlockX(), loc.getBlockY() + properties.getWallHeight() + 2, loc.getBlockZ(), properties.getCellSize() + properties.getWallWidth(), properties.getCellSize() + properties.getWallWidth(), new MaterialWithData(Material.OAK_FENCE));
			fillSquare(w, loc.getBlockX(), loc.getBlockY() + properties.getWallHeight() + 4, loc.getBlockZ(), properties.getCellSize() + properties.getWallWidth(), properties.getCellSize() + properties.getWallWidth(), new MaterialWithData(Material.BARRIER));
			
			Location loc2 = Tools.getCellLocation(location, properties, layer.getCell(0, layer.getSizeY() - 1));
			fill(w, loc2.getBlockX(), loc2.getBlockY() + properties.getWallHeight() + 1, loc2.getBlockZ(), properties.getCellSize() + properties.getWallWidth(), 1, properties.getCellSize() + properties.getWallWidth(), properties.getFieldMaterial());
			
			fillDirectional(w, loc2.getBlockX() + 1, loc2.getBlockY() + 1, loc2.getBlockZ() + properties.getFieldSize(), 1, properties.getWallHeight() + 1, 1, new MaterialWithData(Material.LADDER), BlockFace.EAST);
			fillSquare(w, loc2.getBlockX(), loc2.getBlockY() + properties.getWallHeight() + 2, loc2.getBlockZ(), properties.getCellSize() + properties.getWallWidth(), properties.getCellSize() + properties.getWallWidth(), new MaterialWithData(Material.OAK_FENCE));
			fillSquare(w, loc2.getBlockX(), loc2.getBlockY() + properties.getWallHeight() + 4, loc2.getBlockZ(), properties.getCellSize() + properties.getWallWidth(), properties.getCellSize() + properties.getWallWidth(), new MaterialWithData(Material.BARRIER));
		});
		
		return new BuildTask(tasks);
	}
	
	public static Runnable buildWall(Location cellLocation, MazeDirection direction, int cellSize, int wallHeight, MaterialWithData wallMaterial) {
		switch(direction) {
			case UP:
				return () -> fill(cellLocation.getWorld(), cellLocation.getBlockX(), cellLocation.getBlockY() + 1, cellLocation.getBlockZ(), cellSize + 1, wallHeight, 1, wallMaterial);
			case DOWN:
				return () -> fill(cellLocation.getWorld(), cellLocation.getBlockX(), cellLocation.getBlockY() + 1, cellLocation.getBlockZ() + cellSize, cellSize + 1, wallHeight, 1, wallMaterial);
			case LEFT:
				return () -> fill(cellLocation.getWorld(), cellLocation.getBlockX(), cellLocation.getBlockY() + 1, cellLocation.getBlockZ(), 1, wallHeight, cellSize + 1, wallMaterial);
			case RIGHT:
				return () -> fill(cellLocation.getWorld(), cellLocation.getBlockX() + cellSize, cellLocation.getBlockY() + 1, cellLocation.getBlockZ(), 1, wallHeight, cellSize + 1, wallMaterial);
		}
		throw new IllegalArgumentException("Invalid direction");
	}
	
	public static Runnable buildInnerWall(Location cellLocation, MazeDirection direction, int fieldSize, int wallWidth, int wallHeight, MaterialWithData wallMaterial) {
		switch(direction) {
			case UP:
				return () -> fill(cellLocation.getWorld(), cellLocation.getBlockX() + 1, cellLocation.getBlockY() + 1, cellLocation.getBlockZ(), fieldSize, wallHeight, 1, wallMaterial);
			case DOWN:
				return () -> fill(cellLocation.getWorld(), cellLocation.getBlockX() + 1, cellLocation.getBlockY() + 1, cellLocation.getBlockZ() + fieldSize + wallWidth, fieldSize, wallHeight, 1, wallMaterial);
			case LEFT:
				return () -> fill(cellLocation.getWorld(), cellLocation.getBlockX(), cellLocation.getBlockY() + 1, cellLocation.getBlockZ() + 1, 1, wallHeight, fieldSize, wallMaterial);
			case RIGHT:
				return () -> fill(cellLocation.getWorld(), cellLocation.getBlockX() + fieldSize + wallWidth, cellLocation.getBlockY() + 1, cellLocation.getBlockZ() + 1, 1, wallHeight, fieldSize, wallMaterial);
		}
		throw new IllegalArgumentException("Invalid direction");
	}
	
	private static void placeStampSign(Location arenaLocation, MazeLayer layer, MazeCell cell, MazeBuilderProperties properties, ChatColor color) {
		MazeDirection d = cell.getWalls().stream().findFirst().orElse(null);
		
		List<MazeDirection> ds = new ArrayList<>(cell.getWalls());
		if(ds.size() == 3) {
			d = Arrays.stream(MazeDirection.values())
					.filter(md -> !ds.contains(md))
					.findFirst().orElse(null).getOpposite();
		}
		
		Location l = Tools.getCellLocation(arenaLocation, properties, cell);
		BlockFace f = BlockFace.NORTH;
		switch(d) {
			case UP:
				l.add(1, 2, 1);
				f = BlockFace.SOUTH;
				break;
			case DOWN:
				l.add(1, 2, properties.getCellSize() - 1);
				f = BlockFace.NORTH;
				break;
			case LEFT:
				l.add(1, 2, 1);
				f = BlockFace.EAST;
				break;
			case RIGHT:
				l.add(properties.getCellSize() - 1, 2, 1);
				f = BlockFace.WEST;
				break;
		}
		
		BlockUtils.placeBlock(l, Material.OAK_WALL_SIGN, (byte) 0);
		
		Sign sign = (Sign) l.getBlock().getState();
		sign.setLine(0, Config.getMessage(Message.valueOf("SIGN_STAMP_" + color.name())));
//		sign.setLine(1, Config.getMessage(Message.SIGN_FINISH_LINE_2));
		sign.update();
		
		Directional rot = (Directional) l.getBlock().getBlockData();
		rot.setFacing(f);
		l.getBlock().setBlockData(rot);
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
	
	private static void fillSquare(World world, int x, int y, int z, int w, int d, MaterialWithData type) {
		for(int cX = x; cX < x + w; cX++) {
			for(int cZ = z; cZ < z + d; cZ++) {
				if(cX != x && cX != x + w - 1 && cZ != z && cZ != z + d - 1) continue;
				BlockUtils.placeBlock(new Location(world, cX, y, cZ), type.getMaterial(), (byte) type.getData());
			}
		}
	}
	
	private static void fillDirectional(World world, int x, int y, int z, int w, int h, int d, MaterialWithData type, BlockFace direction) {
		for(int cX = x; cX < x + w; cX++) {
			for(int cY = y; cY < y + h; cY++) {
				for(int cZ = z; cZ < z + d; cZ++) {
					Location l = new Location(world, cX, cY, cZ);
					BlockUtils.placeBlock(l, type.getMaterial(), (byte) type.getData());
					
					Directional dir = (Directional) l.getBlock().getBlockData();
					dir.setFacing(direction);
					l.getBlock().setBlockData(dir);
				}
			}
		}
	}
	
	public static BuildTask resetMaze(Location location, int mazeLayers, int mazeSizeX, int mazeSizeY, int cellSize, int wallHeight) {
		List<Runnable> tasks = new ArrayList<>();
		
		for(int lI = mazeLayers - 1; lI >= 0; lI--) {
			for(int x = 0; x < mazeSizeX; x++) {
				for(int y = 0; y < mazeSizeY; y++) {
					final int fLI = lI, fX = x, fY = y;
					tasks.add(() -> fill(location.getWorld(), location.getBlockX() + fX * cellSize, location.getBlockY() + fLI * (wallHeight + 1), location.getBlockZ() + fY * cellSize, cellSize + 1, wallHeight + 5, cellSize + 1, new MaterialWithData(Material.AIR)));
				}
			}
		}
		
		tasks.add(() -> Bukkit.getScheduler().runTask(CrazyMaze.plugin, () -> {
			Config.cmWorld.getNearbyEntities(location, cellSize * mazeSizeX, mazeLayers * (wallHeight + 1), cellSize * mazeSizeY)
				.forEach(Entity::remove);
		}));
		
		return new BuildTask(tasks);
	}

	
	public static BuildTask resetMaze(Location location, Maze3D maze, MazeBuilderProperties properties) {
		return resetMaze(location, maze.getNumLayers(), maze.getSizeX(), maze.getSizeY(), properties.getFieldSize() + properties.getWallWidth(), properties.getWallHeight());
	}

}
