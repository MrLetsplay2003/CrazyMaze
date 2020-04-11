package me.mrletsplay.crazymaze.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import me.mrletsplay.crazymaze.game.Game;
import me.mrletsplay.crazymaze.game.GameStage;
import me.mrletsplay.crazymaze.game.Games;
import me.mrletsplay.crazymaze.generation.BuiltArena;
import me.mrletsplay.crazymaze.generation.MazeBuilderProperties;
import me.mrletsplay.crazymaze.maze.MazeCell;
import me.mrletsplay.mrcore.config.CustomConfig;

public class Tools {
	
	public static void setMaterial(CustomConfig cfg, String path, MaterialWithData materialWithData) {
		String s = materialWithData.getMaterial().name().toLowerCase();
		if(materialWithData.getData() > 0) s += ":" + materialWithData.getData();
		cfg.set(path, s);
	}
	
	public static MaterialWithData loadMaterial(CustomConfig cfg, String path) {
		String md = cfg.getString(path);
		String[] spl = md.split(":");
		if(spl.length > 2) throw new IllegalArgumentException("Invalid material with data");
		Material m = Material.valueOf(spl[0].toUpperCase());
		byte d = 0;
		if(spl.length == 2) d = Byte.parseByte(spl[1]);
		return new MaterialWithData(m, d);
	}
	
	public static Location getNextSpiralLocation() {
		int x = 0, y = 0, tX = 0, tY = 0, n = 1, dir = 0;
		boolean a = false;
		int spacing = 1000;
		Location loc;
		while(containsLoc((loc = new Location(Config.cmWorld, x * spacing, Config.MAZE_Y, y * spacing)))) {
			if(x > tX) x--;
			if(x < tX) x++;
			if(y > tY) y--;
			if(y < tY) y++;
			if(x == tX && y == tY) {
				switch(dir) {
					case 0:
						tX += n;
						break;
					case 1:
						tY += n;
						break;
					case 2:
						tX -= n;
						break;
					case 3:
						tY -= n;
						break;
				}
				dir++;
				if(dir > 3) dir = 0;
				n = a ? n + 1 : n;
				a = !a;
			}
		}
		return loc;
	}
	
	private static boolean containsLoc(Location l) {
		return Games.games.values().stream().anyMatch(g -> !g.getStage().equals(GameStage.WAITING) && g.getBuiltArena() != null && g.getBuiltArena().getArenaLocation().equals(l));
	}
	
	public static void removePotionEffects(Player p) {
		for(PotionEffectType t : p.getActivePotionEffects().stream().map(PotionEffect::getType).collect(Collectors.toList())) {
			p.removePotionEffect(t);
		}
	}
	
	public static void copyWorld(File source, File target){
	    try {
	        ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.lock", "level.dat_old"));
	        if(!ignore.contains(source.getName())) {
	            if(source.isDirectory()) {
	                if(!target.exists())
	                target.mkdirs();
	                String files[] = source.list();
	                for (String file : files) {
	                    File srcFile = new File(source, file);
	                    File destFile = new File(target, file);
	                    copyWorld(srcFile, destFile);
	                }
	            } else {
	                InputStream in = new FileInputStream(source);
	                OutputStream out = new FileOutputStream(target);
	                byte[] buffer = new byte[1024];
	                int length;
	                while ((length = in.read(buffer)) > 0)
	                    out.write(buffer, 0, length);
	                in.close();
	                out.close();
	            }
	        }
	    } catch (IOException e) {
	 
	    }
	}
	
	public static void deleteFolder(File f){
		if(f.exists() && f.isDirectory()){
			for(File fl : f.listFiles()){
				if(fl.isDirectory()){
					deleteFolder(fl);
				}else{
					try{
						fl.deleteOnExit();
						fl.delete();
					}catch(Exception e){}
				}
			}
			try{
				f.deleteOnExit();
				f.delete();
			}catch(Exception e){}
		}else if(f.exists()&&!f.isDirectory()){
			f.delete();
		}
	}
	
	public static String formatTime(int ms) {
		String[] formats = new String[] {" s", " min", " h"};
		if(ms < 60000) {
			return ms / 1000 + formats[0];
		}else if(ms < 60 * 60000) {
			return ms / 60000 + formats[1];
		}else {
			return ms / (60*60000) + formats[2];
		}
	}
	
