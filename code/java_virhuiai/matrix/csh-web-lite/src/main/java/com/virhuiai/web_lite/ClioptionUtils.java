package com.virhuiai.web_lite;


import com.virhuiai.log.logext.LogFactory;
import org.apache.commons.logging.Log;

public class ClioptionUtils {
    private static final Log LOGGER = LogFactory.getLog(ClioptionUtils.class);


//    public static  String getOptionValue(String opt, String defaultValue) throws Exception {
//        return CshClioptionUtils.getOptionValue(opt, defaultValue);
//    }
//
//    public static void parseCmd(String[] args){
//        ClioptionUtils.dealOption(args);
//        try {
//            CshClioptionUtils.parseCmd(args);
//        } catch (Exception e) {
//            LOGGER.error("失败于：CshClioptionUtils.parseCmd", e);
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static void dealOption(String[] args){
//        CshClioptionUtils.addOption(options->{
//            // 创建帮助选项
//            Option helpOption = Option.builder("h") // 短选项 "h"
//                    .longOpt("help") // 长选项 "help"
//                    .desc("显示帮助信息") // 描述
//                    .build();
//            options.addOption(helpOption);
//        });
//        /////////////
//        CshClioptionUtils.addOption(options->{
//            // 创建选项
//            Option option = Option.builder()// 无短选项
//                    .longOpt("try")// 长选项 "mode"
//                    .desc("try it")
//                    .hasArg()//指明该选项后需要跟参数值
//                    .argName("试一试_打开")//参数的名称，用于帮助信息中显示
//                    .build();
//            options.addOption(option);
//        });
//
//        CshClioptionUtils.addOption(options->{
//            // 创建选项
//            Option option = Option.builder()// 无短选项
//                    .longOpt("try_path")// 长选项 "mode"
//                    .desc("try it path")
//                    .hasArg()//指明该选项后需要跟参数值
//                    .argName("试一试_路径")//参数的名称，用于帮助信息中显示
//                    .build();
//            options.addOption(option);
//        });
//
//        CshClioptionUtils.addOption(options->{
//            // 创建选项
//            Option option = Option.builder()// 无短选项
//                    .longOpt("bind_path")// 长选项 "mode"
//                    .desc("逗号分隔，再以冒号分隔")
//                    .hasArg()//指明该选项后需要跟参数值
//                    .argName("bind_path")//参数的名称，用于帮助信息中显示
//                    .build();
//            options.addOption(option);
//        });
//
//
//        CshClioptionUtils.addOption(options->{
//            // 创建选项
//            Option option = Option.builder()// 无短选项
//                    .longOpt("root_resource")// 长选项 "mode"
//                    .desc("根目录使用resource")
//                    .hasArg()//指明该选项后需要跟参数值
//                    .argName("根目录使用resource")//参数的名称，用于帮助信息中显示
//                    .build();
//            options.addOption(option);
//        });
//
//
//        CshClioptionUtils.addOption(options->{
//            // 创建选项
//            Option option = Option.builder()// 无短选项
//                    .longOpt("root_path")// 长选项 "mode"
//                    .desc("根目录使用root_path")
//                    .hasArg()//指明该选项后需要跟参数值
//                    .argName("根目录使用root_path")//参数的名称，用于帮助信息中显示
//                    .build();
//            options.addOption(option);
//        });
//        /////////////
//
//
//
////        CshClioptionUtils.addOption(options->{
////            // 创建选项
////            Option option = Option.builder("m")// 短选项 "m"
////                    .longOpt("mode")// 长选项 "mode"
////                    .desc("模式 (server或client)")//（服务器或客户端）
////                    .hasArg()//指明该选项后需要跟参数值
////                    .argName("模式")//参数的名称，用于帮助信息中显示
////                    .build();
////            options.addOption(option);
////        });
//
//
//
////        CshClioptionUtils.parseCmd(args);// getOptionValue 要先调用这个
////
////        String cc = CshClioptionUtils.getOptionValue("abc", "e");
////        System.out.println(cc);
//
////        return false; // 返回 false 表示没有显示帮助信息
//    }
//
//    ////////以下只用于取值
//    // try
//    public static boolean getOptionValue__try() throws Exception {
//        return "1".equals(getOptionValue("try", "0"));
//    }
//
//    // try_path
//    public static String getOptionValue__try_path() throws Exception {
//        return getOptionValue("try_path", null);
//    }
//
//    public static String getOptionValue__bind_path() throws Exception {
//        return getOptionValue("bind_path", null);
//    }
//
//    public static boolean getOptionValue__root_resource() throws Exception {
//        return "1".equals(getOptionValue("root_resource", "0"));
//    }
//    public static String getOptionValue__root_path() throws Exception {
//        return getOptionValue("root_path", null);
//    }

}
