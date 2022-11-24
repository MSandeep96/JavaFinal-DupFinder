package DupFinder.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.plaf.DimensionUIResource;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import DupFinder.interfaces.FileDataCallback;
import DupFinder.interfaces.UICallback;

public class SearchScreen extends JFrame implements FileDataCallback {

  UICallback callback;
  JPanel contentPanel;
  JTextField pathField;
  JLabel filesProcessed;
  JLabel duplicatedFiles;
  JLabel timeElapsed;
  DefaultListModel<String> duplicateList;
  DefaultListModel<Md5File> resultsList;

  int numFilesProcessed = 0;
  int numDuplicatedFiles = 0;

  private HashMap<String, LinkedList<String>> fileMap;
  private String currentPath;
  private Instant startTime;

  public SearchScreen(UICallback callback) {
    super("Search");
    this.callback = callback;
    fileMap = new HashMap<String, LinkedList<String>>();
    createUI();
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  public void setCurrentDirectory(String path) {
    startTime = Instant.now();
    numFilesProcessed = 0;
    numDuplicatedFiles = 0;
    currentPath = path;
    fileMap.clear();
    updateStats();
  }

  @Override
  public synchronized void onFileData(String filename, String data) {
    if (fileMap.containsKey(data)) {
      LinkedList<String> list = fileMap.get(data);
      list.add(filename);
      if (list.size() == 2) {
        // make path relative to current directory
        String finalPath = filename.replace(currentPath, "");
        resultsList.addElement(new Md5File(finalPath, data));
      }
      numDuplicatedFiles++;
    } else {
      LinkedList<String> list = new LinkedList<String>();
      list.add(filename);
      fileMap.put(data, list);
    }
    numFilesProcessed++;
    updateStats();
  }

  private void createUI() {
    contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    contentPanel.setBorder(padding);
    add(contentPanel, BorderLayout.CENTER);
    createInputPanel();
    contentPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
    createStatsPanel();
    contentPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
    contentPanel.add(Box.createVerticalGlue());
    // contentPanel.add(new JTextArea());
    createResultsPanel();
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

  private void createStatsPanel() {
    Box timeFileBox = Box.createHorizontalBox();
    timeFileBox.add(new JLabel("Files Processed:"));
    filesProcessed = new JLabel("0");
    timeFileBox.add(filesProcessed);
    timeFileBox.add(Box.createHorizontalGlue());
    timeFileBox.add(new JLabel("Time Elapsed:"));
    timeElapsed = new JLabel("0");
    timeFileBox.add(timeElapsed);
    contentPanel.add(timeFileBox);
    contentPanel.add(Box.createVerticalStrut(10));
    Box dupFoundBox = Box.createHorizontalBox();
    dupFoundBox.add(new JLabel("Duplicate Files:"));
    duplicatedFiles = new JLabel("0");
    dupFoundBox.add(duplicatedFiles);
    dupFoundBox.add(Box.createHorizontalGlue());
    contentPanel.add(dupFoundBox);
    contentPanel.add(Box.createVerticalStrut(10));
  }

  private void createResultsPanel() {
    JPanel resultsPanel = new JPanel();
    resultsPanel.setLayout(new BorderLayout());
    resultsList = new DefaultListModel<>();
    JList<Md5File> resList = new JList<Md5File>(resultsList);
    // add selected listener to resList
    resList.addListSelectionListener(e -> {
      Md5File selected = resList.getSelectedValue();
      if (selected != null && duplicateList != null) {
        duplicateList.clear();
        for (String path : fileMap.get(selected.md5)) {
          duplicateList.addElement(path);
        }
      }
    });
    JPanel resultGrid = new JPanel();
    resultGrid.setLayout(new GridLayout(1, 2));
    JScrollPane scrollPane = new JScrollPane(resList);
    resultGrid.add(scrollPane);
    duplicateList = new DefaultListModel<String>();
    JList<String> duplist = new JList<String>(duplicateList);
    JScrollPane dupScrollPane = new JScrollPane(duplist);
    resultGrid.add(dupScrollPane);
    resultsPanel.add(resultGrid, BorderLayout.CENTER);
    contentPanel.add(resultsPanel);
  }

  private synchronized void updateStats() {
    filesProcessed.setText(Integer.toString(numFilesProcessed));
    duplicatedFiles.setText(Integer.toString(numDuplicatedFiles));
    Instant end = Instant.now();
    Duration timeTaken = Duration.between(startTime, end);
    timeElapsed.setText(Long.toString(timeTaken.toMillis()) + "ms");
  }

  private void pickFolder() {
    JFileChooser chooser = new JFileChooser();
    File workingDirectory = new File(System.getProperty("user.dir"));
    chooser.setCurrentDirectory(workingDirectory);
    chooser.setDialogTitle("Select start folder");
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.setAcceptAllFileFilterUsed(false);
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      pathField.setText(chooser.getSelectedFile().getAbsolutePath());
    } else {
      System.out.println("No Selection ");
    }
  }

  class Md5File {
    String filename;
    String md5;

    public Md5File(String filename, String md5) {
      this.filename = filename;
      this.md5 = md5;
    }

    public String toString() {
      return filename;
    }
  }

}
