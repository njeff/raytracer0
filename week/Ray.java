public class Ray{
	Vec3 A;
	Vec3 B;
	double _time;
	public Ray(){}
	public Ray(Vec3 a, Vec3 b){ A = a; B = b; _time = 0;}
	public Ray(Vec3 a, Vec3 b, double ti){ A = a; B = b; _time = ti;}
	public Vec3 origin() {return A;}
	public Vec3 direction() {return B;}
	public double time() {return _time;}
	public Vec3 point_at_parameter(double t) {return A.add(B.mul(t));}
	public void set(Ray r){
		A = r.A;
		B = r.B;
		_time = r._time;
	}
}