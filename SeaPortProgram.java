/**
 * @author William Kendall
 * @filename SeaPortProgram.java
 * @date 9/17/2017
 * 
 *       This is the main program class In creates the GUI, and has functions for the two GUI
 *       buttons. The first button will allow the user to open a file the second will allow a user
 *       to search the stored list. The user can also sort the que of ships. Once the sort button is
 *       clicked, than the full list of objects will be displayed
 */


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import javax.swing.JDialog;

public class SeaPortProgram extends JFrame {
  private static final long serialVersionUID = 1L;

  private JFrame frame = new JFrame("SeaPort - William Kendall");

  private JTextField txtFile;
  private JTextField txtSearchName;
  private JTextField txtSearchIndex;
  private JTextField txtSearchSkill;
  private JTextArea txtrResults;
  private JCheckBox chkShowChildren;
  private JTree treeResults;

  // job table
  JTable tblJobs;
  JTable tblWorkers;
  boolean programClose = false;

  // sort buttons
  JRadioButton rbSortName;
  JRadioButton rbSortWeight;
  JRadioButton rbSortLength;
  JRadioButton rbSortWidth;
  JRadioButton rbSortDraft;

  // file open
  JButton btnOpenFile;


  // This object stores things read in from the input file
  private World world;


  // class needed to display progress bars in JTable
  public class ProgressCell extends JProgressBar implements TableCellRenderer {
    // http://fahdshariff.blogspot.jp/2009/12/adding-jprogressbar-to-jtable-cell.html
    private static final long serialVersionUID = 1L;

    // let a table cell have a progress bar
    public ProgressCell() {
      super(0, 100);
      setValue(0);
      setString("0%");
      setStringPainted(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column) {

      // value is a percentage e.g. 95%
      final String sValue = value.toString();
      int index = sValue.indexOf('%');
      if (index != -1) {
        int p = 0;
        try {
          p = Integer.parseInt(sValue.substring(0, index));
        } catch (NumberFormatException e) {
        }
        setValue(p);
        setString(sValue);
      }
      return this;
    }
  }

