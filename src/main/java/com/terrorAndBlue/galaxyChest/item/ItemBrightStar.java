package com.terrorAndBlue.galaxyChest.item;

import net.minecraft.item.Item;

public class ItemBrightStar extends Item
{
	public ItemBrightStar()
	{
		super();
		
		this.setMaxStackSize(1);
		this.setTextureName("galaxyChest:brightStar");
		this.setUnlocalizedName("containedBrightStar");
	}
}
