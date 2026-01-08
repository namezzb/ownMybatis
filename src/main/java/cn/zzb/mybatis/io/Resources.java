package cn.zzb.mybatis.io;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Resources {

    public static Reader getResourceAsReader(String resource) {
        return new InputStreamReader(getResourceAsStream(resource));
    }

    public static InputStream getResourceAsStream(String resource) {
        ClassLoader[] classLoader = new ClassLoader[]{Thread.currentThread().getContextClassLoader(),
                                                        ClassLoader.getSystemClassLoader()};
        for(ClassLoader cl : classLoader){
            InputStream inputStream = cl.getResourceAsStream(resource);
            if(inputStream != null){
                return inputStream;
            }
        }
        return null;
    }

    public static Class<?> classForName(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }
}
