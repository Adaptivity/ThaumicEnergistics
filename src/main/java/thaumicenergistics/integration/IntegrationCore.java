package thaumicenergistics.integration;

import net.minecraft.nbt.NBTTagCompound;
import thaumicenergistics.ThaumicEnergistics;
import thaumicenergistics.api.ThEApi;
import thaumicenergistics.blocks.BlockArcaneAssembler;
import thaumicenergistics.util.EffectiveSide;
import thaumicenergistics.util.ThELog;
import appeng.api.config.Upgrades;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class IntegrationCore
{
	/**
	 * Class path to all integration modules
	 */
	private static final String CLASS_PATH = ThaumicEnergistics.MOD_ID + ".integration.Module";

	/**
	 * Module ID for Waila
	 */
	private static final String MODID_WAILA = "Waila";

	/**
	 * Module ID for NEI
	 */
	private static final String MODID_NEI = "NEI";

	/**
	 * Module ID for ExtraCells2
	 */
	private static final String MODID_EC2 = "EC2";

	@SideOnly(Side.CLIENT)
	private static void integrateWithClientMods()
	{
		// Integrate with version checker
		IntegrationCore.integrateWithVersionChecker();

		// Integrate with NEI
		IntegrationCore.integrateWithNEI();

		// Integrate with Waila
		IntegrationCore.integrateWithMod( IntegrationCore.MODID_WAILA );
	}

	/**
	 * Integrates with the specified mod if it exists
	 * 
	 * @param modID
	 */
	private static void integrateWithMod( final String modID )
	{
		try
		{
			// Attempt to get the module
			Class<?> module = Class.forName( IntegrationCore.CLASS_PATH + modID );

			// Instantiate it
			module.newInstance();

			// Log success
			ThELog.info( "Successfully integrated with %s", modID );
		}
		catch( Throwable e )
		{

			// Log failure
			ThELog.warning( "Skipping integrating with %s", modID );
		}
	}

	/**
	 * Integrates with NEI
	 */
	private static void integrateWithNEI()
	{
		IntegrationCore.integrateWithMod( IntegrationCore.MODID_NEI );
	}

	/**
	 * Integrates with version checker
	 */
	private static void integrateWithVersionChecker()
	{
		// Create the tag
		NBTTagCompound tag = new NBTTagCompound();

		// Set the project name
		tag.setString( "curseProjectName", "223666-thaumic-energistics" );

		// Set the file name
		tag.setString( "curseFilenameParser", "thaumicenergistics-[].jar" );

		// Set the mod name
		tag.setString( "modDisplayName", "Thaumic Energistics" );

		// Send to version checker
		FMLInterModComms.sendRuntimeMessage( ThaumicEnergistics.MOD_ID, "VersionChecker", "addCurseCheck", tag );

	}

	/**
	 * Integrate with all modules
	 */
	public static void init()
	{
		long startTime = ThELog.beginSection( "Integration" );
		try
		{
			if( EffectiveSide.isClientSide() )
			{
				IntegrationCore.integrateWithClientMods();
			}

			// Integrate with EC2 if blacklisting enabled
			if( ThEApi.instance().config().blacklistEssentiaFluidInExtraCells() )
			{
				IntegrationCore.integrateWithMod( IntegrationCore.MODID_EC2 );
			}
		}
		catch( Throwable e )
		{
		}

		// Register the Arcane Assembler for upgrades
		Upgrades.SPEED.registerItem( ThEApi.instance().blocks().ArcaneAssembler.getStack(), BlockArcaneAssembler.MAX_SPEED_UPGRADES );

		ThELog.endSection( "Integration", startTime );
	}
}
