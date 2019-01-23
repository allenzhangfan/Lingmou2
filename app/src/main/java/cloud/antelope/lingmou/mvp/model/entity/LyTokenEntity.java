package cloud.antelope.lingmou.mvp.model.entity;

import java.util.List;

/**
 * Created by ChenXinming on 2017/11/21.
 * description:
 */

public class LyTokenEntity extends LmBaseData {

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * cameraId : 347
         * token : 538378302_3221225472_1511345728_d3a381282b4cac14df9f1b48d2e41726
         */

        private String cameraId;
        private String token;

        public String getCameraId() {
            return cameraId;
        }

        public void setCameraId(String cameraId) {
            this.cameraId = cameraId;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
