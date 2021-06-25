import java.util.ArrayList;
import java.util.Random;

public class currGame extends Game{
	Cell[][] curr;//used to indicate the current board
	ArrayList<Cell> margin; 
	ArrayList<Cell> neighbor;
	Boolean over = false;
	Boolean win = false;

	//initialize game randomly
	public currGame(int dim_i, int dim_j, double p,double lessProb) {
		super(dim_i, dim_j, p, lessProb);
		curr = new Cell[this.dim_i][this.dim_j];
		Random rd = new Random();
		for(int i=0;i<dim_i;i++) {
			for(int j=0;j<dim_j;j++) {
				curr[i][j]= new Cell(i,j,'u',p);
				if(rd.nextInt(100)<lessProb*100&&this.game[i][j].clue>0) {
					curr[i][j].clue=this.game[i][j].clue-rd.nextInt(this.game[i][j].clue+1);
				}else {
					curr[i][j].clue=this.game[i][j].clue;
				}
			}
		}
		margin = new ArrayList<Cell>();
		neighbor = new ArrayList<Cell>();
	}
	
	//check if the cell of the same x,y values has existed in the list
	public boolean existed(ArrayList<Cell> CL, Cell cell) {
		for(int k=0;k<CL.size();k++) {
			if(CL.get(k).x==cell.x&&CL.get(k).y==cell.y) {
				return true;
			}
		}
		return false;
	}
	
	public void printCurr() {
		System.out.println("***Current Map***");
		for(int i=0;i<dim_i;i++) {
			for(int j=0;j<dim_j;j++) {
				if(this.curr[i][j].type=='u')
					System.out.print("u");
				else if(this.curr[i][j].type=='m')
					System.out.print("m");
				else 
					System.out.print(this.curr[i][j].clue);
			}
			System.out.println();
		}
		
		System.out.println("***Current Map Prob***");
		for(int i=0;i<dim_i;i++) {
			for(int j=0;j<dim_j;j++) {
				System.out.print(this.curr[i][j].prob);			
			}
			System.out.println();
		}
		
		System.out.println("curr margins:"+this.margin.size());
		for(int k=0;k<margin.size();k++) {
			System.out.print("("+this.margin.get(k).x+", "+this.margin.get(k).y+")");	
		}
		System.out.println();
		
		System.out.println("curr neighbors:"+this.neighbor.size());
		for(int l=0;l<neighbor.size();l++) {
			System.out.print("("+this.neighbor.get(l).x+", "+this.neighbor.get(l).y+")");	
		}
		System.out.println();
	}
	
}
