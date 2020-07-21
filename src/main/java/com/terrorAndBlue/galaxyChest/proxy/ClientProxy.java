package com.terrorAndBlue.galaxyChest.proxy;

import org.lwjgl.opengl.GL11;

import com.terrorAndBlue.galaxyChest.GalaxyChestMain;
import com.terrorAndBlue.galaxyChest.tileEntity.GalaxyChestSpecialRenderer;
import com.terrorAndBlue.galaxyChest.tileEntity.TileEntityGalaxyChest;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy
{
	public static final ResourceLocation GALAXY_CHEST_ICON = new ResourceLocation(GalaxyChestMain.MODID.toLowerCase() + ":textures/blocks/blockGalaxyChest.png");
	public static final ResourceLocation GALAXY_CHEST_ACTIVATED_ICON = new ResourceLocation(GalaxyChestMain.MODID.toLowerCase() + ":textures/blocks/blockGalaxyChest_activated.png");
	public static final ResourceLocation PANDORAS_BOX_ICON = new ResourceLocation(GalaxyChestMain.MODID.toLowerCase() + ":textures/items/pandorasBoxSmall.png");
	
	@Override
	public void registerRenderInformation(Item toRegister)
	{
		MinecraftForgeClient.registerItemRenderer(toRegister, new IItemRenderer()
		{
			@Override
			public boolean handleRenderType(ItemStack item, ItemRenderType type)
			{
				//TODO need more testing
				//equipped-first-person means standard holding item
				//equipped means 3rd person
				switch(type)
				{
				case EQUIPPED:
				case EQUIPPED_FIRST_PERSON:
					return true;
					
				default: return false;
				}
			}

			@Override
			public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
			{
				return false;
			}

			@Override
			public void renderItem(ItemRenderType type, ItemStack item, Object... data)
			{
				if(item == null)
					return;
				
				switch(type)
				{
				case EQUIPPED:
				case EQUIPPED_FIRST_PERSON:

					GL11.glPushMatrix();

					//EntityLivingBase p = (EntityLivingBase)data[1];

					if(item.getItem() == GalaxyChestMain.pandorasBox)
						Minecraft.getMinecraft().renderEngine.bindTexture(PANDORAS_BOX_ICON);
					else
						Minecraft.getMinecraft().renderEngine.bindTexture((item == null || !item.hasTagCompound() || item.getTagCompound().getBoolean("chestActive")) ? GALAXY_CHEST_ACTIVATED_ICON : GALAXY_CHEST_ICON);

					GL11.glColor4d(1.0, 1.0, 1.0, 1.0);

					Tessellator tes = Tessellator.instance;
					tes.startDrawingQuads();

					//Vec3 look = Minecraft.getMinecraft().thePlayer.getLookVec();

					//double px = Minecraft.getMinecraft().thePlayer.posX;
					//double py = Minecraft.getMinecraft().thePlayer.posY;
					//double pz = Minecraft.getMinecraft().thePlayer.posZ;

					//Vec3 playerDir = Vec3.createVectorHelper(px-(x+0.5), py-(y+0.5), pz-(z+0.5)).normalize();

					GL11.glRotated(-90, 1, 0, 0);
					//GL11.glTranslated(0.2, 0, 0);
					GL11.glRotated(45, 0, 0, 1);
					GL11.glTranslated(0, -0.2, 0);

					tes.setNormal(0, 1, 0);

					//  X    Y    Z    U    V
					tes.addVertexWithUV(0.75, 0, 0, 0.0, 1.0); // Bottom left
					tes.addVertexWithUV(0, 0, 0, 1.0, 1.0); // Bottom right
					tes.addVertexWithUV(0, 0, 0.75, 1.0, 0.0); // Top right
					tes.addVertexWithUV(0.75, 0, 0.75, 0.0, 0.0); // Top left
					tes.draw();//0,66 uleft; 255,188 bottomRight, or 255,155 for more centered view

					//GL11.glRotated(45, 0, 0, 0);

					GL11.glPopMatrix();
					
				default:
					break;
				}
			}
		});
	}
	
	@Override
	public void registerTileEntitySpecialRenderer()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGalaxyChest.class, new GalaxyChestSpecialRenderer());
	}
	
	@Override
	public void preInit()
	{
		for(int i = 0; i < GalaxyChestSpecialRenderer.GALAXY_SWIRL.length; i++)
		{
			String index = i < 10 ? "0"+i : ""+i;
			
			GalaxyChestSpecialRenderer.GALAXY_SWIRL[i] = new ResourceLocation(GalaxyChestMain.MODID.toLowerCase() + ":textures/galaxyFrames/frame_"+index+"_delay-0.06s.png");
		}
	}
}
