package plexus.behavior;

public class Bowser extends PlacedObject
{
	public Bowser(int addr)
	{
		super(addr);
	}

	@Override
	public void evalStarId()
	{
		int maskedC = this.behaviorFlagC & 0x01;
		maskedC *= 0x100;
		this.starId = maskedC + this.behaviorFlagD;
	}

	@Override
	void createBehaviorFlags()
	{
		this.behaviorFlagC = this.behaviorFlagC & 0xFE; //Get rid of the last bit
		this.behaviorFlagC = this.behaviorFlagC | ((this.starId >= 0x100) ? 0x01 : 0x00);
		
		this.behaviorFlagD = this.starId & 0XFF;
	}
}
