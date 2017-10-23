/**
* Test scenes for the raytracer
* Scenes that have the accel parameter determine if the accelerated or nonaccelerated hit structure is used
*/
public class Scenes{
	//generates a random scene of spheres, like the cover of the book
	static HittableList random_scene(){
		int n = 500;
		Hittable[] list = new Hittable[n+1];
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

		return new HittableList(list,i);
	}

	//two sphere test scene
	static HittableList two_spheres(){
		//Texture checker = new CheckerTexture(new ConstantTexture(new Vec3(0.2,0.3,0.1)), new ConstantTexture(new Vec3(0.9,0.9,0.9)));
		Texture pertex = new NoiseTexture(5);
		Hittable[] list = new Hittable[2];
		list[0] = new Sphere(new Vec3(0,-1000,0),1000, new Lambertian(pertex));
		list[1] = new Sphere(new Vec3(0,2,0),2, new Lambertian(pertex));
		return new HittableList(list,2);
	}

	//lighting test scene
	static HittableList simple_light(){
		Texture pertex = new NoiseTexture(4);
		Hittable[] list = new Hittable[4];
		list[0] = new Sphere(new Vec3(0,-1000,0),1000, new Lambertian(pertex));
		list[1] = new Sphere(new Vec3(0,2,0),2, new Lambertian(pertex));
		list[2] = new Sphere(new Vec3(0,7,0),2, new DiffuseLight(new ConstantTexture(new Vec3(4,4,4))));
		list[3] = new XYRect(3,5,1,3,-2, new DiffuseLight(new ConstantTexture(new Vec3(4,4,4))));
		return new HittableList(list,4);
	}

	//Cornell box
	static HittableList cornell_box(boolean accel){
		Hittable[] list = new Hittable[10];
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
		Hittable b1 = new Translate(new Rotate(new Rotate(new Box(new Vec3(0, 0, 0), new Vec3(165, 165, 165), white), -15, Rotate.Z), -20, Rotate.Y), new Vec3(130, 0, 65));
		Hittable b2 = new Translate(new Rotate(new Rotate(new Box(new Vec3(0, 0, 0), new Vec3(165, 330, 165), white), 15, Rotate.X), 15, Rotate.Y), new Vec3(265, 0, 295));
		list[i++] = b1;
		list[i++] = b2;
		//list[i++] = new Translate(new Rotate(new Box(new Vec3(0, 0, 0), new Vec3(165, 165, 165), white), -18, Rotate.Y), new Vec3(130,0,65));
		//list[i++] = new Translate(new Rotate(new Box(new Vec3(0, 0, 0), new Vec3(165, 330, 165), white), 15, Rotate.Y), new Vec3(265,0,295));
		//list[i++] = new ConstantMedium(b1, 0.01, new ConstantTexture(new Vec3(1,1,1)));
		//list[i++] = new ConstantMedium(b2, 0.01, new ConstantTexture(new Vec3(0,0,0)));
		list[i++] = new Sphere(new Vec3(400,60,70),60.0, new Dielectric(new Vec3(1,1,1),1.5));
		list[i++] = new Sphere(new Vec3(150,350,300),50.0, new Lambertian(new ImageTexture("../textures/PathfinderMap.jpg")));
		
		if(accel){
			return new BVHNode(list, 0, i, 0, 1);
		} else {
			return new HittableList(list,i);
		}
	}

	static Camera cornellCam(int nx, int ny){
		Vec3 lookfrom = new Vec3(278,278,-800);
		Vec3 lookat = new Vec3(278,278,0);
		double dist_to_focus = lookfrom.sub(lookat).length(); //focus at end point
		double aperture = 14;
		return new Camera(lookfrom, lookat, new Vec3(0,1,0), 40, (double)(nx)/ny, aperture, dist_to_focus, 0, 1);
	}

	//triangle debugging scene
	static HittableList triangles(boolean accel){
		Hittable[] list = new Hittable[6];
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
		//return new HittableList(list,i);
		if(accel){
			BVHNode bn = new BVHNode(list,0,i,0,1);
			System.out.println(bn);
			return bn;
		} else {
			return new HittableList(list,i);
		}
	}

	static Camera triCam(int nx, int ny){
		Vec3 lookfrom = new Vec3(-5,0,0);
		Vec3 lookat = new Vec3(0,0,0);
		double dist_to_focus = lookfrom.sub(lookat).length(); //focus at end point
		double aperture = 14;
		return new Camera(lookfrom, lookat, new Vec3(0,1,0), 40, (double)(nx)/ny, aperture, dist_to_focus, 0, 1);
	}

