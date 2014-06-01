package hanzidict;

import hanzilookup.HanziLookup;
import hanzilookup.HanziLookup.CharacterSelectionEvent;
import hanzilookup.HanziLookup.CharacterSelectionListener;
import hanzilookup.i18n.HanziLookupBundleKeys;
import hanzilookup.ui.HanziLookupUIBuilder;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.metal.MetalIconFactory;
import kiang.chinese.font.ChineseFontFinder;

public class HanziDict
  extends JApplet
  implements HanziLookup.CharacterSelectionListener
{
  private static final String RESOURCE_DICTIONARY_PREF_KEY = "resource_dictionary_pref";
  private static final String FILE_DICTIONARY_PREF_KEY = "file_dictionary_pref";
  private static final String USING_RESOURCE_DICTIONARY_PREF_KEY = "using_resource_dictionary_pref";
  private static final String FONT_PREF_KEY = "font";
  private static final String SAVE_OPTIONS_PREF = "save_options";
  private static final String DEFAULT_RESOURCE_DICTIONARY_PATH = "cedict_ts.u8";
  private static final String STROKE_DATA = "/strokes.dat";
  private HanziLookup lookupPanel;
  private JEditorPane definitionTextPane;
  private CharacterDictionary dictionary;
  private String resourceDictionaryPath = "cedict_ts.u8";
  private String fileDictionaryPath = "";
  private boolean usingResourceDictionary = true;
  private Font font;
  private Preferences prefs;
  private ResourceBundle bundle = HanziLookupBundleKeys.DEFAULT_ENGLISH_BUNDLE;
  
  public void init()
  {
    if (null != this.prefs) {
      loadDictionaryOptionsFromPreferences(this.prefs);
    }
    if (null == this.font) {
      this.font = ChineseFontFinder.getChineseFont();
    }
    this.lookupPanel = buildLookupPanel(this.font);
    if (null != this.prefs) {
      this.lookupPanel.loadOptionsFromPreferences(this.prefs);
    }
    JComponent localJComponent = buildDefinitionPane(this.font);
    Container localContainer = getContentPane();
    localContainer.setLayout(new BorderLayout());
    localContainer.add(this.lookupPanel, "West");
    localContainer.add(localJComponent, "Center");
    try
    {
      if (this.usingResourceDictionary) {
        loadResourceDictionary(this.resourceDictionaryPath, null);
      } else {
        loadFileDictionary(this.fileDictionaryPath, null);
      }
    }
    catch (IOException localIOException)
    {
      JOptionPane.showMessageDialog(this, localIOException.getMessage());
    }
    setJMenuBar(buildMenuBar());
  }
  
  private void loadDictionaryOptionsFromPreferences(Preferences paramPreferences)
  {
    this.resourceDictionaryPath = paramPreferences.get("resource_dictionary_pref", "cedict_ts.u8");
    InputStream localInputStream = HanziLookup.class.getResourceAsStream(this.resourceDictionaryPath);
    if (null == localInputStream) {
      this.resourceDictionaryPath = "cedict_ts.u8";
    } else {
      try
      {
        localInputStream.close();
      }
      catch (IOException localIOException) {}
    }
    this.fileDictionaryPath = paramPreferences.get("file_dictionary_pref", "");
    this.usingResourceDictionary = paramPreferences.getBoolean("using_resource_dictionary_pref", true);
    String str = paramPreferences.get("font", null);
    if (null != str) {
      this.font = new Font(str, 0, 24);
    }
  }
  
  private void writeOptionsToPreferences(Preferences paramPreferences)
  {
    paramPreferences.put("resource_dictionary_pref", this.resourceDictionaryPath);
    paramPreferences.put("file_dictionary_pref", this.fileDictionaryPath);
    paramPreferences.putBoolean("using_resource_dictionary_pref", this.usingResourceDictionary);
    paramPreferences.put("font", this.lookupPanel.getFont().getName());
    this.lookupPanel.writeOptionsToPreferences(paramPreferences);
  }
  
  private void loadResourceDictionary(String paramString, ChangeListener paramChangeListener)
    throws IOException
  {
    final URL localURL = getClass().getResource(paramString);
    if (null == localURL) {
      throw new MissingResourceException("Can't find resource: " + paramString, getClass().getName(), paramString);
    }
    loadDictionary(new CEDICTCharacterDictionary.CEDICTStreamProvider()
    {
      private final URL val$resourceURL;
      
      public InputStream getCEDICTStream()
        throws IOException
      {
        return localURL.openStream();
      }
    }, paramChangeListener);
    this.resourceDictionaryPath = paramString;
    this.usingResourceDictionary = true;
  }
  
  private void loadFileDictionary(String paramString, ChangeListener paramChangeListener)
    throws IOException
  {
    final File localFile = new File(paramString);
    if (!localFile.canRead()) {
      throw new IOException("Can't read from the specified file: " + paramString);
    }
    loadDictionary(new CEDICTCharacterDictionary.CEDICTStreamProvider()
    {
      private final File val$file;
      
      public InputStream getCEDICTStream()
        throws IOException
      {
        return new FileInputStream(localFile);
      }
    }, paramChangeListener);
    this.fileDictionaryPath = paramString;
    this.usingResourceDictionary = false;
  }
  
  private void loadDictionary(CEDICTCharacterDictionary.CEDICTStreamProvider paramCEDICTStreamProvider, ChangeListener paramChangeListener)
    throws IOException
  {
    this.dictionary = new CEDICTCharacterDictionary(paramCEDICTStreamProvider, paramChangeListener);
  }
  
  private HanziLookup buildLookupPanel(Font paramFont)
  {
    try
    {
      HanziLookup localHanziLookup = new HanziLookup("/strokes.dat", paramFont);
      localHanziLookup.addCharacterReceiver(this);
      return localHanziLookup;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
      JOptionPane.showMessageDialog(this, "Error reading in strokes data!", "Error", 0);
      System.exit(1);
    }
    return null;
  }
  
  private JComponent buildDefinitionPane(Font paramFont)
  {
    this.definitionTextPane = new JEditorPane();
    this.definitionTextPane.setContentType("text/html; charset=UTF-8");
    this.definitionTextPane.setEditable(false);
    JScrollPane localJScrollPane = new JScrollPane(this.definitionTextPane);
    localJScrollPane.setVerticalScrollBarPolicy(22);
    localJScrollPane.setPreferredSize(new Dimension(300, 300));
    localJScrollPane.setMinimumSize(new Dimension(10, 10));
    localJScrollPane.setBorder(BorderFactory.createTitledBorder("Info"));
    return localJScrollPane;
  }
  
  private JMenuBar buildMenuBar()
  {
    JMenuBar localJMenuBar = new JMenuBar();
    ArrayList localArrayList = new ArrayList(1);
    localArrayList.add(this);
    JMenu localJMenu = HanziLookupUIBuilder.buildOptionsMenu(this.lookupPanel, localArrayList, this.bundle);
    try
    {
      JMenuItem localJMenuItem = buildDictionaryChooserMenuItem();
      localJMenu.add(localJMenuItem);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    localJMenuBar.add(localJMenu);
    return localJMenuBar;
  }
  
  private JMenuItem buildDictionaryChooserMenuItem()
  {
    JMenuItem localJMenuItem = new JMenuItem("Choose Dictionary");
    localJMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        JDialog localJDialog = HanziDict.this.buildDictionaryChooserDialog();
        HanziLookupUIBuilder.setChildComponentPosition(HanziDict.this, localJDialog);
        localJDialog.setVisible(true);
      }
    });
    return localJMenuItem;
  }
  
  private JDialog buildDictionaryChooserDialog()
  {
    final JDialog localJDialog = new JDialog();
    localJDialog.setTitle("Choose Dictionary");
    localJDialog.setDefaultCloseOperation(2);
    Container localContainer = localJDialog.getContentPane();
    localJDialog.setLayout(new GridBagLayout());
    GridBagConstraints localGridBagConstraints = new GridBagConstraints();
    ButtonGroup localButtonGroup = new ButtonGroup();
    final JRadioButton localJRadioButton1 = new JRadioButton("Resource:");
    final JTextField localJTextField1 = new JTextField(20);
    localJTextField1.setText(this.resourceDictionaryPath);
    localJRadioButton1.setSelected(this.usingResourceDictionary);
    localButtonGroup.add(localJRadioButton1);
    localGridBagConstraints.weightx = 0.0D;
    localGridBagConstraints.gridx = 0;
    localGridBagConstraints.gridy = 0;
    localGridBagConstraints.gridwidth = 1;
    localGridBagConstraints.fill = 0;
    localContainer.add(localJRadioButton1, localGridBagConstraints);
    localGridBagConstraints.weightx = 1.0D;
    localGridBagConstraints.gridx = 1;
    localGridBagConstraints.gridy = 0;
    localGridBagConstraints.gridwidth = 2;
    localGridBagConstraints.fill = 2;
    localContainer.add(localJTextField1, localGridBagConstraints);
    JRadioButton localJRadioButton2 = new JRadioButton("File:");
    final JTextField localJTextField2 = new JTextField(20);
    localJTextField2.setText(this.fileDictionaryPath);
    boolean bool = false;
    try
    {
      System.getSecurityManager().checkRead(this.fileDictionaryPath);
      bool = true;
    }
    catch (Exception localException1)
    {
      try
      {
        bool = FileSystemView.getFileSystemView().getHomeDirectory().canRead();
      }
      catch (Exception localException2) {}
    }
    localJRadioButton2.setSelected(!this.usingResourceDictionary);
    localJRadioButton2.setEnabled(bool);
    localButtonGroup.add(localJRadioButton2);
    localGridBagConstraints.weightx = 0.0D;
    localGridBagConstraints.gridx = 0;
    localGridBagConstraints.gridy = 1;
    localGridBagConstraints.gridwidth = 1;
    localGridBagConstraints.fill = 2;
    localContainer.add(localJRadioButton2, localGridBagConstraints);
    localJTextField2.setText(this.fileDictionaryPath);
    localJTextField2.setEnabled(bool);
    localGridBagConstraints.weightx = 1.0D;
    localGridBagConstraints.gridx = 1;
    localGridBagConstraints.gridy = 1;
    localGridBagConstraints.gridwidth = 2;
    localGridBagConstraints.fill = 2;
    localContainer.add(localJTextField2, localGridBagConstraints);
    JButton localJButton1 = new JButton(MetalIconFactory.getTreeFolderIcon());
    localJButton1.setEnabled(bool);
    localJButton1.setMargin(new Insets(0, 0, 0, 0));
    if (bool) {
      localJButton1.addActionListener(new ActionListener()
      {
        private final JTextField val$filePathField;
        
        public void actionPerformed(ActionEvent paramAnonymousActionEvent)
        {
          JFileChooser localJFileChooser = new JFileChooser(localJTextField2.getText());
          localJFileChooser.setFileHidingEnabled(false);
          localJFileChooser.showOpenDialog(localJFileChooser);
          File localFile = localJFileChooser.getSelectedFile();
          if (null != localFile) {
            localJTextField2.setText(localFile.getAbsolutePath());
          }
        }
      });
    }
    localGridBagConstraints.weightx = 0.0D;
    localGridBagConstraints.gridx = 3;
    localGridBagConstraints.gridy = 1;
    localGridBagConstraints.gridwidth = 1;
    localGridBagConstraints.fill = 0;
    localContainer.add(localJButton1, localGridBagConstraints);
    JButton localJButton2 = new JButton("OK");
    localJButton2.addActionListener(new ActionListener()
    {
      private final JRadioButton val$resourceRadio;
      private final JTextField val$resourcePathField;
      private final JTextField val$filePathField;
      private final JDialog val$dialog;
      
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        final boolean bool = localJRadioButton1.isSelected();
        final String str = bool ? localJTextField1.getText() : localJTextField2.getText();
        if ((null == str) || (str.length() == 0))
        {
          JOptionPane.showMessageDialog(localJDialog, "No path entered!");
        }
        else
        {
          final JDialog localJDialog = new JDialog();
          localJDialog.setDefaultCloseOperation(0);
          final JProgressBar localJProgressBar = new JProgressBar();
          localJProgressBar.setIndeterminate(true);
          final JLabel localJLabel = new JLabel("0 characters found");
          final JButton localJButton = new JButton("OK");
          localJButton.setEnabled(false);
          localJButton.addActionListener(new ActionListener()
          {
            private final JDialog val$progressDialog;
            
            public void actionPerformed(ActionEvent paramAnonymous2ActionEvent)
            {
              localJDialog.dispose();
            }
          });
          localJDialog.setLayout(new BoxLayout(localJDialog.getContentPane(), 1));
          localJDialog.add(localJProgressBar);
          localJDialog.add(localJLabel);
          localJDialog.add(localJButton);
          localJDialog.pack();
          HanziLookupUIBuilder.setChildComponentPosition(HanziDict.this, localJDialog);
          localJDialog.setVisible(true);
          final ChangeListener local2 = new ChangeListener()
          {
            private final JLabel val$progressLabel;
            private final JProgressBar val$progressBar;
            
            public void stateChanged(ChangeEvent paramAnonymous2ChangeEvent)
            {
              CEDICTCharacterDictionary localCEDICTCharacterDictionary = (CEDICTCharacterDictionary)paramAnonymous2ChangeEvent.getSource();
              int i = localCEDICTCharacterDictionary.getSize();
              localJLabel.setText(i + " characters found");
              localJProgressBar.setValue(i);
            }
          };
          Thread local3 = new Thread()
          {
            private final boolean val$usingResource;
            private final String val$dictionaryPath;
            private final ChangeListener val$progressLabelUpdater;
            private final JProgressBar val$progressBar;
            private final JButton val$progressOKButton;
            private final JDialog val$progressDialog;
            
            public void run()
            {
              try
              {
                if (bool) {
                  HanziDict.this.loadResourceDictionary(str, local2);
                } else {
                  HanziDict.this.loadFileDictionary(str, local2);
                }
                localJProgressBar.setMaximum(localJProgressBar.getValue());
                localJProgressBar.setIndeterminate(false);
                localJButton.setEnabled(true);
                HanziDict.5.this.val$dialog.dispose();
              }
              catch (Exception localException)
              {
                localJDialog.dispose();
                JOptionPane.showMessageDialog(HanziDict.5.this.val$dialog, "Error reading dictionary: " + str, "Error", 0);
              }
            }
          };
          local3.start();
        }
      }
    });
    localGridBagConstraints.weightx = 0.0D;
    localGridBagConstraints.gridx = 1;
    localGridBagConstraints.gridy = 2;
    localGridBagConstraints.anchor = 22;
    localGridBagConstraints.fill = 0;
    localContainer.add(localJButton2, localGridBagConstraints);
    JButton localJButton3 = new JButton("Cancel");
    localJButton3.addActionListener(new ActionListener()
    {
      private final JDialog val$dialog;
      
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        localJDialog.dispose();
      }
    });
    localGridBagConstraints.weightx = 0.0D;
    localGridBagConstraints.gridx = 2;
    localGridBagConstraints.gridy = 2;
    localGridBagConstraints.anchor = 21;
    localGridBagConstraints.fill = 0;
    localContainer.add(localJButton3, localGridBagConstraints);
    localJDialog.pack();
    return localJDialog;
  }
  
  private void loadDefinitionData(char paramChar, CharacterDictionary.Entry paramEntry)
  {
    char c1 = paramEntry.getTraditional();
    char c2 = paramEntry.getSimplified();
    char c3;
    char c4;
    if (paramChar == c1)
    {
      c3 = c1;
      c4 = c2;
    }
    else
    {
      c3 = c2;
      c4 = c1;
    }
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("<html>\n");
    localStringBuffer.append("\t<head>\n");
    localStringBuffer.append("\t\t<style type=\"text/css\">\n");
    Font localFont = getFont();
    if (null != localFont) {
      localStringBuffer.append("\t\tbody {font-family: ").append(getFont().getFamily()).append("; font-size: ").append(getFont().getSize()).append("}\n");
    }
    localStringBuffer.append("\t\t.characters {font-size: 150%}\n");
    localStringBuffer.append("\t\t</style>\n");
    localStringBuffer.append("\t</head>\n");
    localStringBuffer.append("\t<body>\n");
    localStringBuffer.append("<h1 class=\"characters\">").append(c3);
    if (c4 != c3) {
      localStringBuffer.append("(").append(c4).append(")");
    }
    localStringBuffer.append("</h1>\n");
    localStringBuffer.append("<br>\n\n");
    CharacterDictionary.Entry.Definition[] arrayOfDefinition = paramEntry.getDefinitions();
    localStringBuffer.append("<ol>\n");
    for (int i = 0; i < arrayOfDefinition.length; i++)
    {
      String str = Pinyinifier.pinyinify(arrayOfDefinition[i].getPinyin());
      localStringBuffer.append("<li><b>").append(str).append("</b><br>\n");
      String[] arrayOfString = arrayOfDefinition[i].getTranslations();
      for (int j = 0; j < arrayOfString.length; j++)
      {
        localStringBuffer.append(arrayOfString[j]);
        if (j < arrayOfString.length - 1) {
          localStringBuffer.append("; ");
        }
      }
      localStringBuffer.append("\n");
    }
    localStringBuffer.append("</ol>\n");
    localStringBuffer.append("\t</body>\n");
    localStringBuffer.append("</html>");
    this.definitionTextPane.setText(localStringBuffer.toString());
    this.definitionTextPane.setCaretPosition(0);
  }
  
  public Font getFont()
  {
    return this.font;
  }
  
  public void setFont(Font paramFont)
  {
    this.font = paramFont;
    if (null != this.lookupPanel) {
      this.lookupPanel.setFont(paramFont);
    }
    super.setFont(paramFont);
  }
  
  private void loadEmptyDefinition(char paramChar)
  {
    this.definitionTextPane.setText("<html>\n<body>\nNo definition found.\n</body>\n</html>");
  }
  
  public void characterSelected(HanziLookup.CharacterSelectionEvent paramCharacterSelectionEvent)
  {
    char c = paramCharacterSelectionEvent.getSelectedCharacter();
    CharacterDictionary.Entry localEntry = this.dictionary.lookup(c);
    if (null != localEntry) {
      loadDefinitionData(c, localEntry);
    } else {
      loadEmptyDefinition(c);
    }
  }
  
  public static void main(String[] paramArrayOfString)
  {
    final JFrame localJFrame = new JFrame("HanziDict");
    final HanziDict localHanziDict = new HanziDict();
    Preferences localPreferences = retrievePreferences();
    if (null != localPreferences)
    {
      localHanziDict.prefs = localPreferences;
      localJFrame.addWindowListener(new WindowAdapter()
      {
        private final Preferences val$prefs;
        private final JFrame val$frame;
        private final HanziDict val$hanziDict;
        
        public void windowClosing(WindowEvent paramAnonymousWindowEvent)
        {
          try
          {
            if ((this.val$prefs.getBoolean("save_options", false)) || (0 == JOptionPane.showConfirmDialog(localJFrame, "Okay to save options to disk?  Options will be saved according to the Java Preferences API in a system-dependent location.", "Save options?", 0)))
            {
              localHanziDict.writeOptionsToPreferences(this.val$prefs);
              this.val$prefs.putBoolean("save_options", true);
              this.val$prefs.flush();
            }
            else
            {
              this.val$prefs.removeNode();
            }
          }
          catch (Exception localException)
          {
            JOptionPane.showMessageDialog(localJFrame, "Unexpected error handling prferences.", "Error", 0);
            localException.printStackTrace();
          }
        }
      });
    }
    localHanziDict.init();
    localJFrame.setDefaultCloseOperation(3);
    localJFrame.getContentPane().add(localHanziDict);
    localJFrame.pack();
    localJFrame.setVisible(true);
  }
  
  private static Preferences retrievePreferences()
  {
    Preferences localPreferences = null;
    try
    {
      localPreferences = Preferences.userNodeForPackage(HanziDict.class);
    }
    catch (Exception localException)
    {
      System.err.println("Unable to read preferences, loading defaults.");
      localException.printStackTrace();
    }
    return localPreferences;
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     hanzidict.HanziDict
 * JD-Core Version:    0.7.0.1
 */