import java.util.ArrayList;

public class State {
	ArrayList<Cell> positions;
	Cell goal;
	String direction;
	State prev;
	
	public State (ArrayList<Cell> positions, Cell goal) {
		this.positions = positions;
		this.goal = goal;
		this.direction = null;
		this.prev = null;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof State) {
			boolean equal = true;
			State toCompare = (State) o;
			for (int i = 0; i < positions.size(); i++) {
				equal = (positions.get(i).col == toCompare.positions.get(i).col) && 
						(positions.get(i).row == toCompare.positions.get(i).row);
				if (!equal) {
					return false;
				}
			}
			return equal;
		}
		return false;
	}
}
