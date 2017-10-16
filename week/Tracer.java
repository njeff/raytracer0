import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.stream.*;

public class Tracer{
	public static void main(String[] args){
		int nx = 500;
		int ny = 500;
		int ns = 10;
		DrawingPanel d = new DrawingPanel(nx,ny);
		Graphics gr = d.getGraphics();
		BufferedImage img = new BufferedImage(nx,ny,BufferedImage.TYPE_INT_ARGB);
		
		//HitableList world = simple_light();
		/*
		HitableList world = cornell_box();
		Vec3 lookfrom = new Vec3(278,278,-800);
		Vec3 lookat = new Vec3(278,278,0);
		double dist_to_focus = lookfrom.sub(lookat).length(); //focus at end point
		double aperture = 14;
		Camera cam = new Camera(lookfrom, lookat, new Vec3(0,1,0), 40, (double)(nx)/ny, aperture, dist_to_focus, 0, 1);
		*/
		HitableList world = pot();
		Vec3 lookfrom = new Vec3(120,80,200);
		Vec3 lookat = new Vec3(80,40,40);
		double dist_to_focus = lookfrom.sub(lookat).length(); //focus at end point
		double aperture = 128;
		Camera cam = new Camera(lookfrom, lookat, new Vec3(0,1,0), 50, (double)(nx)/ny, aperture, dist_to_focus, 0, 1);
		
		for(int j = 0; j<ny; j++){
			System.out.println("Row: " + j);
			final int jj = j;
			IntStream.range(0, nx).parallel().forEach(i->{ //parallelize
			    Vec3 col = new Vec3(0,0,0);
				for(int s = 0; s<ns; s++){ //multisampling and free anti-aliasing
					double u = (i+Math.random())/nx;
					double v = (jj+Math.random())/ny;
					Ray r = cam.get_ray(u,v);
					//Vec3 p = r.point_at_parameter(2.0);
					col = col.add(color(r,world,0));
				}
				col = col.div(ns);
				if(col.r() > 1) col.e[0] = 1; //clamp outputs due to light souces
				if(col.g() > 1) col.e[1] = 1;
				if(col.b() > 1) col.e[2] = 1;
				col = new Vec3(Math.sqrt(col.e[0]),Math.sqrt(col.e[1]),Math.sqrt(col.e[2])); //gamma
				Color c = new Color((int)(255*col.r()),(int)(255*col.g()),(int)(255*col.b()));
				img.setRGB(i,ny-jj-1,c.getRGB());
			});
			/*
			for(int i = 0; i<nx; i++){
				Vec3 col = new Vec3(0,0,0);
				for(int s = 0; s<ns; s++){ //multisampling and free anti-aliasing
					double u = (i+Math.random())/nx;
					double v = (j+Math.random())/ny;
					Ray r = cam.get_ray(u,v);
					//Vec3 p = r.point_at_parameter(2.0);
					col = col.add(color(r,world,0));
				}
				col = col.div(ns);
				if(col.r() > 1) col.e[0] = 1; //clamp outputs due to light souces
				if(col.g() > 1) col.e[1] = 1;
				if(col.b() > 1) col.e[2] = 1;
				col = new Vec3(Math.sqrt(col.e[0]),Math.sqrt(col.e[1]),Math.sqrt(col.e[2])); //gamma
				Color c = new Color((int)(255*col.r()),(int)(255*col.g()),(int)(255*col.b()));
				img.setRGB(i,ny-j-1,c.getRGB());
			}
			*/
			gr.drawImage(img,0,0,null);
		}
		gr.drawImage(img,0,0,null);
	}

