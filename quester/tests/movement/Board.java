import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.util.Arrays;


class Pair {
  int x, y;
  public Pair () {
    this.x=0;
    this.y=0;
  }
  public Pair (int x, int y) {
    this.x=x;
    this.y=y;
  }
}

public class Board extends JPanel implements ActionListener, KeyListener {
  Timer t= new Timer(10, this);
  Player player;
  Vector <Block> blocks;
  Vector <Bullet> bullets;
  int scl=50;
  Pair[] keys= new Pair[10];
  boolean[] active_keys= new boolean[10];


  public Board () {
    keys[0]= new Pair(0, 1);
    keys[1]=new Pair(0, -1);
    keys[2]= new Pair(-1, 0);
    keys[3]=new Pair(1, 0);
    t.start();
    addKeyListener(this);
    setFocusable(true);
    setFocusTraversalKeysEnabled(false);
    this.player= new Player(650, 450, scl);
    this.blocks= new Vector<Block>();
    this.bullets= new Vector<Bullet>();
    for (int i=0; i<100; i++) {
      this.blocks.addElement(new Block(1000, 1000, scl));
    }
    for (int i=0; i<10; i++) active_keys[i]=false;
  }

  public void addBullet() {
    int dx=0, dy=0;
    for (int i=0; i<4; i++) {
      if (active_keys[i]==true) {
        dx+=keys[i].x;
        dy+=keys[i].y;
      }
    }
    bullets.addElement(new Bullet(player.x, player.y, 5*dx, 5*dy, scl));
  }

  public void moove () {
      int dx=0, dy=0;
      for (int i=0; i<4; i++) {
        if (active_keys[i]==true) {
          dx+=keys[i].x;
          dy+=keys[i].y;
        }
      }
      for (Block b : blocks) b.move(dx, dy);
      for (Bullet b : bullets) b.move(dx, dy);
      for (int i=bullets.size()-1; i>=0; i--) {
        Bullet b= bullets.get(i);
        if (b.life==0) bullets.removeElementAt(i);
      }
  }


  public void pressed(int pos) {
    this.active_keys[pos]=true;
  }

  public void released(int pos) {
    this.active_keys[pos]=false;
  }

  public void paint(Graphics g) {
    super.paintComponent(g);
    int h= this.getHeight();
    int w= this.getWidth();
    player.draw(g);
    this.draw(g);
  }

  public void draw(Graphics g) {
    for (Block b : blocks) b.draw(g);
    for (Bullet b: bullets) b.draw(g);
  }

  public void remove_shot() {
    for (Bullet bullet : bullets) {
      for (int i=blocks.size()-1; i>=0; i--) {
        Block block= blocks.get(i);
        if (block.shot(bullet)) blocks.removeElementAt(i);
      }
    }
  }

  public void actionPerformed (ActionEvent e) {
      this.moove();
      this.remove_shot();
      repaint();
  }

  public void keyPressed (KeyEvent e) {
    int code =e.getKeyCode();
    System.out.println(code);
    if (code==KeyEvent.VK_SPACE) {
      System.out.println("Spavja");
      this.addBullet();

    }
    if (code==KeyEvent.VK_UP) {
      this.pressed(0);
    }
    if (code==KeyEvent.VK_DOWN) {
      this.pressed(1);
    }
    if (code==KeyEvent.VK_RIGHT) {
      this.pressed(2);
    }
    if (code==KeyEvent.VK_LEFT) {
      this.pressed(3);
    }

  }

  public void keyTyped(KeyEvent e) { }
  public void keyReleased(KeyEvent e) {
  }

}
