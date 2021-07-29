package com.iktpreobuka.elektronskiDnevnik2.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.elektronskiDnevnik2.security.Views;

@Service
public class DownloadServiceImpl implements DownloadService {

	private static final String EXPORT_DIRECTORY = "F:\\workspace\\elektronskiDnevnik2\\logs";

	@JsonView(Views.AdminView.class)
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Override
	public Path download(String fileContent, String fileName) {

		Path filePath = Paths.get(EXPORT_DIRECTORY, fileName);
		try {
			Path exportedFilePath = Files.write(filePath, fileContent.getBytes(), StandardOpenOption.CREATE);
			return exportedFilePath;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
}
