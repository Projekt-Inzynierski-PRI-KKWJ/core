package pl.edu.amu.wmi.service.externallink.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.amu.wmi.dao.ExternalLinkHistoryDAO;
import pl.edu.amu.wmi.entity.ExternalLink;
import pl.edu.amu.wmi.entity.ExternalLinkHistory;
import pl.edu.amu.wmi.entity.UserData;
import pl.edu.amu.wmi.mapper.externallink.ExternalLinkHistoryMapper;
import pl.edu.amu.wmi.model.externallink.ExternalLinkHistoryDTO;
import pl.edu.amu.wmi.service.externallink.ExternalLinkHistoryService;

import java.util.List;

@Slf4j
@Service
public class ExternalLinkHistoryServiceImpl implements ExternalLinkHistoryService {

    private final ExternalLinkHistoryDAO externalLinkHistoryDAO;
    private final ExternalLinkHistoryMapper externalLinkHistoryMapper;

    @Autowired
    public ExternalLinkHistoryServiceImpl(
            ExternalLinkHistoryDAO externalLinkHistoryDAO,
            ExternalLinkHistoryMapper externalLinkHistoryMapper) {
        this.externalLinkHistoryDAO = externalLinkHistoryDAO;
        this.externalLinkHistoryMapper = externalLinkHistoryMapper;
    }

    @Override
    @Transactional
    public ExternalLinkHistory recordChange(
            ExternalLink externalLink,
            ExternalLinkHistory.ChangeType changeType,
            UserData changedByUser,
            String changeDescription,
            String previousUrl,
            ExternalLink.LinkType previousLinkType,
            String previousFilePath,
            String previousOriginalFileName,
            String previousContentType,
            Long previousFileSize) {

        ExternalLinkHistory history = new ExternalLinkHistory();
        history.setExternalLink(externalLink);
        history.setChangeType(changeType);
        history.setChangedByUser(changedByUser);
        history.setChangeDescription(changeDescription);
        history.setPreviousUrl(previousUrl);
        history.setPreviousLinkType(previousLinkType);
        history.setPreviousFilePath(previousFilePath);
        history.setPreviousOriginalFileName(previousOriginalFileName);
        history.setPreviousContentType(previousContentType);
        history.setPreviousFileSize(previousFileSize);

        ExternalLinkHistory savedHistory = externalLinkHistoryDAO.save(history);
        
        log.info("Recorded external link history: change type={}, external link id={}, user={}", 
                changeType, externalLink.getId(), changedByUser.getIndexNumber());
        
        return savedHistory;
    }

    @Override
    public List<ExternalLinkHistoryDTO> getHistoryByExternalLinkId(Long externalLinkId) {
        List<ExternalLinkHistory> historyList = externalLinkHistoryDAO.findHistoryByExternalLinkId(externalLinkId);
        return externalLinkHistoryMapper.mapToHistoryDtoList(historyList);
    }

    @Override
    public List<ExternalLinkHistoryDTO> getAllHistoryByStudyYear(String studyYear) {
        List<ExternalLinkHistory> historyList = externalLinkHistoryDAO.findAllHistoryByStudyYear(studyYear);
        return externalLinkHistoryMapper.mapToHistoryDtoList(historyList);
    }

}
