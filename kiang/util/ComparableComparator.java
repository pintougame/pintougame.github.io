package kiang.util;

import java.util.Comparator;

public class ComparableComparator
  implements Comparator
{
  public int compare(Object paramObject1, Object paramObject2)
  {
    Comparable localComparable = (Comparable)paramObject1;
    return localComparable.compareTo(paramObject2);
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     kiang.util.ComparableComparator
 * JD-Core Version:    0.7.0.1
 */