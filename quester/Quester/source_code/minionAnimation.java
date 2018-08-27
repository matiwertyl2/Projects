import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.util.Random;
import java.util.Arrays;


public class minionAnimation extends Animation {
  public static String s= "images/minion/";
  public static String[] down = {s+"minion01.png", s+"minion02.png", s+"minion03.png"};
  public static String[] left = {s+"minion04.png", s+"minion05.png", s+"minion06.png"};
  public static String[] right = {s+"minion07.png", s+"minion08.png", s+"minion09.png"};
  public static String[] up = {s+"minion10.png", s+"minion11.png", s+"minion12.png"};
  public minionAnimation() {
    super(right, left, down, up, down, 4);
  }
}
