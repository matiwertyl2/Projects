#include <stdio.h>
#include <stdlib.h>
#include <gtk/gtk.h>
#include <stdbool.h>
#include "stale_zmienne.h"
#include "rozgrywka.h"
#include "app_manager.h"

int mini (int x, int y) {
  if(x<y) return x;
  return y;
}


void umiesc_gracza (gracz * X, int x, int y, int s) {// ustawia gracza na pozycji (x,y), s
  X->sciany=s;
  X->pozycja=plansza[x][y];
  GtkWidget * pionek= gtk_image_new_from_file (X->image);
  gtk_button_set_image (GTK_BUTTON(X->pozycja->button), pionek);
}

void init_players () {
  A= (gracz*)malloc (sizeof (gracz));
  B= (gracz*)malloc (sizeof (gracz));
  sprintf (A->znak, "%s", Anazwa);
  sprintf (B->znak,"%s",  Bnazwa);
  A->sciany=INIT_WALLS;
  B->sciany=INIT_WALLS;
  A->pozycja= plansza[0][SIZE_OF_GRID/2];
  B->pozycja=plansza[SIZE_OF_GRID-1][SIZE_OF_GRID/2];
  A->win=SIZE_OF_GRID-1;
  B->win=0;
  A->label_sciany= scianyA;
  B->label_sciany= scianyB;
  A->nr='A';
  B->nr='B';
  sprintf (A->css, "css/Azajety.css");
  sprintf (B->css, "css/Bzajety.css");
  sprintf (A->image, "images/pionek_A.png");
  sprintf (B->image, "images/pionek_B.png");
}

//czy meidzy polami P1 i P2 jest sciana
bool miedzy_sciana (pole * P1, pole * P2) {
  if (P1->x==P2->x) {
    if (sciany[P1->x][mini (P1->y, P2->y)+1]==false) return false;
    return true;
  }
  else {
    if (sciany[mini (P1->x, P2->x)+1][P1->y]==false) return false;
    return true;
  }
}

bool pola_sasiednie (pole * P1, pole * P2) {
  if (abs (P1->x-P2->x)==2 && abs (P1->y-P2->y)==0) return true;
  if (abs (P1->x-P2->x)==0 && abs (P1->y-P2->y)==2) return true;
  return false;
}

bool zwyciestwo () {
  if (A->pozycja->x== A->win) return true;
  return false;
}

void clear_used () {
  for (int i=0; i<SIZE_OF_GRID+2; i++) {
    for (int j=0; j<SIZE_OF_GRID+2; j++) {
      used[i][j]=false;
    }
  }
}

void clear_sciany () {
  for (int i=0; i<SIZE_OF_GRID+2; i++) {
    for (int j=0; j<SIZE_OF_GRID+2; j++) {
     sciany[i][j]=false;
    }
  }
}

void wypelnij () {
  for (int i=0; i<SIZE_OF_GRID+2; i++) {
    for (int j=0; j<SIZE_OF_GRID+2; j++) {
      sciany[i][j]=true;
    }
  }
}

bool dfs (pole * P, gracz * player) { // sprawdza czy gracz moze dojsc do sowjej pozycji wygrywajacej
  used[P->x][P->y]=true;
  if (P->x== player->win) return true;
  pole *gora, *dol, *lewo, *prawo;
  if (P->x-2>=0) {
    gora= plansza[P->x-2][P->y];
    if (miedzy_sciana (P, gora)==false &&  used[gora->x][gora->y]==false) {
	if (dfs (gora, player)==true) return true;
    }
  }
  if (P->y+2<SIZE_OF_GRID) {
    prawo=plansza[P->x][P->y+2];
    if (miedzy_sciana (P, prawo)==false && used[prawo->x][prawo->y]==false ){
      if (dfs (prawo, player)==true) return true;
    }
  }
  if (P->x+2<SIZE_OF_GRID) {
    dol=plansza[P->x+2][P->y];
    if (miedzy_sciana (P, dol)==false && used[dol->x][dol->y]==false) {
      if (dfs (dol, player)==true) return true;
    }
  }
  if (P->y-2>=0) {
    lewo= plansza[P->x][P->y-2];
    if (miedzy_sciana (P, lewo)==false && used[lewo->x][lewo->y]==false) {
      if (dfs (lewo, player)==true) return true;
    }
  }
  return false;
}

