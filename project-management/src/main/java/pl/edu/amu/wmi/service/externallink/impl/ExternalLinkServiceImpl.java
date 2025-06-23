package pl.edu.amu.wmi.service.externallink.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.amu.wmi.dao.ExternalLinkDAO;
import pl.edu.amu.wmi.dao.ExternalLinkDefinitionDAO;
import pl.edu.amu.wmi.dao.UserDataDAO;
import pl.edu.amu.wmi.entity.ExternalLink;
import pl.edu.amu.wmi.entity.ExternalLinkDefinition;
import pl.edu.amu.wmi.entity.ExternalLinkHistory;
import pl.edu.amu.wmi.entity.UserData;
import pl.edu.amu.wmi.exception.externallink.ExternalLinkException;
import pl.edu.amu.wmi.model.externallink.ExternalLinkDTO;
import pl.edu.amu.wmi.service.externallink.ExternalLinkHistoryService;
import pl.edu.amu.wmi.service.externallink.ExternalLinkService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExternalLinkServiceImpl implements ExternalLinkService {

    // Allowed file types for external link uploads
    private static final Set<String> ALLOWED_FILE_TYPES = Set.of(
        // PDF files
        "application/pdf",
        
        // Microsoft Word documents
        "application/msword", // .doc
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
        
        // Microsoft Excel
        "application/vnd.ms-excel", // .xls
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
        
        // Microsoft PowerPoint
        "application/vnd.ms-powerpoint", // .ppt
        "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .pptx
        
        // OpenDocument formats
        "application/vnd.oasis.opendocument.text", // .odt
        "application/vnd.oasis.opendocument.spreadsheet", // .ods
        "application/vnd.oasis.opendocument.presentation", // .odp
        
        // RTF
        "application/rtf",
        "text/rtf",
        
        // Plain text
        "text/plain",
        
        // Images
        "image/jpeg",
        "image/jpg", 
        "image/png",
        "image/gif",
        "image/bmp",
        "image/webp",
        "image/svg+xml"
    );

    private static final Set<String> ALLOWED_FILE_EXTENSIONS = Set.of(
        // Documents
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", 
        "odt", "ods", "odp", "rtf", "txt",
        
        // Images
        "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg"
    );

    private final ExternalLinkDAO externalLinkDAO;

    private final ExternalLinkDefinitionDAO externalLinkDefinitionDAO;

    private final ExternalLinkHistoryService externalLinkHistoryService;

    private final UserDataDAO userDataDAO;

    @Value("${app.file-storage.path:${java.io.tmpdir}/external-links}")
    private String fileStoragePath;

    @Autowired
    public ExternalLinkServiceImpl(ExternalLinkDAO externalLinkDAO, 
                                   ExternalLinkDefinitionDAO externalLinkDefinitionDAO,
                                   ExternalLinkHistoryService externalLinkHistoryService,
                                   UserDataDAO userDataDAO) {
        this.externalLinkDAO = externalLinkDAO;
        this.externalLinkDefinitionDAO = externalLinkDefinitionDAO;
        this.externalLinkHistoryService = externalLinkHistoryService;
        this.userDataDAO = userDataDAO;
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
        ExternalLink savedLink = externalLinkDAO.save(externalLink);
        
        // Record history for link creation
        UserData currentUser = getCurrentUser();
        if (currentUser != null) {
            externalLinkHistoryService.recordChange(
                savedLink,
                ExternalLinkHistory.ChangeType.LINK_CREATED,
                currentUser,
                "External link created for definition: " + definition.getName(),
                null, null, null, null, null, null
            );
        }
        
        return savedLink;
    }

    private UserData getCurrentUser() {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return userDataDAO.findByIndexNumber(userDetails.getUsername()).orElse(null);
        } catch (Exception e) {
            log.warn("Could not retrieve current user for history tracking: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Validates that the uploaded file has an allowed file type and extension
     */
    private void validateFileType(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        
        // Check if file has a name and extension
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            throw new ExternalLinkException("File name is required");
        }
        
        // Extract file extension
        String fileExtension = "";
        if (originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
        }
        
        // Validate file extension
        if (fileExtension.isEmpty() || !ALLOWED_FILE_EXTENSIONS.contains(fileExtension)) {
            throw new ExternalLinkException(
                "File type not allowed. Allowed extensions: " + 
                String.join(", ", ALLOWED_FILE_EXTENSIONS) + 
                ". Uploaded file extension: " + (fileExtension.isEmpty() ? "none" : fileExtension)
            );
        }
        
        // Validate MIME type if available
        if (contentType != null && !contentType.trim().isEmpty()) {
            // Normalize content type (remove charset and other parameters)
            String normalizedContentType = contentType.split(";")[0].trim().toLowerCase();
            
            if (!ALLOWED_FILE_TYPES.contains(normalizedContentType)) {
                log.warn("File uploaded with non-standard MIME type: {} for extension: {}", normalizedContentType, fileExtension);
                // We still allow it if the extension is valid, as MIME types can vary
            }
        }
        
        log.info("File validation passed for: {} (extension: {}, content-type: {})", 
                originalFileName, fileExtension, contentType);
    }
    
    @Transactional
    @Override
    public List<ExternalLink> updateExternalLinks(Set<ExternalLinkDTO> externalLinks) {
        Set<ExternalLink> externalLinkEntities = new HashSet<>();
        UserData currentUser = getCurrentUser();

        externalLinks.forEach(externalLinkDto -> {
            ExternalLink externalLink = externalLinkDAO.findById(
                    Long.valueOf(externalLinkDto.getId())).orElseThrow(()
                    -> new ExternalLinkException(MessageFormat.format("External link with id: {0} not found.", externalLinkDto.getId())));
            
            // Store previous values for history
            String previousUrl = externalLink.getUrl();
            ExternalLink.LinkType previousLinkType = externalLink.getLinkType();
            String previousFilePath = externalLink.getFilePath();
            String previousOriginalFileName = externalLink.getOriginalFileName();
            String previousContentType = externalLink.getContentType();
            Long previousFileSize = externalLink.getFileSize();
            
            // Determine if this should be an external URL or file upload
            if (externalLinkDto.getContentType() != null && !externalLinkDto.getContentType().trim().isEmpty()) {
                // This indicates a file upload request
                externalLink.setLinkType(ExternalLink.LinkType.INTERNAL);
                externalLink.setContentType(externalLinkDto.getContentType());
                
                // Record history for link type change if applicable
                if (currentUser != null && previousLinkType != ExternalLink.LinkType.INTERNAL) {
                    externalLinkHistoryService.recordChange(
                        externalLink,
                        ExternalLinkHistory.ChangeType.LINK_TYPE_CHANGED,
                        currentUser,
                        "Link type changed from " + (previousLinkType != null ? previousLinkType.name() : "null") + " to INTERNAL",
                        previousUrl, previousLinkType, previousFilePath, previousOriginalFileName, previousContentType, previousFileSize
                    );
                }
            } else {
                // Regular URL update
                externalLink.setLinkType(ExternalLink.LinkType.EXTERNAL);
                externalLink.setUrl(externalLinkDto.getUrl());

                // Clear file-related fields
                externalLink.setFilePath(null);
                externalLink.setOriginalFileName(null);
                externalLink.setContentType(null);
                externalLink.setFileSize(null);
                
                // Record history for URL update or link type change
                if (currentUser != null) {
                    boolean hadPreviousFile = previousOriginalFileName != null && !previousOriginalFileName.trim().isEmpty();
                    
                    if (hadPreviousFile) {
                        // Changing from file to URL
                        externalLinkHistoryService.recordChange(
                            externalLink,
                            ExternalLinkHistory.ChangeType.LINK_TYPE_CHANGED,
                            currentUser,
                            "Changed from file '" + previousOriginalFileName + "' to URL: '" + externalLinkDto.getUrl() + "'",
                            previousUrl, previousLinkType, previousFilePath, previousOriginalFileName, previousContentType, previousFileSize
                        );
                    } else if (!java.util.Objects.equals(previousUrl, externalLinkDto.getUrl())) {
                        // URL to URL change
                        String previousUrlText = previousUrl != null && !previousUrl.trim().isEmpty() ? 
                            "'" + previousUrl + "'" : "empty";
                        String newUrlText = externalLinkDto.getUrl() != null && !externalLinkDto.getUrl().trim().isEmpty() ?
                            "'" + externalLinkDto.getUrl() + "'" : "empty";
                        externalLinkHistoryService.recordChange(
                            externalLink,
                            ExternalLinkHistory.ChangeType.URL_UPDATED,
                            currentUser,
                            "URL updated from " + previousUrlText + " to " + newUrlText,
                            previousUrl, previousLinkType, previousFilePath, previousOriginalFileName, previousContentType, previousFileSize
                        );
                    }
                }
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

        ExternalLink savedLink = externalLinkDAO.save(externalLink);
        
        // Record history for file upload
        UserData currentUser = getCurrentUser();
        if (currentUser != null) {
            externalLinkHistoryService.recordChange(
                savedLink,
                ExternalLinkHistory.ChangeType.FILE_UPLOADED,
                currentUser,
                "File uploaded: '" + originalFileName + "' (" + file.getSize() + " bytes)",
                null, null, null, null, null, null
            );
        }

        return savedLink;
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

        // Store previous values for history
        String previousUrl = externalLink.getUrl();
        ExternalLink.LinkType previousLinkType = externalLink.getLinkType();
        String previousFilePath = externalLink.getFilePath();
        String previousOriginalFileName = externalLink.getOriginalFileName();
        String previousContentType = externalLink.getContentType();
        Long previousFileSize = externalLink.getFileSize();

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
        
        // Record history for file deletion
        UserData currentUser = getCurrentUser();
        if (currentUser != null) {
            String deletedFileText = previousOriginalFileName != null ? 
                "'" + previousOriginalFileName + "'" : "file";
            String fileSizeText = previousFileSize != null ? " (" + previousFileSize + " bytes)" : "";
            externalLinkHistoryService.recordChange(
                externalLink,
                ExternalLinkHistory.ChangeType.FILE_DELETED,
                currentUser,
                "File deleted: " + deletedFileText + fileSizeText,
                previousUrl, previousLinkType, previousFilePath, previousOriginalFileName, previousContentType, previousFileSize
            );
        }
    }

    @Transactional
    @Override
    public ExternalLink updateExternalLinkWithFile(Long externalLinkId, MultipartFile file) throws IOException {
        // Validate file type first
        validateFileType(file);
        
        ExternalLink externalLink = externalLinkDAO.findById(externalLinkId)
                .orElseThrow(() -> new ExternalLinkException(
                        MessageFormat.format("External link with id: {0} not found.", externalLinkId)));

        // Store previous values for history
        String previousUrl = externalLink.getUrl();
        ExternalLink.LinkType previousLinkType = externalLink.getLinkType();
        String previousFilePath = externalLink.getFilePath();
        String previousOriginalFileName = externalLink.getOriginalFileName();
        String previousContentType = externalLink.getContentType();
        Long previousFileSize = externalLink.getFileSize();

        // Delete existing file if present
        if (externalLink.getLinkType() == ExternalLink.LinkType.INTERNAL && externalLink.getFilePath() != null) {
            Path oldFilePath = Paths.get(externalLink.getFilePath());
            if (Files.exists(oldFilePath)) {
                Files.delete(oldFilePath);
                log.info("Deleted old file: {}", externalLink.getFilePath());
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
        externalLink.setUrl(null); // Clear URL when uploading file
        externalLink.setFilePath(filePath.toString());
        externalLink.setOriginalFileName(originalFileName);
        externalLink.setContentType(file.getContentType());
        externalLink.setFileSize(file.getSize());

        ExternalLink updatedLink = externalLinkDAO.save(externalLink);
        
        // Record history for file operation
        UserData currentUser = getCurrentUser();
        if (currentUser != null) {
            String changeDescription;
            ExternalLinkHistory.ChangeType changeType;
            
            // Check if we actually had a URL before (regardless of linkType inconsistencies)
            boolean hadPreviousUrl = previousUrl != null && !previousUrl.trim().isEmpty();
            boolean hadPreviousFile = previousOriginalFileName != null && !previousOriginalFileName.trim().isEmpty();
            
            if (hadPreviousUrl) {
                // Transitioning from URL to file
                changeType = ExternalLinkHistory.ChangeType.LINK_TYPE_CHANGED;
                changeDescription = "Changed from URL '" + previousUrl + "' to file: '" + originalFileName + 
                                  "' (" + file.getSize() + " bytes)";
            } else if (hadPreviousFile) {
                // Replacing existing file with new file
                changeType = ExternalLinkHistory.ChangeType.FILE_REPLACED;
                changeDescription = "File replaced: '" + previousOriginalFileName + 
                                  "' with '" + originalFileName + "' (" + file.getSize() + " bytes)";
            } else {
                // No previous content, uploading new file
                changeType = ExternalLinkHistory.ChangeType.FILE_UPLOADED;
                changeDescription = "File uploaded: '" + originalFileName + "' (" + file.getSize() + " bytes)";
            }
            
            externalLinkHistoryService.recordChange(
                updatedLink,
                changeType,
                currentUser,
                changeDescription,
                previousUrl, previousLinkType, previousFilePath, previousOriginalFileName, previousContentType, previousFileSize
            );
        }

        return updatedLink;
    }

    @Override
    public ExternalLink findById(Long externalLinkId) {
        return externalLinkDAO.findById(externalLinkId)
                .orElseThrow(() -> new ExternalLinkException(
                        MessageFormat.format("External link with id: {0} not found.", externalLinkId)));
    }

}
