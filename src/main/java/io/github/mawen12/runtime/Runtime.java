package io.github.mawen12.runtime;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author <a href="1181963012mw@gmail.com">mawen12</a>
 * @since 2024/9/17
 */
public class Runtime {

    private static final ThreadLocal<Deque<LatchTask>> threadLocalTaskStack = ThreadLocal.withInitial(ArrayDeque::new);

    public static LatchTask currentTask() {
        Deque<LatchTask> taskStack = Runtime.threadLocalTaskStack.get();
        return taskStack.isEmpty() ? null : taskStack.peek();
    }

    public static void pushTask(final LatchTask task) {
        Runtime.threadLocalTaskStack.get().push(task);
    }

    public static void popTask() {
        Runtime.threadLocalTaskStack.get().pop();
    }
}
