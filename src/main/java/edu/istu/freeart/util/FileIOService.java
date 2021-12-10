package edu.istu.freeart.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileIOService {

    public String saveUploadedFile(MultipartFile file, String uploadFolder) throws IOException {
        if (!file.isEmpty()) {
            final byte[] bytes = file.getBytes();
            final String[] split = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
            String fileExtension = "";
            if (split.length > 0) {
                fileExtension = "." + split[split.length - 1];
            }
            final String fileName = UUID.randomUUID() + fileExtension;
            final Path path = Paths.get(uploadFolder, fileName);
            Files.write(path, bytes, StandardOpenOption.CREATE);
            return fileName;
        }
        return null;
    }

    public void deleteFile(String fileName, String folder) throws IOException {
        final Path path = Paths.get(folder, fileName);
        Files.delete(path);
    }

    public String getFileChecksum(String fileName, String fileFolder) {
        try (InputStream is = Files.newInputStream(Paths.get(fileFolder, fileName))) {
            return DigestUtils.md5Hex(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
