/**
* Implementation of the Cook-Torrance BRDF
* https://computergraphics.stackexchange.com/questions/4394/path-tracing-the-cook-torrance-brdf
* http://www.codinglabs.net/article_physically_based_rendering_cook_torrance.aspx
* http://simonstechblog.blogspot.com/2011/12/microfacet-brdf.html
* https://www.cs.cornell.edu/~srm/publications/EGSR07-btdf.pdf
* todo: get actually working; adjust roughness via texture
* 
* BSDF = f_reflected + f_transmitted
* BSDF = Kd/PI + DFG/4(wo.n)(wi.n)
*        diffuse      specular
*/

class CookTorrance extends Material{
	double rough, metallic, ior;
	Vec3 albedo;

	public CookTorrance(double roughness, double metal, double iOr, Vec3 alb){
		roughness += 0.0001;
		if(roughness > 1){
			rough = 1;
		} else {
			rough = roughness;
		}
		metallic = metal;
		albedo = alb;
		ior = iOr;
	}

	public double scatteringPDF(Ray r_in, HitRecord rec, Ray scattered){
		double cosine = Vec3.dot(rec.normal, Vec3.unit_vector(scattered.direction()));
		if(cosine < 0) cosine = 0; //if not in hemisphere, 0 out
		return cosine/Math.PI;
	}

	public boolean scatter(Ray r_in, HitRecord rec, ScatterRecord srec){
		Vec3 wo = Vec3.unit_vector(r_in.direction().mul(-1)); //light out (towards eye)
		PDF p = new GGXPDF(rec.normal, r_in.direction(), rough);

		srec.specular_ray = new Ray(rec.p, p.generate());
		Vec3 wi = Vec3.unit_vector(srec.specular_ray.direction());
		
		double cosine = Vec3.dot(wi,rec.normal);
		Vec3 h = Vec3.unit_vector(wi.add(wo));

		Vec3 f0 = new Vec3((1-ior)/(1+ior));
		f0 = f0.mul(f0);
		f0 = Utilities.lerp(f0, albedo, metallic); //if metallic tint with albedo

		double ref_prob = Utilities.schlick(cosine, ior);
		Vec3 f = Utilities.schlick2(cosine, f0);

		// srec.attenuation = f.mul(Utilities.GGX1(rec.normal, h, rough)*geometryGGX(rec.normal, h, wi, wo) //specular
		// 	/(4*Math.abs(Vec3.dot(rec.normal, wo))*p.value(srec.specular_ray.direction())));
		// srec.is_specular = 1;
		// srec.pdf = null;

		//FDG/(4*nwo*nwi*pdf) * nwi
		srec.attenuation = f.mul(Utilities.GGX1(rec.normal, h, rough)*geometryGGX(rec.normal, h, wi, wo) //specular
			/(4*Math.abs(Vec3.dot(rec.normal, wo))*Math.abs(Vec3.dot(rec.normal, wi))*p.value(srec.specular_ray.direction())))
			.add(new Vec3(1,1,1).sub(f).mul(albedo).mul(1/Math.PI)) //diffuse
			.mul(clamp(Vec3.dot(rec.normal, wi))); //cos
		srec.is_specular = 1;
		srec.pdf = null;
		return true;
	}

	private double clamp(double v){
		return v > 0 ? v : 0;
	}

	//cook-torrance min geometry
	private double geometry(Vec3 n, Vec3 h, Vec3 wi, Vec3 wo){
		double n_wi = Vec3.dot(n, wi);
		double n_wo = Vec3.dot(n, wo);
		//coefficient = 2(n.h)/(v.h)
		double coefficient = 2*Vec3.dot(n,h)/Vec3.dot(wo,h);
		return Math.min(1,Math.min(coefficient*n_wo,coefficient*n_wi));
	}

	//GGX geometry
	private double geometryGGX(Vec3 n, Vec3 h, Vec3 wi, Vec3 wo){
		double woH2 = Vec3.dot(wo,h);
		double chi = Utilities.chiGGX(woH2/Vec3.dot(wo,n));
		woH2 = woH2*woH2;
		double tan2 = (1-woH2)/woH2;
		return (chi*2)/(1+Math.sqrt(1+rough*rough*tan2));
	}
}

//test material to only see the specular component of cook torrance
//https://computergraphics.stackexchange.com/questions/4394/path-tracing-the-cook-torrance-brdf
class CookTorranceSpecular extends Material{
	double rough, metallic, ior;
	Vec3 albedo, spec_color;
	//roughness cannot be 0, otherwise the G term will degnerate to dirac delta (when half vector cosine is 1)
	public CookTorranceSpecular(double roughness, double metal, double iOr, Vec3 alb, Vec3 specular_color){
		roughness += 0.0001;
		if(roughness > 1){
			rough = 1;
		} else {
			rough = roughness;
		}
		metallic = metal;
		albedo = alb;
		spec_color = specular_color;
		ior = iOr;
	}

	public boolean scatter(Ray r_in, HitRecord rec, ScatterRecord srec){
		Vec3 wo = Vec3.unit_vector(r_in.direction().mul(-1)); //light out (towards eye)
		PDF p = new GGXPDF(rec.normal, r_in.direction(), rough);

		srec.specular_ray = new Ray(rec.p, p.generate());
		Vec3 wi = Vec3.unit_vector(srec.specular_ray.direction());
		
		double cosine = Vec3.dot(wi,rec.normal);
		Vec3 h = Vec3.unit_vector(wi.add(wo));

		Vec3 f0 = new Vec3((1-ior)/(1+ior));
		f0 = f0.mul(f0);
		f0 = Utilities.lerp(f0, spec_color, metallic);

		Vec3 f = Utilities.schlick2(cosine, f0);

		//FDG/(4*nwo*nwi*pdf) * nwi
		srec.attenuation = f.mul(Utilities.GGX1(rec.normal, h, rough)*geometryGGX(rec.normal, h, wi, wo)*clamp(Vec3.dot(rec.normal,wi)) //specular
			/(4*Math.abs(Vec3.dot(rec.normal, wo))*Math.abs(Vec3.dot(rec.normal, wi))*p.value(srec.specular_ray.direction())));
		srec.is_specular = 1;
		srec.pdf = null; //no distribution function
		return true;
	}

	private double clamp(double v){
		return v > 0 ? v : 0;
	}

	private double beckman(Vec3 n, Vec3 h, double roughness){
		double nh2 = Vec3.dot(n,h);
		nh2 = nh2*nh2;
		double r2 = roughness*roughness;
		return Math.exp((nh2-1)/(r2*nh2))/(Math.PI*r2*nh2*nh2);
	}


	//cook-torrance min geometry
	private double geometry(Vec3 n, Vec3 h, Vec3 wi, Vec3 wo){
		double n_wi = Vec3.dot(n, wi);
		double n_wo = Vec3.dot(n, wo);
		//coefficient = 2(n.h)/(v.h)
		double coefficient = 2*Vec3.dot(n,h)/Vec3.dot(wo,h);
		return Math.min(1,Math.min(coefficient*n_wo,coefficient*n_wi));
	}

	//GGX geometry
	private double geometryGGX(Vec3 n, Vec3 h, Vec3 wi, Vec3 wo){
		double woH2 = Vec3.dot(wo,h);
		double chi = Utilities.chiGGX(woH2/Vec3.dot(wo,n));
		woH2 = woH2*woH2;
		double tan2 = (1-woH2)/woH2;
		return (chi*2)/(1+Math.sqrt(1+rough*rough*tan2));
	}
}