

package com.github.cloudecho.jtetris;

public class Tetris implements Runnable {
    public static final int ROW = 19;
    public static final int COL = 11;

    private static final int[] SCORES = new int[]{100, 300, 500, 700};
    private static final int[] SPEEDS = new int[]{1200, 1000, 800, 500, 300, 200};

    static final int STATE_OVER = 1;
    static final int STATE_RUNNING = 2;
    static final int STATE_PAUSED = 3;

    private final boolean[][] model = new boolean[ROW][COL];
    private int level; // start from 0
    private int lines;
    private int score;
    private int speed;
    private int rowBegin;

    private volatile int state = STATE_RUNNING;
    private Shape currShape = new Shape(this.randIndex());
    private Shape nextShape;
    private final Gui gui = new Gui(this);

    public Tetris() {
        this.init();
        this.gui.pack();
        this.gui.setVisible(true);
    }

    private void init() {
        for (int i = 0; i < ROW; ++i) {
            for (int j = 0; j < COL; ++j) {
                this.model[i][j] = false;
            }
        }

        this.speed = SPEEDS[0];
        this.rowBegin = ROW - 1;
        this.score = 0;
        this.lines = 0;
    }

    void rotate() {
        if (!paused()) {
            this.shapeMove(Shape.ROTATE);
        }
    }

    void down() {
        if (!this.paused()) {
            while (this.shapeMove(Shape.DOWN)) ;
        }
    }

    void left() {
        if (!this.paused()) {
            this.shapeMove(Shape.LEFT);
        }
    }

    void right() {
        if (!this.paused()) {
            this.shapeMove(Shape.RIGHT);
        }
    }

    void pause() {
        if (STATE_RUNNING != this.state) {
            System.out.printf("could not pause as current state is %d\n", state);
            return;
        }
        this.state = STATE_PAUSED;
        gui.stateChanged(this.state);
    }

    synchronized void resume() {
        if (STATE_PAUSED != this.state) {
            System.out.printf("could not resume as current state is %d\n", state);
            return;
        }
        this.state = STATE_RUNNING;
        this.notify();
        gui.stateChanged(this.state);
    }

    synchronized void restart() {
        this.init();
        this.state = STATE_RUNNING;
        this.notify();
    }

    private void drift() {
        int len = ROW - this.rowBegin;
        boolean[] bCol = new boolean[len];

        for (int i = 0; i < len; ++i) {
            bCol[i] = this.model[i + this.rowBegin][0];
        }

        for (int j = 0; j < COL - 1; ++j) {
            for (int i = this.rowBegin; i < ROW; ++i) {
                this.model[i][j] = this.model[i][j + 1];
            }

            gui.flushColor(this.model, rowBegin, ROW - 1);
            Tetris.sleep(4);
        }

        for (int i = 0; i < len; ++i) {
            this.model[i + this.rowBegin][COL - 1] = bCol[i];
        }

        gui.flushColor(this.model, rowBegin, ROW - 1);
    }

    private synchronized void randomDrift(double probability) {
        if (Math.random() < probability) {
            int time = (int) Math.floor(COL * Math.random());

            for (int i = 0; i < time; ++i) {
                this.drift();
            }
        }

    }

    synchronized boolean shapeMove(int direction) {
        Shape shape = this.currShape.tryMove(direction);
        if (this.checkShapeMove(shape)) {
            gui.eraseShape(currShape);
            this.currShape = shape;
            gui.showShape(currShape);
            return true;
        }
        return false;
    }

    private synchronized boolean checkShapeMove(Shape shape) {
        if (shape.y >= 0 && shape.y2 < COL && shape.x >= 0 && shape.x2 < ROW) {
            boolean[][] oShape = shape.getShape();

            for (int i = shape.x; i <= shape.x2; ++i) {
                for (int j = shape.y; j <= shape.y2; ++j) {
                    if (oShape[i - shape.x][j - shape.y] && this.model[i][j]) {
                        return false;
                    }
                }
            }

            return true;
        }
        return false;
    }

    private synchronized void updateModel() {
        boolean[][] shape = this.currShape.getShape();

        for (int i = this.currShape.x; i <= this.currShape.x2; ++i) {
            for (int j = this.currShape.y; j <= this.currShape.y2; ++j) {
                if (shape[i - this.currShape.x][j - this.currShape.y]) {
                    this.model[i][j] = shape[i - this.currShape.x][j - this.currShape.y];
                }
            }
        }
    }

    private int randIndex() {
        return (int) Math.floor((double) Shape.SHAPE_NUM * Math.random());
    }

    public void run() {
        while (true) {
            synchronized (this) {
                while (STATE_RUNNING != this.state) {
                    try {
                        System.out.println("wait to start");
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            for (; this.rowBegin > 0; this.randomDrift(0.1d)) {
                this.nextShape = new Shape(this.randIndex());
                gui.showShapeNext(nextShape);
                gui.showShape(currShape);
                Tetris.sleep(this.speed);

                while (this.shapeMove(Shape.DOWN)) {
                    // wait to continue
                    synchronized (this) {
                        while (this.paused()) {
                            try {
                                System.out.println("paused, wait to continue");
                                this.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    Tetris.sleep(this.speed);
                }

                if (this.rowBegin > this.currShape.x) {
                    this.rowBegin = this.currShape.x;
                }

                synchronized (this) {
                    this.updateModel();
                    this.lines += this.addScore();
                    this.gui.scoreChanged(this.score);
                    this.updateLevel();
                    this.currShape = this.nextShape;
                    gui.eraseShapeNext(nextShape);
                }
            }

            this.gameOver();
        }
    }

    boolean paused() {
        return STATE_PAUSED == state;
    }

    private void gameOver() {
        this.state = STATE_OVER;
        gui.stateChanged(this.state);
    }

    private void updateLevel() {
        final int newLevel = this.lines / ROW;
        if (newLevel > this.level && newLevel < SPEEDS.length) {
            this.level = newLevel;
            this.gui.levelChanged(this.level);
        }
    }

    static void sleep(int millisec) {
        try {
            Thread.sleep(millisec);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private int addScore() {
        int addLines = this.addScoreCheck();
        this.score += earnScore(addLines);
        return addLines;
    }

    private int earnScore(int addLines) {
        if (addLines == 0) {
            return 0;
        }
        return SCORES[addLines - 1] + 100 * this.level;
    }

    private synchronized int addScoreCheck() {
        int addLines = 0;

        for (int i = this.rowBegin; i < ROW; ++i) {
            boolean bAdd = true;

            for (int j = 0; j < COL; ++j) {
                if (!this.model[i][j]) {
                    bAdd = false;
                    break;
                }
            }

            if (bAdd) {
                this.removeRow(i);
                ++this.rowBegin;
                ++addLines;
            }
        }

        return addLines;
    }

    private synchronized void removeRow(int rowEnd) {
        gui.wink(rowEnd, 2);

        for (int i = rowEnd; i > this.rowBegin; --i) {
            for (int j = 0; j < COL; ++j) {
                this.model[i][j] = this.model[i - 1][j];
            }
        }

        for (int j = 0; j < COL; ++j) {
            this.model[this.rowBegin][j] = false;
        }

        gui.flushColor(this.model, rowBegin, rowEnd);
    }


    public static void main(String[] args) {
        System.out.printf("SHAPE_NUM: %d\n", Shape.SHAPE_NUM);

        Tetris dg = new Tetris();
        new Thread(dg).start();
    }
}
