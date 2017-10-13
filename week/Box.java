//axis aligned box
public class Box extends Hitable{
	Vec3 pmin, pmax;
	HitableList hlist;

	public Box() {}
	public Box(Vec3 p0, Vec3 p1, Material mat){
		pmin = p0;
		pmax = p1;
		Hitable[] list = new Hitable[6];
		list[0] = new XYRect(p0.x(), p1.x(), p0.y(), p1.y(), p1.z(), mat);
		list[1] = new FlipNormals(new XYRect(p0.x(), p1.x(), p0.y(), p1.y(), p0.z(), mat));
		list[2] = new XZRect(p0.x(), p1.x(), p0.z(), p1.z(), p1.y(), mat);
		list[3] = new FlipNormals(new XZRect(p0.x(), p1.x(), p0.z(), p1.z(), p0.y(), mat));
		list[4] = new YZRect(p0.y(), p1.y(), p0.z(), p1.z(), p1.x(), mat);
		list[5] = new FlipNormals(new YZRect(p0.y(), p1.y(), p0.z(), p1.z(), p0.x(), mat));
		hlist = new HitableList(list,6);
	}

	public boolean hit(Ray r, double t_min, double t_max, HitRecord rec){
		return hlist.hit(r, t_min, t_max, rec);
	}

	public boolean bounding_box(double t0, double t1, AABB box){
		box.set(new AABB(pmin, pmax));
		return true;
	}
}