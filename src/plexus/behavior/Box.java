package plexus.behavior;

public class Box extends SensitiveThird
{
	//TODO this can almost assuredly extend Freestanding
	
	public Box(int addr)
	{
		super(addr);
	}

	@Override
	public void evalStarId()
	{
		switch(this.behaviorFlagB)
		{
			case 0x08:
			case 0x0A:
			case 0x0B:
			case 0x0C:
			case 0x0D:
			case 0x0E:
				break;
			default:
				this.crash();
				break;
		}
		
		//The freestanding check is accurate
		super.evalStarId();
	}
	
	@Override
	void createBehaviorFlags()
	{
		//Do freestanding, but also set behaviorFlagB to 0x08
		super.createBehaviorFlags();
		this.behaviorFlagB = 0x08;
	}
}
