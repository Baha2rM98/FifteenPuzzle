package Puzzle;

import Node.Node;

import java.util.*;

/**
 * @author baha2r
 * Date: 28/Nov/2019 11:03 PM
 **/
//TODO => Document
public class Puzzle {

    private final static int DIMS = 4;
    private int[][] state;
    private int display_width;
    private Node blank;

    public Puzzle() {
        state = new int[DIMS][DIMS];
        int cnt = 1;
        for (int i = 0; i < DIMS; i++) {
            for (int j = 0; j < DIMS; j++) {
                state[i][j] = cnt;
                cnt++;
            }
        }
        display_width = Integer.toString(cnt).length();
        blank = new Node(DIMS - 1, DIMS - 1);
        state[blank.x][blank.y] = 0;
    }

    private Puzzle(Puzzle toClone) {
        this();
        for (Node p : allCellPos()) {
            state[p.x][p.y] = toClone.cell(p);
        }
        blank = toClone.getBlank();
    }

    public void getUserInput() {
        Scanner scn = new Scanner(System.in);
        for (int i = 0; i < DIMS; i++) {
            for (int j = 0; j < DIMS; j++) {
                state[i][j] = scn.nextInt();
            }
        }
        for (int i = 0; i < DIMS; i++) {
            for (int j = 0; j < DIMS; j++) {
                if (state[i][j] < 0 || state[i][j] > 15)
                    throw new RuntimeException("Wrong input! Try again...");
            }
        }
    }

    private List<Node> allCellPos() {
        ArrayList<Node> out = new ArrayList<Node>();
        for (int i = 0; i < DIMS; i++) {
            for (int j = 0; j < DIMS; j++) {
                out.add(new Node(i, j));
            }
        }
        return out;
    }


    private int cell(Node p) {
        return state[p.x][p.y];
    }


    private Node getBlank() {
        return blank;
    }


    private Node whereIs(int x) {
        for (Node p : allCellPos()) {
            if (cell(p) == x) {
                return p;
            }
        }
        return null;
    }

    public void show() {
        System.out.println("-----------------");
        for (int i = 0; i < DIMS; i++) {
            System.out.print("| ");
            for (int j = 0; j < DIMS; j++) {
                int n = state[i][j];
                StringBuilder s;
                if (n > 0) {
                    s = new StringBuilder(Integer.toString(n));
                } else {
                    s = new StringBuilder();
                }
                while (s.length() < display_width) {
                    s.append(" ");
                }
                System.out.print(s + "| ");
            }
            System.out.print("\n");
        }
        System.out.print("-----------------\n\n");
    }


    private List<Node> allValidMoves() {
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


    private boolean isValidMove(Node p) {
        if ((p.x < 0) || (p.x >= DIMS)) {
            return false;
        }
        if ((p.y < 0) || (p.y >= DIMS)) {
            return false;
        }
        int dx = blank.x - p.x;
        int dy = blank.y - p.y;
        return (Math.abs(dx) + Math.abs(dy) == 1) && (dx * dy == 0);
    }


    private void move(Node p) {
        if (!isValidMove(p)) {
            throw new RuntimeException("Invalid move");
        }
        assert state[blank.x][blank.y] == 0;
        state[blank.x][blank.y] = state[p.x][p.y];
        state[p.x][p.y] = 0;
        blank = p;
    }


    /**
     * returns a new puzzle with the move applied
     */
    private Puzzle moveClone(Node p) {
        Puzzle out = new Puzzle(this);
        out.move(p);
        return out;
    }


    public void shuffle(int howMany) {
        for (int i = 0; i < howMany; i++) {
            List<Node> possible = allValidMoves();
            int which = (int) (Math.random() * possible.size());
            Node move = possible.get(which);
            this.move(move);
        }
    }

    private Puzzle getSOLVED() {
        return new Puzzle();
    }

    private int numberMisplacedTiles() {
        Puzzle solved = getSOLVED();
        int wrong = 0;
        for (int i = 0; i < DIMS; i++) {
            for (int j = 0; j < DIMS; j++) {
                if ((state[i][j] > 0) && (state[i][j] != solved.state[i][j])) {
                    wrong++;
                }
            }
        }
        return wrong;
    }


    private boolean isSolved() {
        return numberMisplacedTiles() == 0;
    }

    /**
     * distance heuristic for A*
     */
    private int estimateError() {
        return this.numberMisplacedTiles();
    }


    private List<Puzzle> allAdjacentPuzzles() {
        ArrayList<Puzzle> out = new ArrayList<Puzzle>();
        for (Node move : allValidMoves()) {
            out.add(moveClone(move));
        }
        return out;
    }

    /**
     * returns a list of boards if it was able to solve it, or else null
     */
    public List<Puzzle> aStarSolver() {
        Map<Puzzle, Puzzle> parent = new HashMap<Puzzle, Puzzle>();
        Map<Puzzle, Integer> depth = new HashMap<Puzzle, Integer>();
        Map<Puzzle, Integer> score = new HashMap<Puzzle, Integer>();
        Comparator<Puzzle> comparator = new Comparator<>() {
            @Override
            public int compare(Puzzle a, Puzzle b) {
                return score.get(a) - score.get(b);
            }
        };
        Queue<Puzzle> toVisit = new PriorityQueue<Puzzle>(10000, comparator);
        parent.put(this, null);
        depth.put(this, 0);
        score.put(this, this.estimateError());
        toVisit.add(this);
        int cnt = 0;
        while (toVisit.size() > 0) {
            Puzzle candidate = toVisit.remove();
            cnt++;
            if (cnt % 10000 == 0)
                System.out.printf("Solution tree has %d nodes. PriorityQueue size is: %d\n", cnt, toVisit.size());
            if (cnt > 2900000) {
                System.out.println("Unsolvable!\n");
                break;
            }
            if (candidate.isSolved()) {
                System.out.printf("Solution tree has %d nodes.\n", cnt);
                LinkedList<Puzzle> solution = new LinkedList<Puzzle>();
                Puzzle backtrace = candidate;
                while (backtrace != null) {
                    solution.addFirst(backtrace);
                    backtrace = parent.get(backtrace);
                }
                return solution;
            }
            for (Puzzle fp : candidate.allAdjacentPuzzles()) {
                if (!parent.containsKey(fp)) {
                    parent.put(fp, candidate);
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