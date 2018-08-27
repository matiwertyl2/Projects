import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;


public class Weapon {
  public int strength;
  public int range;
  public int frequency;
  public int timeToAttack;
  public boolean duringAnimation;
  public Animation animation;
  public Image image;
  public Image icon;
  public int loop;
  public boolean multiAttack;

  public Weapon (int strength, int range, int freq, Animation animation, boolean multiAttack, String icon) {
    this.strength=strength;
    this.range=range;
    this.frequency=freq;
    this.timeToAttack=0;
    this.duringAnimation=false;
    this.animation=animation;
    this.loop=0;
    this.multiAttack=multiAttack;
    this.image=new ImageIcon(animation.animationRight[0]).getImage();
    this.icon=new ImageIcon(icon).getImage();
  }

  public Weapon (Weapon w) {
    this.strength=w.strength;
    this.range=w.range;
    this.frequency=w.frequency;
    this.timeToAttack=0;
    this.duringAnimation=false;
    this.animation= new Animation (w.animation);
    this.loop=0;
    this.multiAttack=w.multiAttack;
    this.image= w.image;
    this.icon=w.icon;
  }

  public void draw(Graphics g, Creature owner) {
    if (duringAnimation) {
      image=animation.nextImage(owner, false);
      g.setColor(Color.black);
      if (animation.pos==0) {
        loop++;
        if (loop>animation.frequency) {
          duringAnimation=false;
          loop=0;
        }
      }
      if (duringAnimation) g.drawImage(image, getX(owner), getY(owner), getRangeX(owner), getRangeY(owner), null);

    }
  }

  public int getRangeX (Creature owner) {
    if (owner.direction=="LEFT" || owner.direction=="RIGHT") return this.range;
    return owner.image.getWidth(null);
  }

  public int getRangeY (Creature owner) {
    if (owner.direction=="UP" || owner.direction=="DOWN") return this.range;
    return owner.image.getHeight(null);
  }

  public int getX (Creature owner) {
    if (owner.direction=="UP" || owner.direction=="DOWN") return owner.boardX;
    if (owner.direction=="LEFT") return owner.boardX-range;
    return owner.boardX+owner.image.getWidth(null);
  }

  public int getY (Creature owner) {
    if (owner.direction=="LEFT" || owner.direction=="RIGHT") return owner.boardY;
    if(owner.direction=="UP") return owner.boardY-range;
    return owner.boardY+owner.image.getHeight(null);
  }

  public void updateCounter() {
    timeToAttack--;
    if(timeToAttack<0) timeToAttack=0;
  }

  public void use() {
    duringAnimation=true;
  }


}
