package org.cloud.demo.workflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import org.cloud.demo.common.validate.AddGroup;
import org.cloud.demo.common.validate.EditGroup;
import org.cloud.demo.common.validate.QueryGroup;
import org.cloud.demo.common.web.BaseController;
import org.cloud.demo.common.web.domain.R;
import org.cloud.demo.common.web.domain.TableDataInfo;
import org.cloud.demo.common.web.page.PageQuery;
import org.cloud.demo.workflow.domain.dto.WfFormDTO;
import org.cloud.demo.workflow.domain.vo.WfFormVo;
import org.cloud.demo.workflow.service.WfFormService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 表单操作
 */
@RestController
@RequestMapping("form")
@Tag(name = "表单操作相关", description = "WfFormController")
public class WfFormController extends BaseController {
    private final WfFormService wfFormService;

    public WfFormController(WfFormService wfFormService) {
        this.wfFormService = wfFormService;
    }

    /**
     * 新增流程表单
     *
     * @param wfFormDTO 表单对象
     */
    @PostMapping("save")
    @Operation(description = "新增流程表单")
    public R<Void> add(@Validated(value = AddGroup.class) @RequestBody WfFormDTO wfFormDTO) {
        return toAjax(wfFormService.insert(wfFormDTO));
    }

    /**
     * 删除流程表单
     *
     * @param formIds 主键串
     */
    @Operation(description = "删除流程表单")
    @DeleteMapping("/{formIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] formIds) {
        return toAjax(wfFormService.deleteByIds(Arrays.asList(formIds)) > 0 ? 1 : 0);
    }

    /**
     * 修改流程表单
     *
     * @param wfFormDTO 表单对象
     */
    @Operation(description = "编辑流程表单")
    @PutMapping("edit")
    public R<Void> edit(@Validated(value = EditGroup.class) @RequestBody WfFormDTO wfFormDTO) {
        return toAjax(wfFormService.update(wfFormDTO));
    }

    /**
     * 查询流程表单列表
     */
    @Operation(description = "查询流程表单列表")
    @GetMapping("/list")
    public TableDataInfo<WfFormVo> list(@Validated(QueryGroup.class) WfFormDTO wfFormDTO, PageQuery pageQuery) {
        return wfFormService.queryPageList(wfFormDTO, pageQuery);
    }
}
