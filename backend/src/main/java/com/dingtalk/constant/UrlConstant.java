package com.dingtalk.constant;

/**
 * 钉钉开放接口网关常量
 */
public class UrlConstant {

    /**
     * 获取access_token url
     */
    public static final String GET_ACCESS_TOKEN_URL = "https://oapi.dingtalk.com/gettoken";

    /**
     * 通过免登授权码获取用户信息 url
     */
    public static final String GET_USER_INFO_URL = "https://oapi.dingtalk.com/topapi/v2/user/getuserinfo";
    /**
     * 根据用户id获取用户详情 url
     */
    public static final String USER_GET_URL = "https://oapi.dingtalk.com/topapi/v2/user/get";
    /**
     * 创建模板
     */
    public static final String PROCESS_SAVE = "https://oapi.dingtalk.com/topapi/process/save";
    /**
     * 创建实例
     */
    public static final String PROCESS_WORK_RECORD_CREATE = "https://oapi.dingtalk.com/topapi/process/workrecord/create";
    /**
     * 更新实例
     */
    public static final String PROCESS_WORK_RECORD_UPDATE = "https://oapi.dingtalk.com/topapi/process/workrecord/update";
    /**
     * 创建待办
     */
    public static final String PROCESS_WORK_RECORD_TASK_CREATE = "https://oapi.dingtalk.com/topapi/process/workrecord/task/create";
    /**
     * 更新待办
     */
    public static final String PROCESS_WORK_RECORD_TASK_UPDATE = "https://oapi.dingtalk.com/topapi/v2/user/get";
    /**
     * 获取模板code接口
     */
    public static final String PROCESS_GET_NAME = "https://oapi.dingtalk.com/topapi/process/get_by_name";

    /**
     * 获取待办列表
     */
    public static final String PROCESS_WORK_RECORD_TASK_QUERY = "https://oapi.dingtalk.com/topapi/process/workrecord/task/query";

    /**
     * 获取根目录用户列表
     */
    public static final String USER_LIST_SIMPLE = "https://oapi.dingtalk.com/topapi/user/listsimple";


}
