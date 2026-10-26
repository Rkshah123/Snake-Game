import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VillainSnakeAITest {

    private static boolean[][] emptyGrid(int rows, int cols) {
        return new boolean[rows][cols];
    }

    @Test
    void movesStraightTowardFruitOnEmptyGrid() {
        VillainSnakeAI.Point head = new VillainSnakeAI.Point(0, 0, null);
        VillainSnakeAI.Point fruit = new VillainSnakeAI.Point(3, 0, null);

        VillainSnakeAI.Point next = VillainSnakeAI.getNextMove(head, fruit, 5, 5, emptyGrid(5, 5));

        assertEquals(1, next.x);
        assertEquals(0, next.y);
    }

    @Test
    void routesAroundAnObstacle() {
        int rows = 5;
        int cols = 5;
        boolean[][] obstacles = emptyGrid(rows, cols);
        // Wall between head and fruit with the only gap in the top row.
        obstacles[1][1] = true;
        obstacles[2][1] = true;
        obstacles[3][1] = true;
        obstacles[4][1] = true;

        VillainSnakeAI.Point head = new VillainSnakeAI.Point(0, 2, null);
        VillainSnakeAI.Point fruit = new VillainSnakeAI.Point(3, 2, null);

        VillainSnakeAI.Point next = VillainSnakeAI.getNextMove(head, fruit, rows, cols, obstacles);

        assertNotNull(next);
        assertFalse(obstacles[next.y][next.x], "AI must not step into an obstacle");
        assertTrue(next.x >= 0 && next.x < cols && next.y >= 0 && next.y < rows);
        assertEquals(0, next.x, "the first step must go towards the gap in the wall");
        assertEquals(1, next.y, "the first step must go up towards the gap");
    }

    @Test
    void avoidsObstaclesWhenFruitIsUnreachable() {
        int rows = 5;
        int cols = 5;
        boolean[][] obstacles = emptyGrid(rows, cols);
        // Enclose the fruit completely.
        obstacles[2][2] = true;
        obstacles[2][4] = true;
        obstacles[1][3] = true;
        obstacles[3][3] = true;

        VillainSnakeAI.Point head = new VillainSnakeAI.Point(0, 0, null);
        VillainSnakeAI.Point fruit = new VillainSnakeAI.Point(2, 3, null);

        VillainSnakeAI.Point next = VillainSnakeAI.getNextMove(head, fruit, rows, cols, obstacles);

        assertFalse(obstacles[next.y][next.x], "fallback must stay on free tiles");
    }

    @Test
    void returnsHeadPositionWhenCompletelyTrapped() {
        int rows = 3;
        int cols = 3;
        boolean[][] obstacles = emptyGrid(rows, cols);
        obstacles[0][1] = true;
        obstacles[1][0] = true;
        obstacles[1][2] = true;
        obstacles[2][1] = true;

        VillainSnakeAI.Point head = new VillainSnakeAI.Point(1, 1, null);
        VillainSnakeAI.Point fruit = new VillainSnakeAI.Point(2, 2, null);

        VillainSnakeAI.Point next = VillainSnakeAI.getNextMove(head, fruit, rows, cols, obstacles);

        assertEquals(1, next.x);
        assertEquals(1, next.y);
    }

    @Test
    void findsFruitOnTheSameTileWithoutThrowing() {
        VillainSnakeAI.Point head = new VillainSnakeAI.Point(2, 2, null);
        VillainSnakeAI.Point next = VillainSnakeAI.getNextMove(head, new VillainSnakeAI.Point(2, 2, null), 4, 4, emptyGrid(4, 4));

        assertEquals(2, next.x);
        assertEquals(2, next.y);
    }
}
