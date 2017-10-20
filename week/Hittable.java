//base class for all Hittable objects
import java.util.Comparator;

public class Hittable{
	/**
	* @param r ray looking to intersect
	* @param t_min minimum of range of t allowed for hit
	* @param t_max maximum of range of t allowed for hit
	* @param rec Record of last hit
	*/
	public boolean hit(Ray r, double t_min, double t_max, HitRecord rec){return false;}
	public boolean bounding_box(double t0, double t1, AABB box){return false;}
}

//flips the normal, used for flat rectangles
class FlipNormals extends Hittable{
	Hittable ptr;
	public FlipNormals(Hittable p){
		ptr = p;
	}
	public boolean hit(Ray r, double t_min, double t_max, HitRecord rec){
		if(ptr.hit(r,t_min,t_max,rec)){
			rec.normal = rec.normal.mul(-1);
			return true;
		} else {
			return false;
		}
	}
	public boolean bounding_box(double t0, double t1, AABB box){
		return ptr.bounding_box(t0,t1,box);
	}
}

//translate Hittable objects by an offset
class Translate extends Hittable{
	Hittable ptr;
	Vec3 offset;

	public Translate(Hittable p, Vec3 displacement){
		ptr = p;
		offset = displacement;
	}

	public boolean hit(Ray r, double t_min, double t_max, HitRecord rec){
		Ray moved_r = new Ray(r.origin().sub(offset), r.direction(), r.time()); //translate to origin
		if(ptr.hit(moved_r, t_min, t_max, rec)){
			rec.p = rec.p.add(offset); //translate back
			return true;
		} else {
			return false;
		}
	}

	public boolean bounding_box(double t0, double t1, AABB box){
		if(ptr.bounding_box(t0, t1, box)){
			box.set(new AABB(box.min().add(offset), box.max().add(offset)));
			return true;
		} else {
			return false;
		}
	}
}

//rotate about Y axis
class RotateY extends Hittable{
	Hittable ptr;
	double sin_theta, cos_theta;
	boolean hasbox;
	AABB bbox = new AABB();

	public RotateY(Hittable p, double angle){
		ptr = p;
		double radians = Math.PI*angle/180;
		sin_theta = Math.sin(radians);
		cos_theta = Math.cos(radians);
		hasbox = ptr.bounding_box(0,1,bbox);
		Vec3 min = new Vec3(Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE);
		Vec3 max = new Vec3(-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE);
		for(int i = 0; i<2; i++){
			for(int j = 0; j<2; j++){
				for (int k = 0; k<2; k++) {
					double x = i*bbox.max().x() + (1-i)*bbox.min().x();
					double y = j*bbox.max().y() + (1-j)*bbox.min().y();
					double z = k*bbox.max().z() + (1-k)*bbox.min().z();
					double newx = cos_theta*x + sin_theta*z; //apply rotation
					double newz = -sin_theta*x + cos_theta*z;
					Vec3 tester = new Vec3(newx, y, newz);
					for(int c = 0; c<3; c++){ //find the max of the rotated box
						if(tester.e[c] > max.e[c]){
							max.e[c] = tester.e[c];
						}
						if(tester.e[c] < min.e[c]){
							min.e[c] = tester.e[c];
						}
					}
				}
			}
		}
		bbox.set(new AABB(min,max));
	}

