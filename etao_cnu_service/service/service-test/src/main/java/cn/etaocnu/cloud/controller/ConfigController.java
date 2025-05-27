package cn.etaocnu.cloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@RequestMapping("/config")
public class ConfigController {
    @Value("${config.info:default}")
    private String info;

    @GetMapping("/info")
    public String getInfo() {
        return info;
    }

    @Value("${common.info:default}")
    private String commonInfo;
    @GetMapping("/common")
    public String common() {
        return commonInfo;
    }
    @Value("${wx.miniapp.appId:default}")
    private String wxMiniappAppId;
    @GetMapping("/appid")
    public String getVxMiniappAppId() {
        return wxMiniappAppId;
    }
}
