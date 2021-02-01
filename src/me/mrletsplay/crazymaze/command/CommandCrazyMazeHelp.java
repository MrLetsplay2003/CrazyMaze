package me.mrletsplay.crazymaze.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import me.mrletsplay.crazymaze.main.Config;
import me.mrletsplay.crazymaze.main.Message;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommand;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommandSender;
import me.mrletsplay.mrcore.command.Command;
import me.mrletsplay.mrcore.command.event.CommandInvokedEvent;

public class CommandCrazyMazeHelp extends BukkitCommand {

	public CommandCrazyMazeHelp() {
		super("help");
		
		setDescription("Shows help about a command");
		setUsage("/crazymaze help [command...]");
		
		setTabCompleter(event -> {
			Command c = getCommand(event.getArgs());
			
			if(c == null) return Collections.emptyList();
			
			return c.getSubCommands().stream()
					.map(Command::getName)
					.collect(Collectors.toList());
		});
	}
	
	@Override
	public void action(CommandInvokedEvent event) {
		CommandSender sender = ((BukkitCommandSender) event.getSender()).getBukkitSender();
		String[] args = event.getParsedCommand().getArguments();
		
		Command c = getCommand(args);
		
		if(c == null) {
			sender.sendMessage(Config.getMessage(Message.COMMAND_HELP_UNKNOWN_COMMAND));
			return;
		}
		
		event.getSender().sendMessage(Config.prefix + " §cHelp");
		c.sendCommandInfo(event.getSender());
	}
	
	private static Command getCommand(String... command) {
		Command c = CommandCrazyMaze.INSTANCE;
		List<String> cmds = new ArrayList<>(Arrays.asList(command));
		
		while(!cmds.isEmpty()) {
			String cmd = cmds.remove(0);
			c = c.getSubCommand(cmd);
			if(c == null) return null;
		}
		
		return c;
	}
	
}
