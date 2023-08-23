package com.yupi.springbootinit.model.dto.search;

import com.yupi.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *统一搜索
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PromptQueryRequest extends PageRequest implements Serializable {
    /**
     * 搜索词
     */
    private String text;
    /**
     * 类型
     */
    private String type;
    /**
     * 页码
     */
    private int pageSize;
    /**
     * 页数
     */
    private int pageNum;

    private static final long serialVersionUID = 1L;
}