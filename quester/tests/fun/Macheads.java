import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;


public class Macheads {
  public static void main (String args[]) {
    second s= new second();
    
    JFrame f= new JFrame();
    f.add(s);
    f.setVisible(true);
    f.setSize(600, 400);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

  }
}
