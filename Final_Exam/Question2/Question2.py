import numpy as np
import math
import os

#All states are enumerated
STATE_NEW = 0
STATE_USED1 = 1
STATE_USED2 = 2
STATE_DEAD = 9

#These states are defined as strings so as to print out policies/actions conveniently
STATE_USE = "GO ON USING"
STATE_REPLACE_NEW = "GET A NEW ONE"
STATE_REPLACE_USED= "GET A USED ONE"

#This class is designed to answer all the questions, which is designed based on materials posted by Prof. Cowan on Sakai
class MarkovDecisionProcess(object):
	
	def __init__(self):
		super(MarkovDecisionProcess, self).__init__()
		self.states = [i for i in range(10)]
		self.rewards = [(100 - 10 * i) for i in range(10)]
		self.rewards[STATE_DEAD] = None
		self.replacement_cost = 250
		self.transition_prob = [(0.1 * j) for j in range(10)]
		self.transition_prob[STATE_NEW] = 1.0
		self.utilitites = [0 for i in range(10)]
		self.beta = 0.9
		#indicate if the highest cost in question c) is found or not
		self.highest_cost_is_found = 0 
			

	def valueIteration(self, init_utilities, epsilon = 1e-10):
		utilities = init_utilities
		policies = [None for i in range(10)]
		delta = 1

		while delta >= epsilon:
			new_utilities = [0 for i in range(10)]
			delta = 0

			for i in range(10):
				new_utilities[i], policies[i] = self.bellmanEquation(i, utilities)
				delta = delta + abs(new_utilities[i] - utilities[i])
			utilities = new_utilities
        
		return utilities, policies
    
    #Based on previous design, additional information from Question c) is added
	def valueIterationUpdated(self, cost,init_utilities, epsilon = 1e-10):
		utilities = init_utilities
		policies = [None for i in range(10)]
		delta = 1

		while delta >= epsilon:
			new_utilities = [0 for i in range(10)]
			delta = 0

			for i in range(10):
				new_utilities[i], policies[i] = self.bellmanEquationUpdated(cost, i, utilities)
				delta = delta + abs(new_utilities[i] - utilities[i])

			utilities = new_utilities
        
        #If buying a used machine becomes a part of the optimal policy, then it is recommended that one can buy a used machine at that cost.
		if (STATE_REPLACE_USED in policies and self.highest_cost_is_found == 0):
			print("\nThe highest price that buying a used machine would still be the rational choice is:" + str(cost))
			self.highest_cost_is_found = 1

		return utilities, policies

    #This function gives the optimal reward for each state as well as a corresponding policy
	def bellmanEquation(self, state, utilities):
		#the estimated price of replacing a new machine
		buy_new_one_utility =  self.beta * utilities[STATE_NEW] - self.replacement_cost

		if state == STATE_DEAD:
			return buy_new_one_utility, STATE_REPLACE_NEW
		
		#the utility of go on using
		go_on_using_utility = self.rewards[state] + self.beta * (self.transition_prob[state] * utilities[state + 1] + (1 - self.transition_prob[state]) * utilities[state])

		if go_on_using_utility > buy_new_one_utility:
			return go_on_using_utility, STATE_USE
		else:
			return buy_new_one_utility, STATE_REPLACE_NEW
        
    #Based on previous design, additional information from Question c) is added
	def bellmanEquationUpdated(self, cost, state, utilities):
		#the estimated price of replacing a new machine
		buy_new_one_utility = self.beta * utilities[STATE_NEW] - self.replacement_cost

		if state == STATE_DEAD:
			return buy_new_one_utility, STATE_REPLACE_NEW

		go_on_using_utility = self.rewards[state] + self.beta * (self.transition_prob[state] * utilities[state + 1] + (1 - self.transition_prob[state]) * utilities[state])

		#Now we have an offer provided by the MachineSellingBot, we should take into account
		buy_used_one_utility = self.beta * (0.5 * utilities[STATE_USED1] + 0.5 * utilities[STATE_USED2]) - cost
		
		if max(go_on_using_utility, buy_new_one_utility, buy_used_one_utility) == go_on_using_utility:
				return go_on_using_utility, STATE_USE
		elif max(go_on_using_utility, buy_new_one_utility, buy_used_one_utility) == buy_new_one_utility:
				return buy_new_one_utility, STATE_REPLACE_NEW
		elif max(go_on_using_utility, buy_new_one_utility, buy_used_one_utility) == buy_used_one_utility:
        		return buy_used_one_utility, STATE_REPLACE_USED         
		else:
			print("\nError")
			return offer, STATE_REPLACE_USED;

	def reset(self, init_utilities = None, beta = 0.9):
		if init_utilities == None:
			self.utilitites = [0 for i in range(10)]
		else:
			self.utilitites = init_utilities

		self.beta = beta

    #This program is designed to answer question d)
	def compare_discount(self, discount_list = [0.1, 0.3, 0.5, 0.7, 0.9, 0.99]):
		for discount in discount_list:
			self.reset(beta = discount)
			utilities, policies = self.valueIteration(self.utilitites)
			print("\nBeta/discount = " + str(discount))
			print("Optimal utilities: \n" + str(utilities))
			print("Optimal policies: \n" + str(policies))

if __name__ == "__main__":
	mdp = MarkovDecisionProcess()

    #This panel is designed for the convenience of selecting different questions to answer
	while True:
		print("\nSelect experiment:")
		print("\t1. Question a) and b)")
		print("\t2. Question c)")
		print("\t3. Question d)")
		print("\t4. Quit the program")
		choice = int(input())

		if choice == 1:
			mdp.beta = 0.9
			utilities, policies = mdp.valueIteration(mdp.utilitites)

			print("The optimal utility for the New state is: " + str(utilities[STATE_NEW]))
			print("And the optimal policy is: " + str(policies[STATE_NEW]))
			print("The optimal utilities for the states from Used1 to Used8 are: " + str(utilities[1:9]))
			print("And the optimal policies are: " + str(policies[1:9]))
			print("The optimal utility for the state Dead is: " + str(utilities[STATE_DEAD]))
			print("And the optimal policy is: " + str(policies[STATE_DEAD]))

		elif choice == 2:
			mdp.beta = 0.9
			mdp.highest_cost_is_found = 0
			for i in range(250):
				if mdp.highest_cost_is_found == 0:
					mdp.valueIterationUpdated(250 - i, mdp.utilitites) 
				else:
					break

		elif choice == 3:
			discounts = [0.1, 0.3, 0.5]
			discounts = discounts + [(1 - math.pow(0.3, i + 1)) for i in range(8)]
			mdp.compare_discount(discount_list = discounts)

		elif choice == 4:
			break
			exit()