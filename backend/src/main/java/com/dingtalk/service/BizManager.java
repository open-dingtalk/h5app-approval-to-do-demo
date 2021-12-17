package com.dingtalk.service;

import com.aliyun.dingboot.common.token.ITokenManager;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.dingtalk.config.AppConfig;
import com.dingtalk.constant.UrlConstant;
import com.dingtalk.model.FlowEntity;
import com.dingtalk.utils.RandomUtil;
import com.taobao.api.ApiException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 主业务service，编写你的代码
 */
@Service
public class BizManager {
    @Autowired
    ITokenManager tokenManager;
    @Autowired
    private AppConfig appConfig;
    private HashMap<String, String> hashMap = new HashMap<>();

    public String hello() {
        return "HelloWorld";
    }


    public String getProcessCode() throws ApiException {
        // 1. 获取access_token
        String accessToken = tokenManager.getAccessToken(appConfig.getAppKey(), appConfig.getAppSecret());
        //2获取
        DingTalkClient client = new DefaultDingTalkClient(UrlConstant.PROCESS_GET_NAME);
        OapiProcessGetByNameRequest oapiProcessGetByNameRequest = new OapiProcessGetByNameRequest();
        oapiProcessGetByNameRequest.setName("易快报报销单3");
        // 已经创建后可以通过名称查询模版
//        OapiProcessGetByNameResponse execute = client.execute(oapiProcessGetByNameRequest, accessToken);
//        if(execute.getErrcode() == 0 && StringUtils.isNotEmpty(execute.getProcessCode())){
//            return execute.getProcessCode();
//        }
        // 第一次创建时创建模版
        return createProcess();
    }

    /**
     * 创建模板
     *
     * @return
     */
    public String createProcess() throws ApiException {
        // 1. 获取access_token
        //String accessToken = AccessTokenUtil.getAccessToken();
        String accessToken = tokenManager.getAccessToken(appConfig.getAppKey(), appConfig.getAppSecret());
        // 2. 创建模板
        DingTalkClient client = new DefaultDingTalkClient(UrlConstant.PROCESS_SAVE);
        OapiProcessSaveRequest oapiProcessSaveRequest = new OapiProcessSaveRequest();
        OapiProcessSaveRequest.SaveProcessRequest saveProcessRequest = new OapiProcessSaveRequest.SaveProcessRequest();
        saveProcessRequest.setAgentid(appConfig.getAgentId());
        ArrayList<OapiProcessSaveRequest.FormComponentVo> formComponentVos = new ArrayList<>();
        saveProcessRequest.setName("易快报报销单" + RandomUtil.getRandomString(6));
//        saveProcessRequest.setProcessCode("PROC-C38B684F-087B-4F78-9DFF-9FB3D3CD393D");
        saveProcessRequest.setDescription("易快报报销单");
        OapiProcessSaveRequest.FormComponentVo formComponentVo = new OapiProcessSaveRequest.FormComponentVo();
        formComponentVo.setComponentName("TextField");
        OapiProcessSaveRequest.FormComponentPropVo formComponentPropVo = new OapiProcessSaveRequest.FormComponentPropVo();
        formComponentPropVo.setId("TextField-submitId");
        formComponentPropVo.setLabel("报销人");
        formComponentPropVo.setRequired(true);
        formComponentVo.setProps(formComponentPropVo);
        formComponentVos.add(formComponentVo);
        formComponentVo = new OapiProcessSaveRequest.FormComponentVo();
        formComponentPropVo = new OapiProcessSaveRequest.FormComponentPropVo();
        formComponentVo.setComponentName("TextField");
        formComponentPropVo.setId("TextField-flowDesc");
        formComponentPropVo.setLabel("申请事由");
        formComponentPropVo.setRequired(true);
        formComponentVo.setProps(formComponentPropVo);
        formComponentVos.add(formComponentVo);
        formComponentVo = new OapiProcessSaveRequest.FormComponentVo();
        formComponentPropVo = new OapiProcessSaveRequest.FormComponentPropVo();
        formComponentVo.setComponentName("TextField");
        formComponentPropVo.setId("TextField-flowAmount");
        formComponentPropVo.setLabel("申请金额");
        formComponentPropVo.setRequired(true);
        formComponentVo.setProps(formComponentPropVo);
        formComponentVos.add(formComponentVo);

        saveProcessRequest.setFormComponentList(formComponentVos);
        saveProcessRequest.setFakeMode(true);
        oapiProcessSaveRequest.setSaveProcessRequest(saveProcessRequest);
        OapiProcessSaveResponse execute = client.execute(oapiProcessSaveRequest, accessToken);
        // 3. 返回模板id
        if (execute.getErrcode() != 0) {
            throw new ApiException("-1", execute.getErrmsg());
        }
        return execute.getResult().getProcessCode();
    }

