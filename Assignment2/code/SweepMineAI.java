import java.util.*;

enum State {
	UNKNOWN, SAFE, MINE, TRIGGER;
}

class Cell {
	int x;
	int y;
	State state;
	int mineNum;
	boolean global;
	int p;
}

public class SweepMineAI {
	private int size;
	private int mineNumber;

	private Cell[][] sweepMap;
	private LinkedList<Cell> decisionSet;
	private LinkedList<Cell> mineSet;
	private PriorityQueue<Cell> remainSet;

	private MineField_new mineField;

	int gp = 0;

	class pComparator implements Comparator<Cell> {
		public int compare(Cell c1, Cell c2) {
			return c1.p - c2.p;
		} 
	}

	SweepMineAI(MineField_new mineField) {
		this.mineField = mineField;
		this.size = mineField.getSize();
		
		mineNumber = mineField.getMineNum();

		decisionSet = new LinkedList<Cell>();
		mineSet = new LinkedList<Cell>();
		remainSet = new PriorityQueue<Cell>(size*size, new pComparator());

		sweepMap = new Cell[size][size];
		for (int ii = 0; ii < size; ++ii) {
			for (int jj = 0; jj < size; ++jj) {
				sweepMap[ii][jj] = new Cell();
				sweepMap[ii][jj].x = ii;
				sweepMap[ii][jj].y = jj;
				sweepMap[ii][jj].mineNum = -1;
				sweepMap[ii][jj].global = true;
				sweepMap[ii][jj].p = (int) (mineNumber*100 / (size*size));
				System.out.println("p " + sweepMap[ii][jj].p);
				sweepMap[ii][jj].state = State.UNKNOWN;
				remainSet.add(sweepMap[ii][jj]);
			}
		}
	}

	private void updateGlobalProb() {
		if (!remainSet.isEmpty()) {
			LinkedList<Cell> tmpSet = new LinkedList<Cell>();
			int up = (int) ((mineNumber - mineSet.size())*100 / remainSet.size());
			for (Cell c: remainSet) {
				if (c.global == true) {
					c.p = up;
					tmpSet.add(c);
				}
			}
			for (Cell c: tmpSet) {
				remainSet.remove(c);
				remainSet.add(c);
			}
			if (!tmpSet.isEmpty()) {
				System.out.println("Global p: " + up);
				gp = up;
			}
		}
	}

	/*
	 * Deterministic Part
	 * boolean expand(int, int)
	 * Expand a cell determined to be safe
	 */
	private boolean expand() {
		int x, y;
		while (!decisionSet.isEmpty()) {

			x = decisionSet.peek().x;
			y = decisionSet.peek().y;

			decisionSet.poll();

			int surCount = 0;
		    int lowX = x - 1;
		    lowX = lowX < 0 ? 0 : lowX;
		    int highX = x + 2;
		    highX = highX > size ? size : highX;

		    int lowY = y - 1;
		    lowY = lowY < 0 ? 0 : lowY;
		    int highY = y + 2;
		    highY = highY > size ? size : highY;

			if (mineField.isBomb(x, y) == false) {
				// Uncover a cell
				sweepMap[x][y].mineNum = mineField.countSurroundingMines(x, y);
				sweepMap[x][y].state = State.SAFE;
				sweepMap[x][y].global = false;
				remainSet.remove(sweepMap[x][y]);
				System.out.println("current " + " x = " + x + " y = " + y);
			} else {
				// Boom! Not likely to be here ...
				sweepMap[x][y].state = State.TRIGGER;
				System.out.println("Global p: " + gp);
				System.out.println("Boom p: " + sweepMap[x][y].p);
				return false;
			}

		    int mine = 0, safe = 0;
		    for (int ii = lowX; ii < highX; ii++) {
		        for (int jj = lowY; jj < highY; jj++) {
					if (ii != x || jj != y) {
						++surCount;
						if (sweepMap[ii][jj].state == State.SAFE)
							++safe;
						if (sweepMap[ii][jj].state == State.MINE)
							++mine;
					}
		        }
		    }
			// System.out.println("***" + surCount);

			if ((surCount - safe) == sweepMap[x][y].mineNum) {
				// Able to etermine Mines
				for (int ii = lowX; ii < highX; ii++) {
				    for (int jj = lowY; jj < highY; jj++) {
						if ((ii != x || jj != y) && (sweepMap[ii][jj].state == State.UNKNOWN)) {
							sweepMap[ii][jj].state = State.MINE;
							sweepMap[ii][jj].global = false;
							remainSet.remove(sweepMap[ii][jj]);
							mineSet.add(sweepMap[ii][jj]);
							System.out.println("*** Cur " + x + " " + y);
							System.out.println("*** Mine " + ii + " " + jj);
						}
				    }
				}			 
			} else if ((surCount - safe) > sweepMap[x][y].mineNum) {
				if (sweepMap[x][y].mineNum == mine) {
					System.out.println("wahaha");
					for (int ii = lowX; ii < highX; ii++) {
						for (int jj = lowY; jj < highY; jj++) {
							if ((ii != x || jj != y) && (sweepMap[ii][jj].state == State.UNKNOWN)) {
								if (!decisionSet.contains(sweepMap[ii][jj])) {
									decisionSet.add(sweepMap[ii][jj]);
								}
							}
						}
					}
				} else if (sweepMap[x][y].mineNum > mine) {
					// continue;
					for (int ii = lowX; ii < highX; ii++) {
						for (int jj = lowY; jj < highY; jj++) {
							if ((ii != x || jj != y) && (sweepMap[ii][jj].state == State.UNKNOWN)) {
								int tmp = (int)((sweepMap[x][y].mineNum - mine)*100 / (surCount-safe-mine));
								//System.out.println("tmp = " + tmp + " cur p = " + sweepMap[ii][jj].p);
								if ((sweepMap[ii][jj].global == true) || (sweepMap[ii][jj].global == false && tmp > sweepMap[ii][jj].p)) {
									if (remainSet.contains(sweepMap[ii][jj]))
										remainSet.remove(sweepMap[ii][jj]);
									sweepMap[ii][jj].p = tmp;
									if (sweepMap[ii][jj].global == true)
										sweepMap[ii][jj].global = false;
									remainSet.add(sweepMap[ii][jj]);
									System.out.println("gailv " + sweepMap[ii][jj].p + " ii = " + ii + " jj = " + jj);
								}
							}
						}
					}
					
				} else {
					// Should never be here
					System.out.println("error1");
				}
			} else {
				// Should never be here
				System.out.println("error2");
			}

			System.out.println("" + decisionSet.size());

			// Update probability to be a mine of a global cell 
			updateGlobalProb();

		}

		System.out.println("\n&&&&&&&\n");

		return true;
	}

