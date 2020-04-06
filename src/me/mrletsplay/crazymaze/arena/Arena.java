package me.mrletsplay.crazymaze.arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import me.mrletsplay.crazymaze.game.Game;
import me.mrletsplay.crazymaze.game.Games;
import me.mrletsplay.crazymaze.main.Config;

public class Arena {

	private String name;
	private Location gameLobby, mainLobby;
	private List<Location> signLocations;
	private int maxPlayers, size, minPlayers;
	private boolean enablePowerups;
	private String onWin;
	private List<ArenaLayout> layouts;
	
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
	
	public void updSign() {
//		Game g;
//		if((g = Games.getGame(this)) != null) {
//			g.updSign();
//			return;
//		}
		
		for(Location loc : signLocations) {
			Block b = loc.getBlock();
			if(!(b.getState() instanceof Sign)) continue;
			Sign s = (Sign)b.getState();
			
			Game g = Games.getGame(this);
			
			s.setLine(0, Config.signLayout0);
			s.setLine(1, Config.signLayout1.replace("%name%", getName()).replace("%size%", ""+getSize()));
			String sl2 = powerupsEnabled() ? Config.signLayout2p : Config.signLayout2np;
			s.setLine(2, sl2.replace("%pl%", g == null ? "0" : ""+g.getPlayers().size()).replace("%mpl%", ""+getMaxPlayers()).replace("%npl%", ""+getMinPlayers()));
			
			if(g == null) {
				s.setLine(3, Config.signLayout3w);
				s.update();
			}else {
				String sl3 = "§c???";
				switch(g.getStage()) {
					case WAITING:
						sl3 = Config.signLayout3w;
						break;
					case GENERATING:
						sl3 = Config.signLayout3i;
						break;
					case RUNNING:
						sl3 = Config.signLayout3i;
						break;
					case RESETTING:
						sl3 = Config.signLayout3r;
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
