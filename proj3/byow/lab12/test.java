package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class test {
    public static void main(String[] args) {
        //Random r = new Random(5);
        //System.out.println(r.nextInt());
        //System.out.println(r.nextInt());
        //System.out.println(r.nextInt());
        //System.out.println(r.nextInt());
        //r = new Random(5);
        //System.out.println(r.nextInt());
        //System.out.println(r.nextInt());
        //System.out.println(r.nextInt());
        //System.out.println(r.nextInt());
        TERenderer ter = new TERenderer();
        ter.initialize(20, 20);
        TETile[][] tiles = new TETile[3][12];
        for (int i = 0; i < 3; ++i) {
            tiles[i][0] = Tileset.AVATAR;
            tiles[i][1] = Tileset.WALL;
            tiles[i][2] = Tileset.FLOOR;
            tiles[i][3] = Tileset.NOTHING;
            tiles[i][4] = Tileset.GRASS;
            tiles[i][5] = Tileset.WATER;
            tiles[i][6] = Tileset.FLOWER;
            tiles[i][7] = Tileset.LOCKED_DOOR;
            tiles[i][8] = Tileset.UNLOCKED_DOOR;
            tiles[i][9] = Tileset.SAND;
            tiles[i][10] = Tileset.MOUNTAIN;
            tiles[i][11] = Tileset.TREE;
        }
        ter.renderFrame(tiles);
    }
}
