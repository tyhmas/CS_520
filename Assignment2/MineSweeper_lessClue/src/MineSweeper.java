import java.util.ArrayList;

public class MineSweeper {

	public static void main(String[] args) {
		System.out.println("correct rate:"+MineSweeperTest(10000, 20,20, 0.2,0.02));
		/*currGame test = new currGame(5,5,0.2,0.1);
		test.print();
		test.printCurr();
		boolean r = winMineSweeper(test, 0, 0);
		System.out.println("win?"+r);*/
	}
	
	public static double MineSweeperTest(int its, int dim_i,int dim_j, double p, double lessProb) {
		int win = 0;
		int tmpIts = its;
		while(tmpIts!=0) {
			System.out.println("its: "+tmpIts+"/"+its);
			tmpIts--;
			currGame test = new currGame(dim_i,dim_j,p, lessProb);
			boolean result = winMineSweeper(test, 0, 0);
			if(result==true) {
				win++;
			}
		}
		return (double)win/(double)its;		
	}
	
	//mine sweeper
	public static boolean winMineSweeper(currGame currGame, int x, int y) {
		while(!currGame.over) {
			//if every cell has been marked, end the game with win
			if(diggedAll(currGame)) {
				currGame.over=true;
				currGame.win=true;
				return currGame.win;
			}
			
			digXY(currGame,findNext(currGame).x,findNext(currGame).y);
			clearDigXY(currGame);
			updateNeighbor(currGame);
			
			//currGame.printCurr();
			
			while(markMine(currGame)) {
				updateNeighbor(currGame);
			}
		}
		return currGame.win;
	}
	
	//dig a cell
	public static void digXY(currGame currGame, int x, int y) {
		//dig a mine
		if(currGame.game[x][y].type=='m') {
			currGame.curr[x][y].type='m';
			currGame.over=true;
			currGame.win=false;
			return;
		}
		//dig a margin cell
		if(currGame.game[x][y].type=='c'&&currGame.game[x][y].clue!=0) {
			currGame.curr[x][y].type='c';
			if(!currGame.existed(currGame.margin, currGame.curr[x][y])) {
				currGame.margin.add(currGame.curr[x][y]);
			}
			return;
		}
		
		//dig an internal cell
		if(currGame.game[x][y].type=='c'&&currGame.game[x][y].clue==0) {
			currGame.curr[x][y].type='c';
			currGame.game[x][y].digged=true;
			//up left
			if(x-1>=0&&y-1>=0&&currGame.game[x-1][y-1].digged==false) {
				digXY(currGame, x-1,y-1);
			}
			//left
			if(y-1>=0&&currGame.game[x][y-1].digged==false) {
				digXY(currGame, x,y-1);
			}
			//up right
			if(x-1>=0&&y+1<=currGame.dim_j-1&&currGame.game[x-1][y+1].digged==false) {
				digXY(currGame, x-1,y+1);
			}
			//up
			if(x-1>=0&&currGame.game[x-1][y].digged==false) {
				digXY(currGame, x-1,y);
			}
			//down
			if(x+1<=currGame.dim_i-1&&currGame.game[x+1][y].digged==false) {
				digXY(currGame, x+1,y);
			}
			//right
			if(y+1<=currGame.dim_j-1&&currGame.game[x][y+1].digged==false) {
				digXY(currGame, x,y+1);
			}
			//down left
			if(x+1<=currGame.dim_i-1&&y-1>=0&&currGame.game[x+1][y-1].digged==false) {
				digXY(currGame, x+1,y-1);
			}
			//down right
			if(x+1<=currGame.dim_i-1&&y+1<=currGame.dim_j-1&&currGame.game[x+1][y+1].digged==false) {
				digXY(currGame, x+1,y+1);
			}
			return;
		}
	}

	//clear for next dig
	public static void clearDigXY(currGame currGame) {
		for(int i=0;i<currGame.dim_i;i++) {
			for(int j=0;j<currGame.dim_j;j++) {
				currGame.game[i][j].digged=false;		
			}
		}
	}

