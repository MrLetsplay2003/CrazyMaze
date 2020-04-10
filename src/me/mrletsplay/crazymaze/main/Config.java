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
import me.mrletsplay.crazymaze.game.Games;
import me.mrletsplay.mrcore.bukkitimpl.ItemUtils;
import me.mrletsplay.mrcore.bukkitimpl.ItemUtils.ComparisonParameter;
import me.mrletsplay.mrcore.bukkitimpl.config.BukkitCustomConfig;
import me.mrletsplay.mrcore.bukkitimpl.versioned.VersionedMaterial;
import me.mrletsplay.mrcore.config.ConfigLoader;
import me.mrletsplay.mrcore.config.FileCustomConfig;
import me.mrletsplay.mrcore.misc.Complex;

public class Config {
	
	public static final int MAZE_Y = 1;
	
	private static File configFile = new File(CrazyMaze.pl.getDataFolder(), "config.yml");
	private static File arenasFile = new File(CrazyMaze.pl.getDataFolder(), "arenas.yml");
	
	public static BukkitCustomConfig config = ConfigLoader.loadConfigFromFile(new BukkitCustomConfig(configFile), configFile, true);
	public static BukkitCustomConfig arenaConfig = ConfigLoader.loadConfigFromFile(new BukkitCustomConfig(arenasFile), arenasFile, true);
	public static FileCustomConfig messages;
	public static List<Arena> arenas = new ArrayList<>();
	public static List<ArenaLayout> arenaLayouts = new ArrayList<>();
	
	public static String prefix, inventoryPrefix;
	
//	public static String signLayout0 = "§8[§6Crazy§5Maze§8]";
//	public static String signLayout1 = "%name% §8(%size%x)";
//	public static String signLayout1_2 = "§aFinish";
//	public static String signLayout2p = "§5P §8[§7%pl%/%npl%/%mpl%§8]";
//	public static String signLayout2np = "§aV §8[§7%pl%/%npl%/%mpl%§8]";
//	public static String signLayout3w = "§7Waiting...";
//	public static String signLayout3i = "§aRunning";
//	public static String signLayout3r = "§cRestarting...";
	public static World cmWorld;
	
	public static int
		countdownMinPlayers,
		countdownMaxPlayers,
		wallTime;
		
	public static double
		genSpeed,
		fieldChance,
		dropInterval;
	
	public static String
		swapInvName = "§5Swap",
		votingInvName,
		choiceInvName;
	
	public static boolean 
		hideTablist,
		enable_update_check,
		update_check_on_join,
		update_check_on_command;
	
	public static ItemStack
		gameOptions = ItemUtils.createItem(Material.PAPER, 1, 0, "§7Choose your game options"),
		quitGame = ItemUtils.createItem(Material.SLIME_BALL, 1, 0, "§cLeave the game");
	
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
	
	@SuppressWarnings("deprecation")
	public static void init(){
		Powerup.PASS_WALL.item = ItemUtils.createItem(VersionedMaterial.LIME_DYE, 1, "§aPass through wall", "§7Use this item to pass through a wall", "§7of your choice");
		Powerup.CREATE_BARRIER.item = ItemUtils.createItem(VersionedMaterial.RED_DYE, 1, "§cCreate Barrier", "§7Use this item to create a temporary barrier to slow", "§7down your enemies");
		Powerup.PROTECT.item = ItemUtils.createItem(Material.GOLDEN_APPLE,  1,  1, "§6Protect me", "§7Use this item to bounce back other player");
		Powerup.TOPDOWN_VIEW.item = ItemUtils.createItem(Material.CHAINMAIL_BOOTS, 1, 1, "§bTopdown view", "§7Use this item to take a little look from above the maze");
		
		PowerupField.SPEED.materialWithData = new MaterialWithData(VersionedMaterial.LIGHT_BLUE_STAINED_CLAY);
		PowerupField.SLOW_BLOCK.materialWithData = new MaterialWithData(VersionedMaterial.ORANGE_STAINED_CLAY);
		PowerupField.RANDOM_TELEPORT.materialWithData = new MaterialWithData(VersionedMaterial.PURPLE_STAINED_CLAY);
		
		enable_update_check = config.getBoolean("update-check.enable", true, true);
		update_check_on_join = config.getBoolean("update-check.on-join", true, true);
		update_check_on_command = config.getBoolean("update-check.on-command", true, true);
		
		prefix = config.getString("prefix", "§8[§6Crazy§5Maze§8]", true);
		config.setComment("inventory-prefix", "Remember that the inventory prefix shouldn't be too long as inventory names cannot be longer than 32 characters (including color codes)");
		inventoryPrefix = config.getString("inventory-prefix", "§8[§6C§5M§8]", true);
		countdownMinPlayers = config.getInt("countdown.min-players", 60, true);
		countdownMaxPlayers = config.getInt("countdown.max-players", 10, true);
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
		
		votingInvName = inventoryPrefix+" §8Game Options";
		choiceInvName = inventoryPrefix+" §8Vote";
		
		config.setComment("powerups.wall-time", "The time it takes before a wall placed by the \"Barrier\" powerup disappears");
		wallTime = config.getInt("powerups.wall-time", 3, true);
		
		if(config.getKeys("layout").isEmpty()) {
			Tools.setMaterial(config, "layout.default.walls", new MaterialWithData(Material.BRICKS));
			Tools.setMaterial(config, "layout.default.floor", new MaterialWithData(Material.QUARTZ_BLOCK));
			Tools.setMaterial(config, "layout.default.between", new MaterialWithData(VersionedMaterial.OAK_PLANKS));
			Tools.setMaterial(config, "layout.default.icon", new MaterialWithData(Material.BRICKS));
			config.set("layout.default.display-name", "§6Default");
			
			Tools.setMaterial(config, "layout.birch-forest.walls", new MaterialWithData(VersionedMaterial.OAK_LEAVES));
			Tools.setMaterial(config, "layout.birch-forest.floor", new MaterialWithData(VersionedMaterial.OAK_PLANKS));
			Tools.setMaterial(config, "layout.birch-forest.between", new MaterialWithData(VersionedMaterial.OAK_LOG));
			Tools.setMaterial(config, "layout.birch-forest.icon", new MaterialWithData(VersionedMaterial.OAK_LOG));
			config.set("layout.birch-forest.display-name", "§aBirch Forest");
		}
		
		config.saveToFile();
		
		String wName = config.getString("world", "CrazyMaze", true);
		cmWorld = Bukkit.getWorld(wName);
		if(cmWorld == null) {
			CrazyMaze.pl.getLogger().info("Generating world \""+wName+"\"...");
			cmWorld = Bukkit.createWorld(new WorldCreator(wName).generator(new CrazyMazeWorldGenerator()));
			cmWorld.setGameRuleValue("doMobSpawning", "false");
			cmWorld.setGameRuleValue("doDaylightCycle", "false");
			cmWorld.setTime(6000);
		}
		
		getLayouts().stream().forEach(name -> arenaLayouts.add(loadLayout(name)));
		getArenas().stream().forEach(name -> arenas.add(loadArena(name)));
		CrazyMaze.pl.getLogger().info("Loaded "+arenas.size()+" arena(s)");
		
		importLangFile("de");
		
		messages = getMessageConfig(new File(CrazyMaze.pl.getDataFolder(), "lang/"+config.getString("language", "en", true)+".yml"));
		
		saveConfig();
	}
	
