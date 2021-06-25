import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Iframe extends JFrame implements KeyListener{
	@Override
	public void keyPressed(KeyEvent e) {
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

	}
}