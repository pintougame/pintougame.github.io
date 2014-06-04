package hanzilookup.data;

public class CharacterDescriptor
{
  public static final int MAX_CHARACTER_STROKE_COUNT = 48;
  public static final int MAX_CHARACTER_SUB_STROKE_COUNT = 64;
  private Character character;
  private int characterType;
  private int strokeCount;
  private int subStrokeCount;
  private double[] directions = new double[64];
  private double[] lengths = new double[64];
  
  public Character getCharacter()
  {
    return this.character;
  }
  
  public void setCharacter(Character paramCharacter)
  {
    this.character = paramCharacter;
  }
  
  public int getCharacterType()
  {
    return this.characterType;
  }
  
  public void setCharacterType(int paramInt)
  {
    this.characterType = paramInt;
  }
  
  public int getStrokeCount()
  {
    return this.strokeCount;
  }
  
  public void setStrokeCount(int paramInt)
  {
    this.strokeCount = paramInt;
  }
  
  public int getSubStrokeCount()
  {
    return this.subStrokeCount;
  }
  
  public void setSubStrokeCount(int paramInt)
  {
    this.subStrokeCount = paramInt;
  }
  
  public double[] getDirections()
  {
    return this.directions;
  }
  
  public double[] getLengths()
  {
    return this.lengths;
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     hanzilookup.data.CharacterDescriptor
 * JD-Core Version:    0.7.0.1
 */