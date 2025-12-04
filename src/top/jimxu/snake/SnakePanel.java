package top.jimxu.snake;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class SnakePanel extends JPanel implements KeyListener, ActionListener {
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
	String direction = "R";// R��L��U��D��

	// ʳ������
	Random r = new Random();
	int foodx = r.nextInt(34) * 25 + 25; // 34�����ӣ�һ������25�����أ�����25���ؿհ�
	int foody = r.nextInt(24) * 25 + 75; // 24�����ӣ�һ������25�����أ�����75���ؿհ�

	// ��Ϸ�Ƿ�ʼ
	boolean isStarted = false;

	// ��Ϸ�Ƿ�ʧ��
	boolean isFaild = false;

	// �����ź���
	int level = 1;
	int targetFood = 5;
	int remainingFood = 5;
	int score = 0;
	int countdown = 30;
	boolean isPaused = false;

	// �����ļ�·��
	private static final String SAVE_FILE = "snake_save.dat";

	// ��ʼ����
	public void initSnake() {
		isStarted = false;
		isFaild = false;
		isPaused = false;
		len = 3;
		direction = "R";
		snakex[0] = 100;
		snakey[0] = 100;
		snakex[1] = 75;
		snakey[1] = 100;
		snakex[2] = 50;
		snakey[2] = 100;
		level = 1;
		targetFood = 5;
		remainingFood = 5;
		score = 0;
		countdown = 30;
		// ��������Ϊ150ms
		timer.setDelay(150);
	}

	public SnakePanel() {
		this.setFocusable(true);
		// �ж��Ƿ�洢�ļ�
		if (hasSaveFile()) {
			// �������Ƿ�ָ�����
			int choice = JOptionPane.showConfirmDialog(this, "是否恢复上次存档？", "存档恢复", JOptionPane.YES_NO_OPTION);
			if (choice == JOptionPane.YES_OPTION) {
				loadGame();
			} else {
				initSnake();
			}
		} else {
			initSnake();
		}
		this.addKeyListener(this);// ���Ӽ��̼����ӿ�
		timer.start();
	}

	// �������ƶ��ٶ�
	Timer timer = new Timer(150, this);

	public void paint(Graphics g) {
		// ���ñ�����ɫ
		this.setBackground(Color.black);
		g.fillRect(25, 75, 850, 600);
		// ���ñ���
		title.paintIcon(this, g, 25, 11);

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
			g.drawString("Game Over,Press Space to Start", 230, 350);
		}

		// ��ʳ��
		food.paintIcon(this, g, foodx, foody);
		// �����ź���
		g.setColor(Color.WHITE);
		g.setFont(new Font("arial", Font.BOLD, 18));
		g.drawString("关卡: " + level, 700, 50);
		g.drawString("剩余食物: " + remainingFood + "/" + targetFood, 700, 75);
		g.drawString("倒计时: " + countdown + "s", 700, 100);
		g.drawString("得分: " + score, 700, 125);

		// ����������
		if (isPaused) {
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(25, 75, 850, 600);
			g.setColor(Color.WHITE);
			g.setFont(new Font("arial", Font.BOLD, 30));
			if (remainingFood == 0) {
				g.drawString("进阶至第" + (level + 1) + "关!", 300, 350);
				g.drawString("按Enter键进入下一关", 250, 400);
			} else {
				g.drawString("游戏失败!", 350, 350);
				g.drawString("当前得分: " + score, 320, 400);
				g.drawString("按Space键重新挑战", 250, 450);
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
			if (isFaild || isPaused) {
				initSnake();
			} else {
				isStarted = !isStarted;
				if (!isStarted) {
					// �����Ϸ״̬
					saveGame();
				}
			}
		}
		// ʵ������¼������һ��
		else if (keyCode == KeyEvent.VK_ENTER && isPaused && !isFaild) {
			// �������
			level++;
			targetFood += 3;
			remainingFood = targetFood;
			countdown = 30 + (level - 1) * 5; // ÿ�����ӱ���5��
			// �������ٶ�
			int currentDelay = timer.getDelay();
			if (currentDelay > 50) {
				timer.setDelay(currentDelay - 10); // ÿ�����ٶ�10ms
			}
			isPaused = false;
			isStarted = true;
		}
		// ʵ��ת��
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

	// �ж��Ƿ�洢�ļ�
	private boolean hasSaveFile() {
		File saveFile = new File(SAVE_FILE);
		return saveFile.exists() && saveFile.isFile();
	}

	// �����Ϸ״̬
	private void saveGame() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
			// �����Ϸ״̬����
			oos.writeInt(level);
			oos.writeInt(targetFood);
			oos.writeInt(remainingFood);
			oos.writeInt(score);
			oos.writeInt(countdown);
			oos.writeObject(direction);
			oos.writeInt(len);

			// �����ߵ�����
			for (int i = 0; i < len; i++) {
				oos.writeInt(snakex[i]);
				oos.writeInt(snakey[i]);
			}

			// �����ʳ�����
			oos.writeInt(foodx);
			oos.writeInt(foody);

			// �������ٶ�
			oos.writeInt(timer.getDelay());

			JOptionPane.showMessageDialog(this, "游戏已保存！", "存档成功", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "保存游戏失败：" + e.getMessage(), "存档失败", JOptionPane.ERROR_MESSAGE);
		}
	}

	// �ָ����Ϸ״̬
	private void loadGame() {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
			// �ָ����Ϸ״̬����
			level = ois.readInt();
			targetFood = ois.readInt();
			remainingFood = ois.readInt();
			score = ois.readInt();
			countdown = ois.readInt();
			direction = (String) ois.readObject();
			len = ois.readInt();

			// �ָ����ߵ�����
			for (int i = 0; i < len; i++) {
				snakex[i] = ois.readInt();
				snakey[i] = ois.readInt();
			}

			// �ָ����ʳ�����
			foodx = ois.readInt();
			foody = ois.readInt();

			// �ָ����ٶ�
			int delay = ois.readInt();
			timer.setDelay(delay);

			// ������������״̬
			isStarted = true;
			isPaused = false;
			isFaild = false;

			JOptionPane.showMessageDialog(this, "游戏已恢复！", "读档成功", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException | ClassNotFoundException e) {
			JOptionPane.showMessageDialog(this, "恢复游戏失败：" + e.getMessage(), "读档失败", JOptionPane.ERROR_MESSAGE);
			initSnake();
		}
	}

	/*
	 * 1.��������
	 * 2.���ƶ�
	 * 3.�ػ�һ����
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		timer.start();

		if (isStarted && !isFaild && !isPaused) {
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
				score += 10;
				remainingFood--;
				countdown += 2; // ÿ�˶�һ����ʳ�������2��
				foodx = r.nextInt(34) * 25 + 25;
				foody = r.nextInt(24) * 25 + 75;

				// �ж��Ƿ�ͨ��
				if (remainingFood == 0) {
					isPaused = true;
				}
			}
			// �ж���Ϸʧ��
			for (int i = 1; i < len; i++) {
				if (snakex[0] == snakex[i] && snakey[0] == snakey[i]) {
					isFaild = true;
					isPaused = true;
				}
			}

			// ����ʱ����
			countdown--;
			if (countdown <= 0) {
				isFaild = true;
				isPaused = true;
			}
		}
		repaint();
	}
}
