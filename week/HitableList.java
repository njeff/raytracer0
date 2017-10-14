//list of hitable objects

public class HitableList extends Hitable{
	int list_size;
	Hitable[] list;

	HitableList() {}
	HitableList(Hitable[] l, int n){
		list = l; 
		list_size = n;
	}

	public boolean hit(Ray r, double t_min, double t_max, HitRecord rec) {
		HitRecord temp_rec = new HitRecord();
		boolean hit_anything = false;
		double closest_so_far = t_max;
		for(int i = 0; i<list_size; i++){ //iterates through all hitable objects
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
		if(!first_true){
			return false;
		} else {
			box.set(temp_box);
		}
		for(int i = 1; i < list_size; i++){
			if(list[0].bounding_box(t0, t1, temp_box)){
				box.set(Utilities.surrounding_box(box, temp_box));
			} else {
				return false;
			}
		}
		return true;
	}
}