package net.bc100dev.commons;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataConvert {

    public static List<File> fromListString(List<String> data) {
        if (data == null)
            throw new NullPointerException("No input data to convert");

        if (data.isEmpty())
            return new ArrayList<>();

        List<File> files = new ArrayList<>();

        for (String _data : data)
            files.add(new File(_data));

        return files;
    }

}
