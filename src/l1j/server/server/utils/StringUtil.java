package l1j.server.server.utils;

public class StringUtil
{
  public static boolean isEmpty(String str)
  {
    return (str == null) || (str.trim().length() == 0);
  }

  public static boolean areNotEmpty(String[] values)
  {
    boolean result = true;
    if ((values == null) || (values.length == 0))
      result = false;
    else {
      for (String value : values) {
        result &= !isEmpty(value);
        if (!result) {
          return result;
        }
      }
    }
    return result;
  }

  public static String join(String[] resource, String separater)
  {
    if ((resource == null) || (resource.length == 0)) {
      return null;
    }
    int len = resource.length;
    StringBuilder sb = new StringBuilder();
    if (len > 0) {
      sb.append(resource[0]);
    }
    for (int i = 1; i < len; i++) {
      sb.append(separater);
      sb.append(resource[i]);
    }
    return sb.toString();
  }

  public static String getMiddle(String sourse, String first, String last) {
    if (!areNotEmpty(new String[] { sourse, first, last })) {
      return null;
    }
    int beginIndex = sourse.indexOf(first) + first.length();
    int endIndex = sourse.lastIndexOf(last);
    return sourse.substring(beginIndex, endIndex);
  }
}
