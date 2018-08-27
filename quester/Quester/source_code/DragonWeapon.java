import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;


public class DragonWeapon extends Weapon {
  public DragonWeapon() {
    super(20, 100, 20, new ExplosionAnimation(), false, "images/superblast/a3.png");
  }
}
