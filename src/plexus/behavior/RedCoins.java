package plexus.behavior;

public class RedCoins extends SensitiveThird
{
	//TODO this can almost assuredly extend Freestanding
	
	//Behavior 0x13003E8C
	
	//Calls 802F24F4 (level controller)
	//But doesn't appear to need it after testing
	
	public RedCoins(int addr) {
		super(addr);
	}
}
