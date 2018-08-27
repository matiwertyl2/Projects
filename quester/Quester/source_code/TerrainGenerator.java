import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.util.Random;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Collections;

class pair {
  public int first;
  public int second;
  public pair(int a, int b) {
    this.first=a;
    this.second=b;
  }
}

class four {
  public int first;
  public int second;
  public int third;
  public int fourth;
  public four (int a, int b, int c, int d) {
    this.first=a;
    this.second=b;
    this.third=c;
    this.fourth=d;
  }
}

public class  TerrainGenerator {
  public int[][] map;
  public int[][] spojne;
  public boolean[][] used;
  public int[][] fixspojne;
  public int n;

  public TerrainGenerator(int n) {
    this.n=n;
    map= new int[n+2][n+2];
    spojne= new int[n+2][n+2];
    used = new boolean[n+2][n+2];
    fixspojne= new int[n+2][n+2];
  }

  void print () {
    for (int i=1; i<=n; i++) {
      String S="";
      for (int j=1; j<=n; j++) {
        if (map[i][j]==0) S+= "#";
        else S+= " ";
      }
      //System.out.println(S);
    }
  }

  public void initialize() {
    Random rand= new Random();
    for (int i=1; i<=n; i++) {
      for (int j=1; j<=n; j++ ) {
          int r= rand.nextInt(7);
          if (r<3) map[i][j]=1;
          else map[i][j]=0;
      }
    }
  }

  public int area_count(int x, int y, int t) {
    int res=0;
    for (int i=x-1; i<=x+1; i++) {
      for (int j=y-1; j<=y+1; j++) {
        if (map[i][j]==t) res++;
      }
    }
    return res;
  }

  public void iteration() {
    for (int i=1; i<=n; i++) {
      for (int j=1; j<=n; j++) {
        if (area_count(i, j, 1)>=5) map[i][j]=1;
        else map[i][j]=0;
      }
    }
  }

  public void overlap_space(TerrainGenerator t, int x, int y) {
    for (int i=1; i<=t.n; i++) {
      for (int j=1; j<=t.n; j++) {
        map[i+x-1][j+y-1]= map[i+x-1][j+y-1] | t.map[i][j];
      }
    }
  }

 public void nowa_spojna(int nr, int X, int Y) {
    LinkedList<pair> Q = new LinkedList<pair>();
    int[] dx={1, 0, -1, 0};
    int[] dy={0, 1, 0, -1};
    Q.addLast(new pair (X, Y));
    while (!Q.isEmpty()) {
      pair  P= Q.element();
      int x=P.first;
      int y=P.second;
      Q.remove();
      spojne[x][y]=nr;
      for (int i=0; i<4; i++) {
        if (spojne[x+dx[i]][y+dy[i]]==0 && map[x+dx[i]][y+dy[i]]>0) {
          Q.addLast(new pair(x+dx[i], y+dy[i]));
          spojne[x+dx[i]][y+dy[i]]=nr;
        }
      }
    }
  }

  public void wyznacz_fixspojne() {
    clear();
    wyznacz_spojne();
    for (int i=1; i<=n; i++) {
      for (int j=1; j<=n; j++) fixspojne[i][j]=spojne[i][j];
    }
  }

  public void wyznacz_spojne() {
    int nr=1;
    for (int i=1; i<=n; i++) {
      for (int j=1; j<=n; j++) {
        if (map[i][j]>0 && spojne[i][j]==0) {
          nowa_spojna(nr, i, j);
          nr++;
        }

      }
    }
  }

  public void transpose() {
    for (int i=1; i<=n; i++) {
      for (int j=i+1; j<=n; j++) {
        int tmp=map[i][j];
        map[i][j]=map[j][i];
        map[j][i]=tmp;
      }
    }
  }

  public void connectPoints (int x1, int y1, int x2, int y2) {
    while (x1!=x2 || y1!=y2) {
      int dx= Math.abs(x1-x2);
      int dy=Math.abs(y1-y2);
      map[x1][y1]=1;
      if (dx>dy) {
        map[x1][y1+1]=1;
        map[x1][y1-1]=1;
        if (x1<x2)  x1++;
        else x1--;
      }
      else {
        map[x1+1][y1]=1;
        map[x1-1][y1]=1;
        if (y1<y2) y1++;
        else y1--;
      }
    }
  }

