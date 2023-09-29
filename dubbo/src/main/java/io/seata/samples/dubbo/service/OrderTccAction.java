package io.seata.samples.dubbo.service;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;
import io.seata.samples.dubbo.OrderTcc;

/**
 * Order订单服务for TCC模式
 *
 * @Author: Gary
 * @Date: 2023/9/24 09:51
 * @Version: v1.0.0
 * @Description: TODO
 **/
@LocalTCC
public interface OrderTccAction {

    @TwoPhaseBusinessAction(name = "orderAction")
    boolean createOrder(BusinessActionContext businessActionContext, @BusinessActionContextParameter(paramName = "orderTcc") OrderTcc orderTcc);

    boolean commit(BusinessActionContext businessActionContext);

    boolean rollback(BusinessActionContext businessActionContext);
}
