import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Arrays;
import java.util.Vector;
import java.util.Collections;
import java.util.Comparator;


public class Player extends Creature {

  public int defaultVelocity;
  public Weapon[] weapons;
  public Item[] items;
  public int[] itemsCount;
  public int activeItemNumber;

  public Player (int x, int y) {
    super(x, y, "RIGHT", 10, 100, new FireBall(), new playerAnimation());
    this.defaultVelocity=10;
    this.weapons= new Weapon[4];
    this.weapons[0]=this.weapon;
    this.weapons[1]= new PurpleBlast();
    this.weapons[2]= new SuperBlast();
    this.weapons[3] = new Explosion();
    this.items= new Item[5];
    this.itemsCount= new int[5];
    this.activeItemNumber=0;
  }

  public void draw (Graphics g) {
    super.draw(g);
    for (Weapon w : weapons) {
      if (w!=weapon) {
        w.draw(g, this);
      }
    }
  }

  public void move() {
    super.move();
  }

  public void accelerate() {
    this.velocity=2*defaultVelocity;
    this.animation.frequency=2;
  }

  public void slowDown() {
    this.velocity= defaultVelocity;
    this.animation.frequency=4;
  }

  public void speedUpdate(boolean acc) {
    if (acc==true) accelerate();
    else slowDown();
  }

  public void updateVelocity(Vector<Integer> arrows, boolean speedButton) {
    speedUpdate(speedButton);
    if (arrows.size()==0) stay();
    else if (arrows.lastElement()==1) up();
    else if (arrows.lastElement()==2) down();
    else if (arrows.lastElement()==3) right();
    else if (arrows.lastElement()==4) left();
  }

  public void updateBoardCoordinates(Board board) {
    boardX=board.getWidth()/2;
    boardY=board.getHeight()/2;
  }


  public void attack (Vector<Mob> mobs, Board board) {
    int currentTime=weapon.timeToAttack;
    boolean success=false;
    for (int i=mobs.size()-1; i>=0; i--) {
      Mob enemy= mobs.get(i);
      attack(enemy, board);
      if (currentTime<weapon.timeToAttack) success=true;
      weapon.timeToAttack=currentTime;
      if (enemy.currentHealth<=0) {
        enemy.item.drop(enemy, board);
        mobs.removeElementAt(i);
      }
      if (attackPosibility(enemy, board) && weapon.multiAttack==false) break;
    }
    if (success) weapon.timeToAttack=weapon.frequency;
  }

  public boolean touchItem(Item item) {
    if (this.touchBoardObject(item) || item.touchBoardObject(this)) return true;
    return false;
  }

  public void collectItems (Vector<Item> boardItems) {
    for (int i=boardItems.size()-1; i>=0; i--) {
      Item item= boardItems.get(i);
      if (touchItem(item) && freeSpace(item)) {
        addToEquipment(item);
        boardItems.removeElementAt(i);
      }
    }
  }

  public void removeItem() {
    if (itemsCount[activeItemNumber]!=0){
      itemsCount[activeItemNumber]--;
      if (itemsCount[activeItemNumber]==0) items[activeItemNumber]=null;
    }
  }

  public void useItem(Board board) {
    if (itemsCount[activeItemNumber]!=0) {
      if (items[activeItemNumber].use(board))   itemsCount[activeItemNumber]--;
      if (itemsCount[activeItemNumber]==0) items[activeItemNumber]=null;
    }
  }

  public void addToEquipment(Item item) {
    boolean added=false;
    for (int i=0; i<5; i++) {
      if (items[i]!=null && items[i].typeID==item.typeID) {
        itemsCount[i]++;
        added=true;
        break;
      }
    }
    if (!added) {
      for (int i=0; i<5; i++) {
        if (items[i]==null) {
          items[i]=item;
          itemsCount[i]=1;
          break;
        }
      }
    }
  }

  public boolean freeSpace (Item item) {
    for (int i=0; i<5; i++) {
      if (items[i]==null || items[i].typeID==item.typeID) return true;
    }
    return false;
  }

  public void setActiveWeapon(int x) {
    this.weapon=weapons[x];
  }

  public void nextItem() {
    this.activeItemNumber++;
    if (this.activeItemNumber==5) this.activeItemNumber=0;
  }

  public void previousItem() {
    this.activeItemNumber--;
    if (this.activeItemNumber<0) this.activeItemNumber=4;
  }



}
