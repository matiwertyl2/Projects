import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;


public class PurpleBlast extends Weapon {
  public PurpleBlast() {
    super(8, 200, 100, new PurpleBlastAnimation(), true, "images/purpleblast/a10.png");
  }
}