	public static MazeCell getCell(Game g, Location l) {
		BuiltArena a = g.getBuiltArena();
		MazeBuilderProperties props = a.getBuilderProperties();
		int fieldSize = props.getFieldSize() + props.getWallWidth();

		int h = (l.getBlockY() - a.getArenaLocation().getBlockY()) / (props.getWallHeight() + 1);
		int x = (l.getBlockX() - a.getArenaLocation().getBlockX()) / fieldSize;
		int y = (l.getBlockZ() - a.getArenaLocation().getBlockZ()) / fieldSize;
		
		if(h < 0 || x < 0 || y < 0 || h >= a.getMaze().getNumLayers() || x >= a.getMaze().getSizeX() || y >= a.getMaze().getSizeY()) return null;
		return a.getMaze().getLayer(h).getCell(x, y);
	}
	
	public static Location getCellLocation(Location arenaLocation,  MazeBuilderProperties builderProperties, MazeCell cell) {
		int fieldSize = builderProperties.getFieldSize() + builderProperties.getWallWidth();
		return arenaLocation.clone().add(new Vector(cell.getX() * fieldSize, 0, cell.getY() * fieldSize));
	}
	
	public static Location getCellLocation(Game g, MazeCell cell) {
		BuiltArena a = g.getBuiltArena();
		return getCellLocation(a.getArenaLocation(), a.getBuilderProperties(), cell);
	}
	
	public static Location getCellCenter(Game g, MazeCell cell) {
		BuiltArena a = g.getBuiltArena();
		MazeBuilderProperties props = a.getBuilderProperties();
		
		return getCellLocation(g, cell).add(new Vector(props.getFieldSize() / 2d + 1, 1, props.getFieldSize() / 2d + 1));
	}
	
	public static AbsoluteDirection get(Vector dir) {
		dir = dir.normalize();
		double xDiff = dir.getX(), aXD = Math.abs(xDiff);
		double zDiff = dir.getZ(), aZD = Math.abs(zDiff);
		if(aXD > aZD) {
			return xDiff>0?AbsoluteDirection.X_POS:AbsoluteDirection.X_NEG;
		}else /*if(aZD > aXD && aZD > aYD)*/ {
			return zDiff>0?AbsoluteDirection.Z_POS:AbsoluteDirection.Z_NEG;
		}
	}
	
	public static Vector add(Vector l, AbsoluteDirection dir, int blocks) {
		switch(dir) {
			case X_POS:
				return l.clone().add(new Vector(blocks,0,0));
			case X_NEG:
				return l.clone().add(new Vector(-blocks,0,0));
			case Z_POS:
				return l.clone().add(new Vector(0,0,blocks));
			case Z_NEG:
				return l.clone().add(new Vector(0,0,-blocks));
		}
		return null;
	}
	
	public static enum AbsoluteDirection {
		X_POS("X_NEG"),
		X_NEG("X_POS"),
		Z_POS("Z_NEG"),
		Z_NEG("Z_POS");
		
		private String inverse;
		
		private AbsoluteDirection(String inverse) {
			this.inverse = inverse;
		}
		
		public AbsoluteDirection inverse() {
			return valueOf(inverse);
		}
	}
	
	public static void clearPlayer(Player p, boolean potionEffects) {
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		if(potionEffects) Tools.removePotionEffects(p);
	}
	
	public static List<Integer> getHighestIs(int[] is) {
		List<Integer> hi = new ArrayList<>();
		int hv = -Integer.MAX_VALUE;
		for(int i = 0; i < is.length; i++) {
			if(is[i]>hv) {
				hi.clear();
				hi.add(i);
				hv = is[i];
			}else if(is[i] == hv) {
				hi.add(i);
			}
		}
		return hi;
	}
	
	public static boolean isAllZeros(int[] is) {
		for(int i : is) {
			if(i != 0) return false;
		}
		return true;
	}
	
	public static <T> List<T> getDuplicates(List<T> list){
		List<T> dups = new ArrayList<>();
		List<T> tmp = new ArrayList<>();
		for(T t : list) {
			if(!tmp.contains(t)) {
				tmp.add(t);
			}else {
				if(!dups.contains(t)) dups.add(t);
			}
		}
		return dups;
	}

}
