/*
 * Copyright (C) 2017 LingDaNet.Co.Ltd. All Rights Reserved.
 */

package cloud.antelope.lingmou.app.utils;

/**
 * 作者：安兴亚
 * 创建日期：2017/08/22
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class JsUtils {


    /**
     * 在网页加载完成之后，立即调用该方法给html页面注入js代码并执行
     * 用于获取html页面中的所有图片的链接地址.
     */
    public static String getImages() {
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:(function(){\n");
        //        sb.append("    var videoBg = document.getElementById('video-bg');\n");
        //        sb.append("    if (videoBg) {\n");
        //        sb.append("        var playicon = document.getElementById('playIcon');\n");
        //        sb.append("        videoBg.removeChild(playIcon)\n");
        //        sb.append("    }\n");
        sb.append("    sessionStorage.setItem('inapp', true);\n");
        sb.append("    var imgs = document.getElementsByTagName('img');\n");
        sb.append("    if (imgs && imgs.length > 0) {\n");
        sb.append("        var imgSrc = '';\n");
        sb.append("        for (var i = 0; i < imgs.length; i++) {\n");
        sb.append("            if (i != imgs.length - 1) {\n");
        sb.append("                imgSrc = imgSrc + imgs[i].src + '@@$@@';\n");
        sb.append("            } else {\n");
        sb.append("                imgSrc = imgSrc + imgs[i].src;\n");
        sb.append("            }\n");
        sb.append("        }\n");
        sb.append("        window.location.href = 'js://webview?allImg=' + imgSrc;\n");
        sb.append("    }\n");
        sb.append("})();");
        return sb.toString();
    }


    /**
     * 添加告警线索提交按钮的点击事件.
     *
     * @param caseId
     * @return
     */
    public static String addSubmitClueListener(String caseId) {
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:(function(){\n");
        sb.append("    var report = document.getElementById('comment');\n");
        sb.append("    if (report) {\n");
        sb.append("        report.onclick = function() {\n");
        sb.append("            window.location.href = 'js://webview?report=");
        sb.append(caseId + "';\n");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("})();");
        return sb.toString();
    }

    /**
     * 在网页加载完成之后，立即调用该方法给html页面注入js代码并执行
     * 给网页中除标题Logo图标以外的所有图片添加点击事件
     * 并在点击事件中通过js代码调用java代码的 openImage 方法.
     */
    public static String addImageClickListener() {
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:(function(){\n");
        sb.append("    var imgs = document.getElementsByTagName('img');\n");
        sb.append("    if (imgs) {\n");
        sb.append("        for (var i = 0; i < imgs.length; i++) {\n");
        sb.append("            imgs[i].onclick = function() {\n");
        sb.append("                window.location.href = 'js://webview?img=' + this.src;\n");
        sb.append("            };\n");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("})();");
        return sb.toString();
    }

    /**
     * 添加视频点击事件.
     */
    public static String addVideoClickListener() {
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:(function(){\n");
        sb.append("    var myVideos = document.getElementsByClassName('myVideo');\n");  // 获取所有的video标签
        sb.append("    myVideos = Array.prototype.slice.call(myVideos);\n");
        sb.append("    if (myVideos.length > 0) {\n");
        sb.append("            window.location.href = 'js://webview?hasVideo=' + true;\n");
        // sb.append("myVideos.forEach(function(myVideo,k){");
        sb.append("        for (var i = 0; i < myVideos.length; i++) {\n");
        sb.append("(function(j){");
        sb.append("            var myVideo = myVideos[j];\n");
        //        sb.append("        var playicon = document.getElementById('playicon');\n");
        sb.append("            myVideo.removeAttribute('playsinline');\n");
        //        sb.append("        myVideo.removeAttribute('controls');\n");
        //        sb.append("        playicon.style.display='none';\n");
        sb.append("            var flag = false;\n"); // 暂停
        sb.append("            myVideo.addEventListener('play', function(){\n");
        sb.append("                if (!flag) {\n"); //播放
        sb.append("                    flag = true;\n");
        sb.append("                    window.location.href = 'js://webview?play=' + flag + '&id=' + myVideo.getAttribute(\'id\');\n");
        sb.append("                }\n");
        sb.append("            });\n");
        sb.append("            myVideo.addEventListener('pause', function(){\n");
        sb.append("                if (flag) {\n"); // 暂停
        sb.append("                    flag = false;\n");
        //        sb.append("                alert('pause');\n");
        sb.append("                }\n");
        sb.append("            });\n");
        sb.append("})(i)");
        sb.append("        }\n");
        sb.append("    }\n");
        //        sb.append("    myVideo.onclick = function() {\n");
        //        sb.append("        myVideo.setAttribute('controls','controls');\n");
        //        sb.append("        window.location.href = 'js://webview?myVideo=true';\n");
        //        sb.append("    };\n");
        sb.append("})();");

        return sb.toString();
    }

    /**
     * 播放视频.
     */
    public static String playVideo(String videoId) {
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:(function(){\n");
        sb.append("    var myVideo = document.getElementById('" + videoId + "');\n");
        sb.append("    if (myVideo) {\n");
        sb.append("        if (myVideo.paused) {\n");
        sb.append("            myVideo.play();\n");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("})();");
        return sb.toString();
    }

    /**
     * 暂停视频.
     */
    public static String pauseVideo() {
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:(function(){\n");
        sb.append("    var myVideos = document.getElementsByClassName('myVideo');\n");  // 获取所有的video标签
        sb.append("    if (myVideos && myVideos.length > 0) {\n");
        sb.append("        for (var i = 0; i < myVideos.length; i++) {\n");
        sb.append("            var myVideo = myVideos[i];\n");
        sb.append("            if (myVideo) {\n");
        sb.append("                if (!myVideo.paused) {\n");
        sb.append("                    myVideo.pause();\n");
        sb.append("                }\n");
        sb.append("            }\n");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("})();");
        return sb.toString();
    }

    /**
     * 检测视频的播放状态，如果视频正在播放，则先进行暂停处理
     * 并弹出alert，通知Android层已经转换到移动网络状态了.
     */
    public static String checkVideoAndPause() {
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:(function(){\n");
        sb.append("    var myVideos = document.getElementsByClassName('myVideo');\n");  // 获取所有的video标签
        sb.append("    if (myVideos.length > 0) {\n");
        // sb.append("                window.location.href = 'js://webview?changeTo3G=' + true + '&id=\'myVideo_0\'' ;\n");
        sb.append("        for (var i = 0; i < myVideos.length; i++) {\n");
        // sb.append("         (function(j){");
        sb.append("            var myVideo = myVideos[i];\n");
        sb.append("            if (!myVideo.paused) {\n");
        sb.append("                myVideo.pause();\n");
        sb.append("                window.location.href = 'js://webview?changeTo3G=' + true + '&id=' + myVideo.getAttribute(\'id\');\n");
        // sb.append("                break;\n");
        sb.append("            }\n");
        // sb.append("})(i)");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("})();");
        return sb.toString();
    }

    /**
     * 注入签到攻略的JS点击事件
     */
    public static String signStrategy() {
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:(function(){\n");
        sb.append("    var signTaste = document.getElementById('sign-taste');\n");
        sb.append("    if (signTaste) {\n");
        sb.append("        signTaste.onclick = function() {\n");
        sb.append("            window.location.href = 'js://webview?signStrategy=true';\n");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("})();");
        return sb.toString();
    }
}
