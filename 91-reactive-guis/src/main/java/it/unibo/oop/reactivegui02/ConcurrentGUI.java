package it.unibo.oop.reactivegui02;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    /**
     * Constructor and initializer.
     */
    public ConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        final JPanel panel = new JPanel(new FlowLayout());
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);

        final Agent a = new Agent();

        up.addActionListener(e -> a.setDirection(true));

        down.addActionListener(e -> a.setDirection(false));
        stop.addActionListener(e -> {
            a.setRunning(false);
            up.setEnabled(false);
            down.setEnabled(false);
            stop.setEnabled(false);
        });
        new Thread(a).start();
    }

    private class Agent implements Runnable {
        private boolean dir = true;
        private boolean running = true;
        private double counter;

        @Override
        public void run() {
            while (running) {
                try {
                    if (dir) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    final var nextText = Integer.toString((int) this.counter);
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextText));
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace(); //NOPMD
                }
            }
        }

        public void setRunning(final boolean s) {
            this.running = s;
        }

        public void setDirection(final boolean s) {
            this.dir = s;
        }
    }

}
