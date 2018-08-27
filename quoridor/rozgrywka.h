#include <gtk/gtk.h>
#include <stdbool.h>
#include <stdio.h>

void umiesc_gracza (gracz * X, int x, int y, int s);
void init_players ();
int mini (int x, int y);
bool miedzy_sciana (pole * P1, pole * P2);
bool pola_sasiednie (pole * P1, pole * P2);
bool zwyciestwo ();
void clear_used ();
void clear_sciany ();
void wypelnij ();
bool dfs (pole * P, gracz * player); // sprawdza czy gracz moze dojsc do sowjej pozycji wygrywajacej
bool czy_blokuje (pole * P1, pole * P2);// czy postawienie sciany uniemozliwi zwyciestwo ktoremukolwiek z graczy

bool dozwolony_ruch (pole * P);
bool dozwolona_sciana_pionowa ( pole * P);
bool dozwolona_sciana_pozioma (pole * P);

void uaktualnij_labele (); //przy zmianie ruchu aktualizacja informacji
void przesun_gracza (pole * P);

void postaw_sciane_pionowa (pole * P );
void podswietl_sciane_pionowa (pole * P);
void zgas_sciane_pionowa (pole * P);

void postaw_sciane_pozioma (pole * P);
void podswietl_sciane_pozioma (pole * P);
void zgas_sciane_pozioma (pole * P);

void zmiana_ruchu ();

static void ruch_sciana_pionowa (GtkWidget *widget, gpointer data);
static void enter_sciana_pionowa (GtkWidget *widget, gpointer data);
static void leave_sciana_pionowa (GtkWidget * widget, gpointer data);

static void ruch_sciana_pozioma (GtkWidget *widget, gpointer data);
static void enter_sciana_pozioma (GtkWidget *widget, gpointer data);
static void leave_sciana_pozioma (GtkWidget *widget, gpointer data);

static void ruch_pole (GtkWidget *widget, gpointer data);

GtkWidget * new_button_sciana_pionowa (int x, int y);
GtkWidget * new_button_sciana_pozioma (int x, int y);
GtkWidget * new_button_pole (int x, int y);


















