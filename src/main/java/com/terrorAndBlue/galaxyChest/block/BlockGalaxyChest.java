package com.terrorAndBlue.galaxyChest.block;

import java.util.HashMap;

import com.terrorAndBlue.galaxyChest.GalaxyChestMain;
import com.terrorAndBlue.galaxyChest.item.ItemGalaxyChest;
import com.terrorAndBlue.galaxyChest.tileEntity.TileEntityGalaxyChest;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockGalaxyChest extends BlockContainer
{
	private static final HashMap<Location, NBTTagCompound> TAG_MAP = new HashMap<Location, NBTTagCompound>();
	
	public BlockGalaxyChest()
	{
		super(Material.rock);
		
		//TODO may adjust hardness and/or harvest level

		this.setBlockBounds(0.0625F, 0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
		this.setHardness(8.0F);
		this.setResistance(10.0F);
		this.setBlockName("GalaxyChest");
		this.setBlockTextureName("galaxychest:blockGalaxyChest");
		this.setCreativeTab(CreativeTabs.tabDecorations);
		
		if(GalaxyChestMain.instance.configs.galaxyLighting)
		{
			this.setLightLevel(1f);
			this.setLightOpacity(0);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World w, int meta)
	{
		return new TileEntityGalaxyChest();
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	/*@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}*/
	
	/*@Override
	public boolean canRenderInPass(int pass)
	{
		return false;
	}*/
	
	@Override
	public int getRenderType()
	{
		return -1;
	}
	
	/*@Override
	public int getRenderBlockPass()
    {
		return 1;
    }*/
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int direction, float f0, float f1, float f2)
	{
		TileEntity te = world.getTileEntity(x, y, z);

		if (te == null || !(te instanceof TileEntityGalaxyChest))
		{
			return true;
		}
		
		if (world.isSideSolid(x, y+1, z, ForgeDirection.DOWN))
		{
			return true;
		}

		if (world.isRemote)
		{
			return true;
		}
		
		TileEntityGalaxyChest tegc = (TileEntityGalaxyChest)te;
		
		if(tegc.isChestActive())
		{
			if(player.getHeldItem() != null && player.getHeldItem().getItem() == Items.name_tag && GalaxyChestMain.instance.configs.allowPersonalGalaxyChests)
			{
				if(tegc.getOwner() == null || tegc.getOwner().length() == 0)
				{
					tegc.setOwner(player.getCommandSenderName());
					tegc.markForUpdate();
					
					player.getHeldItem().stackSize--;
				}
				else
				{
					player.addChatComponentMessage(new ChatComponentText("This chest has already been claimed."));
				}
				
				return true;
			}
			
			if(tegc.getOwner() != null && tegc.getOwner().length() > 0 && !tegc.getOwner().equalsIgnoreCase(player.getCommandSenderName()) && GalaxyChestMain.instance.configs.allowPersonalGalaxyChests)
			{
				player.addChatComponentMessage(new ChatComponentText("The chest is locked. It must belong to someone else."));
				return true;
			}
			
			if(tegc.getNumUsingPlayers() < GalaxyChestMain.instance.configs.maxUsingPlayers)
				player.openGui(GalaxyChestMain.instance, 0, world, x, y, z);
		}
		else
		{
			if(player.getHeldItem() != null && player.getHeldItem().getItem() == GalaxyChestMain.brightStar)
			{
				tegc.activate();
				tegc.markForUpdate();
				player.getHeldItem().stackSize--;
			}
			else
			{
				player.addChatComponentMessage(new ChatComponentText("The chest won't open. Perhaps you need to activate it somehow..."));
			}
		}
		
		return true;
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack itemStack)
	{
		NBTTagCompound tag = null;
		if(itemStack != null)
		{
			tag = itemStack.getTagCompound();
		}
		
		byte chestFacing = 0;
		int facing = MathHelper.floor_double((entityliving.rotationYaw * 4F) / 360F + 0.5D) & 3;
		if (facing == 0)
		{
			chestFacing = 2;
		}
		if (facing == 1)
		{
			chestFacing = 5;
		}
		if (facing == 2)
		{
			chestFacing = 3;
		}
		if (facing == 3)
		{
			chestFacing = 4;
		}
		
		TileEntity te = world.getTileEntity(x, y, z);
		if (te != null && te instanceof TileEntityGalaxyChest)
		{
			TileEntityGalaxyChest tegc = (TileEntityGalaxyChest) te;
			//tegc.wasPlaced(entityliving, itemStack); //does nothing currently
			
			if(tag != null)
			{
				tegc.readImportantInfo(tag);
			}
			else//tag null, we assume chest was NOT created in survival
			{
				tegc.activate();
			}
			tegc.setFacing(chestFacing);
			tegc.markForUpdate();
		}
	}
	
	@Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        super.onBlockAdded(world, x, y, z);
        world.markBlockForUpdate(x, y, z);
    }
	
	@Override
	public void breakBlock(World w, int x, int y, int z, Block b, int f)
	{
		TileEntity te = w.getTileEntity(x, y, z);
		
		if(!w.isRemote && te instanceof TileEntityGalaxyChest)
		{
			TileEntityGalaxyChest tegc = (TileEntityGalaxyChest)te;
			
			NBTTagCompound tag = new NBTTagCompound();
			
			TAG_MAP.put(new Location(x, y, z, w.provider.dimensionId), tegc.writeImportantInfo(tag));
			System.out.println("written tag "+tag);
		}
	}
	
	@Override
	public void harvestBlock(World w, EntityPlayer p, int x, int y, int z, int f)
	{
		p.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
		p.addExhaustion(0.025F);

		ItemStack stack = this.createStackedBlock(f);

		if (stack != null)
		{
			final Location LOC = new Location(x, y, z, w.provider.dimensionId);
        	NBTTagCompound tag = TAG_MAP.get(LOC);
        	
        	if(tag != null)
        	{
        		if(stack.hasTagCompound())
        		{
        			stack.setTagCompound(stack.writeToNBT(tag));
        		}
        		else
        			stack.setTagCompound(tag);
        		
        		TAG_MAP.remove(LOC);
        	}
        	
			this.dropBlockAsItem_do(w, x, y, z, stack);
		}
	}
	
	/**
     * Spawns EntityItem in the world for the given ItemStack if the world is not remote.
     */
    protected void dropBlockAsItem_do(World w, int x, int y, int z, ItemStack stack)
    {
        if (!w.isRemote && w.getGameRules().getGameRuleBooleanValue("doTileDrops"))
        {
            float var6 = 0.7F;
            double var7 = (double)(w.rand.nextFloat() * var6) + (double)(1.0F - var6) * 0.5D;
            double var9 = (double)(w.rand.nextFloat() * var6) + (double)(1.0F - var6) * 0.5D;
            double var11 = (double)(w.rand.nextFloat() * var6) + (double)(1.0F - var6) * 0.5D;
            EntityItem var13 = new EntityItem(w, (double)x + var7, (double)y + var9, (double)z + var11, stack);
            var13.delayBeforeCanPickup = 10;
            w.spawnEntityInWorld(var13);
        }
    }
    
    private static class Location
    {
    	public int x, y, z, dim;
    	
    	public Location(int x, int y, int z, int dim)
    	{
    		this.x = x;
    		this.y = y;
    		this.z = z;
    		this.dim = dim;
    	}
    	
    	public boolean equals(Object o)
    	{
    		if(o instanceof Location)
    		{
    			Location loc = (Location)o;
    			
    			return this.x == loc.x && this.y == loc.y && this.z == loc.z && this.dim == loc.dim;
    		}
    		return false;
    	}

		public int hashCode()
		{
			return x;
		}
    }
}
