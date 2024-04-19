import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SettingsFrame extends JFrame implements ActionListener {

    /**The displayed frame*/
    private final JFrame frame;

    private final Settings settings;

    public SettingsFrame(Settings settings) {

        this.settings = settings;

        frame = new JFrame("Settings");
        prepareLaunchFrame();

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JButton difficulty = new JButton("Difficulty");
        difficulty.setFocusable(false);
        difficulty.setBounds(125, 230, 250, 60);
        difficulty.addActionListener(this);
        panel.add(difficulty);

        JButton gamerules = new JButton("Game-rules");
        gamerules.setFocusable(false);
        gamerules.setBounds(125, 115, 250, 60);
        gamerules.addActionListener(this);
        panel.add(gamerules);

        JButton cosmetic = new JButton("Cosmetics");
        cosmetic.setFocusable(false);
        cosmetic.setBounds(125, 345, 250, 60);
        cosmetic.addActionListener(this);
        panel.add(cosmetic);

        JButton back = new JButton("Back");
        back.setFocusable(false);
        back.setBounds(125, 460, 250, 60);
        back.addActionListener(this);
        panel.add(back);

        frame.add(panel);

    }

    /**Prepares the frame(Sets size, image, etc)*/
    private void prepareLaunchFrame() {

        frame.setSize(500, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            ImageIcon image = new ImageIcon(Objects.requireNonNull(getClass().getResource("Snake.png")));
            frame.setIconImage(image.getImage());
        } catch (NullPointerException e) {
            System.out.println("Image not found");
        }
        frame.setAlwaysOnTop(true);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

    }

    /**Listener for the different buttons*/
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() instanceof JButton button) {

            if (button.getText().equalsIgnoreCase("Difficulty")) {
                frame.dispose();
                setDifficulty();
            } else if (button.getText().equalsIgnoreCase("Cosmetics")) {
                frame.dispose();
                new Cosmetics(settings);
            } else if (button.getText().equalsIgnoreCase("Back")) {
                frame.dispose();
                new LaunchFrame(new SettingsManager().load());
            } else if (button.getText().equalsIgnoreCase("Game-rules")) {
                frame.dispose();
                new GameRuleFrame();
            }
        }

    }

    /**Gets an input difficulty as String*/
    private void setDifficulty() {

        String choice = JOptionPane.showInputDialog("Select Difficulty: \n <"+ Modes.NOOB + ", " + Modes.BEGINNER + ", " + Modes.ADULT + ", " + Modes.MASTER + ", " + Modes.GOD +"> \n current mode: " + settings.getMode());
        String mode;

        //TODO spätere Optimierung
        /*String[] choices = Arrays.stream(Modes.values()).map(Enum::toString).toList().toArray(new String[0]);

        List<String> a = new ArrayList<>();
        for (Modes moood : Modes.values()) {
            a.add(moood.toString());
        }
        String[] my_choices = a.toArray(new String[0]);
        JComboBox<String> comboBox = new JComboBox<>(choices);*/

        if (choice != null) {
            if (choice.equalsIgnoreCase("Noob")
                    || choice.equalsIgnoreCase("Beginner")
                    || choice.equalsIgnoreCase("Adult")
                    || choice.equalsIgnoreCase("Master")
                    || choice.equalsIgnoreCase("God")) {
                mode = choice.toUpperCase();
                settings.setMode(Modes.valueOf(mode));
                System.out.println(settings.getMode());
                new SettingsManager().save(settings);
            } else {
                JOptionPane.showMessageDialog(null, "That is NOT a selectable difficulty!", "Not a difficulty!", JOptionPane.WARNING_MESSAGE);
            }
        }
        new SettingsFrame(settings);
    }
}
