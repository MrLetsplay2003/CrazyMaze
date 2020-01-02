package me.mrletsplay.crazymaze.main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import me.mrletsplay.crazymaze.main.Tools.AbsoluteDirection;

public class Events implements Listener{

	@EventHandler
	public void onIn(PlayerInteractEvent e) {
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && (e.getClickedBlock().getType().equals(Material.SIGN_POST) || e.getClickedBlock().getType().equals(Material.WALL_SIGN))) {
			Sign s = (Sign) e.getClickedBlock().getState();
			if(s.getLine(0).equals(Config.signLayout0)) {
				if(s.getLine(1).equals(Config.signLayout1_2)) { 
					if(Games.isInGame(e.getPlayer())) {
						Game g = Games.getGame(e.getPlayer());
						if(g.getStage() > 0) {
							Player winner = e.getPlayer();
							for(Player p : g.getPlayers()) {
								p.sendMessage(Config.getMessage("ingame.end.win", "winner", winner.getDisplayName()));
							}
							g.stop(false);
							if(g.getArena().getOnWin()!=null) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), g.getArena().getOnWin()
									.replace("%winner%", winner.getName())
									.replace("%winneruuid%", winner.getUniqueId().toString()));
						}
					}else {
						e.getPlayer().sendMessage(Config.getMessage("other.not-ingame"));
					}
				}else {
					if(Games.isInGame(e.getPlayer())) {
						e.getPlayer().sendMessage(Config.getMessage("other.already-ingame"));
						return;
					}
					Game g = Games.getSign(s.getLocation());
					if(g!=null) {
						if(g.getStage() == 1 || g.getStage() == 2) {
							e.getPlayer().sendMessage(Config.getMessage("other.game-running"));
							return;
						}
						if(g.getStage() == 3) {
							e.getPlayer().sendMessage(Config.getMessage("other.game-restarting"));
							return;
						}
						if(g.getPlayers().size()>=g.getArena().getMaxPlayers()) {
							e.getPlayer().sendMessage(Config.getMessage("other.game-full"));
							return;
						}
						g.addPlayer(e.getPlayer());
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getItem()!=null && e.getItem().hasItemMeta() && e.getItem().getItemMeta().hasDisplayName()) {
			String dName = e.getItem().getItemMeta().getDisplayName();
			if(Games.isInGame(e.getPlayer())) {
				Game g = Games.getGame(e.getPlayer());
				Player p = e.getPlayer();
				if(g.getStage()==2) {
					if(Config.matchesItem(e.getItem(), Powerup.PASS_WALL.item)) {
						if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().equals(g.getLayout().getWalls())) {
							if(p.getLocation().distance(e.getClickedBlock().getLocation())<2) {
								Vector f = Tools.getField(p.getLocation(), g);
								Vector f2 = Tools.add(f, Tools.get(e.getPlayer().getLocation().getDirection()), 1);
								if(f2.getBlockX()>=0 && f2.getBlockZ()>=0 && f2.getBlockX() < g.getArena().getSize() && f2.getBlockZ() < g.getArena().getSize()) {
									p.teleport(new Location(Config.cmWorld, f2.getBlockX()*g.getPanel().sc+1, Config.MAZE_Y+1, f2.getBlockZ()*g.getPanel().sc+1).setDirection(p.getLocation().getDirection()));
									if(p.getItemInHand().getAmount()>1) {
										p.getItemInHand().setAmount(p.getItemInHand().getAmount()-1);
									}else {
										p.setItemInHand(null);
									}
								}else {
									p.sendMessage(Config.getMessage("ingame.cannot-pass"));
								}
							}
						}
					}else if(Config.matchesItem(e.getItem(), Powerup.CREATE_BARRIER.item)) {
						if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
							Vector f = Tools.getField(p.getLocation(), g);
							AbsoluteDirection dir = Tools.get(e.getPlayer().getLocation().getDirection()).inverse();
							Vector f2 = Tools.add(f, dir, 1);
							Location fLoc = new Location(Config.cmWorld, f2.getBlockX()*g.getPanel().sc, Config.MAZE_Y+1, f2.getBlockZ()*g.getPanel().sc);
							if(f2.getBlockX()>=0 && f2.getBlockZ()>=0 && f2.getBlockX() < g.getArena().getSize() && f2.getBlockZ() < g.getArena().getSize()) {
								g.getPanel().fill(fLoc.getBlockX(), fLoc.getBlockY(), fLoc.getBlockZ(), g.getPanel().scale, Maze3D.wallHeight, g.getPanel().scale, Material.WOOL, Config.cmWorld, 0, () ->{
									g.buildTaskIDs.add(Bukkit.getScheduler().runTaskLater(Main.pl, () -> {
										g.getPanel().fill(fLoc.getBlockX(), fLoc.getBlockY(), fLoc.getBlockZ(), g.getPanel().scale, Maze3D.wallHeight, g.getPanel().scale, Material.AIR, Config.cmWorld, 0, null);
									}, Config.wallTime*20L).getTaskId());
									if(p.getItemInHand().getAmount()>0) {
										p.getItemInHand().setAmount(p.getItemInHand().getAmount()-1);
									}
								});
							}else {
								p.sendMessage(Config.getMessage("ingame.cannot-create-barrier"));
							}
						}
					}else if(Config.matchesItem(e.getItem(), Powerup.PROTECT.item)) {
						if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
							
						}
					}else if(Config.matchesItem(e.getItem(), Powerup.TOPDOWN_VIEW.item)) {
						if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
							
						}
					}
				}else if(g.getStage()==0) {
					if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)){
						if(dName.equals(Config.gameOptions.getItemMeta().getDisplayName())) {
							p.openInventory(GUIs.getVotingGUI(g.getArena()).getForPlayer(p));
						}else if(dName.equals(Config.quitGame.getItemMeta().getDisplayName())) {
							g.removePlayer(p);
							p.sendMessage(Config.getMessage("other.game-left"));
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if(Games.isInGame(e.getPlayer())) {
			Game g = Games.getGame(e.getPlayer());
			g.removePlayer(e.getPlayer());
		}
		if(Main.arenas.containsKey(e.getPlayer())) {
			Main.arenas.remove(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onBlBr(BlockBreakEvent e) {
		if(Games.getSign(e.getBlock().getLocation())!=null) {
			e.setCancelled(true);
		}
		if(Games.isInGame(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlPl(BlockPlaceEvent e) {
		if(Games.isInGame(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		if(Games.isInGame((Player) e.getEntity())) {
			e.setCancelled(true);
		}
	}
	
	
	@EventHandler
	public void onEnDByEn(EntityDamageByEntityEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		if(Games.isInGame((Player) e.getEntity())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onFood(FoodLevelChangeEvent e) {
		if(Games.isInGame((Player) e.getEntity())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		if(Games.isInGame(e.getPlayer())) {
			Player p = e.getPlayer();
			Game g = Games.getGame(p);
			if(g.getStage() == 0) e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if(e.getPlayer().hasPermission(Config.PERM_NOTIFY_UPDATE)){
			if(Config.enable_update_check && Config.update_check_on_join){
				UpdateChecker.Result res = UpdateChecker.checkForUpdate();
				if(res.updAvailable) {
					UpdateChecker.sendUpdateMessage(res, e.getPlayer());
				}
			}
		}
	}
	
}
