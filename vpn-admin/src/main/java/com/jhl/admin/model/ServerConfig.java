package com.jhl.admin.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServerConfig extends BaseEntity implements Serializable {
    @Column(unique = true,nullable = false)
    private String key;
    @Column(nullable = false)
    private String value;

}

