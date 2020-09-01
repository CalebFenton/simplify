package org.cf.smalivm.dex;

import com.google.common.io.CharSink;
import com.google.common.io.Files;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.ClassManagerFactory;
import org.cf.smalivm.type.VirtualClass;
import org.cf.util.Utils;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.DexBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * This updates framework_classes.cfg which contains a list of all framework classes and Smali paths.
 * It's used as a cache so the paths don't have to be enumerated when smalivm starts.
 * Then, it generates Java .class files for each framework class and creates an android-##.jar.
 * This is also used as a cache so smalivm doesn't have to generate Java classes when framework
 * class objects are instantiated.
 *
 * @author caleb
 */

public class FrameworkJarBuilder {

    private static final String FRAMEWORK_ROOT = "/framework/smali";

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: frameworkJarBuilder <framework resource path (e.g. android-25)> <output jar path>");
            System.exit(-1);
        }

        String cacheFileName = "src/main/resources/framework_classes.cfg";
        CharSink cacheSink = Files.asCharSink(new File(cacheFileName), StandardCharsets.UTF_8);
        String resPath = FRAMEWORK_ROOT + "/" + args[0];
        System.out.println("Building framework cache from " + resPath);
        String cache = buildFrameworkCache(resPath);
        cacheSink.write(cache);
        System.out.println("Saved cache to " + cacheFileName + " (" + cache.getBytes().length + " bytes)");

        String outPath = args[1];
        System.out.println("Beginning class generation");
        ClassManager classManager = new ClassManagerFactory().build();
        ClassBuilder builder = new ClassBuilder(classManager);
        Set<String> classNames = buildJar(classManager, builder, outPath);
        System.out.println("Saved " + classNames.size() + " classes to " + outPath);
    }

    private static String buildFrameworkCache(String resPath) throws Exception {
        List<File> resFiles = new LinkedList<>();
        try {
            URL url = FrameworkJarBuilder.class.getResource(resPath);
            File file = new File(url.toURI());
            resFiles.addAll(Utils.getFilesWithSmaliExtension(file));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        List<String> resourcePaths = resFiles
            .parallelStream()
            .map(File::getAbsolutePath)
            .map(p -> p.substring(p.lastIndexOf(FRAMEWORK_ROOT)))
            .sorted() // so they look nice in the output file
            .collect(Collectors.toList());

        DexBuilder dexBuilder = new DexBuilder(Opcodes.getDefault());
        StringBuilder sb = new StringBuilder();
        for (String path : resourcePaths) {
            InputStream is = FrameworkJarBuilder.class.getResourceAsStream(path);
            BuilderClassDef classDef = SmaliParser.parse(path, is, dexBuilder);
            sb.append(ReferenceUtil.getReferenceString(classDef));
            sb.append(':').append(path.substring(1)).append('\n');
        }

        return sb.toString();
    }

    private static Set<String> buildJar(ClassManager classManager, ClassBuilder builder, String outPath) throws IOException {
        Set<String> classNames = classManager.getFrameworkClassNames();
        JarOutputStream out = new JarOutputStream(new FileOutputStream(outPath));
        for (String className : classNames) {
            VirtualClass virtualClass = classManager.getVirtualClass(className);
            ClassDef classDef = virtualClass.getClassDef();
            byte[] b = builder.build(classDef);

            String entryName = className.substring(1, className.length() - 1);
            out.putNextEntry(new ZipEntry(entryName + ".class"));
            out.write(b);
            out.closeEntry();
        }
        out.close();

        return classNames;
    }

}
