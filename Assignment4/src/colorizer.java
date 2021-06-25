import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

public class colorizer {
	//set primary pars
	
	public double lr=0.001;
	public int numberOfNodes1=9;// 9 or 25
	public int numberOfNodes2=4;
	public int numberOfNodes3=3;
	
	public double[] input = new double[this.numberOfNodes1];;
	public double[][] w1 = new double[this.numberOfNodes1][this.numberOfNodes2];;
	public double[] b1 = new double[this.numberOfNodes2];
	public double[] y1 = new double[this.numberOfNodes2];
	public double[][] w2 = new double[this.numberOfNodes2][this.numberOfNodes3];
	public double[] b2 = new double[this.numberOfNodes3];
	public double[] y2 = new double[this.numberOfNodes3];
	public double[] t = new double[this.numberOfNodes3];
	public PrintWriter writer;
	public int width=0;
	public int height=0;
	
	public static void main(String[] args) throws IOException {
		//initially colorizer and set w1, b1 w2, b2 to 1
		colorizer cc= new colorizer();
		cc.initial();
		int number = 10000;
		
		cc.writer = new PrintWriter("loss.txt", "UTF-8");
		
		//train the image n times
		cc.train_image("train/t01.jpg", number);
		
		//test
		cc.colorTest ("train/t01.jpg");
		cc.writer.close();
	}
	
	//color the grey image, save the result and display it
	public void colorTest (String title) throws IOException {
		//read in grey image
		BufferedImage img = null;
 	    File f = null;

 	    try{ 
	    		f = new File(title); 
	    		img = ImageIO.read(f); 
	    } catch(IOException e) { 
	    		System.out.println(e); 
	    } 
	    
 	    //set w, h
        this.width = img.getWidth(); 
        this.height = img.getHeight();
        
        //get grey matrix from grey img
        double[][] grey = imageConverter.getGrey(title);
        
        //color the image
        double[][][]	result = this.color(grey);
        
        //save the result to file and also display it as well
        System.out.println("Save and display result...");
        imageConverter.saveAndDisplayResult(result, img);
	}
	
	public void train_image(String title, int n) throws IOException {
		//read in col image
		BufferedImage colImg = null; 
 	    File f = null; 
 	    try{ 
 	    		f = new File(title); 
 	    		colImg = ImageIO.read(f); 
 	    } catch(IOException e) { 
 	    		System.out.println(e); 
 	    } 
 	    
 	    //set w,h
        this.width = colImg.getWidth(); 
        this.height = colImg.getHeight();
        
        //get col matrix
		double[][][] rgb = imageConverter.getRGB(colImg);
		
		//produce grey image and get grey matrix
		title = title.substring(0, title.length() - 4)+"Grey.jpg";
 	    imageConverter.writeImage(imageConverter.toGrey(colImg),title);
        double[][] grey = imageConverter.getGrey(title);
        
        //train the image n times
        double[][][] result;
        for(int i=0;i<n;i++) {
        		System.out.println((i+1)+" / "+ n);
        		this.train(grey, rgb);
        		result = this.color(grey);
        		//displayNN();
        		this.calLoss(result, rgb);
        }

	}
	
	//[clear]
	public void train(double[][] grey, double[][][] rgb) {
		//double[][][] result;
		for(int i=0;i<width;i++) {
			for (int j=0;j<height;j++) {
				//System.out.println("padding:"+ (i*this.height+j)+"/"+(this.width*this.height));
				this.padding(i, j, grey);
				this.forward_1();
				this.forward_2(grey[i][j]);
				this.t[0]=rgb[i][j][0]/128-1;
				this.t[1]=rgb[i][j][1]/128-1;
				this.t[2]=rgb[i][j][2]/128-1;
				this.backpopagate_2();
				this.backpopagate_1();
				
				//this.displayNN();
				//result = this.color(grey);
		        //this.calLoss(result, rgb);
			}
		}
	}
	
