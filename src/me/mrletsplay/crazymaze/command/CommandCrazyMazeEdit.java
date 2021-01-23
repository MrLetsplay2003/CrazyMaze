package me.mrletsplay.crazymaze.command;

import java.util.Collections;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mrletsplay.crazymaze.arena.Arena;
import me.mrletsplay.crazymaze.game.Game;
import me.mrletsplay.crazymaze.game.Games;
import me.mrletsplay.crazymaze.main.Config;
import me.mrletsplay.crazymaze.main.CrazyMaze;
import me.mrletsplay.crazymaze.main.Message;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommand;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommandSender;
import me.mrletsplay.mrcore.command.CommandInvokedEvent;

public class CommandCrazyMazeEdit extends BukkitCommand {

	public CommandCrazyMazeEdit() {
		super("edit");
		
		setDescription("Edit an arena");
		setUsage("/crazymaze edit <name>");
		
		setTabCompleter((sender, command, label, args) -> {
			if(args.length != 0) return Collections.emptyList();
			
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
		
		if(CrazyMaze.arenas.containsKey(p.getUniqueId())) {
			p.sendMessage(Config.getMessage(Message.COMMAND_ALREADY_EDITING));
			return;
		}
		
		Arena a = Config.getByName(args[0]);
		if(a == null) {
			p.sendMessage(Config.getMessage(Message.COMMAND_ARENA_DOESNT_EXIST));
			return;
		}
		
		Game g = Games.getGame(a);
		if(g != null) g.stop(false, Config.getMessage(Message.INGAME_KICK_EDIT));
		Config.arenas.remove(a);
		CrazyMaze.arenas.put(p.getUniqueId(), a);
		CrazyMaze.backupArenas.put(p.getUniqueId(), a.clone());
		p.sendMessage(Config.getMessage(Message.COMMAND_NOW_EDITING, "arena", args[0]));
	}
	
}
