package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.osintgram4j.pcl.PCLConfig;

import java.util.List;

public class Connect {

    // Invoked manually by `Method.invoke`
    public static int launchCmd(String[] args, List<PCLConfig> pclConfigs) {
        return 0;
    }

    // Invoked manually by `Method.invoke`
    public static String helpCmd() {
        return """
                Requires either given &username and &password variables, otherwise
                the connection requires the given parameters of 'connect [username] [password] (mfa)'.
                                
                If an account has MFA (Multi-Factor Authentication) enabled, the MFA code needs to be passed.
                Make sure that the MFA code has at least a duration of 10 seconds.""";
    }
}