bool czy_blokuje (pole * P1, pole * P2) { // czy postawienie sciany uniemozliwi zwyciestwo ktoremukolwiek z graczy
  sciany[P1->x][P1->y]=true;
  sciany[P2->x][P2->y]=true;
  bool a= dfs (A->pozycja, A);
  clear_used();
  bool b= dfs (B->pozycja, B);
  clear_used();
  sciany[P1->x][P1->y]=false;
  sciany[P2->x][P2->y]=false;
  if (a==true && b==true) return false;
  return true;
}

bool dozwolony_ruch (pole * P) {
  if (P==B->pozycja) return false;
  if (P==A->pozycja) return false;
  if (pola_sasiednie (P, A->pozycja)==true) {
     if (miedzy_sciana (P, A->pozycja)==false) return true;
     return false;
  }
  //przeskok przeciwnika
  if (pola_sasiednie (P, B->pozycja)==true && pola_sasiednie (A->pozycja, B->pozycja)==true && (P->x==A->pozycja->x || P->y==A->pozycja->y) ) {
    if (miedzy_sciana (P, B->pozycja)==false && miedzy_sciana (B->pozycja, A->pozycja)==false) return true;
    return false;
  }
  return false;
}

bool dozwolona_sciana_pionowa ( pole * P) {
  if (A->sciany==0) return false;
  if (sciany[P->x][P->y]==false && P->x+2<SIZE_OF_GRID && sciany[P->x+2][P->y]==false) {
    pole * Pdolne= plansza[P->x+2][P->y];
    if (czy_blokuje (P, Pdolne)==false) return true;
    return false;
  }
  return false;
}

bool dozwolona_sciana_pozioma (pole * P) {
  if (A->sciany==0) return false;
  if (sciany[P->x][P->y]==false && P->y+2<SIZE_OF_GRID && sciany[P->x][P->y+2]==false) {
    pole * Pbok=plansza[P->x][P->y+2];
    if (czy_blokuje (P, Pbok)==false )return true;
    return false;
  }
  return false;
}

void uaktualnij_labele () {
  char polecenie[100];
  sprintf (polecenie, "Ruch gracza %s", A->znak);
  gtk_label_set_text (GTK_LABEL(polecenia_rozgrywki), polecenie);
  sprintf (polecenie, "Ściany %s: %d", A->znak, A->sciany);
  gtk_label_set_text (GTK_LABEL(A->label_sciany), polecenie);
  sprintf (polecenie, "Ściany %s: %d", B->znak, B->sciany);
  gtk_label_set_text (GTK_LABEL(B->label_sciany), polecenie);
}

void przesun_gracza (pole * P) {
  GtkWidget * pionek= gtk_image_new_from_file (A->image);
  gtk_button_set_image (GTK_BUTTON(P->button), pionek);
  gtk_button_set_image (GTK_BUTTON(A->pozycja->button), NULL);
  A->pozycja=P;
 
}

void postaw_sciane_pionowa (pole * P ) {
  pole * Pdolne= plansza[P->x+2][P->y];
  A->sciany--;
  sciany[P->x][P->y]=true;
  sciany[Pdolne->x][Pdolne->y]=true;
  dodaj_styl (P->button, "css/zajeta_sciana.css");
  dodaj_styl (Pdolne->button, "css/zajeta_sciana.css");
}

void podswietl_sciane_pionowa (pole * P) {
  pole * Pdolne= plansza[P->x+2][P->y];
  dodaj_styl (P->button, "css/zajeta_sciana.css");
  dodaj_styl (Pdolne->button, "css/zajeta_sciana.css");
}

void zgas_sciane_pionowa (pole * P) {
  pole * Pdolne= plansza[P->x+2][P->y];
  dodaj_styl (P->button, "css/wolna_sciana.css");
  dodaj_styl (Pdolne->button, "css/wolna_sciana.css");
}

void postaw_sciane_pozioma (pole * P) {
  pole * Pbok= plansza[P->x][P->y+2];
  A->sciany--;
  sciany[P->x][P->y]=true;
  sciany[Pbok->x][Pbok->y]=true;
  dodaj_styl (P->button, "css/zajeta_sciana.css");
  dodaj_styl (Pbok->button, "css/zajeta_sciana.css");
}

void podswietl_sciane_pozioma (pole * P) {
  pole * Pbok= plansza[P->x][P->y+2];
  dodaj_styl (P->button, "css/zajeta_sciana.css");
  dodaj_styl (Pbok->button, "css/zajeta_sciana.css");
}

void zgas_sciane_pozioma (pole * P) {
  pole * Pbok= plansza[P->x][P->y+2];
  dodaj_styl (P->button, "css/wolna_sciana.css");
  dodaj_styl (Pbok->button, "css/wolna_sciana.css");
}

