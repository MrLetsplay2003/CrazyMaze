package me.mrletsplay.crazymaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mrletsplay.crazymaze.main.Config;
import me.mrletsplay.crazymaze.main.CrazyMaze;
import me.mrletsplay.crazymaze.main.Message;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommand;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommandSender;
import me.mrletsplay.mrcore.command.CommandInvokedEvent;

public class CommandCrazyMazeDiscard extends BukkitCommand {
	
	public CommandCrazyMazeDiscard() {
		super("discard");
		
		setDescription("Discard the changes you've made to the arena you're currently editing");
		setUsage("/crazymaze discard");
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
		
		if(!CrazyMaze.arenas.containsKey(p.getUniqueId())) {
			p.sendMessage(Config.getMessage(Message.COMMAND_NOT_EDITING));
			return;
		}
		
		CrazyMaze.arenas.remove(p.getUniqueId());
		if(CrazyMaze.backupArenas.containsKey(p.getUniqueId())) {
			Config.arenas.add(CrazyMaze.backupArenas.remove(p.getUniqueId()));
		}
		
		p.sendMessage(Config.getMessage(Message.COMMAND_CHANGES_DISCARDED));
	}

}
