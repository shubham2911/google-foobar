package level3.problem3;

import java.util.ArrayList;
import java.util.List;

public class Solution {

  public static void main(String[] args) {
    int[] answer =
        solution(
            new int[][] {
              {1, 2, 3, 0, 0, 0},
              {4, 5, 6, 0, 0, 0},
              {7, 8, 9, 1, 0, 0},
              {0, 0, 0, 0, 1, 2},
              {0, 0, 0, 0, 0, 0},
              {0, 0, 0, 0, 0, 0}
            });
    for (int i : answer) {
      System.out.println(i);
    }
    System.out.println("-------");
  }

  // This is an example of Absorbing Markov Chains
  public static int[] solution(int[][] m) {

    // Creating matrix R and Q for
    // Matrix F = (I - Q)^-1
    // For calculating the long run probability to go from a non absorbing state to an absorbing
    // state -> F*R
    List<Integer> terminalIndexes = new ArrayList<>();
    List<Integer> nonTerminalIndexes = new ArrayList<>();
    long[][][] inputInFraction = new long[m.length][m.length][2];
    for (int i = 0; i < m.length; i++) {
      long sum = 0;
      for (int j = 0; j < m.length; j++) {
        sum += m[i][j];
      }
      if (sum == 0 || sum == m[i][i]) {
        terminalIndexes.add(i);
      } else {
        nonTerminalIndexes.add(i);
      }
      for (int j = 0; j < m.length; j++) {
        long[] fraction = reduceFraction(m[i][j], sum);
        inputInFraction[i][j][0] = fraction[0];
        inputInFraction[i][j][1] = fraction[1];
      }
    }

    if (terminalIndexes.size() == 0
        || terminalIndexes.get(0).equals(0)) { // As we are starting with only S0
      return new int[] {1, 1};
    }

    long[][][] R = new long[nonTerminalIndexes.size()][terminalIndexes.size()][2];
    long[][][] Q = new long[nonTerminalIndexes.size()][nonTerminalIndexes.size()][2];

    int rCounterRow = 0;
    int qCounterRow = 0;
    for (int nonTerminalIndex : nonTerminalIndexes) {
      int rCounterColumn = 0;
      int qCounterColumn = 0;
      for (int terminalIndex : terminalIndexes) {
        R[rCounterRow][rCounterColumn][0] = inputInFraction[nonTerminalIndex][terminalIndex][0];
        R[rCounterRow][rCounterColumn][1] = inputInFraction[nonTerminalIndex][terminalIndex][1];
        rCounterColumn++;
      }

      for (int nonTerminalIndex1 : nonTerminalIndexes) {
        Q[qCounterRow][qCounterColumn][0] = inputInFraction[nonTerminalIndex][nonTerminalIndex1][0];
        Q[qCounterRow][qCounterColumn][1] = inputInFraction[nonTerminalIndex][nonTerminalIndex1][1];
        qCounterColumn++;
      }
      rCounterRow++;
      qCounterRow++;
    }

    long[][][] F = new long[Q.length][Q[0].length][2];

    for (int i = 0; i < Q.length; i++) {
      for (int j = 0; j < Q[0].length; j++) {
        if (i == j) {
          long[] fraction = addTwoFractions(1, 1, -Q[i][j][0], Q[i][j][1]);
          F[i][j][0] = fraction[0];
          F[i][j][1] = fraction[1];
        } else {
          F[i][j][0] = -1 * Q[i][j][0];
          F[i][j][1] = Q[i][j][1];
        }
      }
    }

    F = invert(F);

    long[][] FIntoRFirstRow = new long[R[0].length][2];
    for (int i = 0; i < R[0].length; i++) {
      long[] fraction = new long[] {0l, 0l};
      for (int j = 0; j < F.length; j++) {
        fraction =
            addTwoFractions(
                F[0][j][0] * R[j][i][0], F[0][j][1] * R[j][i][1], fraction[0], fraction[1]);
      }
      FIntoRFirstRow[i][0] = fraction[0];
      FIntoRFirstRow[i][1] = fraction[1];
    }

    long lcmOfDenominators = 1;
    for (int i = 0; i < FIntoRFirstRow.length; i++) {
      if (FIntoRFirstRow[i][0] != 0) {
        lcmOfDenominators =
            (lcmOfDenominators * FIntoRFirstRow[i][1])
                / gcd(lcmOfDenominators, FIntoRFirstRow[i][1]);
      }
    }

    int[] answer = new int[R[0].length + 1];
    int index = 0;
    for (int i = 0; i < FIntoRFirstRow.length; i++) {
      if (FIntoRFirstRow[i][0] == 0) {
        answer[index++] = 0;
      } else {
        answer[index++] = (int) (FIntoRFirstRow[i][0] * (lcmOfDenominators / FIntoRFirstRow[i][1]));
      }
    }
    answer[index] = (int) lcmOfDenominators;
    return answer;
  }

  private static long[] addTwoFractions(long n1, long d1, long n2, long d2) {
    if (n1 == 0) return reduceFraction(n2, d2);
    if (n2 == 0) return reduceFraction(n1, d1);
    long[] firstFraction = reduceFraction(n1, d1);
    long[] secondFraction = reduceFraction(n2, d2);
    return reduceFraction(
        firstFraction[0] * secondFraction[1] + secondFraction[0] * firstFraction[1],
        firstFraction[1] * secondFraction[1]);
  }

