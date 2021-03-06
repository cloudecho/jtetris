

package com.github.cloudecho.jtetris;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JPanel with a square matrix (row x col)
 */
class MPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    public static final Color DEFAULT_COLOR = Colors.BG_COLOR;

    private final int row;
    private final int col;
    private final int margin;
    private final int width;
    private final int height;

    private List<ComponentPaintListener> paintListeners = new ArrayList<>(1);

    public MPanel(int row, int col) {
        this(row, col, 1);
    }

    public MPanel(int row, int col, int margin) {
        this.row = row;
        this.col = col;
        this.margin = margin;
        this.width = col * Gui.UNIT_SIZE;
        this.height = row * Gui.UNIT_SIZE;
        this.setPreferredSize(new Dimension(width, height));
    }

    public void paint(int r, int c, Color color) {
        paint(getGraphics(), r, c, color);
    }

    public void paint(Graphics g, int r, int c, Color color) {
        g.setColor(color);
        g.fillRect(c * Gui.UNIT_SIZE,
                r * Gui.UNIT_SIZE,
                Gui.UNIT_SIZE - margin,
                Gui.UNIT_SIZE - margin);
    }

    private void init(Graphics g) {
        g.setColor(DEFAULT_COLOR);
        for (int i = 0; i < this.row; ++i) {
            for (int j = 0; j < this.col; ++j) {
                g.fillRect(j * Gui.UNIT_SIZE,
                        i * Gui.UNIT_SIZE,
                        Gui.UNIT_SIZE - margin,
                        Gui.UNIT_SIZE - margin);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width - margin, height - margin);

        this.init(g);

        paintListeners.forEach(l -> l.paintComponent(g));
    }

    public void addComponentPaintListener(ComponentPaintListener l) {
        paintListeners.add(l);
    }

    public interface ComponentPaintListener {
        void paintComponent(Graphics g);
    }
}
