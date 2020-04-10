package me.mrletsplay.crazymaze.command.set;

import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommand;
import me.mrletsplay.mrcore.command.CommandInvokedEvent;

public class CommandCrazyMazeSet extends BukkitCommand {

	public CommandCrazyMazeSet() {
		super("set");
		setDescription("Set an arena property");
		setUsage("/crazymaze set <property> [value]");

		addSubCommand(new CommandCrazyMazeSetDefaultMainLobby());
		addSubCommand(new CommandCrazyMazeSetLayouts());
		addSubCommand(new CommandCrazyMazeSetLobby());
		addSubCommand(new CommandCrazyMazeSetMainLobby());
		addSubCommand(new CommandCrazyMazeSetMaxPlayers());
		addSubCommand(new CommandCrazyMazeSetMinPlayers());
		addSubCommand(new CommandCrazyMazeSetOnWin());
		addSubCommand(new CommandCrazyMazeSetPowerups());
		addSubCommand(new CommandCrazyMazeSetSign());
		addSubCommand(new CommandCrazyMazeSetSize());
	}
	
	@Override
	public void action(CommandInvokedEvent event) {
		sendCommandInfo(event.getSender());
	}

}
