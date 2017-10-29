public class Utilities{
	/**
	* returns a random vector in the unit sphere
	*/
	static Vec3 random_in_unit_sphere(){
		Vec3 p;
		do{
			p = new Vec3(Math.random(),Math.random(),Math.random()).mul(2).sub(new Vec3(1,1,1));
		} while(p.squared_length() >= 1.0);
		return p;
	}

	/**
	* returns a random vector on the unit sphere
	*/
	static Vec3 random_on_unit_sphere(){
		Vec3 p;
		do{
			p = new Vec3(Math.random(),Math.random(),Math.random()).mul(2).sub(new Vec3(1,1,1));
		} while(p.squared_length() >= 1.0);
		return Vec3.unit_vector(p);
	}

	/**
	* generate random directions weighted by cosine
	*/
	static Vec3 random_cosine_direction(){
		double r1 = Math.random();
		double r2 = Math.random();
		double z = Math.sqrt(1-r2);
		double phi = 2*Math.PI*r1; //random phi around sphere
		double x = Math.cos(phi)*2*Math.sqrt(r2);
		double y = Math.sin(phi)*2*Math.sqrt(r2);
		return new Vec3(x, y, z);
	}

	/**
	* Create random direction to a sphere
	*/
	static Vec3 random_to_sphere(double radius, double distance_squared){
		//create two rand in 0 - 1
		double r1 = Math.random();
		double r2 = Math.random();

		//sqrt(1-radius*radius/distance_squared) = cos(theta from middle to edge) 
		double z = 1 + r2*(Math.sqrt(1-radius*radius/distance_squared)-1); //offset from middle

		double phi = 2*Math.PI*r1; //random phi around sphere
		double x = Math.cos(phi)*Math.sqrt(1-z*z);
		double y = Math.sin(phi)*Math.sqrt(1-z*z);
		return new Vec3(x, y, z);
	}

	/**
	* @param v direction of the incidence vector
	* @param n normal of the surface
	*/
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

	/**
	* Approximation of fresnel equation using schlick's approximation
	* Other material is assumed to be air
	* @param cosine the cosine of the angle between ray and normal
	* @param ref_idx the index of refraction of material
	* Returns proportion of light that is reflected
	*/
	static double schlick(double cosine, double ref_idx){
		double r0 = (1-ref_idx)/(1+ref_idx);
		r0 = r0*r0;
		return r0 + (1-r0)*Math.pow(1-cosine,5);
	}

	/**
	* @param box0
	* @param box1
	* returns a bounding box that encloses both box0 and box1
	*/
	static AABB surrounding_box(AABB box0, AABB box1){
		Vec3 small = new Vec3(Math.min(box0.min().x(), box1.min().x()),
							Math.min(box0.min().y(), box1.min().y()),
							Math.min(box0.min().z(), box1.min().z()));
		Vec3 big = new Vec3(Math.max(box0.max().x(), box1.max().x()),
							Math.max(box0.max().y(), box1.max().y()),
							Math.max(box0.max().z(), box1.max().z()));
		return new AABB(small,big);
	}

	/**
	* @param p array to shuffle
	*/
	static void permute(int[] p){
		for(int i = p.length-1; i > 0; i--){
			int target = (int)(Math.random()*(i+1));
			int temp = p[i];
			p[i] = p[target];
			p[target] = temp;
		}
	}

	/**
	* @param p array to shuffle
	*/
	static void permute(double[] p){
		for(int i = p.length-1; i > 0; i--){
			int target = (int)(Math.random()*(i+1));
			double temp = p[i];
			p[i] = p[target];
			p[target] = temp;
		}
	}
}

//for passing around doubles (this is jank)
class DoubleP{
	double v = 0;
	public DoubleP(){}
	public DoubleP(double a){
		v = a;
	}
	public void set(double a){
		v = a;
	}
}