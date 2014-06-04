package hanzilookup.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MemoryStrokesStreamProvider
  implements StrokesDataSource.StrokesStreamProvider
{
  private byte[] strokeBytes;
  
  public MemoryStrokesStreamProvider(byte[] paramArrayOfByte)
  {
    this.strokeBytes = paramArrayOfByte;
  }
  
  public MemoryStrokesStreamProvider(InputStream paramInputStream)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    byte[] arrayOfByte = new byte[1024];
    for (int i = paramInputStream.read(arrayOfByte); i > -1; i = paramInputStream.read(arrayOfByte)) {
      localByteArrayOutputStream.write(arrayOfByte, 0, i);
    }
    this.strokeBytes = localByteArrayOutputStream.toByteArray();
  }
  
  public InputStream getStrokesStream()
  {
    return new ByteArrayInputStream(this.strokeBytes);
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     hanzilookup.data.MemoryStrokesStreamProvider
 * JD-Core Version:    0.7.0.1
 */