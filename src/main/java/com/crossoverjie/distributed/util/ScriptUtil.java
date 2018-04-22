package com.crossoverjie.distributed.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 22/04/2018 15:55
 * @since JDK 1.8
 */
public class ScriptUtil {

    /**
     * return lua script String
     * @param path
     * @return
     */
    public static String getScript(String path){
        String script = null;
        URL resource = ScriptUtil.class.getResource("/" + path);
        String fileName = resource.getFile();

        FileInputStream in = null;
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long length = file.length();
        byte[] fileContent = new byte[length.intValue()];
        try {
            in = new FileInputStream(file);
            in.read(fileContent);

            script = new String(fileContent, encoding);
        } catch (IOException e) {
            System.err.println(e.getStackTrace());
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                System.err.println(e.getStackTrace());
            }
        }

        return script;
    }
}
