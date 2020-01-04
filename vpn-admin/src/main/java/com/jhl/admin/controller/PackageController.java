package com.jhl.admin.controller;

import com.jhl.admin.model.Package;
import com.jhl.admin.repository.PackageRepository;
import com.ljh.common.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class PackageController {
    @Autowired
    PackageRepository packageRepository;

    @ResponseBody
    @GetMapping("/package/{id}")
    public Result get(@PathVariable Integer id) {
        if (id == null) throw new NullPointerException("id不能为空");
        Package aPackage = packageRepository.findById(id).get();
        return Result.builder().code(Result.CODE_SUCCESS).obj(aPackage).build();
    }

    @ResponseBody
    @GetMapping("/package")
    public Result findByPage(Integer page, Integer pageSize) {
        if (page == null || pageSize == null) throw new NullPointerException("page, pageSize不能为空");
        Page<Package> packages = packageRepository.findAll(Example.of(Package.builder().status(1).build()), PageRequest.of(page, pageSize));
        return Result.builder().code(Result.CODE_SUCCESS).obj(packages.get().toArray()).build();
    }

    @ResponseBody
    @PostMapping("/package")
    public Result addServer(@RequestBody Package aPackage) {
        if (aPackage == null) throw new NullPointerException("Package不能为空");
            packageRepository.save(aPackage);
        return Result.SUCCESS();
    }

    @ResponseBody
    @PutMapping("/package")
    public Result updateServer(@RequestBody Package aPackage) {
        if (aPackage == null) throw new NullPointerException("Package不能为空");
        if (aPackage.getId() ==null) throw  new NullPointerException("id 不能为空");
        packageRepository.save(aPackage);
        return Result.SUCCESS();
    }

    @ResponseBody
    @DeleteMapping("/package/{id}")
    public Result deletePackage(@PathVariable  Integer id) {
        if (id !=null) throw new NullPointerException("id不能为空");
        Package aPackage = packageRepository.findById(id).get();
        aPackage.setStatus(0);
        packageRepository.save(aPackage);
        return Result.SUCCESS();
    }


}
