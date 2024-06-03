package net.bc100dev.commons.process;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProcessData {

    public static final long RUNNING_PID = ProcessHandle.current().pid();

    public static ProcessInfo getCurrentProcessInfo() {
        ProcessHandle handle = ProcessHandle.current();
        // ... .... . / -- .- -.. . / -- . / - .... .. ... / .-- .- -.--

        return new ProcessInfo(handle.pid(),
                handle.info().user().orElse("-"),
                new File(handle.info().command().orElse("-")),
                handle.info().commandLine().orElse("-"),
                handle.info().arguments().orElse(new String[0]));
    }

    public static ProcessInfo getProcessInfo(long pid) throws ProcessExploreException {
        Optional<ProcessHandle> process = ProcessHandle.of(pid);
        if (process.isEmpty())
            throw new ProcessExploreException(String.format("Tried to find process id %d, but there was no such pid currently running, or could not be received", pid));

        ProcessHandle handle = process.get();

        return new ProcessInfo(handle.pid(),
                handle.info().user().orElse("-"),
                new File(handle.info().command().orElse("-")),
                handle.info().commandLine().orElse("-"),
                handle.info().arguments().orElse(new String[0]));
    }

    public static List<ProcessInfo> listProcesses() {
        List<ProcessInfo> processes = new ArrayList<>();

        ProcessHandle.allProcesses().forEach(process -> {
            ProcessInfo info = new ProcessInfo(
                    process.pid(),
                    process.info().user().orElse("-"),
                    new File(process.info().command().orElse("-")),
                    process.info().commandLine().orElse("-"),
                    process.info().arguments().orElse(new String[0])
            );
            processes.add(info);
        });

        return processes;
    }

}
