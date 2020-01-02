package me.mrletsplay.crazymaze.main;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.mrletsplay.crazymaze.arena.Arena;
import me.mrletsplay.crazymaze.arena.ArenaLayout;
import me.mrletsplay.crazymaze.main.Game.VotingData;
import me.mrletsplay.mrcore.bukkitimpl.gui.GUI;
import me.mrletsplay.mrcore.bukkitimpl.gui.GUIBuilder;
import me.mrletsplay.mrcore.bukkitimpl.gui.GUIElementAction;
import me.mrletsplay.mrcore.bukkitimpl.gui.StaticGUIElement;
import me.mrletsplay.mrcore.bukkitimpl.gui.event.GUIElementActionEvent;

public class GUIs {

	public static GUI getVotingGUI(Arena a) {
		GUIBuilder builder = new GUIBuilder(Config.votingInvName, 3);
		
		for(int i = 0; i < 3*9; i++) {
			builder.addElement(i, new StaticGUIElement(Tools.createItem(Material.STAINED_GLASS_PANE, 1, 0, "�0")));
		}

		builder.addElement(10, new StaticGUIElement(Tools.createItem(Material.BRICK, 1, 0, Config.getMessage("gui.vote-maze-layout"), a.getLayouts().size()==1?Config.getMessage("gui.voting-maze-disabled"):""))
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
		
		builder.addElement(13, new StaticGUIElement(Tools.createItem(Material.DIODE, 1, 0, Config.getMessage("gui.vote-game-duration")))
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
		ItemStack gP = Tools.createItem(Material.STAINED_GLASS_PANE, 1, 0, "�0");
		
		for(int i = 0; i < 3*9; i++) {
			builder.addElement(i, new StaticGUIElement(gP));
		}
		
		builder.addElement(10, new StaticGUIElement(Tools.createItem(Material.DIODE, d.gameTime==5?2:1, 0, Config.getMessage("gui.game-duration.5-mins"))).setAction(new GUIElementAction() {
			
			@Override
			public void onAction(GUIElementActionEvent event) {
				VotingData d = g.getVotingData(p);
				d.gameTime = 5;
				g.setVotingData(p, d);
				p.openInventory(GUIs.getGameTimeInv(p, g).getForPlayer(p));
			}
		}));
		builder.addElement(12, new StaticGUIElement(Tools.createItem(Material.DIODE, d.gameTime==10?2:1, 0, Config.getMessage("gui.game-duration.10-mins"))).setAction(new GUIElementAction() {
			
			@Override
			public void onAction(GUIElementActionEvent event) {
				VotingData d = g.getVotingData(p);
				d.gameTime = 10;
				g.setVotingData(p, d);
				p.openInventory(GUIs.getGameTimeInv(p, g).getForPlayer(p));
			}
		}));
		builder.addElement(14, new StaticGUIElement(Tools.createItem(Material.DIODE, d.gameTime==20?2:1, 0, Config.getMessage("gui.game-duration.20-mins"))).setAction(new GUIElementAction() {
			
			@Override
			public void onAction(GUIElementActionEvent event) {
				VotingData d = g.getVotingData(p);
				d.gameTime = 20;
				g.setVotingData(p, d);
				p.openInventory(GUIs.getGameTimeInv(p, g).getForPlayer(p));
			}
		}));
		builder.addElement(16, new StaticGUIElement(Tools.createItem(Material.DIODE, d.gameTime==-1?2:1, 0, Config.getMessage("gui.game-duration.infinite"))).setAction(new GUIElementAction() {
			
			@Override
			public void onAction(GUIElementActionEvent event) {
				VotingData d = g.getVotingData(p);
				d.gameTime = -1;
				g.setVotingData(p, d);
				p.openInventory(GUIs.getGameTimeInv(p, g).getForPlayer(p));
			}
		}));
		
		builder.addElement(18, new StaticGUIElement(Tools.createItem(Tools.arrowLeft(DyeColor.RED), "�cBack")).setAction(new GUIElementAction() {
			
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
		ItemStack gP = Tools.createItem(Material.STAINED_GLASS_PANE, 1, 0, "�0");
		
		for(int i = 0; i < 3*9; i++) {
			builder.addElement(i, new StaticGUIElement(gP));
		}
		ArenaLayout l1 = g.getLayouts().get(0);
		ArenaLayout l2 = g.getLayouts().get(1);
		
		builder.addElement(10, new StaticGUIElement(Tools.createItem(l1.getIconType(), d.mazeLayout==1?2:1, l1.getIconData(), l1.getDisplayName())).setAction(new GUIElementAction() {
			
			@Override
			public void onAction(GUIElementActionEvent event) {
				VotingData d = g.getVotingData(p);
				d.mazeLayout = 1;
				g.setVotingData(p, d);
				p.openInventory(GUIs.getMazeLayoutInv(p, g).getForPlayer(p));
			}
		}));
		
		builder.addElement(12, new StaticGUIElement(Tools.createItem(l2.getIconType(), d.mazeLayout==2?2:1, l2.getIconData(), l2.getDisplayName())).setAction(new GUIElementAction() {
			
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
			builder.addElement(14, new StaticGUIElement(Tools.createItem(l3.getIconType(), d.mazeLayout==3?2:1, l3.getIconData(), l3.getDisplayName())).setAction(new GUIElementAction() {
				
				@Override
				public void onAction(GUIElementActionEvent event) {
					VotingData d = g.getVotingData(p);
					d.mazeLayout = 3;
					g.setVotingData(p, d);
					p.openInventory(GUIs.getMazeLayoutInv(p, g).getForPlayer(p));
				}
			}));
		}else {
			builder.addElement(14, new StaticGUIElement(Tools.createItem(Material.BARRIER, 1, 0, Config.getMessage("gui.maze-layout.not-available"))));
		}
		
		builder.addElement(16, new StaticGUIElement(Tools.createItem(Material.STAINED_CLAY, d.mazeLayout==-1?2:1, 0, Config.getMessage("gui.maze-layout.random"))).setAction(new GUIElementAction() {
			
			@Override
			public void onAction(GUIElementActionEvent event) {
				VotingData d = g.getVotingData(p);
				d.mazeLayout = -1;
				g.setVotingData(p, d);
				p.openInventory(GUIs.getMazeLayoutInv(p, g).getForPlayer(p));
			}
		}));
		
		builder.addElement(18, new StaticGUIElement(Tools.createItem(Tools.arrowLeft(DyeColor.RED), "�cBack")).setAction(new GUIElementAction() {
			
			@Override
			public void onAction(GUIElementActionEvent event) {
				p.openInventory(GUIs.getVotingGUI(g.getArena()).getForPlayer(p));
			}
		}));
		
		return builder.create();
	}
	
}
