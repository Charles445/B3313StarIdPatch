package plexus;

import plexus.version.Version;
import plexus.version.VersionPointNine;

public class Settings
{
	//Input file for the ROM
	public static String ROM_FILE = "C:/Users/Charles/Downloads/Project64-3.0.1-5664-2df3434/Roms/B3313 (v0.9).z64";
	
	//Output file for the ROM
	public static String OUT_FILE = "C:/Users/Charles/Downloads/Project64-3.0.1-5664-2df3434/Roms/B3313 (v0.9)_out.z64";
	
	//Version object to use
	public static Version version = new VersionPointNine();
	
	//Displays the read objects and written objects
	public static boolean DEBUG = true;
	
	//Whether to write the output rom to disk or not
	public static boolean OUTPUT = true;
	
	//Prints out a checklist with star IDs for personal use, if you want
	//It's a hassle to use though, not recommended
	//If you play with a debugger, put a breakpoint at 0x807F0904, when you collect a star register A1 will have the number
	//Please note this doesn't have all star IDs, only ones the program is able to recognize
	public static boolean CHECKLIST = true;
	
}
