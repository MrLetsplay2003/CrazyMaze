package me.mrletsplay.crazymaze.command.set;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mrletsplay.crazymaze.arena.Arena;
import me.mrletsplay.crazymaze.main.Config;
import me.mrletsplay.crazymaze.main.CrazyMaze;
import me.mrletsplay.crazymaze.main.Message;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommand;
import me.mrletsplay.mrcore.bukkitimpl.command.BukkitCommandSender;
import me.mrletsplay.mrcore.command.event.CommandInvokedEvent;

public class CommandCrazyMazeSetSign extends BukkitCommand {

	public CommandCrazyMazeSetSign() {
		super("sign");
		
		setDescription("Sets the sign you're currently looking at to be a \"Join Arena\" sign");
		setUsage("/crazymaze set sign");
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
		Block block = p.getTargetBlock((Set<Material>)null, 100);
		Location bl = block.getLocation();
		
		if(block.getState() instanceof Sign) {
			a.addSignLocation(bl);
			a.updateSign();
			p.sendMessage(Config.getMessage(Message.COMMAND_SET_SIGN));
			return;
		}else {
			p.sendMessage(Config.getMessage(Message.COMMAND_SET_SIGN_NOT_LOOKING));
			return;
		}
	}

}
