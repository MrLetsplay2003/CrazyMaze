package me.mrletsplay.crazymaze.command.set;

import java.util.Arrays;
import java.util.Collections;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mrletsplay.crazymaze.arena.Arena;
import me.mrletsplay.crazymaze.main.Config;
import me.mrletsplay.crazymaze.main.CrazyMaze;
import me.mrletsplay.crazymaze.main.Message;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommand;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommandSender;
import me.mrletsplay.mrcore.command.CommandInvokedEvent;

public class CommandCrazyMazeSetPowerups extends BukkitCommand {

	public CommandCrazyMazeSetPowerups() {
		super("powerups");
		
		setDescription("Sets whether powerups are enabled for an arena");
		setUsage("/crazymaze set powerups <true/false>");
		
		setTabCompleter((sender, command, label, args) -> {
			return args.length == 0 ? Arrays.asList("true", "false") : Collections.emptyList();
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
		
		if(args.length != 1) {
			sendCommandInfo(event.getSender());
			return;
		}
		
		Arena a = CrazyMaze.arenas.get(p.getUniqueId());
		boolean b = Boolean.valueOf(args[0]);
		a.setEnablePowerups(b);
		CrazyMaze.arenas.put(p.getUniqueId(), a);
		p.sendMessage(Config.getMessage(Message.COMMAND_SET_POWERUPS, "state", ""+b));
	}

}
