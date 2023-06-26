package plexus.controller;

import java.util.ArrayList;
import java.util.List;

import plexus.InternalPlexusStarRemapper;
import plexus.Settings;
import plexus.annotation.Nullable;
import plexus.util.ComparableAddress;

public class LevelController implements ComparableAddress
{
	public static final int SHIFT = 0x10;
	
	private int address;
	//Behavior flags for this placed object
	int behaviorFlagA;
	int behaviorFlagB;
	int behaviorFlagC;
	int behaviorFlagD;
	
	public static List<LevelController> controllers = new ArrayList<>(500);
	
	public LevelController(int addr)
	{
		this.address = addr;
		this.loadFlags();
	}
	
	public int getAddress()
	{
		return this.address;
	}
	
	public void loadFlags()
	{
		if(at(this.address) != 0x24 || at(this.address + 1) != 0x18)
			throw new RuntimeException("Bad Level Controller "+this.getClass().getSimpleName()+": "+Integer.toHexString(this.address));
		
		int pointer = this.address + SHIFT;
		if(pointer + 4 >= InternalPlexusStarRemapper.rom.length)
		{
			throw new RuntimeException("Bad value: "+this.getClass().getName()+" - "+address);
		}
		
		behaviorFlagA = at(pointer + 0);
		behaviorFlagB = at(pointer + 1);
		behaviorFlagC = at(pointer + 2);
		behaviorFlagD = at(pointer + 3);
		
		if(Settings.DEBUG)
		{
			System.out.println(this);
		}
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("0x");
		sb.append(Integer.toHexString(this.address));
		sb.append(" : ");
		sb.append(this.getClass().getSimpleName());
		sb.append(" : \t\t0x");
		sb.append(stringByte(behaviorFlagA));
		sb.append(stringByte(behaviorFlagB));
		sb.append(stringByte(behaviorFlagC));
		sb.append(stringByte(behaviorFlagD));
		if(bumpsStarCount())
		{
			sb.append("\t\t");
			sb.append("bump");
		}
		return sb.toString();
	}
	
	/** Whether this level controller applies & 00000100 to star actors that spawn in, may not apply to some types **/
	public boolean bumpsStarCount()
	{
		return (behaviorFlagD & 0x60) > 0x00;
	}
	
	private String stringByte(int i)
	{
		String val = Integer.toHexString(i & 0xFF);
		if(val.length() == 2)
			return val;
		
		return "0" + val;
	}

	private int at(int i)
	{
		return Byte.toUnsignedInt(InternalPlexusStarRemapper.rom[i]);
	}
	
	@Nullable
	public static LevelController getControllerForAddress(int addr)
	{
		//This'll definitely be wrong unless controller is always created first
		//Which it might be, considering???
		//Let's try it and see if it looks wrong
		
		//TODO it looks really wrong, but it's funny anyway
		//Not like it matters I'm turning it off in the current version
		//Whatever
		//wahoo
		
		LevelController bestController = null;
		
		for(LevelController control : controllers)
		{
			if(control.address > addr)
				break;
			
			bestController = control;
		}
		
		return bestController;
	}

	@Override
	public int getComparableAddress()
	{
		return getAddress();
	}
}
