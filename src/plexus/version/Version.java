package plexus.version;

import plexus.InternalPlexusStarRemapper;
import plexus.behavior.PlacedObject;

public interface Version
{
	/** Returns true if a manual tweak was applied **/
	public boolean manualPlacedObjectTweaks(PlacedObject obj);
	
	/** For raw edits to the ROM **/
	public void romTweaks();
	
	public int[] getStarsFreestanding();
	
	public int[] getStarsRedCoins();
	
	public int[] getStarsRedCoinsBowser();
	
	public int[] getStarsHidden();
	
	public int[] getStarsBox();
	
	public int[] getStarsBowser();
	
	public int[] getStarsFirstByteSensitive();
	
	public int[] getStarsFourthByteSensitive();
	
	public boolean isRed(int starId);

	public boolean isValidNonRedStarId(int starId);
	
	/** Level controllers for checking weird custom flags **/
	default int[] getLevelControllers()
	{
		return new int[0];
	}
	
	default void setRom(int address, int... value)
	{
		for(int i=0;i<value.length;i++)
		{
			this.setRom(address + i, value[i]);
		}
	}
	
	default void setRom(int address, int value)
	{
		byte bval = (byte)value;
		
		//System.out.println("setRom: "+Integer.toHexString(address)+" - "+Integer.toHexString(value));
		
		if(InternalPlexusStarRemapper.rom.length <= address)
			throw new RuntimeException("ROM too small for address ("+this.getClass().getSimpleName()+")");
		
		InternalPlexusStarRemapper.rom[address] = bval;
	}
}
