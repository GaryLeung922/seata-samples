package io.seata.samples.dubbo.service;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

/**
 * @Author: Gary
 * @Date: 2023/9/24 16:20
 * @Version: v1.0.0
 * @Description: TODO
 **/
@LocalTCC
public interface StockTccAction {

    @TwoPhaseBusinessAction(name = "stockAction", commitMethod = "commit", rollbackMethod = "rollback")
    boolean reduceStock(BusinessActionContext businessActionContext,
                        @BusinessActionContextParameter(paramName = "commodityCode") String commodityCode,
                        @BusinessActionContextParameter(paramName = "quantity") Integer quantity);


    boolean commit(BusinessActionContext businessActionContext);

    boolean rollback(BusinessActionContext businessActionContext);
}
