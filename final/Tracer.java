import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.stream.*;

/**
* Main renderer
*/
public class Tracer{
	public static final int MAX_DEPTH = 5; //maximum recursion depth
	public static final int nx = 500; //output resolution
	public static final int ny = 500;
	public static final int ns = 20; //samples per pixel

	public static void main(String[] args){
		DrawingPanel d = new DrawingPanel(nx,ny);
		Graphics gr = d.getGraphics();
		BufferedImage img = new BufferedImage(nx,ny,BufferedImage.TYPE_INT_ARGB);

		HittableList world = Scenes.pot2(true);
		Camera cam = Scenes.potCam2(nx,ny);

		//set up objects to bias pdf
		Hittable plight = new XYRect(140, 160, 0, 50, 300, null);
		//Hittable plight = new XZRect(140, 160, 0, 50, 300, null);
		Hittable light_shape = new XZRect(213,343,227,332,554, null);
		Hittable glass_shape = new Sphere(new Vec3(190,90,190),90, null);
		Hittable[] a = new Hittable[2];
		a[0] = light_shape;
		a[1] = glass_shape;
		HittableList hlist = new HittableList(a,2);

		for(int j = 0; j<ny; j++){
			System.out.println("Row: " + j);
			final int jj = j;
			IntStream.range(0, nx).parallel().forEach(i->{ //parallelize
				Vec3 col = new Vec3(0,0,0);
				//Latin hypercube sampling to get more evenly distributed samples
				//See pbrt Chapter 7 available free online
				//Is this implementation inefficient/necessary?
				double delta = 1.0/ns; //width of each cell
				double[] hs = new double[ns];
				double[] vs = new double[ns];
				for(int s = 0; s<ns; s++){ //generate random positions
					hs[s] = (s+Math.random())*delta;
					vs[s] = (s+Math.random())*delta;
				}
				Utilities.permute(hs);
				Utilities.permute(vs);

				for(int s = 0; s<ns; s++){ //multisampling and free anti-aliasing
					double u = (i+hs[s])/nx;
					double v = (jj+vs[s])/ny;
					Ray r = cam.get_ray(u,v);
					col = col.add(color(r,world,plight,0));
				}
				col = col.div(ns);
				if(col.r() > 1) col.e[0] = 1; //clamp outputs due to light sources
				if(col.g() > 1) col.e[1] = 1;
				if(col.b() > 1) col.e[2] = 1;
				col = new Vec3(Math.sqrt(col.e[0]),Math.sqrt(col.e[1]),Math.sqrt(col.e[2])); //gamma
				Color c = new Color((int)(255*col.r()),(int)(255*col.g()),(int)(255*col.b()));
				img.setRGB(i,ny-jj-1,c.getRGB());
			});
			gr.drawImage(img,0,0,null);
		}
		gr.drawImage(img,0,0,null);
	}

	/**
	* Calculates the color of a pixel
	* @param r the ray that needs to hit the materials in the world
	* @param world the list of objects to hit
	* @param depth the current recursion depth
	*/
	static Vec3 color(Ray r, HittableList world, Hittable light_shape, int depth){
		HitRecord rec = new HitRecord();
		if(world.hit(r,0.001,Double.MAX_VALUE,rec)){ //intersect with list of objects
			ScatterRecord srec = new ScatterRecord();
			Vec3 emitted = rec.mat.emitted(r, rec, rec.u, rec.v, rec.p);
			//if we haven't recursed beyond max depth and there is an impact
			if(depth < MAX_DEPTH && rec.mat.scatter(r, rec, srec)){
				if(srec.is_specular){
					return srec.attenuation.mul(color(srec.specular_ray, world, light_shape, depth+1));
				} else {
					HittablePDF plight = new HittablePDF(light_shape, rec.p);
					MixturePDF p = new MixturePDF(plight, srec.pdf);

					Ray scattered = new Ray(rec.p, p.generate(), r.time());
					double pdfv = p.value(scattered.direction());
					//rendering equation
					return emitted.add(
						srec.attenuation.mul(rec.mat.scatteringPDF(r, rec, scattered)).mul(color(scattered, world, light_shape, depth+1)).div(pdfv));
				}
			} else {
				return emitted;
			}
		} else {
			//background acts a large light source
			Vec3 unit_dir = Vec3.unit_vector(r.direction());
			double t = 0.5*(unit_dir.y() + 1.0);
			return new Vec3(1.0,1.0,1.0).mul(1.0-t).add(new Vec3(0.5,0.7,1.0).mul(t)); //create a gradient
			//or all black
			//return new Vec3(0,0,0);
		}
	}
}