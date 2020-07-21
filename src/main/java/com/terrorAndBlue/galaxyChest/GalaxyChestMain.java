package com.terrorAndBlue.galaxyChest;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.terrorAndBlue.galaxyChest.block.BlockGalactiumOre;
import com.terrorAndBlue.galaxyChest.block.BlockGalaxyChest;
import com.terrorAndBlue.galaxyChest.block.CosmicBlock;
import com.terrorAndBlue.galaxyChest.entity.EntityItemPersonalDrop;
import com.terrorAndBlue.galaxyChest.event.FMLEventHandler;
import com.terrorAndBlue.galaxyChest.event.VanillaEventHandler;
import com.terrorAndBlue.galaxyChest.item.ItemBrightStar;
import com.terrorAndBlue.galaxyChest.item.ItemGalaxyChest;
import com.terrorAndBlue.galaxyChest.item.ItemPandorasBox;
import com.terrorAndBlue.galaxyChest.item.ItemStarDust;
import com.terrorAndBlue.galaxyChest.item.ItemCosmicBlock;
import com.terrorAndBlue.galaxyChest.oreGen.GalactiumOreGen;
import com.terrorAndBlue.galaxyChest.packet.ClickedInSpiralPacket;
import com.terrorAndBlue.galaxyChest.packet.ClickedInSpiralResponsePacket;
import com.terrorAndBlue.galaxyChest.packet.HelpButtonClickedPacket;
import com.terrorAndBlue.galaxyChest.packet.HelpButtonClickedResponsePacket;
import com.terrorAndBlue.galaxyChest.packet.SearchRequestPacket;
import com.terrorAndBlue.galaxyChest.packet.SearchRequestResponsePacket;
import com.terrorAndBlue.galaxyChest.proxy.CommonProxy;
import com.terrorAndBlue.galaxyChest.tileEntity.TileEntityGalaxyChest;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod
(
		modid						= GalaxyChestMain.MODID,
		name 						= GalaxyChestMain.NAME,
		version 					= GalaxyChestMain.VERSION,
		dependencies 				= "required-after:Forge@[10.13.1.1217,)",
		acceptedMinecraftVersions	= "[1.7.10,)",
		canBeDeactivated = false
		)
public class GalaxyChestMain
{
	/*		
		\u00A70   = Black
		\u00A71   = Dark Blue
		\u00A72   = Dark Green
		\u00A73   = Dark Cyan
		\u00A74   = Dark Red
		\u00A75   = Purple
		\u00A76   = Orange
		\u00A77   = Light Grey
		\u00A78   = Dark Grey
		\u00A7a   = Light Green
		\u00A7b   = Light Cyan
		\u00A7c   = Light Red
		\u00A7d   = Pink
		\u00A7e   = Yellow
		\u00A7f   = White
	 */

	public static final String VERSION = "1.0";
	public static final String MODID = "GalaxyChest";
	public static final String NAME = "Galaxy Chest";

	//public static final int GUI_CLOSED_ID = 0x624;

	@Instance(MODID)
	public static GalaxyChestMain instance;

	@SidedProxy (clientSide = "com.terrorAndBlue.galaxyChest.proxy.ClientProxy", serverSide = "com.terrorAndBlue.galaxyChest.proxy.CommonProxy")
	public static CommonProxy proxy;
	public static SimpleNetworkWrapper wrapper;

	//public static ItemGalaxyChest galaxyChestItem;
	public static BlockGalaxyChest galaxyChestBlock;
	public static BlockGalactiumOre galactiumOreBlock;
	public static CosmicBlock cosmicBlock;
	
	public static Item pandorasBox;
	public static Item brightStar;
	public static Item starDust;
	
	public Configs configs;
	
	public static final class Configs
	{
		public final int maxChestSlots;
		public final int maxUsingPlayers;
		public final int galactiumOreSpawnChance;
		public final int galactiumOreVeinSize;
		public final int pandorasBoxChance;
		public final boolean galaxyLighting;
		public final boolean allowPersonalGalaxyChests;
		
