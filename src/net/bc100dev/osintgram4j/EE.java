package net.bc100dev.osintgram4j;

import net.bc100dev.commons.ApplicationRuntimeException;
import net.bc100dev.commons.ResourceManager;
import net.bc100dev.commons.utils.StringGenerator;
import net.bc100dev.commons.utils.Utility;
import net.bc100dev.commons.utils.io.FileEncryption;
import net.bc100dev.commons.utils.io.FileUtil;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import static net.bc100dev.commons.utils.RuntimeEnvironment.USER_HOME;
import static net.bc100dev.commons.utils.RuntimeEnvironment.getOperatingSystem;

public class EE {

    private static int getRandomLine(List<Integer> saved, int len) {
        int i = Utility.getRandomInteger(0, len - 1);

        for (int save : saved) {
            if (i == save)
                return getRandomLine(saved, len);
        }

        return i;
    }

    private static boolean isDataEqual(String data, File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] buff = fis.readAllBytes();
        fis.close();

        if (!data.endsWith("\n"))
            data += "\n";

        return new String(buff).equals(data);
    }

    private static void decryptToSource(File srcFile, String pass, File destFile) throws IOException, GeneralSecurityException {
        byte[] buff = FileEncryption.toDecrypted(srcFile, pass);
        String data = new String(buff);

        String[] lines = data.split("\n");
        StringBuilder str = new StringBuilder();

        for (String line : lines)
            str.append(line).append("\n");

        if (!isDataEqual(str.toString(), destFile)) {
            FileOutputStream fos = new FileOutputStream(destFile);
            fos.write(str.toString().getBytes());
            fos.close();

            System.out.println("A new file has appeared in your Home Directory.");
            System.out.println("Go and take a look into " + destFile.getAbsolutePath());
            System.out.println("You might, or might not, find some answers there...");
        }
    }

    private static void fetchFile(File outFile) throws IOException {
        try {
            URL url = new URI("https://github.com/BeChris100/osintgram4j/raw/master/extres/ee.enc").toURL();
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() == 200) {
                DataInputStream dis = new DataInputStream(conn.getInputStream());
                FileOutputStream fos = new FileOutputStream(outFile);

                int len;
                byte[] buff = new byte[1024];

                while ((len = dis.read(buff, 0, 1024)) != -1)
                    fos.write(buff, 0, len);

                dis.close();
                fos.close();
            }

            conn.disconnect();
        } catch (URISyntaxException ex) {
            throw new IOException(ex);
        }
    }

    public static void attemptDecrypt() {
        try {
            File srcFile, progFile,
                    destFile = new File(USER_HOME.getAbsolutePath() + "/TheReveals.txt");
            switch (getOperatingSystem()) {
                case LINUX -> {
                    srcFile = new File(USER_HOME.getAbsolutePath() + "/.local/share/.ee");
                    progFile = new File(USER_HOME.getAbsolutePath() + "/.config/osintgram4j/ee.progress.list");
                }
                case MAC_OS -> {
                    srcFile = new File(USER_HOME.getAbsolutePath() + "/Library/.ee");
                    progFile = new File(USER_HOME.getAbsolutePath() + "/Library/net.bc100dev.osintgram4j/ee.progress.list");
                }
                case WINDOWS -> {
                    srcFile = new File(USER_HOME.getAbsolutePath() + "\\AppData\\Roaming\\ee");
                    progFile = new File(USER_HOME.getAbsolutePath() + "\\AppData\\Local\\osintgram4j\\ee.progress.list");
                }
                default -> throw new ApplicationRuntimeException("Unsupported Operating System");
            }

            String pass = StringGenerator.generateString(5, StringGenerator.DEFAULT_CHARACTER_MAP_NO_SPECIALS);
            fetchFile(srcFile);

            FileInputStream fis = new FileInputStream(srcFile);
            byte[] buff = fis.readAllBytes();
            fis.close();

            FileUtil.write(srcFile.getAbsolutePath(), buff);

            // make an attempt into decrypting the file
            String data = new String(FileEncryption.toDecrypted(srcFile, pass));

            // this will only continue, if the file has been successfully decrypted
            String[] lines = data.split("\n");

            // open the progress file
            if (!progFile.exists())
                FileUtil.createFile(progFile.getAbsolutePath(), true);

            fis = new FileInputStream(progFile);
            byte[] buff0 = fis.readAllBytes();
            fis.close();

            List<Integer> saved = new ArrayList<>();

            String progressData = new String(buff0);
            if (!progressData.trim().isEmpty()) {
                for (String ln : new String(buff0).split("\n")) {
                    if (!ln.trim().isEmpty())
                        saved.add(Integer.parseInt(ln));
                }
            }

            int in = getRandomLine(saved, lines.length);
            saved.add(in);

            String line = lines[in];
            System.out.println(line);

            if (saved.size() >= 12)
                decryptToSource(srcFile, pass, destFile);

            FileOutputStream fos = new FileOutputStream(progFile, true);

            if (!progressData.trim().isEmpty())
                fos.write("\n".getBytes());

            fos.write(String.valueOf(in).getBytes());
            fos.close();

            FileUtil.delete(srcFile.getAbsolutePath());
        } catch (GeneralSecurityException | IOException ex) {
            if (ex instanceof IOException)
                ex.printStackTrace(System.err);
        }
    }

}
