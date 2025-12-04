package top.jimxu.snake;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class SnakePanel extends JPanel implements KeyListener, ActionListener {
	// 存档数据类
	public static class SaveData implements java.io.Serializable {
		private static final long serialVersionUID = 1L;

		int level;
		int score;
		int currentFoodCount;
		int targetFoodCount;
		int countdown;
		int len;
		String direction;
		int[] snakex;
		int[] snakey;
		int foodx;
		int foody;
	}

	// 存档文件路径
	private final String SAVE_FILE_PATH = "snake_save.dat";
	// ��������ͼƬ
	ImageIcon up = new ImageIcon("up.png");
	ImageIcon down = new ImageIcon("down.png");
	ImageIcon left = new ImageIcon("left.png");
	ImageIcon right = new ImageIcon("right.png");
	ImageIcon title = new ImageIcon("title.jpg");
	ImageIcon body = new ImageIcon("body.png");
	ImageIcon food = new ImageIcon("food.png");

	// �ߵ����ݽṹ���
	int[] snakex = new int[750];
	int[] snakey = new int[750];
	int len = 3;
	String direction = "R"; // R��L��U��D��

	// ʳ������
	Random r = new Random();
	int foodx = r.nextInt(34) * 25 + 25; // 34�����ӣ�һ������25�����أ�����25���ؿհ�
	int foody = r.nextInt(24) * 25 + 75; // 24�����ӣ�һ������25�����أ�����75���ؿհ�

	// ��Ϸ�Ƿ�ʼ
	boolean isStarted = false;

	// ��Ϸ�Ƿ�ʧ��
	boolean isFaild = false;

	// �¹���Ϣ
	int level = 1; // ��ǰ�¹�
	int targetFoodCount; // �¹�����ʳ�ﵽ��
	int currentFoodCount = 0; // ��ǰ�¹�ʳ�ﵽ��
	int countdown; // �¹�����ʱ
	int baseTime = 30; // �Ӳ�ʱ��
	int timeIncrementPerLevel = 5; // ÿ��ʱ��ʱ�䵥��
	int timeIncrementPerFood = 2; // ÿʳ��һ���������ʱ�䵥��

	// ������Ϣ
	int score = 0; // �����
	int scorePerFood = 10; // ÿʳ��һ��ĳɼ�

	// �¹���������
	boolean isLevelComplete = false; // �¹��Ƿ�ͨ����
	boolean isShowLevelDialog = false; // �Ƿ��ʾ�¹�����Ի���

	// ��ʼ����
	public void initSnake() {
		isStarted = false;
		isFaild = false;
		len = 3;
		direction = "R";
		snakex[0] = 100;
		snakey[0] = 100;
		snakex[1] = 75;
		snakey[1] = 100;
		snakex[2] = 50;
		snakey[2] = 100;

		// �¹���������
		initLevel();
	}

	// �¹�ʼ��
	public void initLevel() {
		level = 1;
		score = 0;
		currentFoodCount = 0;
		targetFoodCount = 5 + (level - 1) * 3; // 1��5������ÿ��+3
		countdown = baseTime + (level - 1) * timeIncrementPerLevel; // 1��30s������ÿ��+5s
		isLevelComplete = false;
		isShowLevelDialog = false;

		// �������ٶ�
		timer.setDelay(150 - (level - 1) * 15); // ÿ��ʹ��ٶ�ʹ��15ms

		// �������ʱ��������
		countdownTimer.start();
	}

	// �¹���
	public void nextLevel() {
		level++;
		currentFoodCount = 0;
		targetFoodCount = 5 + (level - 1) * 3; // 1��5������ÿ��+3
		countdown = baseTime + (level - 1) * timeIncrementPerLevel; // 1��30s������ÿ��+5s
		isLevelComplete = false;
		isShowLevelDialog = false;

		// �������ٶ�
		timer.setDelay(150 - (level - 1) * 15); // ÿ��ʹ��ٶ�ʹ��15ms

		// �������ʱ��������
		countdownTimer.start();
	}

	public SnakePanel() {
		this.setFocusable(true);
		initSnake(); // ���þ�̬�ߣ�
		this.addKeyListener(this);// ���Ӽ��̼����ӿ�
		timer.start();
		countdownTimer.start();
	}

	// �������ƶ��ٶ�
	Timer timer = new Timer(150, this);
	Timer countdownTimer = new Timer(1000, this);

	public void paint(Graphics g) {
		// ���ñ�����ɫ
		this.setBackground(Color.black);
		g.fillRect(25, 75, 850, 600);
		// ���ñ���
		title.paintIcon(this, g, 25, 11);

		// �¹���Ϣ��Ӵ� - �ұ�
		g.setColor(Color.WHITE);
		g.setFont(new Font("arial", Font.BOLD, 18));
		g.drawString("Level: " + level, 700, 40);
		g.drawString("Food: " + currentFoodCount + "/" + targetFoodCount, 700, 60);
		g.drawString("Time: " + countdown + "s", 700, 80);

		// �������ɼ�
		g.drawString("Score: " + score, 25, 40);

		// ����ͷ
		if (direction.equals("R")) {
			right.paintIcon(this, g, snakex[0], snakey[0]);
		} else if (direction.equals("L")) {
			left.paintIcon(this, g, snakex[0], snakey[0]);
		} else if (direction.equals("U")) {
			up.paintIcon(this, g, snakex[0], snakey[0]);
		} else if (direction.equals("D")) {
			down.paintIcon(this, g, snakex[0], snakey[0]);
		}
		// ������
		for (int i = 1; i < len; i++) {
			body.paintIcon(this, g, snakex[i], snakey[i]);
		}

		// ����ʼ��ʾ��
		if (!isStarted) {
			g.setColor(Color.WHITE);
			g.setFont(new Font("arial", Font.BOLD, 30));
			g.drawString("Press Space to Start or Pause", 230, 350);
		}
		// ��ʧ����ʾ��
		if (isFaild) {
			g.setColor(Color.WHITE);
			g.setFont(new Font("arial", Font.BOLD, 30));
			g.fillRect(300, 250, 350, 200);
			g.setColor(Color.BLACK);
			g.drawString("Game Over!", 380, 320);
			g.drawString("Score: " + score, 390, 360);
			g.drawString("Space to Retry", 360, 400);
		}

		// �¹�ͨ����ʾ��
		if (isShowLevelDialog && isLevelComplete) {
			g.setColor(Color.WHITE);
			g.setFont(new Font("arial", Font.BOLD, 30));
			g.fillRect(300, 250, 350, 200);
			g.setColor(Color.BLACK);
			g.drawString("Level " + level + " Complete!", 320, 320);
			g.drawString("Next Level: " + (level + 1), 330, 360);
			g.drawString("Space to Continue", 320, 400);
		}

		// ��ʳ��
		food.paintIcon(this, g, foodx, foody);
	}

	// �����ʱ�Ӧ�÷���
	public void handleCountdown() {
		if (isStarted && !isFaild && !isLevelComplete) {
			countdown--;
			if (countdown <= 0) {
				isFaild = true;
				countdownTimer.stop();
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	// ��������
	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		// ʵ�ֿո���ͣ ����
		if (keyCode == KeyEvent.VK_SPACE) {
			if (isFaild) {
				// 游戏失败时删除存档
				deleteSaveFile();
				initSnake();
			} else if (isShowLevelDialog && isLevelComplete) {
				// �¹�ͨ�������¹�
				deleteSaveFile();
				nextLevel();
			} else {
				// 暂停/继续游戏
				isStarted = !isStarted;

				if (!isStarted) {
					// 暂停时自动存档
					saveGame();
				}
			}
			// repaint();
		} // ʵ��ת��
		else if (keyCode == KeyEvent.VK_UP && !direction.equals("D")) {
			direction = "U";
		} else if (keyCode == KeyEvent.VK_DOWN && !direction.equals("U")) {
			direction = "D";
		} else if (keyCode == KeyEvent.VK_LEFT && !direction.equals("R")) {
			direction = "L";
		} else if (keyCode == KeyEvent.VK_RIGHT && !direction.equals("L")) {
			direction = "R";
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * 1.��������
	 * 2.���ƶ�
	 * 3.�ػ�һ����
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// �����ʱʱ�Ӧ�÷���
		if (e.getSource() == countdownTimer) {
			handleCountdown();
		}

		timer.start();

		if (isStarted && !isFaild && !isLevelComplete) {
			// �ƶ�����
			for (int i = len; i > 0; i--) {
				snakex[i] = snakex[i - 1];
				snakey[i] = snakey[i - 1];
			}
			// ͷ�ƶ�
			if (direction.equals("R")) {
				// ������+25
				snakex[0] = snakex[0] + 25;
				if (snakex[0] > 850)
					snakex[0] = 25;

			} else if (direction.equals("L")) {
				// ������-25
				snakex[0] = snakex[0] - 25;
				if (snakex[0] < 25)
					snakex[0] = 850;
			} else if (direction.equals("U")) {
				// ������-25
				snakey[0] = snakey[0] - 25;
				if (snakey[0] < 75)
					snakey[0] = 650;
			} else if (direction.equals("D")) {
				// ������+25
				snakey[0] = snakey[0] + 25;
				if (snakey[0] > 650)
					snakey[0] = 75;
			}
			// ��ʳ��
			if (snakex[0] == foodx && snakey[0] == foody) {
				len++;
				currentFoodCount++;
				score += scorePerFood;
				countdown += timeIncrementPerFood; // ÿʳ��һ�������ʱ2s

				foodx = r.nextInt(34) * 25 + 25;
				foody = r.nextInt(24) * 25 + 75;

				// �ж��¹��Ƿ�ͨ��
				if (currentFoodCount >= targetFoodCount) {
					isLevelComplete = true;
					isShowLevelDialog = true;
					countdownTimer.stop();
				}
			}
			// �ж���Ϸʧ��
			for (int i = 1; i < len; i++) {
				if (snakex[0] == snakex[i] && snakey[0] == snakey[i]) {
					isFaild = true;
					countdownTimer.stop();
				}
			}
		}
		repaint();
	}
}
