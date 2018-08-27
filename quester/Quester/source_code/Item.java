import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.util.Random;
import java.util.Arrays;
import java.awt.image.BufferedImage;



public class Item extends BoardObject {
  public boolean dropped;
  public int typeID;
  int rarity;

  public Item(String path, int id, int rarity) {
    super(0, 0, path);
    this.typeID=id;
    this.rarity=rarity;
  }

  public Item (int x, int y, Player player, String path, int id, int rarity) {
    super(x, y, path);
    this.boardX=-player.x+this.x+player.boardX;
    this.boardY=-player.y+this.y+player.boardY;
    this.typeID=id;
    this.rarity=rarity;
  }

  public Item (Item item) {
    super(item);
    this.typeID=item.typeID;
    this.rarity= item.rarity;
  }

  public void drop(Mob owner, Board board) {
    this.x=owner.x+(owner.image.getWidth(null)- this.image.getWidth(null))/2;
    this.y=owner.y+(owner.image.getHeight(null)- this.image.getHeight(null))/2;
    this.boardX=owner.boardX+(owner.image.getWidth(null)- this.image.getWidth(null))/2;
    this.boardY=owner.boardY+(owner.image.getHeight(null)- this.image.getHeight(null))/2;
    board.items.addElement(this);
  }

  public boolean use(Board board) {
    if (board.player.touchBoardObject(board.quest) || board.quest.touchBoardObject(board.player) ) {
      if (board.quest.satisfiableItem(this)){
        board.quest.giveItem(this);
        return true;
      }
    }
    return false;
  }

  public Item copy() {
    return new Item(this);
  }

  public BufferedImage getIcon() {
    BufferedImage itemIcon= new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
    Graphics2D g= itemIcon.createGraphics();
    g.drawImage(image, 0, 0, 100, 100, null);
    return itemIcon;
  }

  public int count (Vector <Item> items) {
    int res=0;
    for (int i=0; i<items.size(); i++) {
      Item other= items.get(i);
      if (this.typeID==other.typeID) res++;
    }
    return res;
  }
}
