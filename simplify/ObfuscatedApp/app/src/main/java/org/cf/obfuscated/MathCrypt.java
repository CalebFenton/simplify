package org.cf.obfuscated;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MathCrypt {

  private static int sieveCount = 1000;
  private static int[] primes = getPrimes();

  private static int[] getPrimes() {
    // Ooh, thank you CS degree!
    int stop = (int) Math.sqrt(sieveCount);
    boolean[] isComposite = new boolean[sieveCount + 1];
    for (int m = 2; m <= stop; m++) {
      if (!isComposite[m]) {
        // Every multiple of m is composite
        for (int k = m * m; k <= sieveCount; k += m) {
          isComposite[k] = true;
        }
      }
    }

    List<Integer> primeList = new LinkedList<Integer>();
    for (int m = 0; m <= sieveCount; m++) {
      if (!isComposite[m]) {
        primeList.add(m);
      }
    }

    int[] primes = new int[primeList.size()];
    int index = 0;
    for ( Integer prime : primeList ) {
      primes[index++] = prime;
    }

    return primes;
  }

  public static int decode(int[] xyz) {
    return decode(xyz[0], xyz[1], xyz[2]);
  }

  public static int decode(int x, int y, int z) {
    int result = primes[x];
    result *= primes[y];
    result <<= 2;
    result -= z;

    return result;
  }

  public static int[] encode(int num) {
    // Pulled directly from ass. Do not actually ever use this.
    int[] encoded = new int[3];

    Random rnd = new Random();
    encoded[0] = rnd.nextInt(primes.length);
    encoded[1] = rnd.nextInt(primes.length);

    int wankershim = primes[encoded[0]] * primes[encoded[1]];
    wankershim <<= 2;
    encoded[2] = wankershim - num;

    return encoded;
  }

  public static void main(String[] argv) {
//    int[] encoded = encode(42);
//    System.out.println(Arrays.toString(encoded));
//    System.out.println("" + decode(encoded));
  }

}
