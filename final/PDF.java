/**
* Probability distribution functions
*/
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

//https://agraphicsguy.wordpress.com/2015/11/01/sampling-microfacet-brdf/
//http://www.codinglabs.net/article_physically_based_rendering_cook_torrance.aspx
class GGXPDF extends PDF{
	ONB uvw = new ONB();
	Vec3 in;
	double r;
	public GGXPDF(Vec3 w, Vec3 in_dir, double roughness){
		uvw.buildFromW(w);
		in = in_dir;
		r = roughness;
	}

	public double value(Vec3 direction){
		Vec3 h = Vec3.unit_vector(direction.sub(in));
		//p = D*abs(h.n)
		return Utilities.GGX1(uvw.w(),h,r)*Math.abs(Vec3.dot(h,uvw.w()));
	}

	public Vec3 generate(){
		return uvw.local(random_GGX());
	}

	private Vec3 random_GGX(){
		double r1 = Math.random();
		double r2 = Math.random();
		double phi = 2*Math.PI*r1;
		//see if this can be simplified later to not use atan
		double theta = Math.atan(r*Math.sqrt(r2)/Math.sqrt(1-r2));
		double x = Math.cos(phi) * Math.sin(theta);
		double y = Math.sin(phi) * Math.sin(theta);
		double z = Math.cos(theta);
		return new Vec3(x, y, z);
	}
}

//generate rays on hemisphere uniformly
class ConstantHemispherePDF extends PDF{
	Vec3 normal;
	public ConstantHemispherePDF(Vec3 n){
		normal = n;
	}

	public double value(Vec3 direction){
		return 0.5/Math.PI;
	}

	public Vec3 generate(){
		Vec3 on_h;
		do{
			on_h = Utilities.random_on_unit_sphere();
		} while (Vec3.dot(normal,on_h) < 0);
		return on_h;
	}
}

//generate rays in all directions uniformly
class IsotropicPDF extends PDF{
	public double value(Vec3 direction){
		return 0.25/Math.PI;
	}

	public Vec3 generate(){
		return Utilities.random_on_unit_sphere();
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