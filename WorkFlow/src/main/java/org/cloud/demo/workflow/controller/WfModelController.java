package org.cloud.demo.workflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.cloud.demo.common.validate.AddGroup;
import org.cloud.demo.common.validate.EditGroup;
import org.cloud.demo.common.web.BaseController;
import org.cloud.demo.common.web.domain.R;
import org.cloud.demo.common.web.domain.TableDataInfo;
import org.cloud.demo.common.web.page.PageQuery;
import org.cloud.demo.workflow.domain.dto.DeployModelDTO;
import org.cloud.demo.workflow.domain.dto.WfBpmnModelDTO;
import org.cloud.demo.workflow.domain.dto.WfModelDTO;
import org.cloud.demo.workflow.domain.vo.WfModelVo;
import org.cloud.demo.workflow.service.WfModelService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 流程模型相关
 */
@RestController
@RequestMapping("model")
@Tag(name = "流程模型相关", description = "WfModelController")
public class WfModelController extends BaseController {
    private final WfModelService wfModelService;

    public WfModelController(WfModelService wfModelService) {
        this.wfModelService = wfModelService;
    }

    /**
     * 新增流程模型
     */
    @Operation(description = "新增流程模型")
    @PostMapping("save")
    public R<Void> add(@Validated(AddGroup.class) @RequestBody WfModelDTO wfModelDTO) {
        wfModelService.insert(wfModelDTO);
        return R.ok();
    }

    /**
     * 删除流程模型
     *
     * @param modelIds 流程模型主键串
     */
    @Operation(description = "删除流程模型")

    @DeleteMapping("/{modelIds}")
    public R<String> remove(@NotEmpty(message = "主键不能为空") @PathVariable String[] modelIds) {
        wfModelService.deleteByIds(Arrays.asList(modelIds));
        return R.ok();
    }

    /**
     * 修改流程模型
     */
    @Operation(description = "修改流程模型")
    @PutMapping("edit")
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody WfModelDTO wfModelDTO) {
        wfModelService.update(wfModelDTO);
        return R.ok();
    }


    /**
     * 查询流程模型列表
     *
     * @param wfModelDTO 流程模型对象
     * @param pageQuery  分页参数
     */
    @Operation(description = "查询流程模型列表")
    @GetMapping("/list")
    public TableDataInfo<WfModelVo> list(WfModelDTO wfModelDTO, PageQuery pageQuery) {
        return wfModelService.list(wfModelDTO, pageQuery);
    }


    /**
     * 保存流程模型
     */
    @Operation(description = "保存流程模型设计")
    @PostMapping("/saveBpmnXml")
    public R<Void> saveBpmnXml(@Validated(value = AddGroup.class) @RequestBody WfBpmnModelDTO wfBpmnModelDTO) {
        wfModelService.saveBpmnXml(wfBpmnModelDTO);
        return R.ok();
    }

    /**
     * 设为最新流程模型
     *
     * @param modelId
     * @return
     */
    @Operation(description = "设为最新流程模型")
    @PostMapping("/latest/{modelId}")
    public R<Void> latest(@NotBlank(message = "模型ID不能为空") @PathVariable String modelId) {
        wfModelService.latest(modelId);
        return R.ok();
    }

    /**
     * 查询流程模型的历史版本
     *
     * @param wfModelDTO 流程模型对象
     * @param pageQuery  分页参数
     */
    @Operation(description = "查询流程模型的历史版本")
    @GetMapping("/historyList")
    public TableDataInfo<WfModelVo> historyList(WfModelDTO wfModelDTO, PageQuery pageQuery) {
        return wfModelService.historyList(wfModelDTO, pageQuery);
    }

    /**
     * 获取流程模型详细信息
     *
     * @param modelId 模型主键
     */
    @Operation(description = "获取流程模型详细信息")
    @GetMapping(value = "/{modelId}")
    public R<WfModelVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable("modelId") String modelId) {
        return R.ok(wfModelService.getModel(modelId));
    }

    /**
     * 获取流程模型设计图
     *
     * @param modelId 模型主键
     */
    @Operation(description = "获取流程模型设计图")
    @GetMapping(value = "/bpmnXml/{modelId}")
    public R<String> getBpmnXml(@NotNull(message = "主键不能为空") @PathVariable("modelId") String modelId) {
        return R.ok(wfModelService.queryBpmnXmlById(modelId));
    }



    /**
     * 部署流程模型
     *
     * @param deployModelDTO 部署模型DTO
     * @return 返回结果
     */
    @Operation(description = "部署流程模型")
    @PostMapping("/deploy")
    public R<Void> deploy(@RequestBody DeployModelDTO deployModelDTO) {
        return toAjax(wfModelService.deploy(deployModelDTO));
    }
}
