package hanzilookup.data;

import java.awt.geom.CubicCurve2D;
import java.awt.geom.CubicCurve2D.Double;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import kiang.awt.geom.CurveUtils;
import kiang.util.PriorityList;

public class StrokesMatcher
{
  private CharacterDescriptor inputCharacter;
  private CharacterDescriptor compareTo;
  private double[][] scoreMatrix;
  private boolean searchTraditional;
  private boolean searchSimplified;
  private double looseness;
  private CharacterMatchCollector matches;
  private boolean running;
  private StrokesDataSource strokesDataSource;
  static final double CORRECT_NUM_STROKES_BONUS = 0.1D;
  static final int CORRECT_NUM_STROKES_CAP = 10;
  private static final double SKIP_PENALTY_MULTIPLIER = 1.75D;
  private static final double[] DIRECTION_SCORE_TABLE = ;
  private static final double[] LENGTH_SCORE_TABLE = initLengthScoreTable();
  
  public StrokesMatcher(CharacterDescriptor paramCharacterDescriptor, boolean paramBoolean1, boolean paramBoolean2, double paramDouble, int paramInt, StrokesDataSource paramStrokesDataSource)
  {
    this.inputCharacter = paramCharacterDescriptor;
    this.compareTo = new CharacterDescriptor();
    this.searchTraditional = paramBoolean1;
    this.searchSimplified = paramBoolean2;
    this.looseness = paramDouble;
    this.running = true;
    this.strokesDataSource = paramStrokesDataSource;
    this.matches = new CharacterMatchCollector(paramInt, null);
    initScoreMatrix();
  }
  
  public Character[] doMatching()
  {
    int i = this.inputCharacter.getStrokeCount();
    int j = this.inputCharacter.getSubStrokeCount();
    int k = getStrokesRange(i, this.looseness);
    int m = Math.max(i - k, 1);
    int n = Math.min(i + k, 48);
    int i1 = getSubStrokesRange(j, this.looseness);
    StrokesDataSource.StrokesDataScanner localStrokesDataScanner = this.strokesDataSource.getStrokesScanner(this.searchTraditional, this.searchSimplified, m, n);
    try
    {
      while (localStrokesDataScanner.loadNextCharacterStrokeData(this.compareTo))
      {
        CharacterMatch localCharacterMatch = compareToNext(i, j, i1);
        this.matches.addMatch(localCharacterMatch);
      }
    }
    catch (IOException localIOException)
    {
      System.err.println("Error running strokes comparison!");
      localIOException.printStackTrace();
    }
    Character[] arrayOfCharacter = this.matches.getMatches();
    if (isRunning()) {
      return arrayOfCharacter;
    }
    return null;
  }
  
  private int getStrokesRange(int paramInt, double paramDouble)
  {
    if (paramDouble == 0.0D) {
      return 0;
    }
    if (paramDouble == 1.0D) {
      return 48;
    }
    double d1 = 0.35D;
    double d2 = paramInt * 0.4D;
    double d3 = 0.6D;
    double d4 = paramInt;
    double[] arrayOfDouble = new double[1];
    CubicCurve2D.Double localDouble = new CubicCurve2D.Double(0.0D, 0.0D, d1, d2, d3, d4, 1.0D, 48.0D);
    CurveUtils.solveCubicCurveForX(localDouble, paramDouble, arrayOfDouble);
    double d5 = arrayOfDouble[0];
    return (int)Math.round(CurveUtils.getPointOnCubicCurve(localDouble, d5).getY());
  }
  
  private int getSubStrokesRange(int paramInt, double paramDouble)
  {
    if (paramDouble == 1.0D) {
      return 64;
    }
    double d1 = paramInt * 0.25D;
    double d2 = 0.4D;
    double d3 = 1.5D * d1;
    double d4 = 0.75D;
    double d5 = 1.5D * d3;
    double[] arrayOfDouble = new double[1];
    CubicCurve2D.Double localDouble = new CubicCurve2D.Double(0.0D, d1, d2, d3, d4, d5, 1.0D, 64.0D);
    CurveUtils.solveCubicCurveForX(localDouble, paramDouble, arrayOfDouble);
    double d6 = arrayOfDouble[0];
    return (int)Math.round(CurveUtils.getPointOnCubicCurve(localDouble, d6).getY());
  }
  
