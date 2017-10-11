//base class for all hitable objects

public class Hitable{
	boolean hit(Ray r, double t_min, double t_max, HitRecord rec){return false;}
}