public class LevelDrei extends GameFrame{
    private static boolean[][] obstacles;
    private final GameStuff gameStuff;

    public LevelDrei(final Settings settings, final GameStuff gameStuff){

        super(settings, gameStuff);

        this.gameStuff = gameStuff;
        gameStuff.setCurrentLevel(Levels.LEVEL3);

        obstacles = translateLevel("level3.txt");
        setLevel("LEVEL 3");
        startTimer();
        printObstacles(obstacles);
        this.revalidate();
        this.repaint();
    }

    private void printObstacles(boolean[][] obstacles) {

        for (int y = 0; y < NUM_FIELDS_VERT; y++) {
            for (int x = 0; x < NUM_FIELDS_HORIZ; x++) {
                System.out.print(obstacles[x][y] ? "X" : " ");
            }
            System.out.println();
        }
        System.out.flush();
    }
    public static boolean[][] getObstacles() {
        return obstacles;
    }
}
