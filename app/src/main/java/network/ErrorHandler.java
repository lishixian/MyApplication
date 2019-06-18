package network;

import java.io.IOException;

import retrofit2.HttpException;

public class ErrorHandler {

    private static final String TAG = "ErrorHandler";

    public static void handleNetworkError(Throwable e) {
        if (e instanceof HttpException) {
            //ToastUtils.showShort(R.string.server_error);
        } else if (e instanceof IOException) {
            //ToastUtils.showShort(R.string.connect_error);
        } else {
            //ToastUtils.showShort(R.string.unknown_error);
        }
        e.printStackTrace();

    }
}
