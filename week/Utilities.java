public class Utilities{
	static Vec3 random_in_unit_sphere(){
		Vec3 p;
		do{
			p = new Vec3(Math.random(),Math.random(),Math.random()).mul(2).sub(new Vec3(1,1,1));
		} while(p.squared_length() >= 1.0);
		return p;
	}

	static Vec3 reflect(Vec3 v, Vec3 n){
		return v.sub(n.mul(Vec3.dot(v,n)*2));
	}

	static boolean refract(Vec3 v, Vec3 n, double ni_over_nt, Vec3 refracted){
		Vec3 uv = Vec3.unit_vector(v);
		double dt = Vec3.dot(uv,n);
		double discriminant = 1.0 - ni_over_nt*ni_over_nt*(1-dt*dt);
		if(discriminant > 0){
			refracted.set(uv.sub(n.mul(dt)).mul(ni_over_nt).sub(n.mul(Math.sqrt(discriminant))));
			return true;
		} else {
			return false;
		}
	}

	//approximation of fresnel equation
	//returns proportion of light that is reflected
	static double schlick(double cosine, double ref_idx){
		double r0 = (1-ref_idx)/(1+ref_idx);
		r0 = r0*r0;
		return r0 + (1-r0)*Math.pow(1-cosine,5);
	}

	static AABB surrounding_box(AABB box0, AABB box1){
		Vec3 small = new Vec3(Math.min(box0.min().x(), box1.min().x()),
							Math.min(box0.min().y(), box1.min().y()),
							Math.min(box0.min().z(), box1.min().z()));
		Vec3 big = new Vec3(Math.min(box0.max().x(), box1.max().x()),
							Math.min(box0.max().y(), box1.max().y()),
							Math.min(box0.max().z(), box1.max().z()));
		return new AABB(small,big);
	}
}