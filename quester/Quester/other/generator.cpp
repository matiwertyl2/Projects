#include <bits/stdc++.h>
using namespace std;


struct Terrain {
  vector < vector <int> > map;
  vector < vector <int> > spojne;
  vector <vector <int> > fixspojne;
  bool used[200][200];
  int n;
  Terrain(int _n) {
    n=_n;
    map.resize(n+2);
    spojne.resize(n+2);
    fixspojne.resize(n+2);
    for (int i=0; i<=n+1; i++) {
      map[i].resize(n+2);
      spojne[i].resize(n+2);
      fixspojne[i].resize(n+2);
    }
    for (int i=0; i<=n+1; i++) {
      for (int j=0; j<=n+1; j++) {
        map[i][j]=0;
        spojne[i][j]=0;
        used[i][j]=false;
        spojne[i][j]=0;
      }
    }
  }

  void print () {
    for (int i=1; i<=n; i++) {
      for (int j=1; j<=n; j++) {
        if (map[i][j]==0) cout << "#";
        else if (map[i][j]==2) cout << " ";
        else if (map[i][j]==3) cout << " ";
        else cout << " ";
      }
      cout << "\n";
    }
  }

  void initialize() {
    for (int i=1; i<=n; i++) {
      for (int j=1; j<=n; j++ ) {
          int r= rand()%7;
          if (r<3) map[i][j]=1;
          else map[i][j]=0;
      }
    }
  }

  int area_count(int x, int y, int t) {
    int res=0;
    for (int i=x-1; i<=x+1; i++) {
      for (int j=y-1; j<=y+1; j++) {
        if (map[i][j]==t) res++;
      }
    }
    return res;
  }

  void iteration() {
    for (int i=1; i<=n; i++) {
      for (int j=1; j<=n; j++) {
        if (area_count(i, j, 1)>=5) map[i][j]=1;
        else map[i][j]=0;
      }
    }
  }

  void overlap_space(Terrain t, int x, int y) {
    for (int i=1; i<=t.n; i++) {
      for (int j=1; j<=t.n; j++) {
        map[i+x-1][j+y-1]= map[i+x-1][j+y-1] or t.map[i][j];
      }
    }
  }

  void nowa_spojna(int nr, int x, int y) {
    queue<pair <int, int> > Q;
    int dx[4]={1, 0, -1, 0};
    int dy[4]={0, 1, 0, -1};
    while(!Q.empty()) Q.pop();
    Q.push(make_pair (x, y));
    while (!Q.empty()) {
      pair <int, int> P= Q.front();
      int x=P.first;
      int y=P.second;
      Q.pop();
      spojne[x][y]=nr;
      for (int i=0; i<4; i++) {
        if (spojne[x+dx[i]][y+dy[i]]==0 && map[x+dx[i]][y+dy[i]]>0) {
          Q.push(make_pair(x+dx[i], y+dy[i]));
          spojne[x+dx[i]][y+dy[i]]=nr;
        }
      }
    }
  }

  void wyznacz_fixspojne() {
    clear();
    wyznacz_spojne();
    for (int i=1; i<=n; i++) {
      for (int j=1; j<=n; j++) fixspojne[i][j]=spojne[i][j];
    }
  }

  void wyznacz_spojne() {
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

  void transpose() {
    for (int i=1; i<=n; i++) {
      for (int j=i+1; j<=n; j++) swap(map[i][j], map[j][i]);
    }
  }

  void connectPoints (int x1, int y1, int x2, int y2) {
    while (x1!=x2 || y1!=y2) {
      int dx= abs(x1-x2);
      int dy=abs(y1-y2);
      map[x1][y1]=2;
      if (dx>dy) {
        map[x1][y1+1]=2;
        map[x1][y1-1]=2;
        if (x1<x2)  x1++;
        else x1--;
      }
      else {
        map[x1+1][y1]=2;
        map[x1-1][y1]=2;
        if (y1<y2) y1++;
        else y1--;
      }
    }
  }

  bool goodIntersection(int x1, int y1, int x2, int y2) {
    int change=0;
    int d=0;
    if (map[x1][y1]==0 || map[x2][y2]==0) return false;
    string prev="space";
    while (x1!=x2 || y1!=y2) {
      int dx= abs(x1-x2);
      int dy=abs(y1-y2);
      d++;
      if (prev=="space" && map[x1][y1]==0) change++;
      else if (prev=="wall" && map[x1][y1]>0) change++;
      if (map[x1][y1]==0) prev="wall";
      else prev="space";
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

  void randomConnections() {
    int count=12;
    while (count !=0) {
      int x1, y1, x2, y2;
      x1=rand()% n+1;
      x2=rand()%n+1;
      y1=rand()%n+1;
      y2=rand()%n+1;
      if (fixspojne[x1][y1]!=fixspojne[x2][y2] && goodIntersection(x1, y1, x2, y2) ) {
        count --;
        cout << x1 << " " << y1 << " " << x2 << " " << y2 << "\n";
        connectPoints(x1, y1, x2, y2);
      }
    }
  }

  void clear() {
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

  bool bfs() {
    bool ok=false;
    queue< pair <int, pair <int, pair <int, int> > > > Q;
    for (int i=1; i<=n; i++) {
      for (int j=1; j<=n; j++) {
        if (spojne[i][j]==1) {
          Q.push(make_pair (i, make_pair (j, make_pair (i, j))));
          used[i][j]=true;
        }
      }
    }
    int dx[4]= {0, 1, -1, 0};
    int dy[4]= {1, 0, 0, -1};
    while (!Q.empty()) {
      int x= Q.front().first;
      int y= Q.front().second.first;
      int skadx=Q.front().second.second.first;
      int skady= Q.front().second.second.second;
      Q.pop();
      if (spojne[x][y]>1) {
        connectPoints(x, y, skadx, skady);
        ok=true;
        break;
      }
      for (int i=0; i<4;i++) {
        if (used[x+dx[i]][y+dy[i]]==false) {
          Q.push(make_pair(x+dx[i], make_pair (y+dy[i], make_pair (skadx, skady))));
          used[x+dx[i]][y+dy[i]]=true;
        }
      }
    }
    while (!Q.empty()) Q.pop();
    return ok;
  }

  void addPlayerArea() {
    for (int i=31; i<=35; i++) {
      for (int j=33; j<=37; j++) map[i][j]=3;
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

  void generate() {
    Terrain naklad(10);
    naklad.initialize();
    int t=35;
    while (t>0) {
        for (int i=1; i+9<=n; i+=10) {
          for (int j=1; j+9<=n; j+=10) {
            naklad.initialize();
            naklad.iteration();
            if (rand () %4<3) naklad.transpose();
            overlap_space(naklad, i, j);
          }
        }
        iteration();
        t--;
    }
    addPlayerArea();
    wyznacz_fixspojne();
    connectSpojne();
    print();
    cout << "\n\n";
    randomConnections();
  }

};

int main () {
  srand(time(0));
  Terrain teren(100);
  teren.generate();
  teren.print();
}
