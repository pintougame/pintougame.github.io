package hanzilookup.data;

import java.util.Map;

public class CharacterTypeRepository
{
  public static final int GENERIC_TYPE = 0;
  public static final int SIMPLIFIED_TYPE = 1;
  public static final int TRADITIONAL_TYPE = 2;
  public static final int EQUIVALENT_TYPE = 3;
  public static final int NOT_FOUND = -1;
  private Map typeMap;
  
  public CharacterTypeRepository(Map paramMap)
  {
    this.typeMap = paramMap;
  }
  
  public TypeDescriptor lookup(Character paramCharacter)
  {
    TypeDescriptor localTypeDescriptor = (TypeDescriptor)this.typeMap.get(paramCharacter);
    return localTypeDescriptor;
  }
  
  public int getType(Character paramCharacter)
  {
    TypeDescriptor localTypeDescriptor = lookup(paramCharacter);
    if (null != localTypeDescriptor)
    {
      if ((localTypeDescriptor.type == 0) || (localTypeDescriptor.type == 1) || (localTypeDescriptor.type == 2)) {
        return localTypeDescriptor.type;
      }
      if (localTypeDescriptor.type == 3) {
        return getType(localTypeDescriptor.altUnicode);
      }
    }
    return -1;
  }
  
  public static class TypeDescriptor
  {
    private int type;
    private Character unicode;
    private Character altUnicode;
    
    public TypeDescriptor(int paramInt, Character paramCharacter1, Character paramCharacter2)
    {
      this.type = paramInt;
      this.unicode = paramCharacter1;
      this.altUnicode = paramCharacter2;
    }
    
    public int getType()
    {
      return this.type;
    }
    
    public Character getUnicode()
    {
      return this.unicode;
    }
    
    public Character getAlUnicode()
    {
      return this.altUnicode;
    }
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     hanzilookup.data.CharacterTypeRepository
 * JD-Core Version:    0.7.0.1
 */