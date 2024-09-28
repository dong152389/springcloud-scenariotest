package org.cloud.demo.workflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.cloud.demo.common.web.BaseController;
import org.cloud.demo.common.web.domain.R;
import org.cloud.demo.common.web.domain.TableDataInfo;
import org.cloud.demo.common.web.page.PageQuery;
import org.cloud.demo.workflow.domain.dto.ProcessQuery;
import org.cloud.demo.workflow.domain.vo.WfDeployVo;
import org.cloud.demo.workflow.service.WfDeployService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 流程部署
 */
@Slf4j
@RestController
@RequestMapping("/workflow/deploy")
@Tag(name = "流程部署相关", description = "WfDeployController")
public class WfDeployController extends BaseController {
    private final WfDeployService wfDeployService;

    public WfDeployController(WfDeployService wfDeployService) {
        this.wfDeployService = wfDeployService;
    }

    /**
     * 查询流程部署列表
     */
    @GetMapping("/list")
    @Operation(description = "查询流程部署列表", parameters = {
            @Parameter(name = "processQuery", description = "流程参数"),
            @Parameter(name = "pageQuery", description = "分页参数")})
    public TableDataInfo<WfDeployVo> list(ProcessQuery processQuery, PageQuery pageQuery) {
        return wfDeployService.queryPageList(processQuery, pageQuery);
    }


    /**
     * 查询流程部署版本列表
     */
    @Operation(description = "查询流程部署版本列表", parameters = {
            @Parameter(name = "processKey", description = "流程定义Key"),
            @Parameter(name = "pageQuery", description = "分页参数")})
    @GetMapping("/publishList")
    public TableDataInfo<WfDeployVo> publishList(@RequestParam String processKey, PageQuery pageQuery) {
        return wfDeployService.queryPublishList(processKey, pageQuery);
    }


    /**
     * 激活或挂起流程
     *
     * @param state        状态（active:激活 suspended:挂起）
     * @param definitionId 流程定义ID
     */
    @Operation(description = "激活或挂起流程", parameters = {
            @Parameter(name = "state", description = " 状态（active:激活 suspended:挂起）"),
            @Parameter(name = "definitionId", description = "流程定义ID")})
    @PutMapping(value = "/changeState")
    public R<Void> changeState(@RequestParam String state, @RequestParam String definitionId) {
        wfDeployService.updateState(definitionId, state);
        return R.ok();
    }

    /**
     * 读取xml文件
     *
     * @param definitionId 流程定义ID
     * @return xml文件内容
     */
    @Operation(description = "读取xml文件", parameters = {
            @Parameter(name = "definitionId", description = "流程定义ID")})
    @GetMapping("/bpmnXml/{definitionId}")
    public R<String> getBpmnXml(@PathVariable(value = "definitionId") String definitionId) {
        return R.ok(wfDeployService.queryBpmnXmlById(definitionId));
    }


    /**
     * 删除流程模型
     *
     * @param deployIds 流程部署ids
     */
    @Operation(description = "删除流程模型", parameters = {
            @Parameter(name = "deployIds", description = "流程部署id集合")})
    @DeleteMapping("delete")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @RequestBody String[] deployIds) {
        wfDeployService.deleteByIds(Arrays.asList(deployIds));
        return R.ok();
    }

}
