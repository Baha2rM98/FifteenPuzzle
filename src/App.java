import Puzzle.Puzzle;

import java.util.List;
import java.util.Scanner;

/**
 * @author baha2r
 * Date: 28/Nov/2019 10:45 PM
 **/

public class App {
    public static void main(String[] args) {
        Scanner scn = new Scanner(System.in);
        System.out.println("1) Random puzzle");
        System.out.println("2) Manual puzzle");
        char ans = scn.next().charAt(0);
        if (ans == '1') {
            double beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            Puzzle p = new Puzzle();
            p.shuffle(85);
            System.out.println("Shuffled puzzle:");
            p.show();
            List<Puzzle> solution;
            System.out.println("A* started to solve the puzzle...");
            long timeStart = System.currentTimeMillis();
            solution = p.aStarSolver();
            showSolution(solution);
            long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long timeEnd = System.currentTimeMillis();
            long allocatedTime = timeEnd - timeStart;
            double allocatedMem = afterUsedMem - beforeUsedMem;
            System.out.println("Time allocated: " + (allocatedTime / 1000.0) + " seconds");
            System.out.println("Memory allocated: " + (allocatedMem / (1024.0 * 1024.0)) + " MB");
        }
        if (ans == '2') {
            double beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            Puzzle p = new Puzzle();
            System.out.println("Please enter initial puzzle state: ");
            p.getUserInput();
            System.out.println("Entered puzzle:");
            p.show();
            List<Puzzle> solution;
            System.out.println("A* started to solve the puzzle...");
            long timeStart = System.currentTimeMillis();
            solution = p.aStarSolver();
            showSolution(solution);
            long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long timeEnd = System.currentTimeMillis();
            long allocatedTime = timeEnd - timeStart;
            double allocatedMem = afterUsedMem - beforeUsedMem;
            System.out.println("Time allocated: " + (allocatedTime / 1000.0) + " seconds");
            System.out.println("Memory allocated: " + (allocatedMem / (1024.0 * 1024.0)) + " MB");
        } else System.out.println("Wrong option!");
    }

    private static void showSolution(List<Puzzle> solution) {
        if (solution != null) {
            System.out.printf("\nPuzzle solved with %d moves:\n", solution.size());
            for (Puzzle sp : solution)
                sp.show();
        }
    }
}
