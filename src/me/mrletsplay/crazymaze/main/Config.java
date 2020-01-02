package me.mrletsplay.crazymaze.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.inventory.ItemStack;

import me.mrletsplay.crazymaze.arena.Arena;
import me.mrletsplay.crazymaze.arena.ArenaLayout;
import me.mrletsplay.mrcore.bukkitimpl.ItemUtils;
import me.mrletsplay.mrcore.bukkitimpl.ItemUtils.ComparisonParameter;
import me.mrletsplay.mrcore.config.ConfigLoader;
import me.mrletsplay.mrcore.config.FileCustomConfig;

public class Config {
	
	public static FileCustomConfig config = ConfigLoader.loadFileConfig(new File(Main.pl.getDataFolder(), "config.yml"));
	public static FileCustomConfig arenaConfig = ConfigLoader.loadFileConfig(new File(Main.pl.getDataFolder(), "arenas.yml"));
	public static FileCustomConfig messages;
	public static List<Arena> arenas = new ArrayList<>();
	public static List<ArenaLayout> arenaLayouts = new ArrayList<>();
	
	public static String prefix, prefix_s;
	
	public static String signLayout0 = "§8[§6Crazy§5Maze§8]";
	public static String signLayout1 = "%name% §8(%size%x)";
	public static String signLayout1_2 = "§aFinish";
	public static String signLayout2p = "§5P §8[§7%pl%/%npl%/%mpl%§8]";
	public static String signLayout2np = "§aV §8[§7%pl%/%npl%/%mpl%§8]";
	public static String signLayout3w = "§7Waiting...";
	public static String signLayout3i = "§aRunning";
	public static String signLayout3r = "§cRestarting...";
//	public static String world;
	public static World cmWorld;
	public static final int MAZE_Y = 1;
	
	public static int
		countdownMinP,
		countdownMaxP,
		wallTime;
		
	public static double
		genSpeed,
		fieldChance,
		dropInterval;
	
	public static String
		swapInvName = "§5Swap",
		votingInvName,
		choiceInvName;
	
//	public static Material
//		field = Material.QUARTZ_BLOCK,
//		walls = Material.BRICK,
//		between = Material.WOOD;
	
	public static boolean 
		hideTablist,
		enable_update_check,
		update_check_on_join,
		update_check_on_command;
	
//	public static Material[] fields = new Material[] {
//		/*Speed*/ Material.STAINED_CLAY
//	};
//	
//	public static byte[] bfield = new byte[] {
//		/*Speed*/ 3
//	};
//	
//	public static ItemStack[] powerups = new ItemStack[] {
//		/*Pass through wall*/ Tools.createItem(Material.INK_SACK, 1, 10, "§aPass through wall", "§7Use this item to pass through a wall", "§7of your choice"),
//		/*Swap*/ /*TODO Tools.createItem(Material.INK_SACK, 1, 12, "§bSwap places", "§7Use this item to swap places with", "§7a player of your choice")*/
//		/*Barrier*/ Tools.createItem(Material.INK_SACK, 1, 1, "§cCreate Barrier", "§7Use this item to create a temporary barrier to slow", "§7down your enemies")
//	};
	
	public static ItemStack
		gameOptions = Tools.createItem(Material.PAPER, 1, 0, "§7Choose your game options"),
		quitGame = Tools.createItem(Material.SLIME_BALL, 1, 0, "§cLeave the game");
	
	public static int wallWidth = 1, pathWidth = 2;
	
	private static final String PERM_BASE = "crazymaze.";
	
	public static final String 
		PERM_ADMIN = PERM_BASE + "admin",
		PERM_KICK = PERM_BASE + "kick",
		PERM_START = PERM_BASE + "start",
		PERM_NOTIFY_UPDATE = PERM_BASE + "notify-update";
	
	public static void saveConfig(){
		config.saveToFile();
	}
	
	public static void saveArenaConfig() {
		arenaConfig.saveToFile();
	}
	
	public static void saveMessageConfig() {
		messages.saveToFile();
	}
	
	public static boolean matchesItem(ItemStack it, ItemStack other) {
		return ItemUtils.compareItems(it, other).matches(ComparisonParameter.ALL_APPLICABLE);
	}
	
