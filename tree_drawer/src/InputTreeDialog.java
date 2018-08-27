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


public class InputTreeDialog extends JDialog {

  public JTextArea input;
  public MyFrame frame;

  public InputTreeDialog (MyFrame frame )
  {
    super(frame, true);
    this.frame=frame;
    this.input = new JTextArea(30, 30);
    JScrollPane scrollPane = new JScrollPane(input);
    scrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setPreferredSize(new Dimension(250, 250));

    setTitle("Input your Tree");
    setSize(400, 600);
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(drawButton(), BorderLayout.SOUTH);
    this.add(panel);
    setVisible(true);
  }

  public void inputTree() {
    try {
    FileWriter writer =new FileWriter("InputTree.txt",false);
    input.write(writer);
    this.dispose();
    }
    catch (Exception e) {}
  }

  public JButton drawButton () {
    JButton button= new JButton("Draw");
    button.setFocusPainted(false);
    button.setBackground(new Color(100, 100, 100));
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        inputTree();
      }
    });
    return button;
  }


}
