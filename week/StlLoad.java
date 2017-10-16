import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;

//simple ASCII STL file loader
public class StlLoad{
	ArrayList<Hitable> list = new ArrayList<Hitable>();
	public StlLoad(String path, Material material){
		try{
			BufferedReader br = new BufferedReader(new FileReader(path));
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
			System.out.println(list.size() + " triangles loaded.");
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public HitableList object(){
		return new HitableList(list.toArray(new Hitable[list.size()]),list.size());
	}
}