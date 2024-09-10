package org.cloud.demo.workflow.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.cloud.demo.common.web.domain.TableDataInfo;
import org.cloud.demo.common.web.page.PageQuery;
import org.cloud.demo.workflow.domain.WfForm;
import org.cloud.demo.workflow.domain.dto.WfFormDTO;
import org.cloud.demo.workflow.domain.vo.WfFormVo;
import org.cloud.demo.workflow.mapper.WfFormMapper;
import org.cloud.demo.workflow.service.WfFormService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WfFormServiceImpl implements WfFormService {
    private final WfFormMapper wfFormMapper;


    @Override
    @Transactional
    public int insert(WfFormDTO wfFormDTO) {
        WfForm wfForm = new WfForm();
        wfForm.setName(wfFormDTO.getName());
        wfForm.setContent(wfFormDTO.getContent());
        wfForm.setDesc(wfFormDTO.getDesc());
        return wfFormMapper.insert(wfForm);
    }

    @Override
    @Transactional
    public int deleteByIds(List<Long> list) {
        return wfFormMapper.deleteBatchIds(list);
    }

    @Override
    @Transactional
    public int update(WfFormDTO wfFormDTO) {
        WfForm wfForm = new WfForm();
        BeanUtil.copyProperties(wfFormDTO, wfForm, CopyOptions.create().ignoreNullValue());
        return wfFormMapper.updateById(wfForm);
    }

    @Override
    public TableDataInfo<WfFormVo> queryPageList(WfFormDTO wfFormDTO, PageQuery pageQuery) {
        LambdaQueryWrapper<WfForm> lqw = buildQueryWrapper(wfFormDTO);
        Page<WfFormVo> result = wfFormMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    private LambdaQueryWrapper<WfForm> buildQueryWrapper(WfFormDTO wfFormDTO) {
        LambdaQueryWrapper<WfForm> lqw = Wrappers.lambdaQuery();
        lqw.like(StrUtil.isNotBlank(wfFormDTO.getName()), WfForm::getName, wfFormDTO.getName());
        lqw.like(StrUtil.isNotBlank(wfFormDTO.getDesc()), WfForm::getDesc, wfFormDTO.getDesc());
        return lqw;
    }
}
