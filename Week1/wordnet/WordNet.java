import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * WordNet class represents a semantic lexicon containing nouns and their relationships.
 */
public class WordNet {
    private final Map<String, ArrayList<Integer>> noun2Id;
    private final Map<Integer, String> id2Noun;
    private int numVertices;
    private SAP sap;

    /**
     * Initializes a WordNet instance with synsets and hypernyms files.
     *
     * @param synsets   the file containing synsets
     * @param hypernyms the file containing hypernyms
     * @throws IllegalArgumentException if the file names are null
     */
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("The file name is null.");
        }

        noun2Id = new HashMap<String, ArrayList<Integer>>();
        id2Noun = new HashMap<Integer, String>();
        numVertices = 0;

        readSynsets(synsets);
        readHypernyms(hypernyms);
    }

    /**
     * Reads synsets from the given file and populates the noun-to-ID and ID-to-noun mappings.
     *
     * @param synsets the file containing synsets
     */
    private void readSynsets(String synsets) {
        In in = new In(synsets);
        String line;

        while (true) {
            line = in.readLine();
            if (line == null) {
                break;
            }

            String[] contents = line.split(",");
            if (contents.length < 2) {
                continue;
            }
            numVertices++;
            int id = Integer.parseInt(contents[0]);
            String syns = contents[1];
            id2Noun.put(id, syns);
            String[] nouns = contents[1].split(" ");
            for (String noun : nouns) {
                ArrayList<Integer> ids = noun2Id.get(noun);
                if (ids != null) {
                    ids.add(id);
                }
                else {
                    ArrayList<Integer> nids = new ArrayList<>();
                    nids.add(id);
                    noun2Id.put(noun, nids);
                }
            }
        }
    }

    /**
     * Reads hypernyms from the given file and constructs the Digraph.
     *
     * @param hypernyms the file containing hypernyms
     */
    private void readHypernyms(String hypernyms) {
        In in = new In(hypernyms);
        String line;
        Digraph digraph = new Digraph(numVertices);

        while (true) {
            line = in.readLine();
            if (line == null) {
                break;
            }

            String[] contents = line.split(",");
            if (contents.length < 2) {
                continue;
            }
            int id = Integer.parseInt(contents[0]);
            for (int i = 1; i < contents.length; i++) {
                digraph.addEdge(id, Integer.parseInt(contents[i]));
            }
        }

        DirectedCycle dc = new DirectedCycle(digraph);
        if (dc.hasCycle()) {
            throw new IllegalArgumentException("The digraph have a directed cycle.");
        }

        int numRoot = 0;
        for (int i = 0; i < digraph.V(); i++) {
            if (digraph.outdegree(i) == 0) {
                numRoot++;
                if (numRoot > 1) {
                    throw new IllegalArgumentException("More than one root.");
                }
            }
        }
        sap = new SAP(digraph);
    }

    /**
     * Returns all WordNet nouns.
     *
     * @return all WordNet nouns
     */
    public Iterable<String> nouns() {
        return noun2Id.keySet();
    }

    /**
     * Checks if the word is a WordNet noun.
     *
     * @param word the word to check
     * @return true if the word is a WordNet noun, false otherwise
     * @throws IllegalArgumentException if the word is null
     */
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Word can not be null.");
        }
        return noun2Id.containsKey(word);
    }

    /**
     * Computes the distance between two WordNet nouns.
     *
     * @param nounA the first noun
     * @param nounB the second noun
     * @return the distance between nounA and nounB
     * @throws IllegalArgumentException if the input nouns are not valid WordNet nouns
     */
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("Input nouns are not valid WordNet nouns.");
        }
        return sap.length(noun2Id.get(nounA), noun2Id.get(nounB));
    }

    /**
     * Finds the common ancestor of two WordNet nouns in a shortest ancestral path.
     *
     * @param nounA the first noun
     * @param nounB the second noun
     * @return the synset that is the common ancestor of nounA and nounB
     * @throws IllegalArgumentException if the input nouns are not valid WordNet nouns
     */
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("Input nouns are not valid WordNet nouns.");
        }
        return id2Noun.get(sap.ancestor(noun2Id.get(nounA), noun2Id.get(nounB)));
    }

    /**
     * Executes unit tests for the WordNet class.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        WordNet wordNet = new WordNet(args[0], args[1]);

        StdOut.println(wordNet.isNoun("a"));
        StdOut.println(wordNet.sap("a", "b"));
        StdOut.println();
        StdOut.println(wordNet.sap("b", "f"));
        StdOut.println(wordNet.distance("b", "f"));

        StdOut.println();
        StdOut.println(wordNet.sap("c", "f"));
        StdOut.println(wordNet.distance("c", "f"));
    }
}
