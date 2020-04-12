package me.mrletsplay.crazymaze.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.mrletsplay.crazymaze.arena.Arena;
import me.mrletsplay.crazymaze.arena.ArenaLayout;
import me.mrletsplay.crazymaze.generation.BuildTask;
import me.mrletsplay.crazymaze.generation.BuiltArena;
import me.mrletsplay.crazymaze.generation.MazeBuilder;
import me.mrletsplay.crazymaze.generation.MazeBuilderProperties;
import me.mrletsplay.crazymaze.main.Config;
import me.mrletsplay.crazymaze.main.CrazyMaze;
import me.mrletsplay.crazymaze.main.Message;
import me.mrletsplay.crazymaze.main.Powerup;
import me.mrletsplay.crazymaze.main.Tools;
import me.mrletsplay.crazymaze.maze.Maze3D;
import me.mrletsplay.crazymaze.maze.MazeCell;
import me.mrletsplay.crazymaze.maze.MazeGenerator;
import me.mrletsplay.crazymaze.maze.MazeLayer;

public class Game {
	
	private Arena arena;
	private Random r;
	
	private GameStage stage;
	private int countdown;
	private int gameTime;
	private ArenaLayout layout;
	private List<Player> players;
	private BuiltArena builtArena;
	private Map<UUID, VotingData> votes;
	
	private double
		itemBuf = 0,
		dropsPerSecond;
	
	private List<ArenaLayout> layouts;
	
	public Game(Arena arena) {
		this.arena = arena;
		this.stage = GameStage.WAITING;
		this.countdown = -1;
		this.r = new Random();
		this.votes = new HashMap<>();
		this.dropsPerSecond = 1 / (Config.dropInterval / (double) (arena.getSize() * arena.getSize()));
		players = new ArrayList<>();
		if(arena.getLayouts().size() <= 3) {
			this.layouts = new ArrayList<>(arena.getLayouts());
		}else {
			List<ArenaLayout> aLayouts = new ArrayList<>(arena.getLayouts());
			Collections.shuffle(aLayouts);
			this.layouts = aLayouts.subList(0, 3);
		}
	}
	
	public List<ArenaLayout> getLayouts() {
		return layouts;
	}
	
	public void addPlayer(Player p) {
		players.add(p);
		p.teleport(arena.getGameLobby());
		p.setFoodLevel(20);
		p.setHealth(20);
		Tools.clearPlayer(p, true);
		p.getInventory().setItem(0, Config.gameOptions);
		p.getInventory().setItem(8, Config.quitGame);
		for(Player pl : players) {
			pl.sendMessage(Config.getMessage(Message.INGAME_PLAYER_JOINED, "player", p.getName(), "currplayers", ""+players.size(), "maxplayers", ""+arena.getMaxPlayers()));
		}
		if(hasEnoughPlayers()) {
			if(countdown == -1) {
				countdown = 60;
			}
		}
		if(players.size() == arena.getMaxPlayers()) {
			if(countdown > 10) {
				countdown = 10;
			}
		}
		arena.updateSign();
	}
	
	public void tick() {
		if(stage.equals(GameStage.WAITING)) {
			if(countdown > 0) {
				if(countdown % 10 == 0 || countdown <= 5) {
					for(Player p : players) {
						p.sendMessage(Config.getMessage(Message.INGAME_COUNTDOWN_START, "countdown", ""+countdown));
					}
				}
				countdown--;
			}else if(countdown == 0) {
				countdown = -1;
				start();
			}
		}else if(stage.equals(GameStage.RUNNING)){
			if(arena.powerupsEnabled()) {
				itemBuf += dropsPerSecond;
				
				while(itemBuf > 1) {
					ItemStack i = Powerup.values()[r.nextInt(Powerup.values().length)].item;
					
					int
						h = r.nextInt(builtArena.getMaze().getNumLayers()),
						x = r.nextInt(arena.getSize()),
						y = r.nextInt(arena.getSize());
					
					builtArena.getArenaLocation().getWorld().dropItem(Tools.getCellCenter(this, builtArena.getMaze().getLayer(h).getCell(x, y)), i);
					
					itemBuf--;
				}
			}
			
			if(Config.hideTablist) {
				for(Player p : players) {
					for(Player pl : Bukkit.getOnlinePlayers()) {
						if(!players.contains(pl)) {
							p.hidePlayer(CrazyMaze.plugin, pl);
						}else {
							p.showPlayer(CrazyMaze.plugin, pl);
						}
					}
				}
			}
			
			if(gameTime > 0) {
				if(gameTime % (60 * 10) == 0 || (gameTime % 60 == 0 && gameTime <= 5 * 60)) {
					for(Player p : players) {
						p.sendMessage(Config.getMessage(Message.INGAME_COUNTDOWN_END_MINUTES, "countdown", "" + (gameTime / 60)));
					}
				}else if(gameTime < 60 && (gameTime % 10 == 0 || gameTime <= 10)) {
					for(Player p : players) {
						p.sendMessage(Config.getMessage(Message.INGAME_COUNTDOWN_END_SECONDS, "countdown", "" + gameTime));
					}
				}
				gameTime--;
			}else if(gameTime == 0) {
				gameTime = -1;
				stop(false, Config.getMessage(Message.INGAME_END_TIE));
			}
		}
	}
	