  private void initScoreMatrix()
  {
    int i = 65;
    this.scoreMatrix = new double[i][i];
    for (int j = 0; j < i; j++)
    {
      double d = -0.5775D * j;
      this.scoreMatrix[j][0] = d;
      this.scoreMatrix[0][j] = d;
    }
  }
  
  private CharacterMatch compareToNext(int paramInt1, int paramInt2, int paramInt3)
  {
    Character localCharacter = this.compareTo.getCharacter();
    int i = this.compareTo.getCharacterType();
    int j = this.compareTo.getStrokeCount();
    int k = this.compareTo.getSubStrokeCount();
    double d1 = computeMatchScore(paramInt2, k, paramInt3);
    if ((paramInt1 == j) && (paramInt1 < 10))
    {
      double d2 = 0.1D * (Math.max(10 - paramInt1, 0) / 10.0D);
      d1 += d2 * d1;
    }
    return new CharacterMatch(localCharacter, d1);
  }
  
  private double computeMatchScore(int paramInt1, int paramInt2, int paramInt3)
  {
    double[] arrayOfDouble1 = this.inputCharacter.getDirections();
    double[] arrayOfDouble2 = this.inputCharacter.getLengths();
    double[] arrayOfDouble3 = this.compareTo.getDirections();
    double[] arrayOfDouble4 = this.compareTo.getLengths();
    for (int i = 0; i < paramInt1; i++)
    {
      double d1 = arrayOfDouble1[i];
      double d2 = arrayOfDouble2[i];
      for (int j = 0; j < paramInt2; j++)
      {
        double d3 = (-1.0D / 0.0D);
        if (Math.abs(i - j) <= paramInt3)
        {
          double d4 = arrayOfDouble3[j];
          double d5 = arrayOfDouble4[j];
          double d6 = this.scoreMatrix[i][(j + 1)] - d2 * 1.75D;
          double d7 = this.scoreMatrix[(i + 1)][j] - d5 * 1.75D;
          double d8 = Math.max(d6, d7);
          double d9 = computeSubStrokeScore(d1, d2, d4, d5);
          double d10 = this.scoreMatrix[i][j];
          d3 = Math.max(d10 + d9, d8);
        }
        this.scoreMatrix[(i + 1)][(j + 1)] = d3;
      }
    }
    return this.scoreMatrix[paramInt1][paramInt2];
  }
  
  private double computeSubStrokeScore(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    double d1 = getDirectionScore(paramDouble1, paramDouble3, paramDouble2);
    double d2 = getLengthScore(paramDouble2, paramDouble4);
    double d3 = d2 * d1;
    return d3;
  }
  
  private static double[] initDirectionScoreTable()
  {
    CubicCurve2D.Double localDouble = new CubicCurve2D.Double(0.0D, 1.0D, 0.5D, 1.0D, 0.25D, -2.0D, 1.0D, 1.0D);
    return initCubicCurveScoreTable(localDouble, 100);
  }
  
  private static double[] initLengthScoreTable()
  {
    CubicCurve2D.Double localDouble = new CubicCurve2D.Double(0.0D, 0.0D, 0.25D, 1.0D, 0.75D, 1.0D, 1.0D, 1.0D);
    return initCubicCurveScoreTable(localDouble, 100);
  }
  
  private static double[] initCubicCurveScoreTable(CubicCurve2D paramCubicCurve2D, int paramInt)
  {
    double d1 = paramCubicCurve2D.getX1();
    double d2 = paramCubicCurve2D.getX2();
    double d3 = d2 - d1;
    double d4 = d1;
    double d5 = d3 / paramInt;
    double[] arrayOfDouble1 = new double[paramInt];
    double[] arrayOfDouble2 = new double[1];
    for (int i = 0; i < paramInt; i++)
    {
      CurveUtils.solveCubicCurveForX(paramCubicCurve2D, Math.min(d4, d2), arrayOfDouble2);
      double d6 = arrayOfDouble2[0];
      arrayOfDouble1[i] = CurveUtils.getPointOnCubicCurve(paramCubicCurve2D, d6).getY();
      d4 += d5;
    }
    return arrayOfDouble1;
  }
  
