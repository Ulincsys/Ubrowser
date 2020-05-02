package ulincsys.extras;

import static ulincsys.extras.Randoms.randInt;
import static ulincsys.pythonics.Pythonics.Str;

/** Class containing many static methods for String decomposition and manipulation.
 * @author Ulincsys
 */
public class Strings {
	
	/** Takes any object and determines whether its Str() equivalent is a palindrome.
	 */
	public static boolean isPalindrome(Object check) {
		String parse = Str(check);
		for(int i = 0, len = (parse.length() - 1); i < len; ++i, --len) {
			if(parse.charAt(i) != parse.charAt(len)) {
				return false;
			}
		}
		return true;
	}
	
	/** Takes any object and returns the reverse of its Str() equivalent.
	 */
	public static String reverse(Object parse) {
		StringBuilder reverse = new StringBuilder(Str(parse));
		return Str(reverse.reverse());
	}
	
	/** Takes any object and returns a randomized version of its Str() equivalent.
	 */
	public static String randomize(Object parse) {
		StringBuilder result = new StringBuilder();
		char[] letters = Str(parse).toCharArray();
		int a, b, count = 3; char temp;
		
		while(0 < count--) {
			for(int i = 0; i < letters.length; ++i) {
				a = randInt(0, letters.length);
				b = randInt(0, letters.length);
				temp = letters[a];
				letters[a] = letters[b];
				letters[b] = temp;
			}
		}
		
		for(char letter : letters) {
			result.append(letter);
		}
		
		return Str(result);
	}
	
	/** Takes any object and returns a stripe cased version of its Str() equivalent.
	 * Stripe cased meaning that every other character is capitalized if it's a letter.
	 */
	public static String stripeCase(String parse) {
		StringBuilder striped = new StringBuilder();
		
		for(int i = 0; i < parse.length(); ++i) {
			if((i % 2) == 0) {
				striped.append(Str(parse.charAt(i)).toUpperCase());
			} else {
				striped.append(parse.charAt(i));
			}
		}
		
		return striped.toString();
	}
}















