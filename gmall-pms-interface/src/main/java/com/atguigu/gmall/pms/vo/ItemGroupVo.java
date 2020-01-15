package com.atguigu.gmall.pms.vo;

import lombok.Data;

import java.util.List;

/**
 * @author lzzzzz
 * @create 2020-01-14 18:19
 */
@Data
public class ItemGroupVo {
    private Long groupId;
    private String groupName;
    private List<ItemBaseAttrVo> attrs;
}
