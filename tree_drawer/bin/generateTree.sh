#!/bin/bash

./treegen $1 $2 > randomTree.txt
./treecoord < randomTree.txt > TREECOORD.txt
cp randomTree.txt tmpTree.txt
