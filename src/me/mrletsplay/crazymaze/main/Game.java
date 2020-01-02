package me.mrletsplay.crazymaze.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.mrletsplay.crazymaze.arena.Arena;
import me.mrletsplay.crazymaze.arena.ArenaLayout;

public class Game {
	
	private Arena a;
	private Random r;
	//private boolean running, ended;
	/* 0 = waiting, 1 = generating, 2 = running, 3 = resetting*/
	private int stage;
	private int countdown;
	private int gameTime;
	private ArenaLayout layout;
	private List<Player> players;
	public Location arenaLoc;
	public Maze3D panel;
//	public int t = 0;
	public List<Integer> buildTaskIDs;
	public HashMap<Player, VotingData> votes;
	private double itemBuf = 0, dPS;
	private List<ArenaLayout> layouts;
	
//	private static final Material[][] layouts = new Material[][] {
//			new Material[] {Material.QUARTZ_BLOCK, Material.BRICK, Material.WOOD},
//			new Material[] {Material.BARRIER, Material.BARRIER, Material.BARRIER},
//			new Material[] {Material.GRASS, Material.LEAVES, Material.LOG}
//	};

	public Game(Arena a) {
		this.a = a;
		this.stage = 0;
		this.countdown = -1;
		this.r = new Random();
		this.buildTaskIDs = new ArrayList<>();
		this.votes = new HashMap<>();
		this.dPS = 1/(Config.dropInterval/(double)(a.getSize()*a.getSize()));
		players = new ArrayList<>();
		System.out.println(a.getLayouts().stream().map(l -> l.getName()).collect(Collectors.joining(",")));
		if(a.getLayouts().size()<=3) {
			this.layouts = new ArrayList<>(a.getLayouts());
		}else {
			List<ArenaLayout> aLayouts = new ArrayList<>(a.getLayouts());
			Collections.shuffle(aLayouts);
			this.layouts = aLayouts.subList(0, 3);
		}
	}
	
	public List<ArenaLayout> getLayouts() {
		return layouts;
	}
	
	public void addPlayer(Player p) {
		players.add(p);
		p.teleport(a.getGameLobby());
		p.setFoodLevel(20);
		p.setHealth(20);
		Tools.clearPlayer(p, true);
		p.getInventory().setItem(0, Config.gameOptions);
		p.getInventory().setItem(8, Config.quitGame);
		for(Player pl : players) {
			pl.sendMessage(Config.getMessage("ingame.player-joined", "player", p.getName(), "currplayers", ""+players.size(), "maxplayers", ""+a.getMaxPlayers()));
		}
		if(hasEnoughPlayers()) {
			if(countdown==-1) {
				countdown = 60;
			}
		}
		if(players.size() == a.getMaxPlayers()) {
			if(countdown>10) {
				countdown = 10;
			}
		}
		updSign();
	}
	
	public void tick() {
		if(stage==0) {
			if(countdown>0) {
				if(countdown%10==0 || countdown <= 5) {
					for(Player p : players) {
						p.sendMessage(Config.getMessage("ingame.countdown.start", "countdown", ""+countdown));
					}
				}
				countdown--;
			}else if(countdown==0) {
				countdown = -1;
				start();
			}
		}else if(stage == 2){
			if(a.powerupsEnabled()) {
				itemBuf += dPS;
				while(itemBuf>1) {
					ItemStack i = Powerup.values()[r.nextInt(Powerup.values().length)].item;
					int x = r.nextInt(a.getSize())+1, y = r.nextInt(a.getSize())+1;
					int sc = Config.wallWidth+Config.pathWidth;
					arenaLoc.getWorld().dropItem(new Location(Config.cmWorld, x*sc, Config.MAZE_Y+1, y*sc).add(0.5, 0, 0.5), i);
					itemBuf--;
				}
			}
			
			if(Config.hideTablist) {
				for(Player p : players) {
					for(Player pl : Bukkit.getOnlinePlayers()) {
						if(!players.contains(pl)) {
							p.hidePlayer(pl);
						}else {
							p.showPlayer(pl);
						}
					}
				}
			}
			
			if(gameTime > 0) {
				if(gameTime%(60*10)==0 || (gameTime%60==0 && gameTime<=5*60)) {
					for(Player p : players) {
						p.sendMessage(Config.getMessage("ingame.countdown.end.minutes", "countdown", ""+(gameTime/60)));
					}
				}else if(gameTime < 60 && (gameTime%10==0 || gameTime <= 10)) {
					for(Player p : players) {
						p.sendMessage(Config.getMessage("ingame.countdown.end.seconds", "countdown", ""+gameTime));
					}
				}
				gameTime--;
			}else if(gameTime==0) {
				gameTime=-1;
				stop(false, Config.getMessage("ingame.end.tie"));
			}
		}
	}
	
