import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.Toolkit;
import java.io.*;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;

public class MazeRunner implements ActionListener {
	private	int dim;
	private int size;
	private float p;
	private JFrame frame;
	private Maze maze;
	private PathFinder pf;

	public void actionPerformed(ActionEvent e) {
		Component[] component = frame.getContentPane().getComponents();
		for (int i = 0; i < component.length; ++i) {
			if (component[i] instanceof JPanel) {
				// JPanel found. Recursing into this panel.
				if (component[i].getName() == "fp") {
					frame.remove(component[i]);
					frame.revalidate();
					frame.repaint();
				}
			}
		}

		String str = e.getActionCommand();
		if (str.equals("BFS")) {
			pf = new PathFinder(maze.getMaze(), dim, size, Ope.BFS);
		} else if (str.equals("DFS")) {
			pf = new PathFinder(maze.getMaze(), dim, size, Ope.DFS);
		} else if (str.equals("A* Eu")) {
			pf = new PathFinder(maze.getMaze(), dim, size, Ope.AST1);
		} else if (str.equals("A* Ma")) {
			pf = new PathFinder(maze.getMaze(), dim, size, Ope.AST2);
		}
	
		pf.setBounds(size, size, dim*size + size, dim*size + size);
		pf.setName("fp");

		frame.add(pf);
		frame.revalidate();
		frame.repaint();
	}

	public MazeRunner(int dim, float p, Cell[][] m) {
		this.dim = dim;
		this.p = p;

		frame = new JFrame( "Maze" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setLayout(null);

		JButton dfs_btn = new JButton("DFS");
		JButton bfs_btn = new JButton("BFS");
		JButton ast1_btn = new JButton("A* Eu");
		JButton ast2_btn = new JButton("A* Ma");
		JButton res_btn = new JButton("New");

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();

		if (dim > 10)
			size = (int)(height * 0.8 / dim);
		else
			size = 300 / dim;
		
		if (m == null)
			maze = new Maze(dim, p, size);
		else
			maze = new Maze(dim, p, size, m);

		maze.setBounds(size, size, dim*size + size, dim*size + size);
		maze.setName("maze");

		if (dim > 10) {
			dfs_btn.setBounds(dim*size + 2*size + 30, size, 120, 60);
			bfs_btn.setBounds(dim*size + 2*size + 30, size + 120, 120, 60);
			ast1_btn.setBounds(dim*size + 2*size + 30, size + 240, 120, 60);
			ast2_btn.setBounds(dim*size + 2*size + 30, size + 360, 120, 60);
			res_btn.setBounds(dim*size + 2*size + 30, size + 480, 120, 60);
		} else {
			dfs_btn.setBounds(dim*size + 2*size + 30, size, 90, 45);
			bfs_btn.setBounds(dim*size + 2*size + 30, size + 60, 90, 45);
			ast1_btn.setBounds(dim*size + 2*size + 30, size + 120, 90, 45);
			ast2_btn.setBounds(dim*size + 2*size + 30, size + 180, 90, 45);
			res_btn.setBounds(dim*size + 2*size + 30, size + 240, 90, 45);
		}

		bfs_btn.addActionListener(this);
		dfs_btn.addActionListener(this);
		ast1_btn.addActionListener(this);
		ast2_btn.addActionListener(this);

		res_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Component[] component = frame.getContentPane().getComponents();
				for (int i = 0; i < component.length; ++i) {
					if (component[i].getName() == "fp") {
						frame.remove(component[i]);
						frame.revalidate();
						frame.repaint();
					} else if (component[i].getName() == "maze") {
						System.out.println("Regenerating Maze...");

						frame.remove(component[i]);

						maze = new Maze(dim, p, size);
						maze.setBounds(size, size, dim*size + size, dim*size + size);
						maze.setName("maze");
			
						frame.add(maze);
						frame.revalidate();
						frame.repaint();
					}
				}
			}
		});

		frame.add(maze);

		frame.add(dfs_btn);
		frame.add(bfs_btn);
		frame.add(ast1_btn);
		frame.add(ast2_btn);
		if (m == null)
			frame.add(res_btn);

		if (dim > 10)
			frame.setSize(dim*size + 2*size + 200, dim*size + 2*size + 60);
		else
			frame.setSize(dim*size + 2*size + 200, dim*size + 2*size + 120);
		frame.setVisible(true);
		frame.setResizable(false);

	}

	public static void main(String[] args) {
		FileInputStream in = null;
		int dim = 0;
		float p = 0.0f;
		Cell[][] m = null;

		if (args.length == 2) {
			try {
				dim = Integer.parseInt(args[0]);
				p = Float.parseFloat(args[1]);
			} catch (NumberFormatException e) {
				System.err.println("Invalid Input: Please enter numbers.");
				return;
			}
		} else if (args.length == 1) {
			try {
				try {
					String curLine;
					File file = new File(args[0]);
					in = new FileInputStream(file);

					BufferedReader br = new BufferedReader(new InputStreamReader(in));

					dim = Integer.parseInt(br.readLine());
					m = new Cell[dim][dim];

					for (int i = 0; i < dim; ++i) {
						curLine = br.readLine();
						for (int j = 0; j < dim; ++j) {
							m[i][j] = new Cell();
							m[i][j].row = i;
							m[i][j].col = j;
							m[i][j].prev = null;
							if (curLine.charAt(j) == '0') {
								m[i][j].color = Color.WHITE;
							} else if (curLine.charAt(j) == '1') {
								m[i][j].color = Color.BLACK;
							}
						}
					}
					br.close();
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			} catch (NumberFormatException e) {
				System.err.println("Invalid Input: Maze file doesn't exist.");
				return;
			}
		} else {
			System.err.println("Wrong Input Number.");
			return;
		}
		
		MazeRunner mr = new MazeRunner(dim, p, m);

	}
} 