    /**
     * 创建并获取实例id
     *
     * @param flowEntity
     * @return
     * @throws ApiException
     */
    public String createWorkRecord(FlowEntity flowEntity) throws ApiException {
        // 1. 获取access_token
//        String accessToken = AccessTokenUtil.getAccessToken();
        String accessToken = tokenManager.getAccessToken(appConfig.getAppKey(), appConfig.getAppSecret());
        OapiProcessWorkrecordCreateRequest oapiProcessWorkrecordCreateRequest = new OapiProcessWorkrecordCreateRequest();
        OapiProcessWorkrecordCreateRequest.SaveFakeProcessInstanceRequest saveFakeProcessInstanceRequest = new OapiProcessWorkrecordCreateRequest.SaveFakeProcessInstanceRequest();
        String processCode = getProcessCode();
        saveFakeProcessInstanceRequest.setProcessCode(processCode);
        saveFakeProcessInstanceRequest.setAgentid(appConfig.getAgentId());
        saveFakeProcessInstanceRequest.setOriginatorUserId(flowEntity.getUserId());
        OapiProcessWorkrecordCreateRequest.FormComponentValueVo formComponentValueVo = new OapiProcessWorkrecordCreateRequest.FormComponentValueVo();
        ArrayList<OapiProcessWorkrecordCreateRequest.FormComponentValueVo> formComponentValueVos = new ArrayList<>();
        formComponentValueVo.setName("报销人");
        formComponentValueVo.setValue(flowEntity.getUserName());
        formComponentValueVos.add(formComponentValueVo);

        formComponentValueVo = new OapiProcessWorkrecordCreateRequest.FormComponentValueVo();
        formComponentValueVo.setName("申请事由");
        formComponentValueVo.setValue(flowEntity.getFlowDesc());
        formComponentValueVos.add(formComponentValueVo);

        formComponentValueVo = new OapiProcessWorkrecordCreateRequest.FormComponentValueVo();
        formComponentValueVo.setName("申请金额");
        formComponentValueVo.setValue(flowEntity.getFlowAmount().setScale(2, BigDecimal.ROUND_UP).toString());
        formComponentValueVos.add(formComponentValueVo);

        saveFakeProcessInstanceRequest.setFormComponentValues(formComponentValueVos);
        saveFakeProcessInstanceRequest.setUrl(flowEntity.getUrl());
        saveFakeProcessInstanceRequest.setTitle(flowEntity.getUserName() + "的" + flowEntity.getFlowTitleName());
        oapiProcessWorkrecordCreateRequest.setRequest(saveFakeProcessInstanceRequest);
        DingTalkClient client = new DefaultDingTalkClient(UrlConstant.PROCESS_WORK_RECORD_CREATE);
        OapiProcessWorkrecordCreateResponse execute = client.execute(oapiProcessWorkrecordCreateRequest, accessToken);
        if (execute.getErrcode() != 0) {
            throw new ApiException("-1", execute.getErrmsg());
        }
        return execute.getResult().getProcessInstanceId();
    }

    /**
     * 更新实例
     *
     * @param flowEntity
     * @return
     * @throws ApiException
     */
    public Long updateWorkRecord(FlowEntity flowEntity) throws ApiException {
        // 1. 获取access_token
//        String accessToken = AccessTokenUtil.getAccessToken();
        String accessToken = tokenManager.getAccessToken(appConfig.getAppKey(), appConfig.getAppSecret());
        OapiProcessWorkrecordUpdateRequest oapiProcessWorkrecordUpdateRequest = new OapiProcessWorkrecordUpdateRequest();
        OapiProcessWorkrecordUpdateRequest.UpdateProcessInstanceRequest updateProcessInstanceRequest = new OapiProcessWorkrecordUpdateRequest.UpdateProcessInstanceRequest();
        updateProcessInstanceRequest.setAgentid(appConfig.getAgentId());
        updateProcessInstanceRequest.setProcessInstanceId(flowEntity.getWorkRecordId());
        updateProcessInstanceRequest.setStatus("COMPLETED");
        updateProcessInstanceRequest.setResult(flowEntity.getFlowStatus());
        oapiProcessWorkrecordUpdateRequest.setRequest(updateProcessInstanceRequest);
        DingTalkClient client = new DefaultDingTalkClient(UrlConstant.PROCESS_WORK_RECORD_UPDATE);
        OapiProcessWorkrecordUpdateResponse execute = client.execute(oapiProcessWorkrecordUpdateRequest, accessToken);
        if (execute.getErrcode() != 0) {
            throw new ApiException("-1", execute.getErrmsg());
        }
        return execute.getErrcode();
    }

