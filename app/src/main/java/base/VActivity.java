package base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;


import butterknife.Unbinder;

public abstract class VActivity<P extends IPresent> extends AppCompatActivity
        implements IView<P> {

    private Unbinder unbinder;

    private P p;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getLayoutId() > 0) {
            setContentView(getLayoutId());
            bindUI(null);
        }

        initData(savedInstanceState);
    }

    @CallSuper
    @Override
    public void bindUI(View rootView) {
        unbinder = KnifeKit.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getOptionsMenuId() > 0) {
            getMenuInflater().inflate(getOptionsMenuId(), menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    protected P getP() {
        if (p == null) {
            p = newP();
            if (p != null) {
                p.attachV(this);
            }
        }
        return p;
    }

    @Override
    public int getOptionsMenuId() {
        return 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (getP() != null) {
            getP().detachV();
        }

        KnifeKit.unbind(unbinder);
        p = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
