import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.util.Random;
import java.util.Arrays;



public class ExplosionAnimation extends Animation {
  public static String s="images/explosion/";
  public static String[] up = { s+"a3.png", s+"a4.png", s+"a5.png", s+"a6.png", s+"a7.png", s+"a8.png"};
  public static String[] right = { s+"b3.png", s+"b4.png", s+"b5.png", s+"b6.png", s+"b7.png", s+"b8.png"};
  public static String[] down = { s+"c3.png", s+"c4.png", s+"c5.png", s+"c6.png", s+"c7.png", s+"c8.png"};
  public static String[] left = {s+"d3.png", s+"d4.png", s+"d5.png", s+"d6.png", s+"d7.png", s+"d8.png"};

  public ExplosionAnimation() {
    super(right, left, down, up, up, 2);
  }
}
