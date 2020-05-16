package level3.problem1;

import java.util.LinkedList;
import java.util.Queue;

public class Solution {
    static int minPathLengthForDfs = 0;

    public static void main(String[] args) {
        int[][] map = {{0, 1, 1, 0}, {0, 0, 0, 1}, {1, 1, 0, 0}, {1, 1, 1, 0}};
        System.out.println(solution(map));
    }

    public static int solution(int[][] map) {
        // Your code here
        boolean[][] visited = new boolean[map.length][map[0].length];
//        minPathLengthForDfs = Integer.MAX_VALUE;
//        dfs(map, visited, false, 0, 0, 0);
//        return minPathLengthForDfs;
        return bfs(map, visited);
    }

    public static int bfs(int[][] map, boolean[][] visited) {
        Queue<Node> queue = new LinkedList();
        queue.add(new Node(0, 0, 1, false));
        visited[0][0] = true;
        while (!queue.isEmpty()) {
            Node current = ((LinkedList<Node>) queue).pop();
            if (current.i == map.length - 1 && current.j == map[0].length - 1) {
                return current.currentPathLength;
            }
            addNodeIfValidForNodeCreation(current.i + 1, current.j, current.currentPathLength, current.wallRemoved, visited, map, queue);
            addNodeIfValidForNodeCreation(current.i - 1, current.j, current.currentPathLength, current.wallRemoved, visited, map, queue);
            addNodeIfValidForNodeCreation(current.i, current.j + 1, current.currentPathLength, current.wallRemoved, visited, map, queue);
            addNodeIfValidForNodeCreation(current.i, current.j - 1, current.currentPathLength, current.wallRemoved, visited, map, queue);
        }
        return -1;
    }

    public static void addNodeIfValidForNodeCreation(int i, int j, int currentPathLength, boolean wallRemoved, boolean[][] visited, int[][] map, Queue<Node> queue) {
        if (i >= map.length || j >= map[0].length || i < 0 || j < 0) {
            return;
        }
        if (visited[i][j]) {
            return;
        }
        if (map[i][j] == 0) {
            visited[i][j] = true;
            queue.add(new Node(i, j, currentPathLength + 1, wallRemoved));
        } else if (wallRemoved) {
            return;
        } else {
            visited[i][j] = true;
            queue.add(new Node(i, j, currentPathLength + 1, true));
        }
    }

    /**
     * DFS solution - Not accepted as the solution was not completing in stipulated time.
     */
    public static void dfs(int[][] map, boolean[][] visited, boolean wallRemoved, int currentPathLength, int i, int j) {
        if (i >= map.length || j >= map[0].length || i < 0 || j < 0) return;
        if (visited[i][j]) return;

        boolean wallRemovedTillNow = wallRemoved;
        if (map[i][j] == 0) {
            currentPathLength++;
            if (i == map.length - 1 && j == map[0].length - 1) {
                if (currentPathLength < minPathLengthForDfs) {
                    minPathLengthForDfs = currentPathLength;
                    return;
                }
            }
        } else if (wallRemovedTillNow) {
            return;
        } else {
            wallRemovedTillNow = true;
            currentPathLength++;
        }
        visited[i][j] = true;
        dfs(map, visited, wallRemovedTillNow, currentPathLength, i + 1, j);
        dfs(map, visited, wallRemovedTillNow, currentPathLength, i - 1, j);
        dfs(map, visited, wallRemovedTillNow, currentPathLength, i, j + 1);
        dfs(map, visited, wallRemovedTillNow, currentPathLength, i, j - 1);
        visited[i][j] = false;
    }

    static class Node {
        int i;
        int j;
        int currentPathLength;
        boolean wallRemoved;

        Node(int i, int j, int currentPathLength, boolean wallRemoved) {
            this.i = i;
            this.j = j;
            this.currentPathLength = currentPathLength;
            this.wallRemoved = wallRemoved;
        }
    }
}