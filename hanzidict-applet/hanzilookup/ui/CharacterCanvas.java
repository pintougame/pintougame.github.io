package hanzilookup.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

public class CharacterCanvas
  extends JLabel
  implements MouseInputListener
{
  private WrittenCharacter inputCharacter = new WrittenCharacter();
  private WrittenCharacter.WrittenStroke currentStroke;
  private WrittenCharacter.WrittenPoint previousPoint;
  private Stroke paintStroke = new BasicStroke(3.0F, 1, 1);
  private Set strokesListeners = new LinkedHashSet();
  private static final double MIN_STROKE_SEGMENT_LENGTH = 5.0D;
  
  public CharacterCanvas()
  {
    setOpaque(true);
    setBackground(Color.WHITE);
    addMouseListener(this);
    addMouseMotionListener(this);
  }
  
  public void clear()
  {
    this.inputCharacter.clear();
    this.currentStroke = null;
  }
  
  public void undo()
  {
    List localList = this.inputCharacter.getStrokeList();
    if (localList.size() > 0) {
      localList.remove(localList.size() - 1);
    }
  }
  
  public WrittenCharacter getCharacter()
  {
    return this.inputCharacter;
  }
  
  public void mousePressed(MouseEvent paramMouseEvent)
  {
    if (SwingUtilities.getRoot(this).isFocusOwner()) {
      requestFocus();
    }
    WrittenCharacter tmp23_20 = this.inputCharacter;
    tmp23_20.getClass();
    this.previousPoint = new WrittenCharacter.WrittenPoint(tmp23_20, paramMouseEvent.getX(), paramMouseEvent.getY());
  }
  
  public void mouseDragged(MouseEvent paramMouseEvent)
  {
    WrittenCharacter tmp8_5 = this.inputCharacter;
    tmp8_5.getClass();
    WrittenCharacter.WrittenPoint localWrittenPoint = new WrittenCharacter.WrittenPoint(tmp8_5, paramMouseEvent.getX(), paramMouseEvent.getY());
    if ((null != this.previousPoint) && (this.previousPoint.distance(localWrittenPoint) >= 5.0D))
    {
      if (null == this.currentStroke)
      {
        WrittenCharacter tmp65_62 = this.inputCharacter;
        tmp65_62.getClass();
        this.currentStroke = new WrittenCharacter.WrittenStroke(tmp65_62);
        this.currentStroke.addPoint(this.previousPoint);
      }
      this.currentStroke.addPoint(localWrittenPoint);
      this.previousPoint = localWrittenPoint;
      repaint();
    }
  }
  
  public void mouseReleased(MouseEvent paramMouseEvent)
  {
    mouseDragged(paramMouseEvent);
    if (null != this.currentStroke)
    {
      this.inputCharacter.addStroke(this.currentStroke);
      this.previousPoint = null;
      this.currentStroke = null;
      repaint();
    }
    notifyStrokesListeners();
  }
  
  public void mouseClicked(MouseEvent paramMouseEvent) {}
  
  public void mouseEntered(MouseEvent paramMouseEvent) {}
  
  public void mouseExited(MouseEvent paramMouseEvent) {}
  
  public void mouseMoved(MouseEvent paramMouseEvent) {}
  
  public void paintComponent(Graphics paramGraphics)
  {
    super.paintComponent(paramGraphics);
    Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
    localGraphics2D.setStroke(this.paintStroke);
    localGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    if (null != this.currentStroke) {
      paintStroke(this.currentStroke, paramGraphics);
    }
    paintCharacter(paramGraphics);
  }
  
  protected void paintCharacter(Graphics paramGraphics)
  {
    List localList = this.inputCharacter.getStrokeList();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      WrittenCharacter.WrittenStroke localWrittenStroke = (WrittenCharacter.WrittenStroke)localIterator.next();
      paintStroke(localWrittenStroke, paramGraphics);
    }
  }
  
  protected void paintStroke(WrittenCharacter.WrittenStroke paramWrittenStroke, Graphics paramGraphics)
  {
    paramGraphics.setColor(Color.BLACK);
    Iterator localIterator = paramWrittenStroke.getPointList().iterator();
    WrittenCharacter.WrittenPoint localWrittenPoint;
    for (Object localObject = (WrittenCharacter.WrittenPoint)localIterator.next(); localIterator.hasNext(); localObject = localWrittenPoint)
    {
      localWrittenPoint = (WrittenCharacter.WrittenPoint)localIterator.next();
      paramGraphics.drawLine((int)((WrittenCharacter.WrittenPoint)localObject).getX(), (int)((WrittenCharacter.WrittenPoint)localObject).getY(), (int)localWrittenPoint.getX(), (int)localWrittenPoint.getY());
    }
  }
  
  public void addStrokesListener(StrokesListener paramStrokesListener)
  {
    if (null != paramStrokesListener) {
      synchronized (this.strokesListeners)
      {
        this.strokesListeners.add(paramStrokesListener);
      }
    }
  }
  
  public void removeStrokesListener(StrokesListener paramStrokesListener)
  {
    if (null != paramStrokesListener) {
      synchronized (this.strokesListeners)
      {
        this.strokesListeners.remove(paramStrokesListener);
      }
    }
  }
  
  private void notifyStrokesListeners()
  {
    synchronized (this.strokesListeners)
    {
      Iterator localIterator = this.strokesListeners.iterator();
      while (localIterator.hasNext())
      {
        StrokesListener localStrokesListener = (StrokesListener)localIterator.next();
        localStrokesListener.strokeFinished(new StrokeEvent(null));
      }
    }
  }
  
  public class StrokeEvent
    extends EventObject
  {
    private StrokeEvent()
    {
      super();
    }
    
    StrokeEvent(CharacterCanvas.1 param1)
    {
      this();
    }
  }
  
  public static abstract interface StrokesListener
  {
    public abstract void strokeFinished(CharacterCanvas.StrokeEvent paramStrokeEvent);
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     hanzilookup.ui.CharacterCanvas
 * JD-Core Version:    0.7.0.1
 */