package com.neo.util;

import com.aliyun.oss.*;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.*;
import java.util.UUID;


/**
 * @author RLY
 * ������OSS�ϴ��ļ�����
 * ֧����ͨ�ļ��ϴ������ƴ�С�ļ��ϴ�,���ƴ�СͼƬ�ϴ�
 */
public class OSSClientUtil {

    private static Logger log = LoggerFactory.getLogger(OSSClientUtil.class);

    /**������API���ڻ���������*/
    public static String ENDPOINT = "oss-cn-shanghai.aliyuncs.com";
    /**OSSǩ��key*/
    public static String ACCESS_KEY_ID = "LTAIgSYh9nYH1wxH";
    /**OSSǩ����Կ*/
    public static String ACCESS_KEY_SECRET = "WlChQR3JtZ12rq3g94Sj1gALkx9ho0";
    /**�洢�ռ�����*/
    public static String BUCKETNAME = "biyesheji-lawliet233";

    /**
     * ��ȡossClient
     * @return
     */
    public static OSSClient ossClientInitialization(){
        return new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
    }

    /**
     * �ж��Ƿ����bucketName
     * @return
     */
    private static boolean hasBucket(OSSClient ossClient){
        return ossClient.doesBucketExist(BUCKETNAME);
    }

    public static String createFileName(String mime) { // ��Ҫ����һ���ļ�����
        String fileName = UUID.randomUUID() + "_" + mime;
        return fileName;
    }

    /**
     * �ϴ��ļ���ǩ
     * @param label
     */
    public static void uploadLabel(String label) {
        // ����OSSClientʵ��
        OSSClient ossClient = new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        // �ϴ��ļ���ǩ
        ossClient.putObject(BUCKETNAME, label, new ByteArrayInputStream(label.getBytes()));
        // �ر�OSSClient
        ossClient.shutdown();
    }

    /**
     * �жϱ�ǩ�Ƿ��Ѿ�����
     * @param fileName
     * @return
     */
    public static boolean isFileExist(String fileName) {
        // ����OSSClientʵ����
        OSSClient ossClient = new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);

        // �ж��ļ��Ƿ���ڡ�doesObjectExist����һ������isOnlyInOSS�����Ϊtrue�����302�ض���������
        //Ϊfalse������302�ض������
        boolean found = ossClient.doesObjectExist(BUCKETNAME, fileName);
        //System.out.println(found);

        // �ر�OSSClient��
        ossClient.shutdown();

        return found;
    }


    /**
     * �ϴ��ļ���OSS������  ���ͬ���ļ��Ḳ�Ƿ������ϵ�
     *
     * @param inputStream �ļ���
     * @param fileName �ļ����� ������׺��
     * @return ������"" ,ΨһMD5����ǩ��
     */
    public static String uploadFile( InputStream inputStream, String fileName) {
        String resultStr  = "";
        try {
            /**
             * ����OSS�ͻ���
             */
            OSSClient ossClient = new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            //�����ϴ�Object��Metadata
            ObjectMetadata metadata  = new ObjectMetadata();
            //�ϴ����ļ��ĳ���
            metadata .setContentLength(inputStream.available());
            //ָ����Object������ʱ����ҳ�Ļ�����Ϊ
            metadata .setCacheControl("no-cache");
            //ָ����Object������Header
            metadata .setHeader("Pragma", "no-cache");
            //ָ����Object������ʱ�����ݱ����ʽ
            metadata.setContentEncoding("utf-8");
            //�ļ���MIME�������ļ������ͼ���ҳ���룬�������������ʲô��ʽ��ʲô�����ȡ�ļ�������û�û��ָ�������Key���ļ�������չ�����ɣ�
            //���û����չ������Ĭ��ֵapplication/octet-stream
            metadata .setContentType(getcontentType(fileName.substring(fileName.lastIndexOf("."))));
            metadata .setContentDisposition("inline;filename=" + fileName);
            //�ϴ��ļ�
            PutObjectResult putResult = ossClient.putObject(BUCKETNAME,fileName, inputStream, metadata);
            //�������
            resultStr = putResult.getETag();
        } catch (IOException e) {
            log.error("�ϴ�������OSS�������쳣." + e.getMessage(), e);
        }
        return resultStr;
    }

    /**
     * Description: �ж�OSS�����ļ��ϴ�ʱ�ļ���contentType
     * @param FilenameExtension �ļ���׺
     * @return String
     */
    public static String getcontentType(String FilenameExtension) {
        if (FilenameExtension.equalsIgnoreCase(".bmp")) {
            return "image/bmp";
        }
        if (FilenameExtension.equalsIgnoreCase(".gif")) {
            return "image/gif";
        }
        if (FilenameExtension.equalsIgnoreCase(".jpeg") ||
                FilenameExtension.equalsIgnoreCase(".jpg") ||
                FilenameExtension.equalsIgnoreCase(".png")) {
            return "image/jpeg";
        }
        if (FilenameExtension.equalsIgnoreCase(".html")) {
            return "text/html";
        }
        if (FilenameExtension.equalsIgnoreCase(".txt")) {
            return "text/plain";
        }
        if (FilenameExtension.equalsIgnoreCase(".vsd")) {
            return "application/vnd.visio";
        }
        if (FilenameExtension.equalsIgnoreCase(".pptx") ||
                FilenameExtension.equalsIgnoreCase(".ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (FilenameExtension.equalsIgnoreCase(".docx") ||
                FilenameExtension.equalsIgnoreCase(".doc")) {
            return "application/msword";
        }
        if (FilenameExtension.equalsIgnoreCase(".xml")) {
            return "text/xml";
        }
        return "image/jpeg";
    }

}