	public static void init(){
		Powerup.PASS_WALL.item = Tools.createItem(Material.INK_SACK, 1, 10, "§aPass through wall", "§7Use this item to pass through a wall", "§7of your choice");
		Powerup.CREATE_BARRIER.item = Tools.createItem(Material.INK_SACK, 1, 1, "§cCreate Barrier", "§7Use this item to create a temporary barrier to slow", "§7down your enemies");
		Powerup.PROTECT.item = Tools.createItem(Material.GOLDEN_APPLE,  1,  1, "§6Protect me", "§7Use this item to bounce back other player");
		Powerup.TOPDOWN_VIEW.item = Tools.createItem(Material.CHAINMAIL_BOOTS, 1, 1, "§bTopdown view", "§7Use this item to take a little look from above the maze");
		
		PowerupField.SPEED.material = Material.STAINED_CLAY;
		PowerupField.SPEED.data = 3;
		PowerupField.SLOW_BLOCK.material = Material.STAINED_CLAY;
		PowerupField.SLOW_BLOCK.data = 3;
		PowerupField.RANDOM_TELEPORT.material = Material.STAINED_CLAY;
		PowerupField.RANDOM_TELEPORT.data = 3;
		
		enable_update_check = config.getBoolean("update-check.enable", true, true);
		update_check_on_join = config.getBoolean("update-check.on-join", true, true);
		update_check_on_command = config.getBoolean("update-check.on-command", true, true);
		
		prefix = config.getString("prefix", "§8[§6Crazy§5Maze§8]", true);
		config.setComment("inventory-prefix", "Remember that the inventory prefix shouldn't be too long as inventory names cannot be longer than 32 characters (including color codes)");
		prefix_s = config.getString("inventory-prefix", "§8[§6C§5M§8]", true);
		countdownMinP = config.getInt("countdown.min-players", 60, true);
		countdownMaxP = config.getInt("countdown.max-players", 10, true);
		config.setComment("generation.speed", "Higher = Faster. Default: 1.0\n"
											+ "Note that increasing this value might result in server lags for bigger maps");
		genSpeed = config.getDouble("generation.speed", 1D, true);
		fieldChance = config.getDouble("generation.field-chance-percent", 1.5D, true)/100D;
		config.setComment("drop-interval", "The drop interval in which powerups should be dropped. (Higher = Slower)\n"
										 + "The final interval will be calculated as dropInterval/numFields. This way, the drops per time per area will stay approximately constant\n"
										 + "E.g.: dropInterval = 1000.0 => On a 10x10 (numFields = 10x10 = 100) a powerup will be dropped every 10 seconds");
		dropInterval = config.getDouble("drop-interval", 10000D, true);
		
		config.setComment("hide-tablist", "When this option is enabled, players that are not in the same game as you will be hidden from the tablist");
		hideTablist = config.getBoolean("hide-tablist", true, true);
		
		votingInvName = prefix_s+" §8Game Options";
		choiceInvName = prefix_s+" §8Vote";
		
//		field = Material.getMaterial(config.getString("material.field", Material.QUARTZ_BLOCK.name(), true));
//		walls = Material.getMaterial(config.getString("material.walls", Material.BRICK.name(), true));
//		between = Material.getMaterial(config.getString("material.between", Material.WOOD.name(), true));
		
		config.setComment("powerups.wall-time", "The time it takes before a wall placed by the \"Barrier\" powerup disappears");
		wallTime = config.getInt("powerups.wall-time", 3, true);
		
		if(config.getKeys("layout").isEmpty()) {
			config.set("layout.default.walls.material", Material.BRICK.name());
			config.set("layout.default.floor.material", Material.QUARTZ_BLOCK.name());
			config.set("layout.default.between.material", Material.WOOD.name());
			config.set("layout.default.icon.material", Material.BRICK.name());
			config.set("layout.default.display-name", "§6Default");
			
			config.set("layout.birch-forest.walls.material", Material.LEAVES.name());
			config.set("layout.birch-forest.walls.data", 2);
			config.set("layout.birch-forest.floor.material", Material.WOOD.name());
			config.set("layout.birch-forest.between.material", Material.LOG.name());
			config.set("layout.birch-forest.between.data", 2);
			config.set("layout.birch-forest.icon.material", Material.LOG.name());
			config.set("layout.birch-forest.icon.data", 2);
			config.set("layout.birch-forest.display-name", "§aBirch Forest");
		}
		config.saveToFile();
		
		getLayouts().stream().forEach(name -> arenaLayouts.add(loadLayout(name)));
		getArenas().stream().forEach(name -> arenas.add(loadArena(name)));
		Main.pl.getLogger().info("Loaded "+arenas.size()+" arena(s)");
		
		importLangFile("de");
		
		messages = getMessageConfig(new File(Main.pl.getDataFolder(), "lang/"+config.getString("language", "en", true)+".yml"));
		
		String wName = config.getString("world", "CrazyMaze", true);
		cmWorld = Bukkit.getWorld(wName);
		if(cmWorld==null) {
			Main.pl.getLogger().info("Generating world \""+wName+"\"...");
			cmWorld = Bukkit.createWorld(new WorldCreator(wName).generator(new CrazyMazeWorldGenerator()));
			cmWorld.setGameRuleValue("doMobSpawning", "false");
			cmWorld.setGameRuleValue("doDaylightCycle", "false");
			cmWorld.setTime(6000);
		}
		
		saveConfig();
	}
	