void zmiana_ruchu () {
  if (zwyciestwo ()==true) {
    wypelnij ();
    char koniec[100];
    sprintf (koniec, "KONIEC GRY, Wygrał gracz %s", A->znak);
    gtk_label_set_text (GTK_LABEL(polecenia_rozgrywki), koniec);
  }
  else {
    gracz * C= A;
    A=B;
    B=C;
    uaktualnij_labele();
  }
}

static void ruch_sciana_pionowa (GtkWidget *widget, gpointer data) {
  pole * P= (pole*)data;
  if (dozwolona_sciana_pionowa(P)==true) {
    postaw_sciane_pionowa (P);
    zmiana_ruchu ();
  }
}

static void enter_sciana_pionowa (GtkWidget *widget, gpointer data) {
  pole * P= (pole*)data;
  if (dozwolona_sciana_pionowa(P)==true) {
    podswietl_sciane_pionowa (P);
  }
}

static void leave_sciana_pionowa (GtkWidget * widget, gpointer data) {
  pole * P= (pole*)data;
  if (dozwolona_sciana_pionowa(P)==true) {
    zgas_sciane_pionowa (P);
  }
}

static void ruch_sciana_pozioma (GtkWidget *widget, gpointer data) {
  pole *P= (pole*)data;
  if (dozwolona_sciana_pozioma (P)==true) {
    postaw_sciane_pozioma (P);
    zmiana_ruchu();
  }
}

static void enter_sciana_pozioma (GtkWidget *widget, gpointer data) {
  pole *P= (pole*)data;
  if (dozwolona_sciana_pozioma (P)==true) {
    podswietl_sciane_pozioma (P);
  }
}

static void leave_sciana_pozioma (GtkWidget *widget, gpointer data) {
  pole *P= (pole*)data;
  if (dozwolona_sciana_pozioma (P)==true) {
    zgas_sciane_pozioma (P);
  }
}

static void ruch_pole (GtkWidget *widget, gpointer data) {
  pole * P = (pole*)data;
  if ( dozwolony_ruch (P)==true) {    
    przesun_gracza (P);
    zmiana_ruchu ();
  }
}

GtkWidget * new_button_sciana_pionowa (int x, int y) {
  GtkWidget * sciana;
  sciana= gtk_button_new ();
  gtk_widget_set_size_request (sciana, SIZE_OF_BOARD, 4*SIZE_OF_BOARD);
  pole * P= (pole*)malloc(sizeof (pole));
  P->x=x;
  P->y=y;
  P->button=sciana;
  plansza[x][y]=P;
  g_signal_connect (sciana, "clicked", G_CALLBACK(ruch_sciana_pionowa), (gpointer)P);
  g_signal_connect (sciana, "enter", G_CALLBACK(enter_sciana_pionowa), (gpointer)P);
  g_signal_connect (sciana, "leave", G_CALLBACK(leave_sciana_pionowa), (gpointer)P);
  dodaj_styl (sciana, "css/wolna_sciana.css");
  return sciana;
}


GtkWidget * new_button_sciana_pozioma (int x, int y) {
  GtkWidget * sciana;
  sciana= gtk_button_new ();
  gtk_widget_set_size_request (sciana, 4*SIZE_OF_BOARD, SIZE_OF_BOARD);
  pole * P= (pole*)malloc(sizeof (pole));
  P->x=x;
  P->y=y;
  P->button=sciana;
  plansza[x][y]=P;
  g_signal_connect (sciana, "clicked", G_CALLBACK(ruch_sciana_pozioma), (gpointer)P);
  g_signal_connect (sciana, "enter", G_CALLBACK(enter_sciana_pozioma), (gpointer)P);
  g_signal_connect (sciana, "leave", G_CALLBACK(leave_sciana_pozioma), (gpointer)P);
  dodaj_styl (sciana, "css/wolna_sciana.css");
  return sciana;
}

GtkWidget * new_button_pole (int x, int y) {
  GtkWidget * button;
  button= gtk_button_new_with_label (NULL);
  dodaj_styl (button, "css/pole_gry.css");
  gtk_widget_set_size_request (button, 4*SIZE_OF_BOARD, 4*SIZE_OF_BOARD);
  pole * P= (pole*)malloc(sizeof (pole));
  P->x=x;
  P->y=y;
  P->button=button;
  plansza[x][y]=P;
  g_signal_connect (button, "clicked", G_CALLBACK(ruch_pole), (gpointer)P);
  return button;
} 
