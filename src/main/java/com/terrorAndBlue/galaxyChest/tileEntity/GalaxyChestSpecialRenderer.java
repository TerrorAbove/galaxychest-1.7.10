package com.terrorAndBlue.galaxyChest.tileEntity;

import org.lwjgl.opengl.GL11;

import com.terrorAndBlue.galaxyChest.GalaxyChestMain;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class GalaxyChestSpecialRenderer extends TileEntitySpecialRenderer
{
	public static final ResourceLocation[] GALAXY_SWIRL = new ResourceLocation[73];
	
	public static final ResourceLocation GALAXY_CHEST_INACTIVE_BACKGROUND = new ResourceLocation(GalaxyChestMain.MODID.toLowerCase() + ":textures/model/space_bg_square_adjusted.png");
	public static final ResourceLocation GALAXY_CHEST_MODEL = new ResourceLocation(GalaxyChestMain.MODID.toLowerCase() + ":textures/model/HD_model_galaxy_chest.png");
	
	private ModelChest model;
	
	public GalaxyChestSpecialRenderer()
	{
		model = new ModelChest();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity t, double x, double y, double z, float partialTick)
	{
		if(t instanceof TileEntityGalaxyChest)
		{
			//int meta = Minecraft.getMinecraft().theWorld.getBlockMetadata((int)x, (int)y, (int)z);
			TileEntityGalaxyChest tileEntity = (TileEntityGalaxyChest)t;

			int facing = tileEntity.getFacing();

			bindTexture(GALAXY_CHEST_MODEL);

			if(facing == 0)
				facing = 3;

			GL11.glPushMatrix();
			//GL11.glEnable(GL11.GL_BLEND);
			//GL11.glDisable(GL11.GL_CULL_FACE_MODE);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslatef((float) x, (float) y + 1.0F, (float) z + 1.0F);
			GL11.glScalef(1.0F, -1F, -1F);
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);

			int k = 0;
			if (facing == 2) {
				k = 180;
			}
			if (facing == 3) {
				k = 0;
			}
			if (facing == 4) {
				k = 90;
			}
			if (facing == 5) {
				k = -90;
			}
			GL11.glRotatef(k, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			float lidangle = tileEntity.prevLidAngle + (tileEntity.lidAngle - tileEntity.prevLidAngle) * partialTick;
			lidangle = 1.0F - lidangle;
			lidangle = 1.0F - lidangle * lidangle * lidangle;
			model.chestLid.rotateAngleX = -((lidangle * 3.141593F) / 2.0F);
			// Render the chest itself
			model.renderAll();

			//GL11.glEnable(GL11.GL_CULL_FACE_MODE);
			//GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);
			GL11.glColor4d(1.0, 1.0, 1.0, 1.0);

			Tessellator tes = Tessellator.instance;
			tes.startDrawingQuads();

			if(tileEntity.isChestActive())
				bindTexture(GALAXY_SWIRL[(int) ((System.currentTimeMillis() / 83) % 73L)]);
			else
				bindTexture(GALAXY_CHEST_INACTIVE_BACKGROUND);

			//Vec3 look = Minecraft.getMinecraft().thePlayer.getLookVec();

			//double px = Minecraft.getMinecraft().thePlayer.posX;
			//double py = Minecraft.getMinecraft().thePlayer.posY;
			//double pz = Minecraft.getMinecraft().thePlayer.posZ;

			//Vec3 playerDir = Vec3.createVectorHelper(px-(x+0.5), py-(y+0.5), pz-(z+0.5)).normalize();

			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			GL11.glRotatef(k-90F, 0.0F, 1.0F, 0.0F);
			if(facing == 4 || facing == 5)
			{
				GL11.glRotatef(180F, 0, 1.0F, 0);
			}
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

			tes.setNormal(0, 1, 0);

			//  X      Y      Z      U       V
			tes.addVertexWithUV(0.0625, 0.615, 0.9375, 0.0, 155.0/255); // Bottom left
			tes.addVertexWithUV(0.9375, 0.615, 0.9375, 1.0, 155.0/255); // Bottom right
			tes.addVertexWithUV(0.9375, 0.615, 0.0625, 1.0, 66.0/255); // Top right
			tes.addVertexWithUV(0.0625, 0.615, 0.0625, 0.0, 66.0/255); // Top left
			tes.draw();//0,66 uleft; 255,188 bottomRight, or 255,155 for more centered view

			GL11.glPopMatrix();
		}
	}
}
