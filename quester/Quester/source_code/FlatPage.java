import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Arrays;
import java.io.FileReader;



public class FlatPage extends Page {



  public FlatPage (String title, String file, MyFrame frame)  {
    super(title, file, frame);
    this.menu= new Menu(frame, "Horizontal");
    this.setLayout(new BorderLayout (30, 30));
    this.add(this.title, BorderLayout.NORTH);
    this.add(this.text, BorderLayout.CENTER );
    this.add(menu, BorderLayout.SOUTH);
  }
}
