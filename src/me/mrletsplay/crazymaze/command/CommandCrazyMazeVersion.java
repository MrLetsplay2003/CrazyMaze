package me.mrletsplay.crazymaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mrletsplay.crazymaze.main.Config;
import me.mrletsplay.crazymaze.main.CrazyMaze;
import me.mrletsplay.crazymaze.main.Message;
import me.mrletsplay.crazymaze.main.UpdateChecker;
import me.mrletsplay.crazymaze.main.UpdateChecker.Result;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommand;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommandSender;
import me.mrletsplay.mrcore.command.CommandInvokedEvent;

public class CommandCrazyMazeVersion extends BukkitCommand {
	
	public CommandCrazyMazeVersion() {
		super("version");
		
		setDescription("Shows the current version and checks for an update (if enabled)");
		setUsage("/crazymaze version");
	}
	
	@Override
	public void action(CommandInvokedEvent event) {
		CommandSender sender = ((BukkitCommandSender) event.getSender()).getBukkitSender();
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(Config.getMessage(Message.COMMAND_CONSOLE));
			return;
		}
		
		Player p = (Player) sender;
		
		if (!p.hasPermission(Config.PERM_NOTIFY_UPDATE)) {
			p.sendMessage(Config.getMessage(Message.COMMAND_NO_PERMISSION));
			return;
		}
		
		p.sendMessage("Current CrazyMaze version: §7" + CrazyMaze.pluginVersion);
		if(Config.enableUpdateCheck && Config.updateCheckOnCommand) {
			Result r = UpdateChecker.checkForUpdate();
			if(r.updAvailable) {
				UpdateChecker.sendUpdateMessage(r, p);
			}else {
				p.sendMessage("§aYou are using the newest version of CrazyMaze");
			}
		}
	}

}
