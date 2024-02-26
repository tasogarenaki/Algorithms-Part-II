/**
 * Class representing Circular Suffix Array and its operations.
 */

import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.Comparator;

public class CircularSuffixArray {
    private final String s;
    private final Integer[] index;

    /**
     * Constructor for CircularSuffixArray.
     *
     * @param s Input string.
     */
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Input string is invalid.");
        }

        this.s = s;
        index = new Integer[s.length()];
        for (int i = 0; i < s.length(); i++) {
            index[i] = i;
        }

        Arrays.sort(index, suffixOrder());
    }

    /**
     * Comparator for suffix ordering.
     *
     * @return Comparator for suffix ordering.
     */
    private Comparator<Integer> suffixOrder() {
        return new SuffixOrder();
    }

    private class SuffixOrder implements Comparator<Integer> {
        public int compare(Integer i1, Integer i2) {
            int first = i1;
            int second = i2;
            for (int i = 0; i < s.length(); i++) {
                char a = s.charAt(first);
                char b = s.charAt(second);
                if (a < b) {
                    return -1;
                }
                else if (a > b) {
                    return 1;
                }
                ++first;
                if (first == s.length()) {
                    first = 0;
                }
                ++second;
                if (second == s.length()) {
                    second = 0;
                }
            }

            return 0;
        }
    }

    /**
     * Length of input string.
     *
     * @return Length of input string.
     */
    public int length() {
        return s.length();
    }

    /**
     * Get index of ith sorted suffix.
     *
     * @param i Index.
     * @return Index of ith sorted suffix.
     */
    public int index(int i) {
        if (i < 0 || i >= s.length()) {
            throw new IllegalArgumentException("Input index is invalid.");
        }

        return index[i];
    }

    /**
     * Main method for unit testing.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        CircularSuffixArray csa = new CircularSuffixArray("ABRACADABRA!");
        for (int i = 0; i < csa.length(); i++) {
            StdOut.print(csa.index(i) + " ");
        }
        StdOut.println();
    }
}