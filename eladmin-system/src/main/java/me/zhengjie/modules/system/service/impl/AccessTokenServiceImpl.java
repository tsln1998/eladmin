/*
*  Copyright 2019-2020 Zheng Jie
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package me.zhengjie.modules.system.service.impl;

import me.zhengjie.modules.system.domain.AccessToken;
import me.zhengjie.modules.system.service.DataFilterService;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.system.repository.AccessTokenRepository;
import me.zhengjie.modules.system.service.AccessTokenService;
import me.zhengjie.modules.system.service.dto.AccessTokenDto;
import me.zhengjie.modules.system.service.dto.AccessTokenQueryCriteria;
import me.zhengjie.modules.system.service.mapstruct.AccessTokenMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author Tsln
* @date 2021-03-02
**/
@Service
@RequiredArgsConstructor
public class AccessTokenServiceImpl implements AccessTokenService {

    private final AccessTokenRepository accessTokenRepository;
    private final AccessTokenMapper accessTokenMapper;
    private final DataFilterService dataFilterService;

    @Override
    public Map<String,Object> queryAll(AccessTokenQueryCriteria criteria, Pageable pageable){
        Page<AccessToken> page = accessTokenRepository.findAll((root, query, cb) -> cb.and(
                QueryHelp.getPredicate(root,criteria,cb),
                root.get("userId").in(dataFilterService.getUserIds(true, false))
        ),pageable);
        return PageUtil.toPage(page.map(accessTokenMapper::toDto));
    }

    @Override
    public List<AccessTokenDto> queryAll(AccessTokenQueryCriteria criteria){
        return accessTokenMapper.toDto(accessTokenRepository.findAll((root, query, cb) -> cb.and(
                QueryHelp.getPredicate(root,criteria,cb),
                root.get("userId").in(dataFilterService.getUserIds(true, false))
        )));
    }

    @Override
    @Transactional
    public AccessTokenDto findById(Long tokenId) {
        AccessToken accessToken = accessTokenRepository.findById(tokenId).orElseGet(AccessToken::new);
        ValidationUtil.isNull(accessToken.getTokenId(),"AccessToken","tokenId",tokenId);
        return accessTokenMapper.toDto(accessToken);
    }

    @Override
    public AccessTokenDto findByAppId(String appId) {
        AccessToken token = new AccessToken();
        token.setAppId(appId);

        AccessToken accessToken = accessTokenRepository.findOne(Example.of(token)).orElseGet(AccessToken::new);
        ValidationUtil.isNull(accessToken.getTokenId(), "AccessToken", "appId", appId);
        return accessTokenMapper.toDto(accessToken);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccessTokenDto create(AccessToken resources) {
        resources.setUserId(SecurityUtils.getCurrentUserId());
        resources.setAppId(RandomStringUtils.randomAlphanumeric(12).toUpperCase(Locale.ROOT));
        resources.setAppKey(RandomStringUtils.randomAlphanumeric(16).toUpperCase(Locale.ROOT));
        return accessTokenMapper.toDto(accessTokenRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(AccessToken resources) {
        AccessToken accessToken = accessTokenRepository.findById(resources.getTokenId()).orElseGet(AccessToken::new);
        ValidationUtil.isNull( accessToken.getTokenId(),"AccessToken","id",resources.getTokenId());
        resources.setUserId(null);
        resources.setAppId(null);
        resources.setAppKey(null);
        accessToken.copy(resources);
        accessTokenRepository.save(accessToken);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long tokenId : ids) {
            accessTokenRepository.deleteById(tokenId);
        }
    }

    @Override
    public void download(List<AccessTokenDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (AccessTokenDto accessToken : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put(" userId",  accessToken.getUserId());
            map.put("AppID", accessToken.getAppId());
            map.put("AppKey", accessToken.getAppKey());
            map.put("备注", accessToken.getTitle());
            map.put("创建时间", accessToken.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}