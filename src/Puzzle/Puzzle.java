package Puzzle;

import Node.Node;

import java.util.*;

/**
 * Illustrates how to solve the fifteen puzzle using Dijkstra's algorithm and A*.
 * Haven't really written any java in at least 5 years, so apologies for sloppiness.
 *
 * @author leodirac
 */
public class Puzzle {

    public final static int DIMS = 4;
    private int[][] tiles;
    private int display_width;
    private Node blank;

    public Puzzle() {
        tiles = new int[DIMS][DIMS];
        int cnt = 1;
        for (int i = 0; i < DIMS; i++) {
            for (int j = 0; j < DIMS; j++) {
                tiles[i][j] = cnt;
                cnt++;
            }
        }
        display_width = Integer.toString(cnt).length();

        // init blank
        blank = new Node(DIMS - 1, DIMS - 1);
        tiles[blank.x][blank.y] = 0;
    }

    public final static Puzzle SOLVED = new Puzzle();


    public Puzzle(Puzzle toClone) {
        this();  // chain to basic init
        for (Node p : allTilePos()) {
            tiles[p.x][p.y] = toClone.tile(p);
        }
        blank = toClone.getBlank();
    }

    public List<Node> allTilePos() {
        ArrayList<Node> out = new ArrayList<Node>();
        for (int i = 0; i < DIMS; i++) {
            for (int j = 0; j < DIMS; j++) {
                out.add(new Node(i, j));
            }
        }
        return out;
    }


    public int tile(Node p) {
        return tiles[p.x][p.y];
    }


    public Node getBlank() {
        return blank;
    }


