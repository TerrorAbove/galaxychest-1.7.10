package com.terrorAndBlue.galaxyChest.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityItemPersonalDrop extends EntityItem
{
	protected EntityPlayer p;
	
	public EntityItemPersonalDrop(World w, double x, double y, double z, ItemStack stack, EntityPlayer p)
	{
		super(w, x, y, z, stack);
		
		this.p = p;
	}
	
	@Override
	public void onCollideWithPlayer(EntityPlayer p)
    {
		if(this.p == p)//TODO check valid?
		{
			this.delayBeforeCanPickup = 0;
		}
		
		super.onCollideWithPlayer(p);
    }
}
