/**
 * Class representing MoveToFront encoding and decoding operations.
 */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.LinkedList;

public class MoveToFront {

    /**
     * Initialize the ASCII character sequence.
     *
     * @return LinkedList of ASCII characters.
     */
    private static LinkedList<Character> theASCII() {
        LinkedList<Character> asciiList = new LinkedList<Character>();
        for (int i = 255; i >= 0; i--) {
            asciiList.addFirst((char) i);
        }
        return asciiList;
    }

    /**
     * Apply move-to-front encoding.
     */
    public static void encode() {
        LinkedList<Character> asciiList = theASCII();
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int index = asciiList.indexOf(c);
            asciiList.remove(index);
            asciiList.addFirst(c);
            BinaryStdOut.write(index, 8);
        }
        BinaryStdOut.close();
    }

    /**
     * Apply move-to-front decoding.
     */
    public static void decode() {
        LinkedList<Character> asciiList = theASCII();
        while (!BinaryStdIn.isEmpty()) {
            int index = BinaryStdIn.readChar();
            char c = asciiList.get(index);
            asciiList.remove(index);
            asciiList.addFirst(c);
            BinaryStdOut.write(c, 8);
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
            encode();
        }
        else if (args[0].equals("+")) {
            decode();
        }
        else {
            throw new IllegalArgumentException("Illegal command line argument");
        }
    }
}
