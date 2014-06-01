package kiang.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;

public abstract class LineParser
{
  public final void parse(InputStream paramInputStream)
    throws IOException
  {
    parse(paramInputStream, Charset.forName("US-ASCII"));
  }
  
  public final void parse(InputStream paramInputStream, Charset paramCharset)
    throws IOException
  {
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream, paramCharset));
    int i = 0;
    for (String str = localBufferedReader.readLine(); null != str; str = localBufferedReader.readLine())
    {
      if ((shouldParseLine(i, str)) && (!parseLine(i, str))) {
        lineError(i, str);
      }
      i++;
    }
    paramInputStream.close();
  }
  
  protected abstract boolean parseLine(int paramInt, String paramString);
  
  protected void lineError(int paramInt, String paramString)
  {
    System.err.println("Error parsing line " + paramInt + ": " + paramString);
  }
  
  protected boolean shouldParseLine(int paramInt, String paramString)
  {
    return (!isLineEmpty(paramString)) && (!isLineComment(paramString));
  }
  
  protected boolean isLineComment(String paramString)
  {
    return (paramString.matches("^\\s*//.*")) || (paramString.matches("^\\s*#.*"));
  }
  
  protected boolean isLineEmpty(String paramString)
  {
    return paramString.matches("^\\s*$");
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     kiang.io.LineParser
 * JD-Core Version:    0.7.0.1
 */