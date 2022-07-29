import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 * @author Luca Bosch
 */
public class MillerRabinTest {

	static BigInteger zero = new BigInteger("0");
	static BigInteger one = new BigInteger("1");
	static BigInteger two = new BigInteger("2");
	static BigInteger three = new BigInteger("3");

	/**
	 * @param args array of two numbers (positive number up to 2^63, integer k), or: isPrime, positive number up to 2^63
	 */
	public static void main(String[] args) {
		try {
			if (args.length == 2) {
				if (args[0].equals("isPrime")) {
					
					final BigInteger number = new BigInteger(args[1]);
					System.out.println(isPrime(number));
					
				} else {
					
					int k = Integer.parseInt(args[1]);
					BigInteger number = new BigInteger(args[0]);
					
					if (number.compareTo(one) == 1) {
						
					System.out.println(rabin(decomp(number), k));
					
					} else {
						
						System.out.println("Please enter a number larger than 1");
					}
				}
			}

			if (args.length == 3) {

				BigInteger startingatx = new BigInteger(args[0]);
				int k = Integer.parseInt(args[1]);
				int ytimes = Integer.parseInt(args[2]);
				testntimes(primes(startingatx), ytimes, k);
			}
		} catch (NumberFormatException e) {
			System.out.println(
					"Enter a positive number (up to 2^63) and k (accuracy) to determine whether the number is a prime, or enter three numbers (range, k and repetitions) to get a analysis on how many false-positives the algorithm produces.");
		}
	}

	/**
	 * @param x Any BigInteger
	 * @return The decomposition of the given number in the form 2^r * d + 1
	 */
	public static BigInteger[] decomp(BigInteger x) {

		BigInteger n = x;
		BigInteger r = zero;
		x = x.subtract(one);

		while ((x.mod(two)).equals(zero)) {
			x = x.divide(two);
			r = r.add(one);
		}

		BigInteger d = x;
		BigInteger[] data = { n, r, d };
		// System.out.println(n + " = 2^" + r + " * " + d + " + " + 1);
		return data;
	}

	/**
	 * @param numbers Array containing the prime number that is to be analyzed, and
	 *                r and d values of its decomposition into n = 2^r*d+1
	 * @param k       Number of times the algorithm should run (higher number leads
	 *                to more accuracy and a higher runtime)
	 * @return Whether the number is not prime (100% guarantee) or maybe prime (more
	 *         reliable with higher k)
	 */
	public static String rabin(BigInteger[] numbers, int k) {

		for (int i = 0; i < k; i++) {
			if (!miller(numbers)) {
				return "not prime";
			}
		}
		return "probably prime";
	}

	/**
	 * @param numbers Array containing the prime number that is to be analyzed, and
	 *                r and d values of its decomposition into n = 2^r*d+1
	 * @return true if a number is suspected to be prime, false if it's not prime
	 */
	private static boolean miller(BigInteger[] numbers) {

		final BigInteger n = numbers[0];
		final BigInteger r = numbers[1];
		final BigInteger d = numbers[2];
		if (n.equals(two)) {
			return true;
		}
		BigDecimal random = new BigDecimal("" + Math.random()); 
		BigDecimal nminusthree = new BigDecimal(n.subtract(three));
		// Because random is a number between 0 and 1, we have use BigDecimal first

		BigInteger a = (two.add(random.multiply(nminusthree).toBigInteger()));
		// Generate a random number smaller than n
				
		BigInteger x = new BigInteger("" + a.modPow(d, n));
		// Calculate a ^ d mod n
		
		if ((x.equals(one)) || (x.equals((n.subtract(one))))) {

			return true;

		} else {
			for (int j = 0; j < (r.intValue() - 1); j++) {

				x = x.multiply(x);
				x = x.mod(n);

				if (x.equals(one)) {
					return false;
				}

				if (x.equals((n.subtract(one)))) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * @param x Any BigInteger
	 * @return false if the number is not prime, true if it is
	 */
	public static boolean isPrime(BigInteger x) {

	    if (!x.equals(two) && x.mod(two).equals(zero)) {
	        return false;}

	    for (BigInteger i = three; i.multiply(i).compareTo(x) < 1; i = i.add(two)) {
	        if (x.mod(i).equals(zero)) {
	            return false;}
	    }
	    return true;
	}
	
	/**
	 * @return ArrayList of 1000 next odd non-primes starting at j
	 */
	public static ArrayList<BigInteger> primes(BigInteger j) {

		ArrayList<BigInteger> primes = new ArrayList<>();
		while (primes.size() < 1000) {
			if (!isPrime(j) && (j.mod(two).equals(zero))) {
				BigInteger x = new BigInteger("" + j);
				primes.add(x);
			}
			j = j.add(one);
		}
		return primes;
	}
	
	/**
	 * Prints the amount of times a non-prime number was considered to be a
	 * potential prime number
	 *
	 * @param primes ArrayList of BigIntegers that are to be tested if they are
	 *               prime
	 * @param x      How many times it should be tested, higher number leads to more
	 *               accuracy and runtime
	 * @param k      Number of times a random a should be generated
	 */
	public static void testntimes(ArrayList<BigInteger> primes, int x, int k) {
		double l = 0;
		for (int i = 0; i < x; i++) {
			l = l + test(primes, k);
		}
		double f = (l / x);
		System.out.println("Tested " + primes.size() + " uneven non-primes " + x + " times with k = " + k);
		System.out.println((f / primes.size()) + " false-positives per number on average = "
				+ (float) ((f * 100) / primes.size()) + "% error rate");
	}

	/**
	 * @param primes ArrayList of BigIntegers that are to be tested if they are
	 *               prime
	 * @param k      Number of times a random a should be generated
	 * @return The amount of false-positives running the Rabin-Miller primality test
	 *         with given numbers and k
	 */
	public static int test(ArrayList<BigInteger> primes, int k) {
		int whoops = 0;
		for (int i = 0; i < primes.size(); i++) {
			if ((rabin(decomp(primes.get(i)), k).equals("probably prime"))) {
				System.out.println("Incorrectly identified " + primes.get(i) + " as potential prime number");
				whoops++;
			}
		}
		return whoops;
	}
}