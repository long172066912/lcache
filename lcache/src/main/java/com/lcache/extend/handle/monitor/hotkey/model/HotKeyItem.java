package com.lcache.extend.handle.monitor.hotkey.model;

import com.lcache.core.cache.annotations.CommandsDataTypeUtil;
import com.lcache.core.constant.CommandsDataTypeEnum;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: HotKeyItem
 * @Description: 热key实体
 * @date 2021/7/5 9:42 AM
 */
public class HotKeyItem implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public HotKeyItem(String commands, String key, LongAdder count) {
        this.commands = commands;
        this.key = key;
        this.count = count;
    }

    private String commands;

    private String key;

    private LongAdder count;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HotKeyItem that = (HotKeyItem) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setCount(LongAdder count) {
        this.count = count;
    }

    public String getKey() {
        return key;
    }

    public LongAdder getCount() {
        return count;
    }

    public String getCommands() {
        return commands;
    }

    public void setCommands(String commands) {
        this.commands = commands;
    }

    public void calibrationCommands(String commands) {
        if (!commands.equals(this.commands)) {
            if (!CommandsDataTypeUtil.getCommandsDataType(commands).equals(CommandsDataTypeEnum.STRING)) {
                this.commands = commands;
            }
        }
    }

    @Override
    public String toString() {
        return "HotKeyItem{" +
                "commands='" + commands + '\'' +
                ", key='" + key + '\'' +
                ", count=" + count +
                '}';
    }
}
