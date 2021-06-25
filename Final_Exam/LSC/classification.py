import math
import random
import numpy as np

LABEL_A = 0
LABEL_B = 1

# Decision Tree Node
class TreeNode(object):
	"""docstring for TreeNode"""
	def __init__(self, X, y):
		super(TreeNode, self).__init__()
		# Features
		self.X = X
		# Labels
		self.y = y
		# Rule correspond to the index of a binary attribute, if leaf node
		# the following three are None
		self.rule = None
		# Left node
		self.left = None
		# Right node
		self.right = None
		# indent when print
		self.indent = ""

	def __str__(self):
		# calculate the number of nodes within each class
		n_A = 0
		n_B = 0
		for label in self.y:
			if label == LABEL_A:
				n_A = n_A + 1
			else:
				n_B = n_B + 1
		s = str(n_A + n_B) + "(" + str(n_A) + "/" + str(n_B) + ")"
		if self.rule != None:
			s = s + ", split rule: attr[" + str(self.rule) + "] == 0\n"
			self.left.indent = self.indent + "\t"
			self.right.indent = self.indent + "\t"
			s = s + self.indent + "\tLeft: " + str(self.left)
			s = s + self.indent + "\tRight: " + str(self.right)
		else:
			s = s + "\n"
		return s

	# Directly predict using this node
	def predict(self, threshold = 0.5):
		# Pre-checking
		if threshold < 0.5:
			threshold = 0.5
		if threshold > 1:
			threshold = 1
		# calculate the number of nodes within each class
		n_A = 0
		n_B = 0
		for label in self.y:
			if label == LABEL_A:
				n_A = n_A + 1
			else:
				n_B = n_B + 1
		# Predict based on majority
		if float(n_A) / (n_A + n_B) >= threshold:
			return LABEL_A
		elif float(n_B) / (n_A + n_B) > threshold:
			return LABEL_B
		elif self.rule == None:
			if n_A > n_B:
				return LABEL_A
			else:
				return LABEL_B
		else:
			return None
		
# Decision tree class
class DecisionTree(object):
	"""docstring for DecisionTree"""
	def __init__(self, debug = False):
		super(DecisionTree, self).__init__()
		self.tree = None
		self.debug = debug

	def __str__(self):
		if self.tree == None:
			return "Classifier not trained"
		else:
			return str(self.tree)

	# Training
	def train(self, X, y):
		root = TreeNode(X, y)
		self.tree = self.find_tree(root)

	# Find the decision tree of the root node
	def find_tree(self, root, depth = 0):
		# Node data
		X = root.X
		y = root.y
		# Node entropy
		parentEntropy, s = self.get_entropy(y)
		if self.debug:
			print("Parent: " + s)
		# If not splittable, stop
		if parentEntropy == 0:
			return None
		# Keep track of the best splitting attribute
		bestRule = []
		bestLeft = []
		bestRight = []
		bestInfoGain = 0
		# Find best attribute
		for attrId in range(len(X[0])):
			if self.debug:
				print("Depth(" + str(depth) + ")" + " test attribute: " + str(attrId))
			leftX = []
			rightX = []
			leftY = []
			rightY = []
			# Split by the attribute
			for i in range(len(X)):
				if X[i][attrId] == 0:
					leftX.append(X[i])
					leftY.append(y[i])
				else:
					rightX.append(X[i])
					rightY.append(y[i])
			# Left node entropy
			if len(leftY) == 0:
				# 0 when there are no data
				leftEntropy = 0
				if self.debug:
					print("Left: 0 (0/0)")
			else:
				leftEntropy, s = self.get_entropy(leftY)
				if self.debug:
					print("Left: " + s)
			# Right node entropy
			if len(rightY) == 0:
				# 0 when there are no data
				rightEntropy = 0
				if self.debug:
					print("Right: 0 (0/0)")
			else:
				rightEntropy, s = self.get_entropy(rightY)
				if self.debug:
					print("Right: " + s)
			# Calculate the overall entropy of the splitted nodes
			totalEntropy = (len(leftY) * leftEntropy + len(rightY) * rightEntropy) / (len(y))
			# If this split is better, record this attribute as the split rule
			if parentEntropy - totalEntropy > bestInfoGain:
				bestInfoGain = parentEntropy - totalEntropy
			if parentEntropy - totalEntropy == bestInfoGain:
				bestRule.append(attrId)
				bestLeft.append(TreeNode(leftX, leftY))
				bestRight.append(TreeNode(rightX, rightY))
		# If no information gain, this is a leaf node
		if bestInfoGain == 0:
			return root
		if self.debug:
			print("information gain: " + str(bestInfoGain))
		# Random select from the set of best split
		choice = random.randint(0, len(bestRule) - 1)
		root.rule = bestRule[choice]
		root.left = bestLeft[choice]
		root.right = bestRight[choice]
		if self.debug:
			print("Best rule: " + str(root.rule))
		if self.debug:
			print("Left / Right: " + str(len(root.left.y)) + " / " + str(len(root.right.y)))
		# Find the decision tree of two children nodes
		if self.debug:
			print("\nTry split left node of depth: " + str(depth))
		self.find_tree(root.left, depth = depth + 1)
		if self.debug:
			print("\nTry split right node of depth: " + str(depth))
		self.find_tree(root.right, depth = depth + 1)
		return root

	# Calculate entropy of a list
	def get_entropy(self, y):
		if y == None or len(y) == 0:
			return 0, "0 (0/0)"
		n_A = 0
		n_B = 0
		for i in range(len(y)):
			if y[i] == LABEL_A:
				n_A = n_A + 1
			else:
				n_B = n_B + 1
		pa = float(n_A) / (n_A + n_B)
		pb = 1 - pa
		Ea = 0
		Eb = 0
		if pa != 0:
			Ea = -pa * math.log(pa, 2)
		if pb != 0:
			Eb = -pb * math.log(pb, 2)
		return Ea + Eb, str(n_A + n_B) + " (" + str(n_A) + "/" + str(n_B) + ")"

	# Prediction using decision tree
	def predict(self, X, threshold = 0.7):
		# Pre-checking
		if self.tree == None:
			print("Abort: Tree not established.")
			return None
		# Find the prediction for each input
		results = []
		for x in X:
			currentRoot = self.tree
			while True:
				predict = currentRoot.predict(threshold)
				if predict != None:
					results.append(predict)
					break
				if currentRoot.rule != None:
					if x[currentRoot.rule] == 0:
						currentRoot = currentRoot.left
					else:
						currentRoot = currentRoot.right
				if currentRoot == None:
					print("Error: reached empty node in prediction")
					results.append(LABEL_A)
					break
		return results

