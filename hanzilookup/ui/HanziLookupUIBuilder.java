package hanzilookup.ui;

import hanzilookup.HanziLookup;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.ResourceBundle;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import kiang.chinese.font.ChineseFontChooserFactory;

public class HanziLookupUIBuilder
{
  public static JMenu buildOptionsMenu(HanziLookup paramHanziLookup, Collection paramCollection, ResourceBundle paramResourceBundle)
  {
    JMenu localJMenu1 = new JMenu(paramResourceBundle.getString("settings"));
    JMenu localJMenu2 = buildCharacterModeMenu(paramHanziLookup, paramResourceBundle);
    localJMenu1.add(localJMenu2);
    JMenuItem localJMenuItem1 = buildLookupOptionMenuItem(paramHanziLookup, paramResourceBundle);
    localJMenu1.add(localJMenuItem1);
    JMenuItem localJMenuItem2 = buildFontMenuItem(paramHanziLookup, paramCollection, paramResourceBundle);
    localJMenu1.add(localJMenuItem2);
    return localJMenu1;
  }
  
  public static JMenu buildCharacterModeMenu(final HanziLookup paramHanziLookup, ResourceBundle paramResourceBundle)
  {
    int i = paramHanziLookup.getSearchType();
    JRadioButtonMenuItem localJRadioButtonMenuItem1 = new JRadioButtonMenuItem(paramResourceBundle.getString("simplified_character_type"), 1 == i);
    final JRadioButtonMenuItem localJRadioButtonMenuItem2 = new JRadioButtonMenuItem(paramResourceBundle.getString("traditional_character_type"), 2 == i);
    JRadioButtonMenuItem localJRadioButtonMenuItem3 = new JRadioButtonMenuItem(paramResourceBundle.getString("both_character_types"), 0 == i);
    ActionListener local1 = new ActionListener()
    {
      private final JRadioButtonMenuItem val$simplifiedButton;
      private final HanziLookup val$lookup;
      private final JRadioButtonMenuItem val$traditionalButton;
      
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        Object localObject = paramAnonymousActionEvent.getSource();
        if (localObject == this.val$simplifiedButton) {
          paramHanziLookup.setSearchType(1);
        } else if (localObject == localJRadioButtonMenuItem2) {
          paramHanziLookup.setSearchType(2);
        } else {
          paramHanziLookup.setSearchType(0);
        }
      }
    };
    localJRadioButtonMenuItem1.addActionListener(local1);
    localJRadioButtonMenuItem2.addActionListener(local1);
    localJRadioButtonMenuItem3.addActionListener(local1);
    JMenu localJMenu = new JMenu(paramResourceBundle.getString("character_type_bundle_key"));
    localJMenu.add(localJRadioButtonMenuItem1);
    localJMenu.add(localJRadioButtonMenuItem2);
    localJMenu.add(localJRadioButtonMenuItem3);
    ButtonGroup localButtonGroup = new ButtonGroup();
    localButtonGroup.add(localJRadioButtonMenuItem1);
    localButtonGroup.add(localJRadioButtonMenuItem2);
    localButtonGroup.add(localJRadioButtonMenuItem3);
    return localJMenu;
  }
  
  public static JMenuItem buildLookupOptionMenuItem(HanziLookup paramHanziLookup, final ResourceBundle paramResourceBundle)
  {
    JMenuItem localJMenuItem = new JMenuItem(paramResourceBundle.getString("lookup_options"));
    localJMenuItem.addActionListener(new ActionListener()
    {
      private final HanziLookup val$lookup;
      private final ResourceBundle val$bundle;
      
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        JDialog localJDialog = HanziLookupUIBuilder.buildLookupOptionDialog(this.val$lookup, paramResourceBundle);
        localJDialog.setVisible(true);
      }
    });
    return localJMenuItem;
  }
  
  public static JMenuItem buildFontMenuItem(HanziLookup paramHanziLookup, final Collection paramCollection, ResourceBundle paramResourceBundle)
  {
    JMenuItem localJMenuItem = new JMenuItem(paramResourceBundle.getString("choose_font"));
    localJMenuItem.addActionListener(new ActionListener()
    {
      private final HanziLookup val$lookup;
      private final Collection val$containers;
      
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        Font localFont = ChineseFontChooserFactory.showDialog(this.val$lookup);
        if (null != localFont)
        {
          this.val$lookup.setFont(localFont);
          if (null != paramCollection)
          {
            Iterator localIterator = paramCollection.iterator();
            while (localIterator.hasNext())
            {
              Container localContainer = (Container)localIterator.next();
              localContainer.setFont(localFont);
            }
          }
        }
      }
    });
    return localJMenuItem;
  }
  
  public static JDialog buildLookupOptionDialog(final HanziLookup paramHanziLookup, ResourceBundle paramResourceBundle)
  {
    JDialog localJDialog = new JDialog();
    localJDialog.setTitle(paramResourceBundle.getString("options"));
    localJDialog.setDefaultCloseOperation(2);
    Container localContainer = localJDialog.getContentPane();
    localContainer.setLayout(new BoxLayout(localContainer, 1));
    JCheckBox localJCheckBox = buildAutoLookupCheckBox(paramHanziLookup, paramResourceBundle);
    localJCheckBox.setAlignmentX(0.5F);
    localJCheckBox.setSelected(paramHanziLookup.getAutoLookup());
    localContainer.add(localJCheckBox);
    localContainer.add(Box.createVerticalStrut(20));
    JLabel localJLabel1 = new JLabel(paramResourceBundle.getString("lookup_looseness"));
    localJLabel1.setAlignmentX(0.5F);
    localContainer.add(localJLabel1);
    JSlider localJSlider = buildLoosenessSlider(paramHanziLookup);
    localJSlider.setAlignmentX(0.5F);
    localContainer.add(localJSlider);
    localContainer.add(Box.createVerticalStrut(20));
    JLabel localJLabel2 = new JLabel(paramResourceBundle.getString("match_count"));
    localJLabel2.setAlignmentX(0.5F);
    localContainer.add(localJLabel2);
    JSpinner localJSpinner = new JSpinner(new SpinnerNumberModel(paramHanziLookup.getNumResults(), 1, 100, 1));
    localJSpinner.addChangeListener(new ChangeListener()
    {
      private final JSpinner val$resultsSpinner;
      private final HanziLookup val$lookup;
      
      public void stateChanged(ChangeEvent paramAnonymousChangeEvent)
      {
        Integer localInteger = (Integer)this.val$resultsSpinner.getValue();
        paramHanziLookup.setNumResults(localInteger.intValue());
      }
    });
    localJSpinner.setMaximumSize(new Dimension(50, 25));
    localContainer.add(localJSpinner);
    localContainer.add(Box.createVerticalStrut(20));
    KeyStroke localKeyStroke1 = paramHanziLookup.getLookupMacro();
    KeyStroke localKeyStroke2 = paramHanziLookup.getClearMacro();
    KeyStroke localKeyStroke3 = paramHanziLookup.getUndoMacro();
    String str1 = paramResourceBundle.getString("type_macro");
    String str2 = null != localKeyStroke1 ? getKeyStrokeText(localKeyStroke1.getKeyCode(), localKeyStroke1.getModifiers()) : str1;
    String str3 = null != localKeyStroke2 ? getKeyStrokeText(localKeyStroke2.getKeyCode(), localKeyStroke2.getModifiers()) : str1;
    String str4 = null != localKeyStroke3 ? getKeyStrokeText(localKeyStroke3.getKeyCode(), localKeyStroke3.getModifiers()) : str1;
    JTextField localJTextField1 = new JTextField(str2, 10);
    JTextField localJTextField2 = new JTextField(str3, 10);
    JTextField localJTextField3 = new JTextField(str4, 10);
    MacroKeyListener localMacroKeyListener = new MacroKeyListener(localJTextField1, localJTextField2, localJTextField3, paramHanziLookup, null);
    localJTextField1.addKeyListener(localMacroKeyListener);
    localJTextField2.addKeyListener(localMacroKeyListener);
    localJTextField3.addKeyListener(localMacroKeyListener);
    JLabel localJLabel3 = new JLabel(paramResourceBundle.getString("lookup_macro"));
    JLabel localJLabel4 = new JLabel(paramResourceBundle.getString("clear_macro"));
    JLabel localJLabel5 = new JLabel(paramResourceBundle.getString("undo_macro"));
    localJLabel3.setHorizontalAlignment(4);
    localJLabel4.setHorizontalAlignment(4);
    localJLabel5.setHorizontalAlignment(4);
    JPanel localJPanel = new JPanel(new GridLayout(3, 2));
    localJPanel.add(localJLabel3);
    localJPanel.add(localJTextField1);
    localJPanel.add(localJLabel4);
    localJPanel.add(localJTextField2);
    localJPanel.add(localJLabel5);
    localJPanel.add(localJTextField3);
    localContainer.add(localJPanel);
    localContainer.add(Box.createVerticalStrut(20));
    JButton localJButton = new JButton(paramResourceBundle.getString("ok"));
    localJButton.setAlignmentX(0.5F);
    localJButton.addActionListener(new ActionListener()
    {
      private final JDialog val$optionsDialog;
      
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        this.val$optionsDialog.dispose();
      }
    });
    localContainer.add(localJButton);
    localJDialog.pack();
    setChildComponentPosition(paramHanziLookup, localJDialog);
    return localJDialog;
  }
  
  public static void setChildComponentPosition(Component paramComponent1, Component paramComponent2)
  {
    Point localPoint = paramComponent1.getLocationOnScreen();
    int i = (int)(localPoint.getX() + paramComponent1.getWidth() / 2 - paramComponent2.getWidth() / 2);
    int j = (int)(localPoint.getY() + paramComponent1.getHeight() / 2 - paramComponent2.getHeight() / 2);
    paramComponent2.setLocation(i, j);
  }
  
  public static JMenuItem buildSaveOptionsMenuItem(HanziLookup paramHanziLookup, ResourceBundle paramResourceBundle)
  {
    JMenuItem localJMenuItem = new JMenuItem(paramResourceBundle.getString("save_settings"));
    localJMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent) {}
    });
    return localJMenuItem;
  }
  
  private static String getKeyStrokeText(int paramInt1, int paramInt2)
  {
    String str = paramInt2 != 0 ? KeyEvent.getKeyModifiersText(paramInt2) + " " : "";
    str = str + KeyEvent.getKeyText(paramInt1);
    return str;
  }
  
  public static JCheckBox buildAutoLookupCheckBox(HanziLookup paramHanziLookup, ResourceBundle paramResourceBundle)
  {
    final JCheckBox localJCheckBox = new JCheckBox(paramResourceBundle.getString("auto_lookup"));
    localJCheckBox.addActionListener(new ActionListener()
    {
      private final HanziLookup val$lookup;
      private final JCheckBox val$autoLookupCheckBox;
      
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        this.val$lookup.setAutoLookup(localJCheckBox.isSelected());
      }
    });
    return localJCheckBox;
  }
  
  public static JSlider buildLoosenessSlider(final HanziLookup paramHanziLookup)
  {
    JSlider localJSlider = new JSlider(0, 20);
    int i = (int)(localJSlider.getMaximum() * paramHanziLookup.getLooseness());
    localJSlider.setValue(i);
    localJSlider.addChangeListener(new ChangeListener()
    {
      private final JSlider val$loosenessSlider;
      private final HanziLookup val$lookup;
      
      public void stateChanged(ChangeEvent paramAnonymousChangeEvent)
      {
        int i = this.val$loosenessSlider.getMaximum();
        int j = this.val$loosenessSlider.getValue();
        double d = j / i;
        paramHanziLookup.setLooseness(d);
      }
    });
    return localJSlider;
  }
  
  private static class MacroKeyListener
    implements KeyListener
  {
    private JTextField lookupField;
    private JTextField clearField;
    private JTextField undoField;
    private HanziLookup hanziLookup;
    
    private MacroKeyListener(JTextField paramJTextField1, JTextField paramJTextField2, JTextField paramJTextField3, HanziLookup paramHanziLookup)
    {
      this.lookupField = paramJTextField1;
      this.clearField = paramJTextField2;
      this.undoField = paramJTextField3;
      this.hanziLookup = paramHanziLookup;
    }
    
    public void keyPressed(KeyEvent paramKeyEvent)
    {
      int i = paramKeyEvent.getKeyCode();
      int j = paramKeyEvent.getModifiers();
      if ((0 != i) && (!isModifier(i)))
      {
        Object localObject = paramKeyEvent.getSource();
        String str = HanziLookupUIBuilder.getKeyStrokeText(i, j);
        KeyStroke localKeyStroke = KeyStroke.getKeyStroke(i, j);
        if (localObject == this.lookupField)
        {
          this.hanziLookup.registerLookupMacro(localKeyStroke);
          this.lookupField.setText(str);
        }
        else if (localObject == this.clearField)
        {
          this.hanziLookup.registerClearMacro(localKeyStroke);
          this.clearField.setText(str);
        }
        else
        {
          this.hanziLookup.registerUndoMacro(localKeyStroke);
          this.undoField.setText(str);
        }
      }
      paramKeyEvent.consume();
    }
    
    public void keyReleased(KeyEvent paramKeyEvent)
    {
      paramKeyEvent.consume();
    }
    
    public void keyTyped(KeyEvent paramKeyEvent)
    {
      paramKeyEvent.consume();
    }
    
    private boolean isModifier(int paramInt)
    {
      return (17 == paramInt) || (18 == paramInt) || (157 == paramInt) || (16 == paramInt);
    }
    
    MacroKeyListener(JTextField paramJTextField1, JTextField paramJTextField2, JTextField paramJTextField3, HanziLookup paramHanziLookup, HanziLookupUIBuilder.1 param1)
    {
      this(paramJTextField1, paramJTextField2, paramJTextField3, paramHanziLookup);
    }
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     hanzilookup.ui.HanziLookupUIBuilder
 * JD-Core Version:    0.7.0.1
 */