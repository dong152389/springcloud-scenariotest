package org.cloud.demo.workflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import org.cloud.demo.common.validate.AddGroup;
import org.cloud.demo.common.validate.EditGroup;
import org.cloud.demo.common.web.BaseController;
import org.cloud.demo.common.web.domain.R;
import org.cloud.demo.common.web.domain.TableDataInfo;
import org.cloud.demo.common.web.page.PageQuery;
import org.cloud.demo.workflow.domain.dto.WfCategoryDTO;
import org.cloud.demo.workflow.domain.vo.WfCategoryVo;
import org.cloud.demo.workflow.service.WfCategoryService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 流程分类
 */
@RequestMapping("category")
@RestController
@Tag(name = "流程分类相关", description = "WfCategoryController")
public class WfCategoryController extends BaseController {
    private final WfCategoryService categoryService;

    public WfCategoryController(WfCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 新增流程分类
     */
    @Operation(description = "新增流程分类")
    @PostMapping("save")
    public R<Void> add(@Validated(value = AddGroup.class) @RequestBody WfCategoryDTO category) {
        return toAjax(categoryService.insert(category));
    }

    /**
     * 查询流程分类列表
     */
    @GetMapping("list")
    @Operation(description = "查询流程分类列表")
    public TableDataInfo<WfCategoryVo> list(WfCategoryDTO category, PageQuery pageQuery) {
        return categoryService.queryPageList(category, pageQuery);
    }

    /**
     * 修改流程分类
     */
    @Operation(description = "修改流程分类")
    @PutMapping("edit")
    public R<Void> edit(@Validated(value = EditGroup.class)  @RequestBody WfCategoryDTO category) {
        return toAjax(categoryService.update(category));
    }

    /**
     * 删除流程分类
     */
    @Operation(description = "删除流程分类")
    @PutMapping("delete/{categoryIds}")
    public R<Void> delete(@NotEmpty(message = "主键不能为空") @PathVariable Long[] categoryIds) {
        return toAjax(categoryService.deleteByIds(categoryIds));
    }
}
