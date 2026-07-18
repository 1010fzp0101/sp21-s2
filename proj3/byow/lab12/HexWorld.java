package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private static final Random RANDOM = new Random();

    private static void drawOneLineHexagon(Position p, int length, TETile[][] world, TETile t) {
        for (int i = 0; i < length; ++i) {
            world[p.x + i][p.y] = t;
        }
    }

    /**
     * change the Position p to the next Position to its lower left
     */
    private static Position changeToNextPosition(Position p) {
        return new Position(p.x - 1, p.y - 1);
    }

    /**
     * change position p to its symmetrical position about the bottom of the hexagon.
     */
    private static Position changeSymmetricalPosition(Position p, int symmetry) {
        return new Position(p.x, symmetry - 1 - (p.y - symmetry));
    }

    private static int getSymmetry(Position p, int length, int size) {
        return p.y - (size - ((length - size) / 2 + 1));
    }

    private static void drawHexagon(Position p, int length, TETile[][] world, TETile t, int size) {
        if (length > 3 * size - 2) {
            return;
        }
        drawOneLineHexagon(p, length, world, t);
        drawHexagon(changeToNextPosition(p), length + 2, world, t, size);
        int symmetry = getSymmetry(p, length, size);
        drawOneLineHexagon(changeSymmetricalPosition(p, symmetry), length, world, t);
    }

    public static void addHexagon(Position p, int size, TETile[][] world, TETile t) {
        drawHexagon(p, size, world, t, size);
    }

    public static void addMultipleHexagonInAColumn(Position p, int num, int size, TETile[][] world) {
        Position p1 = new Position(p.x, p.y + 2 * size);
        TETile t = randomTile();
        for (int i = 0; i < num; ++i) {
            t = randomTile();
            p1.y -= 2 * size;
            drawHexagon(p1, size, world, t, size);
        }
    }

    public static void addSyntheticHexagon(Position p, int size, TETile[][] world) {
        int num = 2;
        for (int i = 0; i < 3; ++i, p.x += 2 * size - 1, p.y += size) {
            num += 1;
            addMultipleHexagonInAColumn(p, num, size, world);
        }
        p.y -= size;
        for (int i = 1; i <=2; ++i) {
            num -= 1;
            p.y -= size;
            addMultipleHexagonInAColumn(p, num, size, world);
            p.x += 2 * size - 1;
        }
    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(11);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.WATER;
            case 3: return Tileset.MOUNTAIN;
            case 4: return Tileset.GRASS;
            case 5: return Tileset.LOCKED_DOOR;
            case 6: return Tileset.SAND;
            case 7: return Tileset.TREE;
            case 8: return Tileset.FLOOR;
            case 9: return Tileset.AVATAR;
            case 10: return Tileset.UNLOCKED_DOOR;
            default: return Tileset.NOTHING;
        }
    }

    private static class Position {
        int x;
        int y;
        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static void initializeWorld(TETile[][] world) {
        for (int i = 0; i < WIDTH; ++i) {
            for (int k = 0; k < HEIGHT; ++k) {
                world[i][k] = Tileset.NOTHING;
            }
        }
    }

    private static Position getInitialPosition(int size) {
        int y = HEIGHT - (HEIGHT - 10 * size) / 2 - 2 * size - 1;
        int x = (WIDTH - 11 * size + 6) / 2 + size - 1;
        return new Position(x, y);
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        initializeWorld(world);
        int size = 2;
        Position p = new Position(getInitialPosition(size).x, getInitialPosition(size).y);
        addSyntheticHexagon(p, size, world);
        ter.renderFrame(world);
    }
}
