/**
 * Class to solve the Boggle game.
 */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;

public class BoggleSolver {
    private static final int[] theRow = { -1, 0, 1, -1, 1, -1, 0, 1 };
    // together with theCol represent the neighbours in 8 directions
    private static final int[] theCol = { -1, -1, -1, 0, 0, 1, 1, 1 };
    private final TrieNode root;

    /**
     * TrieNode class representing nodes in the trie structure.
     */
    private class TrieNode {
        public TrieNode[] next = new TrieNode[26];   // 26 letters
        public boolean isEnd = false;
    }

    /**
     * Initializes the BoggleSolver with a given dictionary.
     *
     * @param dictionary Array of strings representing the dictionary.
     */
    public BoggleSolver(String[] dictionary) {
        root = new TrieNode();
        for (String s : dictionary) {
            insert(s);
        }
    }

    /**
     * Inserts a word into the trie.
     *
     * @param s The word to insert into the trie.
     */
    private void insert(String s) {
        TrieNode node = root;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (node.next[c - 'A'] == null) {
                node.next[c - 'A'] = new TrieNode();
            }
            node = node.next[c - 'A'];
        }
        node.isEnd = true;
    }

    /**
     * Finds all valid words on the Boggle board.
     *
     * @param board The Boggle board to search for words on.
     * @return An iterable containing all valid words found on the board.
     */
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        int x = board.rows();
        int y = board.cols();
        HashSet<String> words = new HashSet<String>();
        boolean[][] visited = new boolean[x][y];

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                dfs(board, i, j, root, "", words, visited);
            }
        }
        return words;
    }

    /**
     * Recursive depth-first search to find valid words on the board.
     *
     * @param board   The Boggle board being searched.
     * @param i       The row index of the current cell.
     * @param j       The column index of the current cell.
     * @param curr    The current TrieNode being processed.
     * @param path    The current path formed by letters.
     * @param words   The set of words found so far.
     * @param visited 2D array indicating visited cells on the board.
     */
    private void dfs(BoggleBoard board, int i, int j, TrieNode curr, String path,
                     HashSet<String> words, boolean[][] visited) {
        char c = board.getLetter(i, j);
        if (curr == null || curr.next[c - 'A'] == null) {
            return;
        }

        String str;
        if (c == 'Q') {
            str = path + "QU";
            curr = curr.next['Q' - 'A'];
            if (curr.next['U' - 'A'] == null) {
                return;
            }
            curr = curr.next['U' - 'A'];
        }
        else {
            str = path + c;
            curr = curr.next[c - 'A'];
        }

        if (curr.isEnd && str.length() > 2) {
            words.add(str);
        }

        visited[i][j] = true;
        int x = board.rows();
        int y = board.cols();
        for (int k = 0; k < theRow.length; k++) {
            int row = i + theRow[k];
            int col = j + theCol[k];
            if (row >= 0 && row < x && col >= 0 && col < y && !visited[row][col]) {
                dfs(board, row, col, curr, str, words, visited);
            }
        }
        visited[i][j] = false;
    }

    /**
     * Calculates the score of a given word.
     *
     * @param word The word to calculate the score for.
     * @return The score of the word.
     */
    public int scoreOf(String word) {
        if (contains(word)) {
            return getScore(word);
        }
        else {
            return 0;
        }
    }

    /**
     * Checks if the given word is in the dictionary.
     *
     * @param s The word to check.
     * @return True if the word is in the dictionary, false otherwise.
     */
    private boolean contains(String s) {
        TrieNode node = root;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (node.next[c - 'A'] == null) {
                return false;
            }
            node = node.next[c - 'A'];
        }
        return node.isEnd;
    }

    /**
     * Gets the score of a given word.
     *
     * @param s The word to get the score for.
     * @return The score of the word.
     */
    private int getScore(String s) {
        int len = s.length();

        if (len <= 2) {
            return 0;
        }
        else if (len <= 4) {
            return 1;
        }
        else if (len == 5) {
            return 2;
        }
        else if (len == 6) {
            return 3;
        }
        else if (len == 7) {
            return 5;
        }
        else {
            return 11;
        }
    }

    /**
     * Main method to run the BoggleSolver.
     *
     * @param args Command line arguments (filename and board configuration).
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
