package com.terrorAndBlue.galaxyChest.gui;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.sun.glass.events.MouseEvent;
import com.terrorAndBlue.galaxyChest.GalaxyChestMain;
import com.terrorAndBlue.galaxyChest.packet.ClickedInSpiralPacket;
import com.terrorAndBlue.galaxyChest.packet.HelpButtonClickedPacket;
import com.terrorAndBlue.galaxyChest.packet.SearchRequestPacket;
import com.terrorAndBlue.galaxyChest.tileEntity.GalaxyChestSpecialRenderer;
import com.terrorAndBlue.galaxyChest.tileEntity.TileEntityGalaxyChest;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;

public class GalaxyChestGui extends GuiContainer
{
	public static final ResourceLocation GALAXY_CHEST_PLAYER_INV = new ResourceLocation("galaxychest:textures/gui/galaxy_chest_player_inv.png");
	public static final ResourceLocation GALAXY_CHEST_INFO = new ResourceLocation("galaxychest:textures/gui/sample.png");
	public static final ResourceLocation MAGNIFYING_GLASS = new ResourceLocation("galaxychest:textures/gui/magnifying_glass.png");
	
	private static final double ELLIPSE_FACTOR = 0.7;
	//private static final int MAX_PAGE = 14564;
	private static final int TOOLTIP_TIME_DELAY = 250;
	private static final int ITEM_CLICK_DELAY = 50;
	private static final int PAGE_NUM_CONVERSION = 4;
	
	private EntityPlayer p;
	private TileEntityGalaxyChest tileEntity;
	
	private SearchBox searchBox;
	private HelpButton helpButton;
	private boolean helpDisplaying;
	
	private int zoom;
	
	private ItemStack tooltipStack;
	private long tooltipSetTime;
	private int tooltipInventoryIndex;
	private int tooltipX;
	private int tooltipY;
	
	public GalaxyChestGui(EntityPlayer p, TileEntityGalaxyChest tileEntity)
	{
		super(new GalaxyContainer(p.inventory, tileEntity));
		
		this.p = p;
		this.tileEntity = tileEntity;
		
		this.helpButton = new HelpButton(0, 0, 0, 12, 10, "?");
		this.helpDisplaying = false;
		
		this.zoom = 0;
		
		this.xSize = 176;
		this.ySize = 96;
		
		this.tooltipStack = null;
		this.tooltipInventoryIndex = -1;
		this.tooltipX = -1;
		this.tooltipY = -1;
	}
	
