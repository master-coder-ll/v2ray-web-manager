package com.jhl.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.jhl.admin.VO.VOI;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "hibernateLazyInitializer", "handler", "fieldHandler"})
public class BaseEntity implements Serializable,ModelI {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    @Column(updatable = false)
    @CreationTimestamp
    private Date createTime;
    @UpdateTimestamp
    private Date updateTime;

    public <T extends VOI> T toVO(Class<T> tClass) {
        T o=null;
        try {
           o  = tClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(this, o);

        } catch (Exception e) {
            log.error("toVO "+this.getClass(), e);
        }
        return o;
    }

    public static  <T extends VOI> List<T> toVOList(List<? extends BaseEntity> srcList, Class<T > tClass) {
        List<T> resultList= Lists.newArrayListWithCapacity(srcList.size());
        try {

            srcList.forEach(o1 -> {
                resultList.add( o1.toVO(tClass));
            });

        } catch (Exception e) {
            log.error("toVOList ", e);
        }
        return resultList;
    }
}
