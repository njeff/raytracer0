//list of Hittable objects

public class HittableList extends Hittable{
	int list_size;
	Hittable[] list;

	HittableList() {}
	/**
	* @param l list of Hittable objects to contain
	* @param n number of objects in list
	*/
	HittableList(Hittable[] l, int n){
		list = l; 
		list_size = n;
	}

	public boolean hit(Ray r, double t_min, double t_max, HitRecord rec) {
		HitRecord temp_rec = new HitRecord();
		boolean hit_anything = false;
		double closest_so_far = t_max;
		for(int i = 0; i<list_size; i++){ //iterates through all Hittable objects
			if(list[i].hit(r, t_min, closest_so_far, temp_rec)){
				hit_anything = true;
				closest_so_far = temp_rec.t;
				rec.set(temp_rec);
			}
		}
		return hit_anything;
	}

	public boolean bounding_box(double t0, double t1, AABB box){
		if(list_size < 1) return false;
		AABB temp_box = new AABB();
		boolean first_true = list[0].bounding_box(t0, t1, temp_box);
		if(!first_true){ //check if at least one thing has a box
			return false;
		} else {
			box.set(temp_box);
		}
		for(int i = 1; i < list_size; i++){
			if(list[i].bounding_box(t0, t1, temp_box)){
				//enlarge box as necessary
				box.set(Utilities.surrounding_box(box, temp_box));
			} else { //if at anytime something doesnt have a box
				return false;
			}
		}
		return true;
	}

	public double pdf_value(Vec3 o, Vec3 v){
		double weight = 1.0/list_size;
		double sum = 0;
		for(int i = 0; i<list_size; i++){
			sum += weight*list[i].pdf_value(o,v);
		}
		return sum;
	}

	public Vec3 random(Vec3 o){
		int index = (int)(Math.random()*list_size);
		return list[index].random(o);
	}
}