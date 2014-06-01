package hanzidict;

public abstract interface CharacterDictionary
{
  public abstract Entry lookup(char paramChar);
  
  public abstract int getSize();
  
  public static abstract interface Entry
  {
    public abstract char getTraditional();
    
    public abstract char getSimplified();
    
    public abstract Definition[] getDefinitions();
    
    public static abstract interface Definition
    {
      public abstract String getPinyin();
      
      public abstract String[] getTranslations();
    }
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     hanzidict.CharacterDictionary
 * JD-Core Version:    0.7.0.1
 */