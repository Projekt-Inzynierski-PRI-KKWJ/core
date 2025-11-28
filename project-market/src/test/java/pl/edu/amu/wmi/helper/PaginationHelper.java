package pl.edu.amu.wmi.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginationHelper {

    @SafeVarargs
    public static <T> Page<T> createPage(T... data) {
        List<T> collection = new ArrayList<>(Arrays.asList(data));
        return new PageImpl<>(
            collection,
            PageRequest.of(0, 10),
            collection.size()
        );
    }
}
