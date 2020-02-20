package com.jhl.admin.controller;

import com.jhl.admin.Interceptor.PreAuth;
import com.jhl.admin.model.Notice;
import com.jhl.admin.repository.NoticeRepository;
import com.ljh.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Slf4j
@Controller
public class NoticeController {
    @Autowired
    NoticeRepository noticeRepository;

    /**
     * 创建一个Notice
     *
     * @return
     */
    @PreAuth("admin")
    @ResponseBody
    @PostMapping("/notice")
    public Result createNotice(@RequestBody Notice notice) {
        addOrUpdate(notice);
        return Result.SUCCESS();
    }

    /**
     * 更新
     * @param notice
     * @return
     */
    @PreAuth("admin")
    @ResponseBody
    @PutMapping("/notice")
    public Result updateNotice(@RequestBody Notice notice) {
        addOrUpdate(notice);
        return Result.SUCCESS();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @PreAuth("admin")
    @ResponseBody
    @DeleteMapping("/notice/{id}")
    public Result delNotice(@PathVariable Integer id) {
        if (id ==null)   throw new NullPointerException("id不能为空");
          noticeRepository.deleteById(id);
        return Result.SUCCESS();
    }

    /**
     * 获取
     * @param id
     * @return
     */
    @ResponseBody
    @GetMapping("/notice/{id}")
    public Result getNotice(@PathVariable Integer id) {
        if (id ==null)   throw new NullPointerException("id不能为空");
        Notice notice = noticeRepository.findById(id).orElse(null);

        return Result.buildSuccess(notice,null);
    }

    /**
     * 获取前7可展示的公告
     * @return
     */
    @ResponseBody
    @GetMapping("/notice")
    public Result list() {
        List<Notice> notices = noticeRepository.findTop7ByStatusAndToDateAfterOrderByUpdateTimeDesc(1, new Date());
        Result success = Result.SUCCESS();success.setObj(notices);
        return success;
    }


    private void addOrUpdate(@RequestBody Notice notice) {
        if (notice == null) throw new NullPointerException("不能为空");
        if (StringUtils.isBlank(notice.getContent())
                || StringUtils.isBlank(notice.getName())
                || notice.getToDate() ==null
                || notice.getStatus() ==null
        )
            {
            log.warn("notice:{}", notice);
            throw new NullPointerException("不能为空");
        }
        noticeRepository.save(notice);
    }
}
