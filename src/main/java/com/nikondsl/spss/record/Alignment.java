package com.nikondsl.spss.record;

/**
 * Created by IntelliJ IDEA.
* User: Igor
* Date: 28/5/2008
* Time: 7:58:37
* To change this template use File | Settings | File Templates.
*/
public enum Alignment {
  LEFT_ALIGNED, RIGHT_ALIGNED, CENTERED;

  public static Alignment valueOf(int code) {
      if (code>=0 && code<values().length) return values()[code];
      return LEFT_ALIGNED;
  }
}
