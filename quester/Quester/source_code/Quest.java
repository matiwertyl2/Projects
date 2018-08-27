import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.Vector;


public class Quest extends BoardObject {
  public Item[] toDoList;
  public int[] listCount;
  int n;

  public Quest (Player player) {
    super (300, 300, "images/quest.png");

    Random rand = new Random();
    this.n=rand.nextInt(Const.questItems.length-5)+5;
    this.listCount= new int[n];
    this.toDoList= new Item[n];
    createTasks();
  }

  public void createTasks() {
    Random rand= new Random();
    boolean[] used= new boolean[Const.questItems.length];
    int cnt=n;
    while (cnt>0) {
      int pos=rand.nextInt(Const.questItems.length);
      if (used[pos]==false) {
        used[pos]=true;
        toDoList[n-cnt]=Const.questItems[pos].copy();
        listCount[n-cnt]= rand.nextInt(Const.questItems[pos].rarity)+1;
        cnt--;

      }
    }
  }

  public void draw (Graphics g, Board board) {
    super.draw(g);
  }

  public void displayInterface(Graphics g, Board board) {
    int posx=board.getWidth()-100;
    int posy=0;
    BufferedImage buffimg  = new BufferedImage (100, 50*n+10, BufferedImage.TYPE_INT_RGB);
    Image img = new ImageIcon("images/quest_background.jpg").getImage();
    g.drawImage(img, posx-20, posy, 120, 50*n+30, null);
    g.drawImage(buffimg, posx-10, posy+10, 100, 50*n+10, null);
    for (int i=0; i<n; i++) {
      g.drawImage(toDoList[i].image, posx, posy+i*50+20, 30, 30, null );
      g.setColor(Color.white);
      g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
      g.drawString(Integer.toString(listCount[i]), posx+40, posy+i*50+30 +10);
    }

  }

  public boolean isCompleted() {
    if (n==0) return true;
    return false;
  }

  public boolean satisfiableItem (Item item) {
    for (int i=0; i<n; i++) {
      if (item.typeID==toDoList[i].typeID && listCount[i]>0) return true;
    }
    return false;
  }

  public void giveItem( Item item) {
    for (int i=0; i<n; i++) {
      if (item.typeID==toDoList[i].typeID) {
        listCount[i]--;
        break;
      }
    }
    update();
  }

  public void pushToFront(int p) {
    for (int i=p+1; i<n; i++) {
      listCount[i-1]=listCount[i];
      toDoList[i-1]=toDoList[i];
    }
  }

  public void update() {
    boolean ok=true;
    while (ok) {
      ok=false;
      for (int i=0; i<n; i++) {
        if (listCount[i]<=0) {
          pushToFront(i);
          n--;
          ok=true;
        }
      }
    }
  }

}
