package com.terrorAndBlue.galaxyChest.gui;

import java.awt.Color;
import java.awt.Rectangle;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class SearchBox extends GuiTextField
{
	public SearchBox(FontRenderer fr, int x, int y, int w, int h)
	{
		super(fr, x, y, w, h);
	}
	
	public boolean hovering(int x, int y)
	{
		return new Rectangle(this.xPosition, this.yPosition, this.width, this.height).contains(x, y);
	}
	
	public void setFocused(boolean focused)
	{
		super.setFocused(focused);
		
		if(!focused)
			setText("");
	}
}
