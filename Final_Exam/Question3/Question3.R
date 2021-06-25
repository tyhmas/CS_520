# TODO: Add comment
# 
# Author: Guo Chen
###############################################################################
#install.packages('class')
#install.packages('nnet')
library(class)
library(nnet)
#source('https://gist.githubusercontent.com/fawda123/7471137/raw/466c1474d0a505ff044412703516c34f1a4684a5/nnet_plot_update.r')

#Each class has 7 images for training
group_number = 7

#All these parameters associated with models are listed here and are all adjustable
knn_parameter = 5
hidden_neuron_number = 15

train.preprocess <- function(data, group_number){
	train.row_length = group_number * 2
	
	train.data <- data[1:train.row_length,]
	train.features <- train.data[1:train.row_length,][1:25]
	
	train.features <- data.frame(train.features)
	names(train.features) <- c('x1','x2','x3','x4','x5','x6','x7','x8','x9','x10','x11','x12','x13','x14','x15','x16','x17','x18','x19','x20','x21','x22','x23','x24','x25')
	
	train.type <- train.data[1:train.row_length,][26]
	train.type <- data.frame(train.type)
	names(train.type) <- c('y')
	
	input <- data.frame(train.type,train.features)
	
	return(input)
}

valid.preprocess <- function(data, group_number){
	train.row_length = group_number * 2
	
	valid.features <- data[(train.row_length  + 1) : (train.row_length  + 5),][1:25]
	names(valid.features) <- c('x1','x2','x3','x4','x5','x6','x7','x8','x9','x10','x11','x12','x13','x14','x15','x16','x17','x18','x19','x20','x21','x22','x23','x24','x25')
	
	input <- data.frame(valid.features)
	
	return(input)
}

kNN_classification <- function(group_number, knn_parameter, train.input, valid.input){
	train.features = train.input[, 1: 25]
	valid.features = valid.input[, 1: 25]
	
	#the categories to which labeled images belong
	cl <- train.input$y
	
	valid.types <- knn(train.features, valid.features, cl, k = knn_parameter)
	
	cat("The categories of mystery images are: (1 represents class A, 0 represents class B)", "\n")
	print(valid.types)
	cat("\n")
	
}

NN_classification <- function(group_number, hidden_neuron_number, train.input, valid.input){
	#Cross validation method is applied in this training to avoid overfitting
	for(i in 1 : group_number){
		row_index <- c(i, i + group_number)
		cross_validation_train_set <- train.input[-row_index,]
		cross_validation_test_set <- train.input[row_index,]
		model_kfold <- nnet(cross_validation_train_set, cross_validation_train_set$y, 
				size = hidden_neuron_number, linout = F)
		pred_results <- predict(model_kfold, newdata=cross_validation_test_set, 
				interval = "prediction")
		print(pred_results)
	}
	
	valid.types <- predict(model_kfold, newdata=valid.input, interval = "class")
	for (i in 1:length(valid.types)){
		if (valid.types[i] > 0.5)
			valid.types[i] = 1
		else
			valid.types[i] = 0
	}
	cat("The categories of mystery images are: (1 represents class A, 0 represents class B)", "\n")
	print(valid.types)
	cat("\n")
}

cat("Please select an input file:\n")
data <- read.csv(file.choose(), header = FALSE)

while (TRUE){
	cat("Select experiment:\n")
	cat("\t1. Question a)\n")
	cat("\t2. Question c)\n")
	cat("\t3. Quit the program\n")
	
	choice <- readline("Which option would you like to choose? ")
	#kNN algorithm is used here
	if (choice == "1"){
	    knn.train.input <- train.preprocess(data, group_number)
		knn.valid.input <- valid.preprocess(data, group_number)
		kNN_classification(group_number, knn_parameter, knn.train.input, knn.valid.input)
	}
	
	#neural network is used here
	if (choice == "2"){
		nn.train.input <- train.preprocess(data, group_number)
		nn.valid.input <- valid.preprocess(data, group_number)
		NN_classification(group_number, hidden_neuron_number, nn.train.input, nn.valid.input)
	}
	
	if (choice == "3"){
		break
	}	
}