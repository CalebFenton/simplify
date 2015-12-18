package org.cf.util;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.DexBuilder;

public class FrameworkCacheBuilder {

    public static void main(String[] argv) throws Exception {
        System.out.println(buildFrameworkCache());
    }

    public static String buildFrameworkCache() throws Exception {
        return buildFrameworkCache("/reflib/android-17");
    }

    public static String buildFrameworkCache(String resPath) throws Exception {
        List<File> resFiles = new LinkedList<File>();
        try {
            URL url = FrameworkCacheBuilder.class.getResource(resPath);
            File file = new File(url.toURI());
            resFiles.addAll(Utils.getFilesWithSmaliExtension(file));
        } catch (URISyntaxException ex) {
        }

        DexBuilder dexBuilder = DexBuilder.makeDexBuilder();
        StringBuilder sb = new StringBuilder();
        for (File resFile : resFiles) {
            String absPath = resFile.getAbsolutePath();
            String path = absPath.substring(absPath.lastIndexOf("/reflib"));
            InputStream is = FrameworkCacheBuilder.class.getResourceAsStream(path);
            BuilderClassDef classDef = Dexifier.dexifySmaliFile(path, is, dexBuilder);
            sb.append(ReferenceUtil.getReferenceString(classDef));
            sb.append(':').append(path.substring(1)).append('\n');
        }

        return sb.toString();
    }

}
