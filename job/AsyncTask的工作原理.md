AsyncTask是Android本身提供的一种轻量级的异步任务类。它可以在线程池中执行后台任务，
然后把执行的进度和最终的结果传递给主线程更新UI。实际上，AsyncTask内部是封装了Thread和Handler。
虽然AsyncTask很方便的执行后台任务，以及在主线程上更新UI，但是，AsyncTask并不合适进行特别耗时的后台操作，
对于特别耗时的任务，个人还是建议使用线程池。

AsyncTask提供有4个核心方法：

1、onPreExecute():该方法在主线程中执行，在执行异步任务之前会被调用，一般用于一些准备工作。

2、doInBackground(String... params):这个方法是在线程池中执行，此方法用于执行异步任务。在这个方法中可以通过publishProgress方法来更新任务的进度，publishProgress方法会调用onProgressUpdate方法，另外，任务的结果返回给onPostExecute方法。

3、onProgressUpdate(Object... values):该方法在主线程中执行，主要用于任务进度更新的时候，该方法会被调用。

4、onPostExecute(Long aLong)：在主线程中执行，在异步任务执行完毕之后，该方法会被调用，该方法的参数及为后台的返回结果。

除了这几个方法之外还有一些不太常用的方法，如onCancelled(),在异步任务取消的情况下，该方法会被调用。



源码可以知道从上面的execute方法内部调用的是executeOnExecutor()方法，
即executeOnExecutor(sDefaultExecutor, params);
而sDefaultExecutor实际上是一个串行的线程池。

而onPreExecute()方法在这里就会被调用了。接着看这个线程池。AsyncTask的执行是排队执行的，
因为有关键字synchronized，而AsyncTask的Params参数就封装成为FutureTask类，
FutureTask这个类是一个并发类，在这里它充当了Runnable的作用。
接着FutureTask会交给SerialExecutor的execute方法去处理，而SerialExecutor的executor方法首先就会将
FutureTask添加到mTasks队列中，如果这个时候没有任务，就会调用scheduleNext()方法，执行下一个任务。
如果有任务的话，则执行完毕后最后在调用 scheduleNext();执行下一个任务。直到所有任务被执行完毕。
而AsyncTask的构造方法中有一个call()方法，而这个方法由于会被FutureTask的run方法执行。
所以最终这个call方法会在线程池中执行。而doInBackground这个方法就是在这里被调用的。
我们好好研究一下这个call()方法。mTaskInvoked.set(true);表示当前任务已经执行过了。
接着执行doInBackground方法，最后将结果通过postResult(result);方法进行传递。
postResult()方法中通过sHandler来发送消息，sHandler的中通过消息的类型来判断一个MESSAGE_POST_RESULT，
这种情况就是调用onPostExecute(result)方法或者是onCancelled(result)。
另一种消息类型是MESSAGE_POST_PROGRESS则调用更新进度onProgressUpdate。


