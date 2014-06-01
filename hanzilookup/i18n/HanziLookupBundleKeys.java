package hanzilookup.i18n;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListResourceBundle;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;

public class HanziLookupBundleKeys
{
  public static final String OK_BUNDLE_KEY = "ok";
  public static final String SETTINGS_BUNDLE_KEY = "settings";
  public static final String SAVE_SETTINGS_BUNDLE_KEY = "save_settings";
  public static final String CHARACTER_TYPE_BUNDLE_KEY = "character_type_bundle_key";
  public static final String SIMPLIFIED_TYPE_BUNDLE_KEY = "simplified_character_type";
  public static final String TRADITIONAL_TYPE_BUNDLE_KEY = "traditional_character_type";
  public static final String BOTH_TYPES_BUNDLE_KEY = "both_character_types";
  public static final String LOOKUP_OPTIONS_BUNDLE_KEY = "lookup_options";
  public static final String CHOOSE_FONT_BUNDLE_KEY = "choose_font";
  public static final String OPTIONS_BUNDLE_KEY = "options";
  public static final String AUTO_LOOKUP_BUNDLE_KEY = "auto_lookup";
  public static final String LOOKUP_LOOSENESS_BUNDLE_KEY = "lookup_looseness";
  public static final String MATCH_COUNT_BUNDLE_KEY = "match_count";
  public static final String TYPE_MACRO_BUNDLE_KEY = "type_macro";
  public static final String LOOKUP_MACRO_BUNDLE_KEY = "lookup_macro";
  public static final String UNDO_MACRO_BUNDLE_KEY = "undo_macro";
  public static final String CLEAR_MACRO_BUNDLE_KEY = "clear_macro";
  public static ResourceBundle DEFAULT_ENGLISH_BUNDLE = new DefaultEnglishBundle(null);
  public static Map DEFAULT_ENGLISH_CONTENTS;
  
  static
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("ok", "Ok");
    localHashMap.put("settings", "Settings");
    localHashMap.put("save_settings", "Save Settings");
    localHashMap.put("character_type_bundle_key", "Character Mode");
    localHashMap.put("simplified_character_type", "Simplified");
    localHashMap.put("traditional_character_type", "Traditional");
    localHashMap.put("both_character_types", "Both");
    localHashMap.put("lookup_options", "Lookup options");
    localHashMap.put("choose_font", "Choose Font");
    localHashMap.put("options", "Options");
    localHashMap.put("auto_lookup", "Auto Lookup");
    localHashMap.put("lookup_looseness", "Lookup Looseness");
    localHashMap.put("match_count", "Match Count");
    localHashMap.put("type_macro", "(type macro)");
    localHashMap.put("lookup_macro", "Lookup macro: ");
    localHashMap.put("undo_macro", "Undo macro: ");
    localHashMap.put("clear_macro", "Clear macro: ");
    DEFAULT_ENGLISH_CONTENTS = Collections.unmodifiableMap(localHashMap);
  }
  
  public static class DefaultEnglishBundle
    extends ListResourceBundle
  {
    private DefaultEnglishBundle() {}
    
    public Object[][] getContents()
    {
      Object[][] arrayOfObject = new Object[HanziLookupBundleKeys.DEFAULT_ENGLISH_CONTENTS.size()][2];
      Iterator localIterator = HanziLookupBundleKeys.DEFAULT_ENGLISH_CONTENTS.entrySet().iterator();
      for (int i = 0; i < arrayOfObject.length; i++)
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        arrayOfObject[i][0] = localEntry.getKey();
        arrayOfObject[i][1] = localEntry.getValue();
      }
      return arrayOfObject;
    }
    
    DefaultEnglishBundle(HanziLookupBundleKeys.1 param1)
    {
      this();
    }
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     hanzilookup.i18n.HanziLookupBundleKeys
 * JD-Core Version:    0.7.0.1
 */