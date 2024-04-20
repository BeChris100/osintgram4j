package osintgram4j.commons;

import net.bc100dev.commons.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TitleBlock {

    public static String TITLE_BLOCK() {
        try {
            ResourceManager mgr = new ResourceManager(TitleBlock.class, false);
            if (!mgr.resourceExists("/net/bc100dev/osintgram4j/res/titleblock.txt"))
                return "OSINTgram v0.1";

            InputStream is = mgr.getResourceInputStream("/net/bc100dev/osintgram4j/res/titleblock.txt");
            StringBuilder str = new StringBuilder();
            int d;
            byte[] b = new byte[1024];

            while ((d = is.read(b, 0, 1024)) != -1)
                str.append(new String(b, 0, d));

            is.close();

            return str.toString();
        } catch (IOException ignore) {
            return "OSINTgram v0.1";
        }
    }

    public static String DISPLAY() {
        try {
            ResourceManager mgr = new ResourceManager(TitleBlock.class, false);
            if (!mgr.resourceExists("/net/bc100dev/osintgram4j/res/app_ver.cfg"))
                return "OSINTgram v0.1";

            Properties props = new Properties();
            InputStream is = mgr.getResourceInputStream("/net/bc100dev/osintgram4j/res/app_ver.cfg");

            props.load(is);
            is.close();

            return props.getProperty("BUILD_DISPLAY") + " v" + props.getProperty("BUILD_VERSION") + "-" + props.getProperty("BUILD_VERSION_CODE") + " (" + props.getProperty("BUILD_DISPLAY_FLAVOR") + ")";
        } catch (IOException ignore) {
            return "OSINTgram v0.1";
        }
    }

}
