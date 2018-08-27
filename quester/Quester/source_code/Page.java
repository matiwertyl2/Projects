import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Arrays;
import java.io.FileReader;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.imageio.ImageIO;
import javax.swing.text.Style;


public class Page extends JPanel {
  public JLabel title;
  public JTextPane text;
  public Menu menu;
  MyFrame frame;

  public String readFile (String path) {
    String res="";
    try {
    FileReader file= new FileReader(path);
      try {
        char[] t = new char[10000];
        int n= file.read(t);
        for (int i=0; i<n; i++) res+=t[i];
      }
      catch (Exception e) {
      }
    }
    catch (Exception e){
    }
    return res;
  }

  public void paintComponent(Graphics page)
  {
    super.paintComponent(page);
    int width = frame.getWidth();
    int height = frame.getHeight();
    page.drawImage(new ImageIcon("images/background.png").getImage(), 0, 0, width, height, null);
  }

  public Page (String title, String path, MyFrame frame)  {
    this.frame=frame;
    this.title= new JLabel(title);
    this.title.setForeground(Color.white);
    this.title.setFont (this.title.getFont ().deriveFont (64.0f));
    this.title.setHorizontalAlignment(SwingConstants.CENTER);
    this.text= new JTextPane();
    SimpleAttributeSet attribs = new SimpleAttributeSet();
    StyleConstants.setAlignment(attribs , StyleConstants.ALIGN_CENTER);
    StyleConstants.setFontSize(attribs, 20);
    StyleConstants.setForeground (attribs, Color.white);
    text.setParagraphAttributes(attribs,true);
    text.setText(readFile(path));

    this.text.setEditable(false);
    this.text.setOpaque(false);
    setOpaque(false);

  }
}
