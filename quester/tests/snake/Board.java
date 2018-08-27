import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Random;

class Food {
  public int x;
  public int y;
  public Food (int scl, int h, int w) {
    Random rand= new Random();
    this.y= rand.nextInt(h/scl)*scl;
    this.x=rand.nextInt(w/scl)*scl;
  }
  public void draw (Graphics g, int scl) {
    Color c= new Color(255, 100, 100);
    g.setColor(c);
    g.fillRect(x, y, scl, scl);
  }
}

public class Board extends JPanel implements ActionListener, KeyListener {
  Timer t= new Timer(150, this);
  Food food;
  Snake snake;
  int scl=50;
  boolean over=false;

  public Board () {
    t.start();

    addKeyListener(this);
    setFocusable(true);
    setFocusTraversalKeysEnabled(false);
    this.snake= new Snake(scl);
    food= new Food (scl, 400, 600);

  }

  public boolean food_eaten() {
    if (snake.blocks.firstElement().x==food.x && snake.blocks.firstElement().y==food.y) return true;
    return false;
  }

  public void paint(Graphics g) {
    super.paintComponent(g);
    int h= this.getHeight();
    int w= this.getWidth();
    for (int i=0; i+scl <=h; i+=scl) {
      for (int j=0; j+scl<=w; j+=scl) {
        g.drawRect(j, i, scl, scl);
      }
    }
    food.draw(g, scl);
    snake.draw(g);
  }

  public void actionPerformed (ActionEvent e) {
    if (over==false) {
      snake.move(getWidth(), getHeight());
      if (food_eaten()) {
        while (snake.foodCollision(food)) food= new Food (scl, getHeight(), getWidth());
        snake.grow++;
      }
      if (snake.selfCollision()) {
        over=true;
      }
      repaint();
    }
  }

  public void keyPressed (KeyEvent e) {
    int code =e.getKeyCode();
    if (code==KeyEvent.VK_UP) {
      snake.up();
    }
    if (code==KeyEvent.VK_DOWN) {
      snake.down();
    }
    if (code==KeyEvent.VK_RIGHT) {
      snake.right();
    }
    if (code==KeyEvent.VK_LEFT) {
      snake.left();
    }
  }

  public void keyTyped(KeyEvent e){}
  public void keyReleased(KeyEvent e) {}

}
