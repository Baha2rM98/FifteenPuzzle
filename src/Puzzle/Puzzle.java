package Puzzle;

import Node.Node;

import java.util.*;

/**
 * @author baha2r
 * Date: 28/Nov/2019 11:03 PM
 **/

public class Puzzle {

    // Dimension of the puzzle
    private final static int DIMENSION = 4;

    // State of each node
    private int[][] state;

    // Display Separator
    private int displaySeparator;

    // Blank Node
    private Node blank;


    /**
     * Constructor
     */
    public Puzzle() {
        this.state = new int[DIMENSION][DIMENSION];
        int count = 1;
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                this.state[i][j] = count;
                count++;
            }
        }
        this.displaySeparator = Integer.toString(count).length();
        blank = new Node(DIMENSION - 1, DIMENSION - 1);
        this.state[this.blank.x][this.blank.y] = 0;
    }


    /**
     * Constructor
     * <p>
     * Creates new puzzle with applied movements
     */
    private Puzzle(Puzzle toBeCloned) {
        this();
        for (Node p : allNodePos()) {
            this.state[p.x][p.y] = toBeCloned.nodeVal(p);
        }
        this.blank = toBeCloned.getBlank();
    }


    /**
     * Returns value of each node
     *
     * @param node Node node
     * @return Returns value of each node
     */
    private int nodeVal(Node node) {
        return this.state[node.x][node.y];
    }


    /**
     * Returns the blank node
     *
     * @return Returns the blank node
     */
    private Node getBlank() {
        return this.blank;
    }


    /**
     * Returns goal puzzle
     *
     * @return Returns goal puzzle to check with each solving puzzle's state
     */
    private Puzzle getSOLVED() {
        return new Puzzle();
    }


    /**
     * A heuristic algorithm uses MissedPlaceTiles heuristic method
     *
     * @return Returns hScore of state to the final state
     */
    private int numberMisplacedNodes() {
        Puzzle solved = getSOLVED();
        int wrong = 0;
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                if ((this.state[i][j] > 0) && (this.state[i][j] != solved.state[i][j])) {
                    wrong++;
                }
            }
        }
        return wrong;
    }


    /**
     * Returns Node of given node value
     *
     * @param nodeValue Given node value
     * @return Returns Node of given node value
     */
    private Node whereIs(int nodeValue) {
        for (Node n : allNodePos()) {
            if (nodeVal(n) == nodeValue) {
                return n;
            }
        }
        return null;
    }


    /**
     * A heuristic algorithm uses manhattan heuristic method
     *
     * @return Returns hScore of state to the final state
     */
    private int manhattanDistance() {
        int sum = 0;
        for (Node n : allNodePos()) {
            int val = nodeVal(n);
            if (val != 0) {
                Node correct = getSOLVED().whereIs(val);
                assert correct != null;
                sum += Math.abs(n.x - correct.x) + Math.abs(n.y - correct.y);
            }
        }
        return sum;
    }


    /**
     * Checks if current puzzle is solved
     *
     * @return Returns true if puzzle is solved, false otherwise
     */
    private boolean isSolved() {
        return this.manhattanDistance() == 0;
    }


    /**
     * A heuristic algorithm for A*
     *
     * @return Returns hScore of state to the final state
     */

    private int estimateError() {
        return this.manhattanDistance();
    }


    /**
     * Returns a list of all positions of nodes
     *
     * @return Returns a list of all positions of nodes
     */
    private List<Node> allNodePos() {
        ArrayList<Node> out = new ArrayList<>();
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                out.add(new Node(i, j));
            }
        }
        return out;
    }


    /**
     * Returns a list of all adjacency of each node
     *
     * @return Returns a list of all adjacency of each node
     */
    private List<Puzzle> allAdjacentNodes() {
        ArrayList<Puzzle> out = new ArrayList<>();
        for (Node move : allValidMoves()) {
            out.add(movedClone(move));
        }
        return out;
    }


    /**
     * Checks if a movement is valid
     *
     * @return Returns true if a movement is valid, false otherwise
     */
    private boolean isValidMove(Node node) {
        if ((node.x < 0) || (node.x >= DIMENSION)) {
            return false;
        }
        if ((node.y < 0) || (node.y >= DIMENSION)) {
            return false;
        }
        int dx = this.blank.x - node.x;
        int dy = this.blank.y - node.y;
        return (Math.abs(dx) + Math.abs(dy) == 1) && (dx * dy == 0);
    }


    /**
     * Returns a list of all valid movements for each node
     *
     * @return Returns a list of all valid movements for each node
     */
    private List<Node> allValidMoves() {
        ArrayList<Node> out = new ArrayList<>();
        for (int dx = -1; dx < 2; dx++) {
            for (int dy = -1; dy < 2; dy++) {
                Node node = new Node(this.blank.x + dx, this.blank.y + dy);
                if (isValidMove(node)) {
                    out.add(node);
                }
            }
        }
        return out;
    }


    /**
     * Moves a node to a new coordination
     */
    private void move(Node node) {
        if (!isValidMove(node)) {
            throw new RuntimeException("Invalid move");
        }
        assert this.state[this.blank.x][this.blank.y] == 0;
        this.state[this.blank.x][this.blank.y] = this.state[node.x][node.y];
        this.state[node.x][node.y] = 0;
        this.blank = node;
    }


    /**
     * Returns a new puzzle with the applied movement
     *
     * @return Returns a new puzzle with the applied movement
     */
    private Puzzle movedClone(Node node) {
        Puzzle out = new Puzzle(this);
        out.move(node);
        return out;
    }


    /**
     * Shuffles goal puzzle as initiation state
     */
    public void shuffle(int howMany) {
        for (int i = 0; i < howMany; i++) {
            List<Node> possible = allValidMoves();
            int which = (int) (Math.random() * possible.size());
            Node move = possible.get(which);
            this.move(move);
        }
    }


    /**
     * Shows each state as a puzzle
     */
    public void show() {
        System.out.println("-----------------");
        for (int i = 0; i < DIMENSION; i++) {
            System.out.print("| ");
            for (int j = 0; j < DIMENSION; j++) {
                int n = this.state[i][j];
                StringBuilder s;
                if (n > 0) {
                    s = new StringBuilder(Integer.toString(n));
                } else {
                    s = new StringBuilder();
                }
                while (s.length() < this.displaySeparator) {
                    s.append(" ");
                }
                System.out.print(s + "| ");
            }
            System.out.print("\n");
        }
        System.out.print("-----------------\n\n");
    }


    /**
     * Gets user input as initiation state
     */
    public void getUserInput() {
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                this.state[i][j] = -1;
            }
        }
        Scanner scn = new Scanner(System.in);
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                this.state[i][j] = scn.nextInt();
            }
        }
    }


    /**
     * A* algorithm try to solve the puzzle and returns a list of puzzles if it was able to solve it, null otherwise
     *
     * @return Returns a list of puzzles if it was able to solve it, null otherwise
     */
    public List<Puzzle> aStarSolver() {
        Map<Puzzle, Puzzle> parent = new HashMap<>();
        Map<Puzzle, Integer> depth = new HashMap<>();
        Map<Puzzle, Integer> fScore = new HashMap<>();
        Comparator<Puzzle> comparator = Comparator.comparingInt(fScore::get);
        Queue<Puzzle> openList = new PriorityQueue<>(10000, comparator);
        parent.put(this, null);
        depth.put(this, 0);
        fScore.put(this, this.estimateError());
        openList.add(this);
        int count = 0;
        try {
            while (!openList.isEmpty()) {
                Puzzle currentNode = openList.remove();
                count++;
                if (count % 10000 == 0)
                    System.out.println(count + " states assumed. PriorityQueue size is: " + openList.size());
                if (currentNode.isSolved()) {
                    System.out.println("\n" + count + " states assumed.");
                    LinkedList<Puzzle> solution = new LinkedList<>();
                    Puzzle backtrace = currentNode;
                    while (backtrace != null) {
                        solution.addFirst(backtrace);
                        backtrace = parent.get(backtrace);
                    }
                    return solution;
                }
                for (Puzzle puzzle : currentNode.allAdjacentNodes()) {
                    if (!parent.containsKey(puzzle)) {
                        parent.put(puzzle, currentNode);
                        depth.put(puzzle, depth.get(currentNode) + 1);
                        int estimate = puzzle.estimateError();
                        fScore.put(puzzle, depth.get(currentNode) + 1 + estimate);
                        openList.add(puzzle);
                    }
                }
            }
        } catch (Exception ignored) {
            System.err.println("Opps!");
        }
        return null;
    }
}