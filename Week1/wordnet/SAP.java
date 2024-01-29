/**
 * SAP class computes the shortest ancestral path in a digraph.
 * Throw an IllegalArgumentException in the following situations:
 * Any argument is null
 * Any vertex argument is outside its prescribed range
 * Any iterable argument contains a null item
 */

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    private final Digraph digraph;

    /**
     * Initializes a SAP instance with the given directed graph.
     *
     * @param G the directed graph
     * @throws IllegalArgumentException if the directed graph is null
     */
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException("The directed graph is null.");
        }

        digraph = new Digraph(G);
    }

    /**
     * throw an IllegalArgumentException unless {@code 0 <= v < V}
     */
    private void validateVertex(int v) {
        int numVertices = digraph.V();
        if (v < 0 || v >= numVertices)
            throw new IllegalArgumentException(
                    "Vertex " + v + " is not between 0 and " + (numVertices - 1) + ".");
    }

    /**
     * throw an IllegalArgumentException unless {@code 0 <= v < V}
     */
    private void validateVertices(Iterable<Integer> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("Argument is null.");
        }
        int numVertices = digraph.V();
        for (Integer v : vertices) {
            if (v == null) {
                throw new IllegalArgumentException("Vertex is null");
            }
            validateVertex(v);
        }
    }

    /**
     * Computes the shortest ancestral path between two vertices.
     *
     * @param v the first vertex
     * @param w the second vertex
     * @return an array containing the length of the shortest ancestral path (index 0) and the
     * common ancestor (index 1)
     * if such a path exists; otherwise, returns {-1, -1}
     */
    private int[] shortest(int v, int w) {
        validateVertex(v);
        validateVertex(w);

        int[] result = new int[2];      // [0]: length; [1]: ancestor
        BreadthFirstDirectedPaths bfsv = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bfsw = new BreadthFirstDirectedPaths(digraph, w);

        int shortestLen = Integer.MAX_VALUE;
        int shortestAncestor = -1;
        for (int i = 0; i < digraph.V(); i++) {
            if (bfsv.hasPathTo(i) && bfsw.hasPathTo(i)) {
                int len = bfsv.distTo(i) + bfsw.distTo(i);
                if (len < shortestLen) {
                    shortestLen = len;
                    shortestAncestor = i;
                }
            }
        }

        result[0] = (shortestAncestor == -1) ? -1 : shortestLen;
        result[1] = (shortestAncestor == -1) ? -1 : shortestAncestor;

        return result;
    }

    /**
     * Computes the shortest ancestral path between two vertices.
     *
     * @param v the first vertex
     * @param w the second vertex
     * @return an array containing the length of the shortest ancestral path (index 0) and the
     * common ancestor (index 1)
     * if such a path exists; otherwise, returns {-1, -1}
     */
    private int[] shortest(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertices(v);
        validateVertices(w);

        int[] result = new int[2];      // [0]: length; [1]: ancestor
        BreadthFirstDirectedPaths bfsv = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bfsw = new BreadthFirstDirectedPaths(digraph, w);

        int shortestLen = Integer.MAX_VALUE;
        int shortestAncestor = -1;
        for (int i = 0; i < digraph.V(); i++) {
            if (bfsv.hasPathTo(i) && bfsw.hasPathTo(i)) {
                int len = bfsv.distTo(i) + bfsw.distTo(i);
                if (len < shortestLen) {
                    shortestLen = len;
                    shortestAncestor = i;
                }
            }
        }

        result[0] = (shortestAncestor == -1) ? -1 : shortestLen;
        result[1] = (shortestAncestor == -1) ? -1 : shortestAncestor;

        return result;
    }


    /**
     * Computes the length of the shortest ancestral path between two vertices.
     *
     * @param v the first vertex
     * @param w the second vertex
     * @return the length of the shortest ancestral path; -1 if no such path
     */
    public int length(int v, int w) {
        int[] result = shortest(v, w);
        return result[0];
    }

    /**
     * Finds a common ancestor of two vertices that participates in the shortest ancestral path.
     *
     * @param v the first vertex
     * @param w the second vertex
     * @return a common ancestor that participates in the shortest ancestral path; -1 if no such path
     */
    public int ancestor(int v, int w) {
        int[] result = shortest(v, w);
        return result[1];
    }

    /**
     * Computes the length of the shortest ancestral path between any vertex in two iterables.
     *
     * @param v the first iterable of vertices
     * @param w the second iterable of vertices
     * @return the length of the shortest ancestral path; -1 if no such path
     */
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        int[] result = shortest(v, w);
        return result[0];
    }

    /**
     * Finds a common ancestor that participates in the shortest ancestral path between any vertex
     * in two iterables.
     *
     * @param v the first iterable of vertices
     * @param w the second iterable of vertices
     * @return a common ancestor that participates in the shortest ancestral path; -1 if no such path
     */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        int[] result = shortest(v, w);
        return result[1];
    }

    /**
     * Executes unit testing of the SAP class.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
