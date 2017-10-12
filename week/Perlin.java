public class Perlin{
	static double[] ranfloat = perlin_generate();
	static int[] perm_x = perlin_generate_perm();
	static int[] perm_y = perlin_generate_perm();
	static int[] perm_z = perlin_generate_perm();

	double noise(Vec3 p){
		double u = p.x() - Math.floor(p.x());
		double v = p.y() - Math.floor(p.y());
		double w = p.z() - Math.floor(p.z());
		//hermite cubic
		u = u*u*(3-2*u);
		v = v*v*(3-2*v);
		w = w*w*(3-2*w);
		int i = (int)Math.floor(p.x());
		int j = (int)Math.floor(p.y());
		int k = (int)Math.floor(p.z());
		double[][][] c = new double[2][2][2];
		for(int di = 0; di<2; di++){
			for(int dj = 0; dj<2; dj++){
				for(int dk = 0; dk<2; dk++){
					c[di][dj][dk] = ranfloat[perm_x[(i+di)&255] ^ perm_y[(j+dj)&255] ^ perm_z[(k+dk)&255]];
				}
			}
		}
		return trilinear_interp(c, u, v, w);
	}

	static double[] perlin_generate(){
		double[] p = new double[256];
		for(int i = 0; i<256; i++){
			p[i] = Math.random();
		}
		return p;
	}

	static void permute(int[] p){ //shuffle
		for(int i = p.length-1; i > 0; i--){
			int target = (int)(Math.random()*(i+1));
			int temp = p[i];
			p[i] = p[target];
			p[target] = temp;
		}
	}

	static int[] perlin_generate_perm(){
		int[] p = new int[256];
		for(int i = 0; i<256; i++){
			p[i] = i;
		}
		permute(p);
		return p;
	}

	static double trilinear_interp(double[][][] c, double u, double v, double w){
		double accum = 0;
		for(int i = 0; i<2; i++){
			for(int j = 0; j<2; j++){
				for(int k = 0; k<2; k++){
					accum += (i*u + (1-i)*(1-u))* //weighted sum of vertex values
							(j*v + (1-j)*(1-v))*
							(k*w + (1-k)*(1-w))*c[i][j][k];
				}
			}
		}
		return accum;
	}
}