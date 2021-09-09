
import java.lang.Math;
import java.util.ArrayList;

public class DollarMethods 
{
	public static ArrayList<Point> Resample(ArrayList<Point> points,double n)
	{
		double I = Pathlength(points) / (n - 1);
		double D = 0;
		ArrayList<Point>newPoints = new ArrayList<Point>();
		newPoints.add(points.get(0));
		for(int i = 1; i < points.size(); i++)
		{
			Point p1 = points.get(i - 1);
			Point p2 = points.get(i);
			double d = Distance(points.get(i - 1),points.get(i));
			if(D + d >= I)
			{
				double qx = p1.getX() + ((I-D)/d) * (p2.getX() - p1.getX());
				double qy = p1.getY() + ((I-D)/d) * (p2.getY() - p1.getY());
				Point q = new Point(qx, qy);
				newPoints.add(q);
				points.add(i, q);
				D = 0;
			}
			else
				D += d;
		}
		if(newPoints.size() == n-1)
		{
			newPoints.add(points.get(points.size() - 1));
		}
		return newPoints;		
	}
	
	public static double Pathlength(ArrayList<Point> points)
	{
		double distance = 0;
		for(int i = 1; i < points.size(); i++)
		{
			distance += Distance(points.get(i - 1),points.get(i));
		}
		return distance;
	}
	
	public static double Distance(Point a, Point b)
	{
		double x = b.getX() - a.getX();
		double y = b.getY() - a.getY();
		return Math.sqrt(x * x + y * y);
	}
	
	public static Point Centroid(ArrayList<Point> points)
	{
		double xsum = 0;
		double ysum = 0;
		for(Point p : points)
		{
			xsum += p.getX();
			ysum += p.getY();
		}
		return new Point(xsum/points.size(),ysum/points.size());
	}
	
	public static double IndicativeAngle(ArrayList<Point> points)
	{
		Point c = Centroid(points);
		return Math.atan2(c.getY()-points.get(0).getY(),c.getX()-points.get(0).getX());
	}
	
	public static ArrayList<Point> RotateBy(ArrayList<Point> points, double angle)
	{
		ArrayList<Point> newPoints = new ArrayList<Point>();
		Point c = Centroid(points);
		for(Point p : points)
		{
			double qx = (p.getX() - c.getX()) * Math.cos(angle) - (p.getY() - c.getY()) * Math.sin(angle) + c.getX();
			double qy = (p.getX() - c.getX()) * Math.sin(angle) + (p.getY() - c.getY()) * Math.cos(angle) + c.getY();
			Point q  = new Point(qx, qy);
			newPoints.add(q);
		}
		return newPoints ;
	}
	
	public static double[] BBox(ArrayList<Point> points)
	{
		double minX = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;
		for (Point p:points)
		{
			if(p.getX() < minX)
				minX = p.getX();
			if(p.getX() > maxX)
				maxX = p.getX();
			if(p.getY() < minY)
				minY = p.getY();
			if(p.getY() > maxY)
				maxY = p.getY();
		}
		double bbox[] = {maxX, minX, maxY, minY};
		return bbox;
	}
	
	public static ArrayList<Point> ScaleTo(ArrayList<Point> points, double s)
	{
		double B[] = BBox(points);
		ArrayList<Point> newPoints = new ArrayList<Point>();
		for(Point p : points)
		{
			double qx = p.getX() * (s / (B[0] - B[1]));
			double qy = p.getY() * (s/ (B[2] - B[3]));
			Point q  = new Point(qx, qy);
			newPoints.add(q);
		}
		return newPoints;
	}
	
	public static ArrayList<Point> TranslateTo(ArrayList<Point> points, Point k)
	{
		ArrayList<Point> newPoints = new ArrayList<Point>();
		Point c = Centroid(points);
		for(Point p : points)
		{
			double qx = p.getX() + k.getX() - c.getX();
			double qy = p.getY() + k.getY() - c.getY();
			Point q  = new Point(qx,qy);
			newPoints.add(q);
		}
		return newPoints ;
	}
	
	public static double PathDistance(ArrayList<Point> A, ArrayList<Point> B)
	{
		double distance = 0;
		for(int i = 1; i < A.size(); i++)
		{ 
			distance += Distance(A.get(i), B.get(i));
		}
		return distance / A.size();
	}
	
	public static double DistanceAtAngle(ArrayList<Point> points,ArrayList<Point> T, double theta)
	{
		double distance = 0;
		ArrayList<Point> newPoints = RotateBy(points, theta);
		distance = PathDistance(newPoints, T);
		return distance;
	}
	
	public static double DistanceAtBestAngle(ArrayList<Point> points,ArrayList<Point> T, double thetaa, double thetab, double thetat)
	{
		double phi = (0.5) * (-1 + Math.sqrt(5));
		double x1 = phi * thetaa + (1 - phi) * thetab;
		double f1 = DistanceAtAngle(points, T, x1);
		double x2 = (1 - phi) * thetaa + phi * thetab;
		double f2 = DistanceAtAngle(points, T, x2);
		while(Math.abs(thetab - thetaa) > thetat)
		{
			if(f1 < f2)
			{
				thetab = x2;
				x2 = x1;
				f2 = f1;
				x1 = phi * thetaa + (1 - phi) * thetab;
				f1 = DistanceAtAngle(points,T,x1);
			}
			else
			{
				thetaa = x1;
				x1 = x2;
				f1 = f2;
				x2 = (1-phi) * thetaa + phi * thetab;
				f2 = DistanceAtAngle(points, T, x2);
			}
		}
		return Math.min(f1, f2);
	}
	
	public static Result Recognize(ArrayList<Point> points,ArrayList<Template> templates)
	{
		double b = Double.MAX_VALUE;
		Template tp = templates.get(0);
		for (Template t : templates)
		{
			double d = DistanceAtBestAngle(points,t.getPoints(), -Math.PI/4, Math.PI/4, Math.PI/90);
			if(d < b)
			{
				b = d;
				tp = t;
			}
		}
		double score = 1 - b / (0.5 * Math.sqrt(250 * 250 + 250 * 250));
		return new Result(tp, score);
	}
}
