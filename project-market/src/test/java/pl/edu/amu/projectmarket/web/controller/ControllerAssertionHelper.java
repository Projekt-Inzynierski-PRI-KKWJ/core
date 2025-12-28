package pl.edu.amu.projectmarket.web.controller;

import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import pl.edu.amu.wmi.exception.BusinessException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ControllerAssertionHelper {

    public static void assertThrows(Supplier<ResponseEntity<?>> supplier, String exceptionMessage) {
        assertThatThrownBy(supplier::get)
            .isInstanceOf(BusinessException.class)
            .hasMessage(exceptionMessage);
    }
}
