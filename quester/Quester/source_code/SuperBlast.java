import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;


public class SuperBlast extends Weapon {
  public SuperBlast() {
    super(10, 100, 100, new SuperBlastAnimation(), true, "images/superblast/a3.png");
  }
}
