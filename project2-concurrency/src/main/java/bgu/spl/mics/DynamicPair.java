package bgu.spl.mics;

public class DynamicPair<T, S> {

    private T first;
    private S second;

    public DynamicPair(T first, S second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() { return this.first; }
    public S getSecond() { return this.second; }
    public void setFirst(T first) { this.first = first; }
    public void setSecond(S second) { this.second = second; }

}
