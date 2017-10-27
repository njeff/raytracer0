public abstract class PDF{
	public abstract double value(Vec3 direction);
	public abstract Vec3 generate();
}

class CosinePDF extends PDF{
	ONB uvw = new ONB();
	public CosinePDF(Vec3 w){
		uvw.buildFromW(w);
	}

	public double value(Vec3 direction){
		double cosine = Vec3.dot(Vec3.unit_vector(direction), uvw.w());
		if(cosine > 0){ //if in hemisphere
			return cosine/Math.PI;
		} else{
			return 0;
		}
	}

	public Vec3 generate(){
		return uvw.local(Utilities.random_cosine_direction());
	}
}

class HittablePDF extends PDF{
	Vec3 o;
	Hittable ptr;

	public HittablePDF(Hittable p, Vec3 origin){
		ptr = p;
		o = origin;
	}

	public double value(Vec3 direction){
		return ptr.pdf_value(o, direction);
	}

	public Vec3 generate(){
		return ptr.random(o);
	}
}

class MixturePDF extends PDF{
	PDF[] p = new PDF[2];

	public MixturePDF(PDF p0, PDF p1){
		p[0] = p0;
		p[1] = p1;
	}

	public double value(Vec3 direction){
		return 0.5*p[0].value(direction) + 0.5*p[1].value(direction);
	}

	public Vec3 generate(){
		if(Math.random() < 0.5){
			return p[0].generate();
		} else {
			return p[1].generate();
		}
	}
}