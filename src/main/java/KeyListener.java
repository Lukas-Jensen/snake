import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Timer;
import java.util.*;

public class KeyListener implements java.awt.event.KeyListener {

    /**
     * RIGHT LEFT UP AND DOWN (directions in which snake is moving)
     */
    private MovingDirections direction = MovingDirections.RIGHT;
    /**
     * The timer which causes the snake to move frequently
     */
    private Timer timer;
    /**
     * true if the snake is currently entering a new level
     */
    private boolean enteringNewLevel = false;
    /**
     * says if the snake can die or not
     */
    private boolean isInvinceble = false;
    /**
     * The head label of the snake
     */
    private final JLabel head;
    /**
     * The label of the displayed boost
     */
    private final JLabel boost;
    /**
     * says if all the keys are collected and potentially the door could open
     */
    private boolean allKeysCollected;
    /**
     * The main frame of the game
     */
    private final JFrame gameFrame;
    /**
     * The main panel (The panel on which the snake and the obstacles are)
     */
    private final JPanel panel;
    /**
     * The label on which the current points are displayed
     */
    private final JLabel points;
    /**
     * The label which displays the current lives of the snake
     */
    private final JLabel lives;
    /**
     * The label which displays the current amount of collected keys
     */
    private final JLabel keys;
    /**
     * The settings object which is given every class though the constructor
     */
    private final Settings settings;
    /**
     * The gameStuff stuff which is given almost every class through the constructor
     */
    private final GameStuff gameStuff;
    /**
     * The instance of the class which manages the displayed timer
     */
    private final PlaytimeManager playtimeManager;
    /**
     * The list of the tails of the snake
     */
    private final List<SnakeTail> tails = new LinkedList<>();
    private final Playsound playBoostSound;
    private final Playsound playDiedSound;
    /**
     * The list of pressed directions in which the first object will be taken for the direction whenever the
     * snake is moving
     */
    private final List<MovingDirections> pressedDirections = new LinkedList<>();
    /**
     * Says if the snake has already started moving so pressing the space-bar again don´t makes it faster
     */
    public boolean startMoving = true;
    /**
     * Says if the timer moving the snake has started so it cant be started again
     */
    private boolean timerStartedOnce;
    /**
     * The type of the current boost
     */
    private IngameBoost currentBoost;
    /**
     * The color for the Achievement text which will be shown if an achievement is reached
     */
    public static final Color ACHIEVEMENT_TEXT_COLOR = new Color(200, 0, 200);
    /**
     * The color for the Achievement border which will be shown if an achievement is reached
     */
    public static final Color ACHIEVEMENT_BORDER_COLOR = new Color(150, 0, 150);
    /**
     * The background-color for the achievement box which will be shown if the player reached an achievement
     */
    public static final Color ACHIEVEMENT_BACKGROUND_COLOR = new Color(96, 96, 96);
    /**
     * List of collected boost types throughout the round. Is used to look if the player managed to collect every boost type in one round
     */
    private final Set<IngameBoost> boostsCollected = new TreeSet<>();
    /**
     * Time a displayed achievement stays in milliseconds
     */
    public static final int ACHIEVEMENT_DISPLAYTIME = 4500;

    /**
     * Constructor
     */
    public KeyListener(JLabel head, JPanel panel, Settings settings, JFrame gameFrame, GameStuff gameStuff, JLabel points,
                       JLabel lives, JLabel keys, PlaytimeManager playtimeManager) {
        this.gameStuff = gameStuff;
        this.settings = settings;
        this.gameFrame = gameFrame;
        this.head = head;
        this.panel = panel;
        this.points = points;
        this.lives = lives;
        this.keys = keys;
        this.playtimeManager = playtimeManager;
        boost = new JLabel();
        playBoostSound = new Playsound("BoostCollect.wav");
        playDiedSound = new Playsound("Died.wav");
        boost.setBounds(600, 1000, GameFrame.FIELD_WIDTH_PX, GameFrame.FIELD_HEIGHT_PX);
        pressedDirections.add(MovingDirections.RIGHT);
        update();
    }

    public void update() {
        timerStartedOnce = false;
        currentBoost = IngameBoost.REGULAR_BOOST;
        spawnBoost();
        allKeysCollected = false;
        isInvinceble = false;
        System.out.println("Updated");
    }

