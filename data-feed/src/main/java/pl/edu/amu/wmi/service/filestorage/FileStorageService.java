package pl.edu.amu.wmi.service.filestorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {



    @Value("${app.file-storage-path:files}")
    private String fileStoragePath;
    
    private Path rootLocation;
    
    public void init() {
        try {
            rootLocation = Paths.get(fileStoragePath).toAbsolutePath().normalize();
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }
    
    public String storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file");
        }
        
        // generate a unique file name
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String storedFilename = UUID.randomUUID().toString() + fileExtension;
        
        // Create the storage path if it doesn't exist
        if (rootLocation == null) {
            init();
        }
        
        // copy the file to the storage location
        Path targetPath = rootLocation.resolve(storedFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        return storedFilename;
    }


    public Path loadFile(String filename) {
        return rootLocation.resolve(filename);
    }
    
    public void deleteFile(String filename) throws IOException {
        Path file = loadFile(filename);
        Files.deleteIfExists(file);
    }


}