package kiang.chinese.font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ChineseFontFinder
{
  private static final String[] PREFERRED_FONTS = { "SimSun", "STHeiti", "Bitstream Cyberbit" };
  private static final String SIMPLIFIED_CHARACTERS = "这来国个汉";
  private static final String TRADITIONAL_CHARACTERS = "這來國個漢";
  public static final int FONT_SIZE = 24;
  
  public static Font getChineseFont()
  {
    Object localObject = null;
    int i = 0;
    int j = 0;
    Font[] arrayOfFont = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    for (int k = 0; k < arrayOfFont.length; k++)
    {
      Font localFont = arrayOfFont[k];
      int m = 0;
      boolean bool1 = isSimplifiedFont(localFont);
      boolean bool2 = isTraditionalFont(localFont);
      if (isPreferredFont(localFont))
      {
        localObject = localFont;
        break;
      }
      if (null == localObject)
      {
        if ((bool1) || (bool2)) {
          m = 1;
        }
      }
      else if ((bool2) && (bool1) && ((j == 0) || (i == 0))) {
        m = 1;
      }
      if (m != 0)
      {
        localObject = localFont;
        i = bool1;
        j = bool2;
      }
    }
    if (null != localObject) {
      localObject = ((Font)localObject).deriveFont(0, 24.0F);
    }
    return localObject;
  }
  
  public static Font[] getAllChineseFonts()
  {
    Font[] arrayOfFont1 = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < arrayOfFont1.length; i++) {
      if ((isTraditionalFont(arrayOfFont1[i])) || (isSimplifiedFont(arrayOfFont1[i]))) {
        localArrayList.add(arrayOfFont1[i]);
      }
    }
    Font[] arrayOfFont2 = new Font[localArrayList.size()];
    return (Font[])localArrayList.toArray(arrayOfFont2);
  }
  
  public static boolean isSimplifiedFont(Font paramFont)
  {
    return canDisplay(paramFont, "这来国个汉");
  }
  
  public static boolean isTraditionalFont(Font paramFont)
  {
    return canDisplay(paramFont, "這來國個漢");
  }
  
  private static boolean canDisplay(Font paramFont, String paramString)
  {
    try
    {
      return -1 == paramFont.canDisplayUpTo(paramString);
    }
    catch (Exception localException)
    {
      System.out.println("JDK error with " + paramFont.getName());
      localException.printStackTrace();
    }
    return false;
  }
  
  private static boolean isPreferredFont(Font paramFont)
  {
    for (int i = 0; i < PREFERRED_FONTS.length; i++) {
      if ((PREFERRED_FONTS[i].equals(paramFont.getFontName())) && (isSimplifiedFont(paramFont)) && (isTraditionalFont(paramFont))) {
        return true;
      }
    }
    return false;
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     kiang.chinese.font.ChineseFontFinder
 * JD-Core Version:    0.7.0.1
 */