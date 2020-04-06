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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import me.mrletsplay.crazymaze.arena.ArenaLayout;
import me.mrletsplay.crazymaze.game.Game;
import me.mrletsplay.crazymaze.game.GameStage;
import me.mrletsplay.crazymaze.game.Games;
import me.mrletsplay.mrcore.config.CustomConfig;

public class Tools {
	
	private static int layers = 1;
	
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
	
	public static Maze3D setupArena(Game g, ArenaLayout layout, Runnable onFinished, Runnable pFinished) {
		Location loc = getNextSpiralLocation();
		g.arenaLoc = loc;
		Maze3D p = new Maze3D(layout.getFloor().getMaterial(), (byte) layout.getFloor().getData(), layout.getWalls().getMaterial(), (byte) layout.getWalls().getData(), layout.getBetween().getMaterial(), (byte) layout.getBetween().getData());
		g.panel = p;
		new Thread(() -> {
			p.init(loc.getBlockX(), loc.getBlockY()+1, loc.getBlockZ(), g.getArena().getSize(), Config.wallWidth, Config.pathWidth, layers, g.getArena().powerupsEnabled(), onFinished, pFinished, new ArrayList<>());
		}).start();
		return p;
	}
	
	public static void resetArena(Game g, boolean force, Runnable onFinished) {
		if(!force) {
			new Thread(() -> {
				resetRaw(g, force, onFinished);
			}).start();
		}else {
			resetRaw(g, force, onFinished);
		}
	}
	
	private static void resetRaw(Game g, boolean force, Runnable onFinished) {
		if(!force) g.getPanel().tasks.forEach(BukkitTask::cancel);
		if(g.getPanel()!=null) {
			Location loc = g.arenaLoc;
			int sc = Config.wallWidth+Config.pathWidth;
			g.getPanel().i = 0;
			if(!force) {
				for(int x = loc.getBlockX()-Config.wallWidth; x < loc.getBlockX()+g.getArena().getSize()*sc+Config.wallWidth-1; x++) {
					g.getPanel().fill(x, loc.getBlockY(), loc.getBlockZ()-Config.wallWidth,
									  1, layers*(Maze3D.wallHeight+1)+1, g.getArena().getSize()*sc+Config.wallWidth,
							Material.AIR,Config.cmWorld,1,
							(x == loc.getBlockX()+g.getArena().getSize()*sc+Config.wallWidth-2?onFinished:null));
				}
			}else {
				for(int x = loc.getBlockX()-Config.wallWidth; x < loc.getBlockX()+g.getArena().getSize()*sc+Config.wallWidth-1; x++) {
					g.getPanel().fill_r(x, loc.getBlockY(), loc.getBlockZ()-Config.wallWidth,
									  1, layers*(Maze3D.wallHeight+1)+1, g.getArena().getSize()*sc+Config.wallWidth,
							Material.AIR,Config.cmWorld,
							(x == loc.getBlockX()+g.getArena().getSize()*sc+Config.wallWidth-2?onFinished:null));
				}
			}
			Bukkit.getScheduler().runTask(CrazyMaze.pl, () -> {
				for(Entity e : Config.cmWorld.getNearbyEntities(new Location(Config.cmWorld, loc.getBlockX()+g.getArena().getSize()*sc/2, loc.getBlockY(), loc.getBlockZ()+g.getArena().getSize()*sc/2), g.getArena().getSize()*sc/2+1, Maze3D.wallHeight, g.getArena().getSize()*sc/2+1)) {
					e.remove();
				}
			});
		}
	}
	
	public static Location getNextSpiralLocation() {
		int x = 0, y = 0, tX = 0, tY = 0, n = 1, dir = 0;
		boolean a = false;
		int spacing = 1000;
		Location loc;
		while(containsLoc((loc = new Location(Config.cmWorld, x*spacing, Config.MAZE_Y, y*spacing)))) {
			if(x > tX) x--;
			if(x < tX) x++;
			if(y > tY) y--;
			if(y < tY) y++;
			if(x == tX && y == tY) {
				switch(dir) {
					case 0:
						tX+=n;
						break;
					case 1:
						tY+=n;
						break;
					case 2:
						tX-=n;
						break;
					case 3:
						tY-=n;
						break;
				}
				dir++;
				if(dir>3) dir = 0;
				n = a?n+1:n;
				a = !a;
			}
		}
		return loc;
	}
	
	private static boolean containsLoc(Location l) {
		return Games.games.values().stream().anyMatch(g -> !g.getStage().equals(GameStage.WAITING) && g.arenaLoc != null && g.arenaLoc.equals(l));
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
		String[] formats = new String[] {"s", "min", "h"};
		if(ms<60000) {
			return ms/1000+formats[0];
		}else if(ms<60*60000) {
			return ms/60000+formats[1];
		}else {
			return ms/(60*60000)+formats[2];
		}
	}
	
	public static Vector getField(Location l, Game g) {
		int x = (l.getBlockX()-g.arenaLoc.getBlockX())/(g.getPanel().sc);
		int z = (l.getBlockZ()-g.arenaLoc.getBlockZ())/(g.getPanel().sc);
		return new Vector(x, 0, z);
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
	
	public static enum AbsoluteDirection{
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
	
	public static boolean isEmpty(int[] is) {
		for(int i : is) {
			if(i!=0) return false;
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
