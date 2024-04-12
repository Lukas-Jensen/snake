import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LaunchFrame implements ActionListener {

    private final JButton button;
    private final JFrame frame;
    private JLabel letter;
    private ImageIcon image;
    private JPanel panel;
    private SnakeHead head;
    private List<Boost> boosts = new ArrayList<>();
    private List<SnakeTail> tails = new ArrayList<>();
    public LaunchFrame(){

        frame = new JFrame("Launch game");
        letter = new JLabel();
        letter.setBounds(145, 100, 250, 60);
        letter.setVisible(true);
        letter.setFont(new Font("Arial", Font.BOLD, 60));

        try {
            image = new ImageIcon(getClass().getResource("Snake.png"));
            frame.setIconImage(image.getImage());

        }catch (NullPointerException e){
            System.out.println("Image not found");
        }

        panel = new JPanel(null);
        button = new JButton("Play game!");
        button.setBounds(125, 500,250, 60);
        button.setFocusable(false);
        button.addActionListener(this);
        panel.add(button);
        panel.add(letter);

        head = new SnakeHead(-10, 300, 10, 10);
        frame.add(head);


        frame.setSize(500, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setResizable(false);
        frame.add(panel);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        playIntroAnimation();

    }

/** Starts to play the animation for the launcher intro*/
    private void playIntroAnimation() {
        drawText();
        drawApples();
        snakeCrawl();
    }


    private void snakeCrawl() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            int counter = 0;
            @Override
            public void run() {

                if (counter < 50) {

                    testEaten();

                    SnakeTail tailpiece = new SnakeTail(head.getX(), head.getY(), 10, 10);
                    panel.add(tailpiece);
                    runRight();
                    tails.add(tailpiece);
                    counter++;

                } else {
                    deleteSnake();
                    cancel();
                }
            }
        }, 10000, 200);
    }

    private void deleteSnake() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            int i = 0;
            @Override
            public void run() {
                if (i < tails.size()) {
                    panel.remove(tails.get(i));
                    panel.revalidate();
                    panel.repaint();
                    i++;
                }else {
                    tails.clear();
                    cancel();
                }
            }
        }, 0, 200);
    }


    private void testEaten() {
        for (Component component : panel.getComponents()) {

            if (component instanceof Boost){

                JLabel label = (Boost) component;

                if (component.getX() == head.getX()){
                    panel.remove(label);
                    panel.revalidate();
                    panel.repaint();
                    break;
                }
            }
        }
    }


    private void drawApples() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int x = 40;
            int boostcount = 0;
            @Override
            public void run() {
                if (boostcount < 11) {
                    Boost boost = new Boost(x, 300, 10, 10, null);
                    panel.add(boost);
                    boosts.add(boost);
                    x += 40;
                    boostcount++;
                    panel.revalidate();
                    panel.repaint();
                }else {
                    cancel();
                }
            }
        }, 4000, 400);

    }


    private void drawText() {
        String word = "SNAKE";
        Timer wordTimer = new Timer();
        wordTimer.scheduleAtFixedRate(new TimerTask() {
            int index = 0;
            @Override
            public void run() {
                if (index < 5) {
                    letter.setText(letter.getText() + word.charAt(index));
                    index++;
                }else {
                    cancel();
                }
            }
        }, 1500, 400);
    }


    private void runRight(){
        head.setLocation(head.getX() + 10, head.getY());
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            new GameFrame();
            frame.dispose();
        }
    }
}
