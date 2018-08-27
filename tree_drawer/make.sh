#!/bin/bash


mkdir -p  bin
javac src/*.java -d bin/
g++ -std=c++11 src/treegen.cpp -o bin/treegen
g++ -std=c++11 src/tree_draw.cpp -o TreeDrawer
g++ -std=c++11 src/treecoord.cpp -o bin/treecoord
cp src/fileTree.sh bin/
cp src/generateTree.sh bin/
cp src/inputTree.sh bin/
chmod +x bin/fileTree.sh
chmod +x bin/generateTree.sh
chmod +x bin/inputTree.sh

