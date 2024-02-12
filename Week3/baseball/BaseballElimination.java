import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.HashMap;

/**
 * The {@code BaseballElimination} class represents a baseball division and provides methods to
 * determine if teams are eliminated.
 * It implements methods to read team and game data from a file, perform calculations, and check for
 * team elimination using max flow algorithms.
 */
public class BaseballElimination {
    private static final double EPSILON = 0.00001;
    private final int num;  // Total number of teams
    private final String[] teams;  // Array to store team names
    private final int[] wins;  // Array to store number of wins for each team
    private final int[] losses;  // Array to store number of losses for each team
    private final int[] remainings;  // Array to store number of remaining games for each team
    private final int[][] g;  // 2D array to store remaining games between each pair of teams
    private final HashMap<String, Integer> teamToIndex;  // Mapping from team names to their indices
    private final HashMap<Integer, String> indexToTeam;  // Mapping from indices to team names
    private final HashMap<String, Bag<String>> certificates;
            // Mapping from team names to the subset of teams that eliminates them
    private int maxWin;  // Maximum number of wins by any team
    private String maxWinTeam;  // Team with the maximum number of wins

    /**
     * Constructs a baseball division from a given file.
     *
     * @param filename the name of the file containing division data
     */
    public BaseballElimination(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename is null.");
        }

        // handle the first number
        In in = new In(filename);
        num = in.readInt();
        teams = new String[num];
        wins = new int[num];
        losses = new int[num];
        remainings = new int[num];
        g = new int[num][num];

        teamToIndex = new HashMap<String, Integer>();
        indexToTeam = new HashMap<Integer, String>();
        certificates = new HashMap<String, Bag<String>>();

        maxWin = 0;
        maxWinTeam = null;

