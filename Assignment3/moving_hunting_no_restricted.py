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
        self.changing_landscape_belief = np.full((dim, dim), 1 / (dim * dim))
        self.landscape_belief = np.full((dim, dim), 1 / (dim * dim))
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
        print('The size of land for hunting: ', len(self.hunting_landscape), len(self.hunting_landscape[0]))
        target = self.hunting_landscape[self.target_x][self.target_y]
        target.set_target(True)

    def recover_landscape(self):
        self.landscape_belief = np.full((self.dim, self.dim), 1/(self.dim*self.dim))

    def get_next(self, rule):
        if rule is 'rule1':
            return divmod(self.changing_landscape_belief.argmax(), self.dim)
        elif rule is 'rule2':
            belief_to_find = self.changing_landscape_belief * self.landscape_terrain_can_find
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
    
    #Figure out which type should be examed in neighbor cells of a given cell at (row, column)
    def type_to_check(self, row, column, type_report):
        cell_type = ''
        known_landscape_type = self.hunting_landscape[row][column].terrain
        if len(set(type_report)) is 1:
            cell_type = known_landscape_type
        else:
            for each_type in type_report:
                if each_type is not known_landscape_type:
                    cell_type = each_type
        return cell_type

    #Check out how many neighbor cells share the same type with the given one at (row, column), then update belief states of them
    def count_proper_neighbor(self, row, column, type_report):
        cell_type = ''
        number_of_proper_type = 0
        neighbor_row_list = [row - 1, row + 1]
        neighbor_column_list = [column -1, column + 1]
        given_landscape_type = self.hunting_landscape[row][column].terrain
        for neighbor_row in neighbor_row_list:
            if neighbor_row >= 0 and neighbor_row < self.dim:
                cell_type = self.type_to_check(row, column, type_report)
                if self.hunting_landscape[neighbor_row][column].terrain is cell_type:
                    number_of_proper_type += 1
                
        for neighbor_column in neighbor_column_list:
            if neighbor_column >= 0 and neighbor_column < self.dim:
                cell_type = self.type_to_check(row, column, type_report)
                if self.hunting_landscape[row][neighbor_column].terrain is cell_type:
                    number_of_proper_type += 1

        return number_of_proper_type
    
    #Update belief states for all neighbor cells of a given one with which share the same type 
    def update_neighbor_cell_belief(self, row, column, number_of_neighbors, landscape_type):
        p = self.landscape_belief[row][column] / number_of_neighbors
        neighbor_row_list = [row - 1, row + 1]
        neighbor_column_list = [column -1, column + 1]
        for neighbor_row in neighbor_row_list:
            if neighbor_row >= 0 and neighbor_row < self.dim:
                if self.hunting_landscape[neighbor_row][column].terrain is landscape_type:
                    self.changing_landscape_belief[neighbor_row][column] += p
                    self.changing_landscape_belief[row][column] = self.changing_landscape_belief[row][column] - p #the original belief state should be updated as well
                
        for neighbor_column in neighbor_column_list:
            if neighbor_column >= 0 and neighbor_column < self.dim:
                if self.hunting_landscape[row][neighbor_column].terrain is landscape_type:
                    self.changing_landscape_belief[row][neighbor_column] += p
                    self.changing_landscape_belief[row][column] = self.changing_landscape_belief[row][column] - p
    
    #Each time a new type report is generated, all cells' belief need to be updated
    def update_all_belief(self, type_report):

        landscape_type = ''
        landscape_type_to_check = ''
        number_of_proper_neighbors = 0
        for row in range(self.dim):
            for column in range(self.dim):
                cell = self.hunting_landscape[row][column]
                landscape_type = cell.terrain

                if landscape_type not in type_report:
                    self.changing_landscape_belief[row][column] = 0
                else:
                    number_of_proper_neighbors = self.count_proper_neighbor(row, column, type_report)
                    #if all of its neighbors' type are not in the report, make the belief as 0
                    if number_of_proper_neighbors == 0:
                        self.changing_landscape_belief[row][column] = 0
                    #count the number of such neighbors, and distribute its belief to them with a uniform distribution
                    else:
                        landscape_type_to_check = self.type_to_check(row, column, type_report)
                        self.update_neighbor_cell_belief(row, column, number_of_proper_neighbors, landscape_type_to_check)			
        
        
    def search(self, rule='random'):
        if rule is 'rule1':
            print('\nSearch cells with the highest probability of containing the target')
        elif rule is 'rule2':
            print('\nSearch cells with the highest probability of finding the target')
        elif rule is 'random':
            print('\nSeach cells in the uniform random searching')
        self.rule = rule
        
        steps = 0
        type_report = []
        movement = 0;
        
        row, column = self.get_next(rule)
        steps += 1
        checked_cell, found_result = self.check_cell(row, column)

        while not found_result:           
            old_target = self.hunting_landscape[self.target_x][self.target_y]
            target_row = self.target_x
            target_column = self.target_y
            old_target.set_target(False)
            
            type_report.clear()
            type_report.append(old_target.terrain)
            
            new_target_row = -1
            new_target_column = -1
			
            #If the target is not found, it would move to one of its neighbor cells randomly
            while new_target_row < 0 or new_target_row >= self.dim or new_target_column < 0 or new_target_column >= self.dim:
                # define how a target would move, 0 represents to go up, 1 represents to go down, 2 represents to go left, 3 represents to go right
                movement = random.randint(0,3) 
                if movement is 1:
                    new_target_column = target_column - 1
                    new_target_row = target_row
                elif movement is 2:
                    new_target_column = target_column + 1
                    new_target_row = target_row
                elif movement is 3:
                    new_target_row = target_row - 1
                    new_target_column = target_column
                else:
                    new_target_row = target_row + 1
                    new_target_column = target_column
                    
            self.target_x = new_target_row
            self.target_y = new_target_column        
            target = self.hunting_landscape[self.target_x][self.target_y]
            target.set_target(True)
            target_terrain = self.hunting_landscape[self.target_x][self.target_y].terrain
            
            #Generate a report
            type_report.append(target_terrain)      

            #Update all belief based on the latest movement    
            self.update_all_belief(type_report) #all belief states should be updated for each unsuccessful search

            
            row, column = self.get_next(rule)
            checked_cell, found_result = self.check_cell(row, column)
            steps += 1

            #Resume beliefs for next probable movement
            self.changing_landscape_belief = np.full((self.dim, self.dim), 1 / (self.dim * self.dim))
        return steps, row, column