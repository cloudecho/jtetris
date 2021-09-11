
package com.github.cloudecho.jtetris;

/**
 * Shape with position
 */
class Shape {
    private final int id;
    public int x; // top
    public int y; // left
    public int x2; // top + height
    public int y2; // left + width

    public static final int DOWN = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int ROTATE = 3;

    private static final boolean _1 = true;
    private static final boolean _0 = false;

    private static final Object[] SHAPES = new Object[]{
            new boolean[][]{ // 0
                    {_1}},
            new boolean[][]{ // 1-2
                    {_1, _1}},
            new boolean[][]{
                    {_1},
                    {_1}},
            new boolean[][]{ // 3-4
                    {_1, _1, _1}},
            new boolean[][]{
                    {_1},
                    {_1},
                    {_1}},
            new boolean[][]{ // 5-8
                    {_1, _0},
                    {_1, _1}},
            new boolean[][]{
                    {_0, _1},
                    {_1, _1}},
            new boolean[][]{
                    {_1, _1},
                    {_0, _1}},
            new boolean[][]{
                    {_1, _1},
                    {_1, _0}},
            new boolean[][]{ // 9-10
                    {_1, _1, _1, _1}},
            new boolean[][]{
                    {_1},
                    {_1},
                    {_1},
                    {_1}},
            new boolean[][]{ // 11-14
                    {_0, _0, _1},
                    {_1, _1, _1}},
            new boolean[][]{
                    {_1, _1},
                    {_0, _1},
                    {_0, _1}},
            new boolean[][]{
                    {_1, _1, _1},
                    {_1, _0, _0}},
            new boolean[][]{
                    {_1, _0},
                    {_1, _0},
                    {_1, _1}},
            new boolean[][]{ // 15
                    {_1, _1},
                    {_1, _1}},
            new boolean[][]{// 16-19
                    {_0, _1, _0},
                    {_1, _1, _1}},
            new boolean[][]{
                    {_0, _1},
                    {_1, _1},
                    {_0, _1}},
            new boolean[][]{
                    {_1, _1, _1},
                    {_0, _1, _0}},
            new boolean[][]{
                    {_1, _0},
                    {_1, _1},
                    {_1, _0}},
            new boolean[][]{ // 20-21
                    {_1, _0},
                    {_1, _1},
                    {_0, _1}},
            new boolean[][]{
                    {_0, _1, _1},
                    {_1, _1, _0}},
            new boolean[][]{ // 22-23
                    {_0, _1},
                    {_1, _1},
                    {_1, _0}},
            new boolean[][]{
                    {_1, _1, _0},
                    {_0, _1, _1}},
            new boolean[][]{ // 24-27
                    {_0, _1},
                    {_0, _1},
                    {_1, _1}},
            new boolean[][]{
                    {_1, _1, _1},
                    {_0, _0, _1}},
            new boolean[][]{
                    {_1, _1},
                    {_1, _0},
                    {_1, _0}},
            new boolean[][]{
                    {_1, _0, _0},
                    {_1, _1, _1}}};

    public static final int SHAPE_NUM = SHAPES.length;
    public static final int RANK = 4;

    public Shape(int id) {
        this.id = id;
        this.x = 0; // top
        this.y = Tetris.COL / 2 - 1; // left
        this.computeXY2();
    }

    public Shape(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.computeXY2();
    }

    /**
     * Compute x2,y2
     */
    private void computeXY2() {
        int[] size = this.getSize();
        this.x2 = this.x + size[0] - 1;
        this.y2 = this.y + size[1] - 1;
    }

    /**
     * Get the original shape (without position)
     */
    public boolean[][] getShape() {
        return (boolean[][]) SHAPES[this.id];
    }

    /**
     * [height, width]
     *
     * @return int[2]
     */
    public int[] getSize() {
        boolean[][] b = this.getShape();
        return new int[]{b.length, b[0].length};
    }

    private Shape tryRotate() {
        int id = this.id;
        int xL = this.x;
        int yL = this.y;
        switch (this.id) {
            case 1:
            case 2:
                id = 3 - this.id;
                break;
            case 3:
            case 4:
                id = 7 - this.id;
                break;
            case 5:
            case 6:
            case 7:
            case 8:
                id = this.id >= 8 ? 5 : this.id + 1;
                break;
            case 9:
            case 10:
                id = 19 - this.id;
                break;
            case 11:
            case 12:
            case 13:
            case 14:
                id = this.id >= 14 ? 11 : this.id + 1;
            case 15:
            default:
                break;
            case 16:
            case 17:
            case 18:
            case 19:
                id = this.id >= 19 ? 16 : this.id + 1;
                break;
            case 20:
            case 21:
                id = 41 - this.id;
                break;
            case 22:
            case 23:
                id = 45 - this.id;
                break;
            case 24:
            case 25:
            case 26:
            case 27:
                id = this.id >= 27 ? 24 : this.id + 1;
        }

        switch (this.id) {
            case 1:
                --xL;
                break;
            case 2:
            case 17:
            case 24:
                ++xL;
                break;
            case 3:
            case 13:
            case 18:
            case 23:
            case 25:
                --xL;
                ++yL;
                break;
            case 4:
            case 22:
                ++xL;
                --yL;
            case 5:
            case 6:
            case 7:
            case 8:
            case 11:
            case 15:
            case 16:
            case 20:
            case 21:
            default:
                break;
            case 9:
                xL -= 2;
                ++yL;
                break;
            case 10:
                xL += 2;
                --yL;
                break;
            case 12:
                ++xL;
                break;
            case 14:
            case 19:
            case 26:
                --yL;
        }

        return new Shape(id, xL, yL);
    }

    public Shape tryMove(int direction) {
        if (direction == ROTATE) {
            return this.tryRotate();
        }

        int id = this.id;
        int xL = this.x;
        int yL = this.y;
        switch (direction) {
            case DOWN:
                ++xL;
                break;
            case LEFT:
                --yL;
                break;
            case RIGHT:
                ++yL;
        }

        return new Shape(id, xL, yL);
    }
}
