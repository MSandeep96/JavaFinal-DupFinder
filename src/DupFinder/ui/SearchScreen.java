package DupFinder.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.plaf.DimensionUIResource;

import java.awt.BorderLayout;

import DupFinder.interfaces.UICallback;

public class SearchScreen extends JFrame {

  UICallback callback;
  JPanel contentPanel;
  JTextField pathField;

  public SearchScreen(UICallback callback) {
    super("Search");
    this.callback = callback;
    createUI();
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  private void createUI() {
    contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    contentPanel.setBorder(padding);
    add(contentPanel, BorderLayout.CENTER);
    createInputPanel();
  }

  private void createInputPanel() {
    Box inputHelperBox = Box.createHorizontalBox();
    inputHelperBox.add(new JLabel("Start Path:"));
    inputHelperBox.add(Box.createHorizontalGlue());
    JButton browseButton = new JButton("Browse");
    browseButton.addActionListener(e -> pickFolder());
    inputHelperBox.add(browseButton);
    contentPanel.add(inputHelperBox);
    contentPanel.add(Box.createVerticalStrut(10));
    Box inputPanel = Box.createHorizontalBox();
    inputPanel.setMaximumSize(new DimensionUIResource(Integer.MAX_VALUE, 25));
    pathField = new JTextField();
    inputPanel.add(pathField);
    JButton startButton = new JButton("Start");
    inputPanel.add(startButton);
    startButton.addActionListener(e -> callback.startSearch(pathField.getText()));
    contentPanel.add(inputPanel);
  }

  private void pickFolder() {
    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(new java.io.File("."));
    chooser.setDialogTitle("Select start folder");
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.setAcceptAllFileFilterUsed(false);
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      pathField.setText(chooser.getSelectedFile().getAbsolutePath());
    } else {
      System.out.println("No Selection ");
    }
  }

}
