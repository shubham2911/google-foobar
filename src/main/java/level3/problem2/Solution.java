package level3.problem2;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Solution {
  static BigInteger TWO = BigInteger.valueOf(2);
  static Map<BigInteger, BigInteger> result;

  public static void main(String[] args) {
    System.out.println(solution("15"));
  }

  public static int solution(String x) {
    // Your code here
    result = new HashMap<BigInteger, BigInteger>();
    result.put(BigInteger.ONE, BigInteger.ZERO);
    return rec(new BigInteger(x)).intValue();
  }

  public static BigInteger rec(BigInteger x) {
    // Your code here
    //    System.out.println("rec called for " + x);
    //    result.forEach((k,v) -> {
    //      System.out.println(k + "," + v);
    //    });
    //    System.out.println("----");
    if (result.containsKey(x)) {
      return result.get(x);
    }
    BigInteger answer;
    if (x.mod(TWO).equals(BigInteger.ZERO)) {
      answer = BigInteger.ONE.add(rec(x.divide(TWO)));
    } else {
      answer = BigInteger.ONE.add(rec(x.subtract(BigInteger.ONE)).min(rec(x.add(BigInteger.ONE))));
    }
    result.put(x, answer);
    return answer;
  }
}
