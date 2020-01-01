package com.jhl.admin.controller;

import com.jhl.admin.Interceptor.PreAuth;
import com.jhl.admin.model.Order;
import com.jhl.admin.model.Package;
import com.jhl.admin.repository.OrderRepository;
import com.jhl.admin.repository.PackageRepository;
import com.ljh.common.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OrderController {
    @Autowired
    PackageRepository packageRepository;
    @Autowired
    OrderRepository orderRepository;


    /**
     * 创建一个order
     *
     * @param order
     * @return
     */
    @PreAuth("admin")
    @ResponseBody
    @PostMapping("/order")
    public Result createOrder(@RequestBody Order order) {
        if (order == null || order.getPackageId() == null || order.getAccountId() !=null) throw new NullPointerException("  不能为空");
        Package aPackage = packageRepository.getOne(order.getPackageId());
        order.setPrice(aPackage.getPrice());
        //正常范围套餐
        if (aPackage.getType() == 1) {
            //todo
        }

        return Result.SUCCESS();
    }


}
