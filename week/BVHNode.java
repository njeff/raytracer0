import java.util.Arrays;

//need to put into use
public class BVHNode extends Hitable{
	AABB box;
	Hitable left, right;

	public BVHNode() {}

	public BVHNode(Hitable[] l, double time0, double time1){
		int axis = (int)(Math.random()*3);
		if(axis == 0){
			Arrays.sort(l, new SortBoxX());
		} else if(axis == 1){
			Arrays.sort(l, new SortBoxY());
		} else {
			Arrays.sort(l, new SortBoxZ());
		}
		if(l.length == 1){
			left = l[0];
			right = l[0];
		} else if(l.length == 2){
			left = l[0];
			right = l[1];
		} else {
			left = new BVHNode(Arrays.copyOfRange(l,0,l.length/2), time0, time1); //not sure if this will work
			right = new BVHNode(Arrays.copyOfRange(l,l.length/2,l.length), time0, time1);
		}
		AABB box_left = new AABB();
		AABB box_right = new AABB();
		if(!left.bounding_box(time0, time1, box_left) || !right.bounding_box(time0, time1, box_right)){
			throw new java.lang.RuntimeException("no bounding box");
		}
		box = Utilities.surrounding_box(box_left, box_right);
	}

	public boolean hit(Ray r, double t_min, double t_max, HitRecord rec){
		if(box.hit(r, t_min, t_max)){
			HitRecord left_rec = new HitRecord();
			HitRecord right_rec = new HitRecord();
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

	public boolean bounding_box(double t0, double t1, AABB b){
		b.set(box);
		return true;
	}
}