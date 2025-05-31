package pl.edu.amu.wmi.service.externallink;

import org.springframework.web.multipart.MultipartFile;
import pl.edu.amu.wmi.entity.ExternalLink;
import pl.edu.amu.wmi.model.externallink.ExternalLinkDTO;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface ExternalLinkService {

    Set<ExternalLink> createEmptyExternalLinks(String studyYear);

    List<ExternalLink> updateExternalLinks(Set<ExternalLinkDTO> externalLinks);

    Set<String> findDefinitionHeadersByStudyYear(String studyYear);
    
    ExternalLink findById(Long externalLinkId);
    
    ExternalLink storeInternalFile(Long definitionId, MultipartFile file) throws IOException;
    
    ExternalLink updateExternalLinkWithFile(Long externalLinkId, MultipartFile file) throws IOException;
    
    byte[] getInternalFileContent(Long externalLinkId) throws IOException;
    
    void deleteInternalFile(Long externalLinkId) throws IOException;
}
