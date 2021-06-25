import java.awt.Color;
import java.awt.Graphics;
import java.io.*;
import java.util.Random;
import javax.swing.JPanel;

public class Maze extends JPanel {
	
	private int dim;
	private float p;
	private int size;
	private Cell[][] maze;

	public Maze(int dim, float p, int size) {
		this.dim = dim;
		this.p = p;
		this.size = size;
		this.maze = new Cell[dim][dim];

		for (int i = 0; i < dim; ++i) {
			for (int j = 0; j < dim; ++j) {
				maze[i][j] = new Cell();
				maze[i][j].row = i;
				maze[i][j].col = j;
				maze[i][j].obj = Integer.MAX_VALUE;
				maze[i][j].cnt = 0;
				maze[i][j].prev = null;
				maze[i][j].visited = false;
				if ((i == 0 && j == 0) || (i == dim-1 && j == dim-1)) {
					maze[i][j].color = Color.WHITE;
					continue;
				}
				int num = new Random().nextInt(9);
				if (num < (int)(10*p)) {
					maze[i][j].color = Color.BLACK;
				} else {
					maze[i][j].color = Color.WHITE;
				}
			}
		}

	}

	public Maze(int dim, float p, int size, Cell[][] m) {
		this.dim = dim;
		this.p = p;
		this.size = size;
		this.maze = m;
	}

	public int getDim() {
		return this.dim;
	}
	
	public float getP() {
		return this.p;
	}
	
	public Cell[][] getMaze() {
		return maze;
	}

	public void clear() {
		for (int i = 0; i < dim; ++i) {
			for (int j = 0; j < dim; ++j) {
				maze[i][j].cnt = 0;
				maze[i][j].obj = Integer.MAX_VALUE;
				maze[i][j].prev = null;
				maze[i][j].visited = false;
				if (maze[i][j].color != Color.BLACK) {
					maze[i][j].color = Color.WHITE;
				}
			}
		}
	}
	
	public Maze duplicate() {
		Maze duplicate = new Maze(this.dim,this.p,0);
		
		//reset maze
		for (int i = 0; i < dim; ++i) {
			for (int j = 0; j < dim; ++j) {
				duplicate.maze[i][j].color=this.maze[i][j].color;
				duplicate.maze[i][j].cnt = 0;
				duplicate.maze[i][j].obj = Integer.MAX_VALUE;
				duplicate.maze[i][j].prev = null;
				if (duplicate.maze[i][j].color != Color.BLACK) {
					duplicate.maze[i][j].color = Color.WHITE;
				}
			}
		}
		return duplicate;
	}
	
	public void printMaze() {
		Cell t = maze[dim-1][dim-1];
		while (t != null) {
			t.color = Color.BLUE;
			t = t.prev;
		}
		
		for (int i = 0; i < dim; ++i) {
			for (int j = 0; j < dim; ++j) {
				if(this.maze[i][j].color==Color.BLACK) {
					System.out.print("X");
				}else if(this.maze[i][j].color==Color.BLUE){
					System.out.print("P");
				}else {
					System.out.print("O");
				}
			}
			System.out.print("\n");
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		int boundary = size * dim;
		for (int i = 0; i <= boundary; i += size) {
			g.drawLine(0, i, boundary, i);
			g.drawLine(i, 0, i, boundary);
		}
		for (int i = 0; i < dim; ++i) {
			for (int j = 0; j < dim; ++j) {
				if (maze[i][j].color == Color.BLACK) {
					g.setColor(Color.BLACK);
					g.fillRect(j*size, i*size, size, size);
				}
			}
		}
	}
}
