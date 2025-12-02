package pl.edu.amu.projectmarket.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import pl.edu.amu.wmi.entity.UserData;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserDataHelper {

    public static UserData defaults() {
        return defaults(RandomStringUtils.randomAlphanumeric(16));
    }

    public static UserData defaults(String indexNumber) {
        UserData userData = new UserData();
        userData.setFirstName(RandomStringUtils.randomAlphabetic(6));
        userData.setLastName(RandomStringUtils.randomAlphabetic(8));
        userData.setIndexNumber(indexNumber);
        userData.setEmail(userData.getFirstName().toLowerCase() + "." + userData.getLastName().toLowerCase() + "@example.com");
        return userData;
    }
}
