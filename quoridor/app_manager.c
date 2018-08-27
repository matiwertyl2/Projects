#include <gtk/gtk.h>
#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include "stale_zmienne.h"
#include "rozgrywka.h"
#include "app_manager.h"

void dodaj_styl (GtkWidget * widget, const char * path) {
  GtkCssProvider * cssProvider= gtk_css_provider_new();
  gtk_css_provider_load_from_path (cssProvider, path, NULL);
  GtkStyleProvider * style= (GtkStyleProvider *) cssProvider;
  gtk_style_context_add_provider (gtk_widget_get_style_context(widget), style, GTK_STYLE_PROVIDER_PRIORITY_USER);
}


void zamaluj_zajete_sciany () {
  for (int i=0; i<SIZE_OF_GRID; i++) {
    for (int j=0; j<SIZE_OF_GRID; j++) {
      if (sciany[i][j]==true) dodaj_styl (plansza[i][j]->button, "css/zajeta_sciana.css");
    }
  }
}

static void zmiana_nazwy_A (GtkWidget * widget, gpointer data) {
  const char * nazwa= gtk_entry_get_text ((GtkEntry *)widget);
  sprintf (Anazwa, "%s", nazwa);
}

static void zmiana_nazwy_B (GtkWidget * widget, gpointer data) {
  const char * nazwa= gtk_entry_get_text ((GtkEntry *)widget);
  sprintf (Bnazwa, "%s", nazwa);
}

static void change_grid_size(GtkWidget * widget, gpointer data) {
  SIZE_OF_GRID=2*(*((int *)data))-1;
  INIT_WALLS=WALLS[SIZE_OF_GRID];
}

void wczytaj_tekst () {
  TEKST[0]='\0';
  FILE * f= fopen ("instrukcje.txt", "r");
  char c= getc (f);
  while (c!=EOF) {
    int len=strlen(TEKST);
    TEKST[len]=c;
    TEKST[len+1]='\0';
    c=getc(f);
  }
  fclose (f);
}

GtkWidget * generuj_pole () {
  GtkWidget *grid;
  grid=gtk_grid_new();
  GtkWidget *button;
  gtk_grid_set_row_homogeneous (GTK_GRID(grid), FALSE);
  gtk_grid_set_column_homogeneous (GTK_GRID(grid), FALSE);
  for (int i=0; i<SIZE_OF_GRID; i++) {
    for (int j=0; j<SIZE_OF_GRID; j++) {
      bool puste=false;
      if (i%2==0 && j%2==0) button= new_button_pole (i, j);
      else if (i%2==0 && j%2==1) button=new_button_sciana_pionowa (i, j);
      else if (i%2==1 && j%2==0) button=new_button_sciana_pozioma (i, j);
      else puste=true; // albo pusty guzik- do ustalenia
      if (puste==false) gtk_grid_attach (GTK_GRID(grid), button, j, i, 1, 1);
    }
  }
  gtk_grid_set_row_homogeneous (GTK_GRID(grid), FALSE);
  gtk_grid_set_column_homogeneous (GTK_GRID(grid), FALSE);
  return grid;
}

