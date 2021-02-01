package me.mrletsplay.crazymaze.command;

import java.util.Collections;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mrletsplay.crazymaze.arena.Arena;
import me.mrletsplay.crazymaze.main.Config;
import me.mrletsplay.crazymaze.main.CrazyMaze;
import me.mrletsplay.crazymaze.main.Message;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommand;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommandSender;
import me.mrletsplay.mrcore.command.event.CommandInvokedEvent;

public class CommandCrazyMazeCreate extends BukkitCommand {

	public CommandCrazyMazeCreate() {
		super("create");
		
		setDescription("Create an arena");
		setUsage("/crazymaze create <name> [arena to copy]");
		
		setTabCompleter((event) -> {
			if(event.getArgs().length != 1) return Collections.emptyList();
			
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
		
		if(!sender.hasPermission(Config.PERM_ADMIN)) {
			sender.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
			return;
		}
		
		if(args.length != 1 && args.length != 2) {
			sendCommandInfo(event.getSender());
			return;
		}
		
		if(CrazyMaze.arenas.containsKey(p.getUniqueId())) {
			p.sendMessage(Config.getMessage(Message.COMMAND_ALREADY_EDITING));
			return;
		}
		
		String name = args[0];
		if(Config.existsArena(name)) {
			p.sendMessage(Config.getMessage(Message.COMMAND_ARENA_ALREADY_EXISTS));
			return;
		}
		
		Arena a = new Arena(name);
		if(args.length == 2) {
			if(!Config.existsArena(args[1])) {
				p.sendMessage(Config.getMessage(Message.COMMAND_ARENA_DOESNT_EXIST));
				return;
			}
			
			Arena b = Config.getByName(args[1]);
			a.setEnablePowerups(b.powerupsEnabled());
			a.setGameLobby(b.getGameLobby());
			a.setMainLobby(b.getMainLobby());
			a.setMaxPlayers(b.getMaxPlayers());
			a.setMinPlayers(b.getMinPlayers());
			a.setOnWin(b.getOnWin());
			a.setSize(b.getSize());
			p.sendMessage(Config.getMessage(Message.COMMAND_PROPERTIES_IMPORTED, "arena", args[1]));
		}
		
		CrazyMaze.arenas.put(p.getUniqueId(), a);
		p.sendMessage(Config.getMessage(Message.COMMAND_ARENA_CREATED));
	}
	
}
