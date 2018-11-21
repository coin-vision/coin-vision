package net.coinshome.coinvision.web;

import net.coinshome.coinvision.utils.ImageUtils;
import net.coinshome.coinvision.utils.TensorflowUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tensorflow.Tensor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.*;

@Service
public class CoinRecognitionService {


    private static final Logger logger = LoggerFactory.getLogger(CoinRecognitionService.class);
    private final int BEST_MATCH_AMOUNT = 10; // how many results with highest probability return
    private final int IMAGE_CROPING_STEP = 50;
    private final int IMAGE_CROPING_SCALE_TO_SIZE = 500; // resize to if original image too large (to avoid huge amount of steps)

    @Autowired
    private CatalogConfigLoader catalogConfigLoader;

    public CoinRecognitionService() {
    }

    public List<PredictionInfo> recognizeCoin(byte[] image) {

        List<byte[]> imagesList = new ArrayList<byte[]>();

        { // add original and grayscaled image scaled down to Inception frame
            try {
                BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(image));
                BufferedImage origImageResized = ImageUtils.scaleImage(originalImage, ImageUtils.partWidth, ImageUtils.partHeight);
                imagesList.add(ImageUtils.toByteArrayAutoClosable(origImageResized));


                BufferedImage origImageResizedGrayscale = ImageUtils.convert2grayScale(originalImage);
                imagesList.add(ImageUtils.toByteArrayAutoClosable(origImageResizedGrayscale));

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


        // scan Inception frame (299x299) over the image with step and crop image samples
        try {

            byte[][] croppedImages = ImageUtils.cropImage(image, IMAGE_CROPING_SCALE_TO_SIZE, IMAGE_CROPING_STEP);

            if (null != croppedImages && croppedImages.length > 0)
                for (int i = 0; i < croppedImages.length; i++)
                    imagesList.add(croppedImages[i]);

        } catch (Exception e1) {
            logger.error("Error cropping image using original", e1);
        }

        // TOOD ?? add grayscale images ??

        // list to arrays
        byte[][] imagesForDNN = new byte[imagesList.size()][];
        for (int i = 0; i < imagesList.size(); i++)
            imagesForDNN[i] = imagesList.get(i);

        List<PredictionInfo> result = searchCatalog(imagesForDNN);

        return result;
    }


    private List<PredictionInfo> searchCatalog(byte[][] imagesBytes) {
        long start = System.currentTimeMillis();


        List<PredictionInfo> bestMatchedCoinIds = new ArrayList<>();


        try (Tensor<Float> image = TensorflowUtils.preprocessImage(imagesBytes)) {

            logger.info("Starting recognition ... ");

            byte[] graphDef = catalogConfigLoader.readGraphDef();

            float[][] labelProbabilities = TensorflowUtils.executeDNNGraph(graphDef, image);


            Map<Integer, String> labelIdx2NameMap = catalogConfigLoader.getLabelIdx2NameMap();

            Map<String, String> label2CoinIdMap = catalogConfigLoader.loadLabel2CoinIdMap();


            int tempMatchAmount = BEST_MATCH_AMOUNT;

            List<PredictionInfo> tempMatchedCoinIds = new ArrayList<>(labelProbabilities.length * tempMatchAmount);

            String[] coinIds = new String[tempMatchAmount]; // is used to resolve images
            for (int j = 0; j < labelProbabilities.length; j++) {
                for (int i = 0; i < tempMatchAmount; i++) {
                    int bestLabelIdx = maxIndex(labelProbabilities[j]);
                    String coinId = labelIdx2NameMap.get(bestLabelIdx);
                    String imageId = label2CoinIdMap.get(coinId);

                    logger.debug("BEST MATCH: {} ({} likely) coinshome.net coinGroupId: {}", coinId, labelProbabilities[j][bestLabelIdx] * 100f, coinId);

                    coinIds[i] = coinId;

                    PredictionInfo predictionInfo = new PredictionInfo(coinId, imageId, labelProbabilities[j][bestLabelIdx]);
                    logger.debug(predictionInfo.toString());

                    tempMatchedCoinIds.add(predictionInfo);
                    labelProbabilities[j][bestLabelIdx] = 0; // reset to 0, to find another max
                }
            }


            // sort top results
            Collections.sort(tempMatchedCoinIds, Collections.reverseOrder(new SortbyProbability()));

            LinkedHashMap<String, PredictionInfo> map = new LinkedHashMap<>(tempMatchAmount);
            for (PredictionInfo predictionInfo : tempMatchedCoinIds) {
                if (!map.containsKey(predictionInfo.getCoinId())) {
                    map.put(predictionInfo.getCoinId(), predictionInfo);
                }
                if (map.size() >= tempMatchAmount) {
                    break;
                }
            }
            int i = 0;
            for (PredictionInfo predictionInfo : map.values()) {
                logger.info(i++ + ": " + predictionInfo.getCoinId() + " prob: " + predictionInfo.getProbability());
                bestMatchedCoinIds.add(predictionInfo);
                if (i > tempMatchAmount) {
                    break;
                }
            }

            for (PredictionInfo predictionInfo : tempMatchedCoinIds) {
                System.out.println(predictionInfo.getCoinId() + " prob: " + predictionInfo.getProbability());
            }
            // after we start using async mode - images are resolved by coinId during pull from DB
            // we don't need to resolve best images here

        } catch (Exception e) {
            logger.error("Error during image search", e);
        }
        logger.info("Recognition is completed in {} ms", (System.currentTimeMillis() - start));
        logger.info("===================");
        return bestMatchedCoinIds;
    }


    /**
     * @param probabilities
     * @return
     */
    private int maxIndex(float[] probabilities) {
        int best = 0;
        for (int i = 1; i < probabilities.length; ++i) {
            if (probabilities[i] > probabilities[best]) {
                best = i;
            }
        }
        return best;
    }


    class SortbyProbability implements Comparator<PredictionInfo> {
        public int compare(PredictionInfo a, PredictionInfo b) {
            return Float.compare(a.getProbability(), b.getProbability());
        }
    }


}