public class Cell {
	int x;
	int y;
	int clue; //initiated as -1,-1 for mine, positive for number of mines around 
	char type; //c - clear, u - unknown, m - mine 
	double prob; //prob to be a mine
	boolean digged = false; //for digXY
	boolean initProb = true; 
	boolean zeroProb = false;
	
	public Cell(int x, int y, char t, double p) {
		this.x=x;
		this.y=y;
		this.clue=-1;
		this.type = t;
		this.prob=p;
	}
}
