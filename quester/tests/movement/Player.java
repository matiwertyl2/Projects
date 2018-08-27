import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Random;


public class Player {
  int x, y, scl;

  public Player (int w, int h, int scl) {
    x= w/2;
    y= h/2;
    this.scl=scl;
  }

  public void draw (Graphics g) {
    g.setColor(Color.red);
    g.fillOval(this.x, this.y, scl, scl);

  }


}
