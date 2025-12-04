package top.jimxu.snake;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

	// 关卡信息
	int level = 1; // 当前关卡
	int targetFood = 5; // 当前关卡需要吃的食物数量
	int eatenFood = 0; // 当前关卡已吃食物数量
	int countdown = 30; // 关卡倒计时（秒）
	int score = 0; // 总得分
	boolean isLevelComplete = false; // 关卡是否完成

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
		// 重置关卡信息
		level = 1;
		targetFood = 5;
		eatenFood = 0;
		countdown = 30;
		score = 0;
		isLevelComplete = false;
		timer.setDelay(150); // 重置速度
	}

	public SnakePanel() {
		this.setFocusable(true);
		initSnake(); // ���þ�̬�ߣ�
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

		// 绘制关卡信息面板
		g.setColor(Color.GRAY);
		g.fillRect(650, 15, 225, 55);
		g.setColor(Color.WHITE);
		g.setFont(new Font("arial", Font.BOLD, 14));
		g.drawString("Level: " + level, 660, 35);
		g.drawString("Food: " + eatenFood + "/" + targetFood, 660, 52);
		g.drawString("Time: " + countdown + "s", 660, 69);
		g.drawString("Score: " + score, 780, 35);

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
			g.drawString("Game Over! Score: " + score, 280, 330);
			g.setFont(new Font("arial", Font.BOLD, 20));
			g.drawString("Press Space to Restart", 320, 360);
		}
		// 关卡完成弹窗
		if (isLevelComplete) {
			g.setColor(new Color(0, 0, 0, 180));
			g.fillRect(0, 0, 900, 720);
			g.setColor(Color.WHITE);
			g.setFont(new Font("arial", Font.BOLD, 30));
			g.drawString("Level " + level + " Complete!", 320, 300);
			g.drawString("Advance to Level " + (level + 1) + "!", 300, 330);
			g.setFont(new Font("arial", Font.BOLD, 20));
			g.drawString("Press Space to Continue", 320, 370);
		}

		// ��ʳ��
		food.paintIcon(this, g, foodx, foody);

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
				initSnake();
			} else if (isLevelComplete) {
				// 进入下一关
				level++;
				targetFood += 3;
				eatenFood = 0;
				countdown = 30 + (level - 1) * 5; // 每升一级增加5秒
				isLevelComplete = false;
				// 提升速度（减少定时器延迟）
				if (timer.getDelay() > 50) {
					timer.setDelay(timer.getDelay() - 10);
				}
			} else {
				isStarted = !isStarted;
				// 暂停时自动存档
				if (!isStarted) {
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

	// 检查存档文件是否存在
	private boolean checkSaveFileExists() {
		java.io.File saveFile = new java.io.File("snake_save.txt");
		return saveFile.exists();
	}

	// 显示加载存档对话框
	private boolean showLoadDialog() {
		int option = JOptionPane.showConfirmDialog(
				this,
				"是否恢复上次存档？",
				"恢复存档",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		return option == JOptionPane.YES_OPTION;
	}

	// 保存游戏状态到本地文件
	private void saveGame() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("snake_save.txt"))) {
			// 保存关卡信息
			writer.write(level + "\n");
			writer.write(targetFood + "\n");
			writer.write(eatenFood + "\n");
			writer.write(countdown + "\n");
			writer.write(score + "\n");
			writer.write(isLevelComplete + "\n");

			// 保存蛇的状态
			writer.write(len + "\n");
			writer.write(direction + "\n");
			for (int i = 0; i < len; i++) {
				writer.write(snakex[i] + "," + snakey[i] + "\n");
			}

			// 保存食物位置
			writer.write(foodx + "\n");
			writer.write(foody + "\n");

			// 保存定时器延迟（速度）
			writer.write(timer.getDelay() + "\n");

			System.out.println("游戏已保存");
		} catch (IOException e) {
			System.err.println("保存游戏失败: " + e.getMessage());
		}
	}

	// 从本地文件加载游戏状态
	private void loadGame() {
		try (BufferedReader reader = new BufferedReader(new FileReader("snake_save.txt"))) {
			// 加载关卡信息
			level = Integer.parseInt(reader.readLine());
			targetFood = Integer.parseInt(reader.readLine());
			eatenFood = Integer.parseInt(reader.readLine());
			countdown = Integer.parseInt(reader.readLine());
			score = Integer.parseInt(reader.readLine());
			isLevelComplete = Boolean.parseBoolean(reader.readLine());

			// 加载蛇的状态
			len = Integer.parseInt(reader.readLine());
			direction = reader.readLine();
			for (int i = 0; i < len; i++) {
				String[] coordinates = reader.readLine().split(",");
				snakex[i] = Integer.parseInt(coordinates[0]);
				snakey[i] = Integer.parseInt(coordinates[1]);
			}

			// 加载食物位置
			foodx = Integer.parseInt(reader.readLine());
			foody = Integer.parseInt(reader.readLine());

			// 加载定时器延迟（速度）
			int delay = Integer.parseInt(reader.readLine());
			timer.setDelay(delay);

			// 设置游戏状态为暂停
			isStarted = false;
			isFaild = false;

			System.out.println("游戏已加载");
		} catch (IOException | NumberFormatException e) {
			System.err.println("加载游戏失败: " + e.getMessage());
			// 加载失败时初始化新游戏
			initSnake();
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
		// TODO Auto-generated method stub
		timer.start();

		if (isStarted && !isFaild && !isLevelComplete) {
			// 倒计时
			countdown--;
			if (countdown <= 0) {
				isFaild = true;
			}

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
				eatenFood++;
				score += 10;
				countdown += 2; // 每吃一个食物增加2秒
				foodx = r.nextInt(34) * 25 + 25;
				foody = r.nextInt(24) * 25 + 75;

				// 检查是否完成关卡
				if (eatenFood >= targetFood) {
					isLevelComplete = true;
				}
			}
			// �ж���Ϸʧ��
			for (int i = 1; i < len; i++) {
				if (snakex[0] == snakex[i] && snakey[0] == snakey[i]) {
					isFaild = true;
				}
			}
		}
		repaint();
	}
}
