package cc.ayakurayuki.repo.singleflight;

import java.util.StringJoiner;

/**
 * R is a wrapper of the result.
 *
 * @author Ayakura Yuki
 */
public final class R<T> {

    private final T val;
    private final boolean shared;

    public R(T val, boolean shared) {
        this.val = val;
        this.shared = shared;
    }

    public T getVal() {
        return val;
    }

    public boolean isShared() {
        return shared;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", R.class.getSimpleName() + "[", "]")
                .add("val=" + val)
                .add("shared=" + shared)
                .toString();
    }

}
