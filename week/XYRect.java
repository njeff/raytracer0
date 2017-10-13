//all axis aligned planes
//XY rectangle
public class XYRect extends Hitable{
	double x0, x1, y0, y1, k;
	Material mp;

	public XYRect() {}

	public XYRect(double _x0, double _x1, double _y0, double _y1, double _k, Material mat){
		x0 = _x0;
		x1 = _x1;
		y0 = _y0;
		y1 = _y1;
		k = _k;
		mp = mat;
	}

	public boolean hit(Ray r, double t_min, double t_max, HitRecord rec){
		double t = (k-r.origin().z())/r.direction().z();
		if(t < t_min || t > t_max){ //cannot hit if before or past rect
			return false;
		}
		double x = r.origin().x() + t*r.direction().x();
		double y = r.origin().y() + t*r.direction().y();
		if(x < x0 || x > x1 || y < y0 || y > y1){ //cannot hit if around rect
			return false;
		}
		rec.u = (x-x0)/(x1-x0);
		rec.v = (y-y0)/(y1-y0);
		rec.t = t;
		rec.mat = mp;
		rec.p = r.point_at_parameter(t);
		rec.normal = new Vec3(0,0,1);
		return true;
	}

	public boolean bounding_box(double t0, double t1, AABB box){
		box.set(new AABB(new Vec3(x0, y0, k-0.0001), new Vec3(x1, y1, k-0.0001))); //very thin
		return true;
	}
}

//XZ Rectangle
class XZRect extends Hitable{
	double x0, x1, z0, z1, k;
	Material mp;

	public XZRect() {}

	public XZRect(double _x0, double _x1, double _z0, double _z1, double _k, Material mat){
		x0 = _x0;
		x1 = _x1;
		z0 = _z0;
		z1 = _z1;
		k = _k;
		mp = mat;
	}

	public boolean hit(Ray r, double t_min, double t_max, HitRecord rec){
		double t = (k-r.origin().y())/r.direction().y();
		if(t < t_min || t > t_max){ //cannot hit if before or past rect
			return false;
		}
		double x = r.origin().x() + t*r.direction().x();
		double z = r.origin().z() + t*r.direction().z();
		if(x < x0 || x > x1 || z < z0 || z > z1){ //cannot hit if around rect
			return false;
		}
		rec.u = (x-x0)/(x1-x0);
		rec.v = (z-z0)/(z1-z0);
		rec.t = t;
		rec.mat = mp;
		rec.p = r.point_at_parameter(t);
		rec.normal = new Vec3(0,1,0);
		return true;
	}

	public boolean bounding_box(double t0, double t1, AABB box){
		box.set(new AABB(new Vec3(x0, k-0.0001, z0), new Vec3(x1, k-0.0001, z1))); //very thin
		return true;
	}
}

//YZ rectangle
class YZRect extends Hitable{
	double y0, y1, z0, z1, k;
	Material mp;

	public YZRect() {}

	public YZRect(double _y0, double _y1, double _z0, double _z1, double _k, Material mat){
		y0 = _y0;
		y1 = _y1;
		z0 = _z0;
		z1 = _z1;
		k = _k;
		mp = mat;
	}

	public boolean hit(Ray r, double t_min, double t_max, HitRecord rec){
		double t = (k-r.origin().x())/r.direction().x();
		if(t < t_min || t > t_max){ //cannot hit if before or past rect
			return false;
		}
		double y = r.origin().y() + t*r.direction().y();
		double z = r.origin().z() + t*r.direction().z();
		if(y < y0 || y > y1 || z < z0 || z > z1){ //cannot hit if around rect
			return false;
		}
		rec.u = (y-y0)/(y1-y0);
		rec.v = (z-z0)/(z1-z0);
		rec.t = t;
		rec.mat = mp;
		rec.p = r.point_at_parameter(t);
		rec.normal = new Vec3(1,0,0);
		return true;
	}

	public boolean bounding_box(double t0, double t1, AABB box){
		box.set(new AABB(new Vec3(k-0.0001, y0, z0), new Vec3(k-0.0001, y1, z1))); //very thin
		return true;
	}
}