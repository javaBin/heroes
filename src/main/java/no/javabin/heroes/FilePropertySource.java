package no.javabin.heroes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;


public class FilePropertySource implements PropertySource {

    private static final long FILE_ACCESS_INTERVAL = 5000L;
    private long nextCheckTime = 0L;
    private long lastModifiedLoaded = 0L;

    private final Properties properties = new Properties();

    private File file;

    public FilePropertySource(File file) {
        this.file = file;
    }

    @Override
    public Optional<String> get(String key) {
        checkForPropertyRefresh();
        return Optional.ofNullable(properties.getProperty(key));
    }

    private void checkForPropertyRefresh() {
        if (System.currentTimeMillis() > nextCheckTime) {
            if (file.lastModified() > lastModifiedLoaded) {
                lastModifiedLoaded = file.lastModified();
                try (InputStream input = new FileInputStream(file)) {
                    properties.clear();
                    properties.load(input);
                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                    throw ExceptionUtil.softenException(e);
                }
            }
            nextCheckTime = System.currentTimeMillis() + FILE_ACCESS_INTERVAL;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + file.getAbsolutePath() + (file.isFile() ? "" : " (not found)") + "}";
    }
}