package net.bc100dev.osintgram4j.sh;

import net.bc100dev.commons.utils.Utility;
import osintgram4j.commons.ShellConfig;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

public class ShellFile {

    private final boolean shebang;
    private final String cliContinue;
    private final List<String> instructions;
    private final List<ShellConfig> env;

    protected ShellFile(boolean shebang, String cliContinue, List<String> instructions, List<ShellConfig> env) {
        this.shebang = shebang;
        this.cliContinue = cliContinue;
        this.instructions = instructions;
        this.env = env;
    }

    public boolean containsShebang() {
        return shebang;
    }

    public boolean canContinueShell() {
        if (cliContinue != null)
            return true;

        for (ShellConfig config : env) {
            String key = config.getName();
            String val = config.getValue();

            if (key.equalsIgnoreCase("ShellFile.FileProcessor.CliContinue"))
                return val.equalsIgnoreCase("True") || val.equalsIgnoreCase("Yes") ||
                        val.equalsIgnoreCase("Accept");
        }

        return false;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public List<ShellConfig> getEnvironment() {
        return env;
    }

    public static ShellFile open(File file) throws IOException {
        if (file == null)
            throw new NullPointerException("File Pointer is null");

        if (!file.exists())
            throw new FileNotFoundException("File at \"" + file.getAbsolutePath() + "\" not found");

        if (!file.canRead())
            throw new AccessDeniedException("\"" + file.getAbsolutePath() + "\": Permission denied");

        FileInputStream fis = new FileInputStream(file);
        StringBuilder str = new StringBuilder();

        int len;
        byte[] buff = new byte[4096];

        while ((len = fis.read(buff, 0, 4096)) != -1)
            str.append(new String(buff, 0, len));

        fis.close();

        boolean shebang = false, cliContinue = false;
        List<String> ins = new ArrayList<>();
        List<ShellConfig> env = new ArrayList<>();
        String data = str.toString();

        if (data.trim().startsWith("#!/"))
            shebang = true;

        String[] lines = data.split(Utility.getLineSeparator(data));
        for (String line : lines) {
            String pLine = line.trim();

            if (pLine.isEmpty())
                continue;

            if (pLine.startsWith("#"))
                continue;

            if (pLine.startsWith("&")) {
                String[] opt = pLine.split("=", 2);
                String key = opt[0].trim().replaceFirst("&", "");
                String value = opt[1].trim();

                if (env.isEmpty()) {
                    env.add(new ShellConfig(key, value));
                    continue;
                }

                List<ShellConfig> cache = new ArrayList<>();
                boolean found = false;
                for (int i = 0; i < env.size(); i++) {
                    ShellConfig config = env.get(i);

                    if (cache.contains(config))
                        continue;

                    if (config.getName().equals(key)) {
                        env.set(i, new ShellConfig(key, value));
                        found = true;
                        break;
                    }

                    cache.add(config);
                }
                cache.clear();

                if (!found)
                    env.add(new ShellConfig(key, value));

                continue;
            }

            if (pLine.equalsIgnoreCase("*CliContinue") || pLine.equalsIgnoreCase("*ContinueCli") ||
                    pLine.equalsIgnoreCase("*ShellContinue"))
                cliContinue = true;

            ins.add(pLine);
        }

        return new ShellFile(shebang, cliContinue ? String.valueOf(cliContinue) : null, ins, env);
    }

}
