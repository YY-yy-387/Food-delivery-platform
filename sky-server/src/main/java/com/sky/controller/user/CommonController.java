package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * C端-通用接口（图片下载）
 */
@RestController("userCommonController")
@RequestMapping("/user/common")
@Api(tags = "C端-通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件下载（小程序端展示图片使用）
     * @param name 文件名或图片URL
     * @param response
     */
    @GetMapping("/download")
    @ApiOperation("文件下载")
    public void download(@RequestParam("name") String name, HttpServletResponse response) {
        log.info("文件下载：{}", name);
        try {
            aliOssUtil.download(name, response.getOutputStream());
        } catch (IOException e) {
            log.error("文件下载失败：{}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}