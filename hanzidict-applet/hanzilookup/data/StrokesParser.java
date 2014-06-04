package hanzilookup.data;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kiang.io.LineParser;

public class StrokesParser
  extends LineParser
{
  private ByteArrayOutputStream[] genericByteStreams;
  private ByteArrayOutputStream[] simplifiedByteStreams;
  private ByteArrayOutputStream[] traditionalByteStreams;
  private DataOutputStream[] genericOutStreams;
  private DataOutputStream[] simplifiedOutStreams;
  private DataOutputStream[] traditionalOutStreams;
  private CharacterTypeRepository typeRepository;
  private int[] subStrokesPerStroke = new int[48];
  private double[] subStrokeDirections = new double[64];
  private double[] subStrokeLengths = new double[64];
  private Pattern linePattern = Pattern.compile("^([a-fA-F0-9]{4})\\s*\\|(.*)$");
  private Pattern subStrokePattern = Pattern.compile("^\\s*\\((\\d+(\\.\\d{1,10})?)\\s*,\\s*(\\d+(\\.\\d{1,10})?)\\)\\s*$");
  
  public StrokesParser(InputStream paramInputStream, CharacterTypeRepository paramCharacterTypeRepository)
    throws IOException
  {
    this.typeRepository = paramCharacterTypeRepository;
    initStrokes(paramInputStream);
  }
  
  public StrokesParser(InputStream paramInputStream1, InputStream paramInputStream2)
    throws IOException
  {
    CharacterTypeParser localCharacterTypeParser = new CharacterTypeParser(paramInputStream2);
    this.typeRepository = localCharacterTypeParser.buildCharacterTypeRepository();
    initStrokes(paramInputStream1);
  }
  
  private void initStrokes(InputStream paramInputStream)
    throws IOException
  {
    try
    {
      prepareStrokeBytes();
      parse(paramInputStream);
      paramInputStream.close();
    }
    catch (IOException localIOException1)
    {
      IOException localIOException2 = new IOException("Error reading character stroke data!");
      localIOException2.initCause(localIOException1);
      throw localIOException2;
    }
  }
  
  public void writeCompiledOutput(OutputStream paramOutputStream)
    throws IOException
  {
    byte[][] arrayOfByte1 = new byte[48][];
    byte[][] arrayOfByte2 = new byte[48][];
    byte[][] arrayOfByte3 = new byte[48][];
    for (int i = 0; i < 48; i++)
    {
      arrayOfByte1[i] = this.genericByteStreams[i].toByteArray();
      arrayOfByte2[i] = this.simplifiedByteStreams[i].toByteArray();
      arrayOfByte3[i] = this.traditionalByteStreams[i].toByteArray();
    }
    DataOutputStream localDataOutputStream = new DataOutputStream(new BufferedOutputStream(paramOutputStream));
    writeStrokes(arrayOfByte1, localDataOutputStream);
    writeStrokes(arrayOfByte2, localDataOutputStream);
    writeStrokes(arrayOfByte3, localDataOutputStream);
    localDataOutputStream.close();
  }
  
  private void writeStrokes(byte[][] paramArrayOfByte, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    for (int i = 0; i < 48; i++)
    {
      int j = paramArrayOfByte[i].length;
      paramDataOutputStream.writeInt(j);
      paramDataOutputStream.write(paramArrayOfByte[i]);
    }
  }
  
  private void prepareStrokeBytes()
  {
    this.genericByteStreams = new ByteArrayOutputStream[48];
    this.genericOutStreams = new DataOutputStream[48];
    this.simplifiedByteStreams = new ByteArrayOutputStream[48];
    this.simplifiedOutStreams = new DataOutputStream[48];
    this.traditionalByteStreams = new ByteArrayOutputStream[48];
    this.traditionalOutStreams = new DataOutputStream[48];
    for (int i = 0; i < 48; i++)
    {
      this.genericByteStreams[i] = new ByteArrayOutputStream();
      this.genericOutStreams[i] = new DataOutputStream(this.genericByteStreams[i]);
      this.simplifiedByteStreams[i] = new ByteArrayOutputStream();
      this.simplifiedOutStreams[i] = new DataOutputStream(this.simplifiedByteStreams[i]);
      this.traditionalByteStreams[i] = new ByteArrayOutputStream();
      this.traditionalOutStreams[i] = new DataOutputStream(this.traditionalByteStreams[i]);
    }
  }
  
  protected boolean parseLine(int paramInt, String paramString)
  {
    Matcher localMatcher = this.linePattern.matcher(paramString);
    int i = 1;
    int j = 0;
    if (localMatcher.matches())
    {
      String str1 = localMatcher.group(1);
      Character localCharacter = new Character((char)Integer.parseInt(str1, 16));
      String str2 = localMatcher.group(2);
      int k = 0;
      StringTokenizer localStringTokenizer = new StringTokenizer(str2, "|");
      Object localObject;
      while (localStringTokenizer.hasMoreTokens())
      {
        if (k >= 48)
        {
          i = 0;
          break;
        }
        localObject = localStringTokenizer.nextToken();
        int n = parseStroke((String)localObject, k, j);
        if (n > 0) {
          j += n;
        } else {
          i = 0;
        }
        k++;
      }
      if (i != 0)
      {
        int m = this.typeRepository.getType(localCharacter);
        if (m == -1) {
          m = 0;
        }
        if (m == 2) {
          localObject = this.traditionalOutStreams[(k - 1)];
        } else if (m == 1) {
          localObject = this.simplifiedOutStreams[(k - 1)];
        } else {
          localObject = this.genericOutStreams[(k - 1)];
        }
        writeStrokeData((DataOutputStream)localObject, localCharacter, m, k, j);
        return true;
      }
    }
    return false;
  }
  
  private int parseStroke(String paramString, int paramInt1, int paramInt2)
  {
    int i = 0;
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "#");
    while (localStringTokenizer.hasMoreTokens())
    {
      if ((i >= 64) || (!parseSubStroke(localStringTokenizer.nextToken(), paramInt2 + i))) {
        return -1;
      }
      i++;
    }
    this.subStrokesPerStroke[paramInt1] = i;
    return i;
  }
  
  private boolean parseSubStroke(String paramString, int paramInt)
  {
    Matcher localMatcher = this.subStrokePattern.matcher(paramString);
    if (localMatcher.matches())
    {
      double d1 = Double.parseDouble(localMatcher.group(1));
      double d2 = Double.parseDouble(localMatcher.group(3));
      this.subStrokeDirections[paramInt] = d1;
      this.subStrokeLengths[paramInt] = d2;
      return true;
    }
    return false;
  }
  
  private void writeStrokeData(DataOutputStream paramDataOutputStream, Character paramCharacter, int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      StrokesIO.writeCharacter(paramCharacter.charValue(), paramDataOutputStream);
      StrokesIO.writeCharacterType(paramInt1, paramDataOutputStream);
      StrokesIO.writeStrokeCount(paramInt2, paramDataOutputStream);
      int i = 0;
      for (int j = 0; j < paramInt2; j++)
      {
        int k = this.subStrokesPerStroke[j];
        StrokesIO.writeSubStrokeCount(k, paramDataOutputStream);
        for (int m = 0; m < k; m++)
        {
          StrokesIO.writeDirection(this.subStrokeDirections[i], paramDataOutputStream);
          StrokesIO.writeLength(this.subStrokeLengths[i], paramDataOutputStream);
          i++;
        }
      }
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }
  
  public static byte[] getStrokeBytes(InputStream paramInputStream1, InputStream paramInputStream2)
    throws IOException
  {
    StrokesParser localStrokesParser = new StrokesParser(paramInputStream1, paramInputStream2);
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    localStrokesParser.writeCompiledOutput(localByteArrayOutputStream);
    return localByteArrayOutputStream.toByteArray();
  }
  
  public static void main(String[] paramArrayOfString)
  {
    Object localObject;
    if (paramArrayOfString.length != 3)
    {
      localObject = new StringBuffer();
      ((StringBuffer)localObject).append("Takes three arguments:\n");
      ((StringBuffer)localObject).append("1: the plain-text strokes data file\n");
      ((StringBuffer)localObject).append("2: the plain-text types data file\n");
      ((StringBuffer)localObject).append("3: the file to output the compiled data file to");
      System.err.println(localObject);
    }
    else
    {
      try
      {
        localObject = new FileInputStream(paramArrayOfString[0]);
        FileInputStream localFileInputStream = new FileInputStream(paramArrayOfString[1]);
        FileOutputStream localFileOutputStream = new FileOutputStream(paramArrayOfString[2]);
        CharacterTypeParser localCharacterTypeParser = new CharacterTypeParser(localFileInputStream);
        CharacterTypeRepository localCharacterTypeRepository = localCharacterTypeParser.buildCharacterTypeRepository();
        StrokesParser localStrokesParser = new StrokesParser((InputStream)localObject, localCharacterTypeRepository);
        localStrokesParser.writeCompiledOutput(localFileOutputStream);
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
      }
    }
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     hanzilookup.data.StrokesParser
 * JD-Core Version:    0.7.0.1
 */