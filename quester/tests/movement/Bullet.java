import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.util.Arrays;


public class Bullet {
  int x, y, dx, dy, scl;
  int size;
  int life;

  public Bullet (int x, int y, int dx, int dy, int scl) {
    this.x=x;
    this.y=y;
    this.dx=dx;
    this.dy=dy;
    this.scl=scl;
    this.life=100;
    size=scl/5;
  }
  public void move (int dx, int dy) {
    this.life--;
    this.x+=dx-this.dx;
    this.y+=dy-this.dy;
  }
  public void draw (Graphics g) {
    g.setColor(Color.black);
    g.fillOval(x, y, scl/5, scl/5);
  }
}
