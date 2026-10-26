import java.util.*;

public class VillainSnakeAI {

    static class Point {
        public int x, y;
        public Point parent;
        public Point(int x, int y, Point parent) {
            this.x = x;
            this.y = y;
            this.parent = parent;
        }
    }

    private static final int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}}; // right, left, down, up

    /**
     * rows = number of rows (height, y dimension)
     * cols = number of columns (width, x dimension)
     * obstacles[y][x] must be true for blocked tile at (x,y)
     */
    public static Point getNextMove(Point villainHead, Point fruit, int rows, int cols, boolean[][] obstacles) {
        Queue<Point> q = new LinkedList<>();
        boolean[][] visited = new boolean[rows][cols];

        // note: visited[y][x]
        q.add(new Point(villainHead.x, villainHead.y, null));
        visited[villainHead.y][villainHead.x] = true;

        Point target = null;

        while (!q.isEmpty()) {
            Point curr = q.poll();
            if (curr.x == fruit.x && curr.y == fruit.y) {
                target = curr;
                break;
            }

            for (int[] d : DIRS) {
                int nx = curr.x + d[0], ny = curr.y + d[1];
                if (nx >= 0 && ny >= 0 && nx < cols && ny < rows &&
                        !visited[ny][nx] && !obstacles[ny][nx]) {
                    visited[ny][nx] = true;
                    q.add(new Point(nx, ny, curr));
                }
            }
        }

        if (target != null) {
            Point move = target;
            // walk back until we find the first step from the head
            while (move.parent != null && move.parent.parent != null) {
                move = move.parent;
            }
            return new Point(move.x, move.y, null);
        }

        // Fallback: any safe adjacent move
        for (int[] d : DIRS) {
            int nx = villainHead.x + d[0], ny = villainHead.y + d[1];
            if (nx >= 0 && ny >= 0 && nx < cols && ny < rows && !obstacles[ny][nx]) {
                return new Point(nx, ny, null);
            }
        }

        // trapped
        return new Point(villainHead.x, villainHead.y, null);
    }
}