GtkWidget * generuj_interfejs_gry (GtkWidget * pole_do_gry) {
  GtkWidget *interfejs= gtk_box_new (GTK_ORIENTATION_VERTICAL, 0);
  GtkWidget *box_sciany= gtk_box_new (GTK_ORIENTATION_HORIZONTAL, 0);
  
  polecenia_rozgrywki= gtk_label_new ("");
  dodaj_styl (polecenia_rozgrywki, "css/napisy_plansza.css");
  
  scianyA= gtk_label_new ("");
  dodaj_styl (scianyA, "css/napisy_czerwony.css");

  scianyB= gtk_label_new ("");
  dodaj_styl (scianyB, "css/napisy_niebieski.css");
  
  init_players();
  uaktualnij_labele();

  gtk_box_pack_start (GTK_BOX(box_sciany), scianyA, TRUE, TRUE, 10);
  gtk_box_pack_start (GTK_BOX(box_sciany), scianyB, TRUE, TRUE, 10);

  gtk_box_pack_start (GTK_BOX(interfejs), polecenia_rozgrywki, TRUE, TRUE, 10);
  gtk_box_pack_start (GTK_BOX(interfejs), box_sciany, TRUE, TRUE, 10);
  gtk_box_pack_start (GTK_BOX(interfejs), pole_do_gry, TRUE, TRUE, 10);
  
  GtkWidget * menu= gtk_box_new (GTK_ORIENTATION_HORIZONTAL, 10);
  
  GtkWidget * restart= gtk_button_new_with_label("RESTART");
  g_signal_connect (GTK_BUTTON(restart), "clicked", G_CALLBACK(restart_game), NULL);
  dodaj_styl (restart, "css/button.css");
  
  GtkWidget * save = gtk_button_new_with_label ("SAVE");
  g_signal_connect (GTK_BUTTON(save), "clicked", G_CALLBACK(zapisz_gre), NULL);
  dodaj_styl (save, "css/button.css");
  
  GtkWidget * main_page= gtk_button_new_with_label ("MAIN PAGE");
  g_signal_connect (GTK_BUTTON(main_page), "clicked", G_CALLBACK(powrot_do_glownej), NULL);
  dodaj_styl (main_page, "css/button.css");
 
  gtk_box_pack_start (GTK_BOX(menu), main_page, TRUE, TRUE, 10);
  gtk_box_pack_start (GTK_BOX(menu), save, TRUE, TRUE, 10);
  gtk_box_pack_start (GTK_BOX(menu), restart, TRUE, TRUE, 10);

  gtk_box_pack_start (GTK_BOX(interfejs), menu, TRUE, TRUE, 10);
  
  return interfejs;
}

GtkWidget * generuj_strone_glowna () {
  if (A->nr!='A') zmiana_ruchu();
  GtkWidget * strona_glowna;
  GtkWidget * play;
  GtkWidget * wczytaj;
  GtkWidget * settings;
  GtkWidget * instructions;
  GtkWidget * tytul;
  GtkWidget * menu= gtk_box_new (GTK_ORIENTATION_VERTICAL, 0);
  
  strona_glowna= gtk_window_new (GTK_WINDOW_TOPLEVEL);
  gtk_window_set_title (GTK_WINDOW(strona_glowna), "QUORIDOR - STRONA GŁÓWNA");
  gtk_window_set_position (GTK_WINDOW(strona_glowna), GTK_WIN_POS_CENTER);
  gtk_widget_set_size_request(strona_glowna, 450, 450);
  gtk_container_set_border_width (GTK_CONTAINER(strona_glowna), 100);
  dodaj_styl (strona_glowna, "css/strona_glowna.css");
  g_signal_connect (G_OBJECT (strona_glowna), "destroy", G_CALLBACK(gtk_main_quit), NULL);
  
  tytul=gtk_label_new ("QUORIDOR");
  dodaj_styl (tytul, "css/tytul_glowna.css");
  
  play=gtk_button_new_with_label ("NEW GAME");
  gtk_widget_set_size_request(play, 200, 50);
  g_signal_connect (GTK_BUTTON(play), "clicked", G_CALLBACK(start_game), NULL);
  dodaj_styl (play, "css/button.css");
  
  wczytaj=gtk_button_new_with_label("LOAD GAME");
  g_signal_connect (GTK_BUTTON(wczytaj), "clicked", G_CALLBACK(wczytaj_gre), NULL);
  gtk_widget_set_size_request(wczytaj, 200, 50);
  dodaj_styl (wczytaj, "css/button.css");

  settings= gtk_button_new_with_label ("SETTINGS");
  g_signal_connect (GTK_BUTTON(settings), "clicked", G_CALLBACK(go_to_settings), NULL);
  gtk_widget_set_size_request(settings, 200, 50);
  dodaj_styl (settings, "css/button.css");
  
  instructions= gtk_button_new_with_label("INSTRUCTIONS");
  gtk_widget_set_size_request (instructions, 200, 50);
  g_signal_connect (GTK_BUTTON(instructions), "clicked", G_CALLBACK(go_to_instructions), NULL);
  dodaj_styl (instructions, "css/button.css");
 
  gtk_box_pack_start (GTK_BOX(menu), tytul, TRUE, TRUE, 10);
  gtk_box_pack_start (GTK_BOX(menu), play, TRUE, TRUE, 0);
  gtk_box_pack_start (GTK_BOX(menu), wczytaj, TRUE, TRUE, 0);
  gtk_box_pack_start (GTK_BOX(menu), settings, TRUE, TRUE, 0);
  gtk_box_pack_start (GTK_BOX(menu), instructions, TRUE, TRUE, 0);

  gtk_container_add (GTK_CONTAINER(strona_glowna), menu);
  return strona_glowna;
}

