package com.redis.core.command.data;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.redis.core.command.RedisDataDict;
import com.redis.core.command.model.RedisDataType;
import com.redis.utils.JsonUtil;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;

/**
 * redis数据持久化，快照方式
 * 文件存储方式为PATH/(crc32/1000)/key.txt
 * 按行操作文件
 *
 * @author JerryLong
 */
public class RedisDataRdb {
    private static String filePath;
    private static final String INTERVAL = "/";
    private static final String FILE_TYPE = ".txt";

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 20, 30, TimeUnit.SECONDS, new LinkedBlockingDeque<>(5000), new DefaultThreadFactory(""), new ThreadPoolExecutor.CallerRunsPolicy());

    public static void init(String filePath) {
        RedisDataRdb.filePath = filePath;
        System.out.println("RDB恢复数据 开始！");
        //启动时初始化数据，2层结构
        final List<File> files = FileUtil.loopFiles(filePath);
        if (CollectionUtil.isNotEmpty(files)) {
            for (File file : files) {
                final List<File> files1 = FileUtil.loopFiles(file.getPath());
                for (File file1 : files1) {
                    if (!file.getName().endsWith(FILE_TYPE)) {
                        continue;
                    }
                    String k;
                    RedisDataDict redisDataDict;
                    try {
                        k = file1.getName().substring(0, file1.getName().length() - 4);
                        redisDataDict = toDict(FileUtil.readString(file, StandardCharsets.UTF_8));
                        System.out.println("RDB恢复数据 ! k : " + k);
                        RedisDataDictFactory.put(k, redisDataDict);
                    } catch (IORuntimeException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("RDB恢复数据 结束！");
        //线程池处理
        executor.prestartCoreThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            if (executor.getQueue().size() > 0) {
                try {
                    if (executor.awaitTermination(30, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    System.out.println("数据持久化处理失败");
                    e.printStackTrace();
                }
            }
        }));
    }

    public static void put(String k, RedisDataDict dataDict) {
        executor.execute(() -> {
            final File file = getKeyFile(k);
            FileUtil.writeBytes(Objects.requireNonNull(JsonUtil.toJSON(dataDict)).getBytes(StandardCharsets.UTF_8), file);
            System.out.println("数据持久化 , k : " + k + " , 文件 : " + file.getPath());
        });
    }

    public static void del(String k) {
        executor.execute(() -> {
            final File file = getKeyFile(k);
            FileUtil.del(file);
            System.out.println("数据清理 , k : " + k + " , 文件 : " + file.getPath());
        });
    }

    private static File getKeyFile(String k) {
        CRC32 crc32 = new CRC32();
        crc32.update(k.getBytes());
        final long l = crc32.getValue() % 1000;
        String fileName = RedisDataRdb.filePath + INTERVAL + l + INTERVAL + k + FILE_TYPE;
        return FileUtil.touch(fileName);
    }

    public static RedisDataDict toDict(String nodeData) {
        final Map<String, Object> map = JsonUtil.toMap(nodeData);
        if (null == map) {
            return null;
        }
        final String ruleType = map.get("redisDataType").toString();
        RedisDataDict dataDict = null;
        switch (RedisDataType.valueOf(ruleType)) {
            case HASH:
                dataDict = JsonUtil.toT(nodeData, RedisHash.class);
                break;
            case STRING:
            default:
                dataDict = JsonUtil.toT(nodeData, RedisString.class);
                break;
        }
        return dataDict;
    }
}
