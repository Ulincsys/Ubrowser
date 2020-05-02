package ulincsys.math;

import java.math.BigInteger;
import java.util.ArrayList;

import ulincsys.pythonics.lists.Plist;
import ulincsys.pythonics.lists.TrianglePlist;

public class Sequences {
	private static Plist consequenceGen = new Plist();
	private static Plist artsequenceGen = new Plist();
	
	/** Generates a sequence of consecutive sums from the given terms, and returns them as a BigInteger ArrayList. 
	 * Returns null if numberOfTerms is 0, does not check for BigInteger overflow.
	 * @param term1 The first term in the consecutive sequence
	 * @param term2 The second term in the consecutive sequence
	 * @param numberOfTerms The number of terms to generate in the sequence
	 * @apiNote If numberOfTerms is 0 or negative, this function will return null
	 */
	public static ArrayList<BigInteger> conSeqGen(BigInteger term1, BigInteger term2, int numberOfTerms) {
		if(numberOfTerms < 1) {
			return consequenceGen.removeType(BigInteger.class);
		} else {
			consequenceGen.add(term1);
			return conSeqGen(term2, term1.add(term2), --numberOfTerms);
		}
	}
	
	/** Generates the fibonacci sequence up to the given number of terms
	 * @param numberOfTerms The number of terms to generate in the sequence
	 * @apiNote If numberOfTerms is 0 or negative, this function will return null
	 * @see conSeqGen(term1, term2, numberOfTerms)
	 */
	public static ArrayList<BigInteger> fib(int numberOfTerms) {
		return conSeqGen(BigInteger.ONE, BigInteger.ONE, numberOfTerms);
	}
	
	/** Generates an arithmetic sequence using the given start term and addend.
	 * @param start The starting term for the sequence
	 * @param add The term to add to the previous term in the sequence
	 * @param numberOfTerms The number of terms to generate in the sequence
	 * @apiNote If numberOfTerms is 0 or negative, this function will return null
	 */
	public static ArrayList<BigInteger> arthSeqGen(BigInteger start, BigInteger add, int numberOfTerms) {
		if(numberOfTerms < 1) {
			return artsequenceGen.removeType(BigInteger.class);
		} else {
			artsequenceGen.add(start);
			return arthSeqGen(start.add(add), add, --numberOfTerms);
		}
	}
	
	/** Generates a sequence of consecutive integers starting with 0. If numberOfTerms is negative, the sequence will 
	 * count backward from 0.
	 * @param numberOfTerms The number of terms to generate in the sequence
	 * @apiNote If numberOfTerms is 0, this function will return null
	 */
	public static ArrayList<BigInteger> integers(int numberOfTerms) {
		return integers(0, numberOfTerms);
	}
	
	/** Generates a sequence of consecutive integers starting with the given term. If numberOfTerms is negative, the sequence will 
	 * count backward from start.
	 * @param start The starting point for the sequence
	 * @param numberOfTerms The number of terms to generate in the sequence
	 * @apiNote If numberOfTerms is 0, this function will return null
	 * @see arthSeqGen(start, add, numberOfTerms)
	 */
	public static ArrayList<BigInteger> integers(int start, int numberOfTerms) {
		if(numberOfTerms > 0) {
			return arthSeqGen(BigInteger.valueOf(start), BigInteger.ONE, numberOfTerms);
		} else if(numberOfTerms < 0) {
			return arthSeqGen(BigInteger.valueOf(start), BigInteger.valueOf(-1), Math.abs(numberOfTerms));
		}
		
		return null;
	}
	
	public static TrianglePlist pascal(int numOfRows) {
		Plist sets = new Plist();
		TrianglePlist result = new TrianglePlist();
		
		for(int line = 1; line <= numOfRows; ++line) {
			BigInteger cursor = BigInteger.ONE;
			
			for(int i = 1; i <= line; ++i) {
				sets.add(cursor);
				cursor = cursor.multiply(BigInteger.valueOf(line - i)).divide(BigInteger.valueOf(i));
			}
			
			result.addRow(sets.reset());
		}
		
		return result;
	}
}



















