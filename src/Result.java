public class Result 
{
	Template template;
	double score;
	public Result(Template T, double s)
	{
		template = T;
		score = s;
	}
	public Template getTemplate()
	{
		return template;
	}
}
