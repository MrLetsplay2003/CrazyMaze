package me.mrletsplay.crazymaze.command.set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mrletsplay.crazymaze.arena.Arena;
import me.mrletsplay.crazymaze.main.Config;
import me.mrletsplay.crazymaze.main.CrazyMaze;
import me.mrletsplay.crazymaze.main.Message;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommand;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommandSender;
import me.mrletsplay.mrcore.command.CommandInvokedEvent;

public class CommandCrazyMazeSetMainLobby extends BukkitCommand {

	public CommandCrazyMazeSetMainLobby() {
		super("mainlobby");
		
		setDescription("Sets an arena's mainlobby players will be teleported to after the game ends");
		setUsage("/crazymaze set mainlobby");
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
		a.setMainLobby(p.getLocation());
		CrazyMaze.arenas.put(p.getUniqueId(), a);
		p.sendMessage(Config.getMessage(Message.COMMAND_SET_MAINLOBBY));
	}

}
