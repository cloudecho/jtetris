

package com.github.cloudecho.jtetris;

import java.awt.*;
import javax.swing.JPanel;

/**
 * JPanel with a label matrix
 */
class MPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    public static final Color DEFAULT_COLOR = Colors.BG_COLOR;
    public static final Dimension DEFAULT_SIZE = new Dimension(Gui.UNIT_SIZE, Gui.UNIT_SIZE);
    public Label[][] matrix;
    private final int row;
    private final int col;
    private final int margin;

    public MPanel(int row, int col) {
        this(row, col, 1);
    }

    public MPanel(int row, int col, int margin) {
        this.row = row;
        this.col = col;
        this.margin = margin;
        this.init();
    }

    private void init() {
        this.matrix = new Label[this.row][this.col];
        this.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(margin, margin, margin, margin);
        for (int i = 0; i < this.row; ++i) {
            for (int j = 0; j < this.col; ++j) {
                this.matrix[i][j] = new Label();
                this.matrix[i][j].setBackground(DEFAULT_COLOR);
                this.matrix[i][j].setPreferredSize(DEFAULT_SIZE);

                c.gridx = j;
                c.gridy = i;
                this.add(this.matrix[i][j], c);
            }
        }
    }
}