  // reducing a fraction bf dividing both numerator and denominator by gcf
  private static long[] reduceFraction(long n, long d) {
    if (n == 0) return new long[] {0, 0};
    long gcd = gcd(n, d);
    return new long[] {n / gcd, d / gcd};
  }

  private static long gcd(long a, long b) {
    if (b == 0) return a;
    return gcd(b, a % b);
  }

  public static long[][][] invert(long[][][] a) {
    int n = a.length;
    long[][][] x = new long[n][n][2];
    long[][][] b = new long[n][n][2];
    int[] index = new int[n];
    for (int i = 0; i < n; ++i) {
      b[i][i][0] = 1;
      b[i][i][1] = 1;
    }

    // Transform the matrix into an upper triangle
    gaussian(a, index);

    // Update the matrix b[i][j] with the ratios stored
    for (int i = 0; i < n - 1; ++i) {
      for (int j = i + 1; j < n; ++j) {
        for (int k = 0; k < n; ++k) {
          long[] fraction =
              reduceFraction(
                  a[index[j]][i][0] * b[index[i]][k][0], a[index[j]][i][1] * b[index[i]][k][1]);
          fraction =
              addTwoFractions(b[index[j]][k][0], b[index[j]][k][1], -1 * fraction[0], fraction[1]);
          b[index[j]][k][0] = fraction[0];
          b[index[j]][k][1] = fraction[1];
        }
      }
    }

    // Perform backward substitutions
    for (int i = 0; i < n; ++i) {
      long[] fraction =
          reduceFraction(
              b[index[n - 1]][i][0] * a[index[n - 1]][n - 1][1],
              b[index[n - 1]][i][1] * a[index[n - 1]][n - 1][0]);
      x[n - 1][i][0] = fraction[0];
      x[n - 1][i][1] = fraction[1];
      for (int j = n - 2; j >= 0; --j) {
        x[j][i][0] = b[index[j]][i][0];
        x[j][i][1] = b[index[j]][i][1];
        for (int k = j + 1; k < n; ++k) {
          fraction = reduceFraction(a[index[j]][k][0] * x[k][i][0], a[index[j]][k][1] * x[k][i][1]);
          fraction = addTwoFractions(x[j][i][0], x[j][i][1], -1 * fraction[0], fraction[1]);
          x[j][i][0] = fraction[0];
          x[j][i][1] = fraction[1];
        }
        fraction = reduceFraction(x[j][i][0] * a[index[j]][j][1], x[j][i][1] * a[index[j]][j][0]);
        x[j][i][0] = fraction[0];
        x[j][i][1] = fraction[1];
      }
    }
    return x;
  }

  // Method to carry out the partial-pivoting Gaussian
  // elimination.  Here index[] stores pivoting order.

  public static void gaussian(long[][][] a, int[] index) {
    int n = index.length;
    long[][] c = new long[n][2];

    // Initialize the index
    for (int i = 0; i < n; ++i) index[i] = i;

    // Find the rescaling factors, one from each row
    for (int i = 0; i < n; ++i) {
      long[] c1 = new long[] {0, 0};
      for (int j = 0; j < n; ++j) {
        long[] c0 = new long[2];
        c0[0] = Math.abs(a[i][j][0]);
        c0[1] = Math.abs(a[i][j][1]);
        if (getDecimalFromFraction(c0) > getDecimalFromFraction(c1)) {
          c1[0] = c0[0];
          c1[1] = c0[1];
        }
      }
      c[i][0] = c1[0];
      c[i][1] = c1[1];
    }

    // Search the pivoting element from each column
    int k = 0;
    for (int j = 0; j < n - 1; ++j) {
      long[] pi1 = new long[] {0, 0};
      for (int i = j; i < n; ++i) {
        long[] pi0 = new long[2];
        pi0[0] = Math.abs(a[index[i]][j][0]);
        pi0[1] = Math.abs(a[index[i]][j][1]);
        long[] fraction = reduceFraction(pi0[0] * c[index[i]][1], pi0[1] * c[index[i]][0]);
        pi0[0] = fraction[0];
        pi0[1] = fraction[1];
        if (getDecimalFromFraction(pi0) > getDecimalFromFraction(pi1)) {
          pi1[0] = pi0[0];
          pi1[1] = pi0[1];
          k = i;
        }
      }

      // Interchange rows according to the pivoting order
      int itmp = index[j];
      index[j] = index[k];
      index[k] = itmp;
      for (int i = j + 1; i < n; ++i) {
        long[] fraction =
            reduceFraction(
                a[index[i]][j][0] * a[index[j]][j][1], a[index[i]][j][1] * a[index[j]][j][0]);
        long[] pj = new long[2];
        pj[0] = fraction[0];
        pj[1] = fraction[1];

        // Record pivoting ratios below the diagonal
        a[index[i]][j][0] = pj[0];
        a[index[i]][j][1] = pj[1];

        // Modify other elements accordingly
        for (int l = j + 1; l < n; ++l) {
          fraction = reduceFraction(pj[0] * a[index[j]][l][0], pj[1] * a[index[j]][l][1]);
          fraction =
              addTwoFractions(a[index[i]][l][0], a[index[i]][l][1], -1 * fraction[0], fraction[1]);
          a[index[i]][l][0] = fraction[0];
          a[index[i]][l][1] = fraction[1];
        }
      }
    }
  }

  private static double getDecimalFromFraction(long[] fraction) {
    if (fraction[0] == 0) {
      return 0.0;
    }
    return (1.0 * fraction[0]) / fraction[1];
  }
}
