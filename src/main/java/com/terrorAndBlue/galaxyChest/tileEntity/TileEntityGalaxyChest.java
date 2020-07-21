package com.terrorAndBlue.galaxyChest.tileEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.terrorAndBlue.galaxyChest.GalaxyChestMain;
import com.terrorAndBlue.galaxyChest.gui.GalaxyContainer;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Constants.NBT;
import scala.actors.threadpool.Arrays;

public class TileEntityGalaxyChest extends TileEntity implements IInventory
{
	public static final String TAG_NAME = "GalaxyChestItems";
	public static final int NUM_ITEMS_TO_DISPLAY = 36;
	
	private static final Comparator<ItemStack> LOCALIZED_ALPHABETICAL = new Comparator<ItemStack>()
	{
		@Override
		public int compare(ItemStack o1, ItemStack o2)
		{
			if(o1 != null && o2 == null)
				return -1;
			if(o2 != null && o1 == null)
				return 1;
			if(o1 == null && o2 == null)
				return 0;
			
			//Item i1 = o1.getItem();
			//Item i2 = o2.getItem();
			
			if(!o1.getDisplayName().equals(o2.getDisplayName()))
				return o1.getDisplayName().compareTo(o2.getDisplayName());
			
			return o2.stackSize - o1.stackSize;
		}
	};
	
	private int ticksSinceSync = -1;
	public float prevLidAngle;
	public float lidAngle;
	
	private List<EntityPlayer> usingPlayers;
	
	private byte facing;
	private boolean chestActive;
	
	private String owner;
	
	private List<ItemStack> inv;

	public TileEntityGalaxyChest()
	{
		//super();
		
		inv = new ArrayList<ItemStack>(getSizeInventory());
		usingPlayers = new ArrayList<EntityPlayer>();
		owner = "";
	}

	@Override
	public ItemStack decrStackSize(int i, int j)
	{
		if (i < inv.size() && inv.get(i) != null)
		{
			ItemStack s = inv.get(i);
			
			if (s.stackSize <= j)
			{
				ItemStack itemstack = s.copy();
				inv.set(i, null);
				markDirty();
				return itemstack;
			}
			ItemStack itemstack1 = s.splitStack(j);
			if (s.stackSize == 0)
			{
				inv.set(i, null);
			}
			markDirty();
			return itemstack1;
		}
		return null;
	}

	@Override
	public String getInventoryName()
	{
		return "Galaxy Chest";
	}

	@Override
	public int getInventoryStackLimit()
	{
		return Integer.MAX_VALUE;
	}

	@Override
	public int getSizeInventory()
	{
		return GalaxyChestMain.instance.configs.maxChestSlots;
	}
	
	public int getListSize()
	{
		return inv.size();
	}

	@Override
	public ItemStack getStackInSlot(int i)
	{
		if(i < inv.size())
			return inv.get(i);
		
		return null;
	}
	
	public List<ItemStack> getPartition(int zoom)
	{
		List<ItemStack> list = new ArrayList<ItemStack>();
		
		for(int i = zoom*NUM_ITEMS_TO_DISPLAY/4; i < (zoom+4)*NUM_ITEMS_TO_DISPLAY/4; i++)
		{
			if(inv.size() <= i || inv.get(i) == null)
				break;
			
			list.add(inv.get(i));
		}
		return list;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int arg0, ItemStack arg1)
	{
		return true;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer arg0)
	{
		return true;
	}

	public void setFacing(byte facing2)
	{
		this.facing = facing2;
	}
	
	public int getFacing()
	{
		return this.facing;
	}
	
	public boolean isChestActive()
	{
		return chestActive;
	}
	
	public void activate()
	{
		chestActive = true;
	}
	
	public String getOwner()
	{
		return owner;
	}
	
	public void setOwner(String name)
	{
		this.owner = name;
	}

