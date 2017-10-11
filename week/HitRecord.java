//keeps track of current distance from last point, intersection point, normal, and material hit

public class HitRecord{
	double t;
	Vec3 p;
	Vec3 normal;
	Material mat;
	Hitable h;

	//get pass problem with java not being able to properly pass by refernce
	public void set(HitRecord hr){
		t = hr.t;
		p = hr.p;
		normal = hr.normal;
		mat = hr.mat;
		h = hr.h;
	}
}