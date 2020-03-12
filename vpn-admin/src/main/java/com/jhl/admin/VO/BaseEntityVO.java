package com.jhl.admin.VO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jhl.admin.model.ModelI;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true,value = { "hibernateLazyInitializer", "hibernateLazyInitializer", "handler", "fieldHandler"})
public class BaseEntityVO implements Serializable,VOI {
    private Integer id;
    private Date createTime;
    private Date updateTime;

    public <T extends ModelI> T toModel(Class<T> tClass) {
        T instance = null;
        try {

            instance=tClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(this, instance);

        }catch (Exception e){
            log.error("to mode:"+this.getClass().getName(),e);
        }

        return instance;
    }
}