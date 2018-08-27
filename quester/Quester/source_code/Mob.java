import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.util.Random;


public class Mob extends Creature {
  public int moveDuration;
  public Item item;
  public int rarity;
  public int typeID;

  public Mob (int x, int y, Player player, int velocity, int hitPoints, Weapon weapon, Animation animation, Item item, int rarity, int id) {
    super(x, y, "RIGHT", velocity, hitPoints, weapon, animation);
    this.boardX=-player.x+this.x+player.boardX;
    this.boardY=-player.y+this.y+player.boardY;
    this.item=item;
    this.rarity=rarity;
    this.typeID=id;
  }

  public Mob (Mob mob) {
    super(mob);
    this.item= new Item(mob.item);
    this.rarity=mob.rarity;
    this.typeID=mob.typeID;
  }

  public Mob copy() {
    return new Mob(this);
  }


  public void randomMove (Player player, Board board) {
    if (attackPosibility(player, board)) {
      stay();
      moveDuration=2;
    }
    else {
    if (moveDuration>0) moveDuration--;
    else {
        Random rand= new Random();
        int dist=distance(player);
        int mod= 50;
        if (dist!=0) mod= 7000000/dist+60;
        else if (dist>250000) mod= 50;
        int m= rand.nextInt(mod);
        if (dist <100000) m=50;
        if (m<10) left();
        else if (m<20) right();
        else if (m<30) down();
        else if (m<40) up();
        else if (m<50) stay();
        else getCloseToPlayer(player, board);
        moveDuration= rand.nextInt(10)+1;

    }
    }
    move();
    boardMove();
  }

  public void getCloseToPlayer(Player player, Board board) {
    Random rand= new Random();
    int dist = distance(player);
    int distnew = dist;
    int dx=0, dy=0;
    int loop=0;
    while (distnew>=dist && distnew>0 && loop<10) {
      loop++;
       dx = rand.nextInt(3);
      dx--;
       dy= rand.nextInt(3);
      dy--;
      if (dx!=0 && dy!=0) {
        int x= rand.nextInt(2);
        if (x==0) dx=0;
        else dy=0;
      }
      changeDirection(dx, dy);
      move();
      distnew= distance(player);
      if (terrainCollision(board.terrain.map) || mobCollision(board.mobs) || playerCollision(board.player)) distnew=1234567891;

      changeDirection(-dx, -dy);
      move();
    }
    changeDirection(dx, dy);
  }

  public void attack(Creature enemy, Board board) {
    if ( attackPosibility(enemy, board) && new Random().nextInt(5)==0) {
      super.attack(enemy, board);
    }
  }

  public int count (Vector <Mob> mobs) {
    int res=0;
    for (int i=0; i<mobs.size(); i++) {
      Mob other= mobs.get(i);
      if (this.typeID==other.typeID) res++;
    }
    return res;
  }


}
