package com.iktpreobuka.elektronskiDnevnik2.controllers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.elektronskiDnevnik2.security.Views;
import com.iktpreobuka.elektronskiDnevnik2.services.DownloadService;

@Controller

public class DownloadController {

	@JsonView(Views.AdminView.class)
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Autowired
	DownloadService downloadService;

	@RequestMapping("/download")
	public String download() {
		return "download";
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping("/download/log")
	public ResponseEntity<?> downloadLogg() throws IOException {
		logger.info("Download of a log started");
		String fileName = "spring-boot-logging.log";
		String fileContent = "Log aplikacije \n";

		try {
			Path exportedPath = downloadService.download(fileContent, fileName);
			logger.info("Log downloaded");

			// Download file with byte[]
			byte[] expotedFileData = Files.readAllBytes(exportedPath);

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
					.contentType(MediaType.TEXT_PLAIN).contentLength(expotedFileData.length).body(expotedFileData);
		} catch (IOException e) {
			logger.error("Log failed to download " + e.toString());
			e.printStackTrace();
			return new ResponseEntity<>("Log failed to download " + e.toString(), HttpStatus.BAD_REQUEST);
		}

	}
}
