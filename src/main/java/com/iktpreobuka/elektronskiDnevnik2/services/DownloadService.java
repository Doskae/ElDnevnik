package com.iktpreobuka.elektronskiDnevnik2.services;

import java.nio.file.Path;

public interface DownloadService {
	
	public Path download(String fileContent, String fileName);

}
