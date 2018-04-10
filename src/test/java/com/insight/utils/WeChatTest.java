package com.insight.utils;

import com.insight.utils.wechat.WeChatHelper;
import junit.framework.TestCase;
import org.junit.Test;


/**
 * @author luwenbao
 * @date 2018/1/5.
 * @remark
 */
public class WeChatTest extends TestCase {

    @Test
    public void testGetUserInfo(){
        WeChatHelper.getUserInfo("111111","222222","33333");
    }
}
