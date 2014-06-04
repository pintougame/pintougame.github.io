package hanzilookup;

import hanzilookup.data.CharacterDescriptor;
import hanzilookup.data.MatcherThread;
import hanzilookup.data.MatcherThread.ResultsHandler;
import hanzilookup.data.MemoryStrokesStreamProvider;
import hanzilookup.data.ResourceStrokesStreamProvider;
import hanzilookup.data.StrokesDataSource;
import hanzilookup.data.StrokesMatcher;
import hanzilookup.data.StrokesParser;
import hanzilookup.ui.CharacterCanvas;
import hanzilookup.ui.CharacterCanvas.StrokeEvent;
import hanzilookup.ui.CharacterCanvas.StrokesListener;
import hanzilookup.ui.WrittenCharacter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import kiang.swing.JClickableList;
import kiang.swing.JClickableList.ListClickedEvent;
import kiang.swing.JClickableList.ListClickedListener;

public class HanziLookup
  extends JPanel
{
  public static final String SEARCH_TYPE_PREF_KEY = "search_type";
  public static final String LOOSENESS_PREF_KEY = "looseness";
  public static final String AUTOLOOKUP_PREF_KEY = "autolookup";
  public static final String MATCH_COUNT_PREF_KEY = "match_count";
  public static final String LOOKUP_MACRO_PREF_CODE_KEY = "lookup_macro_code";
  public static final String LOOKUP_MACRO_PREF_MODIFIERS_KEY = "lookup_macro_modifiers";
  public static final String CLEAR_MACRO_PREF_CODE_KEY = "clear_macro_code";
  public static final String CLEAR_MACRO_PREF_MODIFIERS_KEY = "clear_macro_modifiers";
  public static final String UNDO_MACRO_PREF_KEY = "undo_macro_code";
  public static final String UNDO_MACRO_PREF_MODIFIERS = "undo_macro_modifiers";
  private int searchType;
  private CharacterCanvas inputCanvas;
  private JButton lookupButton;
  private JButton clearButton;
  private JButton undoButton;
  private KeyStroke lookupMacro;
  private KeyStroke clearMacro;
  private KeyStroke undoMacro;
  private JList candidatesList;
  private StrokesDataSource strokesDataSource;
  private boolean autoLookup = true;
  private double looseness = 0.25D;
  private int numResults = 15;
  private MatcherThread matcherThread;
  private Set characterHandlers = new LinkedHashSet();
  private static final Dimension CANVAS_DIMENSION = new Dimension(235, 235);
  private static final Dimension SELECTION_DIMENSION = new Dimension(275, 130);
  
  public HanziLookup(InputStream paramInputStream1, InputStream paramInputStream2, Font paramFont)
    throws IOException
  {
    this(new StrokesDataSource(new MemoryStrokesStreamProvider(StrokesParser.getStrokeBytes(paramInputStream1, paramInputStream2))), paramFont);
  }
  
  public HanziLookup(InputStream paramInputStream, Font paramFont)
    throws IOException
  {
    this(new StrokesDataSource(new MemoryStrokesStreamProvider(paramInputStream)), paramFont);
  }
  
  public HanziLookup(String paramString, Font paramFont)
    throws IOException
  {
    this(new StrokesDataSource(new ResourceStrokesStreamProvider(paramString)), paramFont);
  }
  
  public HanziLookup(StrokesDataSource paramStrokesDataSource, Font paramFont)
  {
    this.strokesDataSource = paramStrokesDataSource;
    initMatcherThread();
    initUI(paramFont);
  }
  
  private void initMatcherThread()
  {
    this.matcherThread = new MatcherThread();
    this.matcherThread.addResultsHandler(new MatcherThread.ResultsHandler()
    {
      public void handleResults(Character[] paramAnonymousArrayOfCharacter)
      {
        HanziLookup.this.handleResults(paramAnonymousArrayOfCharacter);
      }
    });
    this.matcherThread.setDaemon(true);
    this.matcherThread.setPriority(5);
    this.matcherThread.start();
  }
  
  private void initUI(Font paramFont)
  {
    JPanel localJPanel1 = buildInputPanel();
    JPanel localJPanel2 = buildSelectionPanel();
    setFont(paramFont);
    setLayout(new BorderLayout());
    add(localJPanel1, "North");
    add(localJPanel2, "Center");
    KeyStroke localKeyStroke1 = KeyStroke.getKeyStroke(76, 8);
    KeyStroke localKeyStroke2 = KeyStroke.getKeyStroke(67, 8);
    KeyStroke localKeyStroke3 = KeyStroke.getKeyStroke(90, 2);
    registerLookupMacro(localKeyStroke1);
    registerClearMacro(localKeyStroke2);
    registerUndoMacro(localKeyStroke3);
  }
  
  public void addCharacterReceiver(CharacterSelectionListener paramCharacterSelectionListener)
  {
    synchronized (this.characterHandlers)
    {
      this.characterHandlers.add(paramCharacterSelectionListener);
    }
  }
  
  public void removeCharacterReceiver(CharacterSelectionListener paramCharacterSelectionListener)
  {
    synchronized (this.characterHandlers)
    {
      this.characterHandlers.remove(paramCharacterSelectionListener);
    }
  }
  
  private void notifyReceivers(char paramChar)
  {
    synchronized (this.characterHandlers)
    {
      Iterator localIterator = this.characterHandlers.iterator();
      while (localIterator.hasNext())
      {
        CharacterSelectionListener localCharacterSelectionListener = (CharacterSelectionListener)localIterator.next();
        localCharacterSelectionListener.characterSelected(new CharacterSelectionEvent(this, paramChar, null));
      }
    }
  }
  
  public void loadOptionsFromPreferences(Preferences paramPreferences)
  {
    this.searchType = paramPreferences.getInt("search_type", this.searchType);
    this.looseness = paramPreferences.getDouble("looseness", this.looseness);
    this.autoLookup = paramPreferences.getBoolean("autolookup", this.autoLookup);
    this.numResults = paramPreferences.getInt("match_count", this.numResults);
    int i = paramPreferences.getInt("lookup_macro_code", this.lookupMacro.getKeyCode());
    int j = paramPreferences.getInt("lookup_macro_modifiers", this.lookupMacro.getModifiers());
    registerLookupMacro(KeyStroke.getKeyStroke(i, j));
    int k = paramPreferences.getInt("clear_macro_code", this.clearMacro.getKeyCode());
    int m = paramPreferences.getInt("clear_macro_modifiers", this.clearMacro.getModifiers());
    registerClearMacro(KeyStroke.getKeyStroke(k, m));
    int n = paramPreferences.getInt("undo_macro_code", this.undoMacro.getKeyCode());
    int i1 = paramPreferences.getInt("undo_macro_modifiers", this.undoMacro.getModifiers());
    registerUndoMacro(KeyStroke.getKeyStroke(n, i1));
  }
  
  public void writeOptionsToPreferences(Preferences paramPreferences)
  {
    paramPreferences.putInt("search_type", this.searchType);
    paramPreferences.putDouble("looseness", this.looseness);
    paramPreferences.putBoolean("autolookup", this.autoLookup);
    paramPreferences.putInt("match_count", this.numResults);
    paramPreferences.putInt("lookup_macro_code", this.lookupMacro.getKeyCode());
    paramPreferences.putInt("lookup_macro_modifiers", this.lookupMacro.getModifiers());
    paramPreferences.putInt("clear_macro_code", this.clearMacro.getKeyCode());
    paramPreferences.putInt("clear_macro_modifiers", this.clearMacro.getModifiers());
    paramPreferences.putInt("undo_macro_code", this.undoMacro.getKeyCode());
    paramPreferences.putInt("undo_macro_modifiers", this.undoMacro.getModifiers());
  }
  
  void runLookup()
  {
    synchronized (this.candidatesList)
    {
      this.candidatesList.setModel(new DefaultListModel());
    }
    ??? = this.inputCanvas.getCharacter();
    if (((WrittenCharacter)???).getStrokeList().size() == 0)
    {
      handleResults(new Character[0]);
      return;
    }
    CharacterDescriptor localCharacterDescriptor = ((WrittenCharacter)???).buildCharacterDescriptor();
    boolean bool1 = (this.searchType == 0) || (this.searchType == 2);
    boolean bool2 = (this.searchType == 0) || (this.searchType == 1);
    StrokesMatcher localStrokesMatcher = new StrokesMatcher(localCharacterDescriptor, bool1, bool2, this.looseness, this.numResults, this.strokesDataSource);
    this.matcherThread.setStrokesMatcher(localStrokesMatcher);
  }
  
  private void handleResults(final Character[] paramArrayOfCharacter)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      private final Character[] val$results;
      
      public void run()
      {
        HanziLookup.this.candidatesList.setListData(paramArrayOfCharacter);
        HanziLookup.this.lookupButton.setEnabled(true);
      }
    });
  }
  
  private void clear()
  {
    this.inputCanvas.clear();
    this.inputCanvas.repaint();
    synchronized (this.candidatesList)
    {
      this.candidatesList.setListData(new Object[0]);
      this.candidatesList.repaint();
    }
  }
  
  private void undo()
  {
    this.inputCanvas.undo();
    if ((this.autoLookup) && (this.inputCanvas.getCharacter().getStrokeList().size() > 0)) {
      runLookup();
    } else {
      handleResults(new Character[0]);
    }
    this.inputCanvas.repaint();
  }
  
  private void lookup()
  {
    this.lookupButton.setEnabled(false);
    runLookup();
  }
  
  private JPanel buildInputPanel()
  {
    this.inputCanvas = new CharacterCanvas();
    this.inputCanvas.setPreferredSize(CANVAS_DIMENSION);
    this.inputCanvas.addStrokesListener(new CharacterCanvas.StrokesListener()
    {
      public void strokeFinished(CharacterCanvas.StrokeEvent paramAnonymousStrokeEvent)
      {
        HanziLookup.this.strokeFinished(paramAnonymousStrokeEvent);
      }
    });
    JPanel localJPanel1 = buildCanvasButtons();
    JPanel localJPanel2 = new JPanel();
    localJPanel2.setLayout(new BorderLayout());
    localJPanel2.add(this.inputCanvas, "Center");
    localJPanel2.add(localJPanel1, "South");
    localJPanel2.setBorder(BorderFactory.createTitledBorder("Enter character"));
    return localJPanel2;
  }
  
  private JPanel buildCanvasButtons()
  {
    this.lookupButton = new JButton("Lookup");
    this.clearButton = new JButton("Clear");
    this.undoButton = new JButton("Undo");
    ActionListener local4 = new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        if (paramAnonymousActionEvent.getSource() == HanziLookup.this.lookupButton) {
          HanziLookup.this.lookup();
        } else if (paramAnonymousActionEvent.getSource() == HanziLookup.this.clearButton) {
          HanziLookup.this.clear();
        } else if (paramAnonymousActionEvent.getSource() == HanziLookup.this.undoButton) {
          HanziLookup.this.undo();
        }
      }
    };
    this.lookupButton.addActionListener(local4);
    this.clearButton.addActionListener(local4);
    this.undoButton.addActionListener(local4);
    JPanel localJPanel = new JPanel();
    localJPanel.setLayout(new GridLayout(1, 2));
    localJPanel.add(this.lookupButton);
    localJPanel.add(this.undoButton);
    localJPanel.add(this.clearButton);
    return localJPanel;
  }
  
  private JPanel buildSelectionPanel()
  {
    this.candidatesList = buildCandidatesList();
    JScrollPane localJScrollPane = new JScrollPane(this.candidatesList);
    localJScrollPane.setVerticalScrollBarPolicy(22);
    JPanel localJPanel = new JPanel();
    localJPanel.setLayout(new BorderLayout());
    localJPanel.add(localJScrollPane, "Center");
    localJPanel.setPreferredSize(SELECTION_DIMENSION);
    localJPanel.setBorder(BorderFactory.createTitledBorder("Select character"));
    return localJPanel;
  }
  
  private JList buildCandidatesList()
  {
    final JClickableList localJClickableList = new JClickableList();
    localJClickableList.setSelectionMode(0);
    localJClickableList.setLayoutOrientation(2);
    localJClickableList.setVisibleRowCount(-1);
    ((DefaultListCellRenderer)localJClickableList.getCellRenderer()).setHorizontalAlignment(0);
    localJClickableList.addListClickedListener(new JClickableList.ListClickedListener()
    {
      private final JClickableList val$candidatesList;
      
      public void listClicked(JClickableList.ListClickedEvent paramAnonymousListClickedEvent)
      {
        Character localCharacter = (Character)localJClickableList.getSelectedValue();
        if (null != localCharacter) {
          HanziLookup.this.notifyReceivers(localCharacter.charValue());
        }
      }
    });
    return localJClickableList;
  }
  
  public void setFont(Font paramFont)
  {
    super.setFont(paramFont);
    if ((null != paramFont) && (null != this.candidatesList))
    {
      this.candidatesList.setFont(paramFont);
      resetListCellWidth(paramFont);
    }
  }
  
  protected void validateTree()
  {
    super.validateTree();
    resetListCellWidth(this.candidatesList.getFont());
  }
  
  private void resetListCellWidth(Font paramFont)
  {
    FontMetrics localFontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(paramFont);
    int i = this.candidatesList.getWidth();
    i = i == 0 ? (int)this.candidatesList.getPreferredSize().getWidth() : i;
    int j = 2 * localFontMetrics.charWidth('一');
    this.candidatesList.setFixedCellWidth(j);
  }
  
  public void setSearchType(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1) && (paramInt != 2)) {
      throw new IllegalArgumentException("searchType invalid!");
    }
    int i = this.searchType;
    this.searchType = paramInt;
    if ((this.autoLookup) && (i != paramInt)) {
      runLookup();
    }
  }
  
  public int getSearchType()
  {
    return this.searchType;
  }
  
  public void setNumResults(int paramInt)
  {
    if (paramInt < 1) {
      throw new IllegalArgumentException("numResults must be at least 1!");
    }
    this.numResults = paramInt;
  }
  
  public int getNumResults()
  {
    return this.numResults;
  }
  
  public void setAutoLookup(boolean paramBoolean)
  {
    this.autoLookup = paramBoolean;
  }
  
  public boolean getAutoLookup()
  {
    return this.autoLookup;
  }
  
  public void setLooseness(double paramDouble)
  {
    if ((paramDouble < 0.0D) || (paramDouble > 1.0D)) {
      throw new IllegalArgumentException("looseness must be between 0.0 and 1.0!");
    }
    this.looseness = paramDouble;
  }
  
  public double getLooseness()
  {
    return this.looseness;
  }
  
  private void strokeFinished(CharacterCanvas.StrokeEvent paramStrokeEvent)
  {
    if (this.autoLookup) {
      runLookup();
    }
  }
  
  public KeyStroke getLookupMacro()
  {
    return this.lookupMacro;
  }
  
  public KeyStroke getUndoMacro()
  {
    return this.undoMacro;
  }
  
  public KeyStroke getClearMacro()
  {
    return this.clearMacro;
  }
  
  public void registerLookupMacro(KeyStroke paramKeyStroke)
  {
    AbstractAction local6 = new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        HanziLookup.this.lookupButton.doClick();
      }
    };
    registerMacro(paramKeyStroke, this.lookupMacro, "hanzilookup.lookup", local6);
    this.lookupMacro = paramKeyStroke;
  }
  
  public void registerClearMacro(KeyStroke paramKeyStroke)
  {
    AbstractAction local7 = new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        HanziLookup.this.clearButton.doClick();
      }
    };
    registerMacro(paramKeyStroke, this.clearMacro, "hanzilookup.clear", local7);
    this.clearMacro = paramKeyStroke;
  }
  
  public void registerUndoMacro(KeyStroke paramKeyStroke)
  {
    AbstractAction local8 = new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        HanziLookup.this.undoButton.doClick();
      }
    };
    registerMacro(paramKeyStroke, this.undoMacro, "hanzilookup.undo", local8);
    this.undoMacro = paramKeyStroke;
  }
  
  private void registerMacro(KeyStroke paramKeyStroke1, KeyStroke paramKeyStroke2, String paramString, Action paramAction)
  {
    InputMap localInputMap = getInputMap(2);
    synchronized (localInputMap)
    {
      if (null != paramKeyStroke2) {
        localInputMap.remove(paramKeyStroke2);
      }
      localInputMap.put(paramKeyStroke1, paramString);
    }
    ??? = getActionMap();
    synchronized (???)
    {
      ((ActionMap)???).put(paramString, paramAction);
    }
  }
  
  public static class CharacterSelectionEvent
    extends EventObject
  {
    private char character;
    
    private CharacterSelectionEvent(Object paramObject, char paramChar)
    {
      super();
      this.character = paramChar;
    }
    
    public char getSelectedCharacter()
    {
      return this.character;
    }
    
    CharacterSelectionEvent(Object paramObject, char paramChar, HanziLookup.1 param1)
    {
      this(paramObject, paramChar);
    }
  }
  
  public static abstract interface CharacterSelectionListener
  {
    public abstract void characterSelected(HanziLookup.CharacterSelectionEvent paramCharacterSelectionEvent);
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     hanzilookup.HanziLookup
 * JD-Core Version:    0.7.0.1
 */