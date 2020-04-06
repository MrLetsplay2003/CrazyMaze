package me.mrletsplay.crazymaze.main;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.mrletsplay.crazymaze.arena.Arena;
import me.mrletsplay.crazymaze.arena.ArenaLayout;
import me.mrletsplay.crazymaze.game.Game;
import me.mrletsplay.crazymaze.game.Games;
import me.mrletsplay.crazymaze.game.Game.VotingData;
import me.mrletsplay.mrcore.bukkitimpl.ItemUtils;
import me.mrletsplay.mrcore.bukkitimpl.gui.GUI;
import me.mrletsplay.mrcore.bukkitimpl.gui.GUIBuilder;
import me.mrletsplay.mrcore.bukkitimpl.gui.GUIElementAction;
import me.mrletsplay.mrcore.bukkitimpl.gui.StaticGUIElement;
import me.mrletsplay.mrcore.bukkitimpl.gui.event.GUIElementActionEvent;
import me.mrletsplay.mrcore.bukkitimpl.versioned.VersionedDyeColor;
import me.mrletsplay.mrcore.bukkitimpl.versioned.VersionedMaterial;

public class GUIs {
	
	public static GUI getVotingGUI(Arena a) {
		GUIBuilder builder = new GUIBuilder(Config.votingInvName, 3);
		
		for(int i = 0; i < 3*9; i++) {
			builder.addElement(i, new StaticGUIElement(ItemUtils.createItem(VersionedMaterial.WHITE_STAINED_GLASS_PANE, 1, "§0")));
		}

		builder.addElement(10, new StaticGUIElement(ItemUtils.createItem(Material.BRICK, 1, 0, Config.getMessage(Message.GUI_VOTE_MAZE_LAYOUT), a.getLayouts().size() == 1 ? Config.getMessage(Message.GUI_VOTING_MAZE_DISABLED):""))
			.setAction(new GUIElementAction() {
				
				@Override
				public void onAction(GUIElementActionEvent event) {
					Player p = event.getPlayer();
					if(Games.isInGame(p)) {
						Game g = Games.getGame(p);
						if(g.getArena().getLayouts().size()>1) {
							p.openInventory(GUIs.getMazeLayoutInv(p, g).getForPlayer(p));
						}
					}
					event.setCancelled(true);
				}
			}));
		
		builder.addElement(13, new StaticGUIElement(ItemUtils.createItem(VersionedMaterial.REPEATER, 1, Config.getMessage(Message.GUI_VOTE_GAME_DURATION)))
				.setAction(new GUIElementAction() {
					
					@Override
					public void onAction(GUIElementActionEvent event) {
						Player p = event.getPlayer();
						if(Games.isInGame(p)) {
							p.openInventory(GUIs.getGameTimeInv(p, Games.getGame(p)).getForPlayer(p));
						}
					}
				}));
		
		return builder.create();
	}
	
	public static GUI getGameTimeInv(Player p, Game g) {
		GUIBuilder builder = new GUIBuilder(Config.choiceInvName, 3);
		
		VotingData d = g.getVotingData(p);
		ItemStack gP = ItemUtils.createItem(VersionedMaterial.WHITE_STAINED_GLASS_PANE, 1, "§0");
		
		for(int i = 0; i < 3*9; i++) {
			builder.addElement(i, new StaticGUIElement(gP));
		}
		
		builder.addElement(10, new StaticGUIElement(ItemUtils.createItem(VersionedMaterial.REPEATER, d.gameTime==5?2:1, Config.getMessage(Message.GUI_GAME_DURATION_5_MINUTES))).setAction(new GUIElementAction() {
			
			@Override
			public void onAction(GUIElementActionEvent event) {
				VotingData d = g.getVotingData(p);
				d.gameTime = 5;
				g.setVotingData(p, d);
				p.openInventory(GUIs.getGameTimeInv(p, g).getForPlayer(p));
			}
		}));
		builder.addElement(12, new StaticGUIElement(ItemUtils.createItem(VersionedMaterial.REPEATER, d.gameTime==10?2:1, Config.getMessage(Message.GUI_GAME_DURATION_10_MINUTES))).setAction(new GUIElementAction() {
			
			@Override
			public void onAction(GUIElementActionEvent event) {
				VotingData d = g.getVotingData(p);
				d.gameTime = 10;
				g.setVotingData(p, d);
				p.openInventory(GUIs.getGameTimeInv(p, g).getForPlayer(p));
			}
		}));
		builder.addElement(14, new StaticGUIElement(ItemUtils.createItem(VersionedMaterial.REPEATER, d.gameTime==20?2:1, Config.getMessage(Message.GUI_GAME_DURATION_20_MINUTES))).setAction(new GUIElementAction() {
			
			@Override
			public void onAction(GUIElementActionEvent event) {
				VotingData d = g.getVotingData(p);
				d.gameTime = 20;
				g.setVotingData(p, d);
				p.openInventory(GUIs.getGameTimeInv(p, g).getForPlayer(p));
			}
		}));
		builder.addElement(16, new StaticGUIElement(ItemUtils.createItem(VersionedMaterial.REPEATER, d.gameTime==-1?2:1, Config.getMessage(Message.GUI_GAME_DURATION_INFINITE))).setAction(new GUIElementAction() {
			
			@Override
			public void onAction(GUIElementActionEvent event) {
				VotingData d = g.getVotingData(p);
				d.gameTime = -1;
				g.setVotingData(p, d);
				p.openInventory(GUIs.getGameTimeInv(p, g).getForPlayer(p));
			}
		}));
		
		builder.addElement(18, new StaticGUIElement(ItemUtils.createItem(ItemUtils.arrowLeft(VersionedDyeColor.RED), "§cBack")).setAction(new GUIElementAction() {
			
			@Override
			public void onAction(GUIElementActionEvent event) {
				p.openInventory(GUIs.getVotingGUI(g.getArena()).getForPlayer(p));
			}
		}));
		
		return builder.create();
	}
	