	//renders a teapot
	static HittableList pot(){
		Hittable[] list = new Hittable[10];
		Material porcelain = new Metal(new Vec3(1,1,1), 0.05);
		//Texture floor = new ConstantTexture(new Vec3(0.9,0.9,0.9));
		StlLoad stl = new StlLoad("../objects/teapot.stl", porcelain);
		int i = 0;
		list[i++] = stl.objectBVH();
		list[i++] = new XZRect(-1000, 1000, -1000, 1000, 10, new Metal(new Vec3(0.8,0.8,0.8), 0.05));
		list[i++] = new XZRect(140, 160, 0, 50, 300, new DiffuseLight(new ConstantTexture(new Vec3(4,4,4))));
		return new HittableList(list,i);
	}

	//camera setup for the pot
	static Camera potCam(int nx, int ny){
		Vec3 lookfrom = new Vec3(120,80,200);
		Vec3 lookat = new Vec3(90,40,40);
		double dist_to_focus = lookfrom.sub(lookat).length(); //focus at end point
		double aperture = 128;
		Camera cam = new Camera(lookfrom, lookat, new Vec3(0,1,0), 50, (double)(nx)/ny, aperture, dist_to_focus, 0, 1);
		return cam;
	}

	//renders a magnolia (one of the surface normals is off I think)
	static HittableList magnolia(boolean accel){
		Hittable[] list = new Hittable[1];
		Material light_yellow = new Metal(new Vec3(1,0.99,0.8), 0.1);
		//Texture floor = new ConstantTexture(new Vec3(0.9,0.9,0.9));
		StlLoad stl = new StlLoad("../objects/magnolia.stl", light_yellow);
		int i = 0;
		if(accel){
			list[i++] = stl.objectBVH();
		} else {
			list[i++] = stl.objectHL();
		}
		return new HittableList(list,i);
	}

	//camera setup for the magnolia
	static Camera magCam(int nx, int ny){
		Vec3 lookfrom = new Vec3(10,-120,20);
		Vec3 lookat = new Vec3(0,0,0);
		double dist_to_focus = lookfrom.sub(lookat).length(); //focus at end point
		double aperture = 128;
		Camera cam = new Camera(lookfrom, lookat, new Vec3(0,0,1), 90, (double)(nx)/ny, aperture, dist_to_focus, 0, 1);
		return cam;
	}

	static HittableList poke(boolean accel){
		Hittable[] list = new Hittable[3];
		StlLoad st = new StlLoad("../objects/pokemon.stl", new Lambertian(new ConstantTexture(new Vec3(0.5, 0.5, 0.5))));
		int i = 0;
		if(accel){
			list[i++] = st.objectBVH();
		} else {
			list[i++] = st.objectHL();
		}
		list[i++] = new FlipNormals(new XYRect(-10000, 100, -500, 100, 40, new Metal(new Vec3(0.58,0.82,1), 0)));
		list[i++] = new XYRect(-100, 100, -100, 100, -150, new DiffuseLight(new ConstantTexture(new Vec3(5,5,5))));
		return new HittableList(list,i);
	}

	static Camera pokeCam(int nx, int ny){
		Vec3 lookfrom = new Vec3(20,80,-20);
		Vec3 lookat = new Vec3(0,0,-10);
		double dist_to_focus = lookfrom.sub(lookat).length(); //focus at end point
		double aperture = 128;
		Camera cam = new Camera(lookfrom, lookat, new Vec3(0,0,-1), 90, (double)(nx)/ny, aperture, dist_to_focus, 0, 1);
		return cam;
	}

	static HittableList turner(){
		Hittable[] list = new Hittable[3];
		StlLoad st = new StlLoad("../objects/Turners Cube.stl", new Lambertian(new ConstantTexture(new Vec3(0.5, 0.5, 0.5))));
		int i = 0;
		list[i++] = st.objectBVH();
		//list[i++] = new FlipNormals(new XYRect(-10000, 100, -500, 100, 40, new Metal(new Vec3(0.58,0.82,1), 0)));
		//list[i++] = new XYRect(-100, 100, -100, 100, -150, new DiffuseLight(new ConstantTexture(new Vec3(5,5,5))));
		return new HittableList(list,i);
	}

	static Camera turnerCam(int nx, int ny){
		Vec3 lookfrom = new Vec3(200,150,150);
		Vec3 lookat = new Vec3(0,0,0);
		double dist_to_focus = lookfrom.sub(lookat).length(); //focus at end point
		double aperture = 128;
		Camera cam = new Camera(lookfrom, lookat, new Vec3(0,0,1), 90, (double)(nx)/ny, aperture, dist_to_focus, 0, 1);
		return cam;
	}
}