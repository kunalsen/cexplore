package sw.proj.utility;

import java.util.ResourceBundle;

public class PropertyManager {

	public static final String kTomcat = "tomcat";
	
	public static String getPropertyValue(String resource,String key){
		ResourceBundle bundle = ResourceBundle.getBundle(resource);
		return bundle.getString(key);
	}
	
	public static void main(String[] args) {
		System.out.println(PropertyManager.getPropertyValue("sw", "cexplore.path"));
	}
}
