package org.cloud.demo.workflow.service;


import org.cloud.demo.common.web.domain.TableDataInfo;
import org.cloud.demo.common.web.page.PageQuery;
import org.cloud.demo.workflow.domain.dto.WfCategoryDTO;
import org.cloud.demo.workflow.domain.vo.WfCategoryVo;

public interface WfCategoryService {

    /**
     * 新增分类
     * @param category  分类对象
     * @return
     */
    int insert(WfCategoryDTO category);

    /**
     * 分页查询
     * @param category          分类对象
     * @param pageQuery         分页对象
     * @return {@link TableDataInfo}
     */
    TableDataInfo<WfCategoryVo> queryPageList(WfCategoryDTO category, PageQuery pageQuery);
    /**
     * 更新分类
     * @param category  分类对象
     * @return
     */
    int update(WfCategoryDTO category);

    /**
     * 根据ID集合删除
     * @param categoryIds ID集合
     * @return
     */
    int deleteByIds(Long[] categoryIds);
}
