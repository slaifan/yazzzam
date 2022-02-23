package uk.ac.ed.yazzzam.index;

import uk.ac.ed.yazzzam.disk.PostingListDiskRepr;
import uk.ac.ed.yazzzam.index.postinglists.VarByteProximityPostingList;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class IndexMeta {
    private static final String INDEX_META_FILENAME = "index_meta.properties";
    private static final Properties props = new Properties();

    static {
        InputStream is = IndexMeta.class.getClassLoader().getResourceAsStream(INDEX_META_FILENAME);
        try {
            props.load(is);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot read the index metadata file!");
            System.exit(2001);
        }
    }

    public static String getPostingListImplementationClassName(PostingListDiskRepr diskRepr) {
        return props.getProperty(diskRepr.name(), VarByteProximityPostingList.class.getSimpleName());
    }
}
