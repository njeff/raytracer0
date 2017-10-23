/**
* Axis aligned bounding box
*/
public class AABB{
	Vec3 _min, _max;

	public AABB() {}

	public AABB(Vec3 a, Vec3 b){
		_min = a;
		_max = b;
	}

	Vec3 min(){return _min;}
	Vec3 max(){return _max;}

	boolean hit(Ray r, double tmin, double tmax){
		//solve along each component
		for(int a = 0; a < 3; a++){
			//check both extremes
			double t0 = Math.min((_min.get(a) - r.origin().get(a))/r.direction().get(a),
				(_max.get(a)-r.origin().get(a))/r.direction().get(a));
			double t1 = Math.max((_min.get(a) - r.origin().get(a))/r.direction().get(a),
				(_max.get(a)-r.origin().get(a))/r.direction().get(a));
			//look for minimum t for intersection
			tmin = Math.max(t0, tmin);
			//look for maxmimum t for intersection
			tmax = Math.min(t1, tmax);
			//if any component has a minimum t required for intersection 
			//that surpasses the max for the intersection of another
			//return no intersection
			if(tmax < tmin){
				return false;
			}
		}
		return true;
	}

	public void set(AABB ab){
		_min = ab._min;
		_max = ab._max;
	}

	public String toString(){
		return _min.toString() + " | " + _max.toString();
	}
}