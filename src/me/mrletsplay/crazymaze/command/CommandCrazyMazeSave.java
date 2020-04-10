package me.mrletsplay.crazymaze.command;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mrletsplay.crazymaze.main.Config;
import me.mrletsplay.crazymaze.main.CrazyMaze;
import me.mrletsplay.crazymaze.main.Message;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommand;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommandSender;
import me.mrletsplay.mrcore.command.CommandInvokedEvent;

public class CommandCrazyMazeSave extends BukkitCommand {
	
	public CommandCrazyMazeSave() {
		super("save");
		
		setDescription("Saves the changes you've made to the arena you're currently editing");
		setUsage("/crazymaze save");
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
		
		List<String> ss = Config.saveArena(CrazyMaze.arenas.get(p.getUniqueId()));
		if(!ss.isEmpty()) {
			p.sendMessage(Config.getMessage(Message.COMMAND_SETUP_INCOMPLETE, "missing", ""+ss));
		}else{
			p.sendMessage(Config.getMessage(Message.COMMAND_ARENA_SAVED));
			CrazyMaze.arenas.remove(p.getUniqueId());
			return;
		}
	}

}
