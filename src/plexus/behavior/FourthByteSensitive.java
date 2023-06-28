package plexus.behavior;

public class FourthByteSensitive extends SensitiveThird
{
	//TODO verify...
	//TODO one of them is bugged
	
	//The data is contained in the fourth byte instead of the first byte
	
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