    /**
     * 创建待办
     *
     * @param flowEntity userName,userId，
     * @return
     * @throws ApiException
     */
    public String createWorkRecordTask(FlowEntity flowEntity) throws ApiException {
        // 1. 获取access_token
//        String accessToken = AccessTokenUtil.getAccessToken();
        String accessToken = tokenManager.getAccessToken(appConfig.getAppKey(), appConfig.getAppSecret());
        //2创建实例
        String workRecord = createWorkRecord(flowEntity);
        //3创建待办任务
        OapiProcessWorkrecordTaskCreateRequest request = new OapiProcessWorkrecordTaskCreateRequest();
        OapiProcessWorkrecordTaskCreateRequest.SaveTaskRequest saveTaskRequest = new OapiProcessWorkrecordTaskCreateRequest.SaveTaskRequest();
        ArrayList<OapiProcessWorkrecordTaskCreateRequest.TaskTopVo> taskTopVos = new ArrayList<>();
        OapiProcessWorkrecordTaskCreateRequest.TaskTopVo taskTopVo = new OapiProcessWorkrecordTaskCreateRequest.TaskTopVo();
        taskTopVo.setUserid(flowEntity.getUserId());
        taskTopVo.setUrl(flowEntity.getUrl());
        taskTopVos.add(taskTopVo);
        saveTaskRequest.setTasks(taskTopVos);
        saveTaskRequest.setProcessInstanceId(workRecord);
        request.setRequest(saveTaskRequest);
        DingTalkClient client = new DefaultDingTalkClient(UrlConstant.PROCESS_WORK_RECORD_TASK_CREATE);
        OapiProcessWorkrecordTaskCreateResponse execute = client.execute(request, accessToken);
        if (execute.getErrcode() != 0) {
            throw new ApiException("-1", execute.getErrmsg());
        }
        hashMap.put(flowEntity.getUuid(), workRecord);
        return workRecord;
    }

    /**
     * 先不用
     *
     * @param flowEntity
     * @throws ApiException
     */
    public void UpdateWorkRecordTask(FlowEntity flowEntity) throws ApiException {
        // 1. 获取access_token
//        String accessToken = AccessTokenUtil.getAccessToken();
        String accessToken = tokenManager.getAccessToken(appConfig.getAppKey(), appConfig.getAppSecret());
        OapiProcessWorkrecordUpdateRequest oapiProcessWorkrecordUpdateRequest = new OapiProcessWorkrecordUpdateRequest();
        OapiProcessWorkrecordUpdateRequest.UpdateProcessInstanceRequest updateProcessInstanceRequest = new OapiProcessWorkrecordUpdateRequest.UpdateProcessInstanceRequest();
        updateProcessInstanceRequest.setAgentid(appConfig.getAgentId());
        updateProcessInstanceRequest.setProcessInstanceId(flowEntity.getWorkRecordId());
        updateProcessInstanceRequest.setStatus("COMPLETED");
        updateProcessInstanceRequest.setResult(flowEntity.getFlowStatus());
        oapiProcessWorkrecordUpdateRequest.setRequest(updateProcessInstanceRequest);
        DingTalkClient client = new DefaultDingTalkClient(UrlConstant.PROCESS_WORK_RECORD_UPDATE);
        OapiProcessWorkrecordUpdateResponse execute = client.execute(oapiProcessWorkrecordUpdateRequest, accessToken);
    }

    /**
     * 获取待办列表
     *
     * @param flowEntity
     * @throws ApiException
     */
    public OapiProcessWorkrecordTaskQueryResponse getTasks(FlowEntity flowEntity) throws ApiException {
        // 1. 获取access_token
//        String accessToken = AccessTokenUtil.getAccessToken();
        String accessToken = tokenManager.getAccessToken(appConfig.getAppKey(), appConfig.getAppSecret());
        OapiProcessWorkrecordTaskQueryRequest oapiProcessWorkrecordTaskQueryRequest = new OapiProcessWorkrecordTaskQueryRequest();
        oapiProcessWorkrecordTaskQueryRequest.setUserid(flowEntity.getUserId());
        oapiProcessWorkrecordTaskQueryRequest.setOffset(0L);
        oapiProcessWorkrecordTaskQueryRequest.setCount(50L);
        oapiProcessWorkrecordTaskQueryRequest.setStatus(flowEntity.getTaskStatus());
        DingTalkClient client = new DefaultDingTalkClient(UrlConstant.PROCESS_WORK_RECORD_TASK_QUERY);
        OapiProcessWorkrecordTaskQueryResponse execute = client.execute(oapiProcessWorkrecordTaskQueryRequest, accessToken);
        return execute;
    }

    /**
     * 获取根部门用户集合
     */
    public List<OapiUserListsimpleResponse.ListUserSimpleResponse> getUserlist() throws ApiException {
        String accessToken = tokenManager.getAccessToken(appConfig.getAppKey(), appConfig.getAppSecret());
        OapiUserListsimpleRequest oapiUserListsimpleRequest = new OapiUserListsimpleRequest();
        oapiUserListsimpleRequest.setDeptId(1L);
        oapiUserListsimpleRequest.setCursor(0L);
        oapiUserListsimpleRequest.setSize(100L);
        oapiUserListsimpleRequest.setOrderField("entry_asc");
        DingTalkClient client = new DefaultDingTalkClient(UrlConstant.USER_LIST_SIMPLE);
        OapiUserListsimpleResponse execute = client.execute(oapiUserListsimpleRequest, accessToken);
        if (execute.getErrcode() != 0) {
            throw new ApiException(execute.getErrmsg());
        }
        List<OapiUserListsimpleResponse.ListUserSimpleResponse> list = execute.getResult().getList();
        return list;
    }

    public String selectUuid(String uuid) {
        return hashMap.get(uuid);
    }
}
