package hanzidict;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CEDICTCharacterDictionary
  implements CharacterDictionary
{
  private static final char LOW_CHAR = '一';
  private static final char HIGH_CHAR = '鿿';
  private CEDICTStreamProvider streamProvider;
  private int[][] characterIndices;
  private int characterCount = 0;
  
  public CEDICTCharacterDictionary(CEDICTStreamProvider paramCEDICTStreamProvider)
    throws IOException
  {
    this(paramCEDICTStreamProvider, null);
  }
  
  public CEDICTCharacterDictionary(CEDICTStreamProvider paramCEDICTStreamProvider, ChangeListener paramChangeListener)
    throws IOException
  {
    this.streamProvider = paramCEDICTStreamProvider;
    indexCharacters(paramChangeListener);
  }
  
  public int getSize()
  {
    return this.characterCount;
  }
  
  public CharacterDictionary.Entry lookup(char paramChar)
  {
    int i = paramChar - '一';
    if ((i >= 0) && (i < this.characterIndices.length))
    {
      int[] arrayOfInt = this.characterIndices[(paramChar - '一')];
      if (null != arrayOfInt) {
        try
        {
          Reader localReader = getCEDICTReader();
          int j = 0;
          int k = 0;
          char c1 = '\000';
          char c2 = '\000';
          ArrayList localArrayList1 = new ArrayList();
          for (int m = 0; m < arrayOfInt.length; m++)
          {
            while (j < arrayOfInt[m]) {
              j = (int)(j + localReader.skip(arrayOfInt[m] - j));
            }
            c1 = (char)localReader.read();
            localReader.read();
            c2 = (char)localReader.read();
            j += 3;
            while ((k > -1) && ('[' != (char)k))
            {
              k = localReader.read();
              j++;
            }
            StringBuffer localStringBuffer = new StringBuffer();
            k = localReader.read();
            j++;
            while ((k > -1) && (']' != (char)k))
            {
              localStringBuffer.append((char)k);
              k = localReader.read();
              j++;
            }
            while ((k > -1) && ('/' != (char)k))
            {
              k = localReader.read();
              j++;
            }
            ArrayList localArrayList2 = new ArrayList();
            k = localReader.read();
            j++;
            while ((k > -1) && (k != 10) && (k != 13))
            {
              localObject = new StringBuffer();
              while ((k > -1) && ('/' != (char)k))
              {
                ((StringBuffer)localObject).append((char)k);
                k = localReader.read();
                j++;
              }
              localArrayList2.add(((StringBuffer)localObject).toString());
              k = localReader.read();
              j++;
            }
            Object localObject = new String[localArrayList2.size()];
            localObject = (String[])localArrayList2.toArray((Object[])localObject);
            localArrayList1.add(new CEDICTCharacterDictionary.CEDICTEntry.CEDICTDefinition(localStringBuffer.toString(), (String[])localObject));
          }
          CharacterDictionary.Entry.Definition[] arrayOfDefinition = new CharacterDictionary.Entry.Definition[localArrayList1.size()];
          arrayOfDefinition = (CharacterDictionary.Entry.Definition[])localArrayList1.toArray(arrayOfDefinition);
          return new CEDICTEntry(c1, c2, arrayOfDefinition);
        }
        catch (IOException localIOException)
        {
          localIOException.printStackTrace();
        }
      }
    }
    return null;
  }
  
  private void indexCharacters(final ChangeListener paramChangeListener)
    throws IOException
  {
    final int[] arrayOfInt = new int[20991];
    final ChangeEvent localChangeEvent = new ChangeEvent(this);
    scanPositions(new ScanAction()
    {
      private final int[] val$characterCounts;
      private final ChangeListener val$changeListener;
      private final ChangeEvent val$ce;
      
      public void indexPosition(char paramAnonymousChar, int paramAnonymousInt)
      {
        if ((paramAnonymousChar >= '一') && (paramAnonymousChar <= 40959))
        {
          arrayOfInt[(paramAnonymousChar - '一')] += 1;
          CEDICTCharacterDictionary.access$008(CEDICTCharacterDictionary.this);
          if (null != paramChangeListener) {
            paramChangeListener.stateChanged(localChangeEvent);
          }
        }
      }
    });
    final int[][] arrayOfInt1 = new int[arrayOfInt.length][];
    for (int i = 0; i < arrayOfInt1.length; i++) {
      if (arrayOfInt[i] > 0) {
        arrayOfInt1[i] = new int[arrayOfInt[i]];
      }
    }
    scanPositions(new ScanAction()
    {
      private final int[][] val$characterIndices;
      
      public void indexPosition(char paramAnonymousChar, int paramAnonymousInt)
      {
        if ((paramAnonymousChar >= '一') && (paramAnonymousChar <= 40959)) {
          for (int i = 0; i < arrayOfInt1[(paramAnonymousChar - '一')].length; i++) {
            if (arrayOfInt1[(paramAnonymousChar - '一')][i] == 0)
            {
              arrayOfInt1[(paramAnonymousChar - '一')][i] = paramAnonymousInt;
              break;
            }
          }
        }
      }
    });
    this.characterIndices = arrayOfInt1;
  }
  
  private Reader getCEDICTReader()
    throws IOException
  {
    return new BufferedReader(new InputStreamReader(this.streamProvider.getCEDICTStream(), "UTF-8"));
  }
  
  private void scanPositions(ScanAction paramScanAction)
    throws IOException
  {
    Reader localReader = getCEDICTReader();
    int j = 1;
    int i;
    for (int k = 0; (i = localReader.read()) > -1; k++)
    {
      char c1 = (char)i;
      if (('\n' == c1) || ('\r' == c1))
      {
        j = 1;
      }
      else if (j != 0)
      {
        j = 0;
        if ('#' != c1) {
          try
          {
            char c2 = c1;
            i = localReader.read();
            c1 = (char)i;
            i = localReader.read();
            if ((' ' == c1) && (i > -1))
            {
              paramScanAction.indexPosition(c2, k);
              char c3 = (char)i;
              if (c2 != c3) {
                paramScanAction.indexPosition(c3, k);
              }
            }
            k += 2;
          }
          catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
          {
            localArrayIndexOutOfBoundsException.printStackTrace();
          }
        }
      }
    }
    localReader.close();
  }
  
  private static class CEDICTEntry
    implements CharacterDictionary.Entry
  {
    private char traditional;
    private char simplified;
    private CharacterDictionary.Entry.Definition[] definitions;
    
    public CEDICTEntry(char paramChar1, char paramChar2, CharacterDictionary.Entry.Definition[] paramArrayOfDefinition)
    {
      this.traditional = paramChar1;
      this.simplified = paramChar2;
      this.definitions = paramArrayOfDefinition;
    }
    
    public char getTraditional()
    {
      return this.traditional;
    }
    
    public char getSimplified()
    {
      return this.simplified;
    }
    
    public CharacterDictionary.Entry.Definition[] getDefinitions()
    {
      return this.definitions;
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer();
      int i = 0;
      for (;;)
      {
        localStringBuffer.append(this.traditional).append(' ').append(this.simplified);
        localStringBuffer.append(this.definitions[i].toString());
        i++;
        if (i >= this.definitions.length) {
          break;
        }
        localStringBuffer.append('\n');
      }
      return localStringBuffer.toString();
    }
    
    private static class CEDICTDefinition
      implements CharacterDictionary.Entry.Definition
    {
      private String pinyin;
      private String[] translations;
      
      public CEDICTDefinition(String paramString, String[] paramArrayOfString)
      {
        this.pinyin = paramString;
        this.translations = paramArrayOfString;
      }
      
      public String getPinyin()
      {
        return this.pinyin;
      }
      
      public String[] getTranslations()
      {
        return this.translations;
      }
      
      public String toString()
      {
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append('[').append(this.pinyin).append(']').append(' ').append('/');
        for (int i = 0; i < this.translations.length; i++) {
          localStringBuffer.append(this.translations[i]).append('/');
        }
        return localStringBuffer.toString();
      }
    }
  }
  
  public static abstract interface CEDICTStreamProvider
  {
    public abstract InputStream getCEDICTStream()
      throws IOException;
  }
  
  private static abstract interface ScanAction
  {
    public abstract void indexPosition(char paramChar, int paramInt);
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     hanzidict.CEDICTCharacterDictionary
 * JD-Core Version:    0.7.0.1
 */