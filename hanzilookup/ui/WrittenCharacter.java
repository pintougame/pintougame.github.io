package hanzilookup.ui;

import hanzilookup.data.CharacterDescriptor;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WrittenCharacter
{
  private double leftX;
  private double rightX;
  private double topY;
  private double bottomY;
  private List strokeList = new ArrayList();
  
  public WrittenCharacter()
  {
    resetEdges();
  }
  
  public List getStrokeList()
  {
    return this.strokeList;
  }
  
  public void addStroke(WrittenStroke paramWrittenStroke)
  {
    this.strokeList.add(paramWrittenStroke);
  }
  
  public void clear()
  {
    this.strokeList.clear();
    resetEdges();
  }
  
  private void resetEdges()
  {
    this.leftX = (1.0D / 0.0D);
    this.rightX = (-1.0D / 0.0D);
    this.topY = (1.0D / 0.0D);
    this.bottomY = (-1.0D / 0.0D);
  }
  
  public void analyzeAndMark()
  {
    Iterator localIterator = this.strokeList.iterator();
    while (localIterator.hasNext())
    {
      WrittenStroke localWrittenStroke = (WrittenStroke)localIterator.next();
      if (!localWrittenStroke.isAnalyzed()) {
        localWrittenStroke.analyzeAndMark();
      }
    }
  }
  
  public CharacterDescriptor buildCharacterDescriptor()
  {
    int i = this.strokeList.size();
    int j = 0;
    CharacterDescriptor localCharacterDescriptor = new CharacterDescriptor();
    double[] arrayOfDouble1 = localCharacterDescriptor.getDirections();
    double[] arrayOfDouble2 = localCharacterDescriptor.getLengths();
    Iterator localIterator1 = this.strokeList.iterator();
    while ((localIterator1.hasNext()) && (j < 64))
    {
      WrittenStroke localWrittenStroke = (WrittenStroke)localIterator1.next();
      List localList = localWrittenStroke.getSubStrokes();
      Iterator localIterator2 = localList.iterator();
      while ((localIterator2.hasNext()) && (j < 64))
      {
        SubStrokeDescriptor localSubStrokeDescriptor = (SubStrokeDescriptor)localIterator2.next();
        arrayOfDouble1[j] = localSubStrokeDescriptor.direction;
        arrayOfDouble2[j] = localSubStrokeDescriptor.length;
        j++;
      }
    }
    localCharacterDescriptor.setStrokeCount(i);
    localCharacterDescriptor.setSubStrokeCount(j);
    return localCharacterDescriptor;
  }
  
  public static class SubStrokeDescriptor
  {
    private double direction;
    private double length;
    
    private SubStrokeDescriptor(double paramDouble1, double paramDouble2)
    {
      this.direction = paramDouble1;
      this.length = paramDouble2;
    }
    
    public double getDirection()
    {
      return this.direction;
    }
    
    public double getLength()
    {
      return this.length;
    }
    
    SubStrokeDescriptor(double paramDouble1, double paramDouble2, WrittenCharacter.1 param1)
    {
      this(paramDouble1, paramDouble2);
    }
  }
  
  public class WrittenPoint
    extends Point
  {
    private int subStrokeIndex;
    private boolean isPivot;
    
    public WrittenPoint(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public int getSubStrokeIndex()
    {
      return this.subStrokeIndex;
    }
    
    private void setSubStrokeIndex(int paramInt)
    {
      this.subStrokeIndex = paramInt;
    }
    
    public boolean isPivot()
    {
      return this.isPivot;
    }
    
    private void setIsPivot(boolean paramBoolean)
    {
      this.isPivot = paramBoolean;
    }
    
    private double getDistanceNormalized(WrittenPoint paramWrittenPoint)
    {
      double d1 = WrittenCharacter.this.rightX - WrittenCharacter.this.leftX;
      double d2 = WrittenCharacter.this.bottomY - WrittenCharacter.this.topY;
      double d3 = d1 > d2 ? d1 * d1 : d2 * d2;
      double d4 = Math.sqrt(d3 + d3);
      double d5 = distance(paramWrittenPoint) / d4;
      d5 = Math.min(d5, 1.0D);
      return d5;
    }
    
    private double getDirection(WrittenPoint paramWrittenPoint)
    {
      double d1 = getX() - paramWrittenPoint.getX();
      double d2 = getY() - paramWrittenPoint.getY();
      double d3 = 3.141592653589793D - Math.atan2(d2, d1);
      return d3;
    }
  }
  
  public class WrittenStroke
  {
    private List pointList = new ArrayList();
    private boolean isAnalyzed = false;
    private static final double MIN_SEGMENT_LENGTH = 12.5D;
    private static final double MAX_LOCAL_LENGTH_RATIO = 1.1D;
    private static final double MAX_RUNNING_LENGTH_RATIO = 1.09D;
    
    public WrittenStroke() {}
    
    public List getPointList()
    {
      return this.pointList;
    }
    
    public boolean isAnalyzed()
    {
      return this.isAnalyzed;
    }
    
    public void addPoint(WrittenCharacter.WrittenPoint paramWrittenPoint)
    {
      int i = (int)paramWrittenPoint.getX();
      int j = (int)paramWrittenPoint.getY();
      WrittenCharacter.this.leftX = Math.min(i, WrittenCharacter.this.leftX);
      WrittenCharacter.this.rightX = Math.max(i, WrittenCharacter.this.rightX);
      WrittenCharacter.this.topY = Math.min(j, WrittenCharacter.this.topY);
      WrittenCharacter.this.bottomY = Math.max(j, WrittenCharacter.this.bottomY);
      this.pointList.add(paramWrittenPoint);
    }
    
    public List getSubStrokes()
    {
      if (!this.isAnalyzed) {
        analyzeAndMark();
      }
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = this.pointList.iterator();
      Object localObject = (WrittenCharacter.WrittenPoint)localIterator.next();
      while (localIterator.hasNext())
      {
        WrittenCharacter.WrittenPoint localWrittenPoint = (WrittenCharacter.WrittenPoint)localIterator.next();
        if (localWrittenPoint.isPivot())
        {
          double d1 = ((WrittenCharacter.WrittenPoint)localObject).getDirection(localWrittenPoint);
          double d2 = ((WrittenCharacter.WrittenPoint)localObject).getDistanceNormalized(localWrittenPoint);
          WrittenCharacter.SubStrokeDescriptor localSubStrokeDescriptor = new WrittenCharacter.SubStrokeDescriptor(d1, d2, null);
          localArrayList.add(localSubStrokeDescriptor);
          localObject = localWrittenPoint;
        }
      }
      return localArrayList;
    }
    
    private void analyzeAndMark()
    {
      Iterator localIterator = this.pointList.iterator();
      Object localObject1 = (WrittenCharacter.WrittenPoint)localIterator.next();
      Object localObject2 = localObject1;
      Object localObject3 = (WrittenCharacter.WrittenPoint)localIterator.next();
      ((WrittenCharacter.WrittenPoint)localObject1).setIsPivot(true);
      int i = 1;
      ((WrittenCharacter.WrittenPoint)localObject1).setSubStrokeIndex(i);
      ((WrittenCharacter.WrittenPoint)localObject3).setSubStrokeIndex(i);
      double d1 = ((WrittenCharacter.WrittenPoint)localObject1).distance((Point2D)localObject3);
      double d2 = d1;
      while (localIterator.hasNext())
      {
        WrittenCharacter.WrittenPoint localWrittenPoint = (WrittenCharacter.WrittenPoint)localIterator.next();
        double d3 = ((WrittenCharacter.WrittenPoint)localObject3).distance(localWrittenPoint);
        d1 += d3;
        d2 += d3;
        if ((d1 >= 1.1D * localObject2.distance(localWrittenPoint)) || (d2 >= 1.09D * ((WrittenCharacter.WrittenPoint)localObject1).distance(localWrittenPoint)))
        {
          if ((localObject2.isPivot()) && (localObject2.distance((Point2D)localObject3) < 12.5D))
          {
            localObject2.setIsPivot(false);
            localObject2.setSubStrokeIndex(i - 1);
          }
          else
          {
            i++;
          }
          ((WrittenCharacter.WrittenPoint)localObject3).setIsPivot(true);
          d2 = d3;
          localObject1 = localObject3;
        }
        d1 = d3;
        localObject2 = localObject3;
        localObject3 = localWrittenPoint;
        ((WrittenCharacter.WrittenPoint)localObject3).setSubStrokeIndex(i);
      }
      ((WrittenCharacter.WrittenPoint)localObject3).setIsPivot(true);
      if ((localObject2.isPivot()) && (localObject2.distance((Point2D)localObject3) < 12.5D) && (localObject2 != this.pointList.get(0)))
      {
        localObject2.setIsPivot(false);
        ((WrittenCharacter.WrittenPoint)localObject3).setSubStrokeIndex(i - 1);
      }
      this.isAnalyzed = true;
    }
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     hanzilookup.ui.WrittenCharacter
 * JD-Core Version:    0.7.0.1
 */