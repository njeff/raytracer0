import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class PlyLoad{
	int vertN = 0;
	int triN = 0;
	ArrayList<Vec3> vertices = new ArrayList<Vec3>();
	ArrayList<Hittable> triangles = new ArrayList<Hittable>();

	public PlyLoad(String path, Material mat){
		try{
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line = "";
			boolean header_end = false;
			while((line = br.readLine())!=null){
				String[] e = line.trim().split("\\s+");
				//enter data section
				if(header_end){
					if(vertN > 0){
						vertices.add(new Vec3(Double.parseDouble(e[0]),Double.parseDouble(e[1]),Double.parseDouble(e[2])));
						vertN--;
					} else {
						triangles.add(new Triangle(vertices.get(Integer.parseInt(e[1])), 
							vertices.get(Integer.parseInt(e[2])), 
							vertices.get(Integer.parseInt(e[3])),
							mat));
					}
				} else {
					if(e[0].equals("element")){
						if(e[1].equals("vertex")){
							vertN = Integer.parseInt(e[2]);
						}
						if(e[1].equals("face")){
							triN = Integer.parseInt(e[2]);
						}
					}
				}
				if(e[0].equals("end_header")){
					header_end = true;
				}
			}
			System.out.println(vertices.size() + " vertices loaded.");
			System.out.println(triangles.size() + " triangles loaded.");
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public HittableList objectBVH(){
		return new BVHNode(triangles.toArray(new Hittable[triangles.size()]),0,triangles.size(),0,1);
	}
}