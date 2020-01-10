package com.zk.community.util;

import com.zk.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，用于代替session对象
 */
@Component
public class HostHolder {

    private ThreadLocal<User> threadLocal = new ThreadLocal<>();

    public void set(User user) {
        threadLocal.set(user);
    }

    public User get() {
        return threadLocal.get();
    }

    public void clear() {
        threadLocal.remove();
    }
}
