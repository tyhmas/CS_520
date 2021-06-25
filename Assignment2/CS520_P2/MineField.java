/* 
 * File: MineField.java
 * Desc: Board of an MineSweeper Game
 * Auth: Yujie REN
 * Date: 10/12/2018
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;

class MineField {
    private int size;

    // Records bomb locations.
    private boolean[][] mineMap;

    // Records whether this location has been exposed.
    private boolean[][] exposeMap;
    private int mineNumber;
    private Random r = new Random();

    MineField(int size, int mineNumber) {
        this.size = size;
        this.mineNumber = mineNumber;

        mineMap = new boolean[size][size];
        exposeMap = new boolean[size][size];
        ArrayList<Point> locations = new ArrayList<>();
        for (int ii = 0; ii < size; ii++) {
            for (int jj = 0; jj < size; jj++) {
                mineMap[ii][jj] = false;
                // must change this to false for the actual game.
				exposeMap[ii][jj] = false;
                Point p = new Point(ii, jj);
                locations.add(p);
            }
        }
        Collections.shuffle(locations, r);
        for (int ii = 0; ii < mineNumber; ii++) {
            Point p = locations.get(ii);
            mineMap[p.x][p.y] = true;
			//exposeMap[p.x][p.y] = true;
        }

		/*mineMap[4][1] = true;
		mineMap[5][4] = true;
		mineMap[6][0] = true;
		mineMap[6][3] = true;
		mineMap[7][4] = true;
		mineMap[7][5] = true;*/

		/*mineMap[2][7] = true;
		mineMap[4][2] = true;
		mineMap[4][3] = true;
		mineMap[5][1] = true;
		mineMap[6][5] = true;
		mineMap[7][5] = true;*/

		/*mineMap[7][2] = true;
		mineMap[6][2] = true;
		mineMap[5][2] = true;
		mineMap[5][3] = true;
		mineMap[5][4] = true;
		mineMap[6][4] = true;
		mineMap[7][4] = true;*/

		/*mineMap[0][3] = true;
		mineMap[0][7] = true;
		mineMap[1][0] = true;
		mineMap[1][1] = true;
		mineMap[1][5] = true;
		mineMap[1][6] = true;
		mineMap[1][8] = true;
		mineMap[1][9] = true;
		mineMap[2][4] = true;
		mineMap[4][3] = true;
		mineMap[4][5] = true;
		mineMap[4][6] = true;
		mineMap[4][7] = true;
		mineMap[5][0] = true;
		mineMap[6][0] = true;
		mineMap[6][3] = true;
		mineMap[6][7] = true;
		mineMap[7][2] = true;
		mineMap[7][3] = true;
		mineMap[9][3] = true;*/

		// Logic Inference
		// 5x5
		/*mineMap[0][0] = true;
		mineMap[0][3] = true;
		mineMap[2][0] = true;
		mineMap[2][3] = true;*/

		// 10x10
		/*mineMap[0][4] = true;
		mineMap[0][5] = true;
		mineMap[0][8] = true;		
		mineMap[1][5] = true;
		mineMap[2][0] = true;
		mineMap[3][3] = true;
		mineMap[3][8] = true;
		mineMap[4][3] = true;
		mineMap[4][7] = true;
		mineMap[5][5] = true;
		mineMap[5][6] = true;
		mineMap[6][1] = true;
		mineMap[6][3] = true;
		mineMap[6][4] = true;
		mineMap[6][6] = true;
		mineMap[7][0] = true;
		mineMap[7][1] = true;
		mineMap[7][2] = true;		
		mineMap[8][5] = true;
		mineMap[8][8] = true;*/

		// 10x10
		/*mineMap[0][0] = true;
		mineMap[0][4] = true;
		mineMap[1][1] = true;		
		mineMap[1][9] = true;
		mineMap[2][8] = true;
		mineMap[3][3] = true;
		mineMap[3][4] = true;
		mineMap[3][7] = true;
		mineMap[3][8] = true;
		mineMap[4][2] = true;
		mineMap[4][3] = true;
		mineMap[4][5] = true;
		mineMap[5][2] = true;
		mineMap[5][6] = true;
		mineMap[5][8] = true;
		mineMap[6][5] = true;
		mineMap[8][0] = true;
		mineMap[8][1] = true;		
		mineMap[8][3] = true;
		mineMap[9][1] = true;*/

    }

    public boolean isBomb(int x, int y) {
        return mineMap[x][y];
    }

    public boolean isExposed(int x, int y) {
        return exposeMap[x][y];
    }

	public void setExposed(int x, int y) {
		exposeMap[x][y] = true;
	}

	public void clearExposed(int x, int y) {
		exposeMap[x][y] = false;
	}

    public int getSize() {
        return size;
    }

	public int getMineNum() {
		return mineNumber;
	}

    public int countSurroundingMines(int x, int y) {
		int lowX  = (x - 1) < 0 ? 0 : (x - 1);
		int highX = (x + 2) > size ? size : (x + 2);
		int lowY  = (y - 1) < 0 ? 0 : (y - 1);
		int highY = (y + 2) > size ? size : (y + 2);

        int count = 0;
        for (int ii = lowX; ii < highX; ii++) {
            for (int jj = lowY; jj < highY; jj++) {
                if (ii != x || jj != y) {
                    if (mineMap[ii][jj]) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

}
