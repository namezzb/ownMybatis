package cn.zzb.mybatis.test.po;

import com.alibaba.fastjson.annotation.JSONType;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Sku 视图对象，使用驼峰命名，用于测试 resultMap 列名映射
 */
@JSONType(orders = {"id", "source", "goodsId", "goodsName", "originalPrice", "createTime", "updateTime"})
public class SkuVO {

    private Long id;
    private String source;
    private String goodsId;
    private String goodsName;
    private BigDecimal originalPrice;
    private Date createTime;
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getGoodsId() { return goodsId; }
    public void setGoodsId(String goodsId) { this.goodsId = goodsId; }

    public String getGoodsName() { return goodsName; }
    public void setGoodsName(String goodsName) { this.goodsName = goodsName; }

    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

}