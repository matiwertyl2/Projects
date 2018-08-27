import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Arrays;



class MyFrame extends JFrame {

  public MyFrame() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int screenWidth = (int)screenSize.getWidth();
    int screenHeight = (int)screenSize.getHeight();
    this.setSize(screenWidth, screenHeight);
    this.setTitle("Hazus - TreeDrawer");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.add(new Manager(this));
    this.setVisible(true);
  }

}
