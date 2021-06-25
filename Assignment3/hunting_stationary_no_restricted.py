import numpy as np
import random

class LandscapeCell(object):
    def __init__(self, row, column):
        self.row = row
        self.column = column
        self.target = False
        self.terrain = ''
        self.is_target = False
        self.not_found_probability = 0.1

    def set_terrain(self, terrain):
        self.terrain = terrain
        if terrain == 'flat':
            self.not_found_probability = 0.1
        elif terrain == 'hilly':
            self.not_found_probability = 0.3
        elif terrain == 'forested':
            self.not_found_probability = 0.7
        elif terrain == 'maze':
            self.not_found_probability = 0.9

    def set_target(self, target):
        self.is_target = target


class ProbabilisticHunting(object):
    def __init__(self, dim=50):
        self.dim = dim
        self.hunting_landscape = []
        self.landscape_belief = np.full((dim, dim), 1/(dim*dim))
        self.landscape_terrain_can_find = np.full((dim, dim), 0.1)
        self.generate_landscape()
        self.rule = ''

    def generate_landscape(self):
        self.target_x = random.randint(0, self.dim-1)
        self.target_y = random.randint(0, self.dim-1)
        for row in range(self.dim):
            cells = []
            for column in range(self.dim):
                cell = LandscapeCell(row, column)
                rand = random.random()
                if rand < 0.2:
                    cell.set_terrain('flat')
                    self.landscape_terrain_can_find[row][column] = 1 - 0.1
                elif rand >= 0.2 and rand < 0.5:
                    self.landscape_terrain_can_find[row][column] = 1 - 0.3
                    cell.set_terrain('hilly')
                elif rand >= 0.5 and rand < 0.8:
                    self.landscape_terrain_can_find[row][column] = 1 - 0.7
                    cell.set_terrain('forested')
                elif rand >= 0.8:
                    self.landscape_terrain_can_find[row][column] = 1 - 0.9
                    cell.set_terrain('maze')
                cells.append(cell)
            self.hunting_landscape.append(cells)
        print('landscape:', len(self.hunting_landscape), len(self.hunting_landscape[0]))
        target = self.hunting_landscape[self.target_x][self.target_y]
        target.set_target(True)
        print('target:',target.terrain, target.row, target.column)

    def recover_landscape(self):
        self.landscape_belief = np.full((self.dim, self.dim), 1/(self.dim*self.dim))

    def get_next(self, rule):
        if rule is 'rule1':
            return divmod(self.landscape_belief.argmax(), self.dim)
        elif rule is 'rule2':
            belief_to_find = self.landscape_belief * self.landscape_terrain_can_find
            return divmod(belief_to_find.argmax(), self.dim)
        elif rule is 'random':
            random_row = random.randint(0, self.dim - 1)
            random_column = random.randint(0, self.dim - 1)
            return random_row, random_column

    def check_cell(self, row, column):
        cell = self.hunting_landscape[row][column]
        if cell.is_target:
            rand = random.random()
            print('search in target cell:', cell.terrain, cell.not_found_probability, rand)
            return cell, rand > cell.not_found_probability
        else:
            return cell, False

    def get_belief(self, row, column):
        return self.landscape_belief[row][column]

    #Each time a new belief is generated, all cells' belief need to be updated
    def update_all_belief(self, row, column, new_belief):
        self.landscape_belief[row][column] = new_belief
        belief_sum = self.landscape_belief.sum()
        self.landscape_belief = self.landscape_belief / belief_sum

    def search(self, rule='random'):
        if rule is 'rule1':
            print('\nSearch cells with the highest probability of containing the target')
        elif rule is 'rule2':
            print('\nSearch cells with the highest probability of finding the target')
        elif rule is 'random':
            print('\nSeach cells in the uniform random searching')
        self.rule = rule
        
        row, column = self.get_next(rule)
        steps = 1
        checked_cell, found_result = self.check_cell(row, column)
        
        while not found_result:
            checked_cell_old_belief = self.get_belief(row, column)
            checked_cell_new_belief = checked_cell.not_found_probability * checked_cell_old_belief
            
            self.update_all_belief(row, column, checked_cell_new_belief)

            row, column = self.get_next(rule) 
            steps += 1
            checked_cell, found_result = self.check_cell(row, column)
        
        return steps, row, column