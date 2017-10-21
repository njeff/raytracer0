import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.*;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;

/**
* Simple STL file loader
* Can load binary and ASCII files
*/
public class StlLoad{
	ArrayList<Hittable> list = new ArrayList<Hittable>();

	/**
	* @param path the path to the stl file
	* @param material the material of the final object
	*/
	public StlLoad(String path, Material material){
		try{
			BufferedReader br = new BufferedReader(new FileReader(path));

			if(!br.readLine().toLowerCase().trim().startsWith("solid")){ //is binary
				Path fileLocation = Paths.get(path);
				byte[] data = Files.readAllBytes(fileLocation);
				DataInputStream din = new DataInputStream(new ByteArrayInputStream(data));
				din.skipBytes(80);//skip first 80 bytes of header

				int numt = Integer.reverseBytes(din.readInt()); //get number of triangles
				Vec3[] vertices = new Vec3[3];

				//for all the triangles
				for(int i = 0; i <numt; i++){
					double[] components = new double[3];

					//get normal
					for(int j = 0; j<3; j++){
						components[j] = Float.intBitsToFloat(Integer.reverseBytes(din.readInt()));
					}
					Vec3 normal = new Vec3(components[0],components[1],components[2]);

					//get vertices
					for(int j = 0; j<3; j++){
						for(int k = 0; k<3; k++){
							components[k] = Float.intBitsToFloat(Integer.reverseBytes(din.readInt()));
						}
						vertices[j] = new Vec3(components[0],components[1],components[2]);
					}
					din.skipBytes(2); //skip attributes
					Triangle t = new Triangle(vertices[0],vertices[1],vertices[2],material);
					list.add(t);
				}
				
			} else { //is ASCII
				String line;
				int triangle = -1;
				Vec3[] vertices = new Vec3[3];
				while((line = br.readLine())!=null){
					String[] e = line.trim().split("\\s+");
					if(triangle != -1){
						vertices[triangle] = new Vec3(Double.parseDouble(e[1]),Double.parseDouble(e[2]),Double.parseDouble(e[3]));
						triangle--;
					}
					if(e[0].equals("outer")){
						triangle = 2;
					}
					if(e[0].equals("endloop")){
						list.add(new Triangle(vertices[2],vertices[1],vertices[0],material));
					}
				}
			}
			
			System.out.println(list.size() + " triangles loaded.");
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public HittableList objectHL(){
		return new HittableList(list.toArray(new Hittable[list.size()]),list.size());
	}

	public HittableList objectBVH(){
		return new BVHNode(list.toArray(new Hittable[list.size()]),0,list.size(),0,1);
	}
}