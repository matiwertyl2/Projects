import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;


public class FireBall extends Weapon {
  public FireBall() {
    super(1, 40, 3, new commonAttackAnimation(), false, "images/superblast/a3.png");
  }
}
