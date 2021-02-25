package me.zhengjie.modules.system.service.impl;

import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import me.zhengjie.config.ElPermissionConfig;
import me.zhengjie.modules.system.domain.AccessToken;
import me.zhengjie.modules.system.domain.Role;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.modules.system.repository.AccessTokenRepository;
import me.zhengjie.modules.system.repository.RoleRepository;
import me.zhengjie.modules.system.repository.UserRepository;
import me.zhengjie.modules.system.service.DataFilterService;
import me.zhengjie.utils.SecurityUtils;
import me.zhengjie.utils.SpringContextHolder;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("filter")
@RequiredArgsConstructor
public class DataFilterServiceImpl implements DataFilterService {
    private final ElPermissionConfig el;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AccessTokenRepository accessTokenRepository;

    private Optional<DataFilterService> _self_instance = Optional.empty();

    private DataFilterService self() {
        if (!_self_instance.isPresent())
            _self_instance = Optional.of(SpringContextHolder.getBean(DataFilterService.class));
        return _self_instance.get();
    }

    private Set<Long> getBreakRoleIdSet() {
        return roleRepository
                .findAll(Example.of(new Role().setBreakLookup(true)))
                .stream().map(Role::getId)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isNeedFilter() {
        return !el.check("__hack__");
    }

    @Override
    public Long getParentId() {
        return self().getParentId(true);
    }

    @Override
    public Long getParentId(boolean useBreak) {
        return self().getParentPath(SecurityUtils.getCurrentUserId(), useBreak).firstElement().getId();
    }

    @Override
    public Stack<User> getParentPath(Long userId) {
        return self().getParentPath(userId, false);
    }

    @Override
    public Stack<User> getParentPath(Long userId, boolean useBreak) {
        Set<Long> breakRoleIdSet = useBreak ? getBreakRoleIdSet() : Sets.newHashSet();

        Long nextId = userId;
        Stack<User> path = new Stack<>();
        while (Objects.nonNull(nextId)) {
            User user = userRepository.findById(nextId).orElseGet(User::new);
            if (Objects.nonNull(user.getId())) {
                path.add(user);
            }
            if (user.getRoles().stream().map(Role::getId).anyMatch(breakRoleIdSet::contains)) {
                break;
            }
            nextId = user.getParentId();
        }
        return path;
    }

    @Override
    public Collection<Long> getUserIds() {
        return self().getUserIds(false, false);
    }

    @Override
    public Collection<Long> getUserIds(boolean useBreak, boolean containBreakNode) {
        // 查找熔断权限ID
        Set<Long> breakRoleIdSet = useBreak ? getBreakRoleIdSet() : Sets.newHashSet();
        // 查找ID
        Set<Long> next = Sets.newHashSet(self().getParentId());
        Set<Long> set = Sets.newHashSet();
        while (next.size() > 0) {
            // Add to set
            set.addAll(next);
            // Search Next
            List<User> users = userRepository.findAll((Specification<User>) (root, query, cb)
                    -> cb.and(root.get("parentId").in(next)));
            // Clear
            next.clear();
            // Check break
            for (User u : users) {
                boolean isBreak = u.getRoles().stream()
                        .map(Role::getId)
                        .anyMatch(breakRoleIdSet::contains);
                if (isBreak) {
                    if (containBreakNode) {
                        set.add(u.getId());
                    }
                } else {
                    next.add(u.getId());
                }
            }
        }
        return set;
    }

    @Override
    public boolean accessCheck(Long... targetUserId) {
        return !self().isNeedFilter() || self().getUserIds().containsAll(Sets.newHashSet(targetUserId));
    }

    @Override
    public boolean accessRoleCheck(Long... targetRoleId) {
        return Arrays.stream(targetRoleId)
                .map(roleRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Role::getCreateById)
                .allMatch(id -> self().accessCheck(id));
    }

    @Override
    public boolean accessAccessTokenCheck(Long... targetId) {
        return Arrays.stream(targetId)
                .map(accessTokenRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(AccessToken::getUserId)
                .allMatch(id -> self().accessCheck(id));
    }
}