	//initially set w1, b1 w2, b2 to 1 [clear]
	public void initial() {
		for (int i=0;i<this.numberOfNodes1;i++) {
			for (int j=0;j<this.numberOfNodes2;j++) {
				this.w1[i][j]=1;
			}
		}
		for (int j=0;j<this.numberOfNodes2;j++) {
			this.b1[j]=1;
		}
		for (int j=0;j<this.numberOfNodes2;j++) {
			for (int k=0;k<this.numberOfNodes3;k++) {
				this.w2[j][k]=1;
			}
		}
		for (int k=0;k<this.numberOfNodes3;k++) {
			this.b2[k]=1;
		}
	}
	
	//[clear]
	public double calLoss(double[][][] result, double[][][] rgb) {
		double loss=0;
		for(int i=0;i<this.width;i++) {
			for (int j=0;j<this.height;j++) {
				for (int k=0;k<this.numberOfNodes3;k++) {
					loss+=0.5*Math.pow(result[i][j][k]/128-rgb[i][j][k]/128,2);
				}
			}
		}
		
		//System.out.println(loss);
		this.writer.println(loss);
		return loss;
	}
	
	//[clear]
	public double[][][] color(double[][] grey) {
		double[][][] output = new double[this.width][this.height][this.numberOfNodes3];
		for(int i=0;i<this.width;i++) {
			for (int j=0;j<this.height;j++) {
				this.padding(i, j, grey);
				this.forward_1();
				this.forward_2(grey[i][j]);
				
				output[i][j][0]=(y2[0]+1)*128;
				output[i][j][1]=(y2[1]+1)*128;
				output[i][j][2]=(y2[2]+1)*128;
				/*double[] hold = this.normalize(i,j, grey);
				output[i][j][0]=hold[0];
				output[i][j][1]=hold[1];
				output[i][j][2]=hold[2];*/
				//System.out.println(y2[0]+","+y2[1]+","+y2[2]);
			}
		}
		return output;
	}
	
	//[clear]
	public void forward_1 () {
		for (int j=0;j<this.numberOfNodes2;j++) {
			this.y1[j]=0;
			for (int i=0;i<this.numberOfNodes1;i++) {
				this.y1[j]+= this.w1[i][j]*this.input[i];
			}
			this.y1[j]+=this.b1[j];
			this.y1[j]=Math.tanh(this.y1[j]);
		}
	}
	
	//[clear]
	public void forward_2 (double grey) {
		for (int k=0;k<this.numberOfNodes3;k++) {
			this.y2[k]=0;
			for (int j=0;j<this.numberOfNodes2;j++) {
				this.y2[k]+= this.w2[j][k]*this.y1[j];
			}
			this.y2[k]+=this.b2[k];
			this.y2[k]=Math.tanh(this.y2[k]);
		}
	}
	
	//update w2 and b2 [clear]
	public void backpopagate_2() {
		for (int j=0;j<this.numberOfNodes2;j++){
			for (int k=0;k<this.numberOfNodes3;k++){
				this.w2[j][k]-=this.lr*(this.y2[k]-this.t[k])*tanh_prime(this.y2[k])*this.y1[j];
				this.b2[k]-=this.lr*(this.y2[k]-this.t[k])*tanh_prime(this.y2[k]);
			}
		}
	}
	
	//update w1 and b1 [clear]
	public void backpopagate_1() {
		double delta=0;
		for (int i=0;i<this.numberOfNodes1;i++) {
			for (int j=0;j<this.numberOfNodes2;j++) {
				delta=0;
				for (int k=0;k<this.numberOfNodes3;k++) {
					delta+=(this.y2[k]-this.t[k])*tanh_prime(this.y2[k])*this.w2[j][k];
				}
				this.w1[i][j]-=this.lr*delta*tanh_prime(this.y1[j])*this.input[i];
				this.b1[j]-=this.lr*delta*tanh_prime(this.y1[j]);
			}
		}
	}
	
