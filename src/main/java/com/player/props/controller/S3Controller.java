package com.player.props.controller;

import java.io.FileWriter;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

import javax.persistence.criteria.CriteriaBuilder.In;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.player.props.model.response.UploadResponse;
import com.player.props.service.impl.S3ServiceImpl;
import com.player.props.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class S3Controller {

    @Autowired
    S3ServiceImpl s3Service;    

    @PostMapping("/upload-prize-picks-json")
    public ResponseEntity<UploadResponse> sendPrizePicksJson(@RequestBody String requestBody) throws Exception {        
        DateUtil.prizePickLastUpdated = Instant.now();
        // log.info("Difference between last update is {}", Duration.between(before, DateUtil.lastProjectionsUpdated).toMillis());
        saveJsonToFile(requestBody, "nba-prize-picks.json");
        String status = s3Service.uploadFile(Paths.get("nba-prize-picks.json"), "prize-picks");
        UploadResponse uploadResponse = UploadResponse.builder()
                .status(status)
                .lastUpdated(DateUtil.formatInstant(DateUtil.prizePickLastUpdated))
                .build();
        return new ResponseEntity<>(uploadResponse, HttpStatus.OK);
    }

    @PostMapping("/upload-underdog-json")
    public ResponseEntity<UploadResponse> sendUnderdogJson(@RequestBody String requestBody) throws Exception {
        DateUtil.underdogLastUpdated = Instant.now();
        // log.info("Difference between last update is {}", Duration.between(before, DateUtil.lastProjectionsUpdated).toMillis());
        saveJsonToFile(requestBody, "nba-underdog.json");
        String status = s3Service.uploadFile(Paths.get("nba-underdog.json"), "underdog");        
        UploadResponse uploadResponse = UploadResponse.builder()
                .status(status)
                .lastUpdated(DateUtil.formatInstant(DateUtil.underdogLastUpdated))
                .build();
        return new ResponseEntity<>(uploadResponse, HttpStatus.OK);
    }

    @GetMapping("/download-json-file")
    public ResponseEntity<ByteArrayResource> downloadFile(@RequestParam String fileName) {
        byte[] data = s3Service.downloadFile(fileName, "prize-picks");
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @GetMapping("/delete-json-file")
    public ResponseEntity<String> deleteFile(@RequestParam String fileName) {
        return new ResponseEntity<>(s3Service.deleteFile(fileName, "prize-picks"), HttpStatus.OK);
    }

    private void saveJsonToFile(String requestBody, String string) {
        try {FileWriter fileWriter = new FileWriter(string);
            fileWriter.write(requestBody);
            fileWriter.close();
        } catch (Exception e) {
            log.error("Error saving json to file: {}", e.getMessage());
        }
    }
}