/**
* Generates an orthonormal basis with n/w as one of the basis vectors
*/
public class ONB{
	Vec3[] axis = new Vec3[3];
	public ONB(){}

	public void buildFromW(Vec3 n){
		axis[2] = Vec3.unit_vector(n);
		Vec3 a;
		if(Math.abs(w().x()) > 0.9){ //if x component of given vector is already large
			a = new Vec3(0, 1, 0);
		} else {
			a = new Vec3(1, 0, 0);
		}
		axis[1] = Vec3.unit_vector(Vec3.cross(w(),a));
		axis[0] = Vec3.cross(w(), v());
	}

	/**
	* Transforms from this basis to standard basis
	* @param a u coord
	* @param b v coord
	* @param c w coord
	*/
	public Vec3 local(double a, double b, double c){
		return u().mul(a).add(v().mul(b)).add(w().mul(c));
	}

	public Vec3 local(Vec3 a){
		return u().mul(a.x()).add(v().mul(a.y())).add(w().mul(a.z()));
	}

	public Vec3 u(){
		return axis[0];
	}

	public Vec3 v(){
		return axis[1];
	}

	public Vec3 w(){
		return axis[2];
	}
}