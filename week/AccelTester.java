/**
* Intersection acceleration structure tester
* Verifies that ray object intersections in acceleration structures 
* are the same as ones found in brute forced calculations
*/
public class AccelTester{
	public static int RAYS = 1000;

	public static void main(String[] args){
		//load scenes to compare
		if(args.length == 1){
			RAYS = Integer.parseInt(args[0]);
		}
		HittableList accelerated = Scenes.cornell_box(true);
		HittableList non_accel = Scenes.cornell_box(false);
		AABB box = new AABB();
		AABB enclosing;
		if(accelerated.bounding_box(0,1,box)){
			//create a box around the scene with some padding
			enclosing = new AABB(box.min().sub(new Vec3(10,10,10)),box.max().add(new Vec3(10,10,10)));
			Ray[] rays = new Ray[RAYS];
			//generate random rays
			for(int i = 0; i<RAYS; i++){
				rays[i] = new Ray(enclosing.min().add(enclosing.max().sub(enclosing.min()).mul(Math.random())),
					Vec3.unit_vector(Utilities.random_in_unit_sphere()));
			}
			//test all rays
			int passed = 0;
			System.out.println("Beginning test.");
			for(Ray r: rays){
				HitRecord rec_a = new HitRecord();
				boolean ah = accelerated.hit(r,0.001,Double.MAX_VALUE,rec_a);
				HitRecord rec_na = new HitRecord();
				boolean nah = non_accel.hit(r,0.001,Double.MAX_VALUE,rec_na);
				String error = "";
				if(ah && nah){
					if(!rec_a.p.equals(rec_na.p)){
						error += "\nHP: " + rec_a.p.toString() + " v " + rec_na.p.toString() + ", ";
					}
					if(rec_a.t != rec_na.t){
						error += "\nT: " + rec_a.t + " v " + rec_na.t + ", ";
					}
					if(!rec_a.normal.equals(rec_a.normal)){
						error += "\nN: " + rec_a.normal.toString() + " v " + rec_na.normal.toString() + ", ";
					}
					if(error.equals("")){
						//System.out.println("Intersection passed");
						passed++;
					} else {
						System.out.println("Intersection failed: " + error);
					}
				} else {
					if(ah != nah){
						System.out.println("Intersection conflict");
					} else {
						//System.out.println("Intersection passed");
						passed++;
					}
				}
			}
			System.out.println(passed + "/" + RAYS + " intersections passed.");
		} else {
			System.out.println("No bounding box?");
		}
	}
}