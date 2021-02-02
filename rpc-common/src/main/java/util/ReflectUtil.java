package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class ReflectUtil {
    private static final Logger logger = LoggerFactory.getLogger(ReflectUtil.class);
    public static String getMainClassName()
    {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        String mainClassname = stack[stack.length-1].getClassName();
        return mainClassname;
    }
    public static Set<Class<?>> findAllClass(String packName){
        Set<Class<?>> classes = new HashSet<>();
        try {
            String packDirName = packName.replace('.', File.separatorChar);
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packDirName);
            while(urls.hasMoreElements()){
                URL element = urls.nextElement();
                String protocol = element.getProtocol();
                String packPath = element.getPath();
                if("file".equals(protocol)){
                    AddClass(packName, packPath, classes);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }
    public static void AddClass(String packName,String packPath,Set<Class<?>> classes) {
        File dir = new File(packPath);
        if(!dir.exists()||!dir.isDirectory()){
            return;
        }
        File [] files = dir.listFiles(new FileFilter(){

            @Override
            public boolean accept(File file) {
                return file.isDirectory()||file.getName().endsWith(".class");
            }
        });
        for(File file :files){
            if(file.isDirectory()){
                if(!"".equals(packName))
                    AddClass(packName+"."+file.getName(),packPath+File.separator+file.getName(),classes);
                else
                    AddClass(file.getName(), packPath+File.separator+file.getName(),classes);
            }
            else if(file.getName().endsWith(".class")){
                String className = file.getName().substring(0, file.getName().length()-6);
                try {
                    Class clazz = null;
                    if (!"".equals(packName)) {
                        clazz = Thread.currentThread().getContextClassLoader().loadClass(packName + "." + className);
                    }
                    else{
                        clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
                    }
                    classes.add(clazz);
                }
                catch (ClassNotFoundException e) {
                    logger.error("类{}加载失败",packName + className, e);
                }
            }
        }
    }
}
