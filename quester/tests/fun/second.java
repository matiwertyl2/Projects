import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class second extends JPanel implements ActionListener, KeyListener {
  Timer t= new Timer(250, this);
  double x=0, y=0, velx=0, vely=0;
  private Image face = new ImageIcon("obama.png").getImage();

  public second() {
    t.start();
    addKeyListener(this);
    setFocusable(true);
    setFocusTraversalKeysEnabled(false);
  }
  public void paint(Graphics g) {
    super.paintComponent(g);
    int h= this.getHeight();
    int w= this.getWidth();
    int scale=40;
    for (int i=0; i+scale <=h; i+=scale) {
      for (int j=0; j+scale<=w; j+=scale) {
        g.drawRect(j, i, scale, scale);
      }
    }
    g.drawImage(face, (int)x, (int)y, null);
  }

  public void actionPerformed(ActionEvent e) {
    x+=velx*40;
    y+=vely*40;
    repaint();
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
  public void keyPressed (KeyEvent e) {
    int code =e.getKeyCode();
    if (code==KeyEvent.VK_UP) {
      up();
    }
    if (code==KeyEvent.VK_DOWN) {
      down();
    }
    if (code==KeyEvent.VK_RIGHT) {
      right();
    }
    if (code==KeyEvent.VK_LEFT) {
      left();
    }
  }

  public void keyTyped(KeyEvent e){}
  public void keyReleased(KeyEvent e) {}
}
