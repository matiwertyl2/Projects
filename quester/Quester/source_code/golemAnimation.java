import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.util.Random;
import java.util.Arrays;


public class golemAnimation extends Animation {
  public static String s="images/golem/";
  public static String[] down = {s+"golem01.png", s+"golem02.png", s+"golem03.png", s+"golem01.png", s+"golem02.png", s+"golem03.png"};
  public static String[] left = {s+"golem04.png", s+"golem05.png", s+"golem06.png", s+"golem04.png", s+"golem05.png", s+"golem06.png"};
  public static String[] right = {s+"golem07.png", s+"golem08.png", s+"golem09.png", s+"golem07.png", s+"golem08.png", s+"golem09.png"};
  public static String[] up = {s+"golem10.png", s+"golem11.png", s+"golem12.png", s+"golem10.png", s+"golem11.png", s+"golem12.png" };
  public golemAnimation() {
    super(right, left, down, up, down, 4);
  }
}
