package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * @author lzzzzz
 * @create 2020-01-05 13:04
 */
@Data
public class BaseAttrVo extends ProductAttrValueEntity {

    private List<String> valueSelected;

    public void setValueSelected(List<String> valueSelected){
        if (!CollectionUtils.isEmpty(valueSelected)){
            this.setAttrValue(StringUtils.join(valueSelected,","));
        }else {
            this.setAttrValue(null);
        }
    }
}
