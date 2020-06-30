import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * CS440 Localization
 * MazeSolvingBot.java
 * Purpose: Solve a conformant maze problem.
 *
 * @author Audrey Zhou
 * @version 1.0 5/11/20
 */
public class MazeSolvingBot {	
	
	/**
	 * Read a grid maze text file under resources.
	 * The grid maze is a matrix where fields containing 0 are accessible 
	 * while fields containing 1 are blocked. The goal is indicated with "G"
	 * and possible starting points are indicated with "A," "B," and "C."
	 * @param fileName
	 * @return
	 */
	public static Cell[][] generateBoard (String fileName) {
		File file = new File(fileName); 
		try {
			boolean allStarts = false;
			if (file.getName().equals("MapAllStarts.txt")) { allStarts = true;}
			System.out.println("allStarts =" + allStarts);
			Scanner sc = new Scanner (file);
			int rows = 0; int cols = 0;
		    while (sc.hasNextLine()) {
		    	rows++;
    			String [] line = sc.nextLine().split("	");
    			cols = line.length;
		    }
		    System.out.println("rows=" + rows);
		    System.out.println("cols=" + cols);
			Cell[][] maze = new Cell [rows][cols];
			Scanner scan = new Scanner (file);
		    int currentRow = 0;
		    int numStarts = 0;
		    while (scan.hasNextLine()) {
		    	currentRow++;
		    	String [] line = scan.nextLine().split("	");
		    	
		    	for (int j = 0; j < line.length; j++) {
		    		boolean isEmpty; boolean isGoal; boolean isStart;
		    		//For the MapAllStarts.txt, all open spaces, indicated by "0" are
		    		//possible starting points.
		    		if (allStarts) {
			    		if (line[j].equals("0")) { //blocked cell
			    			isEmpty = false; isGoal = false; isStart = false;
			    		}
			    		else if (line[j].equals("G")) { //goal cell
			    			isEmpty = true; isGoal = true; isStart = true;
			    			numStarts++;
			    		}
			    		else { //clear
			    			isEmpty = true; isGoal = false; isStart = true;
			    			numStarts++;
			    		}
		    		}
		    		else {
			    		if (line[j].equals("0")) { //blocked cell
			    			isEmpty = false; isGoal = false; isStart = false;
			    		}
			    		else if (line[j].equals("1")) { //clear cell
			    			isEmpty = true; isGoal = false; isStart = false;
			    		}
			    		else if (line[j].equals("G")) { //goal
			    			isEmpty = true; isGoal = true; isStart = false;
			    		}
			    		else { //starting points
			    			isEmpty = true; isGoal = false; isStart = true;
			    		}
		    		}
		    		maze[currentRow - 1][j] = new Cell (currentRow - 1, j, isEmpty, isGoal, isStart);
		    	}
		    }
		    System.out.println(numStarts);
		    return maze;
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found");
		}
		return null;
	}
	
