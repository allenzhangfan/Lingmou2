package cloud.antelope.lingmou.app.service;

/**
 * 避免创建过多Runnable的对象
 * Created by zhulei on 17/6/27.
 */

public abstract class CustomRunnable<T> implements Runnable {
      /**
       * CustomRunnable 中保存的临时值
       * 比如：seek 的时候代表的是时间点
       * */
       public T value;
}
