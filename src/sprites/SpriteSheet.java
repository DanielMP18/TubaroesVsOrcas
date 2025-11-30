package sprites;

import java.awt.image.BufferedImage;

public class SpriteSheet {
    private final BufferedImage sheet;
    private final int frameWidth;
    private final int frameHeight;
    private final int cols;
    private final int rows;

    public SpriteSheet(BufferedImage sheet, int frameWidth, int frameHeight) {
        if (sheet == null) throw new IllegalArgumentException("sheet null");
        this.sheet = sheet;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.cols = sheet.getWidth() / frameWidth;
        this.rows = sheet.getHeight() / frameHeight;
    }

    public int getCols() { return cols; }
    public int getRows() { return rows; }

    // Retorna todos os frames (row-major)
    public BufferedImage[] getSprites() {
        BufferedImage[] frames = new BufferedImage[cols * rows];
        int idx = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                frames[idx++] = sheet.getSubimage(c * frameWidth, r * frameHeight, frameWidth, frameHeight);
            }
        }
        return frames;
    }

    // Retorna todos os frames de uma linha (row)
    public BufferedImage[] getRow(int row) {
        if (row < 0 || row >= rows) throw new IndexOutOfBoundsException("row fora do range");
        BufferedImage[] frames = new BufferedImage[cols];
        for (int c = 0; c < cols; c++) {
            frames[c] = sheet.getSubimage(c * frameWidth, row * frameHeight, frameWidth, frameHeight);
        }
        return frames;
    }

    // Pega um frame específico
    public BufferedImage getSprite(int col, int row) {
        if (col < 0 || col >= cols || row < 0 || row >= rows) throw new IndexOutOfBoundsException();
        return sheet.getSubimage(col * frameWidth, row * frameHeight, frameWidth, frameHeight);
    }
}
