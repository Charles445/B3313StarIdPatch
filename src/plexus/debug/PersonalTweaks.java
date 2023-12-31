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
			
			//Prevents the music box effect at night
			System.out.println("Tweak: Disable Nighttime Music Box");
			//807ECA60 - ADDIU T3, R0, 0x0000
			setRom(0x194EB0, 0x24,0x0B,0x00,0x00);
			//807EDB94 - ADDIU V1, R0, 0x0000
			setRom(0x195FE4, 0x24,0x03,0x00,0x00);
			
			//Removes the angle-locking when running towards the camera
			System.out.println("Tweak: Remove movement angle locking");
			//802840AC
			setRom(0x3F0AC, 0x00, 0x00, 0x00,0x00);
			//TODO restore this to vanilla, I assume vanilla does this a little bit to allow mario to walk past lakitu
			//just that some change in b3313 made it never end
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
