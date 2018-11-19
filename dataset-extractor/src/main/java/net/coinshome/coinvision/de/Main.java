package net.coinshome.coinvision.de;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Main {

    final static int MAX_COINS_GROUP_AMOUNT = 250000; // max amount of coin groups (labels) when processing SQL output
    final static int MAX_COINS_PER_GROUP = 25; // max amount of coin instances inside group when processing SQL output
    private static final int IMG_OUTPUT_WIDTH = 600;
    private static final int IMG_OUTPUT_HEIGHT = 300;
    private static String ALL_CSV_INPUT_FILE = null; // expected as command line argument
    private static String OUTPUT_DATASET_DIR = null; // expected as command line argument
    private static String CSV_SPLITER = ",";
    private static String ORIGINAL_IMG_DIR = null; // OUTPUT_DATASET_DIR + "/original-images";
    private static String LABELED_IMG_DIR = null; // OUTPUT_DATASET_DIR + "/labeled-images-${set}";
    private static String OUTPUT_DATASET_COPY_CSV_FILE = null; //OUTPUT_DATASET_DIR + "/dataset.csv"; // copy of input file

    public static void main(String[] args) {

        System.out.println("coins DNN dataset generation tool");
        if (null == args || args.length < 2) {
            System.out.println("2 input arguments are expected: 1 - path to CSV input file, 2 - path to dataset output directory ");
            System.out.println("  example");
            System.out.println("  java -jar build/libs/dataset-extractor-all.jar /home/spa/git/coin-vision/datasets/dataset-input.csv /home/spa/git/coin-vision/dataset");
            return;
        }

        ALL_CSV_INPUT_FILE = args[0];
        OUTPUT_DATASET_DIR = args[1];

        System.out.println("input CSV file: " + ALL_CSV_INPUT_FILE);
        System.out.println("out dataset dir: " + new File(OUTPUT_DATASET_DIR).getAbsolutePath());


        ORIGINAL_IMG_DIR = OUTPUT_DATASET_DIR + "/original-images";
        LABELED_IMG_DIR = OUTPUT_DATASET_DIR + "/labeled-images";
        OUTPUT_DATASET_COPY_CSV_FILE = OUTPUT_DATASET_DIR + "/dataset.csv"; // copy of input file


        File imgOutputDir = new File(ORIGINAL_IMG_DIR);
        if (!imgOutputDir.exists())
            imgOutputDir.mkdirs();

        File datasetCSVFile = new File(OUTPUT_DATASET_COPY_CSV_FILE);

        try {
            if (ALL_CSV_INPUT_FILE.startsWith("http")) {
                IOUtils.copyLarge(new URL(ALL_CSV_INPUT_FILE).openStream(), new FileOutputStream(datasetCSVFile));
            } else {
                FileUtils.copyFile(new File(ALL_CSV_INPUT_FILE), datasetCSVFile);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        downloadImagesFromCoinsHomeNet();

        createMachineLearningDataset();

        return;
    }


    /**
     * create file with labels
     * create distortion for images
     */
    private static void createMachineLearningDataset() {
        // training, validation datasets

        Set<String> labelRecords = Collections.synchronizedSet(new HashSet<String>());
        Set<String> tempRecords = new HashSet<String>();
        Map<String, Integer> labelCounts = Collections.synchronizedMap(new HashMap<>());

        try {

            File imgDir = new File(ORIGINAL_IMG_DIR);
            if (!imgDir.exists())
                imgDir.mkdirs();

            BufferedReader bufferedReader = new BufferedReader(new FileReader(OUTPUT_DATASET_COPY_CSV_FILE));
            String str = null;

            String previousCoinGroupId = null;
            int coinInstanceCounter = 0;
            int coinGroupCounter = 0;
            long rowCounter = 0;
            while ((str = bufferedReader.readLine()) != null) {
                rowCounter++;
                if (1 == rowCounter) // skip headers 
                    continue;

//                System.out.println(str);
                String[] lineArr = str.split(CSV_SPLITER);
//                String coinLabel = lineArr[1]; 
                String coinGroupId = lineArr[4]; // coinGroupID 

                if (!coinGroupId.equals(previousCoinGroupId)) {
                    // new coin group is started
                    coinInstanceCounter = 1;
                    coinGroupCounter++;

                } else {
                    coinInstanceCounter++;
                }

                if (coinGroupCounter > MAX_COINS_GROUP_AMOUNT)
                    break;

                previousCoinGroupId = coinGroupId;
//                if (coinInstanceCounter > MAX_COINS_PER_GROUP)
//                    continue;

                String datasetType = (coinInstanceCounter > MAX_COINS_PER_GROUP) ? "tst" : "tr";

                labelCounts.put(coinGroupId, coinInstanceCounter);

                tempRecords.add(datasetType + CSV_SPLITER + str);
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("processing " + tempRecords.size() + " entries ...");
        AtomicInteger atomicInteger = new AtomicInteger(0);

        Stream<String> parallelStream = tempRecords.parallelStream();
        parallelStream.forEach(tempRecord -> {
            atomicInteger.getAndIncrement();

            String[] lineArr = tempRecord.split(CSV_SPLITER);

            String datasetType = lineArr[0];
            String imageId = lineArr[3];
            String coinGroupId = lineArr[5];


            System.out.println("processing entry ... [" + atomicInteger.get() + " from " + tempRecords.size() + "]");

            File labelDir = new File(LABELED_IMG_DIR + "-" + datasetType + "/" + coinGroupId);
            if (!labelDir.exists())
                labelDir.mkdirs();

            labelRecords.add(coinGroupId);

            try {
                FileUtils.copyFile(new File(ORIGINAL_IMG_DIR + "/" + imageId + ".jpg"), new File(LABELED_IMG_DIR + "-" + datasetType + "/" + coinGroupId + "/" + imageId + ".jpg"));
//                FileUtils.copyFile(new File(ORIGINAL_IMG_DIR + "/" + imageId + ".jpg"), new File(LABELED_IMG_DIR + "/" + coinLabel + "/" + imageId + ".jpg"));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            generateArtificialImages(imageId, coinGroupId, datasetType);

        });

        return;
    }


    private static void generateArtificialImages(String imageId, String coinLabel, String datasetType) {

        File inImgFile = new File(ORIGINAL_IMG_DIR + "/" + imageId + ".jpg");
        String imgFileNamePrefix = imageId + "_";

        try {
            BufferedImage img = ImageIO.read(new FileInputStream(inImgFile));

            // extract coin image by coin image info
            BufferedImage extrCoinImg = img;
            List<BufferedImage> generatedImgs = null;

            // scale img - all output images should have the same dimension (width/height)
            extrCoinImg = ImageGenerator.scaleImage(extrCoinImg, IMG_OUTPUT_WIDTH, IMG_OUTPUT_HEIGHT);

            generatedImgs = ImageGenerator.generate(extrCoinImg, "tr".equals(datasetType) ? true : false);

            for (int imgIdx = 0; imgIdx < generatedImgs.size(); imgIdx++) {
                BufferedImage genImg = generatedImgs.get(imgIdx);
                String generatedImageFileName = imgFileNamePrefix + (imgIdx + 1) + ".jpg";
                File genImgFile = new File(LABELED_IMG_DIR + "-" + datasetType + "/" + coinLabel + "/", generatedImageFileName);
//                File genImgFile = new File(LABELED_IMG_DIR + "/" + coinLabel + "/", generatedImageFileName);
                ImageIO.write(genImg, "jpg", new FileOutputStream(genImgFile));
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return;
    }

    /**
     * download images from www.coinshome.net
     */
    private static void downloadImagesFromCoinsHomeNet() {

        List<String> imgList = new ArrayList<String>();

        AtomicInteger atomicInteger = new AtomicInteger(0);

        try {

            BufferedReader br = new BufferedReader(new FileReader(OUTPUT_DATASET_COPY_CSV_FILE));
            String str = null;

            int rowCounter = 0;
            while ((str = br.readLine()) != null) {
                rowCounter++;
                if (1 == rowCounter) // skip headers 
                    continue;

//                System.out.println(str);
                String[] lineArr = str.split(CSV_SPLITER);
                String imageId = lineArr[2];

                imgList.add(imageId);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("downloading " + imgList.size() + " images ...");

        Stream<String> parallelStream = imgList.parallelStream();

        parallelStream.forEach(imageId -> {
            atomicInteger.getAndIncrement();
            File inImgFile = new File(ORIGINAL_IMG_DIR, imageId + ".jpg");
            if (!inImgFile.exists() || inImgFile.length() == 0) {
                System.out.println("downloading " + imageId + ".jpg ... [" + atomicInteger.get() + " from " + imgList.size() + "]");
                CrawlerUtils.downloadImageFromCoinshomeNetSite(imageId, inImgFile);
            }
        });

        return;
    }

}
