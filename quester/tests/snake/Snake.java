import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

class Pair {
  public int x, y;

  public Pair (int x, int y) {
    this.x=x;
    this.y=y;
  }
  public Pair (Pair p) {
    this.x=p.x;
    this.y=p.y;
  }
}

public class Snake {
  int x, y, scl, velx, vely;
  int grow=0;
  Vector <Pair> blocks;
  public Snake (int scl) {
    this.scl=scl;
    this.x=0;
    this.y=0;
    blocks= new Vector<Pair>();
    this.blocks.addElement(new Pair(scl, 0));
    this.blocks.addElement(new Pair(0, 0));

    this.velx=1;
    this.vely=0;
  }

  public void draw (Graphics g) {
    g.setColor(Color.blue);
    for (Pair block : blocks) {
      g.fillRect(block.x, block.y, scl, scl);
    }
  }
  public boolean selfCollision () {
    Pair head=blocks.firstElement();
    for (int i=1; i<blocks.size(); i++) {
      Pair block= blocks.get(i);
      if (head.x==block.x && head.y==block.y) return true;
    }
    return false;
  }
  public boolean foodCollision (Food food) {
    for (int i=0; i<blocks.size(); i++) {
      Pair block= blocks.get(i);
      if (food.x==block.x && food.y==block.y) return true;
    }
    return false;
  }
  public void move (int w, int h) {
    Pair lastblock= new Pair (blocks.lastElement());
    for (int i=blocks.size()-1; i>0; i--){
      Pair blockprev = blocks.get(i-1);
      Pair block = blocks.get(i);
      block.x=blockprev.x;
      block.y=blockprev.y;
    }
    if (grow>0) {
      blocks.addElement(lastblock);
      grow--;
    }
    Pair head= blocks.firstElement();
    head.x+=velx*scl;
    head.y+=vely*scl;
    if (head.x+scl>w) head.x=0;
    if (head.x<0) head.x= w/scl*scl-scl;
    if (head.y+scl>h) head.y=0;
    if (head.y<0) head.y=h/scl*scl-scl;
  }
  public void up() {
    velx=0;
    vely=-1;
  }
  public void left() {
    velx=-1;
    vely=0;
  }
  public void down () {
    velx=0;
    vely=1;
  }
  public void right() {
    velx=1;
    vely=0;
  }
}