	private static void importLangFile(String lang) {
		if(!new File(Main.pl.getDataFolder(), "/lang/"+lang+".yml").exists()) {
			Main.pl.saveResource("lang/"+lang+".yml", false);
		}
	}

	private static FileCustomConfig getMessageConfig(File f) {
		FileCustomConfig c = ConfigLoader.loadFileConfig(f);
		
		c.addDefault("command.console", "%prefix% §cThe console can't do that");
		c.addDefault("command.no-permission", "%prefix% §cNo permission");
		c.addDefault("command.arena-already-exists", "%prefix% §cArena already exists!");
		c.addDefault("command.already-editing", "%prefix% §cYou are already editing an arena");
		c.addDefault("command.arena-doesnt-exist", "%prefix% §cThat arena doesn't exist");
		c.addDefault("command.properties-imported", "%prefix% §aProperties of §7%arena% §aimported");
		c.addDefault("command.arena-created", "%prefix% §aArena created");
		c.addDefault("command.now-editing", "%prefix% §aYou are now editing the arena §7%arena%");
		c.addDefault("command.not-editing", "%prefix% §cYou are currently not editing an arena");
		c.addDefault("command.set.max-players", "%prefix% §aMax players set to §7%num%");
		c.addDefault("command.set.more-players", "%prefix% §cCan't have less than 1 player");
		c.addDefault("command.set.size", "%prefix% §aSize set to §7%num%");
		c.addDefault("command.set.more-size", "%prefix% §cCan't have size < 3");
		c.addDefault("command.set.powerups", "%prefix% §aEnablePowerups set to §7%state%");
		c.addDefault("command.set.onwin", "%prefix% §aOnWin command set to §7/%cmd%");
		c.addDefault("command.set.lobby", "%prefix% §aLobby set");
		c.addDefault("command.set.mainlobby", "%prefix% §aMainlobby set");
		c.addDefault("command.set.defaultmainlobby", "%prefix% §aDefault mainlobby set");
		c.addDefault("command.set.sign", "%prefix% §aSign set");
		c.addDefault("command.set.sign-not-looking", "%prefix% §cYou need to look at a sign to set it");
		c.addDefault("command.set.min-players", "%prefix% §aMin players set to §7%num%");
		c.addDefault("command.set.too-many-players", "%prefix% §cMin players cannot be > than max players");
		c.addDefault("command.set.layouts", "%prefix% §aArena layouts set to §7%layouts%");
		c.addDefault("command.set.layouts-not-found", "%prefix% §cCouldn't find layouts: §7%layouts%");
		c.addDefault("command.set.layouts-duplicate", "%prefix% §cFound duplicate entries: §7%layouts%");
		c.addDefault("command.setup-incomplete", "%prefix% §cArena setup incomplete. Missing: %missing%");
		c.addDefault("command.arena-saved", "%prefix% §aArena saved");
		c.addDefault("command.changes-discarded", "%prefix% §cChanges discarded");
		c.addDefault("command.arena-is-being-edited", "%prefix% §cThat arena is currently being edited");
		c.addDefault("command.arena-deleted", "%prefix% §cDeleted Arena §6%arena% §csuccessfully");
		c.addDefault("command.kick.player-not-online", "%prefix% §cThat player doesn't exist or isn't currently online");
		c.addDefault("command.kick.player-not-ingame", "%prefix% §cThat player is currently not ingame");
		c.addDefault("command.kick.kicked", "%prefix% §aYou've kicked the player §7%player% §afrom their game successfully");
		c.addDefault("command.start.already-running", "%prefix% §cThe game is already running");
		c.addDefault("command.start.already-starting", "%prefix% §cThe game is already starting");
		c.addDefault("command.start.not-enough-players", "%prefix% §cThere are not enough players in the game");
		c.addDefault("command.start.skipped", "%prefix% §aCountdown skipped");
		c.addDefault("command.list.title", "%prefix% §aAll Arenas:");
		c.addDefault("command.list.entry", "- %name% §7(%size%x, Powerups: %powerups%)");
		
		c.addDefault("ingame.kick-edit", "%prefix% §cYou were kicked from the game because the arena is being edited");
		c.addDefault("ingame.player-joined", "%prefix% §7%player% §ajoined the game §8[§7%currplayers%/%maxplayers%§8]");
		c.addDefault("ingame.player-left", "%prefix% §7%player% §cleft the game §8[§7%currplayers%/%maxplayers%§8]");
		c.addDefault("ingame.countdown.start", "%prefix% §aThe game starts in §7%countdown% §asecond(s)");
		c.addDefault("ingame.countdown.go", "%prefix% §aGo!");
		c.addDefault("ingame.countdown.end.minutes", "%prefix% §6The game ends in §7%countdown% §6minute(s)");
		c.addDefault("ingame.countdown.end.seconds", "%prefix% §6The game ends in §7%countdown% §6second(s)");
		c.addDefault("ingame.end.tie", "%prefix% §cLooks like no one won. §6It's a tie!");
		c.addDefault("ingame.end.win", "%prefix% §7%winner% §awon the game!");
		c.addDefault("ingame.countdown-stopped", "%prefix% §cThe countdown was stopped");
		c.addDefault("ingame.kick-everyone-left", "%prefix% §cYou were kicked from the game because all your opponents left");
		c.addDefault("ingame.arena-loading.1", "%prefix% §6Loading arena...");
		c.addDefault("ingame.arena-loading.2", "%prefix% §6Expected loading time: %time%");
		c.addDefault("ingame.cannot-pass", "%prefix% §cYou cannot pass through that wall");
		c.addDefault("ingame.cannot-create-barrier", "%prefix% §cYou cannot build a barrier there");
		c.addDefault("ingame.kick-restart", "%prefix% §cYou were kicked from the game because the plugin is restarting");
		
		c.addDefault("other.not-ingame", "%prefix% §cYou are currently not ingame");
		c.addDefault("other.already-ingame", "%prefix% §cYou are already in a game");
		c.addDefault("other.game-running", "%prefix% §cThat game is already running");
		c.addDefault("other.game-restarting", "%prefix% §cThat game is currently restarting");
		c.addDefault("other.game-full", "%prefix% §cThat game is already full");
		c.addDefault("other.game-left", "%prefix% §aYou've left the game successfully");
		
		c.addDefault("gui.vote-game-duration", "§bVote for game duration");
		c.addDefault("gui.game-duration.5-mins", "§75 Minutes");
		c.addDefault("gui.game-duration.10-mins", "§710 Minutes");
		c.addDefault("gui.game-duration.20-mins", "§720 Minutes");
		c.addDefault("gui.game-duration.infinite", "§7No time limit");
		c.addDefault("gui.vote-maze-layout", "§bVote for maze layout");
		c.addDefault("gui.maze-layout.random", "§7Random");
		c.addDefault("gui.maze-layout.not-available", "§cThat option is not available");
		c.addDefault("gui.voting-maze-disabled", "§cLayout voting is disabled on this map");
		
		c.applyDefaults();
		
		return c;
	}
	
