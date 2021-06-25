import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

import java.io.*;
import java.util.*;
import java.lang.Math;

public class PathFinder extends JPanel {

	private int dim;
	private int size;
	private Cell[][] maze;
	private Ope ope;

	private FileOutputStream out = null;

	/*private int _fringe_size_max;
	private int _length;
	private int _time;
	private int _remain;*/

	private Result res = null;
	private int _remain = 0;

	class AST_Comparator implements Comparator<Cell> {
		public int compare(Cell c1, Cell c2) {
			return (int)(c1.obj - c2.obj);
		} 
	}

	private double euclideanDist(int x1, int x2, int y1, int y2) {
		return (Math.sqrt(Math.pow((x1-x2), 2) + Math.pow((y1-y2), 2)));
	}

	private int manhattanDist(int x1, int x2, int y1, int y2) {
		return Math.abs(x1-x2) + Math.abs(y1-y2);
	}

	private void dumpMazeToFile() throws IOException {
		String name = Long.toString(System.currentTimeMillis());
		File file = new File(name);
		out = new FileOutputStream(file);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));

		bw.write(Integer.toString(dim));
		bw.newLine();

		for (int i = 0; i < dim; ++i) {
			for (int j = 0; j < dim; ++j) {
				if (maze[i][j].color == Color.BLACK) {
					bw.write('1');
				} else {
					bw.write('0');
				}
			}
			bw.newLine();
		}
		bw.close();
	}

	public PathFinder(Cell[][] maze, int dim, int size, Ope ope) {
		this.dim = dim;
		this.size = size;
		this.maze = maze;
		this.ope = ope;

		this.res = new Result();

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

		long t1 = System.currentTimeMillis();

		switch (ope) {
			case BFS:
				//System.out.println("Running BFS!");
				run_bfs();
				break;
			case DFS:
				//System.out.println("Running DFS!");
				run_dfs();
				break;
			case AST1:
				//System.out.println("Running AST1!");
				run_ast(Ope.AST1);
				break;
			case AST2:
				//System.out.println("Running AST2!");
				run_ast(Ope.AST2);
				break;
			default:
				// todo;
				break;
		}

		long t2 = System.currentTimeMillis();
		res._time = (int)(t2 - t1);
		//System.out.println("--- Spend: " + res._time + " ms");

		if (maze[dim-1][dim-1].color == Color.GRAY) {
			Cell t = maze[dim-1][dim-1];
			while (t != null) {
				t = t.prev;
				res._length++;
			}
			//System.out.println("--- Length: " + res._length);
			
			if (ope == Ope.BFS || ope == Ope.DFS) {
				for (int i = 0; i < dim; ++i) {
					for (int j = 0; j < dim; ++j) {
						if (maze[i][j].color == Color.WHITE || maze[i][j].color == Color.BLACK)
							++_remain;
					}
				}
				res._search_space = dim*dim - _remain;
				//System.out.println("--- Search Space: " + res._search_space + "\n");
			} else {
				res._search_space = res._fringe_size_max;
				//System.out.println("--- Search Space: " + res._fringe_size_max + "\n");
			}
			/*try {
				dumpMazeToFile();
			}
			catch(IOException e) {
				e.printStackTrace();
			}*/
		} else {

		}

	}

	public void clearMaze() {
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

	public Result getResult() {
		return res;
	}

	private void run_bfs() {
		int i = 0, j = 0;
		int expand_number = 0;
		Cell cur = null;
		Queue<Cell> fringe = new LinkedList<>();
		fringe.add(maze[0][0]);
		maze[0][0].color = Color.YELLOW;		

		while(!fringe.isEmpty()) {
			cur = fringe.remove();
			i = cur.row;
			j = cur.col;
			if (i == dim-1 && j == dim-1) {
				maze[i][j].color = Color.GRAY;
				break;
			}
			if (i > 0 && maze[i-1][j].color == Color.WHITE) {
				maze[i-1][j].prev = maze[i][j];
				maze[i-1][j].color = Color.YELLOW;
				fringe.add(maze[i-1][j]);
				expand_number++;
			}
			if (i < dim-1 && maze[i+1][j].color == Color.WHITE) {
				maze[i+1][j].prev = maze[i][j];
				maze[i+1][j].color = Color.YELLOW;
				fringe.add(maze[i+1][j]);
				expand_number++;
			}
			if (j > 0 && maze[i][j-1].color == Color.WHITE) {
				maze[i][j-1].prev = maze[i][j];
				maze[i][j-1].color = Color.YELLOW;
				fringe.add(maze[i][j-1]);
				expand_number++;
			}
			if (j < dim-1 && maze[i][j+1].color == Color.WHITE) {
				maze[i][j+1].prev = maze[i][j];
				maze[i][j+1].color = Color.YELLOW;
				fringe.add(maze[i][j+1]);
				expand_number++;
			}
			maze[i][j].color = Color.GRAY;
			res._fringe_size_max = (fringe.size() > res._fringe_size_max) ? fringe.size() : res._fringe_size_max; 
		}
		res._expand_number = expand_number;
		//System.out.println("*** FINISH! ***");
		//System.out.println("--- Fringe Max Size " + res._fringe_size_max);
	}

	private void run_dfs() {
		int i = 0, j = 0;
		int expand_number = 0;
		Cell cur = null;
		Stack<Cell> fringe = new Stack<>();
		fringe.push(maze[0][0]);	
		maze[0][0].color = Color.YELLOW;

		while(!fringe.empty()) {
			cur = fringe.peek();
			i = cur.row;
			j = cur.col;
			if (i > 0 && maze[i-1][j].color == Color.WHITE) {
				maze[i-1][j].prev = maze[i][j];
				maze[i-1][j].color = Color.YELLOW;
				fringe.push(maze[i-1][j]);
				expand_number++;
				continue;
			}
			if (i < dim-1 && maze[i+1][j].color == Color.WHITE) {
				maze[i+1][j].prev = maze[i][j];
				maze[i+1][j].color = Color.YELLOW;
				fringe.push(maze[i+1][j]);
				expand_number++;
				continue;
			}
			if (j > 0 && maze[i][j-1].color == Color.WHITE) {
				maze[i][j-1].prev = maze[i][j];
				maze[i][j-1].color = Color.YELLOW;
				fringe.push(maze[i][j-1]);
				expand_number++;
				continue;
			}
			if (j < dim-1 && maze[i][j+1].color == Color.WHITE) {
				maze[i][j+1].prev = maze[i][j];
				maze[i][j+1].color = Color.YELLOW;
				fringe.push(maze[i][j+1]);
				expand_number++;
				continue;
			}
			fringe.pop();
			maze[i][j].color = Color.GRAY;
			res._fringe_size_max = (fringe.size() > res._fringe_size_max) ? fringe.size() : res._fringe_size_max; 
		}
		res._expand_number = expand_number;
		//System.out.println("*** FINISH! ***\n");
		//System.out.println("--- Fringe Max Size " + res._fringe_size_max);
	}

	private void run_ast(Ope ope) {
		int i = 0, j = 0;
		int expand_number = 0;
		double h = 0, g = 0;
		Cell cur = null;
		PriorityQueue<Cell> fringe = new PriorityQueue<Cell>(dim*dim, new AST_Comparator());

		if (ope != Ope.AST1 && ope != Ope.AST2)
			return;

		fringe.add(maze[0][0]);
		maze[0][0].color = Color.YELLOW;
		maze[0][0].visited = true;

		while(!fringe.isEmpty()) {
			cur = fringe.remove();
			i = cur.row;
			j = cur.col;
			if (i == dim-1 && j == dim-1) {
				maze[i][j].color = Color.GRAY;
				break;
			}
			g = maze[i][j].cnt + 1;

			if (i > 0 && maze[i-1][j].color == Color.WHITE) {
				if (ope == Ope.AST1)
					h = euclideanDist(i-1, dim-1, j, dim-1);
				else
					h = manhattanDist(i-1, dim-1, j, dim-1);
				if ((g + h) < maze[i-1][j].obj) {
					maze[i-1][j].obj = g + h;
					maze[i-1][j].cnt = g;
					maze[i-1][j].prev = maze[i][j];
					fringe.add(maze[i-1][j]);
					if (!maze[i-1][j].visited) {
						maze[i-1][j].visited = true;
						expand_number++;
					}
				}				
			}
			if (i < dim-1 && maze[i+1][j].color == Color.WHITE) {
				if (ope == Ope.AST1) 
					h = euclideanDist(i+1, dim-1, j, dim-1);
				else
					h = manhattanDist(i+1, dim-1, j, dim-1);	
				if ((g + h) < maze[i+1][j].obj) {				
					maze[i+1][j].obj = g + h;
					maze[i+1][j].cnt = g;
					maze[i+1][j].prev = maze[i][j];
					fringe.add(maze[i+1][j]);
					if (!maze[i+1][j].visited) {
						maze[i+1][j].visited = true;
						expand_number++;
					}
				}
			}
			if (j > 0 && maze[i][j-1].color == Color.WHITE) {
				if (ope == Ope.AST1) 
					h = euclideanDist(i, dim-1, j-1, dim-1);
				else
					h = manhattanDist(i, dim-1, j-1, dim-1);
				if ((g + h) < maze[i][j-1].obj) {		
					maze[i][j-1].obj = g + h;
					maze[i][j-1].cnt = g;
					maze[i][j-1].prev = maze[i][j];
					fringe.add(maze[i][j-1]);
					if (!maze[i][j-1].visited) {
						maze[i][j-1].visited = true;
						expand_number++;
					}
				}
			}
			if (j < dim-1 && maze[i][j+1].color == Color.WHITE) {
				if (ope == Ope.AST1)
					h = euclideanDist(i, dim-1, j+1, dim-1);
				else
					h = manhattanDist(i, dim-1, j+1, dim-1);
				if ((g + h) < maze[i][j+1].obj) {					
					maze[i][j+1].obj = g + h;
					maze[i][j+1].cnt = g;
					maze[i][j+1].prev = maze[i][j];
					fringe.add(maze[i][j+1]);
					if (!maze[i][j+1].visited) {
						maze[i][j+1].visited = true;
						expand_number++;
					}
				}
			}
			res._fringe_size_max = (fringe.size() > res._fringe_size_max) ? fringe.size() : res._fringe_size_max;

		}
		res._expand_number = expand_number;
		//System.out.println("*** FINISH! ***");
		//System.out.println("--- Fringe Max Size: " + res._fringe_size_max);
	}

	@Override
	public void paintComponent(Graphics g) {
		
		if (maze[dim-1][dim-1].color == Color.GRAY) {
			Cell t = maze[dim-1][dim-1];
			while (t != null) {
				g.setColor(Color.YELLOW);
				g.fillRect((t.col)*size, (t.row)*size, size, size);
				t = t.prev;
			}

		} else {
			JLabel label = new JLabel("Not Found!");
			label.setVerticalTextPosition(JLabel.CENTER);
			label.setHorizontalTextPosition(JLabel.CENTER);
			JOptionPane.showMessageDialog(null, label, "Result", JOptionPane.PLAIN_MESSAGE);
		}
	}

}
