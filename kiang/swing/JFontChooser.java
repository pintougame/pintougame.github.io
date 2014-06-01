package kiang.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;

public class JFontChooser
  extends JComponent
{
  private JTextField fontChooserField;
  private JList fontChooserList;
  private JTextListLink fontChooserLink;
  private JTextField sizeChooserField;
  private JList sizeChooserList;
  private JTextListLink sizeChooserLink;
  private JCheckBox boldCheckBox;
  private JCheckBox italicCheckBox;
  private JLabel previewLabel;
  private Font[] fontOptions;
  private Set appliedFilters;
  
  public static Font showDialog(Component paramComponent)
  {
    Font localFont = paramComponent.getFont();
    String str = "The quick brown fox jumps over the lazy dog.  123456790";
    int[] arrayOfInt = { 8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72 };
    Font[] arrayOfFont = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    return showDialog(paramComponent, localFont, arrayOfFont, arrayOfInt, new FontFilter[0], str);
  }
  
  public static Font showDialog(Component paramComponent, Font paramFont, Font[] paramArrayOfFont, int[] paramArrayOfInt, FontFilter[] paramArrayOfFontFilter, String paramString)
  {
    JFontChooser localJFontChooser = new JFontChooser(paramFont, paramArrayOfFont, paramArrayOfInt, paramArrayOfFontFilter, paramString);
    JFontChooser tmp22_20 = localJFontChooser;
    tmp22_20.getClass();
    FontReturner localFontReturner = new FontReturner(tmp22_20, null);
    FontChooserDialog localFontChooserDialog = new FontChooserDialog(paramComponent, "Choose a Font", true, localJFontChooser, localFontReturner, null, null);
    localFontChooserDialog.setLocation(paramComponent.getLocationOnScreen());
    localFontChooserDialog.pack();
    localFontChooserDialog.setVisible(true);
    return localFontReturner.getFont();
  }
  
  public JFontChooser(Font paramFont, Font[] paramArrayOfFont, int[] paramArrayOfInt, FontFilter[] paramArrayOfFontFilter, String paramString)
  {
    init(paramFont, paramArrayOfFont, paramArrayOfInt, paramArrayOfFontFilter, paramString);
  }
  
  private void init(Font paramFont, Font[] paramArrayOfFont, int[] paramArrayOfInt, FontFilter[] paramArrayOfFontFilter, String paramString)
  {
    this.fontOptions = paramArrayOfFont;
    JComponent localJComponent1 = buildOptionsPanel(paramFont, paramArrayOfFont, paramArrayOfInt, paramArrayOfFontFilter);
    JComponent localJComponent2 = buildPreviewPanel(paramFont, paramString);
    setLayout(new BorderLayout());
    add(localJComponent1, "Center");
    add(localJComponent2, "South");
  }
  
  public Font getSelectedFont()
  {
    Font localFont = null;
    String str = (String)this.fontChooserList.getSelectedValue();
    if (null != str)
    {
      int i = 0;
      i |= (this.boldCheckBox.isSelected() ? 1 : 0);
      i |= (this.italicCheckBox.isSelected() ? 2 : 0);
      int j = -1;
      try
      {
        j = Integer.parseInt(this.sizeChooserField.getText());
      }
      catch (NumberFormatException localNumberFormatException) {}
      if (j <= 0) {
        j = this.previewLabel.getFont().getSize();
      }
      localFont = new Font(str, i, j);
    }
    return localFont;
  }
  
  private JComponent buildOptionsPanel(Font paramFont, Font[] paramArrayOfFont, int[] paramArrayOfInt, FontFilter[] paramArrayOfFontFilter)
  {
    JPanel localJPanel1 = new JPanel();
    localJPanel1.setLayout(new BoxLayout(localJPanel1, 0));
    JComponent localJComponent1 = buildFontChooserPanel(paramFont, paramArrayOfFont);
    JComponent localJComponent2 = buildSizeChooserPanel(paramFont, paramArrayOfInt);
    JComponent localJComponent3 = buildStyleChooserPanel(paramFont);
    localJPanel1.add(localJComponent1);
    localJPanel1.add(localJComponent2);
    localJPanel1.add(localJComponent3);
    Object localObject = null;
    if (paramArrayOfFontFilter.length > 0)
    {
      JComponent localJComponent4 = buildFilterChooser(paramArrayOfFontFilter);
      localJComponent4.setBorder(BorderFactory.createTitledBorder("Filters"));
      JPanel localJPanel2 = new JPanel(new BorderLayout());
      localJPanel2.add(localJPanel1, "Center");
      localJPanel2.add(localJComponent4, "South");
      localObject = localJPanel2;
    }
    else
    {
      localObject = localJPanel1;
    }
    loadFilteredFonts(paramFont);
    setupFontSelectionListener();
    return localObject;
  }
  
  private void setupFontSelectionListener()
  {
    FontSelectionListener localFontSelectionListener = new FontSelectionListener(null);
    this.fontChooserField.getDocument().addDocumentListener(localFontSelectionListener);
    this.fontChooserList.addListSelectionListener(localFontSelectionListener);
    this.sizeChooserField.getDocument().addDocumentListener(localFontSelectionListener);
    this.sizeChooserList.addListSelectionListener(localFontSelectionListener);
    this.boldCheckBox.addActionListener(localFontSelectionListener);
    this.italicCheckBox.addActionListener(localFontSelectionListener);
  }
  
  private JComponent buildFontChooserPanel(Font paramFont, Font[] paramArrayOfFont)
  {
    this.fontChooserField = new JTextField(paramFont.getName());
    JComponent localJComponent = buildFontChooserListPane();
    this.fontChooserLink = new JTextListLink(this.fontChooserField, this.fontChooserList, true);
    JPanel localJPanel = new JPanel(new BorderLayout());
    localJPanel.add(this.fontChooserField, "North");
    localJPanel.add(localJComponent, "Center");
    localJPanel.setBorder(BorderFactory.createTitledBorder("Font"));
    return localJPanel;
  }
  
  private JComponent buildFontChooserListPane()
  {
    this.fontChooserList = new JList();
    this.fontChooserList.setSelectionMode(0);
    JScrollPane localJScrollPane = new JScrollPane(this.fontChooserList, 22, 30);
    return localJScrollPane;
  }
  
  private void loadFilteredFonts(Font paramFont)
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < this.fontOptions.length; i++)
    {
      int j = 1;
      localIterator = this.appliedFilters.iterator();
      while (localIterator.hasNext())
      {
        FontFilter localFontFilter = (FontFilter)localIterator.next();
        if (!localFontFilter.shouldInclude(this.fontOptions[i]))
        {
          j = 0;
          break;
        }
      }
      if (j != 0) {
        localArrayList.add(this.fontOptions[i]);
      }
    }
    Object localObject = null != paramFont ? paramFont.getName() : null;
    String[] arrayOfString = new String[localArrayList.size()];
    Iterator localIterator = localArrayList.iterator();
    int k = 0;
    for (int m = 0; localIterator.hasNext(); m++)
    {
      Font localFont = (Font)localIterator.next();
      arrayOfString[m] = localFont.getName();
      if (arrayOfString[m].equals(localObject)) {
        k = 1;
      }
    }
    Arrays.sort(arrayOfString, new Comparator()
    {
      public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2)
      {
        return paramAnonymousObject1.toString().compareToIgnoreCase(paramAnonymousObject2.toString());
      }
    });
    this.fontChooserList.setListData(arrayOfString);
    if (k != 0) {
      this.fontChooserList.setSelectedValue(localObject, true);
    } else {
      this.fontChooserField.setText("");
    }
  }
  
  private JComponent buildSizeChooserPanel(Font paramFont, int[] paramArrayOfInt)
  {
    int i = paramFont.getSize();
    this.sizeChooserField = new JTextField(Integer.toString(i));
    JComponent localJComponent = buildSizeChooserList(i, paramArrayOfInt);
    this.sizeChooserLink = new JTextListLink(this.sizeChooserField, this.sizeChooserList, true);
    JPanel localJPanel = new JPanel(new BorderLayout());
    localJPanel.add(this.sizeChooserField, "North");
    localJPanel.add(localJComponent, "Center");
    localJPanel.setBorder(BorderFactory.createTitledBorder("Size"));
    return localJPanel;
  }
  
  private JComponent buildSizeChooserList(int paramInt, int[] paramArrayOfInt)
  {
    String[] arrayOfString = new String[paramArrayOfInt.length];
    for (int i = 0; i < paramArrayOfInt.length; i++) {
      arrayOfString[i] = Integer.toString(paramArrayOfInt[i]);
    }
    this.sizeChooserList = new JList(arrayOfString);
    this.sizeChooserList.setSelectionMode(0);
    JScrollPane localJScrollPane = new JScrollPane(this.sizeChooserList, 22, 30);
    return localJScrollPane;
  }
  
  private JComponent buildStyleChooserPanel(Font paramFont)
  {
    this.boldCheckBox = new JCheckBox("Bold", paramFont.isBold());
    this.italicCheckBox = new JCheckBox("Italics", paramFont.isItalic());
    JPanel localJPanel = new JPanel();
    localJPanel.setLayout(new BoxLayout(localJPanel, 1));
    localJPanel.add(this.boldCheckBox);
    localJPanel.add(this.italicCheckBox);
    localJPanel.setBorder(BorderFactory.createTitledBorder("Style"));
    return localJPanel;
  }
  
  private JComponent buildFilterChooser(FontFilter[] paramArrayOfFontFilter)
  {
    this.appliedFilters = new HashSet();
    JPanel localJPanel = new JPanel();
    localJPanel.setLayout(new FlowLayout());
    for (int i = 0; i < paramArrayOfFontFilter.length; i++)
    {
      if (paramArrayOfFontFilter[i].isDefaultOn()) {
        this.appliedFilters.add(paramArrayOfFontFilter[i]);
      }
      JCheckBox localJCheckBox = buildFilterCheckBox(paramArrayOfFontFilter[i]);
      localJPanel.add(localJCheckBox);
    }
    return localJPanel;
  }
  
  private JCheckBox buildFilterCheckBox(final FontFilter paramFontFilter)
  {
    ActionListener local2 = new ActionListener()
    {
      private final JFontChooser.FontFilter val$filter;
      
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        JCheckBox localJCheckBox = (JCheckBox)paramAnonymousActionEvent.getSource();
        if (localJCheckBox.isSelected()) {
          JFontChooser.this.appliedFilters.add(paramFontFilter);
        } else {
          JFontChooser.this.appliedFilters.remove(paramFontFilter);
        }
        JFontChooser.this.loadFilteredFonts(JFontChooser.this.getSelectedFont());
      }
    };
    JCheckBox localJCheckBox = new JCheckBox(paramFontFilter.getDisplayName(), paramFontFilter.isDefaultOn());
    localJCheckBox.addActionListener(local2);
    return localJCheckBox;
  }
  
  private JComponent buildPreviewPanel(Font paramFont, String paramString)
  {
    this.previewLabel = new JLabel(paramString);
    this.previewLabel.setFont(paramFont);
    this.previewLabel.setHorizontalAlignment(0);
    this.previewLabel.setVerticalAlignment(0);
    this.previewLabel.setBorder(BorderFactory.createTitledBorder("Preview"));
    JScrollPane localJScrollPane = new JScrollPane(this.previewLabel, 20, 30);
    return localJScrollPane;
  }
  
  public static void main(String[] paramArrayOfString)
  {
    JFrame localJFrame = new JFrame();
    localJFrame.setDefaultCloseOperation(3);
    localJFrame.pack();
    localJFrame.setVisible(true);
    Font localFont = new Font("Simsun", 0, 36);
    int[] arrayOfInt = { 8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72 };
    String str = "汉  漢";
    Font[] arrayOfFont1 = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < arrayOfFont1.length; i++) {
      if ((arrayOfFont1[i].canDisplay('汉')) || (arrayOfFont1[i].canDisplay('漢'))) {
        localArrayList.add(arrayOfFont1[i]);
      }
    }
    Font[] arrayOfFont2 = new Font[localArrayList.size()];
    Iterator localIterator = localArrayList.iterator();
    for (int j = 0; localIterator.hasNext(); j++) {
      arrayOfFont2[j] = ((Font)localIterator.next());
    }
    FontFilter local3 = new FontFilter()
    {
      public boolean isDefaultOn()
      {
        return true;
      }
      
      public boolean shouldInclude(Font paramAnonymousFont)
      {
        return paramAnonymousFont.canDisplay('က');
      }
      
      public String getDisplayName()
      {
        return "Test1";
      }
    };
    FontFilter local4 = new FontFilter()
    {
      public boolean isDefaultOn()
      {
        return false;
      }
      
      public boolean shouldInclude(Font paramAnonymousFont)
      {
        return paramAnonymousFont.canDisplay('漢');
      }
      
      public String getDisplayName()
      {
        return "Test2";
      }
    };
    localFont = showDialog(localJFrame, localFont, arrayOfFont1, arrayOfInt, new FontFilter[] { local3, local4 }, str);
  }
  
  public static abstract interface FontFilter
  {
    public abstract String getDisplayName();
    
    public abstract boolean isDefaultOn();
    
    public abstract boolean shouldInclude(Font paramFont);
  }
  
  private class FontReturner
    implements ActionListener
  {
    private Font font;
    
    private FontReturner() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      this.font = JFontChooser.this.getSelectedFont();
    }
    
    public Font getFont()
    {
      return this.font;
    }
    
    FontReturner(JFontChooser.1 param1)
    {
      this();
    }
  }
  
  private static class FontChooserDialog
    extends JDialog
  {
    private FontChooserDialog(Component paramComponent, String paramString, boolean paramBoolean, JFontChooser paramJFontChooser, ActionListener paramActionListener1, ActionListener paramActionListener2)
    {
      super(paramString, paramBoolean);
      JButton localJButton1 = new JButton("OK");
      JButton localJButton2 = new JButton("Cancel");
      ActionListener local1 = new ActionListener()
      {
        public void actionPerformed(ActionEvent paramAnonymousActionEvent)
        {
          JFontChooser.FontChooserDialog.this.setVisible(false);
        }
      };
      localJButton1.addActionListener(local1);
      localJButton2.addActionListener(local1);
      if (null != paramActionListener1) {
        localJButton1.addActionListener(paramActionListener1);
      }
      if (null != paramActionListener2) {
        localJButton2.addActionListener(paramActionListener2);
      }
      JPanel localJPanel = new JPanel(new FlowLayout());
      localJPanel.add(localJButton1);
      localJPanel.add(localJButton2);
      Container localContainer = getContentPane();
      localContainer.setLayout(new BorderLayout());
      localContainer.add(paramJFontChooser, "Center");
      localContainer.add(localJPanel, "South");
    }
    
    FontChooserDialog(Component paramComponent, String paramString, boolean paramBoolean, JFontChooser paramJFontChooser, ActionListener paramActionListener1, ActionListener paramActionListener2, JFontChooser.1 param1)
    {
      this(paramComponent, paramString, paramBoolean, paramJFontChooser, paramActionListener1, paramActionListener2);
    }
  }
  
  private class FontSelectionListener
    implements DocumentListener, ListSelectionListener, ActionListener
  {
    private FontSelectionListener() {}
    
    public void updatePreviewFont()
    {
      JFontChooser.this.previewLabel.setFont(JFontChooser.this.getSelectedFont());
    }
    
    public void insertUpdate(DocumentEvent paramDocumentEvent)
    {
      updatePreviewFont();
    }
    
    public void removeUpdate(DocumentEvent paramDocumentEvent)
    {
      updatePreviewFont();
    }
    
    public void changedUpdate(DocumentEvent paramDocumentEvent)
    {
      updatePreviewFont();
    }
    
    public void valueChanged(ListSelectionEvent paramListSelectionEvent)
    {
      updatePreviewFont();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      updatePreviewFont();
    }
    
    FontSelectionListener(JFontChooser.1 param1)
    {
      this();
    }
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     kiang.swing.JFontChooser
 * JD-Core Version:    0.7.0.1
 */