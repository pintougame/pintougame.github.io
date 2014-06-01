package kiang.awt.geom;

import java.awt.geom.CubicCurve2D;
import java.awt.geom.CubicCurve2D.Double;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.QuadCurve2D.Double;

public class CurveUtils
{
  public static Point2D getPointOnQuadCurve(QuadCurve2D paramQuadCurve2D, double paramDouble)
  {
    if (null == paramQuadCurve2D) {
      throw new NullPointerException("curve must be non-null!");
    }
    if ((paramDouble < 0.0D) || (paramDouble > 1.0D)) {
      throw new IllegalArgumentException("t must be between 0 and 1!");
    }
    double d1 = getQuadAx(paramQuadCurve2D);
    double d2 = getQuadBx(paramQuadCurve2D);
    double d3 = getQuadAy(paramQuadCurve2D);
    double d4 = getQuadBy(paramQuadCurve2D);
    double d5 = paramDouble * paramDouble;
    double d6 = d1 * d5 + d2 * paramDouble + paramQuadCurve2D.getX1();
    double d7 = d3 * d5 + d4 * paramDouble + paramQuadCurve2D.getY1();
    return new Point2D.Double(d6, d7);
  }
  
  public static int solveQuadCurveForX(QuadCurve2D paramQuadCurve2D, double paramDouble, double[] paramArrayOfDouble)
  {
    double d1 = getQuadAx(paramQuadCurve2D);
    double d2 = getQuadBx(paramQuadCurve2D);
    double d3 = paramQuadCurve2D.getX1() - paramDouble;
    double[] arrayOfDouble = { d3, d2, d1 };
    int i = QuadCurve2D.solveQuadratic(arrayOfDouble);
    return copyValidSolutions(i, arrayOfDouble, paramArrayOfDouble);
  }
  
  public static int solveQuadCurveForY(QuadCurve2D paramQuadCurve2D, double paramDouble, double[] paramArrayOfDouble)
  {
    double d1 = getQuadAy(paramQuadCurve2D);
    double d2 = getQuadBy(paramQuadCurve2D);
    double d3 = paramQuadCurve2D.getY1() - paramDouble;
    double[] arrayOfDouble = { d3, d2, d1 };
    int i = QuadCurve2D.solveQuadratic(arrayOfDouble, paramArrayOfDouble);
    return copyValidSolutions(i, arrayOfDouble, paramArrayOfDouble);
  }
  
  private static double getQuadAx(QuadCurve2D paramQuadCurve2D)
  {
    return paramQuadCurve2D.getX1() - 2.0D * paramQuadCurve2D.getCtrlX() + paramQuadCurve2D.getX2();
  }
  
  private static double getQuadBx(QuadCurve2D paramQuadCurve2D)
  {
    return 2.0D * (-paramQuadCurve2D.getX1() + paramQuadCurve2D.getCtrlX());
  }
  
  private static double getQuadAy(QuadCurve2D paramQuadCurve2D)
  {
    return paramQuadCurve2D.getY1() - 2.0D * paramQuadCurve2D.getCtrlY() + paramQuadCurve2D.getY2();
  }
  
  private static double getQuadBy(QuadCurve2D paramQuadCurve2D)
  {
    return 2.0D * (-paramQuadCurve2D.getY1() + paramQuadCurve2D.getCtrlY());
  }
  
  public static Point2D getPointOnCubicCurve(CubicCurve2D paramCubicCurve2D, double paramDouble)
  {
    if (null == paramCubicCurve2D) {
      throw new NullPointerException("curve must be non-null!");
    }
    if ((paramDouble < 0.0D) || (paramDouble > 1.0D)) {
      throw new IllegalArgumentException("t must be between 0 and 1!");
    }
    double d1 = getCubicAx(paramCubicCurve2D);
    double d2 = getCubicBx(paramCubicCurve2D);
    double d3 = getCubicCx(paramCubicCurve2D);
    double d4 = getCubicAy(paramCubicCurve2D);
    double d5 = getCubicBy(paramCubicCurve2D);
    double d6 = getCubicCy(paramCubicCurve2D);
    double d7 = paramDouble * paramDouble;
    double d8 = paramDouble * d7;
    double d9 = d1 * d8 + d2 * d7 + d3 * paramDouble + paramCubicCurve2D.getX1();
    double d10 = d4 * d8 + d5 * d7 + d6 * paramDouble + paramCubicCurve2D.getY1();
    return new Point2D.Double(d9, d10);
  }
  
  public static int solveCubicCurveForX(CubicCurve2D paramCubicCurve2D, double paramDouble, double[] paramArrayOfDouble)
  {
    double d1 = getCubicAx(paramCubicCurve2D);
    double d2 = getCubicBx(paramCubicCurve2D);
    double d3 = getCubicCx(paramCubicCurve2D);
    double d4 = paramCubicCurve2D.getX1() - paramDouble;
    double[] arrayOfDouble = { d4, d3, d2, d1 };
    int i = CubicCurve2D.solveCubic(arrayOfDouble);
    return copyValidSolutions(i, arrayOfDouble, paramArrayOfDouble);
  }
  
