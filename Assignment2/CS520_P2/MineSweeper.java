/* 
 * File: MineSweeper.java
 * Desc: Graphic Interface for MineSweeper Game
 * Auth: Yujie REN
 * Date: 10/12/2018
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MineSweeper implements ActionListener {

    private JComponent ui = null;
    private MineField mineField;
	private SweepMineAI ai;

    Color[] colors = {
        Color.BLUE,
        Color.CYAN.darker(),
        Color.YELLOW.darker(),
        Color.ORANGE.darker(),
        Color.RED,
        Color.RED.darker(),
        Color.MAGENTA,
        Color.MAGENTA.darker(),
		Color.GREEN.darker()
    };
    public final static String BOMB = new String(Character.toChars(128163));
	public final static String FLAG = new String(Character.toChars(128681));
	public final static String BOOM = new String(Character.toChars(128165));

    JButton[][] buttons;
	JButton clearButton;
	JButton sweepButton;

    int size = 4;
	int mineNumber = 2;

	int counter;

    public MineSweeper(int size, int mineNumber) {
		this.size = size;
		this.mineNumber = mineNumber;
		
		counter = size*size;

        if (ui != null) {
            return;
        }

        ui = new JPanel(new BorderLayout(4, 4));
        ui.setBorder(new EmptyBorder(4, 4, 4, 4));

        mineField = new MineField(size, mineNumber);

		// AI of MineSweeper
		ai = new SweepMineAI(mineField);

		clearButton = new JButton("Clear");
		sweepButton = new JButton("Sweep");

		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				counter = size*size;
				for (int ii = 0; ii < size; ++ii) {
				    for (int jj = 0; jj < size; ++jj) {
						mineField.clearExposed(ii, jj);
						buttons[ii][jj].setText("");
						buttons[ii][jj].setEnabled(true);
				    }
				}
				ai = new SweepMineAI(mineField);
			}
		});

		sweepButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Cell[][] res = ai.sweepMine();
				for (int ii = 0; ii < size; ++ii) {
				    for (int jj = 0; jj < size; ++jj) {
						if (res[ii][jj].state == State.SAFE) {
							// buttons[ii][jj].doClick();
							buttons[ii][jj].setText("" + mineField.countSurroundingMines(ii, jj));
							mineField.setExposed(ii, jj);
						} else if (res[ii][jj].state == State.MINE) {
							if (mineField.isBomb(ii, jj) == true) {
								buttons[ii][jj].setForeground(Color.RED);
								buttons[ii][jj].setText(FLAG);
							} else {
								buttons[ii][jj].setForeground(Color.RED);
								buttons[ii][jj].setText("X");
							}
							mineField.setExposed(ii, jj);
						} else if (res[ii][jj].state == State.TRIGGER) {
							buttons[ii][jj].setForeground(Color.RED);
							buttons[ii][jj].setText(BOOM);
							mineField.setExposed(ii, jj);
							System.out.println("explode " + ii + " " + jj);
							explode(false);
						} else {}
				    }
				}
				
			}
		});

        JPanel mineFieldContainer = new JPanel(new GridLayout(size, size));

        ui.add(mineFieldContainer, BorderLayout.CENTER);
		ui.add(clearButton, BorderLayout.NORTH);
		ui.add(sweepButton, BorderLayout.SOUTH);

        Insets insets = new Insets(5, 5, 5, 5);
        Font f = getCompatibleFonts().firstElement().deriveFont(16f);
        buttons = new JButton[size][size];
        for (int ii = 0; ii < size; ++ii) {
            for (int jj = 0; jj < size; ++jj) {
                buttons[ii][jj] = new JButton();
                buttons[ii][jj].setMargin(insets);
				buttons[ii][jj].setPreferredSize(new Dimension(40, 40));
                buttons[ii][jj].setFont(f);
                buttons[ii][jj].setText("");
				buttons[ii][jj].setName(Integer.toString(ii*size + jj));
                /*if (mineField.isExposed(ii, jj)) {
                    if (mineField.isBomb(ii, jj)) {
                        buttons[ii][jj].setForeground(Color.RED);
                        buttons[ii][jj].setForeground(Color.BLACK);
                        buttons[ii][jj].setText(BOMB);
                    } else {
                        int count = mineField.countSurroundingMines(ii, jj);
				        if (count > 0)
				            buttons[ii][jj].setForeground(colors[count - 1]);
				        else
							buttons[ii][jj].setForeground(colors[8]);
						buttons[ii][jj].setText("" + count);
                    }
                }*/
				buttons[ii][jj].addActionListener(this);
                mineFieldContainer.add(buttons[ii][jj]);
            }
        }
    }

	private void explode(boolean win) {
        for (int ii = 0; ii < size; ++ii) {
            for (int jj = 0; jj < size; ++jj) {
				JButton cur = buttons[ii][jj];
                if (!mineField.isExposed(ii, jj) && mineField.isBomb(ii, jj)) {
					if (win == false) {
						cur.setOpaque(true);
					    cur.setForeground(Color.RED);
					    cur.setText(BOMB);
					} else {
						cur.setOpaque(true);
					    cur.setForeground(Color.BLACK);
					    cur.setText(FLAG);
					}	
				}
				//cur.setEnabled(false);
            }
        }
	}

	private void autoExpand(int x, int y) {
		int lowX  = (x - 1) < 0 ? 0 : (x - 1);
		int highX = (x + 2) > size ? size : (x + 2);
		int lowY  = (y - 1) < 0 ? 0 : (y - 1);
		int highY = (y + 2) > size ? size : (y + 2);

        int count = 0;
        for (int ii = lowX; ii < highX; ii++) {
            for (int jj = lowY; jj < highY; jj++) {
				if (!mineField.isExposed(ii, jj)) {
					mineField.setExposed(ii, jj);
					--counter;
					//System.out.println("" + counter);

					JButton cur = buttons[ii][jj];
					count = mineField.countSurroundingMines(ii, jj);
		            if (count == 0) {
						cur.setEnabled(false);
						autoExpand(ii, jj);
		            } else {
						cur.setForeground(colors[count - 1]);
						cur.setText("" + count);
					}
				}
            }
        }		
	}

    private static Vector<Font> getCompatibleFonts() {
        Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        Vector<Font> fontVector = new Vector<>();

        for (Font font : fonts) {
            if (font.canDisplayUpTo("12345678" + BOMB) < 0) {
                fontVector.add(font);
            }
        }

        return fontVector;
    }

	public void actionPerformed(ActionEvent e) {
		// Todo
		JButton cur = (JButton) e.getSource();
		// cur.setText("?");
		int coordinate = Integer.parseInt(cur.getName());
		int i = coordinate / size;
		int j = coordinate % size;

		
        if (!mineField.isExposed(i, j)) {
			mineField.setExposed(i, j);
			--counter;
			//System.out.println("" + counter);

            if (mineField.isBomb(i, j)) {
				cur.setOpaque(true);
                cur.setForeground(Color.RED);
                cur.setText(BOOM);
				explode(false);
				return;
            } else {
                int count = mineField.countSurroundingMines(i, j);
                /*if (count > 0)
                    cur.setForeground(colors[count - 1]);
                else
					cur.setForeground(colors[8]);
				cur.setText("" + count);*/
				if (count > 0) {
                    cur.setForeground(colors[count - 1]);
					cur.setText("" + count);
                } else {
					cur.setEnabled(false);
					autoExpand(i, j);
				}
            }

			if (counter == mineNumber) {
				explode(true);
			}
        }

	}

    public JComponent getUI() {
        return ui;
    }

    public static void main(String[] args) {
        //Runnable r = () -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception useDefault) {
            }

			int size = 0;
			int mineNumber = 0;

			if (args.length == 2) {
				try {
					size = Integer.parseInt(args[0]);
					mineNumber = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					System.err.println("Invalid Input: Please enter numbers.");
					return;
				}
			} else {
				System.err.println("Wrong Input Number.");
				return;
			}

            MineSweeper ms = new MineSweeper(size, mineNumber);

            JFrame f = new JFrame(ms.getClass().getSimpleName());
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            f.setLocationByPlatform(true);

            f.setContentPane(ms.getUI());
            f.pack();
            f.setMinimumSize(f.getSize());

            f.setVisible(true);
        //};
        //SwingUtilities.invokeLater(r);

    }
}