	private static void importLangFile(String lang) {
		if(!new File(CrazyMaze.pl.getDataFolder(), "/lang/"+lang+".yml").exists()) {
			CrazyMaze.pl.saveResource("lang/"+lang+".yml", false);
		}
	}

	private static FileCustomConfig getMessageConfig(File f) {
		FileCustomConfig c = ConfigLoader.loadFileConfig(f);
		
		for(Message m : Message.values()) {
			c.addDefault(m.getPath(), m.getFallback());
		}
		
		c.applyDefaults();
		c.saveToFile();
		
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
		
		if(!ss.isEmpty()) return ss;
		
		String n = a.getName();
		arenaConfig.set("arena."+n+".lobby", a.getGameLobby());
		arenaConfig.set("arena."+n+".mainlobby", a.getMainLobby());
		arenaConfig.set("arena."+n+".signs", a.getSignLocations());
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
		Location l = arenaConfig.getLocation("arena."+name+".lobby");
		Location ml = arenaConfig.getLocation("arena."+name+".mainlobby");
		List<Location> sLocs = arenaConfig.getComplex("arena." + name + ".signs", Complex.list(Location.class));
		int mP = arenaConfig.getInt("arena."+name+".max-players");
		int sz = arenaConfig.getInt("arena."+name+".size");
		String oW = arenaConfig.getString("arena."+name+".onwin");
		int miP = arenaConfig.getInt("arena."+name+".minplayers");
		boolean eP = arenaConfig.getBoolean("arena."+name+".powerups");
		List<ArenaLayout> layouts = new ArrayList<>();
		for(String aName : arenaConfig.getStringList("arena."+name+".layouts")) {
			layouts.add(arenaLayouts.stream().filter(a -> a.getName().equals(aName)).findFirst().orElse(null));
		}
		return new Arena(name, l, ml, sLocs, mP, sz, oW, miP, eP, layouts);
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
		MaterialWithData walls = Tools.loadMaterial(config, "layout."+name+".walls");
		MaterialWithData floor = Tools.loadMaterial(config, "layout."+name+".floor");
		MaterialWithData between = Tools.loadMaterial(config, "layout."+name+".between");
		MaterialWithData icon = Tools.loadMaterial(config, "layout."+name+".icon");
		String displayName = config.getString("layout."+name+".display-name");
		return new ArenaLayout(name, displayName, icon, walls, floor, between);
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
			CrazyMaze.pl.getLogger().info("Message "+path+" in file "+file+" is missing");
			return "§cMessage missing ("+path+")";
		}
	}
	
	public static String getMessage(Message message) {
		return getAndTranslate("messages", message.getPath());
	}
	
	public static String getMessage(Message message, String... params) {
		if(params.length%2!=0) return null;
		String msg2 = getAndTranslate("messages", message.getPath());
		for(int i = 0; i < params.length; i+=2) {
			msg2 = msg2.replace("%"+params[i]+"%", params[i+1]);
		}
		return msg2;
	}
	
}
