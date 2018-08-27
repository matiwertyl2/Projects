#include <stdio.h>
#include "stale_zmienne.h"

int  SIZE_OF_BOARD =10;
int SIZE_OF_GRID= 17;
int INIT_WALLS= 10;

char TEKST[100000];

int grid_sizes[100]={5, 9, 11};
pole * plansza[100][100]; //plansza przyciskow
bool sciany[100][100]; // ktore sciany zajete
bool used[100][100]; // ktore pola zajete w dfsie
pole * zajete; // zajete pole przez gracza niewykonujacego ruch
gracz * A; // gracz wykonujacy ruch
gracz * B; // gracz niewykonujacy ruchu
char Anazwa[100]="A";
char Bnazwa[100]="B";
int WALLS[100];


GtkWidget * polecenia_rozgrywki;
GtkWidget * scianyA;
GtkWidget * scianyB;
GtkWidget * okienko_aktywne;



