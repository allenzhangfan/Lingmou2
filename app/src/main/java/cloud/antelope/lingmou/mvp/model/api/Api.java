/**
 * Copyright 2017 JessYan
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cloud.antelope.lingmou.mvp.model.api;

import cloud.antelope.lingmou.common.UrlConstant;

/**
 * ================================================
 * 存放一些与 API 有关的东西,如请求地址,请求码等
 * <p>
 * Created by JessYan on 08/05/2016 11:25
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public interface Api {
    // String APP_DOMAIN = "https://api.github.com";
    // String APP_DOMAIN = "http://192.168.100.31/service/";
    String APP_DOMAIN = UrlConstant.LD_SERVER;
    // String APP_DOMAIN = "http://192.168.10.167/service/";
//    String APP_DOMAIN = "http://218.95.36.58/service/";
    String RequestSuccess = "0";

    /**
     * 羚羊云的BaseUrl.
     */
    String LY_URL = UrlConstant.LY_URL;

}
