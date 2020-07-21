package com.terrorAndBlue.galaxyChest.event;

import java.util.List;

import com.terrorAndBlue.galaxyChest.GalaxyChestMain;
import com.terrorAndBlue.galaxyChest.entity.EntityItemPersonalDrop;
import com.terrorAndBlue.galaxyChest.misc.PandoraBoxProperty;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public class VanillaEventHandler
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=false)
	public void onEvent(LivingDropsEvent event)
	{
		if (event.entityLiving instanceof EntityMob)
		{
			EntityMob elb = (EntityMob)event.entityLiving;
			
			if(elb.getLastAttacker() instanceof EntityPlayer)
			{
				EntityPlayer p = (EntityPlayer)elb.getLastAttacker();
				
				if(elb.getDistanceToEntity(p) <= 12 && Math.random() < 1.0 / GalaxyChestMain.instance.configs.pandorasBoxChance)
				{
					ItemStack itemStackToDrop = new ItemStack(GalaxyChestMain.pandorasBox);
					EntityItemPersonalDrop entityItemToDrop = new EntityItemPersonalDrop(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, itemStackToDrop, p);
					
					entityItemToDrop.delayBeforeCanPickup = 100;//the delay before OTHER players can pick up
					
					event.drops.add(entityItemToDrop);
					
					List players = elb.worldObj.playerEntities;
					
					for(Object o : players)
					{
						if(o instanceof EntityPlayer)
						{
							EntityPlayer pl = (EntityPlayer)o;
							pl.addChatComponentMessage(new ChatComponentText(p.getDisplayName()+" has found Pandora's Box!"));
						}
					}
				}
			}
		}
	}
}