GtkWidget * generuj_okienko_gry ( ) {
  GtkWidget * okienko_gra;
  okienko_gra = gtk_window_new (GTK_WINDOW_TOPLEVEL);
  gtk_window_set_title (GTK_WINDOW(okienko_gra), "QUORIDOR");
  gtk_window_set_position (GTK_WINDOW(okienko_gra), GTK_WIN_POS_CENTER);
  gtk_container_set_border_width (GTK_CONTAINER(okienko_gra), 30);
  g_signal_connect (G_OBJECT (okienko_gra), "destroy", G_CALLBACK (gtk_main_quit), NULL);
  dodaj_styl (okienko_gra, "css/strona_gra.css");
  GtkWidget *pole_do_gry= generuj_pole();
  GtkWidget * interfejs= generuj_interfejs_gry (pole_do_gry);
  gtk_container_add (GTK_CONTAINER(okienko_gra), interfejs);
  return okienko_gra;
}

GtkWidget * generuj_okienko_settings () {
  GtkWidget *settings;
  settings = gtk_window_new (GTK_WINDOW_TOPLEVEL);
  gtk_window_set_title (GTK_WINDOW (settings), "SETTINGS");
  gtk_window_set_position (GTK_WINDOW(settings), GTK_WIN_POS_CENTER);
  gtk_container_set_border_width (GTK_CONTAINER(settings), 30);
  //gtk_widget_set_size_request(settings, 450, 450);
  g_signal_connect (G_OBJECT(settings), "destroy", G_CALLBACK (gtk_main_quit), NULL);
  dodaj_styl (settings, "css/strona_settings.css");
  
  GtkWidget * menu= gtk_grid_new ();
  gtk_grid_set_column_homogeneous (GTK_GRID(menu), TRUE);
  gtk_grid_set_row_homogeneous (GTK_GRID(menu), TRUE);

  
  //nazwy graczy
  GtkWidget * label;
  label=gtk_label_new ("PLAYER NAMES");
  dodaj_styl (label, "css/tytuly_settings.css");
  gtk_grid_attach (GTK_GRID(menu), label, 1, 0, 1, 1);
  label=gtk_label_new ("Red player name :  ");
  dodaj_styl (label, "css/napisy_settings.css");
  gtk_grid_attach (GTK_GRID(menu), label, 0, 1, 1, 1);
  label=gtk_label_new ("Blue player name :  ");
  dodaj_styl (label, "css/napisy_settings.css");
  gtk_grid_attach (GTK_GRID(menu), label, 0, 2, 1, 1);
  
  GtkWidget * Aname;
  Aname=gtk_entry_new();
  g_signal_connect (GTK_ENTRY(Aname), "activate", G_CALLBACK(zmiana_nazwy_A), NULL);
  gtk_grid_attach (GTK_GRID(menu), Aname, 1, 1, 2, 1);
  dodaj_styl (Aname, "css/button.css");

  GtkWidget * Bname;
  Bname=gtk_entry_new();
  g_signal_connect (GTK_ENTRY(Bname), "activate", G_CALLBACK(zmiana_nazwy_B), NULL);
  gtk_grid_attach (GTK_GRID(menu), Bname, 1, 2, 2, 1);
  dodaj_styl (Bname, "css/button.css");
  
  // wielkosc planszy 
  label= gtk_label_new ("BOARD SIZE");
  dodaj_styl (label, "css/tytuly_settings.css");
  gtk_grid_attach (GTK_GRID(menu), label, 1, 3, 1, 1);
  
  GtkWidget * button;
  button= gtk_button_new_with_label ("5x5");
  g_signal_connect (GTK_BUTTON(button), "clicked", G_CALLBACK(change_grid_size), &grid_sizes[0] );
  gtk_grid_attach (GTK_GRID(menu), button, 0, 4, 1, 1);
  dodaj_styl (button, "css/button.css");
  
  button= gtk_button_new_with_label ("9x9");
  g_signal_connect (GTK_BUTTON(button), "clicked", G_CALLBACK(change_grid_size), &grid_sizes[1] );
  gtk_grid_attach (GTK_GRID(menu), button, 1, 4, 1, 1);
  dodaj_styl (button, "css/button.css");
  
  button= gtk_button_new_with_label ("11x11");
  g_signal_connect (GTK_BUTTON(button), "clicked", G_CALLBACK(change_grid_size), &grid_sizes[2] );
  gtk_grid_attach (GTK_GRID(menu), button, 2, 4, 1, 1);
  dodaj_styl (button, "css/button.css");

  GtkWidget * main_page= gtk_button_new_with_label("MAIN PAGE");
  g_signal_connect (GTK_BUTTON(main_page), "clicked", G_CALLBACK(powrot_do_glownej), NULL);
  gtk_grid_attach (GTK_GRID(menu), main_page, 1, 10, 1, 1);
  dodaj_styl (main_page, "css/button.css");

  gtk_container_add (GTK_CONTAINER(settings), menu);
  return settings;
}

