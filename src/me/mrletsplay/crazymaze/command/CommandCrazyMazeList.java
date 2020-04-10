package me.mrletsplay.crazymaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mrletsplay.crazymaze.arena.Arena;
import me.mrletsplay.crazymaze.main.Config;
import me.mrletsplay.crazymaze.main.Message;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommand;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommandSender;
import me.mrletsplay.mrcore.command.CommandInvokedEvent;

public class CommandCrazyMazeList extends BukkitCommand {
	
	public CommandCrazyMazeList() {
		super("list");
		
		setDescription("Lists all arenas");
		setUsage("/crazymaze list");
	}
	
	@Override
	public void action(CommandInvokedEvent event) {
		CommandSender sender = ((BukkitCommandSender) event.getSender()).getBukkitSender();
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(Config.getMessage(Message.COMMAND_CONSOLE));
			return;
		}
		
		Player p = (Player) sender;
		
		if(!p.hasPermission(Config.PERM_ADMIN)) {
			p.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
			return;
		}
		
		p.sendMessage(Config.getMessage(Message.COMMAND_LIST_TITLE));
		for(Arena a : Config.arenas) {
			p.sendMessage(Config.getMessage(Message.COMMAND_LIST_ENTRY, "name", a.getName(), "size", ""+a.getSize(), "powerups", ""+a.powerupsEnabled()));
		}
	}

}
