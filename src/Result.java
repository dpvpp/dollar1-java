public class Result 
{
	private Template template;
	private double score;
	
	public Result(Template T, double s)
	{
		template = T;
		score = s;
	}
	
	public Template getTemplate()
	{
		return template;
	}
	
	public double getScore() 
	{
		return score;
	}
}
