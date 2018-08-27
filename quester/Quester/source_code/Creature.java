import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.Vector;


public class Creature extends BoardObject {
  public String direction;
  public int velocity;
  public int velx;
  public int vely;

  public int healthPoints;
  public int currentHealth;

  public Weapon weapon;

  public Animation animation;

  public Creature (int x, int y, String direction, int velocity, int health, Weapon weapon, Animation animation) {
    super(x, y, animation.animationRight[0]);
    this.velocity=velocity;
    this.velx=0;
    this.vely=0;
    this.direction=direction;
    this.healthPoints=health;
    this.currentHealth=health;
    this.weapon=weapon;
    this.animation=animation;
  }

  public Creature(Creature c) {
    super(c);
    this.velocity=c.velocity;
    this.velx=c.velx;
    this.vely=c.vely;
    this.healthPoints=c.healthPoints;
    this.currentHealth=c.currentHealth;
    this.weapon= new Weapon (c.weapon);
    this.animation = new Animation(c.animation);
  }

  public void draw(Graphics g) {
    this.weapon.draw(g, this);
    super.draw(g);
    drawHealth(g);
    this.image=animation.nextImage(this, true);
  }



  public boolean attackPosibility (Creature enemy, Board board) {
    int attackX=x, attackY=y;
    int attackRangeX=image.getWidth(null);
    int attackRangeY=image.getHeight(null);
    if (direction=="RIGHT") {
      attackRangeX=weapon.range;
      attackX+=image.getWidth(null);
    }
    if (direction=="LEFT"){
      attackX-=weapon.range;
      attackRangeX=weapon.range;
    }
    if (direction=="DOWN") {
      attackRangeY=weapon.range;
      attackY+=image.getHeight(null);
    }
    if (direction=="UP") {
      attackY-= weapon.range;
      attackRangeY=weapon.range;
    }
    BoardObject attackArea= new BoardObject(attackX, attackY, attackRangeX, attackRangeY);
    if (enemy.touchBoardObject(attackArea) || attackArea.touchBoardObject(enemy)) {
      return true;
    }
    return false;
  }
  public void attack(Creature enemy, Board board) {
    int attackX=x, attackY=y;
    int attackRangeX=image.getWidth(null);
    int attackRangeY=image.getHeight(null);
    if (direction=="RIGHT") {
      attackRangeX+=weapon.range;
    }
    if (direction=="LEFT"){
      attackX-=weapon.range;
      attackRangeX+=weapon.range;
    }
    if (direction=="DOWN") {
      attackRangeY+=weapon.range;
    }
    if (direction=="UP") {
      attackY-= weapon.range;
      attackRangeY+=weapon.range;
    }
      if (weapon.timeToAttack<=0) {
      //  if (this instanceof Player) g.fillRect(attackX-x+board.getWidth()/2, attackY-y+board.getHeight()/2, attackRangeX, attackRangeY);
        if (attackPosibility(enemy, board)) {
          enemy.currentHealth-=weapon.strength;
        }
        weapon.use();
        weapon.timeToAttack=weapon.frequency;
      }
  }

  public void move() {
    this.x+=velx;
    this.y+=vely;
  }
  public void boardMove() {
    this.boardX+=velx;
    this.boardY+=vely;
  }


  public void changeDirection(String dir) {
    this.direction=dir;
  }

  public void up() {
    velx=0;
    vely=-velocity;
    direction="UP";
  }
  public void down() {
    velx=0;
    vely=velocity;
    direction="DOWN";
  }
  public void left() {
    velx=-velocity;
    vely=0;
    direction="LEFT";
  }
  public void right() {
    velx=velocity;
    vely=0;
    direction="RIGHT";
  }
  public void stay() {
    velx=0;
    vely=0;
  }
  public void changeDirection(int dx, int dy) {
    velx=dx*velocity;
    vely=dy*velocity;
    if(dx==1 && dy==0) direction="RIGHT";
    else if (dx==-1 && dy==0) direction="LEFT";
    else if (dx==0 && dy==1) direction="DOWN";
    else if (dx==0 && dy==-1) direction="UP";
  }

  public void stoppedByObject (int[][] Map, Vector<Mob> mobs, Player player) {
    if (terrainCollision(Map) || mobCollision(mobs) || playerCollision(player)) {
      velx*=-1;
      vely*=-1;
      move();
      if (!(this instanceof Player)) boardMove();
      stay();
    }
  }

  public void drawHealth(Graphics g) {
    int lgreen=currentHealth*image.getWidth(null)/healthPoints;
    if (lgreen<0) lgreen=0;
    g.setColor(Color.green);
    g.fillRect(boardX, boardY-5, lgreen, 5);
    g.setColor(Color.red);
    g.fillRect(boardX+lgreen, boardY-5, image.getWidth(null)-lgreen, 5);
  }


}
