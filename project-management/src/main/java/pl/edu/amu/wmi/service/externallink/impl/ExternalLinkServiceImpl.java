package pl.edu.amu.wmi.service.externallink.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.amu.wmi.dao.ExternalLinkDAO;
import pl.edu.amu.wmi.dao.ExternalLinkDefinitionDAO;
import pl.edu.amu.wmi.entity.ExternalLink;
import pl.edu.amu.wmi.entity.ExternalLinkDefinition;
import pl.edu.amu.wmi.exception.externallink.ExternalLinkException;
import pl.edu.amu.wmi.model.externallink.ExternalLinkDTO;
import pl.edu.amu.wmi.service.externallink.ExternalLinkService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExternalLinkServiceImpl implements ExternalLinkService {

    private final ExternalLinkDAO externalLinkDAO;

    private final ExternalLinkDefinitionDAO externalLinkDefinitionDAO;

    @Value("${app.file-storage.path:${java.io.tmpdir}/external-links}")
    private String fileStoragePath;

    @Autowired
    public ExternalLinkServiceImpl(ExternalLinkDAO externalLinkDAO, ExternalLinkDefinitionDAO externalLinkDefinitionDAO) {
        this.externalLinkDAO = externalLinkDAO;
        this.externalLinkDefinitionDAO = externalLinkDefinitionDAO;
    }

    @Transactional
    @Override
    public Set<ExternalLink> createEmptyExternalLinks(String studyYear) {
        Set<ExternalLinkDefinition> definitionEntities = externalLinkDefinitionDAO.findAllByStudyYear_StudyYear(studyYear);
        Set<ExternalLink> externalLinkEntities = new HashSet<>();

        definitionEntities.forEach(entity ->
                externalLinkEntities.add(createEmptyExternalLink(entity))
        );
        return externalLinkEntities;
    }

    private ExternalLink createEmptyExternalLink(ExternalLinkDefinition definition) {
        ExternalLink externalLink = new ExternalLink();
        externalLink.setExternalLinkDefinition(definition);
        externalLink.setUrl(null);
        return externalLinkDAO.save(externalLink);
    }

    @Transactional
    @Override
    public List<ExternalLink> updateExternalLinks(Set<ExternalLinkDTO> externalLinks) {
        Set<ExternalLink> externalLinkEntities = new HashSet<>();

        externalLinks.forEach(externalLinkDto -> {
            ExternalLink externalLink = externalLinkDAO.findById(
                    Long.valueOf(externalLinkDto.getId())).orElseThrow(()
                    -> new ExternalLinkException(MessageFormat.format("External link with id: {0} not found.", externalLinkDto.getId())));
            
            // Determine if this should be an external URL or file upload placeholder
            if (externalLinkDto.getContentType() != null && !externalLinkDto.getContentType().trim().isEmpty()) {
                // This indicates a file upload request - set as internal type but URL will be handled by upload endpoint
                externalLink.setLinkType(ExternalLink.LinkType.INTERNAL);
                externalLink.setContentType(externalLinkDto.getContentType());
                // Keep existing file info if any, URL will be updated by file upload endpoint
            } else {
                // Regular URL update
                externalLink.setLinkType(ExternalLink.LinkType.EXTERNAL);
                externalLink.setUrl(externalLinkDto.getUrl());
                // Clear file-related fields for external links
                externalLink.setFilePath(null);
                externalLink.setOriginalFileName(null);
                externalLink.setContentType(null);
                externalLink.setFileSize(null);
            }
            
            externalLinkEntities.add(externalLink);
        });

        return externalLinkDAO.saveAll(externalLinkEntities);
    }

    @Override
    public Set<String> findDefinitionHeadersByStudyYear(String studyYear) {
        return externalLinkDefinitionDAO.findAllByStudyYear_StudyYear(studyYear)
                .stream()
                .map(ExternalLinkDefinition::getColumnHeader)
                .collect(Collectors.toSet());
    }

    @Transactional
    @Override
    public ExternalLink storeInternalFile(Long definitionId, MultipartFile file) throws IOException {
        ExternalLinkDefinition definition = externalLinkDefinitionDAO.findById(definitionId)
                .orElseThrow(() -> new ExternalLinkException(
                        MessageFormat.format("External link definition with id: {0} not found.", definitionId)));

        // Create storage directory if it doesn't exist
        Path storageDir = Paths.get(fileStoragePath);
        Files.createDirectories(storageDir);

        // Generate unique filename
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName != null && originalFileName.contains(".") 
                ? originalFileName.substring(originalFileName.lastIndexOf(".")) 
                : "";
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        Path filePath = storageDir.resolve(uniqueFileName);

        // Save file to storage
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Create or update external link entity
        ExternalLink externalLink = new ExternalLink();
        externalLink.setExternalLinkDefinition(definition);
        externalLink.setLinkType(ExternalLink.LinkType.INTERNAL);
        externalLink.setFilePath(filePath.toString());
        externalLink.setOriginalFileName(originalFileName);
        externalLink.setContentType(file.getContentType());
        externalLink.setFileSize(file.getSize());

        return externalLinkDAO.save(externalLink);
    }

    @Override
    public byte[] getInternalFileContent(Long externalLinkId) throws IOException {
        ExternalLink externalLink = externalLinkDAO.findById(externalLinkId)
                .orElseThrow(() -> new ExternalLinkException(
                        MessageFormat.format("External link with id: {0} not found.", externalLinkId)));

        if (externalLink.getLinkType() != ExternalLink.LinkType.INTERNAL || externalLink.getFilePath() == null) {
            throw new ExternalLinkException("External link is not an internal file or file path is missing.");
        }

        Path filePath = Paths.get(externalLink.getFilePath());
        if (!Files.exists(filePath)) {
            throw new ExternalLinkException("File not found on filesystem: " + externalLink.getFilePath());
        }

        return Files.readAllBytes(filePath);
    }

    @Transactional
    @Override
    public void deleteInternalFile(Long externalLinkId) throws IOException {
        ExternalLink externalLink = externalLinkDAO.findById(externalLinkId)
                .orElseThrow(() -> new ExternalLinkException(
                        MessageFormat.format("External link with id: {0} not found.", externalLinkId)));

        if (externalLink.getLinkType() == ExternalLink.LinkType.INTERNAL && externalLink.getFilePath() != null) {
            Path filePath = Paths.get(externalLink.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Deleted file: {}", externalLink.getFilePath());
            }
        }

        // Reset file-related fields but keep the entity
        externalLink.setLinkType(ExternalLink.LinkType.EXTERNAL);
        externalLink.setFilePath(null);
        externalLink.setOriginalFileName(null);
        externalLink.setContentType(null);
        externalLink.setFileSize(null);
        externalLink.setUrl(null);

        externalLinkDAO.save(externalLink);
    }

    @Transactional
    @Override
    public ExternalLink updateExternalLinkWithFile(Long externalLinkId, MultipartFile file) throws IOException {
        ExternalLink externalLink = externalLinkDAO.findById(externalLinkId)
                .orElseThrow(() -> new ExternalLinkException(
                        MessageFormat.format("External link with id: {0} not found.", externalLinkId)));

        // Delete existing file if present
        if (externalLink.getLinkType() == ExternalLink.LinkType.INTERNAL && externalLink.getFilePath() != null) {
            Path existingFilePath = Paths.get(externalLink.getFilePath());
            if (Files.exists(existingFilePath)) {
                Files.delete(existingFilePath);
                log.info("Deleted existing file: {}", externalLink.getFilePath());
            }
        }

        // Create storage directory if it doesn't exist
        Path storageDir = Paths.get(fileStoragePath);
        Files.createDirectories(storageDir);

        // Generate unique filename
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName != null && originalFileName.contains(".") 
                ? originalFileName.substring(originalFileName.lastIndexOf(".")) 
                : "";
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        Path filePath = storageDir.resolve(uniqueFileName);

        // Save file to storage
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Update external link entity
        externalLink.setLinkType(ExternalLink.LinkType.INTERNAL);
        externalLink.setFilePath(filePath.toString());
        externalLink.setOriginalFileName(originalFileName);
        externalLink.setContentType(file.getContentType());
        externalLink.setFileSize(file.getSize());
        externalLink.setUrl(null);

        return externalLinkDAO.save(externalLink);
    }

    @Override
    public ExternalLink findById(Long externalLinkId) {
        return externalLinkDAO.findById(externalLinkId)
                .orElseThrow(() -> new ExternalLinkException(
                        MessageFormat.format("External link with id: {0} not found.", externalLinkId)));
    }

}
