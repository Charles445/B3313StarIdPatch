package plexus.debug;

import plexus.InternalPlexusStarRemapper;
import plexus.Settings;
import plexus.version.VersionPointNine;

public class PersonalTweaks
{
	public static void romTweaks()
	{
		//TODO do these
		//Check version first
		
		if(Settings.version.getClass().equals(VersionPointNine.class))
		{
			System.out.println("Tweak: Health Regen");
			//Enable health regen even after earlygame
			setRom(0x115B82, 0x4F);
			
			//Disables blue harming coins
			//8036FAA8 - ADDIU	V0, R0, 0x0000
			System.out.println("Tweak: Disable Blue Harming Coins");
			setRom(0x1148DB, 0x00);
		}
		
		
	}
	
	private static void setRom(int address, int... value)
	{
		for(int i=0;i<value.length;i++)
		{
			setRom(address + i, value[i]);
		}
	}
	
	private static void setRom(int address, int value)
	{
		byte bval = (byte)value;
		
		//System.out.println("setRom: "+Integer.toHexString(address)+" - "+Integer.toHexString(value));
		
		if(InternalPlexusStarRemapper.rom.length <= address)
			throw new RuntimeException("ROM too small for address (PersonaLTweaks)");
		
		InternalPlexusStarRemapper.rom[address] = bval;
	}
}