GtkWidget * generuj_okienko_instructions () {
  GtkWidget * okienko= gtk_window_new (GTK_WINDOW_TOPLEVEL);
  gtk_window_set_title (GTK_WINDOW(okienko), "INSTRUCTIONS");
  gtk_window_set_position(GTK_WINDOW(okienko), GTK_WIN_POS_CENTER);
  gtk_container_set_border_width (GTK_CONTAINER(okienko), 30);
  g_signal_connect (G_OBJECT (okienko), "destroy", G_CALLBACK (gtk_main_quit), NULL);
  dodaj_styl (okienko, "css/strona_instructions.css");
  
  GtkWidget * box= gtk_box_new (GTK_ORIENTATION_VERTICAL, 0);
  
  
  wczytaj_tekst ();
  
  GtkWidget * instrukcje = gtk_label_new (TEKST);  
  dodaj_styl (instrukcje, "css/napisy_instrukcje.css");
  
  GtkWidget * main_page=gtk_button_new_with_label ("MAIN PAGE");
  g_signal_connect (GTK_BUTTON(main_page), "clicked", G_CALLBACK(powrot_do_glownej),  NULL);
  dodaj_styl (main_page, "css/button.css");

  
  gtk_box_pack_start (GTK_BOX(box), instrukcje, TRUE, TRUE, 10);
  gtk_box_pack_start (GTK_BOX(box), main_page, TRUE, TRUE, 10);

  
  gtk_container_add (GTK_CONTAINER(okienko), box);

  
  return okienko;
}



static void go_to_instructions (GtkWidget * widget, gpointer data) {
  GtkWidget * instructions = generuj_okienko_instructions ();
  gtk_widget_hide (okienko_aktywne);
  gtk_widget_show_all(instructions);
  okienko_aktywne=instructions;
}

