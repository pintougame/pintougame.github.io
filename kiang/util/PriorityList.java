package kiang.util;

import java.util.AbstractCollection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class PriorityList
  extends AbstractCollection
{
  private LinkedList list = new LinkedList();
  private Comparator comparator;
  private boolean allowsDuplicates;
  private boolean ascendingOrder;
  
  public PriorityList()
  {
    this(true, true);
  }
  
  public PriorityList(Comparator paramComparator)
  {
    this(paramComparator, true, true);
  }
  
  public PriorityList(boolean paramBoolean1, boolean paramBoolean2)
  {
    this(new ComparableComparator(), paramBoolean1, paramBoolean2);
  }
  
  public PriorityList(Comparator paramComparator, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.comparator = paramComparator;
    this.allowsDuplicates = paramBoolean1;
    this.ascendingOrder = paramBoolean2;
  }
  
  public Comparator getComparator()
  {
    if ((this.comparator instanceof ComparableComparator)) {
      return null;
    }
    return this.comparator;
  }
  
  public void setComparator(Comparator paramComparator)
  {
    this.comparator = paramComparator;
    resort();
  }
  
  public boolean getOrder()
  {
    return this.ascendingOrder;
  }
  
  public void setOrder(boolean paramBoolean)
  {
    boolean bool = this.ascendingOrder;
    this.ascendingOrder = paramBoolean;
    if (bool != this.ascendingOrder) {
      resort();
    }
  }
  
  private void resort()
  {
    Collections.sort(this.list, this.comparator);
    if (!this.ascendingOrder) {
      Collections.reverse(this.list);
    }
  }
  
  public void setAllowsDuplicates(boolean paramBoolean)
  {
    if ((this.allowsDuplicates) && (!paramBoolean)) {
      removeDuplicates();
    }
    this.allowsDuplicates = paramBoolean;
  }
  
  public boolean getAllowsDuplicates()
  {
    return this.allowsDuplicates;
  }
  
  private void removeDuplicates()
  {
    Object localObject1 = null;
    Iterator localIterator = iterator();
    while (localIterator.hasNext())
    {
      Object localObject2 = localIterator.next();
      if ((null != localObject1) && (localObject1.equals(localObject2))) {
        localIterator.remove();
      }
      localObject1 = localObject2;
    }
  }
  
  public boolean add(Object paramObject)
  {
    ListIterator localListIterator = this.list.listIterator();
    while (localListIterator.hasNext())
    {
      Object localObject = localListIterator.next();
      int i = compare(paramObject, localObject);
      if (i < 0)
      {
        localListIterator.previous();
        break;
      }
      if (i == 0)
      {
        if (this.allowsDuplicates) {
          break;
        }
        return false;
      }
    }
    localListIterator.add(paramObject);
    return true;
  }
  
  private int compare(Object paramObject1, Object paramObject2)
  {
    int i = this.comparator.compare(paramObject1, paramObject2);
    if (((this.ascendingOrder) && (i < 0)) || ((!this.ascendingOrder) && (i > 0))) {
      return -1;
    }
    if (i != 0) {
      return 1;
    }
    return 0;
  }
  
  public Iterator iterator()
  {
    return this.list.iterator();
  }
  
  public int size()
  {
    return this.list.size();
  }
  
  public Object getFirst()
  {
    return this.list.getFirst();
  }
  
  public Object removeFirst()
  {
    return this.list.removeFirst();
  }
  
  public Object getLast()
  {
    return this.list.getLast();
  }
  
  public Object removeLast()
  {
    return this.list.removeLast();
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     kiang.util.PriorityList
 * JD-Core Version:    0.7.0.1
 */