package me.mrletsplay.crazymaze.command.set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mrletsplay.crazymaze.arena.Arena;
import me.mrletsplay.crazymaze.main.Config;
import me.mrletsplay.crazymaze.main.CrazyMaze;
import me.mrletsplay.crazymaze.main.Message;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommand;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommandSender;
import me.mrletsplay.mrcore.command.event.CommandInvokedEvent;

public class CommandCrazyMazeSetLobby extends BukkitCommand {

	public CommandCrazyMazeSetLobby() {
		super("lobby");
		
		setDescription("Sets an arena's waiting lobby players will be teleported to before the game starts");
		setUsage("/crazymaze set lobby");
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
		
		Arena a = CrazyMaze.arenas.get(p.getUniqueId());
		a.setGameLobby(p.getLocation());
		CrazyMaze.arenas.put(p.getUniqueId(), a);
		p.sendMessage(Config.getMessage(Message.COMMAND_SET_LOBBY));
	}

}
