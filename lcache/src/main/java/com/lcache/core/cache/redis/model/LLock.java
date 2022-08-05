package com.lcache.core.cache.redis.model;

import com.lcache.core.handle.AbstractCacheHandle;
import com.lcache.exception.CacheExceptionFactory;
import org.redisson.api.RLock;

import java.util.Optional;

/**
* @Title: LLock
* @Description: //TODO (用一句话描述该文件做什么)
* @author JerryLong
* @date 2022/8/5 4:19 PM
* @version V1.0
*/
    public class LLock implements AutoCloseable {
        
        public LLock(RLock lock, AbstractCacheHandle cacheExecutor) {
            this.lock = lock;
            this.cacheExecutor = cacheExecutor;
        }

        private RLock lock;

        private AbstractCacheHandle cacheExecutor;

        public void unlock() {
            Optional.ofNullable(lock).ifPresent(e ->
                    cacheExecutor.execute(() -> {
                        try {
                            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                                lock.unlock();
                            }
                        } catch (Exception ex) {
                            //防止判断时异常导致锁一直不释放
                            lock.unlock();
                            CacheExceptionFactory.throwException("unLock error !", ex);
                        }
                        return null;
                    }, lock.getName())
            );
        }

        @Override
        public void close() throws Exception {
            this.unlock();
        }
    }