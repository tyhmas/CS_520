import java.io.*;

public class BatchRun {
	public static void main(String[] args) {
		int i = 0, cnt = 0;
		int dim = 0;
		float p = 0.0f;
		Maze maze = null;
		PathFinder pf = null;
		Result res = new Result();

		String name = "result";

		if (args.length == 3) {
			try {
				dim = Integer.parseInt(args[0]);
				p = Float.parseFloat(args[1]);
				cnt = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				System.err.println("Invalid Input: Please enter numbers.");
				return;
			}
		} else {
			System.err.println("Invalid Input: Please enter TWO arguments");
		}
		
		try {
			File file = new File(name);
			FileOutputStream out = new FileOutputStream(file);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));

			bw.write("Algorithm    _fringe_size_max    _length    _time    _search_space");
			bw.newLine();

			while (i < cnt*4) {
				maze = new Maze(dim, p, 0);
				pf = new PathFinder(maze.getMaze(), dim, 0, Ope.BFS);
				res = pf.getResult();

				if (res._length > 0) {
					bw.write("BFS    " + res._fringe_size_max + "    " + res._length + "    " + res._time + "    " + res._search_space);
					bw.newLine();
					++i;	
				}

				pf = new PathFinder(maze.getMaze(), dim, 0, Ope.DFS);
				res = pf.getResult();
				if (res._length > 0) {
					bw.write("DFS    " + res._fringe_size_max + "    " + res._length + "    " + res._time + "    " + res._search_space);
					bw.newLine();
					++i;	
				}

				pf = new PathFinder(maze.getMaze(), dim, 0, Ope.AST1);
				res = pf.getResult();
				if (res._length > 0) {
					bw.write("AST1   " + res._fringe_size_max + "    " + res._length + "    " + res._time + "    " + res._search_space);
					bw.newLine();
					++i;	
				}

				pf = new PathFinder(maze.getMaze(), dim, 0, Ope.AST2);
				res = pf.getResult();
				if (res._length > 0) {
					bw.write("AST2   " + res._fringe_size_max + "    " + res._length + "    " + res._time + "    " + res._search_space);
					bw.newLine();
					++i;	
				}		
				bw.newLine();
			}

			bw.close();
		} catch(IOException e) {
				e.printStackTrace();
		}

	}
} 
