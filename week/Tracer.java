import java.awt.*;
import java.awt.image.BufferedImage;

public class Tracer{
	public static void main(String[] args){
		int nx = 600;
		int ny = 400;
		int ns = 50;
		DrawingPanel d = new DrawingPanel(nx,ny);
		Graphics gr = d.getGraphics();
		BufferedImage img = new BufferedImage(nx,ny,BufferedImage.TYPE_INT_ARGB);
		
		HitableList world = two_spheres();
		Vec3 lookfrom = new Vec3(13,2,3);
		Vec3 lookat = new Vec3(0,0,0);
		double dist_to_focus = lookfrom.sub(lookat).length(); //focus at end point
		double aperture = 7.1;
		Camera cam = new Camera(lookfrom, lookat, new Vec3(0,1,0), 40, (double)(nx)/ny, aperture, dist_to_focus, 0, 1);
		
		for(int j = 0; j<ny; j++){
			System.out.println("Row: " + j);
			for(int i = 0; i<nx; i++){
				Vec3 col = new Vec3(0,0,0);
				for(int s =0; s<ns; s++){ //anti-aliasing
					double u = (i+Math.random())/nx;
					double v = (j+Math.random())/ny;
					Ray r = cam.get_ray(u,v);
					//Vec3 p = r.point_at_parameter(2.0);
					col = col.add(color(r,world,0));
				}
				col = col.div(ns);
				col = new Vec3(Math.sqrt(col.e[0]),Math.sqrt(col.e[1]),Math.sqrt(col.e[2])); //gamma
				Color c = new Color((int)(255*col.r()),(int)(255*col.g()),(int)(255*col.b()));
				img.setRGB(i,ny-j-1,c.getRGB());
			}
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
			if(depth < 5 && rec.mat.scatter(r,rec,attenuation,scattered)){ //if we haven't recursed beyond max depth and there is an impact
				return color(scattered,world,depth+1).mul(attenuation); //attenuate and recurse
			} else {
				return new Vec3(0,0,0); //if too deep, set to black (no light is going to be able to reflect that far)
			}
		} else { //background acts a large light source
			Vec3 unit_dir = Vec3.unit_vector(r.direction());
			double t = 0.5*(unit_dir.y() + 1.0);
			return new Vec3(1.0,1.0,1.0).mul(1.0-t).add(new Vec3(0.5,0.7,1.0).mul(t)); //create a gradient
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
		list[i++] = new Sphere(new Vec3(-4,1,0),1.0, new Lambertian(new ConstantTexture(new Vec3(0.4,0.2,0.1))));
		list[i++] = new Sphere(new Vec3(4,1,0),1.0, new Metal(new Vec3(0.7,0.6,0.5), 0.1));

		return new HitableList(list,i);
	}

	static HitableList two_spheres(){
		//Texture checker = new CheckerTexture(new ConstantTexture(new Vec3(0.2,0.3,0.1)), new ConstantTexture(new Vec3(0.9,0.9,0.9)));
		Texture pertex = new NoiseTexture();
		Hitable[] list = new Hitable[2];
		list[0] = new Sphere(new Vec3(0,-1000,0),1000, new Lambertian(pertex));
		list[1] = new Sphere(new Vec3(0,2,0),2, new Lambertian(pertex));
		return new HitableList(list,2);
	}
}