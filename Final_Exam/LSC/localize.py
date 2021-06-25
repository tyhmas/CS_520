from tkinter import *
import time
import numbers
import ast
import random
import math
import heapq

class Node(object):
	"""docstring for Node"""
	def __init__(self, maze, cost, estimate, seq):
		super(Node, self).__init__()
		self.map = maze
		self.estimate = estimate
		self.sequence = seq
		self.cost = cost

	def fn(self):
		return self.cost + self.estimate
		# return 695 + self.estimate
	def __lt__(self, other):
		if self.fn() <= other.fn():
			return True
		else:
			return False


# Maze default parameters
DefaultNWhite = 695
# Visualization parameters
DefaultCanvasHeight = 600
DefaultCanvasWidth = 600
DefaultTopPadding = 10
DefaultLeftPadding = 10
DefaultBottomPadding = 10
DefaultRightPadding = 10
DefaultHeaderHeight = 0
# Maze file
DefaultMazePath = "maze.csv"
# Configurations
BLOCK = 1
LEFTBTN = "<Button-1>"
KEYBOARD = "<Key>"
COLORFACTOR = 0.15
# The system
class MazeSimulator(object):
	# Initialization
	def __init__(self):
		# The maze data
		self.map = self.load_maze(DefaultMazePath)
		# If editing or not
		self.edit = True
		# Cells
		self.tiles = None
		# Cell labels indicating the number of people standing on it
		self.labels = None
		# The number people
		self.population = DefaultNWhite
		# The gradiant of color power factor
		self.colorPower = COLORFACTOR
		# Record the number of moves
		self.nMoves = 1
		# For calculating shortest sequence
		self.shortestPathMatrix = None
		self.bestParentMatrix = None
		self.total_cost = 0
		# Maze window
		self.window = Tk()
		self.window.title("Maze Simulator")
		self.window.geometry('+200+10')
		self.canvas = Canvas(self.window, bg = "#ffffff",\
			height = DefaultCanvasHeight, width = DefaultCanvasWidth)
		self.canvas.focus_set()
		self.drawMap()
		self.canvas.bind(KEYBOARD, self.key_down)
		self.canvas.bind('<Up>', lambda event: self.everyone_move(-1, 0))
		self.canvas.bind('<Down>', lambda event: self.everyone_move(1, 0))
		self.canvas.bind('<Left>', lambda event: self.everyone_move(0, -1))
		self.canvas.bind('<Right>', lambda event: self.everyone_move(0, 1))
		self.canvas.bind(LEFTBTN, self.left_btn_released)
		self.popuLabel = self.canvas.create_text(200, 20, \
			anchor="center", text="Population: " + str(DefaultNWhite), font=("Times New Roman", 10))
		self.canvas.pack()
		# Control panel
		configWindow = MazeSimulator.prepare_configuration(self)
		# Start visualization
		configWindow.mainloop()
		self.window.mainloop()

	# Draw basic maps
	def drawMap(self):
		# Refresh
		self.canvas.delete("all")
		# Cell size
		tileHeight = (DefaultCanvasHeight - 20) / self.n_row
		tileWidth = (DefaultCanvasWidth - 20) / self.n_col
		# Initialize new cells and their labels
		self.tiles = [[self.canvas.create_rectangle(\
			self.cell_to_pixel(self.canvas, i, j)) for j in range(self.n_col)] \
			for i in range(self.n_row)]
		self.labels = [[None for j in range(self.n_col)] for i in range(self.n_row)]
		# Draw cell colors and initial labels
		for i in range(self.n_row):
			for j in range(self.n_col):
				if self.map[i][j] == BLOCK:
					self.canvas.itemconfig(self.tiles[i][j], fill="#000000")
				else:
					left, top, right, bottom = self.cell_to_pixel(self.canvas, i, j)
					self.labels[i][j] = self.canvas.create_text((left + right)/2, (top + bottom)/2, \
						anchor="center", text="1", font=("Times New Roman", 10))

	# Keyboard event WSAD as moving directions
	def key_down(self, event):
		if event.char == 'w':
			self.everyone_move(-1, 0)
		elif event.char == 's':
			self.everyone_move(1, 0)
		elif event.char == 'a':
			self.everyone_move(0, -1)
		elif event.char == 'd':
			self.everyone_move(0, 1)
		return

	# Keyboard event Up(-1,0), Down(1,0), Left(0,-1), Right(0,1)
	def everyone_move(self, roffset, coffset):
		newMap, new_cost = self.apply_move(self.map, roffset, coffset)
		# Update the original map
		self.map = newMap
		# Disable editing
		if self.edit == True:
			self.play()
		self.print_move(roffset, coffset)
		self.total_cost = self.total_cost + new_cost
		print(self.total_cost)
		estimate = self.get_estimate(newMap)
		print("estimate: " + str(estimate))
		print("total: " + str(self.total_cost + estimate))

	def apply_move(self, maze, roffset, coffset):
		total_cost = 0
		# New map after moving
		newMap = [[0 for j in range(self.n_col)] for i in range(self.n_row)]
		for i in range(self.n_row):
			for j in range(self.n_col):
				# Block cells does not change
				if maze[i][j] == BLOCK:
					newMap[i][j] = BLOCK
				# If not a block cell, update its value
				else:
					# Blocking on the moving direction, then people stay
					if maze[i + roffset][j + coffset] == BLOCK:
						newMap[i][j] = maze[i][j]
					else:
						total_cost = total_cost + 1
					# If other people moving towards this place, then welcome them in
					if maze[i - roffset][j - coffset] != BLOCK:
						newMap[i][j] = newMap[i][j] + maze[i - roffset][j - coffset]
		return newMap, total_cost


	def print_move(self, roffset, coffset):
		if roffset == -1 and coffset == 0:
			print(str(self.nMoves) + ",Up")
		elif roffset == 1 and coffset == 0:
			print(str(self.nMoves) + ",Down")
		elif roffset == 0 and coffset == -1:
			print(str(self.nMoves) + ",Left")
		elif roffset == 0 and coffset == 1:
			print(str(self.nMoves) + ",Right")
		else:
			return
		self.nMoves = self.nMoves + 1

	# Start playing
	def play(self):
		# Disable editing
		self.edit = False
		# Start view update
		self.window.after(40, self.update_view)
		# Reset number of moves
		self.nMoves = 1
		# Shortest path
		self.calculate_shortest_paths(start = [30,30])

	# Update detail of the maze
	def update_view(self, thread = True):
		# If start editing, stop playing
		if self.edit:
			print("Stop playing")
			return
		# If playing continue updating
		# print("Update view"
		# Draw maze
		for i in range(self.n_row):
			for j in range(self.n_col):
				# Block cells set black
				if self.map[i][j] == BLOCK:
					self.drawColor(i, j, 'black')
				# Empty cells draw color with respect to its number of people
				else:
					amptitude = float(self.population + 1) / (self.map[i][j] + 1) - 1
					amptitude = amptitude / self.population
					amptitude = math.pow(amptitude, self.colorPower)
					shade = hex(int(amptitude * 0xff))
					shade = shade[2:4]
					if len(shade) == 1:
						shade = "0" + shade
					shade = "#ff" + shade + shade # R=ff, G=B=color
					self.drawColor(i, j, shade)
		if thread:
			self.canvas.after(40, self.update_view)

	# One mouse left button clicked
	def left_btn_released(self, event):
		# Get the cell that clicked
		row, col = self.pixel_to_cell(self.canvas, event.x, event.y)
		# Return if click outside of the mine field
		if row < 0 or col < 0 or row > self.n_row or col > self.n_col:
			print("Click on the outside of maze")
			self.calculate_shortest_paths(start = [30,30])
			node = Node(self.map, 0, self.get_estimate(self.map), [])
			print(node.fn())
			return
		# Block cells transform to empty cell, empty transform to block
		self.flip_a_cell(row, col)

	# Block cells transform to empty cell, empty transform to block
	def flip_a_cell(self, row, col):
		# Editing disabled when playing
		if not self.edit:
			return
		# If the cell is block, change to empty with 1 person as default
		if self.map[row][col] == BLOCK:
			self.map[row][col] = 1
			self.drawColor(row, col, "#ffffff")
		# If not block, set to block cell
		else:
			self.map[row][col] = BLOCK
			self.drawColor(row, col, "#000000")
		return

	# Draw the color of a cell
	def drawColor(self, x, y, color):
		self.canvas.itemconfig(self.tiles[x][y], fill = color)
		self.canvas.itemconfig(self.labels[x][y], text = str(self.map[x][y]))

	@staticmethod
	def prepare_configuration(resp):
		print("Show configuration")
		root = Tk()
		root.title("Control Panel")
		# Reset button
		carTypeBtn = Button(root, text = "Reset", command = resp.reset_simulator)
		carTypeBtn.pack()
		# Play
		solverBtn = Button(root, text = "Play", command = resp.play)
		solverBtn.pack()
		# Initial surrounding block
		sbLabel = Label(root, text = "Starting number of surrounding blocks")
		sbLabel.pack()
		resp.sbEntry = Entry(root, justify = 'center', \
			textvariable = StringVar(root, value = "#"))
		resp.sbEntry.pack()
		# Filter button
		filterBtn = Button(root, text = "Filter", command = resp.filter)
		filterBtn.pack()
		# Test filter from file
		testFilterBtn = Button(root, text = "Load and Test Filter", command = resp.load_test_filter)
		testFilterBtn.pack()
		# Run Solver
		solverBtn = Button(root, text = "Solve for shortest sequence", command = resp.solve)
		solverBtn.pack()
		# Save maze
		solverBtn = Button(root, text = "Save Maze", command = resp.save_maze)
		solverBtn.pack()
		return root

	def filter(self, sb = None):
		if self.edit:
			self.play()
		if sb == None:
			# Get initial surrounding block number setting
			sb = self.sbEntry.get()
			if sb != None and "#" != sb:
				sb = int(sb)
		# Population will be updated
		self.population = 0
		for i in range(self.n_row):
			for j in range(self.n_col):
				if self.map[i][j] != BLOCK:
					if self.get_n_sb(i, j) != sb:
						self.map[i][j] = 0
					self.population = self.population + self.map[i][j]
		self.canvas.itemconfig(self.popuLabel, text = "Population: " + str(self.population))
		print("Updated population: " + str(self.population))

	# Count the number of surrounding blocks of a point
	def get_n_sb(self, row, col):
		count = 0
		for i in range(3):
			for j in range(3):
				if self.map[row+i-1][col+j-1] == BLOCK:
					count = count + 1
		return count

	# Load filter and action list, and find possible positions
	def load_test_filter(self):
		# Load observations
		infile = open("observations.txt", 'r')
		observations = ast.literal_eval('[%s]' % infile.read())
		observations = observations[0]
		infile.close()
		# Load actions
		infile = open("actions.txt", 'r')
		actions = ast.literal_eval('[%s]' % infile.read())
		actions = actions[0]
		infile.close()
		# Parameter setting
		self.colorPower = 1
		# Observation describe the initial state
		self.filter(observations[0])
		# For each action and its corresponding filter, test possible positions
		self.total_cost = 0
		for i in range(len(actions)):
			sb = observations[i+1]
			act = actions[i]
			self.everyone_move(act[0], act[1])
			self.filter(sb)
		print("Total cost: " + str(self.total_cost))

	# Find the shortest sequence to ensure G, by A *
	def solve(self):
		# Stop everything ongoing
		self.reset_simulator()
		# Calculate shortest distance matrix
		G = [30,30]
		self.play()
		# self.calculate_shortest_paths(G)
		# Find the best sequence by A *
		sequence = self.find_best_sequence(G)
		# sequence = self.find_greedy_best_sequence(G)
		# Save output
		print("Length of the best sequence: " + str(len(sequence)))
		# print(sequence
		for action in sequence:
			self.everyone_move(action[0], action[1])

	# Breadth First Search to get shortest path matrix
	def calculate_shortest_paths(self, start):
		# Initialize
		self.shortestPathMatrix = [[696 for j in range(len(self.map))] for i in range(len(self.map[0]))]
		self.bestParentMatrix = [[None for j in range(len(self.map))] for i in range(len(self.map[0]))]
		result = dict()
		# BFS queue initialization
		v = list()
		v.insert(0, start)
		self.shortestPathMatrix[start[0]][start[1]] = 0
		# Start BFS
		while not (len(v) == 0):
			# Get the oldest element
			loc = v.pop()
			# The best length from loc to goal
			pLength = self.shortestPathMatrix[loc[0]][loc[1]]
			# Iterate all neighbors and check their path length
			for neighbor in self.get_neighbors(loc):
				# If not visited then add to queue
				if self.shortestPathMatrix[neighbor[0]][neighbor[1]] == 696:
					print("(" + str(loc[0]) + "," + str(loc[1]) + "): " + str(self.shortestPathMatrix[loc[0]][loc[1]]))
					v.insert(0, neighbor)
				# If the new path is shorter, record the new path as the best
				if pLength + 1 < self.shortestPathMatrix[neighbor[0]][neighbor[1]]:
					self.shortestPathMatrix[neighbor[0]][neighbor[1]] = pLength + 1
					self.bestParentMatrix[neighbor[0]][neighbor[1]] = loc

	def get_neighbors(self, loc):
		if self.map[loc[0]][loc[1]] == BLOCK:
			return []
		candidates = [[-1,0],[1,0],[0,-1],[0,1]]
		neighbors = []
		for i in range(len(candidates)):
			cdd = [loc[0] + candidates[i][0], loc[1] + candidates[i][1]]
			if self.map[cdd[0]][cdd[1]] != BLOCK:
				neighbors.append(cdd)
		return neighbors

	# Find the best sequence by a-star algorithm
	def find_best_sequence(self, goal):
		fringe = []
		heapq.heappush(fringe, Node(self.map, 0, self.get_estimate(self.map), []))
		moves = [[-1,0],[1,0],[0,-1],[0,1]]
		n_search = 0
		upper_bound = float("Inf")
		fringe_bound = 50
		while True:
			if len(fringe) > 2 * fringe_bound:
				fringe = self.filter_fringe(fringe, 0.5)
			# while len(fringe) > 0 and random.random() < 0.9:
			currentNode = heapq.heappop(fringe)
			sL = len(currentNode.sequence)
			# while len(fringe) > 0:
			# 	levelNode = heapq.heappop(fringe)
			# 	if len(levelNode.sequence) > sL:
			# 		currentNode = levelNode
			# 		break
			n_search = n_search + 1
			if n_search % 200 == 0:
				print("Step: " + str(n_search))
				print("Size of fringe: " + str(len(fringe)))				
				print("The f estimation: " + str(currentNode.fn()))
				print("Sequence length: " + str(len(currentNode.sequence)))
				print("Sequence last step: " + str(currentNode.sequence[-1]))
			# if n_search >= 40000:
			# 	print("No result in " + str(n_search) + " steps"
			# 	return currentNode.sequence
			# All people reached the goal, output the sequence
			if currentNode.map[goal[0]][goal[1]] == self.population:
				return currentNode.sequence
			for move in moves:
				seq = []
				seq = seq + currentNode.sequence
				seq.append(move)
				newMap, newCost = self.apply_move(currentNode.map, move[0], move[1])
				newNode = Node(newMap, currentNode.cost + newCost, self.get_estimate(newMap), seq)
				if newNode.fn() <= upper_bound and random.random() < 0.4:
					heapq.heappush(fringe, newNode)

	def filter_fringe(self, fringe, rate):
		newFringe = []
		while len(fringe) != 0:
			node = heapq.heappop(fringe)
			if random.random() < rate:
				heapq.heappush(newFringe, node)
		return newFringe

	def find_greedy_best_sequence(self, goal):
		n_search = 0
		upper_bound = 20000
		moves = [[-1,0],[1,0],[0,-1],[0,1]]
		currentMap = self.map
		sequence = []
		while n_search < upper_bound:
			if currentMap[goal[0]][goal[1]] == self.population:
				return sequence
			bestMove = None
			bestEstimate = float("Inf")
			for move in moves:
				newMap, newCost = self.apply_move(currentMap, move[0], move[1])
				estimate = self.get_estimate(newMap)
				if estimate < bestEstimate:
					bestEstimate = estimate
					bestMove = move
					currentMap = newMap
			sequence.append(bestMove)
			if n_search % 100 == 0:
				print("Step " + str(n_search) + " estimate: " + str(bestEstimate))
			n_search = n_search + 1
		print("Did not reach goal in " + str(upper_bound) + " steps")
		return sequence
		
	# Sum of shortest path length heuristics
	def get_estimate(self, maze):
		farthestLoc = None
		depth = 0
		newMap = [[0 for j in range(len(maze[0]))] for i in range(len(maze))]
		estimate = 0
		for i in range(len(maze)):
			for j in range(len(maze[0])):
				newMap[i][j] = maze[i][j]
				if maze[i][j] != BLOCK:
					estimate = estimate + self.shortestPathMatrix[i][j] * maze[i][j]
				# if maze[i][j] > 0 and self.shortestPathMatrix[i][j] > depth:
				# 	depth = self.shortestPathMatrix[i][j]
				# 	farthestLoc = [i,j]
		# estimate = 0
		# while farthestLoc[0] != 30 or farthestLoc[1] != 30:
		# 	nextLoc = self.bestParentMatrix[farthestLoc[0]][farthestLoc[1]]
		# 	move = [nextLoc[0] - farthestLoc[0], nextLoc[1] - farthestLoc[1]]
		# 	newMap, newCost = self.apply_move(newMap, move[0], move[1])
		# 	estimate = estimate + newCost
		return estimate

	# Reset the system
	def reset_simulator(self):
		self.colorPower = COLORFACTOR
		self.edit = True
		self.map = self.load_maze(DefaultMazePath)
		self.shortestPathMatrix = None
		self.bestParentMatrix = None
		self.total_cost = 0
		self.drawMap()

	# Save maze
	def save_maze(self):
		outfile = open("maze" + str(random.getrandbits(128)) + ".csv", 'w')
		outfile.write(str(self.map))
		self.drawMap()

	# Load maze from file
	def load_maze(self,filename):
		infile = open(filename, 'r')
		cube = ast.literal_eval('[%s]' % infile.read())
		self.n_row = len(cube[0])
		self.n_col = len(cube[0][0])
		return cube[0]

	# Convert a point to the cell index
	def pixel_to_cell(self, canvas, x, y):
		cellHeight = (canvas.winfo_reqheight() - \
			(DefaultHeaderHeight + DefaultBottomPadding + \
			DefaultTopPadding)) / self.n_row
		cellWidth = (canvas.winfo_reqwidth() - \
			(DefaultLeftPadding + DefaultRightPadding)) / self.n_col
		col = (x - DefaultLeftPadding) / cellWidth
		row = (y - DefaultTopPadding - DefaultHeaderHeight) / cellHeight
		return row, col

	# Convert a cell's index into a pixel boudary
	def cell_to_pixel(self, canvas, row, col):
		cellHeight = (canvas.winfo_reqheight() - \
			(DefaultHeaderHeight + DefaultBottomPadding + \
			DefaultTopPadding)) / self.n_row
		cellWidth = (canvas.winfo_reqwidth() - \
			(DefaultLeftPadding + DefaultRightPadding)) / self.n_col
		left = DefaultLeftPadding + col * cellWidth
		top = DefaultTopPadding + DefaultHeaderHeight + row * cellHeight
		right = DefaultLeftPadding + (col + 1) * cellWidth
		bottom = DefaultTopPadding + DefaultHeaderHeight + (row + 1) * cellHeight
		return left, top, right, bottom

if __name__ == "__main__":
	ms = MazeSimulator()