class Perceptron(object):
	"""docstring for Perceptron"""
	def __init__(self):
		super(Perceptron, self).__init__()
		self.weights1 = None
		self.weights2 = None
		self.alpha = 1

	def train(self, X_train, y_train, n_hidden = 8, epsilon = 1e-3, alpha = 1):
		print("Number of nodes in hidden layer: " + str(n_hidden))
		self.alpha = alpha
		# m * n matrix
		X = np.array(X_train)
		# m * 1 vector
		y = np.array(y_train)
		m = len(X)
		n = len(X[0])
		X = np.vstack((np.ones(m), X.T)).T
		print("(" + str(m) + "," + str(n) + ")")
		self.weights1 = np.random.rand(n+1, n_hidden) * 2 / n
		self.weights2 = np.random.rand(n_hidden, 1) / n_hidden
		delta = 1
		step = 0
		while delta > epsilon:
			newWeights1 = np.matrix.copy(self.weights1)
			newWeights2 = np.matrix.copy(self.weights2)
			# Back propagation
			for i in range(m):
				# Forward
				z1 = np.matmul(X, newWeights1)
				# # sigmoid hidden layer
				# hidden_results = float(1.0) / (1 + np.exp(-z1))
				# linear hidden layer
				hidden_results = alpha * z1
				# # Relue hidden layer
				# hidden_results = self.relu(z1)
				z2 = np.matmul(hidden_results, newWeights2)
				y_pred = float(1.0) / (1 + np.exp(-z2))
				error = np.matrix(y).T - y_pred
				# scalar
				delta_y = error[i,0]
				# print("delta_y: " + str(delta_y)
				# scalar
				o_y = y_pred[i][0]
				# print("o_y: " + str(o_y)
				# 1 * J vector
				o_hidden = hidden_results[i]
				# print("o_hidden: " + str(len(o_hidden))
				# 1 * J vector
				delta2 = -(delta_y * o_y * (1 - o_y)) * o_hidden
				# print("delta2: " + str(len(delta2))
				# J * 1 matrix
				error_hidden = np.multiply(np.matrix(delta2).T, newWeights2)
				# n * J matrix
				# # Sigmoid back propagation
				# delta1 = np.multiply(np.matrix(np.multiply(o_hidden, (1 - o_hidden))).T, error_hidden) * X[i]
				# delta1 = delta1.T
				delta1 = error_hidden * X[i]
				delta1 = alpha * delta1.T
				delta2 = np.matrix(delta2).T
				# Update weights
				# stId = random.randint(0, n-1)
				# newWeights1[stId] = newWeights1[stId] - delta1[stId]
				newWeights1 = newWeights1 - delta1
				newWeights2 = newWeights2 - delta2
				# print("weights1: " + str(newWeights1.shape)
				# print("weights2: " + str(newWeights2.shape)
			delta = self.matrix_distance(newWeights2, self.weights2) + \
				self.matrix_distance(newWeights1, self.weights1)
			if step % 50 == 0:
				print("Iteration: " + str(step) + "\n\tGradiant amplitude:" + str(delta))
			step = step + 1
			# print("Gradient amplitude: " + str(delta)
			self.weights1 = newWeights1
			self.weights2 = newWeights2

	def matrix_distance(self, m1, m2):
		dist = 0
		for i in range(len(m1)):
			for j in range(len(m1[0])):
				dist = dist + abs(m1[i,j] - m2[i,j])
		return dist

	def predict(self, X_test):
		# Forward
		X_test = np.array(X_test)
		X = np.vstack((np.ones(len(X_test)), X_test.T)).T
		z1 = np.matmul(X, self.weights1)
		# # Sigmoid hiddenlayer
		# hidden_results = 1 / (1 + np.exp(-z1))
		# Linear hidden layer
		hidden_results = self.alpha * z1
		z2 = np.matmul(hidden_results, self.weights2)
		y_pred = 1 / (1 + np.exp(-z2))
		results = []
		for i in range(len(y_pred)):
			if y_pred[i,0] > 0.5:
				results.append(1)
			else:
				results.append(0)
		return results

