//a sphere that moves between two points at a constant velocity
public class MovingSphere extends Hittable{
	Vec3 center0, center1;
	double time0, time1;
	double radius;
	Material mat;

	public MovingSphere() {}

	/**
	* @param cen0 center of sphere at t0
	* @param cen1 center of sphere at 71
	* @param t0 starting time
	* @param t1 ending time
	* @param r radius
	* @param m material
	*/
	public MovingSphere(Vec3 cen0, Vec3 cen1, double t0, double t1, double r, Material m){
		center0 = cen0;
		center1 = cen1;
		time0 = t0;
		time1 = t1;
		radius = r;
		mat = m;
	}

	public boolean hit(Ray r, double t_min, double t_max, HitRecord rec){
		Vec3 oc = r.origin().sub(center(r.time()));
		double a = Vec3.dot(r.direction(),r.direction());
		double b = Vec3.dot(oc, r.direction());
		double c = Vec3.dot(oc,oc) - radius*radius;
		double discriminant = b*b - a*c;
		if(discriminant > 0){
			rec.mat = mat;
			rec.h = this;
			double temp = (-b - Math.sqrt(discriminant))/a;
			if(temp < t_max && temp > t_min){
				rec.t = temp;
				rec.p = r.point_at_parameter(rec.t);
				rec.normal = rec.p.sub(center(r.time())).div(radius);
				return true;
			}
			temp = (-b + Math.sqrt(discriminant))/a;
			if(temp < t_max && temp > t_min){
				rec.t = temp;
				rec.p = r.point_at_parameter(rec.t);
				rec.normal = rec.p.sub(center(r.time())).div(radius);
				return true;
			}
		}
		return false;
	}

	Vec3 center(double time){
		return center0.add(center1.sub(center0).mul((time-time0)/(time1-time0)));
	}
}