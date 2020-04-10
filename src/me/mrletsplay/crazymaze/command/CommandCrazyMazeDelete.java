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
import me.mrletsplay.mrcore.command.CommandInvokedEvent;

public class CommandCrazyMazeDelete extends BukkitCommand {
	
	public CommandCrazyMazeDelete() {
		super("delete");
		
		setDescription("Delete an arena");
		setUsage("/crazymaze delete <name>");
		
		setTabCompleter((command, label, args) -> {
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
		
		if(args.length != 1) {
			sendCommandInfo(event.getSender());
			return;
		}
		
		Arena a = Config.getByName(args[0]);
		if(a == null) {
			p.sendMessage(Config.getMessage(Message.COMMAND_ARENA_DOESNT_EXIST));
			return;
		}
		
		if(CrazyMaze.arenas.containsValue(a)) {
			p.sendMessage(Config.getMessage(Message.COMMAND_ARENA_IS_BEING_EDITED));
			return;
		}
		
		Config.deleteArena(a);
		p.sendMessage(Config.getMessage(Message.COMMAND_ARENA_DELETED, "arena", a.getName()));
	}

}
