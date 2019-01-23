package cloud.antelope.lingmou.mvp.model.entity;

/**
 * 作者：陈新明
 * 创建日期：2018/05/17
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class FaceUrlSearchEntity {
    /**
     * face : {"feature":"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","attr":{"female":0,"male":0,"nation":0,"age":0,"pose":{"roll":5.4436584,"yaw":2.0454404,"pitch":3.2916682},"eyeglass":0,"facemask":0},"quality":0.91779315,"rect":{"left":135,"top":176,"width":412,"height":418},"vids":null}
     * checkcode : -1011
     * checkmsg : structrue: no body
     * facemsg : success
     */

    private FaceBean face;
    private int checkcode;
    private String checkmsg;
    private String facemsg;

    public FaceBean getFace() {
        return face;
    }

    public void setFace(FaceBean face) {
        this.face = face;
    }

    public int getCheckcode() {
        return checkcode;
    }

    public void setCheckcode(int checkcode) {
        this.checkcode = checkcode;
    }

    public String getCheckmsg() {
        return checkmsg;
    }

    public void setCheckmsg(String checkmsg) {
        this.checkmsg = checkmsg;
    }

    public String getFacemsg() {
        return facemsg;
    }

    public void setFacemsg(String facemsg) {
        this.facemsg = facemsg;
    }

    public static class FaceBean {
        /**
         * feature : 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
         * attr : {"female":0,"male":0,"nation":0,"age":0,"pose":{"roll":5.4436584,"yaw":2.0454404,"pitch":3.2916682},"eyeglass":0,"facemask":0}
         * quality : 0.91779315
         * rect : {"left":135,"top":176,"width":412,"height":418}
         * vids : null
         */

        private String feature;
        private AttrBean attr;
        private double quality;
        private RectBean rect;
        private Object vids;

        public String getFeature() {
            return feature;
        }

        public void setFeature(String feature) {
            this.feature = feature;
        }

        public AttrBean getAttr() {
            return attr;
        }

        public void setAttr(AttrBean attr) {
            this.attr = attr;
        }

        public double getQuality() {
            return quality;
        }

        public void setQuality(double quality) {
            this.quality = quality;
        }

        public RectBean getRect() {
            return rect;
        }

        public void setRect(RectBean rect) {
            this.rect = rect;
        }

        public Object getVids() {
            return vids;
        }

        public void setVids(Object vids) {
            this.vids = vids;
        }

        public static class AttrBean {
            /**
             * female : 0
             * male : 0
             * nation : 0
             * age : 0
             * pose : {"roll":5.4436584,"yaw":2.0454404,"pitch":3.2916682}
             * eyeglass : 0
             * facemask : 0
             */

            private float female;
            private float male;
            private float nation;
            private int age;
            private PoseBean pose;
            private int eyeglass;
            private int facemask;

            public float getFemale() {
                return female;
            }

            public void setFemale(int female) {
                this.female = female;
            }

            public float getMale() {
                return male;
            }

            public void setMale(int male) {
                this.male = male;
            }

            public float getNation() {
                return nation;
            }

            public void setNation(int nation) {
                this.nation = nation;
            }

            public int getAge() {
                return age;
            }

            public void setAge(int age) {
                this.age = age;
            }

            public PoseBean getPose() {
                return pose;
            }

            public void setPose(PoseBean pose) {
                this.pose = pose;
            }

            public int getEyeglass() {
                return eyeglass;
            }

            public void setEyeglass(int eyeglass) {
                this.eyeglass = eyeglass;
            }

            public int getFacemask() {
                return facemask;
            }

            public void setFacemask(int facemask) {
                this.facemask = facemask;
            }

            public static class PoseBean {
                /**
                 * roll : 5.4436584
                 * yaw : 2.0454404
                 * pitch : 3.2916682
                 */

                private double roll;
                private double yaw;
                private double pitch;

                public double getRoll() {
                    return roll;
                }

                public void setRoll(double roll) {
                    this.roll = roll;
                }

                public double getYaw() {
                    return yaw;
                }

                public void setYaw(double yaw) {
                    this.yaw = yaw;
                }

                public double getPitch() {
                    return pitch;
                }

                public void setPitch(double pitch) {
                    this.pitch = pitch;
                }
            }
        }

        public static class RectBean {
            /**
             * left : 135
             * top : 176
             * width : 412
             * height : 418
             */

            private int left;
            private int top;
            private int width;
            private int height;

            public int getLeft() {
                return left;
            }

            public void setLeft(int left) {
                this.left = left;
            }

            public int getTop() {
                return top;
            }

            public void setTop(int top) {
                this.top = top;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }
        }
    }

