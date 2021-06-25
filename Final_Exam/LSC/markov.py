import math

ST_NEW = 0
ST_DEAD = 9
ACT_USE = "USE"
ACT_REPLACE = "REPLACE"

class OldMachineProblem(object):
	"""docstring for OldMachineProblem"""
	def __init__(self):
		super(OldMachineProblem, self).__init__()
		self.states = [i for i in range(10)]
		self.aUseRewards = [(100 - 10 * i) for i in range(10)]
		self.aUseRewards[ST_DEAD] = None
		self.replaceCost = -250
		self.aUseTransmission = [(0.1 * j) for j in range(10)]
		self.aUseTransmission[ST_NEW] = 1.0
		self.utilitites = [0 for i in range(10)]
		self.gamma = 0.9

	def reset_params(self, init_utils = None, gamma = 0.9):
		if init_utils == None:
			self.utilitites = [0 for i in range(10)]
		else:
			self.utilitites = init_utils
		self.gamma = gamma

	def compare_discount(self, discount_list = [0.1, 0.3, 0.5, 0.7, 0.9, 0.99]):
		for discount in discount_list:
			self.reset_params(gamma = discount)
			utils, policies, nSteps = self.value_iteration(self.utilitites)
			print "\nDiscount = " + str(discount)
			print "Number of step to convergence: " + str(nSteps)
			print "Optimal utilities: \n" + str(utils)
			print "Optimal policies: \n" + str(policies)
			

	def value_iteration(self, init_utils, epsilon = 1e-10):
		utils = init_utils
		policies = [None for i in range(10)]
		delta = 1
		step = 0
		while delta >= epsilon:
			newUtils = [0 for i in range(10)]
			delta = 0
			for i in range(10):
				newUtils[i], policies[i] = self.bellman_update(i, utils)
				delta = delta + abs(newUtils[i] - utils[i])
			utils = newUtils
			step = step + 1
			# print "\nStep " + str(step)
			# print "Utilities: \n" + str(utils)
			# print "Delta: " + str(delta)
		return utils, policies, step

	# def bellman_update(self, state, utils):
	# 	# action REPLACE
	# 	estimate = self.replaceCost + utils[ST_NEW]
		
	# 	if state == ST_DEAD:
	# 		return estimate, ACT_REPLACE
	# 	# action USE
	# 	re = self.aUseRewards[state]
	# 	re = re + self.aUseTransmission[state] * utils[state + 1] + \
	# 		(1 - self.aUseTransmission[state]) * utils[state]
	# 	if re > estimate:
	# 		return (self.gamma * re), ACT_USE
	# 	else:
	# 		return (self.gamma * estimate), ACT_REPLACE

	def bellman_update(self, state, utils):
		estimate = self.replaceCost + self.gamma * utils[ST_NEW]
		if state == ST_DEAD:
			return estimate, ACT_REPLACE
		# action USE
		re = self.aUseRewards[state] + self.gamma * (\
			self.aUseTransmission[state] * utils[state + 1] + \
			(1 - self.aUseTransmission[state]) * utils[state])
		if re > estimate:
			return re, ACT_USE
		else:
			return estimate, ACT_REPLACE

if __name__ == "__main__":
	omp = OldMachineProblem()
	while True:
		print "\nSelect experiment:"
		print "\t1. Calculate the optimal utilities and policies for gamma = 0.9"
		print "\t2. Test optimal policies for different gamma"
		print "\t0. Exit"
		choice = int(raw_input())
		if choice == 0:
			break
		elif choice == 1:
			omp.gamma = 0.9
			utils, policies, nSteps = omp.value_iteration(omp.utilitites)
			print "\nOptimal utilities:"
			print "State(New): " + str(utils[ST_NEW])
			print "State(Used1 - Used8):" + str(utils[1:9])
			print "State(Dead): " + str(utils[ST_DEAD])
			print "\nOptimal policies:"
			print "Policy(New): " + str(policies[ST_NEW])
			print "Policy(Used1 - Used8):" + str(policies[1:9])
			print "Policy(Dead): " + str(policies[ST_DEAD])
			print "Number of steps to convergence: " + str(nSteps)
		elif choice == 2:
			discounts = [0.1, 0.3, 0.5]
			discounts = discounts + [(1 - math.pow(0.3, i + 1)) for i in range(8)]
			omp.compare_discount(discount_list = discounts)