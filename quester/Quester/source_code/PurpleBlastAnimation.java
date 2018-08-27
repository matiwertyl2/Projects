import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.util.Random;
import java.util.Arrays;



public class PurpleBlastAnimation extends Animation {
  public static String s="images/purpleblast/";
  public static String[] up = { s+"a3.png", s+"a4.png", s+"a5.png", s+"a6.png", s+"a7.png", s+"a8.png",
  s+"a9.png", s+"a10.png", s+"a11.png", s+"a12.png", s+"a13.png", s+"a14.png"};

  public PurpleBlastAnimation() {
    super(up, up, up, up, up, 1);
  }
}
