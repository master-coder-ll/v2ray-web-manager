package com.jhl.admin.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServerConfigVO extends BaseEntityVO implements Serializable {
    private String key;

    private String name;

    private String value;

    private String scope;

}

