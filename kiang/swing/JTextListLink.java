package kiang.swing;

import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;

public class JTextListLink
{
  private JTextField textField;
  private JList list;
  
  public JTextListLink(JTextField paramJTextField, JList paramJList, boolean paramBoolean)
  {
    this.textField = paramJTextField;
    this.list = paramJList;
    UpdateListener localUpdateListener = new UpdateListener(null);
    if (paramBoolean) {
      localUpdateListener.matchListToText();
    } else {
      localUpdateListener.matchTextToList();
    }
    this.textField.getDocument().addDocumentListener(localUpdateListener);
    this.list.addListSelectionListener(localUpdateListener);
  }
  
  public static void main(String[] paramArrayOfString)
  {
    JFrame localJFrame = new JFrame();
    String[] arrayOfString = { "america", "aardvark", "abate", "alabaster", "asynchronous", "amen", "alimony", "ack", "acknowledge", "ache", "ape", "antidisestablishmentarianism" };
    JTextField localJTextField = new JTextField();
    JList localJList = new JList(arrayOfString);
    JTextListLink localJTextListLink = new JTextListLink(localJTextField, localJList, true);
    localJFrame.getContentPane().add(localJTextField, "North");
    localJFrame.getContentPane().add(localJList, "Center");
    localJFrame.setDefaultCloseOperation(3);
    localJFrame.pack();
    localJFrame.setVisible(true);
  }
  
  private class UpdateListener
    implements ListSelectionListener, DocumentListener
  {
    private boolean isAdjusting = false;
    
    private UpdateListener() {}
    
    private synchronized boolean toggleIsAdjustingLock()
    {
      this.isAdjusting = (!this.isAdjusting);
      return this.isAdjusting;
    }
    
    public void valueChanged(ListSelectionEvent paramListSelectionEvent)
    {
      matchTextToList();
    }
    
    public void insertUpdate(DocumentEvent paramDocumentEvent)
    {
      matchListToText();
    }
    
    public void removeUpdate(DocumentEvent paramDocumentEvent)
    {
      matchListToText();
    }
    
    public void changedUpdate(DocumentEvent paramDocumentEvent)
    {
      matchListToText();
    }
    
    private int getStringMatchScore(String paramString1, String paramString2)
    {
      int i = Math.min(paramString1.length(), paramString2.length());
      paramString1 = paramString1.substring(0, i).toUpperCase();
      paramString2 = paramString2.substring(0, i).toUpperCase();
      int j = 0;
      for (int k = 0; (k < i) && (paramString1.charAt(k) == paramString2.charAt(k)); k++) {
        j++;
      }
      return j;
    }
    
    private boolean matchListToText()
    {
      if (toggleIsAdjustingLock())
      {
        String str = JTextListLink.this.textField.getText();
        ListModel localListModel = JTextListLink.this.list.getModel();
        int i = localListModel.getSize();
        if (i > 0)
        {
          int j = getStringMatchScore(str, localListModel.getElementAt(0).toString());
          int k = 0;
          for (int m = 1; m < i; m++)
          {
            int n = getStringMatchScore(str, localListModel.getElementAt(m).toString());
            if (n > j)
            {
              j = n;
              k = m;
            }
          }
          JTextListLink.this.list.setSelectedIndex(k);
          JTextListLink.this.list.ensureIndexIsVisible(k);
        }
        return true;
      }
      return false;
    }
    
    private boolean matchTextToList()
    {
      if (toggleIsAdjustingLock())
      {
        Object localObject = JTextListLink.this.list.getSelectedValue();
        if (null != localObject) {
          JTextListLink.this.textField.setText(localObject.toString());
        } else {
          JTextListLink.this.textField.setText("");
        }
        return true;
      }
      return false;
    }
    
    UpdateListener(JTextListLink.1 param1)
    {
      this();
    }
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     kiang.swing.JTextListLink
 * JD-Core Version:    0.7.0.1
 */