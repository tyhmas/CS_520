# TODO: Add comment
# 
# Author: Yujie Ren
###############################################################################
library(class)
k = 5
groups = 8
rows = groups * 2

data <- read.csv('AI_data.csv', header = FALSE)
input <- data[1:rows,]
categories <- train.data[1:rows,][1:25]
input <- data.frame(input)
names(input) <- c('x1','x2','x3','x4','x5','x6','x7','x8','x9','x10','x11','x12','x13','x14','x15','x16','x17','x18','x19','x20','x21','x22','x23','x24','x25')
type <- train.data[1:rows,][26]
type <- data.frame(type)

names(type) <- c('y')
knn.input <- data.frame(type, input)

test.input <- data[(rows  + 1) : (rows  + 5),][1:25]
names(test.input) <- c('x1','x2','x3','x4','x5','x6','x7','x8','x9','x10','x11','x12','x13','x14','x15','x16','x17','x18','x19','x20','x21','x22','x23','x24','x25')
cl <- knn.input$y

test.types <- knn(input, test.input, cl, k = k)
print(test.types)