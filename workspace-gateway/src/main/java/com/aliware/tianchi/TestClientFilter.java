package com.aliware.tianchi;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

/**
 * @author daofeng.xjf
 *
 * 客户端过滤器
 * 可选接口
 * 用户可以在客户端拦截请求和响应,捕获 rpc 调用时产生、服务端返回的已知异常。
 */
@Activate(group = Constants.CONSUMER)
public class TestClientFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        URL url = invoker.getUrl();
        String methodName = invocation.getMethodName();
        RpcStatus count = RpcStatus.getStatus(invoker.getUrl(), invocation.getMethodName());
        count.beginCount(url, methodName);

        boolean isSuccess = true;
        long begin = System.currentTimeMillis();
        try{
            Result result = invoker.invoke(invocation);
            return result;
        }catch (Exception e){
            isSuccess = false;
            throw e;
        } finally {
            count.endCount(url, methodName, System.currentTimeMillis() - begin, isSuccess);
        }

    }

    @Override
    public Result onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
        return result;
    }
}
