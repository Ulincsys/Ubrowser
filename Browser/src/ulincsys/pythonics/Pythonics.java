package ulincsys.pythonics;

import java.util.ArrayList;

import ulincsys.pythonics.io.IOFormatter;

/** Contains various static methods to replace common Java syntax with Pythonic syntax
 *  @author Ulincsys
 */
public class Pythonics {
	
	/** Prints given Objects to standard out, or to a specified file.
	 * Can print any number of objects of any type,
	 * and will attempt to print string representations of non-string Objects.
	 * Will search for format specifiers in up-to the last 5 indices of the given Objects.
	 * 
	 * @param Ulincsys_IOFormatter Primary parameter can be a Ulincsys.IOFormatter Object.
	 * By default, a new IOFormatter is created with every call to print, but
	 * print will use a given IOFormatter if it is the first parameter.
	 * If you pass an IOFormatter into the print function and also pass in format options,
	 * they will be ignored.
	 * @param out An auto-generated Object array of parameters.
	 * 
	 * @apiNote Will <b>not</b> print null Objects.
	 */
	public static IOFormatter print(Object... out) {
		IOFormatter format;
		try {
			if(out[0] instanceof IOFormatter) {
				format = (IOFormatter)out[0];
				out[0] = null;
			} else {
				throw new Exception();
			}
		} catch(Exception e) {
			format = new IOFormatter(out);
			out = format.getOutBuffer();
		}
		
		if(out.length > 0) {
			for(int i = 0; i < out.length; ++i) {
				if(out[i] != null) {
					format.write(Str(out[i]));
					if(i + 1 < out.length && out[i + 1] != null) {
						format.write((i + 1 < out.length) ? format.getSeparator() : "");
					}
				}
			}
		}
		format.write(format.getEnd());
		return format;
	}
	
	// used to wrap an array in an IOFormatter
	private static void printArray(Object[] out, IOFormatter format) {
		for(Object item : out) {
			print(format, item);
		}
	}
	
	/** Reads the next available line from a give input source, or System.in.
	 * Prints given Objects to standard out, NOT followed by a newline.
	 * Can print any number of objects of any type, and will attempt to
	 * read the next line from standard in or a specified file after doing so.
	 * Will search for format specifiers in up-to the last 5 indices of the given Objects.
	 * @apiNote To specify a file to read in from, add "file=filename" to the end of your parameters.
	 * @apiNote By default, the IOFormatter used for input is set to "append=true".
	 * If you specify "append=false", the contents of your input file will be erased.
	 * @param Ulincsys_IOFormatter Primary parameter can be a Ulincsys.IOFormatter Object.
	 * By default, a new IO formatter is created with every call to input, but
	 * input will use a given IOFormatter if it is the first parameter.
	 * If you pass an IOFormatter into the input function and also pass in format options,
	 * they will be ignored.
	 * @param out An auto-generated Object array of parameters.*/
	public static String input(Object... out) {
		IOFormatter format, reprint = new IOFormatter("", " ", null, true, true);
		try {
			format = (IOFormatter)out[0];
			out[0] = null;
		} catch(Exception e) {
			format = new IOFormatter(out);
			out = format.getOutBuffer();
		}
		
		printArray(out, reprint);
		String data = format.read();
		
		return data;
	}
	
	public static ArrayList<String> getLines(String filename, boolean notStandard) {
		IOFormatter format = new IOFormatter();
		format.setFilename(filename);
		
		if(format.isStandard() && notStandard) {
			return null;
		}
		
		return format.readLines();
	}
	
	public static ArrayList<String> getLines(String filename) {
		return getLines(filename, false);
	}
	
	/** Generic subclass cast function.
	 * This function takes an Object which has been wrapped (or "caught") in a superclass type, 
	 * and casts it to its original subclass type. </br></br>
	 * For instance:</br>
	 * cast(new Object()) returns type Object</br>
	 * cast(new Long()) returns type Long</br></br>
	 * The function return is not checked, so the user is responsible for ensuring 
	 * that they are using the appropriate receiving type.
	 * 
	 * @param e The object to subclass cast.
	 * 
	 * @apiNote Any attempt to cast a null Object will return null
	 */
	@SuppressWarnings("unchecked")
	public static <Any> Any cast(Object e) {
		return (e != null) ? (Any)e.getClass().cast(e) : null;
	}
	
	/** Determines the comparative equality of two Objects. 
	 * Uses three methods to determine equality:</br>
	 * - Comparing the Objects' respective identity hash codes.</br>
	 * - Comparing the Objects using each Object's equals() method</br>
	 * - Comparing the Objects directly using the == operator and cast()</br>
	 * 
	 * @param a The first Object to compare.
	 * @param b The second Object to compare.
	 */
	public static boolean isEqual(Object a, Object b) {
		return ((System.identityHashCode(a) == System.identityHashCode(b)) || 
				((a == null) ? b == null : a.equals(b)) || 
				((b == null) ? a == null : b.equals(a)) ||
				(cast(b) == cast(a))); 
	}
	
	/** Generates an Identity String used to identify the relative equality of Objects. 
	 * This String consists of the Object's Classname and toString() implementation.*/
	public static String identityString(Object data) {
		return (data == null) ? "null" :
				new StringBuilder().append(data.getClass()).append("::").append(data.toString()).toString();
	}
	
	/** Accepts any Object, and returns its toString() implementation.
	 * Will return "null" for any null Object, or any Object for which toString() throws an error
	 */
	public static String Str(Object parse) {
		try {
			return parse.toString();
		} catch(Exception e) {
			return "null";
		}
	}
	
	/** Accepts any Object, and returns the Integer parse equivalent.
	 * Will throw NumberFormatException if given Object cannot be cast to an Integer.
	 * @apiNote Utilizes the Integer.parseInt() function, and as such cannot
	 * accept any floating-point type representation. To cast from String(float)
	 * to Integer, use tryInt().
	 */
	public static Integer Int(Object parse) {
		return Integer.parseInt(Str(parse));
	}
	
	/** Accepts any Object, and returns the Double parse equivalent.
	 * Will throw NumberFormatException if given Object cannot be cast to a Double.
	 */
	public static Double Double(Object parse) {
		return Double.parseDouble(Str(parse));
	}
	
	/** Accepts any Object, and returns the Integer parse equivalent.
	 * Will return null if given Object cannot be cast to an Integer.
	 * @apiNote Will attempt to cast from String(float) representation
	 * to Integer.
	 */
	public static Integer tryInt(Object parse) {
		try {
			return Int(parse);
		} catch(NumberFormatException e) {
			try {
				return (int)((double)Double(parse));
			} catch (Exception e1) {
				return null;
			}
		} catch(Exception en) {
			return null;
		}
	}
	
	/** Accepts any Object, and returns its truthiness.
	 * Returns true if Object is truthy (containing "true, yes, y or 1"),
	 * or else returns false if the Object is falsy or null.
	 */
	public static Boolean Bool(Object parse) {
		try {
			String truthy = Str(parse).toLowerCase();
			if(truthy.contains("true") ||
					truthy.contains("yes") ||
					truthy.contains("y") ||
					truthy.contains("1")) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
}
	
	
	
	
	
	
	
	
	
	
	
	
	
