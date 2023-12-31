package plexus.behavior;

public class FourthByteSensitive extends SensitiveThird
{
	//The data is contained in the fourth byte instead of the first byte
	//These are really touchy and sensitive, that's why they're fourth byte I bet
	
	public FourthByteSensitive(int addr)
	{
		super(addr);
	}
	
	@Override
	void createBehaviorFlags()
	{
		//Only set D
		this.behaviorFlagD = this.starId & 0xFF;
	}
	
	@Override
	public void evalStarId()
	{
		this.starId = ((this.behaviorFlagC & 0x01) * 0x100) + this.behaviorFlagD;
	}

}
