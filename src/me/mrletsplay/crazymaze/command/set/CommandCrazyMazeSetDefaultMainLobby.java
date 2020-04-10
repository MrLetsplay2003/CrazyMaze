package me.mrletsplay.crazymaze.command.set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mrletsplay.crazymaze.main.Config;
import me.mrletsplay.crazymaze.main.Message;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommand;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommandSender;
import me.mrletsplay.mrcore.command.CommandInvokedEvent;

public class CommandCrazyMazeSetDefaultMainLobby extends BukkitCommand {

	public CommandCrazyMazeSetDefaultMainLobby() {
		super("defaultmainlobby");
		
		setDescription("Sets the default main lobby which will be used when no mainlobby is set for an arena");
		setUsage("/crazymaze set defaultmainlobby");
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
		
		Config.config.set("settings.defaultmainlobby", p.getLocation());
		Config.saveConfig();
		p.sendMessage(Config.getMessage(Message.COMMAND_SET_DEFAULTMAINLOBBY));
	}

}
