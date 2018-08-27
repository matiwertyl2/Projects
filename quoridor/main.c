#include <gtk/gtk.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <math.h>
#include "stale_zmienne.h"
#include "rozgrywka.h"
#include "app_manager.h"


int main (int argc, char **argv) {
  gtk_init (&argc, &argv);
  WALLS[9]=5;
  WALLS[17]=10;
  WALLS[21]=15;
  init_players();
  GtkWidget * strona_glowna= generuj_strone_glowna();
  
  gtk_widget_show_all(strona_glowna);
  okienko_aktywne=strona_glowna;
  gtk_main();
}