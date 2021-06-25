/* 
 * File: SweepMineAI.java
 * Desc: AI Program of MineSweeper
 * Auth: Yujie REN
 * Date: 10/12/2018
 */

import java.util.*;
import java.lang.Math.*;

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

	ArrayList<Cell> surround;
}

class Pair {
	Cell p;
	Cell q;
}

public class SweepMineAI {
	private int size;
	private int mineNumber;

	private Cell[][] sweepMap;
	private LinkedList<Cell> decisionSet;
	private ArrayList<Cell> mineSet;
	private PriorityQueue<Cell> remainSet;

	private MineField mineField;

	int gp = 0;

	class pComparator implements Comparator<Cell> {
		public int compare(Cell c1, Cell c2) {
			return c1.p - c2.p;
		} 
	}

	// Constructor
	public SweepMineAI(MineField mineField) {
		this.mineField = mineField;
		this.size = mineField.getSize();
		this.mineNumber = mineField.getMineNum();

		// Initialize Three sets of our Knowledge Base
		decisionSet = new LinkedList<Cell>();
		mineSet = new ArrayList<Cell>();
		remainSet = new PriorityQueue<Cell>(size*size, new pComparator());

		// Initialize the Board Knowledge Base of our MineSweeper AI program
		sweepMap = new Cell[size][size];
		for (int x = 0; x < size; ++x) {
			for (int y = 0; y < size; ++y) {
				sweepMap[x][y] = new Cell();
				sweepMap[x][y].x = x;
				sweepMap[x][y].y = y;
				sweepMap[x][y].mineNum = -1;
				sweepMap[x][y].global = true;
				sweepMap[x][y].p = (int) (mineNumber*100 / (size*size));
				sweepMap[x][y].state = State.UNKNOWN;
				sweepMap[x][y].surround = new ArrayList<Cell>();

				//System.out.println("p " + sweepMap[x][y].p);
				remainSet.add(sweepMap[x][y]);
			}
		}

		// For each cell, adding reference of its surrounding Cells 
		for (int x = 0; x < size; ++x) {
			for (int y = 0; y < size; ++y) {
				int lowX  = (x - 1) < 0 ? 0 : (x - 1);
				int highX = (x + 2) > size ? size : (x + 2);
				int lowY  = (y - 1) < 0 ? 0 : (y - 1);
				int highY = (y + 2) > size ? size : (y + 2);

				for (int ii = lowX; ii < highX; ii++) {
					for (int jj = lowY; jj < highY; jj++) {
						if (ii != x || jj != y) {
							sweepMap[x][y].surround.add(sweepMap[ii][jj]);
						}
					}
				}
			}
		}

	}

	/*
	 * Update global probability
	 */
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
				//System.out.println("Global cnt: " + tmpSet.size());
				//System.out.println("Global p: " + up);
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
			// Now get a cell to uncover from decision set
			Cell cur = decisionSet.poll();

			if (mineField.isBomb(cur.x, cur.y) == false) {
				// Uncover a cell, mark it as safe and remove it from remain set
				cur.mineNum = mineField.countSurroundingMines(cur.x, cur.y);
				cur.state = State.SAFE;
				cur.global = false;
				remainSet.remove(cur);
				//System.out.println("current " + " x = " + cur.x + " y = " + cur.y);
			} else {
				// Boom! Not likely to be here ...
				cur.state = State.TRIGGER;
				//System.out.println("Boom " + " x = " + cur.x + " y = " + cur.y);
				//System.out.println("Global?: " + cur.global);
				//System.out.println("Global p: " + gp);
				//System.out.println("Boom p: " + cur.p);
				return false;
			}

			// Now calculate surrounding already covered cell
			int mine = 0, safe = 0, surCount = cur.surround.size();
			for (Cell sur: cur.surround) {
				if (sur.state == State.SAFE) {
					++safe;
				} else if (sur.state == State.MINE) {
					++mine;
				}
			}

