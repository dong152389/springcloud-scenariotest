package org.cloud.demo.workflow.service;


import org.cloud.demo.common.web.domain.TableDataInfo;
import org.cloud.demo.common.web.page.PageQuery;
import org.cloud.demo.workflow.domain.dto.WfFormDTO;
import org.cloud.demo.workflow.domain.vo.WfFormVo;

import java.util.List;

public interface WfFormService {
    int insert(WfFormDTO wfFormDTO);

    int deleteByIds(List<Long> list);

    int update(WfFormDTO wfFormDTO);

    TableDataInfo<WfFormVo> queryPageList(WfFormDTO wfFormDTO, PageQuery pageQuery);
}
