package me.mrletsplay.crazymaze.arena;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import me.mrletsplay.crazymaze.game.Game;
import me.mrletsplay.crazymaze.game.Games;
import me.mrletsplay.crazymaze.generation.BuildTask;
import me.mrletsplay.crazymaze.main.Config;
import me.mrletsplay.crazymaze.main.CrazyMaze;
import me.mrletsplay.crazymaze.main.Message;

public class Arena {

	private String name;
	private Location gameLobby, mainLobby;
	private List<Location> signLocations;
	private int maxPlayers, size, minPlayers;
	private boolean enablePowerups;
	private String onWin;
	private List<ArenaLayout> layouts;
	private boolean isReady;
	
	public Arena(String name) {
		this.name = name;
		this.mainLobby = Config.config.getLocation("settings.defaultmainlobby");
		this.signLocations = new ArrayList<>();
	}
	
	public Arena(String name, Location gameLobby, Location mainLobby, List<Location> signLocations, int maxPlayers, int size, String onWin, int minPlayers, boolean powerups, List<ArenaLayout> layouts) {
		this.name = name;
		this.gameLobby = gameLobby;
		this.mainLobby = mainLobby;
		this.signLocations = signLocations;
		this.maxPlayers = maxPlayers;
		this.minPlayers = minPlayers;
		this.size = size;
		this.onWin = onWin;
		this.enablePowerups = powerups;
		this.layouts = layouts;
		
		Bukkit.getScheduler().runTaskAsynchronously(CrazyMaze.plugin, () -> {
			System.out.println("HELLO WORLD");
			BuildTask task = Config.resetArenaToBeReset(this);
			System.out.println(task);
			if(task != null) {
				task.execute(Config.tasksPerTick, () -> isReady = true);
			}else {
				isReady = true;
			}
		});
	}
	
	public Arena(String name, Location gameLobby, Location mainLobby, int maxPlayers, int size, String onWin, int minPlayers, boolean powerups, List<ArenaLayout> layouts) {
		this(name, gameLobby, mainLobby, new ArrayList<>(), maxPlayers, size, onWin, minPlayers, powerups, layouts);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setGameLobby(Location gameLobby) {
		this.gameLobby = gameLobby;
	}
	
	public void setMainLobby(Location mainLobby) {
		this.mainLobby = mainLobby;
	}
	
	public void addSignLocation(Location signLocation) {
		signLocations.add(signLocation);
	}
	
	public void removeSignLocation(Location signLocation) {
		signLocations.remove(signLocation);
	}
	
	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public void setOnWin(String onWin) {
		this.onWin = onWin;
	}
	
	public void setMinPlayers(int minPlayers) {
		this.minPlayers = minPlayers;
	}
	
	public void setEnablePowerups(boolean enablePowerups) {
		this.enablePowerups = enablePowerups;
	}
	
	public List<String> setLayouts(List<String> layouts) {
		List<String> notFound = new ArrayList<>();
		List<ArenaLayout> ls = new ArrayList<>();
		for(String n : layouts) {
			ArenaLayout l = Config.arenaLayouts.stream().filter(lo -> lo.getName().equals(n)).findFirst().orElse(null);
			if(l==null) {
				notFound.add(n);
				ls.clear();
			}else if(notFound.isEmpty()) {
				ls.add(l);
			}
		}
		this.layouts = ls;
		return notFound;
	}
	
	public String getName() {
		return name;
	}
	
	public Location getGameLobby() {
		return gameLobby;
	}
	
	public Location getMainLobby() {
		return mainLobby;
	}
	
	public List<Location> getSignLocations() {
		return signLocations;
	}
	
	public int getMaxPlayers() {
		return maxPlayers;
	}
	
	public int getSize() {
		return size;
	}
	
	public String getOnWin() {
		return onWin;
	}
	
	public int getMinPlayers() {
		return minPlayers;
	}
	
	public boolean powerupsEnabled() {
		return enablePowerups;
	}
	
	public List<ArenaLayout> getLayouts() {
		return layouts;
	}
	
	public boolean isReady() {
		return isReady;
	}
	
	public void updateSign() {
		Iterator<Location> lIt = signLocations.iterator();
		while(lIt.hasNext()) {
			Location loc = lIt.next();
			Block b = loc.getBlock();
			if(!(b.getState() instanceof Sign)) {
				lIt.remove();
				continue;
			}
			Sign s = (Sign)b.getState();
			
			Game g = Games.getGame(this);
			
			s.setLine(0, Config.getMessage(Message.SIGN_JOIN_LINE_1));
			s.setLine(1, Config.getMessage(Message.SIGN_JOIN_LINE_2, "name", getName(), "size", "" + getSize()));
			s.setLine(2, Config.getMessage(powerupsEnabled() ? Message.SIGN_JOIN_LINE_3_POWERUPS : Message.SIGN_JOIN_LINE_3_NO_POWERUPS, "players", g == null ? "0" : "" + g.getPlayers().size(), "min-players", "" + getMaxPlayers(), "max-players", "" + getMinPlayers()));
			
			if(g == null) {
				if(isReady) {
					s.setLine(3, Config.getMessage(Message.SIGN_JOIN_LINE_4_WAITING));
				}else {
					s.setLine(3, Config.getMessage(Message.SIGN_JOIN_LINE_4_RESTARTING));
				}
				s.update();
			}else {
				String sl3 = "§c???";
				switch(g.getStage()) {
					case WAITING:
						sl3 = Config.getMessage(Message.SIGN_JOIN_LINE_4_WAITING);
						break;
					case GENERATING:
						sl3 = Config.getMessage(Message.SIGN_JOIN_LINE_4_RUNNING);
						break;
					case RUNNING:
						sl3 = Config.getMessage(Message.SIGN_JOIN_LINE_4_RUNNING);
						break;
					case RESETTING:
						sl3 = Config.getMessage(Message.SIGN_JOIN_LINE_4_RESTARTING);
						break;
				}
				s.setLine(3, sl3);
				s.update();
			}
		}
	}
	
	public Arena clone() {
		Arena newArena = new Arena(name, gameLobby, mainLobby, maxPlayers, size, onWin, minPlayers, enablePowerups, layouts);
		newArena.signLocations.addAll(signLocations);
		return newArena;
	}
	
}
