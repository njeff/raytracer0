public class Camera{
	Vec3 origin;
	Vec3 lower_left_corner;
	Vec3 horizontal;
	Vec3 vertical;
	Vec3 u,v,w;
	double lens_radius;

	public Camera(Vec3 lookfrom, Vec3 lookat, Vec3 vup, double hfov, double aspect, double aperture, double focus_dist){ //hfov is left to right in degrees
		lens_radius = 1/(aperture*2);
		double theta = hfov*Math.PI/180;
		double half_width = Math.tan(theta/2);
		double half_height = half_width/aspect;
		origin = lookfrom;
		w = Vec3.unit_vector(lookfrom.sub(lookat)); //vector pointing out of camera
		u = Vec3.unit_vector(Vec3.cross(vup,w)); //vector pointing out of side of camera, orthogonal to both view and up direction
		v = Vec3.cross(w,u); //vector pointing out top of camera
		//lower_left_corner = new Vec3(-half_width,-half_height,-1.0);
		lower_left_corner = origin.sub(u.mul(half_width*focus_dist)).sub(v.mul(half_height*focus_dist)).sub(w.mul(focus_dist));
		horizontal = u.mul(2*half_width*focus_dist);
		vertical = v.mul(2*half_height*focus_dist);
	}

	Ray get_ray(double s, double t){
		Vec3 rd = random_in_unit_disk().mul(lens_radius);
		Vec3 offset = u.mul(rd.x()).sub(v.mul(rd.y()));
		return new Ray(origin.add(offset), lower_left_corner.add(horizontal.mul(s)).add(vertical.mul(t)).sub(origin).sub(offset));
	}

	private Vec3 random_in_unit_disk(){
		Vec3 p;
		do{
			p = new Vec3(Math.random(),Math.random(),0).mul(2).sub(new Vec3(1,1,0));
		} while(Vec3.dot(p,p) >= 1.0);
		return p;
	}

	//static Vec3 translate(){

	//}

	//static Vec3 rotate(){

	//}
}