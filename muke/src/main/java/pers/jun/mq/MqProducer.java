/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: MqProducer
 * Author:   俊哥
 * Date:     2020/1/2 17:46
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.mq;

import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pers.jun.dao.StockLogMapper;
import pers.jun.error.BusinessException;
import pers.jun.pojo.StockLog;
import pers.jun.service.ItemService;
import pers.jun.service.OrderService;
import pers.jun.service.model.OrderItemModel;
import pers.jun.service.model.OrderModel;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2020/1/2
 * @since 1.0.0
 */
@Component
public class MqProducer {

    @Value("${mq.nameserver.addr}")
    private String nameAddr;

    @Value("${mq.topicname}")
    private String topicName;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StockLogMapper stockLogMapper;

    @Autowired
    private ItemService itemService;

    private DefaultMQProducer producer;

    private TransactionMQProducer transactionMQProducer;

    @PostConstruct
    public void init() throws MQClientException {
        // producer的初始化
        producer = new DefaultMQProducer("producer_group");
        producer.setNamesrvAddr(nameAddr);
        producer.start();

        // 事务型producer
        transactionMQProducer = new TransactionMQProducer("transaction_producer_group");
        transactionMQProducer.setNamesrvAddr(nameAddr);
        transactionMQProducer.start();

        //设置transactionListener
        transactionMQProducer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object arg) {
                //真正要做的事
                //这里的arg就是sendMessageInTransaction传过来的参数
                Map<String,Object> argMap = (Map)arg;
                OrderModel orderModel = (OrderModel) argMap.get("orderModel");
                String stockLogId = (String) argMap.get("stockLogId");

                try {
                    //调用orderservice执行下单逻辑
                    orderService.createOrderPromo(orderModel,stockLogId);
                } catch (BusinessException e) {
                    e.printStackTrace();
                    //如果操作失败，将库存流水状态更新为3，回滚缓存中的数量
                    itemService.increaseStock(orderModel.getOrderItems().get(0).getItemId(),orderModel.getOrderItems().get(0).getAmount());

                    StockLog stockLog = stockLogMapper.selectByPrimaryKey(stockLogId);
                    stockLog.setStatus(3);
                    stockLogMapper.updateByPrimaryKey(stockLog);
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                return LocalTransactionState.COMMIT_MESSAGE;
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                //根据是否扣减库存成功，来判断返回COMMIT,ROLLBACK,还是继续返回UNKNOWN
                String jsonString = new String(msg.getBody());
                Map<String,Object> map = JSON.parseObject(jsonString, Map.class);
                Integer itemId = (Integer) map.get("itemId");
                Integer amount = (Integer) map.get("amount");
                String stockLogId = (String) map.get("stockLogId");

                StockLog stockLog = stockLogMapper.selectByPrimaryKey(stockLogId);
                //判空或者状态为1，即操作还未完成
                if(stockLog == null || stockLog.getStatus() == 1)
                    //return LocalTransactionState.UNKNOW;
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                //如果状态为2，即下单完成
                if(stockLog.getStatus() == 2)
                    return LocalTransactionState.COMMIT_MESSAGE;
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        });
    }

    //事务型同步扣减库存消息
    public boolean transactionAsyncReduceStock(OrderModel orderModel,String stockLogId) {
        // 获取参数值
        OrderItemModel orderItemModel = orderModel.getOrderItems().get(0);
        Integer itemId = orderItemModel.getItemId();
        Integer amount = orderItemModel.getAmount();

        // 将参数值发送出去
        Map<String,Object> map = new HashMap<>();
        map.put("itemId",itemId);
        map.put("amount",amount);
        map.put("stockLogId",stockLogId);
        Message message = new Message(topicName,"increase", JSON.toJSON(map).toString().getBytes(Charset.forName("UTF-8")));

        Map<String,Object> argsMap = new HashMap<>();
        argsMap.put("stockLogId",stockLogId);
        argsMap.put("orderModel",orderModel);

        // 向中间件发送消息的返回值
        TransactionSendResult sendResult = null;
        try {
            sendResult = transactionMQProducer.sendMessageInTransaction(message, argsMap);
        } catch (MQClientException e) {
            e.printStackTrace();
            return false;
        }
        // 只有当发送消息发返回值为COMMIT_MESSAGE的时候，才返回true
        if (sendResult.getLocalTransactionState() == LocalTransactionState.COMMIT_MESSAGE) {
            return true;
        }
        return false;
    }

    //同步减库存消息
    public boolean asyncReduceStock(Integer itemId, Integer amount) {
        Map<String,Object> map = new HashMap<>();
        map.put("itemId",itemId);
        map.put("amount",amount);
        Message message = new Message(topicName,"increase", JSON.toJSON(map).toString().getBytes(Charset.forName("UTF-8")));
        try {
            producer.send(message);
        } catch (MQClientException e) {
            e.printStackTrace();
            return false;
        } catch (RemotingException e) {
            e.printStackTrace();
            return false;
        } catch (MQBrokerException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }
}