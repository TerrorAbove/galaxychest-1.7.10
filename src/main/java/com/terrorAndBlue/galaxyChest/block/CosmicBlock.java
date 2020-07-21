package com.terrorAndBlue.galaxyChest.block;

import com.terrorAndBlue.galaxyChest.GalaxyChestMain;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

public class CosmicBlock extends Block
{
	public CosmicBlock(Material mat)
	{
		super(mat);
		
		this.setBlockName("cosmicBlock");
		this.setBlockTextureName("galaxychest:cosmic_obsidian2");
		this.setHardness(5.0F);
		this.setResistance(10.0F);
		this.setHarvestLevel("pickaxe", 3);
		this.setStepSound(soundTypePiston);
		
		if(GalaxyChestMain.instance.configs.galaxyLighting)
		{
			this.setLightLevel(1f);
			this.setLightOpacity(0);
		}
	}
}
