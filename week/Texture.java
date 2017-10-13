import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public abstract class Texture{
	public abstract Vec3 value(double u, double v, Vec3 p);
}

class ConstantTexture extends Texture{
	Vec3 color;
	public ConstantTexture() {}

	public ConstantTexture(Vec3 c){
		color = c;
	}

	public Vec3 value(double u, double v, Vec3 p){
		return color;
	}
}

class CheckerTexture extends Texture{
	Texture odd, even;

	public CheckerTexture() {}

	public CheckerTexture(Texture t0, Texture t1){
		even = t0;
		odd = t1;
	}

	public Vec3 value(double u, double v, Vec3 p){
		double sines = Math.sin(10*p.x()) * Math.sin(10*p.y()) * Math.sin(10*p.z());
		if (sines < 0){
			return odd.value(u,v,p);
		} else{
			return even.value(u,v,p);
		}
	}
}

class NoiseTexture extends Texture{
	Perlin noise = new Perlin();
	double scale = 1;

	public NoiseTexture() {}

	public NoiseTexture(double sc) {
		scale = sc;
	}

	public Vec3 value(double u, double v, Vec3 p){
		//return (new Vec3(1,1,1)).mul(noise.noise(p.mul(scale)));
		return (new Vec3(1,1,1)).mul(0.5).mul(1+Math.sin(scale*p.z() + 10*noise.turb(p)));
	}
}

class ImageTexture extends Texture{
	BufferedImage img = null;
	int height, width;

	public ImageTexture() {}
	public ImageTexture(String path){
		try {
		    img = ImageIO.read(new File(path));
		    height = img.getHeight();
		    width = img.getWidth();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Vec3 value(double u, double v, Vec3 p){
		int x = (int)(u*width);
		int y = (int)((1-v)*height);
		if(x < 0) x = 0;
		if(y < 0) y = 0;
		if(x > width-1) x = width-1;
		if(y > height-1) y = height-1;
		int rgb = img.getRGB(x, y);
		double red = ((rgb >> 16 ) & 0xFF)/255.0;
		double green = ((rgb >> 8 ) & 0xFF)/255.0;
		double blue = ((rgb) & 0xFF)/255.0;
		return new Vec3(red, green, blue);
	}
}