	/*
	 * Deterministic Part
	 * boolean fix
	 * Determine some cells based on updated information and logical inferrence
	 */
	private boolean fix() {
		int counter = 0;
		do {
			counter = remainSet.size();

			for (Cell c: remainSet) {
				int x = c.x;
				int y = c.y;

				int lowX = x - 1;
				lowX = lowX < 0 ? 0 : lowX;
				int highX = x + 2;
				highX = highX > size ? size : highX;

				int lowY = y - 1;
				lowY = lowY < 0 ? 0 : lowY;
				int highY = y + 2;
				highY = highY > size ? size : highY;
				for (int ii = lowX; ii < highX; ii++) {
					for (int jj = lowY; jj < highY; jj++) {
						if (!decisionSet.contains(sweepMap[ii][jj]) && (sweepMap[ii][jj].state == State.SAFE))
							decisionSet.add(sweepMap[ii][jj]);
					}
				}
			}


			if (remainSet.size() == mineNumber - mineSet.size()) {
				// Now able to determine the rest of this game !
				//System.out.println("nitamashibushishabi " + unknownCounter + " " + remainMineCounter);
				for (Cell c: remainSet) {
					int ii = c.x;
					int jj = c.y;
					if (mineNumber - mineSet.size() != 0) {
						sweepMap[ii][jj].state = State.MINE;
						mineSet.add(sweepMap[ii][jj]);
					} else {
						sweepMap[ii][jj].state = State.SAFE;
					}
					//remainSet.remove(sweepMap[ii][jj]);
				}
				return true;
			}

			// Call expand() again if there's any update information to determine a mine
			expand();

			System.out.println("remainSet size: " + remainSet.size());
		} while (counter != remainSet.size());

		

		return false;
	}

	/*
	 * Deterministic Part
	 * boolean infer
	 * Determine some cells based on updated information and logical inferrence
	 */
	private boolean infer() {
		return true;
	}

	private boolean determineSolver() {
		if (expand() == true) {
			if (fix() != true) {
				return false;
			}
		}
		return true;
	}

	private boolean uncertainSolver() {
		while (!remainSet.isEmpty()) {
			int x = remainSet.peek().x;
			int y = remainSet.peek().y;
			decisionSet.add(remainSet.poll());

			if (determineSolver() == true)
				return true;
		}
		return true;
	}

	public Cell[][] sweepMine() {
		/*int start = new Random().nextInt(3);
		switch (start) {
			case 0:
				decisionSet.add(sweepMap[0][0]);
				determineSolver(0, 0);
				uncertainSolver();
				break;
			case 1:
				decisionSet.add(sweepMap[0][size-1]);
				determineSolver(0, size-1);
				uncertainSolver();
				break;
			case 2:
				decisionSet.add(sweepMap[0][size-1]);
				determineSolver(size-1, 0);
				uncertainSolver();
				break;
			case 3:
				decisionSet.add(sweepMap[0][size-1]);
				determineSolver(size-1, size-1);
				uncertainSolver();
				break;
		}*/

		//int i = new Random().nextInt(size-1);
		//int j = new Random().nextInt(size-1);

		decisionSet.add(sweepMap[0][0]);
		determineSolver();
		uncertainSolver();

		return sweepMap;
	}

	private boolean win(Cell[][] res) {
		for (int ii = 0; ii < size; ++ii) {
		    for (int jj = 0; jj < size; ++jj) {
				if (res[ii][jj].state == State.TRIGGER) {
					return false;
				} else {
					// Todo
				}
		    }
		}
		return true;
	}

	public static void main(String[] args) {
		int win = 0, size = 0, mineNumber = 0;
		SweepMineAI ai;
		MineField_new mineField;

		if (args.length == 2) {
			try {
				size = Integer.parseInt(args[0]);
				mineNumber = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				System.err.println("Invalid Input: Please enter numbers.");
				return;
			}
		} else if (args.length == 1) {
			// Todo
			return;
		} else {
			System.err.println("Wrong Input Number.");
			return;
		}

		for (int i = 0; i < 1000; ++i) {
		    mineField = new MineField_new(size, mineNumber);

			// AI of MineSweeper
			ai = new SweepMineAI(mineField);

			if (true == ai.win(ai.sweepMine())) {
				++win;
			}
		}

		System.out.println("**** win " + win + " out of 1000 ****");

	}

}
