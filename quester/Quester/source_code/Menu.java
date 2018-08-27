import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Arrays;


class Menu extends JPanel {
  public JButton play;
  public JButton instructions;
  public JButton controls;
  public JButton quit;
  public JButton home;
  public MyFrame frame;
  public JPanel box;


  public Menu (MyFrame f, String orientation) {
    this.frame=f;

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int w = (int)f.getWidth();
    int h = (int)f.getHeight();

    if (orientation=="Horizontal") box= new JPanel(new GridLayout(1, 0));
    else {
      box=box= new JPanel(new GridLayout(0, 1));
      box.setPreferredSize(new Dimension(w/2, h/3));
    }
    box.setOpaque(false);
    setOpaque(false);
    this.home= Widgets.homeButton(f);
    this.play= Widgets.playButton(f);
    this.instructions= Widgets.instructionsButton(f);
    this.controls= Widgets.controlsButton(f);
    this.quit= Widgets.quitButton(f);
    box.add(play);
    box.add(instructions);
    box.add(controls);
    box.add(home);
    box.add(quit);
    this.add(box);
  }

}
