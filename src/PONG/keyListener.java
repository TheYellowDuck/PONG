package PONG;

import java.awt.event.*;

public class keyListener implements KeyListener {

	static boolean wDown, sDown, upDown, downDown;

	@Override
	public void keyPressed(KeyEvent k) {
		switch (k.getKeyCode()) {
		case KeyEvent.VK_W:
			wDown = true;
			break;
		case KeyEvent.VK_S:
			sDown = true;
			break;
		case KeyEvent.VK_UP:
			upDown = true;
			break;
		case KeyEvent.VK_DOWN:
			downDown = true;
			break;
		case KeyEvent.VK_SPACE:
			if (!PONG.showMenu) PONG.serving = false;
			break;
		case KeyEvent.VK_1:
			if (PONG.showMenu) PONG.startGame(1);
			break;
		case KeyEvent.VK_2:
			if (PONG.showMenu) PONG.startGame(2);
			break;
		case KeyEvent.VK_ESCAPE:
			if (PONG.showMenu) System.exit(0);
			else PONG.showMenu = true;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent k) {
		switch (k.getKeyCode()) {
		case KeyEvent.VK_W:
			wDown = false;
			break;
		case KeyEvent.VK_S:
			sDown = false;
			break;
		case KeyEvent.VK_UP:
			upDown = false;
			break;
		case KeyEvent.VK_DOWN:
			downDown = false;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}
}