	//calculates the color of a pixel
	static Vec3 color(Ray r, HitableList world, int depth){
		HitRecord rec = new HitRecord();
		if(world.hit(r,0.001,Double.MAX_VALUE,rec)){ //intersect with list of objects
			Ray scattered = new Ray();
			Vec3 attenuation = new Vec3();
			Vec3 emitted = rec.mat.emitted(rec.u, rec.v, rec.p);
			if(depth < 5 && rec.mat.scatter(r,rec,attenuation,scattered)){ //if we haven't recursed beyond max depth and there is an impact
				return color(scattered,world,depth+1).mul(attenuation).add(emitted); //attenuate and recurse
			} else {
				return emitted;
			}
		} else { //background acts a large light source
			Vec3 unit_dir = Vec3.unit_vector(r.direction());
			double t = 0.5*(unit_dir.y() + 1.0);
			return new Vec3(1.0,1.0,1.0).mul(1.0-t).add(new Vec3(0.5,0.7,1.0).mul(t)); //create a gradient
			//return new Vec3(0,0,0);
		}
	}

	//generates a random scene of spheres, like the cover of the book
	static HitableList random_scene(){
		int n = 500;
		Hitable[] list = new Hitable[n+1];
		Texture checker = new CheckerTexture(new ConstantTexture(new Vec3(0.2,0.3,0.1)), new ConstantTexture(new Vec3(0.9,0.9,0.9)));
		list[0] = new Sphere(new Vec3(0,-1000,0),1000, new Lambertian(checker)); //ground
		int i = 1;
		for(int a = -7; a < 7; a++){
			for(int b = -7; b< 7; b++){
				double choose_mat = Math.random();
				Vec3 center = new Vec3(a+0.9*Math.random(),0.2,b+0.9*Math.random());
				if(center.sub(new Vec3(4,0.2,0)).length() > 0.9){
					if(choose_mat < 0.8){
						list[i++] = new MovingSphere(center, center.add(new Vec3(0, 0.5*Math.random(), 0)), //centers to move between 
							0.0, 1.0, //time of movement
							0.2, 
							new Lambertian(new ConstantTexture(new Vec3(Math.random()*Math.random(),Math.random()*Math.random(),Math.random()*Math.random()))));
					} else if(choose_mat < 0.95){
						list[i++] = new Sphere(center, 0.2, 
							new Metal(new Vec3(0.5*(1+Math.random()),0.5*(1+Math.random()),0.5*(1+Math.random())), 0.5*Math.random()));
					} else {
						list[i++] = new Sphere(center, 0.2, 
							new Dielectric(new Vec3(Math.random()/2+0.5,Math.random()/2+0.5,Math.random()/2+0.5), 1.5));
					}
				}
			}
		}

		//three center spheres
		list[i++] = new Sphere(new Vec3(0,1,0),1.0, new Dielectric(new Vec3(0.95,0.95,0.95),1.5));
		//list[i++] = new Sphere(new Vec3(0,1,0),1.0, new TextureSphere("../textures/PathfinderMap.jpg"));
		//list[i++] = new Sphere(new Vec3(-4,1,0),1.0, new Lambertian(new ConstantTexture(new Vec3(0.4,0.2,0.1))));
		list[i++] = new Sphere(new Vec3(-4,1,0),1.0, new Lambertian(new ImageTexture("../textures/PathfinderMap.jpg")));
		list[i++] = new Sphere(new Vec3(4,1,0),1.0, new Metal(new Vec3(0.7,0.6,0.5), 0.1));

		return new HitableList(list,i);
	}

	static HitableList two_spheres(){
		//Texture checker = new CheckerTexture(new ConstantTexture(new Vec3(0.2,0.3,0.1)), new ConstantTexture(new Vec3(0.9,0.9,0.9)));
		Texture pertex = new NoiseTexture(5);
		Hitable[] list = new Hitable[2];
		list[0] = new Sphere(new Vec3(0,-1000,0),1000, new Lambertian(pertex));
		list[1] = new Sphere(new Vec3(0,2,0),2, new Lambertian(pertex));
		return new HitableList(list,2);
	}

	static HitableList simple_light(){
		Texture pertex = new NoiseTexture(4);
		Hitable[] list = new Hitable[4];
		list[0] = new Sphere(new Vec3(0,-1000,0),1000, new Lambertian(pertex));
		list[1] = new Sphere(new Vec3(0,2,0),2, new Lambertian(pertex));
		list[2] = new Sphere(new Vec3(0,7,0),2, new DiffuseLight(new ConstantTexture(new Vec3(4,4,4))));
		list[3] = new XYRect(3,5,1,3,-2, new DiffuseLight(new ConstantTexture(new Vec3(4,4,4))));
		return new HitableList(list,4);
	}

