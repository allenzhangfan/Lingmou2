package cloud.antelope.lingmou.mvp.model.entity;


import java.io.Serializable;
import java.util.List;

/**
 * Created by ChenXinming on 2017/11/20.
 * description:
 */

public class FaceKeyEntity implements Serializable {

    /**
     * searchImageType : FACE
     * featureKey : feature:e71473701435fc42d0fbde93fb14bd11
     * imageFileName : PFSB:/bimg/bimg/data/20171120/14_0/ea9ed5be30a39a793e49b97576b995f5
     * meta : {"feature":"U05GQwEAAwAABAAA8JmAA0u1KbxUx/c8NLOtO4otqL3YSY68gCRVPQRJCD5uFgy9U32BvVFbl71YNXO9ummrPYkRgzw4x9y8tZ+YvKYyib2FwUw9GF3ZvHnlhz0aIoI9Q8q8Og/OJL20ji+9lWDXPLXjjr0OSka9ke0XO6haNL2edsU9UfTDO+CKqzzU3O28SxuWvPXP6L228Fm8NXQLvZ/L5LyrEk+80UxRPf7A97spbR67NrCDvOpnwzwIHPc89S+QvGFzkjpRue68V27DPRRFA7076y+8MEJ6PQ+pUz2zXos92mKJPMN9Az4iWeK8t791vUA6KT0KxRO9QZSoPYAIJjyU4Tu7p5KFvSvAFL2AnAc9IZfAvPQBm70ddrW8bK6tPTbTKjwNj7a8zl0LviOopjyc6To9ABiWPczaJL68GAk+IOOnvd7+wLvSIvs7tw7WvVFMBr6iExg70RrBPX2G8Lx6IFK9fFo8PY7nBLwlKuI8ywZ2vWWkqTzV6Z49ev2fvSqQKL1JIoS8EZyIvYKTaT2/MEi9QqKQvZqwdby3DqM8/FNWPUQnfryDybW9npVOPZDqJz321ri88HabvUAH6jySdC09tucnvDphQr0batW7mcoJPYpGvT1bEmg9BrLQu+CS0rxGMp29JDIAOxe5jj2CoCM+xea2vbqipj3IQbS8bcNEPYnrLL3Jv8k8InNtve3Yjr3aM469d2y9vYfWTT2P17o9TSj9Pf8iKTtAH/e9VAJ/vfOCUj104TI9AnV9Pa8Zyj0za5W9F9QmPUiCULw9dyI+N5P/vN45EDwufP69QdG5u6rN2bzN4Uo94zP9PSv2Mz29i829C5E9veu50r0LpA09h8AoO6f3Vb1YDxm8eaw6vbodAz2rFf48LcKkvWLhOr1ZS229syOuPKvxVr2ALK49VqZcPYUJYb2DfQO+DgejPfn/3jzPEHW8TqmFPa8Uxr122SS9N9+tPSYQpT1w6429yjkOPVZyib3gkI68VCjcvO/sET3uxYm9lXygvPenJz1UPE89NkIbvbC6Aj7UzBy7p+QqPWaHjTyvopA9in+XvMjj7ztSPBS9BZd0Pa9ioz05qUu8PWWxvVG8l7xc+gG9aJZfvRgSDj4j4Ny8YpyOPUiDRr3rMXc99IvuvC/cDb102/+8mTF+PQZgWL2NJDe8cTLCPL1iQL01CDO9ZhQ5PZCSp72ilz482ilzPbFMvz38cBQ8ENSWPEw6Mr3I6yy9fAFIPeJaVb2J4CK8KXhHPXeDfz1QI7O9+0WoPfwSEz69/go+ig/ovVLHqz1EVv27dEqTvbeSQD2+Z4e9dRMCvrNtAr0nrai9nDV9vckZw700Pj89VU/3PB5ajb2zWO49AYMfvRRWGL1TVE9Q","featureLen":1044,"result":[{"ATTRIBUTE":{"Age":26,"Attractive":-1,"Beard":0,"EyeGlass":0,"EyeOpen":0,"Gender":1,"Mask":-1,"MouthOpen":0,"Race":-1,"Smile":-1,"SunGlass":0,"Uyghui":0},"Confidence":35,"DataSize":1044,"Offset":0,"Pos":{"Bottom":557,"Left":142,"Right":458,"Top":241}}]}
     */

    private String searchImageType;
    private String featureKey;
    private String imageFileName;
    private MetaBean meta;

    public String getSearchImageType() {
        return searchImageType;
    }

    public void setSearchImageType(String searchImageType) {
        this.searchImageType = searchImageType;
    }

