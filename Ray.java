public class Ray{
	Vec3 A;
	Vec3 B;
	public Ray(){}
	public Ray(Vec3 a, Vec3 b){ A = a; B = b;}
	public Vec3 origin() {return A;}
	public Vec3 direction() {return B;}
	public Vec3 point_at_parameter(double t) {return A.add(B.mul(t));}
	public void set(Ray r){
		A = r.A;
		B = r.B;
	}
}