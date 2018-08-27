#include <gtk/gtk.h>
#include <stdio.h>

void zamaluj_zajete_sciany ();
void dodaj_styl (GtkWidget * widget, const char * path);

GtkWidget * generuj_pole ();
GtkWidget * generuj_interfejs_gry (GtkWidget * pole_do_gry);
GtkWidget * generuj_strone_glowna ();
GtkWidget * generuj_okienko_gry ( );
GtkWidget * generuj_okienko_settings ();
GtkWidget * generuj_okienko_instructions ();

static void powrot_do_glownej (GtkWidget * widget, gpointer data);
static void restart_game (GtkWidget * widget, gpointer data);
static void start_game (GtkWidget * widget, gpointer data);
static void wczytaj_gre (GtkWidget * widget, gpointer data);
static void zapisz_gre (GtkWidget * widget, gpointer data);
static void go_to_settings (GtkWidget * widget, gpointer data);
static void go_to_instructions (GtkWidget * widget, gpointer data);





