import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;


public class Main {
  public static void main (String args[]) {
    Board board= new Board();

    JFrame f= new JFrame();
    f.add(board);
    f.setVisible(true);
    f.setSize(600, 400);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

  }
}
