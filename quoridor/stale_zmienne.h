#include <stdio.h>
#include <gtk/gtk.h>
#include <stdbool.h>

extern int  SIZE_OF_BOARD;
extern int SIZE_OF_GRID;
extern int INIT_WALLS;


typedef struct Pole {
  int x, y;
  GtkWidget* button;
}pole;

typedef struct Gracz {
  int sciany; // ile zostalo scian
  pole * pozycja; // pozycja gracza na planszy
  char  znak[100]; // nazwa gracza
  int win; // rzad wygrywajacy dla gracza
  GtkWidget * label_sciany; // label informujacy o stanie scian
  char nr; // ktory to gracz
  char css[100];
  char image[100];
}gracz;

extern char TEKST[100000];
extern int grid_sizes[100];
extern pole * plansza[100][100];
extern bool sciany[100][100];
extern bool used[100][100];
extern pole * zajete;
extern gracz * A;
extern gracz * B;
extern GtkWidget * polecenia_rozgrywki;
extern GtkWidget * scianyA;
extern GtkWidget * scianyB;
extern GtkWidget * okienko_aktywne;
extern char Anazwa[100];
extern char Bnazwa[100];
extern int WALLS[100];
