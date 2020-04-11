package me.mrletsplay.crazymaze.game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.mrletsplay.crazymaze.arena.Arena;
import me.mrletsplay.crazymaze.main.Config;

public class Games {

	public static ConcurrentHashMap<Arena, Game> games = new ConcurrentHashMap<Arena, Game>();
	
	public static List<Game> getGames() {
		return new ArrayList<>(games.values());
	}
	
	public static Game getGame(Arena a) {
		return games.get(a);
	}
	
	public static Game getSign(Location l) {
		Arena a = Config.arenas.stream().filter(ar -> ar.getSignLocations().contains(l)).findFirst().orElse(null);
		if(a == null) return null;
		Game g = getGame(a);
		if(g == null) g = newGame(a);
		return g;
	}
	
	public static Game newGame(Arena a) {
		Game g = new Game(a);
		games.put(a, g);
		return g;
	}
	
	public static boolean isInGame(Player p) {
		return getGame(p) != null;
	}
	
	public static Game getGame(Player p) {
		return games.values().stream().filter(g -> g.getPlayers().contains(p)).findFirst().orElse(null);
	}
	
	public static Arena getArena(String arenaName) {
		return Config.arenas.stream()
				.filter(a -> a.getName().equals(arenaName))
				.findFirst().orElse(null);
	}
	
}
