import java.util.ArrayList;

public class Template 
{
	private String name;
	private ArrayList<Point> points;
	
	public Template(String n, ArrayList<Point> p)
	{
		name = n;
		points = p;
	}
	
	public String getName()
	{
		return name;
	}
	
	public ArrayList<Point> getPoints()
	{
		return points;
	}
	
}
