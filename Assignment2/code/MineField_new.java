import java.awt.*;
import java.awt.event.*;
import java.util.*;

class MineField_new {
    private int size;

    // Records bomb locations.
    private boolean[][] mineMap;

    // Records whether this location has been exposed.
    private boolean[][] exposeMap;
    private int mineNumber;
    private Random r = new Random();

    MineField_new(int size, int mineNumber) {
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
        int lowX = x - 1;
        lowX = lowX < 0 ? 0 : lowX;
        int highX = x + 2;
        highX = highX > size ? size : highX;

        int lowY = y - 1;
        lowY = lowY < 0 ? 0 : lowY;
        int highY = y + 2;
        highY = highY > size ? size : highY;

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
