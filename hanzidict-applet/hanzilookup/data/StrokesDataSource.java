package hanzilookup.data;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StrokesDataSource
{
  private StrokesStreamProvider streamProvider;
  private long[] genericPositions = new long[48];
  private long[] simplifiedPositions = new long[48];
  private long[] traditionalPositions = new long[48];
  
  public StrokesDataSource(StrokesStreamProvider paramStrokesStreamProvider)
    throws IOException
  {
    this.streamProvider = paramStrokesStreamProvider;
    indexPositions();
  }
  
  private void indexPositions()
    throws IOException
  {
    DataInputStream localDataInputStream = new DataInputStream(this.streamProvider.getStrokesStream());
    long l = 0L;
    l = loadPositions(this.genericPositions, localDataInputStream, l);
    l = loadPositions(this.simplifiedPositions, localDataInputStream, l);
    l = loadPositions(this.traditionalPositions, localDataInputStream, l);
    localDataInputStream.close();
  }
  
  private long loadPositions(long[] paramArrayOfLong, DataInputStream paramDataInputStream, long paramLong)
    throws IOException
  {
    for (int i = 0; i < paramArrayOfLong.length; i++)
    {
      paramArrayOfLong[i] = paramLong;
      int j = paramDataInputStream.readInt();
      paramLong += j + 4;
      skipFully(j, paramDataInputStream);
    }
    return paramLong;
  }
  
  public StrokesDataScanner getStrokesScanner(boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
  {
    paramInt1 = Math.max(1, paramInt1);
    paramInt2 = Math.min(48, paramInt2);
    return new StrokesDataScanner(paramBoolean1, paramBoolean2, paramInt1, paramInt2, null);
  }
  
  private static void skipFully(long paramLong, DataInputStream paramDataInputStream)
    throws IOException
  {
    while (paramLong > 0L) {
      paramLong -= paramDataInputStream.skip(paramLong);
    }
  }
  
  public static abstract interface StrokesStreamProvider
  {
    public abstract InputStream getStrokesStream();
  }
  
  public class StrokesDataScanner
  {
    private DataInputStream strokeDataStream;
    private Iterator positionsIter;
    private long position;
    private long endOfStrokeCount;
    private boolean skipToNextTypePosition;
    private boolean loadNextStrokeCount;
    private int strokeCount;
    private int minStrokes;
    private int maxStrokes;
    
    private StrokesDataScanner(boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
    {
      int i = paramInt1 - 1;
      ArrayList localArrayList = new ArrayList(3);
      localArrayList.add(new Long(StrokesDataSource.this.genericPositions[i]));
      if (paramBoolean2) {
        localArrayList.add(new Long(StrokesDataSource.this.simplifiedPositions[i]));
      }
      if (paramBoolean1) {
        localArrayList.add(new Long(StrokesDataSource.this.traditionalPositions[i]));
      }
      DataInputStream localDataInputStream = new DataInputStream(StrokesDataSource.this.streamProvider.getStrokesStream());
      if (null == localDataInputStream) {
        throw new NullPointerException("Unable to get strokes stream!");
      }
      this.strokeDataStream = new DataInputStream(localDataInputStream);
      this.positionsIter = localArrayList.iterator();
      this.position = 0L;
      this.skipToNextTypePosition = true;
      this.loadNextStrokeCount = true;
      this.strokeCount = paramInt1;
      this.minStrokes = paramInt1;
      this.maxStrokes = paramInt2;
    }
    
    public boolean loadNextCharacterStrokeData(CharacterDescriptor paramCharacterDescriptor)
      throws IOException
    {
      if (null == this.strokeDataStream) {
        return false;
      }
      if (this.skipToNextTypePosition)
      {
        if (!this.positionsIter.hasNext()) {
          return false;
        }
        long l1 = ((Long)this.positionsIter.next()).longValue();
        long l2 = l1 - this.position;
        StrokesDataSource.skipFully(l2, this.strokeDataStream);
        this.position = l1;
        this.skipToNextTypePosition = false;
      }
      if (this.loadNextStrokeCount)
      {
        this.position += 4L;
        this.endOfStrokeCount = (this.position + this.strokeDataStream.readInt());
        this.loadNextStrokeCount = false;
      }
      if (this.position < this.endOfStrokeCount)
      {
        loadNextCharacterDataFromStream(paramCharacterDescriptor, this.strokeDataStream);
        this.position += 4 + paramCharacterDescriptor.getStrokeCount() + 4 * paramCharacterDescriptor.getSubStrokeCount();
      }
      if (this.position == this.endOfStrokeCount)
      {
        this.loadNextStrokeCount = true;
        if (this.strokeCount == this.maxStrokes)
        {
          this.skipToNextTypePosition = true;
          this.strokeCount = this.minStrokes;
        }
        else
        {
          this.strokeCount += 1;
        }
      }
      return true;
    }
    
    private void loadNextCharacterDataFromStream(CharacterDescriptor paramCharacterDescriptor, DataInputStream paramDataInputStream)
      throws IOException
    {
      Character localCharacter = new Character(StrokesIO.readCharacter(paramDataInputStream));
      int i = StrokesIO.readCharacterType(paramDataInputStream);
      int j = StrokesIO.readStrokeCount(paramDataInputStream);
      int k = 0;
      double[] arrayOfDouble1 = paramCharacterDescriptor.getDirections();
      double[] arrayOfDouble2 = paramCharacterDescriptor.getLengths();
      for (int m = 0; m < j; m++)
      {
        int n = StrokesIO.readSubStrokeCount(paramDataInputStream);
        for (int i1 = 0; i1 < n; i1++)
        {
          double d1 = StrokesIO.readDirection(paramDataInputStream);
          double d2 = StrokesIO.readLength(paramDataInputStream);
          arrayOfDouble1[k] = d1;
          arrayOfDouble2[k] = d2;
          k++;
        }
      }
      paramCharacterDescriptor.setCharacter(localCharacter);
      paramCharacterDescriptor.setCharacterType(i);
      paramCharacterDescriptor.setStrokeCount(j);
      paramCharacterDescriptor.setSubStrokeCount(k);
    }
    
    StrokesDataScanner(boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2, StrokesDataSource.1 param1)
    {
      this(paramBoolean1, paramBoolean2, paramInt1, paramInt2);
    }
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     hanzilookup.data.StrokesDataSource
 * JD-Core Version:    0.7.0.1
 */