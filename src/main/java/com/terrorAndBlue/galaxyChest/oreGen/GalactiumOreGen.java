package com.terrorAndBlue.galaxyChest.oreGen;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

import java.util.Random;

import com.terrorAndBlue.galaxyChest.GalaxyChestMain;

public class GalactiumOreGen implements IWorldGenerator
{
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{ 
		switch(world.provider.dimensionId)
		{
		case -1:
			//generateNether(world, random, chunkX * 16, chunkZ * 16);
			break;
		case 0:
			generateSurface(world, random, chunkX * 16, chunkZ * 16);
			break;
		case 1:
			//generateEnd(world, random, chunkX * 16, chunkZ * 16);
			break;
		}
	}

	private void generateEnd(World world, Random random, int chunkX, int chunkZ)
	{
		
	}

	private void generateSurface(World world, Random random, int chunkX, int chunkZ)
	{//TODO possibly add Artifice:blockBasalt to list of override-able stones
		if(random.nextInt(GalaxyChestMain.instance.configs.galactiumOreSpawnChance) == 0)
		{
			int xCoord = chunkX + random.nextInt(16);
			int yCoord = 10 + random.nextInt(11);
			int zCoord = chunkZ + random.nextInt(16);
			
			(new WorldGenMinable(GalaxyChestMain.galactiumOreBlock, GalaxyChestMain.instance.configs.galactiumOreVeinSize, Blocks.stone)).generate(world, random, xCoord, yCoord, zCoord);
		}
	}

	private void generateNether(World world, Random random, int chunkX, int chunkZ)
	{
		
	}

}