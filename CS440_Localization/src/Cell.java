
public class Cell {
	int row;
	int col;
	boolean isEmpty; //true if Cell is open to move on
	boolean isGoal;
	boolean isStart;
	
	public Cell (int row, int col, boolean isEmpty, boolean isGoal, boolean isStart) {
		this.row = row;
		this.col = col;
		this.isEmpty = isEmpty;
		this.isGoal = isGoal;
		this.isStart = isStart;
	}
	
}
