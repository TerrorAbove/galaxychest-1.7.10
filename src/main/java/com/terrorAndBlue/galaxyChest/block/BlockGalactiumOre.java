package com.terrorAndBlue.galaxyChest.block;

import java.util.ArrayList;

import com.terrorAndBlue.galaxyChest.GalaxyChestMain;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import scala.actors.threadpool.Arrays;

public class BlockGalactiumOre extends BlockOre
{
	public BlockGalactiumOre()
	{
		super();
		
		this.setBlockName("GalactiumOre");
		this.setHardness(3.0f);
		this.setResistance(10.0f);
		this.setHarvestLevel("pickaxe", 3);
		this.setStepSound(soundTypePiston);
		this.setBlockTextureName("galaxychest:s2");
		
		if(GalaxyChestMain.instance.configs.galaxyLighting)
		{
			this.setLightLevel(1f);
			this.setLightOpacity(0);
		}
	}
	
	@Override
	public ArrayList<ItemStack> getDrops(World w, int x, int y, int z, int meta, int fortune)
	{
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		
		int min = 1 + Math.min(3, Math.max(0, fortune));//clamp fortune to 0..3 and add this to the minimum
		int max = 7;
		
		if(fortune > 0)//1 extra dust for having any level of fortune
			max++;
		
		int numDrops = min + (int)(Math.random()*(1+max-min));
		
		for(int i = 0; i < numDrops; i++)
			list.add(new ItemStack(GalaxyChestMain.starDust));
		
		return list;
	}
	
	/*public boolean isReplaceableOreGen(World w, int x, int y, int z, Block target)
	{
		return false;
	}*/
}
