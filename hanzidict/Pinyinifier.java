package hanzidict;

public class Pinyinifier
{
  public static String pinyinify(String paramString)
  {
    String str = "";
    if (paramString.length() < 2) {
      return null;
    }
    int i = paramString.charAt(paramString.length() - 1);
    int j = -1;
    switch (i)
    {
    case 49: 
      j = 1;
      break;
    case 50: 
      j = 2;
      break;
    case 51: 
      j = 3;
      break;
    case 52: 
      j = 4;
      break;
    case 53: 
      j = 5;
    }
    if (j < 0) {
      return null;
    }
    int k = findTonePos(paramString);
    for (int m = 0; m < paramString.length() - 1; m++)
    {
      char c = paramString.charAt(m);
      if (m == k)
      {
        int n = 0;
        if (c == 'a') {
          switch (j)
          {
          case 1: 
            n = 257;
            break;
          case 2: 
            n = 225;
            break;
          case 3: 
            n = 259;
            break;
          case 4: 
            n = 224;
            break;
          case 5: 
            n = 97;
          }
        } else if (c == 'o') {
          switch (j)
          {
          case 1: 
            n = 333;
            break;
          case 2: 
            n = 243;
            break;
          case 3: 
            n = 335;
            break;
          case 4: 
            n = 242;
            break;
          case 5: 
            n = 111;
          }
        } else if (c == 'e') {
          switch (j)
          {
          case 1: 
            n = 275;
            break;
          case 2: 
            n = 233;
            break;
          case 3: 
            n = 277;
            break;
          case 4: 
            n = 232;
            break;
          case 5: 
            n = 101;
          }
        } else if (c == 'i') {
          switch (j)
          {
          case 1: 
            n = 299;
            break;
          case 2: 
            n = 237;
            break;
          case 3: 
            n = 301;
            break;
          case 4: 
            n = 236;
            break;
          case 5: 
            n = 105;
          }
        } else if (c == 'u') {
          switch (j)
          {
          case 1: 
            n = 363;
            break;
          case 2: 
            n = 250;
            break;
          case 3: 
            n = 365;
            break;
          case 4: 
            n = 249;
            break;
          case 5: 
            n = 117;
          }
        } else if (c == 'v') {
          switch (j)
          {
          case 1: 
            n = 470;
            break;
          case 2: 
            n = 472;
            break;
          case 3: 
            n = 474;
            break;
          case 4: 
            n = 476;
            break;
          case 5: 
            n = 252;
          }
        }
        if (n > 0) {
          str = str + n;
        }
      }
      else
      {
        str = str + c;
      }
    }
    return str;
  }
  
  private static int findTonePos(String paramString)
  {
    int i = 0;
    int j = -1;
    for (int k = 0; k < paramString.length(); k++)
    {
      int m = paramString.charAt(k);
      switch (m)
      {
      case 97: 
      case 101: 
        return k;
      case 105: 
      case 118: 
        j = k;
        break;
      case 111: 
      case 117: 
        if (i == 0)
        {
          if ((k + 1 < paramString.length()) && (paramString.charAt(k) == 'u') && (paramString.charAt(k + 1) == 'o')) {
            j = k + 1;
          } else {
            j = k;
          }
          i = 1;
        }
        break;
      }
    }
    return j;
  }
}


/* Location:           C:\Users\ShantaviaeWynn\Desktop\HanziDict.jar
 * Qualified Name:     hanzidict.Pinyinifier
 * JD-Core Version:    0.7.0.1
 */