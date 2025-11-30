package sprites;

import java.awt.image.BufferedImage;

public class SpriteSheet {
    private final BufferedImage sheet;
    private final int frameWidth;
    private final int frameHeight;

    public SpriteSheet(BufferedImage sheet, int frameWidth, int frameHeight) {
        this.sheet = sheet;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
    }

    // Extrai todos os frames em leitura por linhas (row-major)
    public BufferedImage[] getSprites() {
        int cols = sheet.getWidth() / frameWidth;
        int rows = sheet.getHeight() / frameHeight;
        BufferedImage[] frames = new BufferedImage[cols * rows];
        int idx = 0;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                frames[idx++] = sheet.getSubimage(x * frameWidth, y * frameHeight, frameWidth, frameHeight);
            }
        }
        return frames;
    }

    // Pega um frame específico (coluna, linha)
    public BufferedImage getSprite(int col, int row) {
        return sheet.getSubimage(col * frameWidth, row * frameHeight, frameWidth, frameHeight);
    }
}