class Classifier(object):
	"""docstring for Classifier"""
	def __init__(self, debug = False):
		super(Classifier, self).__init__()
		self.clsAdata = None
		self.clsBdata = None
		self.testData = None
		self.dt = None
		self.pt = None
		self.debug = debug

	def load_files(self):
		self.clsAdata = self.load_file('ClassA.txt')
		print(len(self.clsAdata))
		self.clsBdata = self.load_file('ClassB.txt')
		print(len(self.clsBdata))
		self.testData = self.load_file('Mystery.txt')
		print(len(self.testData))

	def load_file(self, filename):
		infile = open(filename, 'r')
		print("Load " + filename)
		row = 0
		grid = [[0 for j in range(5)] for i in range(5)]
		data = []
		for line in infile:
			if row == 5:
				row = 0
				continue
			splits = line[:-1].split("\t")
			for i in range(5):
				grid[row][i] = int(splits[i])
			row = row + 1
			if row == 5:
				data.append(grid)
				grid = [[0 for j in range(5)] for i in range(5)]
				continue
		print(data)
		return data
	
	# Training method:
	# 1. DT - Decision Tree
	# 2. NN - Neural Network
	def train(self, method = "DT", n_hidden = 8):
		X = self.reshape_X(self.clsAdata + self.clsBdata)
		y = [LABEL_A for i in range(len(self.clsAdata))] + \
			[LABEL_B for j in range(len(self.clsBdata))]
		if method == "DT":
			self.dt = DecisionTree(debug = self.debug)
			self.dt.train(X, y)
			return self.dt
		elif method == "PT":
			self.pt = Perceptron()
			self.pt.train(X, y, n_hidden = n_hidden)
			return self.pt


	# Reshape the input matrix data to vector features
	def reshape_X(self, data):
		X = []
		for matrix in data:
			v = []
			for row in matrix:
				v = v + row
			X.append(v)
		return X

	# Generate model using cross validation
	def run_holdout(self, n_rounds = 10):
		self.load_files()
		# The set of models to generate
		models = [None for i in range(n_rounds)]
		XA = self.clsAdata
		XB = self.clsBdata
		# Run multiple rounds
		for i in range(n_rounds):
			sel = random.randint(0, 4)
			X_train = self.clsAdata[0:sel] + self.clsAdata[sel+1:5] + \
				self.clsBdata[0:sel] + self.clsBdata[sel+1:5]
			X_val = [self.clsAdata[sel]] + [self.clsBdata[sel]]
			y_train = [LABEL_A for i in range(4)] + [LABEL_B for i in range(4)]
			y_val = [LABEL_A, LABEL_B]
		return



if __name__ == "__main__":
	while True:
		print("Choose the experiment:")
		print("1. Test decision tree classification on the data.")
		print("2. 5-fold cross-validation for decision tree.")
		print("3. 1-hidden-layer perceptron method.")
		print("0. Exit.")
		choice = int(raw_input())
		if choice == 0:
			break
		elif choice == 1:
			print("Training classifier")
			clf = Classifier(debug = True)
			clf.load_files()
			clf.train()
			print("\nFinal Decision Tree: ")
			print(clf.dt)
			print("Prediction: ")
			X = clf.reshape_X(clf.testData)
			print(clf.dt.predict(X, threshold = 1))
			print("Note: decision tree does not guarantee a same model if you run multiple times, each time only local optimum is reached.\n")
		elif choice == 2:
			clf = Classifier()
			clf.run_holdout(n_rounds=10)
		elif choice == 3:
			print("Enter the number of nodes in hidden layer:")
			n_hidden = int(raw_input())
			clf = Classifier()
			clf.load_files()
			clf.train(method = "PT", n_hidden=n_hidden)
			print("Weights1:")
			print(clf.pt.weights1)
			print("Weights2")
			print(clf.pt.weights2)
			X_test = clf.reshape_X(clf.testData)
			print("Prediction: ")
			print(clf.pt.predict(X_test)
			continue		