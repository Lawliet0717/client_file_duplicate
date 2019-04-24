package com.neo.controller;

import com.neo.util.BloomFileter;
import com.neo.util.BytesToHex;
import com.neo.util.DESUtil;
import com.neo.util.OSSClientUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.BitSet;

import org.apache.log4j.Logger;

/**
 * @author RLY
 */
@Controller
public class UploadController {

     private static Logger logger = Logger.getLogger(UploadController.class);
    /**
     * 保存到项目根目录下
     */
    @Value("${pic.path}")
    private String UPLOAD_FOLDER;

    //保存到指定目录
    //private static String UPLOAD_FOLDER = "E://temp//";



    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping("/upload")
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws Exception {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:uploadStatus";
        }

        try {
            //生成标签
            String label = DigestUtils.sha1Hex(new String(file.getBytes()));
            //检查云端是否存在标签
            boolean found = OSSClientUtil.isFileExist(label);
            //云端不存在标签，继续上传标签以及加密后的文件
            if(found == false) {
                //文件的明文
                String plainText = new String(file.getBytes());
                //上传标签
                OSSClientUtil.uploadString(label);
                //生成DES加密用的秘钥
                String key = DigestUtils.md5Hex(new String(file.getBytes())).substring(0,8);
                byte[] keys = key.getBytes();
                //加密文件得到密文
                byte[] desResult = DESUtil.encryptDES(file.getBytes(), keys);
                String ciperText = BytesToHex.fromBytesToHex(desResult);
                //生成布隆过滤器
                BloomFileter fileter = new BloomFileter(8);
                String[] str = new String[8];
                int length = plainText.length();
                int n = length/8;
                for(int i = 0; i < 8; i++) {
                    str[i] = plainText.substring(i*n, i*n + n);
                    fileter.addIfNotExist(str[i]);
                }
                //布隆过滤器的标记数组，作为文件的签名一起上传
                BitSet bitSet = fileter.getBitSet();
                String fileName = OSSClientUtil.createFileName(file.getOriginalFilename());
                //上传文件
                fileName=OSSClientUtil.uploadFile(file.getInputStream(),fileName);
                //上传文件签名
                OSSClientUtil.uploadString(bitSet.toString());

                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOAD_FOLDER + file.getOriginalFilename());
                //如果没有files文件夹，则创建
                if (!Files.isWritable(path)) {
                    Files.createDirectories(Paths.get(UPLOAD_FOLDER));
                }
                //文件写入指定路径
                Files.write(path, bytes);

                redirectAttributes.addFlashAttribute("message",
                        "You successfully uploaded '" + file.getOriginalFilename() + "'");
            } else {
                // 云端已经存在文件
                logger.info("OSS is already existing this label！");
                redirectAttributes.addFlashAttribute("message",
                         file.getOriginalFilename() + " is already exist");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/uploadStatus";
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }

}