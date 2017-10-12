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
		for(int a = 0; a < 3; a++){
			double t0 = Math.min((_min.get(a) - r.origin().get(a))/r.direction().get(a),
				(_max.get(a)-r.origin().get(a))/r.direction().get(a));
			double t1 = Math.max((_min.get(a) - r.origin().get(a))/r.direction().get(a),
				(_max.get(a)-r.origin().get(a))/r.direction().get(a));
			tmin = Math.max(t0, tmin);
			tmax = Math.min(t1, tmax);
			if(tmax <= tmin){
				return false;
			}
		}
		return true;
	}

	public void set(AABB ab){
		_min = ab._min;
		_max = ab._max;
	}
}