package com.crossoverjie.distributed.lock;

import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 19/04/2018 23:52
 * @since JDK 1.8
 */
public class FileTest {

    @Test
    public void fileReadTest() throws IOException {
        URL resource = this.getClass().getResource("/" + "lock.lua");
        String fileName = resource.getFile();

        FileInputStream in = null;
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long length = file.length();
        byte[] fileContent = new byte[length.intValue()];
        try {
            in = new FileInputStream(file);
            in.read(fileContent);

            String script = new String(fileContent, encoding);
            System.out.println(script);
        } catch (IOException e) {
            System.err.println(e.getStackTrace());
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                System.err.println(e.getStackTrace());
            }
        }

    }
}
