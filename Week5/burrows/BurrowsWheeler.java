/**
 * Class representing Burrows-Wheeler encoding and decoding operations.
 */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.Queue;

import java.util.Arrays;
import java.util.HashMap;

public class BurrowsWheeler {
    /**
     * Apply Burrows-Wheeler encoding.
     */
    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(s);
        for (int i = 0; i < csa.length(); i++) {
            if (csa.index(i) == 0) {
                BinaryStdOut.write(i);
                break;
            }
        }

        for (int i = 0; i < csa.length(); i++) {
            int index = csa.index(i);
            if (index == 0) {
                BinaryStdOut.write(s.charAt(s.length() - 1), 8);
            }
            else {
                BinaryStdOut.write(s.charAt(index - 1), 8);
            }
        }
        BinaryStdOut.close();
    }

    /**
     * Apply Burrows-Wheeler decoding.
     */
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String s = BinaryStdIn.readString();
        char[] t = s.toCharArray();
        HashMap<Character, Queue<Integer>> table = new HashMap<Character, Queue<Integer>>();
        for (int i = 0; i < t.length; i++) {
            if (!table.containsKey(t[i])) {
                table.put(t[i], new Queue<Integer>());
            }
            table.get(t[i]).enqueue(i);
        }

        Arrays.sort(t);
        int[] next = new int[t.length];
        for (int i = 0; i < next.length; i++) {
            next[i] = table.get(t[i]).dequeue();
        }

        for (int i = 0; i < next.length; i++) {
            BinaryStdOut.write(t[first], 8);
            first = next[first];
        }
        BinaryStdOut.close();
    }

    /**
     * Main method to execute encoding or decoding based on command line arguments.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Illegal command line argument");
        }
        if (args[0].equals("-")) {
            transform();
        }
        else if (args[0].equals("+")) {
            inverseTransform();
        }
        else {
            throw new IllegalArgumentException("Illegal command line argument");
        }
    }
}
