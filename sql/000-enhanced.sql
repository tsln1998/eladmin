DELETE FROM sys_menu WHERE menu_id BETWEEN 90 AND 114;
DELETE FROM sys_roles_menus WHERE menu_id BETWEEN 90 AND 114;
DELETE FROM sys_menu WHERE menu_id BETWEEN 21 AND 27;
DELETE FROM sys_roles_menus WHERE menu_id BETWEEN 21 AND 27;
drop table tool_qiniu_config;
drop table tool_qiniu_content;

drop table if exists sys_access_token;
create table sys_access_token
(
    token_id    bigint auto_increment
        primary key,
    user_id     bigint       not null,
    title       varchar(128) not null comment '备注',
    app_id      varchar(128) not null comment 'AppID',
    app_key     varchar(128) not null comment 'AppKey',
    create_time varchar(255) null comment '创建时间'
)
    comment '用户接口授权';

INSERT INTO sys_menu (menu_id, pid, sub_count, type, title, name, component, menu_sort, icon, path, i_frame, cache, hidden, permission, create_by, update_by, create_time, update_time) VALUES (1001, 1, 0, 1, 'API 授权', 'AccessToken', 'system/accessToken/index', 999, 'validCode', 'access-token', false, false, false, 'accessToken:list', 'admin', 'admin', '2021-03-02 12:52:50', '2021-03-02 12:54:45');
INSERT INTO sys_roles_menus (menu_id, role_id) VALUES (1001, 1);
INSERT INTO sys_menu (menu_id, pid, sub_count, type, title, name, component, menu_sort, icon, path, i_frame, cache, hidden, permission, create_by, update_by, create_time, update_time) VALUES (1002, 1001, 0, 2, '授权新增', null, null, 999, null, null, false, false, false, 'accessToken:add', 'admin', 'admin', '2021-03-04 15:08:35', '2021-03-04 15:10:36');
INSERT INTO sys_menu (menu_id, pid, sub_count, type, title, name, component, menu_sort, icon, path, i_frame, cache, hidden, permission, create_by, update_by, create_time, update_time) VALUES (1003, 1001, 0, 2, '授权编辑', null, null, 999, null, null, false, false, false, 'accessToken:edit', 'admin', 'admin', '2021-03-04 15:09:26', '2021-03-04 15:10:47');
INSERT INTO sys_menu (menu_id, pid, sub_count, type, title, name, component, menu_sort, icon, path, i_frame, cache, hidden, permission, create_by, update_by, create_time, update_time) VALUES (1004, 1001, 0, 2, '授权删除', null, null, 999, null, null, false, false, false, 'accessToken:del', 'admin', 'admin', '2021-03-04 15:09:42', '2021-03-04 15:10:58');

drop table if exists sys_dept;
drop table if exists sys_roles_depts;
alter table sys_user drop column dept_id;
delete from sys_menu where menu_id in (35,56,57,58);
delete from sys_roles_menus where menu_id in (35,56,57,58);
drop table if exists sys_job;
drop table if exists sys_users_jobs;
delete from sys_menu where menu_id in (37,60,61,62);
delete from sys_roles_menus where menu_id in (37,60,61,62);

# alter table sys_role drop column break_lookup;
alter table sys_role add column break_lookup tinyint not null default 0 comment '数据隔离';
alter table sys_role add column create_by_id bigint not null comment '创建人';

alter table sys_role drop column level;
alter table sys_role drop column data_scope;
alter table sys_user add parent_id bigint default null null;


delete from sys_quartz_job;

drop table mnt_app;
drop table mnt_database;
drop table mnt_deploy;
drop table mnt_deploy_history;
drop table mnt_deploy_server;
drop table mnt_server;


