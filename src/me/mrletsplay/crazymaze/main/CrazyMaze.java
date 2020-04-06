package me.mrletsplay.crazymaze.main;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.mrletsplay.crazymaze.arena.Arena;
import me.mrletsplay.crazymaze.game.Game;
import me.mrletsplay.crazymaze.game.GameStage;
import me.mrletsplay.crazymaze.game.Games;
import me.mrletsplay.crazymaze.main.UpdateChecker.Result;

public class CrazyMaze extends JavaPlugin {

	public static JavaPlugin pl;
	public static HashMap<Player, Arena> pArenas = new HashMap<>();
	public static HashMap<Player, Arena> arenas = new HashMap<>();
	public static Random r = new Random();
	public static String PLUGIN_VERSION;
	
	@Override
	public void onEnable() {
		pl = this;
		MrCoreBukkitImpl.loadMrCore(this);
		
		PLUGIN_VERSION = getDescription().getVersion();
		Config.init();
		Bukkit.getPluginManager().registerEvents(new Events(), this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> Games.games.values().forEach(g -> {
			g.tick();
		}), 20, 20);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> Config.arenas.forEach(a -> {
			a.updSign();
		}), 20, 20);
		CMTimer.init();
		
		if (Config.enable_update_check && Config.update_check_on_join) {
			getLogger().info("Checking for update...");
			UpdateChecker.Result res = UpdateChecker.checkForUpdate();
			if(res.updAvailable) {
				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (pl.hasPermission(Config.PERM_NOTIFY_UPDATE)) {
						UpdateChecker.sendUpdateMessage(res, pl);
					}
				}
			}
			getLogger().info("Current version: "+PLUGIN_VERSION+", Newest version: "+res.updVer);
			if(res.updAvailable) {
				getLogger().info("----------------------------------------------");
				getLogger().info("There's an update available: "+res.updVer);
				res.updChlog.stream().forEach(getLogger()::info);
				getLogger().info("----------------------------------------------");
			}else{
				getLogger().info("No update available!");
			}
		}
		