  public static int solveCubicCurveForY(CubicCurve2D paramCubicCurve2D, double paramDouble, double[] paramArrayOfDouble)
  {
    double d1 = getCubicAy(paramCubicCurve2D);
    double d2 = getCubicBy(paramCubicCurve2D);
    double d3 = getCubicCy(paramCubicCurve2D);
    double d4 = paramCubicCurve2D.getY1() - paramDouble;
    double[] arrayOfDouble = { d4, d3, d2, d1 };
    int i = CubicCurve2D.solveCubic(arrayOfDouble);
    return copyValidSolutions(i, arrayOfDouble, paramArrayOfDouble);
  }
  
  private static double getCubicAx(CubicCurve2D paramCubicCurve2D)
  {
    return paramCubicCurve2D.getX2() - paramCubicCurve2D.getX1() - getCubicBx(paramCubicCurve2D) - getCubicCx(paramCubicCurve2D);
  }
  
  private static double getCubicAy(CubicCurve2D paramCubicCurve2D)
  {
    return paramCubicCurve2D.getY2() - paramCubicCurve2D.getY1() - getCubicBy(paramCubicCurve2D) - getCubicCy(paramCubicCurve2D);
  }
  
  private static double getCubicBx(CubicCurve2D paramCubicCurve2D)
  {
    return 3.0D * (paramCubicCurve2D.getCtrlX2() - paramCubicCurve2D.getCtrlX1()) - getCubicCx(paramCubicCurve2D);
  }
  
  private static double getCubicBy(CubicCurve2D paramCubicCurve2D)
  {
    return 3.0D * (paramCubicCurve2D.getCtrlY2() - paramCubicCurve2D.getCtrlY1()) - getCubicCy(paramCubicCurve2D);
  }
  
  private static double getCubicCx(CubicCurve2D paramCubicCurve2D)
  {
    return 3.0D * (paramCubicCurve2D.getCtrlX1() - paramCubicCurve2D.getX1());
  }
  
  private static double getCubicCy(CubicCurve2D paramCubicCurve2D)
  {
    return 3.0D * (paramCubicCurve2D.getCtrlY1() - paramCubicCurve2D.getY1());
  }
  
  private static int copyValidSolutions(int paramInt, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
  {
    int i = 0;
    for (int j = 0; j < paramInt; j++) {
      if ((paramArrayOfDouble1[j] >= 0.0D) && (paramArrayOfDouble1[j] <= 1.0D))
      {
        int k = 1;
        for (int m = 0; m < i; m++) {
          if (paramArrayOfDouble1[j] == paramArrayOfDouble1[m])
          {
            k = 0;
            break;
          }
        }
        if (k != 0)
        {
          if (i < paramArrayOfDouble2.length) {
            paramArrayOfDouble2[i] = paramArrayOfDouble1[j];
          }
          i++;
        }
      }
    }
    return i;
  }
  
  public static double quadCurveLength(QuadCurve2D paramQuadCurve2D, double paramDouble)
  {
    if (null == paramQuadCurve2D) {
      throw new NullPointerException("curve must be non-null!");
    }
    if (paramDouble <= 0.0D) {
      throw new IllegalArgumentException("flatness must be greater than 0!");
    }
    if (paramQuadCurve2D.getFlatness() > paramDouble)
    {
      QuadCurve2D.Double localDouble1 = new QuadCurve2D.Double();
      QuadCurve2D.Double localDouble2 = new QuadCurve2D.Double();
      paramQuadCurve2D.subdivide(localDouble1, localDouble2);
      return quadCurveLength(localDouble1, paramDouble) + quadCurveLength(localDouble2, paramDouble);
    }
    return Point2D.distance(paramQuadCurve2D.getX1(), paramQuadCurve2D.getY1(), paramQuadCurve2D.getX2(), paramQuadCurve2D.getY2());
  }
  
  public static double cubicCurveLength(CubicCurve2D paramCubicCurve2D, double paramDouble)
  {
    if (null == paramCubicCurve2D) {
      throw new NullPointerException("curve must be non-null!");
    }
    if (paramDouble <= 0.0D) {
      throw new IllegalArgumentException("flatness must be greater than 0!");
    }
    if (paramCubicCurve2D.getFlatness() > paramDouble)
    {
      CubicCurve2D.Double localDouble1 = new CubicCurve2D.Double();
      CubicCurve2D.Double localDouble2 = new CubicCurve2D.Double();
      paramCubicCurve2D.subdivide(localDouble1, localDouble2);
      return cubicCurveLength(localDouble1, paramDouble) + cubicCurveLength(localDouble2, paramDouble);
    }
    return Point2D.distance(paramCubicCurve2D.getX1(), paramCubicCurve2D.getY1(), paramCubicCurve2D.getX2(), paramCubicCurve2D.getY2());
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     kiang.awt.geom.CurveUtils
 * JD-Core Version:    0.7.0.1
 */