package me.mrletsplay.crazymaze.command;

import java.util.Collections;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mrletsplay.crazymaze.game.Game;
import me.mrletsplay.crazymaze.game.Games;
import me.mrletsplay.crazymaze.main.Config;
import me.mrletsplay.crazymaze.main.Message;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommand;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommandSender;
import me.mrletsplay.mrcore.command.CommandInvokedEvent;

public class CommandCrazyMazeKick extends BukkitCommand {
	
	public CommandCrazyMazeKick() {
		super("kick");
		
		setDescription("Kick a player from their current game");
		setUsage("/crazymaze kick <Player>");
		
		setTabCompleter((command, label, args) -> {
			if(args.length != 0) return Collections.emptyList();
			
			return Games.getGames().stream()
					.flatMap(g -> g.getPlayers().stream())
					.map(p -> p.getName())
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
		
		if(!p.hasPermission(Config.PERM_KICK)) {
			p.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
			return;
		}
		
		if(args.length != 1) {
			sendCommandInfo(event.getSender());
			return;
		}
		
		Player pl = Bukkit.getPlayer(args[0]);
		if(pl==null) {
			p.sendMessage(Config.getMessage(Message.COMMAND_KICK_PLAYER_NOT_ONLINE));
			return;
		}
		
		if(!Games.isInGame(pl)) {
			p.sendMessage(Config.getMessage(Message.COMMAND_KICK_PLAYER_NOT_INGAME));
			return;
		}
		
		Game g = Games.getGame(pl);
		pl.teleport(g.getArena().getMainLobby());
		g.removePlayer(pl);
		p.sendMessage(Config.getMessage(Message.COMMAND_KICK_KICKED, "player", pl.getName()));
	}

}
