import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Solver
{

    private final boolean solvable;
    private final int solutionMoves;
    private Node solutionBoard;

    private static class Node implements Comparable<Node>
    {
        private final Board board;
        private final int moves;
        private final Node previousNode;
        private final int priority;

        public Node(Board board, int moves, Node previousNode)
        {
            this.board = board;
            this.moves = moves;
            this.previousNode = previousNode;
            priority = moves + board.manhattan();
        }

        @Override
        public int compareTo(Node other)
        {
            if (this.priority > other.priority)
                return 1;
            if (this.priority < other.priority)
                return -1;
            return 0;
        }
    }

    /**
     * Find a solution to the initial board (using the A* algorithm). <br>
     * Runs the A* Algorithm on two puzzle instances - one with the initial board and one with the initial board
     * modified by swapping a pair of tiles - in lockstep (alternating back and forth between exploring search nodes
     * in each of the two game trees). Exactly one of the two will lead to the goal board.
     */
    public Solver(Board initial)
    {
        MinPQ<Node> searchNodeQueue = new MinPQ<>();
        MinPQ<Node> twinSearchNodeQueue = new MinPQ<>();

        Node node = new Node(initial, 0, null);

        // modify the initial search node by swapping any pair of tiles (blank square is not a tile)
        Node twinNode = new Node(initial.twin(), 0, null);

        // insert initial nodes (root node)
        searchNodeQueue.insert(node);

        twinSearchNodeQueue.insert(twinNode);

        //  Repeat this procedure until the search node dequeued corresponds to the goal board.
        while (true)
        {
            // Delete from the priority queue the search node with the minimum priority, and insert onto the
            // priority queue all neighboring search nodes (those that can be reached in one move from the
            // dequeue search node).
            Node minimumNode = searchNodeQueue.delMin();
            Node minimumTwinNode = twinSearchNodeQueue.delMin();

            if (minimumNode.board.isGoal())
            {
                solutionBoard = minimumNode;
                solvable = true;
                solutionMoves = minimumNode.moves;
                break;
            }

            if (minimumTwinNode.board.isGoal())
            {
                solvable = false;
                solutionMoves = -1;
                break;
            }

            insertNeighbors(minimumNode, searchNodeQueue);
            insertNeighbors(minimumTwinNode, twinSearchNodeQueue);
        }
    }

    /**
     * Inserts nodes to the queue. Firstly, it gets the neighbors (aka child nodes) of the search node represented as a
     * stack by calling the <i>neighbors()</i> method. Then, it traverses through all the Boards of the neighbors. If
     *
     * @param searchNode
     * @param queue
     */
    private void insertNeighbors(Node searchNode, MinPQ<Node> queue)
    {
        // Depending on the location of the blank square, a board can have 2, 3, or 4 neighbors.
        Iterable<Board> neighbors = searchNode.board.neighbors();
        for (Board nextBoard : neighbors)
        {
            // if no previous boards.
            // do not insert a neighbor if its board is the same as the board of the previous search node in the game tree
            if ((searchNode.previousNode == null) || (!nextBoard.equals(searchNode.previousNode.board)))
            {
                queue.insert(new Node(nextBoard, searchNode.moves + 1, searchNode));
            }
        }
    }

    /**
     * is the initial board solvable? (see below)
     */
    public boolean isSolvable()
    {
        return solvable;
    }


    /**
     * min number of moves to solve initial board; -1 if unsolvable
     * @return number of moves to get to the solution
     */
    public int moves()
    {
        return solutionMoves;
    }

    /**
     * sequence of boards in a shortest solution; null if unsolvable
     * @return solution
     */
    public Iterable<Board> solution()
    {
        if (!solvable) return null;
//        Stack<Board> solution = new Stack<>();

        List<Board> solutionList = new ArrayList<>();

        Node board = solutionBoard;
        // Uncomment below and comment 2nd while loop if you want to use Stack instead.
//        while (board != null)
//        {
//            solution.push(board.board);
//            board = board.previousNode;
//        }
//        return solution;

        while (board != null)
        {
            solutionList.add(board.board);
            board = board.previousNode;
        }
        Collections.reverse(solutionList);
        return solutionList;
    }

    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