    public String getFeatureKey() {
        return featureKey;
    }

    public void setFeatureKey(String featureKey) {
        this.featureKey = featureKey;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public MetaBean getMeta() {
        return meta;
    }

    public void setMeta(MetaBean meta) {
        this.meta = meta;
    }

    public static class MetaBean implements Serializable{
        /**
         * feature : U05GQwEAAwAABAAA8JmAA0u1KbxUx/c8NLOtO4otqL3YSY68gCRVPQRJCD5uFgy9U32BvVFbl71YNXO9ummrPYkRgzw4x9y8tZ+YvKYyib2FwUw9GF3ZvHnlhz0aIoI9Q8q8Og/OJL20ji+9lWDXPLXjjr0OSka9ke0XO6haNL2edsU9UfTDO+CKqzzU3O28SxuWvPXP6L228Fm8NXQLvZ/L5LyrEk+80UxRPf7A97spbR67NrCDvOpnwzwIHPc89S+QvGFzkjpRue68V27DPRRFA7076y+8MEJ6PQ+pUz2zXos92mKJPMN9Az4iWeK8t791vUA6KT0KxRO9QZSoPYAIJjyU4Tu7p5KFvSvAFL2AnAc9IZfAvPQBm70ddrW8bK6tPTbTKjwNj7a8zl0LviOopjyc6To9ABiWPczaJL68GAk+IOOnvd7+wLvSIvs7tw7WvVFMBr6iExg70RrBPX2G8Lx6IFK9fFo8PY7nBLwlKuI8ywZ2vWWkqTzV6Z49ev2fvSqQKL1JIoS8EZyIvYKTaT2/MEi9QqKQvZqwdby3DqM8/FNWPUQnfryDybW9npVOPZDqJz321ri88HabvUAH6jySdC09tucnvDphQr0batW7mcoJPYpGvT1bEmg9BrLQu+CS0rxGMp29JDIAOxe5jj2CoCM+xea2vbqipj3IQbS8bcNEPYnrLL3Jv8k8InNtve3Yjr3aM469d2y9vYfWTT2P17o9TSj9Pf8iKTtAH/e9VAJ/vfOCUj104TI9AnV9Pa8Zyj0za5W9F9QmPUiCULw9dyI+N5P/vN45EDwufP69QdG5u6rN2bzN4Uo94zP9PSv2Mz29i829C5E9veu50r0LpA09h8AoO6f3Vb1YDxm8eaw6vbodAz2rFf48LcKkvWLhOr1ZS229syOuPKvxVr2ALK49VqZcPYUJYb2DfQO+DgejPfn/3jzPEHW8TqmFPa8Uxr122SS9N9+tPSYQpT1w6429yjkOPVZyib3gkI68VCjcvO/sET3uxYm9lXygvPenJz1UPE89NkIbvbC6Aj7UzBy7p+QqPWaHjTyvopA9in+XvMjj7ztSPBS9BZd0Pa9ioz05qUu8PWWxvVG8l7xc+gG9aJZfvRgSDj4j4Ny8YpyOPUiDRr3rMXc99IvuvC/cDb102/+8mTF+PQZgWL2NJDe8cTLCPL1iQL01CDO9ZhQ5PZCSp72ilz482ilzPbFMvz38cBQ8ENSWPEw6Mr3I6yy9fAFIPeJaVb2J4CK8KXhHPXeDfz1QI7O9+0WoPfwSEz69/go+ig/ovVLHqz1EVv27dEqTvbeSQD2+Z4e9dRMCvrNtAr0nrai9nDV9vckZw700Pj89VU/3PB5ajb2zWO49AYMfvRRWGL1TVE9Q
         * featureLen : 1044
         * result : [{"ATTRIBUTE":{"Age":26,"Attractive":-1,"Beard":0,"EyeGlass":0,"EyeOpen":0,"Gender":1,"Mask":-1,"MouthOpen":0,"Race":-1,"Smile":-1,"SunGlass":0,"Uyghui":0},"Confidence":35,"DataSize":1044,"Offset":0,"Pos":{"Bottom":557,"Left":142,"Right":458,"Top":241}}]
         */

        private String feature;
        private int featureLen;
        private List<ResultBean> result;

        public String getFeature() {
            return feature;
        }

        public void setFeature(String feature) {
            this.feature = feature;
        }

        public int getFeatureLen() {
            return featureLen;
        }

        public void setFeatureLen(int featureLen) {
            this.featureLen = featureLen;
        }

        public List<ResultBean> getResult() {
            return result;
        }

        public void setResult(List<ResultBean> result) {
            this.result = result;
        }

        public static class ResultBean {
            /**
             * ATTRIBUTE : {"Age":26,"Attractive":-1,"Beard":0,"EyeGlass":0,"EyeOpen":0,"Gender":1,"Mask":-1,"MouthOpen":0,"Race":-1,"Smile":-1,"SunGlass":0,"Uyghui":0}
             * Confidence : 35
             * DataSize : 1044
             * Offset : 0
             * Pos : {"Bottom":557,"Left":142,"Right":458,"Top":241}
             */

            private ATTRIBUTEBean ATTRIBUTE;
            private int Confidence;
            private int DataSize;
            private int Offset;
            private PosBean Pos;

            public ATTRIBUTEBean getATTRIBUTE() {
                return ATTRIBUTE;
            }

            public void setATTRIBUTE(ATTRIBUTEBean ATTRIBUTE) {
                this.ATTRIBUTE = ATTRIBUTE;
            }

            public int getConfidence() {
                return Confidence;
            }

            public void setConfidence(int Confidence) {
                this.Confidence = Confidence;
            }

            public int getDataSize() {
                return DataSize;
            }

            public void setDataSize(int DataSize) {
                this.DataSize = DataSize;
            }

            public int getOffset() {
                return Offset;
            }

            public void setOffset(int Offset) {
                this.Offset = Offset;
            }

            public PosBean getPos() {
                return Pos;
            }

            public void setPos(PosBean Pos) {
                this.Pos = Pos;
            }

            public static class ATTRIBUTEBean {
                /**
                 * Age : 26
                 * Attractive : -1
                 * Beard : 0
                 * EyeGlass : 0
                 * EyeOpen : 0
                 * Gender : 1
                 * Mask : -1
                 * MouthOpen : 0
                 * Race : -1
                 * Smile : -1
                 * SunGlass : 0
                 * Uyghui : 0
                 */

                private int Age;
                private int Attractive;
                private int Beard;
                private int EyeGlass;
                private int EyeOpen;
                private int Gender;
                private int Mask;
                private int MouthOpen;
                private int Race;
                private int Smile;
                private int SunGlass;
                private int Uyghui;

                public int getAge() {
                    return Age;
                }

                public void setAge(int Age) {
                    this.Age = Age;
                }

                public int getAttractive() {
                    return Attractive;
                }

                public void setAttractive(int Attractive) {
                    this.Attractive = Attractive;
                }

                public int getBeard() {
                    return Beard;
                }

                public void setBeard(int Beard) {
                    this.Beard = Beard;
                }

                public int getEyeGlass() {
                    return EyeGlass;
                }

                public void setEyeGlass(int EyeGlass) {
                    this.EyeGlass = EyeGlass;
                }

                public int getEyeOpen() {
                    return EyeOpen;
                }

                public void setEyeOpen(int EyeOpen) {
                    this.EyeOpen = EyeOpen;
                }

                public int getGender() {
                    return Gender;
                }

                public void setGender(int Gender) {
                    this.Gender = Gender;
                }

                public int getMask() {
                    return Mask;
                }

                public void setMask(int Mask) {
                    this.Mask = Mask;
                }

                public int getMouthOpen() {
                    return MouthOpen;
                }

                public void setMouthOpen(int MouthOpen) {
                    this.MouthOpen = MouthOpen;
                }

                public int getRace() {
                    return Race;
                }

                public void setRace(int Race) {
                    this.Race = Race;
                }

                public int getSmile() {
                    return Smile;
                }

                public void setSmile(int Smile) {
                    this.Smile = Smile;
                }

                public int getSunGlass() {
                    return SunGlass;
                }

                public void setSunGlass(int SunGlass) {
                    this.SunGlass = SunGlass;
                }

                public int getUyghui() {
                    return Uyghui;
                }

                public void setUyghui(int Uyghui) {
                    this.Uyghui = Uyghui;
                }
            }

            public static class PosBean {
                /**
                 * Bottom : 557
                 * Left : 142
                 * Right : 458
                 * Top : 241
                 */

                private int Bottom;
                private int Left;
                private int Right;
                private int Top;

                public int getBottom() {
                    return Bottom;
                }

                public void setBottom(int Bottom) {
                    this.Bottom = Bottom;
                }

                public int getLeft() {
                    return Left;
                }

                public void setLeft(int Left) {
                    this.Left = Left;
                }

                public int getRight() {
                    return Right;
                }

                public void setRight(int Right) {
                    this.Right = Right;
                }

                public int getTop() {
                    return Top;
                }

                public void setTop(int Top) {
                    this.Top = Top;
                }
            }
        }
    }
}
