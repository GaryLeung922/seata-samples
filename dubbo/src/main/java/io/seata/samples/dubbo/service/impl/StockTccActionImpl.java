package io.seata.samples.dubbo.service.impl;

import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.samples.dubbo.IdempotentUtil;
import io.seata.samples.dubbo.service.StockService;
import io.seata.samples.dubbo.service.StockTccAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @Author: Gary
 * @Date: 2023/9/24 16:29
 * @Version: v1.0.0
 * @Description: TODO
 **/
public class StockTccActionImpl implements StockTccAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockService.class);

    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public boolean reduceStock(BusinessActionContext businessActionContext, String commodityCode, Integer quantity) {
        if (Objects.nonNull(IdempotentUtil.getMarker(getClass(), businessActionContext.getXid()))) {
            LOGGER.warn("已经执行过try阶段");
            return true;
        }
        LOGGER.info("Stock Service TCC Begin ... xid: " + RootContext.getXID());
        LOGGER.info("Deducting inventory SQL: update stock_tcc set count = count - {}, frozen = frozen + {} where commodity_code = {}", quantity,
                quantity, commodityCode);

        jdbcTemplate.update("update stock_tcc set count = count - ?, frozen = frozen + ? where commodity_code = ?",
                new Object[]{quantity, quantity, commodityCode});
        LOGGER.info("Stock Service TCC End ... ");

        // 增加防重标记
        IdempotentUtil.addMarker(getClass(), businessActionContext.getXid(), "marker");
        return true;
    }

    @Override
    @Transactional
    public boolean commit(BusinessActionContext businessActionContext) {
        LOGGER.info("Stock Service TCC commit Start ... ");
        if (Objects.isNull(IdempotentUtil.getMarker(getClass(), businessActionContext.getXid()))) {
            LOGGER.warn("已经执行过commit阶段");
            return true;
        }

        String commodityCode = businessActionContext.getActionContext("commodityCode").toString();
        int quantity = Integer.parseInt(businessActionContext.getActionContext("quantity").toString());
        try{
            jdbcTemplate.update("update stock_tcc set frozen = frozen - ?, solded = solded + ? where commodity_code = ?",
                    new Object[]{quantity, quantity, commodityCode});
        }catch (Exception e){
            LOGGER.error("Stock Service TCC commit error", e);
            return false;
        }finally {
            // 移除标记
            IdempotentUtil.removeMarker(getClass(), businessActionContext.getXid());
        }

        LOGGER.info("Stock Service TCC commit End ... ");
        return true;
    }

    @Override
    @Transactional
    public boolean rollback(BusinessActionContext businessActionContext) {
        LOGGER.info("Stock Service TCC rollback Start ... ");

        if (Objects.isNull(IdempotentUtil.getMarker(getClass(), businessActionContext.getXid()))) {
            LOGGER.warn("已经执行过rollback阶段");
            return true;
        }

        String commodityCode = businessActionContext.getActionContext("commodityCode").toString();
        int quantity = Integer.parseInt(businessActionContext.getActionContext("quantity").toString());

        jdbcTemplate.update("update stock_tcc set frozen = frozen - ?, count = count + ? where commodity_code = ?",
                new Object[]{quantity, quantity, commodityCode});
        LOGGER.info("Stock Service TCC rollback End ... ");

        // 移除标记
        IdempotentUtil.removeMarker(getClass(), businessActionContext.getXid());

        return true;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
