import java.awt.Color;
import java.util.*;

enum State {
	UNKNOWN, SAFE, CLEAR, MINE, TRIGGER;
}

class Cell {
	State state;
	Color color;
	int   mineNum;	
	float p;
}

public class SweepMineAI {
	private int size;
	private int mineNumber;
	
	private int counter;

	private Cell[][] sweepMap;

	private MineField mineField;

	SweepMineAI(MineField mineField) {
		this.mineField = mineField;
		this.size = mineField.getSize();
		sweepMap = new Cell[size][size];
		for (int ii = 0; ii < size; ++ii) {
			for (int jj = 0; jj < size; ++jj) {
				sweepMap[ii][jj] = new Cell();
				sweepMap[ii][jj].color = Color.WHITE;
				sweepMap[ii][jj].mineNum = -1;
				sweepMap[ii][jj].p = 0.0f;
				sweepMap[ii][jj].state = State.UNKNOWN;
			}
		}
	}

	public Cell[][] sweepMine() {
		//int start = new Random().nextInt(3);
		for (int ii = 0; ii < size; ++ii) {
			for (int jj = 0; jj < size; ++jj) {
				if (mineField.isBomb(ii, jj) == false) {
					sweepMap[ii][jj].mineNum = mineField.countSurroundingMines(ii, jj);
					/*if (ii == 1 && jj == 1)
						sweepMap[ii][jj].state = State.MINE;
					else*/
						sweepMap[ii][jj].state = State.SAFE;
				} else {
					sweepMap[ii][jj].state = State.TRIGGER;
					return sweepMap;
				}
			}
		}

		return sweepMap;
	}



}
