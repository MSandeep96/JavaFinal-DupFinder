package DupFinder.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import java.awt.BorderLayout;

import DupFinder.interfaces.UICallback;

public class SearchScreen extends JFrame {

  UICallback callback;

  public SearchScreen(UICallback callback) {
    super("Search");
    this.callback = callback;
    createUI();
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  private void createUI() {
    createInputPanel();
    createStatsPanel();
  }

  private void createInputPanel() {
    JPanel inputContainer = new JPanel();
    inputContainer.setLayout(new BoxLayout(inputContainer, BoxLayout.Y_AXIS));
    Box box = Box.createHorizontalBox();
    box.add(new JLabel("Start Path:"));
    box.add(Box.createHorizontalGlue());
    inputContainer.add(box);
    inputContainer.add(Box.createVerticalStrut(10));
    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
    JTextField startPath = new JTextField();
    inputPanel.add(startPath);
    JButton startButton = new JButton("Start");
    inputPanel.add(startButton);
    startButton.addActionListener(e -> callback.startSearch(startPath.getText()));
    inputContainer.add(inputPanel);
    Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    inputContainer.setBorder(padding);
    add(inputContainer, BorderLayout.NORTH);
  }

}
