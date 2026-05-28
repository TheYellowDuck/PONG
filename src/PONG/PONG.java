package PONG;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.Timer;

public class PONG extends JPanel {

	static final int WIDTH = 1200, HEIGHT = 600;
	static final int PADDLE_W = 15, PADDLE_H = 100;
	static final int BALL_SIZE = 15;
	static final int PADDLE_MARGIN = 30;
	static final int PADDLE_SPEED = 6;
	static final double BASE_SPEED = 5.0, MAX_SPEED = 14.0;
	static final double AI_SPEED = 5.0; // slightly slower than player

	static double ballX = WIDTH / 2.0, ballY = HEIGHT / 2.0;
	static double ballVX = 0, ballVY = 0;
	static int leftY = HEIGHT / 2 - PADDLE_H / 2;
	static int rightY = HEIGHT / 2 - PADDLE_H / 2;
	static int score1 = 0, score2 = 0;
	static boolean serving = true;
	static boolean showMenu = true;
	static int gameMode = 1;

	static double aiTargetY = HEIGHT / 2.0 - PADDLE_H / 2.0;
	static int aiUpdateTimer = 0;

	public PONG() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.black);

		new Timer(1000 / 60, (ActionEvent e) -> {
			if (!showMenu) game();
			repaint();
		}).start();
	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.addKeyListener(new keyListener());
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setTitle("PONG");
		f.add(new PONG(), BorderLayout.CENTER);
		f.setResizable(false);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}

	static void startGame(int mode) {
		gameMode = mode;
		showMenu = false;
		score1 = 0;
		score2 = 0;
		leftY = HEIGHT / 2 - PADDLE_H / 2;
		rightY = HEIGHT / 2 - PADDLE_H / 2;
		aiTargetY = HEIGHT / 2.0 - PADDLE_H / 2.0;
		aiUpdateTimer = 0;
		resetBall(Math.random() < 0.5);
	}

	static void resetBall(boolean serveLeft) {
		ballX = WIDTH / 2.0;
		ballY = HEIGHT / 2.0;
		double angle = (Math.random() * 60 - 30) * Math.PI / 180;
		double dir = serveLeft ? -1 : 1;
		ballVX = dir * BASE_SPEED * Math.cos(angle);
		ballVY = BASE_SPEED * Math.sin(angle);
		serving = true;
	}

	// Simulate ball path to predict where it will reach the right paddle X
	static double predictBallY() {
		double simX = ballX, simY = ballY, simVX = ballVX, simVY = ballVY;
		int targetX = WIDTH - PADDLE_MARGIN - PADDLE_W;
		for (int i = 0; i < 2000 && simX < targetX; i++) {
			simX += simVX;
			simY += simVY;
			if (simY - BALL_SIZE / 2.0 <= 0) { simY = BALL_SIZE / 2.0; simVY = Math.abs(simVY); }
			else if (simY + BALL_SIZE / 2.0 >= HEIGHT) { simY = HEIGHT - BALL_SIZE / 2.0; simVY = -Math.abs(simVY); }
		}
		return simY;
	}

	static void moveAI() {
		// Recompute target periodically so AI isn't pixel-perfect every frame
		if (aiUpdateTimer <= 0) {
			aiUpdateTimer = 15;
			if (ballVX > 0) {
				double predicted = predictBallY() - PADDLE_H / 2.0;
				aiTargetY = predicted + (Math.random() * 50 - 25); // ±25px imperfection
			} else {
				aiTargetY = HEIGHT / 2.0 - PADDLE_H / 2.0; // drift to center when ball moves away
			}
			aiTargetY = Math.max(0, Math.min(HEIGHT - PADDLE_H, aiTargetY));
		}
		aiUpdateTimer--;

		double diff = aiTargetY - rightY;
		if (Math.abs(diff) <= AI_SPEED) {
			rightY = (int) Math.round(aiTargetY);
		} else {
			rightY += diff > 0 ? (int) AI_SPEED : -(int) AI_SPEED;
		}
	}

	static void game() {
		if (keyListener.wDown && leftY > 0) leftY -= PADDLE_SPEED;
		if (keyListener.sDown && leftY < HEIGHT - PADDLE_H) leftY += PADDLE_SPEED;

		if (gameMode == 2) {
			if (keyListener.upDown && rightY > 0) rightY -= PADDLE_SPEED;
			if (keyListener.downDown && rightY < HEIGHT - PADDLE_H) rightY += PADDLE_SPEED;
		} else {
			moveAI();
		}

		if (serving) return;

		ballX += ballVX;
		ballY += ballVY;

		if (ballY - BALL_SIZE / 2.0 <= 0) { ballY = BALL_SIZE / 2.0; ballVY = Math.abs(ballVY); }
		else if (ballY + BALL_SIZE / 2.0 >= HEIGHT) { ballY = HEIGHT - BALL_SIZE / 2.0; ballVY = -Math.abs(ballVY); }

		int lx = PADDLE_MARGIN;
		if (ballVX < 0
				&& ballX - BALL_SIZE / 2.0 <= lx + PADDLE_W
				&& ballX + BALL_SIZE / 2.0 >= lx
				&& ballY + BALL_SIZE / 2.0 >= leftY
				&& ballY - BALL_SIZE / 2.0 <= leftY + PADDLE_H) {
			double rel = (ballY - (leftY + PADDLE_H / 2.0)) / (PADDLE_H / 2.0);
			double speed = Math.min(Math.hypot(ballVX, ballVY) * 1.05, MAX_SPEED);
			double angle = rel * 60 * Math.PI / 180;
			ballVX = speed * Math.cos(angle);
			ballVY = speed * Math.sin(angle);
			ballX = lx + PADDLE_W + BALL_SIZE / 2.0;
			aiUpdateTimer = 0; // force AI to recompute on next frame
		}

		int rx = WIDTH - PADDLE_MARGIN - PADDLE_W;
		if (ballVX > 0
				&& ballX + BALL_SIZE / 2.0 >= rx
				&& ballX - BALL_SIZE / 2.0 <= rx + PADDLE_W
				&& ballY + BALL_SIZE / 2.0 >= rightY
				&& ballY - BALL_SIZE / 2.0 <= rightY + PADDLE_H) {
			double rel = (ballY - (rightY + PADDLE_H / 2.0)) / (PADDLE_H / 2.0);
			double speed = Math.min(Math.hypot(ballVX, ballVY) * 1.05, MAX_SPEED);
			double angle = rel * 60 * Math.PI / 180;
			ballVX = -speed * Math.cos(angle);
			ballVY = speed * Math.sin(angle);
			ballX = rx - BALL_SIZE / 2.0;
		}

		if (ballX < 0) { score2++; resetBall(false); }
		else if (ballX > WIDTH) { score1++; resetBall(true); }
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (showMenu) { paintMenu(g); return; }

		g.setColor(new Color(255, 255, 255, 70));
		for (int y = 0; y < HEIGHT; y += 30) g.fillRect(WIDTH / 2 - 2, y, 4, 18);

		g.setColor(Color.white);
		g.fillRoundRect(PADDLE_MARGIN, leftY, PADDLE_W, PADDLE_H, 6, 6);
		g.fillRoundRect(WIDTH - PADDLE_MARGIN - PADDLE_W, rightY, PADDLE_W, PADDLE_H, 6, 6);
		g.fillOval((int) (ballX - BALL_SIZE / 2.0), (int) (ballY - BALL_SIZE / 2.0), BALL_SIZE, BALL_SIZE);

		g.setFont(new Font("Arial", Font.BOLD, 64));
		FontMetrics fm = g.getFontMetrics();
		String s1 = String.valueOf(score1), s2 = String.valueOf(score2);
		g.drawString(s1, WIDTH / 4 - fm.stringWidth(s1) / 2, 80);
		g.drawString(s2, WIDTH * 3 / 4 - fm.stringWidth(s2) / 2, 80);

		g.setColor(new Color(100, 100, 100));
		g.setFont(new Font("Arial", Font.PLAIN, 14));
		fm = g.getFontMetrics();
		String modeLabel = gameMode == 1 ? "1P vs BOT" : "2P";
		g.drawString(modeLabel, WIDTH / 2 - fm.stringWidth(modeLabel) / 2, 22);

		if (serving) {
			g.setColor(Color.white);
			g.setFont(new Font("Arial", Font.PLAIN, 20));
			fm = g.getFontMetrics();
			String msg = "Press SPACE to serve";
			g.drawString(msg, WIDTH / 2 - fm.stringWidth(msg) / 2, HEIGHT - 25);
		}

		g.setColor(new Color(90, 90, 90));
		g.setFont(new Font("Arial", Font.PLAIN, 14));
		g.drawString("W / S", PADDLE_MARGIN, HEIGHT - 10);
		if (gameMode == 2) {
			g.drawString("↑ / ↓", WIDTH - PADDLE_MARGIN - 28, HEIGHT - 10);
		} else {
			g.setColor(new Color(70, 140, 70));
			g.drawString("BOT", WIDTH - PADDLE_MARGIN - 18, HEIGHT - 10);
		}
		g.setColor(new Color(90, 90, 90));
		fm = g.getFontMetrics();
		g.drawString("ESC = menu", WIDTH / 2 - fm.stringWidth("ESC = menu") / 2, HEIGHT - 10);
	}

	void paintMenu(Graphics g) {
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.BOLD, 90));
		FontMetrics fm = g.getFontMetrics();
		String title = "PONG";
		g.drawString(title, WIDTH / 2 - fm.stringWidth(title) / 2, HEIGHT / 2 - 70);

		int btnW = 200, btnH = 55, gap = 20;
		int totalW = btnW * 2 + gap;
		int btnX1 = WIDTH / 2 - totalW / 2;
		int btnX2 = btnX1 + btnW + gap;
		int btnY = HEIGHT / 2 + 10;

		g.setColor(new Color(50, 50, 50));
		g.fillRoundRect(btnX1, btnY, btnW, btnH, 12, 12);
		g.fillRoundRect(btnX2, btnY, btnW, btnH, 12, 12);
		g.setColor(new Color(80, 80, 80));
		g.drawRoundRect(btnX1, btnY, btnW, btnH, 12, 12);
		g.drawRoundRect(btnX2, btnY, btnW, btnH, 12, 12);

		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.BOLD, 20));
		fm = g.getFontMetrics();
		String l1 = "1  -  1 Player", l2 = "2  -  2 Player";
		g.drawString(l1, btnX1 + (btnW - fm.stringWidth(l1)) / 2, btnY + 34);
		g.drawString(l2, btnX2 + (btnW - fm.stringWidth(l2)) / 2, btnY + 34);

		g.setColor(new Color(120, 120, 120));
		g.setFont(new Font("Arial", Font.PLAIN, 15));
		fm = g.getFontMetrics();
		g.drawString("ESC to quit", WIDTH / 2 - fm.stringWidth("ESC to quit") / 2, btnY + 90);
	}
}
