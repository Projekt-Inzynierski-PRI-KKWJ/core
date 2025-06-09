package pl.edu.amu.wmi.model.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;

@Getter
@RequiredArgsConstructor
public enum DataFeedFileExtension {
    CSV("csv"), JSON("json");

    private final String extension;

    public static boolean isValidEnum(String value) {
        return EnumUtils.isValidEnum(DataFeedFileExtension.class, value.toUpperCase());
    }
}
