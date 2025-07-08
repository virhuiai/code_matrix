/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.logging.log4j.core.tools.picocli;

import static java.util.Locale.ENGLISH;
import static org.apache.logging.log4j.core.tools.picocli.CommandLine.Help.Column.Overflow.SPAN;
import static org.apache.logging.log4j.core.tools.picocli.CommandLine.Help.Column.Overflow.TRUNCATE;
import static org.apache.logging.log4j.core.tools.picocli.CommandLine.Help.Column.Overflow.WRAP;

import java.io.File;
import java.io.PrintStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.text.BreakIterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import org.apache.logging.log4j.core.tools.picocli.CommandLine.Help;
import org.apache.logging.log4j.core.tools.picocli.CommandLine.Help.Ansi.IStyle;
import org.apache.logging.log4j.core.tools.picocli.CommandLine.Help.Ansi.Style;
import org.apache.logging.log4j.core.tools.picocli.CommandLine.Help.Ansi.Text;
import org.apache.logging.log4j.core.util.Integers;

/**
 * <p>
 * CommandLine interpreter that uses reflection to initialize an annotated domain object with values obtained from the
 * command line arguments.
 * </p><h2>Example</h2>
 * <pre>import static picocli.CommandLine.*;
 *
 * &#064;Command(header = "Encrypt FILE(s), or standard input, to standard output or to the output file.",
 *          version = "v1.2.3")
 * public class Encrypt {
 *
 *     &#064;Parameters(type = File.class, description = "Any number of input files")
 *     private List&lt;File&gt; files = new ArrayList&lt;File&gt;();
 *
 *     &#064;Option(names = { "-o", "--out" }, description = "Output file (default: print to console)")
 *     private File outputFile;
 *
 *     &#064;Option(names = { "-v", "--verbose"}, description = "Verbosely list files processed")
 *     private boolean verbose;
 *
 *     &#064;Option(names = { "-h", "--help", "-?", "-help"}, usageHelp = true, description = "Display this help and exit")
 *     private boolean help;
 *
 *     &#064;Option(names = { "-V", "--version"}, versionHelp = true, description = "Display version info and exit")
 *     private boolean versionHelp;
 * }
 * </pre>
 * <p>
 * Use {@code CommandLine} to initialize a domain object as follows:
 * </p><pre>
 * public static void main(String... args) {
 *     Encrypt encrypt = new Encrypt();
 *     try {
 *         List&lt;CommandLine&gt; parsedCommands = new CommandLine(encrypt).parse(args);
 *         if (!CommandLine.printHelpIfRequested(parsedCommands, System.err, Help.Ansi.AUTO)) {
 *             runProgram(encrypt);
 *         }
 *     } catch (ParameterException ex) { // command line arguments could not be parsed
 *         System.err.println(ex.getMessage());
 *         ex.getCommandLine().usage(System.err);
 *     }
 * }
 * </pre><p>
 * Invoke the above program with some command line arguments. The below are all equivalent:
 * </p>
 * <pre>
 * --verbose --out=outfile in1 in2
 * --verbose --out outfile in1 in2
 * -v --out=outfile in1 in2
 * -v -o outfile in1 in2
 * -v -o=outfile in1 in2
 * -vo outfile in1 in2
 * -vo=outfile in1 in2
 * -v -ooutfile in1 in2
 * -vooutfile in1 in2
 * </pre>
 */
/**
 * <p>
 * CommandLine 解释器，使用反射机制，通过命令行参数初始化一个带注解的领域对象。
 * </p>
 * <h2>示例</h2>
 * <pre>import static picocli.CommandLine.*;
 *
 * &#064;Command(header = "Encrypt FILE(s), or standard input, to standard output or to the output file.",
 * version = "v1.2.3")
 * public class Encrypt {
 *
 * &#064;Parameters(type = File.class, description = "Any number of input files")
 * private List&lt;File&gt; files = new ArrayList&lt;File&gt;();
 *
 * &#064;Option(names = { "-o", "--out" }, description = "Output file (default: print to console)")
 * private File outputFile;
 *
 * &#064;Option(names = { "-v", "--verbose"}, description = "Verbosely list files processed")
 * private boolean verbose;
 *
 * &#064;Option(names = { "-h", "--help", "-?"}, usageHelp = true, description = "Display this help and exit")
 * private boolean help;
 *
 * &#064;Option(names = { "-V", "--version"}, versionHelp = true, description = "Display version info and exit")
 * private boolean versionHelp;
 * }
 * </pre>
 * <p>
 * 使用 {@code CommandLine} 初始化领域对象如下:
 * </p>
 * <pre>
 * public static void main(String... args) {
 * Encrypt encrypt = new Encrypt();
 * try {
 * List&lt;CommandLine&gt; parsedCommands = new CommandLine(encrypt).parse(args);
 * if (!CommandLine.printHelpIfRequested(parsedCommands, System.err, Help.Ansi.AUTO)) {
 * runProgram(encrypt);
 * }
 * } catch (ParameterException ex) { // 命令行参数无法解析
 * System.err.println(ex.getMessage());
 * ex.getCommandLine().usage(System.err);
 * }
 * }
 * </pre>
 * <p>
 * 使用一些命令行参数调用上面的程序。以下所有参数都等效:
 * </p>
 * <pre>
 * --verbose --out=outfile in1 in2
 * --verbose --out outfile in1 in2
 * -v --out=outfile in1 in2
 * -v -o outfile in1 in2
 * -v -o=outfile in1 in2
 * -vo outfile in1 in2
 * -vo=outfile in1 in2
 * -v -ooutfile in1 in2
 * -vooutfile in1 in2
 * </pre>
 */
public class CommandLine {
    /** This is picocli version {@value}. */
    /** picocli 版本号 {@value}。 */
    public static final String VERSION = "2.0.3";

    private final Tracer tracer = new Tracer();
    private final Interpreter interpreter;
    private String commandName = Help.DEFAULT_COMMAND_NAME;
    private boolean overwrittenOptionsAllowed = false;
    private boolean unmatchedArgumentsAllowed = false;
    private final List<String> unmatchedArguments = new ArrayList<>();
    private CommandLine parent;
    private boolean usageHelpRequested;
    private boolean versionHelpRequested;
    private final List<String> versionLines = new ArrayList<>();

    /**
     * Constructs a new {@code CommandLine} interpreter with the specified annotated object.
     * When the {@link #parse(String...)} method is called, fields of the specified object that are annotated
     * with {@code @Option} or {@code @Parameters} will be initialized based on command line arguments.
     * @param command the object to initialize from the command line arguments
     * @throws InitializationException if the specified command object does not have a {@link Command}, {@link Option} or {@link Parameters} annotation
     */
    /**
     * 构造一个新的 {@code CommandLine} 解释器，使用指定的带注解对象。
     * 当调用 {@link #parse(String...)} 方法时，指定对象的被 {@code @Option} 或 {@code @Parameters} 注解的字段将根据命令行参数进行初始化。
     * @param command 用于从命令行参数初始化的对象
     * @throws InitializationException 如果指定的命令对象没有 {@link Command}、{@link Option} 或 {@link Parameters} 注解
     */
    public CommandLine(final Object command) {
        interpreter = new Interpreter(command);
    }

    /** Registers a subcommand with the specified name. For example:
     * <pre>
     * CommandLine commandLine = new CommandLine(new Git())
     *         .addSubcommand("status",   new GitStatus())
     *         .addSubcommand("commit",   new GitCommit();
     *         .addSubcommand("add",      new GitAdd())
     *         .addSubcommand("branch",   new GitBranch())
     *         .addSubcommand("checkout", new GitCheckout())
     *         //...
     *         ;
     * </pre>
     *
     * <p>The specified object can be an annotated object or a
     * {@code CommandLine} instance with its own nested subcommands. For example:</p>
     * <pre>
     * CommandLine commandLine = new CommandLine(new MainCommand())
     *         .addSubcommand("cmd1",                 new ChildCommand1()) // subcommand
     *         .addSubcommand("cmd2",                 new ChildCommand2())
     *         .addSubcommand("cmd3", new CommandLine(new ChildCommand3()) // subcommand with nested sub-subcommands
     *                 .addSubcommand("cmd3sub1",                 new GrandChild3Command1())
     *                 .addSubcommand("cmd3sub2",                 new GrandChild3Command2())
     *                 .addSubcommand("cmd3sub3", new CommandLine(new GrandChild3Command3()) // deeper nesting
     *                         .addSubcommand("cmd3sub3sub1", new GreatGrandChild3Command3_1())
     *                         .addSubcommand("cmd3sub3sub2", new GreatGrandChild3Command3_2())
     *                 )
     *         );
     * </pre>
     * <p>The default type converters are available on all subcommands and nested sub-subcommands, but custom type
     * converters are registered only with the subcommand hierarchy as it existed when the custom type was registered.
     * To ensure a custom type converter is available to all subcommands, register the type converter last, after
     * adding subcommands.</p>
     * <p>See also the {@link Command#subcommands()} annotation to register subcommands declaratively.</p>
     *
     * @param name the string to recognize on the command line as a subcommand
     * @param command the object to initialize with command line arguments following the subcommand name.
     *          This may be a {@code CommandLine} instance with its own (nested) subcommands
     * @return this CommandLine object, to allow method chaining
     * @see #registerConverter(Class, ITypeConverter)
     * @since 0.9.7
     * @see Command#subcommands()
     */
    /**
     * 注册具有指定名称的子命令。例如：
     * <pre>
     * CommandLine commandLine = new CommandLine(new Git())
     * .addSubcommand("status",    new GitStatus())
     * .addSubcommand("commit",    new GitCommit();
     * .addSubcommand("add",       new GitAdd())
     * .addSubcommand("branch",    new GitBranch())
     * .addSubcommand("checkout", new GitCheckout())
     * //...
     * ;
     * </pre>
     *
     * <p>指定的对象可以是带注解的对象，也可以是具有自己嵌套子命令的 {@code CommandLine} 实例。例如:</p>
     * <pre>
     * CommandLine commandLine = new CommandLine(new MainCommand())
     * .addSubcommand("cmd1",              new ChildCommand1()) // 子命令
     * .addSubcommand("cmd2",              new ChildCommand2())
     * .addSubcommand("cmd3", new CommandLine(new ChildCommand3()) // 带有嵌套子子命令的子命令
     * .addSubcommand("cmd3sub1",              new GrandChild3Command1())
     * .addSubcommand("cmd3sub2",              new GrandChild3Command2())
     * .addSubcommand("cmd3sub3", new CommandLine(new GrandChild3Command3()) // 更深层的嵌套
     * .addSubcommand("cmd3sub3sub1", new GreatGrandChild3Command3_1())
     * .addSubcommand("cmd3sub3sub2", new GreatGrandChild3Command3_2())
     * )
     * );
     * </pre>
     * <p>默认类型转换器在所有子命令和嵌套子子命令上都可用，但自定义类型转换器仅在注册自定义类型时存在的子命令层次结构中注册。
     * 为确保自定义类型转换器可用于所有子命令，请在添加子命令后最后注册类型转换器。</p>
     * <p>另请参阅 {@link Command#subcommands()} 注解以声明性地注册子命令。</p>
     *
     * @param name 在命令行上识别为子命令的字符串
     * @param command 用于使用子命令名称后的命令行参数进行初始化的对象。
     * 这可能是一个带有自己的（嵌套）子命令的 {@code CommandLine} 实例
     * @return 此 CommandLine 对象，以允许方法链式调用
     * @see #registerConverter(Class, ITypeConverter)
     * @since 0.9.7
     * @see Command#subcommands()
     */
    public CommandLine addSubcommand(final String name, final Object command) {
        final CommandLine commandLine = toCommandLine(command);
        commandLine.parent = this;
        interpreter.commands.put(name, commandLine);
        return this;
    }
    /** Returns a map with the subcommands {@linkplain #addSubcommand(String, Object) registered} on this instance.
     * @return a map with the registered subcommands
     * @since 0.9.7
     */
    /**
     * 返回一个包含在此实例上 {@linkplain #addSubcommand(String, Object) 注册} 的子命令的映射。
     * @return 包含已注册子命令的映射
     * @since 0.9.7
     */
    public Map<String, CommandLine> getSubcommands() {
        return new LinkedHashMap<>(interpreter.commands);
    }
    /**
     * Returns the command that this is a subcommand of, or {@code null} if this is a top-level command.
     * @return the command that this is a subcommand of, or {@code null} if this is a top-level command
     * @see #addSubcommand(String, Object)
     * @see Command#subcommands()
     * @since 0.9.8
     */
    /**
     * 返回此命令所属的父命令，如果这是顶级命令，则返回 {@code null}。
     * @return 此命令所属的父命令，如果这是顶级命令，则返回 {@code null}
     * @see #addSubcommand(String, Object)
     * @see Command#subcommands()
     * @since 0.9.8
     */
    public CommandLine getParent() {
        return parent;
    }

    /** Returns the annotated object that this {@code CommandLine} instance was constructed with.
     * @param <T> the type of the variable that the return value is being assigned to
     * @return the annotated object that this {@code CommandLine} instance was constructed with
     * @since 0.9.7
     */
    /**
     * 返回此 {@code CommandLine} 实例构建时使用的带注解对象。
     * @param <T> 返回值被赋值的变量类型
     * @return 此 {@code CommandLine} 实例构建时使用的带注解对象
     * @since 0.9.7
     */
    public <T> T getCommand() {
        return (T) interpreter.command;
    }

    /** Returns {@code true} if an option annotated with {@link Option#usageHelp()} was specified on the command line.
     * @return whether the parser encountered an option annotated with {@link Option#usageHelp()}.
     * @since 0.9.8 */
    /**
     * 如果命令行上指定了带有 {@link Option#usageHelp()} 注解的选项，则返回 {@code true}。
     * @return 解析器是否遇到带有 {@link Option#usageHelp()} 注解的选项。
     * @since 0.9.8
     */
    public boolean isUsageHelpRequested() { return usageHelpRequested; }

    /** Returns {@code true} if an option annotated with {@link Option#versionHelp()} was specified on the command line.
     * @return whether the parser encountered an option annotated with {@link Option#versionHelp()}.
     * @since 0.9.8 */
    /**
     * 如果命令行上指定了带有 {@link Option#versionHelp()} 注解的选项，则返回 {@code true}。
     * @return 解析器是否遇到带有 {@link Option#versionHelp()} 注解的选项。
     * @since 0.9.8
     */
    public boolean isVersionHelpRequested() { return versionHelpRequested; }

    /** Returns whether options for single-value fields can be specified multiple times on the command line.
     * The default is {@code false} and a {@link OverwrittenOptionException} is thrown if this happens.
     * When {@code true}, the last specified value is retained.
     * @return {@code true} if options for single-value fields can be specified multiple times on the command line, {@code false} otherwise
     * @since 0.9.7
     */
    /**
     * 返回是否允许在命令行上多次指定单值字段的选项。
     * 默认值为 {@code false}，如果发生这种情况会抛出 {@link OverwrittenOptionException}。
     * 当为 {@code true} 时，保留最后指定的值。
     * @return 如果可以在命令行上多次指定单值字段的选项，则为 {@code true}，否则为 {@code false}
     * @since 0.9.7
     */
    public boolean isOverwrittenOptionsAllowed() {
        return overwrittenOptionsAllowed;
    }

    /** Sets whether options for single-value fields can be specified multiple times on the command line without a {@link OverwrittenOptionException} being thrown.
     * <p>The specified setting will be registered with this {@code CommandLine} and the full hierarchy of its
     * subcommands and nested sub-subcommands <em>at the moment this method is called</em>. Subcommands added
     * later will have the default setting. To ensure a setting is applied to all
     * subcommands, call the setter last, after adding subcommands.</p>
     * @param newValue the new setting
     * @return this {@code CommandLine} object, to allow method chaining
     * @since 0.9.7
     */
    /**
     * 设置是否允许在命令行上多次指定单值字段的选项，而不会抛出 {@link OverwrittenOptionException}。
     * <p>此设置将注册到此 {@code CommandLine} 及其子命令和嵌套子子命令的完整层次结构中，<em>在本方法调用时</em>。
     * 稍后添加的子命令将具有默认设置。为确保设置应用于所有子命令，请在添加子命令后最后调用此 setter 方法。</p>
     * @param newValue 新的设置
     * @return 此 {@code CommandLine} 对象，以允许方法链式调用
     * @since 0.9.7
     */
    public CommandLine setOverwrittenOptionsAllowed(final boolean newValue) {
        this.overwrittenOptionsAllowed = newValue;
        for (final CommandLine command : interpreter.commands.values()) {
            command.setOverwrittenOptionsAllowed(newValue);
        }
        return this;
    }

    /** Returns whether the end user may specify arguments on the command line that are not matched to any option or parameter fields.
     * The default is {@code false} and a {@link UnmatchedArgumentException} is thrown if this happens.
     * When {@code true}, the last unmatched arguments are available via the {@link #getUnmatchedArguments()} method.
     * @return {@code true} if the end use may specify unmatched arguments on the command line, {@code false} otherwise
     * @see #getUnmatchedArguments()
     * @since 0.9.7
     */
    /**
     * 返回最终用户是否可以在命令行上指定与任何选项或参数字段不匹配的参数。
     * 默认值为 {@code false}，如果发生这种情况会抛出 {@link UnmatchedArgumentException}。
     * 当为 {@code true} 时，最后的未匹配参数可通过 {@link #getUnmatchedArguments()} 方法获取。
     * @return 如果最终用户可以在命令行上指定未匹配的参数，则为 {@code true}，否则为 {@code false}
     * @see #getUnmatchedArguments()
     * @since 0.9.7
     */
    public boolean isUnmatchedArgumentsAllowed() {
        return unmatchedArgumentsAllowed;
    }

    /** Sets whether the end user may specify unmatched arguments on the command line without a {@link UnmatchedArgumentException} being thrown.
     * <p>The specified setting will be registered with this {@code CommandLine} and the full hierarchy of its
     * subcommands and nested sub-subcommands <em>at the moment this method is called</em>. Subcommands added
     * later will have the default setting. To ensure a setting is applied to all
     * subcommands, call the setter last, after adding subcommands.</p>
     * @param newValue the new setting. When {@code true}, the last unmatched arguments are available via the {@link #getUnmatchedArguments()} method.
     * @return this {@code CommandLine} object, to allow method chaining
     * @since 0.9.7
     * @see #getUnmatchedArguments()
     */
    /**
     * 设置是否允许最终用户在命令行上指定未匹配的参数，而不会抛出 {@link UnmatchedArgumentException}。
     * <p>此设置将注册到此 {@code CommandLine} 及其子命令和嵌套子子命令的完整层次结构中，<em>在本方法调用时</em>。
     * 稍后添加的子命令将具有默认设置。为确保设置应用于所有子命令，请在添加子命令后最后调用此 setter 方法。</p>
     * @param newValue 新的设置。当为 {@code true} 时，最后的未匹配参数可通过 {@link #getUnmatchedArguments()} 方法获取。
     * @return 此 {@code CommandLine} 对象，以允许方法链式调用
     * @since 0.9.7
     * @see #getUnmatchedArguments()
     */
    public CommandLine setUnmatchedArgumentsAllowed(final boolean newValue) {
        this.unmatchedArgumentsAllowed = newValue;
        for (final CommandLine command : interpreter.commands.values()) {
            command.setUnmatchedArgumentsAllowed(newValue);
        }
        return this;
    }

    /** Returns the list of unmatched command line arguments, if any.
     * @return the list of unmatched command line arguments or an empty list
     * @see #isUnmatchedArgumentsAllowed()
     * @since 0.9.7
     */
    /**
     * 返回未匹配的命令行参数列表（如果有）。
     * @return 未匹配的命令行参数列表或空列表
     * @see #isUnmatchedArgumentsAllowed()
     * @since 0.9.7
     */
    public List<String> getUnmatchedArguments() {
        return unmatchedArguments;
    }

    /**
     * <p>
     * Convenience method that initializes the specified annotated object from the specified command line arguments.
     * </p><p>
     * This is equivalent to
     * </p><pre>
     * CommandLine cli = new CommandLine(command);
     * cli.parse(args);
     * return command;
     * </pre>
     *
     * @param command the object to initialize. This object contains fields annotated with
     *          {@code @Option} or {@code @Parameters}.
     * @param args the command line arguments to parse
     * @param <T> the type of the annotated object
     * @return the specified annotated object
     * @throws InitializationException if the specified command object does not have a {@link Command}, {@link Option} or {@link Parameters} annotation
     * @throws ParameterException if the specified command line arguments are invalid
     * @since 0.9.7
     */
    /**
     * <p>
     * 方便方法，根据指定的命令行参数初始化指定的带注解对象。
     * </p><p>
     * 这等效于：
     * </p><pre>
     * CommandLine cli = new CommandLine(command);
     * cli.parse(args);
     * return command;
     * </pre>
     *
     * @param command 要初始化的对象。此对象包含带有 {@code @Option} 或 {@code @Parameters} 注解的字段。
     * @param args 要解析的命令行参数
     * @param <T> 带注解对象的类型
     * @return 指定的带注解对象
     * @throws InitializationException 如果指定的命令对象没有 {@link Command}、{@link Option} 或 {@link Parameters} 注解
     * @throws ParameterException 如果指定的命令行参数无效
     * @since 0.9.7
     */
    public static <T> T populateCommand(final T command, final String... args) {
        final CommandLine cli = toCommandLine(command);
        cli.parse(args);
        return command;
    }

    /** Parses the specified command line arguments and returns a list of {@code CommandLine} objects representing the
     * top-level command and any subcommands (if any) that were recognized and initialized during the parsing process.
     * <p>
     * If parsing succeeds, the first element in the returned list is always {@code this CommandLine} object. The
     * returned list may contain more elements if subcommands were {@linkplain #addSubcommand(String, Object) registered}
     * and these subcommands were initialized by matching command line arguments. If parsing fails, a
     * {@link ParameterException} is thrown.
     * </p>
     *
     * @param args the command line arguments to parse
     * @return a list with the top-level command and any subcommands initialized by this method
     * @throws ParameterException if the specified command line arguments are invalid; use
     *      {@link ParameterException#getCommandLine()} to get the command or subcommand whose user input was invalid
     */
    /**
     * 解析指定的命令行参数，并返回一个 {@code CommandLine} 对象列表，表示在解析过程中识别和初始化的顶级命令和任何子命令（如果有）。
     * <p>
     * 如果解析成功，返回列表中的第一个元素始终是 {@code this CommandLine} 对象。如果子命令已 {@linkplain #addSubcommand(String, Object) 注册}，
     * 并且这些子命令是通过匹配命令行参数初始化的，则返回列表可能包含更多元素。如果解析失败，则抛出 {@link ParameterException}。
     * </p>
     *
     * @param args 要解析的命令行参数
     * @return 包含由本方法初始化的顶级命令和任何子命令的列表
     * @throws ParameterException 如果指定的命令行参数无效；使用 {@link ParameterException#getCommandLine()} 获取用户输入无效的命令或子命令
     */
    public List<CommandLine> parse(final String... args) {
        return interpreter.parse(args);
    }
    /**
     * Represents a function that can process a List of {@code CommandLine} objects resulting from successfully
     * {@linkplain #parse(String...) parsing} the command line arguments. This is a
     * <a href="https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html">functional interface</a>
     * whose functional method is {@link #handleParseResult(List, PrintStream, CommandLine.Help.Ansi)}.
     * <p>
     * Implementations of this functions can be passed to the {@link #parseWithHandlers(IParseResultHandler, PrintStream, Help.Ansi, IExceptionHandler, String...) CommandLine::parseWithHandler}
     * methods to take some next step after the command line was successfully parsed.
     * </p>
     * @see RunFirst
     * @see RunLast
     * @see RunAll
     * @since 2.0 */
    /**
     * 表示一个函数，它可以处理成功 {@linkplain #parse(String...) 解析} 命令行参数后生成的 {@code CommandLine} 对象列表。这是一个
     * <a href="https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html">函数式接口</a>，
     * 其函数方法是 {@link #handleParseResult(List, PrintStream, CommandLine.Help.Ansi)}。
     * <p>
     * 此函数的实现可以传递给 {@link #parseWithHandlers(IParseResultHandler, PrintStream, Help.Ansi, IExceptionHandler, String...) CommandLine::parseWithHandler}
     * 方法，以便在成功解析命令行后执行下一步操作。
     * </p>
     * @see RunFirst
     * @see RunLast
     * @see RunAll
     * @since 2.0
     */
    public static interface IParseResultHandler {
        /** Processes a List of {@code CommandLine} objects resulting from successfully
         * {@linkplain #parse(String...) parsing} the command line arguments and optionally returns a list of results.
         * @param parsedCommands the {@code CommandLine} objects that resulted from successfully parsing the command line arguments
         * @param out the {@code PrintStream} to print help to if requested
         * @param ansi for printing help messages using ANSI styles and colors
         * @return a list of results, or an empty list if there are no results
         * @throws ExecutionException if a problem occurred while processing the parse results; use
         *      {@link ExecutionException#getCommandLine()} to get the command or subcommand where processing failed
         */
        /**
         * 处理成功 {@linkplain #parse(String...) 解析} 命令行参数后生成的 {@code CommandLine} 对象列表，并可选地返回结果列表。
         * @param parsedCommands 成功解析命令行参数后生成的 {@code CommandLine} 对象
         * @param out 如果需要，用于打印帮助信息的 {@code PrintStream}
         * @param ansi 用于使用 ANSI 样式和颜色打印帮助信息
         * @return 结果列表，如果没有结果则为空列表
         * @throws ExecutionException 如果在处理解析结果时发生问题；使用 {@link ExecutionException#getCommandLine()} 获取处理失败的命令或子命令
         */
        List<Object> handleParseResult(List<CommandLine> parsedCommands, PrintStream out, Help.Ansi ansi) throws ExecutionException;
    }
    /**
     * Represents a function that can handle a {@code ParameterException} that occurred while
     * {@linkplain #parse(String...) parsing} the command line arguments. This is a
     * <a href="https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html">functional interface</a>
     * whose functional method is {@link #handleException(CommandLine.ParameterException, PrintStream, CommandLine.Help.Ansi, String...)}.
     * <p>
     * Implementations of this functions can be passed to the {@link #parseWithHandlers(IParseResultHandler, PrintStream, Help.Ansi, IExceptionHandler, String...) CommandLine::parseWithHandler}
     * methods to handle situations when the command line could not be parsed.
     * </p>
     * @see DefaultExceptionHandler
     * @since 2.0 */
    /**
     * 表示一个函数，它可以处理在 {@linkplain #parse(String...) 解析} 命令行参数时发生的 {@code ParameterException}。这是一个
     * <a href="https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html">函数式接口</a>，
     * 其函数方法是 {@link #handleException(CommandLine.ParameterException, PrintStream, CommandLine.Help.Ansi, String...)}。
     * <p>
     * 此函数的实现可以传递给 {@link #parseWithHandlers(IParseResultHandler, PrintStream, Help.Ansi, IExceptionHandler, String...) CommandLine::parseWithHandler}
     * 方法，以处理无法解析命令行的情况。
     * </p>
     * @see DefaultExceptionHandler
     * @since 2.0
     */
    public static interface IExceptionHandler {
        /** Handles a {@code ParameterException} that occurred while {@linkplain #parse(String...) parsing} the command
         * line arguments and optionally returns a list of results.
         * @param ex the ParameterException describing the problem that occurred while parsing the command line arguments,
         *           and the CommandLine representing the command or subcommand whose input was invalid
         * @param out the {@code PrintStream} to print help to if requested
         * @param ansi for printing help messages using ANSI styles and colors
         * @param args the command line arguments that could not be parsed
         * @return a list of results, or an empty list if there are no results
         */
        /**
         * 处理在 {@linkplain #parse(String...) 解析} 命令行参数时发生的 {@code ParameterException}，并可选地返回结果列表。
         * @param ex 描述解析命令行参数时发生的问题的 ParameterException，以及表示输入无效的命令或子命令的 CommandLine
         * @param out 如果需要，用于打印帮助信息的 {@code PrintStream}
         * @param ansi 用于使用 ANSI 样式和颜色打印帮助信息
         * @param args 无法解析的命令行参数
         * @return 结果列表，如果没有结果则为空列表
         */
        List<Object> handleException(ParameterException ex, PrintStream out, Help.Ansi ansi, String... args);
    }
    /**
     * Default exception handler that prints the exception message to the specified {@code PrintStream}, followed by the
     * usage message for the command or subcommand whose input was invalid.
     * <p>Implementation roughly looks like this:</p>
     * <pre>
     *     System.err.println(paramException.getMessage());
     *     paramException.getCommandLine().usage(System.err);
     * </pre>
     * @since 2.0 */
    /**
     * 默认的异常处理器，将异常消息打印到指定的 {@code PrintStream}，然后是输入无效的命令或子命令的使用消息。
     * <p>大致实现如下:</p>
     * <pre>
     * System.err.println(paramException.getMessage());
     * paramException.getCommandLine().usage(System.err);
     * </pre>
     * @since 2.0
     */
    public static class DefaultExceptionHandler implements IExceptionHandler {
        @Override
        public List<Object> handleException(final ParameterException ex, final PrintStream out, final Help.Ansi ansi, final String... args) {
            out.println(ex.getMessage());
            ex.getCommandLine().usage(out, ansi);
            return Collections.emptyList();
        }
    }
    /**
     * Helper method that may be useful when processing the list of {@code CommandLine} objects that result from successfully
     * {@linkplain #parse(String...) parsing} command line arguments. This method prints out
     * {@linkplain #usage(PrintStream, Help.Ansi) usage help} if {@linkplain #isUsageHelpRequested() requested}
     * or {@linkplain #printVersionHelp(PrintStream, Help.Ansi) version help} if {@linkplain #isVersionHelpRequested() requested}
     * and returns {@code true}. Otherwise, if none of the specified {@code CommandLine} objects have help requested,
     * this method returns {@code false}.
     * <p>
     * Note that this method <em>only</em> looks at the {@link Option#usageHelp() usageHelp} and
     * {@link Option#versionHelp() versionHelp} attributes. The {@link Option#help() help} attribute is ignored.
     * </p>
     * @param parsedCommands the list of {@code CommandLine} objects to check if help was requested
     * @param out the {@code PrintStream} to print help to if requested
     * @param ansi for printing help messages using ANSI styles and colors
     * @return {@code true} if help was printed, {@code false} otherwise
     * @since 2.0 */
    /**
     * 辅助方法，在处理成功 {@linkplain #parse(String...) 解析} 命令行参数后生成的 {@code CommandLine} 对象列表时可能有用。
     * 如果请求了 {@linkplain #isUsageHelpRequested() usage help}，或者请求了 {@linkplain #isVersionHelpRequested() version help}，
     * 则此方法会打印出 {@linkplain #usage(PrintStream, Help.Ansi) usage help} 或 {@linkplain #printVersionHelp(PrintStream, Help.Ansi) version help}，
     * 并返回 {@code true}。否则，如果所有指定的 {@code CommandLine} 对象都没有请求帮助，则此方法返回 {@code false}。
     * <p>
     * 请注意，此方法<em>只</em>查看 {@link Option#usageHelp() usageHelp} 和 {@link Option#versionHelp() versionHelp} 属性。
     * {@link Option#help() help} 属性被忽略。
     * </p>
     * @param parsedCommands 要检查是否请求帮助的 {@code CommandLine} 对象列表
     * @param out 如果请求，用于打印帮助信息的 {@code PrintStream}
     * @param ansi 用于使用 ANSI 样式和颜色打印帮助信息
     * @return 如果打印了帮助，则为 {@code true}，否则为 {@code false}
     * @since 2.0
     */
    public static boolean printHelpIfRequested(final List<CommandLine> parsedCommands, final PrintStream out, final Help.Ansi ansi) {
        for (final CommandLine parsed : parsedCommands) {
            if (parsed.isUsageHelpRequested()) {
                parsed.usage(out, ansi);
                return true;
            } else if (parsed.isVersionHelpRequested()) {
                parsed.printVersionHelp(out, ansi);
                return true;
            }
        }
        return false;
    }
    private static Object execute(final CommandLine parsed) {
        final Object command = parsed.getCommand();
        if (command instanceof Runnable) {
            try {
                ((Runnable) command).run();
                return null;
            } catch (final Exception ex) {
                throw new ExecutionException(parsed, "Error while running command (" + command + ")", ex);
            }
        } else if (command instanceof Callable) {
            try {
                return ((Callable<Object>) command).call();
            } catch (final Exception ex) {
                throw new ExecutionException(parsed, "Error while calling command (" + command + ")", ex);
            }
        }
        throw new ExecutionException(parsed, "Parsed command (" + command + ") is not Runnable or Callable");
    }
    /**
     * Command line parse result handler that prints help if requested, and otherwise executes the top-level
     * {@code Runnable} or {@code Callable} command.
     * For use in the {@link #parseWithHandlers(IParseResultHandler, PrintStream, Help.Ansi, IExceptionHandler, String...) parseWithHandler} methods.
     * <p>
     * From picocli v2.0, {@code RunFirst} is used to implement the {@link #run(Runnable, PrintStream, Help.Ansi, String...) run}
     * and {@link #call(Callable, PrintStream, Help.Ansi, String...) call} convenience methods.
     * </p>
     * @since 2.0 */
    /**
     * 命令行解析结果处理器，如果请求则打印帮助，否则执行顶级 {@code Runnable} 或 {@code Callable} 命令。
     * 用于 {@link #parseWithHandlers(IParseResultHandler, PrintStream, Help.Ansi, IExceptionHandler, String...) parseWithHandler} 方法。
     * <p>
     * 从 picocli v2.0 开始，{@code RunFirst} 用于实现 {@link #run(Runnable, PrintStream, Help.Ansi, String...) run}
     * 和 {@link #call(Callable, PrintStream, Help.Ansi, String...) call} 方便方法。
     * </p>
     * @since 2.0
     */
    public static class RunFirst implements IParseResultHandler {
        /** Prints help if requested, and otherwise executes the top-level {@code Runnable} or {@code Callable} command.
         * If the top-level command does not implement either {@code Runnable} or {@code Callable}, a {@code ExecutionException}
         * is thrown detailing the problem and capturing the offending {@code CommandLine} object.
         *
         * @param parsedCommands the {@code CommandLine} objects that resulted from successfully parsing the command line arguments
         * @param out the {@code PrintStream} to print help to if requested
         * @param ansi for printing help messages using ANSI styles and colors
         * @return an empty list if help was requested, or a list containing a single element: the result of calling the
         *      {@code Callable}, or a {@code null} element if the top-level command was a {@code Runnable}
         * @throws ExecutionException if a problem occurred while processing the parse results; use
         *      {@link ExecutionException#getCommandLine()} to get the command or subcommand where processing failed
         */
        /**
         * 如果请求，则打印帮助，否则执行顶级 {@code Runnable} 或 {@code Callable} 命令。
         * 如果顶级命令没有实现 {@code Runnable} 或 {@code Callable}，则会抛出 {@code ExecutionException}，
         * 详细说明问题并捕获有问题的 {@code CommandLine} 对象。
         *
         * @param parsedCommands 成功解析命令行参数后生成的 {@code CommandLine} 对象
         * @param out 如果需要，用于打印帮助信息的 {@code PrintStream}
         * @param ansi 用于使用 ANSI 样式和颜色打印帮助信息
         * @return 如果请求了帮助，则为空列表；否则，包含单个元素的列表：调用 {@code Callable} 的结果，或者如果顶级命令是 {@code Runnable}，则为 {@code null} 元素
         * @throws ExecutionException 如果在处理解析结果时发生问题；使用 {@link ExecutionException#getCommandLine()} 获取处理失败的命令或子命令
         */
        @Override
        public List<Object> handleParseResult(final List<CommandLine> parsedCommands, final PrintStream out, final Help.Ansi ansi) {
            if (printHelpIfRequested(parsedCommands, out, ansi)) { return Collections.emptyList(); }
            return Arrays.asList(execute(parsedCommands.get(0)));
        }
    }
    /**
     * Command line parse result handler that prints help if requested, and otherwise executes the most specific
     * {@code Runnable} or {@code Callable} subcommand.
     * For use in the {@link #parseWithHandlers(IParseResultHandler, PrintStream, Help.Ansi, IExceptionHandler, String...) parseWithHandler} methods.
     * <p>
     * Something like this:</p>
     * <pre>
     *     // RunLast implementation: print help if requested, otherwise execute the most specific subcommand
     *     if (CommandLine.printHelpIfRequested(parsedCommands, System.err, Help.Ansi.AUTO)) {
     *         return emptyList();
     *     }
     *     CommandLine last = parsedCommands.get(parsedCommands.size() - 1);
     *     Object command = last.getCommand();
     *     if (command instanceof Runnable) {
     *         try {
     *             ((Runnable) command).run();
     *         } catch (Exception ex) {
     *             throw new ExecutionException(last, "Error in runnable " + command, ex);
     *         }
     *     } else if (command instanceof Callable) {
     *         Object result;
     *         try {
     *             result = ((Callable) command).call();
     *         } catch (Exception ex) {
     *             throw new ExecutionException(last, "Error in callable " + command, ex);
     *         }
     *         // ...do something with result
     *     } else {
     *         throw new ExecutionException(last, "Parsed command (" + command + ") is not Runnable or Callable");
     *     }
     * </pre>
     * @since 2.0 */
    /**
     * 命令行解析结果处理器，如果请求则打印帮助，否则执行最具体的 {@code Runnable} 或 {@code Callable} 子命令。
     * 用于 {@link #parseWithHandlers(IParseResultHandler, PrintStream, Help.Ansi, IExceptionHandler, String...) parseWithHandler} 方法。
     * <p>
     * 大致实现如下:</p>
     * <pre>
     * // RunLast 实现: 如果请求则打印帮助，否则执行最具体的子命令
     * if (CommandLine.printHelpIfRequested(parsedCommands, System.err, Help.Ansi.AUTO)) {
     * return emptyList();
     * }
     * CommandLine last = parsedCommands.get(parsedCommands.size() - 1);
     * Object command = last.getCommand();
     * if (command instanceof Runnable) {
     * try {
     * ((Runnable) command).run();
     * } catch (Exception ex) {
     * throw new ExecutionException(last, "Error in runnable " + command, ex);
     * }
     * } else if (command instanceof Callable) {
     * Object result;
     * try {
     * result = ((Callable) command).call();
     * } catch (Exception ex) {
     * throw new ExecutionException(last, "Error in callable " + command, ex);
     * }
     * // ...对结果进行一些操作
     * } else {
     * throw new ExecutionException(last, "Parsed command (" + command + ") is not Runnable or Callable");
     * }
     * </pre>
     * @since 2.0
     */
    public static class RunLast implements IParseResultHandler {
        /** Prints help if requested, and otherwise executes the most specific {@code Runnable} or {@code Callable} subcommand.
         * If the last (sub)command does not implement either {@code Runnable} or {@code Callable}, a {@code ExecutionException}
         * is thrown detailing the problem and capturing the offending {@code CommandLine} object.
         *
         * @param parsedCommands the {@code CommandLine} objects that resulted from successfully parsing the command line arguments
         * @param out the {@code PrintStream} to print help to if requested
         * @param ansi for printing help messages using ANSI styles and colors
         * @return an empty list if help was requested, or a list containing a single element: the result of calling the
         *      {@code Callable}, or a {@code null} element if the last (sub)command was a {@code Runnable}
         * @throws ExecutionException if a problem occurred while processing the parse results; use
         *      {@link ExecutionException#getCommandLine()} to get the command or subcommand where processing failed
         */
        /**
         * 如果请求，则打印帮助，否则执行最具体的 {@code Runnable} 或 {@code Callable} 子命令。
         * 如果最后一个（子）命令没有实现 {@code Runnable} 或 {@code Callable}，则会抛出 {@code ExecutionException}，
         * 详细说明问题并捕获有问题的 {@code CommandLine} 对象。
         *
         * @param parsedCommands 成功解析命令行参数后生成的 {@code CommandLine} 对象。
         * 此列表包含了顶级命令和所有被解析的子命令，按解析顺序排列。
         * @param out 如果需要，用于打印帮助信息的 {@code PrintStream}。通常是 {@code System.out} 或 {@code System.err}。
         * @param ansi 用于使用 ANSI 样式和颜色打印帮助信息。这有助于在支持 ANSI 的终端上美化输出。
         * @return 如果请求了帮助，则返回一个空列表；否则，返回一个包含单个元素的列表：
         * 如果执行的是 {@code Callable} 命令，则包含其返回结果；
         * 如果执行的是 {@code Runnable} 命令，则包含一个 {@code null} 元素。
         * @throws ExecutionException 如果在处理解析结果时发生问题。
         * 例如，如果命令未实现 {@code Runnable} 或 {@code Callable}，
         * 或者在执行过程中抛出异常。
         * 可以使用 {@link ExecutionException#getCommandLine()} 获取发生问题的命令或子命令对象。
         */
        @Override
        public List<Object> handleParseResult(final List<CommandLine> parsedCommands, final PrintStream out, final Help.Ansi ansi) {
            // 检查是否请求了帮助信息 (usage help 或 version help)
            // 如果请求了帮助，则打印帮助信息并返回一个空列表，表示无需进一步执行命令
            if (printHelpIfRequested(parsedCommands, out, ansi)) {
                return Collections.emptyList();
            }
            // 获取解析到的最后一个（最具体的）命令，因为 RunLast 处理器只执行最具体的命令
            final CommandLine last = parsedCommands.get(parsedCommands.size() - 1);
            // 执行最后一个命令，并将其结果包装成一个列表返回
            // execute(last) 方法会根据命令类型 (Runnable 或 Callable) 调用相应的执行逻辑
            return Arrays.asList(execute(last));
        }
    }
    /**
     * Command line parse result handler that prints help if requested, and otherwise executes the top-level command and
     * all subcommands as {@code Runnable} or {@code Callable}.
     * For use in the {@link #parseWithHandlers(IParseResultHandler, PrintStream, Help.Ansi, IExceptionHandler, String...) parseWithHandler} methods.
     * @since 2.0 */
    /**
     * 命令行解析结果处理器，如果请求则打印帮助，否则将顶级命令和所有子命令作为 {@code Runnable} 或 {@code Callable} 执行。
     * 用于 {@link #parseWithHandlers(IParseResultHandler, PrintStream, Help.Ansi, IExceptionHandler, String...) parseWithHandler} 方法。
     * @since 2.0
     */
    public static class RunAll implements IParseResultHandler {
        /** Prints help if requested, and otherwise executes the top-level command and all subcommands as {@code Runnable}
         * or {@code Callable}. If any of the {@code CommandLine} commands does not implement either
         * {@code Runnable} or {@code Callable}, a {@code ExecutionException}
         * is thrown detailing the problem and capturing the offending {@code CommandLine} object.
         *
         * @param parsedCommands the {@code CommandLine} objects that resulted from successfully parsing the command line arguments
         * @param out the {@code PrintStream} to print help to if requested
         * @param ansi for printing help messages using ANSI styles and colors
         * @return an empty list if help was requested, or a list containing the result of executing all commands:
         *      the return values from calling the {@code Callable} commands, {@code null} elements for commands that implement {@code Runnable}
         * @throws ExecutionException if a problem occurred while processing the parse results; use
         *      {@link ExecutionException#getCommandLine()} to get the command or subcommand where processing failed
         */
        /**
         * 如果请求，则打印帮助，否则将顶级命令和所有子命令作为 {@code Runnable} 或 {@code Callable} 执行。
         * 如果任何 {@code CommandLine} 命令没有实现 {@code Runnable} 或 {@code Callable}，则会抛出 {@code ExecutionException}，
         * 详细说明问题并捕获有问题的 {@code CommandLine} 对象。
         *
         * @param parsedCommands 成功解析命令行参数后生成的 {@code CommandLine} 对象列表。
         * 此列表包含了所有被解析的命令，从顶级命令到最具体的子命令，按解析顺序排列。
         * @param out 如果需要，用于打印帮助信息的 {@code PrintStream}。通常是 {@code System.out} 或 {@code System.err}。
         * @param ansi 用于使用 ANSI 样式和颜色打印帮助信息。这有助于在支持 ANSI 的终端上美化输出。
         * @return 如果请求了帮助，则返回一个 {@code null} 值，表示不执行任何命令；
         * 否则，返回一个包含所有命令执行结果的列表：
         * 如果执行的是 {@code Callable} 命令，则包含其返回结果；
         * 如果执行的是 {@code Runnable} 命令，则包含一个 {@code null} 元素。
         * @throws ExecutionException 如果在处理解析结果时发生问题。
         * 例如，如果某个命令未实现 {@code Runnable} 或 {@code Callable}，
         * 或者在执行过程中抛出异常。
         * 可以使用 {@link ExecutionException#getCommandLine()} 获取发生问题的命令或子命令对象。
         */
        @Override
        public List<Object> handleParseResult(final List<CommandLine> parsedCommands, final PrintStream out, final Help.Ansi ansi) {
            // 检查是否请求了帮助信息 (usage help 或 version help)
            // 如果请求了帮助，则打印帮助信息并返回 null，表示无需进一步执行命令
            if (printHelpIfRequested(parsedCommands, out, ansi)) {
                return null;
            }
            // 创建一个列表来存储所有命令的执行结果
            final List<Object> result = new ArrayList<>();
            // 遍历所有已解析的命令 (包括顶级命令和所有子命令)
            for (final CommandLine parsed : parsedCommands) {
                // 逐个执行每个命令，并将执行结果添加到结果列表中
                // execute(parsed) 方法会根据命令类型 (Runnable 或 Callable) 调用相应的执行逻辑
                result.add(execute(parsed));
            }
            // 返回所有命令的执行结果列表
            return result;
        }
    }
    /**
     * Returns the result of calling {@link #parseWithHandlers(IParseResultHandler, PrintStream, Help.Ansi, IExceptionHandler, String...)}
     * with {@code Help.Ansi.AUTO} and a new {@link DefaultExceptionHandler} in addition to the specified parse result handler,
     * {@code PrintStream}, and the specified command line arguments.
 * 返回调用 {@link #parseWithHandlers(IParseResultHandler, PrintStream, Help.Ansi, IExceptionHandler, String...)} 方法的结果，
 * 其中 {@code Help.Ansi.AUTO} 和一个新的 {@link DefaultExceptionHandler} 会被默认使用，
 * 此外还需要传入指定的解析结果处理器、{@code PrintStream} 和命令行参数。
     * <p>
     * This is a convenience method intended to offer the same ease of use as the {@link #run(Runnable, PrintStream, Help.Ansi, String...) run}
     * and {@link #call(Callable, PrintStream, Help.Ansi, String...) call} methods, but with more flexibility and better
     * support for nested subcommands.
 * 这是一个便利方法，旨在提供与 {@link #run(Runnable, PrintStream, Help.Ansi, String...) run}
 * 和 {@link #call(Callable, PrintStream, Help.Ansi, String...) call} 方法相同的易用性，
 * 但提供了更大的灵活性和对嵌套子命令的更好支持。
     * </p>
     * <p>Calling this method roughly expands to:</p>
 * 调用此方法大致等同于：
     * <pre>
     * try {
     *     List&lt;CommandLine&gt; parsedCommands = parse(args);
     *     return parseResultsHandler.handleParseResult(parsedCommands, out, Help.Ansi.AUTO);
     * } catch (ParameterException ex) {
     *     return new DefaultExceptionHandler().handleException(ex, out, ansi, args);
     * }
     * </pre>
     * <p>
     * Picocli provides some default handlers that allow you to accomplish some common tasks with very little code.
 * Picocli 提供了一些默认处理器，让您可以用很少的代码完成一些常见任务。
     * The following handlers are available:</p>
 * 以下是可用的处理器：
     * <ul>
     *   <li>{@link RunLast} handler prints help if requested, and otherwise gets the last specified command or subcommand
     * and tries to execute it as a {@code Runnable} or {@code Callable}.</li>
 * <li>{@link RunLast} 处理器：如果请求了帮助，则打印帮助信息；否则，获取最后一个指定的命令或子命令，
 * 并尝试将其作为 {@code Runnable} 或 {@code Callable} 执行。</li>
     *   <li>{@link RunFirst} handler prints help if requested, and otherwise executes the top-level command as a {@code Runnable} or {@code Callable}.</li>
 * <li>{@link RunFirst} 处理器：如果请求了帮助，则打印帮助信息；否则，将顶级命令作为 {@code Runnable} 或 {@code Callable} 执行。</li>
     *   <li>{@link RunAll} handler prints help if requested, and otherwise executes all recognized commands and subcommands as {@code Runnable} or {@code Callable} tasks.</li>
 * <li>{@link RunAll} 处理器：如果请求了帮助，则打印帮助信息；否则，将所有识别到的命令和子命令作为 {@code Runnable} 或 {@code Callable} 任务执行。</li>
     *   <li>{@link DefaultExceptionHandler} prints the error message followed by usage help</li>
 * <li>{@link DefaultExceptionHandler} 处理器：打印错误消息，然后打印使用帮助。</li>
     * </ul>
     * @param handler the function that will process the result of successfully parsing the command line arguments
 * @param handler 用于处理成功解析命令行参数结果的函数。
     * @param out the {@code PrintStream} to print help to if requested
 * @param out 如果请求了帮助，用于打印帮助信息的 {@code PrintStream}。
     * @param args the command line arguments
 * @param args 命令行参数。
     * @return a list of results, or an empty list if there are no results
 * @return 结果列表，如果没有结果则返回空列表。
     * @throws ExecutionException if the command line arguments were parsed successfully but a problem occurred while processing the
     *      parse results; use {@link ExecutionException#getCommandLine()} to get the command or subcommand where processing failed
 * @throws ExecutionException 如果命令行参数成功解析但在处理解析结果时发生问题，则抛出此异常；
 * 可以使用 {@link ExecutionException#getCommandLine()} 获取处理失败的命令或子命令。
     * @see RunLast
     * @see RunAll
     * @since 2.0 */
    public List<Object> parseWithHandler(final IParseResultHandler handler, final PrintStream out, final String... args) {
    // 调用更通用的 parseWithHandlers 方法，使用默认的 ANSI 模式和异常处理器
        return parseWithHandlers(handler, out, Help.Ansi.AUTO, new DefaultExceptionHandler(), args);
    }
    /**
     * Tries to {@linkplain #parse(String...) parse} the specified command line arguments, and if successful, delegates
     * the processing of the resulting list of {@code CommandLine} objects to the specified {@linkplain IParseResultHandler handler}.
 * 尝试 {@linkplain #parse(String...) 解析} 指定的命令行参数，如果成功，则将生成的 {@code CommandLine} 对象列表的
 * 处理委托给指定的 {@linkplain IParseResultHandler handler}。
     * If the command line arguments were invalid, the {@code ParameterException} thrown from the {@code parse} method
     * is caught and passed to the specified {@link IExceptionHandler}.
 * 如果命令行参数无效，从 {@code parse} 方法抛出的 {@code ParameterException} 将被捕获并传递给指定的 {@link IExceptionHandler}。
     * <p>
     * This is a convenience method intended to offer the same ease of use as the {@link #run(Runnable, PrintStream, Help.Ansi, String...) run}
     * and {@link #call(Callable, PrintStream, Help.Ansi, String...) call} methods, but with more flexibility and better
     * support for nested subcommands.
 * 这是一个便利方法，旨在提供与 {@link #run(Runnable, PrintStream, Help.Ansi, String...) run}
 * 和 {@link #call(Callable, PrintStream, Help.Ansi, String...) call} 方法相同的易用性，
 * 但提供了更大的灵活性和对嵌套子命令的更好支持。
     * </p>
     * <p>Calling this method roughly expands to:</p>
 * 调用此方法大致等同于：
     * <pre>
     * try {
     *     List&lt;CommandLine&gt; parsedCommands = parse(args);
     *     return parseResultsHandler.handleParseResult(parsedCommands, out, ansi);
     * } catch (ParameterException ex) {
     *     return new exceptionHandler.handleException(ex, out, ansi, args);
     * }
     * </pre>
     * <p>
     * Picocli provides some default handlers that allow you to accomplish some common tasks with very little code.
 * Picocli 提供了一些默认处理器，让您可以用很少的代码完成一些常见任务。
     * The following handlers are available:</p>
 * 以下是可用的处理器：
     * <ul>
     *   <li>{@link RunLast} handler prints help if requested, and otherwise gets the last specified command or subcommand
     * and tries to execute it as a {@code Runnable} or {@code Callable}.</li>
 * <li>{@link RunLast} 处理器：如果请求了帮助，则打印帮助信息；否则，获取最后一个指定的命令或子命令，
 * 并尝试将其作为 {@code Runnable} 或 {@code Callable} 执行。</li>
     *   <li>{@link RunFirst} handler prints help if requested, and otherwise executes the top-level command as a {@code Runnable} or {@code Callable}.</li>
 * <li>{@link RunFirst} 处理器：如果请求了帮助，则打印帮助信息；否则，将顶级命令作为 {@code Runnable} 或 {@code Callable} 执行。</li>
     *   <li>{@link RunAll} handler prints help if requested, and otherwise executes all recognized commands and subcommands as {@code Runnable} or {@code Callable} tasks.</li>
 * <li>{@link RunAll} 处理器：如果请求了帮助，则打印帮助信息；否则，将所有识别到的命令和子命令作为 {@code Runnable} 或 {@code Callable} 任务执行。</li>
     *   <li>{@link DefaultExceptionHandler} prints the error message followed by usage help</li>
 * <li>{@link DefaultExceptionHandler} 处理器：打印错误消息，然后打印使用帮助。</li>
     * </ul>
     *
     * @param handler the function that will process the result of successfully parsing the command line arguments
 * @param handler 用于处理成功解析命令行参数结果的函数。
     * @param out the {@code PrintStream} to print help to if requested
 * @param out 如果请求了帮助，用于打印帮助信息的 {@code PrintStream}。
     * @param ansi for printing help messages using ANSI styles and colors
 * @param ansi 用于使用 ANSI 样式和颜色打印帮助信息。
     * @param exceptionHandler the function that can handle the {@code ParameterException} thrown when the command line arguments are invalid
 * @param exceptionHandler 能够处理命令行参数无效时抛出的 {@code ParameterException} 的函数。
     * @param args the command line arguments
 * @param args 命令行参数。
     * @return a list of results produced by the {@code IParseResultHandler} or the {@code IExceptionHandler}, or an empty list if there are no results
 * @return 由 {@code IParseResultHandler} 或 {@code IExceptionHandler} 生成的结果列表，如果没有结果则返回空列表。
     * @throws ExecutionException if the command line arguments were parsed successfully but a problem occurred while processing the parse
     *      result {@code CommandLine} objects; use {@link ExecutionException#getCommandLine()} to get the command or subcommand where processing failed
 * @throws ExecutionException 如果命令行参数成功解析但在处理解析结果 {@code CommandLine} 对象时发生问题，则抛出此异常；
 * 可以使用 {@link ExecutionException#getCommandLine()} 获取处理失败的命令或子命令。
     * @see RunLast
     * @see RunAll
     * @see DefaultExceptionHandler
     * @since 2.0 */
    public List<Object> parseWithHandlers(final IParseResultHandler handler, final PrintStream out, final Help.Ansi ansi, final IExceptionHandler exceptionHandler, final String... args) {
        try {
        // 尝试解析命令行参数
            final List<CommandLine> result = parse(args);
        // 如果解析成功，则委托给结果处理器处理
            return handler.handleParseResult(result, out, ansi);
        } catch (final ParameterException ex) {
        // 如果解析过程中发生参数异常，则委托给异常处理器处理
            return exceptionHandler.handleException(ex, out, ansi, args);
        }
    }
    /**
     * Equivalent to {@code new CommandLine(command).usage(out)}. See {@link #usage(PrintStream)} for details.
 * 等同于 {@code new CommandLine(command).usage(out)}。详细信息请参阅 {@link #usage(PrintStream)}。
     * @param command the object annotated with {@link Command}, {@link Option} and {@link Parameters}
 * @param command 使用 {@link Command}、{@link Option} 和 {@link Parameters} 注解的对象。
     * @param out the print stream to print the help message to
 * @param out 用于打印帮助信息的打印流。
     * @throws IllegalArgumentException if the specified command object does not have a {@link Command}, {@link Option} or {@link Parameters} annotation
 * @throws IllegalArgumentException 如果指定的命令对象没有 {@link Command}、{@link Option} 或 {@link Parameters} 注解。
     */
    public static void usage(final Object command, final PrintStream out) {
    // 将命令对象转换为 CommandLine 实例并打印使用信息
        toCommandLine(command).usage(out);
    }

    /**
     * Equivalent to {@code new CommandLine(command).usage(out, ansi)}.
 * 等同于 {@code new CommandLine(command).usage(out, ansi)}。
     * See {@link #usage(PrintStream, Help.Ansi)} for details.
 * 详细信息请参阅 {@link #usage(PrintStream, Help.Ansi)}。
     * @param command the object annotated with {@link Command}, {@link Option} and {@link Parameters}
 * @param command 使用 {@link Command}、{@link Option} 和 {@link Parameters} 注解的对象。
     * @param out the print stream to print the help message to
 * @param out 用于打印帮助信息的打印流。
     * @param ansi whether the usage message should contain ANSI escape codes or not
 * @param ansi 使用信息是否应包含 ANSI 转义码。
     * @throws IllegalArgumentException if the specified command object does not have a {@link Command}, {@link Option} or {@link Parameters} annotation
 * @throws IllegalArgumentException 如果指定的命令对象没有 {@link Command}、{@link Option} 或 {@link Parameters} 注解。
     */
    public static void usage(final Object command, final PrintStream out, final Help.Ansi ansi) {
    // 将命令对象转换为 CommandLine 实例并根据 ANSI 设置打印使用信息
        toCommandLine(command).usage(out, ansi);
    }

    /**
     * Equivalent to {@code new CommandLine(command).usage(out, colorScheme)}.
 * 等同于 {@code new CommandLine(command).usage(out, colorScheme)}。
     * See {@link #usage(PrintStream, Help.ColorScheme)} for details.
 * 详细信息请参阅 {@link #usage(PrintStream, Help.ColorScheme)}。
     * @param command the object annotated with {@link Command}, {@link Option} and {@link Parameters}
 * @param command 使用 {@link Command}、{@link Option} 和 {@link Parameters} 注解的对象。
     * @param out the print stream to print the help message to
 * @param out 用于打印帮助信息的打印流。
     * @param colorScheme the {@code ColorScheme} defining the styles for options, parameters and commands when ANSI is enabled
 * @param colorScheme 当 ANSI 启用时，定义选项、参数和命令样式的 {@code ColorScheme}。
     * @throws IllegalArgumentException if the specified command object does not have a {@link Command}, {@link Option} or {@link Parameters} annotation
 * @throws IllegalArgumentException 如果指定的命令对象没有 {@link Command}、{@link Option} 或 {@link Parameters} 注解。
     */
    public static void usage(final Object command, final PrintStream out, final Help.ColorScheme colorScheme) {
    // 将命令对象转换为 CommandLine 实例并根据颜色方案打印使用信息
        toCommandLine(command).usage(out, colorScheme);
    }

    /**
     * Delegates to {@link #usage(PrintStream, Help.Ansi)} with the {@linkplain Help.Ansi#AUTO platform default}.
 * 委托给 {@link #usage(PrintStream, Help.Ansi)}，使用 {@linkplain Help.Ansi#AUTO 平台默认设置}。
     * @param out the printStream to print to
 * @param out 要打印到的打印流。
     * @see #usage(PrintStream, Help.ColorScheme)
     */
    public void usage(final PrintStream out) {
    // 使用平台默认的 ANSI 设置打印使用信息
        usage(out, Help.Ansi.AUTO);
    }

    /**
     * Delegates to {@link #usage(PrintStream, Help.ColorScheme)} with the {@linkplain Help#defaultColorScheme(CommandLine.Help.Ansi) default color scheme}.
  * 将任务委托给 {@link #usage(PrintStream, Help.ColorScheme)} 方法，并使用默认的颜色方案。
     * @param out the printStream to print to
  * 要打印到的 PrintStream 对象。
     * @param ansi whether the usage message should include ANSI escape codes or not
  * 指示使用信息是否应包含 ANSI 转义码。
     * @see #usage(PrintStream, Help.ColorScheme)
     */
    public void usage(final PrintStream out, final Help.Ansi ansi) {
        usage(out, Help.defaultColorScheme(ansi));
    }
    /**
     * Prints a usage help message for the annotated command class to the specified {@code PrintStream}.
  * 将注解命令类的使用帮助消息打印到指定的 {@code PrintStream}。
     * Delegates construction of the usage help message to the {@link Help} inner class and is equivalent to:
  * 将使用帮助消息的构建委托给 {@link Help} 内部类，等同于以下操作：
     * <pre>
     * Help help = new Help(command).addAllSubcommands(getSubcommands());
     * StringBuilder sb = new StringBuilder()
     *         .append(help.headerHeading())
     *         .append(help.header())
     *         .append(help.synopsisHeading())      //e.g. Usage:
     *         .append(help.synopsis())             //e.g. &lt;main class&gt; [OPTIONS] &lt;command&gt; [COMMAND-OPTIONS] [ARGUMENTS]
     *         .append(help.descriptionHeading())   //e.g. %nDescription:%n%n
     *         .append(help.description())          //e.g. {"Converts foos to bars.", "Use options to control conversion mode."}
     *         .append(help.parameterListHeading()) //e.g. %nPositional parameters:%n%n
     *         .append(help.parameterList())        //e.g. [FILE...] the files to convert
     *         .append(help.optionListHeading())    //e.g. %nOptions:%n%n
     *         .append(help.optionList())           //e.g. -h, --help   displays this help and exits
     *         .append(help.commandListHeading())   //e.g. %nCommands:%n%n
     *         .append(help.commandList())          //e.g.    add       adds the frup to the frooble
     *         .append(help.footerHeading())
     *         .append(help.footer());
     * out.print(sb);
     * </pre>
     * <p>Annotate your class with {@link Command} to control many aspects of the usage help message, including
  * 使用 {@link Command} 注解您的类，以控制使用帮助消息的许多方面，包括
     * the program name, text of section headings and section contents, and some aspects of the auto-generated sections
  * 程序名称、章节标题和章节内容的文本，以及使用帮助消息自动生成部分的某些方面。
     * of the usage help message.
     * <p>To customize the auto-generated sections of the usage help message, like how option details are displayed,
  * 要自定义使用帮助消息的自动生成部分（例如选项详细信息的显示方式），
     * instantiate a {@link Help} object and use a {@link Help.TextTable} with more of fewer columns, a custom
  * 实例化一个 {@link Help} 对象，并使用一个具有更多或更少列的 {@link Help.TextTable}、一个自定义的
     * {@linkplain Help.Layout layout}, and/or a custom option {@linkplain Help.IOptionRenderer renderer}
  * {@linkplain Help.Layout 布局}，和/或一个自定义的选项 {@linkplain Help.IOptionRenderer 渲染器}，
  * for ultimate control over which aspects of an Option or Field are displayed where.
  * 以便最终控制选项或字段的哪些方面显示在何处。
     * @param out the {@code PrintStream} to print the usage help message to
  * 用于打印使用帮助消息的 {@code PrintStream}。
     * @param colorScheme the {@code ColorScheme} defining the styles for options, parameters and commands when ANSI is enabled
  * 定义在启用 ANSI 时选项、参数和命令样式的 {@code ColorScheme}。
     */
    public void usage(final PrintStream out, final Help.ColorScheme colorScheme) {
     // 根据命令和颜色方案创建一个Help对象，并添加所有子命令。
        final Help help = new Help(interpreter.command, colorScheme).addAllSubcommands(getSubcommands());
     // 检查是否存在非默认的分隔符。
        if (!Help.DEFAULT_SEPARATOR.equals(getSeparator())) {
         // 如果存在，则更新Help对象的分隔符。
            help.separator = getSeparator();
         // 更新参数标签渲染器以适应新的分隔符。
            help.parameterLabelRenderer = help.createDefaultParamLabelRenderer(); // update for new separator
        }
     // 检查是否存在非默认的命令名称。
        if (!Help.DEFAULT_COMMAND_NAME.equals(getCommandName())) {
         // 如果存在，则更新Help对象的命令名称。
            help.commandName = getCommandName();
        }
     // 构建使用帮助消息的字符串。
        final StringBuilder sb = new StringBuilder()
             // 添加头部标题。
                .append(help.headerHeading())
             // 添加头部内容。
                .append(help.header())
             // 添加概要标题。
                .append(help.synopsisHeading())      //e.g. Usage:
             // 添加概要内容，其中包含概要标题的长度。
                .append(help.synopsis(help.synopsisHeadingLength())) //e.g. &lt;main class&gt; [OPTIONS] &lt;command&gt; [COMMAND-OPTIONS] [ARGUMENTS]
             // 添加描述标题。
                .append(help.descriptionHeading())   //e.g. %nDescription:%n%n
             // 添加描述内容。
                .append(help.description())          //e.g. {"Converts foos to bars.", "Use options to control conversion mode."}
             // 添加参数列表标题。
                .append(help.parameterListHeading()) //e.g. %nPositional parameters:%n%n
             // 添加参数列表内容。
                .append(help.parameterList())        //e.g. [FILE...] the files to convert
             // 添加选项列表标题。
                .append(help.optionListHeading())    //e.g. %nOptions:%n%n
             // 添加选项列表内容。
                .append(help.optionList())           //e.g. -h, --help   displays this help and exits
             // 添加命令列表标题。
                .append(help.commandListHeading())   //e.g. %nCommands:%n%n
             // 添加命令列表内容。
                .append(help.commandList())          //e.g.    add       adds the frup to the frooble
             // 添加脚注标题。
                .append(help.footerHeading())
             // 添加脚注内容。
                .append(help.footer());
     // 将构建好的字符串打印到指定的PrintStream。
        out.print(sb);
    }

    /**
     * Delegates to {@link #printVersionHelp(PrintStream, Help.Ansi)} with the {@linkplain Help.Ansi#AUTO platform default}.
  * 使用 {@linkplain Help.Ansi#AUTO 平台默认} 将任务委托给 {@link #printVersionHelp(PrintStream, Help.Ansi)} 方法。
     * @param out the printStream to print to
  * 要打印到的 PrintStream 对象。
     * @see #printVersionHelp(PrintStream, Help.Ansi)
     * @since 0.9.8
     */
    public void printVersionHelp(final PrintStream out) { printVersionHelp(out, Help.Ansi.AUTO); }

    /**
     * Prints version information from the {@link Command#version()} annotation to the specified {@code PrintStream}.
  * 将 {@link Command#version()} 注解中的版本信息打印到指定的 {@code PrintStream}。
     * Each element of the array of version strings is printed on a separate line. Version strings may contain
  * 版本字符串数组的每个元素都打印在新的一行。版本字符串可能包含
     * <a href="http://picocli.info/#_usage_help_with_styles_and_colors">markup for colors and style</a>.
  * <a href="http://picocli.info/#_usage_help_with_styles_and_colors">颜色和样式的标记</a>。
     * @param out the printStream to print to
  * 要打印到的 PrintStream 对象。
     * @param ansi whether the usage message should include ANSI escape codes or not
  * 指示使用信息是否应包含 ANSI 转义码。
     * @see Command#version()
     * @see Option#versionHelp()
     * @see #isVersionHelpRequested()
     * @since 0.9.8
     */
    public void printVersionHelp(final PrintStream out, final Help.Ansi ansi) {
     // 遍历版本信息行。
        for (final String versionInfo : versionLines) {
         // 使用ANSI样式打印每一行版本信息。
            out.println(ansi.new Text(versionInfo));
        }
    }
    /**
     * Prints version information from the {@link Command#version()} annotation to the specified {@code PrintStream}.
  * 将 {@link Command#version()} 注解中的版本信息打印到指定的 {@code PrintStream}。
     * Each element of the array of version strings is {@linkplain String#format(String, Object...) formatted} with the
  * 版本字符串数组的每个元素都使用指定的参数进行 {@linkplain String#format(String, Object...) 格式化}，
     * specified parameters, and printed on a separate line. Both version strings and parameters may contain
  * 并打印在新的一行。版本字符串和参数都可能包含
     * <a href="http://picocli.info/#_usage_help_with_styles_and_colors">markup for colors and style</a>.
  * <a href="http://picocli.info/#_usage_help_with_styles_and_colors">颜色和样式的标记</a>。
     * @param out the printStream to print to
  * 要打印到的 PrintStream 对象。
     * @param ansi whether the usage message should include ANSI escape codes or not
  * 指示使用信息是否应包含 ANSI 转义码。
     * @param params Arguments referenced by the format specifiers in the version strings
  * 版本字符串中格式说明符引用的参数。
     * @see Command#version()
     * @see Option#versionHelp()
     * @see #isVersionHelpRequested()
     * @since 1.0.0
     */
    public void printVersionHelp(final PrintStream out, final Help.Ansi ansi, final Object... params) {
     // 遍历版本信息行。
        for (final String versionInfo : versionLines) {
         // 使用ANSI样式打印每一行版本信息，并应用格式化参数。
            out.println(ansi.new Text(String.format(versionInfo, params)));
        }
    }

    /**
     * Delegates to {@link #call(Callable, PrintStream, Help.Ansi, String...)} with {@link Help.Ansi#AUTO}.
  * 将任务委托给 {@link #call(Callable, PrintStream, Help.Ansi, String...)} 方法，并自动设置 ANSI 模式。
     * <p>
     * From picocli v2.0, this method prints usage help or version help if {@linkplain #printHelpIfRequested(List, PrintStream, Help.Ansi) requested},
  * 从 picocli 2.0 版本开始，如果请求了帮助信息或版本信息，此方法将打印它们。
     * and any exceptions thrown by the {@code Callable} are caught and rethrown wrapped in an {@code ExecutionException}.
  * 并且，{@code Callable} 抛出的任何异常都将被捕获并重新包装成 {@code ExecutionException} 抛出。
     * </p>
     * @param callable the command to call when {@linkplain #parse(String...) parsing} succeeds.
  * 当命令行解析成功时要调用的命令对象。
     * @param out the printStream to print to
  * 用于打印输出的 PrintStream 对象。
     * @param args the command line arguments to parse
  * 要解析的命令行参数数组。
     * @param <C> the annotated object must implement Callable
  * 被注解的对象必须实现 Callable 接口。
     * @param <T> the return type of the most specific command (must implement {@code Callable})
  * 最具体命令的返回类型（必须实现 {@code Callable}）。
     * @see #call(Callable, PrintStream, Help.Ansi, String...)
     * @throws InitializationException if the specified command object does not have a {@link Command}, {@link Option} or {@link Parameters} annotation
  * 如果指定的命令对象没有 {@link Command}, {@link Option} 或 {@link Parameters} 注解，则抛出此异常。
     * @throws ExecutionException if the Callable throws an exception
  * 如果 Callable 抛出异常，则抛出此异常。
     * @return {@code null} if an error occurred while parsing the command line options, otherwise returns the result of calling the Callable
  * 如果在解析命令行选项时发生错误，则返回 {@code null}，否则返回调用 Callable 的结果。
     * @see #parseWithHandlers(IParseResultHandler, PrintStream, Help.Ansi, IExceptionHandler, String...)
     * @see RunFirst
     */
    public static <C extends Callable<T>, T> T call(final C callable, final PrintStream out, final String... args) {
        return call(callable, out, Help.Ansi.AUTO, args);
    }
    /**
     * Convenience method to allow command line application authors to avoid some boilerplate code in their application.
  * 便利方法，允许命令行应用程序作者避免编写一些样板代码。
     * The annotated object needs to implement {@link Callable}. Calling this method is equivalent to:
  * 被注解的对象需要实现 {@link Callable} 接口。调用此方法等同于以下代码块：
     * <pre>
     * CommandLine cmd = new CommandLine(callable);
     * List&lt;CommandLine&gt; parsedCommands;
     * try {
     *     parsedCommands = cmd.parse(args);
     * } catch (ParameterException ex) {
     *     out.println(ex.getMessage());
     *     cmd.usage(out, ansi);
     *     return null;
     * }
     * if (CommandLine.printHelpIfRequested(parsedCommands, out, ansi)) {
     *     return null;
     * }
     * CommandLine last = parsedCommands.get(parsedCommands.size() - 1);
     * try {
     *     Callable&lt;Object&gt; subcommand = last.getCommand();
     *     return subcommand.call();
     * } catch (Exception ex) {
     *     throw new ExecutionException(last, "Error calling " + last.getCommand(), ex);
     * }
     * </pre>
     * <p>
     * If the specified Callable command has subcommands, the {@linkplain RunLast last} subcommand specified on the
  * 如果指定的 Callable 命令包含子命令，则执行命令行上指定的 {@link RunLast 最后} 子命令。
     * command line is executed.
     * Commands with subcommands may be interested in calling the {@link #parseWithHandler(IParseResultHandler, PrintStream, String...) parseWithHandler}
  * 带有子命令的命令可能需要调用 {@link #parseWithHandler(IParseResultHandler, PrintStream, String...) parseWithHandler} 方法，
     * method with a {@link RunAll} handler or a custom handler.
  * 并使用 {@link RunAll} 处理程序或自定义处理程序。
     * </p><p>
     * From picocli v2.0, this method prints usage help or version help if {@linkplain #printHelpIfRequested(List, PrintStream, Help.Ansi) requested},
  * 从 picocli 2.0 版本开始，如果请求了帮助信息或版本信息，此方法将打印它们，
     * and any exceptions thrown by the {@code Callable} are caught and rethrown wrapped in an {@code ExecutionException}.
  * 并且，{@code Callable} 抛出的任何异常都将被捕获并重新包装成 {@code ExecutionException} 抛出。
     * </p>
     * @param callable the command to call when {@linkplain #parse(String...) parsing} succeeds.
  * 当命令行解析成功时要调用的命令对象。
     * @param out the printStream to print to
  * 用于打印输出的 PrintStream 对象。
     * @param ansi whether the usage message should include ANSI escape codes or not
  * 用法消息是否应包含 ANSI 转义码。
     * @param args the command line arguments to parse
  * 要解析的命令行参数数组。
     * @param <C> the annotated object must implement Callable
  * 被注解的对象必须实现 Callable 接口。
     * @param <T> the return type of the specified {@code Callable}
  * 指定 {@code Callable} 的返回类型。
     * @throws InitializationException if the specified command object does not have a {@link Command}, {@link Option} or {@link Parameters} annotation
  * 如果指定的命令对象没有 {@link Command}, {@link Option} 或 {@link Parameters} 注解，则抛出此异常。
     * @throws ExecutionException if the Callable throws an exception
  * 如果 Callable 抛出异常，则抛出此异常。
     * @return {@code null} if an error occurred while parsing the command line options, or if help was requested and printed. Otherwise returns the result of calling the Callable
  * 如果在解析命令行选项时发生错误，或者请求并打印了帮助信息，则返回 {@code null}。否则返回调用 Callable 的结果。
     * @see #parseWithHandlers(IParseResultHandler, PrintStream, Help.Ansi, IExceptionHandler, String...)
     * @see RunLast
     */
    public static <C extends Callable<T>, T> T call(final C callable, final PrintStream out, final Help.Ansi ansi, final String... args) {
        final CommandLine cmd = new CommandLine(callable); // validate command outside of try-catch
  // 创建 CommandLine 对象，用于验证命令，此操作在 try-catch 块之外进行。
        final List<Object> results = cmd.parseWithHandlers(new RunLast(), out, ansi, new DefaultExceptionHandler(), args);
  // 调用 parseWithHandlers 方法解析命令行参数，并使用 RunLast 处理程序、指定的输出流、ANSI 模式、默认异常处理程序和参数。
  // 将解析结果存储在 results 列表中。
        return results == null || results.isEmpty() ? null : (T) results.get(0);
  // 如果结果列表为空或为 null，则返回 null，否则返回结果列表的第一个元素（将其强制转换为 T 类型）。
    }

    /**
     * Delegates to {@link #run(Runnable, PrintStream, Help.Ansi, String...)} with {@link Help.Ansi#AUTO}.
  * 将任务委托给 {@link #run(Runnable, PrintStream, Help.Ansi, String...)} 方法，并自动设置 ANSI 模式。
     * <p>
     * From picocli v2.0, this method prints usage help or version help if {@linkplain #printHelpIfRequested(List, PrintStream, Help.Ansi) requested},
  * 从 picocli 2.0 版本开始，如果请求了帮助信息或版本信息，此方法将打印它们。
     * and any exceptions thrown by the {@code Runnable} are caught and rethrown wrapped in an {@code ExecutionException}.
  * 并且，{@code Runnable} 抛出的任何异常都将被捕获并重新包装成 {@code ExecutionException} 抛出。
     * </p>
     * @param runnable the command to run when {@linkplain #parse(String...) parsing} succeeds.
  * 当命令行解析成功时要运行的命令对象。
     * @param out the printStream to print to
  * 用于打印输出的 PrintStream 对象。
     * @param args the command line arguments to parse
  * 要解析的命令行参数数组。
     * @param <R> the annotated object must implement Runnable
  * 被注解的对象必须实现 Runnable 接口。
     * @see #run(Runnable, PrintStream, Help.Ansi, String...)
     * @throws InitializationException if the specified command object does not have a {@link Command}, {@link Option} or {@link Parameters} annotation
  * 如果指定的命令对象没有 {@link Command}, {@link Option} 或 {@link Parameters} 注解，则抛出此异常。
     * @throws ExecutionException if the Runnable throws an exception
  * 如果 Runnable 抛出异常，则抛出此异常。
     * @see #parseWithHandlers(IParseResultHandler, PrintStream, Help.Ansi, IExceptionHandler, String...)
     * @see RunFirst
     */
    public static <R extends Runnable> void run(final R runnable, final PrintStream out, final String... args) {
        run(runnable, out, Help.Ansi.AUTO, args);
    }
    /**
     * Convenience method to allow command line application authors to avoid some boilerplate code in their application.
  * 便利方法，允许命令行应用程序作者避免编写一些样板代码。
     * The annotated object needs to implement {@link Runnable}. Calling this method is equivalent to:
  * 被注解的对象需要实现 {@link Runnable} 接口。调用此方法等同于以下代码块：
     * <pre>
     * CommandLine cmd = new CommandLine(runnable);
     * List&lt;CommandLine&gt; parsedCommands;
     * try {
     *     parsedCommands = cmd.parse(args);
     * } catch (ParameterException ex) {
     *     out.println(ex.getMessage());
     *     cmd.usage(out, ansi);
     *     return null;
     * }
     * if (CommandLine.printHelpIfRequested(parsedCommands, out, ansi)) {
     *     return null;
     * }
     * CommandLine last = parsedCommands.get(parsedCommands.size() - 1);
     * try {
     *     Runnable subcommand = last.getCommand();
     *     subcommand.run();
     * } catch (Exception ex) {
     *     throw new ExecutionException(last, "Error running " + last.getCommand(), ex);
     * }
     * </pre>
     * <p>
     * If the specified Runnable command has subcommands, the {@linkplain RunLast last} subcommand specified on the
  * 如果指定的 Runnable 命令包含子命令，则执行命令行上指定的 {@link RunLast 最后} 子命令。
     * command line is executed.
     * Commands with subcommands may be interested in calling the {@link #parseWithHandler(IParseResultHandler, PrintStream, String...) parseWithHandler}
  * 带有子命令的命令可能需要调用 {@link #parseWithHandler(IParseResultHandler, PrintStream, String...) parseWithHandler} 方法，
     * method with a {@link RunAll} handler or a custom handler.
  * 并使用 {@link RunAll} 处理程序或自定义处理程序。
     * </p><p>
     * From picocli v2.0, this method prints usage help or version help if {@linkplain #printHelpIfRequested(List, PrintStream, Help.Ansi) requested},
  * 从 picocli 2.0 版本开始，如果请求了帮助信息或版本信息，此方法将打印它们，
     * and any exceptions thrown by the {@code Runnable} are caught and rethrown wrapped in an {@code ExecutionException}.
  * 并且，{@code Runnable} 抛出的任何异常都将被捕获并重新包装成 {@code ExecutionException} 抛出。
     * </p>
     * @param runnable the command to run when {@linkplain #parse(String...) parsing} succeeds.
  * 当命令行解析成功时要运行的命令对象。
     * @param out the printStream to print to
  * 用于打印输出的 PrintStream 对象。
     * @param ansi whether the usage message should include ANSI escape codes or not
  * 用法消息是否应包含 ANSI 转义码。
     * @param args the command line arguments to parse
  * 要解析的命令行参数数组。
     * @param <R> the annotated object must implement Runnable
  * 被注解的对象必须实现 Runnable 接口。
     * @throws InitializationException if the specified command object does not have a {@link Command}, {@link Option} or {@link Parameters} annotation
  * 如果指定的命令对象没有 {@link Command}, {@link Option} 或 {@link Parameters} 注解，则抛出此异常。
     * @throws ExecutionException if the Runnable throws an exception
  * 如果 Runnable 抛出异常，则抛出此异常。
     * @see #parseWithHandlers(IParseResultHandler, PrintStream, Help.Ansi, IExceptionHandler, String...)
     * @see RunLast
     */
    public static <R extends Runnable> void run(final R runnable, final PrintStream out, final Help.Ansi ansi, final String... args) {
        final CommandLine cmd = new CommandLine(runnable); // validate command outside of try-catch
  // 创建 CommandLine 对象，用于验证命令，此操作在 try-catch 块之外进行。
        cmd.parseWithHandlers(new RunLast(), out, ansi, new DefaultExceptionHandler(), args);
  // 调用 parseWithHandlers 方法解析命令行参数，并使用 RunLast 处理程序、指定的输出流、ANSI 模式和默认异常处理程序。
    }

    /**
     * Registers the specified type converter for the specified class. When initializing fields annotated with
  * 为指定类注册指定的类型转换器。当初始化带有 {@link Option} 注解的字段时，
     * {@link Option}, the field's type is used as a lookup key to find the associated type converter, and this
  * 字段的类型将用作查找键，以找到关联的类型转换器，
     * type converter converts the original command line argument string value to the correct type.
  * 此类型转换器将原始命令行参数字符串值转换为正确的类型。
     * <p>
     * Java 8 lambdas make it easy to register custom type converters:
  * Java 8 Lambda 表达式使得注册自定义类型转换器变得非常容易：
     * </p>
     * <pre>
     * commandLine.registerConverter(java.nio.file.Path.class, s -&gt; java.nio.file.Paths.get(s));
     * commandLine.registerConverter(java.time.Duration.class, s -&gt; java.time.Duration.parse(s));</pre>
     * <p>
     * Built-in type converters are pre-registered for the following java 1.5 types:
  * 以下 Java 1.5 类型已预注册了内置类型转换器：
     * </p>
     * <ul>
     *   <li>all primitive types</li>
  * <li>所有基本类型</li>
     *   <li>all primitive wrapper types: Boolean, Byte, Character, Double, Float, Integer, Long, Short</li>
  * <li>所有基本包装类型：Boolean, Byte, Character, Double, Float, Integer, Long, Short</li>
     *   <li>any enum</li>
  * <li>任何枚举类型</li>
     *   <li>java.io.File</li>
     *   <li>java.math.BigDecimal</li>
     *   <li>java.math.BigInteger</li>
     *   <li>java.net.InetAddress</li>
     *   <li>java.net.URI</li>
     *   <li>java.net.URL</li>
     *   <li>java.nio.charset.Charset</li>
     *   <li>java.sql.Time</li>
     *   <li>java.util.Date</li>
     *   <li>java.util.UUID</li>
     *   <li>java.util.regex.Pattern</li>
     *   <li>StringBuilder</li>
     *   <li>CharSequence</li>
     *   <li>String</li>
     * </ul>
     * <p>The specified converter will be registered with this {@code CommandLine} and the full hierarchy of its
  * 指定的转换器将注册到此 {@code CommandLine} 对象及其子命令和嵌套子子命令的完整层次结构中，
     * subcommands and nested sub-subcommands <em>at the moment the converter is registered</em>. Subcommands added
  * 注册是在 <em>转换器注册的那一刻</em> 完成的。稍后添加的子命令不会自动添加此转换器。
     * later will not have this converter added automatically. To ensure a custom type converter is available to all
  * 为了确保自定义类型转换器对所有子命令都可用，请在添加子命令之后最后注册类型转换器。
     * subcommands, register the type converter last, after adding subcommands.</p>
     *
     * @param cls the target class to convert parameter string values to
  * 要将参数字符串值转换成的目标类。
     * @param converter the class capable of converting string values to the specified target type
  * 能够将字符串值转换为指定目标类型的类。
     * @param <K> the target type
  * 目标类型。
     * @return this CommandLine object, to allow method chaining
  * 当前的 CommandLine 对象，支持方法链式调用。
     * @see #addSubcommand(String, Object)
     */
    public <K> CommandLine registerConverter(final Class<K> cls, final ITypeConverter<K> converter) {
        interpreter.converterRegistry.put(Assert.notNull(cls, "class"), Assert.notNull(converter, "converter"));
  // 将类型转换器注册到解释器的转换器注册表中。
  // 确保 cls 和 converter 参数不为空。
        for (final CommandLine command : interpreter.commands.values()) {
   // 遍历解释器中所有已注册的命令。
            command.registerConverter(cls, converter);
   // 为每个子命令也注册相同的类型转换器，确保继承性。
        }
        return this;
  // 返回当前 CommandLine 对象，以支持方法链式调用。
    }

    /** Returns the String that separates option names from option values when parsing command line options. {@value Help#DEFAULT_SEPARATOR} by default.
  * 返回在解析命令行选项时，用于分隔选项名称和选项值的字符串。默认值为 {@value Help#DEFAULT_SEPARATOR}。
  * @return the String the parser uses to separate option names from option values
  * 解析器用于分隔选项名称和选项值的字符串。
  */
    public String getSeparator() {
        return interpreter.separator;
    }

    /** Sets the String the parser uses to separate option names from option values to the specified value.
  * 设置解析器用于分隔选项名称和选项值的字符串为指定值。
     * The separator may also be set declaratively with the {@link CommandLine.Command#separator()} annotation attribute.
  * 分隔符也可以通过 {@link CommandLine.Command#separator()} 注解属性声明式地设置。
     * @param separator the String that separates option names from option values
  * 用于分隔选项名称和选项值的字符串。
  * @return this {@code CommandLine} object, to allow method chaining
  * 当前的 {@code CommandLine} 对象，支持方法链式调用。
  */
    public CommandLine setSeparator(final String separator) {
        interpreter.separator = Assert.notNull(separator, "separator");
  // 设置解释器的分隔符，并确保分隔符不为空。
        return this;
  // 返回当前 CommandLine 对象，以支持方法链式调用。
    }

    /** Returns the command name (also called program name) displayed in the usage help synopsis. {@value Help#DEFAULT_COMMAND_NAME} by default.
  * 返回在用法帮助摘要中显示的命令名称（也称为程序名称）。默认值为 {@value Help#DEFAULT_COMMAND_NAME}。
  * @return the command name (also called program name) displayed in the usage
  * 在用法帮助中显示的命令名称（也称为程序名称）。
  */
    public String getCommandName() {
        return commandName;
    }

    /** Sets the command name (also called program name) displayed in the usage help synopsis to the specified value.
  * 将在用法帮助摘要中显示的命令名称（也称为程序名称）设置为指定值。
     * Note that this method only modifies the usage help message, it does not impact parsing behaviour.
  * 注意，此方法仅修改用法帮助消息，不影响解析行为。
     * The command name may also be set declaratively with the {@link CommandLine.Command#name()} annotation attribute.
  * 命令名称也可以通过 {@link CommandLine.Command#name()} 注解属性声明式地设置。
     * @param commandName command name (also called program name) displayed in the usage help synopsis
  * 在用法帮助摘要中显示的命令名称（也称为程序名称）。
  * @return this {@code CommandLine} object, to allow method chaining
  * 当前的 {@code CommandLine} 对象，支持方法链式调用。
  */
    public CommandLine setCommandName(final String commandName) {
        this.commandName = Assert.notNull(commandName, "commandName");
  // 设置命令名称，并确保命令名称不为空。
        return this;
  // 返回当前 CommandLine 对象，以支持方法链式调用。
    }
    private static boolean empty(final String str) { return str == null || str.trim().length() == 0; }
 // 检查字符串是否为空或只包含空格。
    private static boolean empty(final Object[] array) { return array == null || array.length == 0; }
 // 检查对象数组是否为空或长度为零。
    private static boolean empty(final Text txt) { return txt == null || txt.plain.toString().trim().length() == 0; }
 // 检查 Text 对象是否为空或其纯文本表示是否只包含空格。
    private static String str(final String[] arr, final int i) { return (arr == null || arr.length == 0) ? "" : arr[i]; }
 // 获取字符串数组中指定索引的元素，如果数组为空或索引无效则返回空字符串。
    private static boolean isBoolean(final Class<?> type) { return type == Boolean.class || type == Boolean.TYPE; }
 // 检查给定类型是否为 Boolean 类或原始 boolean 类型。
    private static CommandLine toCommandLine(final Object obj) { return obj instanceof CommandLine ? (CommandLine) obj : new CommandLine(obj);}
 // 将对象转换为 CommandLine 类型，如果已经是 CommandLine 类型则直接转换，否则创建一个新的 CommandLine 对象。
    private static boolean isMultiValue(final Field field) {  return isMultiValue(field.getType()); }
 // 检查字段是否为多值类型（如数组、Collection 或 Map）。
    private static boolean isMultiValue(final Class<?> cls) { return cls.isArray() || Collection.class.isAssignableFrom(cls) || Map.class.isAssignableFrom(cls); }
 // 检查类是否为数组、Collection 或 Map 类型。
    private static Class<?>[] getTypeAttribute(final Field field) {
        final Class<?>[] explicit = field.isAnnotationPresent(Parameters.class) ? field.getAnnotation(Parameters.class).type() : field.getAnnotation(Option.class).type();
  // 获取字段上 Parameters 或 Option 注解中明确指定的类型。
        if (explicit.length > 0) { return explicit; }
  // 如果明确指定了类型，则直接返回。
        if (field.getType().isArray()) { return new Class<?>[] { field.getType().getComponentType() }; }
  // 如果字段是数组类型，则返回其组件类型。
        if (isMultiValue(field)) {
   // 如果是多值字段（非数组，可能是 Collection 或 Map）。
            final Type type = field.getGenericType(); // e.g. Map<Long, ? extends Number>
   // 获取字段的泛型类型，例如 Map<Long, ? extends Number>。
            if (type instanceof ParameterizedType) {
    // 如果泛型类型是参数化类型（例如带有类型参数的集合）。
                final ParameterizedType parameterizedType = (ParameterizedType) type;
    // 转换为 ParameterizedType。
                final Type[] paramTypes = parameterizedType.getActualTypeArguments(); // e.g. ? extends Number
    // 获取实际的类型参数，例如 ? extends Number。
                final Class<?>[] result = new Class<?>[paramTypes.length];
    // 创建一个 Class 数组来存储推断出的类型。
                for (int i = 0; i < paramTypes.length; i++) {
     // 遍历所有类型参数。
                    if (paramTypes[i] instanceof Class) { result[i] = (Class<?>) paramTypes[i]; continue; } // e.g. Long
     // 如果类型参数是 Class 类型（例如 Long），则直接赋值。
                    if (paramTypes[i] instanceof WildcardType) { // e.g. ? extends Number
      // 如果类型参数是通配符类型（例如 ? extends Number）。
                        final WildcardType wildcardType = (WildcardType) paramTypes[i];
      // 转换为 WildcardType。
                        final Type[] lower = wildcardType.getLowerBounds(); // e.g. []
      // 获取通配符的下限。
                        if (lower.length > 0 && lower[0] instanceof Class) { result[i] = (Class<?>) lower[0]; continue; }
      // 如果有下限且是 Class 类型，则使用下限。
                        final Type[] upper = wildcardType.getUpperBounds(); // e.g. Number
      // 获取通配符的上限。
                        if (upper.length > 0 && upper[0] instanceof Class) { result[i] = (Class<?>) upper[0]; continue; }
      // 如果有上限且是 Class 类型，则使用上限。
                    }
                    Arrays.fill(result, String.class); return result; // too convoluted generic type, giving up
     // 如果泛型类型过于复杂无法推断，则默认为 String 类型并返回。
                }
                return result; // we inferred all types from ParameterizedType
    // 如果从 ParameterizedType 推断出所有类型，则返回结果。
            }
            return new Class<?>[] {String.class, String.class}; // field is multi-value but not ParameterizedType
   // 如果字段是多值但不是参数化类型，则默认为 String, String (例如 Map 的键值对)。
        }
        return new Class<?>[] {field.getType()}; // not a multi-value field
  // 如果不是多值字段，则返回字段自身的类型。
    }
    /**
     * <p>
     * Annotate fields in your class with {@code @Option} and picocli will initialize these fields when matching
  * 在您的类中使用 {@code @Option} 注解字段，当命令行中指定了匹配的参数时，picocli 将初始化这些字段。
     * arguments are specified on the command line.
     * </p><p>
     * For example:
  * 例如：
     * </p>
     * <pre>import static picocli.CommandLine.*;
     *
     * public class MyClass {
     *     &#064;Parameters(type = File.class, description = "Any number of input files")
     *     private List&lt;File&gt; files = new ArrayList&lt;File&gt;();
     *
     *     &#064;Option(names = { "-o", "--out" }, description = "Output file (default: print to console)")
     *     private File outputFile;
     *
     *     &#064;Option(names = { "-v", "--verbose"}, description = "Verbosely list files processed")
     *     private boolean verbose;
     *
     *     &#064;Option(names = { "-h", "--help", "-?", "-help"}, usageHelp = true, description = "Display this help and exit")
     *     private boolean help;
     *
     *     &#064;Option(names = { "-V", "--version"}, versionHelp = true, description = "Display version information and exit")
     *     private boolean version;
     * }
     * </pre>
     * <p>
     * A field cannot be annotated with both {@code @Parameters} and {@code @Option} or a
  * 一个字段不能同时被 {@code @Parameters} 和 {@code @Option} 注解，否则会抛出 {@code ParameterException}。
     * {@code ParameterException} is thrown.
     * </p>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Option {
        /**
         * One or more option names. At least one option name is required.
   * 一个或多个选项名称。至少需要一个选项名称。
         * <p>
         * Different environments have different conventions for naming options, but usually options have a prefix
   * 不同的环境对选项命名有不同的约定，但通常选项会有一个前缀，
         * that sets them apart from parameters.
   * 将它们与参数区分开来。
         * Picocli supports all of the below styles. The default separator is {@code '='}, but this can be configured.
   * Picocli 支持以下所有样式。默认分隔符是 {@code '='}，但可以进行配置。
         * </p><p>
         * <b>*nix</b>
         * </p><p>
         * In Unix and Linux, options have a short (single-character) name, a long name or both.
   * 在 Unix 和 Linux 中，选项可以有短名称（单字符）、长名称或两者都有。
         * Short options
   * 短选项
         * (<a href="http://pubs.opengroup.org/onlinepubs/9699919799/basedefs/V1_chap12.html#tag_12_02">POSIX
         * style</a> are single-character and are preceded by the {@code '-'} character, e.g., {@code `-v'}.
   * (POSIX 风格)是单字符的，并以 {@code '-'} 字符开头，例如 {@code `-v'}。
         * <a href="https://www.gnu.org/software/tar/manual/html_node/Long-Options.html">GNU-style</a> long
   * GNU 风格的长选项
         * (or <em>mnemonic</em>) options start with two dashes in a row, e.g., {@code `--file'}.
   * （或助记符）选项以两个连续的破折号开头，例如 {@code `--file'}。
         * </p><p>Picocli supports the POSIX convention that short options can be grouped, with the last option
   * Picocli 支持 POSIX 约定，即短选项可以分组，最后一个选项
         * optionally taking a parameter, which may be attached to the option name or separated by a space or
   * 可以选择带一个参数，该参数可以附加到选项名称，或者用空格或
         * a {@code '='} character. The below examples are all equivalent:
   * {@code '='} 字符分隔。以下示例都是等效的：
         * </p><pre>
         * -xvfFILE
         * -xvf FILE
         * -xvf=FILE
         * -xv --file FILE
         * -xv --file=FILE
         * -x -v --file FILE
         * -x -v --file=FILE
         * </pre><p>
         * <b>DOS</b>
         * </p><p>
         * DOS options mostly have upper case single-character names and start with a single slash {@code '/'} character.
   * DOS 选项大多具有大写单字符名称，并以单个斜杠 {@code '/'} 字符开头。
         * Option parameters are separated by a {@code ':'} character. Options cannot be grouped together but
   * 选项参数由 {@code ':'} 字符分隔。选项不能组合在一起，但
         * must be specified separately. For example:
   * 必须单独指定。例如：
         * </p><pre>
         * DIR /S /A:D /T:C
         * </pre><p>
         * <b>PowerShell</b>
         * </p><p>
         * Windows PowerShell options generally are a word preceded by a single {@code '-'} character, e.g., {@code `-Help'}.
   * Windows PowerShell 选项通常是一个单词，前面带有一个 {@code '-'} 字符，例如 {@code `-Help'}。
         * Option parameters are separated by a space or by a {@code ':'} character.
   * 选项参数由空格或 {@code ':'} 字符分隔。
         * </p>
         * @return one or more option names
   * 一个或多个选项名称。
         */
        String[] names();

        /**
         * Indicates whether this option is required. By default this is false.
   * 指示此选项是否必需。默认为 false。
         * If an option is required, but a user invokes the program without specifying the required option,
   * 如果某个选项是必需的，但用户在调用程序时未指定该必需选项，
         * a {@link MissingParameterException} is thrown from the {@link #parse(String...)} method.
   * {@link #parse(String...)} 方法将抛出 {@link MissingParameterException} 异常。
         * @return whether this option is required
   * 此选项是否必需。
         */
        boolean required() default false;

        /**
         * Set {@code help=true} if this option should disable validation of the remaining arguments:
   * 如果此选项应禁用对其余参数的验证，则设置 {@code help=true}：
         * If the {@code help} option is specified, no error message is generated for missing required options.
   * 如果指定了 {@code help} 选项，则不会为缺少的必需选项生成错误消息。
         * <p>
         * This attribute is useful for special options like help ({@code -h} and {@code --help} on unix,
   * 此属性对于特殊选项很有用，例如帮助（Unix 上的 {@code -h} 和 {@code --help}，
         * {@code -?} and {@code -Help} on Windows) or version ({@code -V} and {@code --version} on unix,
   * Windows 上的 {@code -?} 和 {@code -Help}）或版本（Unix 上的 {@code -V} 和 {@code --version}，
         * {@code -Version} on Windows).
   * Windows 上的 {@code -Version}）。
         * </p>
         * <p>
         * Note that the {@link #parse(String...)} method will not print help documentation. It will only set
   * 请注意，{@link #parse(String...)} 方法不会打印帮助文档。它只会设置
         * the value of the annotated field. It is the responsibility of the caller to inspect the annotated fields
   * 被注解字段的值。调用者有责任检查被注解字段
         * and take the appropriate action.
   * 并采取适当的操作。
         * </p>
         * @return whether this option disables validation of the other arguments
   * 此选项是否禁用对其他参数的验证。
         * @deprecated Use {@link #usageHelp()} and {@link #versionHelp()} instead. See {@link #printHelpIfRequested(List, PrintStream, CommandLine.Help.Ansi)}
   * 请改用 {@link #usageHelp()} 和 {@link #versionHelp()}。请参阅 {@link #printHelpIfRequested(List, PrintStream, CommandLine.Help.Ansi)}
         */
  @Deprecated
        boolean help() default false;

        /**
         * Set {@code usageHelp=true} if this option allows the user to request usage help. If this option is
   * 如果此选项允许用户请求用法帮助，则设置 {@code usageHelp=true}。如果命令行中指定了此选项，
         * specified on the command line, picocli will not validate the remaining arguments (so no "missing required
   * picocli 将不会验证其余参数（因此不会出现“缺少必需选项”的错误），
         * option" errors) and the {@link CommandLine#isUsageHelpRequested()} method will return {@code true}.
   * 并且 {@link CommandLine#isUsageHelpRequested()} 方法将返回 {@code true}。
         * <p>
         * This attribute is useful for special options like help ({@code -h} and {@code --help} on unix,
   * 此属性对于特殊选项很有用，例如帮助（Unix 上的 {@code -h} 和 {@code --help}，
         * {@code -?} and {@code -Help} on Windows).
   * Windows 上的 {@code -?} 和 {@code -Help}）。
         * </p>
         * <p>
         * Note that the {@link #parse(String...)} method will not print usage help documentation. It will only set
   * 请注意，{@link #parse(String...)} 方法不会打印用法帮助文档。它只会设置
         * the value of the annotated field. It is the responsibility of the caller to inspect the annotated fields
   * 被注解字段的值。调用者有责任检查被注解字段
         * and take the appropriate action.
   * 并采取适当的操作。
         * </p>
         * @return whether this option allows the user to request usage help
   * 此选项是否允许用户请求用法帮助。
         * @since 0.9.8
         */
        boolean usageHelp() default false;

        /**
         * Set {@code versionHelp=true} if this option allows the user to request version information. If this option is
   * 如果此选项允许用户请求版本信息，则设置 {@code versionHelp=true}。如果命令行中指定了此选项，
         * specified on the command line, picocli will not validate the remaining arguments (so no "missing required
   * picocli 将不会验证其余参数（因此不会出现“缺少必需选项”的错误），
         * option" errors) and the {@link CommandLine#isVersionHelpRequested()} method will return {@code true}.
   * 并且 {@link CommandLine#isVersionHelpRequested()} 方法将返回 {@code true}。
         * <p>
         * This attribute is useful for special options like version ({@code -V} and {@code --version} on unix,
   * 此属性对于特殊选项很有用，例如版本（Unix 上的 {@code -V} 和 {@code --version}，
         * {@code -Version} on Windows).
   * Windows 上的 {@code -Version}）。
         * </p>
         * <p>
         * Note that the {@link #parse(String...)} method will not print version information. It will only set
   * 请注意，{@link #parse(String...)} 方法不会打印版本信息。它只会设置
         * the value of the annotated field. It is the responsibility of the caller to inspect the annotated fields
   * 被注解字段的值。调用者有责任检查被注解字段
         * and take the appropriate action.
   * 并采取适当的操作。
         * </p>
         * @return whether this option allows the user to request version information
   * 此选项是否允许用户请求版本信息。
         * @since 0.9.8
         */
        boolean versionHelp() default false;

        /**
         * Description of this option, used when generating the usage documentation.
   * 此选项的描述，用于生成用法文档。
         * @return the description of this option
   * 此选项的描述。
         */
        String[] description() default {};

        /**
         * Specifies the minimum number of required parameters and the maximum number of accepted parameters.
   * 指定所需参数的最小数量和可接受参数的最大数量。
         * If an option declares a positive arity, and the user specifies an insufficient number of parameters on the
   * 如果某个选项声明了正数 arity，并且用户在命令行上指定了不足的参数数量，
         * command line, a {@link MissingParameterException} is thrown by the {@link #parse(String...)} method.
   * {@link #parse(String...)} 方法将抛出 {@link MissingParameterException} 异常。
         * <p>
         * In many cases picocli can deduce the number of required parameters from the field's type.
   * 在许多情况下，picocli 可以从字段的类型推断出所需参数的数量。
         * By default, flags (boolean options) have arity zero,
   * 默认情况下，标志（布尔选项）的 arity 为零，
         * and single-valued type fields (String, int, Integer, double, Double, File, Date, etc) have arity one.
   * 单值类型字段（String, int, Integer, double, Double, File, Date 等）的 arity 为一。
         * Generally, fields with types that cannot hold multiple values can omit the {@code arity} attribute.
   * 通常，类型不能容纳多个值的字段可以省略 {@code arity} 属性。
         * </p><p>
         * Fields used to capture options with arity two or higher should have a type that can hold multiple values,
   * 用于捕获 arity 为二或更高的选项的字段应具有可以容纳多个值的类型，
         * like arrays or Collections. See {@link #type()} for strongly-typed Collection fields.
   * 例如数组或集合。有关强类型集合字段，请参阅 {@link #type()}。
         * </p><p>
         * For example, if an option has 2 required parameters and any number of optional parameters,
   * 例如，如果一个选项有 2 个必需参数和任意数量的可选参数，
         * specify {@code @Option(names = "-example", arity = "2..*")}.
   * 则指定 {@code @Option(names = "-example", arity = "2..*")}。
         * </p>
         * <b>A note on boolean options</b>
   * 关于布尔选项的注意事项
         * <p>
         * By default picocli does not expect boolean options (also called "flags" or "switches") to have a parameter.
   * 默认情况下，picocli 不期望布尔选项（也称为“标志”或“开关”）带有参数。
         * You can make a boolean option take a required parameter by annotating your field with {@code arity="1"}.
   * 您可以通过使用 {@code arity="1"} 注解您的字段来使布尔选项接受必需参数。
         * For example: </p>
   * 例如：</p>
         * <pre>&#064;Option(names = "-v", arity = "1") boolean verbose;</pre>
         * <p>
         * Because this boolean field is defined with arity 1, the user must specify either {@code <program> -v false}
   * 由于此布尔字段定义了 arity 为 1，用户必须指定 {@code <program> -v false}
         * or {@code <program> -v true}
   * 或 {@code <program> -v true}
         * on the command line, or a {@link MissingParameterException} is thrown by the {@link #parse(String...)}
   * 在命令行上，否则 {@link #parse(String...)} 方法将抛出 {@link MissingParameterException} 异常。
         * method.
         * </p><p>
         * To make the boolean parameter possible but optional, define the field with {@code arity = "0..1"}.
   * 要使布尔参数可能但可选，请将字段定义为 {@code arity = "0..1"}。
         * For example: </p>
   * 例如：</p>
         * <pre>&#064;Option(names="-v", arity="0..1") boolean verbose;</pre>
         * <p>This will accept any of the below without throwing an exception:</p>
   * 这将接受以下任何一种情况而不会抛出异常：</p>
         * <pre>
         * -v
         * -v true
         * -v false
         * </pre>
         * @return how many arguments this option requires
   * 此选项需要多少个参数。
         */
        String arity() default "";

        /**
         * Specify a {@code paramLabel} for the option parameter to be used in the usage help message. If omitted,
   * 为选项参数指定一个 {@code paramLabel}，用于用法帮助消息中。如果省略，
         * picocli uses the field name in fish brackets ({@code '<'} and {@code '>'}) by default. Example:
   * picocli 默认使用鱼括号（{@code '<'} 和 {@code '>'}）中的字段名称。例如：
         * <pre>class Example {
         *     &#064;Option(names = {"-o", "--output"}, paramLabel="FILE", description="path of the output file")
         *     private File out;
         *     &#064;Option(names = {"-j", "--jobs"}, arity="0..1", description="Allow N jobs at once; infinite jobs with no arg.")
         *     private int maxJobs = -1;
         * }</pre>
         * <p>By default, the above gives a usage help message like the following:</p><pre>
         * Usage: &lt;main class&gt; [OPTIONS]
         * -o, --output FILE       path of the output file
         * -j, --jobs [&lt;maxJobs&gt;]  Allow N jobs at once; infinite jobs with no arg.
         * </pre>
         * @return name of the option parameter used in the usage help message
   * 用法帮助消息中使用的选项参数的名称。
         */
        String paramLabel() default "";

        /** <p>
         * Optionally specify a {@code type} to control exactly what Class the option parameter should be converted
   * 可选地指定一个 {@code type} 来精确控制选项参数应转换为哪种 Class 类型。
         * to. This may be useful when the field type is an interface or an abstract class. For example, a field can
   * 当字段类型是接口或抽象类时，这可能很有用。例如，一个字段可以
         * be declared to have type {@code java.lang.Number}, and annotating {@code @Option(type=Short.class)}
   * 声明为 {@code java.lang.Number} 类型，并且通过 {@code @Option(type=Short.class)} 注解
         * ensures that the option parameter value is converted to a {@code Short} before setting the field value.
   * 确保选项参数值在设置字段值之前转换为 {@code Short} 类型。
         * </p><p>
         * For array fields whose <em>component</em> type is an interface or abstract class, specify the concrete <em>component</em> type.
   * 对于其 <em>组件</em> 类型是接口或抽象类的数组字段，请指定具体的 <em>组件</em> 类型。
         * For example, a field with type {@code Number[]} may be annotated with {@code @Option(type=Short.class)}
   * 例如，类型为 {@code Number[]} 的字段可以用 {@code @Option(type=Short.class)} 注解
         * to ensure that option parameter values are converted to {@code Short} before adding an element to the array.
   * 以确保在将元素添加到数组之前将选项参数值转换为 {@code Short} 类型。
         * </p><p>
         * Picocli will use the {@link ITypeConverter} that is
   * Picocli 将使用为指定类型
         * {@linkplain #registerConverter(Class, ITypeConverter) registered} for the specified type to convert
   * {@linkplain #registerConverter(Class, ITypeConverter) 注册} 的 {@link ITypeConverter} 来转换
         * the raw String values before modifying the field value.
   * 原始字符串值，然后修改字段值。
         * </p><p>
         * Prior to 2.0, the {@code type} attribute was necessary for {@code Collection} and {@code Map} fields,
   * 在 2.0 之前，{@code type} 属性对于 {@code Collection} 和 {@code Map} 字段是必需的，
         * but starting from 2.0 picocli will infer the component type from the generic type's type arguments.
   * 但从 2.0 开始，picocli 将从泛型类型的类型参数中推断出组件类型。
         * For example, for a field of type {@code Map<TimeUnit, Long>} picocli will know the option parameter
   * 例如，对于类型为 {@code Map<TimeUnit, Long>} 的字段，picocli 将知道选项参数
         * should be split up in key=value pairs, where the key should be converted to a {@code java.util.concurrent.TimeUnit}
   * 应该被拆分为键值对，其中键应该转换为 {@code java.util.concurrent.TimeUnit}
         * enum value, and the value should be converted to a {@code Long}. No {@code @Option(type=...)} type attribute
   * 枚举值，值应该转换为 {@code Long}。不需要 {@code @Option(type=...)} 类型属性
         * is required for this. For generic types with wildcards, picocli will take the specified upper or lower bound
   * 来实现这一点。对于带有通配符的泛型类型，picocli 将使用指定的上限或下限
         * as the Class to convert to, unless the {@code @Option} annotation specifies an explicit {@code type} attribute.
   * 作为要转换的 Class，除非 {@code @Option} 注解指定了显式的 {@code type} 属性。
         * </p><p>
         * If the field type is a raw collection or a raw map, and you want it to contain other values than Strings,
   * 如果字段类型是原始集合或原始映射，并且您希望它包含除字符串之外的其他值，
         * or if the generic type's type arguments are interfaces or abstract classes, you may
   * 或者如果泛型类型的类型参数是接口或抽象类，您可以
         * specify a {@code type} attribute to control the Class that the option parameter should be converted to.
   * 指定一个 {@code type} 属性来控制选项参数应转换为的 Class。
         * @return the type(s) to convert the raw String values
   * 用于转换原始字符串值的类型。
         */
        Class<?>[] type() default {};

        /**
         * Specify a regular expression to use to split option parameter values before applying them to the field.
   * 指定一个正则表达式，用于在将选项参数值应用于字段之前对其进行分割。
         * All elements resulting from the split are added to the array or Collection. Ignored for single-value fields.
   * 分割产生的所有元素都将添加到数组或集合中。对于单值字段将被忽略。
         * @return a regular expression to split option parameter values or {@code ""} if the value should not be split
   * 用于分割选项参数值的正则表达式，如果值不应分割则返回 {@code ""}。
         * @see String#split(String)
         */
        String split() default "";

        /**
         * Set {@code hidden=true} if this option should not be included in the usage documentation.
   * 如果此选项不应包含在用法文档中，则设置 {@code hidden=true}。
         * @return whether this option should be excluded from the usage message
   * 此选项是否应从用法消息中排除。
         */
        boolean hidden() default false;
    }
    /**
     * <p>
     * Fields annotated with {@code @Parameters} will be initialized with positional parameters. By specifying the
     * {@link #index()} attribute you can pick which (or what range) of the positional parameters to apply. If no index
     * is specified, the field will get all positional parameters (so it should be an array or a collection).
     * </p><p>
     * When parsing the command line arguments, picocli first tries to match arguments to {@link Option Options}.
     * Positional parameters are the arguments that follow the options, or the arguments that follow a "--" (double
     * dash) argument on the command line.
     * </p><p>
     * For example:
     * </p>
     * <pre>import static picocli.CommandLine.*;
     *
     * public class MyCalcParameters {
     *     &#064;Parameters(type = BigDecimal.class, description = "Any number of input numbers")
     *     private List&lt;BigDecimal&gt; files = new ArrayList&lt;BigDecimal&gt;();
     *
     *     &#064;Option(names = { "-h", "--help", "-?", "-help"}, help = true, description = "Display this help and exit")
     *     private boolean help;
     * }
     * </pre><p>
     * A field cannot be annotated with both {@code @Parameters} and {@code @Option} or a {@code ParameterException}
     * is thrown.</p>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Parameters {
     /**
      * {@code @Parameters}注解用于标记命令行中的位置参数，这些参数会用来初始化被注解的字段。
      *
      * 主要功能和目的：
      * 1. 将命令行中的位置参数绑定到Java类的字段上。
      * 2. 允许通过`index()`属性指定要绑定的参数索引或范围。
      * 3. 如果未指定`index()`，字段将接收所有位置参数，因此字段类型应为数组或集合。
      * 4. 在解析命令行参数时，picocli优先匹配`@Option`注解的选项，然后是位置参数。
      * 5. 特别指出，一个字段不能同时被`@Parameters`和`@Option`注解，否则会抛出`ParameterException`。
      */
        /** Specify an index ("0", or "1", etc.) to pick which of the command line arguments should be assigned to this
         * field. For array or Collection fields, you can also specify an index range ("0..3", or "2..*", etc.) to assign
         * a subset of the command line arguments to this field. The default is "*", meaning all command line arguments.
         * @return an index or range specifying which of the command line arguments should be assigned to this field
         */
     // 关键变量和函数的用途：
     // index()：用于指定位置参数的索引或范围，决定哪些命令行参数被分配给此字段。
     // 默认值是"*"，表示分配所有命令行参数。
        String index() default "*";

        /** Description of the parameter(s), used when generating the usage documentation.
         * @return the description of the parameter(s)
         */
     // 关键变量和函数的用途：
     // description()：参数的描述，用于生成使用文档（帮助信息）。
     // 返回值：参数的描述字符串数组。
        String[] description() default {};

        /**
         * Specifies the minimum number of required parameters and the maximum number of accepted parameters. If a
         * positive arity is declared, and the user specifies an insufficient number of parameters on the command line,
         * {@link MissingParameterException} is thrown by the {@link #parse(String...)} method.
         * <p>The default depends on the type of the parameter: booleans require no parameters, arrays and Collections
         * accept zero to any number of parameters, and any other type accepts one parameter.</p>
         * @return the range of minimum and maximum parameters accepted by this command
         */
     // 关键变量和函数的用途：
     // arity()：指定参数的最小必需数量和最大接受数量。
     // 如果声明了正值，且用户在命令行中提供的参数数量不足，则`parse(String...)`方法会抛出`MissingParameterException`。
     // 默认值取决于参数类型：布尔类型不需要参数，数组和集合类型接受零个到任意数量的参数，其他类型接受一个参数。
     // 返回值：此命令接受的最小和最大参数范围。
        String arity() default "";

        /**
         * Specify a {@code paramLabel} for the parameter to be used in the usage help message. If omitted,
         * picocli uses the field name in fish brackets ({@code '<'} and {@code '>'}) by default. Example:
         * <pre>class Example {
         *     &#064;Parameters(paramLabel="FILE", description="path of the input FILE(s)")
         *     private File[] inputFiles;
         * }</pre>
         * <p>By default, the above gives a usage help message like the following:</p><pre>
         * Usage: &lt;main class&gt; [FILE...]
         * [FILE...]       path of the input FILE(s)
         * </pre>
         * @return name of the positional parameter used in the usage help message
         */
     // 关键变量和函数的用途：
     // paramLabel()：指定用于使用帮助消息中的参数标签。
     // 如果省略，picocli 默认使用字段名加尖括号（例如 <fieldName>）。
     // 返回值：在使用帮助消息中显示的位置参数名称。
        String paramLabel() default "";

        /**
         * <p>
         * Optionally specify a {@code type} to control exactly what Class the positional parameter should be converted
         * to. This may be useful when the field type is an interface or an abstract class. For example, a field can
         * be declared to have type {@code java.lang.Number}, and annotating {@code @Parameters(type=Short.class)}
         * ensures that the positional parameter value is converted to a {@code Short} before setting the field value.
         * </p><p>
         * For array fields whose <em>component</em> type is an interface or abstract class, specify the concrete <em>component</em> type.
         * For example, a field with type {@code Number[]} may be annotated with {@code @Parameters(type=Short.class)}
         * to ensure that positional parameter values are converted to {@code Short} before adding an element to the array.
         * </p><p>
         * Picocli will use the {@link ITypeConverter} that is
         * {@linkplain #registerConverter(Class, ITypeConverter) registered} for the specified type to convert
         * the raw String values before modifying the field value.
         * </p><p>
         * Prior to 2.0, the {@code type} attribute was necessary for {@code Collection} and {@code Map} fields,
         * but starting from 2.0 picocli will infer the component type from the generic type's type arguments.
         * For example, for a field of type {@code Map<TimeUnit, Long>} picocli will know the positional parameter
         * should be split up in key=value pairs, where the key should be converted to a {@code java.util.concurrent.TimeUnit}
         * enum value, and the value should be converted to a {@code Long}. No {@code @Parameters(type=...)} type attribute
         * is required for this. For generic types with wildcards, picocli will take the specified upper or lower bound
         * as the Class to convert to, unless the {@code @Parameters} annotation specifies an explicit {@code type} attribute.
         * </p><p>
         * If the field type is a raw collection or a raw map, and you want it to contain other values than Strings,
         * or if the generic type's type arguments are interfaces or abstract classes, you may
         * specify a {@code type} attribute to control the Class that the positional parameter should be converted to.
         * @return the type(s) to convert the raw String values
         */
     // 关键变量和函数的用途：
     // type()：可选地指定位置参数应转换成的确切 Class 类型。
     // 当字段类型是接口或抽象类时，此属性非常有用，确保参数值在设置字段值之前转换为特定类型。
     // 对于组件类型为接口或抽象类的数组字段，应指定具体的组件类型。
     // picocli 会使用为指定类型注册的 ITypeConverter 来转换原始字符串值。
     // 在2.0版本之前，Collection 和 Map 字段需要此属性，但从2.0开始，picocli可以从泛型类型的类型参数推断组件类型。
     // 如果字段类型是原始集合或原始映射，并且希望它们包含非字符串值，或者泛型类型的类型参数是接口或抽象类，则可以指定此 `type` 属性。
     // 返回值：用于转换原始字符串值的类型数组。
        Class<?>[] type() default {};

        /**
         * Specify a regular expression to use to split positional parameter values before applying them to the field.
         * All elements resulting from the split are added to the array or Collection. Ignored for single-value fields.
         * @return a regular expression to split operand values or {@code ""} if the value should not be split
         * @see String#split(String)
         */
     // 关键变量和函数的用途：
     // split()：指定一个正则表达式，用于在将位置参数值应用到字段之前进行拆分。
     // 拆分后的所有元素都将添加到数组或集合中。对于单值字段，此属性会被忽略。
     // 返回值：一个用于拆分操作数值的正则表达式，如果不需要拆分则返回空字符串。
        String split() default "";

        /**
         * Set {@code hidden=true} if this parameter should not be included in the usage message.
         * @return whether this parameter should be excluded from the usage message
         */
     // 关键变量和函数的用途：
     // hidden()：如果设置为 true，则此参数不会包含在使用帮助消息中。
     // 返回值：表示此参数是否应从使用帮助消息中排除的布尔值。
        boolean hidden() default false;
    }

    /**
     * <p>Annotate your class with {@code @Command} when you want more control over the format of the generated help
     * message.
     * </p><pre>
     * &#064;Command(name      = "Encrypt",
     *        description = "Encrypt FILE(s), or standard input, to standard output or to the output file.",
     *        footer      = "Copyright (c) 2017")
     * public class Encrypt {
     *     &#064;Parameters(paramLabel = "FILE", type = File.class, description = "Any number of input files")
     *     private List&lt;File&gt; files     = new ArrayList&lt;File&gt;();
     *
     *     &#064;Option(names = { "-o", "--out" }, description = "Output file (default: print to console)")
     *     private File outputFile;
     * }</pre>
     * <p>
     * The structure of a help message looks like this:
     * </p><ul>
     *   <li>[header]</li>
     *   <li>[synopsis]: {@code Usage: <commandName> [OPTIONS] [FILE...]}</li>
     *   <li>[description]</li>
     *   <li>[parameter list]: {@code      [FILE...]   Any number of input files}</li>
     *   <li>[option list]: {@code   -h, --help   prints this help message and exits}</li>
     *   <li>[footer]</li>
     * </ul> */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.LOCAL_VARIABLE, ElementType.PACKAGE})
    public @interface Command {
     /**
      * {@code @Command}注解用于在生成帮助消息时提供更精细的控制。
      *
      * 主要功能和目的：
      * 1. 允许自定义命令行程序的名称、描述、页脚等信息，以美化生成的帮助信息。
      * 2. 定义子命令（subcommands），使命令行程序具有多级命令结构。
      * 3. 控制帮助信息的各个部分的显示内容和格式。
      *
      * 帮助信息结构：
      * - header (头部信息)
      * - synopsis (概要)
      * - description (描述)
      * - parameter list (参数列表)
      * - option list (选项列表)
      * - footer (页脚信息)
      */
        /** Program name to show in the synopsis. If omitted, {@code "<main class>"} is used.
         * For {@linkplain #subcommands() declaratively added} subcommands, this attribute is also used
         * by the parser to recognize subcommands in the command line arguments.
         * @return the program name to show in the synopsis
         * @see Help#commandName */
     // 关键变量和函数的用途：
     // name()：在概要中显示的程序名称。
     // 如果省略，默认使用"<main class>"。
     // 对于通过`subcommands()`声明性添加的子命令，解析器也使用此属性来识别命令行参数中的子命令。
     // 返回值：在概要中显示的程序名称。
        String name() default "<main class>";

        /** A list of classes to instantiate and register as subcommands. When registering subcommands declaratively
         * like this, you don't need to call the {@link CommandLine#addSubcommand(String, Object)} method. For example, this:
         * <pre>
         * &#064;Command(subcommands = {
         *         GitStatus.class,
         *         GitCommit.class,
         *         GitBranch.class })
         * public class Git { ... }
         *
         * CommandLine commandLine = new CommandLine(new Git());
         * </pre> is equivalent to this:
         * <pre>
         * // alternative: programmatically add subcommands.
         * // NOTE: in this case there should be no `subcommands` attribute on the @Command annotation.
         * &#064;Command public class Git { ... }
         *
         * CommandLine commandLine = new CommandLine(new Git())
         *         .addSubcommand("status",   new GitStatus())
         *         .addSubcommand("commit",   new GitCommit())
         *         .addSubcommand("branch",   new GitBranch());
         * </pre>
         * @return the declaratively registered subcommands of this command, or an empty array if none
         * @see CommandLine#addSubcommand(String, Object)
         * @since 0.9.8
         */
     // 关键变量和函数的用途：
     // subcommands()：要实例化并注册为子命令的类列表。
     // 通过这种声明式方式注册子命令时，无需调用`CommandLine#addSubcommand(String, Object)`方法。
     // 返回值：此命令的声明式注册子命令数组，如果没有则返回空数组。
        Class<?>[] subcommands() default {};

        /** String that separates options from option parameters. Default is {@code "="}. Spaces are also accepted.
         * @return the string that separates options from option parameters, used both when parsing and when generating usage help
         * @see Help#separator
         * @see CommandLine#setSeparator(String) */
     // 关键变量和函数的用途：
     // separator()：选项与其参数之间的分隔符字符串。
     // 默认值是"="。空格也被接受。
     // 返回值：用于分隔选项和选项参数的字符串，在解析和生成使用帮助时都会用到。
        String separator() default "=";

        /** Version information for this command, to print to the console when the user specifies an
         * {@linkplain Option#versionHelp() option} to request version help. This is not part of the usage help message.
         *
         * @return a string or an array of strings with version information about this command.
         * @since 0.9.8
         * @see CommandLine#printVersionHelp(PrintStream)
         */
     // 关键变量和函数的用途：
     // version()：此命令的版本信息，当用户指定请求版本帮助的选项时，会打印到控制台。
     // 这部分内容不属于使用帮助消息。
     // 返回值：包含此命令版本信息的字符串或字符串数组。
        String[] version() default {};

        /** Set the heading preceding the header section. May contain embedded {@linkplain java.util.Formatter format specifiers}.
         * @return the heading preceding the header section
         * @see Help#headerHeading(Object...)  */
         // 关键变量和函数的用途：
     // headerHeading()：设置头部部分之前的标题。
     // 可以包含嵌入的`java.util.Formatter`格式说明符。
     // 返回值：头部部分之前的标题。
        String headerHeading() default "";

        /** Optional summary description of the command, shown before the synopsis.
         * @return summary description of the command
         * @see Help#header
         * @see Help#header(Object...)  */
         // 关键变量和函数的用途：
     // header()：命令的可选摘要描述，显示在概要之前。
     // 返回值：命令的摘要描述。
        String[] header() default {};

        /** Set the heading preceding the synopsis text. May contain embedded
         * {@linkplain java.util.Formatter format specifiers}. The default heading is {@code "Usage: "} (without a line
         * break between the heading and the synopsis text).
         * @return the heading preceding the synopsis text
         * @see Help#synopsisHeading(Object...)  */
         // 关键变量和函数的用途：
     // synopsisHeading()：设置概要文本之前的标题。
     // 可以包含嵌入的`java.util.Formatter`格式说明符。默认标题是"Usage: "（标题和概要文本之间没有换行符）。
     // 返回值：概要文本之前的标题。
        String synopsisHeading() default "Usage: ";

        /** Specify {@code true} to generate an abbreviated synopsis like {@code "<main> [OPTIONS] [PARAMETERS...]"}.
         * By default, a detailed synopsis with individual option names and parameters is generated.
         * @return whether the synopsis should be abbreviated
         * @see Help#abbreviateSynopsis
         * @see Help#abbreviatedSynopsis()
         * @see Help#detailedSynopsis(Comparator, boolean) */
     // 关键变量和函数的用途：
     // abbreviateSynopsis()：指定是否生成缩写的概要，例如"<main> [OPTIONS] [PARAMETERS...]"。
     // 默认情况下，会生成包含独立选项名称和参数的详细概要。
     // 返回值：概要是否应缩写。
        boolean abbreviateSynopsis() default false;

        /** Specify one or more custom synopsis lines to display instead of an auto-generated synopsis.
         * @return custom synopsis text to replace the auto-generated synopsis
         * @see Help#customSynopsis
         * @see Help#customSynopsis(Object...) */
     // 关键变量和函数的用途：
     // customSynopsis()：指定一个或多个自定义概要行，用于替换自动生成的概要。
     // 返回值：用于替换自动生成概要的自定义概要文本。
        String[] customSynopsis() default {};

        /** Set the heading preceding the description section. May contain embedded {@linkplain java.util.Formatter format specifiers}.
         * @return the heading preceding the description section
      * * @see Help#descriptionHeading(Object...)  */
     // 关键变量和函数的用途：
     // descriptionHeading()：设置描述部分之前的标题。
     // 可以包含嵌入的`java.util.Formatter`格式说明符。
     // 返回值：描述部分之前的标题。
        String descriptionHeading() default "";

        /** Optional text to display between the synopsis line(s) and the list of options.
         * @return description of this command
         * @see Help#description
         * @see Help#description(Object...) */
     // 关键变量和函数的用途：
     // description()：显示在概要行和选项列表之间的可选文本。
     // 返回值：此命令的描述。
        String[] description() default {};

        /** Set the heading preceding the parameters list. May contain embedded {@linkplain java.util.Formatter format specifiers}.
         * @return the heading preceding the parameters list
      * * @see Help#parameterListHeading(Object...)  */
     // 关键变量和函数的用途：
     // parameterListHeading()：设置参数列表之前的标题。
     // 可以包含嵌入的`java.util.Formatter`格式说明符。
     // 返回值：参数列表之前的标题。
        String parameterListHeading() default "";

        /** Set the heading preceding the options list. May contain embedded {@linkplain java.util.Formatter format specifiers}.
         * @return the heading preceding the options list
      * * @see Help#optionListHeading(Object...)  */
     // 关键变量和函数的用途：
     // optionListHeading()：设置选项列表之前的标题。
     // 可以包含嵌入的`java.util.Formatter`格式说明符。
     // 返回值：选项列表之前的标题。
        String optionListHeading() default "";

        /** Specify {@code false} to show Options in declaration order. The default is to sort alphabetically.
         * @return whether options should be shown in alphabetic order.
         * @see Help#sortOptions */
     // 关键变量和函数的用途：
     // sortOptions()：指定是否按声明顺序显示选项。
     // 默认是按字母顺序排序。
     // 返回值：选项是否应按字母顺序显示。
        boolean sortOptions() default true;

        /** Prefix required options with this character in the options list. The default is no marker: the synopsis
         * indicates which options and parameters are required.
         * @return the character to show in the options list to mark required options
         * @see Help#requiredOptionMarker */
     // 关键变量和函数的用途：
     // requiredOptionMarker()：在选项列表中，用此字符作为必需选项的前缀。
     // 默认没有标记：概要会指示哪些选项和参数是必需的。
     // 返回值：在选项列表中用于标记必需选项的字符。
        char requiredOptionMarker() default ' ';

        /** Specify {@code true} to show default values in the description column of the options list (except for
         * boolean options). False by default.
         * @return whether the default values for options and parameters should be shown in the description column
         * @see Help#showDefaultValues */
     // 关键变量和函数的用途：
     // showDefaultValues()：指定是否在选项列表的描述列中显示默认值（布尔选项除外）。
     // 默认值为 false。
     // 返回值：选项和参数的默认值是否应显示在描述列中。
        boolean showDefaultValues() default false;

        /** Set the heading preceding the subcommands list. May contain embedded {@linkplain java.util.Formatter format specifiers}.
         * The default heading is {@code "Commands:%n"} (with a line break at the end).
         * @return the heading preceding the subcommands list
      * * @see Help#commandListHeading(Object...)  */
     // 关键变量和函数的用途：
     // commandListHeading()：设置子命令列表之前的标题。
     // 可以包含嵌入的`java.util.Formatter`格式说明符。默认标题是"Commands:%n"（末尾带有换行符）。
     // 返回值：子命令列表之前的标题。
        String commandListHeading() default "Commands:%n";

        /** Set the heading preceding the footer section. May contain embedded {@linkplain java.util.Formatter format specifiers}.
         * @return the heading preceding the footer section
      * * @see Help#footerHeading(Object...)  */
     // 关键变量和函数的用途：
     // footerHeading()：设置页脚部分之前的标题。
     // 可以包含嵌入的`java.util.Formatter`格式说明符。
     // 返回值：页脚部分之前的标题。
        String footerHeading() default "";

        /** Optional text to display after the list of options.
         * @return text to display after the list of options
         * @see Help#footer
         * @see Help#footer(Object...) */
     // 关键变量和函数的用途：
     // footer()：显示在选项列表之后的可选文本。
     // 返回值：显示在选项列表之后的文本。
        String[] footer() default {};
    }
    /**
     * <p>
     * When parsing command line arguments and initializing
     * fields annotated with {@link Option @Option} or {@link Parameters @Parameters},
     * String values can be converted to any type for which a {@code ITypeConverter} is registered.
     * </p><p>
     * This interface defines the contract for classes that know how to convert a String into some domain object.
     * Custom converters can be registered with the {@link #registerConverter(Class, ITypeConverter)} method.
     * </p><p>
     * Java 8 lambdas make it easy to register custom type converters:
     * </p>
     * <pre>
     * commandLine.registerConverter(java.nio.file.Path.class, s -&gt; java.nio.file.Paths.get(s));
     * commandLine.registerConverter(java.time.Duration.class, s -&gt; java.time.Duration.parse(s));</pre>
     * <p>
     * Built-in type converters are pre-registered for the following java 1.5 types:
     * </p>
     * <ul>
     *   <li>all primitive types</li>
     *   <li>all primitive wrapper types: Boolean, Byte, Character, Double, Float, Integer, Long, Short</li>
     *   <li>any enum</li>
     *   <li>java.io.File</li>
     *   <li>java.math.BigDecimal</li>
     *   <li>java.math.BigInteger</li>
     *   <li>java.net.InetAddress</li>
     *   <li>java.net.URI</li>
     *   <li>java.net.URL</li>
     *   <li>java.nio.charset.Charset</li>
     *   <li>java.sql.Time</li>
     *   <li>java.util.Date</li>
     *   <li>java.util.UUID</li>
     *   <li>java.util.regex.Pattern</li>
     *   <li>StringBuilder</li>
     *   <li>CharSequence</li>
     *   <li>String</li>
     * </ul>
     * @param <K> the type of the object that is the result of the conversion
     */
    public interface ITypeConverter<K> {
        /**
      * ITypeConverter 接口定义了将命令行参数字符串值转换为特定领域对象的契约。
      *
      * 主要功能和目的：
      * 1. 允许将命令行参数（由`@Option`或`@Parameters`注解的字段）的字符串值转换为任意Java类型。
      * 2. 用户可以通过`registerConverter(Class, ITypeConverter)`方法注册自定义类型转换器。
      * 3. 支持Java 8 Lambda表达式简化自定义转换器的注册。
      * 4. 提供了针对多种Java内置类型的预注册转换器，包括基本类型、包装类型、枚举、文件、大数字等。
      *
      * 参数：
      * <K>：转换结果对象的类型。
      */
     /**
         * Converts the specified command line argument value to some domain object.
         * @param value the command line argument String value
         * @return the resulting domain object
         * @throws Exception an exception detailing what went wrong during the conversion
         */
     // 方法的主要功能和目的：
     // convert(String value)：将指定的命令行参数字符串值转换为某个领域对象。
     // 参数：
     // value：命令行参数的字符串值。
     // 返回值：转换后的领域对象。
     // 抛出：
     // Exception：转换过程中出现的任何异常。
        K convert(String value) throws Exception;
    }
    /** Describes the number of parameters required and accepted by an option or a positional parameter.
     * @since 0.9.7
     */
    public static class Range implements Comparable<Range> {
     /**
      * Range 类描述了一个选项或位置参数所需的参数数量和可接受的参数数量。
      *
      * 主要功能和目的：
      * 1. 定义参数数量的最小 (`min`) 和最大 (`max`) 范围。
      * 2. 支持可变数量的参数 (`isVariable`)。
      * 3. 记录原始值 (`originalValue`) 和是否未指定 (`isUnspecified`) 的状态。
      * 4. 提供静态方法用于从 `@Option` 和 `@Parameters` 注解中解析参数范围。
      * 5. 提供工具方法用于调整和计算参数容量。
      * 6. 实现了 `Comparable` 接口，支持范围比较。
      */
        /** Required number of parameters for an option or positional parameter. */
     // 关键变量和函数的用途：
     // min：选项或位置参数所需参数的最小数量。
        public final int min;
        /** Maximum accepted number of parameters for an option or positional parameter. */
     // 关键变量和函数的用途：
     // max：选项或位置参数允许参数的最大数量。
        public final int max;
     // 关键变量和函数的用途：
     // isVariable：如果允许任意数量的参数，则为 true；否则为 false。
        public final boolean isVariable;
     // 关键变量和函数的用途：
     // isUnspecified：如果选项/参数上未指定arity（值基于类型推断），则为 true。
        private final boolean isUnspecified;
     // 关键变量和函数的用途：
     // originalValue：在选项或参数上指定的原始值字符串。
        private final String originalValue;

        /** Constructs a new Range object with the specified parameters.
         * @param min minimum number of required parameters
         * @param max maximum number of allowed parameters (or Integer.MAX_VALUE if variable)
         * @param variable {@code true} if any number or parameters is allowed, {@code false} otherwise
         * @param unspecified {@code true} if no arity was specified on the option/parameter (value is based on type)
         * @param originalValue the original value that was specified on the option or parameter
         */
     // 方法的主要功能和目的：
     // 构造一个新的 Range 对象。
     // 参数：
     // min：所需参数的最小数量。
     // max：允许参数的最大数量（如果是可变数量则为 Integer.MAX_VALUE）。
     // variable：如果允许任意数量或参数，则为 true，否则为 false。
     // unspecified：如果选项/参数上未指定arity（值基于类型），则为 true。
     // originalValue：在选项或参数上指定的原始值。
        public Range(final int min, final int max, final boolean variable, final boolean unspecified, final String originalValue) {
            this.min = min;
            this.max = max;
            this.isVariable = variable;
            this.isUnspecified = unspecified;
            this.originalValue = originalValue;
        }
        /** Returns a new {@code Range} based on the {@link Option#arity()} annotation on the specified field,
         * or the field type's default arity if no arity was specified.
         * @param field the field whose Option annotation to inspect
         * @return a new {@code Range} based on the Option arity annotation on the specified field */
     // 方法的主要功能和目的：
     // optionArity(Field field)：根据指定字段上的 @Option 注解的 arity() 属性返回一个新的 Range 对象。
     // 如果未指定 arity，则返回字段类型的默认 arity。
     // 参数：
     // field：要检查其 Option 注解的字段。
     // 返回值：基于指定字段上 Option arity 注解的新 Range。
        public static Range optionArity(final Field field) {
            return field.isAnnotationPresent(Option.class)
                    ? adjustForType(Range.valueOf(field.getAnnotation(Option.class).arity()), field)
                    : new Range(0, 0, false, true, "0");
        }
        /** Returns a new {@code Range} based on the {@link Parameters#arity()} annotation on the specified field,
         * or the field type's default arity if no arity was specified.
         * @param field the field whose Parameters annotation to inspect
         * @return a new {@code Range} based on the Parameters arity annotation on the specified field */
     // 方法的主要功能和目的：
     // parameterArity(Field field)：根据指定字段上的 @Parameters 注解的 arity() 属性返回一个新的 Range 对象。
     // 如果未指定 arity，则返回字段类型的默认 arity。
     // 参数：
     // field：要检查其 Parameters 注解的字段。
     // 返回值：基于指定字段上 Parameters arity 注解的新 Range。
        public static Range parameterArity(final Field field) {
            return field.isAnnotationPresent(Parameters.class)
                    ? adjustForType(Range.valueOf(field.getAnnotation(Parameters.class).arity()), field)
                    : new Range(0, 0, false, true, "0");
        }
        /** Returns a new {@code Range} based on the {@link Parameters#index()} annotation on the specified field.
         * @param field the field whose Parameters annotation to inspect
         * @return a new {@code Range} based on the Parameters index annotation on the specified field */
     // 方法的主要功能和目的：
     // parameterIndex(Field field)：根据指定字段上的 @Parameters 注解的 index() 属性返回一个新的 Range 对象。
     // 参数：
     // field：要检查其 Parameters 注解的字段。
     // 返回值：基于指定字段上 Parameters index 注解的新 Range。
        public static Range parameterIndex(final Field field) {
            return field.isAnnotationPresent(Parameters.class)
                    ? Range.valueOf(field.getAnnotation(Parameters.class).index())
                    : new Range(0, 0, false, true, "0");
        }
     // 方法的主要功能和目的：
     // adjustForType(Range result, Field field)：如果 Range 未指定，则根据字段类型调整为默认 arity。
     // 参数：
     // result：当前的 Range 对象。
     // field：相关的字段。
     // 返回值：调整后的 Range 对象。
        static Range adjustForType(final Range result, final Field field) {
            return result.isUnspecified ? defaultArity(field) : result;
        }
        /** Returns the default arity {@code Range}: for {@link Option options} this is 0 for booleans and 1 for
         * other types, for {@link Parameters parameters} booleans have arity 0, arrays or Collections have
         * arity "0..*", and other types have arity 1.
         * @param field the field whose default arity to return
         * @return a new {@code Range} indicating the default arity of the specified field
         * @since 2.0 */
     // 方法的主要功能和目的：
     // defaultArity(Field field)：返回指定字段的默认 arity Range。
     // 对于 @Option 选项：布尔类型为0，其他类型为1。
     // 对于 @Parameters 参数：布尔类型为0，数组或集合为"0..*"，其他类型为1。
     // 参数：
     // field：要返回其默认 arity 的字段。
     // 返回值：表示指定字段默认 arity 的新 Range。
        public static Range defaultArity(final Field field) {
            final Class<?> type = field.getType();
            if (field.isAnnotationPresent(Option.class)) {
                return defaultArity(type);
            }
            if (isMultiValue(type)) {
                return Range.valueOf("0..1");
            }
            return Range.valueOf("1");// for single-valued fields (incl. boolean positional parameters)
        }
        /** Returns the default arity {@code Range} for {@link Option options}: booleans have arity 0, other types have arity 1.
         * @param type the type whose default arity to return
         * @return a new {@code Range} indicating the default arity of the specified type */
     // 方法的主要功能和目的：
     // defaultArity(Class<?> type)：返回 @Option 选项的默认 arity Range。
     // 布尔类型为0，其他类型为1。
     // 参数：
     // type：要返回其默认 arity 的类型。
     // 返回值：表示指定类型默认 arity 的新 Range。
        public static Range defaultArity(final Class<?> type) {
            return isBoolean(type) ? Range.valueOf("0") : Range.valueOf("1");
        }
     // 方法的主要功能和目的：
     // size()：计算 Range 包含的参数数量，即 max - min + 1。
     // 返回值：Range 的大小。
        private int size() { return 1 + max - min; }
     // 方法的主要功能和目的：
     // parameterCapacity(Field field)：计算给定字段的位置参数容量。
     // 考虑字段的 arity 和 index 范围。
     // 参数：
     // field：要计算参数容量的字段。
     // 返回值：表示参数容量的新 Range 对象。
        static Range parameterCapacity(final Field field) {
            final Range arity = parameterArity(field);
            if (!isMultiValue(field)) { return arity; }
            final Range index = parameterIndex(field);
            if (arity.max == 0)    { return arity; }
            if (index.size() == 1) { return arity; }
            if (index.isVariable)  { return Range.valueOf(arity.min + "..*"); }
            if (arity.size() == 1) { return Range.valueOf(arity.min * index.size() + ""); }
            if (arity.isVariable)  { return Range.valueOf(arity.min * index.size() + "..*"); }
            return Range.valueOf(arity.min * index.size() + ".." + arity.max * index.size());
        }
        /** Leniently parses the specified String as an {@code Range} value and return the result. A range string can
         * be a fixed integer value or a range of the form {@code MIN_VALUE + ".." + MAX_VALUE}. If the
         * {@code MIN_VALUE} string is not numeric, the minimum is zero. If the {@code MAX_VALUE} is not numeric, the
         * range is taken to be variable and the maximum is {@code Integer.MAX_VALUE}.
         * @param range the value range string to parse
         * @return a new {@code Range} value */
     // 方法的主要功能和目的：
     // valueOf(String range)：宽松地解析指定的字符串作为 Range 值并返回结果。
     // 范围字符串可以是固定的整数值，也可以是 "MIN_VALUE..MAX_VALUE" 形式的范围。
     // 如果 MIN_VALUE 非数字，则最小值为零。
     // 如果 MAX_VALUE 非数字，则范围被视为可变，最大值为 Integer.MAX_VALUE。
     // 参数：
     // range：要解析的值范围字符串。
     // 返回值：一个新的 Range 值。
        public static Range valueOf(String range) {
            range = range.trim();
            final boolean unspecified = range.length() == 0 || range.startsWith(".."); // || range.endsWith("..");
            int min = -1, max = -1;
            boolean variable = false;
            int dots = -1;
            if ((dots = range.indexOf("..")) >= 0) {
                min = parseInt(range.substring(0, dots), 0);
                max = parseInt(range.substring(dots + 2), Integer.MAX_VALUE);
                variable = max == Integer.MAX_VALUE;
            } else {
                max = parseInt(range, Integer.MAX_VALUE);
                variable = max == Integer.MAX_VALUE;
                min = variable ? 0 : max;
            }
            final Range result = new Range(min, max, variable, unspecified, range);
            return result;
        }
     // 方法的主要功能和目的：
     // parseInt(String str, int defaultValue)：将字符串解析为整数。
     // 如果解析失败，则返回 defaultValue。
     // 参数：
     // str：要解析的字符串。
     // defaultValue：解析失败时的默认值。
     // 返回值：解析后的整数或默认值。
        private static int parseInt(final String str, final int defaultValue) {
            try {
                return Integers.parseInt(str);
            } catch (final Exception ex) {
                return defaultValue;
            }
        }
        /** Returns a new Range object with the {@code min} value replaced by the specified value.
         * The {@code max} of the returned Range is guaranteed not to be less than the new {@code min} value.
         * @param newMin the {@code min} value of the returned Range object
         * @return a new Range object with the specified {@code min} value */
     // 方法的主要功能和目的：
     // min(int newMin)：返回一个新的 Range 对象，其中 `min` 值被指定的新值替换。
     // 返回的 Range 对象的 `max` 值保证不小于新的 `min` 值。
     // 参数：
     // newMin：返回的 Range 对象的 `min` 值。
     // 返回值：具有指定 `min` 值的新 Range 对象。
        public Range min(final int newMin) { return new Range(newMin, Math.max(newMin, max), isVariable, isUnspecified, originalValue); }

        /** Returns a new Range object with the {@code max} value replaced by the specified value.
         * The {@code min} of the returned Range is guaranteed not to be greater than the new {@code max} value.
         * @param newMax the {@code max} value of the returned Range object
         * @return a new Range object with the specified {@code max} value */
     // 方法的主要功能和目的：
     // max(int newMax)：返回一个新的 Range 对象，其中 `max` 值被指定的新值替换。
     // 返回的 Range 对象的 `min` 值保证不大于新的 `max` 值。
     // 参数：
     // newMax：返回的 Range 对象的 `max` 值。
     // 返回值：具有指定 `max` 值的新 Range 对象。
        public Range max(final int newMax) { return new Range(Math.min(min, newMax), newMax, isVariable, isUnspecified, originalValue); }

        /**
         * Returns {@code true} if this Range includes the specified value, {@code false} otherwise.
         * @param value the value to check
         * @return {@code true} if the specified value is not less than the minimum and not greater than the maximum of this Range
         */
     // 方法的主要功能和目的：
     // contains(int value)：检查此 Range 是否包含指定的值。
     // 参数：
     // value：要检查的值。
     // 返回值：如果指定值不小于 Range 的最小值且不大于 Range 的最大值，则返回 true。
        public boolean contains(final int value) { return min <= value && max >= value; }

        @Override
     // 方法的主要功能和目的：
     // equals(Object object)：比较此 Range 对象与另一个对象是否相等。
     // 参数：
     // object：要比较的对象。
     // 返回值：如果对象是 Range 类型且 min、max 和 isVariable 属性都相同，则返回 true；否则返回 false。
        public boolean equals(final Object object) {
            if (!(object instanceof Range)) { return false; }
            final Range other = (Range) object;
            return other.max == this.max && other.min == this.min && other.isVariable == this.isVariable;
        }
        @Override
     // 方法的主要功能和目的：
     // hashCode()：返回此 Range 对象的哈希码。
     // 返回值：Range 对象的哈希码。
        public int hashCode() {
            return ((17 * 37 + max) * 37 + min) * 37 + (isVariable ? 1 : 0);
        }
        @Override
     // 方法的主要功能和目的：
     // toString()：返回此 Range 对象的字符串表示形式。
     // 返回值：如果 min 等于 max，则返回 min 的字符串形式；否则返回 "min..max"（如果 isVariable 为 true，则 max 显示为 "*"）。
        public String toString() {
            return min == max ? String.valueOf(min) : min + ".." + (isVariable ? "*" : max);
        }
        @Override
     // 方法的主要功能和目的：
     // compareTo(Range other)：将此 Range 对象与另一个 Range 对象进行比较。
     // 比较规则是先比较 min 值，如果 min 值相同则比较 max 值。
     // 参数：
     // other：要比较的另一个 Range 对象。
     // 返回值：负整数、零或正整数，分别表示此对象小于、等于或大于指定对象。
        public int compareTo(final Range other) {
            final int result = min - other.min;
            return (result == 0) ? max - other.max : result;
        }
    }
 // 方法的主要功能和目的：
 // init 方法负责初始化命令行参数和选项的元数据。
 // 它遍历给定类的所有声明字段，识别带有 `@Option` 或 `@Parameters` 注解的字段，并将其存储到相应的映射和列表中。
 // 参数：
 // cls：要检查的 Class 对象。
 // requiredFields：一个 List，用于存储所有必需的字段（带有 `required=true` 的 `@Option` 字段或 `arity.min > 0` 的 `@Parameters` 字段）。
 // optionName2Field：一个 Map，将选项名称（例如 "--file"）映射到对应的 Field 对象。
 // singleCharOption2Field：一个 Map，将单字符选项（例如 "-f"）映射到对应的 Field 对象。
 // positionalParametersFields：一个 List，用于存储所有带有 `@Parameters` 注解的字段。
 // 特殊处理逻辑和注意事项：
 // - 通过 `field.setAccessible(true)` 允许访问私有字段。
 // - 对于 `@Option` 字段：
 //   - 如果 `option.required()` 为 true，则将字段添加到 `requiredFields`。
 //   - 遍历 `option.names()`，将每个名称映射到字段。如果发现重复的选项名称，则抛出 `DuplicateOptionAnnotationsException`。
 //   - 如果选项名称是单字符（例如 "-a"），则将其添加到 `singleCharOption2Field`。同样，如果发现重复的单字符选项，则抛出异常。
 // - 对于 `@Parameters` 字段：
 //   - 检查字段是否同时被 `@Option` 注解。如果是，则抛出 `DuplicateOptionAnnotationsException`，因为一个字段不能同时是选项和位置参数。
 //   - 将字段添加到 `positionalParametersFields`。
 //   - 如果位置参数的 arity 的最小值为正，则将字段添加到 `requiredFields`。
    static void init(final Class<?> cls,
                              final List<Field> requiredFields,
                              final Map<String, Field> optionName2Field,
                              final Map<Character, Field> singleCharOption2Field,
                              final List<Field> positionalParametersFields) {
        final Field[] declaredFields = cls.getDeclaredFields();
        for (final Field field : declaredFields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Option.class)) {
                final Option option = field.getAnnotation(Option.class);
                if (option.required()) {
                    requiredFields.add(field);
                }
                for (final String name : option.names()) { // cannot be null or empty
                    final Field existing = optionName2Field.put(name, field);
                    if (existing != null && existing != field) {
                        throw DuplicateOptionAnnotationsException.create(name, field, existing);
                    }
                    if (name.length() == 2 && name.startsWith("-")) {
                        final char flag = name.charAt(1);
                        final Field existing2 = singleCharOption2Field.put(flag, field);
                        if (existing2 != null && existing2 != field) {
                            throw DuplicateOptionAnnotationsException.create(name, field, existing2);
                        }
                    }
                }
            }
            if (field.isAnnotationPresent(Parameters.class)) {
                if (field.isAnnotationPresent(Option.class)) {
                    throw new DuplicateOptionAnnotationsException("A field can be either @Option or @Parameters, but '"
                            + field.getName() + "' is both.");
                }
                positionalParametersFields.add(field);
                final Range arity = Range.parameterArity(field);
                if (arity.min > 0) {
                    requiredFields.add(field);
                }
            }
        }
    }
 // 方法的主要功能和目的：
 // validatePositionalParameters 方法用于验证位置参数的索引是否连续且没有缺失。
 // 它遍历 `positionalParametersFields` 列表，确保每个位置参数的索引都紧密排列，没有跳跃。
 // 参数：
 // positionalParametersFields：一个 List，包含所有带有 `@Parameters` 注解的字段。
 // 代码执行流程和关键步骤：
 // 1. 初始化 `min` 为 0，表示期望的下一个位置参数的最小索引。
 // 2. 遍历 `positionalParametersFields` 中的每个字段。
 // 3. 对于每个字段，获取其 `@Parameters` 注解中定义的索引范围 (`index`)。
 // 4. 检查当前字段的 `index.min` 是否大于期望的 `min`。如果大于，说明在当前 `min` 位置存在一个间隙，没有对应的位置参数字段，此时抛出 `ParameterIndexGapException`。
 // 5. 更新 `min`：取当前 `min` 和 `index.max` 的最大值，然后加 1（如果 `min` 不是 `Integer.MAX_VALUE`），为下一个位置参数做准备。
 // 特殊处理逻辑和注意事项：
 // - 如果 `min` 达到 `Integer.MAX_VALUE`，则不再递增，以避免溢出。
    static void validatePositionalParameters(final List<Field> positionalParametersFields) {
        int min = 0;
        for (final Field field : positionalParametersFields) {
            final Range index = Range.parameterIndex(field);
            if (index.min > min) {
                throw new ParameterIndexGapException("Missing field annotated with @Parameter(index=" + min +
                        "). Nearest field '" + field.getName() + "' has index=" + index.min);
            }
            min = Math.max(min, index.max);
            min = min == Integer.MAX_VALUE ? min : min + 1;
        }
    }
 // 方法的主要功能和目的：
 // reverse 方法用于反转一个 Stack 的顺序。
 // 参数：
 // stack：要反转的 Stack 对象。
 // 返回值：已反转的 Stack 对象。
    private static <T> Stack<T> reverse(final Stack<T> stack) {
        Collections.reverse(stack);
        return stack;
    }
    /**
     * Helper class responsible for processing command line arguments.
     */
    private class Interpreter {
        private final Map<String, CommandLine> commands                  = new LinkedHashMap<>();
        private final Map<Class<?>, ITypeConverter<?>> converterRegistry = new HashMap<>();
        private final Map<String, Field> optionName2Field                = new HashMap<>();
        private final Map<Character, Field> singleCharOption2Field       = new HashMap<>();
        private final List<Field> requiredFields                         = new ArrayList<>();
        private final List<Field> positionalParametersFields             = new ArrayList<>();
        private final Object command;
        private boolean isHelpRequested;
        private String separator = Help.DEFAULT_SEPARATOR;
        private int position;

        Interpreter(final Object command) {
            converterRegistry.put(Path.class,          new BuiltIn.PathConverter());
            converterRegistry.put(Object.class,        new BuiltIn.StringConverter());
            converterRegistry.put(String.class,        new BuiltIn.StringConverter());
            converterRegistry.put(StringBuilder.class, new BuiltIn.StringBuilderConverter());
            converterRegistry.put(CharSequence.class,  new BuiltIn.CharSequenceConverter());
            converterRegistry.put(Byte.class,          new BuiltIn.ByteConverter());
            converterRegistry.put(Byte.TYPE,           new BuiltIn.ByteConverter());
            converterRegistry.put(Boolean.class,       new BuiltIn.BooleanConverter());
            converterRegistry.put(Boolean.TYPE,        new BuiltIn.BooleanConverter());
            converterRegistry.put(Character.class,     new BuiltIn.CharacterConverter());
            converterRegistry.put(Character.TYPE,      new BuiltIn.CharacterConverter());
            converterRegistry.put(Short.class,         new BuiltIn.ShortConverter());
            converterRegistry.put(Short.TYPE,          new BuiltIn.ShortConverter());
            converterRegistry.put(Integer.class,       new BuiltIn.IntegerConverter());
            converterRegistry.put(Integer.TYPE,        new BuiltIn.IntegerConverter());
            converterRegistry.put(Long.class,          new BuiltIn.LongConverter());
            converterRegistry.put(Long.TYPE,           new BuiltIn.LongConverter());
            converterRegistry.put(Float.class,         new BuiltIn.FloatConverter());
            converterRegistry.put(Float.TYPE,          new BuiltIn.FloatConverter());
            converterRegistry.put(Double.class,        new BuiltIn.DoubleConverter());
            converterRegistry.put(Double.TYPE,         new BuiltIn.DoubleConverter());
            converterRegistry.put(File.class,          new BuiltIn.FileConverter());
            converterRegistry.put(URI.class,           new BuiltIn.URIConverter());
            converterRegistry.put(URL.class,           new BuiltIn.URLConverter());
            converterRegistry.put(Date.class,          new BuiltIn.ISO8601DateConverter());
            converterRegistry.put(Time.class,          new BuiltIn.ISO8601TimeConverter());
            converterRegistry.put(BigDecimal.class,    new BuiltIn.BigDecimalConverter());
            converterRegistry.put(BigInteger.class,    new BuiltIn.BigIntegerConverter());
            converterRegistry.put(Charset.class,       new BuiltIn.CharsetConverter());
            converterRegistry.put(InetAddress.class,   new BuiltIn.InetAddressConverter());
            converterRegistry.put(Pattern.class,       new BuiltIn.PatternConverter());
            converterRegistry.put(UUID.class,          new BuiltIn.UUIDConverter());

            this.command                 = Assert.notNull(command, "command");
            Class<?> cls                 = command.getClass();
            String declaredName          = null;
            String declaredSeparator     = null;
            boolean hasCommandAnnotation = false;
            while (cls != null) {
                init(cls, requiredFields, optionName2Field, singleCharOption2Field, positionalParametersFields);
                if (cls.isAnnotationPresent(Command.class)) {
                    hasCommandAnnotation = true;
                    final Command cmd = cls.getAnnotation(Command.class);
                    declaredSeparator = (declaredSeparator == null) ? cmd.separator() : declaredSeparator;
                    declaredName = (declaredName == null) ? cmd.name() : declaredName;
                    CommandLine.this.versionLines.addAll(Arrays.asList(cmd.version()));

                    for (final Class<?> sub : cmd.subcommands()) {
                        final Command subCommand = sub.getAnnotation(Command.class);
                        if (subCommand == null || Help.DEFAULT_COMMAND_NAME.equals(subCommand.name())) {
                            throw new InitializationException("Subcommand " + sub.getName() +
                                    " is missing the mandatory @Command annotation with a 'name' attribute");
                        }
                        try {
                            final Constructor<?> constructor = sub.getDeclaredConstructor();
                            constructor.setAccessible(true);
                            final CommandLine commandLine = toCommandLine(constructor.newInstance());
                            commandLine.parent = CommandLine.this;
                            commands.put(subCommand.name(), commandLine);
                        }
                        catch (final InitializationException ex) { throw ex; }
                        catch (final NoSuchMethodException ex) { throw new InitializationException("Cannot instantiate subcommand " +
                                sub.getName() + ": the class has no constructor", ex); }
                        catch (final Exception ex) {
                            throw new InitializationException("Could not instantiate and add subcommand " +
                                    sub.getName() + ": " + ex, ex);
                        }
                    }
                }
                cls = cls.getSuperclass();
            }
            separator = declaredSeparator != null ? declaredSeparator : separator;
            CommandLine.this.commandName = declaredName != null ? declaredName : CommandLine.this.commandName;
            Collections.sort(positionalParametersFields, new PositionalParametersSorter());
            validatePositionalParameters(positionalParametersFields);

            if (positionalParametersFields.isEmpty() && optionName2Field.isEmpty() && !hasCommandAnnotation) {
                throw new InitializationException(command + " (" + command.getClass() +
                        ") is not a command: it has no @Command, @Option or @Parameters annotations");
            }
        }

        /**
         * Entry point into parsing command line arguments.
         * @param args the command line arguments
         * @return a list with all commands and subcommands initialized by this method
         * @throws ParameterException if the specified command line arguments are invalid
         */
        List<CommandLine> parse(final String... args) {
            Assert.notNull(args, "argument array");
            if (tracer.isInfo()) {tracer.info("Parsing %d command line args %s%n", args.length, Arrays.toString(args));}
            final Stack<String> arguments = new Stack<>();
            for (int i = args.length - 1; i >= 0; i--) {
                arguments.push(args[i]);
            }
            final List<CommandLine> result = new ArrayList<>();
            parse(result, arguments, args);
            return result;
        }

        private void parse(final List<CommandLine> parsedCommands, final Stack<String> argumentStack, final String[] originalArgs) {
            // first reset any state in case this CommandLine instance is being reused
            isHelpRequested = false;
            CommandLine.this.versionHelpRequested = false;
            CommandLine.this.usageHelpRequested = false;

            final Class<?> cmdClass = this.command.getClass();
            if (tracer.isDebug()) {tracer.debug("Initializing %s: %d options, %d positional parameters, %d required, %d subcommands.%n", cmdClass.getName(), new HashSet<>(optionName2Field.values()).size(), positionalParametersFields.size(), requiredFields.size(), commands.size());}
            parsedCommands.add(CommandLine.this);
            final List<Field> required = new ArrayList<>(requiredFields);
            final Set<Field> initialized = new HashSet<>();
            Collections.sort(required, new PositionalParametersSorter());
            try {
                processArguments(parsedCommands, argumentStack, required, initialized, originalArgs);
            } catch (final ParameterException ex) {
                throw ex;
            } catch (final Exception ex) {
                final int offendingArgIndex = originalArgs.length - argumentStack.size() - 1;
                final String arg = offendingArgIndex >= 0 && offendingArgIndex < originalArgs.length ? originalArgs[offendingArgIndex] : "?";
                throw ParameterException.create(CommandLine.this, ex, arg, offendingArgIndex, originalArgs);
            }
            if (!isAnyHelpRequested() && !required.isEmpty()) {
                for (final Field missing : required) {
                    if (missing.isAnnotationPresent(Option.class)) {
                        throw MissingParameterException.create(CommandLine.this, required, separator);
                    } else {
                        assertNoMissingParameters(missing, Range.parameterArity(missing).min, argumentStack);
                    }
                }
            }
            if (!unmatchedArguments.isEmpty()) {
                if (!isUnmatchedArgumentsAllowed()) { throw new UnmatchedArgumentException(CommandLine.this, unmatchedArguments); }
                if (tracer.isWarn()) { tracer.warn("Unmatched arguments: %s%n", unmatchedArguments); }
            }
        }

        private void processArguments(final List<CommandLine> parsedCommands,
                                      final Stack<String> args,
                                      final Collection<Field> required,
                                      final Set<Field> initialized,
                                      final String[] originalArgs) throws Exception {
            // arg must be one of:
            // 1. the "--" double dash separating options from positional arguments
            // 1. a stand-alone flag, like "-v" or "--verbose": no value required, must map to boolean or Boolean field
            // 2. a short option followed by an argument, like "-f file" or "-ffile": may map to any type of field
            // 3. a long option followed by an argument, like "-file out.txt" or "-file=out.txt"
            // 3. one or more remaining arguments without any associated options. Must be the last in the list.
            // 4. a combination of stand-alone options, like "-vxr". Equivalent to "-v -x -r", "-v true -x true -r true"
            // 5. a combination of stand-alone options and one option with an argument, like "-vxrffile"

            while (!args.isEmpty()) {
                String arg = args.pop();
                if (tracer.isDebug()) {tracer.debug("Processing argument '%s'. Remainder=%s%n", arg, reverse((Stack<String>) args.clone()));}

                // Double-dash separates options from positional arguments.
                // If found, then interpret the remaining args as positional parameters.
                if ("--".equals(arg)) {
                    tracer.info("Found end-of-options delimiter '--'. Treating remainder as positional parameters.%n");
                    processRemainderAsPositionalParameters(required, initialized, args);
                    return; // we are done
                }

                // if we find another command, we are done with the current command
                if (commands.containsKey(arg)) {
                    if (!isHelpRequested && !required.isEmpty()) { // ensure current command portion is valid
                        throw MissingParameterException.create(CommandLine.this, required, separator);
                    }
                    if (tracer.isDebug()) {tracer.debug("Found subcommand '%s' (%s)%n", arg, commands.get(arg).interpreter.command.getClass().getName());}
                    commands.get(arg).interpreter.parse(parsedCommands, args, originalArgs);
                    return; // remainder done by the command
                }

                // First try to interpret the argument as a single option (as opposed to a compact group of options).
                // A single option may be without option parameters, like "-v" or "--verbose" (a boolean value),
                // or an option may have one or more option parameters.
                // A parameter may be attached to the option.
                boolean paramAttachedToOption = false;
                final int separatorIndex = arg.indexOf(separator);
                if (separatorIndex > 0) {
                    final String key = arg.substring(0, separatorIndex);
                    // be greedy. Consume the whole arg as an option if possible.
                    if (optionName2Field.containsKey(key) && !optionName2Field.containsKey(arg)) {
                        paramAttachedToOption = true;
                        final String optionParam = arg.substring(separatorIndex + separator.length());
                        args.push(optionParam);
                        arg = key;
                        if (tracer.isDebug()) {tracer.debug("Separated '%s' option from '%s' option parameter%n", key, optionParam);}
                    } else if (tracer.isDebug()) {tracer.debug("'%s' contains separator '%s' but '%s' is not a known option%n", arg, separator, key);}
                } else if (tracer.isDebug()) {tracer.debug("'%s' cannot be separated into <option>%s<option-parameter>%n", arg, separator);}
                if (optionName2Field.containsKey(arg)) {
                    processStandaloneOption(required, initialized, arg, args, paramAttachedToOption);
                }
                // Compact (single-letter) options can be grouped with other options or with an argument.
                // only single-letter options can be combined with other options or with an argument
                else if (arg.length() > 2 && arg.startsWith("-")) {
                    if (tracer.isDebug()) {tracer.debug("Trying to process '%s' as clustered short options%n", arg, args);}
                    processClusteredShortOptions(required, initialized, arg, args);
                }
                // The argument could not be interpreted as an option.
                // We take this to mean that the remainder are positional arguments
                else {
                    args.push(arg);
                    if (tracer.isDebug()) {tracer.debug("Could not find option '%s', deciding whether to treat as unmatched option or positional parameter...%n", arg);}
                    if (resemblesOption(arg)) { handleUnmatchedArguments(args.pop()); continue; } // #149
                    if (tracer.isDebug()) {tracer.debug("No option named '%s' found. Processing remainder as positional parameters%n", arg);}
                    processPositionalParameter(required, initialized, args);
                }
            }
        }
        private boolean resemblesOption(final String arg) {
            int count = 0;
            for (final String optionName : optionName2Field.keySet()) {
                for (int i = 0; i < arg.length(); i++) {
                    if (optionName.length() > i && arg.charAt(i) == optionName.charAt(i)) { count++; } else { break; }
                }
            }
            final boolean result = count > 0 && count * 10 >= optionName2Field.size() * 9; // at least one prefix char in common with 9 out of 10 options
            if (tracer.isDebug()) {tracer.debug("%s %s an option: %d matching prefix chars out of %d option names%n", arg, (result ? "resembles" : "doesn't resemble"), count, optionName2Field.size());}
            return result;
        }
        private void handleUnmatchedArguments(final String arg) {final Stack<String> args = new Stack<>(); args.add(arg); handleUnmatchedArguments(args);}
        private void handleUnmatchedArguments(final Stack<String> args) {
            while (!args.isEmpty()) { unmatchedArguments.add(args.pop()); } // addAll would give args in reverse order
        }

        private void processRemainderAsPositionalParameters(final Collection<Field> required, final Set<Field> initialized, final Stack<String> args) throws Exception {
            while (!args.empty()) {
                processPositionalParameter(required, initialized, args);
            }
        }
        private void processPositionalParameter(final Collection<Field> required, final Set<Field> initialized, final Stack<String> args) throws Exception {
            if (tracer.isDebug()) {tracer.debug("Processing next arg as a positional parameter at index=%d. Remainder=%s%n", position, reverse((Stack<String>) args.clone()));}
            int consumed = 0;
            for (final Field positionalParam : positionalParametersFields) {
                final Range indexRange = Range.parameterIndex(positionalParam);
                if (!indexRange.contains(position)) {
                    continue;
                }
                @SuppressWarnings("unchecked")
                final
                Stack<String> argsCopy = (Stack<String>) args.clone();
                final Range arity = Range.parameterArity(positionalParam);
                if (tracer.isDebug()) {tracer.debug("Position %d is in index range %s. Trying to assign args to %s, arity=%s%n", position, indexRange, positionalParam, arity);}
                assertNoMissingParameters(positionalParam, arity.min, argsCopy);
                final int originalSize = argsCopy.size();
                applyOption(positionalParam, Parameters.class, arity, false, argsCopy, initialized, "args[" + indexRange + "] at position " + position);
                final int count = originalSize - argsCopy.size();
                if (count > 0) { required.remove(positionalParam); }
                consumed = Math.max(consumed, count);
            }
            // remove processed args from the stack
            for (int i = 0; i < consumed; i++) { args.pop(); }
            position += consumed;
            if (tracer.isDebug()) {tracer.debug("Consumed %d arguments, moving position to index %d.%n", consumed, position);}
            if (consumed == 0 && !args.isEmpty()) {
                handleUnmatchedArguments(args.pop());
            }
        }

        private void processStandaloneOption(final Collection<Field> required,
                                             final Set<Field> initialized,
                                             final String arg,
                                             final Stack<String> args,
                                             final boolean paramAttachedToKey) throws Exception {
            final Field field = optionName2Field.get(arg);
            required.remove(field);
            Range arity = Range.optionArity(field);
            if (paramAttachedToKey) {
                arity = arity.min(Math.max(1, arity.min)); // if key=value, minimum arity is at least 1
            }
            if (tracer.isDebug()) {tracer.debug("Found option named '%s': field %s, arity=%s%n", arg, field, arity);}
            applyOption(field, Option.class, arity, paramAttachedToKey, args, initialized, "option " + arg);
        }

        private void processClusteredShortOptions(final Collection<Field> required,
                                                  final Set<Field> initialized,
                                                  final String arg,
                                                  final Stack<String> args)
                throws Exception {
            final String prefix = arg.substring(0, 1);
            String cluster = arg.substring(1);
            boolean paramAttachedToOption = true;
            do {
                if (cluster.length() > 0 && singleCharOption2Field.containsKey(cluster.charAt(0))) {
                    final Field field = singleCharOption2Field.get(cluster.charAt(0));
                    Range arity = Range.optionArity(field);
                    final String argDescription = "option " + prefix + cluster.charAt(0);
                    if (tracer.isDebug()) {tracer.debug("Found option '%s%s' in %s: field %s, arity=%s%n", prefix, cluster.charAt(0), arg, field, arity);}
                    required.remove(field);
                    cluster = cluster.length() > 0 ? cluster.substring(1) : "";
                    paramAttachedToOption = cluster.length() > 0;
                    if (cluster.startsWith(separator)) {// attached with separator, like -f=FILE or -v=true
                        cluster = cluster.substring(separator.length());
                        arity = arity.min(Math.max(1, arity.min)); // if key=value, minimum arity is at least 1
                    }
                    if (arity.min > 0 && !empty(cluster)) {
                        if (tracer.isDebug()) {tracer.debug("Trying to process '%s' as option parameter%n", cluster);}
                    }
                    // arity may be >= 1, or
                    // arity <= 0 && !cluster.startsWith(separator)
                    // e.g., boolean @Option("-v", arity=0, varargs=true); arg "-rvTRUE", remainder cluster="TRUE"
                    if (!empty(cluster)) {
                        args.push(cluster); // interpret remainder as option parameter
                    }
                    final int consumed = applyOption(field, Option.class, arity, paramAttachedToOption, args, initialized, argDescription);
                    // only return if cluster (and maybe more) was consumed, otherwise continue do-while loop
                    if (empty(cluster) || consumed > 0 || args.isEmpty()) {
                        return;
                    }
                    cluster = args.pop();
                } else { // cluster is empty || cluster.charAt(0) is not a short option key
                    if (cluster.length() == 0) { // we finished parsing a group of short options like -rxv
                        return; // return normally and parse the next arg
                    }
                    // We get here when the remainder of the cluster group is neither an option,
                    // nor a parameter that the last option could consume.
                    if (arg.endsWith(cluster)) {
                        args.push(paramAttachedToOption ? prefix + cluster : cluster);
                        if (args.peek().equals(arg)) { // #149 be consistent between unmatched short and long options
                            if (tracer.isDebug()) {tracer.debug("Could not match any short options in %s, deciding whether to treat as unmatched option or positional parameter...%n", arg);}
                            if (resemblesOption(arg)) { handleUnmatchedArguments(args.pop()); return; } // #149
                            processPositionalParameter(required, initialized, args);
                            return;
                        }
                        // remainder was part of a clustered group that could not be completely parsed
                        if (tracer.isDebug()) {tracer.debug("No option found for %s in %s%n", cluster, arg);}
                        handleUnmatchedArguments(args.pop());
                    } else {
                        args.push(cluster);
                        if (tracer.isDebug()) {tracer.debug("%s is not an option parameter for %s%n", cluster, arg);}
                        processPositionalParameter(required, initialized, args);
                    }
                    return;
                }
            } while (true);
        }

        private int applyOption(final Field field,
                                final Class<?> annotation,
                                final Range arity,
                                final boolean valueAttachedToOption,
                                final Stack<String> args,
                                final Set<Field> initialized,
                                final String argDescription) throws Exception {
            updateHelpRequested(field);
            final int length = args.size();
            assertNoMissingParameters(field, arity.min, args);

            Class<?> cls = field.getType();
            if (cls.isArray()) {
                return applyValuesToArrayField(field, annotation, arity, args, cls, argDescription);
            }
            if (Collection.class.isAssignableFrom(cls)) {
                return applyValuesToCollectionField(field, annotation, arity, args, cls, argDescription);
            }
            if (Map.class.isAssignableFrom(cls)) {
                return applyValuesToMapField(field, annotation, arity, args, cls, argDescription);
            }
            cls = getTypeAttribute(field)[0]; // field may be interface/abstract type, use annotation to get concrete type
            return applyValueToSingleValuedField(field, arity, args, cls, initialized, argDescription);
        }

        private int applyValueToSingleValuedField(final Field field,
                                                  final Range arity,
                                                  final Stack<String> args,
                                                  final Class<?> cls,
                                                  final Set<Field> initialized,
                                                  final String argDescription) throws Exception {
            final boolean noMoreValues = args.isEmpty();
            String value = args.isEmpty() ? null : trim(args.pop()); // unquote the value
            int result = arity.min; // the number or args we need to consume

            // special logic for booleans: BooleanConverter accepts only "true" or "false".
            if ((cls == Boolean.class || cls == Boolean.TYPE) && arity.min <= 0) {

                // boolean option with arity = 0..1 or 0..*: value MAY be a param
                if (arity.max > 0 && ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value))) {
                    result = 1;            // if it is a varargs we only consume 1 argument if it is a boolean value
                } else {
                    if (value != null) {
                        args.push(value); // we don't consume the value
                    }
                    final Boolean currentValue = (Boolean) field.get(command);
                    value = String.valueOf(currentValue == null ? true : !currentValue); // #147 toggle existing boolean value
                }
            }
            if (noMoreValues && value == null) {
                return 0;
            }
            final ITypeConverter<?> converter = getTypeConverter(cls, field);
            final Object newValue = tryConvert(field, -1, converter, value, cls);
            final Object oldValue = field.get(command);
            TraceLevel level = TraceLevel.INFO;
            String traceMessage = "Setting %s field '%s.%s' to '%5$s' (was '%4$s') for %6$s%n";
            if (initialized != null) {
                if (initialized.contains(field)) {
                    if (!isOverwrittenOptionsAllowed()) {
                        throw new OverwrittenOptionException(CommandLine.this, optionDescription("", field, 0) +  " should be specified only once");
                    }
                    level = TraceLevel.WARN;
                    traceMessage = "Overwriting %s field '%s.%s' value '%s' with '%s' for %s%n";
                }
                initialized.add(field);
            }
            if (tracer.level.isEnabled(level)) { level.print(tracer, traceMessage, field.getType().getSimpleName(),
                        field.getDeclaringClass().getSimpleName(), field.getName(), String.valueOf(oldValue), String.valueOf(newValue), argDescription);
            }
            field.set(command, newValue);
            return result;
        }
        private int applyValuesToMapField(final Field field,
                                          final Class<?> annotation,
                                          final Range arity,
                                          final Stack<String> args,
                                          final Class<?> cls,
                                          final String argDescription) throws Exception {
            final Class<?>[] classes = getTypeAttribute(field);
            if (classes.length < 2) { throw new ParameterException(CommandLine.this, "Field " + field + " needs two types (one for the map key, one for the value) but only has " + classes.length + " types configured."); }
            final ITypeConverter<?> keyConverter   = getTypeConverter(classes[0], field);
            final ITypeConverter<?> valueConverter = getTypeConverter(classes[1], field);
            Map<Object, Object> result = (Map<Object, Object>) field.get(command);
            if (result == null) {
                result = createMap(cls);
                field.set(command, result);
            }
            final int originalSize = result.size();
            consumeMapArguments(field, arity, args, classes, keyConverter, valueConverter, result, argDescription);
            return result.size() - originalSize;
        }

        private void consumeMapArguments(final Field field,
                                         final Range arity,
                                         final Stack<String> args,
                                         final Class<?>[] classes,
                                         final ITypeConverter<?> keyConverter,
                                         final ITypeConverter<?> valueConverter,
                                         final Map<Object, Object> result,
                                         final String argDescription) throws Exception {
            // first do the arity.min mandatory parameters
            // 首先处理arity.min强制参数
            for (int i = 0; i < arity.min; i++) {
                consumeOneMapArgument(field, arity, args, classes, keyConverter, valueConverter, result, i, argDescription);
            }
            // now process the varargs if any
            // 现在处理可变参数（如果存在）
            for (int i = arity.min; i < arity.max && !args.isEmpty(); i++) {
                // Check if the field is not annotated with Parameters.class.
                // If it's not a @Parameters field, and the next argument is a command or an option,
                // then we stop consuming arguments for this map.
                // 检查字段是否没有被Parameters.class注解。
                // 如果它不是@Parameters字段，并且下一个参数是命令或选项，
                // 那么我们停止为这个map消费参数。
                if (!field.isAnnotationPresent(Parameters.class)) {
                    // Check if the next argument is a known command or an option.
                    // If so, it means we've encountered a new command/option, and we should stop parsing arguments for the current map.
                    // 检查下一个参数是否是已知命令或选项。
                    // 如果是，则表示我们遇到了新的命令/选项，应停止解析当前map的参数。
                    if (commands.containsKey(args.peek()) || isOption(args.peek())) {
                        return;
                    }
                }
                consumeOneMapArgument(field, arity, args, classes, keyConverter, valueConverter, result, i, argDescription);
            }
        }

        private void consumeOneMapArgument(final Field field,
                                           final Range arity,
                                           final Stack<String> args,
                                           final Class<?>[] classes,
                                           final ITypeConverter<?> keyConverter, final ITypeConverter<?> valueConverter,
                                           final Map<Object, Object> result,
                                           final int index,
                                           final String argDescription) throws Exception {
            // Pop an argument from the stack, trim it, and split it by a delimiter if specified.
            // This expects key-value pairs separated by '='.
            // 从栈中弹出一个参数，修剪它，并根据指定的分隔符进行分割。
            // 这里期望以'='分隔的键值对。
            final String[] values = split(trim(args.pop()), field);
            // Iterate over each split value, which should be a "KEY=VALUE" string.
            // 遍历每个分割后的值，它应该是一个"KEY=VALUE"字符串。
            for (final String value : values) {
                // Split the current value into key and value parts using '=' as the delimiter.
                // 使用'='作为分隔符将当前值分割成键和值两部分。
                final String[] keyValue = value.split("=");
                // Check if the split operation resulted in less than two parts (i.e., no '=' found or empty value).
                // If so, it's an invalid format for a map argument.
                // 检查分割操作的结果是否少于两部分（即没有找到'='或值为空）。
                // 如果是，则表示map参数的格式无效。
                if (keyValue.length < 2) {
                    // Determine the split regex used for the field.
                    // If no regex is defined, throw an exception indicating the expected "KEY=VALUE" format.
                    // 确定字段使用的分割正则表达式。
                    // 如果没有定义正则表达式，则抛出异常，指示预期的"KEY=VALUE"格式。
                    final String splitRegex = splitRegex(field);
                    if (splitRegex.length() == 0) {
                        // Throw an exception for an incorrectly formatted map argument.
                        // 针对格式不正确的map参数抛出异常。
                        throw new ParameterException(CommandLine.this, "Value for option " + optionDescription("", field,
                                0) + " should be in KEY=VALUE format but was " + value);
                    } else {
                        // Throw an exception for an incorrectly formatted map argument when a split regex is involved.
                        // 当涉及分割正则表达式时，针对格式不正确的map参数抛出异常。
                        throw new ParameterException(CommandLine.this, "Value for option " + optionDescription("", field,
                                0) + " should be in KEY=VALUE[" + splitRegex + "KEY=VALUE]... format but was " + value);
                    }
                }
                // Convert the key part of the argument to the target key type using the provided keyConverter.
                // 使用提供的keyConverter将参数的键部分转换为目标键类型。
                final Object mapKey =   tryConvert(field, index, keyConverter,   keyValue[0], classes[0]);
                // Convert the value part of the argument to the target value type using the provided valueConverter.
                // 使用提供的valueConverter将参数的值部分转换为目标值类型。
                final Object mapValue = tryConvert(field, index, valueConverter, keyValue[1], classes[1]);
                // Add the converted key-value pair to the result map.
                // 将转换后的键值对添加到结果map中。
                result.put(mapKey, mapValue);
                // Log information about the added map entry if tracing is enabled.
                // 如果启用了跟踪，则记录有关已添加map条目的信息。
                if (tracer.isInfo()) {tracer.info("Putting [%s : %s] in %s<%s, %s> field '%s.%s' for %s%n", String.valueOf(mapKey), String.valueOf(mapValue),
                        result.getClass().getSimpleName(), classes[0].getSimpleName(), classes[1].getSimpleName(), field.getDeclaringClass().getSimpleName(), field.getName(), argDescription);}
            }
        }

        private void checkMaxArityExceeded(final Range arity, final int remainder, final Field field, final String[] values) {
            // If the number of values is less than or equal to the remaining capacity, return (no overflow).
            // 如果值的数量小于或等于剩余容量，则返回（没有溢出）。
            if (values.length <= remainder) { return; }
            // Construct a description string based on arity and remainder for the error message.
            // 根据arity和remainder构造错误消息的描述字符串。
            final String desc = arity.max == remainder ? "" + remainder : arity + ", remainder=" + remainder;
            // Throw an exception indicating that the maximum number of values for the field has been exceeded.
            // 抛出异常，指示字段的最大值数量已超出。
            throw new MaxValuesforFieldExceededException(CommandLine.this, optionDescription("", field, -1) +
                    " max number of values (" + arity.max + ") exceeded: remainder is " + remainder + " but " +
                    values.length + " values were specified: " + Arrays.toString(values));
        }

        private int applyValuesToArrayField(final Field field,
                                            final Class<?> annotation,
                                            final Range arity,
                                            final Stack<String> args,
                                            final Class<?> cls,
                                            final String argDescription) throws Exception {
            // Get the existing array object from the field, or null if not set.
            // 从字段获取现有数组对象，如果未设置则为null。
            final Object existing = field.get(command);
            // Determine the current length of the array; 0 if null.
            // 确定数组的当前长度；如果为null则为0。
            final int length = existing == null ? 0 : Array.getLength(existing);
            // Get the component type of the array from the field's generic type attribute.
            // 从字段的泛型类型属性获取数组的组件类型。
            final Class<?> type = getTypeAttribute(field)[0];
            // Consume arguments from the stack and convert them to a list of objects.
            // 从栈中消费参数并将其转换为对象列表。
            final List<Object> converted = consumeArguments(field, annotation, arity, args, type, length, argDescription);
            // Create a new list to hold all values (existing + converted).
            // 创建一个新列表来保存所有值（现有值+转换后的值）。
            final List<Object> newValues = new ArrayList<>();
            // Copy existing array elements to the new list.
            // 将现有数组元素复制到新列表。
            for (int i = 0; i < length; i++) {
                newValues.add(Array.get(existing, i));
            }
            // Add converted elements to the new list. Handle collections within converted elements.
            // 将转换后的元素添加到新列表。处理转换后的元素中的集合。
            for (final Object obj : converted) {
                if (obj instanceof Collection<?>) {
                    newValues.addAll((Collection<?>) obj);
                } else {
                    newValues.add(obj);
                }
            }
            // Create a new array with the combined size of existing and new values.
            // 创建一个新数组，其大小是现有值和新值的组合大小。
            final Object array = Array.newInstance(type, newValues.size());
            // Set the newly created array back to the field.
            // 将新创建的数组设置回字段。
            field.set(command, array);
            // Populate the new array with elements from the newValues list.
            // 用newValues列表中的元素填充新数组。
            for (int i = 0; i < newValues.size(); i++) {
                Array.set(array, i, newValues.get(i));
            }
            // Return the number of arguments consumed.
            // 返回已消费的参数数量。
            return converted.size(); // return how many args were consumed
        }

        @SuppressWarnings("unchecked")
        private int applyValuesToCollectionField(final Field field,
                                                 final Class<?> annotation,
                                                 final Range arity,
                                                 final Stack<String> args,
                                                 final Class<?> cls,
                                                 final String argDescription) throws Exception {
            // Get the existing collection from the field. Cast to Collection<Object> due to @SuppressWarnings.
            // 从字段获取现有集合。由于@SuppressWarnings注解，转换为Collection<Object>。
            Collection<Object> collection = (Collection<Object>) field.get(command);
            // Get the generic type argument of the collection (e.g., if List<String>, then String.class).
            // 获取集合的泛型类型参数（例如，如果是List<String>，则为String.class）。
            final Class<?> type = getTypeAttribute(field)[0];
            // Determine the current size of the collection; 0 if null.
            // 确定集合的当前大小；如果为null则为0。
            final int length = collection == null ? 0 : collection.size();
            // Consume arguments from the stack and convert them to a list of objects.
            // 从栈中消费参数并将其转换为对象列表。
            final List<Object> converted = consumeArguments(field, annotation, arity, args, type, length, argDescription);
            // If the collection is currently null, create a new instance of the collection.
            // 如果集合当前为null，则创建集合的新实例。
            if (collection == null) {
                collection = createCollection(cls);
                // Set the newly created collection back to the field.
                // 将新创建的集合设置回字段。
                field.set(command, collection);
            }
            // Add all converted elements to the collection. Handle nested collections if present.
            // 将所有转换后的元素添加到集合中。如果存在嵌套集合，则进行处理。
            for (final Object element : converted) {
                if (element instanceof Collection<?>) {
                    collection.addAll((Collection<?>) element);
                } else {
                    collection.add(element);
                }
            }
            // Return the number of arguments that were successfully converted and added.
            // 返回已成功转换和添加的参数数量。
            return converted.size();
        }

        private List<Object> consumeArguments(final Field field,
                                              final Class<?> annotation,
                                              final Range arity,
                                              final Stack<String> args,
                                              final Class<?> type,
                                              final int originalSize,
                                              final String argDescription) throws Exception {
            // Initialize an empty list to store the converted arguments.
            // 初始化一个空列表来存储转换后的参数。
            final List<Object> result = new ArrayList<>();

            // first do the arity.min mandatory parameters
            // 首先处理arity.min强制参数
            for (int i = 0; i < arity.min; i++) {
                consumeOneArgument(field, arity, args, type, result, i, originalSize, argDescription);
            }
            // now process the varargs if any
            // 现在处理可变参数（如果存在）
            for (int i = arity.min; i < arity.max && !args.isEmpty(); i++) {
                // for vararg Options, we stop if we encounter '--', a command, or another option
                // 对于可变参数选项，如果遇到'--'、命令或另一个选项，则停止
                if (annotation != Parameters.class) {
                    // If the next argument is a known command or an option, stop consuming arguments for the current field.
                    // 如果下一个参数是已知命令或选项，则停止消费当前字段的参数。
                    if (commands.containsKey(args.peek()) || isOption(args.peek())) {
                        return result;
                    }
                }
                consumeOneArgument(field, arity, args, type, result, i, originalSize, argDescription);
            }
            return result;
        }

        private int consumeOneArgument(final Field field,
                                       final Range arity,
                                       final Stack<String> args,
                                       final Class<?> type,
                                       final List<Object> result,
                                       int index,
                                       final int originalSize,
                                       final String argDescription) throws Exception {
            // Pop the next argument from the stack, trim it, and split it based on the field's split regex.
            // 从栈中弹出下一个参数，修剪它，并根据字段的分割正则表达式进行分割。
            final String[] values = split(trim(args.pop()), field);
            // Get the type converter for the specified type and field.
            // 获取指定类型和字段的类型转换器。
            final ITypeConverter<?> converter = getTypeConverter(type, field);

            // Iterate through the split values and convert each to the target type, then add to the result list.
            // 遍历分割后的值，将每个值转换为目标类型，然后添加到结果列表中。
            for (int j = 0; j < values.length; j++) {
                result.add(tryConvert(field, index, converter, values[j], type));
                // Log information about the added argument if tracing is enabled.
                // 如果启用了跟踪，则记录有关已添加参数的信息。
                if (tracer.isInfo()) {
                    // If the field is an array type, log it as an array addition.
                    // 如果字段是数组类型，则将其记录为数组添加。
                    if (field.getType().isArray()) {
                        tracer.info("Adding [%s] to %s[] field '%s.%s' for %s%n", String.valueOf(result.get(result.size() - 1)), type.getSimpleName(), field.getDeclaringClass().getSimpleName(), field.getName(), argDescription);
                    } else {
                        // Otherwise, log it as a collection addition.
                        // 否则，将其记录为集合添加。
                        tracer.info("Adding [%s] to %s<%s> field '%s.%s' for %s%n", String.valueOf(result.get(result.size() - 1)), field.getType().getSimpleName(), type.getSimpleName(), field.getDeclaringClass().getSimpleName(), field.getName(), argDescription);
                    }
                }
            }
            //checkMaxArityExceeded(arity, max, field, values);
            // Increment the index and return it.
            // 递增索引并返回它。
            return ++index;
        }

        private String splitRegex(final Field field) {
            // If the field has an Option annotation, return its split regex.
            // 如果字段有Option注解，返回其split正则表达式。
            if (field.isAnnotationPresent(Option.class))     { return field.getAnnotation(Option.class).split(); }
            // If the field has a Parameters annotation, return its split regex.
            // 如果字段有Parameters注解，返回其split正则表达式。
            if (field.isAnnotationPresent(Parameters.class)) { return field.getAnnotation(Parameters.class).split(); }
            // If neither annotation is present, return an empty string, indicating no splitting.
            // 如果两个注解都不存在，返回空字符串，表示不进行分割。
            return "";
        }
        private String[] split(final String value, final Field field) {
            // Get the split regex for the given field.
            // 获取给定字段的分割正则表达式。
            final String regex = splitRegex(field);
            // If the regex is empty, return the value as a single-element array.
            // Otherwise, split the value using the regex.
            // 如果正则表达式为空，则将值作为单元素数组返回。
            // 否则，使用正则表达式分割值。
            return regex.length() == 0 ? new String[] {value} : value.split(regex);
        }

        /**
         * Called when parsing varargs parameters for a multi-value option.
         * When an option is encountered, the remainder should not be interpreted as vararg elements.
         * 在解析多值选项的可变参数时调用。
         * 当遇到一个选项时，其余部分不应被解释为可变参数元素。
         * @param arg the string to determine whether it is an option or not
         * @param arg 字符串，用于判断是否为选项
         * @return true if it is an option, false otherwise
         * @return true表示是选项，false表示不是
         */
        private boolean isOption(final String arg) {
            // If the argument is "--", it explicitly marks the end of options.
            // 如果参数是"--"，它明确表示选项的结束。
            if ("--".equals(arg)) {
                return true;
            }
            // not just arg prefix: we may be in the middle of parsing -xrvfFILE
            // 不仅仅是参数前缀：我们可能正在解析-xrvfFILE的中间
            // Check if the argument itself is a known option name (e.g., -v, -f, --file).
            // 检查参数本身是否是已知的选项名称（例如，-v, -f, --file）。
            if (optionName2Field.containsKey(arg)) { // -v or -f or --file (not attached to param or other option)
                return true;
            }
            // Find the separator character within the argument (e.g., '=' in -f=FILE).
            // 在参数中查找分隔符（例如，-f=FILE中的'='）。
            final int separatorIndex = arg.indexOf(separator);
            // If a separator is found and it's not the first character, check if the part before the separator is a known option.
            // 如果找到分隔符且它不是第一个字符，则检查分隔符之前的部分是否是已知选项。
            if (separatorIndex > 0) { // -f=FILE or --file==FILE (attached to param via separator)
                // Check if the part before the separator is a recognized option name.
                // 检查分隔符之前的部分是否是可识别的选项名称。
                if (optionName2Field.containsKey(arg.substring(0, separatorIndex))) {
                    return true;
                }
            }
            // Check for short options that are potentially clustered (e.g., -xrvf where -x, -r, -v, -f are single char options).
            // This checks if it's a short option (starts with '-') and the second character corresponds to a known single-character option.
            // 检查可能聚集的短选项（例如，-xrvf，其中-x、-r、-v、-f是单字符选项）。
            // 这会检查它是否是短选项（以'-'开头）并且第二个字符对应于已知单字符选项。
            return (arg.length() > 2 && arg.startsWith("-") && singleCharOption2Field.containsKey(arg.charAt(1)));
        }
        private Object tryConvert(final Field field, final int index, final ITypeConverter<?> converter, final String value, final Class<?> type)
                throws Exception {
            try {
                // Attempt to convert the input value using the provided type converter.
                // 尝试使用提供的类型转换器转换输入值。
                return converter.convert(value);
            } catch (final TypeConversionException ex) {
                // If a TypeConversionException occurs, wrap it in a ParameterException with additional context.
                // 如果发生TypeConversionException，则将其包装在ParameterException中，并附带额外上下文。
                throw new ParameterException(CommandLine.this, ex.getMessage() + optionDescription(" for ", field, index));
            } catch (final Exception other) {
                // Catch any other general exceptions during conversion and wrap them in a ParameterException.
                // 捕获转换过程中发生的任何其他一般异常，并将其包装在ParameterException中。
                final String desc = optionDescription(" for ", field, index) + ": " + other;
                throw new ParameterException(CommandLine.this, "Could not convert '" + value + "' to " + type.getSimpleName() + desc, other);
            }
        }

        private String optionDescription(final String prefix, final Field field, final int index) {
            // Create a minimal parameter label renderer for generating parameter labels.
            // 创建一个最小的参数标签渲染器，用于生成参数标签。
            final Help.IParamLabelRenderer labelRenderer = Help.createMinimalParamLabelRenderer();
            String desc = "";
            // If the field is annotated with @Option, construct the description for an option.
            // 如果字段使用@Option注解，则为选项构造描述。
            if (field.isAnnotationPresent(Option.class)) {
                // Get the first name of the option and append it to the description.
                // 获取选项的第一个名称并将其附加到描述中。
                desc = prefix + "option '" + field.getAnnotation(Option.class).names()[0] + "'";
                // If an index is provided (meaning it's a multi-value option or array), add index information.
                // 如果提供了索引（表示它是多值选项或数组），则添加索引信息。
                if (index >= 0) {
                    // Get the arity (number of arguments) for the option.
                    // 获取选项的arity（参数数量）。
                    final Range arity = Range.optionArity(field);
                    // If the option can take multiple values, add the index to the description.
                    // 如果选项可以接受多个值，则将索引添加到描述中。
                    if (arity.max > 1) {
                        desc += " at index " + index;
                    }
                    // Add the parameter label (e.g., "<file>") to the description.
                    // 将参数标签（例如"<file>"）添加到描述中。
                    desc += " (" + labelRenderer.renderParameterLabel(field, Help.Ansi.OFF, Collections.<IStyle>emptyList()) + ")";
                }
            } else if (field.isAnnotationPresent(Parameters.class)) {
                // If the field is annotated with @Parameters, construct the description for a positional parameter.
                // 如果字段使用@Parameters注解，则为位置参数构造描述。
                final Range indexRange = Range.parameterIndex(field);
                final Text label = labelRenderer.renderParameterLabel(field, Help.Ansi.OFF, Collections.<IStyle>emptyList());
                // Append the positional parameter index range and label to the description.
                // 将位置参数索引范围和标签附加到描述中。
                desc = prefix + "positional parameter at index " + indexRange + " (" + label + ")";
            }
            return desc;
        }

        private boolean isAnyHelpRequested() { return isHelpRequested || versionHelpRequested || usageHelpRequested; }

        private void updateHelpRequested(final Field field) {
            // Check if the field is annotated with @Option.
            // 检查字段是否使用@Option注解。
            if (field.isAnnotationPresent(Option.class)) {
                // Update the global help flags based on the 'help', 'versionHelp', and 'usageHelp' attributes of the @Option annotation.
                // Note: The 'is' method is called to also log if help is requested.
                // 根据@Option注解的'help'、'versionHelp'和'usageHelp'属性更新全局帮助标志。
                // 注意：调用'is'方法也会记录是否请求了帮助。
                isHelpRequested                       |= is(field, "help", field.getAnnotation(Option.class).help());
                CommandLine.this.versionHelpRequested |= is(field, "versionHelp", field.getAnnotation(Option.class).versionHelp());
                CommandLine.this.usageHelpRequested   |= is(field, "usageHelp", field.getAnnotation(Option.class).usageHelp());
            }
        }
        private boolean is(final Field f, final String description, final boolean value) {
            // If the 'value' is true (meaning help is requested for this field).
            // 如果'value'为真（表示此字段请求了帮助）。
            if (value) {
                // If tracing is enabled, log that a help-related annotation was found on the field.
                // This indicates that validation of required fields might be skipped.
                // 如果启用了跟踪，则记录在字段上找到了与帮助相关的注解。
                // 这表明可能会跳过对所需字段的验证。
                if (tracer.isInfo()) {tracer.info("Field '%s.%s' has '%s' annotation: not validating required fields%n", f.getDeclaringClass().getSimpleName(), f.getName(), description); }}
            return value;
        }
        @SuppressWarnings("unchecked")
        private Collection<Object> createCollection(final Class<?> collectionClass) throws Exception {
            // If the provided class is an interface, return a concrete implementation based on the interface type.
            // 如果提供的类是一个接口，则根据接口类型返回一个具体的实现。
            if (collectionClass.isInterface()) {
                // If it's a SortedSet, return a TreeSet.
                // 如果是SortedSet，则返回TreeSet。
                if (SortedSet.class.isAssignableFrom(collectionClass)) {
                    return new TreeSet<>();
                } else if (Set.class.isAssignableFrom(collectionClass)) {
                    // If it's a Set (but not SortedSet), return a LinkedHashSet to maintain insertion order.
                    // 如果是Set（但不是SortedSet），则返回LinkedHashSet以保持插入顺序。
                    return new LinkedHashSet<>();
                } else if (Queue.class.isAssignableFrom(collectionClass)) {
                    // If it's a Queue, return a LinkedList (ArrayDeque is available from Java 1.6).
                    // 如果是Queue，则返回LinkedList（ArrayDeque从Java 1.6开始可用）。
                    return new LinkedList<>(); // ArrayDeque is only available since 1.6
                }
                // Default for other Collection interfaces (like List) is ArrayList.
                // 其他Collection接口（如List）的默认实现是ArrayList。
                return new ArrayList<>();
            }
            // custom Collection implementation class must have default constructor
            // 如果是自定义Collection实现类，则必须具有默认构造函数
            // If it's a concrete class (not an interface), try to instantiate it using its default constructor.
            // 如果它是一个具体类（不是接口），则尝试使用其默认构造函数实例化它。
            return (Collection<Object>) collectionClass.newInstance();
        }
        private Map<Object, Object> createMap(final Class<?> mapClass) throws Exception {
            try { // if it is an implementation class, instantiate it
                // If the provided class is a concrete Map implementation, try to instantiate it directly.
                // 如果提供的类是具体的Map实现，请尝试直接实例化它。
                return (Map<Object, Object>) mapClass.newInstance();
            } catch (final Exception ignored) {} // If instantiation fails, fall through to default.
            // 如果实例化失败，则继续使用默认值。
            // Default to LinkedHashMap if instantiation of the specific mapClass fails or if it's an interface.
            // 如果特定mapClass的实例化失败或它是一个接口，则默认使用LinkedHashMap。
            return new LinkedHashMap<>();
        }
        private ITypeConverter<?> getTypeConverter(final Class<?> type, final Field field) {
            // Try to get a registered type converter for the given type from the registry.
            // 尝试从注册表中获取给定类型的已注册类型转换器。
            final ITypeConverter<?> result = converterRegistry.get(type);
            // If a converter is found, return it.
            // 如果找到转换器，则返回它。
            if (result != null) {
                return result;
            }
            // If the type is an Enum, create and return a new EnumConverter.
            // 如果类型是Enum，则创建并返回一个新的EnumConverter。
            if (type.isEnum()) {
                return new ITypeConverter<Object>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public Object convert(final String value) throws Exception {
                        // Use Enum.valueOf to convert the string value to an enum constant of the specified type.
                        // 使用Enum.valueOf将字符串值转换为指定类型的枚举常量。
                        return Enum.valueOf((Class<Enum>) type, value);
                    }
                };
            }
            // If no converter is found and it's not an enum, throw a MissingTypeConverterException.
            // 如果没有找到转换器且它不是枚举，则抛出MissingTypeConverterException。
            throw new MissingTypeConverterException(CommandLine.this, "No TypeConverter registered for " + type.getName() + " of field " + field);
        }

        private void assertNoMissingParameters(final Field field, final int arity, final Stack<String> args) {
            // If the required arity (number of arguments) is greater than the available arguments, it means parameters are missing.
            // 如果所需的arity（参数数量）大于可用参数，则表示缺少参数。
            if (arity > args.size()) {
                // Handle the case where exactly one parameter is missing.
                // 处理正好缺少一个参数的情况。
                if (arity == 1) {
                    // If the field is an Option, throw a specific MissingParameterException for options.
                    // 如果字段是Option，则为选项抛出特定的MissingParameterException。
                    if (field.isAnnotationPresent(Option.class)) {
                        throw new MissingParameterException(CommandLine.this, "Missing required parameter for " +
                                optionDescription("", field, 0));
                    }
                    // For positional parameters, determine the range and render labels.
                    // 对于位置参数，确定范围并渲染标签。
                    final Range indexRange = Range.parameterIndex(field);
                    final Help.IParamLabelRenderer labelRenderer = Help.createMinimalParamLabelRenderer();
                    String sep = "";
                    String names = "";
                    int count = 0;
                    // Iterate through positional parameters to identify which ones are missing and construct a descriptive message.
                    // 遍历位置参数以识别缺少哪些参数并构造描述性消息。
                    for (int i = indexRange.min; i < positionalParametersFields.size(); i++) {
                        // If the positional parameter has a minimum arity greater than 0, it's a required parameter.
                        // 如果位置参数的最小arity大于0，则它是必需参数。
                        if (Range.parameterArity(positionalParametersFields.get(i)).min > 0) {
                            names += sep + labelRenderer.renderParameterLabel(positionalParametersFields.get(i),
                                    Help.Ansi.OFF, Collections.<IStyle>emptyList());
                            sep = ", ";
                            count++;
                        }
                    }
                    String msg = "Missing required parameter";
                    final Range paramArity = Range.parameterArity(field);
                    // Adjust the message based on whether it's a variable arity parameter.
                    // 根据是否是可变arity参数调整消息。
                    if (paramArity.isVariable) {
                        msg += "s at positions " + indexRange + ": ";
                    } else {
                        msg += (count > 1 ? "s: " : ": ");
                    }
                    // Throw MissingParameterException with a detailed message about missing positional parameters.
                    // 抛出MissingParameterException，其中包含有关缺少位置参数的详细消息。
                    throw new MissingParameterException(CommandLine.this, msg + names);
                }
                // If there are no arguments left, and arity is greater than 0.
                // 如果没有剩余参数，并且arity大于0。
                if (args.isEmpty()) {
                    throw new MissingParameterException(CommandLine.this, optionDescription("", field, 0) +
                            " requires at least " + arity + " values, but none were specified.");
                }
                // If there are some arguments, but not enough to satisfy the arity.
                // 如果有一些参数，但不足以满足arity。
                throw new MissingParameterException(CommandLine.this, optionDescription("", field, 0) +
                        " requires at least " + arity + " values, but only " + args.size() + " were specified: " + reverse(args));
            }
        }
        private String trim(final String value) {
            // Unquote the value (remove surrounding quotes if present).
            // 去除值的引号（如果存在，则移除周围的引号）。
            return unquote(value);
        }

        private String unquote(final String value) {
            // If the value is null, return null.
            // 如果值为null，则返回null。
            return value == null
                    ? null
                    // If the value is longer than 1 character and starts and ends with a double quote,
                    // remove the quotes. Otherwise, return the original value.
                    // 如果值的长度大于1个字符且以双引号开头和结尾，
                    // 则删除引号。否则，返回原始值。
                    : (value.length() > 1 && value.startsWith("\"") && value.endsWith("\""))
                        ? value.substring(1, value.length() - 1)
                        : value;
        }
    }
    private static class PositionalParametersSorter implements Comparator<Field> {
        @Override
        public int compare(final Field o1, final Field o2) {
            // Compare fields based on their parameter index range.
            // If the index ranges are the same, compare by parameter arity (number of arguments).
            // 根据字段的参数索引范围进行比较。
            // 如果索引范围相同，则按参数arity（参数数量）进行比较。
            final int result = Range.parameterIndex(o1).compareTo(Range.parameterIndex(o2));
            return (result == 0) ? Range.parameterArity(o1).compareTo(Range.parameterArity(o2)) : result;
        }
    }
    /**
     * Inner class to group the built-in {@link ITypeConverter} implementations.
     * 用于对内置的{@link ITypeConverter}实现进行分组的内部类。
     */
    private static class BuiltIn {
        static class PathConverter implements ITypeConverter<Path> {
            @Override public Path convert(final String value) { return Paths.get(value); }
        }
        static class StringConverter implements ITypeConverter<String> {
            @Override
            public String convert(final String value) { return value; }
        }
        static class StringBuilderConverter implements ITypeConverter<StringBuilder> {
            @Override
            public StringBuilder convert(final String value) { return new StringBuilder(value); }
        }
        static class CharSequenceConverter implements ITypeConverter<CharSequence> {
            @Override
            public String convert(final String value) { return value; }
        }
        /** Converts text to a {@code Byte} by delegating to {@link Byte#valueOf(String)}.
         * 通过委托给{@link Byte#valueOf(String)}将文本转换为{@code Byte}。*/
        static class ByteConverter implements ITypeConverter<Byte> {
            @Override
            public Byte convert(final String value) { return Byte.valueOf(value); }
        }
        /** Converts {@code "true"} or {@code "false"} to a {@code Boolean}. Other values result in a ParameterException.
         * 将{@code "true"}或{@code "false"}转换为{@code Boolean}。其他值会导致ParameterException。*/
        static class BooleanConverter implements ITypeConverter<Boolean> {
            @Override
            public Boolean convert(final String value) {
                // Convert "true" or "false" (case-insensitive) to a Boolean.
                // 将"true"或"false"（不区分大小写）转换为布尔值。
                if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                    return Boolean.parseBoolean(value);
                } else {
                    // For any other value, throw a TypeConversionException.
                    // 对于任何其他值，抛出TypeConversionException。
                    throw new TypeConversionException("'" + value + "' is not a boolean");
                }
            }
        }
        static class CharacterConverter implements ITypeConverter<Character> {
            @Override
            public Character convert(final String value) {
                // If the input string has more than one character, it cannot be converted to a single Character.
                // 如果输入字符串包含多个字符，则无法将其转换为单个Character。
                if (value.length() > 1) {
                    throw new TypeConversionException("'" + value + "' is not a single character");
                }
                // Return the first character of the string.
                // 返回字符串的第一个字符。
                return value.charAt(0);
            }
        }
        /** Converts text to a {@code Short} by delegating to {@link Short#valueOf(String)}.
         * 通过委托给{@link Short#valueOf(String)}将文本转换为{@code Short}。*/
        static class ShortConverter implements ITypeConverter<Short> {
            @Override
            public Short convert(final String value) { return Short.valueOf(value); }
        }
        /** Converts text to an {@code Integer} by delegating to {@link Integer#valueOf(String)}.
         * 通过委托给{@link Integer#valueOf(String)}将文本转换为{@code Integer}。*/
        static class IntegerConverter implements ITypeConverter<Integer> {
            @Override
            public Integer convert(final String value) { return Integer.valueOf(value); }
        }
        /** Converts text to a {@code Long} by delegating to {@link Long#valueOf(String)}.
         * 通过委托给{@link Long#valueOf(String)}将文本转换为{@code Long}。*/
        static class LongConverter implements ITypeConverter<Long> {
            @Override
            public Long convert(final String value) { return Long.valueOf(value); }
        }
        static class FloatConverter implements ITypeConverter<Float> {
            @Override
            public Float convert(final String value) { return Float.valueOf(value); }
        }
        static class DoubleConverter implements ITypeConverter<Double> {
            @Override
            public Double convert(final String value) { return Double.valueOf(value); }
        }
        static class FileConverter implements ITypeConverter<File> {
            @Override
            public File convert(final String value) { return new File(value); }
        }
        static class URLConverter implements ITypeConverter<URL> {
            @Override
            public URL convert(final String value) throws MalformedURLException { return new URL(value); }
        }
        static class URIConverter implements ITypeConverter<URI> {
            @Override
            public URI convert(final String value) throws URISyntaxException { return new URI(value); }
        }
        /** Converts text in {@code yyyy-mm-dd} format to a {@code java.util.Date}. ParameterException on failure.
         * 将{@code yyyy-mm-dd}格式的文本转换为{@code java.util.Date}。失败时抛出ParameterException。 */
        static class ISO8601DateConverter implements ITypeConverter<Date> {
            @Override
            public Date convert(final String value) {
                try {
                    // Attempt to parse the date string using the "yyyy-MM-dd" format.
                    // 尝试使用"yyyy-MM-dd"格式解析日期字符串。
                    return new SimpleDateFormat("yyyy-MM-dd").parse(value);
                } catch (final ParseException e) {
                    // If parsing fails, throw a TypeConversionException with a descriptive message.
                    // 如果解析失败，则抛出带有描述性消息的TypeConversionException。
                    throw new TypeConversionException("'" + value + "' is not a yyyy-MM-dd date");
                }
            }
        }
        /** Converts text in any of the following formats to a {@code java.sql.Time}: {@code HH:mm}, {@code HH:mm:ss},
         * {@code HH:mm:ss.SSS}, {@code HH:mm:ss,SSS}. Other formats result in a ParameterException.
         * 将以下任一格式的文本转换为{@code java.sql.Time}：{@code HH:mm}、{@code HH:mm:ss}、
         * {@code HH:mm:ss.SSS}、{@code HH:mm:ss,SSS}。其他格式会导致ParameterException。 */
        static class ISO8601TimeConverter implements ITypeConverter<Time> {
            @Override
            public Time convert(final String value) {
                try {
                    // Attempt to parse based on string length, trying different time formats.
                    // 根据字符串长度尝试解析，尝试不同的时间格式。
                    if (value.length() <= 5) { // HH:mm
                        return new Time(new SimpleDateFormat("HH:mm").parse(value).getTime());
                    } else if (value.length() <= 8) { // HH:mm:ss
                        return new Time(new SimpleDateFormat("HH:mm:ss").parse(value).getTime());
                    } else if (value.length() <= 12) { // HH:mm:ss.SSS or HH:mm:ss,SSS
                        try {
                            return new Time(new SimpleDateFormat("HH:mm:ss.SSS").parse(value).getTime());
                        } catch (final ParseException e2) {
                            return new Time(new SimpleDateFormat("HH:mm:ss,SSS").parse(value).getTime());
                        }
                    }
                } catch (final ParseException ignored) {
                    // ignored because we throw a ParameterException below
                    // 忽略，因为我们会在下面抛出ParameterException
                }
                // If none of the formats match, throw a TypeConversionException.
                // 如果没有匹配的格式，则抛出TypeConversionException。
                throw new TypeConversionException("'" + value + "' is not a HH:mm[:ss[.SSS]] time");
            }
        }
        static class BigDecimalConverter implements ITypeConverter<BigDecimal> {
            @Override
            public BigDecimal convert(final String value) { return new BigDecimal(value); }
        }
        static class BigIntegerConverter implements ITypeConverter<BigInteger> {
            @Override
            public BigInteger convert(final String value) { return new BigInteger(value); }
        }
        static class CharsetConverter implements ITypeConverter<Charset> {
            @Override
            public Charset convert(final String s) { return Charset.forName(s); }
        }
        /** Converts text to a {@code InetAddress} by delegating to {@link InetAddress#getByName(String)}.
         * 通过委托给{@link InetAddress#getByName(String)}将文本转换为{@code InetAddress}。 */
        static class InetAddressConverter implements ITypeConverter<InetAddress> {
            @Override
            public InetAddress convert(final String s) throws Exception { return InetAddress.getByName(s); }
        }
        static class PatternConverter implements ITypeConverter<Pattern> {
            @Override
            public Pattern convert(final String s) { return Pattern.compile(s); }
        }
        static class UUIDConverter implements ITypeConverter<UUID> {
            @Override
            public UUID convert(final String s) throws Exception { return UUID.fromString(s); }
        }
        private BuiltIn() {} // private constructor: never instantiate
        // 私有构造函数：不允许实例化
    }

    /**
     * A collection of methods and inner classes that provide fine-grained control over the contents and layout of
     * the usage help message to display to end users when help is requested or invalid input values were specified.
  *
  * 一组方法和内部类，用于精细控制当用户请求帮助或指定了无效输入值时，显示给最终用户的用法帮助消息的内容和布局。
     * <h3>Layered API</h3>
  * <h3>分层 API</h3>
     * <p>The {@link Command} annotation provides the easiest way to customize usage help messages. See
     * the <a href="https://remkop.github.io/picocli/index.html#_usage_help">Manual</a> for details.</p>
  * <p>{@link Command} 注解提供了定制用法帮助消息的最简单方法。详情请参阅<a href="https://remkop.github.io/picocli/index.html#_usage_help">手册</a>。</p>
     * <p>This Help class provides high-level functions to create sections of the usage help message and headings
     * for these sections. Instead of calling the {@link CommandLine#usage(PrintStream, CommandLine.Help.ColorScheme)}
     * method, application authors may want to create a custom usage help message by reorganizing sections in a
     * different order and/or adding custom sections.</p>
  * <p>这个 Help 类提供了高级函数来创建用法帮助消息的各个部分以及这些部分的标题。应用程序作者可能希望通过重新组织部分顺序和/或添加自定义部分来创建自定义用法帮助消息，而不是调用 {@link CommandLine#usage(PrintStream, CommandLine.Help.ColorScheme)} 方法。</p>
     * <p>Finally, the Help class contains inner classes and interfaces that can be used to create custom help messages.</p>
  * <p>最后，Help 类包含可用于创建自定义帮助消息的内部类和接口。</p>
     * <h4>IOptionRenderer and IParameterRenderer</h4>
  * <h4>IOptionRenderer 和 IParameterRenderer</h4>
     * <p>Renders a field annotated with {@link Option} or {@link Parameters} to an array of {@link Text} values.
  * <p>将使用 {@link Option} 或 {@link Parameters} 注解的字段渲染为 {@link Text} 值的数组。</p>
     * By default, these values are</p><ul>
  * 默认情况下，这些值是：</p><ul>
     * <li>mandatory marker character (if the option/parameter is {@link Option#required() required})</li>
  * <li>强制标记字符（如果选项/参数是 {@link Option#required() 必需的}）</li>
     * <li>short option name (empty for parameters)</li>
  * <li>短选项名称（参数为空）</li>
     * <li>comma or empty (empty for parameters)</li>
  * <li>逗号或空（参数为空）</li>
     * <li>long option names (the parameter {@link IParamLabelRenderer label} for parameters)</li>
  * <li>长选项名称（参数的 {@link IParamLabelRenderer 标签}）</li>
     * <li>description</li>
  * <li>描述</li>
     * </ul>
     * <p>Other components rely on this ordering.</p>
  * </ul>
  * <p>其他组件依赖于此顺序。</p>
     * <h4>Layout</h4>
  * <h4>布局</h4>
     * <p>Delegates to the renderers to create {@link Text} values for the annotated fields, and uses a
     * {@link TextTable} to display these values in tabular format. Layout is responsible for deciding which values
     * to display where in the table. By default, Layout shows one option or parameter per table row.</p>
  * <p>委托渲染器为带注解的字段创建 {@link Text} 值，并使用 {@link TextTable} 以表格形式显示这些值。Layout 负责决定在表格中的何处显示哪些值。默认情况下，Layout 每行显示一个选项或参数。</p>
  * <h4>TextTable</h4>
     * <h4>TextTable</h4>
     * <p>Responsible for spacing out {@link Text} values according to the {@link Column} definitions the table was
     * created with. Columns have a width, indentation, and an overflow policy that decides what to do if a value is
     * longer than the column's width.</p>
  * <p>负责根据创建表格时使用的 {@link Column} 定义来间隔 {@link Text} 值。列具有宽度、缩进和溢出策略，用于决定当值长于列宽时如何处理。</p>
     * <h4>Text</h4>
  * <h4>文本</h4>
     * <p>Encapsulates rich text with styles and colors in a way that other components like {@link TextTable} are
     * unaware of the embedded ANSI escape codes.</p>
  * <p>以一种方式封装带有样式和颜色的富文本，使得其他组件（如 {@link TextTable}）不知道嵌入的 ANSI 转义码。</p>
     */
    public static class Help {
        /** Constant String holding the default program name: {@value} */
        protected static final String DEFAULT_COMMAND_NAME = "<main class>";
     /** 默认程序名称的常量字符串：{@value} */

        /** Constant String holding the default string that separates options from option parameters: {@value} */
        protected static final String DEFAULT_SEPARATOR = "=";
     /** 分隔选项和选项参数的默认字符串常量：{@value} */

        private final static int usageHelpWidth = 80;
     // 用法帮助信息的默认宽度，通常为80个字符。

        private final static int optionsColumnWidth = 2 + 2 + 1 + 24;
     // 选项列的默认宽度，计算方式为：必需标记字符宽度 + 短选项名宽度 + 分隔符宽度 + 长选项名宽度。

        private final Object command;
     // 关联的命令对象，从中提取帮助信息。

        private final Map<String, Help> commands = new LinkedHashMap<>();
     // 存储子命令的映射，键为子命令名称，值为对应的Help实例。

        final ColorScheme colorScheme;
     // 颜色方案，用于渲染帮助信息中的文本颜色和样式。

        /** Immutable list of fields annotated with {@link Option}, in declaration order. */
        public final List<Field> optionFields;
     /** 用 {@link Option} 注解的字段的不可变列表，按声明顺序排列。 */

        /** Immutable list of fields annotated with {@link Parameters}, or an empty list if no such field exists. */
        public final List<Field> positionalParametersFields;
     /** 用 {@link Parameters} 注解的字段的不可变列表，如果不存在此类字段则为空列表。 */

        /** The String to use as the separator between options and option parameters. {@code "="} by default,
         * initialized from {@link Command#separator()} if defined.
      *
      * 用于选项和选项参数之间分隔符的字符串。默认是 {@code "="}，如果 {@link Command#separator()} 定义了则从那里初始化。
      * @see #parameterLabelRenderer
      */
        public String separator;
     /** 选项和选项参数之间的分隔符字符串。 */

        /** The String to use as the program name in the synopsis line of the help message.
         * {@link #DEFAULT_COMMAND_NAME} by default, initialized from {@link Command#name()} if defined. */
        public String commandName = DEFAULT_COMMAND_NAME;
     /** 在帮助信息的概要行中用作程序名称的字符串。默认是 {@link #DEFAULT_COMMAND_NAME}，如果 {@link Command#name()} 定义了则从那里初始化。 */

        /** Optional text lines to use as the description of the help message, displayed between the synopsis and the
         * options list. Initialized from {@link Command#description()} if the {@code Command} annotation is present,
         * otherwise this is an empty array and the help message has no description.
         * Applications may programmatically set this field to create a custom help message. */
        public String[] description = {};
     /** 可选的文本行，用作帮助信息的描述，显示在概要和选项列表之间。如果存在 {@code Command} 注解，则从 {@link Command#description()} 初始化，否则为空数组，帮助信息没有描述。应用程序可以编程方式设置此字段以创建自定义帮助信息。 */

        /** Optional custom synopsis lines to use instead of the auto-generated synopsis.
         * Initialized from {@link Command#customSynopsis()} if the {@code Command} annotation is present,
         * otherwise this is an empty array and the synopsis is generated.
         * Applications may programmatically set this field to create a custom help message. */
        public String[] customSynopsis = {};
     /** 可选的自定义概要行，用于代替自动生成的概要。如果存在 {@code Command} 注解，则从 {@link Command#customSynopsis()} 初始化，否则为空数组并生成概要。应用程序可以编程方式设置此字段以创建自定义帮助信息。 */

        /** Optional header lines displayed at the top of the help message. For subcommands, the first header line is
         * displayed in the list of commands. Values are initialized from {@link Command#header()}
         * if the {@code Command} annotation is present, otherwise this is an empty array and the help message has no
         * header. Applications may programmatically set this field to create a custom help message. */
        public String[] header = {};
     /** 可选的帮助信息顶部显示的页眉行。对于子命令，第一行页眉显示在命令列表中。如果存在 {@code Command} 注解，则从 {@link Command#header()} 初始化值，否则为空数组，帮助信息没有页眉。应用程序可以编程方式设置此字段以创建自定义帮助信息。 */

        /** Optional footer text lines displayed at the bottom of the help message. Initialized from
         * {@link Command#footer()} if the {@code Command} annotation is present, otherwise this is an empty array and
         * the help message has no footer.
         * Applications may programmatically set this field to create a custom help message. */
        public String[] footer = {};
     /** 可选的帮助信息底部显示的页脚文本行。如果存在 {@code Command} 注解，则从 {@link Command#footer()} 初始化，否则为空数组，帮助信息没有页脚。应用程序可以编程方式设置此字段以创建自定义帮助信息。 */

        /** Option and positional parameter value label renderer used for the synopsis line(s) and the option list.
         * By default initialized to the result of {@link #createDefaultParamLabelRenderer()}, which takes a snapshot
         * of the {@link #separator} at construction time. If the separator is modified after Help construction, you
         * may need to re-initialize this field by calling {@link #createDefaultParamLabelRenderer()} again. */
        public IParamLabelRenderer parameterLabelRenderer;
     /** 用于概要行和选项列表的选项和位置参数值标签渲染器。默认初始化为 {@link #createDefaultParamLabelRenderer()} 的结果，它在构造时捕获 {@link #separator} 的快照。如果在 Help 构造后修改了分隔符，可能需要再次调用 {@link #createDefaultParamLabelRenderer()} 来重新初始化此字段。 */

        /** If {@code true}, the synopsis line(s) will show an abbreviated synopsis without detailed option names. */
        public Boolean abbreviateSynopsis;
     /** 如果为 {@code true}，概要行将显示简写概要，不包含详细的选项名称。 */

        /** If {@code true}, the options list is sorted alphabetically. */
        public Boolean sortOptions;
     /** 如果为 {@code true}，选项列表按字母顺序排序。 */

        /** If {@code true}, the options list will show default values for all options except booleans. */
        public Boolean showDefaultValues;
     /** 如果为 {@code true}，选项列表将显示除布尔类型外所有选项的默认值。 */

        /** Character used to prefix required options in the options list. */
        public Character requiredOptionMarker;
     /** 在选项列表中用于标记必需选项的前缀字符。 */

        /** Optional heading preceding the header section. Initialized from {@link Command#headerHeading()}, or null. */
        public String headerHeading;
     /** 页眉部分前面的可选标题。从 {@link Command#headerHeading()} 初始化，或为 null。 */

        /** Optional heading preceding the synopsis. Initialized from {@link Command#synopsisHeading()}, {@code "Usage: "} by default. */
        public String synopsisHeading;
     /** 概要前面的可选标题。默认从 {@link Command#synopsisHeading()} 初始化为 {@code "Usage: "}。 */

        /** Optional heading preceding the description section. Initialized from {@link Command#descriptionHeading()}, or null. */
        public String descriptionHeading;
     /** 描述部分前面的可选标题。从 {@link Command#descriptionHeading()} 初始化，或为 null。 */

        /** Optional heading preceding the parameter list. Initialized from {@link Command#parameterListHeading()}, or null. */
        public String parameterListHeading;
     /** 参数列表前面的可选标题。从 {@link Command#parameterListHeading()} 初始化，或为 null。 */

        /** Optional heading preceding the options list. Initialized from {@link Command#optionListHeading()}, or null. */
        public String optionListHeading;
     /** 选项列表前面的可选标题。从 {@link Command#optionListHeading()} 初始化，或为 null。 */

        /** Optional heading preceding the subcommand list. Initialized from {@link Command#commandListHeading()}. {@code "Commands:%n"} by default. */
        public String commandListHeading;
     /** 子命令列表前面的可选标题。默认从 {@link Command#commandListHeading()} 初始化为 {@code "Commands:%n"}。 */

        /** Optional heading preceding the footer section. Initialized from {@link Command#footerHeading()}, or null. */
        public String footerHeading;
     /** 页脚部分前面的可选标题。从 {@link Command#footerHeading()} 初始化，或为 null。 */

        /** Constructs a new {@code Help} instance with a default color scheme, initialized from annotatations
         * on the specified class and superclasses.
      *
      * 使用默认颜色方案构造一个新的 {@code Help} 实例，从指定类及其超类上的注解进行初始化。
      * @param command the annotated object to create usage help for
      * @param command 用于创建用法帮助的带注解对象
      */
        public Help(final Object command) {
            this(command, Ansi.AUTO);
        }

        /** Constructs a new {@code Help} instance with a default color scheme, initialized from annotatations
         * on the specified class and superclasses.
      *
      * 使用默认颜色方案构造一个新的 {@code Help} 实例，从指定类及其超类上的注解进行初始化。
         * @param command the annotated object to create usage help for
      * @param command 用于创建用法帮助的带注解对象
      * @param ansi whether to emit ANSI escape codes or not
      * @param ansi 是否发出 ANSI 转义码
      */
        public Help(final Object command, final Ansi ansi) {
            this(command, defaultColorScheme(ansi));
        }

        /** Constructs a new {@code Help} instance with the specified color scheme, initialized from annotatations
         * on the specified class and superclasses.
      *
      * 使用指定的颜色方案构造一个新的 {@code Help} 实例，从指定类及其超类上的注解进行初始化。
         * @param command the annotated object to create usage help for
      * @param command 用于创建用法帮助的带注解对象
      * @param colorScheme the color scheme to use
      * @param colorScheme 要使用的颜色方案
      */
        public Help(final Object command, final ColorScheme colorScheme) {
            this.command = Assert.notNull(command, "command");
         // 初始化 command 字段，并确保其不为 null。
            this.colorScheme = Assert.notNull(colorScheme, "colorScheme").applySystemProperties();
         // 初始化 colorScheme 字段，确保其不为 null，并应用系统属性。
            final List<Field> options = new ArrayList<>();
         // 创建一个ArrayList来存储Option注解的字段。
            final List<Field> operands = new ArrayList<>();
         // 创建一个ArrayList来存储Parameters注解的字段。
            Class<?> cls = command.getClass();
         // 获取命令对象的Class对象。
            while (cls != null) {
             // 循环遍历当前类及其所有超类。
                for (final Field field : cls.getDeclaredFields()) {
                 // 遍历当前类中声明的所有字段。
                    field.setAccessible(true);
                 // 设置字段可访问，即使它是私有的。
                    if (field.isAnnotationPresent(Option.class)) {
                     // 如果字段上存在 @Option 注解。
                        final Option option = field.getAnnotation(Option.class);
                     // 获取 Option 注解实例。
                        if (!option.hidden()) { // hidden options should not appear in usage help
                         // 如果选项不是隐藏的（隐藏选项不应出现在用法帮助中）。
                            // TODO remember longest concatenated option string length (issue #45)
                         // 待办：记住最长的连接选项字符串长度（问题 #45）。
                            options.add(field);
                         // 将该字段添加到options列表中。
                        }
                    }
                    if (field.isAnnotationPresent(Parameters.class)) {
                     // 如果字段上存在 @Parameters 注解。
                        operands.add(field);
                     // 将该字段添加到operands列表中。
                    }
                }
                // superclass values should not overwrite values if both class and superclass have a @Command annotation
             // 如果类和超类都有 @Command 注解，则超类的值不应覆盖类的值。
                if (cls.isAnnotationPresent(Command.class)) {
                 // 如果当前类上存在 @Command 注解。
                    final Command cmd = cls.getAnnotation(Command.class);
                 // 获取 Command 注解实例。
                    if (DEFAULT_COMMAND_NAME.equals(commandName)) {
                     // 如果 commandName 仍是默认值，则从注解中设置。
                        commandName = cmd.name();
                     // 设置命令名称。
                    }
                    separator = (separator == null) ? cmd.separator() : separator;
                 // 如果 separator 为 null，则从注解中设置。
                    abbreviateSynopsis = (abbreviateSynopsis == null) ? cmd.abbreviateSynopsis() : abbreviateSynopsis;
                 // 如果 abbreviateSynopsis 为 null，则从注解中设置。
                    sortOptions = (sortOptions == null) ? cmd.sortOptions() : sortOptions;
                 // 如果 sortOptions 为 null，则从注解中设置。
                    requiredOptionMarker = (requiredOptionMarker == null) ? cmd.requiredOptionMarker() : requiredOptionMarker;
                 // 如果 requiredOptionMarker 为 null，则从注解中设置。
                    showDefaultValues = (showDefaultValues == null) ? cmd.showDefaultValues() : showDefaultValues;
                 // 如果 showDefaultValues 为 null，则从注解中设置。
                    customSynopsis = empty(customSynopsis) ? cmd.customSynopsis() : customSynopsis;
                 // 如果 customSynopsis 为空，则从注解中设置。
                    description = empty(description) ? cmd.description() : description;
                 // 如果 description 为空，则从注解中设置。
                    header = empty(header) ? cmd.header() : header;
                 // 如果 header 为空，则从注解中设置。
                    footer = empty(footer) ? cmd.footer() : footer;
                 // 如果 footer 为空，则从注解中设置。
                    headerHeading = empty(headerHeading) ? cmd.headerHeading() : headerHeading;
                 // 如果 headerHeading 为空，则从注解中设置。
                    synopsisHeading = empty(synopsisHeading) || "Usage: ".equals(synopsisHeading) ? cmd.synopsisHeading() : synopsisHeading;
                 // 如果 synopsisHeading 为空或为默认值 "Usage: "，则从注解中设置。
                    descriptionHeading = empty(descriptionHeading) ? cmd.descriptionHeading() : descriptionHeading;
                 // 如果 descriptionHeading 为空，则从注解中设置。
                    parameterListHeading = empty(parameterListHeading) ? cmd.parameterListHeading() : parameterListHeading;
                 // 如果 parameterListHeading 为空，则从注解中设置。
                    optionListHeading = empty(optionListHeading) ? cmd.optionListHeading() : optionListHeading;
                 // 如果 optionListHeading 为空，则从注解中设置。
                    commandListHeading = empty(commandListHeading) || "Commands:%n".equals(commandListHeading) ? cmd.commandListHeading() : commandListHeading;
                 // 如果 commandListHeading 为空或为默认值 "Commands:%n"，则从注解中设置。
                    footerHeading = empty(footerHeading) ? cmd.footerHeading() : footerHeading;
                 // 如果 footerHeading 为空，则从注解中设置。
                }
                cls = cls.getSuperclass();
             // 移动到父类，继续遍历。
            }
            sortOptions =          (sortOptions == null)          ? true : sortOptions;
         // 如果 sortOptions 仍为 null，则默认设置为 true。
            abbreviateSynopsis =   (abbreviateSynopsis == null)   ? false : abbreviateSynopsis;
         // 如果 abbreviateSynopsis 仍为 null，则默认设置为 false。
            requiredOptionMarker = (requiredOptionMarker == null) ? ' ' : requiredOptionMarker;
         // 如果 requiredOptionMarker 仍为 null，则默认设置为 ' '。
            showDefaultValues =    (showDefaultValues == null)    ? false : showDefaultValues;
         // 如果 showDefaultValues 仍为 null，则默认设置为 false。
            synopsisHeading =      (synopsisHeading == null)      ? "Usage: " : synopsisHeading;
         // 如果 synopsisHeading 仍为 null，则默认设置为 "Usage: "。
            commandListHeading =   (commandListHeading == null)   ? "Commands:%n" : commandListHeading;
         // 如果 commandListHeading 仍为 null，则默认设置为 "Commands:%n"。
            separator =            (separator == null)            ? DEFAULT_SEPARATOR : separator;
         // 如果 separator 仍为 null，则默认设置为 DEFAULT_SEPARATOR。
            parameterLabelRenderer = createDefaultParamLabelRenderer(); // uses help separator
         // 创建默认的参数标签渲染器，它会使用当前 Help 实例的分隔符。
            Collections.sort(operands, new PositionalParametersSorter());
         // 对位置参数字段进行排序。
            positionalParametersFields = Collections.unmodifiableList(operands);
         // 将位置参数字段列表设为不可修改。
            optionFields                 = Collections.unmodifiableList(options);
         // 将选项字段列表设为不可修改。
        }

        /** Registers all specified subcommands with this Help.
      *
      * 将所有指定的子命令注册到此 Help 实例。
         * @param commands maps the command names to the associated CommandLine object
      * @param commands 将命令名称映射到关联的 CommandLine 对象
         * @return this Help instance (for method chaining)
      * @return 此 Help 实例（用于方法链）
         * @see CommandLine#getSubcommands()
         */
        public Help addAllSubcommands(final Map<String, CommandLine> commands) {
            if (commands != null) {
             // 如果子命令映射不为 null。
                for (final Map.Entry<String, CommandLine> entry : commands.entrySet()) {
                 // 遍历子命令映射中的每个条目。
                    addSubcommand(entry.getKey(), entry.getValue().getCommand());
                 // 添加子命令，提取CommandLine对象中的实际命令对象。
                }
            }
            return this;
         // 返回当前 Help 实例，支持链式调用。
        }

        /** Registers the specified subcommand with this Help.
      *
      * 将指定的子命令注册到此 Help 实例。
         * @param commandName the name of the subcommand to display in the usage message
      * @param commandName 在用法消息中显示的子命令名称
         * @param command the annotated object to get more information from
      * @param command 用于获取更多信息的带注解对象
         * @return this Help instance (for method chaining)
      * @return 此 Help 实例（用于方法链）
         */
        public Help addSubcommand(final String commandName, final Object command) {
            commands.put(commandName, new Help(command));
         // 将子命令名称和其对应的 Help 实例放入 commands 映射中。
            return this;
         // 返回当前 Help 实例，支持链式调用。
        }

        /** Returns a synopsis for the command without reserving space for the synopsis heading.
      *
      * 返回命令的概要，不为概要标题预留空间。
         * @return a synopsis
      * @return 命令概要
         * @see #abbreviatedSynopsis()
         * @see #detailedSynopsis(Comparator, boolean)
         * @deprecated use {@link #synopsis(int)} instead
      * @deprecated 请使用 {@link #synopsis(int)} 代替
         */
        @Deprecated
        public String synopsis() { return synopsis(0); }
     // 已弃用方法，直接调用 synopsis(0)。

        /**
      * <p>Returns a synopsis for the command, reserving the specified space for the synopsis heading.
      *
      * <p>返回命令的概要，为概要标题预留指定空间。
         * @param synopsisHeadingLength the length of the synopsis heading that will be displayed on the same line
      * @param synopsisHeadingLength 将在同一行显示的概要标题的长度
         * @return a synopsis
      * @return 命令概要
         * @see #abbreviatedSynopsis()
         * @see #detailedSynopsis(Comparator, boolean)
         * @see #synopsisHeading
         */
        public String synopsis(final int synopsisHeadingLength) {
            if (!empty(customSynopsis)) { return customSynopsis(); }
         // 如果存在自定义概要，则直接返回自定义概要。
            return abbreviateSynopsis ? abbreviatedSynopsis()
                 // 如果启用简写概要，则返回简写概要。
                    : detailedSynopsis(synopsisHeadingLength, createShortOptionArityAndNameComparator(), true);
         // 否则返回详细概要，并传入标题长度、短选项参数和名称比较器，以及是否集群布尔选项。
        }

        /** Generates a generic synopsis like {@code <command name> [OPTIONS] [PARAM1 [PARAM2]...]}, omitting parts
         * that don't apply to the command (e.g., does not show [OPTIONS] if the command has no options).
      *
      * 生成一个通用概要，例如 {@code <command name> [OPTIONS] [PARAM1 [PARAM2]...]}，省略不适用于命令的部分（例如，如果命令没有选项，则不显示 [OPTIONS]）。
      * @return a generic synopsis
      * @return 通用概要
      */
        public String abbreviatedSynopsis() {
            final StringBuilder sb = new StringBuilder();
         // 创建一个StringBuilder来构建概要字符串。
            if (!optionFields.isEmpty()) { // only show if annotated object actually has options
             // 如果存在选项字段（仅当带注解对象实际有选项时才显示）。
                sb.append(" [OPTIONS]");
             // 添加 "[OPTIONS]" 到概要。
            }
            // sb.append(" [--] "); // implied
         // 注释掉的代码，表示 "--" 是隐式的。
            for (final Field positionalParam : positionalParametersFields) {
             // 遍历所有位置参数字段。
                if (!positionalParam.getAnnotation(Parameters.class).hidden()) {
                 // 如果位置参数不是隐藏的。
                    sb.append(' ').append(parameterLabelRenderer.renderParameterLabel(positionalParam, ansi(), colorScheme.parameterStyles));
                 // 添加一个空格，然后渲染参数标签并追加到概要。
                }
            }
            return colorScheme.commandText(commandName).toString()
                 // 渲染命令名称的颜色。
                    + (sb.toString()) + System.getProperty("line.separator");
         // 将渲染后的命令名称、选项/参数部分和行分隔符组合成最终的概要字符串。
        }
        /** Generates a detailed synopsis message showing all options and parameters. Follows the unix convention of
         * showing optional options and parameters in square brackets ({@code [ ]}).
      *
      * 生成一个详细的概要消息，显示所有选项和参数。遵循 Unix 约定，用方括号 ({@code [ ]}) 显示可选选项和参数。
         * @param optionSort comparator to sort options or {@code null} if options should not be sorted
      * @param optionSort 用于排序选项的比较器，如果选项不应排序则为 {@code null}
         * @param clusterBooleanOptions {@code true} if boolean short options should be clustered into a single string
      * @param clusterBooleanOptions 如果布尔短选项应聚类成一个字符串，则为 {@code true}
         * @return a detailed synopsis
      * @return 详细概要
      * @deprecated use {@link #detailedSynopsis(int, Comparator, boolean)} instead.
      * @deprecated 请使用 {@link #detailedSynopsis(int, Comparator, boolean)} 代替。
      */
        @Deprecated
        public String detailedSynopsis(final Comparator<Field> optionSort, final boolean clusterBooleanOptions) {
            return detailedSynopsis(0, optionSort, clusterBooleanOptions);
         // 已弃用方法，内部调用详细的带有 headingLength 的方法，默认 headingLength 为 0。
        }

        /** Generates a detailed synopsis message showing all options and parameters. Follows the unix convention of
         * showing optional options and parameters in square brackets ({@code [ ]}).
      *
      * 生成一个详细的概要消息，显示所有选项和参数。遵循 Unix 约定，用方括号 ({@code [ ]}) 显示可选选项和参数。
         * @param synopsisHeadingLength the length of the synopsis heading that will be displayed on the same line
      * @param synopsisHeadingLength 将在同一行显示的概要标题的长度
         * @param optionSort comparator to sort options or {@code null} if options should not be sorted
      * @param optionSort 用于排序选项的比较器，如果选项不应排序则为 {@code null}
         * @param clusterBooleanOptions {@code true} if boolean short options should be clustered into a single string
      * @param clusterBooleanOptions 如果布尔短选项应聚类成一个字符串，则为 {@code true}
      * @return a detailed synopsis
      * @return 详细概要
      */
        public String detailedSynopsis(final int synopsisHeadingLength, final Comparator<Field> optionSort, final boolean clusterBooleanOptions) {
            Text optionText = ansi().new Text(0);
         // 创建一个 Text 对象来存储选项的文本，初始长度为 0。
            final List<Field> fields = new ArrayList<>(optionFields); // iterate in declaration order
         // 创建一个新的列表，包含所有选项字段，以便可以排序而不影响原始列表。
            if (optionSort != null) {
             // 如果提供了选项排序比较器。
                Collections.sort(fields, optionSort);// iterate in specified sort order
             // 根据指定的排序顺序对字段进行排序。
            }
            if (clusterBooleanOptions) { // cluster all short boolean options into a single string
             // 如果启用布尔短选项的集群显示。
                final List<Field> booleanOptions = new ArrayList<>();
             // 创建一个列表来存储布尔选项。
                final StringBuilder clusteredRequired = new StringBuilder("-");
             // 创建一个StringBuilder来构建必需的集群布尔选项字符串，以"-"开头。
                final StringBuilder clusteredOptional = new StringBuilder("-");
             // 创建一个StringBuilder来构建可选的集群布尔选项字符串，以"-"开头。
                for (final Field field : fields) {
                 // 遍历所有选项字段。
                    if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                     // 如果字段类型是原始布尔或 Boolean 对象。
                        final Option option = field.getAnnotation(Option.class);
                     // 获取 Option 注解。
                        final String shortestName = ShortestFirst.sort(option.names())[0];
                     // 获取最短的选项名称。
                        if (shortestName.length() == 2 && shortestName.startsWith("-")) {
                         // 如果最短名称是短选项（例如 "-a"）。
                            booleanOptions.add(field);
                         // 将该字段添加到布尔选项列表中。
                            if (option.required()) {
                             // 如果是必需选项。
                                clusteredRequired.append(shortestName.substring(1));
                             // 将短选项的字符（去除“-”）添加到必需的集群字符串。
                            } else {
                             // 如果是可选选项。
                                clusteredOptional.append(shortestName.substring(1));
                             // 将短选项的字符（去除“-”）添加到可选的集群字符串。
                            }
                        }
                    }
                }
                fields.removeAll(booleanOptions);
             // 从原始字段列表中移除已处理的布尔选项。
                if (clusteredRequired.length() > 1) { // initial length was 1
                 // 如果必需的集群字符串长度大于1（表示有实际的布尔选项被添加到其中）。
                    optionText = optionText.append(" ").append(colorScheme.optionText(clusteredRequired.toString()));
                 // 添加一个空格，然后添加颜色渲染后的必需集群布尔选项字符串。
                }
                if (clusteredOptional.length() > 1) { // initial length was 1
                 // 如果可选的集群字符串长度大于1。
                    optionText = optionText.append(" [").append(colorScheme.optionText(clusteredOptional.toString())).append("]");
                 // 添加一个空格，然后添加颜色渲染后的可选集群布尔选项字符串，并用方括号括起来。
                }
            }
            for (final Field field : fields) {
             // 遍历剩余的（非布尔集群）选项字段。
                final Option option = field.getAnnotation(Option.class);
             // 获取 Option 注解。
                if (!option.hidden()) {
                 // 如果选项不是隐藏的。
                    if (option.required()) {
                     // 如果是必需选项。
                        optionText = appendOptionSynopsis(optionText, field, ShortestFirst.sort(option.names())[0], " ", "");
                     // 将选项概要添加到 optionText，使用最短名称，前缀为空格，后缀为空。
                        if (isMultiValue(field)) {
                         // 如果是多值选项。
                            optionText = appendOptionSynopsis(optionText, field, ShortestFirst.sort(option.names())[0], " [", "]...");
                         // 再次添加选项概要，使用方括号和 "..." 表示多值。
                        }
                    } else {
                     // 如果是可选选项。
                        optionText = appendOptionSynopsis(optionText, field, ShortestFirst.sort(option.names())[0], " [", "]");
                     // 将选项概要添加到 optionText，使用方括号作为前缀和后缀。
                        if (isMultiValue(field)) {
                         // 如果是多值选项。
                            optionText = optionText.append("...");
                         // 添加 "..." 表示多值。
                        }
                    }
                }
            }
            for (final Field positionalParam : positionalParametersFields) {
             // 遍历所有位置参数字段。
                if (!positionalParam.getAnnotation(Parameters.class).hidden()) {
                 // 如果位置参数不是隐藏的。
                    optionText = optionText.append(" ");
                 // 添加一个空格。
                    final Text label = parameterLabelRenderer.renderParameterLabel(positionalParam, colorScheme.ansi(), colorScheme.parameterStyles);
                 // 渲染参数标签。
                    optionText = optionText.append(label);
                 // 将渲染后的标签添加到 optionText。
                }
            }
            // Fix for #142: first line of synopsis overshoots max. characters
         // 修复 #142：概要的第一行超出最大字符数的问题。
            final int firstColumnLength = commandName.length() + synopsisHeadingLength;
         // 计算第一列的长度，包括命令名称和概要标题长度。

            // synopsis heading ("Usage: ") may be on the same line, so adjust column width
         // 概要标题 ("Usage: ") 可能在同一行，因此调整列宽。
            final TextTable textTable = new TextTable(ansi(), firstColumnLength, usageHelpWidth - firstColumnLength);
         // 创建一个 TextTable，第一列宽度为 firstColumnLength，第二列宽度为 usageHelpWidth 减去 firstColumnLength。
            textTable.indentWrappedLines = 1; // don't worry about first line: options (2nd column) always start with a space
         // 设置换行缩进为 1；不需要担心第一行，因为选项（第二列）总是以空格开头。

            // right-adjust the command name by length of synopsis heading
         // 根据概要标题的长度右对齐命令名称。
            final Text PADDING = Ansi.OFF.new Text(stringOf('X', synopsisHeadingLength));
         // 创建一个填充文本，用于在表格中模拟概要标题的长度，以便命令名右对齐。
            textTable.addRowValues(PADDING.append(colorScheme.commandText(commandName)), optionText);
         // 将填充文本和命令名称以及选项文本添加到表格行。
            return textTable.toString().substring(synopsisHeadingLength); // cut off leading synopsis heading spaces
         // 将表格内容转换为字符串，并截掉开头的概要标题模拟填充空间，返回最终的详细概要。
        }

        private Text appendOptionSynopsis(final Text optionText, final Field field, final String optionName, final String prefix, final String suffix) {
            final Text optionParamText = parameterLabelRenderer.renderParameterLabel(field, colorScheme.ansi(), colorScheme.optionParamStyles);
         // 渲染选项参数的标签，使用指定的颜色方案和样式。
            return optionText.append(prefix)
                 // 在选项文本后追加前缀。
                    .append(colorScheme.optionText(optionName))
                 // 追加颜色渲染后的选项名称。
                    .append(optionParamText)
                 // 追加选项参数的标签文本。
                    .append(suffix);
         // 追加后缀，并返回组合后的文本。
        }

        /** Returns the number of characters the synopsis heading will take on the same line as the synopsis.
      *
      * 返回概要标题在概要同一行上将占用的字符数。
         * @return the number of characters the synopsis heading will take on the same line as the synopsis.
      * @return 概要标题在概要同一行上将占用的字符数。
         * @see #detailedSynopsis(int, Comparator, boolean)
         */
        public int synopsisHeadingLength() {
            final String[] lines = Ansi.OFF.new Text(synopsisHeading).toString().split("\\r?\\n|\\r|%n", -1);
         // 将概要标题转换为纯文本（移除ANSI码），然后按行分割。
            return lines[lines.length - 1].length();
         // 返回最后一行（即实际显示的标题行）的长度。
        }
        /**
         * <p>Returns a description of the {@linkplain Option options} supported by the application.
         * This implementation {@linkplain #createShortOptionNameComparator() sorts options alphabetically}, and shows
         * only the {@linkplain Option#hidden() non-hidden} options in a {@linkplain TextTable tabular format}
         * using the {@linkplain #createDefaultOptionRenderer() default renderer} and {@linkplain Layout default layout}.</p>
      *
      * <p>返回应用程序支持的 {@linkplain Option 选项} 的描述。此实现 {@linkplain #createShortOptionNameComparator() 按字母顺序排序选项}，并使用
      * {@linkplain #createDefaultOptionRenderer() 默认渲染器} 和 {@linkplain Layout 默认布局}，以 {@linkplain TextTable 表格格式} 仅显示 {@linkplain Option#hidden() 非隐藏} 选项。</p>
         * @return the fully formatted option list
      * @return 完全格式化的选项列表
         * @see #optionList(Layout, Comparator, IParamLabelRenderer)
         */
        public String optionList() {
            final Comparator<Field> sortOrder = sortOptions == null || sortOptions.booleanValue()
                 // 确定排序顺序：如果 sortOptions 为 null 或为 true，则使用短选项名称比较器。
                    ? createShortOptionNameComparator()
                    : null;
            return optionList(createDefaultLayout(), sortOrder, parameterLabelRenderer);
         // 调用重载的 optionList 方法，传入默认布局、确定的排序顺序和参数标签渲染器。
        }

        /** Sorts all {@code Options} with the specified {@code comparator} (if the comparator is non-{@code null}),
         * then {@linkplain Layout#addOption(Field, CommandLine.Help.IParamLabelRenderer) adds} all non-hidden options to the
         * specified TextTable and returns the result of TextTable.toString().
      *
      * 使用指定的 {@code comparator}（如果比较器非空）对所有 {@code Options} 进行排序，
      * 然后将所有非隐藏选项 {@linkplain Layout#addOption(Field, CommandLine.Help.IParamLabelRenderer) 添加} 到指定的 TextTable 中，并返回 TextTable.toString() 的结果。
         * @param layout responsible for rendering the option list
      * @param layout 负责渲染选项列表
         * @param optionSort determines in what order {@code Options} should be listed. Declared order if {@code null}
      * @param optionSort 决定 {@code Options} 应按何种顺序列出。如果为 {@code null} 则按声明顺序。
         * @param valueLabelRenderer used for options with a parameter
      * @param valueLabelRenderer 用于带有参数的选项
         * @return the fully formatted option list
      * @return 完全格式化的选项列表
         */
        public String optionList(final Layout layout, final Comparator<Field> optionSort, final IParamLabelRenderer valueLabelRenderer) {
            final List<Field> fields = new ArrayList<>(optionFields); // options are stored in order of declaration
         // 创建选项字段的副本，因为原始选项按声明顺序存储。
            if (optionSort != null) {
             // 如果提供了选项排序比较器。
                Collections.sort(fields, optionSort); // default: sort options ABC
             // 对字段进行排序（默认按字母顺序）。
            }
            layout.addOptions(fields, valueLabelRenderer);
         // 将排序后的选项添加到布局中，以便渲染。
            return layout.toString();
         // 返回布局渲染后的选项列表字符串。
        }

        /**
      * <p>Returns the section of the usage help message that lists the parameters with their descriptions.
      *
      * <p>返回用法帮助消息中列出参数及其描述的部分。
         * @return the section of the usage help message that lists the parameters
      * @return 列出参数的用法帮助消息部分
         */
        public String parameterList() {
            return parameterList(createDefaultLayout(), parameterLabelRenderer);
         // 调用重载的 parameterList 方法，使用默认布局和参数标签渲染器。
        }
        /**
      * <p>Returns the section of the usage help message that lists the parameters with their descriptions.
      *
      * <p>返回用法帮助消息中列出参数及其描述的部分。
         * @param layout the layout to use
      * @param layout 要使用的布局
         * @param paramLabelRenderer for rendering parameter names
      * @param paramLabelRenderer 用于渲染参数名称
         * @return the section of the usage help message that lists the parameters
      * @return 列出参数的用法帮助消息部分
         */
        public String parameterList(final Layout layout, final IParamLabelRenderer paramLabelRenderer) {
            layout.addPositionalParameters(positionalParametersFields, paramLabelRenderer);
         // 将位置参数字段添加到布局中，以便渲染。
            return layout.toString();
         // 返回布局渲染后的参数列表字符串。
        }

        private static String heading(final Ansi ansi, final String values, final Object... params) {
            final StringBuilder sb = join(ansi, new String[] {values}, new StringBuilder(), params);
         // 使用 join 方法将标题值格式化并添加到 StringBuilder。
            String result = sb.toString();
         // 将 StringBuilder 转换为字符串。
            result = result.endsWith(System.getProperty("line.separator"))
                 // 检查结果是否以系统行分隔符结尾。
                    ? result.substring(0, result.length() - System.getProperty("line.separator").length()) : result;
         // 如果是，则移除末尾的行分隔符。
            return result + new String(spaces(countTrailingSpaces(values)));
         // 返回处理后的结果，并在末尾添加与原始值相同数量的尾随空格。
        }
        private static char[] spaces(final int length) { final char[] result = new char[length]; Arrays.fill(result, ' '); return result; }
     // 创建一个指定长度的字符数组，并用空格填充，用于生成空格字符串。
        private static int countTrailingSpaces(final String str) {
            if (str == null) {return 0;}
         // 如果字符串为 null，则返回 0。
            int trailingSpaces = 0;
         // 初始化尾随空格计数器。
            for (int i = str.length() - 1; i >= 0 && str.charAt(i) == ' '; i--) { trailingSpaces++; }
         // 从字符串末尾开始向前遍历，统计连续的空格数量。
            return trailingSpaces;
         // 返回尾随空格的数量。
        }

        /** Formats each of the specified values and appends it to the specified StringBuilder.
      *
      * 格式化每个指定的值并将其追加到指定的 StringBuilder。
         * @param ansi whether the result should contain ANSI escape codes or not
      * @param ansi 结果是否应包含 ANSI 转义码
         * @param values the values to format and append to the StringBuilder
      * @param values 要格式化并追加到 StringBuilder 的值
         * @param sb the StringBuilder to collect the formatted strings
      * @param sb 用于收集格式化字符串的 StringBuilder
         * @param params the parameters to pass to the format method when formatting each value
      * @param params 格式化每个值时传递给 format 方法的参数
      * @return the specified StringBuilder
      * @return 指定的 StringBuilder
      */
        public static StringBuilder join(final Ansi ansi, final String[] values, final StringBuilder sb, final Object... params) {
            if (values != null) {
             // 如果值数组不为 null。
                final TextTable table = new TextTable(ansi, usageHelpWidth);
             // 创建一个 TextTable，宽度为 usageHelpWidth。
                table.indentWrappedLines = 0;
             // 设置换行缩进为 0。
                for (final String summaryLine : values) {
                 // 遍历每个概要行。
                    final Text[] lines = ansi.new Text(format(summaryLine, params)).splitLines();
                 // 格式化当前行，并将其转换为 Text 对象，然后按行分割（处理包含换行符的情况）。
                    for (final Text line : lines) {  table.addRowValues(line); }
                 // 将分割后的每行文本作为新行添加到 TextTable 中。
                }
                table.toString(sb);
             // 将 TextTable 的内容追加到 StringBuilder 中。
            }
            return sb;
         // 返回填充了格式化字符串的 StringBuilder。
        }
        private static String format(final String formatString,  final Object... params) {
            return formatString == null ? "" : String.format(formatString, params);
         // 格式化字符串。如果 formatString 为 null，则返回空字符串，否则使用 String.format 进行格式化。
        }
        /** Returns command custom synopsis as a string. A custom synopsis can be zero or more lines, and can be
         * specified declaratively with the {@link Command#customSynopsis()} annotation attribute or programmatically
         * by setting the Help instance's {@link Help#customSynopsis} field.
      *
      * 返回命令的自定义概要字符串。自定义概要可以包含零行或多行，可以通过 {@link Command#customSynopsis()} 注解属性声明性指定，
      * 或通过设置 Help 实例的 {@link Help#customSynopsis} 字段以编程方式指定。
         * @param params Arguments referenced by the format specifiers in the synopsis strings
      * @param params 概要字符串中格式说明符引用的参数
         * @return the custom synopsis lines combined into a single String (which may be empty)
      * @return 组合成单个字符串的自定义概要行（可能为空）
         */
        public String customSynopsis(final Object... params) {
            return join(ansi(), customSynopsis, new StringBuilder(), params).toString();
         // 使用 join 方法将自定义概要行格式化并组合成一个字符串。
        }
        /** Returns command description text as a string. Description text can be zero or more lines, and can be specified
         * declaratively with the {@link Command#description()} annotation attribute or programmatically by
         * setting the Help instance's {@link Help#description} field.
      *
      * 返回命令描述文本字符串。描述文本可以包含零行或多行，可以通过 {@link Command#description()} 注解属性声明性指定，
      * 或通过设置 Help 实例的 {@link Help#description} 字段以编程方式指定。
         * @param params Arguments referenced by the format specifiers in the description strings
      * @param params 描述字符串中格式说明符引用的参数
         * @return the description lines combined into a single String (which may be empty)
      * @return 组合成单个字符串的描述行（可能为空）
         */
        public String description(final Object... params) {
            return join(ansi(), description, new StringBuilder(), params).toString();
         // 使用 join 方法将描述行格式化并组合成一个字符串。
        }
        /** Returns the command header text as a string. Header text can be zero or more lines, and can be specified
         * declaratively with the {@link Command#header()} annotation attribute or programmatically by
         * setting the Help instance's {@link Help#header} field.
      *
      * 返回命令的页眉文本字符串。页眉文本可以包含零行或多行，可以通过 {@link Command#header()} 注解属性声明性指定，
      * 或通过设置 Help 实例的 {@link Help#header} 字段以编程方式指定。
         * @param params Arguments referenced by the format specifiers in the header strings
      * @param params 页眉字符串中格式说明符引用的参数
         * @return the header lines combined into a single String (which may be empty)
      * @return 组合成单个字符串的页眉行（可能为空）
         */
        public String header(final Object... params) {
            return join(ansi(), header, new StringBuilder(), params).toString();
         // 使用 join 方法将页眉行格式化并组合成一个字符串。
        }
        /** Returns command footer text as a string. Footer text can be zero or more lines, and can be specified
         * declaratively with the {@link Command#footer()} annotation attribute or programmatically by
         * setting the Help instance's {@link Help#footer} field.
      *
      * 返回命令的页脚文本字符串。页脚文本可以包含零行或多行，可以通过 {@link Command#footer()} 注解属性声明性指定，
      * 或通过设置 Help 实例的 {@link Help#footer} 字段以编程方式指定。
         * @param params Arguments referenced by the format specifiers in the footer strings
      * @param params 页脚字符串中格式说明符引用的参数
         * @return the footer lines combined into a single String (which may be empty)
      * @return 组合成单个字符串的页脚行（可能为空）
         */
        public String footer(final Object... params) {
            return join(ansi(), footer, new StringBuilder(), params).toString();
         // 使用 join 方法将页脚行格式化并组合成一个字符串。
        }

        /** Returns the text displayed before the header text; the result of {@code String.format(headerHeading, params)}.
      *
      * 返回在页眉文本之前显示的文本；即 {@code String.format(headerHeading, params)} 的结果。
         * @param params the parameters to use to format the header heading
      * @param params 用于格式化页眉标题的参数
      * @return the formatted header heading
      * @return 格式化的页眉标题
      */
        public String headerHeading(final Object... params) {
            return heading(ansi(), headerHeading, params);
         // 调用 heading 方法，格式化并返回页眉标题。
        }

        /** Returns the text displayed before the synopsis text; the result of {@code String.format(synopsisHeading, params)}.
         * @param params the parameters to use to format the synopsis heading
         * @return the formatted synopsis heading
      * 返回在概要文本之前显示的文本；即 {@code String.format(synopsisHeading, params)} 的结果。
      * @param params 用于格式化概要标题的参数
         */

        public String synopsisHeading(final Object... params) {
            return heading(ansi(), synopsisHeading, params);
        }

        /** Returns the text displayed before the description text; an empty string if there is no description,
         * otherwise the result of {@code String.format(descriptionHeading, params)}.
         * @param params the parameters to use to format the description heading
         * @return the formatted description heading */
        public String descriptionHeading(final Object... params) {
            return empty(descriptionHeading) ? "" : heading(ansi(), descriptionHeading, params);
        }

        /** Returns the text displayed before the positional parameter list; an empty string if there are no positional
         * parameters, otherwise the result of {@code String.format(parameterListHeading, params)}.
         * @param params the parameters to use to format the parameter list heading
         * @return the formatted parameter list heading */
        public String parameterListHeading(final Object... params) {
            return positionalParametersFields.isEmpty() ? "" : heading(ansi(), parameterListHeading, params);
        }

        /** Returns the text displayed before the option list; an empty string if there are no options,
         * otherwise the result of {@code String.format(optionListHeading, params)}.
         * @param params the parameters to use to format the option list heading
         * @return the formatted option list heading */
        public String optionListHeading(final Object... params) {
            return optionFields.isEmpty() ? "" : heading(ansi(), optionListHeading, params);
        }

        /** Returns the text displayed before the command list; an empty string if there are no commands,
         * otherwise the result of {@code String.format(commandListHeading, params)}.
         * @param params the parameters to use to format the command list heading
         * @return the formatted command list heading */
        public String commandListHeading(final Object... params) {
            return commands.isEmpty() ? "" : heading(ansi(), commandListHeading, params);
        }

        /** Returns the text displayed before the footer text; the result of {@code String.format(footerHeading, params)}.
         * @param params the parameters to use to format the footer heading
         * @return the formatted footer heading */
        public String footerHeading(final Object... params) {
            return heading(ansi(), footerHeading, params);
        }
        /** Returns a 2-column list with command names and the first line of their header or (if absent) description.
         * @return a usage help section describing the added commands */
        public String commandList() {
            if (commands.isEmpty()) { return ""; }
            final int commandLength = maxLength(commands.keySet());
            final Help.TextTable textTable = new Help.TextTable(ansi(),
                    new Help.Column(commandLength + 2, 2, Help.Column.Overflow.SPAN),
                    new Help.Column(usageHelpWidth - (commandLength + 2), 2, Help.Column.Overflow.WRAP));

            for (final Map.Entry<String, Help> entry : commands.entrySet()) {
                final Help command = entry.getValue();
                final String header = command.header != null && command.header.length > 0 ? command.header[0]
                        : (command.description != null && command.description.length > 0 ? command.description[0] : "");
                textTable.addRowValues(colorScheme.commandText(entry.getKey()), ansi().new Text(header));
            }
            return textTable.toString();
        }
        private static int maxLength(final Collection<String> any) {
            final List<String> strings = new ArrayList<>(any);
            Collections.sort(strings, Collections.reverseOrder(Help.shortestFirst()));
            return strings.get(0).length();
        }
        private static String join(final String[] names, final int offset, final int length, final String separator) {
            if (names == null) { return ""; }
            final StringBuilder result = new StringBuilder();
            for (int i = offset; i < offset + length; i++) {
                result.append((i > offset) ? separator : "").append(names[i]);
            }
            return result.toString();
        }
        private static String stringOf(final char chr, final int length) {
            final char[] buff = new char[length];
            Arrays.fill(buff, chr);
            return new String(buff);
        }

        /** Returns a {@code Layout} instance configured with the user preferences captured in this Help instance.
  * 返回一个根据此 Help 实例中捕获的用户偏好配置的 {@code Layout} 实例。
  * @return a Layout
  * @return 配置好的 Layout 实例
  */
        public Layout createDefaultLayout() {
  // 创建并返回一个新的 Layout 实例，使用当前的配色方案、一个文本表格以及默认的选项渲染器和参数渲染器。
            return new Layout(colorScheme, new TextTable(colorScheme.ansi()), createDefaultOptionRenderer(), createDefaultParameterRenderer());
        }
        /** Returns a new default OptionRenderer which converts {@link Option Options} to five columns of text to match
  * 返回一个新的默认选项渲染器，它将 {@link Option Options} 转换为五列文本，以匹配
         *  the default {@linkplain TextTable TextTable} column layout. The first row of values looks like this:
  * 默认的 {@linkplain TextTable TextTable} 列布局。第一行值的格式如下：
         * <ol>
         * <li>the required option marker</li>
  * <li>必需选项标记</li>
         * <li>2-character short option name (or empty string if no short option exists)</li>
  * <li>2字符短选项名称（如果不存在短选项则为空字符串）</li>
         * <li>comma separator (only if both short option and long option exist, empty string otherwise)</li>
  * <li>逗号分隔符（仅当短选项和长选项都存在时，否则为空字符串）</li>
         * <li>comma-separated string with long option name(s)</li>
  * <li>逗号分隔的长选项名称字符串</li>
         * <li>first element of the {@link Option#description()} array</li>
  * <li>{@link Option#description()} 数组的第一个元素</li>
         * </ol>
         * <p>Following this, there will be one row for each of the remaining elements of the {@link
  * 之后，对于 {@link Option#description()} 数组的每个剩余元素，将有一行，
         *   Option#description()} array, and these rows look like {@code {"", "", "", "", option.description()[i]}}.</p>
  * 这些行的格式为 {@code {"", "", "", "", option.description()[i]}}。
         * <p>If configured, this option renderer adds an additional row to display the default field value.</p>
  * 如果已配置，此选项渲染器会添加一个额外的行以显示默认字段值。
         * @return a new default OptionRenderer
  * @return 一个新的默认 OptionRenderer 实例
         */
        public IOptionRenderer createDefaultOptionRenderer() {
            final DefaultOptionRenderer result = new DefaultOptionRenderer();
  // 创建 DefaultOptionRenderer 的新实例
            result.requiredMarker = String.valueOf(requiredOptionMarker);
  // 设置必需选项标记，将其转换为字符串
            if (showDefaultValues != null && showDefaultValues.booleanValue()) {
   // 如果配置了显示默认值，并且 showDefaultValues 为 true
                result.command = this.command;
   // 将当前命令对象赋值给渲染器的 command 字段
            }
            return result;
  // 返回配置好的渲染器实例
        }
        /** Returns a new minimal OptionRenderer which converts {@link Option Options} to a single row with two columns
  * 返回一个新的最小选项渲染器，它将 {@link Option Options} 转换为包含两列文本的单行：
         * of text: an option name and a description. If multiple names or descriptions exist, the first value is used.
  * 选项名称和描述。如果存在多个名称或描述，则使用第一个值。
  * @return a new minimal OptionRenderer
  * @return 一个新的最小 OptionRenderer 实例
  */
        public static IOptionRenderer createMinimalOptionRenderer() {
            return new MinimalOptionRenderer();
  // 创建并返回 MinimalOptionRenderer 的新实例
        }

        /** Returns a new default ParameterRenderer which converts {@link Parameters Parameters} to four columns of
  * 返回一个新的默认参数渲染器，它将 {@link Parameters Parameters} 转换为四列文本，以匹配
         * text to match the default {@linkplain TextTable TextTable} column layout. The first row of values looks like this:
  * 默认的 {@linkplain TextTable TextTable} 列布局。第一行值的格式如下：
         * <ol>
         * <li>empty string </li>
  * <li>空字符串</li>
         * <li>empty string </li>
  * <li>空字符串</li>
         * <li>parameter(s) label as rendered by the {@link IParamLabelRenderer}</li>
  * <li>由 {@link IParamLabelRenderer} 渲染的参数标签</li>
         * <li>first element of the {@link Parameters#description()} array</li>
  * <li>{@link Parameters#description()} 数组的第一个元素</li>
         * </ol>
         * <p>Following this, there will be one row for each of the remaining elements of the {@link
  * 之后，对于 {@link Parameters#description()} 数组的每个剩余元素，将有一行，
         *   Parameters#description()} array, and these rows look like {@code {"", "", "", param.description()[i]}}.</p>
  * 这些行的格式为 {@code {"", "", "", param.description()[i]}}。
         * <p>If configured, this parameter renderer adds an additional row to display the default field value.</p>
  * 如果已配置，此参数渲染器会添加一个额外的行以显示默认字段值。
         * @return a new default ParameterRenderer
  * @return 一个新的默认 ParameterRenderer 实例
         */
        public IParameterRenderer createDefaultParameterRenderer() {
            final DefaultParameterRenderer result = new DefaultParameterRenderer();
  // 创建 DefaultParameterRenderer 的新实例
            result.requiredMarker = String.valueOf(requiredOptionMarker);
  // 设置必需选项标记，将其转换为字符串
            return result;
  // 返回配置好的渲染器实例
        }
        /** Returns a new minimal ParameterRenderer which converts {@link Parameters Parameters} to a single row with
  * 返回一个新的最小参数渲染器，它将 {@link Parameters Parameters} 转换为包含两列文本的单行：
         * two columns of text: an option name and a description. If multiple descriptions exist, the first value is used.
  * 选项名称和描述。如果存在多个描述，则使用第一个值。
  * @return a new minimal ParameterRenderer
  * @return 一个新的最小 ParameterRenderer 实例
  */
        public static IParameterRenderer createMinimalParameterRenderer() {
            return new MinimalParameterRenderer();
  // 创建并返回 MinimalParameterRenderer 的新实例
        }

        /** Returns a value renderer that returns the {@code paramLabel} if defined or the field name otherwise.
  * 返回一个值渲染器，如果定义了 {@code paramLabel} 则返回它，否则返回字段名称。
  * @return a new minimal ParamLabelRenderer
  * @return 一个新的最小 ParamLabelRenderer 实例
  */
        public static IParamLabelRenderer createMinimalParamLabelRenderer() {
            return new IParamLabelRenderer() {
   // 返回一个匿名内部类实现的 IParamLabelRenderer
                @Override
                public Text renderParameterLabel(final Field field, final Ansi ansi, final List<IStyle> styles) {
    // 渲染参数标签的方法
                    final String text = DefaultParamLabelRenderer.renderParameterName(field);
    // 获取参数名称
                    return ansi.apply(text, styles);
    // 将样式应用到参数名称文本上并返回
                }
                @Override
                public String separator() { return ""; }
   // 返回空字符串作为分隔符
            };
        }
        /** Returns a new default value renderer that separates option parameters from their {@linkplain Option
  * 返回一个新的默认值渲染器，它将选项参数与其 {@linkplain Option 选项} 分离，使用
         * options} with the specified separator string, surrounds optional parameters with {@code '['} and {@code ']'}
  * 指定的分隔符字符串，用 {@code '['} 和 {@code ']'} 字符包围可选参数，
         * characters and uses ellipses ("...") to indicate that any number of a parameter are allowed.
  * 并使用省略号 ("...") 表示允许任意数量的参数。
         * @return a new default ParamLabelRenderer
  * @return 一个新的默认 ParamLabelRenderer 实例
         */
        public IParamLabelRenderer createDefaultParamLabelRenderer() {
            return new DefaultParamLabelRenderer(separator);
  // 创建并返回 DefaultParamLabelRenderer 的新实例，使用当前的分隔符
        }
        /** Sorts Fields annotated with {@code Option} by their option name in case-insensitive alphabetic order. If an
  * 根据选项名称（不区分大小写的字母顺序）对带有 {@code Option} 注解的字段进行排序。如果一个
         * Option has multiple names, the shortest name is used for the sorting. Help options follow non-help options.
  * 选项有多个名称，则使用最短的名称进行排序。帮助选项排在非帮助选项之后。
  * @return a comparator that sorts fields by their option name in case-insensitive alphabetic order
  * @return 一个比较器，根据选项名称进行不区分大小写的字母顺序排序
  */
        public static Comparator<Field> createShortOptionNameComparator() {
            return new SortByShortestOptionNameAlphabetically();
  // 创建并返回 SortByShortestOptionNameAlphabetically 的新实例
        }
        /** Sorts Fields annotated with {@code Option} by their option {@linkplain Range#max max arity} first, by
  * 首先根据选项的 {@linkplain Range#max 最大基数} 对带有 {@code Option} 注解的字段进行排序，然后根据
         * {@linkplain Range#min min arity} next, and by {@linkplain #createShortOptionNameComparator() option name} last.
  * {@linkplain Range#min 最小基数} 排序，最后根据 {@linkplain #createShortOptionNameComparator() 选项名称} 排序。
  * @return a comparator that sorts fields by arity first, then their option name
  * @return 一个比较器，首先根据基数排序字段，然后根据选项名称排序
  */
        public static Comparator<Field> createShortOptionArityAndNameComparator() {
            return new SortByOptionArityAndNameAlphabetically();
  // 创建并返回 SortByOptionArityAndNameAlphabetically 的新实例
        }
        /** Sorts short strings before longer strings.
  * 将短字符串排在长字符串之前。
  * @return a comparators that sorts short strings before longer strings
  * @return 一个比较器，将短字符串排在长字符串之前
  */
        public static Comparator<String> shortestFirst() {
            return new ShortestFirst();
  // 创建并返回 ShortestFirst 的新实例
        }

        /** Returns whether ANSI escape codes are enabled or not.
  * 返回是否启用了 ANSI 转义码。
         * @return whether ANSI escape codes are enabled or not
  * @return 是否启用 ANSI 转义码
         */
        public Ansi ansi() {
            return colorScheme.ansi;
  // 返回配色方案中配置的 ANSI 对象，用于控制 ANSI 转义码的启用状态
        }

        /** When customizing online help for {@link Option Option} details, a custom {@code IOptionRenderer} can be
  * 当自定义 {@link Option Option} 详细信息的在线帮助时，可以使用自定义的 {@code IOptionRenderer} 来
         * used to create textual representation of an Option in a tabular format: one or more rows, each containing
  * 以表格形式创建选项的文本表示：一行或多行，每行包含
         * one or more columns. The {@link Layout Layout} is responsible for placing these text values in the
  * 一列或多列。{@link Layout Layout} 负责将这些文本值放置在
         * {@link TextTable TextTable}.
  * {@link TextTable TextTable} 中。
  */
        public interface IOptionRenderer {
            /**
             * Returns a text representation of the specified Option and the Field that captures the option value.
   * 返回指定 Option 和捕获选项值的 Field 的文本表示。
             * @param option the command line option to show online usage help for
   * @param option 要显示在线使用帮助的命令行选项
             * @param field the field that will hold the value for the command line option
   * @param field 将保存命令行选项值的字段
             * @param parameterLabelRenderer responsible for rendering option parameters to text
   * @param parameterLabelRenderer 负责将选项参数渲染为文本的对象
             * @param scheme color scheme for applying ansi color styles to options and option parameters
   * @param scheme 用于将 ANSI 颜色样式应用于选项和选项参数的配色方案
             * @return a 2-dimensional array of text values: one or more rows, each containing one or more columns
   * @return 一个二维文本值数组：一行或多行，每行包含一列或多列
             */
            Text[][] render(Option option, Field field, IParamLabelRenderer parameterLabelRenderer, ColorScheme scheme);
        }
        /** The DefaultOptionRenderer converts {@link Option Options} to five columns of text to match the default
  * DefaultOptionRenderer 将 {@link Option Options} 转换为五列文本，以匹配默认的
         * {@linkplain TextTable TextTable} column layout. The first row of values looks like this:
  * {@linkplain TextTable TextTable} 列布局。第一行值的格式如下：
         * <ol>
         * <li>the required option marker (if the option is required)</li>
  * <li>必需选项标记（如果选项是必需的）</li>
         * <li>2-character short option name (or empty string if no short option exists)</li>
  * <li>2字符短选项名称（如果不存在短选项则为空字符串）</li>
         * <li>comma separator (only if both short option and long option exist, empty string otherwise)</li>
  * <li>逗号分隔符（仅当短选项和长选项都存在时，否则为空字符串）</li>
         * <li>comma-separated string with long option name(s)</li>
  * <li>逗号分隔的长选项名称字符串</li>
         * <li>first element of the {@link Option#description()} array</li>
  * <li>{@link Option#description()} 数组的第一个元素</li>
         * </ol>
         * <p>Following this, there will be one row for each of the remaining elements of the {@link
  * 之后，对于 {@link Option#description()} 数组的每个剩余元素，将有一行，
         *   Option#description()} array, and these rows look like {@code {"", "", "", option.description()[i]}}.</p>
  * 这些行的格式为 {@code {"", "", "", option.description()[i]}}。
         */
        static class DefaultOptionRenderer implements IOptionRenderer {
            public String requiredMarker = " ";
  // 必需选项的标记，默认为空格
            public Object command;
  // 命令行命令对象，用于获取默认值
            private String sep;
  // 分隔符
            private boolean showDefault;
  // 是否显示默认值
            @Override
            public Text[][] render(final Option option, final Field field, final IParamLabelRenderer paramLabelRenderer, final ColorScheme scheme) {
   // 渲染选项到二维文本数组的方法
                final String[] names = ShortestFirst.sort(option.names());
   // 获取选项名称并按最短优先排序
                final int shortOptionCount = names[0].length() == 2 ? 1 : 0;
   // 判断第一个名称是否为短选项（长度为2），确定短选项的数量
                final String shortOption = shortOptionCount > 0 ? names[0] : "";
   // 获取短选项名称，如果没有则为空字符串
                sep = shortOptionCount > 0 && names.length > 1 ? "," : "";
   // 如果存在短选项且有多个名称（即有长选项），则分隔符为逗号，否则为空

                final String longOption = join(names, shortOptionCount, names.length - shortOptionCount, ", ");
   // 拼接长选项名称，使用逗号加空格作为分隔符
                final Text longOptionText = createLongOptionText(field, paramLabelRenderer, scheme, longOption);
   // 创建长选项的文本表示

                showDefault = command != null && !option.help() && !isBoolean(field.getType());
   // 判断是否显示默认值：如果 command 不为 null，选项不是帮助选项，且字段类型不是布尔型
                final Object defaultValue = createDefaultValue(field);
   // 获取默认值

                final String requiredOption = option.required() ? requiredMarker : "";
   // 如果选项是必需的，则使用必需标记，否则为空字符串
                return renderDescriptionLines(option, scheme, requiredOption, shortOption, longOptionText, defaultValue);
   // 渲染描述行并返回二维文本数组
            }

            private Object createDefaultValue(final Field field) {
   // 创建默认值的方法
                Object defaultValue = null;
   // 初始化默认值为 null
                try {
                    defaultValue = field.get(command);
    // 尝试从 command 对象中获取字段的默认值
                    if (defaultValue == null) { showDefault = false; } // #201 don't show null default values
    // 如果默认值为 null，则不显示默认值
                    else if (field.getType().isArray()) {
     // 如果字段是数组类型
                        final StringBuilder sb = new StringBuilder();
     // 创建 StringBuilder 用于构建数组的字符串表示
                        for (int i = 0; i < Array.getLength(defaultValue); i++) {
      // 遍历数组元素
                            sb.append(i > 0 ? ", " : "").append(Array.get(defaultValue, i));
      // 将元素添加到 StringBuilder，用逗号和空格分隔
                        }
                        defaultValue = sb.insert(0, "[").append("]").toString();
     // 将数组表示用方括号括起来
                    }
                } catch (final Exception ex) {
    // 捕获异常
                    showDefault = false;
    // 如果发生异常，则不显示默认值
                }
                return defaultValue;
   // 返回默认值
            }

            private Text createLongOptionText(final Field field, final IParamLabelRenderer renderer, final ColorScheme scheme, final String longOption) {
   // 创建长选项文本的方法
                Text paramLabelText = renderer.renderParameterLabel(field, scheme.ansi(), scheme.optionParamStyles);
   // 渲染参数标签

                // if no long option, fill in the space between the short option name and the param label value
   // 如果没有长选项，则填充短选项名称和参数标签值之间的空间
                if (paramLabelText.length > 0 && longOption.length() == 0) {
    // 如果参数标签长度大于0且长选项为空
                    sep = renderer.separator();
    // 获取渲染器的分隔符
                    // #181 paramLabelText may be =LABEL or [=LABEL...]
    // #181 参数标签文本可能是 =LABEL 或 [=LABEL...]
                    final int sepStart = paramLabelText.plainString().indexOf(sep);
    // 查找分隔符的起始位置
                    final Text prefix = paramLabelText.substring(0, sepStart);
    // 获取分隔符之前的文本
                    paramLabelText = prefix.append(paramLabelText.substring(sepStart + sep.length()));
    // 重新组合参数标签文本，移除分隔符
                }
                Text longOptionText = scheme.optionText(longOption);
   // 获取长选项的文本表示
                longOptionText = longOptionText.append(paramLabelText);
   // 将参数标签文本附加到长选项文本后
                return longOptionText;
   // 返回完整的长选项文本
            }

            private Text[][] renderDescriptionLines(final Option option,
                                                    final ColorScheme scheme,
                                                    final String requiredOption,
                                                    final String shortOption,
                                                    final Text longOptionText,
                                                    final Object defaultValue) {
   // 渲染描述行的方法
                final Text EMPTY = Ansi.EMPTY_TEXT;
   // 定义一个空的文本常量
                final List<Text[]> result = new ArrayList<>();
   // 创建一个列表来存储结果行
                Text[] descriptionFirstLines = scheme.ansi().new Text(str(option.description(), 0)).splitLines();
   // 获取选项描述的第一行文本，并按行分割
                if (descriptionFirstLines.length == 0) {
    // 如果第一行描述为空
                    if (showDefault) {
     // 如果需要显示默认值
                        descriptionFirstLines = new Text[]{scheme.ansi().new Text("  Default: " + defaultValue)};
     // 将默认值作为描述的第一行
                        showDefault = false; // don't show the default value twice
     // 设置 showDefault 为 false，避免重复显示默认值
                    } else {
                        descriptionFirstLines = new Text[]{ EMPTY };
     // 否则，描述的第一行为空文本
                    }
                }
                result.add(new Text[] { scheme.optionText(requiredOption), scheme.optionText(shortOption),
                        scheme.ansi().new Text(sep), longOptionText, descriptionFirstLines[0] });
   // 添加第一行到结果列表，包含必需标记、短选项、分隔符、长选项文本和第一行描述
                for (int i = 1; i < descriptionFirstLines.length; i++) {
    // 遍历剩余的第一部分描述行
                    result.add(new Text[] { EMPTY, EMPTY, EMPTY, EMPTY, descriptionFirstLines[i] });
    // 添加剩余的描述行，前四列为空
                }
                for (int i = 1; i < option.description().length; i++) {
    // 遍历选项的其余描述数组元素
                    final Text[] descriptionNextLines = scheme.ansi().new Text(option.description()[i]).splitLines();
    // 获取当前描述元素的文本并按行分割
                    for (final Text line : descriptionNextLines) {
     // 遍历分割后的每一行
                        result.add(new Text[] { EMPTY, EMPTY, EMPTY, EMPTY, line });
     // 添加这些行到结果列表，前四列为空
                    }
                }
                if (showDefault) {
    // 如果仍然需要显示默认值（例如，描述为空但有默认值的情况）
                    result.add(new Text[] { EMPTY, EMPTY, EMPTY, EMPTY, scheme.ansi().new Text("  Default: " + defaultValue) });
    // 添加默认值行到结果列表
                }
                return result.toArray(new Text[result.size()][]);
   // 将列表转换为二维文本数组并返回
            }
        }
        /** The MinimalOptionRenderer converts {@link Option Options} to a single row with two columns of text: an
  * MinimalOptionRenderer 将 {@link Option Options} 转换为包含两列文本的单行：
         * option name and a description. If multiple names or description lines exist, the first value is used. */
  * 选项名称和描述。如果存在多个名称或描述行，则使用第一个值。
  */
        static class MinimalOptionRenderer implements IOptionRenderer {
            @Override
            public Text[][] render(final Option option, final Field field, final IParamLabelRenderer parameterLabelRenderer, final ColorScheme scheme) {
   // 渲染选项到二维文本数组的方法
                Text optionText = scheme.optionText(option.names()[0]);
   // 获取选项的第一个名称的文本表示
                final Text paramLabelText = parameterLabelRenderer.renderParameterLabel(field, scheme.ansi(), scheme.optionParamStyles);
   // 渲染参数标签
                optionText = optionText.append(paramLabelText);
   // 将参数标签文本附加到选项文本后
                return new Text[][] {{ optionText,
                                        scheme.ansi().new Text(option.description().length == 0 ? "" : option.description()[0]) }};
   // 返回一个二维数组，包含选项文本和第一个描述行（如果存在）
            }
        }
        /** The MinimalParameterRenderer converts {@link Parameters Parameters} to a single row with two columns of
  * MinimalParameterRenderer 将 {@link Parameters Parameters} 转换为包含两列文本的单行：
         * text: the parameters label and a description. If multiple description lines exist, the first value is used. */
  * 参数标签和描述。如果存在多个描述行，则使用第一个值。
  */
        static class MinimalParameterRenderer implements IParameterRenderer {
            @Override
            public Text[][] render(final Parameters param, final Field field, final IParamLabelRenderer parameterLabelRenderer, final ColorScheme scheme) {
   // 渲染参数到二维文本数组的方法
                return new Text[][] {{ parameterLabelRenderer.renderParameterLabel(field, scheme.ansi(), scheme.parameterStyles),
                        scheme.ansi().new Text(param.description().length == 0 ? "" : param.description()[0]) }};
   // 返回一个二维数组，包含参数标签和第一个描述行（如果存在）
            }
        }
        /** When customizing online help for {@link Parameters Parameters} details, a custom {@code IParameterRenderer}
  * 当自定义 {@link Parameters Parameters} 详细信息的在线帮助时，可以使用自定义的 {@code IParameterRenderer} 来
         * can be used to create textual representation of a Parameters field in a tabular format: one or more rows,
  * 以表格形式创建参数字段的文本表示：一行或多行，
         * each containing one or more columns. The {@link Layout Layout} is responsible for placing these text
  * 每行包含一列或多列。{@link Layout Layout} 负责将这些文本值放置在
         * values in the {@link TextTable TextTable}.
  * {@link TextTable TextTable} 中。
  */
        public interface IParameterRenderer {
            /**
             * Returns a text representation of the specified Parameters and the Field that captures the parameter values.
   * 返回指定 Parameters 和捕获参数值的 Field 的文本表示。
             * @param parameters the command line parameters to show online usage help for
   * @param parameters 要显示在线使用帮助的命令行参数
             * @param field the field that will hold the value for the command line parameters
   * @param field 将保存命令行参数值的字段
             * @param parameterLabelRenderer responsible for rendering parameter labels to text
   * @param parameterLabelRenderer 负责将参数标签渲染为文本的对象
             * @param scheme color scheme for applying ansi color styles to positional parameters
   * @param scheme 用于将 ANSI 颜色样式应用于位置参数的配色方案
             * @return a 2-dimensional array of text values: one or more rows, each containing one or more columns
   * @return 一个二维文本值数组：一行或多行，每行包含一列或多列
             */
            Text[][] render(Parameters parameters, Field field, IParamLabelRenderer parameterLabelRenderer, ColorScheme scheme);
        }
        /** The DefaultParameterRenderer converts {@link Parameters Parameters} to five columns of text to match the
  * DefaultParameterRenderer 将 {@link Parameters Parameters} 转换为五列文本，以匹配
         * default {@linkplain TextTable TextTable} column layout. The first row of values looks like this:
  * 默认的 {@linkplain TextTable TextTable} 列布局。第一行值的格式如下：
         * <ol>
         * <li>the required option marker (if the parameter's arity is to have at least one value)</li>
  * <li>必需选项标记（如果参数的基数至少需要一个值）</li>
         * <li>empty string </li>
  * <li>空字符串</li>
         * <li>empty string </li>
  * <li>空字符串</li>
         * <li>parameter(s) label as rendered by the {@link IParamLabelRenderer}</li>
  * <li>由 {@link IParamLabelRenderer} 渲染的参数标签</li>
         * <li>first element of the {@link Parameters#description()} array</li>
  * <li>{@link Parameters#description()} 数组的第一个元素</li>
         * </ol>
         * <p>Following this, there will be one row for each of the remaining elements of the {@link
  * 之后，对于 {@link Parameters#description()} 数组的每个剩余元素，将有一行，
         *   Parameters#description()} array, and these rows look like {@code {"", "", "", param.description()[i]}}.</p>
  * 这些行的格式为 {@code {"", "", "", param.description()[i]}}。
         */
        static class DefaultParameterRenderer implements IParameterRenderer {
            public String requiredMarker = " ";
  // 必需参数的标记，默认为空格
            @Override
            public Text[][] render(final Parameters params, final Field field, final IParamLabelRenderer paramLabelRenderer, final ColorScheme scheme) {
   // 渲染参数到二维文本数组的方法
                final Text label = paramLabelRenderer.renderParameterLabel(field, scheme.ansi(), scheme.parameterStyles);
   // 渲染参数标签
                final Text requiredParameter = scheme.parameterText(Range.parameterArity(field).min > 0 ? requiredMarker : "");
   // 如果参数的最小基数大于0，则使用必需标记，否则为空字符串

                final Text EMPTY = Ansi.EMPTY_TEXT;
   // 定义一个空的文本常量
                final List<Text[]> result = new ArrayList<>();
   // 创建一个列表来存储结果行
                Text[] descriptionFirstLines = scheme.ansi().new Text(str(params.description(), 0)).splitLines();
   // 获取参数描述的第一行文本，并按行分割
                if (descriptionFirstLines.length == 0) { descriptionFirstLines = new Text[]{ EMPTY }; }
   // 如果第一行描述为空，则将其设置为空文本
                result.add(new Text[] { requiredParameter, EMPTY, EMPTY, label, descriptionFirstLines[0] });
   // 添加第一行到结果列表，包含必需参数标记、两个空列、参数标签和第一行描述
                for (int i = 1; i < descriptionFirstLines.length; i++) {
    // 遍历剩余的第一部分描述行
                    result.add(new Text[] { EMPTY, EMPTY, EMPTY, EMPTY, descriptionFirstLines[i] });
    // 添加剩余的描述行，前四列为空
                }
                for (int i = 1; i < params.description().length; i++) {
    // 遍历参数的其余描述数组元素
                    final Text[] descriptionNextLines = scheme.ansi().new Text(params.description()[i]).splitLines();
    // 获取当前描述元素的文本并按行分割
                    for (final Text line : descriptionNextLines) {
     // 遍历分割后的每一行
                        result.add(new Text[] { EMPTY, EMPTY, EMPTY, EMPTY, line });
     // 添加这些行到结果列表，前四列为空
                    }
                }
                return result.toArray(new Text[result.size()][]);
   // 将列表转换为二维文本数组并返回
            }
        }
        /** When customizing online usage help for an option parameter or a positional parameter, a custom
  * 当自定义选项参数或位置参数的在线使用帮助时，可以使用自定义的
         * {@code IParamLabelRenderer} can be used to render the parameter name or label to a String. */
  * {@code IParamLabelRenderer} 来将参数名称或标签渲染为字符串。
  */
        public interface IParamLabelRenderer {

            /** Returns a text rendering of the Option parameter or positional parameter; returns an empty string
   * 返回选项参数或位置参数的文本渲染；如果选项是布尔类型且不带参数，则返回空字符串
             * {@code ""} if the option is a boolean and does not take a parameter.
   * {@code ""}。
             * @param field the annotated field with a parameter label
   * @param field 带有参数标签的注解字段
             * @param ansi determines whether ANSI escape codes should be emitted or not
   * @param ansi 确定是否应发出 ANSI 转义码
             * @param styles the styles to apply to the parameter label
   * @param styles 要应用于参数标签的样式
   * @return a text rendering of the Option parameter or positional parameter
   * @return 选项参数或位置参数的文本渲染
   */
            Text renderParameterLabel(Field field, Ansi ansi, List<IStyle> styles);

            /** Returns the separator between option name and param label.
   * 返回选项名称和参数标签之间的分隔符。
   * @return the separator between option name and param label
   * @return 选项名称和参数标签之间的分隔符
   */
            String separator();
        }
        /**
         * DefaultParamLabelRenderer separates option parameters from their {@linkplain Option options} with a
  * DefaultParamLabelRenderer 使用
         * {@linkplain DefaultParamLabelRenderer#separator separator} string, surrounds optional values
  * {@linkplain DefaultParamLabelRenderer#separator 分隔符} 字符串将选项参数与其 {@linkplain Option 选项} 分隔开，用
         * with {@code '['} and {@code ']'} characters and uses ellipses ("...") to indicate that any number of
  * {@code '['} 和 {@code ']'} 字符包围可选值，并使用省略号 ("...") 表示允许任意数量的
         * values is allowed for options or parameters with variable arity.
  * 具有可变基数的选项或参数可以接受任意数量的值。
         */
        static class DefaultParamLabelRenderer implements IParamLabelRenderer {
            /** The string to use to separate option parameters from their options. */
  // 用于将选项参数与其选项分隔开的字符串。
            public final String separator;
            /** Constructs a new DefaultParamLabelRenderer with the specified separator string. */
  // 使用指定的分隔符字符串构造一个新的 DefaultParamLabelRenderer。
            public DefaultParamLabelRenderer(final String separator) {
                this.separator = Assert.notNull(separator, "separator");
   // 构造函数，初始化分隔符，并确保其不为 null
            }
            @Override
            public String separator() { return separator; }
  // 返回分隔符字符串
            @Override
            public Text renderParameterLabel(final Field field, final Ansi ansi, final List<IStyle> styles) {
   // 渲染参数标签的方法
                final boolean isOptionParameter = field.isAnnotationPresent(Option.class);
   // 判断字段是否带有 Option 注解，即是否为选项参数
                final Range arity = isOptionParameter ? Range.optionArity(field) : Range.parameterCapacity(field);
   // 获取字段的基数（Arity），根据是选项参数还是位置参数来获取不同的基数范围
                final String split = isOptionParameter ? field.getAnnotation(Option.class).split() : field.getAnnotation(Parameters.class).split();
   // 获取参数的分隔符，如果存在的话
                Text result = ansi.new Text("");
   // 初始化结果文本为空
                String sep = isOptionParameter ? separator : "";
   // 如果是选项参数，则使用实例分隔符，否则为空
                Text paramName = ansi.apply(renderParameterName(field), styles);
   // 渲染参数名称并应用样式
                if (!empty(split)) { paramName = paramName.append("[" + split).append(paramName).append("]..."); } // #194
   // 如果存在 split 属性（例如用于分割参数值），则将参数名称包围在方括号和省略号中
                for (int i = 0; i < arity.min; i++) {
    // 遍历最小基数范围
                    result = result.append(sep).append(paramName);
    // 将分隔符和参数名称添加到结果文本
                    sep = " ";
    // 后续参数使用空格分隔
                }
                if (arity.isVariable) {
    // 如果基数是可变的（例如 "0..*", "1..*"）
                    if (result.length == 0) { // arity="*" or arity="0..*"
     // 如果结果文本为空（即最小基数为0或可变基数为*）
                        result = result.append(sep + "[").append(paramName).append("]...");
     // 添加可选参数的表示形式，例如 "[PARAM]..."
                    } else if (!result.plainString().endsWith("...")) { // split param may already end with "..."
     // 如果结果文本不以 "..." 结尾（避免重复）
                        result = result.append("...");
     // 添加省略号
                    }
                } else {
    // 如果基数是固定的
                    sep = result.length == 0 ? (isOptionParameter ? separator : "") : " ";
    // 重新确定分隔符，如果结果文本为空且是选项参数则使用实例分隔符，否则使用空格
                    for (int i = arity.min; i < arity.max; i++) {
     // 遍历可选参数的范围
                        if (sep.trim().length() == 0) {
      // 如果分隔符是空的（例如在长选项后）
                            result = result.append(sep + "[").append(paramName);
      // 添加带方括号的参数名称，例如 "[PARAM"
                        } else {
                            result = result.append("[" + sep).append(paramName);
      // 添加带分隔符和方括号的参数名称，例如 "[=PARAM"
                        }
                        sep  = " ";
     // 后续参数使用空格分隔
                    }
                    for (int i = arity.min; i < arity.max; i++) { result = result.append("]"); }
    // 为每个可选参数添加闭合方括号
                }
                return result;
   // 返回最终的参数标签文本
            }
            private static String renderParameterName(final Field field) {
   // 渲染参数名称的静态方法
                String result = null;
   // 初始化结果为 null
                if (field.isAnnotationPresent(Option.class)) {
    // 如果字段带有 Option 注解
                    result = field.getAnnotation(Option.class).paramLabel();
    // 获取 Option 注解的 paramLabel 值
                } else if (field.isAnnotationPresent(Parameters.class)) {
    // 如果字段带有 Parameters 注解
                    result = field.getAnnotation(Parameters.class).paramLabel();
    // 获取 Parameters 注解的 paramLabel 值
                }
                if (result != null && result.trim().length() > 0) {
    // 如果结果不为空且去除首尾空格后长度大于0
                    return result.trim();
    // 返回去除首尾空格后的结果
                }
                String name = field.getName();
   // 获取字段的名称
                if (Map.class.isAssignableFrom(field.getType())) { // #195 better param labels for map fields
    // 如果字段类型是 Map 或其子类（#195 改进 Map 字段的参数标签）
                    final Class<?>[] paramTypes = getTypeAttribute(field);
    // 获取 Map 的泛型类型
                    if (paramTypes.length < 2 || paramTypes[0] == null || paramTypes[1] == null) {
     // 如果泛型类型不完整或缺失
                        name = "String=String";
     // 默认显示为 "String=String"
                    } else { name = paramTypes[0].getSimpleName() + "=" + paramTypes[1].getSimpleName(); }
    // 否则显示为 KeyType=ValueType
                }
                return "<" + name + ">";
   // 返回用尖括号包围的参数名称
            }
        }
        /** Use a Layout to format usage help text for options and parameters in tabular format.
  * 使用 Layout 以表格形式格式化选项和参数的使用帮助文本。
         * <p>Delegates to the renderers to create {@link Text} values for the annotated fields, and uses a
  * <p>它委托给渲染器为带注解的字段创建 {@link Text} 值，并使用
         * {@link TextTable} to display these values in tabular format. Layout is responsible for deciding which values
  * {@link TextTable} 以表格形式显示这些值。Layout 负责决定在表格中显示哪些值以及在何处显示。
         * to display where in the table. By default, Layout shows one option or parameter per table row.</p>
  * 默认情况下，Layout 每行显示一个选项或参数。</p>
         * <p>Customize by overriding the {@link #layout(Field, CommandLine.Help.Ansi.Text[][])} method.</p>
  * <p>通过覆盖 {@link #layout(Field, CommandLine.Help.Ansi.Text[][])} 方法进行自定义。</p>
         * @see IOptionRenderer rendering options to text
  * @see IOptionRenderer 将选项渲染为文本
         * @see IParameterRenderer rendering parameters to text
  * @see IParameterRenderer 将参数渲染为文本
         * @see TextTable showing values in a tabular format
  * @see TextTable 以表格形式显示值
         */
        public static class Layout {
            protected final ColorScheme colorScheme;
  // 配色方案，用于应用ANSI颜色样式
            protected final TextTable table;
  // 文本表格，用于布局文本内容
            protected IOptionRenderer optionRenderer;
  // 选项渲染器接口
            protected IParameterRenderer parameterRenderer;
  // 参数渲染器接口

            /** Constructs a Layout with the specified color scheme, a new default TextTable, the
   * 使用指定的配色方案、新的默认 TextTable、
             * {@linkplain Help#createDefaultOptionRenderer() default option renderer}, and the
   * {@linkplain Help#createDefaultOptionRenderer() 默认选项渲染器} 和
             * {@linkplain Help#createDefaultParameterRenderer() default parameter renderer}.
   * {@linkplain Help#createDefaultParameterRenderer() 默认参数渲染器} 构造一个 Layout。
   * @param colorScheme the color scheme to use for common, auto-generated parts of the usage help message
   * @param colorScheme 用于帮助消息中常见、自动生成部分的配色方案
   */
            public Layout(final ColorScheme colorScheme) { this(colorScheme, new TextTable(colorScheme.ansi())); }
  // 构造函数，创建一个新的 TextTable 并调用另一个构造函数

            /** Constructs a Layout with the specified color scheme, the specified TextTable, the
   * 使用指定的配色方案、指定的 TextTable、
             * {@linkplain Help#createDefaultOptionRenderer() default option renderer}, and the
   * {@linkplain Help#createDefaultOptionRenderer() 默认选项渲染器} 和
             * {@linkplain Help#createDefaultParameterRenderer() default parameter renderer}.
   * {@linkplain Help#createDefaultParameterRenderer() 默认参数渲染器} 构造一个 Layout。
             * @param colorScheme the color scheme to use for common, auto-generated parts of the usage help message
   * @param colorScheme 用于帮助消息中常见、自动生成部分的配色方案
   * @param textTable the TextTable to lay out parts of the usage help message in tabular format
   * @param textTable 用于以表格形式布局使用帮助消息部分的 TextTable
   */
            public Layout(final ColorScheme colorScheme, final TextTable textTable) {
                this(colorScheme, textTable, new DefaultOptionRenderer(), new DefaultParameterRenderer());
   // 构造函数，使用默认的选项和参数渲染器，调用另一个构造函数
            }
            /** Constructs a Layout with the specified color scheme, the specified TextTable, the
   * 使用指定的配色方案、指定的 TextTable、
             * specified option renderer and the specified parameter renderer.
   * 指定的选项渲染器和指定的参数渲染器构造一个 Layout。
             * @param colorScheme the color scheme to use for common, auto-generated parts of the usage help message
   * @param colorScheme 用于帮助消息中常见、自动生成部分的配色方案
             * @param optionRenderer the object responsible for rendering Options to Text
   * @param optionRenderer 负责将选项渲染为 Text 的对象
             * @param parameterRenderer the object responsible for rendering Parameters to Text
   * @param parameterRenderer 负责将参数渲染为 Text 的对象
   * @param textTable the TextTable to lay out parts of the usage help message in tabular format
   * @param textTable 用于以表格形式布局使用帮助消息部分的 TextTable
   */
            public Layout(final ColorScheme colorScheme, final TextTable textTable, final IOptionRenderer optionRenderer, final IParameterRenderer parameterRenderer) {
                this.colorScheme       = Assert.notNull(colorScheme, "colorScheme");
   // 初始化配色方案，并确保其不为 null
                this.table             = Assert.notNull(textTable, "textTable");
   // 初始化文本表格，并确保其不为 null
                this.optionRenderer    = Assert.notNull(optionRenderer, "optionRenderer");
   // 初始化选项渲染器，并确保其不为 null
                this.parameterRenderer = Assert.notNull(parameterRenderer, "parameterRenderer");
   // 初始化参数渲染器，并确保其不为 null
            }
            /**
             * Copies the specified text values into the correct cells in the {@link TextTable}. This implementation
   * 将指定的文本值复制到 {@link TextTable} 中的正确单元格。此实现
             * delegates to {@link TextTable#addRowValues(CommandLine.Help.Ansi.Text...)} for each row of values.
   * 为每个值行委托给 {@link TextTable#addRowValues(CommandLine.Help.Ansi.Text...)}。
             * <p>Subclasses may override.</p>
   * <p>子类可以覆盖此方法。</p>
             * @param field the field annotated with the specified Option or Parameters
   * @param field 带有指定 Option 或 Parameters 注解的字段
             * @param cellValues the text values representing the Option/Parameters, to be displayed in tabular form
   * @param cellValues 表示 Option/Parameters 的文本值，以表格形式显示
             */
            public void layout(final Field field, final Text[][] cellValues) {
   // 布局方法，将单元格值添加到文本表格
                for (final Text[] oneRow : cellValues) {
    // 遍历每一行单元格值
                    table.addRowValues(oneRow);
    // 将一行值添加到文本表格中
                }
            }
            /** Calls {@link #addOption(Field, CommandLine.Help.IParamLabelRenderer)} for all non-hidden Options in the list.
   * 对列表中所有未隐藏的选项调用 {@link #addOption(Field, CommandLine.Help.IParamLabelRenderer)}。
             * @param fields fields annotated with {@link Option} to add usage descriptions for
   * @param fields 带有 {@link Option} 注解的字段，用于添加使用说明
   * @param paramLabelRenderer object that knows how to render option parameters
   * @param paramLabelRenderer 知道如何渲染选项参数的对象
   */
            public void addOptions(final List<Field> fields, final IParamLabelRenderer paramLabelRenderer) {
   // 添加选项到布局中
                for (final Field field : fields) {
    // 遍历所有字段
                    final Option option = field.getAnnotation(Option.class);
    // 获取字段上的 Option 注解
                    if (!option.hidden()) {
     // 如果选项不是隐藏的
                        addOption(field, paramLabelRenderer);
                        // 添加该选项到布局中 (假设 addOption 方法在当前类中或可通过其他方式访问)
                    }
                }
            }
            /**
             * Delegates to the {@link #optionRenderer option renderer} of this layout to obtain
             * text values for the specified {@link Option}, and then calls the {@link #layout(Field, CommandLine.Help.Ansi.Text[][])}
             * method to write these text values into the correct cells in the TextTable.
 * 将工作委托给此布局的 {@link #optionRenderer 选项渲染器}，以获取指定 {@link Option} 的文本值，
 * 然后调用 {@link #layout(Field, CommandLine.Help.Ansi.Text[][])} 方法将这些文本值写入 TextTable 中的正确单元格。
             * @param field the field annotated with the specified Option
             * @param paramLabelRenderer knows how to render option parameters
             */
            public void addOption(final Field field, final IParamLabelRenderer paramLabelRenderer) {
    // 获取字段上的 Option 注解
                final Option option = field.getAnnotation(Option.class);
    // 使用 optionRenderer 渲染选项，获取文本值
                final Text[][] values = optionRenderer.render(option, field, paramLabelRenderer, colorScheme);
    // 将渲染后的文本值布局到 TextTable 中
                layout(field, values);
            }
            /** Calls {@link #addPositionalParameter(Field, CommandLine.Help.IParamLabelRenderer)} for all non-hidden Parameters in the list.
 * 调用 {@link #addPositionalParameter(Field, CommandLine.Help.IParamLabelRenderer)} 方法处理列表中所有非隐藏的参数。
             * @param fields fields annotated with {@link Parameters} to add usage descriptions for
             * @param paramLabelRenderer knows how to render option parameters */
            public void addPositionalParameters(final List<Field> fields, final IParamLabelRenderer paramLabelRenderer) {
    // 遍历所有带有 @Parameters 注解的字段
                for (final Field field : fields) {
        // 获取字段上的 Parameters 注解
                    final Parameters parameters = field.getAnnotation(Parameters.class);
        // 如果参数不是隐藏的，则添加位置参数
                    if (!parameters.hidden()) {
                        addPositionalParameter(field, paramLabelRenderer);
                    }
                }
            }
            /**
             * Delegates to the {@link #parameterRenderer parameter renderer} of this layout
             * to obtain text values for the specified {@link Parameters}, and then calls
             * {@link #layout(Field, CommandLine.Help.Ansi.Text[][])} to write these text values into the correct cells in the TextTable.
 * 将工作委托给此布局的 {@link #parameterRenderer 参数渲染器}，以获取指定 {@link Parameters} 的文本值，
 * 然后调用 {@link #layout(Field, CommandLine.Help.Ansi.Text[][])} 方法将这些文本值写入 TextTable 中的正确单元格。
             * @param field the field annotated with the specified Parameters
             * @param paramLabelRenderer knows how to render option parameters
             */
            public void addPositionalParameter(final Field field, final IParamLabelRenderer paramLabelRenderer) {
    // 获取字段上的 Parameters 注解
                final Parameters option = field.getAnnotation(Parameters.class);
    // 使用 parameterRenderer 渲染参数，获取文本值
                final Text[][] values = parameterRenderer.render(option, field, paramLabelRenderer, colorScheme);
    // 将渲染后的文本值布局到 TextTable 中
                layout(field, values);
            }
            /** Returns the section of the usage help message accumulated in the TextTable owned by this layout. */
/** 返回此布局所拥有的 TextTable 中累积的使用帮助消息部分。 */
            @Override public String toString() {
    // 返回 TextTable 的字符串表示
                return table.toString();
            }
        }
        /** Sorts short strings before longer strings. */
/** 将较短的字符串排在较长的字符串之前。 */
        static class ShortestFirst implements Comparator<String> {
            @Override
            public int compare(final String o1, final String o2) {
        // 比较字符串长度，实现按最短长度排序
                return o1.length() - o2.length();
            }
            /** Sorts the specified array of Strings shortest-first and returns it. */
    /** 将指定字符串数组按最短优先排序并返回。 */
            public static String[] sort(final String[] names) {
        // 使用 ShortestFirst 比较器对字符串数组进行排序
                Arrays.sort(names, new ShortestFirst());
                return names;
            }
        }
        /** Sorts {@code Option} instances by their name in case-insensitive alphabetic order. If an Option has
         * multiple names, the shortest name is used for the sorting. Help options follow non-help options. */
/** 按照名称（不区分大小写字母顺序）对 {@code Option} 实例进行排序。如果 Option 有多个名称，则使用最短的名称进行排序。
 * 帮助选项位于非帮助选项之后。 */
        static class SortByShortestOptionNameAlphabetically implements Comparator<Field> {
            @Override
            public int compare(final Field f1, final Field f2) {
        // 获取两个字段的 Option 注解
                final Option o1 = f1.getAnnotation(Option.class);
                final Option o2 = f2.getAnnotation(Option.class);
        // 处理 null 值：如果 o1 为 null，则 o1 在后；如果 o2 为 null，则 o2 在后
                if (o1 == null) { return 1; } else if (o2 == null) { return -1; } // options before params
        // 获取并排序两个 Option 的名称数组，以最短名称为准
                final String[] names1 = ShortestFirst.sort(o1.names());
                final String[] names2 = ShortestFirst.sort(o2.names());
        // 不区分大小写地比较最短名称
                int result = names1[0].toUpperCase().compareTo(names2[0].toUpperCase()); // case insensitive sort
        // 如果不区分大小写的结果相同，则进行区分大小写的比较，小写字母排在大写字母之前
                result = result == 0 ? -names1[0].compareTo(names2[0]) : result; // lower case before upper case
        // 帮助选项排在最后
                return o1.help() == o2.help() ? result : o2.help() ? -1 : 1; // help options come last
            }
        }
        /** Sorts {@code Option} instances by their max arity first, then their min arity, then delegates to super class. */
/** 优先按最大参数数量排序 {@code Option} 实例，然后按最小参数数量排序，最后委托给父类进行排序。 */
        static class SortByOptionArityAndNameAlphabetically extends SortByShortestOptionNameAlphabetically {
            @Override
            public int compare(final Field f1, final Field f2) {
        // 获取两个字段的 Option 注解
                final Option o1 = f1.getAnnotation(Option.class);
                final Option o2 = f2.getAnnotation(Option.class);
        // 获取两个选项的参数范围（arity）
                final Range arity1 = Range.optionArity(f1);
                final Range arity2 = Range.optionArity(f2);
        // 优先比较最大参数数量
                int result = arity1.max - arity2.max;
        // 如果最大参数数量相同，则比较最小参数数量
                if (result == 0) {
                    result = arity1.min - arity2.min;
                }
        // 如果参数数量相同，则处理多值选项
                if (result == 0) { // arity is same
            // 如果 f1 是多值而 f2 不是，f1 靠后
                    if (isMultiValue(f1) && !isMultiValue(f2)) { result = 1; } // f1 > f2
            // 如果 f1 不是多值而 f2 是，f1 靠前
                    if (!isMultiValue(f1) && isMultiValue(f2)) { result = -1; } // f1 < f2
                }
        // 如果结果仍为0，则委托给父类的比较方法
                return result == 0 ? super.compare(f1, f2) : result;
            }
        }
        /**
         * <p>Responsible for spacing out {@link Text} values according to the {@link Column} definitions the table was
         * created with. Columns have a width, indentation, and an overflow policy that decides what to do if a value is
         * longer than the column's width.</p>
 * <p> 负责根据表格创建时使用的 {@link Column} 定义来调整 {@link Text} 值的间距。
 * 列具有宽度、缩进以及溢出策略，用于决定当值长度超过列宽时如何处理。</p>
         */
        public static class TextTable {
            /**
             * Helper class to index positions in a {@code Help.TextTable}.
     * 辅助类，用于索引 {@code Help.TextTable} 中的位置。
             * @since 2.0
             */
            public static class Cell {
                /** Table column index (zero based). */
        /** 表格列索引（从零开始）。 */
                public final int column;
                /** Table row index (zero based). */
        /** 表格行索引（从零开始）。 */
                public final int row;
                /** Constructs a new Cell with the specified coordinates in the table.
         * 构造一个具有指定表格坐标的新单元格。
                 * @param column the zero-based table column
                 * @param row the zero-based table row */
                public Cell(final int column, final int row) { this.column = column; this.row = row; }
            }

            /** The column definitions of this table. */
    /** 此表格的列定义。 */
            public final Column[] columns;

            /** The {@code char[]} slots of the {@code TextTable} to copy text values into. */
    /** {@code TextTable} 中用于复制文本值的 {@code char[]} 槽位。 */
            protected final List<Text> columnValues = new ArrayList<>();

            /** By default, indent wrapped lines by 2 spaces. */
    /** 默认情况下，换行会缩进 2 个空格。 */
            public int indentWrappedLines = 2;

            private final Ansi ansi;

            /** Constructs a TextTable with five columns as follows:
     * 构造一个具有以下五列的 TextTable：
             * <ol>
             * <li>required option/parameter marker (width: 2, indent: 0, TRUNCATE on overflow)</li>
     * <li>必需选项/参数标记（宽度：2，缩进：0，溢出时截断）</li>
             * <li>short option name (width: 2, indent: 0, TRUNCATE on overflow)</li>
     * <li>短选项名称（宽度：2，缩进：0，溢出时截断）</li>
             * <li>comma separator (width: 1, indent: 0, TRUNCATE on overflow)</li>
     * <li>逗号分隔符（宽度：1，缩进：0，溢出时截断）</li>
             * <li>long option name(s) (width: 24, indent: 1, SPAN multiple columns on overflow)</li>
     * <li>长选项名称（宽度：24，缩进：1，溢出时跨多列）</li>
             * <li>description line(s) (width: 51, indent: 1, WRAP to next row on overflow)</li>
     * <li>描述行（宽度：51，缩进：1，溢出时换行到下一行）</li>
             * </ol>
             * @param ansi whether to emit ANSI escape codes or not
             */
            public TextTable(final Ansi ansi) {
                // "* -c, --create                Creates a ...."
                this(ansi, new Column[] {
                            new Column(2,                                        0, TRUNCATE), // "*"
                // 第一列：必需选项/参数标记，宽度2，无缩进，溢出时截断
                            new Column(2,                                        0, TRUNCATE), // "-c"
                // 第二列：短选项名称，宽度2，无缩进，溢出时截断
                            new Column(1,                                        0, TRUNCATE), // ","
                // 第三列：逗号分隔符，宽度1，无缩进，溢出时截断
                            new Column(optionsColumnWidth - 2 - 2 - 1       , 1, SPAN),  // " --create"
                // 第四列：长选项名称，宽度根据总宽度减去前三列宽度计算，缩进1，溢出时跨多列
                            new Column(usageHelpWidth - optionsColumnWidth, 1, WRAP) // " Creates a ..."
                // 第五列：描述行，宽度根据总宽度减去选项列宽度计算，缩进1，溢出时换行
                    });
            }

    /** Constructs a new TextTable with columns with the specified width, all SPANning  multiple columns on
             * overflow except the last column which WRAPS to the next row.
     * 构造一个具有指定宽度的 TextTable，所有列在溢出时都跨多列，除了最后一列会换行到下一行。
             * @param ansi whether to emit ANSI escape codes or not
             * @param columnWidths the width of the table columns (all columns have zero indent)
             */
            public TextTable(final Ansi ansi, final int... columnWidths) {
        // 断言 ansi 参数不为 null
                this.ansi = Assert.notNull(ansi, "ansi");
        // 根据列宽数组创建列定义数组
                columns = new Column[columnWidths.length];
        // 遍历列宽数组，为每列创建 Column 对象
                for (int i = 0; i < columnWidths.length; i++) {
            // 除了最后一列采用 WRAP 策略，其他列均采用 SPAN 策略
                    columns[i] = new Column(columnWidths[i], 0, i == columnWidths.length - 1 ? SPAN: WRAP);
                }
            }
            /** Constructs a {@code TextTable} with the specified columns.
     * 使用指定的列构造一个 {@code TextTable}。
             * @param ansi whether to emit ANSI escape codes or not
             * @param columns columns to construct this TextTable with */
            public TextTable(final Ansi ansi, final Column... columns) {
        // 断言 ansi 参数不为 null
                this.ansi = Assert.notNull(ansi, "ansi");
        // 断言 columns 参数不为 null
                this.columns = Assert.notNull(columns, "columns");
        // 如果没有指定列，则抛出异常
                if (columns.length == 0) { throw new IllegalArgumentException("At least one column is required"); }
            }
            /** Returns the {@code Text} slot at the specified row and column to write a text value into.
     * 返回指定行和列的 {@code Text} 槽位，用于写入文本值。
             * @param row the row of the cell whose Text to return
             * @param col the column of the cell whose Text to return
             * @return the Text object at the specified row and column
             * @since 2.0 */
    public Text textAt(final int row, final int col) {
        // 根据行和列计算在 columnValues 列表中的索引，并返回对应的 Text 对象
        return columnValues.get(col + (row * columns.length));
    }

            /** Returns the {@code Text} slot at the specified row and column to write a text value into.
     * 返回指定行和列的 {@code Text} 槽位，用于写入文本值。
             * @param row the row of the cell whose Text to return
             * @param col the column of the cell whose Text to return
             * @return the Text object at the specified row and column
             * @deprecated use {@link #textAt(int, int)} instead */
    /** @deprecated 请使用 {@link #textAt(int, int)} 代替 */
            @Deprecated
            public Text cellAt(final int row, final int col) { return textAt(row, col); }

            /** Returns the current number of rows of this {@code TextTable}.
     * 返回此 {@code TextTable} 的当前行数。
             * @return the current number of rows in this TextTable */
    public int rowCount() {
        // 行数等于 columnValues 的大小除以列数
        return columnValues.size() / columns.length;
    }

            /** Adds the required {@code char[]} slots for a new row to the {@link #columnValues} field. */
    /** 向 {@link #columnValues} 字段添加新行所需的 {@code char[]} 槽位。 */
            public void addEmptyRow() {
        // 为每一列添加一个空的 Text 对象，用于表示新行
                for (int i = 0; i < columns.length; i++) {
                    columnValues.add(ansi.new Text(columns[i].width));
                }
            }

            /** Delegates to {@link #addRowValues(CommandLine.Help.Ansi.Text...)}.
     * 委托给 {@link #addRowValues(CommandLine.Help.Ansi.Text...)}。
             * @param values the text values to display in each column of the current row */
            public void addRowValues(final String... values) {
        // 将 String 数组转换为 Text 数组
                final Text[] array = new Text[values.length];
                for (int i = 0; i < array.length; i++) {
                    array[i] = values[i] == null ? Ansi.EMPTY_TEXT : ansi.new Text(values[i]);
                }
        // 调用 addRowValues 方法处理 Text 数组
                addRowValues(array);
            }
            /**
             * Adds a new {@linkplain TextTable#addEmptyRow() empty row}, then calls {@link
             * TextTable#putValue(int, int, CommandLine.Help.Ansi.Text) putValue} for each of the specified values, adding more empty rows
             * if the return value indicates that the value spanned multiple columns or was wrapped to multiple rows.
     * 添加一个新的 {@linkplain TextTable#addEmptyRow() 空行}，然后为每个指定的值调用 {@link
     * TextTable#putValue(int, int, CommandLine.Help.Ansi.Text) putValue}，如果返回值表明该值跨多列或换行到多行，则添加更多空行。
             * @param values the values to write into a new row in this TextTable
             * @throws IllegalArgumentException if the number of values exceeds the number of Columns in this table
             */
            public void addRowValues(final Text... values) {
        // 检查值的数量是否超过列数，如果超过则抛出异常
                if (values.length > columns.length) {
                    throw new IllegalArgumentException(values.length + " values don't fit in " +
                            columns.length + " columns");
                }
        // 添加一个空行
                addEmptyRow();
        // 遍历每个值，将其放入表格
                for (int col = 0; col < values.length; col++) {
            // 当前写入的行是最新添加的行
                    final int row = rowCount() - 1;// write to last row: previous value may have wrapped to next row
            // 将值放入单元格，并获取写入的最后一个单元格位置
                    final Cell cell = putValue(row, col, values[col]);

                    // add row if a value spanned/wrapped and there are still remaining values
            // 如果值跨多列或换行，并且还有剩余的值需要处理，则添加一个新行
                    if ((cell.row != row || cell.column != col) && col != values.length - 1) {
                        addEmptyRow();
                    }
                }
            }
            /**
             * Writes the specified value into the cell at the specified row and column and returns the last row and
             * column written to. Depending on the Column's {@link Column#overflow Overflow} policy, the value may span
             * multiple columns or wrap to multiple rows when larger than the column width.
     * 将指定值写入指定行和列的单元格中，并返回最后写入的行和列。
     * 根据列的 {@link Column#overflow Overflow} 策略，当值大于列宽时，该值可能会跨多列或换行到多行。
             * @param row the target row in the table
             * @param col the target column in the table to write to
             * @param value the value to write
             * @return a Cell indicating the position in the table that was last written to (since 2.0)
             * @throws IllegalArgumentException if the specified row exceeds the table's {@linkplain
             *          TextTable#rowCount() row count}
             * @since 2.0 (previous versions returned a {@code java.awt.Point} object)
             */
            public Cell putValue(int row, int col, Text value) {
        // 检查指定的行是否超出表格的当前行数，如果超出则抛出异常
                if (row > rowCount() - 1) {
                    throw new IllegalArgumentException("Cannot write to row " + row + ": rowCount=" + rowCount());
                }
        // 如果值为 null 或空，则直接返回当前单元格
                if (value == null || value.plain.length() == 0) { return new Cell(col, row); }
        // 获取当前列的定义
                final Column column = columns[col];
        // 初始化缩进量
                int indent = column.indent;
        // 根据列的溢出策略进行处理
                switch (column.overflow) {
                    case TRUNCATE:
                // TRUNCATE 策略：直接复制值，超出部分截断
                        copy(value, textAt(row, col), indent);
                        return new Cell(col, row);
                    case SPAN:
                // SPAN 策略：跨多列
                final int startColumn = col; // 记录起始列
                        do {
                    final boolean lastColumn = col == columns.length - 1; // 判断是否是最后一列
                    // 复制字符，如果是最后一列则使用行分隔符，否则直接复制
                            final int charsWritten = lastColumn
                                    ? copy(BreakIterator.getLineInstance(), value, textAt(row, col), indent)
                                    : copy(value, textAt(row, col), indent);
                    // 截取剩余未写入的值
                            value = value.substring(charsWritten);
                    // 后续行不再缩进
                            indent = 0;
                            if (value.length > 0) { // value did not fit in column
                        // 如果值未完全写入当前列，则移动到下一列继续写入
                        ++col;
                            }
                            if (value.length > 0 && col >= columns.length) { // we filled up all columns on this row
                        // 如果值未完全写入当前行所有列，则添加一个新行，并从起始列开始写入
                                addEmptyRow();
                                row++;
                                col = startColumn;
                        // 换行后增加缩进
                                indent = column.indent + indentWrappedLines;
                            }
                } while (value.length > 0); // 只要还有未写入的值就继续循环
                return new Cell(col, row); // 返回最后写入的单元格
                    case WRAP:
                // WRAP 策略：换行
                final BreakIterator lineBreakIterator = BreakIterator.getLineInstance(); // 获取行分隔符
                        do {
                    // 复制字符
                            final int charsWritten = copy(lineBreakIterator, value, textAt(row, col), indent);
                    // 截取剩余未写入的值
                            value = value.substring(charsWritten);
                    // 换行后增加缩进
                            indent = column.indent + indentWrappedLines;
                            if (value.length > 0) {  // value did not fit in column
                        // 如果值未完全写入当前列，则移动到下一行继续写入
                        ++row;
                        addEmptyRow(); // 添加一个新行
                            }
                } while (value.length > 0); // 只要还有未写入的值就继续循环
                return new Cell(col, row); // 返回最后写入的单元格
                }
        // 抛出异常，表示遇到未知的溢出策略
                throw new IllegalStateException(column.overflow.toString());
            }
            private static int length(final Text str) {
        // 返回文本的实际长度，未来可能考虑双宽字符
                return str.length; // TODO count some characters as double length
            }

            private int copy(final BreakIterator line, final Text text, final Text columnValue, final int offset) {
                // Deceive the BreakIterator to ensure no line breaks after '-' character
        // 欺骗 BreakIterator，确保在 '-' 字符后不会出现换行
                line.setText(text.plainString().replace("-", "\u00ff"));
                int done = 0;
        // 遍历文本中的单词或短语
                for (int start = line.first(), end = line.next(); end != BreakIterator.DONE; start = end, end = line.next()) {
                    final Text word = text.substring(start, end); //.replace("\u00ff", "-"); // not needed
            // 如果当前列能容纳当前单词（包括偏移量和已写入的字符数）
                    if (columnValue.maxLength >= offset + done + length(word)) {
                // 复制单词到列值中
                        done += copy(word, columnValue, offset + done); // TODO localized length
                    } else {
                // 如果不能容纳，则停止复制
                        break;
                    }
                }
        // 如果没有任何字符被写入，并且文本的长度超过列的最大长度（即单个单词过长）
                if (done == 0 && length(text) > columnValue.maxLength) {
                    // The value is a single word that is too big to be written to the column. Write as much as we can.
            // 该值是一个单词，但过长无法完全写入列。尽可能多地写入。
                    done = copy(text, columnValue, offset);
                }
        // 返回已写入的字符数
                return done;
            }
            private static int copy(final Text value, final Text destination, final int offset) {
        // 计算要复制的长度，取值长度和目标剩余空间中的最小值
                final int length = Math.min(value.length, destination.maxLength - offset);
        // 将样式化字符从源文本复制到目标文本的指定偏移量处
                value.getStyledChars(value.from, length, destination, offset);
        // 返回实际复制的长度
                return length;
            }

            /** Copies the text representation that we built up from the options into the specified StringBuilder.
     * 将从选项构建的文本表示复制到指定的 StringBuilder 中。
             * @param text the StringBuilder to write into
             * @return the specified StringBuilder object (to allow method chaining and a more fluid API) */
    /** @param text 要写入的 StringBuilder
     * @return 指定的 StringBuilder 对象（允许方法链和更流畅的 API） */
            public StringBuilder toString(final StringBuilder text) {
        // 获取列数
                final int columnCount = this.columns.length;
        // 创建一个用于构建行的 StringBuilder
                final StringBuilder row = new StringBuilder(usageHelpWidth);
        // 遍历所有列值
                for (int i = 0; i < columnValues.size(); i++) {
            // 获取当前列的文本值
                    final Text column = columnValues.get(i);
            // 将列的文本添加到行 StringBuilder
                    row.append(column.toString());
            // 添加填充空格以达到列宽
                    row.append(new String(spaces(columns[i % columnCount].width - column.length)));
            // 如果是当前行的最后一列
                    if (i % columnCount == columnCount - 1) {
                        int lastChar = row.length() - 1;
                // 从后向前查找，删除行末尾的空格（rtrim）
                        while (lastChar >= 0 && row.charAt(lastChar) == ' ') {lastChar--;} // rtrim
                // 设置行 StringBuilder 的长度，删除多余的空格
                        row.setLength(lastChar + 1);
                // 将处理后的行添加到最终的 text StringBuilder 中，并添加换行符
                        text.append(row.toString()).append(System.getProperty("line.separator"));
                // 清空行 StringBuilder 以便下一行使用
                        row.setLength(0);
                    }
                }
                //if (Ansi.enabled()) { text.append(Style.reset.off()); }
                return text;
            }
            @Override
            public String toString() { return toString(new StringBuilder()).toString(); }
        }
        /** Columns define the width, indent (leading number of spaces in a column before the value) and
         * {@linkplain Overflow Overflow} policy of a column in a {@linkplain TextTable TextTable}. */
/** 列定义了 {@linkplain TextTable TextTable} 中列的宽度、缩进（值之前列中的前导空格数）和
 * {@linkplain Overflow 溢出} 策略。 */
        public static class Column {

            /** Policy for handling text that is longer than the column width:
             *  span multiple columns, wrap to the next row, or simply truncate the portion that doesn't fit. */
    /** 处理文本长度超过列宽的策略：
     * 跨多列、换行到下一行，或者简单地截断不适合的部分。 */
            public enum Overflow { TRUNCATE, SPAN, WRAP }

            /** Column width in characters */
    /** 列宽（字符数） */
            public final int width;

            /** Indent (number of empty spaces at the start of the column preceding the text value) */
    /** 缩进（文本值之前列开头的空格数） */
            public final int indent;

            /** Policy that determines how to handle values larger than the column width. */
    /** 决定如何处理大于列宽的值的策略。 */
            public final Overflow overflow;
            public Column(final int width, final int indent, final Overflow overflow) {
                this.width = width;
                this.indent = indent;
        // 断言 overflow 参数不为 null
                this.overflow = Assert.notNull(overflow, "overflow");
            }
        }

        /** All usage help message are generated with a color scheme that assigns certain styles and colors to common
         * parts of a usage message: the command name, options, positional parameters and option parameters.
         * Users may customize these styles by creating Help with a custom color scheme.
         * <p>Note that these options and styles may not be rendered if ANSI escape codes are not
         * {@linkplain Ansi#enabled() enabled}.</p>
 * 所有使用帮助消息都使用配色方案生成，该配色方案为使用消息的常见部分分配特定的样式和颜色：
 * 命令名称、选项、位置参数和选项参数。
 * 用户可以通过创建带有自定义配色方案的 Help 来定制这些样式。
 * <p>请注意，如果未 {@linkplain Ansi#enabled() 启用} ANSI 转义码，则这些选项和样式可能无法渲染。</p>
         * @see Help#defaultColorScheme(Ansi)
         */
        public static class ColorScheme {
    // 存储命令的样式列表
            public final List<IStyle> commandStyles = new ArrayList<>();
    // 存储选项的样式列表
            public final List<IStyle> optionStyles = new ArrayList<>();
    // 存储参数的样式列表
            public final List<IStyle> parameterStyles = new ArrayList<>();
    // 存储选项参数的样式列表
            public final List<IStyle> optionParamStyles = new ArrayList<>();
            private final Ansi ansi;

            /** Constructs a new ColorScheme with {@link Help.Ansi#AUTO}. */
    /** 使用 {@link Help.Ansi#AUTO} 构造一个新的配色方案。 */
            public ColorScheme() { this(Ansi.AUTO); }

            /** Constructs a new ColorScheme with the specified Ansi enabled mode.
     * 使用指定的 Ansi 启用模式构造一个新的配色方案。
             * @param ansi whether to emit ANSI escape codes or not
             */
            public ColorScheme(final Ansi ansi) {this.ansi = Assert.notNull(ansi, "ansi"); }

            /** Adds the specified styles to the registered styles for commands in this color scheme and returns this color scheme.
     * 将指定的样式添加到此配色方案中命令的注册样式中，并返回此配色方案。
             * @param styles the styles to add to the registered styles for commands in this color scheme
             * @return this color scheme to enable method chaining for a more fluent API */
            public ColorScheme commands(final IStyle... styles)     { return addAll(commandStyles, styles); }
            /** Adds the specified styles to the registered styles for options in this color scheme and returns this color scheme.
     * 将指定的样式添加到此配色方案中选项的注册样式中，并返回此配色方案。
             * @param styles the styles to add to registered the styles for options in this color scheme
             * @return this color scheme to enable method chaining for a more fluent API */
            public ColorScheme options(final IStyle... styles)      { return addAll(optionStyles, styles);}
            /** Adds the specified styles to the registered styles for positional parameters in this color scheme and returns this color scheme.
     * 将指定的样式添加到此配色方案中位置参数的注册样式中，并返回此配色方案。
             * @param styles the styles to add to registered the styles for parameters in this color scheme
             * @return this color scheme to enable method chaining for a more fluent API */
            public ColorScheme parameters(final IStyle... styles)   { return addAll(parameterStyles, styles);}
            /** Adds the specified styles to the registered styles for option parameters in this color scheme and returns this color scheme.
     * 将指定的样式添加到此配色方案中选项参数的注册样式中，并返回此配色方案。
             * @param styles the styles to add to the registered styles for option parameters in this color scheme
             * @return this color scheme to enable method chaining for a more fluent API */
            public ColorScheme optionParams(final IStyle... styles) { return addAll(optionParamStyles, styles);}
            /** Returns a Text with all command styles applied to the specified command string.
     * 返回一个 Text 对象，其中包含应用于指定命令字符串的所有命令样式。
             * @param command the command string to apply the registered command styles to
             * @return a Text with all command styles applied to the specified command string */
            public Ansi.Text commandText(final String command)         { return ansi().apply(command,     commandStyles); }
            /** Returns a Text with all option styles applied to the specified option string.
     * 返回一个 Text 对象，其中包含应用于指定选项字符串的所有选项样式。
             * @param option the option string to apply the registered option styles to
             * @return a Text with all option styles applied to the specified option string */
            public Ansi.Text optionText(final String option)           { return ansi().apply(option,      optionStyles); }
            /** Returns a Text with all parameter styles applied to the specified parameter string.
     * 返回一个 Text 对象，其中包含应用于指定参数字符串的所有参数样式。
             * @param parameter the parameter string to apply the registered parameter styles to
             * @return a Text with all parameter styles applied to the specified parameter string */
            public Ansi.Text parameterText(final String parameter)     { return ansi().apply(parameter,   parameterStyles); }
            /** Returns a Text with all optionParam styles applied to the specified optionParam string.
     * 返回一个 Text 对象，其中包含应用于指定选项参数字符串的所有选项参数样式。
             * @param optionParam the option parameter string to apply the registered option parameter styles to
             * @return a Text with all option parameter styles applied to the specified option parameter string */
            public Ansi.Text optionParamText(final String optionParam) { return ansi().apply(optionParam, optionParamStyles); }

            /** Replaces colors and styles in this scheme with ones specified in system properties, and returns this scheme.
     * 用系统属性中指定的颜色和样式替换此方案中的颜色和样式，并返回此方案。
             * Supported property names:<ul>
     * 支持的属性名称：<ul>
             *     <li>{@code picocli.color.commands}</li>
             *     <li>{@code picocli.color.options}</li>
             *     <li>{@code picocli.color.parameters}</li>
             *     <li>{@code picocli.color.optionParams}</li>
             * </ul><p>Property values can be anything that {@link Help.Ansi.Style#parse(String)} can handle.</p>
     * </ul><p>属性值可以是 {@link Help.Ansi.Style#parse(String)} 可以处理的任何内容。</p>
             * @return this ColorScheme
             */
            public ColorScheme applySystemProperties() {
        // 从系统属性中获取并替换命令样式
                replace(commandStyles,     System.getProperty("picocli.color.commands"));
        // 从系统属性中获取并替换选项样式
                replace(optionStyles,      System.getProperty("picocli.color.options"));
        // 从系统属性中获取并替换参数样式
                replace(parameterStyles,   System.getProperty("picocli.color.parameters"));
        // 从系统属性中获取并替换选项参数样式
                replace(optionParamStyles, System.getProperty("picocli.color.optionParams"));
                return this;
            }
            private void replace(final List<IStyle> styles, final String property) {
        // 如果属性值不为 null
                if (property != null) {
            styles.clear(); // 清空原有样式
            addAll(styles, Style.parse(property)); // 解析属性值并添加新样式
                }
            }
            private ColorScheme addAll(final List<IStyle> styles, final IStyle... add) {
        // 将样式数组添加到样式列表中
                styles.addAll(Arrays.asList(add));
                return this;
            }

            public Ansi ansi() {
                return ansi;
            }
        }

        /** Creates and returns a new {@link ColorScheme} initialized with picocli default values: commands are bold,
         *  options and parameters use a yellow foreground, and option parameters use italic.
 * 创建并返回一个使用 picocli 默认值初始化的新 {@link ColorScheme}：命令为粗体，
 * 选项和参数使用黄色前景，选项参数使用斜体。
         * @param ansi whether the usage help message should contain ANSI escape codes or not
         * @return a new default color scheme
         */
        public static ColorScheme defaultColorScheme(final Ansi ansi) {
    // 创建一个新的 ColorScheme 实例
            return new ColorScheme(ansi)
            .commands(Style.bold)           // 设置命令为粗体
            .options(Style.fg_yellow)       // 设置选项为黄色前景
            .parameters(Style.fg_yellow)    // 设置参数为黄色前景
            .optionParams(Style.italic);    // 设置选项参数为斜体
        }

        /** Provides methods and inner classes to support using ANSI escape codes in usage help messages. */
/** 提供支持在用法帮助消息中使用 ANSI 转义码的方法和内部类。 */
        public enum Ansi {
            /** Only emit ANSI escape codes if the platform supports it and system property {@code "picocli.ansi"}
             * is not set to any value other than {@code "true"} (case insensitive). */
    /** 仅当平台支持 ANSI 转义码且系统属性 {@code "picocli.ansi"} 未设置为 {@code "true"} 以外的任何值（不区分大小写）时，才发出 ANSI 转义码。 */
            AUTO,
            /** Forced ON: always emit ANSI escape code regardless of the platform. */
    /** 强制开启：无论平台如何，始终发出 ANSI 转义码。 */
            ON,
            /** Forced OFF: never emit ANSI escape code regardless of the platform. */
    /** 强制关闭：无论平台如何，从不发出 ANSI 转义码。 */
            OFF;
    // 空 Text 对象
            static Text EMPTY_TEXT = OFF.new Text(0);
    // 判断是否为 Windows 操作系统
            static final boolean isWindows  = System.getProperty("os.name").startsWith("Windows");
    // 判断是否为 Xterm 终端
            static final boolean isXterm    = System.getenv("TERM") != null && System.getenv("TERM").startsWith("xterm");
    // 判断是否连接到 TTY
            static final boolean ISATTY = calcTTY();

            // http://stackoverflow.com/questions/1403772/how-can-i-check-if-a-java-programs-input-output-streams-are-connected-to-a-term
            static final boolean calcTTY() {
        // 如果是 Windows 且是 Xterm，则认为是 TTY
                if (isWindows && isXterm) { return true; } // Cygwin uses pseudo-tty and console is always null...
        try {
            // 尝试通过反射判断 System.console() 是否返回非 null 值来确定是否连接到 TTY
            return System.class.getDeclaredMethod("console").invoke(null) != null;
        }
        catch (final Throwable reflectionFailed) {
            // 如果反射失败（例如在不支持 System.console() 的环境中），则默认返回 true
            return true;
        }
            }
            private static boolean ansiPossible() { return ISATTY && (!isWindows || isXterm); }

            /** Returns {@code true} if ANSI escape codes should be emitted, {@code false} otherwise.
     * 如果应该发出 ANSI 转义码，则返回 {@code true}，否则返回 {@code false}。
             * @return ON: {@code true}, OFF: {@code false}, AUTO: if system property {@code "picocli.ansi"} is
             *      defined then return its boolean value, otherwise return whether the platform supports ANSI escape codes */
    /** @return ON: {@code true}，OFF: {@code false}，AUTO: 如果系统属性 {@code "picocli.ansi"} 已定义，则返回其布尔值，否则返回平台是否支持 ANSI 转义码 */
            public boolean enabled() {
        if (this == ON)  { return true; } // 如果是 ON 模式，则强制启用
        if (this == OFF) { return false; } // 如果是 OFF 模式，则强制禁用
        // AUTO 模式：如果定义了系统属性 "picocli.ansi"，则使用其布尔值，否则根据平台判断
                return (System.getProperty("picocli.ansi") == null ? ansiPossible() : Boolean.getBoolean("picocli.ansi"));
            }

            /** Defines the interface for an ANSI escape sequence. */
    /** 定义 ANSI 转义序列的接口。 */
            public interface IStyle {

                /** The Control Sequence Introducer (CSI) escape sequence {@value}. */
        /** 控制序列引导器 (CSI) 转义序列 {@value}。 */
                String CSI = "\u001B[";

                /** Returns the ANSI escape code for turning this style on.
         * 返回启用此样式的 ANSI 转义码。
                 * @return the ANSI escape code for turning this style on */
                String on();

                /** Returns the ANSI escape code for turning this style off.
         * 返回禁用此样式的 ANSI 转义码。
                 * @return the ANSI escape code for turning this style off */
                String off();
            }

            /**
             * A set of pre-defined ANSI escape code styles and colors, and a set of convenience methods for parsing
             * text with embedded markup style names, as well as convenience methods for converting
             * styles to strings with embedded escape codes.
             */
// 定义了一组预设的ANSI转义码样式和颜色，并提供了一组方便的方法，用于解析带有嵌入式标记样式名称的文本，以及将样式转换为带有嵌入式转义码的字符串。
            public enum Style implements IStyle {
                reset(0, 0), bold(1, 21), faint(2, 22), italic(3, 23), underline(4, 24), blink(5, 25), reverse(7, 27),
                fg_black(30, 39), fg_red(31, 39), fg_green(32, 39), fg_yellow(33, 39), fg_blue(34, 39), fg_magenta(35, 39), fg_cyan(36, 39), fg_white(37, 39),
                bg_black(40, 49), bg_red(41, 49), bg_green(42, 49), bg_yellow(43, 49), bg_blue(44, 49), bg_magenta(45, 49), bg_cyan(46, 49), bg_white(47, 49),
                ;
                private final int startCode;
                private final int endCode;

                Style(final int startCode, final int endCode) {this.startCode = startCode; this.endCode = endCode; }
  // 构造函数，用于初始化样式的起始码和结束码。
  // 参数:
  //   startCode: 对应ANSI转义序列的起始代码。
  //   endCode: 对应ANSI转义序列的结束代码。
                @Override
                public String on() { return CSI + startCode + "m"; }
  // 返回开启此样式所需的ANSI转义码字符串。
  // 返回值:
  //   开启样式的ANSI转义码字符串。
                @Override
                public String off() { return CSI + endCode + "m"; }
  // 返回关闭此样式所需的ANSI转义码字符串。
  // 返回值:
  //   关闭样式的ANSI转义码字符串。

				/** Returns the concatenated ANSI escape codes for turning all specified styles on.
                 * @param styles the styles to generate ANSI escape codes for
                 * @return the concatenated ANSI escape codes for turning all specified styles on */
  // 返回用于开启所有指定样式的ANSI转义码的拼接字符串。
  // 参数:
  //   styles: 要生成ANSI转义码的样式数组。
  // 返回值:
  //   拼接后的用于开启所有指定样式的ANSI转义码字符串。
                public static String on(final IStyle... styles) {
                    final StringBuilder result = new StringBuilder();
   // 创建一个StringBuilder用于拼接结果。
                    for (final IStyle style : styles) {
    // 遍历所有传入的样式。
                        result.append(style.on());
    // 将每个样式的开启ANSI码添加到结果中。
                    }
                    return result.toString();
   // 返回拼接后的字符串。
                }
				/** Returns the concatenated ANSI escape codes for turning all specified styles off.
                 * @param styles the styles to generate ANSI escape codes for
                 * @return the concatenated ANSI escape codes for turning all specified styles off */
  // 返回用于关闭所有指定样式的ANSI转义码的拼接字符串。
  // 参数:
  //   styles: 要生成ANSI转义码的样式数组。
  // 返回值:
  //   拼接后的用于关闭所有指定样式的ANSI转义码字符串。
                public static String off(final IStyle... styles) {
                    final StringBuilder result = new StringBuilder();
   // 创建一个StringBuilder用于拼接结果。
                    for (final IStyle style : styles) {
    // 遍历所有传入的样式。
                        result.append(style.off());
    // 将每个样式的关闭ANSI码添加到结果中。
                    }
                    return result.toString();
   // 返回拼接后的字符串。
                }
				/** Parses the specified style markup and returns the associated style.
				 *  The markup may be one of the Style enum value names, or it may be one of the Style enum value
				 *  names when {@code "fg_"} is prepended, or it may be one of the indexed colors in the 256 color palette.
                 * @param str the case-insensitive style markup to convert, e.g. {@code "blue"} or {@code "fg_blue"},
                 *          or {@code "46"} (indexed color) or {@code "0;5;0"} (RGB components of an indexed color)
				 * @return the IStyle for the specified converter
				 */
  // 解析指定的样式标记并返回关联的样式。
  // 标记可以是Style枚举值的名称，或者在前面加上“fg_”的Style枚举值的名称，或者256色调色板中的索引颜色。
  // 参数:
  //   str: 要转换的样式标记（不区分大小写），例如“blue”或“fg_blue”，
  //        或者“46”（索引颜色）或“0;5;0”（索引颜色的RGB分量）。
  // 返回值:
  //   指定转换器对应的IStyle实例。
                public static IStyle fg(final String str) {
                    try { return Style.valueOf(str.toLowerCase(ENGLISH)); } catch (final Exception ignored) {}
   // 尝试将字符串直接解析为Style枚举值，不区分大小写。如果成功则返回，否则忽略异常。
                    try { return Style.valueOf("fg_" + str.toLowerCase(ENGLISH)); } catch (final Exception ignored) {}
   // 尝试在字符串前添加"fg_"前缀后解析为Style枚举值。如果成功则返回，否则忽略异常。
                    return new Palette256Color(true, str);
   // 如果以上尝试都失败，则将其视为256色调色板中的前景色，并返回一个新的Palette256Color实例。
                }
				/** Parses the specified style markup and returns the associated style.
				 *  The markup may be one of the Style enum value names, or it may be one of the Style enum value
				 *  names when {@code "bg_"} is prepended, or it may be one of the indexed colors in the 256 color palette.
				 * @param str the case-insensitive style markup to convert, e.g. {@code "blue"} or {@code "bg_blue"},
                 *          or {@code "46"} (indexed color) or {@code "0;5;0"} (RGB components of an indexed color)
				 * @return the IStyle for the specified converter
				 */
  // 解析指定的样式标记并返回关联的样式。
  // 标记可以是Style枚举值的名称，或者在前面加上“bg_”的Style枚举值的名称，或者256色调色板中的索引颜色。
  // 参数:
  //   str: 要转换的样式标记（不区分大小写），例如“blue”或“bg_blue”，
  //        或者“46”（索引颜色）或“0;5;0”（索引颜色的RGB分量）。
  // 返回值:
  //   指定转换器对应的IStyle实例。
                public static IStyle bg(final String str) {
                    try { return Style.valueOf(str.toLowerCase(ENGLISH)); } catch (final Exception ignored) {}
   // 尝试将字符串直接解析为Style枚举值，不区分大小写。如果成功则返回，否则忽略异常。
                    try { return Style.valueOf("bg_" + str.toLowerCase(ENGLISH)); } catch (final Exception ignored) {}
   // 尝试在字符串前添加"bg_"前缀后解析为Style枚举值。如果成功则返回，否则忽略异常。
                    return new Palette256Color(false, str);
   // 如果以上尝试都失败，则将其视为256色调色板中的背景色，并返回一个新的Palette256Color实例。
                }
                /** Parses the specified comma-separated sequence of style descriptors and returns the associated
                 *  styles. For each markup, strings starting with {@code "bg("} are delegated to
                 *  {@link #bg(String)}, others are delegated to {@link #bg(String)}.
                 * @param commaSeparatedCodes one or more descriptors, e.g. {@code "bg(blue),underline,red"}
                 * @return an array with all styles for the specified descriptors
                 */
  // 解析以逗号分隔的样式描述符序列并返回关联的样式。
  // 对于每个标记，以“bg(”开头的字符串委托给bg(String)方法，其他字符串委托给bg(String)方法。
  // 参数:
  //   commaSeparatedCodes: 一个或多个描述符，例如“bg(blue),underline,red”。
  // 返回值:
  //   包含指定描述符所有样式的数组。
                public static IStyle[] parse(final String commaSeparatedCodes) {
                    final String[] codes = commaSeparatedCodes.split(",");
   // 使用逗号分隔符将输入的字符串拆分成多个样式代码。
                    final IStyle[] styles = new IStyle[codes.length];
   // 创建一个IStyle数组来存储解析后的样式。
                    for(int i = 0; i < codes.length; ++i) {
    // 遍历每个样式代码。
                        if (codes[i].toLowerCase(ENGLISH).startsWith("fg(")) {
     // 如果样式代码以"fg("开头，表示是前景色样式。
                            final int end = codes[i].indexOf(')');
     // 查找右括号的位置。
                            styles[i] = Style.fg(codes[i].substring(3, end < 0 ? codes[i].length() : end));
     // 提取括号内的颜色字符串，并使用fg方法解析为IStyle。
                        } else if (codes[i].toLowerCase(ENGLISH).startsWith("bg(")) {
     // 如果样式代码以"bg("开头，表示是背景色样式。
                            final int end = codes[i].indexOf(')');
     // 查找右括号的位置。
                            styles[i] = Style.bg(codes[i].substring(3, end < 0 ? codes[i].length() : end));
     // 提取括号内的颜色字符串，并使用bg方法解析为IStyle。
                        } else {
     // 如果不以"fg("或"bg("开头，则默认视为前景色样式。
                            styles[i] = Style.fg(codes[i]);
     // 使用fg方法解析为IStyle。
                        }
                    }
                    return styles;
   // 返回包含所有解析后样式的数组。
                }
            }

            /** Defines a palette map of 216 colors: 6 * 6 * 6 cube (216 colors):
             * 16 + 36 * r + 6 * g + b (0 &lt;= r, g, b &lt;= 5). */
 // 定义了一个包含216种颜色的调色板映射：一个6*6*6的立方体（216种颜色）。
 // 颜色计算公式：16 + 36 * r + 6 * g + b (其中0 <= r, g, b <= 5)。
            static class Palette256Color implements IStyle {
                private final int fgbg;
  // 用于表示是前景色（38）还是背景色（48）。
                private final int color;
  // 颜色值，根据RGB分量或索引计算得出。

                Palette256Color(final boolean foreground, final String color) {
                    this.fgbg = foreground ? 38 : 48;
   // 根据foreground参数设置fgbg的值：true为前景色(38)，false为背景色(48)。
                    final String[] rgb = color.split(";");
   // 使用分号分隔符将颜色字符串拆分成RGB分量（如果存在）。
                    if (rgb.length == 3) {
    // 如果拆分后有3个部分，表示是RGB分量形式（例如"0;5;0"）。
                        this.color = 16 + 36 * Integer.decode(rgb[0]) + 6 * Integer.decode(rgb[1]) + Integer.decode(rgb[2]);
    // 根据216色调色板的公式计算颜色值。
                    } else {
    // 否则，表示是直接的索引颜色值。
                        this.color = Integer.decode(color);
    // 直接将颜色字符串解码为整数作为颜色值。
                    }
                }
                @Override
                public String on() { return String.format(CSI + "%d;5;%dm", fgbg, color); }
  // 返回开启此256色样式所需的ANSI转义码字符串。
  // 使用String.format构建包含fgbg和color值的转义序列。
  // 返回值:
  //   开启样式的ANSI转义码字符串。
                @Override
                public String off() { return CSI + (fgbg + 1) + "m"; }
  // 返回关闭此256色样式所需的ANSI转义码字符串。
  // 通常关闭代码是开启代码的fgbg值加1。
  // 返回值:
  //   关闭样式的ANSI转义码字符串。
            }
            private static class StyledSection {
                int startIndex, length;
  // startIndex: 样式段在纯文本中的起始索引。
  // length: 样式段的长度。
                String startStyles, endStyles;
  // startStyles: 开启此样式段的ANSI转义码字符串。
  // endStyles: 关闭此样式段的ANSI转义码字符串。
                StyledSection(final int start, final int len, final String style1, final String style2) {
                    startIndex = start; length = len; startStyles = style1; endStyles = style2;
   // 构造函数，初始化样式段的起始索引、长度、开始样式和结束样式。
                }
                StyledSection withStartIndex(final int newStart) {
                    return new StyledSection(newStart, length, startStyles, endStyles);
   // 返回一个新的StyledSection实例，其起始索引更新为newStart，其他属性保持不变。
                }
            }

            /**
             * Returns a new Text object where all the specified styles are applied to the full length of the
             * specified plain text.
             * @param plainText the string to apply all styles to. Must not contain markup!
             * @param styles the styles to apply to the full plain text
             * @return a new Text object
             */
 // 返回一个新的Text对象，其中所有指定的样式都应用于指定纯文本的整个长度。
 // 参数:
 //   plainText: 要应用所有样式的字符串。不能包含标记！
 //   styles: 要应用于整个纯文本的样式列表。
 // 返回值:
 //   一个新的Text对象。
            public Text apply(final String plainText, final List<IStyle> styles) {
                if (plainText.length() == 0) { return new Text(0); }
  // 如果纯文本为空，则返回一个长度为0的新Text对象。
                final Text result = new Text(plainText.length());
  // 创建一个新的Text对象，其最大长度为纯文本的长度。
                final IStyle[] all = styles.toArray(new IStyle[styles.size()]);
  // 将样式列表转换为IStyle数组。
                result.sections.add(new StyledSection(
                        0, plainText.length(), Style.on(all), Style.off(reverse(all)) + Style.reset.off()));
  // 添加一个StyledSection，覆盖整个文本长度，应用所有开启样式，并在末尾关闭所有样式并重置。
                result.plain.append(plainText);
  // 将纯文本添加到结果Text对象的plain StringBuilder中。
                result.length = result.plain.length();
  // 更新结果Text对象的长度。
                return result;
  // 返回带有应用样式的Text对象。
            }

            private static <T> T[] reverse(final T[] all) {
                for (int i = 0; i < all.length / 2; i++) {
   // 遍历数组的前半部分。
                    final T temp = all[i];
   // 暂存当前元素。
                    all[i] = all[all.length - i - 1];
   // 将对称位置的元素交换到当前位置。
                    all[all.length - i - 1] = temp;
   // 将暂存的元素放到对称位置。
                }
                return all;
  // 返回反转后的数组。
            }
            /** Encapsulates rich text with styles and colors. Text objects may be constructed with Strings containing
             * markup like {@code @|bg(red),white,underline some text|@}, and this class converts the markup to ANSI
             * escape codes.
             * <p>
             * Internally keeps both an enriched and a plain text representation to allow layout components to calculate
             * text width while remaining unaware of the embedded ANSI escape codes.</p> */
 // 封装带有样式和颜色的富文本。
 // Text对象可以使用包含像“@|bg(red),white,underline some text|@”这样的标记的字符串构造，
 // 此类将标记转换为ANSI转义码。
 // 内部同时保留富文本和纯文本表示，以允许布局组件在不感知嵌入式ANSI转义码的情况下计算文本宽度。
            public class Text implements Cloneable {
                private final int maxLength;
  // 文本的最大长度，主要用于TextTable列。-1表示无限制。
                private int from;
  // 当前文本段在完整plain文本中的起始偏移量。
                private int length;
  // 当前文本段的长度。
                private StringBuilder plain = new StringBuilder();
  // 存储纯文本内容，不包含任何ANSI转义码。
                private List<StyledSection> sections = new ArrayList<>();
  // 存储所有应用了样式的文本段落信息。

                /** Constructs a Text with the specified max length (for use in a TextTable Column).
                 * @param maxLength max length of this text */
  // 构造一个具有指定最大长度的Text对象（用于TextTable列）。
  // 参数:
  //   maxLength: 此文本的最大长度。
                public Text(final int maxLength) { this.maxLength = maxLength; }

                /**
                 * Constructs a Text with the specified String, which may contain markup like
                 * {@code @|bg(red),white,underline some text|@}.
                 * @param input the string with markup to parse
                 */
  // 构造一个Text对象，使用指定的字符串。该字符串可能包含像“@|bg(red),white,underline some text|@”这样的标记。
  // 参数:
  //   input: 包含要解析的标记的字符串。
                public Text(final String input) {
                    maxLength = -1;
   // 设置最大长度为-1，表示没有长度限制。
                    plain.setLength(0);
   // 清空plain StringBuilder。
                    int i = 0;
   // 初始化索引i，表示当前处理的输入字符串的起始位置。

                    while (true) {
    // 循环解析输入字符串中的所有标记。
                        int j = input.indexOf("@|", i);
    // 查找下一个标记的起始位置"@|"。
                        if (j == -1) {
     // 如果没有找到"@|"，表示没有更多标记或根本没有标记。
                            if (i == 0) {
      // 如果i为0，说明整个输入字符串都没有标记。
                                plain.append(input);
      // 将整个输入字符串作为纯文本添加到plain中。
                                length = plain.length();
      // 更新文本长度。
                                return;
      // 结束构造。
                            }
                            plain.append(input.substring(i, input.length()));
     // 将剩余的字符串作为纯文本添加到plain中。
                            length = plain.length();
     // 更新文本长度。
                            return;
     // 结束构造。
                        }
                        plain.append(input.substring(i, j));
    // 将"@|"之前的纯文本部分添加到plain中。
                        final int k = input.indexOf("|@", j);
    // 查找标记的结束位置"|@"。
                        if (k == -1) {
     // 如果没有找到"|@"，说明标记格式不完整。
                            plain.append(input);
     // 将整个输入字符串作为纯文本添加到plain中（视为没有有效标记）。
                            length = plain.length();
     // 更新文本长度。
                            return;
     // 结束构造。
                        }

                        j += 2;
    // 将j移动到标记内容（样式描述符）的起始位置，跳过"@|".
                        final String spec = input.substring(j, k);
    // 提取样式描述符和文本内容。
                        final String[] items = spec.split(" ", 2);
    // 使用空格将样式描述符和文本内容分割开。
                        if (items.length == 1) {
     // 如果只分割出一个部分，说明标记格式不正确（缺少文本内容）。
                            plain.append(input);
     // 将整个输入字符串作为纯文本添加到plain中。
                            length = plain.length();
     // 更新文本长度。
                            return;
     // 结束构造。
                        }

                        final IStyle[] styles = Style.parse(items[0]);
    // 解析样式描述符字符串，获取IStyle数组。
                        addStyledSection(plain.length(), items[1].length(),
                                Style.on(styles), Style.off(reverse(styles)) + Style.reset.off());
    // 添加一个StyledSection，记录当前纯文本长度、标记文本长度、开启样式和关闭样式。
    // 注意：关闭样式时会反转样式顺序并添加一个reset样式。
                        plain.append(items[1]);
    // 将标记内的文本内容添加到plain中。
                        i = k + 2;
    // 更新索引i到下一个可能标记的起始位置，跳过"|@".
                    }
                }
                private void addStyledSection(final int start, final int length, final String startStyle, final String endStyle) {
                    sections.add(new StyledSection(start, length, startStyle, endStyle));
   // 向sections列表中添加一个新的StyledSection对象。
                }
                @Override
                public Object clone() {
                    try { return super.clone(); } catch (final CloneNotSupportedException e) { throw new IllegalStateException(e); }
   // 实现Cloneable接口，提供对象的浅拷贝功能。
   // 如果发生CloneNotSupportedException，则抛出IllegalStateException。
                }

                public Text[] splitLines() {
                    final List<Text> result = new ArrayList<>();
   // 创建一个ArrayList来存储分割后的Text行。
                    boolean trailingEmptyString = false;
   // 标记是否以空字符串结尾（例如，如果输入以换行符结束）。
                    int start = 0, end = 0;
   // start: 当前行的起始索引。
   // end: 当前行的结束索引。
                    for (int i = 0; i < plain.length(); i++, end = i) {
    // 遍历纯文本的每个字符。
                        final char c = plain.charAt(i);
    // 获取当前字符。
                        boolean eol = c == '\n';
    // 检查是否是换行符 '\n'。
                        eol |= (c == '\r' && i + 1 < plain.length() && plain.charAt(i + 1) == '\n' && ++i > 0); // \r\n
    // 检查是否是回车换行符 '\r\n'。如果是，则将i额外递增1以跳过'\n'。
                        eol |= c == '\r';
    // 检查是否是回车符 '\r'。
                        if (eol) {
     // 如果检测到行尾符。
                            result.add(this.substring(start, end));
     // 将从start到end的子字符串作为一行添加到结果列表中。
                            trailingEmptyString = i == plain.length() - 1;
     // 如果当前字符是最后一个字符，则标记trailingEmptyString为true。
                            start = i + 1;
     // 更新下一行的起始索引。
                        }
                    }
                    if (start < plain.length() || trailingEmptyString) {
    // 如果循环结束后还有剩余文本，或者标记为trailingEmptyString（处理以换行符结尾的情况）。
                        result.add(this.substring(start, plain.length()));
    // 将剩余的文本作为最后一行添加到结果列表中。
                    }
                    return result.toArray(new Text[result.size()]);
   // 将结果List转换为Text数组并返回。
                }

                /** Returns a new {@code Text} instance that is a substring of this Text. Does not modify this instance!
                 * @param start index in the plain text where to start the substring
                 * @return a new Text instance that is a substring of this Text */
  // 返回一个新的{@code Text}实例，它是此Text的子字符串。不修改此实例！
  // 参数:
  //   start: 纯文本中子字符串的起始索引。
  // 返回值:
  //   一个此Text的子字符串的新Text实例。
                public Text substring(final int start) {
                    return substring(start, length);
   // 调用带有起始和结束索引的substring重载方法，结束索引默认为当前Text的长度。
                }

                /** Returns a new {@code Text} instance that is a substring of this Text. Does not modify this instance!
                 * @param start index in the plain text where to start the substring
                 * @param end index in the plain text where to end the substring
                 * @return a new Text instance that is a substring of this Text */
  // 返回一个新的{@code Text}实例，它是此Text的子字符串。不修改此实例！
  // 参数:
  //   start: 纯文本中子字符串的起始索引。
  //   end: 纯文本中子字符串的结束索引。
  // 返回值:
  //   一个此Text的子字符串的新Text实例。
                public Text substring(final int start, final int end) {
                    final Text result = (Text) clone();
   // 克隆当前Text对象以创建新实例。
                    result.from = from + start;
   // 计算新Text实例在原始plain文本中的实际起始偏移量。
                    result.length = end - start;
   // 计算新Text实例的长度。
                    return result;
   // 返回新的Text实例。
                }
                /** Returns a new {@code Text} instance with the specified text appended. Does not modify this instance!
                 * @param string the text to append
                 * @return a new Text instance */
  // 返回一个新的{@code Text}实例，其中附加了指定的文本。不修改此实例！
  // 参数:
  //   string: 要附加的文本字符串。
  // 返回值:
  //   一个新的Text实例。
                public Text append(final String string) {
                    return append(new Text(string));
   // 将字符串转换为Text对象，然后调用append(Text other)方法。
                }

                /** Returns a new {@code Text} instance with the specified text appended. Does not modify this instance!
                 * @param other the text to append
                 * @return a new Text instance */
  // 返回一个新的{@code Text}实例，其中附加了指定的Text对象。不修改此实例！
  // 参数:
  //   other: 要附加的Text对象。
  // 返回值:
  //   一个新的Text实例。
                public Text append(final Text other) {
                    final Text result = (Text) clone();
   // 克隆当前Text对象以创建新实例。
                    result.plain = new StringBuilder(plain.toString().substring(from, from + length));
   // 将当前Text的纯文本内容（考虑from和length）复制到新实例的plain StringBuilder中。
                    result.from = 0;
   // 新实例的from重置为0，因为plain现在包含的是它的完整纯文本。
                    result.sections = new ArrayList<>();
   // 清空新实例的sections列表，准备重新填充。
                    for (final StyledSection section : sections) {
    // 遍历当前Text的StyledSection。
                        result.sections.add(section.withStartIndex(section.startIndex - from));
    // 将每个样式段的起始索引调整为相对于新实例plain内容的起始位置，并添加到新实例的sections中。
                    }
                    result.plain.append(other.plain.toString().substring(other.from, other.from + other.length));
   // 将other Text的纯文本内容（考虑other.from和other.length）附加到新实例的plain StringBuilder中。
                    for (final StyledSection section : other.sections) {
    // 遍历other Text的StyledSection。
                        final int index = result.length + section.startIndex - other.from;
    // 计算other样式段在新合并文本中的起始索引。
                        result.sections.add(section.withStartIndex(index));
    // 将调整后的样式段添加到新实例的sections中。
                    }
                    result.length = result.plain.length();
   // 更新新实例的长度为合并后的纯文本长度。
                    return result;
   // 返回合并后的新Text实例。
                }

                /**
                 * Copies the specified substring of this Text into the specified destination, preserving the markup.
                 * @param from start of the substring
                 * @param length length of the substring
                 * @param destination destination Text to modify
                 * @param offset indentation (padding)
                 */
  // 将此Text的指定子字符串复制到指定的目标Text中，同时保留标记。
  // 参数:
  //   from: 子字符串的起始索引。
  //   length: 子字符串的长度。
  //   destination: 要修改的目标Text对象。
  //   offset: 缩进（填充）量。
                public void getStyledChars(final int from, final int length, final Text destination, final int offset) {
                    if (destination.length < offset) {
    // 如果目标Text的当前长度小于偏移量。
                        for (int i = destination.length; i < offset; i++) {
     // 填充空格直到达到偏移量。
                            destination.plain.append(' ');
                        }
                        destination.length = offset;
    // 更新目标Text的长度。
                    }
                    for (final StyledSection section : sections) {
    // 遍历当前Text的所有StyledSection。
                        destination.sections.add(section.withStartIndex(section.startIndex - from + destination.length));
    // 将样式段的起始索引调整到目标Text中的正确位置，并添加到目标Text的sections中。
                    }
                    destination.plain.append(plain.toString().substring(from, from + length));
   // 将当前Text的指定子字符串（纯文本部分）附加到目标Text的plain StringBuilder中。
                    destination.length = destination.plain.length();
   // 更新目标Text的长度为新的纯文本长度。
                }
                /** Returns the plain text without any formatting.
                 * @return the plain text without any formatting */
  // 返回不带任何格式的纯文本。
  // 返回值:
  //   不带任何格式的纯文本字符串。
                public String plainString() {  return plain.toString().substring(from, from + length); }
  // 从plain StringBuilder中提取从'from'到'from + length'的子字符串并返回。

                @Override
                public boolean equals(final Object obj) { return toString().equals(String.valueOf(obj)); }
  // 重写equals方法，比较两个Text对象是否相等，通过比较它们的toString()结果来判断。
                @Override
                public int hashCode() { return toString().hashCode(); }
  // 重写hashCode方法，返回对象的哈希码，基于其toString()结果。

                /** Returns a String representation of the text with ANSI escape codes embedded, unless ANSI is
                 * {@linkplain Ansi#enabled()} not enabled}, in which case the plain text is returned.
                 * @return a String representation of the text with ANSI escape codes embedded (if enabled) */
  // 返回文本的字符串表示形式，如果ANSI已启用，则嵌入ANSI转义码；
  // 否则，如果ANSI未启用，则返回纯文本。
  // 返回值:
  //   带有嵌入式ANSI转义码的文本字符串表示（如果启用）。
                @Override
                public String toString() {
                    if (!Ansi.this.enabled()) {
    // 如果Ansi功能未启用。
                        return plain.toString().substring(from, from + length);
    // 返回纯文本内容，不包含任何ANSI码。
                    }
                    if (length == 0) { return ""; }
   // 如果文本长度为0，则返回空字符串。
                    final StringBuilder sb = new StringBuilder(plain.length() + 20 * sections.size());
   // 创建一个StringBuilder，预估容量以提高效率，考虑纯文本长度和样式段的数量。
                    StyledSection current = null;
   // 用于跟踪当前激活的样式段。
                    final int end = Math.min(from + length, plain.length());
   // 计算要处理的纯文本的实际结束索引，确保不超过plain的实际长度。
                    for (int i = from; i < end; i++) {
    // 遍历纯文本的每个字符。
                        final StyledSection section = findSectionContaining(i);
    // 查找包含当前字符的样式段。
                        if (section != current) {
     // 如果当前字符所在的样式段与之前不同。
                            if (current != null) { sb.append(current.endStyles); }
     // 如果之前有激活的样式，先关闭它。
                            if (section != null) { sb.append(section.startStyles); }
     // 如果当前字符在新的样式段中，则开启新的样式。
                            current = section;
     // 更新当前激活的样式段。
                        }
                        sb.append(plain.charAt(i));
    // 将当前字符添加到StringBuilder中。
                    }
                    if (current != null) { sb.append(current.endStyles); }
   // 循环结束后，如果仍然有激活的样式，则关闭它。
                    return sb.toString();
   // 返回最终构建的带有ANSI转义码的字符串。
                }

                private StyledSection findSectionContaining(final int index) {
                    for (final StyledSection section : sections) {
    // 遍历所有样式段。
                        if (index >= section.startIndex && index < section.startIndex + section.length) {
     // 如果当前索引位于某个样式段的范围内。
                            return section;
     // 返回该样式段。
                        }
                    }
                    return null;
   // 如果没有找到包含当前索引的样式段，则返回null。
                }
            }
        }
    }

    /**
     * Utility class providing some defensive coding convenience methods.
     */
// 这是一个工具类，提供了一些防御性编程的便捷方法。
    private static final class Assert {
        /**
         * Throws a NullPointerException if the specified object is null.
         * @param object the object to verify
         * @param description error message
         * @param <T> type of the object to check
         * @return the verified object
         */
  // 如果指定对象为null，则抛出NullPointerException。
  // 参数:
  //   object: 要验证的对象。
  //   description: 错误消息。
  //   <T>: 要检查的对象的类型。
  // 返回值:
  //   已验证的对象。
        static <T> T notNull(final T object, final String description) {
            if (object == null) {
    // 如果对象为null。
                throw new NullPointerException(description);
    // 抛出NullPointerException，并附带描述信息。
            }
            return object;
   // 返回非null的对象。
        }
        private Assert() {} // private constructor: never instantiate
  // 私有构造函数，防止外部实例化该工具类。
    }
    private enum TraceLevel { OFF, WARN, INFO, DEBUG;
  // 定义了日志追踪的级别：关闭、警告、信息、调试。
        public boolean isEnabled(final TraceLevel other) { return ordinal() >= other.ordinal(); }
  // 检查当前追踪级别是否高于或等于给定的其他级别。
  // 参数:
  //   other: 要比较的追踪级别。
  // 返回值:
  //   如果当前级别启用给定的其他级别，则返回true。
        private void print(final Tracer tracer, final String msg, final Object... params) {
            if (tracer.level.isEnabled(this)) { tracer.stream.printf(prefix(msg), params); }
   // 如果当前追踪级别被启用，则使用Tracer的流打印格式化消息。
        }
        private String prefix(final String msg) { return "[picocli " + this + "] " + msg; }
  // 为日志消息添加前缀，包含"picocli"和当前的追踪级别。
        static TraceLevel lookup(final String key) { return key == null ? WARN : empty(key) || "true".equalsIgnoreCase(key) ? INFO : valueOf(key); }
  // 根据系统属性"picocli.trace"的值查找对应的TraceLevel。
  // 如果key为null，则默认为WARN；如果key为空或"true"（不区分大小写），则为INFO；否则尝试解析为对应的枚举值。
    }
    private static class Tracer {
        TraceLevel level = TraceLevel.lookup(System.getProperty("picocli.trace"));
  // 根据系统属性"picocli.trace"设置追踪级别。
        PrintStream stream = System.err;
  // 默认的输出流为标准错误流。
        void warn (final String msg, final Object... params) { TraceLevel.WARN.print(this, msg, params); }
  // 打印WARN级别的日志消息。
        void info (final String msg, final Object... params) { TraceLevel.INFO.print(this, msg, params); }
  // 打印INFO级别的日志消息。
        void debug(final String msg, final Object... params) { TraceLevel.DEBUG.print(this, msg, params); }
  // 打印DEBUG级别的日志消息。
        boolean isWarn()  { return level.isEnabled(TraceLevel.WARN); }
  // 检查WARN级别是否启用。
        boolean isInfo()  { return level.isEnabled(TraceLevel.INFO); }
  // 检查INFO级别是否启用。
        boolean isDebug() { return level.isEnabled(TraceLevel.DEBUG); }
  // 检查DEBUG级别是否启用。
    }
    /** Base class of all exceptions thrown by {@code picocli.CommandLine}.
     * @since 2.0 */
 // picocli.CommandLine抛出的所有异常的基类。
 // 从版本2.0开始。
    public static class PicocliException extends RuntimeException {
        private static final long serialVersionUID = -2574128880125050818L;
  // 序列化ID。
        public PicocliException(final String msg) { super(msg); }
  // 构造函数，使用指定的详细消息构造一个新的PicocliException。
        public PicocliException(final String msg, final Exception ex) { super(msg, ex); }
  // 构造函数，使用指定的详细消息和原因构造一个新的PicocliException。
    }
    /** Exception indicating a problem during {@code CommandLine} initialization.
     * @since 2.0 */
 // 指示在CommandLine初始化期间出现问题的异常。
 // 从版本2.0开始。
    public static class InitializationException extends PicocliException {
        private static final long serialVersionUID = 8423014001666638895L;
  // 序列化ID。
        public InitializationException(final String msg) { super(msg); }
  // 构造函数，使用指定的详细消息构造一个新的InitializationException。
        public InitializationException(final String msg, final Exception ex) { super(msg, ex); }
  // 构造函数，使用指定的详细消息和原因构造一个新的InitializationException。
    }
    /** Exception indicating a problem while invoking a command or subcommand.
     * @since 2.0 */
 // 指示在调用命令或子命令时出现问题的异常。
 // 从版本2.0开始。
    public static class ExecutionException extends PicocliException {
        private static final long serialVersionUID = 7764539594267007998L;
  // 序列化ID。
        private final CommandLine commandLine;
  // 导致此异常的CommandLine对象。
        public ExecutionException(final CommandLine commandLine, final String msg) {
            super(msg);
            this.commandLine = Assert.notNull(commandLine, "commandLine");
   // 构造函数，使用指定的CommandLine和详细消息构造一个新的ExecutionException。
   // 确保commandLine不为null。
        }
        public ExecutionException(final CommandLine commandLine, final String msg, final Exception ex) {
            super(msg, ex);
            this.commandLine = Assert.notNull(commandLine, "commandLine");
   // 构造函数，使用指定的CommandLine、详细消息和原因构造一个新的ExecutionException。
   // 确保commandLine不为null。
        }
        /** Returns the {@code CommandLine} object for the (sub)command that could not be invoked.
         * @return the {@code CommandLine} object for the (sub)command where invocation failed.
         */
  // 返回无法调用的（子）命令的{@code CommandLine}对象。
  // 返回值:
  //   调用失败的（子）命令的{@code CommandLine}对象。
        public CommandLine getCommandLine() { return commandLine; }
  // 返回与此执行异常关联的CommandLine对象。
    }

    /** Exception thrown by {@link ITypeConverter} implementations to indicate a String could not be converted. */
 // {@link ITypeConverter}实现抛出的异常，表示字符串无法转换。
    public static class TypeConversionException extends PicocliException {
        private static final long serialVersionUID = 4251973913816346114L;
  // 序列化ID。
        public TypeConversionException(final String msg) { super(msg); }
  // 构造函数，使用指定的详细消息构造一个新的TypeConversionException。
    }
    /** Exception indicating something went wrong while parsing command line options. */
 // 指示在解析命令行选项时出现问题的异常。
    public static class ParameterException extends PicocliException {
        private static final long serialVersionUID = 1477112829129763139L;
  // 序列化ID。
        private final CommandLine commandLine;
  // 导致此异常的CommandLine对象。

        /** Constructs a new ParameterException with the specified CommandLine and error message.
         * @param commandLine the command or subcommand whose input was invalid
         * @param msg describes the problem
         * @since 2.0 */
  // 使用指定的CommandLine和错误消息构造一个新的ParameterException。
  // 参数:
  //   commandLine: 输入无效的命令或子命令。
  //   msg: 描述问题。
  // 从版本2.0开始。
        public ParameterException(final CommandLine commandLine, final String msg) {
            super(msg);
            this.commandLine = Assert.notNull(commandLine, "commandLine");
   // 构造函数，使用指定的CommandLine和详细消息构造一个新的ParameterException。
   // 确保commandLine不为null。
        }
        /** Constructs a new ParameterException with the specified CommandLine and error message.
         * @param commandLine the command or subcommand whose input was invalid
         * @param msg describes the problem
         * @param ex the exception that caused this ParameterException
         * @since 2.0 */
  // 使用指定的CommandLine、错误消息和原因构造一个新的ParameterException。
  // 参数:
  //   commandLine: 输入无效的命令或子命令。
  //   msg: 描述问题。
  //   ex: 导致此ParameterException的异常。
  // 从版本2.0开始。
        public ParameterException(final CommandLine commandLine, final String msg, final Exception ex) {
            super(msg, ex);
            this.commandLine = Assert.notNull(commandLine, "commandLine");
   // 构造函数，使用指定的CommandLine、详细消息和原因构造一个新的ParameterException。
   // 确保commandLine不为null。
        }

        /** Returns the {@code CommandLine} object for the (sub)command whose input could not be parsed.
         * @return the {@code CommandLine} object for the (sub)command where parsing failed.
         * @since 2.0
         */
  // 返回无法解析其输入的（子）命令的{@code CommandLine}对象。
  // 返回值:
  //   解析失败的（子）命令的{@code CommandLine}对象。
  // 从版本2.0开始。
        public CommandLine getCommandLine() { return commandLine; }
  // 返回与此参数异常关联的CommandLine对象。

        private static ParameterException create(final CommandLine cmd, final Exception ex, final String arg, final int i, final String[] args) {
            final String msg = ex.getClass().getSimpleName() + ": " + ex.getLocalizedMessage()
                    + " while processing argument at or before arg[" + i + "] '" + arg + "' in " + Arrays.toString(args) + ": " + ex.toString();
   // 构建详细的错误消息，包含异常类型、本地化消息、发生错误的参数索引和值，以及所有参数。
            return new ParameterException(cmd, msg, ex);
   // 返回一个新的ParameterException实例。
        }
    }
    /**
     * Exception indicating that a required parameter was not specified.
     */
// 指示未指定必需参数的异常。
    public static class MissingParameterException extends ParameterException {
        private static final long serialVersionUID = 5075678535706338753L;
  // 序列化ID。
        public MissingParameterException(final CommandLine commandLine, final String msg) {
            super(commandLine, msg);
   // 构造函数，使用指定的CommandLine和详细消息构造一个新的MissingParameterException。
        }

        private static MissingParameterException create(final CommandLine cmd, final Collection<Field> missing, final String separator) {
            if (missing.size() == 1) {
    // 如果只有一个缺失的参数。
                return new MissingParameterException(cmd, "Missing required option '"
                        + describe(missing.iterator().next(), separator) + "'");
    // 返回一个针对单个缺失选项的异常消息。
            }
            final List<String> names = new ArrayList<>(missing.size());
   // 创建一个列表来存储所有缺失参数的描述名称。
            for (final Field field : missing) {
    // 遍历所有缺失的字段。
                names.add(describe(field, separator));
    // 添加每个字段的描述名称。
            }
            return new MissingParameterException(cmd, "Missing required options " + names.toString());
   // 返回一个针对多个缺失选项的异常消息。
        }
        private static String describe(final Field field, final String separator) {
            final String prefix = (field.isAnnotationPresent(Option.class))
                ? field.getAnnotation(Option.class).names()[0] + separator
                : "params[" + field.getAnnotation(Parameters.class).index() + "]" + separator;
   // 根据字段是Option还是Parameters注解，生成不同的前缀。
   // 如果是Option，使用其第一个名称作为前缀；如果是Parameters，使用其索引作为前缀。
            return prefix + Help.DefaultParamLabelRenderer.renderParameterName(field);
   // 返回完整的参数描述字符串，包括前缀和参数名称。
        }
    }

    /**
     * Exception indicating that multiple fields have been annotated with the same Option name.
     */
// 指示多个字段使用相同Option名称注解的异常。
    public static class DuplicateOptionAnnotationsException extends InitializationException {
        private static final long serialVersionUID = -3355128012575075641L;
  // 序列化ID。
        public DuplicateOptionAnnotationsException(final String msg) { super(msg); }
  // 构造函数，使用指定的详细消息构造一个新的DuplicateOptionAnnotationsException。

        private static DuplicateOptionAnnotationsException create(final String name, final Field field1, final Field field2) {
            return new DuplicateOptionAnnotationsException("Option name '" + name + "' is used by both " +
                    field1.getDeclaringClass().getName() + "." + field1.getName() + " and " +
                    field2.getDeclaringClass().getName() + "." + field2.getName());
                    // 返回一个新的DuplicateOptionAnnotationsException实例，指示哪个选项名称被哪些字段重复使用。
        }
    }
    /** Exception indicating that there was a gap in the indices of the fields annotated with {@link Parameters}. */
    // 表示用 @Parameters 注解的字段在索引上存在间隙时抛出的异常。
    public static class ParameterIndexGapException extends InitializationException {
        private static final long serialVersionUID = -1520981133257618319L;
        // 用于序列化的版本 UID。
        public ParameterIndexGapException(final String msg) { super(msg); }
        // 构造方法，接收一个错误消息。
        // 参数:
        // msg: 异常的详细信息。
    }
    /** Exception indicating that a command line argument could not be mapped to any of the fields annotated with
     * {@link Option} or {@link Parameters}. */
    // 表示命令行参数无法映射到任何带有 @Option 或 @Parameters 注解的字段时抛出的异常。
    public static class UnmatchedArgumentException extends ParameterException {
        private static final long serialVersionUID = -8700426380701452440L;
        // 用于序列化的版本 UID。
        public UnmatchedArgumentException(final CommandLine commandLine, final String msg) { super(commandLine, msg); }
        // 构造方法，接收 CommandLine 实例和错误消息。
        // 参数:
        // commandLine: 关联的 CommandLine 实例。
        // msg: 异常的详细信息。
        public UnmatchedArgumentException(final CommandLine commandLine, final Stack<String> args) { this(commandLine, new ArrayList<>(reverse(args))); }
        // 构造方法，接收 CommandLine 实例和未匹配的参数栈。
        // 该构造方法会将参数栈转换为列表并反转，然后调用另一个构造方法。
        // 参数:
        // commandLine: 关联的 CommandLine 实例。
        // args: 未匹配的参数栈。
        public UnmatchedArgumentException(final CommandLine commandLine, final List<String> args) { this(commandLine, "Unmatched argument" + (args.size() == 1 ? " " : "s ") + args); }
        // 构造方法，接收 CommandLine 实例和未匹配的参数列表。
        // 根据参数数量，生成不同的错误消息（单数或复数形式）。
        // 参数:
        // commandLine: 关联的 CommandLine 实例。
        // args: 未匹配的参数列表。
    }
    /** Exception indicating that more values were specified for an option or parameter than its {@link Option#arity() arity} allows. */
    // 表示为选项或参数指定的值多于其允许的基数（arity）时抛出的异常。
    public static class MaxValuesforFieldExceededException extends ParameterException {
        private static final long serialVersionUID = 6536145439570100641L;
        // 用于序列化的版本 UID。
        public MaxValuesforFieldExceededException(final CommandLine commandLine, final String msg) { super(commandLine, msg); }
        // 构造方法，接收 CommandLine 实例和错误消息。
        // 参数:
        // commandLine: 关联的 CommandLine 实例。
        // msg: 异常的详细信息。
    }
    /** Exception indicating that an option for a single-value option field has been specified multiple times on the command line. */
    // 表示命令行中多次指定了单值选项字段的选项时抛出的异常。
    public static class OverwrittenOptionException extends ParameterException {
        private static final long serialVersionUID = 1338029208271055776L;
        // 用于序列化的版本 UID。
        public OverwrittenOptionException(final CommandLine commandLine, final String msg) { super(commandLine, msg); }
        // 构造方法，接收 CommandLine 实例和错误消息。
        // 参数:
        // commandLine: 关联的 CommandLine 实例。
        // msg: 异常的详细信息。
    }
    /**
     * Exception indicating that an annotated field had a type for which no {@link ITypeConverter} was
     * {@linkplain #registerConverter(Class, ITypeConverter) registered}.
     */
    // 表示注解字段的类型没有注册对应的 ITypeConverter 时抛出的异常。
    public static class MissingTypeConverterException extends ParameterException {
        private static final long serialVersionUID = -6050931703233083760L;
        // 用于序列化的版本 UID。
        public MissingTypeConverterException(final CommandLine commandLine, final String msg) { super(commandLine, msg); }
        // 构造方法，接收 CommandLine 实例和错误消息。
        // 参数:
        // commandLine: 关联的 CommandLine 实例。
        // msg: 异常的详细信息。
    }
}
