package org.cloud.demo.workflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.cloud.demo.common.web.BaseController;
import org.cloud.demo.common.web.domain.R;
import org.cloud.demo.common.web.domain.TableDataInfo;
import org.cloud.demo.common.web.page.PageQuery;
import org.cloud.demo.workflow.domain.dto.ProcessQuery;
import org.cloud.demo.workflow.domain.vo.ProcessFormVo;
import org.cloud.demo.workflow.domain.vo.WfDefinitionVo;
import org.cloud.demo.workflow.service.WfProcessService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 工作流流程管理
 */
@Slf4j
@RestController
@RequestMapping("/workflow/process")
@Tag(name = "工作流流程管理", description = "WfProcessController")
public class WfProcessController extends BaseController {
    private final WfProcessService wfProcessService;

    public WfProcessController(WfProcessService wfProcessService) {
        this.wfProcessService = wfProcessService;
    }

    /**
     * 查询流程部署关联表单信息
     *
     * @param definitionId 流程定义id
     * @param deployId     流程部署id
     * @param procInsId
     */
    @GetMapping("/getProcessForm")
    @Operation(description = "查询流程部署关联表单信息", parameters = {
            @Parameter(name = "definitionId", description = "流程定义id", required = true),
            @Parameter(name = "deployId", description = "流程部署id", required = true),
            @Parameter(name = "procInsId", description = "流程实例id")}
    )
    public R<ProcessFormVo> getForm(@RequestParam(value = "definitionId") String definitionId,
                                    @RequestParam(value = "deployId") String deployId,
                                    @RequestParam(value = "procInsId", required = false) String procInsId) {
        return R.ok(wfProcessService.selectFormContent(definitionId, deployId, procInsId));
    }

    /**
     * 根据流程定义id启动流程实例
     *
     * @param processDefId 流程定义id
     * @param variables    变量集合,json对象
     */
    @PostMapping("/start/{processDefId}")
    @Operation(description = "查询流程部署关联表单信息", parameters = {
            @Parameter(name = "processDefId", description = "流程定义id", required = true),
            @Parameter(name = "variables", description = "变量集合,json对象", required = true)}
    )
    public R<String> start(@PathVariable(value = "processDefId") String processDefId, @RequestBody Map<String, Object> variables) {
        wfProcessService.startProcessByDefId(processDefId, variables);
        return R.ok("流程启动成功");
    }


    /**
     * 查询可发起流程列表
     *
     * @param processQuery 查询参数
     * @param pageQuery 分页参数
     */
    @GetMapping(value = "/list")
    public TableDataInfo<WfDefinitionVo> startProcessList(ProcessQuery processQuery, PageQuery pageQuery) {
        return wfProcessService.selectPageStartProcessList(processQuery, pageQuery);
    }
}

