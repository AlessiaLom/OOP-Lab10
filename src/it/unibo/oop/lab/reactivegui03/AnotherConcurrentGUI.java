package it.unibo.oop.lab.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class AnotherConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private static final int TIME = 10000;
    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");

    private final Agent agent = new Agent();

    /**
     * Builds a new CGUI.
     */
    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(stop);
        panel.add(up);
        panel.add(down);
        this.getContentPane().add(panel);
        this.setVisible(true);

        new Thread(agent).start();
        new Thread(() -> {
            try {
                Thread.sleep(TIME);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            this.stopCounting();
        }).start();

        stop.addActionListener(e -> {
                agent.stopCounting();
                stop.setEnabled(false);
                up.setEnabled(false);
                down.setEnabled(false);
            });

        up.addActionListener(e -> agent.upCounting());

        down.addActionListener(e -> agent.downCounting());

    }
        private void stopCounting() {
            stop.setEnabled(false);
            up.setEnabled(false);
            down.setEnabled(false);
            agent.stopCounting();
        }

    private class Agent implements Runnable {

        private volatile boolean stop;
        private int counter;
        private volatile boolean up = true;

        @Override
        public void run() {
            while (!this.stop) {
                try {

                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(Integer.toString(Agent.this.counter)));
                    counter += (up ? 1 : -1);

                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    /*
                     * This is just a stack trace print, in a real program there
                     * should be some logging and decent error reporting
                     */
                    ex.printStackTrace();
                }
            }
        }

        /**
         * External command to stop counting.
         */
        public void stopCounting() {
            this.stop = true;
        }

        public void upCounting() {
            this.up = true;
        }

        public void downCounting() {
            this.up = false;
        }
    }
}
