package com.example.diploma.service;

import com.example.diploma.config.AppConfig;
import com.example.diploma.data.response.CaptchaResponse;
import com.example.diploma.model.CaptchaCode;
import com.example.diploma.repository.CaptchaCodeRepository;
import com.github.cage.Cage;
import com.github.cage.GCage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;

@Service
public class CaptchaService {

    private final AppConfig appConfig;
    private final CaptchaCodeRepository captchaCodeRepository;
    private static int captchaHeight;
    private static int captchaWight;

    private static final Cage CAGE = new GCage();

    public CaptchaService(
        AppConfig appConfig,
        CaptchaCodeRepository captchaCodeRepository
    ){
        this.appConfig = appConfig;
        this.captchaCodeRepository = captchaCodeRepository;
        captchaHeight = appConfig.getCaptchaHeight();
        captchaWight = appConfig.getCaptchaWidth();
    }

    public CaptchaCode getCaptcha() {
        deleteOutdatedCaptchas(appConfig.getCaptchaHoursToBeUpdated());

        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setTime(LocalDateTime.now());
        captchaCode.setCode(generateCode(appConfig.getCaptchaLength()));
        captchaCode.setSecretCode(UUID.randomUUID().toString());
        return captchaCodeRepository.save(captchaCode);
    }

    private void deleteOutdatedCaptchas(int captchaHoursToBeUpdated) {
        final LocalDateTime timeToBeUpdated = LocalDateTime.now().minusHours(captchaHoursToBeUpdated);
        captchaCodeRepository.deleteLessThanTime(timeToBeUpdated);
    }

    private String generateCode(int length) {
        Random r = new Random();
        return r.ints(48, 122)
                .filter(i -> (i < 57 || i > 65) && (i < 90 || i > 97))
                .mapToObj(i -> (char) i)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();

    }

    public ResponseEntity<CaptchaResponse> getCaptchaResponse() {
        CaptchaResponse response = new CaptchaResponse();

        CaptchaCode captchaCode = getCaptcha();
        response.setImage("data:image/png;base64, ".concat(generateBase64Image(captchaCode.getCode())));
        response.setSecret(captchaCode.getSecretCode());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private static String generateBase64Image(String text) {
        String result = "";
        try {
            BufferedImage captchaImage = ImageIO.read(new ByteArrayInputStream(CAGE.draw(text)));
            captchaImage = resizeImage(captchaImage);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(captchaImage, "png", baos);
            baos.flush();
            result = new String(Base64.getEncoder().encode(baos.toByteArray()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static BufferedImage resizeImage(BufferedImage captchaImage) {
        final int width = captchaWight;
        final int height = captchaHeight;
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int widthStep = captchaImage.getWidth() / width;
        int heightStep = captchaImage.getHeight() / height;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = captchaImage.getRGB(x * widthStep, y * heightStep);
                newImage.setRGB(x, y, rgb);
            }
        }
        return newImage;
    }
}
