import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.util.Random;
import java.util.Arrays;


public class Animation {
  public String [] animationLeft;
  public String [] animationRight;
  public String [] animationUp;
  public String [] animationDown;
  public String [] animationStay;
  public String animation[];
  public int frequency;
  public int clock;
  public int pos;
  public Animation (String [] animationRight, String [] animationLeft, String [] animationDown, String [] animationUp, String [] animationStay, int f) {
    this.animationLeft=animationLeft;
    this.animationRight=animationRight;
    this.animationUp=animationUp;
    this.animationDown=animationDown;
    this.animationStay=animationStay;
    this.animation=animationRight;
    this.pos=0;
    this.frequency=f;
    this.clock=this.frequency;

  }

  public Animation (Animation a) {
    this.animationLeft=a.animationLeft;
    this.animationRight=a.animationRight;
    this.animationUp=a.animationUp;
    this.animationDown=a.animationDown;
    this.animationStay=a.animationStay;
    this.animation=a.animationRight;
    this.pos=0;
    this.frequency=a.frequency;
    this.clock=this.frequency;

  }

  public void reset() {
    this.pos=0;
    this.clock=this.frequency;
  }

  public Image nextImage (Creature C, boolean stay) {
  //  if (C.velx==0 && C.vely==0 && stay ) this.animation=this.animationStay;
     if (C.direction=="LEFT") this.animation=this.animationLeft;
    else if (C.direction=="RIGHT") this.animation=this.animationRight;
    else if (C.direction=="DOWN") this.animation=this.animationDown;
    else if (C.direction=="UP") this.animation= this.animationUp;
    if (clock<=0) {
      pos++;
      clock=frequency;
    }
    clock--;
    if (pos==animation.length) reset();
    return new ImageIcon(animation[pos]).getImage();
  }
}
