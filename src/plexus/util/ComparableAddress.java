package plexus.util;

import java.util.Comparator;

public interface ComparableAddress extends Comparator<ComparableAddress>
{	
	public static Instance instance = new Instance();
	
	public int getComparableAddress();
	
	default public int compare(ComparableAddress arg0, ComparableAddress arg1)
	{
		if(arg0.getComparableAddress() < arg1.getComparableAddress())
		{
			return -1;
		}
		if(arg0.getComparableAddress() > arg1.getComparableAddress())
		{
			return 1;
		}
		return 0;
	}
	
	public static class Instance implements ComparableAddress
	{
		@Override
		public int getComparableAddress() {
			return -1;
		}
	}
}
