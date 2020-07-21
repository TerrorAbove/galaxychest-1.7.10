package com.terrorAndBlue.galaxyChest.packet;

import com.terrorAndBlue.galaxyChest.gui.GalaxyChestGui;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class HelpButtonClickedResponsePacket implements IMessage
{
	private boolean addedItem;
	
	public static class Handler implements IMessageHandler<HelpButtonClickedResponsePacket, IMessage>
	{
		@Override
		public IMessage onMessage(HelpButtonClickedResponsePacket message, MessageContext ctx)
		{
			if(ctx.side == Side.CLIENT)
			{
				GuiScreen curr = Minecraft.getMinecraft().currentScreen;
				EntityPlayer p = Minecraft.getMinecraft().thePlayer;
				if(curr instanceof GalaxyChestGui)
				{
					GalaxyChestGui gui = (GalaxyChestGui)curr;
					//gui.setHelpButtonEnabled(!message.addedItem);
					if(message.addedItem && p != null)
					{
						//TODO change apple to the helpful info book
						//p.inventory.addItemStackToInventory(new ItemStack(Items.apple));
					}
				}
			}
			return null;
		}
	}
	
	public HelpButtonClickedResponsePacket() {}
	
	public HelpButtonClickedResponsePacket(boolean addedItem)
	{
		this.addedItem = addedItem;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		addedItem = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(addedItem);
	}
}
