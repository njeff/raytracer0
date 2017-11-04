public class Phong extends Material{
	Texture albedo;
	double kd;
	public Phong(Texture a, double k_d){
		albedo = a;
		kd = k_d;
	}

	public double scatteringPDF(Ray r_in, HitRecord rec, Ray scattered){
		double cosine = Vec3.dot(rec.normal, Vec3.unit_vector(scattered.direction()));
		if(cosine < 0) cosine = 0; //if not in hemisphere
		return cosine/Math.PI; //probability of direction follows cosine law
	}

	public boolean scatter(Ray r_in, HitRecord rec, ScatterRecord srec){
		if(Math.random() < kd){
			srec.is_specular = 0;
			srec.attenuation = albedo.value(rec.u, rec.v, rec.p);
			srec.pdf = new CosinePDF(rec.normal);
		} else {
			PDF p = new ConstantHemispherePDF(rec.normal);
			Vec3 reflected = p.generate();
			srec.is_specular = 1;
			srec.specular_ray = new Ray(rec.p, reflected);
			srec.attenuation = new Vec3(Math.pow(Math.max(0,Vec3.dot(rec.normal, reflected)), 50));
		}
		return true;
	}
}