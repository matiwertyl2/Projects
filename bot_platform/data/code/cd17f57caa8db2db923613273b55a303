#include<bits/stdc++.h>
#include <string>
using namespace std;

int tab[100][100];

int dx[] = {-1, 1, 0, 0};
int dy[] = {0, 0 -1, 1};
string moves[] = {"UP", "DOWN", "LEFT", "RIGHT"};

bool inBoard(int x, int y, int n) {
    if (x < 0 || x >=n || y  < 0 || y >= n) return false;
    return true;
}

bool colision(int x, int y) {
    if (tab[x][y] == -1) return false;
    return true;
} 


int main () {
    int n, nr;
    while(true) {
        vector< pair<int, int> > pos;
        int px, py;
        char s[5];
        scanf("%d %d", &n, &nr);
        for (int i=0; i<2; i++) {
            scanf("%d %d", &px, &py);
            pos.push_back(make_pair(px, py));
        }
        int x, y;
        x = pos[nr].first;
        y = pos[nr].second;
        for (int i=0; i<n; i++) {
            for (int j=0; j<n; j++) {
                scanf("%d", &tab[i][j]);
            }
        }
        scanf("%s", s);
        assert(s == string("END"));
        bool ok=false;
        for (size_t i=0; i<4; i++) {
            int newX = x + dx[i];
            int newY = y+ dy[i];
            if (inBoard(newX, newY, n) && colision(newX, newY)==false) {
                cerr << "BOT " << newX << " " << newY << "\n";
                ok =true;
                printf("%s\n", moves[i].c_str());
                break;
            }
        }
        if (ok == false) {
                    printf("%s\n", moves[0].c_str());
        }
                printf("END\n");
                fflush(stdout);
    }
}