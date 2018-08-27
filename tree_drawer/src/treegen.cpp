#include <bits/stdc++.h>
using namespace std;

vector< vector <int> > V;
vector<int> mapping;
map <pair <int, int>, bool > used;

int main (int argc, char** argv)
{
  srand(atoi(argv[1]));
  int n=atoi(argv[2]);
  cout << n << "\n";
  V.resize(n+1);
  for (int i=2; i<=n; i++)
  {
    int par= rand() % i +1;
    if (par==i) par--;
    V[par].push_back(i);
    V[i].push_back(par);
  }
  for (int i=1; i<=n; i++) random_shuffle(V[i].begin(), V[i].end());
  for (int i=0; i<=n; i++)
  {
    for (int j=0; j<V[i].size(); j++)
    {
      int u=i;
      int v=V[i][j];
      if (used[make_pair (u, v)]==false && used[make_pair (v, u)]==false) {
        cout << u << " " << v << "\n";
        used[make_pair (u, v)]=true;
      }
    }
  }
}