			// The following nested if-else statements make some decisions based on information of current cell
			/*if ((surCount - safe) == cur.mineNum) {
				// Be able to determine the surrounding cells are all mine
				for (Cell sur: cur.surround) {
					if (sur.state == State.UNKNOWN) {
						sur.state = State.MINE;
						sur.global = false;
						remainSet.remove(sur);
						mineSet.add(sur);
						//System.out.println("*** Cur " + cur.x + " " + cur.y);
						//System.out.println("*** Mine " + sur.x + " " + sur.y);
					}
				}			 
			} else if ((surCount - safe) > cur.mineNum) {
				if (cur.mineNum == mine) {
					// Be able to determine the surrounding cells are all clear
					//System.out.println("wahaha");
					for (Cell sur: cur.surround) {
						if ((sur.state == State.UNKNOWN) && (!decisionSet.contains(sur))) {
							decisionSet.add(sur);
						}
					}
				} else if (cur.mineNum > mine) {
					// Cannot actually determine any surrounding cell of current cell
					for (Cell sur: cur.surround) {
						if (sur.state == State.UNKNOWN) {
							int tmp = (int)((cur.mineNum - mine)*100 / (surCount-safe-mine));
							//System.out.println("tmp = " + tmp + " cur p = " + sweepMap[ii][jj].p);
							if ((sur.global == true) || (sur.global == false && tmp > sur.p)) {
								if (remainSet.contains(sur))
									remainSet.remove(sur);
								sur.p = tmp;
								if (sur.global == true)
									sur.global = false;
								remainSet.add(sur);
								//System.out.println("gailv " + sur.p + " ii = " + sur.x + " jj = " + sur.y);
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
			}*/


