package com.cy.sdkstrategy_master.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by cy on 2018/12/24.
 */

public class IOUtils {
    private IOUtils() {
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
