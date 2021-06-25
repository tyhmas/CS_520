import java.util.Random;

import javafx.util.Pair;

public class Game {
	public int dim_i;//x 
	public int dim_j;//y
	public double p; //prob of mines
	public double uncertainP;//prob of giving the clue
	public Cell[][] game; //used to tell dig result
	
	//initialize game randomly
	public Game(int dim_i, int dim_j, double p, double uncertainP) {
		this.dim_i=dim_i;
		this.dim_j=dim_j;
		this.p=p;
		this.uncertainP=uncertainP;
		game = new Cell[dim_i][dim_j];
		
		Random rd = new Random();
		for(int i=0;i<dim_i;i++) {
			for(int j=0;j<dim_j;j++) {
				if(rd.nextInt(100)<p*100)
					this.game[i][j] = new Cell(i,j,'m',p);
				else
					this.game[i][j] = new Cell(i,j,'c',p);				
			}
		}
		
		/*for(int i=0;i<dim_i;i++) {
			for(int j=0;j<dim_j;j++) {
				this.game[i][j] = new Cell(i,j,'c',p);				
			}
		}
		
		this.game[0][2] = new Cell(0,2,'m',p);
		this.game[4][4] = new Cell(4,4,'m',p);*/
		
		
		for(int i=0;i<dim_i;i++) {
			for(int j=0;j<dim_j;j++) {
				if(this.game[i][j].type!='m')
					this.game[i][j].clue = (int) getClue(this.game, i, j).getKey();	
					this.game[i][j].info = uncertainP * (double)getClue(this.game, i, j).getValue();//for part 5-1
			}
		}
	}
	
	//helper method to indicate clue of a cell
	public Pair<Integer, Double> getClue(Cell[][] game, int x, int y) {
		
		int mines = 0;
		double neighbors =0;
		//up left
		if(x-1>=0&&y-1>=0) {
			neighbors++;//for part 5-1
			if(game[x-1][y-1].type=='m')
				mines++;
		}
		//left
		if(y-1>=0) {
			neighbors++;//for part 5-1
			if(game[x][y-1].type=='m')
				mines++;
		}
		//up right
		if(x-1>=0&&y+1<=dim_j-1) {
			neighbors++;//for part 5-1
			if(game[x-1][y+1].type=='m')
				mines++;
		}
		//up
		if(x-1>=0) {
			neighbors++;//for part 5-1
			if(game[x-1][y].type=='m')
				mines++;
		}
		//down
		if(x+1<=dim_i-1) {
			neighbors++;//for part 5-1
			if(game[x+1][y].type=='m')
				mines++;
		}
		//right
		if(y+1<=dim_j-1) {
			neighbors++;//for part 5-1
			if(game[x][y+1].type=='m')
				mines++;
		}
		//down left
		if(x+1<=dim_i-1&&y-1>=0) {
			neighbors++;//for part 5-1
			if(game[x+1][y-1].type=='m')
				mines++;
		}
		//down right
		if(x+1<=dim_i-1&&y+1<=dim_j-1) {
			neighbors++;//for part 5-1
			if(game[x+1][y+1].type=='m')
				mines++;
		}
		
		Pair<Integer, Double> result = new Pair<>(mines, neighbors);
		return result;
	}
	
	//print out all mines
	public void print() {
		System.out.println("***Mine Map***");
		for(int i=0;i<dim_i;i++) {
			for(int j=0;j<dim_j;j++) {
				if(this.game[i][j].type=='m')
					System.out.print("X");
				else
					System.out.print("0");
			}
			System.out.println();
		}
		
		System.out.println("***Clue Map***");
		for(int i=0;i<dim_i;i++) {
			for(int j=0;j<dim_j;j++) {
				System.out.print(this.game[i][j].clue);
			}
			System.out.println();
		}
	}
}
