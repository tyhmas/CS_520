package com.tutorialspoint.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;

public class AwtControlDemo {

   private Frame mainFrame;
   private Label headerLabel;
   private Label statusLabel;
   private Panel controlPanel;

   public AwtControlDemo(){
      prepareGUI();
   }

   private void prepareGUI(){
      mainFrame = new Frame("Java AWT Examples");
      mainFrame.setSize(400,400);
      mainFrame.setLayout(new GridLayout(3, 1));
      mainFrame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent windowEvent){
            System.exit(0);
         }        
      });    
      headerLabel = new Label();
      headerLabel.setAlignment(Label.CENTER);
      statusLabel = new Label();        
      statusLabel.setAlignment(Label.CENTER);
      statusLabel.setSize(350,100);

      controlPanel = new Panel();
      controlPanel.setLayout(new FlowLayout());

      mainFrame.add(headerLabel);
      mainFrame.add(controlPanel);
      mainFrame.add(statusLabel);
      mainFrame.setVisible(true);  
   }

   private void showCanvasDemo(){
      headerLabel.setText("Control in action: Canvas"); 

      controlPanel.add(new MyCanvas());
      mainFrame.setVisible(true);  
   } 

   public class KL extends KeyAdapter{
         public void keyPressed(KeyEvent e) {
            System.out.println("Press");
            GC gc = new GC(canvas);
            Rectangle rect = canvas.getClientArea();
            gc.fillRectangle(rect.x, rect.y, rect.width, rect.height);
        
            Font font = new Font(display, "Arial", 32, SWT.BOLD );
            gc.setFont(font);
        
            gc.drawString("" + e.character, 15, 10);
        
            gc.dispose();
            font.dispose();
         }
         public void keyReleased(KeyEvent e) {
            
         }
         public void keyTyped(KeyEvent e){
            
         }
   }

   class MyCanvas extends Canvas{

      public MyCanvas () {
         setBackground (Color.GRAY);
         setSize(300, 300);
      }

      public void paint (Graphics g) {
         Graphics2D g2;
         g2 = (Graphics2D) g;
         g2.drawString ("It is a custom canvas area", 70, 70);
      }

      canvas.addKeyListener(new KL());
    


      /*public void keyPressed(KeyEvent e) {
         System.out.println("Press");
         switch (e.getKeyCode()){
            case KeyEvent.VK_DOWN :
               if(e.isControlDown()){
                  System.out.println("Down");
                  break;   
               }
            }

      }

         @Override
         public void keyTyped(KeyEvent e){

         }

         @Override
         public void keyReleased(KeyEvent e){

         }*/
   }


   public static void main(String[] args){
      AwtControlDemo  awtControlDemo = new AwtControlDemo();
      awtControlDemo.showCanvasDemo();
   }
}