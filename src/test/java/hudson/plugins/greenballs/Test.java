package hudson.plugins.greenballs;

import java.awt.image.BufferedImage;
import java.io.File;
import org.w3c.dom.Node;

import javax.imageio.*;
import javax.imageio.metadata.*;
import javax.imageio.stream.ImageOutputStream;

/**
 * Creates an animated GIF from GIF frames. A thin wrapper to code written by
 * other people, as documented on the thread on the Sun forums 'Create animated
 * GIF using imageio' http://forums.sun.com/thread.jspa?threadID=5395006 See the
 * printUsage() method for details on paramaters required.
 *
 * @author Andrew Thompson
 */
class WriteAnimatedGif {

    /**
     * See http://forums.sun.com/thread.jspa?messageID=10755673#10755673
     *
     * @author Maxideon
     * @param delayTime
     *            String Frame delay for this frame.
     */
    public static void configure(IIOMetadata meta, String delayTime, int imageIndex) {

        String metaFormat = meta.getNativeMetadataFormatName();

        if (!"javax_imageio_gif_image_1.0".equals(metaFormat)) {
            throw new IllegalArgumentException("Unfamiliar gif metadata format: " + metaFormat);
        }

        Node root = meta.getAsTree(metaFormat);

        // find the GraphicControlExtension node
        Node child = root.getFirstChild();
        while (child != null) {
            if ("GraphicControlExtension".equals(child.getNodeName())) {
                break;
            }
            child = child.getNextSibling();
        }

        IIOMetadataNode gce = (IIOMetadataNode) child;
        gce.setAttribute("userDelay", "FALSE");
        gce.setAttribute("delayTime", delayTime);

        // only the first node needs the ApplicationExtensions node
        if (imageIndex == 0) {
            IIOMetadataNode aes = new IIOMetadataNode("ApplicationExtensions");
            IIOMetadataNode ae = new IIOMetadataNode("ApplicationExtension");
            ae.setAttribute("applicationID", "NETSCAPE");
            ae.setAttribute("authenticationCode", "2.0");
            byte[] uo = new byte[] {
                    // last two bytes is an unsigned short (little endian) that
                    // indicates the the number of times to loop.
                    // 0 means loop forever.
                    0x1, 0x0, 0x0 };
            ae.setUserObject(uo);
            aes.appendChild(ae);
            root.appendChild(aes);
        }

        try {
            meta.setFromTree(metaFormat, root);
        } catch (IIOInvalidTreeException e) {
            // shouldn't happen
            throw new Error(e);
        }
    }

    /**
     * See http://forums.sun.com/thread.jspa?messageID=9988198
     *
     * @author GeoffTitmus
     * @param file
     *            File A File in which to store the animation.
     * @param frames
     *            BufferedImage[] Array of BufferedImages, the frames of the
     *            animation.
     * @param delayTimes
     *            String[] Array of Strings, representing the frame delay times.
     */
    public static void saveAnimate(File file, BufferedImage[] frames, String[] delayTimes) throws Exception {

        ImageWriter iw = ImageIO.getImageWritersByFormatName("gif").next();

        ImageOutputStream ios = ImageIO.createImageOutputStream(file);
        iw.setOutput(ios);
        iw.prepareWriteSequence(null);

        for (int i = 0; i < frames.length; i++) {
            BufferedImage src = frames[i];

            ImageWriteParam iwp = iw.getDefaultWriteParam();

            IIOMetadata metadata = iw.getDefaultImageMetadata(new ImageTypeSpecifier(src), iwp);

            configure(metadata, delayTimes[i], i);

            IIOImage ii = new IIOImage(src, null, metadata);

            iw.writeToSequence(ii, null);

        }

        iw.endWriteSequence();

        ios.close();

    }
}