		private Configs(int maxChestSlots, int maxUsingPlayers, int galactiumOreSpawnChance, int galactiumOreVeinSize,
				int pandorasBoxChance, boolean galaxyLighting, boolean allowPersonalGalaxyChests)
		{
			this.maxChestSlots = maxChestSlots;
			this.maxUsingPlayers = maxUsingPlayers;
			this.galactiumOreSpawnChance = galactiumOreSpawnChance;
			this.galactiumOreVeinSize = galactiumOreVeinSize;
			this.pandorasBoxChance = pandorasBoxChance;
			this.galaxyLighting = galaxyLighting;
			this.allowPersonalGalaxyChests = allowPersonalGalaxyChests;
		}
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent preInitEvent)
	{
		ModMetadata meta = preInitEvent.getModMetadata();
		meta.modId=MODID;
		meta.name=NAME;
		meta.version=VERSION;
		meta.description="This mod adds the Galaxy Chest to the game. It is like a regular chest, but A LOT better. ;)";
		meta.url="";
		meta.updateUrl="";
		meta.authorList=Arrays.asList (new String[] { "Terror Above", "Bluesnake198" });
		meta.credits="Programmed by Terror, textured by Bluesnake";
		meta.logoFile="";//relative to the location of the mcmod.info file
		
		Configuration config = new Configuration(new File("config/GalaxyChest.cfg"));

		config.load();

		//chestSorted = config.getBoolean("chestSorted", "Server Options", true, "Whether to sort the chest alphabetically");

		configs = new Configs(config.getInt("maxChestSlots", "Server Options", 52416, 36, 524287, "The maximum amount of item stacks a galaxy chest can hold"),
				config.getInt("maxUsingPlayers", "Server Options", 5, 1, 100, "The maximum amount of players using a given galaxy chest at the same time"),
				config.getInt("galactiumOreSpawnChance", "Server Options", 50, 1, 100, "The rarity of galactium ore: 50 indicates a 1/50 chance a chunk will contain it."),
				config.getInt("galactiumOreVeinSize", "Server Options", 8, 4, 8, "The amount of galactium ore in a vein"),
				config.getInt("pandorasBoxChance", "Server Options", 500, 10, 10000, "The rarity of pandora's box drop: 500 means a 1/500 chance."),
				config.getBoolean("galaxyLighting", "Server Options", true, "Whether galaxy chests and their components should provide block light"),
				config.getBoolean("allowPersonalGalaxyChests", "Server Options", true, "Whether to allow players to use a name tag to claim a chest as their own"));
		
		config.save();

		MinecraftForge.EVENT_BUS.register(new VanillaEventHandler());
		FMLCommonHandler.instance().bus().register(new FMLEventHandler());

		wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		
		GameRegistry.registerWorldGenerator(new GalactiumOreGen(), 0);
		
		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		galaxyChestBlock = new BlockGalaxyChest();
		galactiumOreBlock = new BlockGalactiumOre();
		cosmicBlock = new CosmicBlock(Material.rock);
		
		pandorasBox = new ItemPandorasBox();
		brightStar = new ItemBrightStar();
		starDust = new ItemStarDust();

		GameRegistry.registerBlock(galaxyChestBlock, ItemGalaxyChest.class, "BlockGalaxyChest");
		GameRegistry.registerBlock(cosmicBlock, ItemCosmicBlock.class, "BlockCosmicEnergy");
		GameRegistry.registerBlock(galactiumOreBlock, "BlockGalactiumOre");
		
		GameRegistry.registerItem(pandorasBox, "pandorasBox");
		GameRegistry.registerItem(brightStar, "containedBrightStar");
		GameRegistry.registerItem(starDust, "starDust");
		
		GameRegistry.registerTileEntity(TileEntityGalaxyChest.class, "TileEntityGalaxyChest");
		
		EntityRegistry.registerGlobalEntityID(EntityItemPersonalDrop.class, "entityItemPersonalDrop", EntityRegistry.findGlobalUniqueEntityId());
		
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("chestActive", true);
		ItemStack activeChest = new ItemStack(galaxyChestBlock);
		
		if(activeChest.hasTagCompound())
		{
			activeChest.setTagCompound(activeChest.writeToNBT(tag));
		}
		else
			activeChest.setTagCompound(tag);
		
		tag.setBoolean("chestActive", false);
		ItemStack inactiveChest = new ItemStack(galaxyChestBlock);
		
		if(inactiveChest.hasTagCompound())
		{
			inactiveChest.setTagCompound(inactiveChest.writeToNBT(tag));
		}
		else
			inactiveChest.setTagCompound(tag);
		
		
		
		GameRegistry.addSmelting(galactiumOreBlock, new ItemStack(starDust, 8), 1.0F);
		GameRegistry.addRecipe(new ItemStack(cosmicBlock), "###", "#O#", "###", '#', starDust, 'O', Blocks.obsidian);
		GameRegistry.addRecipe(inactiveChest, "$$$", "$ $", "$$$", '$', cosmicBlock);
		GameRegistry.addRecipe(activeChest, "$$$", "$B$", "$$$", '$', cosmicBlock, 'B', brightStar);
		
		proxy.registerRenderInformation(GameRegistry.findItem(MODID, "BlockGalaxyChest"));
		proxy.registerRenderInformation(pandorasBox);
		proxy.registerTileEntitySpecialRenderer();

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
		
		wrapper.registerMessage(ClickedInSpiralPacket.Handler.class, ClickedInSpiralPacket.class, 0, Side.SERVER);
		wrapper.registerMessage(ClickedInSpiralResponsePacket.Handler.class, ClickedInSpiralResponsePacket.class, 1, Side.CLIENT);
		wrapper.registerMessage(SearchRequestPacket.Handler.class, SearchRequestPacket.class, 2, Side.SERVER);
		wrapper.registerMessage(SearchRequestResponsePacket.Handler.class, SearchRequestResponsePacket.class, 3, Side.CLIENT);
		wrapper.registerMessage(HelpButtonClickedPacket.Handler.class, HelpButtonClickedPacket.class, 4, Side.SERVER);
		wrapper.registerMessage(HelpButtonClickedResponsePacket.Handler.class, HelpButtonClickedResponsePacket.class, 5, Side.CLIENT);
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event)
	{
		
	}

