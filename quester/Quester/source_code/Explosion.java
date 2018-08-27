import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;


public class Explosion extends Weapon {
  public Explosion() {
    super(20, 150, 200, new ExplosionAnimation(), true, "images/explosion/a5.png");
  }
}
