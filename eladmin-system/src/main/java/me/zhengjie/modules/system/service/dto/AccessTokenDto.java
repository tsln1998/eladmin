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
package me.zhengjie.modules.system.service.dto;

import lombok.Data;
import java.io.Serializable;
import java.sql.Timestamp;

/**
* @website https://el-admin.vip
* @description /
* @author Tsln
* @date 2021-03-02
**/
@Data
public class AccessTokenDto implements Serializable {

    /** ID */
    private Long tokenId;

    private Long userId;

    /** AppID */
    private String appId;

    /** AppKey */
    private String appKey;

    /** 备注 */
    private String title;

    /** 创建时间 */
    private Timestamp createTime;
}