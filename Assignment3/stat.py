import probabilistic_hunting
import random

if __name__ == '__main__':

        """
        for get_next function
        rule1: search the cell with the highest probability of containing the target
        rule2: search the cell with the highest probability of finding the target
        """

        size = 50
        s_r = []
        s_1 = []
        s_2 = []
        a_1 = 0
        a_2 = 0

        t = 'maze'
        hunting = probabilistic_hunting.ProbabilisticHunting(size)
        target = hunting.hunting_landscape[hunting.target_x][hunting.target_y]
        while target.terrain != t:
            target.set_target(False)
            hunting.target_x = random.randint(0, hunting.dim - 1)
            hunting.target_y = random.randint(0, hunting.dim - 1)
            target = hunting.hunting_landscape[hunting.target_x][hunting.target_y]
            target.set_target(True)

        iter = 1000
        for i in range(1, iter):

            count_1, row, column = hunting.search('rule1')
            print('In total:', count_1, 'steps')
            s_1.append(count_1)
            a_1 += count_1

            hunting.recover_landscape()

            count_2, row, column = hunting.search('rule2')
            print('In total:', count_2, 'steps')
            s_2.append(count_2)
            a_2 += count_2
         
            hunting.recover_landscape()
            

            target.set_target(False)
            hunting.target_x = random.randint(0, hunting.dim - 1)
            hunting.target_y = random.randint(0, hunting.dim - 1)
            target = hunting.hunting_landscape[hunting.target_x][hunting.target_y]
            target.set_target(True)

            while target.terrain != t:
                target.set_target(False)
                hunting.target_x = random.randint(0, hunting.dim - 1)
                hunting.target_y = random.randint(0, hunting.dim - 1)
                target = hunting.hunting_landscape[hunting.target_x][hunting.target_y]
                target.set_target(True)

        a_1 = a_1/iter
        a_2 = a_2/iter


        print('in ', t, 'target in: ', row, column)
        print('rule1, average step: ', a_1)
        print('rule2, average step: ', a_2)