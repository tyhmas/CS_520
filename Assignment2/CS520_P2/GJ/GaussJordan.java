import java.util.*;
import java.lang.Math.*;

public class GaussJordan {

    private static int[][] GaussJordanSolver(int[][] a, int row, int col) {
        // Gauss-Jordan elimination
        for (int p = 0; p < row; p++) {
            // find pivot row using partial pivoting
            int max = p;
            for (int i = p + 1; i < row; i++) {
                if (a[i][p] == 1) {
                    max = i;
					break;
                } else if (a[i][p] == -1) {
					for (int j = 0; j < col; j++) {
						a[i][j] /= -1;
					}
                    max = i;
					break;
				}
            }
            /*for (int i = p + 1; i < row; i++) {
                if (Math.abs(a[i][p]) > Math.abs(a[max][p])) {
                    max = i;
                }
            }*/

            // exchange row p with row max
		    int[] temp = a[p];
		    a[p] = a[max];
		    a[max] = temp;

            // singular or nearly singular
            if (a[p][p] == 0) {
                continue;
                // throw new RuntimeException("Matrix is singular or nearly singular");
            }

            // pivot
		    for (int i = 0; i < row; i++) {
		        for (int j = col-1; j >= 0; --j) {
		            if (i != p && j != p)
						a[i][j] = a[i][j]*a[p][p] - a[p][j]*a[i][p];
		        }
		    }

		    /*for (int i = 0; i < row; i++) {
				if (i != p && a[i][p] != 0) {
				    for (int j = col-1; j >= 0; --j) {
						a[i][j] = a[i][j]*a[p][p] - a[p][j]*a[i][p];
				    }
				}
		    }*/

		    // zero out column p
		    for (int i = 0; i < row; i++) {
		        if (i != p) 
					a[i][p] = 0;
			}

		    // scale row p (ok to go from p+1 to N, but do this for consistency with simplex pivot)
		    /*for (int j = 0; j < col; j++) {
		        if (j != p) 
					a[p][j] /= a[p][p];
			}
		    a[p][p] = 1;*/
        }
		
		/*int p = 0;
		while (p < col-1 && a[row-1][p] == 0)
			++p;

		if (a[row-1][p] == 0)
			return a;

		for (int i = 0; i < row-1; i++) {
			if (a[i][p] != 0) {
				for (int j = 0; j < col; j++) {
					a[i][j] = a[i][j]*a[p][p] - a[p][j]*a[i][p];
				}
			}
		}*/

		int p = 0;
		while (p < col-1 && a[row-1][p] == 0)
			++p;

		if (a[row-1][p] == 0)
			return a;

	    for (int i = 0; i < row-1; i++) {
	        for (int j = col-1; j >= 0; --j) {
	            if (i != p && j != p)
					a[i][j] = a[i][j]*a[row-1][p] - a[row-1][j]*a[i][p];
	        }
	    }

	    /*for (int i = 0; i < row-1; i++) {
			if (a[i][p] != 0) {
			    for (int j = col-1; j >= 0; --j) {
					a[i][j] = a[i][j]*a[row-1][p] - a[row-1][j]*a[i][p];
			    }
			}
	    }*/

	    // zero out column p
	    for (int i = 0; i < row-1; i++) {
	        if (i != p) 
				a[i][p] = 0;
		}

		// Scale
		for (int l = 0; l < row; ++l) {
			p = 0;
			while (p < col-1 && a[l][p] == 0)
				++p;

			if (a[l][p] == 0)
				continue;

			for (int j = col-1; j >= 0; --j) {
				a[l][j] = (int) Math.ceil(a[l][j] / a[l][p]);
			}
	
		}


		return a;
    }

	public static void main(String[] args) {
		int row = 6, col = 15;

		/*int A[][] = new int[][]{
			{0,1,0,0,1,1},
			{1,0,1,1,0,1},
			{1,1,1,1,0,2}
		};*/		

		int A[][] = new int[][]{
			{1,1,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{0,1,1,1,1,1,1,0,0,0,0,0,0,0,1},
			{1,0,1,0,0,0,0,1,1,1,1,0,0,0,2},
			{0,1,0,0,0,0,1,0,0,0,0,1,1,1,2},
			{1,0,0,0,0,0,0,0,0,1,0,0,0,0,1},
			{1,1,1,1,0,0,0,0,0,0,1,0,0,0,2}
		};

		/*int A[][] = new int[][]{
			{3,1,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{2,1,1,1,0,0,0,0,0,0,1,0,0,0,2}
		};*/

		for (int i = 0; i < row; ++i) {
			for (int j = 0; j < col; ++j) {
				System.out.print("" + A[i][j] + " ");
			}
			System.out.println();
		}		

		System.out.println();

		A = GaussJordanSolver(A, row, col);



		for (int i = 0; i < row; ++i) {
			for (int j = 0; j < col; ++j) {
				System.out.print("" + A[i][j] + " ");
			}
			System.out.println();
		}

	}

}
