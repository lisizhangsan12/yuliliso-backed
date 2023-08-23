package com.yupi.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

@Data
public class Picture implements Serializable {
    private String title;
    private String url;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
