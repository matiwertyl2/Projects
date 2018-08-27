import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.util.Random;
import java.util.Arrays;
import java.util.Collections;
import java.awt.image.BufferedImage;
import java.io.FileReader;
import javax.imageio.*;
import java.io.*;
import javax.swing.JFileChooser;
import java.io.File;

class Line {
  int x1;
  int x2;
  int y1;
  int y2;
  public Line (int x1, int y1, int x2, int y2)
  {
    this.x1=x1;
    this.y1=y1;
    this.x2=x2;
    this.y2=y2;
  }
}

class Node {
  int x;
  int y;
  int label;
  public Node (int x, int y, int label)
  {
    this.x=x;
    this.y=y;
    this.label=label;
  }
}

public class TreeCanvas extends JPanel {
  MyFrame frame;
  Vector<Line> Lines;
  Vector<Node> Nodes;
  public BufferedImage image;
  int X;
  int Y;

  public TreeCanvas (MyFrame frame)
  {
    this.frame=frame;
    this.X=1200;
    this.Y=600;
    this.Lines= new Vector<Line> ();
    this.Nodes= new Vector<Node> ();
    this.image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    createImage(0);
    setFocusable(true);
  }

  public double findGoodImgSize()
  {
    int x=50, y=50;
    for (Line line: Lines)
    {
      x= Math.max (x, line.x1+50);
      x= Math.max(x, line.x2+50);
      y= Math.max (y, line.y1+50);
      y= Math. max (y, line.y2+50);
    }
    double scalex= ((double)X) / ((double) x);
    double scaley= ((double)Y)/((double)y);
    double scale= Math.min(scalex, scaley);
    for (Line line: Lines)
    {
      line.x1= (int)(line.x1 * scalex+Math.max(25*scalex, 25));
      line.x2= (int)(line.x2 * scalex+Math.max(25*scalex, 25));
      line.y1= (int)(line.y1 * scaley+Math.max(25*scaley, 25));
      line.y2= (int)(line.y2 * scaley+Math.max(25*scaley, 25));
    }
    for (Node node : Nodes)
    {

      node.x= (int)(node.x *scalex+Math.max(25*scalex, 25) );
      node.y= (int)(node.y * scaley+Math.max(25*scaley, 25));
    }
    return Math.min(scalex, scaley);
  }

  public void getTree() {
    String s= readFile("TREECOORD.txt");
    String[] numbers= s.split(" ");
    int N= (numbers.length+4)/7;
    for (int i=0; i<4*(N-1); i+=4)
    {
      int x1= Integer.parseInt(numbers[i]);
      int y1= Integer.parseInt(numbers[i+1]);
      int x2= Integer.parseInt(numbers[i+2]);
      int y2= Integer.parseInt(numbers[i+3]);
      Lines.addElement(new Line(x1, y1, x2, y2));
    }
    for (int i=4*(N-1); i<numbers.length; i+=3)
    {
      int x= Integer.parseInt(numbers[i]);
      int y= Integer.parseInt(numbers[i+1]);
      int label= Integer.parseInt(numbers[i+2]);
      Nodes.addElement(new Node(x, y, label));
    }
  }

  public void createImage(double scale) {
      image= new BufferedImage(1250, 650, BufferedImage.TYPE_INT_RGB);
      Graphics2D g2d = image.createGraphics();
      for (Line line : Lines) {
        g2d.setColor(Color.WHITE);
        g2d.drawLine(line.x1, line.y1, line.x2, line.y2);
      }
      for (Node node : Nodes) {
        drawLabeledNode(g2d, node.x, node.y, (int)(50*scale), Integer.toString(node.label));
      }
  }

  public void drawLabeledNode(Graphics2D g2d, int x, int y, int d, String label)
  {
      g2d.setColor(Color.RED);
      g2d.fillOval (x-d/2, y-d/2, d, d);
      g2d.setFont(new Font("TimesRoman", Font.PLAIN, d*3/5));

      FontMetrics fm = g2d.getFontMetrics();
      java.awt.geom.Rectangle2D rect = fm.getStringBounds(label, g2d);
      int textHeight = (int) (rect.getHeight());
      int textWidth = (int) (rect.getWidth());

      int cornerX = x - (textWidth / 2);
      int cornerY = y - (textHeight / 2) + fm.getAscent();

      g2d.setColor(Color.WHITE);
      g2d.drawString(label, cornerX, cornerY );
  }

  public void updateImage() {
    Lines.clear();
    Nodes.clear();
    getTree();
    double scale= findGoodImgSize();
    createImage(scale);
    repaint();
  }

  public void paint( Graphics g)
  {
    super.paintComponent(g);
    int x= (this.getWidth() - 1250)/2;
    int y= (this.getHeight()-650)/2;
    g.drawImage(this.image, x, y, 1250, 650, null);
  }

  public String readFile (String path) {
    String res="";
    try {
    FileReader file= new FileReader(path);
      try {
        char[] t = new char[1000000];
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

  public void prepareRandomTree() {
    Random rand= new Random();
    int seed= rand.nextInt(1000000);
    int treesize= rand.nextInt(300) + 1;
    try {
      String command= "./generateTree.sh " + seed + " " + treesize;
      final Process p = Runtime.getRuntime().exec(command);
      p.waitFor();
    }
    catch (Exception e)  {}
  }

  public void prepareFileTree(String path) {
    try {
      String command = "./fileTree.sh " + path;
      final Process p= Runtime.getRuntime().exec(command);
      p.waitFor();
    }
    catch (Exception e) {}
  }

  public void prepareInputTree () {
    try {
      String command = "./inputTree.sh";
      final Process p = Runtime.getRuntime().exec(command);
      p.waitFor();
    }
    catch (Exception e) {}
  }

  public void saveCurrentImage () {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save As");

    int userSelection = fileChooser.showSaveDialog(this);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
      File fileToSave = fileChooser.getSelectedFile();
      String path= fileToSave.getAbsolutePath();
      try {
        File outputfile = new File(path);
        ImageIO.write(image, "png", outputfile);
        }
        catch (IOException e) {

        }
    }
  }

  public void saveCurrentTree () {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save As");

    int userSelection = fileChooser.showSaveDialog(this);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
      File fileToSave = fileChooser.getSelectedFile();
      String path= fileToSave.getAbsolutePath();
      try {
        String command = "cp tmpTree.txt " + path;
        final Process p = Runtime.getRuntime().exec(command);
        p.waitFor();
      }
      catch (Exception e) {}
    }
  }

  public void drawInputTree () {
    InputTreeDialog dialog = new InputTreeDialog(this.frame);
    prepareInputTree();
    updateImage();
  }

  public void drawFileTree () {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
    int result = fileChooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      String path= selectedFile.getAbsolutePath();
      prepareFileTree(path);
      updateImage();
    }
  }

  public void drawRandomTree()
  {
    prepareRandomTree();
    updateImage();
  }

}
