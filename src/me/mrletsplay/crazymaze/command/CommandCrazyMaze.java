package me.mrletsplay.crazymaze.command;

import me.mrletsplay.crazymaze.command.set.CommandCrazyMazeSet;
import me.mrletsplay.crazymaze.main.CrazyMaze;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommand;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommandSender;
import me.mrletsplay.mrcore.command.event.CommandInvokedEvent;

public class CommandCrazyMaze extends BukkitCommand {
	
	public static final CommandCrazyMaze INSTANCE = new CommandCrazyMaze();

	private CommandCrazyMaze() {
		super("crazymaze");
		addAlias("cm");
		
		setDescription("Manage everything related to CrazyMaze");
		setUsage("/crazymaze [...]");
		
		addSubCommand(new CommandCrazyMazeCreate());
		addSubCommand(new CommandCrazyMazeDelete());
		addSubCommand(new CommandCrazyMazeDiscard());
		addSubCommand(new CommandCrazyMazeEdit());
		addSubCommand(new CommandCrazyMazeHelp());
		addSubCommand(new CommandCrazyMazeInfo());
		addSubCommand(new CommandCrazyMazeKick());
		addSubCommand(new CommandCrazyMazeLeave());
		addSubCommand(new CommandCrazyMazeList());
		addSubCommand(new CommandCrazyMazeSave());
		addSubCommand(new CommandCrazyMazeSet());
		addSubCommand(new CommandCrazyMazeStart());
		addSubCommand(new CommandCrazyMazeVersion());
	}

	@Override
	public void action(CommandInvokedEvent event) {
		CrazyMaze.sendCommandHelp(((BukkitCommandSender) event.getSender()).getBukkitSender());
	}

}
