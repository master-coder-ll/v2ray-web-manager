package com.jhl.admin.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServerConfig extends BaseEntity implements Serializable {
    @Column(unique = true,nullable = false)
    private String key;

    private String name;

    @Column(nullable = false)
    private String value;

    private String scope;

}

