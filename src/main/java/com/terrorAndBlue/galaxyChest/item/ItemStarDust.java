package com.terrorAndBlue.galaxyChest.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemStarDust extends Item
{
	public ItemStarDust()
	{
		super();
		
		this.setUnlocalizedName("starDust");
		this.setTextureName("galaxychest:starDust");
	}
	
	@Override
	public boolean hasEffect(ItemStack stack, int pass)
	{
		return true;
	}
}
