package ServerStuff;

import java.net.MalformedURLException;
import java.net.URL;

public class ServletHelper {

	public static boolean validateURL(String url){
		return (url.startsWith("http") ? validateURLHelper(url) : false);
	}
	
	public static boolean validateURLHelper(String link){
		try{
			URL url = new URL(link);
		} catch (MalformedURLException e){
			return false;
		}
		return true;
	}

}
