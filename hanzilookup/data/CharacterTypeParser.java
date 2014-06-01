package hanzilookup.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kiang.io.LineParser;

public class CharacterTypeParser
  extends LineParser
{
  private Map typeMap = new HashMap();
  private Pattern linePattern = Pattern.compile("^([a-f0-9]{4})\\s*\\|\\s*(\\d)(\\s*\\|\\s*([a-f0-9]{4}))?\\s*$");
  
  public CharacterTypeParser(InputStream paramInputStream)
    throws IOException
  {
    try
    {
      parse(paramInputStream);
      paramInputStream.close();
    }
    catch (IOException localIOException1)
    {
      IOException localIOException2 = new IOException("Error reading character type data!");
      localIOException2.initCause(localIOException1);
      throw localIOException2;
    }
  }
  
  public CharacterTypeRepository buildCharacterTypeRepository()
  {
    CharacterTypeRepository localCharacterTypeRepository = new CharacterTypeRepository(this.typeMap);
    return localCharacterTypeRepository;
  }
  
  protected boolean parseLine(int paramInt, String paramString)
  {
    int i = 0;
    Matcher localMatcher = this.linePattern.matcher(paramString);
    if (localMatcher.matches())
    {
      String str1 = localMatcher.group(1);
      String str2 = localMatcher.group(2);
      Character localCharacter1 = new Character((char)Integer.parseInt(str1, 16));
      int j = Integer.parseInt(str2);
      Character localCharacter2 = null;
      Object localObject;
      if (0 == j)
      {
        i = 1;
      }
      else if ((1 == j) || (2 == j) || (3 == j))
      {
        localObject = localMatcher.group(4);
        if (null != localObject)
        {
          localCharacter2 = new Character((char)Integer.parseInt((String)localObject, 16));
          i = 1;
        }
      }
      if (i != 0)
      {
        localObject = new CharacterTypeRepository.TypeDescriptor(j, localCharacter1, localCharacter2);
        this.typeMap.put(localCharacter1, localObject);
        return true;
      }
    }
    return false;
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     hanzilookup.data.CharacterTypeParser
 * JD-Core Version:    0.7.0.1
 */