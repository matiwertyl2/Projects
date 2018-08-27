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



public class Manager extends JPanel
{
  public MyFrame frame;
  public TreeCanvas treeCanvas;
  public JButton drawRandomTreeButton;
  public JButton drawFileTreeButton;
  public JButton saveCurrentImageButton;
  public JButton drawInputTreeButton;
  public JButton saveCurrentTreeButton;

  public Manager (MyFrame frame)
  {
    this.frame=frame;
    this.treeCanvas= new TreeCanvas(frame);
    this.treeCanvas.setAlignmentY(JComponent.CENTER_ALIGNMENT);
    this.drawRandomTreeButton= drawRandomTreeButton();
    this.drawFileTreeButton= drawFileTreeButton();
    this.drawInputTreeButton= drawInputTreeButton();
    this.saveCurrentImageButton  = saveCurrentImageButton();
    this.saveCurrentTreeButton = saveCurrentTreeButton();
    this.setLayout(new BorderLayout(30, 30));
    this.add(treeCanvas, BorderLayout.CENTER);
    JPanel menu= new JPanel();
    menu.setLayout(new GridLayout(1, 0));
    menu.add(drawRandomTreeButton);
    menu.add(drawFileTreeButton);
    menu.add(drawInputTreeButton);
    menu.add(saveCurrentTreeButton);
    menu.add(saveCurrentImageButton);
    this.add(menu, BorderLayout.SOUTH);
  }


  public JButton drawRandomTreeButton () {
    JButton button= new JButton("Draw Random Tree");
    button.setFocusPainted(false);
    button.setBackground(new Color(100, 100, 100));
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        treeCanvas.drawRandomTree();
      }
    });
    return button;
  }

  public JButton drawInputTreeButton () {
    JButton button= new JButton("Draw Tree from File");
    button.setFocusPainted(false);
    button.setBackground(new Color(100, 100, 100));
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        treeCanvas.drawFileTree();
      }
    });
    return button;
  }

  public JButton drawFileTreeButton () {
    JButton button= new JButton("Draw Input Tree");
    button.setFocusPainted(false);
    button.setBackground(new Color(100, 100, 100));
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        treeCanvas.drawInputTree();
      }
    });
    return button;
  }


  public JButton saveCurrentImageButton () {
    JButton button= new JButton("Save Current Image");
    button.setFocusPainted(false);
    button.setBackground(new Color(100, 100, 100));
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        treeCanvas.saveCurrentImage();
      }
    });
    return button;
  }

  public JButton saveCurrentTreeButton () {
    JButton button= new JButton("Save Current Tree");
    button.setFocusPainted(false);
    button.setBackground(new Color(100, 100, 100));
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        treeCanvas.saveCurrentTree();
      }
    });
    return button;
  }
}
