package com.lcache.core.cache.redis.commands;

import com.lcache.core.cache.LcacheCommands;
import com.lcache.core.cache.annotations.CommandsDataType;
import com.lcache.core.constant.CommandsDataTypeEnum;
import com.lcache.core.constant.LocalCacheHandleTypeEnum;
import com.lcache.core.model.InterfacePubSubModel;
import io.lettuce.core.*;
import redis.clients.jedis.*;
import redis.clients.jedis.params.geo.GeoRadiusParam;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;
import redis.clients.util.Slowlog;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RedisCommands
 * @Description: Jedis命令接口
 * @date 2021/1/26 3:29 PM
 */
public interface RedisCommands extends LcacheCommands {

//    /**
//     * set
//     *
//     * @param key
//     * @param value
//     * @return
//     */
//    Boolean set(String key, String value);

    /**
     * 实际调用的setex
     *
     * @param key
     * @param value
     * @param timeout
     * @return
     */
    @CommandsDataType(commands = "set", localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Boolean set(String key, String value, int timeout);

    /**
     * set
     *
     * @param key
     * @param value
     * @param nxxx
     * @param expx
     * @param time
     * @return
     */
    @CommandsDataType(commands = "set", localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Boolean set(final String key, final String value, final String nxxx, final String expx, final long time);

    /**
     * get
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "get", localCacheHandleType = LocalCacheHandleTypeEnum.GET)
    String get(String key);

    /**
     * exists
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "exists")
    Boolean exists(String key);

    /**
     * del
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "del", localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Long del(String key);

    /**
     * expire
     *
     * @param key
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "expire")
    Boolean expire(String key, int seconds);

    /**
     * EXPIREAT 的作用和 EXPIRE 类似，都用于为 key 设置生存时间。
     * 不同在于 EXPIREAT 命令接受的时间参数是 UNIX 时间戳(unix timestamp)。
     * 时间复杂度：O(1)
     *
     * @param key
     * @param unixTime
     * @return
     */
    @CommandsDataType(commands = "expireAt")
    Boolean expireAt(String key, long unixTime);

    /**
     * TTL key
     * 获取key的有效时间（单位：秒）
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "ttl")
    Long ttl(String key);

    /**
     * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)。
     * 当 key 存在但不是字符串类型时，返回一个错误。
     * 时间复杂度：O(1)
     *
     * @param key
     * @param value
     * @return
     */
    @CommandsDataType(commands = "getSet", localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    String getSet(String key, String value);

    /**
     * mget
     *
     * @param keys
     * @return
     */
    @CommandsDataType(commands = "mget")
    Map<String, Object> mget(String... keys);

    /**
     * 将 key 的值设为 value ，当且仅当 key 不存在。
     * 若给定的 key 已经存在，则 SETNX 不做任何动作。
     * SETNX 是『SET if Not eXists』(如果不存在，则 SET)的简写。
     * 时间复杂度：O(1)
     *
     * @param key
     * @param value
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "setnx", localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Boolean setnx(String key, String value, int seconds);

    /**
     * 将值 value 关联到 key ，并将 key 的生存时间设为 seconds (以秒为单位)。
     * 如果 key 已经存在， SETEX 命令将覆写旧值。
     * SETEX 是一个原子性(atomic)操作，关联值和设置生存时间两个动作会在同一时间内完成，该命令在 Redis 用作缓存时，非常实用。
     * 时间复杂度：O(1)
     *
     * @param key
     * @param seconds
     * @param value
     * @return
     */
    @CommandsDataType(commands = "setex", localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    String setex(String key, int seconds, String value);

    /**
     * mset , 请注意需要设置过期时间
     *
     * @param seconds
     * @param keysvalues
     * @return
     */
    @CommandsDataType(commands = "mset", localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    String mset(int seconds, String... keysvalues);

    /**
     * 同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在。
     * 即使只有一个给定 key 已存在， MSETNX 也会拒绝执行所有给定 key 的设置操作。
     * MSETNX 是原子性的，因此它可以用作设置多个不同 key 表示不同字段(field)的唯一性逻辑对象(unique logic object)，所有字段要么全被设置，要么全不被设置。
     * 时间复杂度：
     * O(N)， N 为要设置的 key 的数量。
     *
     * @param seconds
     * @param keysvalues
     * @return
     */
    @CommandsDataType(commands = "msetnx", localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Boolean msetnx(int seconds, String... keysvalues);

    /**
     * 将 key 所储存的值减去减量 decrement 。
     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECRBY 操作。
     * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
     * 本操作的值限制在 64 位(bit)有符号数字表示之内。
     * 时间复杂度：
     * O(1)
     *
     * @param key
     * @param decrement
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "decrBy", localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Long decrBy(String key, long decrement, int seconds);

    /**
     * 将 key 中储存的数字值减一。
     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECR 操作。
     * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
     * 本操作的值限制在 64 位(bit)有符号数字表示之内。
     * 时间复杂度：
     * O(1)
     *
     * @param key
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "decr", localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Long decr(String key, int seconds);

    /**
     * 与decrBy同理
     *
     * @param key
     * @param increment
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "incrBy", localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Long incrBy(String key, long increment, int seconds);

    /**
     * incrByFloat
     *
     * @param key
     * @param increment
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "incrByFloat", localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Double incrByFloat(String key, double increment, int seconds);

    /**
     * incr
     *
     * @param key
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "incr", localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Long incr(String key, int seconds);

    /**
     * 如果 key 已经存在并且是一个字符串， APPEND 命令将 value 追加到 key 原来的值的末尾。
     * 如果 key 不存在， APPEND 就简单地将给定 key 设为 value ，就像执行 SET key value 一样。
     * 时间复杂度：
     * 平摊O(1)
     *
     * @param key
     * @param value
     * @return
     */
    @CommandsDataType(commands = "append", localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Long append(String key, String value);

    /**
     * 返回 key 中字符串值的子字符串，字符串的截取范围由 start 和 end 两个偏移量决定(包括 start 和 end 在内)。
     * 负数偏移量表示从字符串最后开始计数， -1 表示最后一个字符， -2 表示倒数第二个，以此类推。
     * GETRANGE 通过保证子字符串的值域(range)不超过实际字符串的值域来处理超出范围的值域请求。
     * 在 <= 2.0 的版本里，GETRANGE 被叫作 SUBSTR。
     * 时间复杂度：
     * O(N)， N 为要返回的字符串的长度。
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    @CommandsDataType(commands = "substr", localCacheHandleType = LocalCacheHandleTypeEnum.GET)
    String substr(String key, int start, int end);

    /**
     * hset
     *
     * @param key
     * @param field
     * @param value
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "hset", dataType = CommandsDataTypeEnum.HASH, localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Boolean hset(String key, String field, String value, int seconds);

    /**
     * hset
     *
     * @param key
     * @param hash
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "hset", dataType = CommandsDataTypeEnum.HASH, localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Long hset(String key, Map<String, String> hash, int seconds);

    /**
     * hget
     *
     * @param key
     * @param field
     * @return
     */
    @CommandsDataType(commands = "hget", dataType = CommandsDataTypeEnum.HASH, localCacheHandleType = LocalCacheHandleTypeEnum.GET)
    String hget(String key, String field);

    /**
     * 只在 key 指定的哈希集中不存在指定的字段时，设置字段的值。如果 key 指定的哈希集不存在，会创建一个新的哈希集并与 key 关联。如果字段已存在，该操作无效果。
     *
     * @param key
     * @param field
     * @param value
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "hsetnx", dataType = CommandsDataTypeEnum.HASH, localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Boolean hsetnx(String key, String field, String value, int seconds);

    /**
     * hmset
     *
     * @param key
     * @param hash
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "hmset", dataType = CommandsDataTypeEnum.HASH, localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    String hmset(String key, Map<String, String> hash, int seconds);

    /**
     * hmget
     *
     * @param key
     * @param fields
     * @return
     */
    @CommandsDataType(commands = "hmget", dataType = CommandsDataTypeEnum.HASH, localCacheHandleType = LocalCacheHandleTypeEnum.GET)
    List<String> hmget(String key, String[] fields);


    /**
     * hincrBy
     *
     * @param key
     * @param field
     * @param value
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "hincrBy", dataType = CommandsDataTypeEnum.HASH, localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Long hincrBy(String key, String field, long value, int seconds);

    /**
     * hincrByFloat
     *
     * @param key
     * @param field
     * @param value
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "hincrByFloat", dataType = CommandsDataTypeEnum.HASH, localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Double hincrByFloat(String key, String field, double value, int seconds);

    /**
     * hexists
     *
     * @param key
     * @param field
     * @return
     */
    @CommandsDataType(commands = "hexists", dataType = CommandsDataTypeEnum.HASH, localCacheHandleType = LocalCacheHandleTypeEnum.GET)
    Boolean hexists(String key, String field);

    /**
     * hdel
     *
     * @param key
     * @param field
     * @return
     */
    @CommandsDataType(commands = "hdel", dataType = CommandsDataTypeEnum.HASH, localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Long hdel(String key, String field);

    /**
     * hdel
     *
     * @param key
     * @param fields
     * @return
     */
    @CommandsDataType(commands = "hdel", dataType = CommandsDataTypeEnum.HASH, localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Long hdel(String key, String[] fields);

    /**
     * hlen
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "hlen", dataType = CommandsDataTypeEnum.HASH, localCacheHandleType = LocalCacheHandleTypeEnum.GET)
    Long hlen(String key);

    /**
     * hkeys
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "hkeys", dataType = CommandsDataTypeEnum.HASH, localCacheHandleType = LocalCacheHandleTypeEnum.GET)
    Set<String> hkeys(String key);

    /**
     * hvals
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "hvals", dataType = CommandsDataTypeEnum.HASH, localCacheHandleType = LocalCacheHandleTypeEnum.GET)
    List<String> hvals(String key);

    /**
     * hgetAll
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "hgetAll", dataType = CommandsDataTypeEnum.HASH, localCacheHandleType = LocalCacheHandleTypeEnum.GET)
    Map<String, String> hgetAll(String key);

    /**
     * rpush
     *
     * @param key
     * @param string
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "rpush", dataType = CommandsDataTypeEnum.LIST)
    Long rpush(String key, String string, int seconds);

    /**
     * rpush
     *
     * @param key
     * @param strings
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "rpush", dataType = CommandsDataTypeEnum.LIST)
    Long rpush(String key, String[] strings, int seconds);

    /**
     * lpush
     *
     * @param key
     * @param string
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "lpush", dataType = CommandsDataTypeEnum.LIST)
    Long lpush(String key, String string, int seconds);

    /**
     * lpush
     *
     * @param key
     * @param strings
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "lpush", dataType = CommandsDataTypeEnum.LIST)
    Long lpush(String key, String[] strings, int seconds);

    /**
     * llen
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "llen", dataType = CommandsDataTypeEnum.LIST)
    Long llen(String key);

    /**
     * 返回存储在 key 的列表里指定范围内的元素。 start 和 end 偏移量都是基于0的下标，即list的第一个元素下标是0（list的表头），第二个元素下标是1，以此类推。
     * 偏移量也可以是负数，表示偏移量是从list尾部开始计数。 例如， -1 表示列表的最后一个元素，-2 是倒数第二个，以此类推。
     *
     * @param key
     * @param start
     * @param stop
     * @return
     */
    @CommandsDataType(commands = "lrange", dataType = CommandsDataTypeEnum.LIST)
    List<String> lrange(String key, long start, long stop);

    /**
     * 修剪(trim)一个已存在的 list，这样 list 就会只包含指定范围的指定元素。start 和 stop 都是由0开始计数的， 这里的 0 是列表里的第一个元素（表头），1 是第二个元素，以此类推。
     *
     * @param key
     * @param start
     * @param stop
     * @return
     */
    @CommandsDataType(commands = "ltrim", dataType = CommandsDataTypeEnum.LIST)
    String ltrim(String key, long start, long stop);

    /**
     * lindex
     *
     * @param key
     * @param index
     * @return
     */
    @CommandsDataType(commands = "lindex", dataType = CommandsDataTypeEnum.LIST)
    String lindex(String key, long index);

    /**
     * lset
     *
     * @param key
     * @param index
     * @param value
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "lset", dataType = CommandsDataTypeEnum.LIST)
    String lset(String key, long index, String value, int seconds);

    /**
     * lrem
     *
     * @param key
     * @param count
     * @param value
     * @return
     */
    @CommandsDataType(commands = "lrem", dataType = CommandsDataTypeEnum.LIST)
    Long lrem(String key, long count, String value);

    /**
     * lpop
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "lpop", dataType = CommandsDataTypeEnum.LIST)
    String lpop(String key);

    /**
     * rpop
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "rpop", dataType = CommandsDataTypeEnum.LIST)
    String rpop(String key);

    /**
     * 原子性地返回并移除存储在 source 的列表的最后一个元素（列表尾部元素）， 并把该元素放入存储在 destination 的列表的第一个元素位置（列表头部）。
     * 例如：假设 source 存储着列表 a,b,c， destination存储着列表 x,y,z。 执行 RPOPLPUSH 得到的结果是 source 保存着列表 a,b ，而 destination 保存着列表 c,x,y,z。
     * 如果 source 不存在，那么会返回 nil 值，并且不会执行任何操作。 如果 source 和 destination 是同样的，那么这个操作等同于移除列表最后一个元素并且把该元素放在列表头部， 所以这个命令也可以当作是一个旋转列表的命令。
     *
     * @param srckey
     * @param dstkey
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "rpoplpush", dataType = CommandsDataTypeEnum.LIST)
    String rpoplpush(String srckey, String dstkey, int seconds);

    /**
     * sadd
     *
     * @param key
     * @param member
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "sadd", dataType = CommandsDataTypeEnum.SET, localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Long sadd(String key, String member, int seconds);

    /**
     * sadd
     *
     * @param key
     * @param members
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "sadd", dataType = CommandsDataTypeEnum.SET, localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Long sadd(String key, String[] members, int seconds);

    /**
     * smembers
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "smembers", dataType = CommandsDataTypeEnum.SET, localCacheHandleType = LocalCacheHandleTypeEnum.GET)
    Set<String> smembers(String key);

    /**
     * srem
     *
     * @param key
     * @param member
     * @return
     */
    @CommandsDataType(commands = "srem", dataType = CommandsDataTypeEnum.SET, localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Long srem(String key, String member);

    /**
     * srem
     *
     * @param key
     * @param members
     * @return
     */
    @CommandsDataType(commands = "srem", dataType = CommandsDataTypeEnum.SET, localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Long srem(String key, String[] members);

    /**
     * 从存储在key的集合中移除并返回一个或多个随机元素。
     * 此操作与SRANDMEMBER类似，它从一个集合中返回一个或多个随机元素，但不删除元素。
     * count参数将在更高版本中提供，但是在2.6、2.8、3.0中不可用
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "spop", dataType = CommandsDataTypeEnum.SET, localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    String spop(String key);

    /**
     * spop
     *
     * @param key
     * @param count
     * @return
     */
    @CommandsDataType(commands = "spop", dataType = CommandsDataTypeEnum.SET, localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Set<String> spop(String key, long count);

    /**
     * smove
     *
     * @param srckey
     * @param dstkey
     * @param member
     * @return
     */
    @CommandsDataType(commands = "smove", dataType = CommandsDataTypeEnum.SET, localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Boolean smove(String srckey, String dstkey, String member);

    /**
     * 返回集合存储的key的基数 (集合元素的数量).
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "scard", dataType = CommandsDataTypeEnum.SET, localCacheHandleType = LocalCacheHandleTypeEnum.GET)
    Long scard(String key);

    /**
     * sismember
     *
     * @param key
     * @param member
     * @return
     */
    @CommandsDataType(commands = "sismember", dataType = CommandsDataTypeEnum.SET, localCacheHandleType = LocalCacheHandleTypeEnum.GET)
    Boolean sismember(String key, String member);

    /**
     * 返回指定所有的集合的成员的交集.
     *
     * @param keys
     * @return
     */
    @CommandsDataType(commands = "sinter", dataType = CommandsDataTypeEnum.SET)
    Set<String> sinter(String... keys);

    /**
     * 这个命令与SINTER命令类似, 但是它并不是直接返回结果集,而是将结果保存在 destination集合中.
     * 如果destination 集合存在, 则会被重写.
     *
     * @param dstkey
     * @param seconds
     * @param keys
     * @return
     */
    @CommandsDataType(commands = "sinterstore", dataType = CommandsDataTypeEnum.SET)
    Long sinterstore(String dstkey, int seconds, String... keys);

    /**
     * 返回给定的多个集合的并集中的所有成员.
     *
     * @param keys
     * @return
     */
    @CommandsDataType(commands = "sunion", dataType = CommandsDataTypeEnum.SET)
    Set<String> sunion(String... keys);

    /**
     * 该命令作用类似于SUNION命令,不同的是它并不返回结果集,而是将结果存储在destination集合中.
     * 如果destination 已经存在,则将其覆盖.
     *
     * @param dstkey
     * @param seconds
     * @param keys
     * @return
     */
    @CommandsDataType(commands = "sunionstore", dataType = CommandsDataTypeEnum.SET)
    Long sunionstore(String dstkey, int seconds, String... keys);

    /**
     * sdiff
     *
     * @param keys
     * @return
     */
    @CommandsDataType(commands = "sdiff", dataType = CommandsDataTypeEnum.SET)
    Set<String> sdiff(String... keys);

    /**
     * sdiffstore
     *
     * @param dstkey
     * @param seconds
     * @param keys
     * @return
     */
    @CommandsDataType(commands = "sdiffstore", dataType = CommandsDataTypeEnum.SET)
    Long sdiffstore(String dstkey, int seconds, String... keys);

    /**
     * 该命令作用类似于SPOP命令，不同的是SPOP命令会将被选择的随机元素从集合中移除，而SRANDMEMBER仅仅是返回该随记元素，而不做任何操作.
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "srandmember", dataType = CommandsDataTypeEnum.SET)
    String srandmember(String key);

    /**
     * srandmember
     *
     * @param key
     * @param count
     * @return
     */
    @CommandsDataType(commands = "srandmember", dataType = CommandsDataTypeEnum.SET)
    List<String> srandmember(String key, int count);

    /**
     * zadd
     *
     * @param key
     * @param score
     * @param member
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "zadd", dataType = CommandsDataTypeEnum.ZSET)
    Long zadd(String key, double score, String member, int seconds);

    /**
     * zadd
     *
     * @param key
     * @param score
     * @param member
     * @param params
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "zadd", dataType = CommandsDataTypeEnum.ZSET)
    Long zadd(String key, double score, String member, ZAddParams params, int seconds);

    /**
     * zadd
     *
     * @param key
     * @param scoreMembers
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "zadd", dataType = CommandsDataTypeEnum.ZSET)
    Long zadd(String key, Map<String, Double> scoreMembers, int seconds);

    /**
     * zadd
     *
     * @param key
     * @param scoreMembers
     * @param params
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "zadd", dataType = CommandsDataTypeEnum.ZSET)
    Long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params, int seconds);

    /**
     * zrange
     *
     * @param key
     * @param start
     * @param stop
     * @return
     */
    @CommandsDataType(commands = "zrange", dataType = CommandsDataTypeEnum.ZSET)
    Set<String> zrange(String key, long start, long stop);

    /**
     * zrem
     *
     * @param key
     * @param member
     * @return
     */
    @CommandsDataType(commands = "zrem", dataType = CommandsDataTypeEnum.ZSET)
    Long zrem(String key, String member);

    /**
     * zrem
     *
     * @param key
     * @param members
     * @return
     */
    @CommandsDataType(commands = "zrem", dataType = CommandsDataTypeEnum.ZSET)
    Long zrem(String key, String[] members);

    /**
     * zincrby
     *
     * @param key
     * @param increment
     * @param member
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "zincrby", dataType = CommandsDataTypeEnum.ZSET)
    Double zincrby(String key, double increment, String member, int seconds);

    /**
     * zincrby
     *
     * @param key
     * @param increment
     * @param member
     * @param params
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "zincrby", dataType = CommandsDataTypeEnum.ZSET)
    Double zincrby(String key, double increment, String member, ZIncrByParams params, int seconds);

    /**
     * 返回有序集key中成员member的排名。其中有序集成员按score值递增(从小到大)顺序排列。排名以0为底，也就是说，score值最小的成员排名为0。
     * 使用ZREVRANK命令可以获得成员按score值递减(从大到小)排列的排名。
     *
     * @param key
     * @param member
     * @return
     */
    @CommandsDataType(commands = "zrank", dataType = CommandsDataTypeEnum.ZSET)
    Long zrank(String key, String member);

    /**
     * 返回有序集key中成员member的排名，其中有序集成员按score值从大到小排列。排名以0为底，也就是说，score值最大的成员排名为0。
     * 使用ZRANK命令可以获得成员按score值递增(从小到大)排列的排名。
     *
     * @param key
     * @param member
     * @return
     */
    @CommandsDataType(commands = "zrevrank", dataType = CommandsDataTypeEnum.ZSET)
    Long zrevrank(String key, String member);

    /**
     * 返回有序集key中，指定区间内的成员。其中成员的位置按score值递减(从大到小)来排列。具有相同score值的成员按字典序的反序排列。 除了成员按score值递减的次序排列这一点外，ZREVRANGE命令的其他方面和ZRANGE命令一样
     *
     * @param key
     * @param start
     * @param stop
     * @return
     */
    @CommandsDataType(commands = "zrevrange", dataType = CommandsDataTypeEnum.ZSET)
    Set<String> zrevrange(String key, long start, long stop);

    /**
     * 返回有序集 key 中，指定区间内的成员。
     * 其中成员的位置按 score 值递增(从小到大)来排序。
     *
     * @param key
     * @param start
     * @param stop
     * @return
     */
    @CommandsDataType(commands = "zrangeWithScores", dataType = CommandsDataTypeEnum.ZSET)
    Set<Tuple> zrangeWithScores(String key, long start, long stop);

    /**
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列。
     *
     * @param key
     * @param start
     * @param stop
     * @return
     */
    @CommandsDataType(commands = "zrevrangeWithScores", dataType = CommandsDataTypeEnum.ZSET)
    Set<Tuple> zrevrangeWithScores(String key, long start, long stop);

    /**
     * 返回key的有序集元素个数。
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "zcard", dataType = CommandsDataTypeEnum.ZSET)
    Long zcard(String key);

    /**
     * 返回有序集key中，成员member的score值。
     * 如果member元素不是有序集key的成员，或key不存在，返回nil。
     *
     * @param key
     * @param member
     * @return
     */
    @CommandsDataType(commands = "zscore", dataType = CommandsDataTypeEnum.ZSET)
    Double zscore(String key, String member);

    /**
     * 删除并返回有序集合key中的最多count个具有最高得分的成员。
     * 如未指定，count的默认值为1。指定一个大于有序集合的基数的count不会产生错误。 当返回多个元素时候，得分最高的元素将是第一个元素，然后是分数较低的元素。
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "zpopmax", dataType = CommandsDataTypeEnum.ZSET)
    ScoredValue zpopmax(String key);

    /**
     * zpopmax
     *
     * @param key
     * @param count
     * @return
     */
    @CommandsDataType(commands = "zpopmax", dataType = CommandsDataTypeEnum.ZSET)
    List<ScoredValue> zpopmax(String key, int count);

    /**
     * 删除并返回有序集合key中的最多count个具有最低得分的成员。
     * 如未指定，count的默认值为1。指定一个大于有序集合的基数的count不会产生错误。 当返回多个元素时候，得分最低的元素将是第一个元素，然后是分数较高的元素。
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "zpopmin", dataType = CommandsDataTypeEnum.ZSET)
    ScoredValue zpopmin(String key);

    /**
     * zpopmin
     *
     * @param key
     * @param count
     * @return
     */
    @CommandsDataType(commands = "zpopmin", dataType = CommandsDataTypeEnum.ZSET)
    List<ScoredValue> zpopmin(String key, int count);

    /**
     * Removes and returns a member with the highest scores in the sorted set stored at one of the keys.
     * 注：阻塞类命令，需考虑Lettuce单连接会阻塞其他命令执行，应单独申请cacheType或者使用连接池
     *
     * @param timeout
     * @param keys
     * @return
     */
    @CommandsDataType(commands = "bzpopmax", dataType = CommandsDataTypeEnum.ZSET)
    KeyValue<String, ScoredValue> bzpopmax(long timeout, String... keys);

    /**
     * Removes and returns a member with the lowest scores in the sorted set stored at one of the keys.
     * 注：阻塞类命令，需考虑Lettuce单连接会阻塞其他命令执行，应单独申请cacheType或者使用连接池
     *
     * @param timeout
     * @param keys
     * @return
     */
    @CommandsDataType(commands = "bzpopmin", dataType = CommandsDataTypeEnum.ZSET)
    KeyValue<String, ScoredValue> bzpopmin(long timeout, String... keys);

    /**
     * 排序
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "sort", dataType = CommandsDataTypeEnum.LIST)
    List<String> sort(String key);

    /**
     * Jedis专用
     *
     * @param key
     * @param sortingParameters
     * @return
     */
    @CommandsDataType(commands = "sort", dataType = CommandsDataTypeEnum.LIST)
    List<String> sort(String key, SortingParams sortingParameters);

    /**
     * Lettuce专用
     *
     * @param key
     * @param sortArgs
     * @return
     */
    @CommandsDataType(commands = "sort", dataType = CommandsDataTypeEnum.LIST)
    List<String> sort(String key, SortArgs sortArgs);

    /**
     * blpop
     * 注：阻塞类命令，需考虑Lettuce单连接会阻塞其他命令执行，应单独申请cacheType或者使用连接池
     *
     * @param timeout
     * @param keys
     * @return
     */
    @CommandsDataType(commands = "blpop", dataType = CommandsDataTypeEnum.LIST)
    List<String> blpop(int timeout, String... keys);

    /**
     * Jedis专用
     *
     * @param key
     * @param sortingParameters
     * @param dstkey
     * @return
     */
    @CommandsDataType(commands = "sort", dataType = CommandsDataTypeEnum.LIST)
    Long sort(String key, SortingParams sortingParameters, String dstkey);

    /**
     * Lettuce专用
     *
     * @param key
     * @param sortArgs
     * @param dstkey
     * @return
     */
    @CommandsDataType(commands = "sort", dataType = CommandsDataTypeEnum.LIST)
    Long sort(String key, SortArgs sortArgs, String dstkey);

    /**
     * sort
     *
     * @param key
     * @param dstkey
     * @return
     */
    @CommandsDataType(commands = "sort", dataType = CommandsDataTypeEnum.LIST)
    Long sort(String key, String dstkey);

    /**
     * brpop
     * 注：阻塞类命令，需考虑Lettuce单连接会阻塞其他命令执行，应单独申请cacheType或者使用连接池
     *
     * @param timeout
     * @param keys
     * @return
     */
    @CommandsDataType(commands = "brpop", dataType = CommandsDataTypeEnum.LIST)
    List<String> brpop(int timeout, String[] keys);

    /**
     * zcount
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    @CommandsDataType(commands = "zcount", dataType = CommandsDataTypeEnum.ZSET)
    Long zcount(String key, double min, double max);

    /**
     * zcount
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    @CommandsDataType(commands = "zcount", dataType = CommandsDataTypeEnum.ZSET)
    Long zcount(String key, String min, String max);

    /**
     * 返回有序集合中指定分数区间内的成员，分数由低到高排序。
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    @CommandsDataType(commands = "zrangeByScore", dataType = CommandsDataTypeEnum.ZSET)
    Set<String> zrangeByScore(String key, double min, double max);

    /**
     * 返回有序集合中指定分数区间内的成员，分数由低到高排序。
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    @CommandsDataType(commands = "zrangeByScore", dataType = CommandsDataTypeEnum.ZSET)
    Set<String> zrangeByScore(String key, String min, String max);

    /**
     * 返回有序集合中指定分数区间内的成员，分数由低到高排序。
     *
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    @CommandsDataType(commands = "zrangeByScore", dataType = CommandsDataTypeEnum.ZSET)
    Set<String> zrangeByScore(String key, double min, double max, int offset, int count);

    /**
     * 返回有序集合中指定分数区间内的成员，分数由低到高排序。
     *
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    @CommandsDataType(commands = "zrangeByScore", dataType = CommandsDataTypeEnum.ZSET)
    Set<String> zrangeByScore(String key, String min, String max, int offset, int count);

    /**
     * 返回已排序的at key集合中的所有元素，得分在min和max之间，分数由低到高排序。
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    @CommandsDataType(commands = "zrangeByScoreWithScores", dataType = CommandsDataTypeEnum.ZSET)
    Set<Tuple> zrangeByScoreWithScores(String key, double min, double max);

    /**
     * 返回已排序的at key集合中的所有元素，得分在min和max之间，分数由低到高排序。
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    @CommandsDataType(commands = "zrangeByScoreWithScores", dataType = CommandsDataTypeEnum.ZSET)
    Set<Tuple> zrangeByScoreWithScores(String key, String min, String max);

    /**
     * 返回已排序的at key集合中的所有元素，得分在min和max之间，分数由低到高排序。
     *
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    @CommandsDataType(commands = "zrangeByScoreWithScores", dataType = CommandsDataTypeEnum.ZSET)
    Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count);

    /**
     * 返回已排序的at key集合中的所有元素，得分在min和max之间，分数由低到高排序。
     *
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    @CommandsDataType(commands = "zrangeByScoreWithScores", dataType = CommandsDataTypeEnum.ZSET)
    Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count);

    /**
     * 返回已排序的at key集合中的所有元素，得分在min和max之间，分数由高到低排序。
     *
     * @param key
     * @param max
     * @param min
     * @return
     */
    @CommandsDataType(commands = "zrevrangeByScore", dataType = CommandsDataTypeEnum.ZSET)
    Set<String> zrevrangeByScore(String key, double max, double min);

    /**
     * 返回已排序的at key集合中的所有元素，得分在min和max之间，分数由高到低排序。
     *
     * @param key
     * @param max
     * @param min
     * @return
     */
    @CommandsDataType(commands = "zrevrangeByScore", dataType = CommandsDataTypeEnum.ZSET)
    Set<String> zrevrangeByScore(String key, String max, String min);

    /**
     * 返回已排序的at key集合中的所有元素，得分在min和max之间，分数由高到低排序。
     *
     * @param key
     * @param max
     * @param min
     * @param offset
     * @param count
     * @return
     */
    @CommandsDataType(commands = "zrevrangeByScore", dataType = CommandsDataTypeEnum.ZSET)
    Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count);

    /**
     * 返回已排序的at key集合中的所有元素，得分在min和max之间，分数由高到低排序。
     *
     * @param key
     * @param max
     * @param min
     * @return
     */
    @CommandsDataType(commands = "zrevrangeByScoreWithScores", dataType = CommandsDataTypeEnum.ZSET)
    Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min);

    /**
     * 返回已排序的at key集合中的所有元素，得分在min和max之间，分数由高到低排序。
     *
     * @param key
     * @param max
     * @param min
     * @param offset
     * @param count
     * @return
     */
    @CommandsDataType(commands = "zrevrangeByScoreWithScores", dataType = CommandsDataTypeEnum.ZSET)
    Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count);

    /**
     * 返回已排序的at key集合中的所有元素，得分在min和max之间，分数由高到低排序。
     *
     * @param key
     * @param max
     * @param min
     * @param offset
     * @param count
     * @return
     */
    @CommandsDataType(commands = "zrevrangeByScoreWithScores", dataType = CommandsDataTypeEnum.ZSET)
    Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count);

    /**
     * 返回已排序的at key集合中的所有元素，得分在min和max之间，分数由高到低排序。
     *
     * @param key
     * @param max
     * @param min
     * @param offset
     * @param count
     * @return
     */
    @CommandsDataType(commands = "zrevrangeByScore", dataType = CommandsDataTypeEnum.ZSET)
    Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count);

    /**
     * 返回已排序的at key集合中的所有元素，得分在min和max之间，分数由高到低排序。
     *
     * @param key
     * @param max
     * @param min
     * @return
     */
    @CommandsDataType(commands = "zrevrangeByScoreWithScores", dataType = CommandsDataTypeEnum.ZSET)
    Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min);

    /**
     * 在排序设置的所有成员在给定的索引中删除
     *
     * @param key
     * @param start
     * @param stop
     * @return
     */
    @CommandsDataType(commands = "zremrangeByRank", dataType = CommandsDataTypeEnum.ZSET)
    Long zremrangeByRank(String key, long start, long stop);

    /**
     * 删除一个排序的设置在给定的分数所有成员
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    @CommandsDataType(commands = "zremrangeByScore", dataType = CommandsDataTypeEnum.ZSET)
    Long zremrangeByScore(String key, double min, double max);

    /**
     * ZREMRANGEBYSCORE key min max
     * 删除一个排序的设置在给定的分数所有成员
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    @CommandsDataType(commands = "zremrangeByScore", dataType = CommandsDataTypeEnum.ZSET)
    Long zremrangeByScore(String key, String min, String max);

    /**
     * ZUNIONSTORE destination numkeys key [key ...] [WEIGHTS weight [weight ...]] [AGGREGATE SUM|MIN|MAX]
     * 添加多个排序集和导致排序的设置存储在一个新的关键
     *
     * @param dstkey
     * @param sets
     * @return
     */
    @CommandsDataType(commands = "zunionstore", dataType = CommandsDataTypeEnum.ZSET)
    Long zunionstore(String dstkey, String... sets);

    /**
     * ZUNIONSTORE destination numkeys key [key ...] [WEIGHTS weight [weight ...]] [AGGREGATE SUM|MIN|MAX]
     * 添加多个排序集和导致排序的设置存储在一个新的关键
     *
     * @param dstkey
     * @param params
     * @param sets
     * @return
     */
    @CommandsDataType(commands = "zunionstore", dataType = CommandsDataTypeEnum.ZSET)
    Long zunionstore(String dstkey, ZParams params, String... sets);

    /**
     * ZINTERSTORE destination numkeys key [key ...] [WEIGHTS weight [weight ...]] [AGGREGATE SUM|MIN|MAX]
     * 相交多个排序集，导致排序的设置存储在一个新的关键
     *
     * @param dstkey
     * @param sets
     * @return
     */
    @CommandsDataType(commands = "zinterstore", dataType = CommandsDataTypeEnum.ZSET)
    Long zinterstore(String dstkey, String... sets);

    /**
     * ZINTERSTORE destination numkeys key [key ...] [WEIGHTS weight [weight ...]] [AGGREGATE SUM|MIN|MAX]
     * 相交多个排序集，导致排序的设置存储在一个新的关键
     *
     * @param dstkey
     * @param params
     * @param sets
     * @return
     */
    @CommandsDataType(commands = "zinterstore", dataType = CommandsDataTypeEnum.ZSET)
    Long zinterstore(String dstkey, ZParams params, String... sets);

    /**
     * ZLEXCOUNT key min max
     * 返回成员之间的成员数量
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    @CommandsDataType(commands = "zlexcount", dataType = CommandsDataTypeEnum.ZSET)
    Long zlexcount(String key, String min, String max);

    /**
     * ZRANGEBYLEX key min max [LIMIT offset count]
     * 返回指定成员区间内的成员，按字典正序排列, 分数必须相同。
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    @CommandsDataType(commands = "zrangeByLex", dataType = CommandsDataTypeEnum.ZSET)
    Set<String> zrangeByLex(String key, String min, String max);

    /**
     * ZRANGEBYLEX key min max [LIMIT offset count]
     * 返回指定成员区间内的成员，按字典正序排列, 分数必须相同。
     *
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    @CommandsDataType(commands = "zrangeByLex", dataType = CommandsDataTypeEnum.ZSET)
    Set<String> zrangeByLex(String key, String min, String max, int offset, int count);

    /**
     * ZREVRANGEBYLEX key max min [LIMIT offset count]
     * 返回指定成员区间内的成员，按字典倒序排列, 分数必须相同
     *
     * @param key
     * @param max
     * @param min
     * @return
     */
    @CommandsDataType(commands = "zrevrangeByLex", dataType = CommandsDataTypeEnum.ZSET)
    Set<String> zrevrangeByLex(String key, String max, String min);

    /**
     * ZREVRANGEBYLEX key max min [LIMIT offset count]
     * 返回指定成员区间内的成员，按字典倒序排列, 分数必须相同
     *
     * @param key
     * @param max
     * @param min
     * @param offset
     * @param count
     * @return
     */
    @CommandsDataType(commands = "zrevrangeByLex", dataType = CommandsDataTypeEnum.ZSET)
    Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count);

    /**
     * ZREMRANGEBYLEX key min max
     * 删除名称按字典由低到高排序成员之间所有成员。
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    @CommandsDataType(commands = "zremrangeByLex", dataType = CommandsDataTypeEnum.ZSET)
    Long zremrangeByLex(String key, String min, String max);

    /**
     * STRLEN key
     * 获取指定key值的长度
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "strlen", localCacheHandleType = LocalCacheHandleTypeEnum.GET)
    Long strlen(String key);

    /**
     * LPUSHX key value
     * 当队列存在时，从队到左边入队一个元素
     *
     * @param key
     * @param string
     * @return
     */
    @CommandsDataType(commands = "lpushx", dataType = CommandsDataTypeEnum.LIST)
    Long lpushx(String key, String... string);
    //去掉key过期时间，禁用
    //Long persist(String key);

    /**
     * RPUSHX key value
     * 从队列的右边入队一个元素，仅队列存在时有效
     *
     * @param key
     * @param string
     * @return
     */
    @CommandsDataType(commands = "rpushx", dataType = CommandsDataTypeEnum.LIST)
    Long rpushx(String key, String... string);

    /**
     * ECHO message
     * 回显输入的字符串
     *
     * @param string
     * @return
     */
    @CommandsDataType(commands = "echo", dataType = CommandsDataTypeEnum.ECHO)
    String echo(String string);

    /**
     * LINSERT key BEFORE|AFTER pivot value
     * 在列表中的另一个元素之前或之后插入一个元素
     *
     * @param key
     * @param where
     * @param pivot
     * @param value
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "linsert", dataType = CommandsDataTypeEnum.LIST)
    Long linsert(String key, Client.LIST_POSITION where, String pivot, String value, int seconds);

    /**
     * BRPOPLPUSH source destination timeout
     * 弹出一个列表的值，将它推到另一个列表，并返回它;或阻塞，直到有一个可用
     * 注：阻塞类命令，需考虑Lettuce单连接会阻塞其他命令执行，应单独申请cacheType或者使用连接池
     *
     * @param source
     * @param destination
     * @param timeout
     * @return
     */
    @CommandsDataType(commands = "brpoplpush", dataType = CommandsDataTypeEnum.LIST)
    String brpoplpush(String source, String destination, int timeout);

    /**
     * SETBIT key offset value
     * 设置或清除存储在键值的字符串值中的偏移位
     *
     * @param key
     * @param offset
     * @param value
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "setbit", dataType = CommandsDataTypeEnum.BITMAP)
    Boolean setbit(String key, long offset, boolean value, int seconds);

    /**
     * SETBIT key offset value
     * 设置或清除存储在键值的字符串值中的偏移位
     *
     * @param key
     * @param offset
     * @param value
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "setbit", dataType = CommandsDataTypeEnum.BITMAP)
    Boolean setbit(String key, long offset, String value, int seconds);

    /**
     * GETBIT key offset
     * 返回位的值存储在关键的字符串值的偏移量。
     *
     * @param key
     * @param offset
     * @return
     */
    @CommandsDataType(commands = "getbit", dataType = CommandsDataTypeEnum.BITMAP)
    Boolean getbit(String key, long offset);

    /**
     * 这个命令的作用是覆盖key对应的string的一部分，从指定的offset处开始，覆盖value的长度。如果offset比当前key对应string还要长，那这个string后面就补0以达到offset。不存在的keys被认为是空字符串，所以这个命令可以确保key有一个足够大的字符串，能在offset处设置value。
     * 注意，offset最大可以是229-1(536870911),因为redis字符串限制在512M大小。如果你需要超过这个大小，你可以用多个keys。
     * 警告：当set最后一个字节并且key还没有一个字符串value或者其value是个比较小的字符串时，Redis需要立即分配所有内存，这有可能会导致服务阻塞一会。在一台2010MacBook Pro上，set536870911字节（分配512MB）需要～300ms，set134217728字节(分配128MB)需要～80ms，set33554432比特位（分配32MB）需要～30ms，set8388608比特（分配8MB）需要8ms。注意，一旦第一次内存分配完，后面对同一个key调用SETRANGE就不会预先得到内存分配。
     *
     * @param key
     * @param offset
     * @param value
     * @return
     */
    @CommandsDataType(commands = "setrange", localCacheHandleType = LocalCacheHandleTypeEnum.SET)
    Long setrange(String key, long offset, String value);

    /**
     * 覆盖字符串中从指定偏移量开始的关键字部分
     *
     * @param key
     * @param startOffset
     * @param endOffset
     * @return
     */
    @CommandsDataType(commands = "getrange")
    String getrange(String key, long startOffset, long endOffset);

    /**
     * BITPOS key bit [start] [end]
     * 查找字符串中设置或清除的第一个位
     *
     * @param key
     * @param value
     * @return
     */
    @CommandsDataType(commands = "bitpos", dataType = CommandsDataTypeEnum.BITMAP)
    Long bitpos(String key, boolean value);

    /**
     * 查找字符串中设置或清除的第一个位
     *
     * @param key
     * @param state
     * @param start
     * @param end
     * @return
     */
    @CommandsDataType(commands = "bitpos", dataType = CommandsDataTypeEnum.BITMAP)
    Long bitpos(String key, boolean state, long start, long end);

    /**
     * subscribe
     *
     * @param jedisPubSub
     * @param channels
     */
    @CommandsDataType(commands = "subscribe", dataType = CommandsDataTypeEnum.PUBSUB)
    void subscribe(JedisPubSub jedisPubSub, String[] channels);


    /**
     * subscribe
     *
     * @param pubSubModel
     * @param channels
     */
    @CommandsDataType(commands = "subscribe", dataType = CommandsDataTypeEnum.PUBSUB)
    void subscribe(InterfacePubSubModel pubSubModel, String[] channels);

    /**
     * subscribe
     *
     * @param jedisPubSub
     * @param channel
     */
    @CommandsDataType(commands = "subscribe", dataType = CommandsDataTypeEnum.PUBSUB)
    void subscribe(JedisPubSub jedisPubSub, String channel);


    /**
     * subscribe
     *
     * @param pubSubModel
     * @param channel
     */
    @CommandsDataType(commands = "subscribe", dataType = CommandsDataTypeEnum.PUBSUB)
    void subscribe(InterfacePubSubModel pubSubModel, String channel);

    /**
     * publish
     *
     * @param channel
     * @param message
     * @return
     */
    @CommandsDataType(commands = "publish", dataType = CommandsDataTypeEnum.PUBSUB)
    Long publish(String channel, String message);

    /**
     * 异步发送消息
     *
     * @param channel
     * @param message
     */
    @CommandsDataType(commands = "publishAsync", dataType = CommandsDataTypeEnum.PUBSUB)
    void publishAsync(String channel, String message);

    /**
     * 监听发布到匹配给定模式的通道上的消息
     *
     * @param jedisPubSub
     * @param patterns
     */
    @CommandsDataType(commands = "psubscribe", dataType = CommandsDataTypeEnum.PUBSUB)
    void psubscribe(JedisPubSub jedisPubSub, String... patterns);

    /**
     * EVAL script numkeys key [key ...] arg [arg ...]
     * 在服务器端执行 LUA 脚本
     *
     * @param script
     * @param keyCount
     * @param params
     * @return
     */
    @CommandsDataType(commands = "eval", dataType = CommandsDataTypeEnum.EVAL)
    Object eval(String script, ScriptOutputType outputType, int keyCount, String[] params);

    /**
     * EVAL script numkeys key [key ...] arg [arg ...]
     * 在服务器端执行 LUA 脚本
     *
     * @param script
     * @param keys
     * @param args
     * @return
     */
    @CommandsDataType(commands = "eval", dataType = CommandsDataTypeEnum.EVAL)
    Object eval(String script, ScriptOutputType outputType, List<String> keys, List<String> args);

    /**
     * EVAL script numkeys key [key ...] arg [arg ...]
     * 在服务器端执行 LUA 脚本
     *
     * @param script
     * @return
     */
    @CommandsDataType(commands = "eval", dataType = CommandsDataTypeEnum.EVAL)
    Object eval(String script, ScriptOutputType outputType);

    /**
     * EVALSHA sha1 numkeys key [key ...] arg [arg ...]
     * 在服务器端执行 LUA 脚本
     *
     * @param sha1
     * @return
     */
    @CommandsDataType(commands = "evalsha", dataType = CommandsDataTypeEnum.EVAL)
    Object evalsha(String sha1, ScriptOutputType outputType);

    /**
     * EVALSHA sha1 numkeys key [key ...] arg [arg ...]
     * 在服务器端执行 LUA 脚本
     *
     * @param sha1
     * @param keys
     * @param args
     * @return
     */
    @CommandsDataType(commands = "evalsha", dataType = CommandsDataTypeEnum.EVAL)
    Object evalsha(String sha1, ScriptOutputType outputType, List<String> keys, List<String> args);

    /**
     * EVALSHA sha1 numkeys key [key ...] arg [arg ...]
     * 在服务器端执行 LUA 脚本
     *
     * @param sha1
     * @param keyCount
     * @param params
     * @return
     */
    @CommandsDataType(commands = "evalsha", dataType = CommandsDataTypeEnum.EVAL)
    Object evalsha(String sha1, ScriptOutputType outputType, int keyCount, String[] params);

    /**
     * 命令用于校验指定的脚本是否已经被保存在缓存当中
     *
     * @param sha1
     * @return
     */
    @CommandsDataType(commands = "scriptExists", dataType = CommandsDataTypeEnum.EVAL)
    Boolean scriptExists(String sha1);

    /**
     * 命令用于校验指定的脚本是否已经被保存在缓存当中
     *
     * @param sha1
     * @return
     */
    @CommandsDataType(commands = "scriptExists", dataType = CommandsDataTypeEnum.EVAL)
    List<Boolean> scriptExists(String... sha1);

    /**
     * SCRIPT LOAD script
     * 从服务器缓存中装载一个Lua脚本。
     *
     * @param script
     * @return
     */
    @CommandsDataType(commands = "scriptLoad", dataType = CommandsDataTypeEnum.EVAL)
    String scriptLoad(String script);

    /**
     * 读取和重置redis慢请求日志
     *
     * @return
     */
    @CommandsDataType(commands = "slowlogGet", dataType = CommandsDataTypeEnum.SLOWLOG)
    List<Slowlog> slowlogGet();

    /**
     * 读取和重置redis慢请求日志
     *
     * @param entries
     * @return
     */
    @CommandsDataType(commands = "slowlogGet", dataType = CommandsDataTypeEnum.SLOWLOG)
    List<Slowlog> slowlogGet(long entries);

//    Long objectRefcount(String key);
//
//    String objectEncoding(String key);
//
//    Long objectIdletime(String key);
//
//    List<String> objectHelp();

//    Long objectFreq(String key);

    /**
     * BITCOUNT key [start end]
     * 统计字符串指定起始位置的字节数
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "bitcount", dataType = CommandsDataTypeEnum.BITMAP)
    Long bitcount(String key);

    /**
     * BITCOUNT key [start end]
     * 统计字符串指定起始位置的字节数
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    @CommandsDataType(commands = "bitcount", dataType = CommandsDataTypeEnum.BITMAP)
    Long bitcount(String key, long start, long end);

    /**
     * 对一个或多个保存二进制位的字符串 key 进行位元操作，并将结果保存到 destkey 上。
     *
     * @param op
     * @param destKey
     * @param srcKeys
     * @return
     */
    @CommandsDataType(commands = "bitop", dataType = CommandsDataTypeEnum.BITMAP)
    Long bitop(BitOP op, String destKey, String... srcKeys);

//    List<Map<String, String>> sentinelMasters();

//    List<String> sentinelGetMasterAddrByName(String masterName);

//    Long sentinelReset(String pattern);

//    List<Map<String, String>> sentinelSlaves(String masterName);

//    String sentinelFailover(String masterName);
//
//    String sentinelMonitor(String masterName, String ip, int port, int quorum);
//
//    String sentinelRemove(String masterName);
//
//    String sentinelSet(String masterName, Map<String, String> parameterMap);

//    byte[] dump(String key);
//
//    String restore(String key, int ttl, byte[] serializedValue);
//
//    String restoreReplace(String key, int ttl, byte[] serializedValue);

    /**
     * PEXPIRE key milliseconds
     * 设置key的有效时间以毫秒为单位
     *
     * @param key
     * @param milliseconds
     * @return
     */
    @CommandsDataType(commands = "pexpire", dataType = CommandsDataTypeEnum.EXPIRE)
    Boolean pexpire(String key, long milliseconds);

    /**
     * PEXPIREAT key milliseconds-timestamp
     * 设置key的到期UNIX时间戳以毫秒为单位
     *
     * @param key
     * @param millisecondsTimestamp
     * @return
     */
    @CommandsDataType(commands = "pexpireAt", dataType = CommandsDataTypeEnum.EXPIRE)
    Boolean pexpireAt(String key, long millisecondsTimestamp);

    /**
     * PTTL key
     * 获取key的有效毫秒数
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "pttl", dataType = CommandsDataTypeEnum.EXPIRE)
    Long pttl(String key);

    /**
     * 以毫秒为单位设置 key 的生存时间。
     *
     * @param key
     * @param milliseconds
     * @param value
     * @return
     */
    @CommandsDataType(commands = "psetex")
    String psetex(String key, long milliseconds, String value);

    /**
     * SCAN cursor [MATCH pattern] [COUNT count]
     * 增量迭代key
     *
     * @param cursor
     * @return
     */
    @CommandsDataType(commands = "scan")
    ScanResult<String> scan(String cursor);

    /**
     * SCAN cursor [MATCH pattern] [COUNT count]
     * 增量迭代key
     *
     * @param cursor
     * @param params
     * @return
     */
    @CommandsDataType(commands = "scan")
    ScanResult<String> scan(String cursor, ScanParams params);

    /**
     * HSCAN key cursor [MATCH pattern] [COUNT count]
     * 迭代hash里面的元素
     *
     * @param key
     * @param cursor
     * @return
     */
    @CommandsDataType(commands = "hscan", dataType = CommandsDataTypeEnum.HASH)
    ScanResult<Map.Entry<String, String>> hscan(String key, String cursor);

    /**
     * HSCAN key cursor [MATCH pattern] [COUNT count]
     * 迭代hash里面的元素
     *
     * @param key
     * @param cursor
     * @param params
     * @return
     */
    @CommandsDataType(commands = "hscan", dataType = CommandsDataTypeEnum.HASH)
    ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, ScanParams params);

    /**
     * SSCAN key cursor [MATCH pattern] [COUNT count]
     * 迭代set里面的元素
     *
     * @param key
     * @param cursor
     * @return
     */
    @CommandsDataType(commands = "sscan", dataType = CommandsDataTypeEnum.SET)
    ScanResult<String> sscan(String key, String cursor);

    /**
     * SSCAN key cursor [MATCH pattern] [COUNT count]
     * 迭代set里面的元素
     *
     * @param key
     * @param cursor
     * @param params
     * @return
     */
    @CommandsDataType(commands = "sscan", dataType = CommandsDataTypeEnum.SET)
    ScanResult<String> sscan(String key, String cursor, ScanParams params);

    /**
     * ZSCAN key cursor [MATCH pattern] [COUNT count]
     * 迭代sorted sets里面的元素
     *
     * @param key
     * @param cursor
     * @return
     */
    @CommandsDataType(commands = "zscan", dataType = CommandsDataTypeEnum.ZSET)
    ScanResult<Tuple> zscan(String key, String cursor);

    /**
     * ZSCAN key cursor [MATCH pattern] [COUNT count]
     * 迭代sorted sets里面的元素
     *
     * @param key
     * @param cursor
     * @param params
     * @return
     */
    @CommandsDataType(commands = "zscan", dataType = CommandsDataTypeEnum.ZSET)
    ScanResult<Tuple> zscan(String key, String cursor, ScanParams params);

    /**
     * 返回服务器当前被订阅的频道
     *
     * @param pattern
     * @return
     */
    @CommandsDataType(commands = "pubsubChannels", dataType = CommandsDataTypeEnum.PUBSUB)
    List<String> pubsubChannels(String pattern);

    /**
     * 返回服务器当前被订阅模式的数量
     *
     * @return
     */
    @CommandsDataType(commands = "pubsubNumPat", dataType = CommandsDataTypeEnum.PUBSUB)
    Long pubsubNumPat();

    /**
     * 返回这些频道的订阅者数量
     *
     * @param channels
     * @return
     */
    @CommandsDataType(commands = "pubsubNumSub", dataType = CommandsDataTypeEnum.PUBSUB)
    Map<String, String> pubsubNumSub(String... channels);

    /**
     * PFADD key element [element ...]
     * 将指定元素添加到HyperLogLog
     *
     * @param key
     * @param seconds
     * @param elements
     * @return
     */
    @CommandsDataType(commands = "pfadd", dataType = CommandsDataTypeEnum.HYPERLOGLOG)
    Long pfadd(String key, int seconds, String... elements);

    /**
     * 返回给定 HyperLogLog 的基数估算值。
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "pfcount", dataType = CommandsDataTypeEnum.HYPERLOGLOG)
    long pfcount(String key);

    /**
     * 返回给定 HyperLogLog 的基数估算值。
     *
     * @param keys
     * @return
     */
    @CommandsDataType(commands = "pfcount", dataType = CommandsDataTypeEnum.HYPERLOGLOG)
    long pfcount(String... keys);

    /**
     * 将多个 HyperLogLog 合并为一个 HyperLogLog ，合并后的 HyperLogLog 的基数估算值是通过对所有 给定 HyperLogLog 进行并集计算得出的。
     *
     * @param destkey
     * @param seconds
     * @param sourcekeys
     * @return
     */
    @CommandsDataType(commands = "pfmerge", dataType = CommandsDataTypeEnum.HYPERLOGLOG)
    String pfmerge(String destkey, int seconds, String... sourcekeys);

    /**
     * BLPOP key [key ...] timeout
     * 删除，并获得该列表中的第一元素，或阻塞，直到有一个可用
     *
     * @param timeout
     * @param key
     * @return
     */
    @CommandsDataType(commands = "blpop", dataType = CommandsDataTypeEnum.LIST)
    List<String> blpop(int timeout, String key);

    /**
     * BRPOP key [key ...] timeout
     * 删除，并获得该列表中的最后一个元素，或阻塞，直到有一个可用
     * 注：阻塞类命令，需考虑Lettuce单连接会阻塞其他命令执行，应单独申请cacheType或者使用连接池
     *
     * @param timeout
     * @param key
     * @return
     */
    @CommandsDataType(commands = "brpop", dataType = CommandsDataTypeEnum.LIST)
    List<String> brpop(int timeout, String key);

    /**
     * GEOADD key longitude latitude member [longitude latitude member ...]
     * 添加一个或多个地理空间位置到sorted set
     *
     * @param key
     * @param longitude
     * @param latitude
     * @param member
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "geoadd", dataType = CommandsDataTypeEnum.GEO)
    Long geoadd(String key, double longitude, double latitude, String member, int seconds);

    /**
     * GEOADD key longitude latitude member [longitude latitude member ...]
     * 添加一个或多个地理空间位置到sorted set
     *
     * @param key
     * @param memberCoordinateMap
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "geoadd", dataType = CommandsDataTypeEnum.GEO)
    Long geoadd(String key, Map<String, GeoCoordinate> memberCoordinateMap, int seconds);

    /**
     * GEODIST key member1 member2 [unit]
     * 返回两个地理空间之间的距离
     *
     * @param key
     * @param member1
     * @param member2
     * @return
     */
    @CommandsDataType(commands = "geodist", dataType = CommandsDataTypeEnum.GEO)
    Double geodist(String key, String member1, String member2);

    /**
     * GEODIST key member1 member2 [unit]
     * 返回两个地理空间之间的距离
     *
     * @param key
     * @param member1
     * @param member2
     * @param unit
     * @return
     */
    @CommandsDataType(commands = "geodist", dataType = CommandsDataTypeEnum.GEO)
    Double geodist(String key, String member1, String member2, GeoUnit unit);

    /**
     * GEOHASH key member [member ...]
     * 返回一个标准的地理空间的Geohash字符串
     *
     * @param key
     * @param members
     * @return
     */
    @CommandsDataType(commands = "geohash", dataType = CommandsDataTypeEnum.GEO)
    List<String> geohash(String key, String... members);

    /**
     * GEOPOS key member [member ...]
     * 返回地理空间的经纬度
     *
     * @param key
     * @param members
     * @return
     */
    @CommandsDataType(commands = "geopos", dataType = CommandsDataTypeEnum.GEO)
    List<GeoCoordinate> geopos(String key, String... members);
    //禁用，Jedis与Lettuce返回不一致，让大家用下面带param的方法，TODO 可以分开提供实现
    //List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit);

    /**
     * GEORADIUS key longitude latitude radius m|km|ft|mi [WITHCOORD] [WITHDIST] [WITHHASH] [COUNT count]
     * 查询指定半径内所有的地理空间元素的集合。
     *
     * @param key
     * @param longitude
     * @param latitude
     * @param radius
     * @param unit
     * @param param
     * @return
     */
    @CommandsDataType(commands = "georadius", dataType = CommandsDataTypeEnum.GEO)
    List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit, GeoRadiusParam param);
    //禁用，Jedis与Lettuce返回不一致，让大家用下面带param的方法，TODO 可以分开提供实现
    //List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit);

    /**
     * GEORADIUSBYMEMBER key member radius m|km|ft|mi [WITHCOORD] [WITHDIST] [WITHHASH] [COUNT count]
     * 查询指定半径内匹配到的最大距离的一个地理空间元素。
     *
     * @param key
     * @param member
     * @param radius
     * @param unit
     * @param param
     * @return
     */
    @CommandsDataType(commands = "georadiusByMember", dataType = CommandsDataTypeEnum.GEO)
    List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit, GeoRadiusParam param);

    /**
     * 该命令将 Redis 字符串视为一个位数组，并且能够处理具有不同位宽和任意非（必要）对齐偏移量的特定整数字段。实际上，使用此命令可以将位偏移量为1234的带符号5位整数设置为特定值，从偏移量4567中检索31位无符号整数。类似地，该命令处理指定整数的递增和递减，提供保证和良好指定的溢出和下溢行为，用户可以配置。
     * BITFIELD 能够在同一个命令调用中使用多位字段。它需要执行一系列操作，并返回一个响应数组，其中每个数组都与参数列表中的相应操作相匹配。
     * Jedis 专用
     *
     * @param key
     * @param arguments
     * @return
     */
    @CommandsDataType(commands = "bitfield", dataType = CommandsDataTypeEnum.BITMAP)
    List<Long> bitfield(String key, String... arguments);

    /**
     * 该命令将 Redis 字符串视为一个位数组，并且能够处理具有不同位宽和任意非（必要）对齐偏移量的特定整数字段。实际上，使用此命令可以将位偏移量为1234的带符号5位整数设置为特定值，从偏移量4567中检索31位无符号整数。类似地，该命令处理指定整数的递增和递减，提供保证和良好指定的溢出和下溢行为，用户可以配置。
     * BITFIELD 能够在同一个命令调用中使用多位字段。它需要执行一系列操作，并返回一个响应数组，其中每个数组都与参数列表中的相应操作相匹配。
     * Lettuce 专用
     *
     * @param key
     * @param bitFieldArgs
     * @return
     */
    @CommandsDataType(commands = "bitfield", dataType = CommandsDataTypeEnum.BITMAP)
    List<Long> bitfield(String key, BitFieldArgs bitFieldArgs);

    //List<Long> bitfieldReadonly(String key, String... arguments);

    /**
     * HSTRLEN key field
     * 获取hash里面指定field的长度
     * jedis不支持
     *
     * @param key
     * @param field
     * @return
     */
    @CommandsDataType(commands = "hstrlen", dataType = CommandsDataTypeEnum.HASH)
    Long hstrlen(String key, String field);

//
//    StreamEntryID xadd(String key, StreamEntryID id, Map<String, String> hash);
//
//    StreamEntryID xadd(String key, StreamEntryID id, Map<String, String> hash, long maxLen, boolean approximateLength);
//
//    Long xlen(String key);
//
//    List<StreamEntry> xrange(String key, StreamEntryID start, StreamEntryID end, int count);
//
//    List<StreamEntry> xrevrange(String key, StreamEntryID end, StreamEntryID start, int count);
//
//    List<Map.Entry<String, List<StreamEntry>>> xread(int count, long block, Map.Entry... streams);
//
//    long xack(String key, String group, StreamEntryID... ids);
//
//    String xgroupCreate(String key, String groupname, StreamEntryID id, boolean makeStream);
//
//    String xgroupSetID(String key, String groupname, StreamEntryID id);
//
//    long xgroupDestroy(String key, String groupname);
//
//    Long xgroupDelConsumer(String key, String groupname, String consumerName);
//
//    long xdel(String key, StreamEntryID... ids);
//
//    long xtrim(String key, long maxLen, boolean approximateLength);
//
//    List<Map.Entry<String, List<StreamEntry>>> xreadGroup(String groupname, String consumer, int count, long block, boolean noAck, Map.Entry... streams);
//
//    List<StreamPendingEntry> xpending(String key, String groupname, StreamEntryID start, StreamEntryID end, int count, String consumername);
//
//    List<StreamEntry> xclaim(String key, String group, String consumername, long minIdleTime, long newIdleTime, int retries, boolean force, StreamEntryID... ids);
//
//    StreamInfo xinfoStream(String key);
//
//    List<StreamGroupInfo> xinfoGroup(String key);
//
//    List<StreamConsumersInfo> xinfoConsumers(String key, String group);
}
