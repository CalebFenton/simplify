package org.cf.smalivm.dex;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import org.cf.util.Utils;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.DexBuilder;

import com.google.common.io.Files;

/**
 * This builds a list of all framework files and is used to create framework_classes.cfg.
 * The file is used to cache all the names and paths so they don't have to be enumerated when smalivm starts.
 *
 * @author caleb
 *
 */
public class FrameworkCacheBuilder {

    private static final String FRAMEWORK_SMALI_PATH = "/framework/smali";
    private static final String DEFAULT_FRAMEWORK_PATH = FRAMEWORK_SMALI_PATH + "/android-23";

    public static void main(String[] argv) throws Exception {
        System.out.println("Building framework cache ...");
        String cache = buildFrameworkCache();

        String fileName = "framework_classes.cfg";
        Files.write(cache, new File(fileName), Charset.forName("UTF-8"));

        System.out.println("Saved cache to " + fileName);
    }

    public static String buildFrameworkCache() throws Exception {
        return buildFrameworkCache(DEFAULT_FRAMEWORK_PATH);
    }

    public static String buildFrameworkCache(String resPath) throws Exception {
        List<File> resFiles = new LinkedList<>();
        try {
            URL url = FrameworkCacheBuilder.class.getResource(resPath);
            File file = new File(url.toURI());
            resFiles.addAll(Utils.getFilesWithSmaliExtension(file));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        DexBuilder dexBuilder = DexBuilder.makeDexBuilder();
        StringBuilder sb = new StringBuilder();
        for (File resFile : resFiles) {
            String absPath = resFile.getAbsolutePath();
            String path = absPath.substring(absPath.lastIndexOf(FRAMEWORK_SMALI_PATH));
            InputStream is = FrameworkCacheBuilder.class.getResourceAsStream(path);
            BuilderClassDef classDef = SmaliParser.parse(path, is, dexBuilder);
            sb.append(ReferenceUtil.getReferenceString(classDef));
            sb.append(':').append(path.substring(1)).append('\n');
        }

        return sb.toString();
    }

}
