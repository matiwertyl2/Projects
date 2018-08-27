#include <bits/stdc++.h>
using namespace std;

const int defaultDist=75;

int VSum( vector<int>& V)
{
  int res=0;
  for (size_t i=0; i<V.size(); i++) res+=V[i];
  return res;
}

vector<int> VCopy (vector<int>& V)
{
  vector<int> res;
  for (size_t i=0; i<V.size(); i++) res.push_back(V[i]);
  return res;
}

vector<int> VIncrease( vector<int>& V, int x)
{
  vector<int> res;
  for (size_t i=0; i<V.size(); i++) res.push_back(V[i]+x);
  return res;
}


struct Node
{
  vector<Node*> sons;
  vector<int> leftB;
  vector<int> rightB;
  int x;
  int y;
  int dx;
  int label;
  int parent;

  Node(vector<vector<int> >&  G, int v, int p)
    : label{v}, parent{p}
  {
    for (size_t i=0; i<G[v].size(); i++)
    {
      int u=G[v][i];
      if (u!=p)
      {
        sons.push_back(new Node(G, u, v));
      }
    }
  }

  void Print()
  {
  //  cout << "(" << x << "," << y << ")\n";
    for (size_t i=0; i<sons.size(); i++)
    {
      sons[i]->Print();
    }
    for (size_t i=0; i<sons.size(); i++)
    {
      cout << label << " " << sons[i]->label << "\n";
    }
  }

  void PrintEdges()
  {
    for (size_t i=0; i<sons.size(); i++)
    {
      if (sons[i]->label!=parent)
      {
        cout << x << " " << y << " " << sons[i]->x << " " << sons[i]->y << " ";
        sons[i]->PrintEdges();
      }
    }
  }

  void PrintNodes ()
  {
    cout << x << " " << y << " " << label << " ";
    for (size_t i=0; i<sons.size(); i++)
    {
      if (sons[i]->label!=parent)
      {
        sons[i]->PrintNodes();
      }
    }
  }

  int CountRequiredDist(vector<int>& left, vector<int>& right)
  {
    int res=defaultDist;
    int n= min (left.size(), right.size());
    for (int i=0; i<n; i++)
    {
      res= max (res, -right[i]+left[i]+defaultDist);
    }
    return res;
  }

  vector<int> updateLeftBorder(vector<int>& old, vector<int>& fresh, int d)
  {
    vector<int> res;
    int n= min (old.size(), fresh.size());
    for (size_t i=0; i<n; i++)
    {
      res.push_back (min (old[i], fresh[i]+d));
    }
    if (fresh.size()>old.size())
    {
      for (size_t i=n; i<fresh.size(); i++) res.push_back(fresh[i]+d);
    }
    else
    {
      for (size_t i=n; i<old.size(); i++) res.push_back(old[i]);
    }
    return res;
  }

  vector<int> updateRightBorder(vector<int>& old, vector<int>& fresh, int d)
  {
    vector<int> res;
    int n= min (old.size(), fresh.size());
    for (size_t i=0; i<n; i++)
    {
      res.push_back (max (old[i]-d, fresh[i]));
    }
    if (fresh.size()>old.size())
    {
      for (size_t i=n; i<fresh.size(); i++) res.push_back(fresh[i]);
    }
    else
    {
      for (size_t i=n; i<old.size(); i++) res.push_back(old[i]-d);
    }
    return res;
  }

  void CountCoordinates()
  {
    //cout << label << endl;
    for (size_t i=0; i<sons.size(); i++)
    {
      sons[i]->CountCoordinates();
    }
    if (sons.size()==0) return;
    else if (sons.size()==1)
    {
      leftB.push_back(0);
      rightB.push_back(0);
      leftB.insert(leftB.end(), sons[0]->leftB.begin(), sons[0]->leftB.end());
      rightB.insert(rightB.end(), sons[0]->rightB.begin(), sons[0]->rightB.end());
      sons[0]->dx=0;
    }
    else {
      vector<int> left=VCopy(sons[0]->leftB);
      vector<int> right=VCopy(sons[0]->rightB);
      vector<int> distances;
      for (size_t i=1; i<sons.size(); i++)
      {
        int d= CountRequiredDist(right, sons[i]->leftB);
        distances.push_back(d);
        left= updateLeftBorder(left, sons[i]->leftB, VSum(distances));
        right= updateRightBorder(right, sons[i]->rightB, d);
      }

      int sum=VSum(distances);
      distances.push_back(0);
    //  cout << sum << "\n";
      int sonDx=-sum/2;
      for (size_t i=0; i<sons.size(); i++)
      {
        sons[i]->dx=sonDx;
        sonDx+=distances[i];
      }
      leftB.push_back(0);
      rightB.push_back(0);
      leftB.insert(leftB.end(), left.begin(), left.end());
      rightB.insert(rightB.end(), right.begin(), right.end());
      leftB= VIncrease(leftB, -sum/2);
      rightB= VIncrease(rightB, sum-sum/2);
    }
  }

  void CountX(int Dx) {
    x=Dx+dx;
    for (size_t i=0; i<sons.size(); i++) sons[i]->CountX(x);
  }

  void CountY (int depth)
  {
    y=depth*defaultDist*3;
    for (size_t i=0; i<sons.size(); i++) sons[i]->CountY(depth+1);
  }

  int FindSmallestX()
  {
    int res=0;
    for (size_t i=0; i<sons.size(); i++) res= min (res, sons[i]->FindSmallestX());
    res= min(res, x);
    return res;
  }

  void MakePositiveX(int increase)
  {
    x+=increase;
    for (size_t i=0; i<sons.size(); i++) sons[i]->MakePositiveX(increase);
  }

};

int main () {
    int n, a, b;
    cin >> n;
    vector< vector<int> > G;
    G.resize(n+1);
    for (int i=1; i<n; i++)
    {
      cin >> a >> b;
      G[a].push_back(b);
      G[b].push_back(a);
    }
    Node *K = new Node(G, 1, -1);
    K->CountCoordinates();
    K->CountX(0);
    K->MakePositiveX(-K->FindSmallestX());
    K->CountY(0);
    K->PrintEdges();
    K->PrintNodes();
}
