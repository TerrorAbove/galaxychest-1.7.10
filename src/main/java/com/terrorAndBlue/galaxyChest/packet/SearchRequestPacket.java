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

public class SearchRequestPacket implements IMessage
{
	private char[] searchTerm;
	
	public static class Handler implements IMessageHandler<SearchRequestPacket, SearchRequestResponsePacket>
	{
		@Override
		public SearchRequestResponsePacket onMessage(SearchRequestPacket message, MessageContext ctx)
		{
			if(ctx.side == Side.SERVER)
			{
				EntityPlayer player = ctx.getServerHandler().playerEntity;
				
				Container c = player.openContainer;
				
				if(c instanceof GalaxyContainer)
				{
					GalaxyContainer gc = (GalaxyContainer)c;
					TileEntityGalaxyChest tegc = gc.getTileEntity();
					
					//search for searchTerm in the list of items
					int result = tegc.search(new String(message.searchTerm));
					
					if(result >= 0)
						return new SearchRequestResponsePacket(result);
				}
			}
			return null;
		}
		
	}
	
	public SearchRequestPacket() {}
	
	public SearchRequestPacket(String search)
	{
		searchTerm = search.toCharArray();
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		int numChars = buf.readableBytes() / 2;
		
		char[] c = new char[numChars];
		
		for(int i = 0; i < c.length; i++)
			c[i] = buf.readChar();
		
		searchTerm = c;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		for(int i = 0; i < searchTerm.length; i++)
			buf.writeChar(searchTerm[i]);
	}
}
