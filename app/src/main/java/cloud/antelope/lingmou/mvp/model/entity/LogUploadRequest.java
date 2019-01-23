package cloud.antelope.lingmou.mvp.model.entity;

public class LogUploadRequest {

    /**
     * function : 104105
     * module : 104100
     * description : 查看点位【永丰县长途汽车站】 2018.12.11 10:54:27的人脸抓拍照片
     */

    public int function;
    public int module;
    public String description;

    public LogUploadRequest(int module,int function,  String description) {
        this.function = function;
        this.module = module;
        this.description = description;
    }

    @Override
    public String toString() {
        return "LogUploadRequest{" +
                "function=" + function +
                ", module=" + module +
                ", description='" + description + '\'' +
                '}';
    }
}
