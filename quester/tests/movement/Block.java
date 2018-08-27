import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Collections;
import java.util.Random;



public class Block {
  int x, y, scl;
  Color c;
  public Block (int X, int Y, int scl) {
    this.scl=scl;
    Random rand= new Random();
    this.y= rand.nextInt(Y);
    this.x=rand.nextInt(X);
    int r, g, b;
    r= rand.nextInt(255);
    b=rand.nextInt(255);
    g= rand.nextInt(255);
    this.c= new Color(r, g, b);
  }
  public void move (int dx, int dy) {
    this.x+=dx;
    this.y+=dy;
  }
  public void draw (Graphics g) {
    g.setColor(c);
    g.fillOval(x, y, scl, scl);
  }

  int distance (int x1, int y1, int x2, int y2) {
    return (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2);
  }
  public boolean shot(Bullet bullet) {
    int rx= x+scl/2;
    int ry=y+scl/2;
    int bx= bullet.x+bullet.size/2;
    int by= bullet.y+bullet.size/2;
    if (distance(rx, ry, bx, by)<= scl*scl/4) return true;
    return false;
  }
}
