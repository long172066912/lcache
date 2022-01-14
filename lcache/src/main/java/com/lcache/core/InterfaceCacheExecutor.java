package com.lcache.core;

import com.lcache.connect.ConnectResource;
import com.lcache.core.cache.redis.commands.RedisAsyncCommands;
import com.lcache.extend.handle.pipeline.PipelineCmd;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CacheExecutorFactory
 * @Description: 执行器接口
 * @date 2021/1/19 8:58 PM
 */
public interface InterfaceCacheExecutor {

    /**
     * 传入连接资源
     *
     * @param connectionResource
     * @return
     */
    void setConnectionResource(ConnectResource connectionResource);

    /**
     * 获取连接资源
     *
     * @return
     */
    Object getConnectResource();

    /**
     * 释放资源
     */
    void returnConnectResource();

    /**
     * 关闭链接
     */
    void close();

    /**
     * 获取连接池
     *
     * @return
     */
    Object getPool();

    /**
     * 获取异步执行器
     *
     * @return
     */
    RedisAsyncCommands async();

    /**
     * 获取Lettuce异步
     *
     * @return
     */
    RedisClusterAsyncCommands asyncL();

    /**
     * 通过管道批量执行命令，同步
     *
     * @param commands
     * @return
     */
    List<Object> pSync(List<PipelineCmd> commands);

    /**
     * 通过管道批量执行命令，同步
     *
     * @param commands
     * @return
     */
    List<Object> pSync(PipelineCmd[] commands);

    /**
     * 通过管道批量执行命令，异步
     *
     * @param commands
     * @return
     */
    CompletableFuture<List<Object>> pAsync(List<PipelineCmd> commands);

    /**
     * 通过管道批量执行命令，异步
     *
     * @param commands
     * @return
     */
    CompletableFuture<List<Object>> pAsync(PipelineCmd[] commands);
}
