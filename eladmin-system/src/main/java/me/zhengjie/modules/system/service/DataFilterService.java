package me.zhengjie.modules.system.service;


import me.zhengjie.modules.system.domain.User;

import java.util.Collection;
import java.util.Stack;

public interface DataFilterService {
    /**
     * 判断指定用户是否需要过滤
     *
     * @return bool
     */
    boolean isNeedFilter();

    Long getParentId();

    Long getParentId(boolean useBreak);

    Stack<User> getParentPath(Long userId);

    Stack<User> getParentPath(Long userId, boolean useBreak);

    /**
     * 获取指定用户可访问的 userId 列表
     *
     * @return ids
     */
    Collection<Long> getUserIds();

    /**
     * 获取指定用户可访问的 userId 列表
     *
     * @return ids
     */
    Collection<Long> getUserIds(boolean useBreak, boolean containBreakNode);

    /**
     * 检查 currentUserId 用户是否可访问 targetUserId
     *
     * @param targetUserId target
     */
    boolean accessCheck(Long... targetUserId);


    boolean accessRoleCheck(Long... targetRoleId);

    boolean accessAccessTokenCheck(Long... targetId);
}
