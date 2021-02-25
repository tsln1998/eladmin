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
package me.zhengjie.modules.system.rest;

import me.zhengjie.annotation.Log;
import me.zhengjie.modules.system.domain.AccessToken;
import me.zhengjie.modules.system.service.AccessTokenService;
import me.zhengjie.modules.system.service.dto.AccessTokenQueryCriteria;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author Tsln
* @date 2021-03-02
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "API授权管理")
@RequestMapping("/api/accessToken")
public class AccessTokenController {

    private final AccessTokenService accessTokenService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('accessToken:list')")
    public void download(HttpServletResponse response, AccessTokenQueryCriteria criteria) throws IOException {
        accessTokenService.download(accessTokenService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询API授权")
    @ApiOperation("查询API授权")
    @PreAuthorize("@el.check('accessToken:list')")
    public ResponseEntity<Object> query(AccessTokenQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(accessTokenService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增API授权")
    @ApiOperation("新增API授权")
    @PreAuthorize("@el.check('accessToken:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody AccessToken resources){
        return new ResponseEntity<>(accessTokenService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改API授权")
    @ApiOperation("修改API授权")
    @PreAuthorize("@el.check('accessToken:edit') && @filter.accessAccessTokenCheck(#resources.tokenId)")
    public ResponseEntity<Object> update(@Validated @RequestBody AccessToken resources){
        accessTokenService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除API授权")
    @ApiOperation("删除API授权")
    @PreAuthorize("@el.check('accessToken:del') && @filter.accessAccessTokenCheck(#ids)")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        accessTokenService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}