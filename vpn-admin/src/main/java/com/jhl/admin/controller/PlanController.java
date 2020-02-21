package com.jhl.admin.controller;

import com.jhl.admin.cache.UserCache;
import com.jhl.admin.model.Package;
import com.jhl.admin.model.User;
import com.jhl.admin.repository.PackageRepository;
import com.ljh.common.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static com.jhl.admin.constant.KVConstant.*;

@Controller
public class PlanController {
    @Autowired
    PackageRepository packageRepository;
    @Autowired
    UserCache userCache;
    @ResponseBody
    @GetMapping("/plan/{id}")
    public Result get(@PathVariable Integer id) {
        if (id == null) throw new NullPointerException("id不能为空");
        Package aPackage = packageRepository.findById(id).orElse(null);
        return Result.builder().code(Result.CODE_SUCCESS).obj(aPackage).build();
    }

    @ResponseBody
    @GetMapping("/plan")
    public Result findByPage(@CookieValue(value = COOKIE_NAME, defaultValue = "") String auth,Integer page, Integer pageSize) {
        if (page == null || pageSize == null) throw new NullPointerException("page, pageSize不能为空");

        User cache = userCache.getCache(auth);
        String role = cache.getRole();
        Page<Package> pages =null;
        if (role.equals(ROLE_ADMIN)){
            pages = packageRepository.findAll(PageRequest.of(page - 1, pageSize));

        }else {
            //寻找正常的，可以显示的
            pages = packageRepository.findAll(Example.of(Package.builder().status(V_TRUE).show(V_TRUE).build()),
                    PageRequest.of(page-1, pageSize));
        }
        if (pages ==null) return Result.SUCCESS();
        return  Result.buildPageObject(pages.getTotalElements(),pages.getContent());
    }

    @ResponseBody
    @PostMapping("/plan")
    public Result add(@RequestBody Package aPackage) {
        if (aPackage == null) throw new NullPointerException("Package不能为空");
            packageRepository.save(aPackage);
        return Result.SUCCESS();
    }

    @ResponseBody
    @PutMapping("/plan")
    public Result put(@RequestBody Package aPackage) {
        if (aPackage == null) throw new NullPointerException("Package不能为空");
        if (aPackage.getId() ==null) throw  new NullPointerException("id 不能为空");
        packageRepository.save(aPackage);
        return Result.SUCCESS();
    }

    @ResponseBody
    @DeleteMapping("/plan/{id}")
    public Result delete(@PathVariable  Integer id) {
        if (id !=null) throw new NullPointerException("id不能为空");
        Package aPackage = packageRepository.findById(id).get();
        aPackage.setStatus(0);
        packageRepository.save(aPackage);
        return Result.SUCCESS();
    }


}
