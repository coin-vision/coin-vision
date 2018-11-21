package net.coinshome.coinvision.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CatalogConfigLoader {

    public static final String CSV_SPLITER = ",";
    private static final String DNN_GRAPH_FILE = "graph.pb";
    private static final String DNN_LABELS_FILE = "labels.txt";
    private static final String ALL_CSV_INPUT_FILE = "dataset.csv";
    private static final Logger logger = LoggerFactory.getLogger(CatalogConfigLoader.class);
    private String dnnGraphFileStr = null;
    private String dnnLabelsFileStr = null;
    private String allCsvInputFileStr = null;
    private Map<Integer, String> labelIdx2NameMap = null;
    private Map<String, String> label2CoinIdMap = null;

    public CatalogConfigLoader(AppConfig appConfig) {
        dnnGraphFileStr = appConfig.getDnnRootDir() + "/" + DNN_GRAPH_FILE;
        dnnLabelsFileStr = appConfig.getDnnRootDir() + "/" + DNN_LABELS_FILE;
        allCsvInputFileStr = appConfig.getDnnRootDir() + "/" + ALL_CSV_INPUT_FILE;
    }

    public Map<String, String> loadLabel2CoinIdMap() {

        if (label2CoinIdMap == null) {
            // load to cache
            label2CoinIdMap = new HashMap<>();

            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(allCsvInputFileStr))) {
                String str = null;
                while ((str = bufferedReader.readLine()) != null) {
                    String[] lineArr = str.split(CSV_SPLITER);
                    String imageId = lineArr[2];
                    String coinGroupId = lineArr[4];

                    label2CoinIdMap.put(coinGroupId, imageId);
                }
            } catch (Exception e) {
                logger.error("Error loading loadLabel2CoinIdMap", e);
            }
        }

        return label2CoinIdMap;
    }


    public Map<Integer, String> getLabelIdx2NameMap() {

        if (labelIdx2NameMap == null) {
            try {
                List<String> labels1 = Files.readAllLines(Paths.get(dnnLabelsFileStr), Charset.forName("UTF-8"));

                labelIdx2NameMap = new HashMap<>(labels1.size());

                for (String str : labels1) {
                    String[] arr = str.split(":");
                    labelIdx2NameMap.put(Integer.parseInt(arr[0]), arr[1]);
                }
            } catch (Exception e) {
                logger.error("Error loading LabelIdx2NameMap", e);
            }
        }

        return labelIdx2NameMap;
    }

    public byte[] readGraphDef() throws IOException {
        return Files.readAllBytes(Paths.get(dnnGraphFileStr));
    }

}