	public static List<String> saveArena(Arena a) {
		List<String> ss = new ArrayList<>();
		if(a.getGameLobby()==null) {
			ss.add("lobby");
		}
		if(a.getMainLobby()==null) {
			ss.add("mainlobby");
		}
		if(a.getSignLocation()==null) {
			ss.add("sign");
		}
		if(a.getMaxPlayers()<1) {
			ss.add("maxplayers");
		}
		if(a.getSize()<3) {
			ss.add("size");
		}
		if(a.getMinPlayers()<1) {
			ss.add("minplayers");
		}
		if(a.getLayouts()==null || a.getLayouts().isEmpty()) {
			ss.add("layouts");
		}
		if(!ss.isEmpty()) {
			return ss;
		}
		String n = a.getName();
		Tools.saveLocation(arenaConfig, "arena."+n+".lobby", a.getGameLobby());
		Tools.saveLocation(arenaConfig, "arena."+n+".mainlobby", a.getMainLobby());
		Tools.saveLocation(arenaConfig, "arena."+n+".sign", a.getSignLocation());
		arenaConfig.set("arena."+n+".max-players", a.getMaxPlayers());
		arenaConfig.set("arena."+n+".size", a.getSize());
		arenaConfig.set("arena."+n+".onwin", a.getOnWin());
		arenaConfig.set("arena."+n+".minplayers", a.getMinPlayers());
		arenaConfig.set("arena."+n+".powerups", a.powerupsEnabled());
		arenaConfig.set("arena."+n+".layouts", a.getLayouts().stream().map(l -> l.getName()).collect(Collectors.toList()));
		saveArenaConfig();
		arenas.add(a);
		return ss;
	}
	
