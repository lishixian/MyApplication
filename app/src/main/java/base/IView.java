package base;


import android.os.Bundle;
import android.view.View;

public interface IView<P> {

    void bindUI(View rootView);

    int getLayoutId();

    void initData(Bundle savedInstanceState);

    int getOptionsMenuId();

    P newP();
}
