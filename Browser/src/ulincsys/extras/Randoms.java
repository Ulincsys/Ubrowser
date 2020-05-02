package ulincsys.extras;

import java.util.concurrent.ThreadLocalRandom;

public class Randoms {
	/** Returns a random integer value between INT_MIN and INT_MAX. O(1)
	 * @see randInt(min, max).
	 */
	public static int randInt() {
		return randInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	
	/** Returns a random Integer with a range from 
	 * and including min, to and excluding max. O(1)
	 * @see ThreadLocalRandom.current().nextInt(min, max)
	 * @param min Integer representing the inclusive minimum boundary
	 * @param max Integer representing the exclusive maximum boundary
	 */
	public static int randInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max);
	}
	
	/** Returns a random Long with a range from 
	 * and including min, to and excluding max. O(1)
	 * @see ThreadLocalRandom.current().nextLong(min, max)
	 * @param min Long representing the inclusive minimum boundary
	 * @param max Long representing the exclusive maximum boundary
	 */
	public static Long randLong(long min, long max) {
		return ThreadLocalRandom.current().nextLong(min, max);
	}
	
	/** Returns a random Long value between LONG_MIN and LONG_MAX. O(1)
	 * @see randLong(min, max).
	 */
	public static Long randLong() {
		return randLong(Long.MIN_VALUE, Long.MAX_VALUE);
	}
	
	/** Returns a random double value between DOUBLE_MIN and DOUBLE_MAX, 
	 * @see randFloat(min, max).
	 */
	public static double randFloat() {
		return randFloat(Double.MIN_VALUE, Double.MAX_VALUE);
	}
	
	/** Returns a random Double with a range from 
	 * and including min, to and excluding max. O(1)
	 * @see ThreadLocalRandom.current().nextDouble(min, max);
	 * @param min Double representing the inclusive minimum boundary
	 * @param max Double representing the exclusive maximum boundary
	 */
	public static double randFloat(double min, double max) {
		return ThreadLocalRandom.current().nextDouble(min, max);
	}
	
	/** Returns a random character generated using randInt(), with a
	 * range from and including 32, to and excluding 127. O(1)
	 * @apiNote This function returns an ASCII character between
	 * #32 (space) and #126 (~).
	 * @see randChar(min, max)
	 */
	public static char randChar() {
		return randChar(32, 127);
	}
	
	/** Returns a random character generated using randInt(), with a
	 * range from and including min, to and excluding max. O(1)
	 * @see randInt(min, max)
	 * @param min Integer representing the inclusive minimum boundary
	 * @param max Integer representing the exclusive maximum boundary
	 */
	public static char randChar(int min, int max) {
		return (char)randInt(min, max);
	}
	
	/** Returns a random character between A and z. O(1)
	 * Uses a random boolean value to determine whether result is lowercase or uppercase.
	 * @see randBool()
	 * @see randAlpha(AlphaCase)
	 * @see AlphaCase
	 */
	public static char randAlpha() {
		return randAlpha(AlphaCase.MIXED_CASE);
	}
	
	/** Returns a random character between A and z. O(1)
	 * @see randChar(int, int)
	 * @param caseSelect Determines whether the random letter is uppercase or lowercase
	 * @see AlphaCase
	 */
	public static char randAlpha(AlphaCase caseSelect) {
		if(caseSelect == AlphaCase.UPPERCASE) {
			return randChar(65, 91);
		} else if(caseSelect == AlphaCase.LOWERCASE) {
			return randChar(97, 123);
		}
		
		return (randBool()) ? randAlpha(AlphaCase.LOWERCASE) : randAlpha(AlphaCase.UPPERCASE);
	}
	
	/** Returns a random boolean value from ThreadLocalRandom. O(1)
	 * @see ThreadLocalRandom.current().nextBoolean()
	 */
	public static boolean randBool() {
		return ThreadLocalRandom.current().nextBoolean();
	}
	
	/** Returns a randomly generated String of alpha characters of the given size. O(n)
	 * @param size Determines the number of characters in the String.
	 * @see randAlpha()
	 */
	public static String randAlphaString(int size) {
		return randAlphaString(size, AlphaCase.MIXED_CASE);
	}
	
	/** Returns a randomly generated String of alpha characters of the given size. O(n)
	 * @param size Determines the number of characters in the String.
	 * @param caseSelect Determines the case of the letters in the String.
	 * @see randAlpha(AlphaCase)
	 * @see AlphaCase
	 */
	public static String randAlphaString(int size, AlphaCase caseSelect) {
		StringBuilder build = new StringBuilder();
		while(size-- > 0) {
			build.append(randAlpha(caseSelect));
		}
		
		return build.toString();
	}
	
	/** Returns a randomly generated String of characters of the given size. O(n)
	 * @param size Determines the number of characters in the String.
	 * @see randChar()
	 */
	public static String randCharString(int size) {
		StringBuilder build = new StringBuilder();
		while(size-- > 0) {
			build.append(randChar());
		}
		
		return build.toString();
	}
}
