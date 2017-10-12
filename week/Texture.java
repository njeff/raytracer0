public abstract class Texture{
	public abstract Vec3 value(double u, double v, Vec3 p);
}

class ConstantTexture extends Texture{
	Vec3 color;
	public ConstantTexture() {}

	public ConstantTexture(Vec3 c){
		color = c;
	}

	public Vec3 value(double u, double v, Vec3 p){
		return color;
	}
}

class CheckerTexture extends Texture{
	Texture odd, even;

	public CheckerTexture() {}

	public CheckerTexture(Texture t0, Texture t1){
		even = t0;
		odd = t1;
	}

	public Vec3 value(double u, double v, Vec3 p){
		double sines = Math.sin(10*p.x()) * Math.sin(10*p.y()) * Math.sin(10*p.z());
		if (sines < 0){
			return odd.value(u,v,p);
		} else{
			return even.value(u,v,p);
		}
	}
}