	public static boolean existsArena(String name) {
		return getArenas().contains(name);
	}
	
	public static Arena getByName(String name) {
		return arenas.stream().filter(a -> a.getName().equals(name)).findFirst().orElse(null);
	}
	
	public static Arena loadArena(String name) {
		Location l = Tools.getLocation(arenaConfig, "arena."+name+".lobby");
		Location ml = Tools.getLocation(arenaConfig, "arena."+name+".mainlobby");
		Location sL = Tools.getLocation(arenaConfig, "arena."+name+".sign");
		int mP = arenaConfig.getInt("arena."+name+".max-players");
		int sz = arenaConfig.getInt("arena."+name+".size");
		String oW = arenaConfig.getString("arena."+name+".onwin");
		int miP = arenaConfig.getInt("arena."+name+".minplayers");
		boolean eP = arenaConfig.getBoolean("arena."+name+".powerups");
		List<ArenaLayout> layouts = new ArrayList<>();
		for(String aName : arenaConfig.getStringList("arena."+name+".layouts")) {
			layouts.add(arenaLayouts.stream().filter(a -> a.getName().equals(aName)).findFirst().orElse(null));
		}
		return new Arena(name, l, ml, sL, mP, sz, oW, miP, eP, layouts);
	}
	
	public static void deleteArena(Arena a) {
		arenas.remove(a);
		Games.games.remove(a);
		arenaConfig.unset("arena."+a.getName());
		saveArenaConfig();
	}
	
	public static List<String> getArenas(){
		return arenaConfig.getKeys("arena");
	}
	
	public static List<String> getLayouts(){
		return config.getKeys("layout");
	}
	
	public static ArenaLayout loadLayout(String name) {
		Material walls = Material.valueOf(config.getString("layout."+name+".walls.material"));
		byte wallsD = config.getByte("layout."+name+".walls.data", (byte) 0, false);
		Material floor = Material.valueOf(config.getString("layout."+name+".floor.material"));
		byte floorD = config.getByte("layout."+name+".floor.data", (byte) 0, false);
		Material between = Material.valueOf(config.getString("layout."+name+".between.material"));
		byte betweenD = config.getByte("layout."+name+".between.data", (byte) 0, false);
		Material iType = Material.valueOf(config.getString("layout."+name+".icon.material"));
		short iDat = config.getShort("layout."+name+".icon.data", (short) 0, false);
		String dName = config.getString("layout."+name+".display-name");
		return new ArenaLayout(name, dName, iType, iDat, walls, wallsD, floor, floorD, between, betweenD);
	}
	
	public static String getAndTranslate(String file, String path) {
		String msg = null;
		try {
			if(file.equalsIgnoreCase("messages")) {
				msg = messages.getString(path);
			}else if(file.equalsIgnoreCase("config")) {
				msg = config.getString(path);
			}
			return ChatColor.translateAlternateColorCodes('&', msg.replace("%prefix%", config.getString("prefix")));
		}catch(NullPointerException e) {
			Main.pl.getLogger().info("Message "+path+" in file "+file+" is missing");
			return "§cMessage missing ("+path+")";
		}
	}
	
	public static String getMessage(String msg) {
		return getAndTranslate("messages", msg);
	}
	
	public static String getMessage(String msg, String... params) {
		if(params.length%2!=0) return null;
		String msg2 = getAndTranslate("messages", msg);
		for(int i = 0; i < params.length; i+=2) {
			msg2 = msg2.replace("%"+params[i]+"%", params[i+1]);
		}
		return msg2;
	}
	
}
