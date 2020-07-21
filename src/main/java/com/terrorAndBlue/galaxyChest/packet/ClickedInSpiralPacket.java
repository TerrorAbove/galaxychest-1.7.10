package com.terrorAndBlue.galaxyChest.packet;

import com.terrorAndBlue.galaxyChest.gui.GalaxyContainer;
import com.terrorAndBlue.galaxyChest.tileEntity.TileEntityGalaxyChest;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class ClickedInSpiralPacket implements IMessage
{
	private int index, flags;//flags: right-click, shift, button state (down or up)
	
	public static class Handler implements IMessageHandler<ClickedInSpiralPacket, ClickedInSpiralResponsePacket>
	{
		@Override
		public ClickedInSpiralResponsePacket onMessage(ClickedInSpiralPacket message, MessageContext ctx)
		{
			if(ctx.side == Side.SERVER)
			{
				EntityPlayer player = ctx.getServerHandler().playerEntity;
				
				boolean clickedAnItem = message.index > -1;
				boolean rightClick = (message.flags & 1) == 1;
				boolean shift = (message.flags & 2) == 2;
				boolean buttonDown = (message.flags & 4) == 4;
				
				ItemStack held = player.inventory.getItemStack();
				Container c = player.openContainer;
				
				if(c instanceof GalaxyContainer)
				{
					GalaxyContainer gc = (GalaxyContainer)c;
					TileEntityGalaxyChest tegc = gc.getTileEntity();
					
					if(!clickedAnItem)
					{
						if(held != null)
						{
							if(rightClick)
							{
								ItemStack copy = held.copy();
								copy.stackSize = 1;
								
								if(tegc.addItemStack(copy))
								{
									if(--held.stackSize == 0)
										held = null;
									
									tegc.markForUpdate();
									player.inventory.setItemStack(held);
									return new ClickedInSpiralResponsePacket(held);
								}
							}
							else if(tegc.addItemStack(held))
							{
								tegc.markForUpdate();
								player.inventory.setItemStack(null);
								return new ClickedInSpiralResponsePacket(null);
							}
						}
					}
					else if(message.index < tegc.getSizeInventory())
					{
						ItemStack stack = tegc.getStackInSlot(message.index);
						
						if(stack != null)
						{
							if(held != null)//item in hand, try to add it
							{
								boolean sameItem = stack.isItemEqual(held) && ItemStack.areItemStackTagsEqual(stack, held) && stack.isStackable();
								
								if(rightClick && sameItem)
								{
									ItemStack copy = held.copy();
									copy.stackSize = 1;
									
									if(tegc.addItemStack(copy))
									{
										if(--held.stackSize == 0)
											held = null;
										
										tegc.markForUpdate();
										player.inventory.setItemStack(held);
										return new ClickedInSpiralResponsePacket(held);
									}
								}
								//if couldn't add to chest, return it to player
								//if it can't be returned to player, abort operation
								else if(!tegc.addItemStack(held) && !player.inventory.addItemStackToInventory(held))
									return new ClickedInSpiralResponsePacket(player.inventory.getItemStack());
								
								player.inventory.setItemStack(null);
								
								if(sameItem)
								{
									tegc.markForUpdate();
									return new ClickedInSpiralResponsePacket(player.inventory.getItemStack());
								}
							}
							else if(rightClick)
							{
								ItemStack newStack = stack.splitStack(stack.stackSize/2);
								
								if(!shift)
									player.inventory.setItemStack(stack);
								else if(!player.inventory.addItemStackToInventory(stack))
								{
									return new ClickedInSpiralResponsePacket(player.inventory.getItemStack());
								}
								
								if(newStack.stackSize > 0)
									tegc.setInventorySlotContents(message.index, newStack);
								else
									tegc.removeItemStack(message.index);
								
								tegc.markForUpdate();
								
								return new ClickedInSpiralResponsePacket(player.inventory.getItemStack());
							}

							if(!shift)
							{
								if(!tegc.removeItemStack(stack))
									return new ClickedInSpiralResponsePacket(player.inventory.getItemStack());
								
								player.inventory.setItemStack(stack);
							}
							else
							{
								if(!tegc.removeItemStack(stack))
									return new ClickedInSpiralResponsePacket(player.inventory.getItemStack());
								
								player.inventory.addItemStackToInventory(stack);
								player.inventory.setItemStack(null);
							}
								
							tegc.markForUpdate();
							
							return new ClickedInSpiralResponsePacket(player.inventory.getItemStack());
						}
					}
				}
			}
			return null;
		}
		
	}
	
	public ClickedInSpiralPacket() {}
	
	public ClickedInSpiralPacket(int index, int flags)
	{
		this.index = index;
		this.flags = flags;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		index = buf.readInt();
		flags = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(index);
		buf.writeInt(flags);
	}
}
