package us.eunoians.mcrpg.configuration;

import dev.dejvokep.boostedyaml.YamlDocument;
import org.jetbrains.annotations.NotNull;
import us.eunoians.mcrpg.McRPG;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages all of McRPGs configuration files and is the point of access
 * to get any {@link YamlDocument}s that hold configuration values.
 */
public final class FileManager {

    private final McRPG mcRPG;
    private final Map<FileType, YamlDocument> loadedFiles;

    public FileManager(@NotNull McRPG mcRPG) {
        this.mcRPG = mcRPG;
        this.loadedFiles = new HashMap<>();

        if (!mcRPG.getDataFolder().exists()) {
            mcRPG.getDataFolder().mkdirs();
        }

        loadFiles();
    }

    /**
     * Loads all the configuration files for McRPG.
     */
    private void loadFiles() {
        for (FileType fileType : FileType.values()) {
            loadedFiles.put(fileType, fileType.initializeFile());
        }
    }

    /**
     * Reloads all the configuration files for McRPG.
     */
    public void reloadFiles() {
        for (YamlDocument yamlDocument : loadedFiles.values()) {
            try {
                yamlDocument.reload();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        mcRPG.getReloadableContentRegistry().reloadAllContent();
    }

    /**
     * Gets the {@link YamlDocument} that contains all the configuration information
     * for the provided {@link FileType}.
     *
     * @param fileType The {@link FileType} to get the configuration for.
     * @return The {@link YamlDocument} that contains all the configuration information for the provided
     * {@link FileType}.
     */
    @NotNull
    public YamlDocument getFile(@NotNull FileType fileType) {
        return loadedFiles.get(fileType);
    }
}
