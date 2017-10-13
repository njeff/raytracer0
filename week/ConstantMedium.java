public class ConstantMedium extends Hitable{
	Hitable boundary;
	double density;
	Material phase_function;

	public ConstantMedium(Hitable b, double d, Texture a){
		boundary = b;
		density = d;
		phase_function = new Isotropic(a);
	}

	public boolean hit(Ray r, double t_min, double t_max, HitRecord rec){
		boolean db = Math.random() < 0.00001; //limit debug output due to high number of collisions
		db = false; //debug
		HitRecord rec1 = new HitRecord();
		HitRecord rec2 = new HitRecord();
		if(boundary.hit(r, -Double.MAX_VALUE, Double.MAX_VALUE, rec1)){ //hit the first side
			if(boundary.hit(r, rec1.t+0.0001, Double.MAX_VALUE, rec2)){ //come out the other side
				if(db){
					System.out.println("t0 t1 " + rec1.t + " " + rec2.t);
				}
				if(rec1.t < t_min) rec1.t = t_min;
				if(rec2.t > t_max) rec2.t = t_max;
				if(rec1.t >= rec2.t) return false;
				if(rec1.t < 0) rec1.t = 0;
				double distance_inside_boundary = (rec2.t - rec1.t)*r.direction().length(); //find distance traveled inside
				double hit_distance = -(1/density)*Math.log(Math.random());
				if(hit_distance < distance_inside_boundary){
					if(db){
						System.out.println("hit_distance = " + hit_distance);
					}
					rec.t = rec1.t + hit_distance/r.direction().length(); //get new length
					if(db){
						System.out.println("rec.t = " + rec.t);
					}
					rec.p = r.point_at_parameter(rec.t); //set new ray origin
					if(db){
						System.out.println("rec.p = " + rec.p);
					}
					rec.normal = new Vec3(1,0,0);
					rec.mat = phase_function;
					return true;
				}
			}
		}
		return false;
	}

	public boolean bounding_box(double t0, double t1, AABB box){
		return boundary.bounding_box(t0,t1,box);
	}
}