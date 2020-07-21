package com.terrorAndBlue.galaxyChest.gui;

import java.util.List;

import com.terrorAndBlue.galaxyChest.tileEntity.TileEntityGalaxyChest;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GalaxyContainer extends Container
{
	private TileEntityGalaxyChest tileEntity;
	
	public GalaxyContainer(IInventory playerInv, TileEntityGalaxyChest tileEntity)
	{
		this.tileEntity = tileEntity;
		
		bindPlayerInventory(playerInv);
	}
	
	public TileEntityGalaxyChest getTileEntity()
	{
		return tileEntity;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer p)
	{
		return tileEntity.isUseableByPlayer(p);
	}
	
	protected void bindPlayerInventory(IInventory inventoryPlayer)
	{
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				//PlayerInventorySlot not needed... may remove in future update
				addSlotToContainer(new PlayerInventorySlot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 14 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++)
		{
			addSlotToContainer(new PlayerInventorySlot(inventoryPlayer, i, 8 + i * 18, 72));
		}
	}
	
	public ItemStack transferStackInSlot(EntityPlayer p, int i)
	{
		Slot slot = (Slot)inventorySlots.get(i);

		ItemStack stack = slot.getStack();

		if(stack != null && tileEntity.addItemStack(stack))
		{
			slot.putStack(null);
		}

		return null;
	}
	
	@Override
	public void onContainerClosed(EntityPlayer p)
	{
		tileEntity.closeInventory(p);
	}
}