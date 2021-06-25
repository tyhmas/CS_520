import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFrame;
import java.awt.event.*;

public class Localize_Canvas extends Canvas{
	private final int ROW = 43;
	private final int COL = 57;
	private final int G = 3; //To indicate the goal in the maze
	private final int size = 22;
	private final int BLOCK = -1;
	private int maze[][];
	private int tracking_maze[][];
	private int steps; //record how many steps to have at least 1/2 of people in the maze arrive at G
	private String filePath = "D:\\CS520\\Final_Exam\\Question1\\Maze.txt";

	//Count the number of surrounding blocks of a given cell
	public int countSurroundingBlock(int maze[][], int x, int y) {
		int lowX  = (x - 1) < 0 ? 0 : (x - 1);
		int highX = (x + 2) > ROW ? ROW : (x + 2);
		int lowY  = (y - 1) < 0 ? 0 : (y - 1);
		int highY = (y + 2) > COL ? COL : (y + 2);

        int count = 0;
        for (int ii = lowX; ii < highX; ++ii) {
            for (int jj = lowY; jj < highY; ++jj) {
				if ((ii != x || jj != y) && (maze[ii][jj] == 1))
					++count;
			}
		}
		return count;
	}

	public Localize_Canvas() {
		File file = new File(filePath);

		maze = new int[ROW][COL];
		tracking_maze = new int[ROW][COL];
		steps = 0;
		
		int i = 0, j = 0;

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				for (j = 0; j < line.length(); ++j) {
					char c = line.charAt(j);
					if (c == '0') {
						maze[i][j] = 0;
					} else if (c == 'G') {
						maze[i][j] = G;
					}
					else{
						maze[i][j] = 1;
					}
				}
				++i;
			}
		} catch (IOException e) {
    		e.printStackTrace();
		}
		
		for (i = 0; i < ROW; i++) {
			for (j = 0; j < COL; j++) {
				if (maze[i][j] == 0 || maze[i][j] == G) 
					tracking_maze[i][j] = 1; //Put one person in each white cell initially
				else
					tracking_maze[i][j] = BLOCK; //-1 indicates the block in the maze
			}
		}
	}
	
	public int countRequiredCells () {
		int count = 0; 
		for (int i = 0; i < ROW; ++i) {
			for (int j = 0; j < COL; ++j) {
				if (maze[i][j] != 1) {
					if (countSurroundingBlock(maze, i, j) == 5) {
						//Case 1: move left->hit the wall->hit the wall
						if (maze[i][j-1] == 1) {
							++count;
							maze[i][j] = 2;
							continue;
						} else if (countSurroundingBlock(maze, i, j-1) == 5) {
							//Case 2: move left->move left->hit the wall
							if (maze[i][j-2] == 1) {
								++count;
								maze[i][j] = 2;
								continue;
							//Case 3: move left->move left->move left
							} else if (countSurroundingBlock(maze, i, j-2) == 5) {
								++count;
								maze[i][j] = 2;
							}						
						}
					}
				}
			}
		}
		return count;
	}

	public void paint(Graphics g) {
		super.paint(g);
		int count = 0;

		int boundary = size * ROW;
		for (int i = 0; i <= boundary; i += size) {
			g.drawLine(0, i, COL*size, i);
		}
		boundary = size * COL;
		for (int i = 0; i <= boundary; i += size) {
			g.drawLine(i, 0, i, ROW*size);
		}

		for (int i = 0; i < ROW; ++i) {
			for (int j = 0; j < COL; ++j) {
				if (maze[i][j] == 1) {
					g.setColor(Color.BLACK);
					g.fillRect(j*size, i*size, size, size);
				} 
				else if (maze[i][j] == 0 || maze[i][j] == G) {
					g.setColor(Color.GREEN);
					g.fillRect(j*size, i*size, size, size);
					g.setColor(Color.BLACK);
					int sum_of_persons = tracking_maze[i][j];
					String s = new String(" " + sum_of_persons + "");
					g.drawString(s, j*size + size, i*size);
				    g.drawString(s, j*size, i*size + size);
				} 
			}
		}
	}
	

	public void showSteps() {
		System.out.println("The total number of steps so far is: " + steps);
	}
	
	//Control the movement of people in the maze
	public void people_move (int horizon, int vertical) {
		int i,j;
		if (horizon != 0) {
			if (horizon == -1) {//Move down
				for (j = 1; j <= COL - 2; j++) {
					for(i = ROW - 2; i >= 1; i--) {
						if (tracking_maze[i][j] != BLOCK) {
							if (!(tracking_maze[i + 1][j] == BLOCK && tracking_maze[i - 1][j] == BLOCK)) {
								if (tracking_maze[i + 1][j] == BLOCK) {//There is a block below
									tracking_maze[i][j] = tracking_maze[i][j] + tracking_maze[i - 1][j];
								}
								else if (tracking_maze[i - 1][j] == BLOCK){//There is a block above
									tracking_maze[i][j] = 0;
								}
								else {
									tracking_maze[i][j] = tracking_maze[i - 1][j];
								}
							}
						}
						//steps++;
					}
				}
			} else if (horizon == 1) {//Move up
				for (j = 1; j <= COL - 2; j++) {
					for(i = 1; i <= ROW - 2; i++) {
						if (tracking_maze[i][j] != BLOCK) {
							if (!(tracking_maze[i - 1][j] == BLOCK && tracking_maze[i + 1][j] == BLOCK)) {
								if (tracking_maze[i - 1][j] == BLOCK) {//There is a block above
									tracking_maze[i][j] = tracking_maze[i][j] + tracking_maze[i + 1][j];
								}
								else if (tracking_maze[i + 1][j] == BLOCK){//There is a block below
									tracking_maze[i][j] = 0;
								}
								else {
									tracking_maze[i][j] = tracking_maze[i + 1][j];
								}
							}
						}
						//steps++;
					}
				}
			}
		}
		
		if (vertical != 0) {
			if (vertical == -1) {//Move left
				for (i = 1; i <= ROW - 2; i++) {
					for(j = 1; j <= COL - 2; j++) {
						if (tracking_maze[i][j] != BLOCK) {
							if (!(tracking_maze[i][j - 1] == BLOCK && tracking_maze[i][j + 1] == BLOCK)) {
								if (tracking_maze[i][j - 1] == BLOCK) {//There is a block on the left
									tracking_maze[i][j] = tracking_maze[i][j] + tracking_maze[i][j + 1];
								}
								else if (tracking_maze[i][j + 1] == BLOCK){//There is a block on the right
									tracking_maze[i][j] = 0;
								}
								else {
									tracking_maze[i][j] = tracking_maze[i][j + 1];
								}
							}
						}
						//steps++;
					}
				}
			
			} else if (vertical == 1) {//Move Right
				for (i = 1; i <= ROW - 2; i++) {
					for(j = COL - 2; j >= 1; j--) {
						if (tracking_maze[i][j] != BLOCK) {
							if (!(tracking_maze[i][j + 1] == BLOCK && tracking_maze[i][j - 1] == BLOCK)) {
								if (tracking_maze[i][j + 1] == BLOCK) {//There is a block on the right
									tracking_maze[i][j] = tracking_maze[i][j] + tracking_maze[i][j - 1];
								}
								else if (tracking_maze[i][j - 1] == BLOCK){//There is a block on the left
									tracking_maze[i][j] = 0;
								}
								else {
									tracking_maze[i][j] = tracking_maze[i][j - 1];
								}
							}
						}
						//steps++;
					}
				}
			}
		}
				
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame( "Question1" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 800);

		Localize_Canvas localize = new Localize_Canvas();
		frame.add(localize);
		frame.setVisible(true);
		//Count the total amount white cells in the maze
		//int count = localize.countRequiredCells();
		localize.addKeyListener(new KeyListener() {
			int steps = 0;
			@Override
			public void keyPressed(KeyEvent e) {
				String key = KeyEvent.getKeyText(e.getKeyCode());
				if (key.equals("Up")) {
					localize.people_move(1, 0);
					localize.repaint();
					steps++;
					System.out.println("Step number" + steps+", going up");
				}else if (key.equals("Down")) {
					localize.people_move(-1, 0);
					localize.repaint();
					steps++;
					System.out.println("Step number" + steps+", going down");
				}else if (key.equals("Left")) {
					localize.people_move(0, -1);
					localize.repaint();
					steps++;
					System.out.println("Step number" + steps+", going left");
				}else if (key.equals("Right")) {
					localize.people_move(0, 1);
					localize.repaint();
					steps++;
					System.out.println("Step number" + steps+", going right");
				}	

				//localize.showSteps();
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				
			}
		});
	}
}