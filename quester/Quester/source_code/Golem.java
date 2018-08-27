import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.util.Random;



public class Golem extends Mob {
  public Golem (int x, int y, Player player) {
    super(x, y, player,  2, 40, new SuperBlast(), new golemAnimation(), new Rock(), 20, 101);
    this.boardX=-player.x+this.x+player.boardX;
    this.boardY=-player.y+this.y+player.boardY;
  }

  public Golem (Golem golem) {
    super(golem);
  }

  public Golem copy() {
    return new Golem(this);
  }


  public void attack (Creature enemy, Board board) {
    if (new Random().nextInt(10)==0) super.attack(enemy, board);
  }
}