	public static GUI getMazeLayoutInv(Player p, Game g) {
		GUIBuilder builder = new GUIBuilder(Config.choiceInvName, 3);
		
		VotingData d = g.getVotingData(p);
		ItemStack gP = ItemUtils.createItem(VersionedMaterial.WHITE_STAINED_GLASS_PANE, 1, "§0");
		
		for(int i = 0; i < 3*9; i++) {
			builder.addElement(i, new StaticGUIElement(gP));
		}
		ArenaLayout l1 = g.getLayouts().get(0);
		ArenaLayout l2 = g.getLayouts().get(1);
		
		builder.addElement(10, new StaticGUIElement(ItemUtils.createItem(l1.getIcon().getMaterial(), d.mazeLayout==1?2:1, l1.getIcon().getData(), l1.getDisplayName())).setAction(new GUIElementAction() {
			
			@Override
			public void onAction(GUIElementActionEvent event) {
				VotingData d = g.getVotingData(p);
				d.mazeLayout = 1;
				g.setVotingData(p, d);
				p.openInventory(GUIs.getMazeLayoutInv(p, g).getForPlayer(p));
			}
		}));
		
		builder.addElement(12, new StaticGUIElement(ItemUtils.createItem(l2.getIcon().getMaterial(), d.mazeLayout==2?2:1, l2.getIcon().getData(), l2.getDisplayName())).setAction(new GUIElementAction() {
			
			@Override
			public void onAction(GUIElementActionEvent event) {
				VotingData d = g.getVotingData(p);
				d.mazeLayout = 2;
				g.setVotingData(p, d);
				p.openInventory(GUIs.getMazeLayoutInv(p, g).getForPlayer(p));
			}
		}));
		
		if(g.getLayouts().size()==3) {
			ArenaLayout l3 = g.getLayouts().get(2);
			builder.addElement(14, new StaticGUIElement(ItemUtils.createItem(l3.getIcon().getMaterial(), d.mazeLayout==3?2:1, l3.getIcon().getData(), l3.getDisplayName())).setAction(new GUIElementAction() {
				
				@Override
				public void onAction(GUIElementActionEvent event) {
					VotingData d = g.getVotingData(p);
					d.mazeLayout = 3;
					g.setVotingData(p, d);
					p.openInventory(GUIs.getMazeLayoutInv(p, g).getForPlayer(p));
				}
			}));
		}else {
			builder.addElement(14, new StaticGUIElement(ItemUtils.createItem(Material.BARRIER, 1, 0, Config.getMessage(Message.GUI_MAZE_LAYOUT_NOT_AVAILABLE))));
		}
		
		builder.addElement(16, new StaticGUIElement(ItemUtils.createItem(VersionedMaterial.WHITE_STAINED_CLAY, d.mazeLayout==-1?2:1, Config.getMessage(Message.GUI_MAZE_LAYOUT_RANDOM))).setAction(new GUIElementAction() {
			
			@Override
			public void onAction(GUIElementActionEvent event) {
				VotingData d = g.getVotingData(p);
				d.mazeLayout = -1;
				g.setVotingData(p, d);
				p.openInventory(GUIs.getMazeLayoutInv(p, g).getForPlayer(p));
			}
		}));
		
		builder.addElement(18, new StaticGUIElement(ItemUtils.createItem(ItemUtils.arrowLeft(VersionedDyeColor.RED), "§cBack")).setAction(new GUIElementAction() {
			
			@Override
			public void onAction(GUIElementActionEvent event) {
				p.openInventory(GUIs.getVotingGUI(g.getArena()).getForPlayer(p));
			}
		}));
		
		return builder.create();
	}
	
}