    public Node whereIs(int x) {
        for (Node p : allTilePos()) {
            if (tile(p) == x) {
                return p;
            }
        }
        return null;
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof Puzzle) {
            for (Node p : allTilePos()) {
                if (this.tile(p) != ((Puzzle) o).tile(p)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    @Override
    public int hashCode() {
        int out = 0;
        for (Node p : allTilePos()) {
            out = (out * DIMS * DIMS) + this.tile(p);
        }
        return out;
    }


    public void show() {
        System.out.println("-----------------");
        for (int i = 0; i < DIMS; i++) {
            System.out.print("| ");
            for (int j = 0; j < DIMS; j++) {
                int n = tiles[i][j];
                String s;
                if (n > 0) {
                    s = Integer.toString(n);
                } else {
                    s = "";
                }
                while (s.length() < display_width) {
                    s += " ";
                }
                System.out.print(s + "| ");
            }
            System.out.print("\n");
        }
        System.out.print("-----------------\n\n");
    }


    public List<Node> allValidMoves() {
        ArrayList<Node> out = new ArrayList<Node>();
        for (int dx = -1; dx < 2; dx++) {
            for (int dy = -1; dy < 2; dy++) {
                Node tp = new Node(blank.x + dx, blank.y + dy);
                if (isValidMove(tp)) {
                    out.add(tp);
                }
            }
        }
        return out;
    }


    public boolean isValidMove(Node p) {
        if ((p.x < 0) || (p.x >= DIMS)) {
            return false;
        }
        if ((p.y < 0) || (p.y >= DIMS)) {
            return false;
        }
        int dx = blank.x - p.x;
        int dy = blank.y - p.y;
        if ((Math.abs(dx) + Math.abs(dy) != 1) || (dx * dy != 0)) {
            return false;
        }
        return true;
    }


    public void move(Node p) {
        if (!isValidMove(p)) {
            throw new RuntimeException("Invalid move");
        }
        assert tiles[blank.x][blank.y] == 0;
        tiles[blank.x][blank.y] = tiles[p.x][p.y];
        tiles[p.x][p.y] = 0;
        blank = p;
    }


    /**
     * returns a new puzzle with the move applied
     *
     * @param p
     * @return
     */
    public Puzzle moveClone(Node p) {
        Puzzle out = new Puzzle(this);
        out.move(p);
        return out;
    }


    public void shuffle(int howmany) {
        for (int i = 0; i < howmany; i++) {
            List<Node> possible = allValidMoves();
            int which = (int) (Math.random() * possible.size());
            Node move = possible.get(which);
            this.move(move);
        }
    }


    public void shuffle() {
        shuffle(DIMS * DIMS * DIMS * DIMS * DIMS);
    }


    public int numberMisplacedTiles() {
        int wrong = 0;
        for (int i = 0; i < DIMS; i++) {
            for (int j = 0; j < DIMS; j++) {
                if ((tiles[i][j] > 0) && (tiles[i][j] != SOLVED.tiles[i][j])) {
                    wrong++;
                }
            }
        }
        return wrong;
    }


    public boolean isSolved() {
        return numberMisplacedTiles() == 0;
    }


    /**
     * another A* heuristic.
     * Total manhattan distance (L1 norm) from each non-blank tile to its correct position
     *
     * @return
     */
    public int manhattanDistance() {
        int sum = 0;
        for (Node p : allTilePos()) {
            int val = tile(p);
            if (val > 0) {
                Node correct = SOLVED.whereIs(val);
                sum += Math.abs(correct.x = p.x);
                sum += Math.abs(correct.y = p.y);
            }
        }
        return sum;
    }

    /**
     * distance heuristic for A*
     *
     * @return
     */
    public int estimateError() {
        return this.numberMisplacedTiles();
        //return 5*this.numberMisplacedTiles(); // finds a non-optimal solution faster
        //return this.manhattanDistance();
    }


    public List<Puzzle> allAdjacentPuzzles() {
        ArrayList<Puzzle> out = new ArrayList<Puzzle>();
        for (Node move : allValidMoves()) {
            out.add(moveClone(move));
        }
        return out;
    }

    /**
     * returns a list of boards if it was able to solve it, or else null
     *
     * @return
     */
    public List<Puzzle> dijkstraSolve() {
        Queue<Puzzle> toVisit = new LinkedList<Puzzle>();
        HashMap<Puzzle, Puzzle> predecessor = new HashMap<Puzzle, Puzzle>();
        toVisit.add(this);
        predecessor.put(this, null);
        int cnt = 0;
        while (toVisit.size() > 0) {
            Puzzle candidate = toVisit.remove();
            cnt++;
            if (cnt % 10000 == 0) {
                System.out.printf("Considered %,d positions. Queue = %,d\n", cnt, toVisit.size());
            }
            if (candidate.isSolved()) {
                System.out.printf("Solution considered %d boards\n", cnt);
                LinkedList<Puzzle> solution = new LinkedList<Puzzle>();
                Puzzle backtrace = candidate;
                while (backtrace != null) {
                    solution.addFirst(backtrace);
                    backtrace = predecessor.get(backtrace);
                }
                return solution;
            }
            for (Puzzle fp : candidate.allAdjacentPuzzles()) {
                if (!predecessor.containsKey(fp)) {
                    predecessor.put(fp, candidate);
                    toVisit.add(fp);
                }
            }
        }
        return null;
    }


    /**
     * returns a list of boards if it was able to solve it, or else null
     */
    public List<Puzzle> aStarSolve() {
        HashMap<Puzzle, Puzzle> predecessor = new HashMap<Puzzle, Puzzle>();
        HashMap<Puzzle, Integer> depth = new HashMap<Puzzle, Integer>();
        final HashMap<Puzzle, Integer> score = new HashMap<Puzzle, Integer>();
        Comparator<Puzzle> comparator = new Comparator<Puzzle>() {
            @Override
            public int compare(Puzzle a, Puzzle b) {
                return score.get(a) - score.get(b);
            }
        };
        PriorityQueue<Puzzle> toVisit = new PriorityQueue<Puzzle>(10000, comparator);

        predecessor.put(this, null);
        depth.put(this, 0);
        score.put(this, this.estimateError());
        toVisit.add(this);
        int cnt = 0;
        while (toVisit.size() > 0) {
            Puzzle candidate = toVisit.remove();
            cnt++;
            if (cnt % 10000 == 0) {
                System.out.printf("Considered %,d positions. Queue = %,d\n", cnt, toVisit.size());
            }
            if (candidate.isSolved()) {
                System.out.printf("Solution considered %d boards\n", cnt);
                LinkedList<Puzzle> solution = new LinkedList<Puzzle>();
                Puzzle backtrace = candidate;
                while (backtrace != null) {
                    solution.addFirst(backtrace);
                    backtrace = predecessor.get(backtrace);
                }
                return solution;
            }
            for (Puzzle fp : candidate.allAdjacentPuzzles()) {
                if (!predecessor.containsKey(fp)) {
                    predecessor.put(fp, candidate);
                    depth.put(fp, depth.get(candidate) + 1);
                    int estimate = fp.estimateError();
                    score.put(fp, depth.get(candidate) + 1 + estimate);
                    toVisit.add(fp);
                }
            }
        }
        return null;
    }
}