package net.coinshome.coinvision.controller;

import net.coinshome.coinvision.web.CoinRecognitionService;
import net.coinshome.coinvision.web.PredictionInfo;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.*;

@Controller
public class ImageCoinSearchController {

    private static final Logger logger = LoggerFactory.getLogger(ImageCoinSearchController.class);

    @Autowired
    CoinRecognitionService coinRecognitionService;

    @GetMapping("/")
    public String index(ModelMap model) {
        return "home";
    }

    @PostMapping("/search-from-mobile")
    public @ResponseBody
    Map handleFileUpload1(@RequestParam("image") MultipartFile file, ModelMap model) {

        List<PredictionInfo> searchResult = new ArrayList<>();
        try {
            byte[] imageBytes = IOUtils.toByteArray(file.getInputStream());
            searchResult = coinRecognitionService.recognizeCoin(imageBytes);
        } catch (IOException e) {
            logger.error("Error during loading image", e);
        }
        Map resp = new HashMap();
        resp.put("predictionStatus", "done");
        resp.put("coinGroups", searchResult);

        return resp;
    }

    @PostMapping("/search-by-picture")
    public ModelAndView handleFileUpload2(@RequestParam("file") MultipartFile file, ModelMap model) {
        try {
            byte[] imageBytes = IOUtils.toByteArray(file.getInputStream());

            byte[] encoded = Base64.getEncoder().encode(imageBytes);

            model.put("originImage", new String(encoded));

            List<PredictionInfo> searchResult = coinRecognitionService.recognizeCoin(imageBytes);
            model.put("searchResult", searchResult);

        } catch (IOException e) {
            logger.error("Error during loading image", e);
        }

        return new ModelAndView("search_result", model);
    }

}
