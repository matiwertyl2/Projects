import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.util.Random;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.imageio.ImageIO;
import java.lang.*;


public class Minimap {
  public BufferedImage image;

  public Minimap() {
    this.image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
  }
  public void updateImage (int[][] Map, Player player, Quest quest) {
    Color brown = new Color(139, 69, 19);
    BufferedImage map= new BufferedImage (Const.mapSize, Const.mapSize, BufferedImage.TYPE_INT_RGB);
    image =new BufferedImage (Const.mapSize+10, Const.mapSize+10, BufferedImage.TYPE_INT_RGB);
    for (int x=0; x<Const.mapSize; x++) {
      for (int y=0; y<Const.mapSize; y++) {
        Color c= brown;
        if (Map[y+1][x+1]==0) {
          c=Color.green.darker();
          if ((x+1)*(y+1)*131 % 41 >=20) c= Color.green.darker().darker();


        }
        map.setRGB(x, y, c.getRGB());
      }
    }
    int px, py;
    px= (player.x+Const.gameSize)/Const.scale;
    py= (player.y+Const.gameSize)/Const.scale;
    map.setRGB(px, py, Color.black.getRGB());
    map.setRGB(Math.min(px+1, Const.gameSize-1), py, Color.black.getRGB());
    map.setRGB(Math.max(0,px-1), py, Color.black.getRGB());
    map.setRGB(px, Math.min(py+1, Const.gameSize-1), Color.black.getRGB());
    map.setRGB(px, Math.max(0,py-1), Color.black.getRGB());

    px= (quest.x+Const.gameSize)/Const.scale;
    py= (quest.y+Const.gameSize)/Const.scale;
    for (int i=px-1; i<=px+1; i++) {
      for (int j=py-1; j<=py+1; j++) {
        if (i>=0 && i<Const.gameSize && j>=0 && j<Const.gameSize && (Math.abs(px-i)==0 || Math.abs(py-j)==0)) map.setRGB(i, j, Color.red.getRGB());
      }
    }



    Graphics2D g2d = image.createGraphics();
    g2d.drawImage(new ImageIcon("images/minimap.jpg").getImage(), 0, 0, Const.mapSize+10, Const.mapSize+10, null);
    g2d.drawImage(map, 5, 5, null);
    g2d.dispose();
  }

  public void draw(Graphics g) {
    g.drawImage(image, 0, 0, 200, 200, null);
  }
}