	//used to decide if win
	public static boolean diggedAll(currGame currGame) {
		for(int i=0;i<currGame.dim_i;i++) {
			for(int j=0;j<currGame.dim_j;j++) {
				if(currGame.curr[i][j].type=='u')
					return false;
			}
		}
		return true;
	}
	
	//find the next cell to dig
	public static Cell findNext(currGame currGame) {
		double minProb=9999;
		for(int i=0;i<currGame.dim_i;i++) {
			for(int j=0;j<currGame.dim_j;j++) {
				if(currGame.curr[i][j].type=='u') {
					if(currGame.curr[i][j].prob<minProb) {
						minProb=currGame.curr[i][j].prob;
					}
				}
			}
		}
		for(int i=0;i<currGame.dim_i;i++) {
			for(int j=0;j<currGame.dim_j;j++) {
				if(currGame.curr[i][j].type=='u') {
					if(currGame.curr[i][j].prob<=minProb){
						//System.out.println("Next: ("+currGame.curr[i][j].x+", "+currGame.curr[i][j].y+")");
						return currGame.curr[i][j];
					}
				}
			}
		}
		//should never reaches here
		return null;
	}
	
	//update neighbor cells' prob
	public static void updateNeighbor(currGame currGame) {
		currGame.neighbor = new ArrayList<Cell>();
		//make initProb true
		for(int i=0;i<currGame.dim_i;i++) {
			for(int j=0;j<currGame.dim_j;j++) {
				currGame.curr[i][j].initProb = true;
			}
		}
		
		for(int i=0;i<currGame.margin.size();i++) {
			updateNeighborHelper(currGame, currGame.margin.get(i).x, currGame.margin.get(i).y);
		}
	}
	
