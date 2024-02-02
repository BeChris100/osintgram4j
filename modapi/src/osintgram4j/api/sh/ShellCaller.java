package osintgram4j.api.sh;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ShellCaller {

    private final String command, helpDesc;
    private Class callableClass;
    private Method callableMethod, callableHelpMethod;
    private final String[] alternateCommands;

    public ShellCaller(String command, String helpDesc, String callableClassName, String... alternateCommands)
            throws ShellException {
        this.command = command;
        this.helpDesc = helpDesc;
        this.alternateCommands = alternateCommands;

        this.callableClass = null;
        this.callableMethod = null;

        try {
            this.callableClass = Class.forName(callableClassName);

            Method[] callableMethodArr = this.callableClass.getDeclaredMethods();
            if (callableMethodArr.length == 0)
                throw new ShellException("No methods are given for the method");

            Method execMethod = this.callableClass.getDeclaredMethod("launchCmd", String[].class, List.class);
            Method helpMethod = this.callableClass.getDeclaredMethod("helpCmd", String[].class);

            if (execMethod.getReturnType() != int.class)
                throw new IllegalArgumentException("Execution Method does not return an Integer Value");

            this.callableMethod = execMethod;
            this.callableHelpMethod = helpMethod;
        } catch (ClassNotFoundException ignore) {
            throw new ShellException("No such class found at " + callableClassName);
        } catch (NoSuchMethodException ignore) {
            throw new ShellException("Class at " + callableClassName + " does not have the necessary methods");
        }
    }

    public String[] getAlternateCommands() {
        return alternateCommands;
    }

    public String getCommand() {
        return command;
    }

    public String retrieveShortHelp() {
        return helpDesc;
    }

    public int execute(String[] args, List<ShellConfig> configList) throws ShellException {
        if (callableMethod == null || callableClass == null)
            throw new NullPointerException("Method and/or Class not initialized");

        try {
            return (Integer) callableMethod.invoke(null, args, configList);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ShellException(e);
        }
    }

    public String retrieveLongHelp() throws ShellException {
        if (callableHelpMethod == null || callableClass == null)
            throw new NullPointerException("Help Method and/or Class not initialized properly");

        try {
            return (String) callableHelpMethod.invoke(String.class);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ShellException(e);
        }
    }

}
