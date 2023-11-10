package observer;

import java.util.ArrayList;
import java.util.List;

public class MyPublisher implements IPublisher {
    List<ISubscriber> subscribers;

    @Override
    public void addSubscriber(ISubscriber sub) {
        if (sub == null)
            return;
        if (this.subscribers == null)
            this.subscribers = new ArrayList<>();
        if (this.subscribers.contains(sub))
            return;
        this.subscribers.add(sub);
    }

    @Override
    public void removeSubscriber(ISubscriber sub) {
        if (sub == null || this.subscribers == null || !this.subscribers.contains(sub))
            return;
        this.subscribers.remove(sub);
    }

    @Override
    public void notifySubscribers(Object notification) {
        if (this.subscribers == null || this.subscribers.isEmpty())
            return;
        for (ISubscriber listener : subscribers) {
            listener.update(notification);
        }
    }
}
