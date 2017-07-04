package getalp.wsd.utils;

public class PercentProgressDisplayer 
{
	private double max;
	
	public int percent;
	
	public PercentProgressDisplayer(double max)
	{
		this.max = max;
		this.percent = 0;
	}
	
	public boolean refresh(double current)
	{
		int currentPercent = (int) (((double) current / (double) max) * 100.0);
		if (currentPercent != percent)
		{
			this.percent = currentPercent;
			return true;
		}
		return false;
	}
}