	//helper for updateNeighbor - update a margin cell's neighbor
	public static void updateNeighborHelper(currGame currGame, int x, int y) {
				//calculate the prob of around cells  
				int minesLeft = currGame.curr[x][y].clue;
				int neighborsLeft = 0;
				
				//up left
				if(x-1>=0&&y-1>=0) {
					if(currGame.curr[x-1][y-1].type=='u')
						neighborsLeft++;
					else if(currGame.curr[x-1][y-1].type=='m')
						minesLeft--;
				}
				//left
				if(y-1>=0) {
					if(currGame.curr[x][y-1].type=='u')
						neighborsLeft++;
					else if(currGame.curr[x][y-1].type=='m')
						minesLeft--;
				}
				//up right
				if(x-1>=0&&y+1<=currGame.dim_j-1) {
					if(currGame.curr[x-1][y+1].type=='u')
						neighborsLeft++;
					else if(currGame.curr[x-1][y+1].type=='m')
						minesLeft--;
				}
				//up
				if(x-1>=0) {
					if(currGame.curr[x-1][y].type=='u')
						neighborsLeft++;
					else if(currGame.curr[x-1][y].type=='m')
						minesLeft--;
				}
				//down
				if(x+1<=currGame.dim_i-1) {
					if(currGame.curr[x+1][y].type=='u')
						neighborsLeft++;
					else if(currGame.curr[x+1][y].type=='m')
						minesLeft--;
				}
				//right
				if(y+1<=currGame.dim_j-1) {
					if(currGame.curr[x][y+1].type=='u')
						neighborsLeft++;
					else if(currGame.curr[x][y+1].type=='m')
						minesLeft--;
				}
				//down left
				if(x+1<=currGame.dim_i-1&&y-1>=0) {
					if(currGame.curr[x+1][y-1].type=='u')
						neighborsLeft++;
					else if(currGame.curr[x+1][y-1].type=='m')
						minesLeft--;
				}
				//down right
				if(x+1<=currGame.dim_i-1&&y+1<=currGame.dim_j-1) {
					if(currGame.curr[x+1][y+1].type=='u')
						neighborsLeft++;
					else if(currGame.curr[x+1][y+1].type=='m')
						minesLeft--;
				}
				
				double prob = (double)minesLeft/(double)neighborsLeft;
			
				//add neighbor cells to the list
				//if the neighbor cell's prob is 0, keep it 0
				//if the neighbor cell's prob is initial, assign prob to the cell no matter its value
				//else only assign prob to the neighbor cell when it's bigger
				
				//up left
				if(x-1>=0&&y-1>=0) {
					if(currGame.curr[x-1][y-1].type=='u') {
						if(!currGame.existed(currGame.neighbor,currGame.curr[x-1][y-1])) {
							currGame.neighbor.add(currGame.curr[x-1][y-1]);
						}
						if(prob==0) {
							currGame.curr[x-1][y-1].zeroProb = true;
							currGame.curr[x-1][y-1].prob=0;
						}
						if(currGame.curr[x-1][y-1].prob<prob&&currGame.curr[x-1][y-1].zeroProb==false) {
							currGame.curr[x-1][y-1].prob=prob;
						}
						if(currGame.curr[x-1][y-1].initProb==true&&currGame.curr[x-1][y-1].zeroProb==false) {
							currGame.curr[x-1][y-1].prob=prob;
							currGame.curr[x-1][y-1].initProb=false;
						}
					}
				}
				//left
				if(y-1>=0) {
					if(currGame.curr[x][y-1].type=='u') {
						if(!currGame.existed(currGame.neighbor,currGame.curr[x][y-1])) {
							currGame.neighbor.add(currGame.curr[x][y-1]);
						}
						if(prob==0) {
							currGame.curr[x][y-1].zeroProb = true;
							currGame.curr[x][y-1].prob=0;
						}
						if(currGame.curr[x][y-1].prob<prob&&currGame.curr[x][y-1].zeroProb==false) {
							currGame.curr[x][y-1].prob=prob;
						}
						if(currGame.curr[x][y-1].initProb==true&&currGame.curr[x][y-1].zeroProb==false) {
							currGame.curr[x][y-1].prob=prob;
							currGame.curr[x][y-1].initProb=false;
						}
					}
				}
				//up right
				if(x-1>=0&&y+1<=currGame.dim_j-1) {
					if(currGame.curr[x-1][y+1].type=='u'){
						if(!currGame.existed(currGame.neighbor,currGame.curr[x-1][y+1])) {
							currGame.neighbor.add(currGame.curr[x-1][y+1]);
						}
						if(prob==0) {
							currGame.curr[x-1][y+1].zeroProb = true;
							currGame.curr[x-1][y+1].prob=0;
						}
						if(currGame.curr[x-1][y+1].prob<prob&&currGame.curr[x-1][y+1].zeroProb==false) {
							currGame.curr[x-1][y+1].prob=prob;
						}
						if(currGame.curr[x-1][y+1].initProb==true&&currGame.curr[x-1][y+1].zeroProb==false) {
							currGame.curr[x-1][y+1].prob=prob;
							currGame.curr[x-1][y+1].initProb=false;
						}
					}
				}
				//up
				if(x-1>=0) {
					if(currGame.curr[x-1][y].type=='u'){
						if(!currGame.existed(currGame.neighbor,currGame.curr[x-1][y])) {
							currGame.neighbor.add(currGame.curr[x-1][y]);
						}
						if(prob==0) {
							currGame.curr[x-1][y].zeroProb = true;
							currGame.curr[x-1][y].prob=0;
						}
						if(currGame.curr[x-1][y].prob<prob&&currGame.curr[x-1][y].zeroProb==false) {
							currGame.curr[x-1][y].prob=prob;
						}
						if(currGame.curr[x-1][y].initProb==true&&currGame.curr[x-1][y].zeroProb==false) {
							currGame.curr[x-1][y].prob=prob;
							currGame.curr[x-1][y].initProb=false;
						}
					}
				}
				//down
				if(x+1<=currGame.dim_i-1) {
					if(currGame.curr[x+1][y].type=='u'){
						if(!currGame.existed(currGame.neighbor,currGame.curr[x+1][y])) {
							currGame.neighbor.add(currGame.curr[x+1][y]);
						}
						if(prob==0) {
							currGame.curr[x+1][y].zeroProb = true;
							currGame.curr[x+1][y].prob=0;
						}
						if(currGame.curr[x+1][y].prob<prob&&currGame.curr[x+1][y].zeroProb==false) {
							currGame.curr[x+1][y].prob=prob;
						}
						if(currGame.curr[x+1][y].initProb==true&&currGame.curr[x+1][y].zeroProb==false) {
							currGame.curr[x+1][y].prob=prob;
							currGame.curr[x+1][y].initProb=false;
						}
					}
				}
				//right
				if(y+1<=currGame.dim_j-1) {
					if(currGame.curr[x][y+1].type=='u'){
						if(!currGame.existed(currGame.neighbor,currGame.curr[x][y+1])) {
							currGame.neighbor.add(currGame.curr[x][y+1]);
						}
						if(prob==0) {
							currGame.curr[x][y+1].zeroProb = true;
							currGame.curr[x][y+1].prob=0;
						}
						if(currGame.curr[x][y+1].prob<prob&&currGame.curr[x][y+1].zeroProb==false) {
							currGame.curr[x][y+1].prob=prob;
						}
						if(currGame.curr[x][y+1].initProb==true&&currGame.curr[x][y+1].zeroProb==false) {
							currGame.curr[x][y+1].prob=prob;
							currGame.curr[x][y+1].initProb=false;
						}
					}
				}
				//down left
				if(x+1<=currGame.dim_i-1&&y-1>=0) {
					if(currGame.curr[x+1][y-1].type=='u'){
						if(!currGame.existed(currGame.neighbor,currGame.curr[x+1][y-1])) {
							currGame.neighbor.add(currGame.curr[x+1][y-1]);
						}
						if(prob==0) {
							currGame.curr[x+1][y-1].zeroProb = true;
							currGame.curr[x+1][y-1].prob=0;
						}
						if(currGame.curr[x+1][y-1].prob<prob&&currGame.curr[x+1][y-1].zeroProb==false) {
							currGame.curr[x+1][y-1].prob=prob;
						}
						if(currGame.curr[x+1][y-1].initProb==true&&currGame.curr[x+1][y-1].zeroProb==false) {
							currGame.curr[x+1][y-1].prob=prob;
							currGame.curr[x+1][y-1].initProb=false;
						}
					}
				}
				//down right
				if(x+1<=currGame.dim_i-1&&y+1<=currGame.dim_j-1) {
					if(currGame.curr[x+1][y+1].type=='u'){
						if(!currGame.existed(currGame.neighbor,currGame.curr[x+1][y+1])) {
							currGame.neighbor.add(currGame.curr[x+1][y+1]);
						}
						if(prob==0) {
							currGame.curr[x+1][y+1].zeroProb = true;
							currGame.curr[x+1][y+1].prob=0;
						}
						if(currGame.curr[x+1][y+1].prob<prob&&currGame.curr[x+1][y+1].zeroProb==false) {
							currGame.curr[x+1][y+1].prob=prob;
						}
						if(currGame.curr[x+1][y+1].initProb==true&&currGame.curr[x+1][y+1].zeroProb==false) {
							currGame.curr[x+1][y+1].prob=prob;
							currGame.curr[x+1][y+1].initProb=false;
						}
					}
				}
	}

	//mark the cells those are sure to be mines
	public static boolean markMine(currGame currGame) {
		for(int i=0;i<currGame.neighbor.size();i++) {
			if(currGame.neighbor.get(i).type=='u'&&currGame.neighbor.get(i).prob==1) {
				currGame.neighbor.get(i).type='m';
				if(currGame.existed(currGame.margin,currGame.neighbor.get(i))) {
					currGame.margin.add(currGame.neighbor.get(i));
				}
				return true;
			}
		}
		return false;
	}
}


