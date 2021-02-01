package me.mrletsplay.crazymaze.command;

import java.util.Collections;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mrletsplay.crazymaze.arena.Arena;
import me.mrletsplay.crazymaze.main.Config;
import me.mrletsplay.crazymaze.main.Message;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommand;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommandSender;
import me.mrletsplay.mrcore.command.event.CommandInvokedEvent;

public class CommandCrazyMazeInfo extends BukkitCommand {
	
	public CommandCrazyMazeInfo() {
		super("info");
		
		setDescription("Shows information about an arena");
		setUsage("/crazymaze info <name>");
		
		setTabCompleter(event -> {
			if(event.getArgs().length != 0) return Collections.emptyList();
			
			return Config.arenas.stream()
					.map(Arena::getName)
					.collect(Collectors.toList());
		});
	}
	
	@Override
	public void action(CommandInvokedEvent event) {
		CommandSender sender = ((BukkitCommandSender) event.getSender()).getBukkitSender();
		String[] args = event.getParsedCommand().getArguments();
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(Config.getMessage(Message.COMMAND_CONSOLE));
			return;
		}
		
		Player p = (Player) sender;
		
		if(!p.hasPermission(Config.PERM_ADMIN)) {
			p.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
			return;
		}
		
		if(args.length != 1) {
			sendCommandInfo(event.getSender());
			return;
		}
		
		if(!Config.existsArena(args[0])) {
			p.sendMessage(Config.getMessage(Message.COMMAND_ARENA_DOESNT_EXIST));
			return;
		}
		
		Arena a = Config.getByName(args[0]);
		p.sendMessage(Config.prefix+" §eInfo");
		p.sendMessage("§6Name: §7" + a.getName());
		p.sendMessage("§6Players: §7Min: " + a.getMinPlayers()+" §8| §7Max: " + a.getMaxPlayers());
		p.sendMessage("§6Size: §7" + a.getSize() + "x");
		p.sendMessage("§6Powerups: §7" + a.powerupsEnabled());
		if(a.getOnWin()!=null) p.sendMessage("§6OnWin: §7/" + a.getOnWin().replace("%winner%", "§8<Winner>§7").replace("%winneruuid%", "§8<WinnerUUID>§7"));
		p.sendMessage("§6Layouts: §7" + a.getLayouts().stream().map(l -> l.getName()).collect(Collectors.toList()));
	}

}
