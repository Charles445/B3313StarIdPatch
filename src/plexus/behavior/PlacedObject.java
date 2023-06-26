package plexus.behavior;

import plexus.InternalPlexusStarRemapper;
import plexus.Settings;
import plexus.controller.LevelController;
import plexus.util.ComparableAddress;

public abstract class PlacedObject implements ComparableAddress
{
	
	int address; //Address for this placed object in the ROM
	//Behavior flags for this placed object
	int behaviorFlagA;
	int behaviorFlagB;
	int behaviorFlagC;
	int behaviorFlagD;
	
	int starId;
	
	public static final int SHIFT = 0x10;
	
	public PlacedObject(int addr)
	{
		this.address = addr;
		//Load the appropriate flags from the rom
		this.loadFlags();
	}
	
	void crash()
	{
		throw new RuntimeException("Bad Object "+this.getClass().getSimpleName()+": "+Integer.toHexString(this.address));
	}
	
	public int getAddress()
	{
		return this.address;
	}
	
	private void loadFlags()
	{
		if(at(this.address) != 0x24 || at(this.address + 1) != 0x18)
			crash();
		
		
		int pointer = this.address + SHIFT;
		if(pointer + 4 >= InternalPlexusStarRemapper.rom.length)
		{
			throw new RuntimeException("Bad value: "+this.getClass().getName()+" - "+Integer.toHexString(address));
		}
		
		behaviorFlagA = at(pointer + 0);
		behaviorFlagB = at(pointer + 1);
		behaviorFlagC = at(pointer + 2);
		behaviorFlagD = at(pointer + 3);
		
		//Load the star id
		this.recalculasteStarId();
		
		if(Settings.DEBUG)
		{
			System.out.println(this);
		}
	}
	
	@Override
	public String toString()
	{
		boolean isRed = Settings.version.isRed(this.starId);
		StringBuilder sb = new StringBuilder();
		
		sb.append("0x");
		sb.append(Integer.toHexString(this.address));
		sb.append(" : ");
		sb.append(this.getClass().getSimpleName());
		sb.append(" : \t\t");
		sb.append((starId==-1?"-1":"0x"+Integer.toHexString(starId)+(isRed?"\t\tRed":"")));
		LevelController controller = LevelController.getControllerForAddress(this.address);
		if(controller != null && controller.bumpsStarCount())
		{
			sb.append("  -  might apply bump");
		}
		
		return sb.toString();
	}
	
	public void recalculasteStarId()
	{
		this.evalStarId();
		
		if(this.starId < 0x10)
		{
			//Old system
			//TODO figure out the scene, somehow, it'd be very helpful
			this.starId = -1;
		}
	}
	
	public void assign(int starId)
	{
		this.starId = starId;
		int desiredStarId = starId;
		createBehaviorFlags();
		
		//Sanity check
		this.recalculasteStarId();
		if(desiredStarId != this.starId)
		{
			throw new RuntimeException("Recalculation FAILED: "+this.getClass().getSimpleName()+" : "+desiredStarId+", was "+this.starId+" instead");
		}
	}
	
	/** create behavior flags from this.starId **/
	abstract void createBehaviorFlags();
	
	abstract void evalStarId();
	
	public int getStarId()
	{
		return starId;
	}
	
	private int at(int i)
	{
		return Byte.toUnsignedInt(InternalPlexusStarRemapper.rom[i]);
	}
	
	public void applyToRom()
	{
		//Apply the behavior flags to the ROM
		//
		//We will be doing a LOT of sanity checks here
		
		applyValueAt(this.behaviorFlagA, this.address + SHIFT + 0);
		applyValueAt(this.behaviorFlagB, this.address + SHIFT + 1);
		applyValueAt(this.behaviorFlagC, this.address + SHIFT + 2);
		applyValueAt(this.behaviorFlagD, this.address + SHIFT + 3);
		
		
	}
	
	private void applyValueAt(int value, int destination)
	{
		
		
		//Does this just work?
		InternalPlexusStarRemapper.rom[destination] = (byte)value;
		
		//SANITY CHECK
		
		int sanityResult = this.at(destination);
		
		if(value != sanityResult)
		{
			//It's not even vague, either they all work or none of them work
			throw new RuntimeException("Sanity check failed: APPLYVALUEAT MATH FAILURE");
		}
		
	}
	
	/** Override to block specific IDs based on object type or data **/
	public boolean acceptsStarId(int starId)
	{
		return true;
	}
	
	@Override
	public int getComparableAddress()
	{
		return getAddress();
	}
}