	public boolean hit(Ray r, double t_min, double t_max, HitRecord rec){
		Vec3 origin = new Vec3();
		origin.set(r.origin());
		Vec3 direction = new Vec3();
		direction.set(r.direction());

		origin.e[0] = cos_theta*r.origin().get(0) - sin_theta*r.origin().get(2); //rotate
		origin.e[2] = sin_theta*r.origin().get(0) + cos_theta*r.origin().get(2);
		direction.e[0] = cos_theta*r.direction().get(0) - sin_theta*r.direction().get(2);
		direction.e[2] = sin_theta*r.direction().get(0) + cos_theta*r.direction().get(2);
		Ray rotated_r = new Ray(origin, direction, r.time());
		if(ptr.hit(rotated_r, t_min, t_max, rec)){
			Vec3 p = new Vec3();
			p.set(rec.p);
			Vec3 normal = new Vec3();
			normal.set(rec.normal);

			p.e[0] = cos_theta*rec.p.get(0) + sin_theta*rec.p.get(2); //unrotate
			p.e[2] = -sin_theta*rec.p.get(0) + cos_theta*rec.p.get(2);
			normal.e[0] = cos_theta*rec.normal.get(0) + sin_theta*rec.normal.get(2);
			normal.e[2] = -sin_theta*rec.normal.get(0) + cos_theta*rec.normal.get(2);
			rec.p = p;
			rec.normal = normal;
			return true; 
		} else {
			return false;
		}
	}

	public boolean bounding_box(double t0, double t1, AABB box){
		box.set(bbox);
		return hasbox;
	}
}

//General rotation
//The boudning box may be bugged, am still testing
class Rotate extends Hittable{
	public static final int X = 0, Y =1, Z = 2;
	Hittable ptr;
	double sin_theta, cos_theta;
	boolean hasbox;
	int axis;
	AABB bbox = new AABB();

	/**
	* @param p object to rotate
	* @param angle rotation angle in degrees
	* @param _axis x = 0, y = 1, z = 2
	*/
	public Rotate(Hittable p, double angle, int _axis){
		ptr = p;
		axis = _axis;
		double radians = Math.PI*angle/180;
		sin_theta = Math.sin(radians);
		cos_theta = Math.cos(radians);
		hasbox = ptr.bounding_box(0,1,bbox);
		Vec3 min = new Vec3(Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE);
		Vec3 max = new Vec3(-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE);
		for(int i = 0; i<2; i++){
			for(int j = 0; j<2; j++){
				for (int k = 0; k<2; k++) {
					double x = i*bbox.max().x() + (1-i)*bbox.min().x();
					double y = j*bbox.max().y() + (1-j)*bbox.min().y();
					double z = k*bbox.max().z() + (1-k)*bbox.min().z();
					double newx = x, newy = y, newz = z;
					switch(axis){ //apply rotation
						case 0:
							newy = cos_theta*y - sin_theta*z;
							newz = sin_theta*y + cos_theta*z;
							break;
						case 1:
							newx = cos_theta*x + sin_theta*z;
							newz = -sin_theta*x + cos_theta*z;
							break;
						case 2:
							newx = cos_theta*x - sin_theta*y;
							newy = sin_theta*x + cos_theta*y;
							break;
					}
					
					Vec3 tester = new Vec3(newx, newy, newz);
					for(int c = 0; c<3; c++){ //find the max of the rotated box
						if(tester.e[c] > max.e[c]){
							max.e[c] = tester.e[c];
						}
						if(tester.e[c] < min.e[c]){
							min.e[c] = tester.e[c];
						}
					}
				}
			}
		}
		bbox.set(new AABB(min,max));
	}

