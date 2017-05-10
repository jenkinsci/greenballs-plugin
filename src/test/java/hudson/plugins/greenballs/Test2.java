package hudson.plugins.greenballs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import hudson.plugins.greenballs.ColorBlindUtils.Status;

public class Test2 {

    @Test
    public void test_png() throws Exception {
        String[] sizes = new String[] { "48x48", "32x32", "24x24", "16x16" };
        for (String size : sizes) {
            BufferedImage image = ColorBlindUtils.read(new File(
                    "/Users/nikolasfalco/git/greenballs-plugin/target/jenkins-for-test/images/" + size + "/blue.png")
                            .toURI().toURL());
            image = ColorBlindUtils.overlay(image, "S");
            ImageIO.write(image, "png", new File("/Users/nikolasfalco/git/greenballs-plugin/" + size + "_blue.png"));
        }
    }

    @Test
    public void test_animation() throws Exception {
        String[] sizes = new String[] { "48x48", "32x32", "24x24", "16x16" };
        for (String size : sizes) {
            File input = new File("/Users/nikolasfalco/git/greenballs-plugin/target/jenkins-for-test/images/" + size + "/blue_anime.png");
            File output = new File("/Users/nikolasfalco/git/greenballs-plugin/" + size + "_blue_anime.gif");
            try (ImageOutputStream ios = ImageIO.createImageOutputStream(input); OutputStream fos = new FileOutputStream(output)) {
                byte[] data = ColorBlindUtils.overlayAnimate(ios, Status.SUCCESS.getOverlay());
                IOUtils.write(data, fos);
            }
        }
    }

}