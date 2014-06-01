package kiang.chinese.font;

import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import kiang.swing.JFontChooser;
import kiang.swing.JFontChooser.FontFilter;

public class ChineseFontChooserFactory
{
  public static Font showDialog(Component paramComponent)
  {
    Font localFont = paramComponent.getFont();
    String str = "汉  漢";
    int[] arrayOfInt = { 8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72 };
    Font[] arrayOfFont = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    return showDialog(paramComponent, localFont, arrayOfFont, arrayOfInt, str);
  }
  
  public static Font showDialog(Component paramComponent, Font paramFont, Font[] paramArrayOfFont, int[] paramArrayOfInt, String paramString)
  {
    JFontChooser.FontFilter[] arrayOfFontFilter = getChineseFilters(paramFont);
    return JFontChooser.showDialog(paramComponent, paramFont, paramArrayOfFont, paramArrayOfInt, arrayOfFontFilter, paramString);
  }
  
  static JFontChooser getInstance(Font paramFont, Font[] paramArrayOfFont, int[] paramArrayOfInt, String paramString)
  {
    JFontChooser.FontFilter[] arrayOfFontFilter = getChineseFilters(paramFont);
    return new JFontChooser(paramFont, paramArrayOfFont, paramArrayOfInt, arrayOfFontFilter, paramString);
  }
  
  private static JFontChooser.FontFilter[] getChineseFilters(Font paramFont)
  {
    return new JFontChooser.FontFilter[] { new SimplifiedFontFilter(paramFont, null), new TraditionalFontFilter(paramFont, null) };
  }
  
  private static class TraditionalFontFilter
    implements JFontChooser.FontFilter
  {
    private Font initialFont;
    
    private TraditionalFontFilter(Font paramFont)
    {
      this.initialFont = paramFont;
    }
    
    public String getDisplayName()
    {
      return "Traditional";
    }
    
    public boolean isDefaultOn()
    {
      return shouldInclude(this.initialFont);
    }
    
    public boolean shouldInclude(Font paramFont)
    {
      return ChineseFontFinder.isTraditionalFont(paramFont);
    }
    
    TraditionalFontFilter(Font paramFont, ChineseFontChooserFactory.1 param1)
    {
      this(paramFont);
    }
  }
  
  private static class SimplifiedFontFilter
    implements JFontChooser.FontFilter
  {
    private Font initialFont;
    
    private SimplifiedFontFilter(Font paramFont)
    {
      this.initialFont = paramFont;
    }
    
    public String getDisplayName()
    {
      return "Simplified";
    }
    
    public boolean isDefaultOn()
    {
      return shouldInclude(this.initialFont);
    }
    
    public boolean shouldInclude(Font paramFont)
    {
      return ChineseFontFinder.isSimplifiedFont(paramFont);
    }
    
    SimplifiedFontFilter(Font paramFont, ChineseFontChooserFactory.1 param1)
    {
      this(paramFont);
    }
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     kiang.chinese.font.ChineseFontChooserFactory
 * JD-Core Version:    0.7.0.1
 */