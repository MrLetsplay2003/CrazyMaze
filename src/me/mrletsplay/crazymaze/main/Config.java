package me.mrletsplay.crazymaze.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.inventory.ItemStack;

import me.mrletsplay.crazymaze.arena.Arena;
import me.mrletsplay.crazymaze.arena.ArenaGameMode;
import me.mrletsplay.crazymaze.arena.ArenaLayout;
import me.mrletsplay.crazymaze.game.Games;
import me.mrletsplay.crazymaze.generation.BuildTask;
import me.mrletsplay.crazymaze.generation.BuiltArena;
import me.mrletsplay.crazymaze.generation.MazeBuilder;
import me.mrletsplay.mrcore.bukkitimpl.ItemUtils;
import me.mrletsplay.mrcore.bukkitimpl.ItemUtils.ComparisonParameter;
import me.mrletsplay.mrcore.bukkitimpl.config.BukkitCustomConfig;
import me.mrletsplay.mrcore.bukkitimpl.versioned.VersionedMaterial;
import me.mrletsplay.mrcore.config.ConfigLoader;
import me.mrletsplay.mrcore.config.FileCustomConfig;
import me.mrletsplay.mrcore.misc.Complex;

public class Config {
	
	public static final int MAZE_Y = 1;
	
	private static File configFile = new File(CrazyMaze.plugin.getDataFolder(), "config.yml");
	private static File arenasFile = new File(CrazyMaze.plugin.getDataFolder(), "arenas.yml");
	
	public static BukkitCustomConfig config = ConfigLoader.loadConfigFromFile(new BukkitCustomConfig(configFile), configFile, true);
	private static BukkitCustomConfig arenaConfig = ConfigLoader.loadConfigFromFile(new BukkitCustomConfig(arenasFile), arenasFile, true);
	private static FileCustomConfig messages;
	
	public static List<Arena> arenas = new ArrayList<>();
	public static List<ArenaLayout> arenaLayouts = new ArrayList<>();
	
	public static String prefix, inventoryPrefix;
	
	public static World cmWorld;
	
	public static int
		countdownMinPlayers,
		countdownMaxPlayers,
		wallTime,
		tasksPerTick;
		
	public static double
		fieldChance,
		holeyness,
		dropInterval;
	
	public static String
		swapInvName = "§5Swap",
		votingInvName,
		choiceInvName;
	
