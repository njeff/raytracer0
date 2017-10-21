import java.util.Arrays;

/*
Designed to accelerate ray object collision detection by putting objects into a binary tree of enclosing bounding boxes
There are currently some issues with this causing improper rendering. 
I think the issue might lie in the bounding box of the rotate Hittable though.
*/
public class BVHNode extends HittableList{
	AABB box;
	Hittable left, right;
	int lower, upper, axis;

	public BVHNode() {}

	/**
	* @param l list of objects to hold
	* @param lower starting index of objects in l that this node holds
	* @param upper ending index of objects in l that this node holds
	* @param time0 times used for moving objects
	* @param time1
	*/
	public BVHNode(Hittable[] l, int lower, int upper, double time0, double time1){
		this.lower = lower;
		this.upper = upper;

		//sort on axis where object centroid have largest range
		Vec3 lowerCentroid = new Vec3();
		Vec3 upperCentroid = new Vec3();
		AABB tempBox = new AABB();
		for(int i = lower; i<upper; i++){
			if(l[i].bounding_box(time0, time1, tempBox)){
				Vec3 centroid = tempBox.max().add(tempBox.min()).div(2);
				for(int j = 0; j<3; j++){
					if(centroid.get(j) < lowerCentroid.e[j]){
						lowerCentroid.e[j] = centroid.get(j);
					}
					if(centroid.get(j) > upperCentroid.e[j]){
						upperCentroid.e[j] = centroid.get(j);
					}
				}
			}
		}
		Vec3 cRange = upperCentroid.sub(lowerCentroid);
		axis = 0;
		for(int i = 0; i<3; i++){
			if(cRange.get(i) > cRange.get(axis)){
				axis = i;
			}
		}

		if(axis == 0){
			Arrays.sort(l, lower, upper, new SortBoxX());
		} else if(axis == 1){
			Arrays.sort(l, lower, upper, new SortBoxY());
		} else {
			Arrays.sort(l, lower, upper, new SortBoxZ());
		}
		if(upper-lower == 1){
			left = l[lower];
			right = l[lower];
		} else if(upper-lower == 2){
			left = l[lower];
			right = l[lower+1];
		} else {
			//build tree
			left = new BVHNode(l, lower, (upper+lower)/2, time0, time1);
			right = new BVHNode(l, (upper+lower)/2, upper, time0, time1);
		}
		AABB box_left = new AABB();
		AABB box_right = new AABB();
		if(!left.bounding_box(time0, time1, box_left) || !right.bounding_box(time0, time1, box_right)){
			throw new java.lang.RuntimeException("no bounding box");
		}
		box = Utilities.surrounding_box(box_left, box_right);
	}

	public boolean hit(Ray r, double t_min, double t_max, HitRecord rec){
		if(box.hit(r, t_min, t_max)){ //if bounding box is hit
			HitRecord left_rec = new HitRecord();
			HitRecord right_rec = new HitRecord();
			boolean hit_left = left.hit(r, t_min, t_max, left_rec); //check if subnodes are hit
			boolean hit_right = right.hit(r, t_min, t_max, right_rec);
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

	public String toString(){
		if(upper-lower <= 2){
			return "l: " + left.toString() + " r: " + right.toString();
		} else{
			return "n: " + (upper-lower) + "\n[" + left.toString() + "-|-" + right.toString() + "]";
		}
	}
}