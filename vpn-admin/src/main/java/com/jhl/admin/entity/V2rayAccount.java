package com.jhl.admin.entity;

import lombok.*;

@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class V2rayAccount {

    /**
     * {
     * "v": "2",
     * "ps": "",
     * "add": "",
     * "port": "443",
     * "id": "976fdcba-fb53-44a1-d7e2-74e4c3de0020",
     * "aid": "0",
     * "net": "ws",
     * "type": "none",
     * "host": "",
     * "path": "/ws/50001/",
     * "tls": "tls"
     * }
     */

    private String v = "2";
    private String ps = "";
    //地址
    private String add;
    private String port;
    private String id;
    private String aid = "0";
    private String net = "ws";
    private String type = "none";
     private String host ="";
    private String path;
    private String tls = "tls";


}
