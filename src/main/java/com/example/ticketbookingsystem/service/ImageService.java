package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.exception.UploadImageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

/**
 * Service class for managing image upload and retrieval.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    @Value("${image.service.url}")
    private String IMAGE_SERVICE_URL;

    private final RestTemplate restTemplate;

    /**
     * Uploads an image to the external image service.
     *
     * @param file the image file to be uploaded
     * @throws UploadImageException if an error occurs during the upload process
     */
    public void uploadImage(MultipartFile file) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    IMAGE_SERVICE_URL + "/upload", requestEntity, String.class
            );
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new IOException("Failed to upload image, received status code: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP error occurred while uploading image");
            throw new UploadImageException(e);
        } catch (ResourceAccessException e) {
            log.error("Resource access error occurred while uploading image");
            throw new UploadImageException(e);
        } catch (IOException e) {
            log.error("I/O error occurred while uploading image");
            throw new UploadImageException(e);
        }
    }

    /**
     * Fetches an image by its filename from the external image service.
     *
     * @param fileName the name of the image file to be fetched
     * @return a byte array containing the image content, or null if not found
     */
    public byte[] fetchImage(String fileName) {
        byte[] imageBytes = null;
        try {
            String url = IMAGE_SERVICE_URL + "/" + fileName;
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody.containsKey("image")) {
                    String imageBase64 = (String) responseBody.get("image");
                    imageBytes = Base64.getDecoder().decode(imageBase64);
                }
            }
        } catch (RestClientException e) {
            log.error("Error fetching image from ImageService", e);
        }
        return imageBytes;
    }

    /**
     * Deletes an image by its filename from the external image service.
     *
     * @param filename the name of the image file to be deleted
     */
    public void deleteImageFromDb(String filename) {
        restTemplate.delete(IMAGE_SERVICE_URL + "/" + filename);
    }
}