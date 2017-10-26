/**
* Sphere Hittable
*/
public class Sphere extends Hittable{
	Vec3 center;
	double radius;
	Material mat;
	
	public Sphere() {}
	
	/**
	* @param cen center of the sphere
	* @param r radius
	* @param m material
	*/
	public Sphere(Vec3 cen, double r, Material m){
		center = cen;
		radius = r;
		mat = m;
	}

	public boolean hit(Ray r, double t_min, double t_max, HitRecord rec){
		Vec3 oc = r.origin().sub(center);
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
				rec.normal = rec.p.sub(center).div(radius);
				get_sphere_uv(rec.normal,rec);
				return true;
			}
			temp = (-b + Math.sqrt(discriminant))/a;
			if(temp < t_max && temp > t_min){
				rec.t = temp;
				rec.p = r.point_at_parameter(rec.t);
				rec.normal = rec.p.sub(center).div(radius);
				get_sphere_uv(rec.normal,rec);
				return true;
			}
		}
		return false;
	}

	public boolean bounding_box(double t0, double t1, AABB box){
		box.set(new AABB(center.sub(new Vec3(radius, radius, radius)),center.add(new Vec3(radius, radius, radius))));
		return true;
	}

	/**
	* Gets UV coordinates of a point p on the sphere
	* @param p point on sphere
	* @param rec the hit record to update with uv coords
	*/
	private void get_sphere_uv(Vec3 p, HitRecord rec){
		double phi = Math.atan2(p.z(), p.x());
		double theta = Math.asin(p.y());
		rec.u = 1-(phi + Math.PI)/(2*Math.PI);
		rec.v = (theta + Math.PI/2)/Math.PI;
	}
}