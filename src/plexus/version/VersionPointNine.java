package plexus.version;

import plexus.behavior.PlacedObject;

public class VersionPointNine implements Version
{
	//Payload Information
	//
	//
	//807FEB00 has rabbit payload (5 words)
	//
	//807FEB20 has null animation payload (8 words)
	
	//Scene 0 information
	//7 available bits (0x7F)
	//If I am not mistaken, these are the bits the yellow switch can affect
	// 0x01 (0) 
	// 0x02 (1) 
	// 0x04 (2) //Entrance with a timer, seems impossible? It does create a star unlike the other one, yt comment theorized launching from a glitchy ramp
	// 0x08 (3) //MIPS courtyard 1
	// 0x10 (4) //MIPS courtyard 2
	// 0x20 (5) 
	// 0x40 (6) 
	
	//This red star stuff is confusing
	
	@Override
	public void romTweaks()
	{
		//Allow the star counter to observe all red stars
		this.setRom(0x19902B, 0x02); //from 0x03
		
		//Remove the starID bumping functionality
		//ad524 37 19 01 00
		//ad524 37 19 00 00
		this.setRom(0xAD526, 0x00);
		
		//Allow any level to give stars
		//Bowser 3 is otherwise blacklisted, presumably for the grand star
		//We're not getting a grand star any time soon, I don't think you can actually get one in b3313 0.9
		//This happened to 0x4ca471c
		this.setRom(0x198E1C, 0x00,0x00,0x00,0x00);
		
		//Fix rabbit so his star ids factor in courseId
		this.rabbitPayload();
		//Possible collisions to note...
		//
		//08 and 10 mask, so S 3, S 4
		//
		// Rabbit in alcove - 00 -> FF, no change
		// Rabbit in red hall - 01 -> 00, 0 3 and 0 4
		//
	}
	
	private void rabbitPayload()
	{
		//Rabbit takes the course id
		//This should prevent missing any stars
		//This does not avoid collisions
		
		//RAM 802F849C
		//J  	0x807FEB00
		this.setRom(0xB349C, 0x08,0x1F,0xFA,0xC0);
		
		//RAM 807FEB00
		//LH 	A0, 0xDDF4 (A0)	; copied from 802F849C
		//LUI 	A1, 0x8034
		//LH 	A1, 0xBAC6 (A1)
		//J  	0x802F84A4
		//ADDIU A1, A1, 0xFFFF
		this.setRom(0x7DEB10, 0x84,0x84,0xDD,0xF4,
										0x3C,0x05,0x80,0x34,
										0x84,0xA5,0xBA,0xC6,
										0x08,0x0B,0xE1,0x29,
										0x24,0xA5,0xFF,0xFF);
		
	}
	
	
	@Override
	public boolean manualPlacedObjectTweaks(PlacedObject obj)
	{
		//Manually set the bumped bowser
		if(obj.getAddress() == 0x4c04d8c)
		{
			obj.assign(0x148);
			return true;
		}
		//A freestanding real red star that would otherwise overlap with a not-so-shy guy's real red star
		//This change may not last, if this ends up not being justified
		else if(obj.getAddress() == 0x4675de8)
		{
			obj.assign(0x149);
			return true;
		}
		
		// // //
		//DO NOT PUT ANYTHING SPECIFIC BELOW HERE!
		// // //
		
		//TODO VERIFY Skip huge batch due to the lack of level controllers?
		//Yet there might be changes to vanilla? Like tall tall mountain?
		//Dunno
		//... is there a scene layout somewhere this is a huge hassle
		//honestly there's a bunch before that too
		if(obj.getAddress() < 0x01400000)
		{
			return true;
		}
		
		//Skip certain object addresses
		int addr = obj.getAddress();
		for(int tst : skippedAddresses)
		{
			if(addr == tst)
				return true;
		}
		
		//DO NOT PUT ANYTHING HERE!
		
		return false;
	}
	
	private int[] skippedAddresses = new int[] {
			0x4c04d8c, //Bowser, bumped to red star, but still invalid red star... redundant
			0x4c04cb8, //Bowser, invalid red star, no bump
			0x3a96100, //Green star, has 0x142
			
	};

