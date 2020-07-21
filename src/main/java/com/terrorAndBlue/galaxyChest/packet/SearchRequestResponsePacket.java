package com.terrorAndBlue.galaxyChest.packet;

import com.terrorAndBlue.galaxyChest.gui.GalaxyChestGui;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class SearchRequestResponsePacket implements IMessage
{
	private int index;
	
	public static class Handler implements IMessageHandler<SearchRequestResponsePacket, IMessage>
	{
		@Override
		public IMessage onMessage(SearchRequestResponsePacket message, MessageContext ctx)
		{
			if(ctx.side == Side.CLIENT)
			{
				GuiScreen screen = Minecraft.getMinecraft().currentScreen;
				
				if(screen instanceof GalaxyChestGui)
				{
					GalaxyChestGui gui = (GalaxyChestGui)screen;
					gui.setZoom(message.index/9);
				}
			}
			return null;
		}
	}
	
	public SearchRequestResponsePacket() {}
	
	public SearchRequestResponsePacket(int index)
	{
		this.index = index;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		index = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(index);
	}
}