	public void removePlayer(Player p) {
		players.remove(p);
		if(Config.hideTablist) {
			for(Player pl : Bukkit.getOnlinePlayers()) {
				p.showPlayer(CrazyMaze.plugin, pl);
			}
		}
		
		votes.remove(p.getUniqueId());
		
		for(Player pl : players) {
			pl.sendMessage(Config.getMessage(Message.INGAME_PLAYER_LEFT, "player", p.getName(), "currplayers", ""+players.size(), "maxplayers", ""+arena.getMaxPlayers()));
		}
		
		Tools.clearPlayer(p, true);
		p.teleport(arena.getMainLobby());
		if(stage.equals(GameStage.WAITING)) {
			boolean c = false;
			if(!hasEnoughPlayers()) {
				countdown = -1;
				c = true;
			}
			if(c) {
				for(Player pl : players) {
					pl.sendMessage(Config.getMessage(Message.INGAME_COUNTDOWN_STOPPED));
				}
			}
		}else if((players.size() <= 1 && arena.getMinPlayers() > 1) || players.size() == 0){
			stop(false, Config.getMessage(Message.INGAME_KICK_EVERYONE_LEFT));
		}
		arena.updateSign();
	}
	
	public List<Player> getPlayers() {
		return players;
	}
	
	public void start() {
		if(!hasEnoughPlayers()) return;
		
		stage = GameStage.GENERATING;
		arena.updateSign();
		
		for(Player p : players) {
			if(p.getOpenInventory()!=null) {
				p.getOpenInventory().close();
			}
			Tools.clearPlayer(p, true);
			p.sendMessage(Config.getMessage(Message.INGAME_ARENA_LOADING_1));
		}
		
		applyLayoutVotes();
		
		Bukkit.getScheduler().runTaskAsynchronously(CrazyMaze.plugin, () -> {
			MazeGenerator generator = new MazeGenerator(0.05);
			
			Maze3D m3d = new Maze3D(arena.getSize(), arena.getSize(), 1);
			MazeLayer layer = m3d.getLayer(0);
			
			MazeCell
				start = layer.getCell(0, 0),
				finish = layer.getCell(layer.getSizeX() - 1, layer.getSizeY() - 1);
			
			generator.populateLayer(layer, start, finish);
			
			Location arenaLocation = Tools.getNextSpiralLocation();
			
			MazeBuilderProperties props = new MazeBuilderProperties(
					2, 1, 3,
					layout.getFloor(), layout.getWalls(), layout.getBetween(),
					finish,
					arena.powerupsEnabled(), Config.fieldChance);
			
			BuildTask buildTask = MazeBuilder.buildLayer(arenaLocation, layer, props);
			buildTask.execute(Config.tasksPerTick, () -> {
				Location pL = Tools.getCellCenter(this, start);
				for(Player p : players) {
					p.teleport(pL);
					p.sendMessage(Config.getMessage(Message.INGAME_COUNTDOWN_GO));
				}
				
				applyTimeVotes();
				
				stage = GameStage.RUNNING;
				arena.updateSign();
			});
			
			for(Player p : players) {
				p.sendMessage(Config.getMessage(Message.INGAME_ARENA_LOADING_2, "time", Tools.formatTime((int) (buildTask.getExpectedTimeTicks() * 50))));
			}
			
			builtArena = new BuiltArena(props, m3d, arenaLocation, buildTask);
		});
	}
	
