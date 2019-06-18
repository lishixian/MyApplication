package network;

import data.Result;

import io.reactivex.functions.Consumer;

@Deprecated
public abstract class ErrorHandledConsumer<T> implements Consumer<Result<T>> {

    @Override
    public void accept(Result<T> result) throws Exception {
        if (result.isOk()) {
            onSuccess(result.getData());
        } else {

         /*   Throwable throwable = new ApiException(result.getErrno(), result.getErrmsg());
            if (!onFailure(throwable)) {
                ErrorHandler.handleNetworkError(throwable);
            }*/
        }
    }

    public abstract void onSuccess(T t);

    public boolean onFailure(Throwable throwable) {
        return false;
    }
}
