package net.bc100dev.commons.process;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class ProcessInvoke {

    private final DataInputStream input, error;
    private final DataOutputStream output;
    private final long pid;
    private final int exit;

    protected ProcessInvoke(DataInputStream input, DataInputStream error, DataOutputStream output, long pid, int exit) {
        this.input = input;
        this.error = error;
        this.output = output;
        this.pid = pid;
        this.exit = exit;
    }

    public DataInputStream getInput() {
        return input;
    }

    public DataInputStream getError() {
        return error;
    }

    public DataOutputStream getOutput() {
        return output;
    }

    public long getProcessID() {
        return pid;
    }

    public int getExitCode() {
        return exit;
    }

    public static ProcessInvoke invokeProcess(File executable, boolean waitTillExit, String[] args) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(executable.getAbsolutePath());
        pb.command(args);

        Process pc = pb.start();
        int exit = 0;

        if (waitTillExit) {
            try {
                exit = pc.waitFor();
            } catch (InterruptedException e) {
                throw new IOException(e);
            }
        }

        return new ProcessInvoke(
                new DataInputStream(pc.getInputStream()),
                new DataInputStream(pc.getErrorStream()),
                new DataOutputStream(pc.getOutputStream()),
                pc.pid(),
                exit
        );
    }

}
