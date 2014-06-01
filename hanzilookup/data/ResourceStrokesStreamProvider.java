package hanzilookup.data;

import java.io.InputStream;

public class ResourceStrokesStreamProvider
  implements StrokesDataSource.StrokesStreamProvider
{
  private String resourcePath;
  
  public ResourceStrokesStreamProvider(String paramString)
  {
    this.resourcePath = paramString;
  }
  
  public InputStream getStrokesStream()
  {
    InputStream localInputStream = getClass().getResourceAsStream(this.resourcePath);
    if (null == localInputStream) {
      throw new NullPointerException("Unable to stream resource: " + this.resourcePath);
    }
    return localInputStream;
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     hanzilookup.data.ResourceStrokesStreamProvider
 * JD-Core Version:    0.7.0.1
 */