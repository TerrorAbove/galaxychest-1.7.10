package com.terrorAndBlue.galaxyChest.packet;

import com.terrorAndBlue.galaxyChest.gui.GalaxyChestGui;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

public class ClickedInSpiralResponsePacket implements IMessage
{
	private ItemStack stack;
	
	public static class Handler implements IMessageHandler<ClickedInSpiralResponsePacket, IMessage>
	{
		@Override
		public IMessage onMessage(ClickedInSpiralResponsePacket message, MessageContext ctx)
		{
			if(ctx.side == Side.CLIENT)
			{
				Minecraft.getMinecraft().thePlayer.inventory.setItemStack(message.stack);
				
				/*GuiScreen screen = Minecraft.getMinecraft().currentScreen;
				
				if(screen instanceof GalaxyChestGui)
				{
					GalaxyChestGui gui = (GalaxyChestGui)screen;
					gui.updateDisplayItems();
				}*/
			}
			return null;
		}
		
	}
	
	public ClickedInSpiralResponsePacket() {}
	
	public ClickedInSpiralResponsePacket(ItemStack stack)
	{
		this.stack = stack;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		stack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeItemStack(buf, stack);
	}
}
