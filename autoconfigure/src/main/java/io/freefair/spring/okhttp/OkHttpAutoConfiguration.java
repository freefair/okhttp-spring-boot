package io.freefair.spring.okhttp;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

abstract class OkHttpAutoConfiguration {

    @Autowired
    OkHttpProperties properties;

    File getCacheDir(String prefix) throws IOException {
        File cacheDir;

        String directory = properties.getCache().getDirectory();
        switch (properties.getCache().getMode()) {
            case TEMPORARY:
                if (directory != null) {
                    cacheDir = Files.createTempDirectory(new File(directory).getAbsoluteFile().toPath(), prefix).toFile();
                } else {
                    cacheDir = Files.createTempDirectory(prefix).toFile();
                }
                break;
            case PERSISTENT:
                if (directory != null) {
                    cacheDir = new File(directory).getAbsoluteFile();
                } else {
                    cacheDir = new File(prefix).getAbsoluteFile();
                }
                break;
            case NONE:
            default:
                return null;
        }

        return cacheDir;
    }

}
