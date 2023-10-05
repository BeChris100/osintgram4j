package net.bc100dev.osintgram4j;

import net.bc100dev.commons.ResourceManager;

import java.io.IOException;
import java.io.InputStream;

public class TitleBlock {

    public static String TITLE_BLOCK() {
        try {
            ResourceManager mgr = new ResourceManager(TitleBlock.class, true);
            if (mgr.resourceExists("titleblock.txt")) {
                InputStream is = mgr.getResourceInputStream("titleblock.txt");
                StringBuilder str = new StringBuilder();
                int d;
                byte[] b = new byte[1024];

                while ((d = is.read(b, 0, 1024)) != -1)
                    str.append(new String(b, 0, d));

                is.close();

                return str.toString();
            } else
                return "OSINTgram v1.0";
        } catch (IOException ignore) {
            return "OSINTgram v1.0";
        }
    }

}