	private void applyTimeVotes() {
		int[] gTVotes = new int[4];
		for(VotingData v : votes.values()){
			switch(v.gameTime) {
				case 5:
					gTVotes[0]++;
					break;
				case 10:
					gTVotes[1]++;
					break;
				case 20:
					gTVotes[2]++;
					break;
				case -1:
					gTVotes[3]++;
					break;
			}
		}
		
		if(!Tools.isAllZeros(gTVotes)) {
			List<Integer> his = Tools.getHighestIs(gTVotes);
			int hi = his.get(r.nextInt(his.size()));
			switch(hi) {
				case 0:
					gameTime = 5*60;
					break;
				case 1:
					gameTime = 10*60;
					break;
				case 2:
					gameTime = 20*60;
					break;
				case 3:
					gameTime = -1;
					break;
			}
		}else{
			gameTime = -1;
		}
	}
	
	private void applyLayoutVotes() {
		int[] layoutVotes = new int[4];
		for(VotingData v : votes.values()){
			switch(v.mazeLayout) {
				case 1:
					layoutVotes[0]++;
					break;
				case 2:
					layoutVotes[1]++;
					break;
				case 3:
					layoutVotes[2]++;
					break;
				case -1:
					layoutVotes[3]++;
					break;
			}
		}
		if(!Tools.isAllZeros(layoutVotes)) {
			List<Integer> his = Tools.getHighestIs(layoutVotes);
			int hi = his.get(r.nextInt(his.size()));
			switch(hi) {
				case 0:
					layout = layouts.get(0);
					break;
				case 1:
					layout = layouts.get(1);
					break;
				case 2:
					layout = layouts.get(2);
					break;
				case 3:
					layout = layouts.get(CrazyMaze.r.nextInt(arena.getLayouts().size()));
					break;
			}
		}else{
			layout = layouts.get(CrazyMaze.r.nextInt(arena.getLayouts().size()));
		}
	}
	
	public void stop(boolean force, String... message) {
		for(Player p : players) {
			if(!p.isOnline()) continue;
			Tools.clearPlayer(p, true);
			p.teleport(arena.getMainLobby());
			if(Config.hideTablist) {
				for(Player pl : Bukkit.getOnlinePlayers()) {
					p.showPlayer(CrazyMaze.plugin, pl);
				}
			}
			for(String l : message) {
				p.sendMessage(l);
			}
		}
		
		if(stage.equals(GameStage.RUNNING) || stage.equals(GameStage.GENERATING) || (force && !stage.equals(GameStage.WAITING))) {
			if(!force) {
				stage = GameStage.RESETTING;
				if(builtArena != null) builtArena.getBuildTask().cancel();
				Bukkit.getScheduler().runTaskAsynchronously(CrazyMaze.plugin, () -> MazeBuilder.resetMaze(builtArena.getArenaLocation(), builtArena.getMaze(), builtArena.getBuilderProperties()).execute(Config.tasksPerTick, () -> {
					Games.games.remove(this.arena);
					stage = GameStage.WAITING;
					arena.updateSign();
				}));
			}else {
				Config.saveArenaToBeReset(arena, builtArena);
			}
		}else{
			Games.games.remove(this.arena);
		}
		
		players.clear();
		arena.updateSign();
	}
	
	public boolean hasEnoughPlayers() {
		return players.size() > 0 && players.size() >= arena.getMinPlayers();
	}
	
	public Arena getArena() {
		return arena;
	}
	
	public ArenaLayout getLayout() {
		return layout;
	}
	
	public GameStage getStage() {
		return stage;
	}
	
	public int getCountdown() {
		return countdown;
	}
	
	public void setCountdown(int countdown) {
		this.countdown = countdown;
	}
	
	public BuiltArena getBuiltArena() {
		return builtArena;
	}
	
	public VotingData getVotingData(Player p) {
		VotingData d = votes.get(p.getUniqueId());
		if(d == null) d = new VotingData(p);
		return d;
	}
	
	public void setVotingData(Player p, VotingData d) {
		votes.put(p.getUniqueId(), d);
	}
	
	public static class VotingData{
		
		public Player p;
		public int
			gameTime,
			mazeLayout;
		
		public VotingData(Player p) {
			this.p = p;
		}
		
	}
	
}
