import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Arrays;


public class MainPage extends Page {

  public MainPage (String title, String text, MyFrame frame)  {
    super(title, text, frame);
    this.menu= new Menu(frame, "Vertical");
  //  this.menu.setMinimumSize(new Dimension(1000, 1000));
  //  this.menu.setSize(1000,1000);
    this.setLayout(new BorderLayout (100, 100));
    this.setBorder(BorderFactory.createEmptyBorder(50,0,0,50));
    this.add(this.title, BorderLayout.NORTH);
    this.add(this.text, BorderLayout.SOUTH );
    this.add(menu, BorderLayout.CENTER);

  }
}