  public boolean goodIntersection(int x1, int y1, int x2, int y2) {
    int d=0;
    if (map[x1][y1]==0 || map[x2][y2]==0) return false;
    while (x1!=x2 || y1!=y2) {
      int dx= Math.abs(x1-x2);
      int dy=Math.abs(y1-y2);
      d++;
      if (dx>dy) {
        if (x1<x2)  x1++;
        else x1--;
      }
      else {
        if (y1<y2) y1++;
        else y1--;
      }
    }
    if (d<20) return true;
    return false;
  }

  public void randomConnections() {
    int count=15;
    Random rand = new Random();
    while (count !=0) {
      int x1, y1, x2, y2;
      x1=rand.nextInt(n)+1;
      x2=rand.nextInt(n)+1;
      y1=rand.nextInt(n)+1;
      y2=rand.nextInt(n)+1;
      if (fixspojne[x1][y1]!=fixspojne[x2][y2] && goodIntersection(x1, y1, x2, y2) ) {
        count --;
        connectPoints(x1, y1, x2, y2);
      }
    }
  }


  public void clear() {
    for (int i=1; i<=n; i++) {
      for (int j=1; j<=n; j++) {
        spojne[i][j]=0;
        used[i][j]=false;
      }
    }
    for (int i=0; i<=n+1; i++) {
      used[i][0]=true;
      used[i][n+1]=true;
      used[0][i]=true;
      used[n+1][i]=true;
    }

  }

  public boolean bfs() {
    boolean ok=false;
    LinkedList<four> Q = new LinkedList<four>();
    for (int i=1; i<=n; i++) {
      for (int j=1; j<=n; j++) {
        if (spojne[i][j]==1) {
          Q.addLast(new four (i, j, i, j));
          used[i][j]=true;
        }
      }
    }
    int[] dx= {0, 1, -1, 0};
    int[] dy= {1, 0, 0, -1};
    while (!Q.isEmpty()) {
      four F= Q.element();
      int x=F.first;
      int y= F.second;
      int skadx=F.third;
      int skady= F.fourth;
      Q.remove();
      if (spojne[x][y]>1) {
        connectPoints(x, y, skadx, skady);
        ok=true;
        break;
      }
      for (int i=0; i<4;i++) {
        if (used[x+dx[i]][y+dy[i]]==false) {
          Q.addLast(new four (x+dx[i], y+dy[i], skadx, skady));
          used[x+dx[i]][y+dy[i]]=true;
        }
      }
    }
    return ok;
  }

  public void addPlayerArea() {
    int y0=n/2+ 300/Const.scale-1;
    int x0=n/2+400/Const.scale-1;
    for (int i=y0; i<=y0+4; i++) {
      for (int j=x0; j<=x0+4; j++) map[i][j]=1;
    }
  }

  void connectSpojne() {
    clear();
    wyznacz_spojne();
    while (bfs()==true) {
      clear();
      wyznacz_spojne();
    }
  }

  public void addBorders() {
    for (int i=1; i<=n; i++) {
      map[i][1]=0;
      map[i][n]=0;
      map[1][i]=0;
      map[n][i]=0;
    }
  }

  public Terrain generate() {
    TerrainGenerator naklad= new TerrainGenerator(10);
    Random rand= new Random();
    naklad.initialize();
    int t=35;
    while (t>0) {
        for (int i=1; i+9<=n; i+=10) {
          for (int j=1; j+9<=n; j+=10) {
            naklad.initialize();
            naklad.iteration();
            if (rand.nextInt (4)<3) naklad.transpose();
            overlap_space(naklad, i, j);
          }
        }
        iteration();
        t--;
    }
    addPlayerArea();
    wyznacz_fixspojne();
    connectSpojne();
    randomConnections();
    addBorders();
    return new Terrain(map, n);
  }

}