  /**
   * readFile sets the world object from a file description of the object if the file could not be
   * loaded, world will be NULL
   * 
   * @param file String name of file to load
   */
  public void readFile(String file) {

    world = null;
    HashMap<Integer, Thing> things = new HashMap<Integer, Thing>();

    // clear job row
    DefaultTableModel cmodel = (DefaultTableModel) this.tblJobs.getModel();
    while (cmodel.getRowCount() > 0) {
      cmodel.removeRow(0);
    }


    // open file
    String goodData = "";
    // open file and clean it up
    try (BufferedReader read = new BufferedReader(new FileReader(file))) {

      for (String line; (line = read.readLine()) != null;) {
        line = line.trim();
        // first two chars are // or is empty then continue
        if (line.matches("[/\\\\].*$") || line.contentEquals(""))
          continue;
        // add good data to the string
        goodData += line + "\n";
      }

      // clean up
      read.close();


    } catch (FileNotFoundException file_error) {
      JOptionPane.showMessageDialog(null, "File Not Found:\n" + file, "File Not Found",
          JOptionPane.INFORMATION_MESSAGE);
      return;
    } catch (IOException file_error) {
      JOptionPane.showMessageDialog(null, file_error.toString(), "File Error",
          JOptionPane.INFORMATION_MESSAGE);
      return;
    }

    // parse input, create world objects
    Scanner inputData = new Scanner(goodData);
    Scanner line;
    while (inputData.hasNextLine()) {
      line = new Scanner(inputData.nextLine());
      switch (line.next().toLowerCase()) {
        case "world":
          world = new World(line);
          things.put(world.index, world);
          break;
        case "port": {
          SeaPort t = new SeaPort(line);
          things.put(t.index, t);
          break;
        }
        case "dock": {
          Dock t = new Dock(line);
          things.put(t.index, t);
          break;
        }
        case "ship": {
          Ship t = new Ship(line);
          things.put(t.index, t);
          break;
        }
        case "pship": {
          PassengerShip t = new PassengerShip(line);
          things.put(t.index, t);
          break;
        }
        case "cship": {
          CargoShip t = new CargoShip(line);
          things.put(t.index, t);
          break;
        }
        case "person": {
          Person t = new Person(line);
          things.put(t.index, t);
          DefaultTableModel model = (DefaultTableModel) this.tblWorkers.getModel();
          model.addRow(new Object[] {t.name, t.skill, "Port", ""});
          break;
        }
        case "job": {
          Job t = new Job(line);
          things.put(t.index, t);

          DefaultTableModel wModel = (DefaultTableModel) this.tblWorkers.getModel();

          DefaultTableModel model = (DefaultTableModel) this.tblJobs.getModel();
          model.addRow(new Object[] {t.name, t.requirements, "0%", false, false});
          // listen for any changes
          t.progressListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              Job job = (Job) e.getSource();
              // get the table row
              int jRow = 0;
              for (int row = 0; row < model.getRowCount(); row++) {
                if (job.name.compareToIgnoreCase((String) model.getValueAt(row, 0)) == 0) {
                  jRow = row;
                  break;
                }
              }

              switch (e.getActionCommand()) {
                case "checkStatus":
                  // pause job?
                  while ((boolean) model.getValueAt(jRow, 3)) {
                    try {
                      Thread.sleep(100);
                    } catch (InterruptedException e1) {
                    }

                    // canceled
                    if ((boolean) model.getValueAt(jRow, 4))
                      break;
                  }

                  // cancel job?
                  if ((boolean) model.getValueAt(jRow, 4)) {
                    // ask

                    int con = JOptionPane.showConfirmDialog(null,
                        "Do you want to cancel job: " + job.name, "Cancel?",
                        JOptionPane.YES_NO_OPTION);
                    if (con == JOptionPane.YES_OPTION) {
                      job.cancelJob();
                      synchronized (world) {
                        world.delete(job);
                        model.removeRow(jRow);
                        updateInformation();
                      } // end sync
                      return;
                    } else {
                      synchronized (world) {
                         model.setValueAt(false, jRow, 4);
                      }
                    }
                  }

                  // cancel all jobs
                  if (programClose) {
                    job.cancelJob();
                    return;
                  }

                  break;

                case "canceled":
                  synchronized (world) {
                    world.delete(job);
                    model.removeRow(jRow);
                    updateInformation();
                  }
                  break;

                case "update":
                  // find row, update progress
                  synchronized (world) {
                    model.setValueAt(((Integer) e.getModifiers()).toString() + "%", jRow, 2);
                  }

                  break;

                case "finished": {
                  synchronized (world) {
                    world.delete(job);
                    updateInformation();
                  }
                  break;
                }
                case "gotWorkers": {

                  for (Person p : job.workersOnTheJob) {
                    for (int row = 0; row < wModel.getRowCount(); row++) {
                      if (p.name.compareToIgnoreCase((String) wModel.getValueAt(row, 0)) == 0) {
                        wModel.setValueAt(job.parent.name, row, wModel.findColumn("Location"));
                        wModel.setValueAt(job.name, row, wModel.findColumn("Job"));
                      }
                    }
                  }
                  break;
                }
                case "releaseWorkers": {
                  for (Person p : job.workersOnTheJob) {
                    for (int row = 0; row < wModel.getRowCount(); row++) {
                      if (p.name.compareToIgnoreCase((String) wModel.getValueAt(row, 0)) == 0) {
                        wModel.setValueAt("Port", row, wModel.findColumn("Location"));
                        wModel.setValueAt("", row, wModel.findColumn("Job"));
                      }
                    }
                  }
                  break;
                }

              }// end switch

            }// end action performed
          };
          break;
        }
      }// end switch
    } // end while

    // done with the file
    inputData.close();


    // project 2: make linked items use a hash map
    // we still need to iterate because objects may not exist when reading the file

    things.forEach((k, v) -> {
      if (k == 0)
        return; // world object
      if (things.containsKey(v.parentIndex)) {
        // found parent, add thing
        things.get(v.parentIndex).add(v);
        // ships in dock need added to the port
        if (v instanceof Ship) {
          Thing dockPort = things.get(v.parentIndex);
          if (dockPort instanceof Dock) {
            // project2 mistake add and move!
            things.get(dockPort.parentIndex).add(v);
          }
        }
      }
    });


    /*
     * note:these loops are not needed. It is just that the data file is not in order, so things do
     * not exist when trying to add
     */
    // project 3 move to docks