//		Metrics m = new Metrics(this);
//		m.addCustomChart(new Metrics.AdvancedPie("maze_size") {
//			
//			@Override
//			public HashMap<String, Integer> getValues(HashMap<String, Integer> valueMap) {
//				HashMap<String, Integer> vals = new HashMap<>();
//				for(Arena a : Config.arenas) {
//					vals.put(String.valueOf(a.getSize()), vals.getOrDefault(String.valueOf(a.getSize()), 0)+1);
//				}
//				return vals;
//			}
//		});
		
		getLogger().info("Enabled");
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Resetting running arenas...");
		for(Game g : Games.games.values()) {
			g.stop(true, Config.getMessage(Message.INGAME_KICK_RESTART));
		}
		getLogger().info("Disabled");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(Config.getMessage(Message.COMMAND_CONSOLE));
			return true;
		}
		Player p = (Player)sender;
		if(command.getName().equalsIgnoreCase("crazymaze")){
			if(args.length==0) {
				sendCommandHelp(sender);
				return true;
			}
			if(args[0].equalsIgnoreCase("create")) {
				if(!p.hasPermission(Config.PERM_ADMIN)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
					return true;
				}
				if(args.length!=2 && args.length!=3) {
					sendCommandHelp(p);
					return true;
				}
				if(arenas.containsKey(p)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_ALREADY_EDITING));
					return true;
				}
				String name = args[1];
				if(Config.existsArena(name)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_ARENA_ALREADY_EXISTS));
					return true;
				}
				Arena a = new Arena(name);
				if(args.length == 3) {
					if(!Config.existsArena(args[2])) {
						p.sendMessage(Config.getMessage(Message.COMMAND_ARENA_DOESNT_EXIST));
						return true;
					}
					Arena b = Config.getByName(args[2]);
					a.setEnablePowerups(b.powerupsEnabled());
					a.setGameLobby(b.getGameLobby());
					a.setMainLobby(b.getMainLobby());
					a.setMaxPlayers(b.getMaxPlayers());
					a.setMinPlayers(b.getMinPlayers());
					a.setOnWin(b.getOnWin());
					a.setSize(b.getSize());
					p.sendMessage(Config.getMessage(Message.COMMAND_PROPERTIES_IMPORTED, "arena", args[2]));
				}
				arenas.put(p, a);
				p.sendMessage(Config.getMessage(Message.COMMAND_ARENA_CREATED));
				return true;
			}else if(args[0].equalsIgnoreCase("edit")) {
				if(!p.hasPermission(Config.PERM_ADMIN)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
					return true;
				}
				if(args.length!=2) {
					sendCommandHelp(p);
					return true;
				}
				if(arenas.containsKey(p)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_ALREADY_EDITING));
					return true;
				}
				Arena a = Config.getByName(args[1]);
				if(a==null) {
					p.sendMessage(Config.getMessage(Message.COMMAND_ARENA_DOESNT_EXIST));
					return true;
				}
				Game g = Games.getGame(a);
				if(g!=null) g.stop(false, Config.getMessage(Message.INGAME_KICK_EDIT));
				Config.arenas.remove(a);
				arenas.put(p, a);
				pArenas.put(p, a.clone());
				p.sendMessage(Config.getMessage(Message.COMMAND_NOW_EDITING, "arena", args[1]));
				return true;
			}else if(args[0].equalsIgnoreCase("setmaxplayers")) {
				if(!p.hasPermission(Config.PERM_ADMIN)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
					return true;
				}
				if(!arenas.containsKey(p)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NOT_EDITING));
					return true;
				}
				if(args.length!=2) {
					sendCommandHelp(p);
					return true;
				}
				Arena a = arenas.get(p);
				try {
					int num = Integer.parseInt(args[1]);
					if(num>=1) {
						a.setMaxPlayers(num);
						a.updSign();
						arenas.put(p, a);
						p.sendMessage(Config.getMessage(Message.COMMAND_SET_MAX_PLAYERS, "num", ""+num));
					}else {
						p.sendMessage(Config.getMessage(Message.COMMAND_SET_MORE_PLAYERS));
					}
				}catch(Exception e) {
					e.printStackTrace();
					sendCommandHelp(p);
				}
			}else if(args[0].equalsIgnoreCase("setsize")) {
				if(!p.hasPermission(Config.PERM_ADMIN)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
					return true;
				}
				if(!arenas.containsKey(p)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NOT_EDITING));
					return true;
				}
				if(args.length!=2) {
					sendCommandHelp(p);
					return true;
				}
				Arena a = arenas.get(p);
				try {
					int num = Integer.parseInt(args[1]);
					if(num>=3) {
						a.setSize(num);
						a.updSign();
						arenas.put(p, a);
						p.sendMessage(Config.getMessage(Message.COMMAND_SET_SIZE, "num", ""+num));
					}else {
						p.sendMessage(Config.getMessage(Message.COMMAND_SET_MORE_SIZE));
					}
				}catch(Exception e) {
					sendCommandHelp(p);
				}
			}else if(args[0].equalsIgnoreCase("setpowerups")) {
				if(!p.hasPermission(Config.PERM_ADMIN)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
					return true;
				}
				if(!arenas.containsKey(p)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NOT_EDITING));
					return true;
				}
				if(args.length!=2) {
					sendCommandHelp(p);
					return true;
				}
				Arena a = arenas.get(p);
				boolean b = Boolean.valueOf(args[1]);
				a.setEnablePowerups(b);
				arenas.put(p, a);
				p.sendMessage(Config.getMessage(Message.COMMAND_SET_POWERUPS, "state", ""+b));
			}else if(args[0].equalsIgnoreCase("setonwin")) {
				if(!p.hasPermission(Config.PERM_ADMIN)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
					return true;
				}
				if(!arenas.containsKey(p)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NOT_EDITING));
					return true;
				}
				if(args.length<2) {
					sendCommandHelp(p);
					return true;
				}
				Arena a = arenas.get(p);
				String cmdR = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
				String cmd = cmdR.startsWith("/")?cmdR.substring(1):cmdR;
				a.setOnWin(cmd);
				arenas.put(p, a);
				p.sendMessage(Config.getMessage(Message.COMMAND_SET_ONWIN,"cmd", cmd.replace("%winner%", "§8<Winner>§7").replace("%winneruuid%", "§8<WinnerUUID>§7")));
			}else if(args[0].equalsIgnoreCase("setlobby")) {
				if(!p.hasPermission(Config.PERM_ADMIN)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
					return true;
				}
				if(!arenas.containsKey(p)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NOT_EDITING));
					return true;
				}
				Arena a = arenas.get(p);
				a.setGameLobby(p.getLocation());
				arenas.put(p, a);
				p.sendMessage(Config.getMessage(Message.COMMAND_SET_LOBBY));
			}else if(args[0].equalsIgnoreCase("setmainlobby")) {
				if(!p.hasPermission(Config.PERM_ADMIN)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
					return true;
				}
				if(!arenas.containsKey(p)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NOT_EDITING));
					return true;
				}
				Arena a = arenas.get(p);
				a.setMainLobby(p.getLocation());
				arenas.put(p, a);
				p.sendMessage(Config.getMessage(Message.COMMAND_SET_MAINLOBBY));
			}else if(args[0].equalsIgnoreCase("setdefaultmainlobby")) {
				if(!p.hasPermission(Config.PERM_ADMIN)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
					return true;
				}
				Config.config.set("settings.defaultmainlobby", p.getLocation());
				Config.saveConfig();
				p.sendMessage(Config.getMessage(Message.COMMAND_SET_DEFAULTMAINLOBBY));
			}else if(args[0].equalsIgnoreCase("setsign")) {
				if(!p.hasPermission(Config.PERM_ADMIN)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
					return true;
				}
				if(!arenas.containsKey(p)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NOT_EDITING));
					return true;
				}
				Arena a = arenas.get(p);
				Block block = p.getTargetBlock((Set<Material>)null, 100);
				Location bl = block.getLocation();
				if(block.getState() instanceof Sign) {
					a.addSignLocation(bl);
					a.updSign();
					p.sendMessage(Config.getMessage(Message.COMMAND_SET_SIGN));
					return true;
				}else {
					p.sendMessage(Config.getMessage(Message.COMMAND_SET_SIGN_NOT_LOOKING));
					return true;
				}
			}else if(args[0].equalsIgnoreCase("setminplayers")) {
				if(!p.hasPermission(Config.PERM_ADMIN)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
					return true;
				}
				if(!arenas.containsKey(p)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NOT_EDITING));
					return true;
				}
				if(args.length!=2) {
					sendCommandHelp(p);
					return true;
				}
				Arena a = arenas.get(p);
				try {
					int num = Integer.parseInt(args[1]);
					if(num>=1) {
						if(num <= a.getMaxPlayers()) {
							a.setMinPlayers(num);
							a.updSign();
							arenas.put(p, a);
							p.sendMessage(Config.getMessage(Message.COMMAND_SET_MIN_PLAYERS, "num", ""+num));
						}else {
							p.sendMessage(Config.getMessage(Message.COMMAND_SET_TOO_MANY_PLAYERS));
						}
					}else {
						p.sendMessage(Config.getMessage(Message.COMMAND_SET_MORE_PLAYERS));
					}
				}catch(Exception e) {
					e.printStackTrace();
					sendCommandHelp(p);
				}
				
			}else if(args[0].equalsIgnoreCase("setlayouts")) {
				if(!p.hasPermission(Config.PERM_ADMIN)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
					return true;
				}
				if(!arenas.containsKey(p)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NOT_EDITING));
					return true;
				}
				if(args.length!=2) {
					sendCommandHelp(p);
					return true;
				}
				Arena a = arenas.get(p);
				List<String> layouts = Arrays.asList(args[1].split(","));
				List<String> dups = Tools.getDuplicates(layouts);
				if(!dups.isEmpty()) {
					p.sendMessage(Config.getMessage(Message.COMMAND_SET_LAYOUTS_DUPLICATE, "layouts", dups.toString()));
					return true;
				}
				List<String> nF = a.setLayouts(layouts);
				if(nF.isEmpty()) {
					arenas.put(p, a);
					p.sendMessage(Config.getMessage(Message.COMMAND_SET_LAYOUTS, "layouts", layouts.toString()));
				}else {
					p.sendMessage(Config.getMessage(Message.COMMAND_SET_LAYOUTS_NOT_FOUND, "layouts", nF.toString()));
				}
			}else if(args[0].equalsIgnoreCase("save")) {
				if(!p.hasPermission(Config.PERM_ADMIN)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
					return true;
				}
				if(!arenas.containsKey(p)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NOT_EDITING));
					return true;
				}
				List<String> ss = Config.saveArena(arenas.get(p));
				if(!ss.isEmpty()) {
					p.sendMessage(Config.getMessage(Message.COMMAND_SETUP_INCOMPLETE, "missing", ""+ss));
					return true;
				}else{
					p.sendMessage(Config.getMessage(Message.COMMAND_ARENA_SAVED));
					arenas.remove(p);
					return true;
				}
			}else if(args[0].equalsIgnoreCase("discard")) {
				if(!p.hasPermission(Config.PERM_ADMIN)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
					return true;
				}
				if(!arenas.containsKey(p)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NOT_EDITING));
					return true;
				}
				arenas.remove(p);
				if(pArenas.containsKey(p)) {
					Config.arenas.add(pArenas.remove(p));
				}
				p.sendMessage(Config.getMessage(Message.COMMAND_CHANGES_DISCARDED));
			}else if(args[0].equalsIgnoreCase("delete")) {
				if(args.length!=2) {
					sendCommandHelp(p);
					return true;
				}
				
				Arena a = Config.getByName(args[1]);
				if(a==null) {
					p.sendMessage(Config.getMessage(Message.COMMAND_ARENA_DOESNT_EXIST));
					return true;
				}
				if(arenas.containsValue(a)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_ARENA_IS_BEING_EDITED));
					return true;
				}
				
				Config.deleteArena(a);
				p.sendMessage(Config.getMessage(Message.COMMAND_ARENA_DELETED, "arena", a.getName()));
			}else if(args[0].equalsIgnoreCase("leave")) {
				if(!Games.isInGame(p)) {
					p.sendMessage(Config.getMessage(Message.OTHER_NOT_INGAME));
					return true;
				}
				Game g = Games.getGame(p);
				g.removePlayer(p);
				p.sendMessage(Config.getMessage(Message.OTHER_GAME_LEFT));
			}else if(args[0].equalsIgnoreCase("kick")) {
				if(!p.hasPermission(Config.PERM_KICK)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
					return true;
				}
				if(args.length!=2) {
					sendCommandHelp(p);
					return true;
				}
				Player pl = Bukkit.getPlayer(args[1]);
				if(pl==null) {
					p.sendMessage(Config.getMessage(Message.COMMAND_KICK_PLAYER_NOT_ONLINE));
					return true;
				}
				if(!Games.isInGame(pl)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_KICK_PLAYER_NOT_INGAME));
					return true;
				}
				Game g = Games.getGame(pl);
				pl.teleport(g.getArena().getMainLobby());
				g.removePlayer(pl);
				p.sendMessage(Config.getMessage(Message.COMMAND_KICK_KICKED, "player", pl.getName()));
			}else if(args[0].equalsIgnoreCase("start")) {
				if(!p.hasPermission(Config.PERM_START)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
					return true;
				}
				
				if(!Games.isInGame(p)) {
					p.sendMessage(Config.getMessage(Message.OTHER_NOT_INGAME));
					return true;
				}
				
				Game g = Games.getGame(p);
				if(!g.getStage().equals(GameStage.WAITING)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_START_ALREADY_RUNNING));
					return true;
				}
				
				if(g.getPlayers().size() < g.getArena().getMinPlayers()) {
					p.sendMessage(Config.getMessage(Message.COMMAND_START_NOT_ENOUGH_PLAYERS));
					return true;
				}
				
				if(g.getCountdown()<=Config.countdownMaxP) {
					p.sendMessage(Config.getMessage(Message.COMMAND_START_ALREADY_STARTING));
					return true;
				}
				
				g.setCountdown(Config.countdownMaxP);
				p.sendMessage(Config.getMessage(Message.COMMAND_START_SKIPPED));
			}else if(args[0].equalsIgnoreCase("list")) {
				if(!p.hasPermission(Config.PERM_ADMIN)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
					return true;
				}
				p.sendMessage(Config.getMessage(Message.COMMAND_LIST_TITLE));
				for(Arena a : Config.arenas) {
					p.sendMessage(Config.getMessage(Message.COMMAND_LIST_ENTRY, "name", a.getName(), "size", ""+a.getSize(), "powerups", ""+a.powerupsEnabled()));
				}
			}else if(args[0].equalsIgnoreCase("info")) {
				if(!p.hasPermission(Config.PERM_ADMIN)) {
					p.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
					return true;
				}
				if(args.length!=2) {
					sendCommandHelp(p);
					return true;
				}
				if(!Config.existsArena(args[1])) {
					p.sendMessage(Config.getMessage(Message.COMMAND_ARENA_DOESNT_EXIST));
					return true;
				}
				Arena a = Config.getByName(args[1]);
				p.sendMessage(Config.prefix+" §eInfo");
				p.sendMessage("§6Name: §7"+a.getName());
				p.sendMessage("§6Players: §7Min: "+a.getMinPlayers()+" §8| §7Max: "+a.getMaxPlayers());
				p.sendMessage("§6Size: §7"+a.getSize()+"x");
				p.sendMessage("§6Powerups: §7"+a.powerupsEnabled());
				if(a.getOnWin()!=null) p.sendMessage("§6OnWin: §7/"+a.getOnWin().replace("%winner%", "§8<Winner>§7").replace("%winneruuid%", "§8<WinnerUUID>§7"));
				p.sendMessage("§6Layouts: §7"+a.getLayouts().stream().map(l -> l.getName()).collect(Collectors.toList()));
			}else if(args[0].equalsIgnoreCase("version")) {
				if (p.hasPermission(Config.PERM_NOTIFY_UPDATE)) {
					p.sendMessage("Current CrazyMaze version: §7"+PLUGIN_VERSION);
					if(Config.enable_update_check && Config.update_check_on_command) {
						Result r = UpdateChecker.checkForUpdate();
						if(r.updAvailable) {
							UpdateChecker.sendUpdateMessage(r, p);
						}else {
							p.sendMessage("§aYou are using the newest version of CrazyMaze");
						}
					}
				} else {
					sendCommandHelp(p);
				}
				return true;
			}else {
				sendCommandHelp(p);
			}
		}
		return false;
	}
	
	private void sendCommandHelp(CommandSender s) {
		s.sendMessage(Config.prefix+" §cHelp");
		if(s.hasPermission(Config.PERM_ADMIN)) {
			s.sendMessage("§7§lArena creation:");
			s.sendMessage("§7/cm create <Name> §8- Create an arena");
			s.sendMessage("§7/cm edit <Name> §8- Edit an arena");
			s.sendMessage("§7/cm setlobby §8- Set the game lobby");
			s.sendMessage("§7/cm setmainlobby §8- Set the main lobby");
			s.sendMessage("§7/cm setsign §8- Set the game sign");
			s.sendMessage("§7/cm setsize <Size> §8- Set the arena size");
			s.sendMessage("§7/cm setonwin <onWin> §8- Set the on win command (%winner% = Winner's name, %winneruuid% = Winner's UUID)");
			s.sendMessage("§7/cm setminplayers <minPlayers> §8- Set the minimum player count");
			s.sendMessage("§7/cm setpowerups <enablePowerups> §8- Enable/Disable powerups for an arena");
			s.sendMessage("§7/cm setdefaultmainlobby §8- Set the default main lobby");
			s.sendMessage("§7/cm setlayouts <Layout name,Layout name 2,Layout name 3,...> §8- Set the arena layouts");
			s.sendMessage("§7/cm discard §8- Discard all changes");
			s.sendMessage("§7/cm save §8- Save an arena");
			s.sendMessage("§7/cm delete <Name> §8- Delete an arena");
			s.sendMessage("§7/cm list §8- Lists all arenas");
			s.sendMessage("§7/cm info <Arena> §8- Shows information about an arena");
			s.sendMessage("§7§l-----------------");
		}
		if(s.hasPermission(Config.PERM_NOTIFY_UPDATE)) {
			s.sendMessage("§7/cm version §8- Shows the current version and checks for an update (if enabled)");
		}
		if(s.hasPermission(Config.PERM_KICK)) {
			s.sendMessage("§7/cm kick <Player> §8- Kick a player from their game");
		}
		if(s.hasPermission(Config.PERM_START)) {
			s.sendMessage("§7/cm start §8- Skip the countdown of your current game");
		}
		s.sendMessage("§7/cm leave §8- Leave your current game");
	}
	
}