			if (cur.mineNum == mine) {
				// Be able to determine the surrounding cells are all clear
				//System.out.println("wahaha");
				for (Cell sur: cur.surround) {
					if ((sur.state == State.UNKNOWN) && (!decisionSet.contains(sur))) {
						decisionSet.add(sur);
					}
				}
			} else if (cur.mineNum > mine) {
				if ((surCount - safe - mine) == (cur.mineNum - mine)) {
					// Be able to determine the surrounding cells are all mine
					for (Cell sur: cur.surround) {
						if (sur.state == State.UNKNOWN) {
							sur.state = State.MINE;
							sur.global = false;
							remainSet.remove(sur);
							mineSet.add(sur);
							//System.out.println("*** Cur " + cur.x + " " + cur.y);
							//System.out.println("*** Mine " + sur.x + " " + sur.y);
						}
					}					
				} else if ((surCount - safe - mine) > (cur.mineNum - mine)) {
					// Cannot actually determine any surrounding cell of current cell
					for (Cell sur: cur.surround) {
						if (sur.state == State.UNKNOWN) {
							int tmp = (int)((cur.mineNum - mine)*100 / (surCount-safe-mine));
							//System.out.println("tmp = " + tmp + " cur p = " + sweepMap[ii][jj].p);
							if ((sur.global == true) || (sur.global == false && tmp > sur.p)) {
								if (remainSet.contains(sur))
									remainSet.remove(sur);
								sur.p = tmp;
								if (sur.global == true)
									sur.global = false;
								remainSet.add(sur);
								//System.out.println("gailv " + sur.p + " ii = " + sur.x + " jj = " + sur.y);
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
				System.out.println("cur.x: " + cur.x + " cur.y " + cur.y);
				System.out.println("mine: " + mine + "; safe: " + safe + "; remain: " + (surCount - safe - mine) + "; Num: " + cur.mineNum);
			}


			//System.out.println("" + decisionSet.size());
			//updateGlobalProb();
		}
		// Update probability to be a mine of a global cell 
		updateGlobalProb();
		//System.out.println("\n&&&&&&&\n");

		return true;
	}

	/*
	 * Deterministic Part
	 * boolean fix
	 * Determine some cells based on updated information
	 */
	private boolean fix() {
		for (Cell rem: remainSet) {
			if (rem.global == false) {
				for (Cell sur: rem.surround) {
					if (!decisionSet.contains(sur) && (sur.state == State.SAFE)) {
						decisionSet.add(sur);
					}
				}
			}
		}

		return true;
	}

	/*
	 * Deterministic Part
	 * boolean multiInfer
	 * Determine surrounding cells based on all relative adjacent cells
	 */
	private boolean multiInfer() {
		int count = 0;
		ArrayList<Cell> inferSet = new ArrayList<Cell>();
		ArrayList<ArrayList<Cell>> combSet = new ArrayList<ArrayList<Cell>>();
		Map<Cell, Integer> equationMap;

		for (Cell rem: remainSet) {
			if (rem.global == false) {
				for (Cell sur: rem.surround) {
					if (!inferSet.contains(sur) && (sur.state == State.SAFE)) {
						inferSet.add(sur);
					}
				}
			}
		}

		//System.out.println("inferSet size: " + inferSet.size());

		for (Cell u: inferSet) {
			ArrayList<Cell> union = new ArrayList<Cell>();
			union.add(u);
			for (Cell v: inferSet) {
				if ((u != v) && (Math.abs(u.y - v.y) < 3 && Math.abs(u.x - v.x) < 3)) {
					union.add(v);
				}
			}
			combSet.add(union);
		}

		//System.out.println("combSet size: " + combSet.size());

		for (ArrayList<Cell> adjSet: combSet) {
			count = 0;
			equationMap = new HashMap<Cell, Integer>();
			int[][] A;
			int[] B;

			for (Cell c: adjSet) {
				for (Cell s: c.surround) {
					if (!equationMap.containsKey(s) && (s.state == State.UNKNOWN))
						equationMap.put(s, count++);
				}
			}

			/*for (Cell c: adjSet) {
				System.out.println("" + c.x + " " + c.y);
			}
			System.out.println("equationMap.size(): " + equationMap.size());*/

			A = new int[adjSet.size()][equationMap.size()+1];
			B = new int[adjSet.size()];

			for (Cell c: adjSet) {
				int mine = 0;
				for (Cell s: c.surround) {
					if (s.state == State.MINE)
						++mine;
				}
				B[adjSet.indexOf(c)] = c.mineNum - mine;
			}

			int i = 0, j = 0;
			for (Cell c: adjSet) {
				for (j = 0; j < equationMap.size()+1; ++j) {
					A[adjSet.indexOf(c)][j] = 0;
				}
			}

			for (Cell c: adjSet) {
				for (Cell s: c.surround) {
					if (equationMap.containsKey(s) && (s.state == State.UNKNOWN))
						A[adjSet.indexOf(c)][equationMap.get(s)] = 1;
				}
				A[adjSet.indexOf(c)][equationMap.size()] = B[adjSet.indexOf(c)];
			}

			/*for (Cell c: adjSet) {
				for (j = 0; j < equationMap.size()+1; ++j) {
					System.out.print("" + A[adjSet.indexOf(c)][j] + " ");
				}
				System.out.println();
			}*/

			i = 0;
			count = 0;
			ArrayList<Cell> dupSet = new ArrayList<Cell>();
			for (Cell m: adjSet) {
				for (Cell n: adjSet) {
					if (!m.equals(n)) {
						count = 0;
						for (i = 0; i < equationMap.size()+1; ++i) {
							if (A[adjSet.indexOf(m)][i] == A[adjSet.indexOf(n)][i])
								++count;
						}
						if (count == equationMap.size()+1 && !dupSet.contains(m) && !dupSet.contains(n)) {				
							dupSet.add(n);
						}
					}
				}
			}

			for (Cell d: dupSet) {
				adjSet.remove(d);
			}

			A = new int[adjSet.size()][equationMap.size()+1];
			B = new int[adjSet.size()];

			for (Cell c: adjSet) {
				int mine = 0;
				for (Cell s: c.surround) {
					if (s.state == State.MINE)
						++mine;
				}
				B[adjSet.indexOf(c)] = c.mineNum - mine;
			}

			i = 0;
			j = 0;
			for (Cell c: adjSet) {
				for (j = 0; j < equationMap.size()+1; ++j) {
					A[adjSet.indexOf(c)][j] = 0;
				}
			}

			for (Cell c: adjSet) {
				for (Cell s: c.surround) {
					if (equationMap.containsKey(s) && (s.state == State.UNKNOWN))
						A[adjSet.indexOf(c)][equationMap.get(s)] = 1;
				}
				A[adjSet.indexOf(c)][equationMap.size()] = B[adjSet.indexOf(c)];
			}

			//System.out.println("adjSet.size(): " + adjSet.size());

			/*for (Cell c: adjSet) {
				for (j = 0; j < equationMap.size()+1; ++j) {
					System.out.print("" + A[adjSet.indexOf(c)][j] + " ");
				}
				System.out.println();
			}*/

			if (adjSet.size() <= equationMap.size())
				A = GaussJordanSolver(A, adjSet.size(), equationMap.size()+1);

			/*System.out.println("GJ result:");
			for (i = 0; i < adjSet.size(); i++) {
			    for (j = 0; j < equationMap.size()+1; j++) {
			        System.out.print("" + A[i][j] + " ");
			    }
				System.out.println();
			}*/

			for (i = 0; i < adjSet.size(); i++) {
				count = 0;
				for (j = 0; j < equationMap.size(); j++) {
					if (A[i][j] == 1) {
						++count;
					} else if (A[i][j] == 0) {
						//continue;
					} else {
						count = -1;
						break;
					}
				}

				//System.out.print("##### count: " + count + "  ");
				//System.out.println("##### right: " + A[i][equationMap.size()]);

				/*
                 * m + n + ... + x + y + z = 0
				 * Now able to infer m, n, ... , x, y, z are all clear!
				 */
				if (count > 0 && A[i][equationMap.size()] == 0) {
					for (j = 0; j < equationMap.size(); j++) {
						if (A[i][j] == 1) {
							for (Map.Entry<Cell, Integer> entry: equationMap.entrySet()) {
								Integer I = new Integer(j);
								if (I.intValue() == entry.getValue().intValue()) {
									Cell c = entry.getKey();
									if (!decisionSet.contains(c)) {
										//System.out.println("???? infer add: " + c.x + " " + c.y);
										decisionSet.add(c);
									}
									break;
								}
							}
						} else {
							// continue;
						}
					}
			    }

				/*
                 * m + n + ... + x + y + z = N (N is the number of variables)
				 * Now able to infer m, n, ... , x, y, z are all mines!
				 */
				if (count > 0 && A[i][equationMap.size()] == count) {
					for (j = 0; j < equationMap.size(); j++) {
						if (A[i][j] == 1) {
							for (Map.Entry<Cell, Integer> entry: equationMap.entrySet()) {
								Integer I = new Integer(j);
								if (I.intValue() == entry.getValue().intValue()) {
									Cell c = entry.getKey();
									if (!mineSet.contains(c)) {
										//System.out.println("???? mine add: " + c.x + " " + c.y);
										c.state = State.MINE;
										c.global = false;
										mineSet.add(c);
										remainSet.remove(c);
									}
									break;
								}
							}
						} else {
							// continue;
						}
					}
				}

			}
		}

		return true;
	}

    private int[][] GaussJordanSolver(int[][] a, int row, int col) {
        // Gauss-Jordan elimination
        for (int p = 0; p < row; p++) {
            // find pivot row using partial pivoting
			if (a[p][p] == 0) {
		        int max = p;
		        for (int i = p + 1; i < row; i++) {
		            if (Math.abs(a[i][p]) > Math.abs(a[max][p])) {
		                max = i;
		            }
		        }

		        // exchange row p with row max
				int[] temp = a[p];
				a[p] = a[max];
				a[max] = temp;
			}

            // singular or nearly singular
            if (a[p][p] == 0) {
                continue;
                // throw new RuntimeException("Matrix is singular or nearly singular");
            }

            // pivot
		    for (int i = 0; i < row; i++) {
				if (i != p && a[i][p] != 0) {
				    for (int j = col-1; j >= 0; --j) {
						a[i][j] = a[i][j]*a[p][p] - a[p][j]*a[i][p];
				    }
				}
		    }
        }

		// pivot on last row
		int p = 0;
		while (p < col-1 && a[row-1][p] == 0)
			++p;

		if (a[row-1][p] == 0)
			return a;

	    for (int i = 0; i < row-1; i++) {
			if (a[i][p] != 0) {
			    for (int j = col-1; j >= 0; --j) {
					a[i][j] = a[i][j]*a[row-1][p] - a[row-1][j]*a[i][p];
			    }
			}
	    }

		// Scale each row
		for (int l = 0; l < row; ++l) {
			p = 0;
			while (p < col-1 && a[l][p] == 0)
				++p;

			if (a[l][p] == 0)
				continue;

			for (int j = col-1; j >= 0; --j) {
				if (a[l][j] != 0 && a[l][j] / a[l][p] == 0.0) {
					// No valuable clues
					a[l][col-1] = -100;
					break;
				}
				a[l][j] = a[l][j] / a[l][p];
			}
		}

		return a;
    }

	/*
	 * Deterministic Part
	 * boolean biInfer
	 * Determine surrounding cells based on only two adjacent cells
	 */
	private boolean biInfer() {
		int count = 0;
		ArrayList<Cell> inferSet = new ArrayList<Cell>();
		ArrayList<Pair> combSet = new ArrayList<Pair>();
		Map<Cell, Integer> equationMap;

		for (Cell rem: remainSet) {
			if (rem.global == false) {
				for (Cell sur: rem.surround) {
					if (!inferSet.contains(sur) && (sur.state == State.SAFE)) {
						inferSet.add(sur);
					}
				}
			}
		}

		//System.out.println("inferSet size: " + inferSet.size());

		for (Cell u: inferSet) {
			for (Cell v: inferSet) {
				boolean flag = true;
				if ((u != v) && ((u.x == v.x && Math.abs(u.y - v.y) == 1) || u.y == v.y && Math.abs(u.x - v.x) == 1)) {

					Pair pair = new Pair();
					pair.p = u;
					pair.q = v;

					for (Pair p: combSet) {
						if ((p.p == v && p.q == u) || (p.p == u && p.q == v)) {
							flag = false;
							break;
						}
					}

					if (flag == true) {
						//System.out.println("u: " + u.x + " " + u.y);
						//System.out.println("v: " + v.x + " " + v.y);
						combSet.add(pair);
					}
				}
			}
		}

		//System.out.println("combSet size: " + combSet.size());

		for (Pair t: combSet) {
			count = 0;
			equationMap = new HashMap<Cell, Integer>();
			int[][] A;
			int[] B;
			int[] C;

			for (Cell c: t.p.surround) {
				if (!equationMap.containsKey(c) && (c.state == State.UNKNOWN))
					equationMap.put(c, count++);
			}
			for (Cell c: t.q.surround) {
				if (!equationMap.containsKey(c) && (c.state == State.UNKNOWN))
					equationMap.put(c, count++);
			}

			A = new int[2][equationMap.size()];
			B = new int[2];
			C = new int[equationMap.size()];

			int mine = 0;
			for (Cell sur: t.p.surround) {
				if (sur.state == State.MINE)
					++mine;
			}
			B[0] = t.p.mineNum - mine;

			mine = 0;
			for (Cell sur: t.q.surround) {
				if (sur.state == State.MINE)
					++mine;
			}
			B[1] = t.q.mineNum - mine;


			int i = 0, j = 0;
			for (i = 0; i < 2; ++i) {
				for (j = 0; j < equationMap.size(); ++j) {
					A[i][j] = 0;
				}
			}

			for (Cell c: t.p.surround) {
				if (equationMap.containsKey(c) && (c.state == State.UNKNOWN))
					A[0][equationMap.get(c)] = 1;
			}
			for (Cell c: t.q.surround) {
				if (equationMap.containsKey(c) && (c.state == State.UNKNOWN))
					A[1][equationMap.get(c)] = 1;
			}

			/*for (i = 0; i < 2; ++i) {
				for (j = 0; j < equationMap.size(); ++j) {
					System.out.print("" + A[i][j] + " ");
				}
				System.out.print("  " + B[i]);
				System.out.println();
			}*/

			for (i = 0; i < equationMap.size(); ++i) {
				C[i] = A[1][i] - A[0][i];
			}

			int pos = 0, neg = 0;
			for (i = 0; i < equationMap.size(); ++i) {
				if (C[i] > 0)
					++pos;
				else if (C[i] < 0)
					++neg;
			}

			//System.out.println("???? pos neg " + pos + " " + neg);

			if ((neg > 0 && pos > 0) || (neg == 0 && pos == 0))
				continue;
			
			//System.out.println("???? WTF");

			if (B[0] - B[1] == 0) {
				for (i = 0; i < equationMap.size(); ++i) {
					if (C[i] != 0) {
						for (Map.Entry<Cell, Integer> entry: equationMap.entrySet()) {
							Integer I = new Integer(i);
							if (I.intValue() == entry.getValue().intValue()) {
								Cell c = entry.getKey();
								if (!decisionSet.contains(c)) {
									//System.out.println("???? infer add: " + c.x + " " + c.y);
									decisionSet.add(c);
								}
								break;
							}
						}
					}
				}
				updateGlobalProb();
			} else {
				int idx = 0;

				if (pos + neg > 1) {
					break;
				}
				for (i = 0; i < equationMap.size(); ++i) {
					if (C[i] != 0) {
						idx = i;
						break;		
					}
				}
				for (Map.Entry<Cell, Integer> entry: equationMap.entrySet()) {
					Integer I = new Integer(idx);
					if (I.intValue() == entry.getValue().intValue()) {
						Cell c = entry.getKey();
						if (!mineSet.contains(c)) {
							//System.out.println("???? mine add: " + c.x + " " + c.y);
							c.state = State.MINE;
							c.global = false;
							mineSet.add(c);
							remainSet.remove(c);
						}
						break;
					}
				}
				updateGlobalProb();
			}
		}

		return true;
	}

	/*
	 * Deterministic Solver
	 * boolean determineSolver
	 * Solves the current board deterministicly until there is no enough information
	 */
	private boolean determineSolver() {
		int counter = 0;
		do {
			counter = remainSet.size();

			if (expand() == true) {
				fix();
			} else {
				return false;
			}

			if (remainSet.size() == mineNumber - mineSet.size() && remainSet.size() > 0) {
				// Now be able to determine the rest are all mines!
				for (Cell rem: remainSet) {
					rem.state = State.MINE;
					mineSet.add(rem);
				}
				remainSet.clear();
				//System.out.println("hehehe");
				return true;
			}

			if (mineNumber == mineSet.size() && remainSet.size() > 0) {
				// Now be able to determine the rest are all clear!
				for (Cell rem: remainSet) {
					rem.state = State.SAFE;
				}
				remainSet.clear();
				//System.out.println("hahaha");
				return true;
			}

			// Make logic inference
			// If we do logic inference here, we'll get more success rate, but the AI will run slower
			//multiInfer();

			//System.out.println("remainSet size: " + remainSet.size());
		} while (counter != remainSet.size());

		// Make logic inference
		// If we do logic inference here, we'll get less success rate, but the AI will run faster
		//multiInfer();

		return true;
	}

	/*
	 * Uncertainty Solver
	 * boolean uncertainSolver
	 * After the deterministic Solver, select an uncovered cell with lowest probability to be a mine 
	 */
	private boolean uncertainSolver() {
		while (!remainSet.isEmpty()) {
			//System.out.println("Now Guessing: " + remainSet.peek().x + " " + remainSet.peek().y);
			decisionSet.add(remainSet.poll());
			if (determineSolver() == false) {
				return false;
			} else {
				//decisionSet.clear();
			}
		}
		return true;
	}

	public Cell[][] sweepMine() {
		int i = new Random().nextInt(size-1);
		int j = new Random().nextInt(size-1);

		// 2 2 is used to test infer()
		//decisionSet.add(sweepMap[2][2]);

		decisionSet.add(sweepMap[i][j]);
		if (determineSolver() == true)
			uncertainSolver();

		return sweepMap;
	}

	public boolean sweepMineTest() {
		boolean res = false;

		int i = new Random().nextInt(size-1);
		int j = new Random().nextInt(size-1);

		decisionSet.add(sweepMap[i][j]);
		if (determineSolver() == true)
			res = uncertainSolver();

		return res;
	}

	public static void main(String[] args) {
		int win = 0, size = 0, mineNumber = 0;
		SweepMineAI ai;
		MineField mineField;

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

		for (int i = 1; i <= 1000; ++i) {
			// Initialize a new Board with given size and mineNumber
		    mineField = new MineField(size, mineNumber);
			// Initialize an AI of MineSweeper
			ai = new SweepMineAI(mineField);
			// Just run MineSweeper AI
			if (true == ai.sweepMineTest()) {
				++win;
			}
			System.out.println("its: " + i + "/1000");
		}

		System.out.println("**** win " + win + " out of 1000 ****");

	}

}
