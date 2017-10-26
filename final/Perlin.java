/**
* Perlin noise generator
*/
public class Perlin{
	static Vec3[] ranvec = perlin_generate();
	static int[] perm_x = perlin_generate_perm();
	static int[] perm_y = perlin_generate_perm();
	static int[] perm_z = perlin_generate_perm();

	static double noise(Vec3 p){
		double u = p.x() - Math.floor(p.x());
		double v = p.y() - Math.floor(p.y());
		double w = p.z() - Math.floor(p.z());
		int i = (int)Math.floor(p.x());
		int j = (int)Math.floor(p.y());
		int k = (int)Math.floor(p.z());
		Vec3[][][] c = new Vec3[2][2][2];
		for(int di = 0; di<2; di++){
			for(int dj = 0; dj<2; dj++){
				for(int dk = 0; dk<2; dk++){
					c[di][dj][dk] = ranvec[perm_x[(i+di)&255] ^ perm_y[(j+dj)&255] ^ perm_z[(k+dk)&255]];
				}
			}
		}
		return perlin_interp(c, u, v, w);
	}

	static Vec3[] perlin_generate(){
		Vec3[] p = new Vec3[256];
		for(int i = 0; i<256; i++){
			p[i] = Vec3.unit_vector(new Vec3(-1 + 2*Math.random(), -1 + 2*Math.random(), -1 + 2*Math.random()));
		}
		return p;
	}

	static int[] perlin_generate_perm(){
		int[] p = new int[256];
		for(int i = 0; i<256; i++){
			p[i] = i;
		}
		Utilities.permute(p);
		return p;
	}

	static double perlin_interp(Vec3[][][] c, double u, double v, double w){
		//hermite cubic
		double uu = u*u*(3-2*u);
		double vv = v*v*(3-2*v);
		double ww = w*w*(3-2*w);
		double accum = 0;
		for(int i = 0; i<2; i++){
			for(int j = 0; j<2; j++){
				for(int k = 0; k<2; k++){
					Vec3 weight_v = new Vec3(u-i, v-j, w-k);
					accum += (i*uu + (1-i)*(1-uu))* //weighted sum of vertex values
							(j*vv + (1-j)*(1-vv))*
							(k*ww + (1-k)*(1-ww))*Vec3.dot(c[i][j][k],weight_v);
				}
			}
		}
		return accum;
	}

	static double turb(Vec3 p){
		return turb(p,7);
	}

	static double turb(Vec3 p, int depth){
		double accum = 0;
		Vec3 temp_p = p;
		double weight = 1;
		for(int i = 0; i<depth; i++){
			accum += weight*noise(temp_p);
			weight *= 0.5;
			temp_p = temp_p.mul(2);
		}
		return Math.abs(accum);
	}
}