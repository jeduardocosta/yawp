package io.yawp.repository.pipes.basic;

import io.yawp.repository.models.basic.PipedObject;
import io.yawp.repository.models.basic.PipedObjectCounter;
import io.yawp.repository.pipes.Pipe;

public class CounterPipe extends Pipe<PipedObject, PipedObjectCounter> {

    @Override
    public void configure(PipedObject object) {
        addSink(id(PipedObjectCounter.class, 1L));
    }

    @Override
    public void clear(PipedObjectCounter sink) {
        sink.setCount(0);
        sink.setCountGroupA(0);
        sink.setCountGroupB(0);
    }

    @Override
    public void flux(PipedObject object, PipedObjectCounter counter) {
        counter.inc();

        if (isGroup(object, "group-a")) {
            counter.incGroupA();
        }

        if (isGroup(object, "group-b")) {
            counter.incGroupB();
        }
    }

    @Override
    public void reflux(PipedObject object, PipedObjectCounter counter) {
        counter.dec();

        if (isGroup(object, "group-a")) {
            counter.decGroupA();
        }

        if (isGroup(object, "group-b")) {
            counter.decGroupB();
        }
    }

    private boolean isGroup(PipedObject object, String groupName) {
        String text = object.getGroup();
        if (text == null) {
            return false;
        }
        return text.equals(groupName);
    }

}