	private int[] manualStarIdBlacklist = new int[] {
			//Blacklist for starIds that are annoying or otherwise tough to move
			
			//Dark Igloo Piranha Plants (scene_id 3, 00 flag) = (scene 02, 0) = 0x20
			//There is no star ID setter in this area, therefore this one must be protected
			0x20,
			
			//Rabbit in the dark red hallway, now that the rabbit is scene based (scene_id 1) = (scene 0)(3 and 4 flags) = 0 3, 0 4
			//Two new stars, then
			0x13,
			0x14,
			
			//The randomly entered timed dark whomp towers that kill you
			//Glitches out depending on which warp you use for some reason
			//Leaving it alone to avoid that
			0xaa,
			
			//Rabbit in a beta rainbow world, now that the rabbit is scene based (scene_id 10) = (scene 9)(3 and 4 flags) = 9 3, 9 4
			0x5b,
			0x5c,
			
			//Treasure chests in one of the jolly roger bay variants, spawns two of the same star
			//scene_id 23 = scene 22 starid 2, (22 2)(0xc2)
			0xc2,
			
			//Manta ray, there's only one in the game!
			//scene_id 23 = scene 22 starid 0, (22 0) (0xc0)
			0xc0,
			
			//Bobomb gift at Nebula Exploding Factory, scene_id 2, scene 1 starid 0, (1 0), 0x18
			0x18
			
			//Red stars that are created by custom bosses
			//146, 14A
			//Green star
			//142
	};
	
	
	@Override
	public boolean isRed(int starId) 
	{
		//I managed to screw this up
		//Course 38, F8 and Course 39, FF
		//| | | | | o o o
		//8 4 2 1 8 4 2 1
		//7 6 5 4 3 2 1 0
		if(starId >= 0x143 && starId <= 0x14f)
		{
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean isValidNonRedStarId(int starId)
	{
		if(starId > 0x1D7) //past course 56, star 7
			return false;
		
		if(starId < 0x10)
			return false;
		
		//TODO this is probably wrong, I forgot
		//Red stars are confusing
		if(starId >= 0x140 && starId <= 0x14f)
			return false;
		
		//TODO this isn't accurate but it's not unusable
		//The course 36 block is limited, and course 37 is off limits
		//For now just avoiding anything 0x130
		if(starId>=0x130 && starId <= 0x13F)
			return false;
		
		//TODO 0x12F and 0x12E are meant to be green? doesn't matter in 0.9 but might be worth blocking just for fun
		//Yeah that's fun, blocking the remaining two green star slots
		if(starId==0x12E || starId == 0x12F)
			return false;
		
		
		//Sanity check for good measure
		if(isRed(starId))
			return false;
		
		for(int manual : manualStarIdBlacklist)
		{
			if(manual == starId)
				return false;
		}
		
		return true;
		
	}
	
	
	//TODO handle this better
	//Going to reverse these arrays temporarily
	//As later entries are far more likely to be current / in use
	
	private int[] reverse(int[] inArr)
	{
		int[] outArr = new int[inArr.length]; //this isn't leetcode
		for(int i=0;i<outArr.length;i++)
		{
			outArr[i] = inArr[inArr.length - 1 - i];
		}
		
		return outArr;
	}

	@Override
	public int[] getStarsFreestanding() {
		return reverse(starsFreestanding);
	}

	@Override
	public int[] getStarsRedCoins() {
		return reverse(starsRedCoins);
	}

	@Override
	public int[] getStarsRedCoinsBowser() {
		return reverse(starsRedCoinsBowser);
	}

	@Override
	public int[] getStarsBox() {
		return reverse(starsBox);
	}
	
	@Override
	public int[] getStarsHidden() {
		return reverse(starsHidden);
	}

	@Override
	public int[] getStarsBowser() {
		return reverse(starsBowser);
	}
	
	@Override
	public int[] getStarsFirstByteSensitive() {
		return reverse(starsFirstByteSensitive);
	}
	
	@Override
	public int[] getStarsFourthByteSensitive() {
		return reverse(starsFourthByteSensitive);
	}
	
	
	//DATA!
	//These are the locations of, uh, behavior... commands? That start with 0x24 0x18
	//And have behaviors that match up with these types of objects
	
	private static int[] starsFreestanding = new int[]{
			0x00382c9c,
			0x00395d98,
			0x003e6d04,
			0x003e6d34,
			0x003e6d4c,
			0x003e6d64,
			0x003e6d7c,
			0x003fba80,
			0x003fbc08,
			0x00405cb8,
			0x0040e850,
			0x0040e868,
			0x0040e934,
			0x0041a2c0,
			0x0041a2f4,
			0x004241e0,
			0x00437444,
			0x0043745c,
			0x00437474,
			0x0043748c,
			0x004374a4,
			0x0044a4b8,
			0x0044a4d0,
			0x0044a500,
			0x0044a518,
			0x0048ce98,
			0x0048d008,
			0x0048d020,
			0x00495c78,
			0x00495cc0,
			0x0049df4c,
			0x0049df64,
			0x0049df94,
			0x0049dfac,
			0x004eb3b8,
			0x004eb400,
			0x004eb418,
			0x004eb430,
			0x015000d4,
			0x01500588,
			0x0150107c,
			0x01501350,
			0x018c1cf0,
			0x018c336c,
			0x018c3964,
			0x018c4154,
			0x018c416c,
			0x018c4184,
			0x018c43ac,
			0x018c43c4,
			0x018c4c64,
			0x018c4c7c,
			0x018c4c94,
			0x018c4cac,
			0x018c57b4,
			0x018c57cc,
			0x018c5ca4,
			0x018c5e70,
			0x018c638c,
			0x018c6bf0,
			0x018c6c08,
			0x018c6c20,
			0x01ae8a08,
			0x01ae92f8,
			0x01ae9930,
			0x01ae9e04,
			0x01ae9f40,
			0x01ae9f58,
			0x01aeb068,
			0x01aeb080,
			0x01aeb684,
			0x01cc2360,
			0x01cc27a0,
			0x01cc39bc,
			0x01cc3c54,
			0x01cc48ec,
			0x01cc4c80,
			0x01e00bc0,
			0x01e01728,
			0x01e021b8,
			0x01e02aa0,
			0x01e031cc,
			0x01f38864,
			0x01f38e80,
			0x01f397f8,
			0x01f39b98,
			0x01f39bb0,
			0x01f39cb8,
			0x01f39e80,
			0x01f39eb0,
			0x01f3a304,
			0x01f3ab70,
			0x020c23d8,
			0x020c2720,
			0x020c2dfc,
			0x020c3170,
			0x020c38dc,
			0x020c3ef8,
			0x020c420c,
			0x020c488c,
			0x020c4c00,
			0x020c4c18,
			0x020c4f94,
			0x020c4fac,
			0x020c5580,
			0x0226fb20,
			0x022709c4,
			0x02271ba4,
			0x022723d8,
			0x022723f0,
			0x022727b4,
			0x02272d0c,
			0x022730c0,
			0x023aadd0,
			0x023ab144,
			0x023ab490,
			0x023ac0fc,
			0x023ac218,
			0x023ac3b4,
			0x023ac6e8,
			0x023ac80c,
			0x023acbe8,
			0x023accd8,
			0x025de7d4,
			0x025deb90,
			0x025dee08,
			0x025deeac,
			0x025df918,
			0x025df960,
			0x025e0158,
			0x025e0b90,
			0x025e1330,
			0x026f4484,
			0x026f4994,
			0x026f4a3c,
			0x026f54a4,
			0x026f578c,
			0x026f5804,
			0x026f6154,
			0x02838fa0,
			0x02839120,
			0x02839420,
			0x0283a0c8,
			0x0283a278,
			0x0283a290,
			0x0283a5ec,
			0x0283a994,
			0x0283ab1c,
			0x0283aec4,
			0x0283b22c,
			0x0283b6d0,
			0x0283b6e8,
			0x0283b700,
			0x0283c3d4,
			0x0283c474,
			0x0283c48c,
			0x0283c4a4,
			0x02a387b4,
			0x02a38b7c,
			0x02a39284,
			0x02a392e8,
			0x02a398fc,
			0x02d87830,
			0x02d87e1c,
			0x02d88648,
			0x02d88660,
			0x02d88934,
			0x02d8894c,
			0x02d893bc,
			0x02d893d4,
			0x02d89570,
			0x02d89ae4,
			0x02d8a448,
			0x02f5f5d0,
			0x02f5f600,
			0x02f5fb28,
			0x02f607a4,
			0x02f60c8c,
			0x02f612a8,
			0x02f61470,
			0x02f61b90,
			0x030db868,
			0x030dc2fc,
			0x030dd1d0,
			0x030dd1e8,
			0x030dd200,
			0x030dd218,
			0x030dd4ec,
			0x030dd908,
			0x030dd920,
			0x03354314,
			0x03354540,
			0x03355f74,
			0x03356250,
			0x03524ef8,
			0x035250ac,
			0x03525694,
			0x03525b90,
			0x035267ac,
			0x03527054,
			0x035271a4,
			0x03527be4,
			0x03527ce8,
			0x03527d00,
			0x03708120,
			0x037086e4,
			0x03708ee0,
			0x03708fa4,
			0x03709008,
			0x03a96100,
			0x0403153c,
			0x04379f80,
			0x0437a010,
			0x0437a0b8,
			0x0437b1e4,
			0x0437b274,
			0x0437b31c,
			0x0437b800,
			0x0437b8c0,
			0x0437bbfc,
			0x0437c29c,
			0x0437c7b0,
			0x044666a8,
			0x04466be8,
			0x04466c4c,
			0x04466cc8,
			0x04466e34,
			0x04675178,
			0x04675814,
			0x04675de8,
			0x04676a08,
			0x04676a38,
			0x047bb6b8,
			0x047bbc04,
			0x047bbe98,
			0x047bc344,
			0x048afe8c,
			0x048b04a8,
			0x048b0f00,
			0x049dee58,
			0x049df90c,
			0x049df9e4,
			0x049dff0c,
			0x049dff6c,
			0x049e01b8,
			0x049e02d8,
			0x049e09f0,
			0x049e0a08,
			0x04c04c58,
			0x04c064ec
	};
	
	private static int[] starsRedCoins = new int[]{
			0x00382cb4,
			0x00395d68,
			0x003e6d1c,
			0x003fba98,
			0x00405c88,
			0x0040e880,
			0x0041a2dc,
			0x004241b0,
			0x0042c710,
			0x004374bc,
			0x0044a4e8,
			0x004612e0,
			0x0046c1e4,
			0x0048ce80,
			0x00495c90,
			0x0049df7c,
			0x004bea3c,
			0x004c272c,
			0x004cd9d4,
			0x004eb3e8,
			0x01500314,
			0x01501488,
			0x018c1d98,
			0x018c4d84,
			0x018c558c,
			0x018c60c8,
			0x018c67ac,
			0x01ae88dc,
			0x01ae8f84,
			0x01ae98e8,
			0x01ae9edc,
			0x01cc36a4,
			0x01cc3eac,
			0x01cc5094,
			0x01e009b0,
			0x01e024bc,
			0x01e029e0,
			0x01e033f8,
			0x01f38bdc,
			0x01f3a0c4,
			0x01f3ab40,
			0x020c23f0,
			0x020c5a48,
			0x0226fb08,
			0x02270a9c,
			0x02271b74,
			0x023abc64,
			0x025de068,
			0x025deaa0,
			0x025dfe9c,
			0x025e0ae8,
			0x025e1258,
			0x026f4844,
			0x02839390,
			0x02839fa8,
			0x0283b748,
			0x0283c5f4,
			0x02a38ffc,
			0x02a397e0,
			0x02d87f84,
			0x02d893ec,
			0x02d898b8,
			0x02d89e2c,
			0x02d8a640,
			0x02f5f5b8,
			0x02f60aa8,
			0x02f60ecc,
			0x030dc4dc,
			0x030dcc24,
			0x030dd0f4,
			0x03355e24,
			0x03525cc8,
			0x035278b0,
			0x037084ac,
			0x037092c4,
			0x0422c6fc,
			0x0437a9d0,
			0x0437becc,
			0x0437c618,
			0x04676974,
			0x047bb778,
			0x048aff64
	};

	private static int[] starsRedCoinsBowser = new int[]{
			0x0045c0c8,
			0x0046aa68,
			0x00477edc,
			0x01cc2378,
			0x01cc46a8,
			0x01e01740,
			0x025dfa68,
			0x025e0f24,
			0x03354168,
			0x03708b9c,
			0x046762a4,
			0x047bbbd4,
			0x048af9a8,
			0x048b0b9c,
			0x04ca471c,
			0x04ca4b94
	};
	
	private static int[] starsBox = new int[]{
			0x004241c8,
			0x014ffe4c,
			0x018c372c,
			0x018c3d0c,
			0x018c5c14,
			0x018c5f18,
			0x01ae836c,
			0x01ae9224,
			0x01ae99f0,
			0x01cc2484,
			0x01cc3dbc,
			0x01cc4c68,
			0x01e01574,
			0x01f3a3dc,
			0x020c2ad0,
			0x02271a9c,
			0x023abafc,
			0x025de60c,
			0x026f3e48,
			0x026f4ad0,
			0x026f5bf8,
			0x02d86ad0,
			0x02d87590,
			0x030dc0bc,
			0x030dcf70,
			0x03353e80,
			0x03708108,
			0x03708d58,
			0x03708e1c,
			0x03709294,
			0x037092ac,
			0x03709858,
			0x04030d38,
			0x04033bdc,
			0x0437a9e8,
			0x0437ad84,
			0x0437c9fc,
			0x04676ba4,
			0x047bb6a0,
			0x048af9c0
	};
	
	private static int[] starsHidden = new int[]{
			0x3fbc20,
			0x405ca0,
			0x41a2a8,
			0x42c6f4,
			0x14ff198,
			0x150032c,
			0x1500f8c,
			0x18c3034,
			0x1aead50,
			0x1cc392c,
			0x1f39348,
			0x1f3a9c0,
			0x23acb40,
			0x25e0a10,
			0x26f4d28,
			0x2a38724,
			0x2d877a0,
			0x2f6054c,
			0x2f61428,
			0x2f6157c,
			0x30db778,
			0x30dd938,
			0x3355e3c,
			0x35260e8,
			0x352617c,
			0x3526fc4,
			0x35273e8,
			0x37081b0,
			0x37085e4,
			0x422cfac,
			0x437c630,
			0x4466420,
			0x47bc590,
			0x49e0c60
	};

	private static int[] starsBowser = new int[]{

			0x04b0bdb8,
			0x04b0bffc,
			0x04b0cde4,
			0x04c04cb8,
			0x04c04d8c,
			0x04c0667c
	};

	private static int[] starsFirstByteSensitive = new int[]{
			
			//Silver Star Counter (bouncing?)
			//0x1F0014D0
			0x01aeb7a4,
			0x01cc4fbc,
			0x01f38cb4,
			0x01f391b0,
			0x02272f0c,
			0x023ab790,
			0x02d89494,
			0x03526794,
			0x04675580,
			0x046758d4,
			0x048b0610,
			
			//Three Bullies Custom Counter
			//0x1F001898
			0x01aeabe8, //is this blue goomba dry town? is this the crystal counter that gets overridden by the other one?
			0x01cc3a64,
			0x01e023cc,
			0x020c30cc,
			0x020c44f4,
			0x02f5f990,
			0x035270b4,	//nebula basement lethal water land, three chuckyas
			
			//Big I, Big Eye
			//With second byte & 0x01
			//0x13000054
			0x00382ccc,
			0x018c3624,
			0x018c3d6c,
			
			//Big Bully Standalone
			//0x13003660
			0x48ccfc,
			0x20c2630,
			0x20c4c30,
			
			//Penguin's Mother
			//TODO some of these are surely unused
			0x00395d50,
			0x023ac230, //Confirmed in use, starter area left
			0x023ac3cc, //confirmed
			0x023ac824, //TODO gets overridden by 23ac8b4, was this the purpose of that actor or is there another star here?
			0x026f3ef0, //confirmed
			0x026f5aa8, //TODO gets overridden by 26f5b68, which was probably meant for the ice bully
			
			//Boo Buddy
			//0x13002768
			//OK so they ARE the right things, but it's not THEIR flags that gets used
			//Bafflingly
			//There is some other controller type object
			//It is a DIRECT copy over
			//FE3B01FDE5C5
			//2D889F0 is a controller (?)
			//1F 00 14 7C
			//Who is this?
			//0x00382b78,
			//0x02d876c8,
			//0x02d88570,
			//0x02d88a3c,
			//0x02d8a538,
			//
			//So actually it's just a container that Boo Buddy searches for
			//0x1F00147C
			//Anything that spawns abs location uses it
			//There's quite a few
			//It's probably not sensitive, freestanding would work fine
			//But I'll do firstbytesensitive anyway because things can break unexpectedly
			//Surprisingly, piranha plants are applied as well
			//I believe it's because this thing watches for stars of a specific kind that spawn in
			
			//SHOCKINGLY there isn't one placed for the piranha plant igloo
			
			0x018c4b74,
			0x01aeabd0, //note that this one applies itself to two, five piranha plants or three crystals (blue goomba dry town)
			0x01e01238, //confirmed, piranha plants
			0x01e026fc,
			0x020c4e70,
			0x023ac8b4, //note this one applies itself to two, penguin mother and ??? //TODO what is the conflict here? was it placed for the mother specifically?
			0x023ad1a4, //confirmed, ice boss
			0x026f5b68, //note this one applies itself to two, penguin mother and ice boss
			0x02d885e8, //confirmed, big boo after little boos, may apply to two? didn't see a big boo in this level
			0x02d889f4, //confirmed, piranha plants
			0x02d8a628, //note this one applies itself to two, five boos and big boo up top, quiet courtyard boo mansion
			0x030dc8c4, //confirmed, wiggler
			0x03527c14, //confirmed, treasure chests
			0x04676334,
			0x04676b28, //confirmed, prince bobomb
			0x047bb958, //confirmed, piranha plants
			0x047bc5c0, //confirmed, wiggler
			
			//Ice Bully
			0x40e900,
			0x26f5620

			
	};
	
	private static int[] starsFourthByteSensitive = new int[]{
			
			//JUST AVOID THESE, DON'T SET THEM
			//14 2
			//0x82
			//Timer that kills you
			//0x1F001930
			//0x3a98bac,
			//0x437af60, //the one randomly found in the entrance, works as it should, unless you come in through another entrance
			//then it unloads mario
			//wow
			//0x49e0264
			
			//Toad with a star
			//byte flag two must have 0x08 set
			//The matching default star IDs here are concerning to say the least
			//I think these are supposed to be like, special stars
			//Red or something, but they conveniently miss those IDs by a lot so who knows
			//
			//
			0x01500bf4, //080800ea	//confirmed (nighttime)
			0x04030b28, //080800c9	//confirmed
			0x040324e0, //080800c8	//confirmed
			0x04032dc4, //0808000a	//confirmed
			0x04033778, //080800c8	
			0x04033e58, //080800c8	//confirmed
			0x04034684, //080800c8	
			0x04b0c83c //08080000	
	};

	
	//Weird level controllers
	//0x1F00706C
	
	private static int[] levelControllers = new int[]{
			0x14feda0,
			0x14ff0c4,
			0x14ff4c8,
			0x14ff71c,
			0x14ffbe8,
			0x15003a4,
			0x15005e8,
			0x1500bac,
			0x1500e48,
			0x15010ac,
			0x15014a0,
			0x15016b4,
			0x1501e38,
			0x18c2038,
			0x18c28a4,
			0x18c2e54,
			0x18c2f78,
			0x18c32bc,
			0x18c38c4,
			0x18c3d84,
			0x18c41cc,
			0x18c5784,
			0x18c58f8,
			0x18c5ddc,
			0x18c6260,
			0x18c68fc,
			0x18c7148,
			0x1ae7f20,
			0x1ae86fc,
			0x1ae8af8,
			0x1ae8cf4,
			0x1ae923c,
			0x1ae95b0,
			0x1ae9c60,
			0x1ae9dd4,
			0x1aea1b0,
			0x1aea334,
			0x1aead68,
			0x1aeae5c,
			0x1aeb84c,
			0x1cc2390,
			0x1cc24cc,
			0x1cc2ba8,
			0x1cc2d5c,
			0x1cc3450,
			0x1cc38fc,
			0x1cc3b60,
			0x1cc3dd4,
			0x1cc3ef8,
			0x1cc491c,
			0x1cc4998,
			0x1cc4c50,
			0x1e01160,
			0x1e0158c,
			0x1e01cf8,
			0x1e01fa4,
			0x1e02230,
			0x1e0257c,
			0x1e03010,
			0x1e031e4,
			0x1e03230,
			0x1f38788,
			0x1f38ccc,
			0x1f39120,
			0x1f39484,
			0x1f394f4,
			0x1f39948,
			0x1f39ee0,
			0x1f3a754,
			0x1f3ad38,
			0x20c2990,
			0x20c2c20,
			0x20c30b4,
			0x20c3458,
			0x20c36a0,
			0x20c3ad4,
			0x20c3f10,
			0x20c44c4,
			0x20c4718,
			0x20c4b74,
			0x20c4ee8,
			0x20c542c,
			0x20c57d8,
			0x22700f0,
			0x2270c34,
			0x2271348,
			0x22717f8,
			0x2271bbc,
			0x2271d78,
			0x2271f80,
			0x22720bc,
			0x22723c0,
			0x22726f4,
			0x2272cf4,
			0x2272f24,
			0x22730d8,
			0x23aaf80,
			0x23ab12c,
			0x23ab220,
			0x23ab444,
			0x23ab820,
			0x23ac0cc,
			0x23ac308,
			0x23ac4a4,
			0x23ac700,
			0x23ac89c,
			0x23accf0,
			0x23ad1bc,
			0x25de098,
			0x25de954,
			0x25deb78,
			0x25dece4,
			0x25dee20,
			0x25def0c,
			0x25df948,
			0x25e001c,
			0x25e0440,
			0x25e0c9c,
			0x25e1390,
			0x26f4070,
			0x26f49ac,
			0x26f4de8,
			0x26f5354,
			0x26f55c0,
			0x26f5834,
			0x26f58b0,
			0x26f5c74,
			0x2839438,
			0x2839da4,
			0x2839fd8,
			0x283a174,
			0x283a214,
			0x283a4a0,
			0x283a97c,
			0x283aeac,
			0x283b25c,
			0x283b7a8,
			0x283b854,
			0x283bdec,
			0x283c45c,
			0x2a38470,
			0x2a387fc,
			0x2a38b00,
			0x2a3908c,
			0x2a397f8,
			0x2a398a4,
			0x2a3992c,
			0x2a39a50,
			0x2a39bc4,
			0x2a39d60,
			0x2a3a570,
			0x2a3a6b4,
			0x2d86920,
			0x2d87148,
			0x2d87848,
			0x2d87eac,
			0x2d88618,
			0x2d8891c,
			0x2d89088,
			0x2d894f4,
			0x2d89558,
			0x2d89acc,
			0x2d8a5e0,
			0x2d8afa4,
			0x2d8b468,
			0x2f5f870,
			0x2f6001c,
			0x2f600f0,
			0x2f60224,
			0x2f602e0,
			0x2f603e0,
			0x2f607bc,
			0x2f60898,
			0x2f60c44,
			0x2f614b8,
			0x2f61864,
			0x2f61c38,
			0x30dbbf8,
			0x30dc53c,
			0x30dc6d8,
			0x30dc894,
			0x30dcfd0,
			0x30dd10c,
			0x30dd338,
			0x30dd684,
			0x30dddb8,
			0x30de65c,
			0x33541e0,
			0x33544ac,
			0x3354750,
			0x335494c,
			0x3354e28,
			0x335546c,
			0x3355fec,
			0x3356080,
			0x33560e4,
			0x3524f10,
			0x352513c,
			0x3525284,
			0x35256ac,
			0x3525904,
			0x3525ef0,
			0x3526374,
			0x35265b8,
			0x35267c4,
			0x3526bd0,
			0x352733c,
			0x35274f0,
			0x3527c2c,
			0x3528438,
			0x3708228,
			0x3708614,
			0x3708714,
			0x3708928,
			0x3708ca4,
			0x3708da0,
			0x3708e94,
			0x3708f28,
			0x3708f8c,
			0x3709020,
			0x370969c,
			0x3709a68,
			0x3709b6c,
			0x3a96118,
			0x3a96b40,
			0x3a96f54,
			0x3a97278,
			0x3a977ac,
			0x3a97bf0,
			0x3a97ee4,
			0x3a98248,
			0x3a98484,
			0x3a985d8,
			0x3a987c4,
			0x3a98a60,
			0x3a98b94,
			0x3a98c40,
			0x40307b8,
			0x403091c,
			0x4030b40,
			0x4030e9c,
			0x4031030,
			0x40316bc,
			0x4031bfc,
			0x4032270,
			0x40328e4,
			0x4032b38,
			0x4032cbc,
			0x4033700,
			0x4033a14,
			0x4033e70,
			0x403460c,
			0x422c148,
			0x422c624,
			0x422cb20,
			0x422cd74,
			0x422cfc4,
			0x422d3f0,
			0x422d694,
			0x422d8e0,
			0x422daf4,
			0x422dd38,
			0x422e004,
			0x422e140,
			0x422e29c,
			0x437a2e0,
			0x437a6a4,
			0x437aa00,
			0x437aed4,
			0x437af78,
			0x437afdc,
			0x437b55c,
			0x437b980,
			0x437bd1c,
			0x437c088,
			0x437c284,
			0x437c2e8,
			0x44666d8,
			0x4466814,
			0x4466998,
			0x4466abc,
			0x46755c8,
			0x46758ec,
			0x4675f98,
			0x46762bc,
			0x4676658,
			0x467698c,
			0x4676b10,
			0x47bb460,
			0x47bb838,
			0x47bbc1c,
			0x47bbd68,
			0x47bbeb0,
			0x47bbf14,
			0x47bc008,
			0x47bc3bc,
			0x47bc6b0,
			0x47bc88c,
			0x47bc93c,
			0x47bca78,
			0x48afa80,
			0x48afc8c,
			0x48afd50,
			0x48b03e4,
			0x48b06b8,
			0x48b0c2c,
			0x48b0f30,
			0x49df548,
			0x49df9cc,
			0x49e01e8,
			0x49e027c,
			0x49e02f0,
			0x49e041c,
			0x49e0c90,
			0x4b0bdd0,
			0x4b0c014,
			0x4b0c240,
			0x4b0c344,
			0x4b0c490,
			0x4b0c584,
			0x4b0c750,
			0x4b0c854,
			0x4b0ca20,
			0x4b0caf8,
			0x4b0cd6c,
			0x4c04d30,
			0x4c05104,
			0x4c052a0,
			0x4c05424,
			0x4c054f8,
			0x4c059dc,
			0x4c060a0,
			0x4c06324,
			0x4c06420,
			0x4c0651c,
			0x4c06600,
			0x4c06694,
			0x4ca43e8,
			0x4ca47ac,
			0x4ca4830,
			0x4ca49fc

	};
	
	@Override
	public int[] getLevelControllers()
	{
		return levelControllers;
	}
}
