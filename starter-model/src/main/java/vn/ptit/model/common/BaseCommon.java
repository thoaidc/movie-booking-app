package vn.ptit.model.common;

import org.springframework.web.multipart.MultipartFile;
import java.util.Objects;

@SuppressWarnings("unused")
public class BaseCommon {

    public static boolean invalidUploadFile(MultipartFile file) {
        return file == null || file.isEmpty() || !Objects.nonNull(file.getOriginalFilename());
    }

    public static boolean invalidUploadFiles(MultipartFile[] files) {
        if (files == null || files.length == 0)
            return true;

        for (MultipartFile file : files) {
            if (invalidUploadFile(file))
                return true;
        }

        return false;
    }
}
