package me.mrletsplay.crazymaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mrletsplay.crazymaze.game.Game;
import me.mrletsplay.crazymaze.game.GameStage;
import me.mrletsplay.crazymaze.game.Games;
import me.mrletsplay.crazymaze.main.Config;
import me.mrletsplay.crazymaze.main.Message;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommand;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommandSender;
import me.mrletsplay.mrcore.command.event.CommandInvokedEvent;

public class CommandCrazyMazeStart extends BukkitCommand {
	
	public CommandCrazyMazeStart() {
		super("start");
		
		setDescription("Starts the game you're currently in");
		setUsage("/crazymaze start");
	}
	
	@Override
	public void action(CommandInvokedEvent event) {
		CommandSender sender = ((BukkitCommandSender) event.getSender()).getBukkitSender();
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(Config.getMessage(Message.COMMAND_CONSOLE));
			return;
		}
		
		Player p = (Player) sender;
		
		if(!p.hasPermission(Config.PERM_START)) {
			p.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
			return;
		}
		
		if(!Games.isInGame(p)) {
			p.sendMessage(Config.getMessage(Message.OTHER_NOT_INGAME));
			return;
		}
		
		Game g = Games.getGame(p);
		if(!g.getStage().equals(GameStage.WAITING)) {
			p.sendMessage(Config.getMessage(Message.COMMAND_START_ALREADY_RUNNING));
			return;
		}
		
		if(g.getPlayers().size() < g.getArena().getMinPlayers()) {
			p.sendMessage(Config.getMessage(Message.COMMAND_START_NOT_ENOUGH_PLAYERS));
			return;
		}
		
		if(g.getCountdown()<=Config.countdownMaxPlayers) {
			p.sendMessage(Config.getMessage(Message.COMMAND_START_ALREADY_STARTING));
			return;
		}
		
		g.setCountdown(Config.countdownMaxPlayers);
		p.sendMessage(Config.getMessage(Message.COMMAND_START_SKIPPED));
	}

}
