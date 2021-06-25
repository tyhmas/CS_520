import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
//package Iframe;
//import Iframe.Iframe;

public class FindCells extends JPanel implements KeyListener, MouseListener{
	private final int ROW = 43;
	private final int COL = 57;
	private final int size = 15;
	private int maze[][];

	@Override
	public void keyPressed(KeyEvent e) {
    	System.out.println("Press");
    	switch (e.getKeyCode()){
        	case KeyEvent.VK_DOWN :
        		if(e.isControlDown()){
            		System.out.println("Down");
            		break;	
        		}
    	}

	}

	@Override
	public void keyTyped(KeyEvent e){

	}

	@Override
	public void keyReleased(KeyEvent e){

	}

	@Override
	public void mouseClicked(MouseEvent e){
		System.out.println("click");
	}

	@Override
	public void mousePressed(MouseEvent e){
		System.out.println("press");
	}

	@Override
	public void mouseReleased(MouseEvent e){

	}

	@Override
	public void mouseEntered(MouseEvent e){

	}

	@Override
	public void mouseExited(MouseEvent e){

	}

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

	public FindCells() {
		File file = new File("Maze.txt");

		maze = new int[ROW][COL];
		int count = 0, i = 0, j = 0;

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				for (j = 0; j < line.length(); ++j) {
					char c = line.charAt(j);
					if (c == '0') {
						maze[i][j] = 0;
					} else {
						maze[i][j] = 1;
					}
				}
				++i;
			}
		} catch (IOException e) {
    		e.printStackTrace();
		}

		for (i = 0; i < ROW; ++i) {
			for (j = 0; j < COL; ++j) {
				if (maze[i][j] != 1) {
					if (countSurroundingBlock(maze, i, j) == 5) {
						if (maze[i][j-1] == 1) {
							++count;
							maze[i][j] = 2;
							continue;
						} else if (countSurroundingBlock(maze, i, j-1) == 5) {
							if (maze[i][j-2] == 1) {
								++count;
								maze[i][j] = 2;
								continue;
							} else if (countSurroundingBlock(maze, i, j-2) == 5) {
								++count;
								maze[i][j] = 2;
							}						
						}
					}
				}
			}
		}
		System.out.println(count);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

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
				} else if (maze[i][j] == 2) {
					g.setColor(Color.BLUE);
					g.fillRect(j*size, i*size, size, size);
				} 
			}
		}
	}


	public static void main(String[] args) {
		JFrame frame = new JFrame( "Question1" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setSize(1200, 800);

		FindCells findCells = new FindCells();
		frame.add(findCells);
		frame.setVisible(true);
		//maze.requestFocusInWindow();
	}
}
