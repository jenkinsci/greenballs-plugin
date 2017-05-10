package hudson.plugins.greenballs;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.annotation.Nonnull;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

public final class ColorBlindUtils {

    private ColorBlindUtils() {
        // default constructor
    }

    public static BufferedImage read(@Nonnull URL url) throws IOException {
        return ImageIO.read(url);
    }

    public static BufferedImage overlay(@Nonnull BufferedImage image, @Nonnull String overlay) throws IOException {
        return overlay(image, overlay, image.getWidth());
    }

    private static BufferedImage overlay(BufferedImage image, String overlay, int size) throws IOException {
        int fontSize = size / 2;

        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial Black", Font.BOLD, fontSize));

        FontMetrics fontMetrics = g.getFontMetrics();
        int width = fontMetrics.stringWidth(overlay);
        int height = fontMetrics.getHeight();
        int ascent = fontMetrics.getAscent();
        int x = Math.round((size - width) / 2f);
        int y = (size - height) / 2 + ascent - 1; // -1 because starts from 0
        g.drawString(overlay, x, y);

        g.dispose();
        return image;
    }

    public static byte[] overlayAnimate(@Nonnull ImageInputStream imageStream, @Nonnull String overlay) throws IOException {
        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        ImageWriter writer = ImageIO.getImageWriter(reader);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(ios);
            writer.prepareWriteSequence(null);

            reader.setInput(imageStream, false);

            int size = 0;
            int noi = reader.getNumImages(true);
            for (int i = 0; i < noi; i++) {
                BufferedImage image = reader.read(i);
                if (size == 0) {
                    size = image.getWidth();
                }
                IIOMetadata metadata = reader.getImageMetadata(i);

                image = overlay(image, Status.SUCCESS.getOverlay(), size);
                IIOImage iim = new IIOImage(image, null, metadata);
                writer.writeToSequence(iim, null);
            }
            writer.endWriteSequence();
        }
        return baos.toByteArray();
    }

    enum Status {
        SUCCESS("S"), UNSTABLE("U"), FAILURE("F");

        private String overlay;

        Status(String overlay) {
            this.overlay = overlay;
        }

        public String getOverlay() {
            return overlay;
        }
    }

}