/**
* Implementation of the Cook-Torrance BRDF
* using GGX
* https://computergraphics.stackexchange.com/questions/4394/path-tracing-the-cook-torrance-brdf
* http://www.codinglabs.net/article_physically_based_rendering_cook_torrance.aspx
* http://simonstechblog.blogspot.com/2011/12/microfacet-brdf.html
* https://www.cs.cornell.edu/~srm/publications/EGSR07-btdf.pdf
*/
public class CookTorrance extends Material{
	Texture albedo;
	double roughness;
	double metallic;
	Vec3 specular_color = new Vec3(1.0);
	Vec3 ks;
	double ior;
	Vec3 F0;

	public CookTorrance(Texture a, double r, double met, double IOR){
		albedo = a;
		//clamp
		r += 0.01;
		if(r < 1){
			roughness = r;
		} else {
			roughness = 1;
		}
		metallic = met;
		ior = IOR;
		Vec3 ks = new Vec3(0);
		Vec3 F0 = new Vec3(0);
	}

	//these must integrate to 1 (integral over hemisphere of BRDF()*COS() = 1)
	//specular part
	public double scatteringPDFS(Ray r_in, HitRecord rec, Ray scattered){
		//negative because of opposite direction
		//we are tracing backwards to wo is view, and wi is light
		Vec3 wo = Vec3.unit_vector(r_in.direction().mul(-1));
		Vec3 wi = Vec3.unit_vector(scattered.direction());
		//halfway vector
		Vec3 h = Vec3.unit_vector(wi.add(wo));

		double n_wo = Vec3.dot(rec.normal, wo);
		if(n_wo < 0) n_wo = 0; //if not in hemisphere clamp
		// DFG/(4*n.wi*n.wo) * n.wi = DFG/(4*n.wo)
		//ks = Utilities.schlick2(Vec3.dot(h,wo), F0);
		return beckman(rec.normal, h, roughness)*geometry(rec.normal, h, wi, wo)/(4*n_wo);
	}

	//diffuse part
	public double scatteringPDF(Ray r_in, HitRecord rec, Ray scattered){
		double cosine = Vec3.dot(rec.normal, Vec3.unit_vector(scattered.direction()));
		if(cosine < 0) cosine = 0;
		ks = new Vec3(0);
		return cosine/Math.PI;
	}

	public boolean scatter(Ray r_in, HitRecord rec, ScatterRecord srec){
		//if(Math.random() < (new Vec3(1)).sub(ks).mul(1-metallic).x()){ //do diffuse
		//	srec.pdf = new CosinePDF(rec.normal); //use default cosine pdf for now
		//	srec.is_specular = 0; //not specular component
		//	srec.attenuation = albedo.value(rec.u, rec.v, rec.p);

		//} else { //do specular
			//srec.pdf = new GGXPDF(rec.normal, r_in.direction(), roughness);
			srec.pdf = new ConstantHemispherePDF(rec.normal);
			srec.is_specular = 2; //do cook torrance

			srec.specular_ray = new Ray(rec.p, srec.pdf.generate()); //generate a ray
			//do the fresnel part here to apply schlick's on all color channels
			Vec3 h = Vec3.unit_vector(srec.specular_ray.direction().sub(r_in.direction()));

			F0 = new Vec3((1-ior)/(1+ior));
			F0 = F0.mul(F0);
			F0 = Utilities.lerp(F0, specular_color, metallic);
			srec.attenuation = Utilities.schlick2(Vec3.dot(rec.normal,h),F0);
		//}
		return true;
	}

	private double geometry(Vec3 n, Vec3 h, Vec3 wi, Vec3 wo){
		double n_wi = Vec3.dot(n, wi);
		double n_wo = Vec3.dot(n, wo);
		//coefficient = 2(n.h)/(v.h)
		double coefficient = 2*Vec3.dot(n,h)/Vec3.dot(wo,h);
		return Math.min(1,Math.min(coefficient*n_wo,coefficient*n_wi));
	}

	private double beckman(Vec3 n, Vec3 h, double roughness){
		double nh2 = Vec3.dot(n,h);
		nh2 = nh2*nh2;
		double r2 = roughness*roughness;
		return Math.exp((nh2-1)/(r2*nh2))/(Math.PI*r2*nh2*nh2);
	}
}

//test material to only see the specular component of cook torrance
//https://computergraphics.stackexchange.com/questions/4394/path-tracing-the-cook-torrance-brdf
class CookTorranceSpecular extends Material{
	double rough, metallic, ior;
	Vec3 spec_color;
	//roughness cannot be 0, otherwise the G term will degnerate to dirac delta (when half vector cosine is 1)
	public CookTorranceSpecular(double roughness, double metal, double iOr, Vec3 specular_color){
		roughness += 0.01;
		if(roughness > 1){
			rough = 1;
		} else {
			rough = roughness;
		}
		metallic = metal;
		spec_color = specular_color;
		ior = iOr;
	}

	public boolean scatter(Ray r_in, HitRecord rec, ScatterRecord srec){
		Vec3 wo = Vec3.unit_vector(r_in.direction().mul(-1));
		
		//PDF p = new ConstantHemispherePDF(rec.normal);
		PDF p = new GGXPDF(rec.normal, r_in.direction(), rough);
		srec.specular_ray = new Ray(rec.p, p.generate()); //create rand on hemisphere
		Vec3 wi = Vec3.unit_vector(srec.specular_ray.direction());
		
		double cosine = Vec3.dot(wi,rec.normal);
		Vec3 h = Vec3.unit_vector(wi.add(wo));

		Vec3 f0 = new Vec3((1-ior)/(1+ior));
		f0 = f0.mul(f0);
		f0 = Utilities.lerp(f0, spec_color, metallic);

		Vec3 f = Utilities.schlick2(cosine, f0);

		//DFG/nwo for beckman sampled evenly
		//srec.attenuation = f.mul(beckman(rec.normal, h, rough)*geometry(rec.normal, h, wi, wo)*(Math.PI/2)/Vec3.dot(rec.normal,wo)); //tint reflection
		
		//DFG/(4*nwo*pdf) for ggx samples with ggxpdf
		srec.attenuation = f.mul(Utilities.GGX1(rec.normal, h, rough)*geometryGGX(rec.normal, h, wi, wo)
			/(4*Vec3.dot(rec.normal, wo)*p.value(srec.specular_ray.direction())));
		srec.is_specular = 1;
		srec.pdf = null; //no distribution function
		return true;
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
		double voH2 = Vec3.dot(wo,h);
		double chi = Utilities.chiGGX(voH2/Vec3.dot(wo,n));
		voH2 = voH2*voH2;
		double tan2 = (1-voH2)/voH2;
		return (chi*2)/(1+Math.sqrt(1+rough*rough*tan2));
	}
}