import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Arrays;
import javax.imageio.ImageIO;


public class Widgets {

  public static JButton playButton (final MyFrame frame) {
    JButton play= new JButton("PLAY");
    play.setFocusPainted(false);
    play.setBackground(new Color(100, 100, 100));
    play.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        frame.play();
      }
    });
    return play;
  }

  public static JButton instructionsButton (final MyFrame frame) {
    JButton instructions= new JButton("INSTRUCTIONS");
    instructions.setBackground(new Color(100, 100, 100));
    instructions.setFocusPainted(false);
    instructions.setBackground(new Color(100, 100, 100));
    instructions.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        frame.instructions();
      }
    });
    return instructions;
  }

  public static JButton controlsButton (final MyFrame frame) {
    JButton controls =new JButton("CONTROLS");
    controls.setFocusPainted(false);
    controls.setBackground(new Color(100, 100, 100));
    controls.addActionListener (new ActionListener()  {
      public void actionPerformed(ActionEvent e) {
        frame.controls();
      }
    });
    return controls;
  }

  public static JButton quitButton (final MyFrame frame) {
    JButton quit=new JButton("QUIT");
    quit.setFocusPainted(false);
    quit.setBackground(new Color(100, 100, 100));
    quit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         System.exit(0);
      }
    });
    return quit;
  }

  public static JButton homeButton (final MyFrame frame) {
    JButton home=new JButton ("HOME");
    home.setFocusPainted(false);
    home.setBackground(new Color(100, 100, 100));
    home.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        frame.home();
      }
    });
    return home;
  }

  static JButton homeDialogButton(final MyFrame frame, final JDialog dialog) {
    JButton button= new JButton("HOME");
    button.setFocusPainted(false);
    button.setBackground(new Color(100, 100, 100));
    button.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
    button.addActionListener(new ActionListener () {
      public void actionPerformed(ActionEvent e) {
        dialog.dispose();
        frame.home();
      }
    });
    return button;
  }

  static JButton resumeButton (final MyFrame frame, final JDialog dialog) {
    JButton resume= new JButton ("RESUME");
    resume.setFocusPainted(false);
    resume.setBackground(new Color(100, 100, 100));
    resume.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
    resume.addActionListener(new ActionListener () {
      public void actionPerformed(ActionEvent e) {
        dialog.dispose();
        frame.resume();
      }
    });
    return resume;
  }

  static JButton nextLevelButton(final MyFrame frame, final JDialog dialog) {
    JButton nextLevel= new JButton ("NEXT LEVEL");
    nextLevel.setFocusPainted(false);
    nextLevel.setBackground(new Color(100, 100, 100));
    nextLevel.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
    nextLevel.addActionListener(new ActionListener () {
      public void actionPerformed(ActionEvent e) {
        dialog.dispose();
        frame.nextLevel();
      }
    });
    return nextLevel;
  }

  public static void gameOverDialog (MyFrame frame) {
    gameOverDialog dialog= new gameOverDialog(frame);

  }

  public static void gamePauseDialog (MyFrame frame) {
    gamePauseDialog dialog= new gamePauseDialog(frame);
  }

  public static void gameWinDialog(MyFrame frame) {
    gameWinDialog dialog= new gameWinDialog (frame);
  }


}

class gameOverDialog extends gameDialog {
  public gameOverDialog (MyFrame frame) {
    super (frame);
    super.setDialog(frame, Widgets.homeDialogButton(frame, this), Widgets.quitButton(frame), "GAME OVER");
  }
}

class gamePauseDialog extends gameDialog {
  public gamePauseDialog (MyFrame frame ) {
    super(frame);
    super.setDialog(frame, Widgets.resumeButton(frame, this), Widgets.homeDialogButton(frame, this), "PAUSE");
  }
}

class gameWinDialog extends gameDialog {
  public gameWinDialog (MyFrame frame) {
    super(frame);
    super.setDialog(frame, Widgets.nextLevelButton(frame, this), Widgets.homeDialogButton(frame, this), "CONGRATULATIONS, YOU WON !");
  }
}


class gameDialog extends JDialog {

  public gameDialog(MyFrame frame) {
    super(frame, true);
  }

  public void setDialog (MyFrame frame, JButton b1, JButton b2, String t) {
    setTitle(t);
    setSize(300, 100);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int w = (int)screenSize.getWidth();
    int h = (int)screenSize.getHeight();
    setLocation(new Point(w/2-150, h/2-300));

    JLabel title= new JLabel(t, SwingConstants.CENTER);
    title.setForeground(Color.white);

    JPanel buttons= new JPanel();
    buttons.setOpaque(false);

    FlowLayout layout2= new FlowLayout();
    buttons.setLayout(layout2);


    buttons.add(b1);
    buttons.add(b2);

    JPanel panel = new JBackgroundPanel();
    GridLayout layout= new GridLayout (2, 1);
    panel.setLayout(layout);

    panel.add(title);
    panel.add(buttons);


    add(panel);

    setUndecorated(true);
    setVisible(true);
  }
}

class JBackgroundPanel extends JPanel {
  public JBackgroundPanel() {
    super();
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.drawImage (new ImageIcon("images/dialog.png").getImage(), 0, 0, 300, 100, null);
  }
}
