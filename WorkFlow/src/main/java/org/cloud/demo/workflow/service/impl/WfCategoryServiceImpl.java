package org.cloud.demo.workflow.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.cloud.demo.common.web.domain.TableDataInfo;
import org.cloud.demo.common.web.exception.ServiceException;
import org.cloud.demo.common.web.page.PageQuery;
import org.cloud.demo.workflow.domain.WfCategory;
import org.cloud.demo.workflow.domain.dto.WfCategoryDTO;
import org.cloud.demo.workflow.domain.vo.WfCategoryVo;
import org.cloud.demo.workflow.mapper.WfCategoryMapper;
import org.cloud.demo.workflow.service.WfCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class WfCategoryServiceImpl implements WfCategoryService {
    private final WfCategoryMapper wfCategoryMapper;

    @Override
    @Transactional
    public int insert(WfCategoryDTO category) {
        if (checkCategoryCodeUnique(category.getName())) {
            throw new ServiceException("新增流程分类'" + category.getName() + "'失败，流程名称已存在");
        }
        WfCategory wfCategory = new WfCategory();
        BeanUtil.copyProperties(category, wfCategory);
        return wfCategoryMapper.insert(wfCategory);
    }

    @Override
    public TableDataInfo<WfCategoryVo> queryPageList(WfCategoryDTO category, PageQuery pageQuery) {
        LambdaQueryWrapper<WfCategory> lqw = buildQueryWrapper(category);
        Page<WfCategoryVo> result = wfCategoryMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional
    public int update(WfCategoryDTO category) {
        if (checkCategoryCodeUnique(category.getName())) {
            throw new ServiceException("修改流程分类'" + category.getName() + "'失败，流程名称已存在");
        }
        WfCategory wfCategory = new WfCategory();
        BeanUtil.copyProperties(category, wfCategory);
        return wfCategoryMapper.updateById(wfCategory);
    }

    @Override
    @Transactional
    public int deleteByIds(Long[] categoryIds) {
        return wfCategoryMapper.deleteBatchIds(Arrays.asList(categoryIds));
    }

    private LambdaQueryWrapper<WfCategory> buildQueryWrapper(WfCategoryDTO category) {
        LambdaQueryWrapper<WfCategory> lqw = Wrappers.lambdaQuery();
        lqw.like(StrUtil.isNotBlank(category.getName()), WfCategory::getName, category.getName());
        lqw.eq(StrUtil.isNotBlank(category.getDesc()), WfCategory::getDesc, category.getDesc());
        return lqw;
    }

    private boolean checkCategoryCodeUnique(String categoryName) {
        return wfCategoryMapper.exists(new LambdaQueryWrapper<WfCategory>().eq(WfCategory::getName, categoryName));
    }
}