        for (int i = 0; i < num; i++) {
            // handle the strings
            teams[i] = in.readString();
            teamToIndex.put(teams[i], i);
            indexToTeam.put(i, teams[i]);

            // handle the numbers, w[i], l[i], r[i], g[i][j]
            wins[i] = in.readInt();
            if (wins[i] > maxWin) {
                maxWin = wins[i];
                maxWinTeam = teams[i];
            }
            losses[i] = in.readInt();
            remainings[i] = in.readInt();
            for (int j = 0; j < num; j++) {
                g[i][j] = in.readInt();
            }
        }
    }

    /**
     * Returns the number of teams in the division.
     *
     * @return the number of teams
     */
    public int numberOfTeams() {
        return num;
    }

    /**
     * Returns an iterable of all teams in the division.
     *
     * @return an iterable of team names
     */
    public Iterable<String> teams() {
        return Arrays.asList(teams);
    }

    /**
     * Returns the number of wins for the specified team.
     *
     * @param team the name of the team
     * @return the number of wins for the team
     * @throws IllegalArgumentException if the team name is invalid
     */
    public int wins(String team) {
        if (team == null || teamToIndex.get(team) == null) {
            throw new IllegalArgumentException("Team is invalid.");
        }
        return wins[teamToIndex.get(team)];
    }

    /**
     * Returns the number of losses for the specified team.
     *
     * @param team the name of the team
     * @return the number of losses for the team
     * @throws IllegalArgumentException if the team name is invalid
     */
    public int losses(String team) {
        if (team == null || teamToIndex.get(team) == null) {
            throw new IllegalArgumentException("Team is invalid.");
        }
        return losses[teamToIndex.get(team)];
    }

    /**
     * Returns the number of remaining games for the specified team.
     *
     * @param team the name of the team
     * @return the number of remaining games for the team
     * @throws IllegalArgumentException if the team name is invalid
     */
    public int remaining(String team) {
        if (team == null || teamToIndex.get(team) == null) {
            throw new IllegalArgumentException("Team is invalid.");
        }
        return remainings[teamToIndex.get(team)];
    }

    /**
     * Returns the number of remaining games between two specified teams.
     *
     * @param team1 the name of the first team
     * @param team2 the name of the second team
     * @return the number of remaining games between the two teams
     * @throws IllegalArgumentException if either team name is invalid
     */
    public int against(String team1, String team2) {
        if (team1 == null || team2 == null || teamToIndex.get(team1) == null
                || teamToIndex.get(team2) == null) {
            throw new IllegalArgumentException("Team is invalid.");
        }
        return g[teamToIndex.get(team1)][teamToIndex.get(team2)];
    }

    /**
     * Determines if the specified team is eliminated from playoff contention.
     *
     * @param team the name of the team
     * @return {@code true} if the team is eliminated, {@code false} otherwise
     * @throws IllegalArgumentException if the team name is invalid
     */
    public boolean isEliminated(String team) {
        if (team == null || teamToIndex.get(team) == null) {
            throw new IllegalArgumentException("Team is invalid.");
        }

        if (!certificates.containsKey(team)) {
            checkEliminated(team);
        }

        return certificates.get(team) != null;
    }

    /**
     * Checks if the specified team is eliminated from playoff contention and updates the
     * certificates accordingly.
     *
     * @param team the name of the team
     */
    private void checkEliminated(String team) {
        int index = teamToIndex.get(team);
        if (wins[index] + remainings[index] < maxWin) {
            Bag<String> b = new Bag<String>();
            b.add(maxWinTeam);
            certificates.put(team, b);
        }
        else {
            FlowNetwork fn = createFlowNetwork(index);
            int s = fn.V() - 2;
            int t = fn.V() - 1;

            FordFulkerson ff = new FordFulkerson(fn, s, t);
            if (isFullFromSource(fn, s)) {
                certificates.put(team, null);
            }
            else {
                Bag<String> b = new Bag<String>();
                for (int i = 0; i < num; i++) {
                    if (ff.inCut(i)) {
                        b.add(indexToTeam.get(i));
                    }
                }
                certificates.put(team, b);
            }
        }
    }

    /**
     * Creates a flow network based on the given team index.
     *
     * @param index the index of the team
     * @return the constructed flow network
     */
    private FlowNetwork createFlowNetwork(int index) {
        int numGames = num * (num - 1) / 2;
        int numV = numGames + num + 2;
        int s = numV - 2;
        int t = numV - 1;

        int sum = wins[index] + remainings[index];
        FlowNetwork fn = new FlowNetwork(numV);
        for (int i = 0; i < num; i++) {
            fn.addEdge(new FlowEdge(i, t, sum - wins[i]));
        }

        for (int i = 0, v = num; i < num; i++) {
            for (int j = i + 1; j < num; j++) {
                fn.addEdge(new FlowEdge(s, v, g[i][j]));
                fn.addEdge(new FlowEdge(v, i, Double.POSITIVE_INFINITY));
                fn.addEdge(new FlowEdge(v, j, Double.POSITIVE_INFINITY));
                v++;
            }
        }
        return fn;
    }

    /**
     * Checks if the flow network is full from the source vertex.
     *
     * @param fn the flow network
     * @param s  the index of the source vertex
     * @return {@code true} if the flow network is full from the source vertex, {@code false} otherwise
     */
    private boolean isFullFromSource(FlowNetwork fn, int s) {
        for (FlowEdge fe : fn.adj(s)) {
            if (Math.abs(fe.flow() - fe.capacity()) > EPSILON) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the subset of teams that eliminate the specified team from playoff contention.
     *
     * @param team the name of the team
     * @return an iterable of team names that eliminate the specified team, or {@code null} if the
     * team is not eliminated
     * @throws IllegalArgumentException if the team name is invalid
     */
    public Iterable<String> certificateOfElimination(String team) {
        if (team == null || teamToIndex.get(team) == null) {
            throw new IllegalArgumentException("Team is invalid.");
        }

        if (!certificates.containsKey(team)) {
            checkEliminated(team);
        }

        return certificates.get(team);
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
