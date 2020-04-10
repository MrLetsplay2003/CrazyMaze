package me.mrletsplay.crazymaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mrletsplay.crazymaze.game.Game;
import me.mrletsplay.crazymaze.game.Games;
import me.mrletsplay.crazymaze.main.Config;
import me.mrletsplay.crazymaze.main.Message;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommand;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommandSender;
import me.mrletsplay.mrcore.command.CommandInvokedEvent;

public class CommandCrazyMazeLeave extends BukkitCommand {
	
	public CommandCrazyMazeLeave() {
		super("leave");
		
		setDescription("Leave your current game");
		setUsage("/crazymaze leave");
	}
	
	@Override
	public void action(CommandInvokedEvent event) {
		CommandSender sender = ((BukkitCommandSender) event.getSender()).getBukkitSender();
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(Config.getMessage(Message.COMMAND_CONSOLE));
			return;
		}
		
		Player p = (Player) sender;
		
		if(!Games.isInGame(p)) {
			p.sendMessage(Config.getMessage(Message.OTHER_NOT_INGAME));
			return;
		}
		
		Game g = Games.getGame(p);
		g.removePlayer(p);
		p.sendMessage(Config.getMessage(Message.OTHER_GAME_LEFT));
	}

}
