package kiang.swing;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.swing.JList;

public class JClickableList
  extends JList
{
  private Set listClickedListeners = new LinkedHashSet();
  
  public JClickableList()
  {
    initEventListeners();
  }
  
  private void initEventListeners()
  {
    addMouseListener(new MouseAdapter()
    {
      public void mouseReleased(MouseEvent paramAnonymousMouseEvent)
      {
        int i = JClickableList.this.locationToIndex(paramAnonymousMouseEvent.getPoint());
        if ((i > -1) && (JClickableList.this.isSelectedIndex(i))) {
          JClickableList.this.notifyClickListeners();
        }
      }
    });
    addKeyListener(new KeyAdapter()
    {
      public void keyPressed(KeyEvent paramAnonymousKeyEvent)
      {
        if (10 == paramAnonymousKeyEvent.getKeyCode())
        {
          int i = JClickableList.this.getSelectedIndex();
          if (i > -1) {
            JClickableList.this.notifyClickListeners();
          }
        }
      }
    });
  }
  
  private void notifyClickListeners()
  {
    synchronized (this.listClickedListeners)
    {
      Iterator localIterator = this.listClickedListeners.iterator();
      while (localIterator.hasNext())
      {
        ListClickedListener localListClickedListener = (ListClickedListener)localIterator.next();
        localListClickedListener.listClicked(new ListClickedEvent(null));
      }
    }
  }
  
  public void addListClickedListener(ListClickedListener paramListClickedListener)
  {
    if (null != paramListClickedListener) {
      synchronized (this.listClickedListeners)
      {
        this.listClickedListeners.add(paramListClickedListener);
      }
    }
  }
  
  public synchronized void removeListClickedListener(ListClickedListener paramListClickedListener)
  {
    if (null != paramListClickedListener) {
      synchronized (this.listClickedListeners)
      {
        this.listClickedListeners.remove(paramListClickedListener);
      }
    }
  }
  
  public static abstract interface ListClickedListener
  {
    public abstract void listClicked(JClickableList.ListClickedEvent paramListClickedEvent);
  }
  
  public class ListClickedEvent
    extends EventObject
  {
    private ListClickedEvent()
    {
      super();
    }
    
    ListClickedEvent(JClickableList.1 param1)
    {
      this();
    }
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     kiang.swing.JClickableList
 * JD-Core Version:    0.7.0.1
 */