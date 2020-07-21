package com.terrorAndBlue.galaxyChest.misc;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PandoraBoxProperty implements IExtendedEntityProperties
{
	private boolean found = false;
	
	public PandoraBoxProperty() {}
	
	public PandoraBoxProperty(boolean found)
	{
		this.found = found;
	}
	
	@Override
	public void saveNBTData(NBTTagCompound compound)
	{
		compound.setBoolean("pandorasBoxFound", found);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound)
	{
		found = compound.getBoolean("pandorasBoxFound");
	}

	@Override
	public void init(Entity entity, World world)
	{
		
	}
	
	public boolean isFound()
	{
		return found;
	}
	
	public void setFound(boolean found)
	{
		this.found = found;
	}
}
