import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.util.Random;
import java.util.Arrays;
import java.awt.image.BufferedImage;


public class WeaponInterface {
  public BufferedImage image;

  public WeaponInterface () {
    this.image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
  }

  public BufferedImage getWeaponIcon(Weapon weapon) {
    BufferedImage weaponIcon= new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
    Graphics2D g= weaponIcon.createGraphics();
    Color c= new Color(255, 0, 0, 100);
    g.setColor(c);
    g.drawImage(weapon.icon, 0, 0, 100, 100, null);
    g.fillRect(0, 0, 100, weapon.timeToAttack*100/weapon.frequency);
    return weaponIcon;
  }

  public void drawFrames(Graphics2D g, int n) {
    Image img= new ImageIcon("images/frame.jpg").getImage();
    g.setColor(Color.blue);
    g.drawImage(img, 0, 0, n*100, 10, null);
    g.drawImage(img, 0, 90, n*100, 10, null);
    for (int i=0; i<n; i++) {
      g.drawImage(img, i*100, 0, 10, 100, null);
      g.drawImage(img, i*100+90, 0, 10, 100, null);
    }
  }

  public void updateImage(Weapon[] weapons) {
    image= new BufferedImage ((weapons.length-1)*100, 100, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = image.createGraphics();
    for (int i=1; i<weapons.length; i++) {
      Weapon w= weapons[i];
      drawFrames(g, weapons.length-1);
      g.drawImage(getWeaponIcon(w), 100*(i-1)+10, 10, 80, 80, null);

    }
    g.dispose();
  }

  public void draw(Graphics g, Board board) {
    int l= 100*(board.player.weapons.length-1)+500;
    g.drawImage(image, board.getWidth()/2-l/2, board.getHeight()-100, l-500, 100, null);

  }
}