	@Override
	public void initGui()
	{
		super.initGui();//IMPORTANT!

		//set these initially to prevent first frame rendering incorrectly
		this.guiLeft = (width - xSize) / 2;
		this.guiTop = (122 + height - ySize) / 2;

		this.helpButton.xPosition = guiLeft + (xSize - 256)/2;
		this.helpButton.yPosition = 122 - helpButton.height;
		this.buttonList.add(helpButton);

		this.searchBox = new SearchBox(fontRendererObj, 120, 4, 52, 8);
		this.searchBox.setEnableBackgroundDrawing(false);//disables the normal background box
		this.searchBox.setTextColor(Color.white.hashCode());
		this.searchBox.setMaxStringLength(7);
		
		//GalaxyChestMain.wrapper.sendToServer(new DisplayItemsPacket(0));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		int currStartIndex = zoom*(TileEntityGalaxyChest.NUM_ITEMS_TO_DISPLAY/PAGE_NUM_CONVERSION);
		int currEndIndex = currStartIndex + Math.min(TileEntityGalaxyChest.NUM_ITEMS_TO_DISPLAY, tileEntity.getListSize()-currStartIndex-1);

		this.helpButton.xPosition = guiLeft + (xSize - 256)/2;
		this.helpButton.yPosition = 122 - helpButton.height;
		
		if(tileEntity.getListSize() > 0)
			this.drawString(fontRendererObj, "Items " + (currStartIndex+1) + " to " + (currEndIndex+1), 5, 4, Color.white.hashCode());
		
		searchBox.drawTextBox();

		RenderHelper.enableGUIStandardItemLighting();

		boolean tooltipMousePosition = false;//true if the mouse is in position for the tooltip this frame

		List<ItemStack> list = tileEntity.getPartition(this.zoom);
		for(int i = 0; i < TileEntityGalaxyChest.NUM_ITEMS_TO_DISPLAY; i++)
		{
			ItemStack stack = i < list.size() ? list.get(i) : null;

			if(stack != null)
			{
				int offset = -8;

				int centerX = guiLeft + (xSize - 256)/2 + 132 + offset;
				int centerY = 50 + offset;//38 + offset;
				int radius = (int)Math.pow(4+TileEntityGalaxyChest.NUM_ITEMS_TO_DISPLAY-i, 1.47)/2;

				int time = (int) ((System.currentTimeMillis() / 83) % 292L);
				double timeFactor = (2.0 * Math.PI * time) / 292;
				double angle = i * 2.0 * Math.PI / TileEntityGalaxyChest.NUM_ITEMS_TO_DISPLAY  + timeFactor;
				
				double cos = Math.cos(angle);
				double sin = Math.sin(angle);

				double x = centerX;
				double y = centerY;
				
				//double drawScale = 1;

				switch(i%4)
				{
				case 0:
					x += radius * cos;
					sin = 3*sin/4 + 0.25;//-0.5 to 1
					y += (radius * sin);
					//drawScale = sin/4 + 0.75;//0.625 to 1
					break;
				case 1:
					x -= radius * cos;
					sin = 3*sin/4 - 0.25;//-1 to 0.5
					y -= (radius * sin);
					//drawScale = -sin/4 + 0.875;//0.625 to 1
					break;
				case 2:
					x += radius * sin;
					cos = 3*cos/4 - 0.25;//-1 to 0.5
					y -= (radius * cos);
					//drawScale = -cos/4 + 0.875;//0.625 to 1
					break;
				case 3:
					x -= radius * sin;
					cos = 3*cos/4 + 0.25;//-0.5 to 1
					y += (radius * cos);
					//drawScale = cos/4 + 0.75;//0.625 to 1
					break;
				}

				y *= ELLIPSE_FACTOR;

				x -= guiLeft;
				y -= guiTop;
				
				final int X = (int)x, Y = (int)y;
				
				final Rectangle ITEM_RECT = new Rectangle(X, Y, 17, 17);

				if(new Rectangle((xSize - 256)/2, -guiTop, 256, 122).intersects(ITEM_RECT))
				{
					drawItemStack(stack, X, Y, stack == tooltipStack ? 1.0 : 0.75);

					if(ITEM_RECT.contains(mouseX-guiLeft, mouseY-guiTop))
					{
						tooltipMousePosition = true;

						tooltipStack = stack;
						tooltipSetTime = System.currentTimeMillis();
						tooltipInventoryIndex = zoom*9 + i;
						tooltipX = mouseX - guiLeft;
						tooltipY = mouseY - guiTop;
					}
				}
			}
			else
				break;
		}

		if(tooltipMousePosition && tooltipStack != null)
		{
			renderToolTip(tooltipStack, tooltipX, tooltipY+18);
			RenderHelper.enableGUIStandardItemLighting();
		}

		if(System.currentTimeMillis() - tooltipSetTime > TOOLTIP_TIME_DELAY)
		{
			this.tooltipStack = null;
			this.tooltipInventoryIndex = -1;
			this.tooltipX = -1;
			this.tooltipY = -1;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		TextureManager tm = mc.getTextureManager();

		GL11.glPushMatrix();

		this.guiLeft = (width - xSize) / 2;
		this.guiTop = (122 + height - ySize) / 2;

		tm.bindTexture(GalaxyChestSpecialRenderer.GALAXY_SWIRL[(int) ((System.currentTimeMillis() / 83) % 73L)]);

		drawTexturedModalRect(guiLeft + (xSize - 256)/2, 0, 0, 67, 256, 122);

		GL11.glPopMatrix();

		tm.bindTexture(GALAXY_CHEST_PLAYER_INV);

		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		if(tileEntity.getListSize() > 0)
		{
			this.mc.getTextureManager().bindTexture(MAGNIFYING_GLASS);
			
			GL11.glPushMatrix();
			GL11.glScaled(0.03125, 0.03125, 1);
			this.drawTexturedModalRect((guiLeft+110)*32, (guiTop+4)*32, 0, 0, 256, 256);
			GL11.glPopMatrix();
		}
	}
	
	public void setZoom(int zoom)
	{
		final int MAX_ZOOM = (tileEntity.getListSize()-1)/(TileEntityGalaxyChest.NUM_ITEMS_TO_DISPLAY/PAGE_NUM_CONVERSION);
		final int PREV_ZOOM = this.zoom;
		
		if(zoom > MAX_ZOOM)
			zoom = MAX_ZOOM;
		
		if(zoom < 0)
			zoom = 0;
		
		this.zoom = zoom;
		
		/*if(this.zoom != PREV_ZOOM)
		{
			GalaxyChestMain.wrapper.sendToServer(new DisplayItemsPacket(this.zoom));
		}*/
	}
	
	public int getZoom()
	{
		return zoom;
	}
	
	public void handleMouseInput()
	{
		if(helpDisplaying)
			return;

		int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
		int button = Mouse.getEventButton();

		boolean mouseInSpiral = new Rectangle(guiLeft + (xSize - 256)/2, 0, 256, 122).contains(x, y);
		boolean magnifyingGlass = new Rectangle(guiLeft + 110, guiTop + 4, 8, 8).contains(x, y);
		
		if(button == 0)
		{
			//searchBox.setFocused(searchBox.hovering(x-guiLeft, y-guiTop));
			searchBox.setFocused(magnifyingGlass);
		}
		
		if(tileEntity.getListSize() == 0)
			searchBox.setFocused(false);

		if(!mouseInSpiral || helpButton.getHoverState() == 2)
		{
			super.handleMouseInput();
			return;
		}
		
		int shift = isShiftKeyDown() ? 2 : 0;
		int down = Mouse.getEventButtonState() ? 4 : 0;
		
		if(button >= 0)//0 left click, 1 right click
		{
			if(down == 4)
			{
				if(tooltipInventoryIndex > -1 && System.currentTimeMillis() - tooltipSetTime <= ITEM_CLICK_DELAY)
				{
					GalaxyChestMain.wrapper.sendToServer(new ClickedInSpiralPacket(tooltipInventoryIndex, button | shift | down));
					tooltipInventoryIndex = -1;
				}
				else
				{
					GalaxyChestMain.wrapper.sendToServer(new ClickedInSpiralPacket(-1, button | shift | down));
				}
			}
		}
		else
		{
			int i = Integer.signum(Mouse.getEventDWheel());

			boolean ctrl = isCtrlKeyDown();
			//boolean shift = isShiftKeyDown();

			if(ctrl)//control means scroll by page number instead of zoom (slightly faster)
				i *= PAGE_NUM_CONVERSION;

			if(shift == 2)//shift means scroll much faster (up/down 5%, or 20% with control)
			{
				double factor = ctrl ? 0.20 : 0.05;

				if(i > 0)
					setZoom((int)(zoom*(1+factor)));
				else if(i < 0)
					setZoom((int)(zoom*(1-factor)));
			}
			else
			{
				setZoom(zoom+i);
			}
		}
	}
	
	public void keyTyped(char c, int i)
	{
		if(helpDisplaying)
		{
			if(i == 1 || c == 'e')//escape or e closes the help screen
			{
				helpDisplaying = false;
			}
			return;
		}
		
		if(searchBox.isFocused())
		{
			switch(i)
			{
			case 1://esc
				searchBox.setFocused(false);
				break;
			case 28://enter
				GalaxyChestMain.wrapper.sendToServer(new SearchRequestPacket(searchBox.getText()));
				searchBox.setText("");//TODO possibly notify user of how many search results there were
				break;
				
				default:
					searchBox.textboxKeyTyped(c, i);
			}

			return;
		}
		
		if(c == 'f' && tileEntity.getListSize() > 0)
		{
			searchBox.setFocused(true);
			return;
		}
		
		int scroll = 0;
		
		switch(i)
		{
		case 205://skip to end, right arrow key
			setZoom(Integer.MAX_VALUE);//will fix itself
			return;
		case 203://skip to beginning, left arrow key
			setZoom(0);
			return;
		case 200://equivalent to scroll down, up arrow key
			scroll = 1;
			break;
		case 208://equivalent to scroll up, down arrow key
			scroll = -1;
			break;
		case 28://enter
			if(tileEntity.getListSize() > 0)
				searchBox.setFocused(true);
			break;
			
			default:
				super.keyTyped(c, i);
				return;
		}
		
		boolean ctrl = isCtrlKeyDown();
        boolean shift = isShiftKeyDown();
        
        if(ctrl)//control means scroll by page number instead of zoom (slightly faster)
    		scroll *= PAGE_NUM_CONVERSION;
        
        if(shift)//shift means scroll much faster (up/down 5%, or 20% with control)
        {
        	double factor = ctrl ? 0.20 : 0.05;
        	
        	if(scroll > 0)
        		setZoom((int)(zoom*(1+factor)));
        	else if(scroll < 0)
        		setZoom((int)(zoom*(1-factor)));
        }
        else
        {
        	setZoom(zoom+scroll);
        }
	}
	
	private void drawItemStack(ItemStack stack, int x, int y, double scale)
	{
		if(scale != 1)
		{
			GL11.glPushMatrix();
			GL11.glScaled(scale, scale, 1);
		}
		
		itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), stack, (int)(x/scale), (int)(y/scale));
		itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), stack, (int)(x/scale), (int)(y/scale));
		
		if(scale != 1)
		{
			GL11.glPopMatrix();
		}
	}
	
	public void setHelpButtonEnabled(boolean enabled)
	{
		helpButton.enabled = enabled;
	}
	
	@Override
	public void actionPerformed(GuiButton button)
	{
		if(button == helpButton)
		{
			helpDisplaying = true;
		}
	}
	
	@Override
	public void drawScreen(int a, int b, float f)
	{
		if(!helpDisplaying)
		{
			super.drawScreen(a, b, f);
		}
		else
		{
			drawDefaultBackground();
			mc.getTextureManager().bindTexture(GALAXY_CHEST_INFO);
			drawTexturedModalRect((width - 256)/2, (height - 180)/2, 0, 0, 256, 180);
		}
	}
}
