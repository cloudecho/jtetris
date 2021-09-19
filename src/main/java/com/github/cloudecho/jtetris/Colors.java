package com.github.cloudecho.jtetris;

import java.awt.*;

public class Colors {
    public static final Color BG_COLOR = new Color(0xe7, 0xe7, 0xe7);
    public static final Color WK_COLOR = Color.DARK_GRAY;

    private static final Color[] COLORS = new Color[]{
            BG_COLOR,
            new Color(82, 173, 173),
            new Color(186, 103, 194),
            new Color(192, 190, 91, 255),
            new Color(88, 119, 155),
            new Color(213, 120, 120),
            new Color(199, 133, 66),
    };

    private Colors() {
    }

    /**
     * A random color id.
     *
     * @return color id, excludes 0 (BG_COLOR)
     */
    public static byte randomId() {
        return (byte) (1 + (COLORS.length - 1) * Math.random());
    }

    /**
     * Get {@code Color} by id.
     *
     * @param id color id, starts from 0
     * @return A {@code Color} object
     */
    public static Color colorOf(byte id) {
        if (id < 0 || id > COLORS.length - 1) {
            throw new IllegalArgumentException("id out of bounds");
        }
        return COLORS[id];
    }
}
