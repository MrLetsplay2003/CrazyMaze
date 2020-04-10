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

public class CommandCrazyMazeSetMinPlayers extends BukkitCommand {

	public CommandCrazyMazeSetMinPlayers() {
		super("minplayers");
		
		setDescription("Sets the minimum amount of players an arena needs so the game can start");
		setUsage("/crazymaze set minplayers <amount>");
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
		try {
			int num = Integer.parseInt(args[0]);
			if(num >= 1) {
				if(num <= a.getMaxPlayers()) {
					a.setMinPlayers(num);
					a.updateSign();
					CrazyMaze.arenas.put(p.getUniqueId(), a);
					p.sendMessage(Config.getMessage(Message.COMMAND_SET_MIN_PLAYERS, "num", ""+num));
				}else {
					p.sendMessage(Config.getMessage(Message.COMMAND_SET_TOO_MANY_PLAYERS));
				}
			}else {
				p.sendMessage(Config.getMessage(Message.COMMAND_SET_MORE_PLAYERS));
			}
		}catch(Exception e) {
			e.printStackTrace();
			sendCommandInfo(event.getSender());
		}
	}

}