	public static boolean 
		hideTablist,
		enableUpdateCheck,
		updateCheckOnJoin,
		updateCheckOnCommand;
	
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
		return ItemUtils.compareItems(it, other).matches(ComparisonParameter.NAME, ComparisonParameter.DURABILITY, ComparisonParameter.TYPE, ComparisonParameter.LORE);
	}
	
	public static void init(){
		Powerup.PASS_WALL.item = ItemUtils.createItem(VersionedMaterial.LIME_DYE, 1, "§aPass through wall", "§7Use this item to pass through a wall", "§7of your choice");
		Powerup.CREATE_BARRIER.item = ItemUtils.createItem(VersionedMaterial.RED_DYE, 1, "§cCreate Barrier", "§7Use this item to create a temporary barrier to slow", "§7down your enemies");
		Powerup.PROTECT.item = ItemUtils.createItem(Material.GOLDEN_APPLE,  1,  0, "§6Protect me", "§7Use this item to bounce back other players");
		Powerup.TOPDOWN_VIEW.item = ItemUtils.createItem(Material.CHAINMAIL_BOOTS, 1, 0, "§bTopdown view", "§7Use this item to take a little look from above the maze");
		Powerup.HIGHLIGHT_PLAYERS.item = ItemUtils.createItem(Material.GLOWSTONE, 1, 0, "§eHighlight players", "§7Use this item to temporarily highlight", "§7all other players");
		
		PowerupField.SPEED.materialWithData = new MaterialWithData(VersionedMaterial.LIGHT_BLUE_STAINED_CLAY);
		PowerupField.SLOW_BLOCK.materialWithData = new MaterialWithData(VersionedMaterial.ORANGE_STAINED_CLAY);
		PowerupField.RANDOM_TELEPORT.materialWithData = new MaterialWithData(VersionedMaterial.PURPLE_STAINED_CLAY);
		
		enableUpdateCheck = config.getBoolean("update-check.enable", true, true);
		updateCheckOnJoin = config.getBoolean("update-check.on-join", true, true);
		updateCheckOnCommand = config.getBoolean("update-check.on-command", true, true);
		
		prefix = config.getString("prefix", "§8[§6Crazy§5Maze§8]", true);
		config.setComment("inventory-prefix", "Remember that the inventory prefix shouldn't be too long as inventory names cannot be longer than 32 characters (including color codes)");
		inventoryPrefix = config.getString("inventory-prefix", "§8[§6C§5M§8]", true);
		countdownMinPlayers = config.getInt("countdown.min-players", 60, true);
		countdownMaxPlayers = config.getInt("countdown.max-players", 10, true);
		config.setComment("generation.tasks-per-tick", "Higher = Faster. Default: 20\n"
											+ "Note that increasing this value might result in server lags for bigger maps");
		tasksPerTick = config.getInt("generation.tasks-per-tick", 20, true);
		fieldChance = config.getDouble("generation.field-chance-percent", 1.5D, true)/100D;
		config.setComment("generation.holeyness", "Increasing this value will increase the amount of randomly created \"holes\" in mazes");
		holeyness = config.getDouble("generation.holeyness", 0.05D, true);
		config.setComment("drop-interval", "The drop interval in which powerups should be dropped. (Higher = Slower)\n"
										 + "The final interval will be calculated as dropInterval/numFields. This way, the drops per time per area will stay approximately constant\n"
										 + "E.g.: dropInterval = 1000.0 => On a 10x10 (numFields = 10x10 = 100) a powerup will be dropped every 10 seconds");
		dropInterval = config.getDouble("drop-interval", 10000D, true);
		
		config.setComment("hide-tablist", "When this option is enabled, players that are not in the same game as you will be hidden from the tablist");
		hideTablist = config.getBoolean("hide-tablist", true, true);
		
		votingInvName = inventoryPrefix + " §8Game Options";
		choiceInvName = inventoryPrefix + " §8Vote";
		
		config.setComment("powerups.wall-time", "The time it takes before a wall placed by the \"Barrier\" powerup disappears");
		wallTime = config.getInt("powerups.wall-time", 3, true);
		
		if(config.getKeys("layout").isEmpty()) {
			Tools.setMaterial(config, "layout.default.walls", new MaterialWithData(Material.BRICKS));
			Tools.setMaterial(config, "layout.default.floor", new MaterialWithData(Material.QUARTZ_BLOCK));
			Tools.setMaterial(config, "layout.default.between", new MaterialWithData(VersionedMaterial.OAK_PLANKS));
			Tools.setMaterial(config, "layout.default.icon", new MaterialWithData(Material.BRICKS));
			config.set("layout.default.display-name", "§6Default");
			
			Tools.setMaterial(config, "layout.birch-forest.walls", new MaterialWithData(VersionedMaterial.BIRCH_LEAVES));
			Tools.setMaterial(config, "layout.birch-forest.floor", new MaterialWithData(VersionedMaterial.BIRCH_PLANKS));
			Tools.setMaterial(config, "layout.birch-forest.between", new MaterialWithData(VersionedMaterial.BIRCH_LOG));
			Tools.setMaterial(config, "layout.birch-forest.icon", new MaterialWithData(VersionedMaterial.BIRCH_LOG));
			config.set("layout.birch-forest.display-name", "§aBirch Forest");
		}
		
		config.saveToFile();
		
		String wName = config.getString("world", "CrazyMaze", true);
		cmWorld = Bukkit.getWorld(wName);
		if(cmWorld == null) {
			CrazyMaze.plugin.getLogger().info("Generating world \"" + wName + "\"...");
			
			cmWorld = Bukkit.createWorld(new WorldCreator(wName).generator(new CrazyMazeWorldGenerator()));
			cmWorld.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
			cmWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
			cmWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
			cmWorld.setTime(6000);
		}
		
		getLayouts().stream().forEach(name -> arenaLayouts.add(loadLayout(name)));
		getArenas().stream().forEach(name -> arenas.add(loadArena(name)));
		CrazyMaze.plugin.getLogger().info("Loaded "+arenas.size()+" arena(s)");
		
		importLangFile("de");
		
		messages = getMessageConfig(new File(CrazyMaze.plugin.getDataFolder(), "lang/" + config.getString("language", "en", true)+".yml"));
		
		saveConfig();
	}
	
	private static void importLangFile(String lang) {
		if(!new File(CrazyMaze.plugin.getDataFolder(), "/lang/"+lang+".yml").exists()) {
			CrazyMaze.plugin.saveResource("lang/"+lang+".yml", false);
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
		ArenaGameMode m = ArenaGameMode.valueOf(arenaConfig.getString("arena." + name + ".mode", ArenaGameMode.CLASSIC.name(), false));
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
		return new Arena(name, m, l, ml, sLocs, mP, sz, oW, miP, eP, layouts);
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
		MaterialWithData walls = Tools.loadMaterial(config, "layout." + name + ".walls");
		MaterialWithData floor = Tools.loadMaterial(config, "layout." + name + ".floor");
		MaterialWithData between = Tools.loadMaterial(config, "layout." + name + ".between");
		MaterialWithData icon = Tools.loadMaterial(config, "layout." + name + ".icon");
		String displayName = config.getString("layout." + name + ".display-name");
		return new ArenaLayout(name, displayName, icon, walls, floor, between);
	}
	
	public static void saveArenaToBeReset(Arena a, BuiltArena built) {
		arenaConfig.set("to-be-reset." + a.getName() + ".location", built.getArenaLocation());
		arenaConfig.set("to-be-reset." + a.getName() + ".layers", built.getMaze().getNumLayers());
		arenaConfig.set("to-be-reset." + a.getName() + ".maze-size-x", built.getMaze().getSizeX());
		arenaConfig.set("to-be-reset." + a.getName() + ".maze-size-y", built.getMaze().getSizeY());
		arenaConfig.set("to-be-reset." + a.getName() + ".cell-size", built.getBuilderProperties().getCellSize());
		arenaConfig.set("to-be-reset." + a.getName() + ".wall-height", built.getBuilderProperties().getWallHeight());
		arenaConfig.saveToFile();
	}
	
	public static BuildTask resetArenaToBeReset(Arena a) {
		if(arenaConfig.getOrCreateSubsection("to-be-reset").getSubsection(a.getName()) == null) return null;
		Location loc = arenaConfig.getLocation("to-be-reset." + a.getName() + ".location");
		int l = arenaConfig.getInt("to-be-reset." + a.getName() + ".layers");
		int sizeX = arenaConfig.getInt("to-be-reset." + a.getName() + ".maze-size-x");
		int sizeY = arenaConfig.getInt("to-be-reset." + a.getName() + ".maze-size-y");
		int cellSize = arenaConfig.getInt("to-be-reset." + a.getName() + ".cell-size");
		int wallHeight = arenaConfig.getInt("to-be-reset." + a.getName() + ".wall-height");
		
		BuildTask t = MazeBuilder.resetMaze(loc, l, sizeX, sizeY, cellSize, wallHeight);
		t.addSubTask(() -> {
			arenaConfig.unset("to-be-reset." + a.getName());
			arenaConfig.saveToFile();
		});
		return t;
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
			CrazyMaze.plugin.getLogger().info("Message "+path+" in file "+file+" is missing");
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
