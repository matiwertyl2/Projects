#pragma GCC optimize("Ofast")
#include <bits/stdc++.h>
#include <sys/time.h>
using namespace std;

struct point
{
    int x, y;
    point(int X = 0, int Y = 0) : x(X), y(Y) { }
};

inline bool operator==(const point &a, const point &b)
{
    return a.x == b.x && a.y == b.y;
}

inline bool operator!=(const point &a, const point &b)
{
    return a.x != b.x || a.y != b.y;
}

inline point operator+(const point &a, const point &b)
{
    return point(a.x + b.x, a.y + b.y);
}

inline point operator-(const point &a, const point &b)
{
    return point(a.x - b.x, a.y - b.y);
}

const int W = 20, H = 20, N = 4, K = 4*4*4*4;
int vis[W+2][H+2];
int tmp[W+2][H+2];
int bfs[W+2][H+2];
typedef int rank_t;
point adj[] = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 } };
string com[] = { "UP", "DOWN", "LEFT", "RIGHT" };
vector<pair<point, int>> qu(W*H*2);
int n, p;
double result[N];
double c[N];
int max_depth;

struct node
{
    long long V;
    int v[N][4];
    double q[N][4];
    node *ch[K];
    point h[N];
    
    static void apply(const vector<int> &move, vector<point> &nh, int dead)
    {
        int dead_tmp = dead;
        for(int i = 0; i < n; i++)
            if(move[i] != -1)
            {
                nh[i] = nh[i] + adj[move[i]];
                tmp[nh[i].x][nh[i].y] = i;
            }
            else if(nh[i].x != -1)
            {
                for(int x = 1; x <= W; x++)
                    for(int y = 1; y <= H; y++)
                        if(tmp[x][y] == i) tmp[x][y] = -1;
                nh[i] = point(-1, dead_tmp++);
            }
    }
    
    void visit(int depth, bool root = false)
    {
        max_depth = max(max_depth, depth);
        int dead = 0;
        for(int i = 0; i < n; i++)
            if(h[i].x == -1) dead++;
        if(n - dead <= 1)
        {
            for(int i = 0; i < n; i++)
                if(h[i].x != -1) result[i] = n - 1;
                else result[i] = h[i].y;
            return;
        }
        V++;
        vector<int> move = { -1, -1, -1, -1 };
        double k = sqrt(log2(V));
        for(int i = 0; i < n; i++)
            if(h[i].x != -1)
            {
                double best = -1;
                for(int j = 0; j < 4; j++)
                {
                    point p = h[i] + adj[j];
                    if(tmp[p.x][p.y] == -1)
                    {
                        double ucb = v[i][j] == 0 ? rand() : q[i][j] / v[i][j] + c[i] * k / sqrt(v[i][j]);
                        if(ucb > best)
                        {
                            best = ucb;
                            move[i] = j;
                        }
                    }
                }
            }
        int nr = 0;
        for(int i = 0; i < n; i++)
            if(move[i] != -1)
            {
                nr *= 4;
                nr += move[i];
            }
        vector<point> nh(h, h + n);
        apply(move, nh, dead);
        if(!ch[nr])
        {
            ch[nr] = new node(nh);
            ch[nr]->heuristics();
        }
        else ch[nr]->visit(depth + 1);
        for(int i = 0; i < n; i++)
            if(move[i] != -1)
            {
                q[i][move[i]] += result[i];
                v[i][move[i]]++;
            }
    }
    
    void heuristics()
    {
        for(int i = 0; i <= W + 1; i++)
            for(int j = 0; j <= H + 1; j++)
                bfs[i][j] = tmp[i][j] == -1 ? -1 : -2;
        int k1 = 0, k2 = 0;
        for(int i = 0; i < n; i++)
            result[i] = 0;
        for(int i = 0; i < n; i++)
            if(h[i].x != -1)
                qu[k2++] = make_pair(h[i], i);
        int alive = k2;
        while(k1 != k2)
        {
            point p = qu[k1].first;
            int k = qu[k1].second;
            k1++;
            for(int i = 0; i < 4; i++)
            {
                point t = p + adj[i];
                if(bfs[t.x][t.y] == -1)
                {
                    bfs[t.x][t.y] = k;
                    result[k]++;
                    qu[k2++] = make_pair(t, k);
                }
            }
        }
        double sum = 0;
        for(auto x: result) sum += x;
        sum = max(sum, 1.0) / (alive * (alive - 1) / 2);
        for(int i = 0; i < n; i++)
            if(h[i].x == -1) result[i] = h[i].y;
            else result[i] = result[i] / sum + (n - alive);
    }
    
