package com.sky.controller.user;

import com.alibaba.fastjson.JSON;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.websocket.WebSocketServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Api(tags="用户端订单相关接口")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderservice;
    private final WebSocketServer webSocketServer;

    @PostMapping("/submit")
    @ApiOperation("用户订单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        OrderSubmitVO orderSubmitVo=orderservice.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVo);

    }
    @PutMapping("/payment")
    @ApiOperation("用户支付")
    public Result paySuccess(@RequestBody Orders orders){
        Map map = new HashMap();
        map.put("type",1); // 1表示来单提醒 2表示客户催单
        map.put("orderId",1);
        map.put("content","订单号：" + 12334556);
        System.out.println("支付了");
        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);

        return Result.success();
    }
}
