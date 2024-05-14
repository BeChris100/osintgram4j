package osintgram4j.api;

import net.bc100dev.commons.utils.OperatingSystem;
import net.bc100dev.commons.utils.io.FileUtil;
import osintgram4j.commons.ShellConfig;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ShellAlias {

    private final String aliasCmd;
    private final ShellCaller caller;
    private final String[] executionArgs;

    private String[] dependenciesArr;

    private final Map<OperatingSystem, Boolean> osSupportList = new HashMap<>();

    public ShellAlias(String aliasCmd, ShellCaller caller, String[] executionArgs) {
        this.aliasCmd = aliasCmd;
        this.caller = caller;
        this.executionArgs = executionArgs;
    }

    public String getAliasCmd() {
        return aliasCmd;
    }

    public ShellCaller getCaller() {
        return caller;
    }

    public String[] getExecutionArgs() {
        return executionArgs;
    }

    public void allowPlatformSupport(OperatingSystem system, boolean supported) {
        osSupportList.put(system, supported);
    }

    public void allowAllPlatforms(boolean supportValue) {
        for (OperatingSystem os : OperatingSystem.values())
            osSupportList.put(os, supportValue);
    }

    public boolean isPlatformSupported(OperatingSystem system) {
        return osSupportList.getOrDefault(system, false);
    }

    public void dependsOn(String[] pathCmdNames) {
        dependenciesArr = pathCmdNames;
    }

    public boolean canExecute() throws IOException {
        String[] PATH_ENV = System.getenv("PATH").split(File.pathSeparator);

        List<String> pathContentCache = new ArrayList<>();
        List<File> binaries = new ArrayList<>();

        for (String PATH : PATH_ENV) {
            List<String> pathContent = FileUtil.listDirectory(PATH, true, false);

            for (String pathEntry : pathContent) {
                if (pathContentCache.contains(pathEntry))
                    continue;

                pathContentCache.add(pathEntry);

                File entry = new File(pathEntry);
                if (!entry.exists())
                    continue;

                for (String dependency : dependenciesArr) {
                    if (binaries.contains(entry))
                        continue;

                    File absCheck = new File(dependency);
                    if (absCheck.isAbsolute()) {
                        if (entry.getAbsolutePath().equalsIgnoreCase(absCheck.getAbsolutePath()))
                            binaries.add(entry);

                        continue;
                    }

                    if (entry.getName().equals(dependency))
                        binaries.add(entry);
                }
            }

            pathContentCache.clear();
        }

        return binaries.size() == dependenciesArr.length;
    }

    public int execute(String[] additionalArgs, List<ShellConfig> env) throws ShellException {
        List<String> listArgs = new ArrayList<>();
        listArgs.addAll(Arrays.asList(executionArgs));
        listArgs.addAll(Arrays.asList(additionalArgs));

        String[] args = new String[listArgs.size()];
        for (int i = 0; i < listArgs.size(); i++)
            args[i] = listArgs.get(i);

        return caller.execute(args, env);
    }

}