    node() = default;
    node(const vector<point> &H)
    {
        V = 0;
        for(int i = 0; i < n; i++)
            h[i] = H[i];
        for(int i = 0; i < K; i++)
            ch[i] = 0;
        for(int i = 0; i < N; i++)
            for(int j = 0; j < 4; j++)
                q[i][j] = v[i][j] = 0;
    }
    
    ~node()
    {
        for(int i = 0; i < K; i++)
            if(ch[i]) delete ch[i];
    }
};

bool alive[N] = { true, true, true, true };

long long get_time()
{
    struct timeval tp;
    gettimeofday(&tp, NULL);
    return tp.tv_sec * 1000LL + tp.tv_usec / 1000LL; //get current timestamp in millisecond
}

int main()
{
    for(int i = 0; i <= W+1; i++)
        for(int j = 0; j <= H+1; j++) {
            vis[i][j] = -2;
        }
    for(int i = 1; i <= W; i++)
        for(int j = 1; j <= H; j++)
            vis[i][j] = -1;
    node *root = 0;
    while(true)
    {
        string ign;
        n = 2;
        cin >> ign >> p;
        for(int i = 0; i < n; i++)
            c[i] = i == p ? 0.1 : 0.1;
        vector<point> h(n);
        for(int i = 0; i < n; i++)
        {
            int x0, y0, x1, y1;
            cin >> y1 >> x1;
            if(x1 == -1 && alive[i])
            {
                for(int x = 1; x <= W; x++)
                    for(int y = 1; y <= H; y++)
                        if(vis[x][y] == i) vis[x][y] = -1;
                alive[i] = false;
            }
            else if(x1 != -1)
            {
                x0++; y0++; x1++; y1++;
                vis[x1][y1] = i;
            }
            h[i] = point(x1, y1);
        }
        for(int i = 0; i < W; i++)
            for(int j = 0; j < H; j++)
                cin >> ign;
        cin >> ign;
        long long start = get_time();
        if(root == 0) root = new node(h);
        else
        {
            bool found = false;
            for(int i = 0; i < K; i++)
                if(root->ch[i])
                {
                    bool ok = true;
                    for(int j = 0; j < n; j++)
                        if((root->ch[i]->h[j].x != -1 || h[j].x != -1) && root->ch[i]->h[j] != h[j])
                            ok = false;
                    if(ok)
                    {
                        found = true;
                        node *n = root->ch[i];
                        root->ch[i] = 0;
                        delete root;
                        root = n;
                        break;
                    }
                }
            if(!found)
            {
                delete root;
                root = new node(h);
            }
        }
        cerr << root->V << endl;
        max_depth = 0;
        while(get_time() - start < 93)
        {
            for(int i = 0; i < 50; i++)
            {
                for(int x = 0; x <= W + 1; x++)
                    for(int y = 0; y <= H+1; y++)
                        tmp[x][y] = vis[x][y];
                root->visit(1, true);
            }
        }
        cerr << root->V << endl;
        cerr << max_depth << endl;
        for(int x = 0; x <= W + 1; x++)
            for(int y = 0; y <= H+1; y++)
                tmp[x][y] = vis[x][y];
        /*auto he = root->heuristics(h);
        for(auto x: he)
            cerr << x << " ";
        cerr << endl;
        for(int i = 0; i < n; i++)
            cerr << root->h[i].x << " " << root->h[i].y << endl;*/
        int move = max_element(root->v[p], root->v[p] + 4) - root->v[p];
        /*for(int i = 0; i < n; i++)
        {
            for(int j = 0; j < 4; j++)
                cerr << "(" << root->q[i][j] << "," << root->v[i][j] << ") ";
            cerr << endl;
        }*/
        cout << com[move] << endl;
        cout << "END" << endl;
    }
}