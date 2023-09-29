package io.seata.samples.dubbo.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.samples.dubbo.OrderTcc;
import io.seata.samples.dubbo.service.OrderService;
import io.seata.samples.dubbo.service.OrderTccAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * OrderTccAction 实现类
 *
 * @Author: Gary
 * @Date: 2023/9/24 10:10
 * @Version: v1.0.0
 * @Description: TODO
 **/
public class OrderTccActionImpl implements OrderTccAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public boolean createOrder(BusinessActionContext businessActionContext, OrderTcc orderTcc) {
        orderTcc.setStatus(1);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        LOGGER.info(
                "Order Service SQL: insert into order_tcc order:{}",
                orderTcc);

        jdbcTemplate.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement pst = con.prepareStatement(
                        "insert into order_tcc (id, user_id, commodity_code, count, money, status) values (?, ?, ?, ?, ?, ?)",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                pst.setObject(1, orderTcc.id);
                pst.setObject(2, orderTcc.userId);
                pst.setObject(3, orderTcc.commodityCode);
                pst.setObject(4, orderTcc.count);
                pst.setObject(5, orderTcc.money);
                pst.setObject(6, orderTcc.status);
                return pst;
            }
        }, keyHolder);

        LOGGER.info("Order Service 一阶段try成功 ... Created " + orderTcc);

        return true;
    }

    @Override
    @Transactional
    public boolean commit(BusinessActionContext businessActionContext) {
        JSONObject orderSrc = (JSONObject)businessActionContext.getActionContext("orderTcc");
        OrderTcc order=new OrderTcc();
        order.setStatus(0);
        order.setId(orderSrc.getString("id"));
        order.setCount(orderSrc.getInteger("count"));
        order.setMoney(orderSrc.getInteger("money"));
        order.setUserId(orderSrc.getString("userId"));
        order.setCommodityCode(orderSrc.getString("commodityCode"));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement pst = con.prepareStatement(
                        "update order_tcc set status = ? where id = ?",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                pst.setObject(1, order.status);
                pst.setObject(2, order.id);
                return pst;
            }
        }, keyHolder);

        LOGGER.info("Order Service 二阶段commit成功 ... " + order);

        return true;
    }

    @Override
    @Transactional
    public boolean rollback(BusinessActionContext businessActionContext) {
        JSONObject orderSrc = (JSONObject)businessActionContext.getActionContext("orderTcc");
        OrderTcc order=new OrderTcc();
        order.setId(orderSrc.getString("id"));
        order.setCount(orderSrc.getInteger("count"));
        order.setMoney(orderSrc.getInteger("money"));
        order.setUserId(orderSrc.getString("userId"));
        order.setCommodityCode(orderSrc.getString("commodityCode"));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement pst = con.prepareStatement(
                    "delete from order_tcc where id = ?",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            pst.setObject(1, order.getId());
            return pst;
        }, keyHolder);

        LOGGER.info("Order Service 二阶段rollback成功 ... " + order);

        return true;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
