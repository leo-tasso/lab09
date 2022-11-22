package it.unibo.oop.reactivegui03;

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
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {

    private static final int WAIT = 10_000;
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final transient Agent a;

    /**
     * Constructor and initializer.
     */
    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel(new FlowLayout());
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);

        a = new Agent();

        up.addActionListener(e -> a.setDirection(true));

        down.addActionListener(e -> a.setDirection(false));
        stop.addActionListener(e -> {
            stop();
        });
        new Thread(a).start();

        //final StopperAgent s = new StopperAgent(this);
        new Thread(() -> {
            try {
                Thread.sleep(WAIT);
                this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(); // NOPMD
            }
        }).start();
    }

    /**
     * stops the counter.
     */
    public synchronized void stop() {
        a.setRunning(false);
        up.setEnabled(false);
        down.setEnabled(false);
        stop.setEnabled(false);
    }
/* REFACTORED WITH LAMBDA
    private static class StopperAgent implements Runnable {

        private static final int WAIT = 10_000;
        private final AnotherConcurrentGUI a;

        StopperAgent(final AnotherConcurrentGUI a) {
            this.a = a;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(WAIT);
                a.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(); // NOPMD
            }
        }

    }
*/
    private class Agent implements Runnable {
        private volatile boolean dir = true;
        private volatile boolean running = true;
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
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace(); // NOPMD
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
