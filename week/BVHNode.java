public class BVHNode extends Hitable{
	AABB box;
	Hitable left, right;

	public BVHNode() {}

	public BVHNode(Hitable[] l, int n, double time0, double time1){
		int axis = (int)(Math.random()*3);
		if(axis == 0){
			Arrays.sort(l, new SortBoxX());
		} else if(axis == 1){
			Arrays.sort(l, new SortBoxY());
		} else {
			Arrays.sort(l, new SortBoxZ());
		}
		if(n == 1){
			left = l[0];
			right = l[1]
		} else if (n == 2){
			left = new bvh_node(1, n/2, time0, time1);
			right = new bvh_node(1 + n/2, n-n/2, time0, time1);
		}
		AABB box_left, boxright;
		if(!left.bounding_box(time0, time1, box_left) || !right.bounding_box(time0, time1, box_right)){
			throw new java.lang.RuntimeException("no bounding box");
		}
		box = surrounding_box(box_left, boxright);
	}

	boolean hit(Ray r, double t_min, double t_max, HitRecord rec){
		if(box.hit(r, t_min, t_max)){
			HitRecord left_rec, right_rec;
			boolean hit_left = left.hit(r, t_min, t_max, left_rec);
			boolean hit_right = right.hit(r, t_min, t_max, left_rec);
			if(hit_left && hit_right){
				if(left_rec.t < right_rec.t){ //left in front of right
					rec.set(left_rec);
				} else {
					rec.set(right_rec);
				}
				return true;
			} else if(hit_left){
				rec.set(left_rec);
				return true;
			} else if(hit_right){
				rec.set(right_rec);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	boolean bounding_box(double t0, double t1, AABB b){
		b.set(box);
		return true;
	}
}