static void go_to_settings (GtkWidget * widget, gpointer data) {
  GtkWidget * settings= generuj_okienko_settings();
  gtk_widget_hide (okienko_aktywne);
  gtk_widget_show_all (settings);
  okienko_aktywne=settings;
}

static void powrot_do_glownej (GtkWidget * widget, gpointer data) {
  GtkWidget * strona_glowna = generuj_strone_glowna ();
  gtk_widget_hide (okienko_aktywne);
  gtk_widget_show_all(strona_glowna);
  okienko_aktywne=strona_glowna;
}

static void restart_game (GtkWidget * widget, gpointer data) {
   gtk_widget_hide (okienko_aktywne); 
   start_game (widget, data);
}

static void zapisz_gre (GtkWidget * widget, gpointer data) {
  if (zwyciestwo()==false) {
    remove ("zapis_gry.txt");
    FILE * zapis_gry = fopen ("zapis_gry.txt", "w");
    fprintf (zapis_gry, "GRID %d", SIZE_OF_GRID);
    putc('\n', zapis_gry);
    for (int i=0; i<SIZE_OF_GRID; i++) {
      for (int j=0; j<SIZE_OF_GRID; j++) {
	if (sciany[i][j]==true) putc ('1', zapis_gry);
	else putc ('0', zapis_gry);
      }
    }
    putc ('\n', zapis_gry);
    fprintf (zapis_gry, "%c : %d %d %d", A->nr, A->sciany, A->pozycja->x, A->pozycja->y);
    putc ('\n', zapis_gry);
    fprintf (zapis_gry, "%c : %d %d %d", B->nr, B->sciany, B->pozycja->x, B->pozycja->y);
    fclose(zapis_gry);
  }
}

static void wczytaj_gre (GtkWidget * widget, gpointer data) {
  
  FILE * zapis_gry= fopen ("zapis_gry.txt", "r");
  char c;
  int g;
  fscanf (zapis_gry, "GRID %d", &g);
  SIZE_OF_GRID=g;
  INIT_WALLS= WALLS[SIZE_OF_GRID];
  GtkWidget * okienko_gry= generuj_okienko_gry();
  init_players();
  clear_used ();
  clear_sciany ();
  
  c=getc (zapis_gry);
  for (int i=0; i<SIZE_OF_GRID; i++) {
    for (int j=0; j<SIZE_OF_GRID; j++) {
      c=getc (zapis_gry);
      if (c=='1') sciany[i][j]=true;
    }
  }
  
  c=getc (zapis_gry);
  char nr;
  int x, y, s;
  fscanf (zapis_gry, "%c : %d %d %d", &nr, &s, &x, &y);
  if (nr!= A->nr) {
    zmiana_ruchu();
  }
  umiesc_gracza (A, x, y, s);
  c=getc (zapis_gry);
  fscanf (zapis_gry, "%c : %d %d %d", &nr, &s, &x, &y);
  umiesc_gracza (B, x, y, s);  
  fclose(zapis_gry);
  zamaluj_zajete_sciany ();
  uaktualnij_labele();
  gtk_widget_show_all(okienko_gry);
  if (okienko_aktywne!=NULL) gtk_widget_hide (okienko_aktywne);
  okienko_aktywne=okienko_gry;
}

static void start_game (GtkWidget * widget, gpointer data) {
  clear_used();
  clear_sciany();
  GtkWidget * okienko_gry= generuj_okienko_gry ();
  umiesc_gracza (A, A->pozycja->x, A->pozycja->y, A->sciany);
  umiesc_gracza (B, B->pozycja->x, B->pozycja->y, B->sciany);
  gtk_widget_show_all (okienko_gry);
  if (okienko_aktywne!=NULL) gtk_widget_hide (okienko_aktywne);
  okienko_aktywne=okienko_gry;
}


