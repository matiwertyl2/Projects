import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.util.Random;



public class Minion extends Mob {
  public Minion (int x, int y, Player player) {
    super(x, y, player,  2, 10, new SlowBall(), new minionAnimation(), new Coin(), 60, 102);
    this.boardX=-player.x+this.x+player.boardX;
    this.boardY=-player.y+this.y+player.boardY;
  }

  public Minion (Minion minion) {
    super(minion);
  }

  public Minion copy() {
    return new Minion(this);
  }
}
