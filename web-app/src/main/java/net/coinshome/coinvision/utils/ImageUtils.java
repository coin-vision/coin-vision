package net.coinshome.coinvision.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {

    public static final int partWidth = 299; // inception preferred size
    public static final int partHeight = 299; // inception preferred size

    public static BufferedImage scaleImage(BufferedImage image, int newWidth, int newHeight) {
        if (null == image)
            return null;

        int scaledWidth = newWidth;
        int scaledHeight = newHeight;

        // Make sure the aspect ratio is maintained, so the image is not skewed
        double scaledRatio = (double) scaledWidth / (double) scaledHeight;
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);

        double imageRatio = (double) imageWidth / (double) imageHeight;
        if (scaledRatio < imageRatio) {
            scaledHeight = (int) (scaledWidth / imageRatio);
        } else {
            scaledWidth = (int) (scaledHeight * imageRatio);
        }

        // Draw the scaled image
        BufferedImage thumbImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = thumbImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // write white background (image can be transparent)
        graphics2D.setColor(Color.WHITE);
        graphics2D.fillRect(0, 0, newWidth, newHeight);
        graphics2D.drawImage(image, (newWidth - scaledWidth) / 2, (newHeight - scaledHeight) / 2, scaledWidth, scaledHeight, null);

        return sameImage(thumbImage);
    }

    private static BufferedImage sameImage(BufferedImage image) {
        if (null == image)
            return null;

        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);

        // Draw the scaled image
        BufferedImage thumbImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = thumbImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // write white background (image can be transparent)
        graphics2D.setColor(Color.WHITE);
        graphics2D.fillRect(0, 0, imageWidth, imageHeight);
        graphics2D.drawImage(image, 0, 0, imageWidth, imageHeight, null);

        return thumbImage;
    }

    public static BufferedImage convert2grayScale(BufferedImage srcImage) {
        // convert to grayscale
        BufferedImage image = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = image.getGraphics();
        g.drawImage(srcImage, 0, 0, null);
        g.dispose();
        return image;
    }


    public static byte[][] cropImage(byte[] imageByte, int scaleToSize, int step) throws IOException {

        InputStream in = new ByteArrayInputStream(imageByte);

        BufferedImage originalImage = ImageIO.read(in);

        int size = (originalImage.getWidth() < originalImage.getHeight()) ? originalImage.getWidth() : originalImage.getHeight();
        if (size > scaleToSize)
            originalImage = scaleImage(originalImage, scaleToSize, scaleToSize);

        int batchSize = 0;
        for (int w_offset = 0; w_offset < originalImage.getWidth(); w_offset = w_offset + step) {
            for (int h_offset = 0; h_offset < originalImage.getHeight(); h_offset = h_offset + step) {
                if (w_offset + partWidth > originalImage.getWidth() || h_offset + partHeight > originalImage.getHeight()) {
                    continue;
                }
                batchSize++;
            }
        }

        byte[][] result = new byte[batchSize][];
        int imageId = 0;

        for (int w_offset = 0; w_offset < originalImage.getWidth(); w_offset = w_offset + step) {
            for (int h_offset = 0; h_offset < originalImage.getHeight(); h_offset = h_offset + step) {
                if (w_offset + partWidth > originalImage.getWidth() || h_offset + partHeight > originalImage.getHeight()) {
                    continue;
                }
                BufferedImage subImgage = originalImage.getSubimage(w_offset, h_offset, partWidth, partHeight);
                try {
                    result[imageId] = toByteArrayAutoClosable(subImgage);
                } catch (IOException e) {
                    imageId--;
                    e.printStackTrace();
                }
                imageId++;
            }
        }

        return result;
    }

    public static byte[] toByteArrayAutoClosable(BufferedImage image) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", out);
            return out.toByteArray();
        }
    }

}
