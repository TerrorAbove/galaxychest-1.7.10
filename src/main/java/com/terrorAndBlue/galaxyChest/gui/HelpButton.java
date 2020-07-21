package com.terrorAndBlue.galaxyChest.gui;

import net.minecraft.client.gui.GuiButton;

public class HelpButton extends GuiButton
{
	public HelpButton(int id, int x, int y, int width, int height, String text)
	{
		super(id, x, y, width, height, text);
	}
	
	public int getHoverState()
	{
		return getHoverState(field_146123_n);
	}
}