  private double getDirectionScore(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    double d1 = Math.abs(paramDouble1 - paramDouble2);
    if (d1 > 3.141592653589793D) {
      d1 = 6.283185307179586D - d1;
    }
    int i = (int)(d1 / 3.141592653589793D * (DIRECTION_SCORE_TABLE.length - 1));
    double d2 = DIRECTION_SCORE_TABLE[i];
    double d3 = Math.min(1.0D, 1.0D - d2);
    double d4 = d3 * (-4.0D * paramDouble3 + 1.0D);
    if (d4 > 0.0D) {
      d2 += d4;
    }
    return d2;
  }
  
  private double getLengthScore(double paramDouble1, double paramDouble2)
  {
    double d1 = paramDouble1 < paramDouble2 ? paramDouble1 / paramDouble2 : paramDouble2 / paramDouble1;
    int i = (int)(d1 * (LENGTH_SCORE_TABLE.length - 1));
    double d2 = LENGTH_SCORE_TABLE[i];
    return d2;
  }
  
  private synchronized boolean isRunning()
  {
    return this.running;
  }
  
  public synchronized void stop()
  {
    this.running = false;
  }
  
  private static class CharacterMatchCollector
  {
    private Map matchMap = new HashMap();
    private PriorityList matches = new PriorityList();
    private int maxSize;
    
    private CharacterMatchCollector(int paramInt)
    {
      this.maxSize = paramInt;
    }
    
    private boolean addMatch(StrokesMatcher.CharacterMatch paramCharacterMatch)
    {
      StrokesMatcher.CharacterMatch localCharacterMatch1 = (StrokesMatcher.CharacterMatch)this.matchMap.get(StrokesMatcher.CharacterMatch.access$300(paramCharacterMatch));
      if (null != localCharacterMatch1) {
        if (StrokesMatcher.CharacterMatch.access$400(paramCharacterMatch) > StrokesMatcher.CharacterMatch.access$400(localCharacterMatch1)) {
          this.matches.remove(localCharacterMatch1);
        } else {
          return false;
        }
      }
      if (this.matches.size() >= this.maxSize)
      {
        StrokesMatcher.CharacterMatch localCharacterMatch2 = (StrokesMatcher.CharacterMatch)this.matches.getLast();
        if (StrokesMatcher.CharacterMatch.access$400(paramCharacterMatch) > StrokesMatcher.CharacterMatch.access$400(localCharacterMatch2))
        {
          this.matches.removeLast();
          this.matchMap.remove(StrokesMatcher.CharacterMatch.access$300(localCharacterMatch2));
        }
        else
        {
          return false;
        }
      }
      this.matchMap.put(StrokesMatcher.CharacterMatch.access$300(paramCharacterMatch), paramCharacterMatch);
      this.matches.add(paramCharacterMatch);
      return true;
    }
    
    private Character[] getMatches()
    {
      Character[] arrayOfCharacter = new Character[this.matches.size()];
      Iterator localIterator = this.matches.iterator();
      for (int i = 0; localIterator.hasNext(); i++)
      {
        StrokesMatcher.CharacterMatch localCharacterMatch = (StrokesMatcher.CharacterMatch)localIterator.next();
        arrayOfCharacter[i] = StrokesMatcher.CharacterMatch.access$300(localCharacterMatch);
      }
      return arrayOfCharacter;
    }
    
    CharacterMatchCollector(int paramInt, StrokesMatcher.1 param1)
    {
      this(paramInt);
    }
  }
  
  private static class CharacterMatch
    implements Comparable
  {
    private Character character;
    private double score;
    
    public CharacterMatch(Character paramCharacter, double paramDouble)
    {
      this.character = paramCharacter;
      this.score = paramDouble;
    }
    
    public int compareTo(Object paramObject)
    {
      CharacterMatch localCharacterMatch = (CharacterMatch)paramObject;
      double d1 = this.score;
      double d2 = localCharacterMatch.score;
      if (d1 < d2) {
        return 1;
      }
      if (d1 > d2) {
        return -1;
      }
      return 0;
    }
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     hanzilookup.data.StrokesMatcher
 * JD-Core Version:    0.7.0.1
 */