package com.jhl.admin.service.v2ray;

public interface ProxyEvent {
    String ADD_EVENT = "ADD";
    String RM_EVENT = "RM";
    String UPDATE_EVENT = "UPDATE";

    String getEvent();



    void updateEvent();

    void rmEvent();

    void createEvent();
}
