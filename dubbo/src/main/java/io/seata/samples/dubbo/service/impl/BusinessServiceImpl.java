/*
 *  Copyright 1999-2021 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.seata.samples.dubbo.service.impl;

import java.util.Random;
import java.util.UUID;

import io.seata.core.context.RootContext;
import io.seata.samples.dubbo.OrderTcc;
import io.seata.samples.dubbo.service.*;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Please add the follow VM arguments:
 * <pre>
 *     -Djava.net.preferIPv4Stack=true
 * </pre>
 */
public class BusinessServiceImpl implements BusinessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessService.class);

    private StockService stockService;
    private OrderService orderService;

    private OrderTccAction orderTccAction;

    private StockTccAction stockTccAction;
    private Random random = new Random();

    @Override
    @GlobalTransactional(timeoutMills = 300000, name = "dubbo-demo-tx")
    public void purchase(String userId, String commodityCode, int orderCount) {
        LOGGER.info("purchase begin ... xid: " + RootContext.getXID());
        stockService.deduct(commodityCode, orderCount);
        // just test batch update
        //stockService.batchDeduct(commodityCode, orderCount);
        orderService.create(userId, commodityCode, orderCount);
        if (random.nextBoolean()) {
//            throw new RuntimeException("random exception mock!");
        }

    }

    @Override
    @GlobalTransactional(timeoutMills = 300000, name = "dubbo-demo-tx-tcc")
    public void purchaseByTcc(String userId, String commodityCode, int orderCount) {
        OrderTcc orderTcc = new OrderTcc();
        LOGGER.info("purchase tcc begin ... xid: " + RootContext.getXID());
        String id = UUID.randomUUID().toString();
        orderTcc.setId(id);
        orderTcc.setUserId(userId);
        orderTcc.setCommodityCode(commodityCode);
        orderTcc.setCount(orderCount);
        orderTcc.setMoney(200 * orderCount);
        boolean result = orderTccAction.createOrder(null, orderTcc);


        if (random.nextBoolean()) {
            throw new RuntimeException("random exception mock!");
        }
        LOGGER.info("purchase tcc succ ... xid: " + RootContext.getXID());
    }

    @Override
    @GlobalTransactional(timeoutMills = 300000, name = "dubbo-demo-tx-tcc-2action")
    public void purchaseByTcc2Action(String userId, String commodityCode, int orderCount) {
        OrderTcc orderTcc = new OrderTcc();
        LOGGER.info("purchase tcc begin ... xid: " + RootContext.getXID());
        String id = UUID.randomUUID().toString();
        orderTcc.setId(id);
        orderTcc.setUserId(userId);
        orderTcc.setCommodityCode(commodityCode);
        orderTcc.setCount(orderCount);
        orderTcc.setMoney(200 * orderCount);
        boolean orderRes = orderTccAction.createOrder(null, orderTcc);
        boolean stockRes = stockTccAction.reduceStock(null, commodityCode, orderCount);
        if (!orderRes || !stockRes) {
            throw new RuntimeException("tcc exception mock!");
        }
        if (true){
            throw new RuntimeException("random exception mock!");
        }
        LOGGER.info("purchase tcc succ ... xid: " + RootContext.getXID());
    }

    /**
     * Sets stock service.
     *
     * @param stockService the stock service
     */
    public void setStockService(StockService stockService) {
        this.stockService = stockService;
    }

    /**
     * Sets order service.
     *
     * @param orderService the order service
     */
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void setOrderTccAction(OrderTccAction orderTccAction) {
        this.orderTccAction = orderTccAction;
    }

    public void setStockTccAction(StockTccAction stockTccAction) {
        this.stockTccAction = stockTccAction;
    }

    public static void main(String[] args) {
        String string = UUID.randomUUID().toString();
        System.out.println(string);
    }
}
