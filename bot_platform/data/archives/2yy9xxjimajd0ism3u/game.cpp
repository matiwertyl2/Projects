#include<bits/stdc++.h>
#include "json.hpp" //z biblioteki https://github.com/nlohmann/json
using namespace std;
using json = nlohmann::json;

const int  BOARD_SIZE = 5;

struct Point {
    int x;
    int y;

    Point() 
        : x(0), y(0)
    {}

    Point(int x, int y) 
        : x(x), y(y)
    {}

    bool operator == (Point p) {
        return (p.x == x && p.y == y);
    }

    void operator = (Point p) {
        x = p.x;
        y = p.y;
    }

    void operator += (Point p) {
        x += p.x;
        y += p.y;
    }
};

struct Board {
    // -1 - free 0 - player A, 1 - player B
    int tab[BOARD_SIZE][BOARD_SIZE];
    Point pos[2];

    Board () {
        for (size_t i=0; i<BOARD_SIZE; i++) {
            for (size_t j=0; j<BOARD_SIZE; j++) {
                tab[i][j]=-1;
            }
        }
        pos[0] = Point(rand() % BOARD_SIZE, rand() % BOARD_SIZE);
        pos[1] = Point(rand() % BOARD_SIZE, rand() % BOARD_SIZE);
        setField(pos[0], 0);
        setField(pos[1], 1);
        while (pos[1] == pos[0]) pos[1] = Point(rand() % BOARD_SIZE, rand() % BOARD_SIZE);

    }


    void setField(Point p, int value) {
        tab[p.x][p.y] = value;
    }

    Point getMovePoint(string s) {
        if (s =="UP") return Point(-1, 0);
        if (s =="DOWN") return Point(1, 0);
        if (s == "LEFT") return Point(0, -1);
        if (s == "RIGHT") return Point(0, 1);
    }

    bool freeField(Point p) {

        if(tab[p.x][p.y] == -1) return true;
        return false;
    }

    bool isValidMove(string move, int nr) {
        Point p = getMovePoint(move);
        p += pos[nr];
        cerr << "POINT " << nr << " " << p.x << p.y << "\n";
        if(p.x < 0 || p.x >= BOARD_SIZE) return false;
        if(p.y < 0 || p.y >= BOARD_SIZE) return false;
        cerr << "BORDERS OK " << nr << "\n";
        return freeField(p);
    } 

    bool makeMove(string move, int nr) {
        Point p = getMovePoint(move);
        p += pos[nr];
        if(freeField(p)) {
            setField(p, nr);
            pos[nr] = p;
            return true;
        }
        return false;
    }

    void write(FILE *f, int nr) {
        cerr << "SEND TO BOTS\n ";
        fprintf(f, "%d %d\n", BOARD_SIZE, nr);
        cerr << BOARD_SIZE << " " << nr << "\n";
        fprintf(f, "%d %d\n", pos[0].x, pos[0].y);
        cerr << pos[0].x << " " << pos[0].y << "\n";
        fprintf(f, "%d %d\n", pos[1].x, pos[1].y);
        cerr << pos[1].x << " " << pos[1].y << "\n";
        for(int i = 0; i < BOARD_SIZE; i++) {
            for(int j = 0; j < BOARD_SIZE; j++)
                fprintf(f, "%d ", tab[i][j]);
               
            fprintf(f, "\n");
        }
    }
};

bool checkMoveCommand(string m) {
    if (m!="UP" && m!="DOWN" && m!="LEFT" && m!="RIGHT") return false;
    return true;
}

void sendJson(const json &j) {
    cout << j << endl;
}

void update(const Board &b) {
    sendJson({
        {"type", "update"},
        {"description", {
            {"board", b.tab}
        }},
        {"nextTurn", true}
    });
}

void finish(int loser) {
    sendJson({
        {"type", "finished"},
        {"results", { loser != 0 ? 1 : 2, loser != 1 ? 1 : 2 }}
    });
    exit(0);
}

void playerFailed(int nr, string reason) {
    sendJson({
        {"type", "player_failed"},
        {"player", nr},
        {"reason", reason}
    });
    finish(nr);
}

void myAssert(bool condition, int nr, string message) {
    if(!condition)
        playerFailed(nr, message);
}

int main(int argc, char *argv[]) {
    srand(time(0));

    assert(argv[argc-1] == string("2"));
    FILE *inputs[2], *outputs[2];
    for(int i = 0; i < 2; i++) {
        outputs[i] = fdopen(2 * i + 3, "w");
        inputs[i] = fdopen(2 * i + 1 + 3, "r");
    }
    Board b;
    update(b);
    int moves = 0;
    while(true) {
        for(int p = 0; p < 2; p++) {
            b.write(outputs[p], p);
            fprintf(outputs[p], "END\n");
            fflush(outputs[p]);
        }
        vector<string> moves;
        vector<bool> lost;
        for(int p =0; p<2; p++) {
            char move[10];
            char end[5];
            myAssert( fscanf(inputs[p], "%s", move) == 1,            p, "Invalid output1");
            myAssert( fscanf(inputs[p], "%4s", end) == 1,            p, "Invalid output2");
            myAssert(checkMoveCommand(move),                         p, "Invalid move command");
            myAssert( end == string("END"),                          p, "Missing END token");
            moves.push_back(move);
            lost.push_back(b.isValidMove(move, p));
        }
        cerr << "finished reading\n";
        cerr << moves[0] << " " << moves[1] << "\n";
        cerr << lost[0] << " " << lost[1] << "\n";
        for(int p =0; p<2; p++) {
            // later should be a draw if more than one lost
            if (lost[p]==false) finish(p);
            if (b.makeMove(moves[p], p) == false ) finish(-1);
        }
        update(b);
    }
}