    things.forEach((k, v) -> {
      if (k == 0)
        return; // world object
      if (things.containsKey(v.parentIndex)) {
        // ships in dock need added to the port
        if (v instanceof Ship) {
          Thing dockPort = things.get(v.parentIndex);
          if (dockPort instanceof Dock) {
            // seaports are the index parents of docks
            ((SeaPort) things.get(dockPort.parentIndex)).moveShipFromQueueToDock((Ship) v,
                (Dock) dockPort);
          }
        }
      }
    });
  }

  // sort que
  ActionListener performSort = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      // check if data is loaded
      if (world == null) {
        txtrResults.setText("No data loaded.\nPlease Select a data file");
        return;
      }

      Thing.ThingComparator comp = Thing.ThingComparators.NAME_SORT;

      if (rbSortName.isSelected())
        comp = Thing.ThingComparators.NAME_SORT;
      if (rbSortWeight.isSelected())
        comp = Ship.ShipComparators.SHIP_WEIGHT_SORT;
      if (rbSortLength.isSelected())
        comp = Ship.ShipComparators.SHIP_LENGTH_SORT;
      if (rbSortWidth.isSelected())
        comp = Ship.ShipComparators.SHIP_WIDTH_SORT;
      if (rbSortDraft.isSelected())
        comp = Ship.ShipComparators.SHIP_DRAFT_SORT;


      HashMap<Integer, Thing> things = new HashMap<Integer, Thing>();
      world.getItems(things);
      // sort ALL THE THINGS (really only seaport implements sort for que)
      Iterator<Entry<Integer, Thing>> i = things.entrySet().iterator();
      while (i.hasNext()) {
        i.next().getValue().Sort(comp, Thing.ThingComparators.NAME_SORT);
        // System.out.println(i.next().getValue().name);
      }
      // display the whole world
      txtrResults.setText(world.toString(0));
    }
  };

  // search button clicked
  ActionListener performSearch = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      // check if data is loaded
      if (world == null) {
        txtrResults.setText("No data loaded.\nPlease Select a data file");
        return;
      }

      // Project 2 search using lots of hash maps
      HashMap<Integer, Thing> allItems = new HashMap<Integer, Thing>();
      ArrayList<Thing> foundItems = new ArrayList<Thing>();
      String sName = txtSearchName.getText().trim();
      String sSkill = txtSearchSkill.getText().trim();
      boolean bName = sName.compareTo("") != 0 ? true : false;
      boolean bSkill = sSkill.compareTo("") != 0 ? true : false;
      boolean bIndex = txtSearchIndex.getText().trim().matches("[0-9]+");
      int sIndex = bIndex ? Integer.parseInt(txtSearchIndex.getText()) : -1;

      world.getItems(allItems);

      allItems.forEach((k, v) -> {
        if (bName && v.name.compareToIgnoreCase(sName) == 0 || bIndex && v.index == sIndex
            || bSkill && v instanceof Person && ((Person) v).skill.compareTo(sSkill) == 0)
          foundItems.add(v);
      });

      String searchResult = "";
      for (Thing t : foundItems) {
        searchResult += chkShowChildren.isSelected() ? t.toString(0) : t.toString();
        searchResult += "\n\n";
      }



      // build dialog
      JDialog dialog = new JDialog();
      dialog.setTitle("Search Results");
      dialog.setLocationByPlatform(true);
      JScrollPane sp = new JScrollPane();
      dialog.add(sp);
      JTextArea txtArea = new JTextArea(10, 30);
      txtArea.setAutoscrolls(true);
      txtArea.setText(searchResult.length() > 0 ? searchResult : "Item not Found");
      sp.setViewportView(txtArea);
      dialog.pack();
      dialog.setVisible(true);

    }
  };

  // user wants to select a file.
  ActionListener performFilePicker = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      // show the open file dialog
      JFileChooser filePicker = new JFileChooser(".");
      int pickerReturn = filePicker.showOpenDialog(SeaPortProgram.this);
      if (pickerReturn == JFileChooser.APPROVE_OPTION) {
        // user selected a file
        File file = filePicker.getSelectedFile();
        txtFile.setText(file.getName());
        readFile(file.getName());
      }

      // world is null if file error
      if (world == null) {
        txtrResults.setText("World was not loaded.");
        return;
      }

      btnOpenFile.setEnabled(false);
      updateInformation();
    }
  };


  public void updateInformation() {
    world.update();
    txtrResults.setText(world.toString(0));
    // project3
    // add tree model
    DefaultTreeModel model = (DefaultTreeModel) treeResults.getModel();
    model.setRoot(this.world.buildTree());
    model.reload();

    // expand tree
    int j = treeResults.getRowCount();
    int i = 0;
    while (i < j) {
      treeResults.expandRow(i);
      i += 1;
      j = treeResults.getRowCount();
    }
  }

  public SeaPortProgram() {
    // build the GUI

    JPanel pnlMain = new JPanel();
    frame.getContentPane().add(pnlMain, BorderLayout.CENTER);
    pnlMain.setLayout(new BorderLayout());

    // Actions
    {
      JPanel pnlAction = new JPanel();
      pnlMain.add(pnlAction, BorderLayout.WEST);
      pnlAction.setLayout(new GridBagLayout());

      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.weighty = 1.0;

      pnlAction.setBorder(
          new TitledBorder(null, "actions", TitledBorder.LEADING, TitledBorder.TOP, null, null));

      // file opener
      {
        JPanel pnlFileOpen = new JPanel();
        pnlFileOpen.setBorder(
            new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        pnlFileOpen.setLayout(new BoxLayout(pnlFileOpen, BoxLayout.PAGE_AXIS));


        pnlAction.add(pnlFileOpen, gbc);
        btnOpenFile = new JButton("OpenFile");
        pnlFileOpen.add(btnOpenFile);

        txtFile = new JTextField();
        txtFile.setEditable(false);

        txtFile.setText("Please Select a File");

        pnlFileOpen.add(txtFile);
        txtFile.setColumns(10);

        btnOpenFile.addActionListener(performFilePicker);
      }


      // new sort panel
      {
        JPanel pnlSort = new JPanel();
        gbc.gridy = 1;
        pnlAction.add(pnlSort, gbc);
        pnlSort.setLayout(new BoxLayout(pnlSort, BoxLayout.PAGE_AXIS));

        pnlSort.setBorder(new TitledBorder(null, "Sort Que By", TitledBorder.LEADING,
            TitledBorder.TOP, null, null));


        // sort radios
        ButtonGroup bgSort = new ButtonGroup();
        rbSortName = new JRadioButton("Name");
        rbSortWeight = new JRadioButton("Weight");
        rbSortLength = new JRadioButton("Length");
        rbSortWidth = new JRadioButton("Width");
        rbSortDraft = new JRadioButton("Draft");
        bgSort.add(rbSortName);
        bgSort.add(rbSortWeight);
        bgSort.add(rbSortLength);
        bgSort.add(rbSortWidth);
        bgSort.add(rbSortDraft);
        pnlSort.add(rbSortName);
        pnlSort.add(rbSortWeight);
        pnlSort.add(rbSortLength);
        pnlSort.add(rbSortWidth);
        pnlSort.add(rbSortDraft);
        rbSortName.setSelected(true);

        JButton btnSort = new JButton("Sort");
        pnlSort.add(btnSort);
        btnSort.addActionListener(performSort);
      }

      // search panel
      {
        JPanel pnlSearch = new JPanel();
        gbc.gridy = 2;

        pnlAction.add(pnlSearch, gbc);

        pnlSearch.setBorder(
            new TitledBorder(null, "Search", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        pnlSearch.setLayout(new BoxLayout(pnlSearch, BoxLayout.PAGE_AXIS));

        JLabel lblSeachName = new JLabel("Name");
        pnlSearch.add(lblSeachName);

        txtSearchName = new JTextField();
        pnlSearch.add(txtSearchName);
        txtSearchName.setColumns(10);

        JLabel lblSearchIndex = new JLabel("Index");
        pnlSearch.add(lblSearchIndex);

        txtSearchIndex = new JTextField();
        pnlSearch.add(txtSearchIndex);
        txtSearchIndex.setColumns(10);

        JLabel lblSearchSkill = new JLabel("Skill");
        pnlSearch.add(lblSearchSkill);
        txtSearchSkill = new JTextField();
        pnlSearch.add(txtSearchSkill);
        txtSearchSkill.setColumns(10);

        JButton btnSearch = new JButton("Search");

        pnlSearch.add(btnSearch);

        chkShowChildren = new JCheckBox("ShowChildren");
        chkShowChildren.setSelected(true);

        pnlSearch.add(chkShowChildren);

        // search button actions
        btnSearch.addActionListener(performSearch);
      }

      gbc.gridy = 3;
      gbc.weighty = 100;
      pnlAction.add(Box.createGlue(), gbc);

    } // end actions panel


    // Information Panel
    {
      JPanel pnlInfo = new JPanel();
      pnlMain.add(pnlInfo, BorderLayout.CENTER);
      pnlInfo.setLayout(new BorderLayout());
      pnlInfo.setBorder(
          new TitledBorder(null, "info", TitledBorder.LEADING, TitledBorder.TOP, null, null));

      // JTree
      {
        JScrollPane treeScrollPane = new JScrollPane();
        treeScrollPane.setPreferredSize(new Dimension(220, 100));
        pnlInfo.add(treeScrollPane, BorderLayout.LINE_START);

        treeResults = new JTree(new DefaultMutableTreeNode("Please Select A File"));
        treeScrollPane.setViewportView(treeResults);
        treeResults.setFont(new java.awt.Font("Monospaced", 0, 12));
      }

      // center container
      JPanel pnlICenter = new JPanel();
      pnlInfo.add(pnlICenter, BorderLayout.CENTER);
      pnlICenter.setLayout(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.fill = GridBagConstraints.BOTH;
      gbc.anchor = GridBagConstraints.NORTH;
      gbc.weighty = 5.0;
      gbc.weightx = 1;

      // results panel setup
      JScrollPane scrollPane = new JScrollPane();// textresults scroll panel
      pnlICenter.add(scrollPane, gbc);
      txtrResults = new JTextArea();
      txtrResults.setEditable(false);
      txtrResults.setFont(new java.awt.Font("Monospaced", 0, 12));
      scrollPane.setViewportView(txtrResults);

      gbc.weighty = 1.0;
      gbc.gridy = 1;
      // worker panel
      {
        JPanel pnlWorker = new JPanel();

        pnlICenter.add(pnlWorker, gbc);

        pnlWorker.setBorder(
            new TitledBorder(null, "Workers", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        pnlWorker.setLayout(new BoxLayout(pnlWorker, BoxLayout.PAGE_AXIS));


        String[] tblCNames = {"Worker", "Skill", "Location", "Job"};

        Object[][] tblData = {};
        DefaultTableModel model = new DefaultTableModel(tblData, tblCNames);

        tblWorkers = new JTable() {
          private static final long serialVersionUID = 1L;

          @Override
          public boolean isCellEditable(int row, int col) {
            return false;
          }
        };


        tblWorkers.setModel(model);
        tblWorkers.setFont(new java.awt.Font("Monospaced", 0, 12));

        pnlWorker.add(tblWorkers.getTableHeader());

        JScrollPane sp = new JScrollPane();
        pnlWorker.add(sp);
        sp.setViewportView(tblWorkers);
      }

      gbc.weighty = 1.0;
      gbc.gridy = 2;
      // job progress table
      {
        JPanel pnlJProgress = new JPanel();
        pnlICenter.add(pnlJProgress, gbc);
        pnlJProgress.setLayout(new BorderLayout());


        pnlJProgress.setBorder(
            new TitledBorder(null, "Jobs", TitledBorder.LEADING, TitledBorder.TOP, null, null));



        String[] tblCNames = {"Job", "Requirements", "Progress", "Pause", "Cancel"};

        Object[][] tblData = {};
        DefaultTableModel model = new DefaultTableModel(tblData, tblCNames);


        tblJobs = new JTable() {
          private static final long serialVersionUID = 1L;

          @Override
          public boolean isCellEditable(int row, int col) {
            if (col < 3)
              return false;
            else
              return true;
          }
        };


        tblJobs.setModel(model);
        tblJobs.setFont(new java.awt.Font("Monospaced", 0, 12));

        // set progress bar column 1
        TableColumn proCol = tblJobs.getColumnModel().getColumn(2);
        proCol.setCellRenderer(new ProgressCell());

        // check boxes
        TableColumn tc = tblJobs.getColumnModel().getColumn(3);
        tc.setCellEditor(tblJobs.getDefaultEditor(Boolean.class));
        tc.setCellRenderer(tblJobs.getDefaultRenderer(Boolean.class));
        tc = tblJobs.getColumnModel().getColumn(4);
        tc.setCellEditor(tblJobs.getDefaultEditor(Boolean.class));
        tc.setCellRenderer(tblJobs.getDefaultRenderer(Boolean.class));

        pnlJProgress.add(tblJobs.getTableHeader(), BorderLayout.PAGE_START);

        JScrollPane sp = new JScrollPane();
        pnlJProgress.add(sp, BorderLayout.CENTER);
        sp.setViewportView(tblJobs);
      }

    } // information panel end


    // window setup
    frame.getContentPane().setPreferredSize(new Dimension(720, 450));
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        programClose = true;
      }
    });

    // Pack and Show
    frame.pack();
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    // call constructor for GUI
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        new SeaPortProgram();
      }
    });

  }
}
