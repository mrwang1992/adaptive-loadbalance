package com.aliware.tianchi;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcStatus;
import org.apache.dubbo.rpc.cluster.LoadBalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author daofeng.xjf
 * <p>
 * 负载均衡扩展接口
 * 必选接口，核心接口
 * 此类可以修改实现，不可以移动类或者修改包名
 * 选手需要基于此类实现自己的负载均衡算法
 */
public class UserLoadBalance implements LoadBalance {

    private long start_time = System.currentTimeMillis();

    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        long uptime = System.currentTimeMillis() - start_time;

        if (uptime < 20000) {
            return invokers.get(ThreadLocalRandom.current().nextInt(invokers.size()));
        }

        int lenght = invokers.size();
        String methodName = invocation.getMethodName();

        int select_index = 0;

        Invoker invoker = invokers.get(select_index);
        RpcStatus count = RpcStatus.getStatus(invoker.getUrl(), methodName);
        long min_average_elapsed = count.getAverageElapsed();

        for (int i = 1; i < lenght; i++) {
            invoker = invokers.get(i);
            count = RpcStatus.getStatus(invoker.getUrl(), methodName);

            if (min_average_elapsed > count.getAverageElapsed()) {
                select_index = i;
                min_average_elapsed = count.getAverageElapsed();
            }
        }

        return invokers.get(select_index);
    }
}