//    /**
//     * feature : ZNVtvbl56r2F9ZI8Js4OPNveHT3lqxw9iwy9PUqai7w2ZQC+8pewPZJRP7zMs3W9A+nnOwtvjD2I23c9NaHsu4yJJjv7ev09IuoVvJxxsT0r46U6gJf0PL1vPr3wY6u9ggpyveIImT0cnDs8Q+o8PZLqrj1h9rU8O6C4PW60kj1P+tC7vXzwvQQ78r2LTx49Sgw9vYaJAb73KIK8yDxavA9clj3m9Lw9LI4XPa/8wr0qLOq8SavbvZ4wP7z+Ipa9gBZ+PPliIr2xPE2917YMPqHixr3YDIe950VdPdXFg7zidW691fz4PCcYqDuqSnO8EEstPTqIyDy82io9ZAFYvZiKHrzueOS9TmtxvQq0sb2cFt287KqSPaemOD0KM8K9pUmKPAzFMT0rjbm9tR3PvD4aBL7gLZw9jke4PTxSVj2NiSO7dB4Jvkd4Ej2DzjA8o+0MOyCGuDzaN7I96iWMPaX6VD2PCTC9F1opPd2kpj2KLhC8JKhKvMRB3LwDLA+8TSyMPVunej28fXm9U8IwvdkZAD4JXqw9ObdFvVYIyr3UvJm8omjJvYnLAD17ECQ9QVMxvEDI9zz9jPo7dflvO2RylTsBz2G97ebcPah/yryCnwK9QlePPdEddD1ex2y6Pq+bvYi4Yj0ohdo8VPvmvDGdCT3N6oo8i+wcPVF0Crukx/e78PH6OzhqEjvX+Qw9HE+pvU/mMj2sVBu920lzveCSXz3WYkm9R0oFvV7Fbjyb1kO9exJMvHsUOjxOXRi+tjqOPfOwdT33km29qOUNPj/MGr0I+/A891+GvSR/Vj2eDsM9JZqNPKvLkzz2nYO9OSCYPVOHAr3pyFc8WEMuvDoNAz3k2ao9luS/vU1lW7yjepi9SQ5/PdBFQzyH4Aa9HHbpvTTm7zvdAaO8gHGwPDKFFr2S28C93p9ovBGMDz7IcfM9Fy3zu0sQCb5lU848L70dvSU+wb3+hcs8H25yPXCS1Dw6yLi76RAMPVUs1r1ccvS8quEQvqq76zrUd8O8RAHkPBAVgL2fqhW9OEeGPEaIFT3lctE80rG9u6I/Gj436fY8/0nBvUDAt73D52E9JC+SvOTCHr28J4w9YlS8PSPo47yNBxq9tD7APO7vhr3PM6C76JQjPJOXeb3T+yC82QgOPs9oT73C1Xy9o5cxvekyoLxHHDW9bU/sPBQK3bw0ubA8MySrvK7y7DoSXnY69VyavX6i3DwWDn+7mc8wvEPPer3J1Ea7NHMbPZlD7ryDjwG+LoJJPSy7JD1Uf789sxKUPENABj4egUI9R1n8vOAc3b3QC5O9RJ2+PesFHz1Q1rw88mCBvNK8dD1Cmsu9dl+TPWoz673elDk9GEpsvQ==
//     */
//
//    private String feature;
//
//    public String getFeature() {
//        return feature;
//    }
//
//    public void setFeature(String feature) {
//        this.feature = feature;
//    }

}
