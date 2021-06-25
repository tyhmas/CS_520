import numpy as np
import random

class LandscapeCell(object):
    def __init__(self, row, column):
        self.row = row
        self.column = column
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
        self.landscape_belief_instant = np.full((dim, dim), 1 / (dim * dim))
        self.landscape_terrain_can_find = np.full((dim, dim), 0.1)
        self.generate_landscape()
        self.rule = ''
        self.cur_location_x = 0
        self.cur_location_y = 0

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
        print('The size of land for hunting: ', len(self.hunting_landscape), len(self.hunting_landscape[0]))
        target = self.hunting_landscape[self.target_x][self.target_y]
        target.set_target(True)

    def recover_landscape(self):
        self.landscape_belief = np.full((self.dim, self.dim), 1/(self.dim*self.dim))
        self.landscape_belief_instant = np.full((self.dim, self.dim), 1/(self.dim*self.dim))
        self.cur_location_x = 0
        self.cur_location_y = 0

    def get_next(self, rule):
        if rule is 'rule1':
            return divmod(self.landscape_belief_instant.argmax(), self.dim)
        elif rule is 'rule2':
            belief_to_find = self.landscape_belief * self.landscape_terrain_can_find
            return divmod(belief_to_find.argmax(), self.dim)
        elif rule is 'random':
            random_row = random.randint(0, self.dim - 1)
            random_column = random.randint(0, self.dim - 1)
            return random_row, random_column

    #To check if the target can be found in the cell or not
    def check_cell(self, row, column):
        cell = self.hunting_landscape[row][column]
        if cell.is_target:
            rand = random.random()
            print('Now we search in the target cell, which is a', cell.terrain, 'cell, and the probability for finding the target is',1 - cell.not_found_probability)
            return cell, rand > cell.not_found_probability
        else:
            return cell, False

    def get_belief(self, row, column):
        return self.landscape_belief[row][column]

    #Each time a new belief is generated, all cells' belief need to be updated
    def update_all_belief(self, row, column, new_belief):
        self.landscape_belief[row][column] = new_belief
        print('the sum is', self.landscape_belief.sum())
        for i in range(self.dim):
            for j in range (self.dim):
                distance = abs(i - self.cur_location_x) + abs(j - self.cur_location_y)
                if distance != 0:
                    self.landscape_belief_instant[i][j] = self.landscape_belief[i][j] / distance
                else:
                    self.landscape_belief_instant[i][j] = self.landscape_belief[i][j]

    def update_curr(self, row, column):
        if (self.cur_location_x >= 0 and self.cur_location_x < self.dim) and (self.cur_location_y - 1 >= 0 and self.cur_location_y - 1 < self.dim):
            up = abs(self.cur_location_x - row) + abs(self.cur_location_y - 1 - column)
        else:
            up = 99999

        if (self.cur_location_x >= 0 and self.cur_location_x < self.dim) and (self.cur_location_y + 1 >= 0 and self.cur_location_y + 1 < self.dim):
            down = abs(self.cur_location_x - row) + abs(self.cur_location_y + 1 - column)
        else:
            down = 99999

        if (self.cur_location_x - 1 >= 0 and self.cur_location_x < self.dim) and (self.cur_location_y >= 0 and self.cur_location_y < self.dim):
            left = abs(self.cur_location_x - 1 - row) + abs(self.cur_location_y - column)
        else:
            left = 99999

        if (self.cur_location_x + 1 >= 0 and self.cur_location_x + 1 < self.dim) and (self.cur_location_y >= 0 and self.cur_location_y < self.dim):
            right = abs(self.cur_location_x + 1 - row) + abs(self.cur_location_y - column)
        else:
            right = 99999

        selfDis = abs(self.cur_location_x - row) + abs(self.cur_location_y - column)

        array = []
        array.append(up)
        array.append(down)
        array.append(left)
        array.append(right)
        array.append(selfDis)
        min_pos = array.index(min(array))

        if min_pos is 0:
            self.cur_location_y -= 1
        elif min_pos is 1:
            self.cur_location_y += 1
        elif min_pos is 2:
            self.cur_location_x -= 1
        elif min_pos is 3:
            self.cur_location_x += 1

    def search(self, rule='random'):
        if rule is 'rule1':
            print('\nSearch cells with the highest probability of containing the target')
        elif rule is 'rule2':
            print('\nSearch cells with the highest probability of finding the target')
        elif rule is 'random':
            print('\nSeach cells in the uniform random searching')
        self.rule = rule
        
        steps = 0
        found_result = False
        
        while not found_result :
            row, column = self.get_next(rule)
            self.update_curr(row, column)
            steps += 1
            checked_cell, found_result = self.check_cell(self.cur_location_x, self.cur_location_y)
            checked_cell_old_belief = self.get_belief(self.cur_location_x, self.cur_location_y)
            checked_cell_new_belief = checked_cell.not_found_probability * checked_cell_old_belief
            self.update_all_belief(self.cur_location_x, self.cur_location_y, checked_cell_new_belief)
        
        return steps, self.cur_location_x, self.cur_location_y