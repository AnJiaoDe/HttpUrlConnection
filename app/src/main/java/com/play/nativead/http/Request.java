package com.play.nativead.http;

/**
 * Created by Administrator on 2018/12/21 0021.
 */

public class Request {

    private final String url;
    private final String method;

    Request(Builder builder) {
        this.url = builder.getUrl();
        this.method = builder.getMethod();
    }

    public static class Builder {
        private String url;
        private String method;

        public Builder() {
            this.method = "GET";

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

        public Request build() {
            return new Request(this);
        }

        public String getUrl() {
            return url;
        }

        public String getMethod() {
            return method;
        }
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }
}
