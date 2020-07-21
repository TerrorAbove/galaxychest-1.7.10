package com.terrorAndBlue.galaxyChest.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class HelpButtonClickedPacket implements IMessage
{
	public static class Handler implements IMessageHandler<HelpButtonClickedPacket, HelpButtonClickedResponsePacket>
	{
		@Override
		public HelpButtonClickedResponsePacket onMessage(HelpButtonClickedPacket message, MessageContext ctx)
		{
			if(ctx.side == Side.SERVER)
			{
				EntityPlayer p = ctx.getServerHandler().playerEntity;
				if(p != null)
				{
					//TODO change from apple to book about chest usage lol
					//return new HelpButtonClickedResponsePacket(p.inventory.addItemStackToInventory(new ItemStack(Items.apple)));
				}
			}
			return null;
		}
	}
	
	public HelpButtonClickedPacket() {}
	
	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}
}
