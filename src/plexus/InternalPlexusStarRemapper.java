package plexus;

import static plexus.Settings.CHECKLIST;
import static plexus.Settings.DEBUG;
import static plexus.Settings.OUTPUT;
import static plexus.Settings.version;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import plexus.behavior.Bowser;
import plexus.behavior.Box;
import plexus.behavior.FirstByteSensitive;
import plexus.behavior.FourthByteSensitive;
import plexus.behavior.Freestanding;
import plexus.behavior.Hidden;
import plexus.behavior.PlacedObject;
import plexus.behavior.RedCoins;
import plexus.behavior.RedCoinsBowser;
import plexus.controller.LevelController;
import plexus.debug.DebugCompatibility;
import plexus.debug.PersonalTweaks;
import plexus.util.ComparableAddress;

public class InternalPlexusStarRemapper
{
	public static byte[] rom;
	
	public static List<PlacedObject> placedObjects = new ArrayList<>(1000);
	
	public static int IDMAX = 800;
	
	public static void main(String[] args)
	{
		
		try
		{
			rom = Files.readAllBytes((new File(Settings.ROM_FILE)).toPath());
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		
		if(rom == null)
		{
			throw new RuntimeException("rom is null?");
		}
		
		//Rom loaded
		
		//Populate level controllers
		
		for(int i=0;i<version.getLevelControllers().length;i++)
		{
			LevelController.controllers.add(new LevelController(version.getLevelControllers()[i]));
		}
		//Sort the levelControllers
		LevelController.controllers.sort(ComparableAddress.instance);
		
		//Add based on priority
		
		//Start with things that are sensitive, as well as things that are probably sensitive
		for(int val : version.getStarsBowser())
		{
			placedObjects.add(new Bowser(val));
		}
		
		for(int val : version.getStarsHidden())
		{
			placedObjects.add(new Hidden(val));
		}
		
		for(int val : version.getStarsFirstByteSensitive())
		{
			placedObjects.add(new FirstByteSensitive(val));
		}
		
		for(int val : version.getStarsFourthByteSensitive())
		{
			placedObjects.add(new FourthByteSensitive(val));
		}
		
		//Now for non-sensitive objects
		
		for(int val : version.getStarsRedCoinsBowser())
		{
			placedObjects.add(new RedCoinsBowser(val));
		}
		
		for(int val : version.getStarsRedCoins())
		{
			placedObjects.add(new RedCoins(val));
		}
		
		for(int val : version.getStarsBox())
		{
			placedObjects.add(new Box(val));
		}
		
		for(int val : version.getStarsFreestanding())
		{
			placedObjects.add(new Freestanding(val));
		}
		
		if(DEBUG)
		{
			System.out.println("------------");
			System.out.println("------------");
			System.out.println("------------");
		}
		
		int nextId = IDMAX;
		
		boolean idLimitReached = false;
		int idsAssigned = 0;
		
		Set<Integer> starSet = new LinkedHashSet<Integer>(); ///Linked hash set is quite the data structure huh
		
		//Set up the id set
		
		while(nextId >= 0)
		{
			if(version.isValidNonRedStarId(nextId))
			{
				starSet.add(nextId);
			}
			nextId--;
		}
		
		//DEBUG, assign specific ids first for migration
		if(Settings.MIGRATE)
			idsAssigned += DebugCompatibility.assignSpecific(starSet);
		
		for(PlacedObject object : placedObjects)
		{
			//Check for automatic assignment first
			//If this is off, then the object has already been assigned
			if(!object.getAutomaticAssignment())
				continue;
			
			//Try manual tweaks first
			if(!version.manualPlacedObjectTweaks(object) && !version.isRed(object.getStarId()))
			{
				boolean succeeded = false;
				
				int usedId = -1;
				
				for(Integer newId : starSet)
				{
					//Test whether this ID is suitable for this object
					if(object.acceptsStarId(newId))
					{
						//It is
						//Assign the ID
						usedId = newId;
						object.assign(newId);
						idsAssigned++;
						succeeded = true;
						break;
					}
				}
				
				//Remove the used ID from the set
				if(succeeded)
					starSet.remove(usedId);
				
				//Check if a suitable ID was not found for the object
				if(!succeeded)
				{
					System.out.println("failed applying id");
					idLimitReached = true;
				}
			}
			
			System.out.println(object);
		}
		
		if(idLimitReached)
		{
			System.out.println("WARNING: ID Limit was reached, or a suitable ID was not found for all objects");
		}

		System.out.println("Assigned "+idsAssigned+" ids...");
		System.out.println("Applying "+placedObjects.size()+" objects...");
		for(PlacedObject object : placedObjects)
		{
			object.applyToRom();
		}
		
		//Run tweaks
		version.romTweaks();
		if(Settings.PERSONAL_TWEAKS)
			PersonalTweaks.romTweaks();
		
		System.out.println("Done applying!");
		
		if(OUTPUT)
		{
			System.out.println("Writing ROM to disk...");
			try
			{
				Files.write((new File(Settings.OUT_FILE)).toPath(), rom);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("OUT is false, skipping write to disk");
		}
		
		System.out.println("All done!");
		if(CHECKLIST)
		{
			System.out.println("\n\n");
			
			//Create and display a sorted checklist of ids and such
			
			placedObjects.sort(ComparableAddress.instance);
			
			for(PlacedObject obj : placedObjects)
			{
				String output = "0x"+Integer.toHexString(obj.getAddress());
				
				while(output.length() < 13)
					output += " ";
				
				output = output + " : " + obj.getClass().getSimpleName();
				
				while(output.length() < 35)
					output += " ";
				
				output += " : ";
				
				if(obj.getStarId() == -1)
				{
					output += "-1";
				}
				else
				{
					output += "0x"+Integer.toHexString(obj.getStarId());
				}
				
				while(output.length() < 43)
					output += " ";
				
				output += " : ";
				
				if(Settings.MIGRATE)
					output += DebugCompatibility.addDescription(obj.getAddress());
				
				System.out.println(output);
			}
		}
	}
}
