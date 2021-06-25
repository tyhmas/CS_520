# TODO: Add comment
# 
# Author: Yujie Ren
###############################################################################
library(nnet)
source('https://gist.githubusercontent.com/fawda123/7471137/raw/466c1474d0a505ff044412703516c34f1a4684a5/nnet_plot_update.r')

hidden = 8
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
nn.input <- data.frame(type, input)

#Now we start to construct our own neural network with 1 hidden layer of 10 neurons
for(i in 1 : groups){
	row <- c(i, i + groups)
	train_set <- nn.input[-row,]
	test_set <- nn.input[row,]	
	model <- nnet(train_set, train_set$y, size = hidden, linout = F)
	#results <- compute(model_kfold, newdata=test_set, interval = "class")
	#print(results)
}
#plot.nnet(model)
test2 <- data[(rows + 1) : (rows  + 5),][1:25]
names(test2) <- c('x1','x2','x3','x4','x5','x6','x7','x8','x9','x10','x11','x12','x13','x14','x15','x16','x17','x18','x19','x20','x21','x22','x23','x24','x25')
test2.types <- predict(model, newdata=test2, interval = "class")
print(test2.types)