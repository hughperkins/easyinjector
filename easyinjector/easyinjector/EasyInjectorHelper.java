package easyinjector;

import java.io.PrintWriter;
import java.io.StringWriter;

public class EasyInjectorHelper {
	public static String exceptionToStackTrace( Exception e ) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter( stringWriter );
		e.printStackTrace( printWriter );
		return stringWriter.toString();
	}	   

}
