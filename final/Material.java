//list of materials hittable objects can take on
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Material{
	/**
	* @param r_in Ray hitting object
	* @param rec HitRecord to update after hit
	* @param attenuation change in light after hit
	* @param scattered ray returned after hitting this material
	*/
	public boolean scatter(Ray r_in, HitRecord rec, ScatterRecord srec){
		return false;
	}

	public double scatteringPDF(Ray r_in, HitRecord rec, Ray scattered){
		return 0;
	}
	
	/**
	* Used for light sources
	*/
	public Vec3 emitted(Ray r_in, HitRecord rec, double u, double v, Vec3 p){
		return new Vec3(0,0,0);
	}
}

class ScatterRecord{
	Ray specular_ray = new Ray();
	boolean is_specular = false;
	Vec3 attenuation = new Vec3();
	PDF pdf;
}

//ideal diffuse material
class Lambertian extends Material{
	Texture albedo;
	public Lambertian(Texture a){
		albedo = a;
	}

	public double scatteringPDF(Ray r_in, HitRecord rec, Ray scattered){
		double cosine = Vec3.dot(rec.normal, Vec3.unit_vector(scattered.direction()));
		if(cosine < 0) cosine = 0; //if not in hemisphere
		return cosine/Math.PI;
	}

	public boolean scatter(Ray r_in, HitRecord rec, ScatterRecord srec){
		srec.is_specular = false;
		srec.attenuation = albedo.value(rec.u, rec.v, rec.p);
		srec.pdf = new CosinePDF(rec.normal);
		return true;
	}
}

//reflective material
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

	public boolean scatter(Ray r_in, HitRecord rec, ScatterRecord srec){
		Vec3 reflected = Utilities.reflect(Vec3.unit_vector(r_in.direction()),rec.normal);
		srec.specular_ray = new Ray(rec.p, reflected.add(Utilities.random_in_unit_sphere().mul(fuzz)));
		srec.attenuation = albedo;
		srec.is_specular = true;
		srec.pdf = null;
		return true;
	}
}

//shiny diffuse
class Plastic extends Material{
	Vec3 albedo;
	double fuzz, roughness;

	public Plastic(Vec3 a, double f, double r){
		albedo = a;
		if(f<1){
			fuzz = f;
		} else {
			fuzz = 1;
		}
		if(r<1){
			roughness = r;
		} else {
			roughness = 1;
		}
	}

	public double scatteringPDF(Ray r_in, HitRecord rec, Ray scattered){
		double cosine = Vec3.dot(rec.normal, Vec3.unit_vector(scattered.direction()));
		if(cosine < 0) cosine = 0; //if not in hemisphere
		return cosine/Math.PI;
	}

	public boolean scatter(Ray r_in, HitRecord rec, ScatterRecord srec){
		Vec3 reflected = Utilities.reflect(Vec3.unit_vector(r_in.direction()),rec.normal);
		srec.specular_ray = new Ray(rec.p, reflected.add(Utilities.random_in_unit_sphere().mul(fuzz)));
		srec.attenuation = albedo;
		if(Math.random() < roughness){
			srec.is_specular = false;
		} else {
			srec.is_specular = true;
		}
		srec.pdf = new CosinePDF(rec.normal);
		return true;
	}
}

//transparent material
class Dielectric extends Material{
	Vec3 transparency;
	double ref_idx; //index of refraction
	public Dielectric(Vec3 a, double ri) {
		transparency = a;
		ref_idx = ri;
	}

	public boolean scatter(Ray r_in, HitRecord rec, ScatterRecord srec){
		Vec3 outward_normal;
		Vec3 reflected = Utilities.reflect(r_in.direction(), rec.normal);
		double ni_over_nt;
		srec.is_specular = true;
		srec.attenuation = transparency; //not accurate, isn't a function of path length
		srec.pdf = null;
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
			srec.specular_ray = new Ray(rec.p, reflected); //sends out reflected ray
		} else {
			srec.specular_ray = new Ray(rec.p, refracted); //sends out refracted ray
		}
		return true;
	}
}

//book's version of a light source
class DiffuseLight extends Material{
	Texture emit;

	public DiffuseLight(Texture a){
		emit = a;
	}
	public boolean scatter(Ray r_in, HitRecord rec, ScatterRecord srec){
		return false;
	}
	public Vec3 emitted(Ray r_in, HitRecord rec, double u, double v, Vec3 p){
		//incident ray and normal must be in opposite directions
		if(Vec3.dot(rec.normal, r_in.direction()) < 0){
			return emit.value(u,v,p);
		} else {
			return new Vec3(0,0,0);
		}
	}
}

//volumetric material
class Isotropic extends Material{
	Texture albedo;
	public Isotropic(Texture a){
		albedo = a;
	}
	public double scatteringPDF(Ray r_in, HitRecord rec, Ray scattered){
		return 0.25/Math.PI;
	}
	public boolean scatter(Ray r_in, HitRecord rec, ScatterRecord srec){
		//isotropic scatters in all directions
		srec.pdf = new IsotropicPDF();
		srec.is_specular = false;
		srec.specular_ray = new Ray(rec.p, Utilities.random_in_unit_sphere());
		srec.attenuation = albedo.value(rec.u, rec.v, rec.p);
		return true;
	}
}