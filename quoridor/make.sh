#!/bin/bash

cc `pkg-config --cflags gtk+-3.0` -std=c99 main.c stale_zmienne.c rozgrywka.c app_manager.c -o QUORIDOR `pkg-config --libs gtk+-3.0`
