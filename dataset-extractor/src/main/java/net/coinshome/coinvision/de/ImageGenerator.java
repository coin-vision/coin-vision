package net.coinshome.coinvision.de;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

public class ImageGenerator {

    private static int MAX_ANGLE_DEVIATION = 90;
    private static int ANGLE_DEVIATION_STEP = 10;
    private static int INCEPTION_PREFERRED_WIDTH = 299; // inception preferred size
    private static int INCEPTION_PREFERRED_HEIGHT = 299; // inception preferred size

    public static List<BufferedImage> generate(BufferedImage srcImg, boolean isTrainingDataset) throws Exception {

        List<BufferedImage> outImages = new ArrayList<BufferedImage>();

        // extract left
        BufferedImage leftImg = new BufferedImage(INCEPTION_PREFERRED_WIDTH, INCEPTION_PREFERRED_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D leftGr = leftImg.createGraphics();
        leftGr.drawImage(srcImg, 0, 0, INCEPTION_PREFERRED_WIDTH, INCEPTION_PREFERRED_HEIGHT, 0, 0, INCEPTION_PREFERRED_WIDTH, INCEPTION_PREFERRED_HEIGHT, null);

        BufferedImage grayScaleLeftImg = null;
        BufferedImage grayScaleRightImg = null;

        {
            BufferedImage singleSideImg = sameImage(leftImg);
            outImages.add(singleSideImg);
            if (isTrainingDataset)
                rotate(singleSideImg, outImages);

            grayScaleLeftImg = convert2grayScale(singleSideImg);
            outImages.add(grayScaleLeftImg);
            if (isTrainingDataset)
                rotate(grayScaleLeftImg, outImages);

//            singleSideImg = scaleImage2(deepCopy(leftImg), (int)((double)INCEPTION_PREFERRED_WIDTH/1.5), (int)((double)INCEPTION_PREFERRED_HEIGHT/1.5)); // scale down 30%
//            outImages.add(singleSideImg);
//            rotate(singleSideImg, outImages);
        }


//        outImages.add(scaleImage(deepCopy(leftImg), 200, 200));

        // extract right
        BufferedImage rightImg = new BufferedImage(INCEPTION_PREFERRED_WIDTH, INCEPTION_PREFERRED_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D rightGr = rightImg.createGraphics();
        rightGr.drawImage(srcImg, 0, 0, INCEPTION_PREFERRED_WIDTH, INCEPTION_PREFERRED_HEIGHT, INCEPTION_PREFERRED_WIDTH, 0, 2 * INCEPTION_PREFERRED_WIDTH, INCEPTION_PREFERRED_HEIGHT, null);

        {
            BufferedImage singleSideImg = sameImage(rightImg);
            outImages.add(singleSideImg);
            if (isTrainingDataset)
                rotate(singleSideImg, outImages);

            grayScaleRightImg = convert2grayScale(singleSideImg);
            outImages.add(grayScaleRightImg);
            if (isTrainingDataset)
                rotate(grayScaleRightImg, outImages);

//            singleSideImg = scaleImage2(deepCopy(rightImg), (int)((double)INCEPTION_PREFERRED_WIDTH/1.5), (int)((double)INCEPTION_PREFERRED_HEIGHT/1.5)); // scale down 30%
//            outImages.add(singleSideImg);
//            rotate(singleSideImg, outImages);
        }

/*        
        // swap
        List<BufferedImage> swapImages = swap(leftImg, rightImg);
        leftImg = swapImages.get(0);
        rightImg = swapImages.get(1);

        // compose parts to one image
        BufferedImage swapedImg = compose(leftImg, rightImg, deepCopy(srcImg));
        outImages.add(swapedImg);

        // compose multiple images using various rotations
        rotate(srcImg, leftImg, rightImg, outImages, 9999); // , (maxAmount - 2) / 2
        rotate(srcImg, rightImg, leftImg, outImages, 9999); // (maxAmount - 2) / 2

        rotate(srcImg, grayScaleLeftImg, grayScaleRightImg, outImages, 9999); // , (maxAmount - 2) / 2
        rotate(srcImg, grayScaleRightImg, grayScaleLeftImg, outImages, 9999); // (maxAmount - 2) / 2
//*/
        return outImages;

    }


    private static void rotate(BufferedImage leftImg, List<BufferedImage> outImages) {
        int angleDeviationStep = ANGLE_DEVIATION_STEP;

        for (int i = angleDeviationStep; i < MAX_ANGLE_DEVIATION; i = i + angleDeviationStep) {
            outImages.add(deepCopy(rotate(deepCopy(leftImg), i * Math.PI / 180)));
            outImages.add(deepCopy(rotate(deepCopy(leftImg), -i * Math.PI / 180)));
        }

        return;
    }

    private static BufferedImage rotate(BufferedImage inImg, double angle) {

        BufferedImage outImg = deepCopy(inImg);

        Graphics2D gr = (Graphics2D) outImg.getGraphics();
        // rotate in center
        gr.rotate(angle, inImg.getWidth() / 2, inImg.getWidth() / 2);

        gr.drawImage(inImg, 0, 0, inImg.getWidth(), inImg.getWidth(), null);

        gr.dispose();
        return outImg;
    }

    /**
     * because BufferedImage result = new BufferedImage(srcImg.getWidth(),
     * srcImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
     * <p>
     * create pink image (java bug)
     *
     * @param bi
     * @return
     */
    private static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static BufferedImage scaleImage(BufferedImage image, int newWidth, int newHeight) {
        if (null == image)
            return null;

        int scaledWidth = newWidth;
        int scaledHeight = newHeight;

        // Make sure the aspect ratio is maintained, so the image is not
        // skewed
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
        // graphics2D.drawImage(image, (newWidth - imageWidth)/2, (newHeight -
        // imageHeight)/2, imageWidth, imageHeight, null);

        return thumbImage;
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

    private static BufferedImage convert2grayScale(BufferedImage srcImage)
            throws Exception {

        // convert to grayscale
        BufferedImage image = new BufferedImage(srcImage.getWidth(),
                srcImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = image.getGraphics();

        g.drawImage(srcImage, 0, 0, null);
        g.dispose();

        return image;

    }
}
