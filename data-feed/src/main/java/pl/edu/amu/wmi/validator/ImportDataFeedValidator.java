package pl.edu.amu.wmi.validator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.amu.wmi.exception.DataFeedFileExtensionException;
import pl.edu.amu.wmi.model.enumeration.DataFeedFileExtension;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ImportDataFeedValidator {

    private static final String EMPTY_FILE_EXTENSION = "File extension is empty or file is wrong";
    private static final String INVALID_FILE_EXTENSION = "File extension is wrong. Should be %s but is %s";
    private static final String SEPARATOR = ".";
    private static final Character LAST_INDEX = '.';
    private static final String DELIMITER = ",";

    public static void validateFileExtension(MultipartFile data, List<DataFeedFileExtension> fileExtensions) {
        if (data == null) {
            return;
        }
        var extension = getFileExtension(data);
        if (StringUtils.isBlank(extension)) {
            throw new DataFeedFileExtensionException(EMPTY_FILE_EXTENSION);
        }
        if (!DataFeedFileExtension.isValidEnum(extension) || !fileExtensions.contains(DataFeedFileExtension.valueOf(extension.toUpperCase()))) {
            throw new DataFeedFileExtensionException(INVALID_FILE_EXTENSION.formatted(fileExtensions.stream()
                .map(Enum::name)
                .collect(Collectors.joining(DELIMITER)), extension));
        }
    }

    private static String getFileExtension(MultipartFile data) {
        var originalFilename = data.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(SEPARATOR)) {
            return StringUtils.EMPTY;
        }
        return originalFilename.substring(originalFilename.lastIndexOf(LAST_INDEX) + 1);
    }
}
