package com.terrorAndBlue.galaxyChest;

import java.util.Timer;
import java.util.TimerTask;

import com.terrorAndBlue.galaxyChest.gui.GalaxyChestGui;
import com.terrorAndBlue.galaxyChest.gui.GalaxyContainer;
import com.terrorAndBlue.galaxyChest.tileEntity.TileEntityGalaxyChest;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == 0)//this should be the only one... no need to label it
		{
			TileEntity te = world.getTileEntity(x, y, z);
			
			if(te instanceof TileEntityGalaxyChest)
			{
				TileEntityGalaxyChest tegc = (TileEntityGalaxyChest)te;
				tegc.openInventory(player);
				
				return new GalaxyContainer(player.inventory, tegc);
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == 0)//this should be the only one... no need to label it
		{
			TileEntity te = world.getTileEntity(x, y, z);
			
			if(te instanceof TileEntityGalaxyChest)
			{
				TileEntityGalaxyChest tegc = (TileEntityGalaxyChest)te;
				tegc.openInventory(player);
				
				return new GalaxyChestGui(player, tegc);
			}
		}
		return null;
	}
}