	public static double roundToSignificantFigures(double num, int n)
	{
		if(num == 0)
			return 0;

		final double d = Math.ceil(Math.log10(num < 0 ? -num: num));
		final int power = n - (int) d;

		final double magnitude = Math.pow(10, power);
		final long shifted = Math.round(num*magnitude);
		return shifted/magnitude;
	}

	//CLIENT only
	public static final EntityLivingBase getTarget(float par1, double distance)
	{
		Minecraft mc = Minecraft.getMinecraft();

		Entity pointedEntity;
		double d0 = distance;
		MovingObjectPosition omo = mc.renderViewEntity.rayTrace(d0, par1);
		double d1 = d0;
		Vec3 vec3 = mc.renderViewEntity.getPosition(par1);
		Vec3 vec31 = mc.renderViewEntity.getLook(par1);
		Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
		pointedEntity = null;
		Vec3 vec33 = null;
		float f1 = 1.0F;
		List list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.renderViewEntity, mc.renderViewEntity.boundingBox.addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double)f1, (double)f1, (double)f1));
		double d2 = d1;

		for (int i = 0; i < list.size(); ++i)
		{
			Entity entity = (Entity)list.get(i);

			if (entity.canBeCollidedWith())
			{
				float f2 = entity.getCollisionBorderSize();
				AxisAlignedBB axisalignedbb = entity.boundingBox.expand((double)f2, (double)f2, (double)f2);
				MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

				if (axisalignedbb.isVecInside(vec3))
				{
					if (0.0D < d2 || d2 == 0.0D)
					{
						pointedEntity = entity;
						vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
						d2 = 0.0D;
					}
				}
				else if (movingobjectposition != null)
				{
					double d3 = vec3.distanceTo(movingobjectposition.hitVec);

					if (d3 < d2 || d2 == 0.0D)
					{
						if (entity == mc.renderViewEntity.ridingEntity && !entity.canRiderInteract())
						{
							if (d2 == 0.0D)
							{
								pointedEntity = entity;
								vec33 = movingobjectposition.hitVec;
							}
						}
						else
						{
							pointedEntity = entity;
							vec33 = movingobjectposition.hitVec;
							d2 = d3;
						}
					}
				}
			}
		}
		if (pointedEntity != null && (d2 < d1 || omo == null))
		{
			omo = new MovingObjectPosition(pointedEntity, vec33);

			if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame)
			{
				mc.pointedEntity = pointedEntity;
			}
		}
		if (omo != null)
		{
			if (omo.typeOfHit == MovingObjectType.ENTITY)
			{
				if(omo.entityHit instanceof EntityLivingBase)
				{
					return (EntityLivingBase)omo.entityHit;
				}
			}
		}
		return null;
	}

	public static EntityLivingBase closestELBToPoint(World world, double x, double y, double z, double offset)
	{
		List list = world.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(x-offset, y-offset, z-offset, x+offset, y+offset, z+offset));

		double smallDist = Double.MAX_VALUE;
		EntityLivingBase closest = null;

		for(Object object : list)
		{
			if(object instanceof EntityLivingBase)
			{
				EntityLivingBase elb = (EntityLivingBase)object;
				double dist = elb.getDistance(x, y, z);

				if(dist < smallDist)
				{
					smallDist = dist;
					closest = elb;
				}
			}
		}
		return closest;
	}

	public static int getColorForRGB(int red, int green, int blue)
	{
		return new Color(red, green, blue).getRGB();
	}
}