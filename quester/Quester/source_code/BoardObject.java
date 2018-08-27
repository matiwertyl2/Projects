import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.Vector;


public class BoardObject implements Comparable<BoardObject> {
  public int boardX;
  public int boardY;
  public int x;
  public int y;
  public Image image;

  public BoardObject (int x, int y, String path) {
    this.x=x;
    this.y=y;
    this.boardX=x;
    this.boardY=y;
    this.image=new ImageIcon(path).getImage();

  }

  public BoardObject (BoardObject b) {
    this.x=b.x;
    this.y=b.y;
    this.boardX=b.boardX;
    this.boardY=b.boardY;
    this.image= b.image;
  }

  public BoardObject(int x, int y, Image image) {
    this.x=x;
    this.y=y;
    this.image=image;
  }

  public BoardObject (int x, int y, int bx, int by, Image img) {
    this.x=x;
    this.y=y;
    this.boardX=bx;
    this.boardY=by;
    this.image=img;
  }

  public BoardObject(int x, int y, int w, int h) {
    this.x=x;
    this.y=y;
    this.image=new BufferedImage (w, h, BufferedImage.TYPE_INT_RGB);
  }

  public int compareTo(BoardObject other) {
        int res= y - other.y;
        if (res != 0) return res;
        if (this instanceof Player) return 1;
        return x - other.x;
   }

  public void draw (Graphics g) {
    g.drawImage(this.image, this.boardX, this.boardY, null);
  }

  public void updateBoardCoordinates(int dx, int dy) {
    this.boardX+=dx;
    this.boardY+=dy;
  }

  public void boardMove(int dx, int dy) {
    boardX+=dx;
    boardY+=dy;
  }

  public void ensureNotColided (Board board ) { // while spawning
    int a, b;
    Random rand= new Random();
    while (true) {
      a= rand.nextInt(Const.gameSize*2-4*Const.scale)-Const.gameSize+2*Const.scale;
      b=rand.nextInt(Const.gameSize*2-4*Const.scale)-Const.gameSize+2*Const.scale;
      changePosition(a, b, board.player);
      if (terrainCollision(board.terrain.map)==false && mobCollision(board.mobs)==false && playerCollision(board.player)==false) {
        break;
      }
    }
  }

  public void changePosition(int x, int y, Player player) {
    this.x=x;
    this.y=y;
    this.boardX=-player.x+this.x+player.boardX;
    this.boardY=-player.y+this.y+player.boardY;
  }


  public int distance (BoardObject object) {
    int a= (this.x+this.image.getWidth(null)/2 -object.x-object.image.getWidth(null)/2);
    int b= (this.y+ this.image.getHeight(null)/2-object.y-object.image.getHeight(null)/2);
    return a*a+b*b;
  }

  public boolean touchBoardObject(BoardObject chest) {
    if (pointInsidePolygon(x, y, chest)) return true;
    if (pointInsidePolygon(x+image.getWidth(null), y, chest)) return true;
    if (pointInsidePolygon(x, y+image.getHeight(null), chest)) return true;
    if (pointInsidePolygon(x+image.getWidth(null), y+image.getHeight(null), chest)) return true;
    if (crossedAreas(chest)) {
      return true;
    }
    return false;
  }

  public boolean touchBoardObjectDX(BoardObject chest, int dx) { // to let overlap them
    if (pointInsidePolygon(x+dx, y+dx, chest)) return true;
    if (pointInsidePolygon(x+image.getWidth(null)-dx, y+dx, chest)) return true;
    if (pointInsidePolygon(x+dx, y+image.getHeight(null)-dx, chest)) return true;
    if (pointInsidePolygon(x+image.getWidth(null)-dx, y+image.getHeight(null)-dx, chest)) return true;
    if (crossedAreas(chest)) {
      return true;
    }
    return false;
  }

  public boolean crossedAreas(BoardObject o) { // needed for touching, special case for big objects
    int x0=x, y0=y, xEnd=x+image.getWidth(null), yEnd=y+image.getHeight(null);
    int ox0=o.x, oy0=o.y, oxEnd=o.x+o.image.getWidth(null), oyEnd=o.y+o.image.getHeight(null);
    if (x0<=ox0 && xEnd>=oxEnd && y0>=oy0 && yEnd<=oyEnd) return true;
    if (y0<=oy0 && yEnd>=oyEnd && x0>=ox0 && xEnd<=oxEnd) return true;
    return false;
  }

  public boolean pointInsidePolygon(int x, int y, BoardObject chest) { // for touchBoardObject
    if (x>=chest.x && x<=chest.x+chest.image.getWidth(null) && y>=chest.y && y<=chest.y+chest.image.getHeight(null)) return true;
    return false;
  }

  public boolean terrainCollision (int[][] Map) {
    int X= (x+Const.gameSize)/Const.scale+1;
    int Y= (y+Const.gameSize)/Const.scale+1;
    int endX= X+image.getWidth(null)/Const.scale+1;
    int endY= Y+ image.getHeight(null)/Const.scale+1;
    int dx= image.getWidth(null)/2;
    for (int i=Y; i<=endY; i++) {
      for (int j=X;j<=endX; j++) {
        if (i>=0 && i<=Const.mapSize && j>=0 && j<=Const.mapSize) {
          BoardObject chest= new BoardObject(-Const.gameSize+(j-1)*Const.scale, -Const.gameSize+(i-1)*Const.scale, Const.scale, Const.scale);
          if ( Map[i][j]==0 && (touchBoardObjectDX(chest, dx) || chest.touchBoardObjectDX(this, dx))  ) return true;
        }
      }
    }
    return false;
  }


  public boolean mobCollision (Vector<Mob> mobs) {
    for (int i=0; i<mobs.size(); i++) {
      Mob mob= mobs.get(i);
      int dx= mob.image.getWidth(null)/3;
      if ((touchBoardObjectDX(mob, dx) || mob.touchBoardObjectDX(this, dx)) && mob!=this)  return true;
    }
    return false;
  }

  public boolean playerCollision (Player player) {
    int dx= image.getWidth(null)/2;
    if (this instanceof Player) return false;
    if (touchBoardObjectDX(player, dx) || player.touchBoardObjectDX(this, dx)) return true;
    return false;
  }



}
