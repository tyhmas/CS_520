import java.awt.Color;

enum Ope { 
    BFS, DFS, AST1, AST2; 
}

class Cell {
	int row;
	int col;
	double obj;
	double cnt;
	boolean visited;
	Cell prev;
	Color color;
}

class Result {
	int _fringe_size_max;
	int _expand_number;
	int _length;
	int _time;
	int _search_space;	
}
