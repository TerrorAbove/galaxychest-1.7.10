package com.terrorAndBlue.galaxyChest.item;

import com.terrorAndBlue.galaxyChest.GalaxyChestMain;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class ItemPandorasBox extends Item
{
	public ItemPandorasBox()
	{
		super();
		
		this.setMaxStackSize(1);
		this.setUnlocalizedName("pandorasBox");
		this.setTextureName("galaxychest:pandorasBox");
		
		//instead of giving it a creative tab, made it so placing the chest in creative auto completes it
	}
	
	public ItemStack onItemRightClick(ItemStack stack, World w, EntityPlayer p)
	{
		ItemStack star = new ItemStack(GalaxyChestMain.brightStar);
		
		if(p.capabilities.isCreativeMode)
		{
			return star;
		}
		
		for(Object o : w.playerEntities)
		{
			if(o instanceof EntityPlayer)
			{
				EntityPlayer pl = (EntityPlayer)o;
				
				pl.addChatMessage(new ChatComponentText(p.getDisplayName() + " has opened Pandora's Box!"));
				
				if(!pl.capabilities.isCreativeMode && p.canAttackPlayer(pl))
				{
					pl.addPotionEffect(new PotionEffect(Potion.poison.id, 10));
					pl.addPotionEffect(new PotionEffect(Potion.blindness.id, 10));
					pl.addPotionEffect(new PotionEffect(Potion.confusion.id, 10));
					pl.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 10));
					pl.addPotionEffect(new PotionEffect(Potion.hunger.id, 10));
					pl.addPotionEffect(new PotionEffect(Potion.weakness.id, 10));
					pl.addPotionEffect(new PotionEffect(Potion.wither.id, 10));
					pl.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 10));
				}
			}
		}
		
		w.createExplosion(p, p.posX, p.posY, p.posZ, 128F, true);
		p.setDead();
		return star;
	}
}
