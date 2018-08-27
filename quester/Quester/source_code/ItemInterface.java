import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.util.Random;
import java.util.Arrays;
import java.awt.image.BufferedImage;



public class ItemInterface {
  public BufferedImage image;

  public ItemInterface () {
    this.image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
  }

  public void updateImage(Item[] items, int [] itemsCount, int active) {
    image= new BufferedImage (5*100, 100, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = image.createGraphics();
    for (int i=0; i<5; i++) {
      if (items[i]!=null) {
        Item item= items[i];
        g.drawImage(item.getIcon(), 100*(i)+25, 25, 50, 50, null);
        g.setColor(Color.white);
        g.drawString(Integer.toString(itemsCount[i]), 100*i+70, 85);
      }
    }
    drawFrames(g, 5, active);
    g.dispose();

  }



  public void drawFrames(Graphics2D g, int n, int active) {
    Image img = new ImageIcon("images/frame.jpg").getImage();
    for (int i=0; i<n; i++) {
      g.drawImage(img, i*100, 0, 10, 100, null);
      g.drawImage(img, i*100+90, 0, 10, 100, null);
    }
    g.drawImage(img, 0, 0, n*100, 10, null);
    g.drawImage(img, 0, 90, n*100, 10, null);
    for (int i=0; i<n; i++) {
      if (i==active) markActiveItem(i, g);
    }
  }

  public void markActiveItem (int pos, Graphics g) {
    Image img= new ImageIcon ("images/activeframe.jpg").getImage();
    g.drawImage(img, pos*100, 0, 10, 100, null);
    g.drawImage(img, pos*100+90, 0, 10, 100, null);
    g.drawImage(img, pos*100, 0, 100, 10, null);
    g.drawImage(img, pos*100, 90, 100, 10, null);
  }

  public void draw(Graphics g, Board board) {
    int l= 100*5+300;
    g.drawImage(image, board.getWidth()/2-l/2+300, board.getHeight()-100, l-300, 100, null);
  }
}
