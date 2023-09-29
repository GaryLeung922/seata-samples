package io.seata.samples.dubbo;

import java.io.Serializable;

/**
 *
 * Order订单模型for TCC模式
 * @Author: Gary
 * @Date: 2023/9/24 09:53
 * @Version: v1.0.0
 * @Description: TODO
 **/
public class OrderTcc implements Serializable {

    /**
     * The Id.
     */
    public String id;
    /**
     * The User id.
     */
    public String userId;
    /**
     * The Commodity code.
     */
    public String commodityCode;
    /**
     * The Count.
     */
    public int count;
    /**
     * The Money.
     */
    public int money;

    /**
     * 订单状态
     * 0表示正常状态
     * 1表示冻结状态
     */
    public int status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommodityCode() {
        return commodityCode;
    }

    public void setCommodityCode(String commodityCode) {
        this.commodityCode = commodityCode;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" + "id=" + id + ", userId='" + userId + '\'' + ", commodityCode='" + commodityCode + '\''
                + ", count=" + count + ", money=" + money + ",status=" +status + '}';
    }
}
