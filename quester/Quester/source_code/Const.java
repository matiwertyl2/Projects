import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.util.Random;
import java.util.Arrays;


public class Const {
  public static int scale=80;
  public static int mapSize=100;
  public static int gameSize=mapSize*scale/2;

  public static Item questItems[] = { new Coin(), new Rock(), new Ruby(), new Emerald(), new Bluestone(), new DragonEgg()};
  public static Item boardItems[] = {new Heart(), new Ruby(), new Emerald(), new Bluestone()};

}
