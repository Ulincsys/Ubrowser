package ulincsys.time;

import java.text.SimpleDateFormat;
import java.util.Date;

import static ulincsys.pythonics.Pythonics.*;

public class Util {
	
	public static String getNow(String format) {
		return ((new SimpleDateFormat(format)).format(new Date(System.currentTimeMillis())));
	}
	
	public static String getNow() {
		return getNow("yyyy-MM-dd 'at' HH:mm:ss z");
	}
	
	public static String getTime() {
		return getNow("HH:mm:ss a");
	}
	
	public static String getDate() {
		return getNow("yyyy-MM-dd");
	}
	
	public static int getDay() {
		return Int(getNow("dd"));
	}
	
	public static int getMonth() {
		return Int(getNow("MM"));
	}
	
	public static int getYear() {
		return Int(getNow("yyyy"));
	}
}
