import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.util.Random;
import java.util.Arrays;



public class playerAnimation extends Animation {
  public static String s="images/player/";
  public static String[] down={s+"player01.png", s+"player02.png", s+"player03.png"};
  public static String[] left={s+"player04.png", s+"player05.png", s+"player06.png"};
  public static String[] right={s+"player07.png", s+"player08.png", s+"player09.png"};
  public static String[] up={s+"player10.png", s+"player11.png", s+"player12.png"};

  public playerAnimation() {
    super(right, left, down, up, down, 4);
  }
}
