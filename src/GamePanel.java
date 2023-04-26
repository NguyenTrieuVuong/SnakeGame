import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
public class GamePanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 884731180169898718L;
	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 25;
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE^2;
	static int DELAY = 100;
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	int bodyPart = 2;
	int applesEaten;
	int appleX;
	int appleY;
	char direction = 'R';
	boolean running = false;
	boolean chooseDifficulty = true;
	boolean pauseGame = false;
	Timer timer;
	Random random;

	public GamePanel() {
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.blue);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
//		startGame();
	}

	public void startGame() {
		newApple();
		running = true;
		timer = new Timer(DELAY, this);
		timer.start();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}

	public void draw(Graphics g) {
		if (chooseDifficulty) {
			chooseDifficulty(g);
		} else if (running) {
			for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
				g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
				g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
			}
			g.setColor(Color.red);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
			for (int i = 0; i < bodyPart; i++) {
				if (i == 0) {
					g.setColor(Color.green);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				} else {
					g.setColor(new Color(45, 180, 0));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			g.setColor(Color.white);
			g.setFont(new Font("Arial", Font.BOLD, 40));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2,
					g.getFont().getSize());
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.setColor(Color.white.darker());
			g.drawString("Press Space to pause the game !",
					(int) ((SCREEN_WIDTH - metrics.stringWidth("")) / 4),
					(int) (SCREEN_HEIGHT / 1.07));
			g.drawString("Press Enter to continue !",
					(int) ((SCREEN_WIDTH - metrics.stringWidth("")) / 3.2),
					(int) (SCREEN_HEIGHT / 1.02));
		} else if (pauseGame) {
			pauseGame(g);
		} else {
			gameOver(g);
		}
	}

	public void pauseGame(Graphics g) {
		if (pauseGame) {
			for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
				g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
				g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
			}
			g.setColor(Color.red);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
			for (int i = 0; i < bodyPart; i++) {
				if (i == 0) {
					g.setColor(Color.green);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				} else {
					g.setColor(new Color(45, 180, 0));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			g.setColor(Color.white);
			g.setFont(new Font("Arial", Font.BOLD, 40));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Press Enter to continue !",
					(SCREEN_WIDTH - metrics.stringWidth("Press Enter to continue !")) / 2, g.getFont().getSize());
		}
	}

	public void newApple() {
		for (int i = 0; i < bodyPart; i++) {
			if (x[i] == appleX && y[i] == appleY) {
				appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
				appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
			}
		}
	}

	public void move() {
		for (int i = bodyPart; i > 0; i--) {
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}
		switch (direction) {
		case 'U': {
			y[0] = y[0] - UNIT_SIZE;
			break;
		}
		case 'D': {
			y[0] = y[0] + UNIT_SIZE;
			break;
		}
		case 'L': {
			x[0] = x[0] - UNIT_SIZE;
			break;
		}
		case 'R': {
			x[0] = x[0] + UNIT_SIZE;
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + direction);
		}
		for (int i = 0; i < bodyPart; i++) {
			if (x[i] < 0) {
				x[i] = SCREEN_WIDTH; // Warp to right
			} else if (x[i] == SCREEN_WIDTH) {
				x[i] = 0; // Warp to left
			}

			if (y[i] < 0) {
				y[i] = SCREEN_HEIGHT; // Warp to bottom
			} else if (y[i] == SCREEN_HEIGHT) {
				y[i] = 0; // Warp to top
			}
		}
	}

	public void checkApple() {
		if ((x[0] == appleX) && (y[0] == appleY)) {
			bodyPart++;
			applesEaten++;
			newApple();
		}
	}

	public void checkCollisions() {
		// check if head collides with body
		for (int i = bodyPart; i > 0; i--) {
			if ((x[0] == x[i]) && (y[0] == y[i])) {
				running = false;
			}
		}
		// check if head touches left border
		if (x[0] < 0) {
			running = false;
		}
		// check if head touches right border
		if (x[0] > SCREEN_WIDTH) {
			running = false;
		}
		// check if head touches top border
		if (y[0] > SCREEN_HEIGHT) {
			running = false;
		}
		// check if head touches bottom border
		if (y[0] < 0) {
			running = false;
		}
		if (!running) {
			timer.stop();
		}
	}

	public void gameOver(Graphics g) {
		// Game Over text
		setBackground(Color.black);
		g.setColor(Color.red);
		g.setFont(new Font("Arial", Font.BOLD, 75));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, (int) (SCREEN_HEIGHT / 2.8));
		g.setFont(new Font("Arial", Font.BOLD, 35));
		FontMetrics metrics3 = getFontMetrics(g.getFont());
		g.drawString("You have just scored " + applesEaten + " points",
				(int) ((SCREEN_WIDTH - metrics3.stringWidth("You have score: " + applesEaten + " points")) / 3.6),
				(int) (SCREEN_HEIGHT / 2));
		g.drawString("Press Space to play again !",
				(int) ((SCREEN_WIDTH - metrics3.stringWidth("Press Space to play again !")) / 2),
				(int) (SCREEN_HEIGHT / 1.5));
	}

	public void chooseDifficulty(Graphics g) {
		setBackground(Color.black);
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.BOLD, 30));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("Welcome to Snake Game", (SCREEN_WIDTH - metrics.stringWidth("Welcome to Snake Game")) / 2,
				SCREEN_HEIGHT / 8);
		g.setFont(new Font("Serif", Font.BOLD, 25));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Press one of the keys below to set difficulty of the game",
				(SCREEN_WIDTH - metrics2.stringWidth("Press one of the keys below to set difficulty of the game")) / 2,
				(int) (SCREEN_HEIGHT / 3.3));
		g.setFont(new Font("Times New Roman", Font.BOLD, 30));
		g.drawString("E - Easy", (int) ((SCREEN_WIDTH - metrics2.stringWidth("E - Easy")) / 2.2),
				(int) (SCREEN_HEIGHT / 2.5));
		g.drawString("M - Medium", (int) ((SCREEN_WIDTH - metrics2.stringWidth("M - Medium")) / 2),
				(int) (SCREEN_HEIGHT / 2.1));
		g.drawString("H - Hard", (int) ((SCREEN_WIDTH - metrics2.stringWidth("H - Hard")) / 2.16),
				(int) (SCREEN_HEIGHT / 1.8));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (running) {
			move();
			checkApple();
			checkCollisions();
		}
		repaint();
	}

	public class MyKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			if (chooseDifficulty) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_E:
					DELAY = 135;
					break;
				case KeyEvent.VK_M:
					DELAY = 100;
					break;
				case KeyEvent.VK_H:
					DELAY = 75;
					break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + e.getKeyCode());
				}
				chooseDifficulty = false;
				startGame();
			} else if (running) {
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

				case KeyEvent.VK_SPACE:
					timer.stop();
					pauseGame = true;
					break;
				case KeyEvent.VK_ENTER:
					timer.start();
					pauseGame = false;
					break;
				}

			} else if (!running && !chooseDifficulty) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_SPACE:
//					chooseDifficulty = true;
					running = true;
					applesEaten = 0;
					bodyPart = 2;
					for (int i = bodyPart; i > 0; i--) {
						x[0] = 0;
						y[0] = SCREEN_HEIGHT;
						direction = 'R';
					}
					timer.restart();
					break;
				default:
//					throw new IllegalArgumentException("Unexpected value: " + e.getKeyCode());
				}
//				startGame();

			}
		}
	}

}
