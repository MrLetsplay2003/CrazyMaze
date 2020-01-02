package me.mrletsplay.crazymaze.main;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CMTimer {

	@SuppressWarnings("deprecation")
	public static void init() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.pl, () -> {
			Games.games.forEach((a, g) -> {
				g.getPlayers().forEach(p -> {
					Block b = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
					if(b.getType().equals(PowerupField.SPEED.material) && b.getData() == PowerupField.SPEED.data) {
						if(p.hasPotionEffect(PotionEffectType.SPEED)) p.removePotionEffect(PotionEffectType.SPEED);
						p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*5, 1, false, false));
					}
				});
			});
		}, 5, 5);
	}
	
}
