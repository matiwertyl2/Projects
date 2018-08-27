import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.util.Random;
import java.util.Arrays;


public class Terrain {
  public int[][] map;
  public int n;

  public Terrain (int[][] map, int n) {
    this.map=map;
    this.n=n;
  }

  public Vector<BoardObject> prepareTrees ( Player player, Board board) {
    Vector<BoardObject> trees= new Vector<BoardObject>();
    Random rand= new Random();
    int dx=player.boardX-player.x;
    int dy=player.boardY-player.y;
    int x0=(player.x-board.getWidth()/2+Const.gameSize)/Const.scale;
    int y0=(player.y-board.getHeight()/2+Const.gameSize)/Const.scale;
    int endX=(player.x+board.getWidth()/2+Const.gameSize)/Const.scale+1;
    int endY=(player.y+board.getHeight()/2+Const.gameSize)/Const.scale+1;
    if (x0<=0) x0=1;
    if (y0<=0) y0=1;
    if (endX>n) endX=n;
    if (endY>n) endY=n;
    for (int i=y0; i<=endY; i++) {
      for (int j=x0; j<=endX; j++) {
        int x=-Const.gameSize+(j-1)*Const.scale+ dx;
        int y= -Const.gameSize+(i-1)*Const.scale+dy;
        Image image;
        if (map[i][j]==0) {
          if ((i*j)*131 % 41 <20)  image=new ImageIcon("images/tree.png").getImage();
          else image= new ImageIcon("images/tree2.png").getImage();
          trees.addElement(new BoardObject(x-dx, y-dy, x, y, image));
          int treedx= (i*j)*131 % Const.scale;
          int treedy= (i*j)*191 % Const.scale;
          if (i+1<=n && j+1<=n) {
            if (map[i+1][j]!=0) treedy=0;
            if (map[i][j+1]!=0) treedx=0;
            if (map[i+1][j+1]!=0) {
              treedx=0;
              treedy=0;
              if (map[i+1][j]==0) treedy= (i*j)*191 % Const.scale;
              else if (map[i][j+1]==0) treedx= (i*j)*131 % Const.scale;
            }
            trees.addElement(new BoardObject(x-dx+treedx, y-dy+treedy, x+treedx, y+treedy, image));
          }
        }
      }
    }
    return trees;
  }

  public void drawBackground (Graphics g, Player player, Board board) {
    int dx=player.boardX-player.x;
    int dy=player.boardY-player.y;
    int x0=(player.x-board.getWidth()/2+Const.gameSize)/Const.scale;
    int y0=(player.y-board.getHeight()/2+Const.gameSize)/Const.scale;
    int endX=(player.x+board.getWidth()/2+Const.gameSize)/Const.scale+1;
    int endY=(player.y+board.getHeight()/2+Const.gameSize)/Const.scale+1;
    if (x0<=0) x0=1;
    if (y0<=0) y0=1;
    if (endX>n) endX=n;
    if (endY>n) endY=n;
    Image image1= new ImageIcon("images/back.png").getImage();
    Image image2= new ImageIcon("images/back2.png").getImage();
    Image image3= new ImageIcon("images/back3.png").getImage();

    for (int i=y0; i<=endY; i++) {
      for (int j=x0; j<=endX; j++) {
        int x=-Const.gameSize+(j-1)*Const.scale+ dx;
        int y= -Const.gameSize+(i-1)*Const.scale+dy;
          drawModule (image1, image2, image3, i, j, x, y, g);
      }
    }
  }

  public void drawModule (Image a, Image b, Image c, int I, int J, int x, int y, Graphics g) {
    int cnt=2;
    for (int i=0; i<cnt; i++) {
      for (int j=0; j<cnt; j++) {
        int m= I*J*131*(i+1)*(j+1);
        if (m % 41<13) g.drawImage(a, x+i*Const.scale/cnt, y+j*Const.scale/cnt, Const.scale/cnt, Const.scale/cnt, null);
        else if (m % 41 <30) g.drawImage(b, x+i*Const.scale/cnt, y+j*Const.scale/cnt, Const.scale/cnt, Const.scale/cnt, null);
        else  g.drawImage(c, x+i*Const.scale/cnt, y+j*Const.scale/cnt, Const.scale/cnt, Const.scale/cnt, null);
      }
    }

  }

}
