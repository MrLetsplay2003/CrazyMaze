package me.mrletsplay.crazymaze.arena;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

import me.mrletsplay.crazymaze.main.Config;
import me.mrletsplay.crazymaze.main.Game;
import me.mrletsplay.crazymaze.main.Games;
import me.mrletsplay.crazymaze.main.Tools;

public class Arena {

	private String name;
	private Location gameLobby, mainLobby, signLocation;
	private int maxPlayers, size, minPlayers;
	private boolean enablePowerups;
	private String onWin;
	private List<ArenaLayout> layouts;
	
	public Arena(String name) {
		this.name = name;
		this.mainLobby = Tools.getLocation(Config.config, "settings.defaultmainlobby");
	}
	
	public Arena(String name, Location gameLobby, Location mainLobby, Location signLocation, int maxPlayers, int size, String onWin, int minPlayers, boolean powerups, List<ArenaLayout> layouts) {
		this.name = name;
		this.gameLobby = gameLobby;
		this.mainLobby = mainLobby;
		this.signLocation = signLocation;
		this.maxPlayers = maxPlayers;
		this.minPlayers = minPlayers;
		this.size = size;
		this.onWin = onWin;
		this.enablePowerups = powerups;
		this.layouts = layouts;
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
	
	public void setSignLocation(Location signLocation) {
		this.signLocation = signLocation;
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
		System.out.println(ls.stream().map(l -> l.getName()).collect(Collectors.joining(",")));
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
	
	public Location getSignLocation() {
		return signLocation;
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
		Game g;
		if((g=Games.getGame(this))!=null) {
			g.updSign();
			return;
		}
		Block b = getSignLocation().getBlock();
		if(!b.getType().equals(Material.SIGN_POST) && !b.getType().equals(Material.WALL_SIGN)) {
			if(b.getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
				b.getRelative(BlockFace.DOWN).setType(Material.STONE);
			}
			b.setType(Material.SIGN_POST);
		}
		Sign s = (Sign)b.getState();
		s.setLine(0, Config.signLayout0);
		s.setLine(1, Config.signLayout1.replace("%name%", getName()).replace("%size%", ""+getSize()));
		String sl2 = powerupsEnabled()?Config.signLayout2p:Config.signLayout2np;
		s.setLine(2, sl2.replace("%pl%", "0").replace("%mpl%", ""+getMaxPlayers()).replace("%npl%", ""+getMinPlayers()));
		s.setLine(3, Config.signLayout3w);
		s.update();
	}
	
	public Arena clone() {
		return new Arena(name, gameLobby, mainLobby, signLocation, maxPlayers, size, onWin, minPlayers, enablePowerups, layouts);
	}
	
}
