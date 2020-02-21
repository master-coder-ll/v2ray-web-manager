package com.ljh.common.model;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> implements Serializable {
    private Integer code = 200;
    private String message;
    private T obj;


    public static int CODE_SUCCESS = 200;

    public static Result SUCCESS() {
        return Result.builder().code(CODE_SUCCESS).build();
    }

    public static Result buildSuccess(Object object, String message) {
        Result result = Result.builder().code(CODE_SUCCESS).build();
        if (object != null) result.setObj(object);
        if (message != null) result.setMessage(message);

        return result;
    }


    public static Result buildPageObject(long total, List objects) {

        PageObject.PageObjectBuilder builder = PageObject.builder();
        if (objects != null) builder.content(objects);
        builder.total(total);
        return buildSuccess(builder.build(), null);

    }

    @Builder
    @Getter
    @Setter
    public static class PageObject<T> {
        private long total;
        private List<T> content;
    }
}
