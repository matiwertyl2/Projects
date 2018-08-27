import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;


public class SlowBall extends Weapon {
  public SlowBall() {
    super(1, 20, 15, new commonAttackAnimation(), false, "images/superblast/a3.png");
  }
}
