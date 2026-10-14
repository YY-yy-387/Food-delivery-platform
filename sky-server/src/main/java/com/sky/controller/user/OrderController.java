package com.sky.controller.user;

import com.alibaba.fastjson.JSON;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.service.OrderAsyncService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
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
    private final OrderService orderService;
    private final OrderAsyncService orderAsyncService;
    private final WebSocketServer webSocketServer;

    /**
     * 用户下单
     */
    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        OrderSubmitVO orderSubmitVo = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVo);
    }

    /**
     * 用户支付（假支付）
     */
    @PutMapping("/payment")
    @ApiOperation("用户支付")
    public Result paySuccess(@RequestBody OrdersPaymentDTO ordersPaymentDTO){
        // 假支付：不调用微信支付接口，直接标记订单为已支付
        Orders orders = orderService.payment(ordersPaymentDTO);

        // 支付成功后，通过WebSocket向商家端推送来单提醒
        Map<String, Object> map = new HashMap<>();
        map.put("type",1); // 1表示来单提醒 2表示客户催单
        map.put("orderId", orders.getId());
        map.put("content","订单号：" + orders.getNumber());
        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);

        return Result.success();
    }

    /**
     * 历史订单查询
     */
    @GetMapping("/historyOrders")
    @ApiOperation("历史订单查询")
    public Result<PageResult> page(int page, int pageSize, Integer status){
        PageResult pageResult = orderService.pageQueryUser(page, pageSize, status);
        return Result.success(pageResult);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> details(@PathVariable("id") Long id){
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }

    /**
     * 用户取消订单
     */
    @PutMapping("/cancel/{id}")
    @ApiOperation("用户取消订单")
    public Result cancel(@PathVariable("id") Long id){
        orderService.userCancelById(id);
        return Result.success();
    }

    /**
     * 再来一单
     */
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable("id") Long id){
        orderService.repetition(id);
        return Result.success();
    }

    /**
     * 客户催单
     */
    @GetMapping("/reminder/{id}")
    @ApiOperation("客户催单")
    public Result reminder(@PathVariable("id") Long id){
        orderService.reminder(id);
        return Result.success();
    }

    @PostMapping("/refund/{id}")
    @ApiOperation("用户申请退款")
    public Result refund(@PathVariable Long id) {
        orderService.refund(id);
        return Result.success();
    }

    @PostMapping("/submitAsync")
    @ApiOperation("异步下单（返回任务ID，轮询结果）")
    public Result<java.util.Map<String, String>> submitAsync(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        String taskId = orderAsyncService.submit(ordersSubmitDTO, com.sky.context.BaseContext.getCurrentId());
        java.util.Map<String, String> result = new java.util.HashMap<>();
        result.put("taskId", taskId);
        return Result.success(result);
    }

    @GetMapping("/asyncResult/{taskId}")
    @ApiOperation("查询异步下单结果")
    public Result<OrderSubmitVO> asyncResult(@PathVariable String taskId) {
        OrderSubmitVO vo = orderAsyncService.getResult(taskId);
        if (vo == null) {
            return Result.error("订单处理中，请稍后查询");
        }
        return Result.success(vo);
    }
}