	//[not used]
	public double[] normalize(int i,int j, double[][] grey) {
		double[] output = new double[this.numberOfNodes3];
	
		double ro = this.y2[0]+1;
		double go = this.y2[1]+1;
		double bo = this.y2[2]+1;
		double scale = grey[i][j]/(0.21*ro +0.72*go+0.07*bo);
		output[0]=0.21*scale*ro;
		output[1]=0.72*scale*go;
		output[2]=0.07*scale*bo;
		
		if(output[0]>255) {
			double difference = (output[0] - 255)/0.21;
			output[0]=output[0]-0.21*difference;
			output[1]=output[1]-0.72*difference;
			output[2]=output[2]-0.07*difference;
		}
		if(output[1]>255){
			double difference = (output[1] - 255)/0.72;
			output[0]=output[0]-0.21*difference;
			output[1]=output[1]-0.72*difference;
			output[2]=output[2]-0.07*difference;
		}
		
		if(output[2]>255){
			double difference = (output[2] - 255)/0.07;
			output[0]=output[0]-0.21*difference;
			output[1]=output[1]-0.72*difference;
			output[2]=output[2]-0.07*difference;
		}
		return output;
	}
	
	//[clear]
	public double tanh_prime(double x) {
		//return (1-Math.pow((Math.exp(x)-Math.exp(-x)),2)/Math.pow((Math.exp(x)+Math.exp(-x)),2));
		return (1-Math.pow(Math.tanh(x),2));
	}
	
	//pad to 0 if no surrounding exists  [9 clear]
	public void padding(int i, int j, double[][] grey) {
		if(this.numberOfNodes1==9)
			this.padding_9(i, j, grey);
		else if(this.numberOfNodes1==25)
			this.padding_25(i, j, grey);
		else {
			System.out.println("Error. Number of nodes 1 must be either 9 or 25.");
		}
	}
	
	//[clear]
	public void padding_9(int i, int j, double[][] grey) {
		//System.out.println("inside padding9("+i+","+j+")");
		if(i>0&&j>0) 
			this.input[0]=grey[i-1][j-1];
		else
			this.input[0]=0;
		
		if(j>0) 
			this.input[1]=grey[i][j-1];
		else
			this.input[1]=0;
		
		if(i>0) 
			this.input[2]=grey[i-1][j];
		else
			this.input[2]=0;
		
		this.input[3]=grey[i][j];
		
		if(i<this.width-1) 
			this.input[4]=grey[i+1][j];
		else
			this.input[4]=0;
		
		if(j<this.height-1) 
			this.input[5]=grey[i][j+1];
		else
			this.input[5]=0;
		
		if(i>0&&j<this.height-1) 
			this.input[6]=grey[i-1][j+1];
		else
			this.input[6]=0;
		
		if(i<this.width-1&&j>0) 
			this.input[7]=grey[i+1][j-1];
		else
			this.input[7]=0;
		
		if(i<this.width-1&&j<this.height-1) 
			this.input[8]=grey[i+1][j+1];
		else
			this.input[8]=0;
	}
	
