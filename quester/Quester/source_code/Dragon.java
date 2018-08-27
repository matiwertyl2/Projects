import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.Random;


public class Dragon extends Mob {
  public Dragon (int x, int y, Player player) {
    super(x, y, player,  4, 100, new DragonWeapon(), new DragonAnimation(), new DragonEgg(), 3, 103);
    this.boardX=-player.x+this.x+player.boardX;
    this.boardY=-player.y+this.y+player.boardY;
  }

  public Dragon (Dragon dragon) {
    super(dragon);
  }

  public Dragon copy() {
    return new Dragon(this);
  }


  public void attack (Creature enemy, Board board) {
    if (new Random().nextInt(10)==0) super.attack(enemy, board);
  }
}
