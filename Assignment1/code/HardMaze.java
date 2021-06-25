import java.awt.Color;
import java.util.Random;
import java.io.*;

public class HardMaze {
	public static void main(String[] args) {
		Ope ope = Ope.BFS;
		String para = "length";
		
		if (args.length == 2) {
			try {
				if (args[0].equals("BFS"))
					ope = Ope.BFS;
				else if (args[0].equals("DFS"))
					ope = Ope.DFS;
				else if (args[0].equals("AST1"))
					ope = Ope.AST1;
				else if (args[0].equals("AST2"))
					ope = Ope.AST2;				
				para = args[1];
			} catch (NumberFormatException e) {
				System.err.println("Invalid Input: Please enter numbers.");
				return;
			}
		} 

		Maze test = SimulatedAnnealing(500000, 500, 0.9, 100, (float)0.1, ope, para);
		test.printMaze();
		
		try {
			String name = "Hard" + "_" + args[0] + "_" + args[1];
			File file = new File(name);
			FileOutputStream out = new FileOutputStream(file);
	
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
	
			bw.write(Integer.toString(test.getDim()));
			bw.newLine();
	
			for (int i = 0; i < test.getDim(); ++i) {
				for (int j = 0; j < test.getDim(); ++j) {
					if (test.getMaze()[i][j].color == Color.BLACK) {
						bw.write('1');
					} else {
						bw.write('0');
					}
				}
				bw.newLine();
			}
			bw.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int getEstimatedValue(Maze maze, Ope ope, String par) {
		Maze curr = maze;
		PathFinder pf = new PathFinder(curr.getMaze(),curr.getDim(),0, ope);
		if (par.equals("length")) {
			return pf.getResult()._length;
		} else if (par.equals("maxFringe")) {
			return pf.getResult()._fringe_size_max;
		} else if (par.equals("expandNodes")) {
			return pf.getResult()._expand_number;
		} else {
			return -1;
		}
	}
	
	public static Maze SimulatedAnnealing(int its, double temp, double decay, int dim, float prob, Ope ope, String par) {
		int PL;
		int nwPL;
		double T = temp;
		double d = decay;
		int itrs = its;
		double WAP; // the prob to accept worse case
		Maze curr,nw;	
		Random rand = new Random(); 
		int row,col;
		
		//generate a random maze as start point
		curr = new Maze(dim, prob,0);
		
		//re-randomize if no path
		while(getEstimatedValue(curr, ope, "length")==0) {
			curr=new Maze(dim, prob,0);
		}
		
		while(itrs>0) {
			//calculate V(j)
			curr.clear();
			PL = getEstimatedValue(curr, ope, par);//V(j)
			//System.out.println("old path length: "+PL);
			
			//generate its neighborhood and calculate V(j')			
			do {
				nw = curr.duplicate();
				row=0;
				col=0;
				while((row==0&&col==0)||(row==dim-1&&col==dim-1)) {
					row = rand.nextInt(dim); //0-dim
					col = rand.nextInt(dim); //0-dim
					if(nw.getMaze()[row][col].color == Color.BLACK) {
						nw.getMaze()[row][col].color=Color.WHITE;
					}else {
						if((row==0&&col==0)||(row==dim-1&&col==dim-1)) {
						}else {
							nw.getMaze()[row][col].color=Color.BLACK;
						}
					}
				}
			}while(getEstimatedValue(nw, ope, "length")==0);
			nw.clear();
			
			nwPL = getEstimatedValue(nw, ope, par); //V(j')
			//System.out.println("new path length: "+nwPL);
			
			//calculate parameter
			WAP = Math.exp((nwPL-PL)/T);
			//System.out.println("WAP:"+WAP);
			
			//if WAP>=1, accept the better change; if 0<WAP<1, accept the worse maze with prob WAP
			if(WAP>=1) {
				curr = nw;
			}else {
				if(rand.nextInt(100)<WAP*100) {
					curr = nw;
				}
			}
			
			//renew parameter
			T=T*d;
			itrs--;
		}
		return curr;
	}
	
}
