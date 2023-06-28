package plexus.debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import plexus.InternalPlexusStarRemapper;
import plexus.behavior.PlacedObject;

public class DebugCompatibility
{
	
	//Assign specific IDs to specific objects to transfer saves from patch to patch
	//
	//You need to provide both a checklist from the version you were playing with and the save file
	//
	//
	//This is really just for my personal use, as I'm constantly changing the star IDs and want to keep my old collected ones while testing
	public static String checklist_fname = "C:/Users/Charles/Downloads/Project64-3.0.1-5664-2df3434/Roms/B3313 (v0.9)_testA_checklist.txt";
	public static String eeprom_fname = "C:/Users/Charles/Downloads/Project64-3.0.1-5664-2df3434/Save/"
			
			+ "BUILD 3313-B8823F4BB10074AF50FA7C6CE664CF80"
			
			+ "/BUILD 3313.eep";
	
	
	
	
	public static Map<Integer, String> checklist_descriptions = new HashMap<>();
	public static int assignSpecific(Set<Integer> starSet)
	{
		System.out.println("Assigning specific stars for debug purposes");
		//Apply saved stars
		
		int idsAssigned = 0;
		
		try
		{
			//Get all of the save byte to address values
			Map<Integer, List<Integer>> idToAddr = parseFromChecklist();
			
			//Load the save and gather collected stars
			byte[] eeprom = Files.readAllBytes((new File(eeprom_fname)).toPath());
			
			//Gather collected stars from the save file
			Set<Integer> collected = gatherCollected(eeprom);
			
			for(Integer col : collected)
			{
				List<Integer> addrList = idToAddr.get(col);
				if(addrList != null && addrList.size() > 0)
				{
					//Found a list of addresses to apply
					//System.out.println("Addresses to apply is of length: "+addrList.size());
					
					for(Integer addr : addrList)
					{
						for(PlacedObject object : InternalPlexusStarRemapper.placedObjects)
						{
							if(object.getAddress() == addr)
							{
								System.out.println("Assigning 0x"+Integer.toHexString(col)+" to 0x"+Integer.toHexString(addr));
								object.assign(col);
								object.setAutomaticAssignment(false);
								break;
							}
						}
					}
					
					idsAssigned++;
					
					//Remove from acceptable IDs
					starSet.remove(col);
				}
				else
				{
					System.out.println("Assignment not found for starID: "+Integer.toHexString(col));
				}
			}
			
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return idsAssigned;
	}
	
	public static Map<Integer, List<Integer>> parseFromChecklist() throws FileNotFoundException
	{
		Map<Integer, List<Integer>> idToAddr = new HashMap<>();
		try(Scanner scn = new Scanner(new File(checklist_fname)))
		{
			while(scn.hasNextLine())
			{
				String line = scn.nextLine();
				if(line.startsWith("0x"))
				{
					String[] split = line.split(":");
					if(split.length < 3)
						throw new RuntimeException("Bad parseFromChecklist, line failed split");

					int objAddr = Integer.parseInt(split[0].trim().substring(2), 16);
					
					if(split.length > 3)
					{
						//TODO could sum the rest of the split but I don't care
						String desc = split[3];
						
						checklist_descriptions.put(objAddr, desc);
					}
					
					//Skip unset
					if(!split[2].trim().startsWith("0x"))
						continue;
					
					int starId = Integer.parseInt(split[2].trim().substring(2), 16);
					
					//Skip unset
					if(starId == -1)
						continue;
					
					//System.out.println(Integer.toHexString(starId)+" -> "+Integer.toHexString(objAddr));
					
					//Found a match
					List<Integer> addrList = idToAddr.get(starId);
					if(addrList == null)
					{
						addrList = new LinkedList<Integer>();
						idToAddr.put(starId, addrList);
					}
					
					//addrList is in the map and can now be added to
					addrList.add(objAddr);
				}
			}
		}
		
		return idToAddr;
	}
	
	public static Set<Integer> gatherCollected(byte[] eeprom)
	{
		//Get collected stars, note that this ignores scene 0 stars (-1 in the save)
		
		Set<Integer> collected = new LinkedHashSet<>();
		
		for(int i = 0x10; i <= 0x1E0; i++)
		{
			//Get the bit value for the starID in question
			
			//Note that this is subtracting 0x10 to fit the save file
			int k = i - 0x10;
			
			//Divide by 8 to get the byte offset
			//Then mod 8 to get the bitmask
			
			int bytesOffset = k / 8;
			int bitmask = (k % 8) + 1;
			
			switch(bitmask)
			{
				case 0: bitmask = 0; break;
				case 1: bitmask = 0x01; break;
				case 2: bitmask = 0x02; break;
				case 3: bitmask = 0x04; break;
				case 4: bitmask = 0x08; break;
				case 5: bitmask = 0x10; break;
				case 6: bitmask = 0x20; break;
				case 7: bitmask = 0x40; break;
				case 8: bitmask = 0x80; break;
				default:
					throw new RuntimeException("bad bitmask, how: "+bitmask);
			}
			
			//Save file is RAM 80207700
			//Add to the bytesOffset to get to the start of starIDs in the save file
			//at RAM 8020770C
			bytesOffset += 0x0C;
					
			if((eeprom[bytesOffset] & bitmask) > 0)
			{
				//Collected
				collected.add(i);
			}
		}
		
		return collected;
	}
	
	//Carries over the descriptions of stars
	public static String addDescription(int objaddr)
	{
		if(DebugCompatibility.checklist_descriptions.containsKey(objaddr))
		{
			return DebugCompatibility.checklist_descriptions.get(objaddr);
		}
		
		return "";
	}
}
