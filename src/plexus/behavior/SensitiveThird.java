package plexus.behavior;

public class SensitiveThird extends Freestanding
{
	//Sensitive Third's star data is in byte A
	

	//Instead of masking behaviorflagC, takes whatever's there and chooses an ID that's most convenient
	
	public SensitiveThird(int addr)
	{
		super(addr);
	}
	
	
	@Override
	void createBehaviorFlags()
	{
		//Only set A
		this.behaviorFlagA = this.starId & 0xFF;
	}
	
	@Override
	public boolean acceptsStarId(int starId)
	{
		return ((starId >> 8) & 0x01) == (this.behaviorFlagC & 0x01);
	}
}
