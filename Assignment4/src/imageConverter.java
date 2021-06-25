import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
public class imageConverter {

	/*public static void main(String[] args) throws IOException {
		// read image
		BufferedImage img = null; 
	    File f = null; 
	    String title = "sea01.jpg";
	    try{ 
	    		f = new File(title); 
	    		img = ImageIO.read(f); 
	    } catch(IOException e) { 
	    		System.out.println(e); 
	    } 
	    
	    //get w,h
        int width = img.getWidth(); 
        int height = img.getHeight();
        //System.out.println("w:"+width);
        //System.out.println("h:"+height);
        
        //get rgb example
        //Color c0_0 = new Color(img.getRGB(0, 0));
        //int red = c0_0.getRed();
        //System.out.println("red:"+red);
        
        writeImage(toGrey(img),title);
        displayImage(img);

	}*/
	
	public static BufferedImage toGrey(BufferedImage img) { 
		int width = img.getWidth(); 
        int height = img.getHeight();
        for(int i=0;i<width;i++) {
        		for(int j=0;j<height;j++) {
        			Color curr = new Color(img.getRGB(i, j));
        			int grey= (int)(0.21*curr.getRed()+0.72*curr.getGreen()+0.07*curr.getBlue());
        			Color greyC = new Color(grey, grey, grey);
        			img.setRGB(i, j, greyC.getRGB());
        		}
        }
		
		return img;
	}
	
	public static void saveAndDisplayResult(double[][][] rgb, BufferedImage grey) throws IOException {
		int width = grey.getWidth(); 
        int height = grey.getHeight();
        Color c;
        for(int i=0;i<width;i++) {
        		for(int j=0;j<height;j++) {
        			if(rgb[i][j][2]<0|rgb[i][j][2]>255)
        				System.out.println("blue"+(int)rgb[i][j][2]);
        			c = new Color((int)rgb[i][j][0], (int)rgb[i][j][1], (int)rgb[i][j][2]);
        			grey.setRGB(i, j, c.getRGB());
        		}
        }
        displayImage(grey);
        writeImage(grey, "result.jpg");
	}
	
	public static double[][][] getRGB(BufferedImage colImg) { 
		//get w,h
        int width = colImg.getWidth(); 
        int height = colImg.getHeight();
        double[][][] rgb = new double[width][height][3];
        //r
        for(int i=0;i<width;i++) {
        		for(int j=0;j<height;j++) {
        			Color curr = new Color(colImg.getRGB(i, j));
        			rgb[i][j][0]= curr.getRed();
        		}
        }
        //g
        for(int i=0;i<width;i++) {
    			for(int j=0;j<height;j++) {
    				Color curr = new Color(colImg.getRGB(i, j));
    				rgb[i][j][1]= curr.getGreen();
    			}
        }
        //b
        for(int i=0;i<width;i++) {
    			for(int j=0;j<height;j++) {
    				Color curr = new Color(colImg.getRGB(i, j));
    				rgb[i][j][2]= curr.getBlue();
    			}
        }
        
		return rgb;
	}
	
	public static double[][] getGrey(String title) { 
		BufferedImage img = null;
		File f = null; 
 	    try{ 
 	    		f = new File(title); 
 	    		img = ImageIO.read(f); 
 	    } catch(IOException e) { 
 	    		System.out.println(e); 
 	    } 
 	    
		int width = img.getWidth(); 
        int height = img.getHeight();
        double[][] grey = new double[width][height];
        for(int i=0;i<width;i++) {
        		for(int j=0;j<height;j++) {
        			Color curr = new Color(img.getRGB(i, j));
        			grey[i][j]= curr.getRed();
        		}
        }
		return grey;
	}
	
    public static void displayImage(BufferedImage img) throws IOException{
        ImageIcon icon=new ImageIcon(img);
        JFrame frame=new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(200,300);
        JLabel lbl=new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public static void writeImage(BufferedImage img, String title) {
    		try{
    	      File f = new File(title);
    	      ImageIO.write(img, "jpg", f);
    	    }catch(IOException e){
    	      System.out.println(e);
    	    }
    }
}