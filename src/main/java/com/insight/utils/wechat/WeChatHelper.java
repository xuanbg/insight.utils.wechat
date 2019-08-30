package com.insight.utils.wechat;

import com.insight.util.Json;
import com.insight.util.http.HttpClientUtil;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;


/**
 * @author luwenbao
 * @date 2018/1/5.
 * @remark 微信相关帮助类
 */
@Component
public class WeChatHelper {

    /**
     * 获取微信token的URL
     */
    private static String getTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token";

    /**
     * 获取微信用户信息的URL
     */
    private static String getUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo";

    /**
     * 授权类型
     */
    private static String grantType = "authorization_code";


    private WeChatHelper(){

    }
    /**
     * 获取微信用户信息
     *
     * @param code        微信授权码
     * @param weChatAppId 微信appId
     * @param secret      微信appId 秘钥
     * @return WeChatUser
     */
    public WeChatUser getUserInfo(String code, String weChatAppId, String secret) {
        String url = buildGetTokenUrl(code, weChatAppId, secret);
        String tokenJson = HttpClientUtil.httpClientGet(url,null,"utf-8");
        if (!checkWeChatResult(tokenJson)) {
            String msg = "AppID is: " + weChatAppId + "\r\nCode is: " + code + "\r\nSecret is: " + secret + "\r\nResult is:" + tokenJson;
            LogFactory.getLog(Json.class).error(msg);

            return null;
        }

        WeChatToken token = Json.toBean(tokenJson, WeChatToken.class);
        if (token == null) {
            LogFactory.getLog(Json.class).error(tokenJson);
            return null;
        }

        url = buildGetUserInfoUrl(token.getOpenid(), token.getAccess_token());
        String resultJson = HttpClientUtil.httpClientGet(url,null,"utf-8");
        if (!checkWeChatResult(resultJson)) {
            String msg = "AppID is: " + weChatAppId + "\r\nToken is: " + token.getAccess_token() + "\r\nResult is:" + resultJson;
            LogFactory.getLog(Json.class).error(msg);

            return null;
        }

        return Json.toBean(resultJson, WeChatUser.class);
    }

    /**
     * 检查微信接口返回值
     *
     * @param resultJson 返回值
     * @return 是否合法
     */
    private Boolean checkWeChatResult(String resultJson) {
        return Optional.ofNullable(Json.toMap(resultJson)).orElse(new HashMap(16)).containsKey("openid");
    }

    /**
     * 创建获取微信token的访问url
     *
     * @param code        微信授权码
     * @param weChatAppId 微信appId
     * @param secret      微信公共号秘钥
     * @return URL
     */
    private String buildGetTokenUrl(String code, String weChatAppId, String secret) {
        return String.format("%s?appid=%s&secret=%s&grant_type=%s&code=%s", getTokenUrl, weChatAppId, secret, grantType, code);
    }

    /**
     * 创建获取用户信息的访问url
     *
     * @param openId      微信OpenID
     * @param accessToken 微信访问令牌
     * @return URL
     */
    private String buildGetUserInfoUrl(String openId, String accessToken) {
        return String.format("%s?access_token=%s&openid=%s&lang=zh_CN", getUserInfoUrl, accessToken, openId);
    }
}
