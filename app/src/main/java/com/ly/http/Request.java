package com.ly.http;

/**
 * Created by Administrator on 2018/12/21 0021.
 */

public class Request {

    private final Object tag;
    private final String url;
    private final String method;

    Request(Builder builder) {
        this.tag=builder.getTag();
        this.url = builder.getUrl();
        this.method = builder.getMethod();
    }

    public static class Builder {
        private Object tag;
        private String url;
        private String method;

        public Builder() {
            this.method = "GET";
            this.tag="tag";

        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder get() {
            return method("GET");
        }
        public Builder tag(Object tag) {
            if(tag!=null)this.tag=tag;
            return this;
        }

        public Request build() {
            return new Request(this);
        }

        public Object getTag() {
            return tag;
        }

        public String getUrl() {
            return url;
        }

        public String getMethod() {
            return method;
        }
    }

    public Object getTag() {
        return tag;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }
}
