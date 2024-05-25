import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {
    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = 600;
    private static final int UNIT_SIZE = 25;
    private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private static final int DELAY = 100;
    private final int[] x = new int[GAME_UNITS];
    private final int[] y = new int[GAME_UNITS];
    private int bodyParts = 3;
    private int applesEaten;
    private int appleX;
    private int appleY;
    private char direction = 'R';
    private boolean running = false;
    private final Random random = new Random();
    private int best = 0; // Track the highest score
    private GameStatus status = GameStatus.NOT_STARTED;

    public SnakeGame() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());
        startGame();
    }

    private void startGame() {
        newApple();
        running = true;
        Timer timer = new Timer(DELAY, this);
        timer.start();
        status = GameStatus.RUNNING;
    }

    private void restartGame() {
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        newApple();
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        running = true;
        status = GameStatus.RUNNING;
    }

    private void gameOver() {
        running = false;
        if (applesEaten > best) {
            best = applesEaten;
        }
        status = GameStatus.GAME_OVER;
    }

    private void newApple() {
        appleX = random.nextInt((SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    private void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U':
                y[0] -= UNIT_SIZE;
                break;
            case 'D':
                y[0] += UNIT_SIZE;
                break;
            case 'L':
                x[0] -= UNIT_SIZE;
                break;
            case 'R':
                x[0] += UNIT_SIZE;
                break;
        }
    }

    private void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    private void checkCollision() {
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                gameOver();
                return;
            }
        }
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            gameOver();
        }
    }

    private void drawCenteredString(Graphics g, String text, int fontSize, int yPosition) {
        g.setColor(Color.white);
        g.setFont(new Font("Ink Free", Font.BOLD, fontSize));
        FontMetrics metrics = g.getFontMetrics();
        int x = (SCREEN_WIDTH - metrics.stringWidth(text)) / 2;
        g.drawString(text, x, yPosition);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollision();
        }
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if (status == GameStatus.RUNNING) {
            // Draw the apple
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Draw the snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    // Setting a random color for the snake's body parts
                    g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            g.setColor(Color.orange);
            g.setFont(new Font("Ink Free", Font.BOLD, 20));
            g.drawString("Score: " + applesEaten*10, 10, 20);
        } else if (status == GameStatus.GAME_OVER) {
            // Draw game over screen
            drawCenteredString(g, "Game Over", 75, SCREEN_HEIGHT / 2);
            drawCenteredString(g, "Score: " + 10*applesEaten, 40, SCREEN_HEIGHT / 2 + 50);
            drawCenteredString(g, "High Score: " + 10*best, 40, SCREEN_HEIGHT / 2 + 100);
            drawCenteredString(g, "Press Enter to Play Again", 20, SCREEN_HEIGHT / 2 + 150);
        } else if (status == GameStatus.PAUSED) {
            // Draw paused screen
            drawCenteredString(g, "Paused", 20, SCREEN_HEIGHT / 2);
        } else if (status == GameStatus.NOT_STARTED) {
            // Draw start screen
            drawCenteredString(g, "Snake Game", 75, SCREEN_HEIGHT / 2 - 50);
            drawCenteredString(g, "Press Enter to Start", 30, SCREEN_HEIGHT / 2 + 50);
            drawCenteredString(g, "Highest Score: " + best, 30, SCREEN_HEIGHT / 2 + 100);

        }
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_ENTER:
                    if (status == GameStatus.GAME_OVER) {
                        restartGame();
                    } else if (status == GameStatus.NOT_STARTED) {
                        startGame();
                    }
                    break;
                case KeyEvent.VK_SPACE:
                    if (status == GameStatus.RUNNING) {
                        status = GameStatus.PAUSED;
                    } else if (status == GameStatus.PAUSED) {
                        status = GameStatus.RUNNING;
                    }
                    break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake Game");
            SnakeGame game = new SnakeGame();
            frame.add(game);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.pack();
            frame.setVisible(true);
            frame.setLocationRelativeTo(null);
        });
    }

    enum GameStatus {
        NOT_STARTED, RUNNING, PAUSED, GAME_OVER
    }
}
