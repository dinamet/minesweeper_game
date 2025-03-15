import java.util.Random;
import java.util.Scanner;

public class minesweeper {
    private final char[][] grid;
    private final boolean[][] uncovered;
    private final boolean[][] flagged;
    private final int rows;
    private final int cols;
    private final int mines;
    private int safeCellsRemaining;
    private boolean firstMove;

    public minesweeper(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        this.safeCellsRemaining = rows * cols - mines;
        this.grid = new char[rows][cols];
        this.uncovered = new boolean[rows][cols];
        this.flagged = new boolean[rows][cols];
        this.firstMove = true;
        initializeGrid();
    }

    private void initializeGrid() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = '0';
            }
        }
    }

    private void placeMines(int safeRow, int safeCol) {
        Random random = new Random();
        int placedMines = 0;
        while (placedMines < mines) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);
            if (grid[r][c] != '*' && !(Math.abs(r - safeRow) <= 1 && Math.abs(c - safeCol) <= 1)) {
                grid[r][c] = '*';
                placedMines++;
                updateAdjacentCells(r, c);
            }
        }
    }

    private void updateAdjacentCells(int row, int col) {
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (r >= 0 && r < rows && c >= 0 && c < cols && grid[r][c] != '*') {
                    grid[r][c] = (char) (grid[r][c] == '0' ? '1' : grid[r][c] + 1);
                }
            }
        }
    }

    public void displayGrid() {
        System.out.print("   | ");
        for (int c = 0; c < cols; c++) System.out.print(c + " | ");
        System.out.println();
        System.out.println("   " + "-".repeat(cols * 4));

        for (int r = 0; r < rows; r++) {
            System.out.print(r + " | ");
            for (int c = 0; c < cols; c++) {
                if (uncovered[r][c]) System.out.print(grid[r][c] + " | ");
                else if (flagged[r][c]) System.out.print("F | ");
                else System.out.print("  | ");
            }
            System.out.println();
            System.out.println("   " + "-".repeat(cols * 4));
        }
    }

    public void uncoverCell(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols || uncovered[row][col] || flagged[row][col]) {
            System.out.println("Invalid move!");
            return;
        }

        if (firstMove) {
            placeMines(row, col);
            firstMove = false;
        }

        if (grid[row][col] == '*') {
            System.out.println("Game Over! You hit a mine.");
            uncoverAllMines();
            displayGrid();
            System.out.println("\nThank you for playing Minesweeper!");
            System.exit(0);
        }

        revealCells(row, col);

        if (safeCellsRemaining == 0) {
            System.out.println("Congratulations! You have uncovered all safe cells!");
            displayGrid();
            System.out.println("\nThank you for playing Minesweeper!");
            System.exit(0);
        }
    }

    private void revealCells(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols || uncovered[row][col]) return;
        uncovered[row][col] = true;
        safeCellsRemaining--;
        if (grid[row][col] == '0') {
            for (int r = row - 1; r <= row + 1; r++) {
                for (int c = col - 1; c <= col + 1; c++) {
                    revealCells(r, c);
                }
            }
        }
    }

    public void flagCell(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols || uncovered[row][col]) {
            System.out.println("Invalid move!");
            return;
        }
        flagged[row][col] = !flagged[row][col];
    }

    private void uncoverAllMines() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == '*') uncovered[r][c] = true;
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Minesweeper!");
        System.out.println("Choose difficulty: 1. Easy (8x10, 10 mines), 2. Medium (14x18, 40 mines), 3. Hard (20x24, 99 mines)");
        System.out.println("Enter your choice:");
        int choice = scanner.nextInt();
        int rows = choice == 1 ? 8 : choice == 2 ? 14 : 20;
        int cols = choice == 1 ? 10 : choice == 2 ? 18 : 24;
        int mines = choice == 1 ? 10 : choice == 2 ? 40 : 99;

        minesweeper game = new minesweeper(rows, cols, mines);
        while (true) {
            game.displayGrid();
            System.out.print("Enter move (u row col to uncover, f row col to flag): ");
            String action = scanner.next();
            int row = scanner.nextInt();
            int col = scanner.nextInt();

            if (action.equalsIgnoreCase("u")) game.uncoverCell(row, col);
            else if (action.equalsIgnoreCase("f")) game.flagCell(row, col);
            else System.out.println("Invalid command!");
        }
    }
}
