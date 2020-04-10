package me.mrletsplay.crazymaze.command.set;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mrletsplay.crazymaze.arena.Arena;
import me.mrletsplay.crazymaze.main.Config;
import me.mrletsplay.crazymaze.main.CrazyMaze;
import me.mrletsplay.crazymaze.main.Message;
import me.mrletsplay.crazymaze.main.Tools;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommand;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommandSender;
import me.mrletsplay.mrcore.command.CommandInvokedEvent;

public class CommandCrazyMazeSetLayouts extends BukkitCommand {

	public CommandCrazyMazeSetLayouts() {
		super("layouts");
		
		setDescription("Sets an arena's layouts which can be voted for before the game starts");
		setUsage("/crazymaze set layouts [layout1 layout2...]");
		
		setTabCompleter((command, label, args) -> {
			return Config.getLayouts().stream()
					.filter(l -> Arrays.binarySearch(args, l) < 0)
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
		
		if(!CrazyMaze.arenas.containsKey(p.getUniqueId())) {
			p.sendMessage(Config.getMessage(Message.COMMAND_NOT_EDITING));
			return;
		}
		
		if(args.length < 1) {
			sendCommandInfo(event.getSender());
			return;
		}
		
		Arena a = CrazyMaze.arenas.get(p.getUniqueId());
		List<String> layouts = Arrays.asList(args);
		List<String> dups = Tools.getDuplicates(layouts);
		
		if(!dups.isEmpty()) {
			p.sendMessage(Config.getMessage(Message.COMMAND_SET_LAYOUTS_DUPLICATE, "layouts", dups.toString()));
			return;
		}
		
		List<String> nF = a.setLayouts(layouts);
		if(nF.isEmpty()) {
			CrazyMaze.arenas.put(p.getUniqueId(), a);
			p.sendMessage(Config.getMessage(Message.COMMAND_SET_LAYOUTS, "layouts", layouts.toString()));
		}else {
			p.sendMessage(Config.getMessage(Message.COMMAND_SET_LAYOUTS_NOT_FOUND, "layouts", nF.toString()));
		}
	}

}
