import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.util.Random;
import java.util.Arrays;


public class DragonAnimation extends Animation {
  public static String s="images/dragon/";
  public static String[] down = {s+"a01.png", s+"a02.png", s+"a03.png", s+"a04.png"};
  public static String[] left = {s+"a05.png", s+"a06.png", s+"a07.png", s+"a08.png"};
  public static String[] right = {s+"a09.png", s+"a10.png", s+"a11.png", s+"a12.png"};
  public static String[] up = {s+"a13.png", s+"a14.png", s+"a15.png", s+"a16.png"};
  public DragonAnimation() {
    super(right, left, down, up, down, 4);
  }
}