	public boolean hit(Ray r, double t_min, double t_max, HitRecord rec){
		Vec3 origin = new Vec3();
		origin.set(r.origin());
		Vec3 direction = new Vec3();
		direction.set(r.direction());

		switch(axis){
			case 0:
				origin.e[1] = cos_theta*r.origin().get(1) - sin_theta*r.origin().get(2); //rotate
				origin.e[2] = sin_theta*r.origin().get(1) + cos_theta*r.origin().get(2);
				direction.e[1] = cos_theta*r.direction().get(1) - sin_theta*r.direction().get(2);
				direction.e[2] = sin_theta*r.direction().get(1) + cos_theta*r.direction().get(2);
				break;
			case 1:
				origin.e[0] = cos_theta*r.origin().get(0) - sin_theta*r.origin().get(2); //rotate
				origin.e[2] = sin_theta*r.origin().get(0) + cos_theta*r.origin().get(2);
				direction.e[0] = cos_theta*r.direction().get(0) - sin_theta*r.direction().get(2);
				direction.e[2] = sin_theta*r.direction().get(0) + cos_theta*r.direction().get(2);
				break;
			case 2:
				origin.e[0] = cos_theta*r.origin().get(0) - sin_theta*r.origin().get(1); //rotate
				origin.e[1] = sin_theta*r.origin().get(0) + cos_theta*r.origin().get(1);
				direction.e[0] = cos_theta*r.direction().get(0) - sin_theta*r.direction().get(1);
				direction.e[1] = sin_theta*r.direction().get(0) + cos_theta*r.direction().get(1);
				break;
		}
		Ray rotated_r = new Ray(origin, direction, r.time());
		if(ptr.hit(rotated_r, t_min, t_max, rec)){
			Vec3 p = new Vec3();
			p.set(rec.p);
			Vec3 normal = new Vec3();
			normal.set(rec.normal);
			switch(axis){
				case 0:
					p.e[1] = cos_theta*rec.p.get(1) + sin_theta*rec.p.get(2); //unrotate
					p.e[2] = -sin_theta*rec.p.get(1) + cos_theta*rec.p.get(2);
					normal.e[1] = cos_theta*rec.normal.get(1) + sin_theta*rec.normal.get(2);
					normal.e[2] = -sin_theta*rec.normal.get(1) + cos_theta*rec.normal.get(2);
					break;
				case 1:
					p.e[0] = cos_theta*rec.p.get(0) + sin_theta*rec.p.get(2); //unrotate
					p.e[2] = -sin_theta*rec.p.get(0) + cos_theta*rec.p.get(2);
					normal.e[0] = cos_theta*rec.normal.get(0) + sin_theta*rec.normal.get(2);
					normal.e[2] = -sin_theta*rec.normal.get(0) + cos_theta*rec.normal.get(2);
					break;
				case 2:
					p.e[0] = cos_theta*rec.p.get(0) + sin_theta*rec.p.get(1); //unrotate
					p.e[1] = -sin_theta*rec.p.get(0) + cos_theta*rec.p.get(1);
					normal.e[0] = cos_theta*rec.normal.get(0) + sin_theta*rec.normal.get(1);
					normal.e[1] = -sin_theta*rec.normal.get(0) + cos_theta*rec.normal.get(1);
					break;
			}
			rec.p = p;
			rec.normal = normal;
			return true; 
		} else {
			return false;
		}
	}

	public boolean bounding_box(double t0, double t1, AABB box){
		box.set(bbox);
		return hasbox;
	}
}

class SortBoxX implements Comparator<Hittable>{
	public int compare(Hittable a, Hittable b){
		AABB box_left = new AABB();
		AABB box_right = new AABB();
		if(!a.bounding_box(0,0, box_left) || !b.bounding_box(0,0, box_right)){
			throw new java.lang.RuntimeException("no bounding box");
		}
		if(box_left.min().x() == box_right.min().x()){
			return 0;
		}
		if(box_left.min().x() < box_right.min().x()){
			return -1;
		} else {
			return 1;
		}
	}
}

class SortBoxY implements Comparator<Hittable>{
	public int compare(Hittable a, Hittable b){
		AABB box_left = new AABB();
		AABB box_right = new AABB();
		if(!a.bounding_box(0,0, box_left) || !b.bounding_box(0,0, box_right)){
			throw new java.lang.RuntimeException("no bounding box");
		}
		if(box_left.min().y() == box_right.min().y()){
			return 0;
		}
		if(box_left.min().y() < box_right.min().y()){
			return -1;
		} else {
			return 1;
		}
	}
}

class SortBoxZ implements Comparator<Hittable>{
	public int compare(Hittable a, Hittable b){
		AABB box_left = new AABB();
		AABB box_right = new AABB();
		if(!a.bounding_box(0,0, box_left) || !b.bounding_box(0,0, box_right)){
			throw new java.lang.RuntimeException("no bounding box");
		}
		if(box_left.min().z() == box_right.min().z()){
			return 0;
		}
		if(box_left.min().z() < box_right.min().z()){
			return -1;
		} else {
			return 1;
		}
	}
}