	@Override
	public void openInventory()
	{
		if (worldObj == null)
		{
			return;
		}
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, GalaxyChestMain.galaxyChestBlock, 1, usingPlayers.size());
	}
	public void openInventory(EntityPlayer p)
	{
		if(!usingPlayers.contains(p))//TODO see if this comparison is valid
			usingPlayers.add(p);
		
		openInventory();
	}
	
	public int getNumUsingPlayers()
	{
		return usingPlayers.size();
	}

	@Override
	public void closeInventory()
	{
		if (worldObj == null)
		{
			return;
		}
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, GalaxyChestMain.galaxyChestBlock, 1, usingPlayers.size());
	}
	public void closeInventory(EntityPlayer p)
	{
		usingPlayers.remove(p);
		
		closeInventory();
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack stack)
	{
		if(i < getSizeInventory())
		{
			if(inv.get(i) == null)
				inv.add(i, stack);
			else
				inv.set(i, stack);
			
			markDirty();
		}
	}
	
	public boolean addItemStack(ItemStack stack)
	{
		//TODO check if merge possible before canceling operation, when inventory full
		if(inv.size() < getSizeInventory())
		{
			inv.add(inv.size(), stack);
			
			Collections.sort(inv, LOCALIZED_ALPHABETICAL);
			
			//after the sort, merge stacks to save space
			int index = inv.indexOf(stack);
			
			if(index > 0)
			{
				ItemStack prev = inv.get(index-1);
				
				//TODO figure out which checks are necessary here...
				if(prev.isItemEqual(stack) && stack.areItemStackTagsEqual(prev, stack))
				{
					int toSplit = prev.getMaxStackSize() - prev.stackSize;
					
					if(prev.isStackable() && toSplit > 0)
					{
						ItemStack newStack = stack.splitStack(Math.min(stack.stackSize, toSplit));
						newStack.stackSize += prev.stackSize;
						inv.set(index-1, newStack);
						
						if(stack.stackSize == 0)
							removeItemStack(index);
					}//shouldn't need to re-sort at this point
				}
			}
			
			if(index < getListSize()-1)
			{
				ItemStack next = inv.get(index+1);
				
				//TODO figure out which checks are necessary here...
				
				if(next.isItemEqual(stack) && stack.areItemStackTagsEqual(next, stack))
				{
					int toSplit = stack.getMaxStackSize() - stack.stackSize;
					
					if(next.isStackable() && toSplit > 0)
					{
						ItemStack newStack = next.splitStack(Math.min(next.stackSize, toSplit));
						newStack.stackSize += stack.stackSize;
						inv.set(index, newStack);
						inv.set(index+1, next);
						
						if(next.stackSize == 0)
							removeItemStack(index+1);
					}//shouldn't need to re-sort at this point
				}
			}
			
			markDirty();
			
			return true;
		}
		
		return false;
	}
	
	public ItemStack removeItemStack(int index)
	{
		markDirty();
		return inv.remove(index);//note: does NOT check for index-out-of-bounds
	}
	
	public boolean removeItemStack(ItemStack stack)
	{
		markDirty();
		return inv.remove(stack);
	}
	
	/**
	 * Assumes the list is ordered, when config says we're sorting<br>
	 * Uses a binary search to find the first occurrence of the input as the beginning of an ItemStack's display name.
	 * <br><br>If the list isn't sorted, uses a linear search.
	 * 
	 * @param searchTerm the String to search for
	 * @return the index, if found, of the first occurrence of searchTerm in this list, negative otherwise
	 */
	public int search(String searchTerm)
	{
		//if(GalaxyChestMain.instance.configs.chestSorted)
			return binarySearch(searchTerm, 0, inv.size()-1);
		
		//return linearSearch(searchTerm);
	}
	
	private int linearSearch(String searchTerm)
	{
		for(int i = 0; i < getListSize(); i++)
		{
			ItemStack stack = inv.get(i);
			
			if(stack.getDisplayName().toLowerCase().contains(searchTerm.toLowerCase()))//TODO let the user keep hitting enter to see the next result
			{
				return i;
			}
		}
		
		return -1;
	}
	
	private int binarySearch(String searchTerm, int min, int max)
	{
		if(max >= min && min >= 0)
		{
			int middle = min + (max - min)/2;
			
			if(inv.get(middle) != null)
			{
				String name = inv.get(middle).getDisplayName().toLowerCase();
				
				name = name.substring(0, Math.min(name.length(), searchTerm.length()));
				
				if(name.compareTo(searchTerm.toLowerCase()) < 0)
				{
					return binarySearch(searchTerm, middle+1, max);
				}
				
				if(name.compareTo(searchTerm.toLowerCase()) > 0)
				{
					return binarySearch(searchTerm, min, middle-1);
				}
				
				int i = middle;
				
				while(--i >= 0)
				{
					String name2 = inv.get(i).getDisplayName().toLowerCase();
					
					name2 = name2.substring(0, Math.min(name2.length(), searchTerm.length()));
					
					if(!name2.equalsIgnoreCase(searchTerm))
					{
						return i+1;
					}
				}
				
				return 0;
			}
		}

		return -1;
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		
		//TODO add usingPlayers to nbt info for client sync?
		if (worldObj != null && !this.worldObj.isRemote && usingPlayers.size() > 0 && (this.ticksSinceSync + xCoord + yCoord + zCoord) % 200 == 0)
        {
			this.usingPlayers.clear();
            float var1 = 5.0F;
            List<EntityPlayer> var2 = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(xCoord - var1, yCoord - var1, zCoord - var1, xCoord + 1 + var1, yCoord + 1 + var1, zCoord + 1 + var1));

            for (EntityPlayer var4 : var2)
            {
                if (var4.openContainer instanceof GalaxyContainer)
                {
                    this.usingPlayers.add(var4);
                }
            }
        }
		
		//System.out.println(usingPlayers.size());

        if (worldObj != null && !worldObj.isRemote && ticksSinceSync < 0)
        {
            worldObj.addBlockEvent(xCoord, yCoord, zCoord, GalaxyChestMain.galaxyChestBlock, 3, ((usingPlayers.size() << 3) & 0xF8) | (facing & 0x7));
        }

        this.ticksSinceSync++;
        prevLidAngle = lidAngle;
        float f = 0.1F;
        if (usingPlayers.size() > 0 && lidAngle == 0.0F)
        {
            double d = xCoord + 0.5D;
            double d1 = zCoord + 0.5D;
            worldObj.playSoundEffect(d, yCoord + 0.5D, d1, "random.chestopen", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }
        if (usingPlayers.size() == 0 && lidAngle > 0.0F || usingPlayers.size() > 0 && lidAngle < 1.0F)
        {
            float f1 = lidAngle;
            if (usingPlayers.size() > 0)
            {
                lidAngle += f;
            }
            else
            {
                lidAngle -= f;
            }
            
            if (lidAngle > 1.0F)
            {
                lidAngle = 1.0F;
            }
            float f2 = 0.5F;
            if (lidAngle < f2 && f1 >= f2)
            {
                double d2 = xCoord + 0.5D;
                double d3 = zCoord + 0.5D;
                worldObj.playSoundEffect(d2, yCoord + 0.5D, d3, "random.chestclosed", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
            }
            if (lidAngle < 0.0F)
            {
                lidAngle = 0.0F;
            }
        }
	}

	public void wasPlaced(EntityLivingBase entityliving, ItemStack itemStack) {}
	
	@Override
	public Packet getDescriptionPacket()//server side
	{
	    NBTTagCompound tag = new NBTTagCompound();
	    
	    writeToNBT(tag);
	    
	    return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)//received client side
	{
	    readFromNBT(pkt.func_148857_g());
	}
	
	public boolean markForUpdate()
	{
		if(worldObj == null)
			return false;
		
	    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	    return true;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		
		compound.setByte("facing", facing);
		
		writeImportantInfo(compound);
	}
	
	@SuppressWarnings("unchecked")
	public NBTTagCompound writeImportantInfo(NBTTagCompound compound)
	{
		NBTTagList items = new NBTTagList();

		for (int i = 0; i < inv.size(); i++)
		{
			if (getStackInSlot(i) != null)
			{
				NBTTagCompound item = new NBTTagCompound();
				getStackInSlot(i).writeToNBT(item);
				items.appendTag(item);
			}
		}
		
		// We're storing our items in a custom tag list using our 'tagName' from above
		// to prevent potential conflicts
		compound.setTag(TAG_NAME, items);
		
		compound.setBoolean("chestActive", chestActive);
		compound.setString("chestOwner", owner);
		
		//System.out.println("Successfully wrote "+items.tagCount()+" items.");
		
		return compound;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		
		setFacing(compound.getByte("facing"));
		
		readImportantInfo(compound);
	}
	
	@SuppressWarnings("unchecked")
	public void readImportantInfo(NBTTagCompound compound)
	{
		if(compound.hasKey(TAG_NAME))
		{
			NBTTagList items = compound.getTagList(TAG_NAME, NBT.TAG_COMPOUND);
			
			List<ItemStack> temp = new ArrayList<ItemStack>();

			for (int i = 0; i < items.tagCount(); i++)
			{
				NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);

				if (temp.size() < getSizeInventory())
				{
					temp.add(ItemStack.loadItemStackFromNBT(item));
				}
			}
			
			inv = temp;
			
			//System.out.println("Successfully read "+inv.size()+" items.");
		}
		
		chestActive = compound.getBoolean("chestActive");
		owner = compound.getString("chestOwner");
	}
}