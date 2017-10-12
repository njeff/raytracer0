//base class for all hitable objects
import java.util.Comparator;

public class Hitable{
	boolean hit(Ray r, double t_min, double t_max, HitRecord rec){return false;}
	boolean bounding_box(double t0, double t1, AABB box){return false;}
}

class SortBoxX implements Comparator<Hitable>{
	public int compare(Hitable a, Hitable b){
		AABB box_left = new AABB();
		AABB box_right = new AABB();
		if(!a.bounding_box(0,0, box_left) || !b.bounding_box(0,0, box_right)){
			throw new java.lang.RuntimeException("no bounding box");
		}
		if(box_left.min().x() - box_right.min().x() < 0){
			return -1;
		} else {
			return 1;
		}
	}
}

class SortBoxY implements Comparator<Hitable>{
	public int compare(Hitable a, Hitable b){
		AABB box_left = new AABB();
		AABB box_right = new AABB();
		if(!a.bounding_box(0,0, box_left) || !b.bounding_box(0,0, box_right)){
			throw new java.lang.RuntimeException("no bounding box");
		}
		if(box_left.min().y() - box_right.min().y() < 0){
			return -1;
		} else {
			return 1;
		}
	}
}

class SortBoxZ implements Comparator<Hitable>{
	public int compare(Hitable a, Hitable b){
		AABB box_left = new AABB();
		AABB box_right = new AABB();
		if(!a.bounding_box(0,0, box_left) || !b.bounding_box(0,0, box_right)){
			throw new java.lang.RuntimeException("no bounding box");
		}
		if(box_left.min().z() - box_right.min().z() < 0){
			return -1;
		} else {
			return 1;
		}
	}
}