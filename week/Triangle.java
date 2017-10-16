//triangle hitable (no thickness)
public class Triangle extends Hitable{
	Vec3 p0, p1, p2, normal;
	Material m;

	public Triangle() {}
	
	public Triangle(Vec3 _p0, Vec3 _p1, Vec3 _p2, Material mat){
		p0 = _p0;
		p1 = _p1;
		p2 = _p2;
		normal = Vec3.unit_vector(Vec3.cross(p1.sub(p0),p2.sub(p1))); //use right hand rule, assuming vertices are listed counterclockwise
		m = mat;
	}

	public boolean hit(Ray r, double t_min, double t_max, HitRecord rec){
		double n_dot_dir = Vec3.dot(r.direction(),normal);
		if(Math.abs(n_dot_dir) < 0.0001) return false; //ray parallel to triangle can't hit

		//find the ray plane intersection first
		double d = -Vec3.dot(normal,p0); //Use p0 and the equation Ax + By + Cz + D = 0, where N = <A, B, C> to find D
		double t = -(Vec3.dot(normal,r.origin()) + d)/n_dot_dir; //Solve for t in A(p) + B(p) + C)p + D = 0, where p = origin + dir * t
		//System.out.println("t: " + t);

		if(t < t_min || t > t_max) return false;
		Vec3 p = r.point_at_parameter(t);

		Vec3 s1 = p1.sub(p0); //get the first side
		Vec3 v_to_p = p.sub(p0); //get vector from one vertex to intersection point
		//if the cross product of the side and line to point isn't in the same direction as the normal, we are outside the triangle
		if(Vec3.dot(Vec3.cross(s1,v_to_p),normal) < 0) return false;
		//repeat for all sides
		Vec3 s2 = p2.sub(p1);
		v_to_p = p.sub(p1);
		if(Vec3.dot(Vec3.cross(s2,v_to_p),normal) < 0) return false;
		
		Vec3 s3 = p0.sub(p2);
		v_to_p = p.sub(p2);
		if(Vec3.dot(Vec3.cross(s3,v_to_p),normal) < 0) return false;

		rec.t = t;
		rec.p = p;
		rec.normal = normal;
		rec.mat = m;
		return true;
	}

	public boolean bounding_box(double t0, double t1, AABB box){
		Vec3 max = new Vec3();
		Vec3 min = new Vec3();
		max.e[0] = Math.max(p0.x(),Math.max(p1.x(),p2.x()));
		max.e[1] = Math.max(p0.y(),Math.max(p1.y(),p2.y()));
		max.e[2] = Math.max(p0.z(),Math.max(p1.z(),p2.z()));
		min.e[0] = Math.min(p0.x(),Math.min(p1.x(),p2.x()));
		min.e[1] = Math.min(p0.y(),Math.min(p1.y(),p2.y()));
		min.e[2] = Math.min(p0.z(),Math.min(p1.z(),p2.z())); 
		return true;
	}
}