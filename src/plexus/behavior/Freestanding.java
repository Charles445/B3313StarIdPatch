package plexus.behavior;

public class Freestanding extends PlacedObject
{
	public Freestanding(int addr)
	{
		super(addr);
	}

	@Override
	public void evalStarId()
	{
		this.starId = ((this.behaviorFlagC & 0x01) * 0x100) + this.behaviorFlagA;
	}

	@Override
	void createBehaviorFlags()
	{
		//Set A and C appropriately
		this.behaviorFlagC = this.behaviorFlagC & 0xFE; //Get rid of the last bit
		this.behaviorFlagC = this.behaviorFlagC | ((this.starId >= 0x100) ? 0x01 : 0x00);
		
		this.behaviorFlagA = this.starId & 0xFF;
	}
}
