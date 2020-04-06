package me.mrletsplay.crazymaze.main;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public class CrazyMazeWorldGenerator extends ChunkGenerator{

	@Override
	public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
		ChunkData c = createChunkData(world);
		c.setRegion(0, 0, 0, 16, 1, 16, Material.STONE);
		return c;
	}
	
}