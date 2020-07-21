package com.terrorAndBlue.galaxyChest.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class PlayerInventorySlot extends Slot
{
	public PlayerInventorySlot(IInventory playerInv, int slotNum, int x, int y)
	{
		super(playerInv, slotNum, x, y);
	}
}
