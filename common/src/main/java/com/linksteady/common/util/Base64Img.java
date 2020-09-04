package com.linksteady.common.util;

import sun.misc.BASE64Decoder;

import java.io.*;

/**
 * @author hxcao
 * @date 2020/4/17
 */
public class Base64Img {

    /**
     * base64字符串转化成图片
     * @param base64
     * @param fileName
     * @return
     * @throws Exception
     */
    public static File base64ToFile(String base64, String fileName) throws Exception {
        if (base64.contains("data:image")) {
            base64 = base64.substring(base64.indexOf(",") + 1);
        }
        base64 = base64.toString().replace("\r\n", "");
        File file = null;
        //创建文件目录
        String filePath = "file/";
        File dir = new File(filePath);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdirs();
        }
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes = decoder.decodeBuffer(base64);

            file = new File(filePath + File.separator + fileName);
            OutputStream out = new FileOutputStream(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }
}
