package net.coinshome.coinvision.controller;

import net.coinshome.coinvision.web.CoinRecognitionService;
import net.coinshome.coinvision.web.PredictionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

@RestController
public class ImageRestController {

    private static final Logger logger = LoggerFactory.getLogger(ImageRestController.class);

    @Autowired
    CoinRecognitionService coinRecognitionService;


    @PostMapping(value = "/search-by-url")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity handleFileUpload2(@RequestBody Map<String, String> parameters, ModelMap model) {

        try {

            URL imgUrl =new URL(parameters.get("importUrl"));

            BufferedImage bufferedImage = ImageIO.read(imgUrl);

            byte[] image = toByteArrayAutoClosable(bufferedImage, "jpg");

            List<PredictionInfo> searchResult = coinRecognitionService.recognizeCoin(image);

            return new ResponseEntity(searchResult, HttpStatus.OK);

        } catch (IOException e) {
            logger.error("Error during loading image", e);
        }

        return new ResponseEntity(HttpStatus.OK);

    }

    private static byte[] toByteArrayAutoClosable(BufferedImage image, String type) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()){
            ImageIO.write(image, type, out);
            return out.toByteArray();
        }
    }

}
