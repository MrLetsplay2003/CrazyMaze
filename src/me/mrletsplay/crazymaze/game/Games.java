package me.mrletsplay.crazymaze.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.mrletsplay.crazymaze.arena.Arena;
import me.mrletsplay.crazymaze.main.Config;

public class Games {

	public static Map<Arena, Game> games = new HashMap<>();
	
	public static synchronized List<Game> getGames() {
		return new ArrayList<>(games.values());
	}
	
	public static synchronized Game getGame(Arena a) {
		return games.get(a);
	}
	
	public static Arena getArenaSign(Location l) {
		return Config.arenas.stream().filter(ar -> ar.getSignLocations().contains(l)).findFirst().orElse(null);
	}
	
	public static Game getGameSign(Location l) {
		Arena a = getArenaSign(l);
		if(a == null) return null;
		Game g = getGame(a);
		if(g == null) g = newGame(a);
		return g;
	}
	
	public static synchronized Game newGame(Arena a) {
		Game g = new Game(a);
		games.put(a, g);
		return g;
	}
	
	public static boolean isInGame(Player p) {
		return getGame(p) != null;
	}
	
	public static synchronized Game getGame(Player p) {
		return games.values().stream()
				.filter(g -> g.getPlayers().contains(p))
				.findFirst().orElse(null);
	}
	
	public static Arena getArena(String arenaName) {
		return Config.arenas.stream()
				.filter(a -> a.getName().equals(arenaName))
				.findFirst().orElse(null);
	}
	
}