	static HitableList cornell_box(){
		Hitable[] list = new Hitable[10];
		int i = 0;
		Material red = new Lambertian(new ConstantTexture(new Vec3(0.65, 0.05, 0.05)));
		Material white = new Lambertian(new ConstantTexture(new Vec3(0.73, 0.73, 0.73)));
		Material green = new Lambertian(new ConstantTexture(new Vec3(0.12, 0.45, 0.15)));
		Material light = new DiffuseLight(new ConstantTexture(new Vec3(7, 7, 7)));
		//walls and light
		list[i++] = new FlipNormals(new YZRect(0, 555, 0, 555, 555, green));
		list[i++] = new YZRect(0, 555, 0, 555, 0, red);
		list[i++] = new XZRect(113, 443, 127, 432, 554, light);
		list[i++] = new FlipNormals(new XZRect(0, 555, 0, 555, 555, white));
		list[i++] = new XZRect(0, 555, 0, 555, 0, white);
		list[i++] = new FlipNormals(new XYRect(0, 555, 0, 555, 555, white));
		//boxes
		Hitable b1 = new Translate(new Rotate(new Rotate(new Box(new Vec3(0, 0, 0), new Vec3(165, 165, 165), white), -15, Rotate.Z), -20, Rotate.Y), new Vec3(130, 0, 65));
		Hitable b2 = new Translate(new Rotate(new Rotate(new Box(new Vec3(0, 0, 0), new Vec3(165, 330, 165), white), 15, Rotate.X), 15, Rotate.Y), new Vec3(265, 0, 295));
		list[i++] = b1;
		list[i++] = b2;
		//list[i++] = new ConstantMedium(b1, 0.01, new ConstantTexture(new Vec3(1,1,1)));
		//list[i++] = new ConstantMedium(b2, 0.01, new ConstantTexture(new Vec3(0,0,0)));
		list[i++] = new Sphere(new Vec3(400,60,70),60.0, new Dielectric(new Vec3(1,1,1),1.5));
		list[i++] = new Sphere(new Vec3(150,350,300),50.0, new Lambertian(new ImageTexture("../textures/PathfinderMap.jpg")));
		
		//return new BVHNode(list, 0, i, 0, 1);
		return new HitableList(list,i);
	}

	static HitableList triangles(){
		Hitable[] list = new Hitable[6];
		int i = 0;
		Material black = new Lambertian(new ConstantTexture(new Vec3(0.1, 0.1, 0.1)));
		Material grey = new Lambertian(new ConstantTexture(new Vec3(0.5, 0.5, 0.5)));
		Material white = new Lambertian(new ConstantTexture(new Vec3(1, 1, 1)));
		//list[i++] = new XZRect(-20, -20, 20, 20, 0, black);
		list[i++] = new Triangle(new Vec3(0,0,0), new Vec3(0,0,1), new Vec3(0,1,1), black);
		list[i++] = new Triangle(new Vec3(0,0,0), new Vec3(0,1,1), new Vec3(0,1,0), grey);
		
		list[i++] = new Sphere(new Vec3(0,0,0),0.1, white);
		list[i++] = new Sphere(new Vec3(0,0,1),0.1, white);
		list[i++] = new Sphere(new Vec3(0,1,1),0.1, white);
		list[i++] = new Sphere(new Vec3(0,1,0),0.1, white);
		return new HitableList(list,i);
	}

	static HitableList pot(){
		Hitable[] list = new Hitable[10];
		Material porcelain = new Metal(new Vec3(1,1,1), 0.05);
		Texture floor = new NoiseTexture(0.5);
		StlLoad stl = new StlLoad("../objects/teapot.stl", porcelain);
		int i = 0;
		list[i++] = stl.object();
		list[i++] = new XZRect(-50, 200,-100, 100, 0, new Lambertian(floor));
		list[i++] = new XZRect(140, 160, 0, 50, 300, new DiffuseLight(new ConstantTexture(new Vec3(4,4,4))));
		return new HitableList(list,i);
	}
}