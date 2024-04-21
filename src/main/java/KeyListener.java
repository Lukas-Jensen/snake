import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

public class KeyListener implements java.awt.event.KeyListener {

    /**
     * 0 == North, 1 == East, 2 == South, 3 == West
     */
    private MovingDirections direction = MovingDirections.RIGHT;
    private final JLabel head;

    private final JPanel panel;

    private final Settings settings;
    private boolean startMoving = true;
    private final GameStuff gameStuff;
    private final JFrame gameFrame;

    private Timer timer;

    public KeyListener(JLabel head, JPanel panel, Settings settings, JFrame gameFrame, GameStuff gameStuff) {
        this.gameStuff = gameStuff;
        this.settings = settings;
        this.gameFrame = gameFrame;
        this.head = head;
        this.panel = panel;
    }

    /**
     * Keys:
     * W 87;
     * A 65;
     * S 83;
     * D 68;
     * Leertaste 32;
     * Esc 27;
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {

            //W
            case 87 -> direction = MovingDirections.UP;

            //A
            case 65 -> direction = MovingDirections.LEFT;

            //S
            case 83 -> direction = MovingDirections.DOWN;

            //D
            case 68 -> direction = MovingDirections.RIGHT;

            //Esc
            case 27 -> openPauseMenu();

            //Space
            case 32 -> {
                if (startMoving) {
                    moveSnake();
                    startMoving = false;
                }
            }
        }
    }

    private void moveSnake() {
        timer = new Timer();

        int speed = getSpeed();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                moveAction();
            }
        }, 0, speed);
    }

    private void moveAction() {
        switch (direction) {
            case UP -> head.setLocation(head.getX(), head.getY() - GameFrame.FIELD_HEIGHT_PX);
            case RIGHT -> head.setLocation(head.getX() + GameFrame.FIELD_WIDTH_PX, head.getY());
            case DOWN -> head.setLocation(head.getX(), head.getY() + GameFrame.FIELD_HEIGHT_PX);
            case LEFT -> head.setLocation(head.getX() - GameFrame.FIELD_WIDTH_PX, head.getY());
        }
        testIfDied(head.getX(), head.getY());
        panel.revalidate();
        panel.repaint();
    }

    private void died() {
        new DiedFrame(settings, gameStuff);
    }

    private void testIfDied(int x, int y) {

        boolean[][] obstacles = getObstacles(gameStuff.getCurrentLevel());

        if (obstacles[x / GameFrame.FIELD_WIDTH_PX][(y - 60) / GameFrame.FIELD_HEIGHT_PX]){
            gameStuff.setLives(gameStuff.getLives() - 1);
            if (gameStuff.getLives() > 0){
                pauseTimer();
                respawn();
            }else {
                pauseTimer();
                died();
            }
        }
    }

    private void respawn() {
        resumeTimer();
    }

    private boolean[][] getObstacles(final Levels level) {
        switch (level){

            case LEVEL1 -> {
                return LevelEins.getObstacles();
            }
            default -> {
                System.out.println("Bla");
                return LevelEins.getObstacles();
            }
        }
    }

    private int getSpeed() {

        switch (settings.getMode()) {

            case BEGINNER -> {
                System.out.println("B");
                return 160;
            }
            case ADULT -> {
                System.out.println("A");
                return 150;
            }
            case MASTER -> {
                System.out.println("M");
                return 130;
            }
            case GOD -> {
                System.out.println("G");
                return 110;
            }
            default -> {
                System.out.println("N");
                return 200;
            }

        }
    }

    private void pauseTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void openPauseMenu() {
        pauseTimer();
        new PauseFrame(settings, this, gameFrame, gameStuff);
        //TODO Pause Timer for time
    }

    public void resumeTimer() {
        moveSnake();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
