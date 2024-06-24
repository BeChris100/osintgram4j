package net.bc100dev.osintgram4j.config.modding;

import java.io.File;

public class ApplicationModification {

    private final ModInfo info;
    private final File modFile;

    private final String[] commands;
    private final Class<?> startClass;

    protected ApplicationModification(ModInfo info, File modFile, String[] commands, Class<?> startClass) {
        this.info = info;
        this.modFile = modFile;
        this.commands = commands;
        this.startClass = startClass;
    }

    public ModInfo getInfo() {
        return info;
    }

    public File getModFile() {
        return modFile;
    }

    public String[] getCommands() {
        return commands;
    }

    public Class<?> getStartClass() {
        return startClass;
    }

    public record ModInfo(String author, String name, String modVersion) {
    }

}
