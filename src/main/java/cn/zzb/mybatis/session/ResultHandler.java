package cn.zzb.mybatis.session;

public interface ResultHandler {

    /**
     * 处理结果
     */
    void handleResult(ResultContext context);

}
