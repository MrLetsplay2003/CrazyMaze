package me.mrletsplay.crazymaze.main;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import me.mrletsplay.crazymaze.arena.Arena;
import me.mrletsplay.crazymaze.command.CommandCrazyMaze;
import me.mrletsplay.crazymaze.game.Game;
import me.mrletsplay.crazymaze.game.Games;

public class CrazyMaze extends JavaPlugin {

	public static JavaPlugin plugin;
	public static HashMap<UUID, Arena> backupArenas = new HashMap<>();
	public static HashMap<UUID, Arena> arenas = new HashMap<>();
	public static Random r = new Random();
	public static String pluginVersion;
	
	@Override
	public void onEnable() {
		plugin = this;
		MrCoreBukkitImpl.loadMrCore(this);
		
		if(Bukkit.getPluginManager().isPluginEnabled("Multiverse-Core")) {
			getLogger().info("Please make sure to remove the CrazyMaze world from the Multiverse config using \"/mvremove CrazyMaze\" or else the generation might mess up");
			getLogger().info("Alternatively, you can import the world into Multiverse using the generator \"CrazyMaze\"");
		}
		
		CommandCrazyMaze cm = CommandCrazyMaze.INSTANCE;
		PluginCommand pc = getCommand("crazymaze");
		pc.setExecutor(cm);
		pc.setTabCompleter(cm);
		pc.setDescription(cm.getDescription());
		pc.setUsage(cm.getUsage());
		
		pluginVersion = getDescription().getVersion();
		Config.init();
		Bukkit.getPluginManager().registerEvents(new Events(), this);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> Games.games.values().forEach(Game::tick), 20, 20);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> Config.arenas.forEach(Arena::updateSign), 20, 20);
		
		CMTimer.init();
		
		if (Config.enableUpdateCheck && Config.updateCheckOnJoin) {
			getLogger().info("Checking for update...");
			UpdateChecker.Result res = UpdateChecker.checkForUpdate();
			if(res.updAvailable) {
				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (pl.hasPermission(Config.PERM_NOTIFY_UPDATE)) {
						UpdateChecker.sendUpdateMessage(res, pl);
					}
				}
			}
			getLogger().info("Current version: "+pluginVersion+", Newest version: "+res.updVer);
			if(res.updAvailable) {
				getLogger().info("----------------------------------------------");
				getLogger().info("There's an update available: "+res.updVer);
				res.updChlog.stream().forEach(getLogger()::info);
				getLogger().info("----------------------------------------------");
			}else{
				getLogger().info("No update available!");
			}
		}
		
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
	
	public static void sendCommandHelp(CommandSender s) {
		s.sendMessage(Config.prefix + " §cHelp");
		if(s.hasPermission(Config.PERM_ADMIN)) {
			s.sendMessage("§7§lArena creation:");
			s.sendMessage("§7/cm create <Name> [copy] §8- Create an arena");
			s.sendMessage("§7/cm edit <Name> §8- Edit an arena");
			s.sendMessage("§7/cm set lobby §8- Set the game lobby");
			s.sendMessage("§7/cm set mainlobby §8- Set the main lobby");
			s.sendMessage("§7/cm set sign §8- Set the game sign");
			s.sendMessage("§7/cm set size <Size> §8- Set the arena size");
			s.sendMessage("§7/cm set onwin <onWin> §8- Set the on win command (%winner% = Winner's name, %winneruuid% = Winner's UUID)");
			s.sendMessage("§7/cm set minplayers <minPlayers> §8- Set the minimum player count");
			s.sendMessage("§7/cm set powerups <enablePowerups> §8- Enable/Disable powerups for an arena");
			s.sendMessage("§7/cm set defaultmainlobby §8- Set the default main lobby");
			s.sendMessage("§7/cm set layouts <Layout name,Layout name 2,Layout name 3,...> §8- Set the arena layouts");
			s.sendMessage("§7/cm discard §8- Discard all changes");
			s.sendMessage("§7/cm save §8- Save an arena");
			s.sendMessage("§7/cm delete <Name> §8- Delete an arena");
			s.sendMessage("§7/cm list §8- Lists all arenas");
			s.sendMessage("§7/cm info <Arena> §8- Shows information about an arena");
			s.sendMessage("§7§l-----------------");
		}
		
		s.sendMessage("§7/cm help [command...] §8- Shows information about a command");
		
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
	
	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return new CrazyMazeWorldGenerator();
	}
	
}
