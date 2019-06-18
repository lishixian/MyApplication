package base;

public interface IPresent<V> {

    void attachV(V view);

    void detachV();
}
