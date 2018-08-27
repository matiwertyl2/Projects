import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Arrays;



class MyFrame extends JFrame implements ActionListener {
  public Board board;
  public boolean active;
  Timer t= new Timer(40, this);
  public MyFrame() {
    this.active=false;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int screenWidth = (int)screenSize.getWidth();
    int screenHeight = (int)screenSize.getHeight();
    this.setSize(screenWidth, screenHeight);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setTitle("The Quester - by Hazus");
    t.start();
    this.add(new MainPage("The Quester", "BY HAZUS", this));
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setVisible(true);
  }

  public void actionPerformed(ActionEvent e) {
    if(active==true && board.player.currentHealth<=0) {
      board.t.stop();
      Widgets.gameOverDialog(this);
    }
    else if (active && board.pauseButton) {
      Widgets.gamePauseDialog(this);
    }
    else if (active && board.quest.isCompleted()) {
      board.t.stop();
      Widgets.gameWinDialog(this);
    }
  }



  public void backToMain() {
    remove(board);
    setVisible(false);
    active=false;
    board=null;
    this.add( new Menu(this, "Vertical"));
    setVisible(true);
  }

  public void play() {
    board= new Board();
    active=true;
    changePage(board);
  }

  public void instructions() {
    active=false;
    changePage(new FlatPage("Instructions", "texts/instructions.txt", this));
  }

  public void controls () {
    active=false;
    changePage(new FlatPage("Controls", "texts/controls.txt", this));
  }

  public void home () {
    active=false;
    changePage(new MainPage("The Quester", "texts/home.txt", this));
  }

  public void resume () {
    board.pauseButton=false;
    board.t.start();

  }

  public void nextLevel () {
    play();
  }

  public void changePage(JPanel newPage) {
    this.getContentPane().removeAll();
    this.add(newPage);
    this.getContentPane().invalidate();
    this.getContentPane().validate();
    this.requestFocusInWindow();
    this.setFocusable(true);
    newPage.requestFocusInWindow();
  }
}
