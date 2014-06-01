package hanzilookup.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StrokesIO
{
  public static void writeCharacter(char paramChar, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeChar(paramChar);
  }
  
  public static char readCharacter(DataInputStream paramDataInputStream)
    throws IOException
  {
    char c = paramDataInputStream.readChar();
    return c;
  }
  
  public static void writeCharacterType(int paramInt, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeByte(paramInt);
  }
  
  public static int readCharacterType(DataInputStream paramDataInputStream)
    throws IOException
  {
    int i = paramDataInputStream.readByte();
    return i;
  }
  
  public static void writeStrokeCount(int paramInt, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeByte(paramInt);
  }
  
  public static int readStrokeCount(DataInputStream paramDataInputStream)
    throws IOException
  {
    int i = paramDataInputStream.readByte();
    return i;
  }
  
  public static void writeSubStrokeCount(int paramInt, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeByte(paramInt);
  }
  
  public static int readSubStrokeCount(DataInputStream paramDataInputStream)
    throws IOException
  {
    int i = paramDataInputStream.readByte();
    return i;
  }
  
  public static void writeDirection(double paramDouble, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    int i = convertDirectionToShort(paramDouble);
    paramDataOutputStream.writeShort(i);
  }
  
  public static double readDirection(DataInputStream paramDataInputStream)
    throws IOException
  {
    short s = paramDataInputStream.readShort();
    double d = convertDirectionFromShort(s);
    return d;
  }
  
  public static void writeLength(double paramDouble, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    int i = convertLengthToShort(paramDouble);
    paramDataOutputStream.writeShort(i);
  }
  
  public static double readLength(DataInputStream paramDataInputStream)
    throws IOException
  {
    int i = paramDataInputStream.readShort();
    double d = convertLengthFromShort(i);
    return d;
  }
  
  private static double convertDirectionFromShort(short paramShort)
  {
    double d1 = (paramShort + 32767.0D) / 32767.0D;
    double d2 = d1 * 2.0D * 3.141592653589793D;
    return d2;
  }
  
  private static double convertLengthFromShort(double paramDouble)
  {
    double d = (paramDouble + 32767.0D) / 32767.0D;
    return d;
  }
  
  private static short convertDirectionToShort(double paramDouble)
  {
    double d = paramDouble / 6.283185307179586D;
    short s = (short)(int)(d * 32767.0D - 32767.0D);
    return s;
  }
  
  private static short convertLengthToShort(double paramDouble)
  {
    short s = (short)(int)(paramDouble * 32767.0D - 32767.0D);
    return s;
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     hanzilookup.data.StrokesIO
 * JD-Core Version:    0.7.0.1
 */