package pl.edu.amu.wmi.service.externallink;

import pl.edu.amu.wmi.entity.ExternalLink;
import pl.edu.amu.wmi.entity.ExternalLinkHistory;
import pl.edu.amu.wmi.entity.UserData;
import pl.edu.amu.wmi.model.externallink.ExternalLinkHistoryDTO;

import java.util.List;

public interface ExternalLinkHistoryService {

    /**
     * Records a change in external link history
     * @param externalLink The external link that was changed
     * @param changeType Type of change that occurred
     * @param changedByUser User who made the change
     * @param changeDescription Optional description of the change
     * @param previousUrl Previous URL (if applicable)
     * @param previousLinkType Previous link type (if applicable)
     * @param previousFilePath Previous file path (if applicable)
     * @param previousOriginalFileName Previous original file name (if applicable)
     * @param previousContentType Previous content type (if applicable)
     * @param previousFileSize Previous file size (if applicable)
     * @return Created history record
     */
    ExternalLinkHistory recordChange(
            ExternalLink externalLink,
            ExternalLinkHistory.ChangeType changeType,
            UserData changedByUser,
            String changeDescription,
            String previousUrl,
            ExternalLink.LinkType previousLinkType,
            String previousFilePath,
            String previousOriginalFileName,
            String previousContentType,
            Long previousFileSize
    );

    /**
     * Get history for a specific external link
     * @param externalLinkId ID of the external link
     * @return List of history records ordered by creation date (newest first)
     */
    List<ExternalLinkHistoryDTO> getHistoryByExternalLinkId(Long externalLinkId);

    /**
     * Get all external link history for a study year
     * @param studyYear Study year to filter by
     * @return List of all history records for the study year
     */
    List<ExternalLinkHistoryDTO> getAllHistoryByStudyYear(String studyYear);

}