    /**
     * Increases the snakes length by the given int in SnakeTails.
     * Does this by adding a tail to the position of the last snake in the tails list.
     */
    private void increaseSnakeLength(int timesToIncrease) {

        for (int i = 0; i < timesToIncrease; i++) {

            SnakeTail tailToAdd = new SnakeTail(tails.get(tails.size() - 1).getX(), tails.get(tails.size() - 1).getY(), GameFrame.FIELD_WIDTH_PX, GameFrame.FIELD_HEIGHT_PX);
            tailToAdd.setBackground(settings.getSkin().getTailColor());
            tailToAdd.setOpaque(true);
            panel.add(tailToAdd);
            tails.add(tailToAdd);
        }
        if (tails.size() >= 100 && !settings.isTHE_LONGEST_OF_THEM_ALLcollected()) {
            new Popup(Achievement.THE_LONGEST_OF_THEM_ALL.getName(), panel, ACHIEVEMENT_TEXT_COLOR,
                    ACHIEVEMENT_BACKGROUND_COLOR, ACHIEVEMENT_BORDER_COLOR, ACHIEVEMENT_DISPLAYTIME, gameStuff,
                    Achievement.THE_LONGEST_OF_THEM_ALL.getDescription());
            settings.setTHE_LONGEST_OF_THEM_ALLcollected(true);
            new SettingsManager().save(settings);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Implemented method which looks what key was pressed
     */
    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {

            //W and arrow key up
            case 87, 38 -> {
                if (pressedDirections.get(pressedDirections.size() - 1) != MovingDirections.DOWN && direction != MovingDirections.UP) {
                    direction = MovingDirections.UP;
                    if (pressedDirections.size() < 5)
                        pressedDirections.add(direction);
                }
            }
            //A and arrow key left
            case 65, 37 -> {
                if (pressedDirections.get(pressedDirections.size() - 1) != MovingDirections.RIGHT && direction != MovingDirections.LEFT) {
                    direction = MovingDirections.LEFT;
                    if (pressedDirections.size() < 5)
                        pressedDirections.add(direction);
                }
            }

            //S and arrow key down
            case 83, 40 -> {
                if (pressedDirections.get(pressedDirections.size() - 1) != MovingDirections.UP && direction != MovingDirections.DOWN) {
                    direction = MovingDirections.DOWN;
                    if (pressedDirections.size() < 5)
                        pressedDirections.add(direction);
                }
            }
            //D and arrow key right
            case 68, 39 -> {
                if (pressedDirections.get(pressedDirections.size() - 1) != MovingDirections.LEFT && direction != MovingDirections.RIGHT) {
                    direction = MovingDirections.RIGHT;
                    if (pressedDirections.size() < 5)
                        pressedDirections.add(direction);
                }
            }
            //Esc
            case 27 -> openPauseMenu();

            //Space
            case 32 -> {

                checkForStartingAchievements();

                if (startMoving) {
                    moveSnake();
                    startMoving = false;
                }
            }
            //B
            case 66 -> spawnBoost();
        }
    }

    private void checkForStartingAchievements() {
        if (!settings.isWHATS_WINDING_THEREcollected()) {
            new Popup(Achievement.WHATS_WINDING_THERE.getName(), panel, ACHIEVEMENT_TEXT_COLOR,
                    ACHIEVEMENT_BACKGROUND_COLOR, ACHIEVEMENT_BORDER_COLOR, ACHIEVEMENT_DISPLAYTIME, gameStuff,
                    Achievement.WHATS_WINDING_THERE.getDescription());
            settings.setWHATS_WINDING_THEREcollected(true);
            new SettingsManager().save(settings);
        }
        if (!settings.isBEGINNERcollected() && settings.getMode().equals(Modes.BEGINNER)) {
            new Popup(Achievement.BEGINNER.getName(), panel, ACHIEVEMENT_TEXT_COLOR,
                    ACHIEVEMENT_BACKGROUND_COLOR, ACHIEVEMENT_BORDER_COLOR, ACHIEVEMENT_DISPLAYTIME, gameStuff,
                    Achievement.BEGINNER.getDescription());
            settings.setBEGINNERcollected(true);
            new SettingsManager().save(settings);
        }
        if (!settings.isIAM_GROWING_UPcollected() && settings.getMode().equals(Modes.ADULT)) {
            new Popup(Achievement.IAM_GROWING_UP.getName(), panel, ACHIEVEMENT_TEXT_COLOR,
                    ACHIEVEMENT_BACKGROUND_COLOR, ACHIEVEMENT_BORDER_COLOR, ACHIEVEMENT_DISPLAYTIME, gameStuff,
                    Achievement.IAM_GROWING_UP.getDescription());
            settings.setIAM_GROWING_UPcollected(true);
            new SettingsManager().save(settings);
        }
        if (!settings.isMASTERcollected() && settings.getMode().equals(Modes.MASTER)) {
            new Popup(Achievement.MASTER.getName(), panel, ACHIEVEMENT_TEXT_COLOR,
                    ACHIEVEMENT_BACKGROUND_COLOR, ACHIEVEMENT_BORDER_COLOR, ACHIEVEMENT_DISPLAYTIME, gameStuff,
                    Achievement.MASTER.getDescription());
            settings.setMASTERcollected(true);
            new SettingsManager().save(settings);
        }
        if (!settings.isDEMIGODcollected() && settings.getMode().equals(Modes.GOD)) {
            new Popup(Achievement.DEMIGOD.getName(), panel, ACHIEVEMENT_TEXT_COLOR,
                    ACHIEVEMENT_BACKGROUND_COLOR, ACHIEVEMENT_BORDER_COLOR, ACHIEVEMENT_DISPLAYTIME, gameStuff,
                    Achievement.DEMIGOD.getDescription());
            settings.setDEMIGODcollected(true);
            new SettingsManager().save(settings);
        }
    }

    /**
     * This starts the timer which will depending on the mode call moveAction() from 110-200 milliseconds
     */
    private void moveSnake() {
        timer = new Timer();
        timerStartedOnce = true;

        int speed = settings.getMode().getSpeed();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                moveAction();
            }
        }, 0, speed);
    }

    private void spawnBoost() {

        int randomNumber = new Random().nextInt(100) + 1;

        /*Set current boost depending on the randomNumber*/
        if (randomNumber <= 25) { //5!!
            currentBoost = IngameBoost.KEY_BOOST;
        } else if (randomNumber <= 45) {
            currentBoost = IngameBoost.REGULAR_BOOST;
        } else if (randomNumber <= 70) {
            currentBoost = IngameBoost.NICE_BOOST;
        } else if (randomNumber <= 73) {
            currentBoost = IngameBoost.MYTHICAL_BOOST;
        } else if (randomNumber <= 80) {
            currentBoost = IngameBoost.BAD_BOOST;
        } else if (randomNumber <= 85) {
            currentBoost = IngameBoost.HEALTH_BOOST;
        } else if (randomNumber <= 95) {
            currentBoost = IngameBoost.GOOD_BOOST;
        } else if (randomNumber == 96) {
            currentBoost = IngameBoost.GOD_BOOST;
        } else {
            currentBoost = IngameBoost.LOSER_BOOST;
        }

        spawnLabel();

    }

    private void spawnLabel() {

        boost.setBackground(currentBoost.getBoostColor());
        boost.setOpaque(true);
        //boost.setIcon(getBoostIcon());
        boost.setVisible(true);
        panel.add(boost);
        setRandomBoostLocation();

    }

    /**
     * Sets a radom Location for the boost and looks if it would be on the snake or on one of
     * the obstacles. If so it will create a new Location for the boost by recursion.
     */
    private void setRandomBoostLocation() {
        int randomX = new Random().nextInt(104);
        int randomY = new Random().nextInt(55);

        System.out.println(gameStuff.getObstacles()[randomX][randomY]);

        if (gameStuff.getObstacles()[randomX][randomY] || boostOnSnake(randomX, randomY)) {
            setRandomBoostLocation();
        } else {
            boost.setLocation(randomX * GameFrame.FIELD_WIDTH_PX, (randomY + 3) * GameFrame.FIELD_HEIGHT_PX);
            System.out.println(randomX + " " + randomY);
            boost.setVisible(true);
            panel.revalidate();
            panel.repaint();
        }


    }

    /**
     * Tests if the boost would spawn on the snake.
     * If so it returns true, otherwise false.
     */
    private boolean boostOnSnake(final int x, final int y) {

        for (SnakeTail tail : tails) {
            if (x == tail.getX() || y == tail.getY()) {
                return true;
            }
        }
        return false;
    }

    private void eatBoost() {

        gameStuff.setPoints(gameStuff.getPoints() + ((long) currentBoost.getPoints() * settings.getMode().getModeMultiplier()));
        gameStuff.setLives(gameStuff.getLives() + currentBoost.getHealthBoost());
        increaseSnakeLength(currentBoost.getLengthBoost());
        gameStuff.setKeyAmount(gameStuff.getKeyAmount() + currentBoost.getKeyBoost());

        points.setText(String.valueOf(gameStuff.getPoints()));
        lives.setText("lives: " + gameStuff.getLives());
        keys.setText("keys: " + gameStuff.getKeyAmount() + "/1");

        panel.remove(boost);
        panel.revalidate();
        panel.repaint();

        playBoostSound.playSound();
        boostsCollected.add(currentBoost);

        //Checks for YUMMY achievement
        if (!settings.isYUMMYcollected()) {
            new Popup(Achievement.YUMMY.getName(), panel, ACHIEVEMENT_TEXT_COLOR, ACHIEVEMENT_BACKGROUND_COLOR,
                    ACHIEVEMENT_BORDER_COLOR, ACHIEVEMENT_DISPLAYTIME, gameStuff,
                    Achievement.YUMMY.getDescription());
            settings.setYUMMYcollected(true);
            new SettingsManager().save(settings);
        }

        lookForPointsAchievement(gameStuff.getPoints());

        //Checks for GOT_THEM_ALL achievement

        if (boostsCollected.containsAll(Set.of(IngameBoost.values())) && !settings.isGOT_THEM_ALLcollected()) {
            new Popup(Achievement.GOT_THEM_ALL.getName(), panel, ACHIEVEMENT_TEXT_COLOR, ACHIEVEMENT_BACKGROUND_COLOR,
                    ACHIEVEMENT_BORDER_COLOR, ACHIEVEMENT_DISPLAYTIME, gameStuff,
                    Achievement.GOT_THEM_ALL.getDescription());
            settings.setGOT_THEM_ALLcollected(true);
            new SettingsManager().save(settings);
        }


        if (gameStuff.getKeyAmount() >= 1) {
            allKeysCollected = true;
        }

        if (!allKeysCollected) {
            spawnBoost();
        } else {
            openDoor();
        }
    }


    private void lookForPointsAchievement(long points) {
        //Checks for COLLECTOR achievement
        if (!settings.isCOLLECTORcollected() && points >= 3000) {
            new Popup(Achievement.COLLECTOR.getName(), panel, ACHIEVEMENT_TEXT_COLOR, ACHIEVEMENT_BACKGROUND_COLOR,
                    ACHIEVEMENT_BORDER_COLOR, ACHIEVEMENT_DISPLAYTIME, gameStuff,
                    Achievement.COLLECTOR.getDescription());
            settings.setCOLLECTORcollected(true);
            new SettingsManager().save(settings);
        }
        //Checks for COLLECTING_MASTER achievement
        if (!settings.isCOLLECTING_MASTERcollected() && points >= 10000) {
            new Popup(Achievement.COLLECTING_MASTER.getName(), panel, ACHIEVEMENT_TEXT_COLOR, ACHIEVEMENT_BACKGROUND_COLOR,
                    ACHIEVEMENT_BORDER_COLOR, ACHIEVEMENT_DISPLAYTIME, gameStuff,
                    Achievement.COLLECTING_MASTER.getDescription());
            settings.setCOLLECTING_MASTERcollected(true);
            new SettingsManager().save(settings);
        }
        //Checks for COLLECTING_GOD achievement
        if (!settings.isCOLLECTING_GODcollected() && points >= 20000) {
            new Popup(Achievement.COLLECTING_GOD.getName(), panel, ACHIEVEMENT_TEXT_COLOR, ACHIEVEMENT_BACKGROUND_COLOR,
                    ACHIEVEMENT_BORDER_COLOR, ACHIEVEMENT_DISPLAYTIME, gameStuff,
                    Achievement.COLLECTING_GOD.getDescription());
            settings.setCOLLECTING_GODcollected(true);
            new SettingsManager().save(settings);
        }
        //Checks for COLLECTING_ADDICT achievement
        if (!settings.isCOLLECTING_ADDICTcollected() && points >= 50000) {
            new Popup(Achievement.COLLECTING_ADDICT.getName(), panel, ACHIEVEMENT_TEXT_COLOR, ACHIEVEMENT_BACKGROUND_COLOR,
                    ACHIEVEMENT_BORDER_COLOR, ACHIEVEMENT_DISPLAYTIME, gameStuff,
                    Achievement.COLLECTING_ADDICT.getDescription());
            settings.setCOLLECTING_ADDICTcollected(true);
            new SettingsManager().save(settings);
        }
        //Checks for I_HAVE_NO_LIFE achievement
        if (!settings.isI_HAVE_NO_LIFEcollected() && points >= 100000) {
            new Popup(Achievement.I_HAVE_NO_LIFE.getName(), panel, ACHIEVEMENT_TEXT_COLOR, ACHIEVEMENT_BACKGROUND_COLOR,
                    ACHIEVEMENT_BORDER_COLOR, ACHIEVEMENT_DISPLAYTIME, gameStuff,
                    Achievement.I_HAVE_NO_LIFE.getDescription());
            settings.setI_HAVE_NO_LIFEcollected(true);
            new SettingsManager().save(settings);
        }
        //Checks for EXPLOIT achievement
        if (!settings.isEXPLOITcollected() && points >= 200000) {
            new Popup(Achievement.EXPLOIT.getName(), panel, ACHIEVEMENT_TEXT_COLOR, ACHIEVEMENT_BACKGROUND_COLOR,
                    ACHIEVEMENT_BORDER_COLOR, ACHIEVEMENT_DISPLAYTIME, gameStuff,
                    Achievement.EXPLOIT.getDescription());
            settings.setEXPLOITcollected(true);
            new SettingsManager().save(settings);
        }
    }

    private void openDoor() {
        //28, 103
        Component[] components = panel.getComponents();
        for (Component component : components) {
            if (component instanceof JLabel &&
                    component.getBounds().contains(new Point(103 * GameFrame.FIELD_WIDTH_PX, (27 + 3) * GameFrame.FIELD_HEIGHT_PX))) {

                panel.remove(component);
                panel.revalidate();
                panel.repaint();
            }
        }
        new Popup("DOOR OPENED", panel,
                new Color(255, 255, 0), ACHIEVEMENT_BACKGROUND_COLOR, new Color(255, 255, 0),
                2000, gameStuff, "Get to it quickly!");
    }

    private void moveAction() {

        moveTails();

        switch (pressedDirections.get(0)) {
            case UP -> head.setLocation(head.getX(), head.getY() - GameFrame.FIELD_HEIGHT_PX);
            case RIGHT -> head.setLocation(head.getX() + GameFrame.FIELD_WIDTH_PX, head.getY());
            case DOWN -> head.setLocation(head.getX(), head.getY() + GameFrame.FIELD_HEIGHT_PX);
            case LEFT -> head.setLocation(head.getX() - GameFrame.FIELD_WIDTH_PX, head.getY());
        }
        if (pressedDirections.size() > 1) {
            pressedDirections.remove(0);
        }

        if (head.getLocation().equals(new Point(103 * GameFrame.FIELD_WIDTH_PX, (27 + 3) * GameFrame.FIELD_HEIGHT_PX))
                && allKeysCollected && !enteringNewLevel) {
            enteringNewLevel = true;
            System.out.println("WON");
            newLevel();
        }

        if (head.getLocation().equals(boost.getLocation())) {
            eatBoost();
        }

        if (!isInvinceble && !enteringNewLevel) {
            testIfDied(head.getX() / GameFrame.FIELD_WIDTH_PX, (head.getY() - 60) / GameFrame.FIELD_HEIGHT_PX);
        }

        panel.revalidate();
        panel.repaint();
    }

    private void newLevel() {
        someStuff();
        isInvinceble = true;
        startMoving = false;
        timer.cancel();

        switch (gameStuff.getCurrentLevel()) {
            case LEVEL1 -> newLevelSettings(Levels.LEVEL2, 2);
            case LEVEL2 -> newLevelSettings(Levels.LEVEL3, 3);
            case LEVEL3 -> newLevelSettings(Levels.LEVEL4, 4);
            case LEVEL4 -> newLevelSettings(Levels.LEVEL5, 5);
            case LEVEL5 -> newLevelSettings(Levels.LEVEL6, 6);
            case LEVEL6 -> newLevelSettings(Levels.LEVEL7, 7);
            case LEVEL7 -> newLevelSettings(Levels.LEVEL8, 8);
            case LEVEL8 -> newLevelSettings(Levels.LEVEL9, 9);
            case LEVEL9 -> newLevelSettings(Levels.LEVEL10, 10);
            case LEVEL10 -> newLevelSettings(Levels.LEVEL11, 11);
            case LEVEL11 -> newLevelSettings(Levels.LEVEL12, 12);
            //TODO IMPORTANT FOR NEW LEVELS!
        }
    }

    /**
     * This method sets the current level up one time if a new level is reached.
     * The method which is called by a timer in the GameFrame class will notice that and start a new level.
     */
    private void newLevelSettings(final Levels level, final int reachedLevel) {
        gameStuff.setCurrentLevel(level);
        if (settings.getUnlockedLevel() < reachedLevel) {
            settings.setUnlockedLevel(reachedLevel);
            gameStuff.setSendUnlockMessage(true);
        } else {
            gameStuff.setSendUnlockMessage(false);
        }
        allKeysCollected = false;
        new SettingsManager().save(settings);
    }

    /**
     * This method will be called from the GameFrame class after it has repainted itself for the label to be shown
     * when the player has the chance to see it and not when the level is repainting. It will send a AchievementThing
     * based on the Colors of the unlocked Skin
     */
    public void sendUnlockThing(final int reachedLevel) {

        for (Skins skin : Skins.values()) {

            if (reachedLevel == skin.getUnlockNumber()) {
                new Popup("New skin unlocked!", panel,
                        skin.getTailColor(), ACHIEVEMENT_BACKGROUND_COLOR, skin.getHeadColor(), 3500, gameStuff,
                        "Skin: " + skin + " unlocked!");
                new SettingsManager().save(settings);
            }
        }
    }

    /**
     * Does some settings in the gameStuff instance
     */
    private void someStuff() {
        System.out.println("some stuff");
        gameStuff.setKeyAmount(0);
        gameStuff.setTimeElapsed(playtimeManager.getTime());
        gameStuff.setLives(gameStuff.getLives());
    }

    /**
     * Will change the highscore if a new points all time high is achieved
     */
    private void highscore() {
        if (gameStuff.getPoints() > settings.getHighestPoints()) {
            settings.setHighestPoints(gameStuff.getPoints());
            settings.setHighScoreTime(playtimeManager.setTime(gameStuff.getTimeElapsed()));
            settings.setHighScoreLevel(gameStuff.getCurrentLevel());
            settings.setHighScoreMode(settings.getMode());
            new SettingsManager().save(settings);
        }
    }

    /**
     * Moves all the tails in the tails list to the position of the next tail
     */
    private void moveTails() {

        int moveToX = head.getX();
        int moveToY = head.getY();

        for (SnakeTail tail : tails) {
            int tailX = tail.getX();
            int tailY = tail.getY();
            tail.setLocation(moveToX, moveToY);
            moveToX = tailX;
            moveToY = tailY;
        }
    }

    /**
     * Method that is called when the player died
     */
    private void died() {
        playtimeManager.stopTimer();
        System.out.println("timer stopped");
        boost.setVisible(false);

        ListIterator<SnakeTail> iterator = tails.listIterator(tails.size());

        Timer removeTimer = new Timer();
        removeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (iterator.hasPrevious()) {
                    SnakeTail tail = iterator.previous();
                    panel.remove(tail);
                    panel.revalidate();
                    panel.repaint();
                    iterator.remove();
                } else {
                    cancel();
                }
            }
        }, 0, settings.getMode().getSpeed() / 4);

        highscore();
        boostsCollected.clear();

        new DiedFrame(settings, gameStuff, gameFrame);
    }

    private void testIfDied(int x, int y) {

        if (gameStuff.getObstacles()[x][y] || snakeHitItself()) {
            pauseTimer();
            startMoving = false;
            playDiedSound.playSound();
            gameStuff.setLives(gameStuff.getLives() - 1);
            if (gameStuff.getLives() > 0) {
                respawn();
                System.out.println("respawn");
            } else {
                died();
                System.out.println("died");
            }
            lives.setText("lives: " + gameStuff.getLives());
        }
    }

    /**
     * Method that looks if the snake hits itself with its head
     */
    private boolean snakeHitItself() {

        for (SnakeTail tail : tails) {

            if (head.getLocation().equals(tail.getLocation())) {

                if (!settings.isSMELLS_FAMILIARcollected()) {
                    new Popup(Achievement.SMELLS_FAMILIAR.getName(), panel, ACHIEVEMENT_TEXT_COLOR,
                            ACHIEVEMENT_BACKGROUND_COLOR, ACHIEVEMENT_BORDER_COLOR, ACHIEVEMENT_DISPLAYTIME, gameStuff,
                            Achievement.SMELLS_FAMILIAR.getDescription());
                    settings.setSMELLS_FAMILIARcollected(true);
                    new SettingsManager().save(settings);
                }

                return true;
            }

        }
        return false;
    }

    /**
     * Called when the snake has to respawn and calls the respawnTheSnake method
     */
    public void respawn() {
        pressedDirections.clear();
        pressedDirections.add(MovingDirections.RIGHT);
        direction = MovingDirections.RIGHT;
        respawnTheSnake();
        if (!settings.isNOOBcollected() && gameStuff.getCurrentLevel().equals(Levels.LEVEL1)) {
            new Popup(Achievement.NOOB.getName(), panel, ACHIEVEMENT_TEXT_COLOR, ACHIEVEMENT_BACKGROUND_COLOR,
                    ACHIEVEMENT_BORDER_COLOR, ACHIEVEMENT_DISPLAYTIME, gameStuff,
                    Achievement.NOOB.getDescription());
            settings.setNOOBcollected(true);
            new SettingsManager().save(settings);
        }
    }

    private void respawnTheSnake() {
        startMoving = false;
        int index = 0;

        if (enteringNewLevel) {
            ListIterator<SnakeTail> iterator = tails.listIterator(2);
            for (int i = 1040; i > 999; i -= GameFrame.FIELD_WIDTH_PX) {
                tails.get(index).setLocation(i, 600);
                panel.revalidate();
                panel.repaint();
                index++;
            }
            head.setLocation(1060, 600);
            while (iterator.hasNext())
                iterator.next().setLocation(tails.get(2).getLocation());
            enteringNewLevel = false;
        } else {

            ListIterator<SnakeTail> iterator = tails.listIterator(tails.size());

            Timer respawnTimer = new Timer();
            respawnTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    if (iterator.hasPrevious()) {
                        SnakeTail tail = iterator.previous();
                        if (!tail.equals(tails.get(0)) &&
                                !tail.equals(tails.get(1)) &&
                                !tail.equals(tails.get(2))) {
                            tail.setLocation(1000, 600);
                            tail.setVisible(false);
                            panel.revalidate();
                            panel.repaint();
                        } else {
                            cancel();
                            replaceTailAndHead();
                        }
                    } else {
                        cancel();
                    }
                }
            }, 0, settings.getMode().getSpeed() / 2);
        }
    }

    private void replaceTailAndHead() {
        int index = 0;
        for (int i = 1040; i > 999; i -= GameFrame.FIELD_WIDTH_PX) {
            tails.get(index).setLocation(i, 600);
            panel.revalidate();
            panel.repaint();
            index++;
        }
        head.setLocation(1060, 600);
        for (SnakeTail tail : tails) {
            tail.setVisible(true);
        }
        startMoving = true;
    }


    /**
     * This will pause the timer which moves the snake, causing the snake to stop
     */
    public void pauseTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * Opens the pause menu for the player when the Esc button is pressed
     */
    private void openPauseMenu() {
        highscore();
        pauseTimer();
        playtimeManager.stopTimer();
        new PauseFrame(settings, this, gameFrame, gameStuff);
    }

    /**
     * This will start the timer again if the timer was paused by the menu
     */
    public void resumeTimer() {
        if (timerStartedOnce) {
            moveSnake();
            playtimeManager.startTimer();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Method which allows other classes to add tails to the tails list
     */
    public void addTail(final SnakeTail pTail) {
        tails.add(pTail);
    }

    public List<SnakeTail> getTails() {
        return tails;
    }
}
