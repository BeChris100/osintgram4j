package osintgram4j.api.sh;

import osintgram4j.commons.ShellConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ShellCaller {

    private final String command, helpDesc;
    private Class<?> callableClass;
    private Method callableMethod, callableHelpMethod;
    private final String[] alternateCommands;

    private final boolean deprecated;

    private final Object classInstance;

    public ShellCaller(boolean deprecated, String command, String helpDesc, String callableClassName, String... alternateCommands) throws ShellException {
        this.deprecated = deprecated;
        this.command = command;
        this.helpDesc = helpDesc;
        this.alternateCommands = alternateCommands;

        this.callableClass = null;
        this.callableMethod = null;
        this.callableHelpMethod = null;

        try {
            this.callableClass = Class.forName(callableClassName);
            this.classInstance = callableClass.getDeclaredConstructor().newInstance();

            if (callableClass.getSuperclass() != Command.class)
                throw new ShellException("Launch class " + callableClassName + " is not extending \"Command.class\"");

            Method[] callableMethodArr = this.callableClass.getDeclaredMethods();
            if (callableMethodArr.length == 0)
                throw new ShellException("No methods are given for the method");

            Method execMethod = this.callableClass.getDeclaredMethod("launchCmd", String[].class, List.class);
            Method helpMethod = this.callableClass.getDeclaredMethod("helpCmd", String[].class);

            if (execMethod.getReturnType() != int.class)
                throw new IllegalArgumentException("Execution Method does not return an Integer Value");

            if (helpMethod.getReturnType() != String.class)
                throw new IllegalArgumentException("Help Method does not return a String value");

            this.callableMethod = execMethod;
            this.callableHelpMethod = helpMethod;
        } catch (ReflectiveOperationException ex) {
            throw new ShellException("An error occurred", ex);
        }
    }

    public boolean isDeprecated() {
        return deprecated;
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
            return (Integer) callableMethod.invoke(classInstance, args, configList);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ShellException(e);
        }
    }

    public String retrieveLongHelp(String[] args) throws ShellException {
        if (callableHelpMethod == null || callableClass == null)
            throw new NullPointerException("Help Method and/or Class not initialized properly");

        try {
            return (String) callableHelpMethod.invoke(classInstance, (Object) args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ShellException(e);
        }
    }

}
