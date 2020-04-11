package me.mrletsplay.crazymaze.main;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.mrletsplay.crazymaze.arena.Arena;
import me.mrletsplay.crazymaze.game.Game;
import me.mrletsplay.crazymaze.game.GameStage;
import me.mrletsplay.crazymaze.game.Games;

public class Events implements Listener{

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getState() instanceof Sign) {
			Sign s = (Sign) e.getClickedBlock().getState();
			Game game = Games.getGame(e.getPlayer());
			if(game != null && game.getStage().equals(GameStage.RUNNING)
					&& game.getBuiltArena().getBuilderProperties().getFinishSignCell().equals(Tools.getCell(game, e.getClickedBlock().getLocation()))) {
				Player winner = e.getPlayer();
				for(Player p : game.getPlayers()) {
					p.sendMessage(Config.getMessage(Message.INGAME_END_WIN, "winner", winner.getDisplayName()));
				}
				
				game.stop(false);
				if(game.getArena().getOnWin() != null) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), game.getArena().getOnWin()
						.replace("%winner%", winner.getName())
						.replace("%winneruuid%", winner.getUniqueId().toString()));
			}else {
				Game g = Games.getSign(s.getLocation());
				if(g != null) {
					if(game != null) {
						e.getPlayer().sendMessage(Config.getMessage(Message.OTHER_ALREADY_INGAME));
						return;
					}
					
					if(g.getStage().equals(GameStage.GENERATING) || g.getStage().equals(GameStage.RUNNING)) {
						e.getPlayer().sendMessage(Config.getMessage(Message.OTHER_GAME_RUNNING));
						return;
					}
					
					if(g.getStage().equals(GameStage.RESETTING)) {
						e.getPlayer().sendMessage(Config.getMessage(Message.OTHER_GAME_RESTARTING));
						return;
					}
					
					if(g.getPlayers().size()>=g.getArena().getMaxPlayers()) {
						e.getPlayer().sendMessage(Config.getMessage(Message.OTHER_GAME_FULL));
						return;
					}
					g.addPlayer(e.getPlayer());
				}
			}
		}else if(e.getItem() != null && e.getItem().hasItemMeta() && e.getItem().getItemMeta().hasDisplayName()) {
			String dName = e.getItem().getItemMeta().getDisplayName();
			if(Games.isInGame(e.getPlayer())) {
				Game g = Games.getGame(e.getPlayer());
				Player p = e.getPlayer();
				/*if(g.getStage().equals(GameStage.RUNNING)) {
					if(Config.matchesItem(e.getItem(), Powerup.PASS_WALL.item)) {
						if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().equals(g.getLayout().getWalls().getMaterial())) {
							if(p.getLocation().distance(e.getClickedBlock().getLocation())<2) {
								Vector f = Tools.getField(p.getLocation(), g);
								Vector f2 = Tools.add(f, Tools.get(e.getPlayer().getLocation().getDirection()), 1);
								if(f2.getBlockX() >= 0 && f2.getBlockZ() >= 0 && f2.getBlockX() < g.getArena().getSize() && f2.getBlockZ() < g.getArena().getSize()) {
									p.teleport(new Location(Config.cmWorld, f2.getBlockX()*g.getPanel().sc+1, Config.MAZE_Y+1, f2.getBlockZ()*g.getPanel().sc+1).setDirection(p.getLocation().getDirection()));
									if(e.getItem().getAmount()>1) {
										e.getItem().setAmount(e.getItem().getAmount()-1);
									}else {
										if(e.getHand().equals(EquipmentSlot.HAND)) {
											p.getInventory().setItemInMainHand(null);
										}else {
											p.getInventory().setItemInOffHand(null);
										}
									}
								}else {
									p.sendMessage(Config.getMessage(Message.INGAME_CANNOT_PASS));
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
								g.getPanel().fill(fLoc.getBlockX(), fLoc.getBlockY(), fLoc.getBlockZ(), g.getPanel().scale, Maze3D.wallHeight, g.getPanel().scale, VersionedMaterial.WHITE_WOOL.getCurrentMaterialDefinition().getMaterial(), Config.cmWorld, 0, () ->{
									g.buildTaskIDs.add(Bukkit.getScheduler().runTaskLater(CrazyMaze.pl, () -> {
										g.getPanel().fill(fLoc.getBlockX(), fLoc.getBlockY(), fLoc.getBlockZ(), g.getPanel().scale, Maze3D.wallHeight, g.getPanel().scale, Material.AIR, Config.cmWorld, 0, null);
									}, Config.wallTime*20L).getTaskId());
									
									if(e.getItem().getAmount()>0) {
										e.getItem().setAmount(e.getItem().getAmount()-1);
									}
								});
							}else {
								p.sendMessage(Config.getMessage(Message.INGAME_CANNOT_CREATE_BARRIER));
							}
						}
					}else if(Config.matchesItem(e.getItem(), Powerup.PROTECT.item)) {
						if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
							
						}
					}else if(Config.matchesItem(e.getItem(), Powerup.TOPDOWN_VIEW.item)) {
						if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
							
						}
					}
				}else */if(g.getStage().equals(GameStage.WAITING)) {
					if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)){
						if(dName.equals(Config.gameOptions.getItemMeta().getDisplayName())) {
							p.openInventory(GUIs.getVotingGUI(g.getArena()).getForPlayer(p));
						}else if(dName.equals(Config.quitGame.getItemMeta().getDisplayName())) {
							Bukkit.getScheduler().runTaskLater(CrazyMaze.plugin, () -> {
								g.removePlayer(p);
								p.sendMessage(Config.getMessage(Message.OTHER_GAME_LEFT));
							}, 1L);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if(Games.isInGame(e.getPlayer())) {
			Game g = Games.getGame(e.getPlayer());
			g.removePlayer(e.getPlayer());
		}
		if(CrazyMaze.arenas.containsKey(e.getPlayer().getUniqueId())) {
			CrazyMaze.arenas.remove(e.getPlayer().getUniqueId());
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if(Games.isInGame(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if(Games.isInGame(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		if(Games.isInGame((Player) e.getEntity())) {
			e.setCancelled(true);
		}
	}
	
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		if(Games.isInGame((Player) e.getEntity())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		if(Games.isInGame((Player) e.getEntity())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		if(Games.isInGame(e.getPlayer())) {
			Player p = e.getPlayer();
			Game g = Games.getGame(p);
			if(g.getStage().equals(GameStage.WAITING)) e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if(e.getPlayer().hasPermission(Config.PERM_NOTIFY_UPDATE)){
			if(Config.enableUpdateCheck && Config.updateCheckOnJoin){
				UpdateChecker.Result res = UpdateChecker.checkForUpdate();
				if(res.updAvailable) {
					UpdateChecker.sendUpdateMessage(res, e.getPlayer());
				}
			}
		}
	}
	
	@EventHandler
	public void onSign(SignChangeEvent e) {
		if(e.getLine(0) != null && e.getLine(0).equalsIgnoreCase("[crazymaze]")) {
			String arenaName = e.getLine(1);
			Arena a = Games.getArena(arenaName);
			if(a == null) {
				e.getPlayer().sendMessage("Invalid arena");
				return;
			}
			a.addSignLocation(e.getBlock().getLocation());
			a.updateSign();
			Config.saveArena(a);
		}
	}
	
}
