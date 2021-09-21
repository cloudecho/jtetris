

package com.github.cloudecho.jtetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class Gui extends JFrame {
    private static final long serialVersionUID = 1L;
    public static final int UNIT_SIZE = 32;
    public static final Font FONT = new Font(Font.MONOSPACED, Font.PLAIN, UNIT_SIZE / 2);
    public static final Font FONT2 = new Font(Font.MONOSPACED, Font.PLAIN, 4 * UNIT_SIZE / 5);

    private MPanel matrix;
    private MPanel matrixNext;

    private JComponent main;

    private final JButton btnRotate = new JButton("^");
    private final JButton btnLeft = new JButton("<");
    private final JButton btnDown = new JButton("v");
    private final JButton btnRight = new JButton(">");
    private final JButton btnPause = new JButton(LABEL_PAUSE);
    private final JToggleButton btnMusic = new JToggleButton("Music");
    private final JLabel[] labelValues = new JLabel[2];
    private final JLabel labelState = new JLabel(" ");

    public Gui(Tetris tetris) {
        this.setLocationByPlatform(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.init();
        this.addListeners(tetris);
    }

    private void init() {
        this.setTitle("Tetris : github.com/cloudecho/jtetris");

        JButton[] buttons = new JButton[]{this.btnRotate, this.btnLeft, this.btnDown, this.btnRight};
        JLabel[] labels = new JLabel[]{new JLabel("SCORE"), new JLabel("LEVEL")};
        MPanel[] mpanels = new MPanel[]{new MPanel(Tetris.ROW, Tetris.COL), new MPanel(Shape.RANK, Shape.RANK)};

        this.matrix = mpanels[0];
        this.matrixNext = mpanels[1];

        for (int i = 0; i < this.labelValues.length; ++i) {
            this.labelValues[i] = new JLabel("0");
            this.labelValues[i].setFont(FONT2);
            this.labelValues[i].setAlignmentX(JTextField.CENTER_ALIGNMENT);
            this.labelValues[i].setAlignmentY(JTextField.CENTER_ALIGNMENT);
        }

        for (JButton b : buttons) {
            b.setFont(FONT);
        }

        this.btnPause.setFont(FONT);
        this.btnMusic.setFont(FONT);

        for (JLabel label : labels) {
            label.setFont(FONT2);
            label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            label.setAlignmentY(JLabel.CENTER_ALIGNMENT);
        }

        this.labelState.setFont(FONT);
        this.labelState.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        this.labelState.setAlignmentY(JLabel.CENTER_ALIGNMENT);

        main = Box.createHorizontalBox();
        this.getContentPane().add(main);

        Box twoButtons = Box.createHorizontalBox();
        twoButtons.add(this.btnMusic);
        twoButtons.add(Box.createHorizontalStrut(UNIT_SIZE / 2));
        twoButtons.add(this.btnPause);

        Panel grid = new Panel();
        main.add(mpanels[0]);
        main.add(grid);

        grid.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        int gridy = 0;

        c.gridwidth = 5;
        addTo(grid, c, 0, gridy++, twoButtons);
        addTo(grid, c, 0, gridy++, Box.createHorizontalStrut(twoButtons.getPreferredSize().width + UNIT_SIZE));
        addTo(grid, c, 0, gridy++, Box.createVerticalStrut(3));
        addTo(grid, c, 0, gridy++, mpanels[1]);
        addTo(grid, c, 0, gridy++, Box.createVerticalStrut(5 * UNIT_SIZE));
        addTo(grid, c, 0, gridy++, labels[0]);
        addTo(grid, c, 0, gridy++, this.labelValues[0]);
        addTo(grid, c, 0, gridy++, Box.createVerticalStrut(UNIT_SIZE / 2));
        addTo(grid, c, 0, gridy++, labels[1]);
        addTo(grid, c, 0, gridy++, this.labelValues[1]);
        addTo(grid, c, 0, gridy++, Box.createVerticalStrut(UNIT_SIZE));

        c.gridwidth = 1;
        addTo(grid, c, 2, gridy++, btnRotate);
        addTo(grid, c, 0, gridy, Box.createHorizontalStrut(btnRotate.getPreferredSize().width));
        addTo(grid, c, 1, gridy, btnLeft);
        addTo(grid, c, 3, gridy++, btnRight);
        addTo(grid, c, 2, gridy++, btnDown);

        c.gridwidth = 5;
        addTo(grid, c, 0, gridy++, Box.createVerticalStrut(UNIT_SIZE / 2));
        addTo(grid, c, 0, gridy++, labelState);
    }

    private void addTo(Panel grid, GridBagConstraints c, int gridx, int gridy, Component comp) {
        c.gridx = gridx;
        c.gridy = gridy;
        grid.add(comp, c);
    }

    void reset() {
        for (int i = 0; i < Tetris.ROW; ++i) {
            for (int j = 0; j < Tetris.COL; ++j) {
                this.matrix.paint(i, j, Colors.BG_COLOR);
            }
        }

        this.labelValues[0].setText("0");
        this.labelValues[1].setText("0");
        this.labelState.setText(" ");
    }

    void showShapeNext(Shape nextShape) {
        showShapeNext(matrixNext.getGraphics(), nextShape);
    }

    void showShapeNext(Graphics g, Shape nextShape) {
        this.drawShapeNext(g, nextShape, nextShape.color());
    }

    void eraseShapeNext(Shape nextShape) {
        this.drawShapeNext(matrixNext.getGraphics(), nextShape, Colors.BG_COLOR);
    }

    void showShape(Shape currShape) {
        showShape(matrix.getGraphics(), currShape);
    }

    void showShape(Graphics g, Shape currShape) {
        this.drawShape(g, currShape, currShape.color());
    }

    void eraseShape(Shape currShape) {
        eraseShape(matrix.getGraphics(), currShape);
    }

    void eraseShape(Graphics g, Shape currShape) {
        this.drawShape(g, currShape, Colors.BG_COLOR);
    }

    private void drawShape(Graphics g, Shape currShape, Color color) {
        boolean[][] shape = currShape.getShape();

        for (int i = currShape.x; i <= currShape.x2; ++i) {
            for (int j = currShape.y; j <= currShape.y2; ++j) {
                if (shape[i - currShape.x][j - currShape.y]) {
                    this.matrix.paint(g, i, j, color);
                }
            }
        }

    }

    private void drawShapeNext(Graphics g, Shape nextShape, Color color) {
        boolean[][] shape = nextShape.getShape();
        int[] size = nextShape.getSize();

        for (int i = 0; i < size[0]; ++i) {
            for (int j = 0; j < size[1]; ++j) {
                if (shape[i][j]) {
                    this.matrixNext.paint(g, i, j, color);
                }
            }
        }
    }

    void wink(int whichRow, int time) {
        for (int k = 0; k < time; ++k) {
            for (int i = 0; i < Tetris.COL; ++i) {
                this.matrix.paint(whichRow, i, Colors.BG_COLOR);
            }
            Tetris.sleep(100);

            for (int i = 0; i < Tetris.COL; ++i) {
                this.matrix.paint(whichRow, i, Colors.WK_COLOR);
            }
            Tetris.sleep(100);
        }
    }

    void flushColor(byte[][] model, int rowBegin, int rowEnd) {
        flushColor(matrix.getGraphics(), model, rowBegin, rowEnd);
    }

    void flushColor(Graphics g, byte[][] model, int rowBegin, int rowEnd) {
        for (int i = rowBegin; i <= rowEnd; ++i) {
            for (int j = 0; j < Tetris.COL; ++j) {
                this.matrix.paint(g, i, j, Colors.colorOf(model[i][j]));
            }
        }
    }

    void music() {
        if (this.btnMusic.isSelected()) {
            GameAudio.getInstance().play();
        } else {
            GameAudio.getInstance().stop();
        }
    }

    private void actionPrr(Tetris tetris) {
        final String t = btnPause.getText();
        if (LABEL_PAUSE.equals(t)) {
            tetris.pause();
        } else if (LABEL_RESUME.equals(t)) {
            tetris.resume();
        } else if (LABEL_RESTART.equals(t)) {
            reset();
            tetris.restart();
        }
    }

    private static final String LABEL_PAUSE = "Pause";
    private static final String LABEL_RESUME = "Resume";
    private static final String LABEL_RESTART = "Restart";

    void stateChanged(int newState) {
        switch (newState) {
            case Tetris.STATE_OVER:
                this.btnPause.setText(LABEL_RESTART);
                this.labelState.setText("GAME OVER");
                break;
            case Tetris.STATE_PAUSED:
                this.btnPause.setText(LABEL_RESUME);
                this.labelState.setText("PAUSED");
                break;
            case Tetris.STATE_RUNNING:
                this.btnPause.setText(LABEL_PAUSE);
                this.labelState.setText(" ");
                break;
        }
    }

    void scoreChanged(int score) {
        this.labelValues[0].setText("" + score);
    }

    void levelChanged(int level) {
        this.labelValues[1].setText("" + level);
    }

    private void addListeners(final Tetris tetris) {
        JButton[] buttons = new JButton[]{this.btnRotate, this.btnLeft, this.btnRight};
        final int[] directions = new int[]{Shape.ROTATE, Shape.LEFT, Shape.RIGHT};

        for (int i = 0; i < 3; ++i) {
            final int k = i;
            buttons[i].addActionListener((actionEvent) -> {
                if (!tetris.paused()) {
                    tetris.shapeMove(directions[k]);
                }
            });
        }

        this.btnDown.addActionListener((actionEvent) -> tetris.down());

        this.btnPause.addActionListener((actionEvent) -> actionPrr(tetris));

        this.btnMusic.addActionListener((actionEvent) -> music());

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                tetris.windowOpened();
            }
        });

        this.matrix.addComponentPaintListener((g) -> {
            if (tetris.currShape != null) {
                showShape(g, tetris.currShape);
                flushColor(g, tetris.model, tetris.rowBegin, Tetris.ROW - 1);
            }
        });

        this.matrixNext.addComponentPaintListener((g) -> {
            if (tetris.nextShape != null) {
                showShapeNext(g, tetris.nextShape);
            }
        });

        this.main.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent keyEvent) {
                int keyCode = keyEvent.getKeyCode();
                switch (keyCode) {
                    case 10:
                        btnMusic.setSelected(!btnMusic.isSelected());
                        music();
                        break;
                    case 32:
                        actionPrr(tetris);
                        break;
                    case 37:
                    case 65:
                    case 226:
                        tetris.left();
                        break;
                    case 38:
                    case 87:
                    case 224:
                        tetris.rotate();
                        break;
                    case 39:
                    case 68:
                    case 227:
                        tetris.right();
                        break;
                    case 40:
                    case 83:
                    case 225:
                        tetris.down();
                        break;
                    default:
                        System.out.println("keyCode=" + keyCode);
                }
            }
        });

        new Thread(new FocusRequesting(main)).start();
    }

    private static class FocusRequesting implements Runnable {
        JComponent component;

        private FocusRequesting(JComponent component) {
            this.component = component;
        }

        public void run() {
            while (true) {
                if (!component.isFocusOwner()) {
                    component.requestFocusInWindow();
                }
                Tetris.sleep(200);
            }
        }
    }
}
