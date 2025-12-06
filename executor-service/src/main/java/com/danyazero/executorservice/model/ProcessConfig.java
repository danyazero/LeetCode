package com.danyazero.executorservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class ProcessConfig {
    @Builder.Default
    private ExecutionMode executionMode = ExecutionMode.STANDALONE_ONCE;
    @Builder.Default
    private int asLimit = 4096;
    @Builder.Default
    private int fileSize = 1;
    @Builder.Default
    private int cpuLimit = 600;
    @Builder.Default
    private boolean disableProc = true;
    @Builder.Default
    private boolean iFaceNoLo = true;
    @Builder.Default
    private boolean verbose = false;
    private Path workingDirectory;
    private List<String> mounts_rw;
    private List<String> mounts_ro;
    private List<String> env;

    public List<String> getConfig() {
        var config = new ArrayList<String>();

        for (String mount : mounts_rw) {
            config.add("--bindmount");
            config.add(mount);
        }

        for (String mount : mounts_ro) {
            config.add("--bindmount_ro");
            config.add(mount);
        }

        if (!env.isEmpty()) {
            config.add("--env");
            config.add(String.join(" ", env));
        }

//        config.add("--time_limit");
//        config.add(String.valueOf(timeLimit));

        config.add("--rlimit_as");
        config.add(String.valueOf(asLimit));

        config.add("--rlimit_cpu");
        config.add(String.valueOf(cpuLimit));

        config.add("--rlimit_fsize");
        config.add(String.valueOf(fileSize));

        if (disableProc) config.add("--disable_proc");
        if (iFaceNoLo) config.add("--iface_no_lo");
        if (verbose) config.add("--verbose");

        return config;
    }
}
