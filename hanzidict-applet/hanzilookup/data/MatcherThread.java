package hanzilookup.data;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class MatcherThread
  extends Thread
{
  private StrokesMatcher strokesMatcher;
  private Object matcherLock = new Object();
  private Set resultsHandlers = new LinkedHashSet();
  private boolean running = true;
  
  public void addResultsHandler(ResultsHandler paramResultsHandler)
  {
    synchronized (this.resultsHandlers)
    {
      this.resultsHandlers.add(paramResultsHandler);
    }
  }
  
  public boolean removeResultsHandler(ResultsHandler paramResultsHandler)
  {
    synchronized (this.resultsHandlers)
    {
      return this.resultsHandlers.remove(paramResultsHandler);
    }
  }
  
  public void kill()
  {
    this.running = false;
    synchronized (this.matcherLock)
    {
      this.matcherLock.notify();
    }
  }
  
  public void run()
  {
    while (this.running)
    {
      StrokesMatcher localStrokesMatcher = null;
      synchronized (this.matcherLock)
      {
        try
        {
          if (null == this.strokesMatcher) {
            this.matcherLock.wait();
          }
        }
        catch (InterruptedException localInterruptedException) {}
        if (null != this.strokesMatcher)
        {
          localStrokesMatcher = this.strokesMatcher;
          this.strokesMatcher = null;
        }
      }
      ??? = null;
      if (null != localStrokesMatcher)
      {
        ??? = localStrokesMatcher.doMatching();
        if (null != ???) {
          synchronized (this.resultsHandlers)
          {
            Iterator localIterator = this.resultsHandlers.iterator();
            while (localIterator.hasNext())
            {
              ResultsHandler localResultsHandler = (ResultsHandler)localIterator.next();
              localResultsHandler.handleResults((Character[])???);
            }
          }
        }
      }
    }
  }
  
  public void setStrokesMatcher(StrokesMatcher paramStrokesMatcher)
  {
    synchronized (this.matcherLock)
    {
      if (null != this.strokesMatcher) {
        this.strokesMatcher.stop();
      }
      this.strokesMatcher = paramStrokesMatcher;
      this.matcherLock.notify();
    }
  }
  
  public static abstract interface ResultsHandler
  {
    public abstract void handleResults(Character[] paramArrayOfCharacter);
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     hanzilookup.data.MatcherThread
 * JD-Core Version:    0.7.0.1
 */