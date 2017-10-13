import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public abstract class Material{
	public abstract boolean scatter(Ray r_in, HitRecord rec, Vec3 attentuation, Ray scattered);
	public Vec3 emitted(double u, double v, Vec3 p){
		return new Vec3(0,0,0);
	}
}

class Lambertian extends Material{
	Texture albedo;
	public Lambertian(Texture a){
		albedo = a;
	}
	public boolean scatter(Ray r_in, HitRecord rec, Vec3 attentuation, Ray scattered){
		Vec3 target = rec.p.add(rec.normal).add(Utilities.random_in_unit_sphere()); //random scatter
		scattered.set(new Ray(rec.p, target.sub(rec.p), r_in.time()));
		attentuation.set(albedo.value(rec.u, rec.v, rec.p));
		return true;
	}
}

class Metal extends Material{
	Vec3 albedo; //color
	double fuzz; //amount of random scattering
	public Metal(Vec3 a, double f){
		albedo = a;
		if(f<1){
			fuzz = f;
		} else {
			fuzz = 1;
		}
	}
	public boolean scatter(Ray r_in, HitRecord rec, Vec3 attentuation, Ray scattered){
		Vec3 reflected = Utilities.reflect(Vec3.unit_vector(r_in.direction()),rec.normal);
		scattered.set(new Ray(rec.p, reflected.add(Utilities.random_in_unit_sphere().mul(fuzz))));
		attentuation.set(albedo);
		return (Vec3.dot(scattered.direction(),rec.normal) > 0);
	}
}

class Dielectric extends Material{
	Vec3 transparency;
	double ref_idx; //index of refraction
	public Dielectric(Vec3 a, double ri) {
		transparency = a;
		ref_idx = ri;
	}

	public boolean scatter(Ray r_in, HitRecord rec, Vec3 attentuation, Ray scattered){
		Vec3 outward_normal;
		Vec3 reflected = Utilities.reflect(r_in.direction(), rec.normal);
		double ni_over_nt;
		attentuation.set(transparency); //not accurate, isn't a function of path length
		Vec3 refracted = new Vec3();
		double cosine;
		double reflect_prob;
		if(Vec3.dot(r_in.direction(), rec.normal) > 0){ //if within hemisphere of normal
			outward_normal = rec.normal.mul(-1); //flip normal inwards
			ni_over_nt = ref_idx; 
			//cosine = ref_idx*Vec3.dot(r_in.direction(), rec.normal)/r_in.direction().length(); //cosine of angle * ref_idx?
			cosine = Vec3.dot(r_in.direction(), rec.normal)/r_in.direction().length();
			cosine = Math.sqrt(1 - ref_idx*ref_idx*(1-cosine*cosine));
		} else { //we are inside the object
			outward_normal = rec.normal;
			ni_over_nt = 1.0/ref_idx; //we are exiting the medium, so flip
			cosine = -1*Vec3.dot(r_in.direction(), rec.normal)/r_in.direction().length(); //cosine of angle * ref_idx
		}
		//only sends out refraction or reflection ray, never both
		if(Utilities.refract(r_in.direction(), outward_normal, ni_over_nt, refracted)){
			reflect_prob = Utilities.schlick(cosine, ref_idx); //using fresnel approximation
		} else {
			reflect_prob = 1;
		}
		if(Math.random() < reflect_prob){ //multiple samples will approximate proportion of rays reflected
			scattered.set(new Ray(rec.p, reflected)); //sends out reflected ray
		} else {
			scattered.set(new Ray(rec.p, refracted)); //sends out refracted ray
		}
		return true;
	}
}

class Emitter extends Material{
	Vec3 light;
	public Emitter(Vec3 a){
		light = a;
	}
	public boolean scatter(Ray r_in, HitRecord rec, Vec3 attentuation, Ray scattered){
		attentuation.set(light);
		scattered.set(new Ray(rec.p, new Vec3(0,0,0)));
		return true;
	}
}

class DiffuseLight extends Material{
	Texture emit;

	public DiffuseLight(Texture a){
		emit = a;
	}
	public boolean scatter(Ray r_in, HitRecord rec, Vec3 attentuation, Ray scattered){
		return false;
	}
	public Vec3 emitted(double u, double v, Vec3 p){
		return emit.value(u,v,p);
	}
}

class Isotropic extends Material{
	Texture albedo;
	public Isotropic(Texture a){
		albedo = a;
	}
	public boolean scatter(Ray r_in, HitRecord rec, Vec3 attentuation, Ray scattered){
		scattered.set(new Ray(rec.p, Utilities.random_in_unit_sphere()));
		attentuation.set(albedo.value(rec.u, rec.v, rec.p));
		return true;
	}
}