package com.terrorAndBlue.galaxyChest.item;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;


public class ItemGalaxyChest extends ItemBlock
{
	private IIcon activatedIcon;
	
	public ItemGalaxyChest(Block b)
	{
		super(b);
	}
	
	@Override
	public IIcon getIcon(ItemStack stack, int b)
	{
		return getIconIndex(stack);
	}
	
	@Override
	public IIcon getIconIndex(ItemStack stack)
	{
		if(stack == null || !stack.hasTagCompound() || stack.getTagCompound().getBoolean("chestActive"))
		{
			return activatedIcon;
		}
		
		return super.getIconIndex(stack);//inactive icon
	}
	
	@Override
	public void registerIcons(IIconRegister reg)
	{
		super.registerIcons(reg);
		activatedIcon = reg.registerIcon("galaxychest:blockGalaxyChest_activated");
	}
}