	/**
	 * printMazeGUI creates a Java window to display the maze in a GUI.
	 * The color key of the different squares in the maze is explained below.
	 * black = blocked spaces in maze, white = open spaces in maze,
	 * green = goal, red = starting points in the maze
	 * @param board
	 */
	public static void printMazeGUI(Cell [][] board) {
		JFrame maze = new JFrame("Maze");
		maze.setSize(500, 500);
		maze.setLayout(new GridLayout(board.length, board[0].length));
		JPanel cells[][] = new JPanel[board.length][board[0].length];
		
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				cells[i][j] = new JPanel();
				cells[i][j].setBorder(BorderFactory.createLineBorder(Color.black));
				if (board[i][j].isGoal) {
					cells[i][j].setBackground(Color.green);
				}
				else if (board[i][j].isStart) {
					cells[i][j].setBackground(Color.red);
				}
				else if (board[i][j].isEmpty) {
					cells[i][j].setBackground(Color.white);
				} 
				else if (!board[i][j].isEmpty) {
					cells[i][j].setBackground(Color.black);
				}
				maze.add(cells[i][j]);
			}
		}
		maze.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		maze.setVisible(true);
	}
	
	/**
	 * findStart is a helper method that is called in the findShortestPath method
	 * that finds all possible starting points in the maze. 
	 * It checks each Cell in the maze and adds starting cells into an ArrayList.
	 * @param maze
	 * @return
	 */
	public static ArrayList<Cell> findStart (Cell[][] maze) {
		ArrayList<Cell> startingPositions = new ArrayList<Cell>();
		for (int i = 0; i < maze.length; i++) {
			for (int j = 0; j < maze[0].length; j++) {
				if (maze[i][j].isStart) {
					startingPositions.add(maze[i][j]);
				}
			}
		}
		return startingPositions;
	}
	
	/**
	 * findGoal is a helper method that is called in the findShortestPath method
	 * that finds the goal cell in the maze. It checks each Cell in the maze and 
	 * when the goal cell is found, it is returned. Mazes will only have one goal cell.
	 * @param maze
	 * @return
	 */
	public static Cell findGoal (Cell[][] maze) {
		for (int i = 0; i < maze.length; i++) {
			for (int j = 0; j < maze[0].length; j++) {
				if (maze[i][j].isGoal) {
					return maze[i][j];
				}
			}
		}
		System.out.println("Goal not found");
		return null;
	}
	
	/**
	 * findShortestPath performs BFS on the belief states of an input maze. 
	 * It returns a goal State which is achieved when all path originating from all
	 * possible starting points arrive at the goal cell. Each state contains field variables
	 * previous, indicating the previous state, and direction, indicating the direction
	 * taken (up, down, left, right) to arrive to that specific state from its previous state.
	 * @param maze
	 * @return
	 */
	public static State findShortestPath (Cell[][] maze) {
		if (maze == null || maze[0] == null || maze[0].length == 0) return null;
		ArrayList<Cell> startingPositions = findStart(maze);
		Cell goal = findGoal(maze);
		System.out.println("goal = (" + goal.row + "," + goal.col + ")");
		ArrayList<Cell> goalPositions = new ArrayList<Cell> ();
		for (int i = 0; i < startingPositions.size(); i++) {
			Cell newGoalPosition = new Cell (goal.row, goal.col, startingPositions.get(i).isEmpty, 
					startingPositions.get(i).isGoal, startingPositions.get(i).isStart);
			goalPositions.add(newGoalPosition);
		}
		State goalState = new State (goalPositions, goal);
		State startState = new State (startingPositions, goal);
		System.out.println("start state:");
		for (int i = 0; i < startState.positions.size(); i++) {
			System.out.println(startState.positions.get(i).col + ", " + startState.positions.get(i).row);
		}
		System.out.println("goal state:");
		for (int i = 0; i < goalState.positions.size(); i++) {
			System.out.println(goalState.positions.get(i).col + ", " + goalState.positions.get(i).row);
		}
		System.out.println("number of starting positions =" + startState.positions.size());
		LinkedList<State> fringe = new LinkedList<State>();
		ArrayList <State> visited = new ArrayList <State> ();
		fringe.add(startState);
		int count = 0;
		while(!fringe.isEmpty()) {
			State currentState = fringe.getFirst();
			fringe.remove();
			visited.add(currentState);
			if (currentState.equals(goalState)) {
				System.out.println("current state = goal state");
				return currentState;
			}
			else {
				count++;
				System.out.println("-------------------------update fringe #:" + count + "-------------------------");
				fringe = updateFringe(maze, fringe, currentState, goalState, visited);
			}
		}
		System.out.println("goal state not found!!!!!!!!!!!!!!!!!!!!!!!!!");
		return null;
	}
	
	/**
	 * updateFringe is a helper method for findShortestPath that considers all adjacent 
	 * belief states when the current state is moved up, down, right, and left.
	 * These belief states are added to the fringe for further exploration.
	 * Only belief states that have not been previously visited and are not already in the fringe
	 * are added to the fringe. If a belief state is found that is equal to the goal state,
	 * it is added to the front of the fringe, so that it is immediately explored in the 
	 * findShortestPath method, reducing the time and space constraints of BFS.
	 * In addition, each time a belief state is added to the fringe, its previous variable 
	 * is set to the current state and its direction variable is set to 
	 * the direction that derived the adjacent belief state from the current state. 
	 * @param map
	 * @param fringe
	 * @param currentState
	 * @param goalState
	 * @param visited
	 * @return
	 */
	public static LinkedList<State> updateFringe (Cell[][] map, LinkedList<State> fringe, State currentState, State goalState,
			ArrayList<State> visited) {	
		//down: row+1
		State downState = copyState(currentState); 
		for (int i = 0; i < currentState.positions.size(); i++) {
			if (currentState.positions.get(i).row + 1 < map.length && 
					map[currentState.positions.get(i).row + 1][currentState.positions.get(i).col].isEmpty) {
				downState.positions.get(i).row++;
			}
		}
//		System.out.println("visited contains down state? " + visited.contains(downState));
//		System.out.println("fringe contains down state? " + fringe.contains(downState));
		if (downState.equals(goalState)) {
			downState.direction = "D";
			downState.prev = currentState;
			fringe.addFirst(downState);
			System.out.println("goal state found and added to front of fringe");
			return fringe;
		}
		else if (!visited.contains(downState) && !fringe.contains(downState)) {
			System.out.println("downState was added to fringe");
			downState.direction = "D";
			downState.prev = currentState;
			fringe.add(downState);
		}
		
		//up: row-1
		State upState = copyState(currentState);
		for (int i = 0; i < currentState.positions.size(); i++) {
			if (currentState.positions.get(i).row - 1 >= 0 && 
					map[currentState.positions.get(i).row - 1][currentState.positions.get(i).col].isEmpty) {
				upState.positions.get(i).row--;
			}
		}
		if (upState.equals(goalState)) {
			upState.direction = "U";
			upState.prev = currentState;
			fringe.addFirst(upState);
			System.out.println("goal state found and added to front of fringe");
			return fringe;
		}
		if (!visited.contains(upState) && !fringe.contains(upState)) {
			System.out.println("upState was added to fringe");
			upState.direction = "U";
			upState.prev = currentState;
			fringe.add(upState);
		}
		
		//right: col+1
		State rightState = copyState(currentState);
		for (int i = 0; i < currentState.positions.size(); i++) {
			if (currentState.positions.get(i).col + 1 < map.length && 
					map[currentState.positions.get(i).row][currentState.positions.get(i).col+1].isEmpty) {
				rightState.positions.get(i).col++;
			}
		}
		if (rightState.equals(goalState)) {
			rightState.direction = "R";
			rightState.prev = currentState;
			fringe.addFirst(rightState);
			System.out.println("goal state found and added to front of fringe");
			return fringe;
		}
		if (!visited.contains(rightState) && !fringe.contains(rightState)) {
			System.out.println("rightState was added to fringe");
			rightState.direction = "R";
			rightState.prev = currentState;
			fringe.add(rightState);
		}
		
		//left: col-1
		State leftState = copyState(currentState);
		for (int i = 0; i < currentState.positions.size(); i++) {
			if (currentState.positions.get(i).col - 1 >= 0 && 
					map[currentState.positions.get(i).row][currentState.positions.get(i).col-1].isEmpty) {
				leftState.positions.get(i).col--;
			}
		}
		if (leftState.equals(goalState)) {
			leftState.direction = "L";
			leftState.prev = currentState;
			fringe.addFirst(leftState);
			System.out.println("goal state found and added to front of fringe");
			return fringe;
		}
		if (!visited.contains(leftState) && !fringe.contains(leftState)) {
			System.out.println("leftState was added to fringe");
			leftState.direction = "L";
			leftState.prev = currentState;
			fringe.add(leftState);
		}
		System.out.println("fringe size = " + fringe.size());
		return fringe;
	}
	
	/**
	 * Creates a deep copy of a State (completely separate object in memory with same
	 * fields and data). Used in updateFringe to preserve a copy to of the original 
	 * State even when the copied State is modified.
	 * @param original
	 * @return
	 */
    public static State copyState(State original){
    	ArrayList<Cell> c = new ArrayList<Cell> ();
    	for (int i = 0; i < original.positions.size(); i++) {
    		Cell originalCell = original.positions.get(i);
    		Cell copyCell = new Cell (originalCell.row, originalCell.col, originalCell.isEmpty, originalCell.isGoal, originalCell.isStart);
    		c.add(copyCell);
    	}
        State copy = new State(c, original.goal);
        return copy;
    }
	
	public static void main(String[] args) {
		Cell [][] maze = generateBoard("resources/Map.txt");
//		Cell [][] maze = generateBoard("resources/HardestMap.txt");
//		Cell [][] maze = generateBoard("resources/Map3Starts.txt");
//		Cell [][] maze = generateBoard("resources/MapAllStarts.txt");
		printMazeGUI (maze);
		State goal = findShortestPath (maze);
		String reverseDirections = new String ();
		
		//Tracing backwards from the goal state with the previous variable stored 
		//in each state, the directions stored in direction variable for each 
		//state is added to a string, creating a string of directions leading
		//backwards from the goal to the starting points.
		State curr = goal;
		while (curr.prev != null) {
			reverseDirections = reverseDirections.concat(curr.direction);
			curr = curr.prev;
		}
		System.out.println("number of steps =" + reverseDirections.length());
//		System.out.println("reverse directions=" + reverseDirections);
		
		//The string of directions are reversed to print the shortest sequence of 
		//directions from the starting points to the goal, ensuring that regardless of 
		//starting point, paths from all possible starting points will lead to the goal.
		String directions = new String();
        for(int i = reverseDirections.length() - 1; i >= 0; i--)
        {
            directions = directions + reverseDirections.charAt(i);
        }
        System.out.println("directions=" + directions);
		
	}

}