	public void removePlayer(Player p) {
		players.remove(p);
		if(Config.hideTablist) {
			for(Player pl : Bukkit.getOnlinePlayers()) {
				p.showPlayer(pl);
			}
		}
		votes.remove(p);
		for(Player pl : players) {
			pl.sendMessage(Config.getMessage("ingame.player-left", "player", p.getName(), "currplayers", ""+players.size(), "maxplayers", ""+a.getMaxPlayers()));
		}
		Tools.clearPlayer(p, true);
		p.teleport(a.getMainLobby());
		if(stage==0) {
			boolean c = false;
			if(!hasEnoughPlayers()) {
				countdown = -1;
				c = true;
			}
			if(c) {
				for(Player pl : players) {
					pl.sendMessage(Config.getMessage("ingame.countdown-stopped"));
				}
			}
		}else if((players.size()<=1 && a.getMinPlayers()>1) || players.size()==0){
			stop(false, Config.getMessage("ingame.kick-everyone-left"));
		}
		updSign();
	}
	
	public void updSign() {
		Block b = a.getSignLocation().getBlock();
		if(!b.getType().equals(Material.SIGN_POST) && !b.getType().equals(Material.WALL_SIGN)) {
			if(b.getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
				b.getRelative(BlockFace.DOWN).setType(Material.STONE);
			}
			b.setType(Material.SIGN_POST);
		}
		Sign s = (Sign)a.getSignLocation().getBlock().getState();
		s.setLine(0, Config.signLayout0);
		s.setLine(1, Config.signLayout1.replace("%name%", a.getName()).replace("%size%", ""+a.getSize()));
		String sl2 = a.powerupsEnabled()?Config.signLayout2p:Config.signLayout2np;
		s.setLine(2, sl2.replace("%pl%", ""+getPlayers().size()).replace("%mpl%", ""+a.getMaxPlayers()).replace("%npl%", ""+a.getMinPlayers()));
		String sl3 = "�c???";
		switch(stage) {
			case 0:
				sl3 = Config.signLayout3w;
				break;
			case 1:
				sl3 = Config.signLayout3i;
				break;
			case 2:
				sl3 = Config.signLayout3i;
				break;
			case 3:
				sl3 = Config.signLayout3r;
				break;
		}
		s.setLine(3, sl3);
		s.update();
	}
	
	public List<Player> getPlayers() {
		return players;
	}
	
	public void start() {
		if(!hasEnoughPlayers()) return;
		stage = 1;
		updSign();
		for(Player p : players) {
			if(p.getOpenInventory()!=null) {
				p.getOpenInventory().close();
			}
			Tools.clearPlayer(p, true);
			p.sendMessage(Config.getMessage("ingame.arena-loading.1"));
		}
		applyLayoutVotes();
		this.panel = Tools.setupArena(this, layout, () ->{
			Location pL = arenaLoc.clone().add(new Vector(1, 1, 1));
			for(Player p : players) {
				p.teleport(pL);
				p.sendMessage(Config.getMessage("ingame.countdown.go"));
			}
			applyTimeVotes();
			stage = 2;
			updSign();
		}, () -> {
			for(Player p : players) {
				p.sendMessage(Config.getMessage("ingame.arena-loading.2", "time", Tools.formatTime((int) (panel.i*50))));
			}
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
		if(!Tools.isEmpty(gTVotes)) {
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
		if(!Tools.isEmpty(layoutVotes)) {
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
					layout = layouts.get(Main.r.nextInt(a.getLayouts().size()));
					break;
			}
		}else{
			layout = layouts.get(Main.r.nextInt(a.getLayouts().size()));
		}
	}
	
	public void stop(boolean force, String... message) {
		for(Player p : players) {
			if(!p.isOnline()) continue;
			Tools.clearPlayer(p, true);
			p.teleport(a.getMainLobby());
			if(Config.hideTablist) {
				for(Player pl : Bukkit.getOnlinePlayers()) {
					p.showPlayer(pl);
				}
			}
			for(String l : message) {
				p.sendMessage(l);
			}
		}
		for(Integer tID : buildTaskIDs) {
			Bukkit.getScheduler().cancelTask(tID);
		}
		if(stage == 2 || stage == 1 || (force && stage > 0)) {
			stage = 3;
			Tools.resetArena(this, force, () -> {
				Games.games.remove(this.a);
				stage = 0;
				updSign();
			});
		}else{
			Games.games.remove(this.a);
		}
		players.clear();
		updSign();
	}
	
	public boolean hasEnoughPlayers() {
		return players.size() > 0 && players.size()>=a.getMinPlayers();
	}
	
	public Arena getArena() {
		return a;
	}
	
	public ArenaLayout getLayout() {
		return layout;
	}
	
	public int getStage() {
		return stage;
	}
	
	public int getCountdown() {
		return countdown;
	}
	
	public void setCountdown(int countdown) {
		this.countdown = countdown;
	}
	
	public Maze3D getPanel() {
		return panel;
	}
	
	public VotingData getVotingData(Player p) {
		VotingData d = votes.get(p);
		if(d==null) d = new VotingData(p);
		return d;
	}
	
	public void setVotingData(Player p, VotingData d) {
		votes.put(p, d);
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