	//[not used, no need to debug]
	public void padding_25(int i, int j, double[][] grey) {
		//System.out.println("("+i+","+j+")");
		if(i>1&&j>1)
			this.input[0]=grey[i-2][j-2];
		else
			this.input[0]=0;
		if(i>1&&j>0)
			this.input[1]=grey[i-2][j-1];
		else
			this.input[1]=0;
		if(i>1)
			this.input[2]=grey[i-2][j];
		else
			this.input[2]=0;
		if(i>1&&j<this.height-1)
			this.input[3]=grey[i-2][j+1];
		else
			this.input[3]=0;
		if(i>1&&j<this.height-2)
			this.input[4]=grey[i-2][j+2];
		else
			this.input[4]=0;
		if(i>0&&j>1)
			this.input[5]=grey[i-1][j-2];
		else
			this.input[5]=0;
		if(i>0&&j>0)
			this.input[6]=grey[i-1][j-1];
		else
			this.input[6]=0;
		if(i>0)
			this.input[7]=grey[i-1][j];
		else
			this.input[7]=0;
		if(i>0&&j<this.height-1)
			this.input[8]=grey[i-1][j+1];
		else
			this.input[8]=0;
		if(i>0&&j<this.height-2)
			this.input[9]=grey[i-1][j+2];
		else
			this.input[9]=0;
		if(j>1)
			this.input[10]=grey[i][j-2];
		else
			this.input[10]=0;
		if(j>0)
			this.input[11]=grey[i][j-1];
		else
			this.input[11]=0;
		this.input[12]=grey[i][j];
		if(j<this.height-1)
			this.input[13]=grey[i][j+1];
		else
			this.input[13]=0;
		if(j<this.height-2)
			this.input[14]=grey[i][j+2];
		else
			this.input[14]=0;
		if(i<this.width-1&&j>1)
			this.input[15]=grey[i+1][j-2];
		else
			this.input[15]=0;
		if(i<this.width-1&&j>0)
			this.input[16]=grey[i+1][j-1];
		else
			this.input[16]=0;
		if(i<this.width-1)
			this.input[17]=grey[i+1][j];
		else
			this.input[17]=0;
		if(i<this.width-1&&j<this.height-1)
			this.input[18]=grey[i+1][j+1];
		else
			this.input[18]=0;
		if(i<this.width-1&&j<this.height-2)
			this.input[19]=grey[i+1][j+2];
		else
			this.input[19]=0;
		if(i<this.width-2&&j>1)
			this.input[20]=grey[i+2][j-2];
		else
			this.input[20]=0;
		if(i<this.width-2&&j>0)
			this.input[21]=grey[i+2][j-1];
		else
			this.input[21]=0;
		if(i<this.width-2)
			this.input[22]=grey[i+2][j];
		else
			this.input[22]=0;
		if(i<this.width-2&&j<this.height-1)
			this.input[23]=grey[i+2][j+1];
		else
			this.input[23]=0;
		if(i<this.width-2&&j<this.height-2)
			this.input[24]=grey[i+2][j+2];
		else
			this.input[24]=0;	
	}
	
	//[no need to debug]
	public void displayNN() {
		System.out.println("*** w1:***");
		for (int i=0;i<this.numberOfNodes1;i++) {
			for (int j=0;j<this.numberOfNodes2;j++) {
				System.out.print(this.w1[i][j]+"  ");
			}
			System.out.println();
		}
		System.out.println("b1:");
		for (int i=0;i<this.numberOfNodes2;i++) {
			System.out.print(this.b1[i]+"  ");
		}
		System.out.println();
		System.out.println("w2:");
		for (int j=0;j<this.numberOfNodes2;j++) {
			for (int k=0;k<this.numberOfNodes3;k++) {
				System.out.print(this.w2[j][k]+"  ");
			}
			System.out.println();
		}
		System.out.println("b2:");
		for (int j=0;j<this.numberOfNodes3;j++) {
			System.out.print(this.b2[j]+"  ");
		}
		System.out.println();
		System.out.println("input:");
		for (int i=0;i<this.numberOfNodes1;i++) {
			System.out.print(this.input[i]+"  ");
		}
		System.out.println();
		System.out.println("y1:");
		for (int j=0;j<this.numberOfNodes2;j++) {
			System.out.print(this.y1[j]+"  ");
		}
		System.out.println();
		System.out.println("y2:");
		for (int k=0;k<this.numberOfNodes3;k++) {
			System.out.print(this.y2[k]+"  ");
		}
		System.out.println();
		System.out.println("t:");
		for (int k=0;k<this.numberOfNodes3;k++) {
			System.out.print(this.t[k]+"  ");
		}
		System.out.println("******");
	}
}
