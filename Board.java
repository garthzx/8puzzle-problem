import java.util.Arrays;
import edu.princeton.cs.algs4.Stack;

public class Board
{
    private final int[][] board;
    private final int dimension;

    private int hamming;
    private int manhattan;

    private int iIndexOf0 = 0;
    private int jIndexOf0 = 0;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles)
    {
        if (tiles == null)
            throw new IllegalArgumentException("tiles cannot be null");

        dimension = tiles.length;

        board = new int[dimension][dimension];
        for (int i = 0; i < tiles.length; i++)
        {
            for (int j = 0; j < tiles[i].length; j++)
            {
                board[i][j] = tiles[i][j];
            }
        }
        calculateDistances();
    }

    /**
     * The Manhattan distance is the sum of the absolute values of the
     * horizontal and the vertical distance:
     *
     * int distance = Math.abs(x1-x0) + Math.abs(y1-y0);
     */
    private void calculateDistances()
    {
        int calcHamming = 0, calcManhattan = 0;

        for (int i = 0; i < dimension; i++)
        {
            for (int j = 0; j < dimension; j++)
            {
                // found 0
                if (board[i][j] == 0)
                {
                    this.iIndexOf0 = i;
                    this.jIndexOf0 = j;
                }
                // 2nd condition, right-hand side of equality gets the expected value
                // at position board[i][j]
                if ((board[i][j] != 0) && board[i][j] != ((i * dimension) + j + 1))
                {
                    calcHamming++;

                    // which row should board[i][j] be.
                    int calcI = (board[i][j]-1) / dimension;
                    // which column should board[i][j] be.
                    int calcJ = (board[i][j]-1) % dimension;

                    // summing manhattan distances for every tile not including 0
                    calcManhattan += Math.abs(i - calcI) + Math.abs(j - calcJ);
                }
            }
        }
        this.hamming = calcHamming;
        this.manhattan = calcManhattan;
    }

    // string representation of this board
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(dimension).append("\n");

        for (int i = 0; i < dimension; i++)
        {
            for (int j = 0; j < dimension; j++)
            {
                sb.append(board[i][j]).append("  ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // board dimension n
    public int dimension()
    {
        return dimension;
    }

    // number of tiles out of place
    public int hamming()
    {
        return hamming;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan()
    {
        return manhattan;
    }

    // is this board the goal board?
    public boolean isGoal()
    {
        return hamming == 0;
    }

    // does this board equal y?
    public boolean equals(Object y)
    {
        if (y == null)
            return false;
        if (y == this)
            return true;
        if (getClass() != y.getClass())
            return false;

        Board other = (Board) y;
        return Arrays.deepEquals(board, other.board);
    }

    // all neighboring boards
    public Iterable<Board> neighbors()
    {
        Stack<Board> boards = new Stack<>();

        // if 0 is anywhere except the first row
        if (iIndexOf0 > 0)
        {
            int[][] blocksCopy = copyBlocks(board, dimension);
            // this simply swaps 0 and a value
            blocksCopy[iIndexOf0][jIndexOf0] = board[iIndexOf0-1][jIndexOf0];
            blocksCopy[iIndexOf0-1][jIndexOf0] = board[iIndexOf0][jIndexOf0];
            boards.push(new Board(blocksCopy));
        }

        // if 0 is anywhere except first column (or the left position)
        // simply moves it to the left
        if (jIndexOf0 > 0)
        {
            int[][] blocksCopy = copyBlocks(board, dimension);
            // set the value at the i and j index of 0 to the value at i and (j-1) index.
            blocksCopy[iIndexOf0][jIndexOf0] = board[iIndexOf0][jIndexOf0 -1];
            blocksCopy[iIndexOf0][jIndexOf0 - 1] = board[iIndexOf0][jIndexOf0];
            boards.push(new Board(blocksCopy));
        }

        // if 0 block is not at bottom position
        // anywhere except the bottom row
        if (iIndexOf0 < dimension-1)
        {
            int[][] blocksCopy = copyBlocks(board, dimension);
            blocksCopy[iIndexOf0][jIndexOf0] = board[iIndexOf0+1][jIndexOf0];
            blocksCopy[iIndexOf0+1][jIndexOf0] = board[iIndexOf0][jIndexOf0];
            boards.push(new Board(blocksCopy));
        }

        // if 0 blocks is not at the right position
        // -- anywhere except the last column
        if (jIndexOf0 < dimension-1)
        {
            int[][] blocksCopy = copyBlocks(board, dimension);
            blocksCopy[iIndexOf0][jIndexOf0] = board[iIndexOf0][jIndexOf0+1];
            blocksCopy[iIndexOf0][jIndexOf0+1] = board[iIndexOf0][jIndexOf0];
            boards.push(new Board(blocksCopy));
        }
        return boards;
    }

    private int[][] copyBlocks(int[][] blocks, int dimension)
    {
        int[][] copy = new int[dimension][dimension];

        for (int i = 0; i < dimension; i++)
        {
            for (int j = 0; j < dimension; j++)
            {
                copy[i][j] = blocks[i][j];
            }
        }
        return copy;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin()
    {
        int[][] copy = copyBlocks(board, dimension);
        for (int i = 0; i < dimension; i++)
        {
            for (int j = 0; j < dimension - 1; j++)
            {
                if (copy[i][j] != 0 && copy[i][j+1] != 0)
                {
                    int swap = copy[i][j];
                    copy[i][j] = copy[i][j+1];
                    copy[i][j+1] = swap;

                    return new Board(copy);
                }
            }
        }
        return null;
    }

    // unit testing (not graded)
    public static void main(String[] args)
    {
        int[][] board = {
                {8, 1, 3},
                {4, 0, 2},
                {7, 6, 5}
        };

        Board testBoard = new Board(board);
        System.out.println("Dimension: " + testBoard.dimension);
        System.out.println("i index of 0: " + testBoard.iIndexOf0);
        System.out.println("j index of 0: " + testBoard.jIndexOf0);
        System.out.println("Manhattan distance: " + testBoard.manhattan);
        System.out.println("Hamming distance: " + testBoard.hamming);
        System.out.println("to String: " + testBoard);
        System.out.println("Twin Board: " + testBoard.twin());
    }
}
