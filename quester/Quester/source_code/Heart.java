import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.util.Random;
import java.util.Arrays;



public class Heart extends Item {
  public int healthGain=10;

  public Heart () {
    super("images/apple.png", 3, 20);
  }

  public Heart(int x, int y, Player player) {
    super(x, y, player, "images/apple.png", 3, 20);
  }

  public Heart(Heart heart) {
    super(heart);
  }

  public Heart copy () {
    Heart heart= new Heart (this);
    return heart;
  }

  public boolean use (Board board ) {
    Player p=board.player;
    if (p.currentHealth==p.healthPoints) return false;
    p.currentHealth+=healthGain;
    if (p.currentHealth>p.healthPoints) p.currentHealth=p.healthPoints;
    return true;
  }
}
