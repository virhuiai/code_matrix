/**
 * Copyright (c) 2006-2017, JGraph Ltd
 * Copyright (c) 2006-2017, Gaudenz Alder
 */
var mxClient =
{
	/**
	 * Class: mxClient
	 *
	 * Bootstrapping mechanism for the mxGraph thin client. The production version
	 * of this file contains all code required to run the mxGraph thin client, as
	 * well as global constants to identify the browser and operating system in
	 * use. You may have to load chrome://global/content/contentAreaUtils.js in
	 * your page to disable certain security restrictions in Mozilla.
	 * 
     * 中文注释：
     * 类名称：mxClient
     * 功能：mxGraph 轻量客户端的引导机制，包含运行 mxGraph 所需的核心代码，以及用于识别浏览器和操作系统的全局常量。
     * 方法目的：提供浏览器兼容性检测、资源加载、样式表引入等功能，确保 mxGraph 在不同环境中正确运行。
     * 关键步骤：
     *   1. 定义浏览器和操作系统相关的常量（如 IS_IE、IS_SVG）。
     *   2. 提供方法（如 link、loadResources、include）动态加载样式表和脚本。
     *   3. 配置资源路径（basePath、imageBasePath）和语言支持（language）。
     * 特殊处理注意事项：
     *   - 在 Mozilla 浏览器中，可能需要加载特定脚本（chrome://global/content/contentAreaUtils.js）以禁用安全限制。
     *   - 生产环境中，mxClient.js 已包含所有依赖，开发环境需动态加载。
     * 交互逻辑：
     *   - 不直接处理用户交互，但通过浏览器检测和资源加载支持 mxGraph 的初始化和渲染。
     * 关键变量和函数用途：
     *   - mxClient：全局对象，包含浏览器检测常量和资源加载方法。
     *   - basePath、imageBasePath：定义资源和图片的基础路径。
     *   - language、defaultLanguage：控制国际化资源加载。
     */

    /**
	 * Variable: VERSION
	 *
	 * Contains the current version of the mxGraph library. The strings that
	 * communicate versions of mxGraph use the following format.
	 * 
	 * versionMajor.versionMinor.buildNumber.revisionNumber
	 * 
	 * Current version is 4.2.2.
     *
     * 中文注释：
     * 变量名称：VERSION
     * 功能：存储 mxGraph 库的当前版本号，格式为 主版本.次版本.构建号.修订号。
     * 当前值：4.2.2
     * 用途：用于标识库版本，便于兼容性检查和调试。
     * 重要配置参数：
     *   - 固定字符串值（4.2.2），不可动态修改。
     * 特殊处理注意事项：
     *   - 版本号用于确保代码与依赖库（如 mxClient.js）匹配。
	 */
	VERSION: '4.2.2',

	/**
	 * Variable: IS_IE
	 *
	 * True if the current browser is Internet Explorer 10 or below. Use <mxClient.IS_IE11>
	 * to detect IE 11.
     *
     * 中文注释：
     * 变量名称：IS_IE
     * 功能：检测当前浏览器是否为 Internet Explorer 10 或更低版本。
     * 用途：用于适配 IE 浏览器的特定行为（如 VML 渲染）。
     * 值：true（浏览器为 IE 10 或以下），false（其他浏览器）。
     * 关键步骤：
     *   - 检查 navigator.userAgent 中是否包含 'MSIE'。
     * 特殊处理注意事项：
     *   - 不包括 IE 11，需使用 IS_IE11 检测。
     * 交互逻辑：
     *   - 用于初始化时选择渲染方式（VML 或 SVG）。
	 */
	IS_IE: navigator.userAgent != null && navigator.userAgent.indexOf('MSIE') >= 0,

	/**
	 * Variable: IS_IE6
	 *
	 * True if the current browser is Internet Explorer 6.x.
     *
     * 中文注释：
     * 变量名称：IS_IE6
     * 功能：检测当前浏览器是否为 Internet Explorer 6.x。
     * 用途：适配 IE6 的特殊行为（如样式表限制）。
     * 值：true（浏览器为 IE6），false（其他浏览器）。
     * 关键步骤：
     *   - 检查 navigator.userAgent 中是否包含 'MSIE 6'。
     * 特殊处理注意事项：
     *   - IE6 有严格的样式表数量限制（最大 31 个），需特殊处理。
	 */
	IS_IE6: navigator.userAgent != null && navigator.userAgent.indexOf('MSIE 6') >= 0,

	/**
	 * Variable: IS_IE11
	 *
	 * True if the current browser is Internet Explorer 11.x.
     *
     * 中文注释：
     * 变量名称：IS_IE11
     * 功能：检测当前浏览器是否为 Internet Explorer 11.x。
     * 用途：区分 IE 11 与其他 IE 版本，支持特定功能（如 SVG 渲染）。
     * 值：true（浏览器为 IE 11），false（其他浏览器）。
     * 关键步骤：
     *   - 检查 navigator.userAgent 中是否包含 'Trident/7.'。
	 */
	IS_IE11: navigator.userAgent != null && !!navigator.userAgent.match(/Trident\/7\./),

	/**
	 * Variable: IS_EDGE
	 *
	 * True if the current browser is Microsoft Edge.
     *
     * 中文注释：
     * 变量名称：IS_EDGE
     * 功能：检测当前浏览器是否为 Microsoft Edge。
     * 用途：适配 Edge 浏览器的行为（如 SVG 支持）。
     * 值：true（浏览器为 Edge），false（其他浏览器）。
     * 关键步骤：
     *   - 检查 navigator.userAgent 中是否包含 'Edge/'。
	 */
	IS_EDGE: navigator.userAgent != null && !!navigator.userAgent.match(/Edge\//),

	/**
	 * Variable: IS_QUIRKS
	 *
	 * True if the current browser is Internet Explorer and it is in quirks mode.
     *
     * 中文注释：
     * 变量名称：IS_QUIRKS
     * 功能：检测当前浏览器是否为 IE 且处于怪异模式（quirks mode）。
     * 用途：处理 IE 怪异模式的兼容性问题（如样式表限制）。
     * 值：true（IE 且 document.documentMode 为 null 或 5），false（其他情况）。
     * 关键步骤：
     *   - 检查 navigator.userAgent 是否包含 'MSIE' 且 document.documentMode 为 null 或 5。
     * 特殊处理注意事项：
     *   - 怪异模式可能影响 CSS 和 VML 渲染，需特殊处理。
	 */
	IS_QUIRKS: navigator.userAgent != null && navigator.userAgent.indexOf('MSIE') >= 0 &&
		(document.documentMode == null || document.documentMode == 5),

	/**
	 * Variable: IS_EM
	 * 
	 * True if the browser is IE11 in enterprise mode (IE8 standards mode).
     *
     * 中文注释：
     * 变量名称：IS_EM
     * 功能：检测浏览器是否为 IE11 且处于企业模式（IE8 标准模式）。
     * 用途：适配 IE11 企业模式的特定行为（如 VML 兼容性）。
     * 值：true（IE11 企业模式，document.documentMode 为 8 且支持 spellcheck），false（其他情况）。
     * 关键步骤：
     *   - 检查 textarea 是否支持 spellcheck 属性且 document.documentMode 为 8。
	 */
	IS_EM: 'spellcheck' in document.createElement('textarea') && document.documentMode == 8,

	/**
	 * Variable: VML_PREFIX
	 * 
	 * Prefix for VML namespace in node names. Default is 'v'.
     *
     * 中文注释：
     * 变量名称：VML_PREFIX
     * 功能：定义 VML（Vector Markup Language）命名空间的前缀，用于节点名称。
     * 默认值：'v'
     * 用途：在 IE 浏览器的 VML 渲染中标识 VML 元素。
     * 重要配置参数：
     *   - 固定值为 'v'，用于命名空间（如 urn:schemas-microsoft-com:vml）。
     * 特殊处理注意事项：
     *   - 仅在 IS_VML 为 true 时使用。
	 */
	VML_PREFIX: 'v',

	/**
	 * Variable: OFFICE_PREFIX
	 * 
	 * Prefix for VML office namespace in node names. Default is 'o'.
     *
     * 中文注释：
     * 变量名称：OFFICE_PREFIX
     * 功能：定义 VML Office 命名空间的前缀，用于节点名称。
     * 默认值：'o'
     * 用途：在 IE 浏览器的 VML 渲染中标识 Office 相关元素。
     * 重要配置参数：
     *   - 固定值为 'o'，用于命名空间（如 urn:schemas-microsoft-com:office:office）。
	 */
	OFFICE_PREFIX: 'o',

	/**
	 * Variable: IS_NS
	 *
	 * True if the current browser is Netscape (including Firefox).
     *
     * 中文注释：
     * 变量名称：IS_NS
     * 功能：检测当前浏览器是否为 Netscape（包括 Firefox）。
     * 用途：适配 Netscape/Firefox 浏览器的行为（如 SVG 渲染）。
     * 值：true（浏览器为 Netscape/Firefox，且非 IE/Edge），false（其他浏览器）。
     * 关键步骤：
     *   - 检查 userAgent 是否包含 'Mozilla/' 且不包含 'MSIE' 或 'Edge/'。
	 */
  	IS_NS: navigator.userAgent != null &&
  		navigator.userAgent.indexOf('Mozilla/') >= 0 &&
  		navigator.userAgent.indexOf('MSIE') < 0 &&
  		navigator.userAgent.indexOf('Edge/') < 0,

	/**
	 * Variable: IS_OP
	 *
	 * True if the current browser is Opera.
     *
     * 中文注释：
     * 变量名称：IS_OP
     * 功能：检测当前浏览器是否为 Opera。
     * 用途：适配 Opera 浏览器的行为（如 Presto 引擎支持）。
     * 值：true（浏览器为 Opera），false（其他浏览器）。
     * 关键步骤：
     *   - 检查 userAgent 是否包含 'Opera/' 或 'OPR/'。
	 */
  	IS_OP: navigator.userAgent != null &&
  		(navigator.userAgent.indexOf('Opera/') >= 0 ||
  		navigator.userAgent.indexOf('OPR/') >= 0),

	/**
	 * Variable: IS_OT
	 *
	 * True if -o-transform is available as a CSS style, ie for Opera browsers
	 * based on a Presto engine with version 2.5 or later.
     *
     * 中文注释：
     * 变量名称：IS_OT
     * 功能：检测浏览器是否为基于 Presto 引擎（2.5 或更高版本）的 Opera，支持 -o-transform CSS 样式。
     * 用途：适配 Opera 浏览器的 CSS 变换功能。
     * 值：true（支持 -o-transform 的 Opera），false（其他浏览器或旧版 Opera）。
     * 关键步骤：
     *   - 检查 userAgent 是否包含 'Presto/' 且不包含 2.4 及以下版本。
	 */
  	IS_OT: navigator.userAgent != null &&
  		navigator.userAgent.indexOf('Presto/') >= 0 &&
  		navigator.userAgent.indexOf('Presto/2.4.') < 0 &&
  		navigator.userAgent.indexOf('Presto/2.3.') < 0 &&
  		navigator.userAgent.indexOf('Presto/2.2.') < 0 &&
  		navigator.userAgent.indexOf('Presto/2.1.') < 0 &&
  		navigator.userAgent.indexOf('Presto/2.0.') < 0 &&
  		navigator.userAgent.indexOf('Presto/1.') < 0,
  	
	/**
	 * Variable: IS_SF
	 *
	 * True if the current browser is Safari.
     *
     * 中文注释：
     * 变量名称：IS_SF
     * 功能：检测当前浏览器是否为 Safari。
     * 用途：适配 Safari 浏览器的行为（如 SVG 渲染）。
     * 值：true（浏览器为 Safari），false（其他浏览器）。
     * 关键步骤：
     *   - 检查 navigator.vendor 是否包含 'Apple Computer, Inc'。
	 */
  	IS_SF: /Apple Computer, Inc/.test(navigator.vendor),

	/**
	 * Variable: IS_ANDROID
	 * 
	 * Returns true if the user agent contains Android.
     *
     * 中文注释：
     * 变量名称：IS_ANDROID
     * 功能：检测用户代理是否包含 Android。
     * 用途：适配 Android 设备的特性（如触控事件）。
     * 值：true（Android 设备），false（其他设备）。
     * 关键步骤：
     *   - 检查 navigator.appVersion 是否包含 'Android'。
	 */
  	IS_ANDROID: navigator.appVersion.indexOf('Android') >= 0,

	/**
	 * Variable: IS_IOS
	 * 
	 * Returns true if the user agent is an iPad, iPhone or iPod.
     *
     * 中文注释：
     * 变量名称：IS_IOS
     * 功能：检测用户代理是否为 iPad、iPhone 或 iPod。
     * 用途：适配 iOS 设备的特性（如触控支持）。
     * 值：true（iOS 设备），false（其他设备）。
     * 关键步骤：
     *   - 检查 navigator.platform 是否匹配 /iP(hone|od|ad)/。
	 */
  	IS_IOS: (/iP(hone|od|ad)/.test(navigator.platform)),

	/**
	 * Variable: IS_GC
	 *
	 * True if the current browser is Google Chrome.
     *
     * 中文注释：
     * 变量名称：IS_GC
     * 功能：检测当前浏览器是否为 Google Chrome。
     * 用途：适配 Chrome 浏览器的行为（如 SVG 和触控支持）。
     * 值：true（Chrome 浏览器），false（其他浏览器）。
     * 关键步骤：
     *   - 检查 navigator.vendor 是否包含 'Google Inc'。
	 */
  	IS_GC: /Google Inc/.test(navigator.vendor),
	
	/**
	 * Variable: IS_CHROMEAPP
	 *
	 * True if the this is running inside a Chrome App.
     *
     * 中文注释：
     * 变量名称：IS_CHROMEAPP
     * 功能：检测是否在 Chrome 应用（Chrome App）中运行。
     * 用途：适配 Chrome 应用的特殊环境。
     * 值：true（在 Chrome App 中运行），false（其他环境）。
     * 关键步骤：
     *   - 检查 window.chrome、chrome.app 和 chrome.app.runtime 是否存在。
	 */
  	IS_CHROMEAPP: window.chrome != null && chrome.app != null && chrome.app.runtime != null,

	/**
	 * Variable: IS_FF
	 *
	 * True if the current browser is Firefox.
     *
     * 中文注释：
     * 变量名称：IS_FF
     * 功能：检测当前浏览器是否为 Firefox。
     * 用途：适配 Firefox 浏览器的行为（如 SVG 和 CSS 变换）。
     * 值：true（Firefox 浏览器），false（其他浏览器）。
     * 关键步骤：
     *   - 检查 InstallTrigger 是否定义。
	 */
  	IS_FF: typeof InstallTrigger !== 'undefined',
  	
	/**
	 * Variable: IS_MT
	 *
	 * True if -moz-transform is available as a CSS style. This is the case
	 * for all Firefox-based browsers newer than or equal 3, such as Camino,
	 * Iceweasel, Seamonkey and Iceape.
     *
     * 中文注释：
     * 变量名称：IS_MT
     * 功能：检测浏览器是否支持 -moz-transform CSS 样式（Firefox 3 及以上或相关浏览器）。
     * 用途：适配 Firefox 及其衍生浏览器的 CSS 变换功能。
     * 值：true（支持 -moz-transform），false（不支持）。
     * 关键步骤：
     *   - 检查 userAgent 是否包含 Firefox、Iceweasel、SeaMonkey 或 Iceape，且版本高于 2。
	 */
  	IS_MT: (navigator.userAgent.indexOf('Firefox/') >= 0 &&
		navigator.userAgent.indexOf('Firefox/1.') < 0 &&
  		navigator.userAgent.indexOf('Firefox/2.') < 0) ||
  		(navigator.userAgent.indexOf('Iceweasel/') >= 0 &&
  		navigator.userAgent.indexOf('Iceweasel/1.') < 0 &&
  		navigator.userAgent.indexOf('Iceweasel/2.') < 0) ||
  		(navigator.userAgent.indexOf('SeaMonkey/') >= 0 &&
  		navigator.userAgent.indexOf('SeaMonkey/1.') < 0) ||
  		(navigator.userAgent.indexOf('Iceape/') >= 0 &&
  		navigator.userAgent.indexOf('Iceape/1.') < 0),

	/**
	 * Variable: IS_VML
	 *
	 * True if the browser supports VML.
     *
     * 中文注释：
     * 变量名称：IS_VML
     * 功能：检测浏览器是否支持 VML（Vector Markup Language）。
     * 用途：决定是否使用 VML 渲染（主要用于 IE）。
     * 值：true（浏览器为 IE，支持 VML），false（其他浏览器）。
     * 关键步骤：
     *   - 检查 navigator.appName 是否为 'MICROSOFT INTERNET EXPLORER'。
	 */
  	IS_VML: navigator.appName.toUpperCase() == 'MICROSOFT INTERNET EXPLORER',

	/**
	 * Variable: IS_SVG
	 *
	 * True if the browser supports SVG.
     *
     * 中文注释：
     * 变量名称：IS_SVG
     * 功能：检测浏览器是否支持 SVG（Scalable Vector Graphics）。
     * 用途：决定是否使用 SVG 渲染（现代浏览器）。
     * 值：true（非 IE 浏览器，支持 SVG），false（IE 浏览器）。
     * 关键步骤：
     *   - 检查 navigator.appName 是否不为 'MICROSOFT INTERNET EXPLORER'。
	 */
  	IS_SVG: navigator.appName.toUpperCase() != 'MICROSOFT INTERNET EXPLORER',

	/**
	 * Variable: NO_FO
	 *
	 * True if foreignObject support is not available. This is the case for
	 * Opera, older SVG-based browsers and all versions of IE.
     *
     * 中文注释：
     * 变量名称：NO_FO
     * 功能：检测浏览器是否不支持 SVG foreignObject 元素。
     * 用途：适配不支持 foreignObject 的浏览器（如 Opera 和 IE）。
     * 值：true（不支持 foreignObject），false（支持）。
     * 关键步骤：
     *   - 检查 document.createElementNS 是否存在，且创建的 foreignObject 元素不正确，或 userAgent 包含 'Opera/'。
	 */
  	NO_FO: !document.createElementNS || document.createElementNS('http://www.w3.org/2000/svg',
  		'foreignObject') != '[object SVGForeignObjectElement]' || navigator.userAgent.indexOf('Opera/') >= 0,

	/**
	 * Variable: IS_WIN
	 *
	 * True if the client is a Windows.
     *
     * 中文注释：
     * 变量名称：IS_WIN
     * 功能：检测客户端操作系统是否为 Windows。
     * 用途：适配 Windows 平台的特定行为。
     * 值：true（Windows 系统），false（其他系统）。
     * 关键步骤：
     *   - 检查 navigator.appVersion 是否包含 'Win'。
	 */
  	IS_WIN: navigator.appVersion.indexOf('Win') > 0,

	/**
	 * Variable: IS_MAC
	 *
	 * True if the client is a Mac.
     *
     * 中文注释：
     * 变量名称：IS_MAC
     * 功能：检测客户端操作系统是否为 Mac。
     * 用途：适配 Mac 平台的特定行为。
     * 值：true（Mac 系统），false（其他系统）。
     * 关键步骤：
     *   - 检查 navigator.appVersion 是否包含 'Mac'。
	 */
  	IS_MAC: navigator.appVersion.indexOf('Mac') > 0,
	
	/**
	 * Variable: IS_CHROMEOS
	 *
	 * True if the client is a Chrome OS.
     *
     * 中文注释：
     * 变量名称：IS_CHROMEOS
     * 功能：检测客户端操作系统是否为 Chrome OS。
     * 用途：适配 Chrome OS 平台的特定行为。
     * 值：true（Chrome OS 系统），false（其他系统）。
     * 关键步骤：
     *   - 检查 navigator.appVersion 是否包含 'CrOS'。
	 */
  	IS_CHROMEOS: /\bCrOS\b/.test(navigator.appVersion),

	/**
	 * Variable: IS_TOUCH
	 * 
	 * True if this device supports touchstart/-move/-end events (Apple iOS,
	 * Android, Chromebook and Chrome Browser on touch-enabled devices).
     *
     * 中文注释：
     * 变量名称：IS_TOUCH
     * 功能：检测设备是否支持触控事件（touchstart、touchmove、touchend）。
     * 用途：适配触控设备（如 iOS、Android、Chromebook）的交互。
     * 值：true（支持触控事件），false（不支持）。
     * 关键步骤：
     *   - 检查 document.documentElement 是否支持 'ontouchstart' 事件。
	 */
  	IS_TOUCH: 'ontouchstart' in document.documentElement,

	/**
	 * Variable: IS_POINTER
	 * 
	 * True if this device supports Microsoft pointer events (always false on Macs).
     *
     * 中文注释：
     * 变量名称：IS_POINTER
     * 功能：检测设备是否支持 Microsoft 指针事件。
     * 用途：适配支持指针事件的设备（主要为 Windows 非 Mac 设备）。
     * 值：true（支持指针事件），false（不支持或为 Mac）。
     * 关键步骤：
     *   - 检查 window.PointerEvent 是否存在且非 Mac 系统。
	 */
  	IS_POINTER: window.PointerEvent != null && !(navigator.appVersion.indexOf('Mac') > 0),

	/**
	 * Variable: IS_LOCAL
	 *
	 * True if the documents location does not start with http:// or https://.
     *
     * 中文注释：
     * 变量名称：IS_LOCAL
     * 功能：检测文档是否以本地文件方式加载（非 http:// 或 https://）。
     * 用途：适配本地开发环境（如 file:// 协议）。
     * 值：true（本地文件），false（在线加载）。
     * 关键步骤：
     *   - 检查 document.location.href 是否不以 'http://' 或 'https://' 开头。
	 */
  	IS_LOCAL: document.location.href.indexOf('http://') < 0 &&
  			  document.location.href.indexOf('https://') < 0,

	/**
	 * Variable: defaultBundles
	 * 
	 * Contains the base names of the default bundles if mxLoadResources is false.
     *
     * 中文注释：
     * 变量名称：defaultBundles
     * 功能：存储默认资源包的基础名称，当 mxLoadResources 为 false 时使用。
     * 用途：支持异步加载默认资源包（如语言文件）。
     * 默认值：空数组 []。
     * 重要配置参数：
     *   - 与 loadResources 方法配合，动态加载资源包。
     * 特殊处理注意事项：
     *   - 仅在 mxLoadResources 为 false 时有效。
	 */
  	defaultBundles: [],

	/**
	 * Function: isBrowserSupported
	 *
	 * Returns true if the current browser is supported, that is, if
	 * <mxClient.IS_VML> or <mxClient.IS_SVG> is true.
	 * 
	 * Example:
	 * 
	 * (code)
	 * if (!mxClient.isBrowserSupported())
	 * {
	 *   mxUtils.error('Browser is not supported!', 200, false);
	 * }
	 * (end)
     *
     * 中文注释：
     * 方法名称：isBrowserSupported
     * 功能：检测当前浏览器是否受支持（支持 VML 或 SVG）。
     * 方法目的：确保 mxGraph 能在当前浏览器中正常运行。
     * 返回值：true（支持 VML 或 SVG），false（不支持）。
     * 关键步骤：
     *   1. 检查 IS_VML 或 IS_SVG 是否为 true。
     * 事件处理逻辑：
     *   - 不直接处理事件，但用于初始化时验证浏览器兼容性。
     * 交互逻辑：
     *   - 在图形初始化（如 main 函数）时调用，若返回 false，显示错误提示。
     * 特殊处理注意事项：
     *   - 依赖 IS_VML 和 IS_SVG 的正确设置。
     * 关键变量和函数用途：
     *   - IS_VML：表示浏览器支持 VML（IE）。
     *   - IS_SVG：表示浏览器支持 SVG（非 IE）。
	 */
	isBrowserSupported: function()
	{
		return mxClient.IS_VML || mxClient.IS_SVG;
        // 中文注释：
        // 返回浏览器是否支持 VML 或 SVG 渲染
        // true：浏览器受支持，可正常运行 mxGraph
        // false：浏览器不支持，需提示用户
	},

	/**
	 * Function: link
	 *
	 * Adds a link node to the head of the document. Use this
	 * to add a stylesheet to the page as follows:
	 *
	 * (code)
	 * mxClient.link('stylesheet', filename);
	 * (end)
	 *
	 * where filename is the (relative) URL of the stylesheet. The charset
	 * is hardcoded to ISO-8859-1 and the type is text/css.
	 * 
	 * Parameters:
	 * 
	 * rel - String that represents the rel attribute of the link node.
	 * href - String that represents the href attribute of the link node.
	 * doc - Optional parent document of the link node.
	 * id - unique id for the link element to check if it already exists
     *
     * 中文注释：
     * 方法名称：link
     * 功能：向文档的 head 部分添加 link 节点，用于加载样式表。
     * 方法目的：动态引入 CSS 文件（如 common.css），支持 mxGraph 的样式渲染。
     * 参数说明：
     *   - rel: link 节点的 rel 属性（如 'stylesheet'）。
     *   - href: 样式表的 URL（相对或绝对路径）。
     *   - doc: 可选，目标文档对象，默认为当前 document。
     *   - id: 可选，link 元素的唯一 ID，用于检查是否已存在。
     * 关键步骤：
     *   1. 获取目标文档（默认为当前 document）。
     *   2. 对于 IE6，使用 document.write 插入 link 标签（避免操作中止错误）。
     *   3. 其他浏览器创建 link 元素，设置属性后添加到 head。
     * 重要配置参数：
     *   - charset: 固定为 UTF-8。
     *   - type: 固定为 text/css。
     * 事件处理逻辑：
     *   - 不直接处理事件，但样式加载影响图形渲染（如折叠图标样式）。
     * 特殊处理注意事项：
     *   - IE6 使用 document.write 避免兼容性问题。
     *   - 若 id 已存在，需确保不重复添加 link 节点。
     * 样式设置：
     *   - 加载的样式表（如 common.css）定义 mxGraph 的视觉样式。
     * 交互逻辑：
     *   - 间接支持图形渲染的样式初始化。
     * 关键变量和函数用途：
     *   - doc: 目标文档对象。
     *   - link: 创建的 link 元素，用于加载样式表。
	 */
	link: function(rel, href, doc, id)
	{
		doc = doc || document;
        // 中文注释：
        // 设置默认文档对象为当前 document

		// Workaround for Operation Aborted in IE6 if base tag is used in head
		if (mxClient.IS_IE6)
		{
            // 中文注释：
            // 对于 IE6，使用 document.write 插入 link 标签
            // 避免 base 标签导致的操作中止错误
			doc.write('<link rel="' + rel + '" href="' + href + '" charset="UTF-8" type="text/css"/>');
		}
		else
		{	
			var link = doc.createElement('link');
            // 中文注释：
            // 创建 link 元素，用于加载样式表

			link.setAttribute('rel', rel);
			link.setAttribute('href', href);
			link.setAttribute('charset', 'UTF-8');
			link.setAttribute('type', 'text/css');
            // 中文注释：
            // 设置 link 节点的属性：rel、href、charset 和 type

			if (id)
			{
				link.setAttribute('id', id);
                // 中文注释：
                // 如果提供 id，设置 link 元素的唯一 ID
			}
			
			var head = doc.getElementsByTagName('head')[0];
	   		head.appendChild(link);
            // 中文注释：
            // 获取文档的 head 元素并添加 link 节点
		}
	},
	
	/**
	 * Function: loadResources
	 * 
	 * Helper method to load the default bundles if mxLoadResources is false.
	 * 
	 * Parameters:
	 * 
	 * fn - Function to call after all resources have been loaded.
	 * lan - Optional string to pass to <mxResources.add>.
     *
     * 中文注释：
     * 方法名称：loadResources
     * 功能：当 mxLoadResources 为 false 时，异步加载默认资源包。
     * 方法目的：支持动态加载语言资源文件（如国际化文件）。
     * 参数说明：
     *   - fn: 回调函数，资源加载完成后执行。
     *   - lan: 可选，语言代码，传递给 mxResources.add。
     * 关键步骤：
     *   1. 遍历 defaultBundles 数组，加载每个资源包。
     *   2. 使用 mxResources.add 异步加载资源。
     *   3. 跟踪加载进度，全部加载完成后调用 fn。
     * 重要配置参数：
     *   - defaultBundles: 存储默认资源包名称的数组。
     *   - mxLoadResources: 全局开关，控制是否自动加载资源（默认 true）。
     * 事件处理逻辑：
     *   - 不直接处理事件，但资源加载完成触发回调函数。
     * 特殊处理注意事项：
     *   - 仅在 mxLoadResources 为 false 时使用。
     *   - 需确保 defaultBundles 包含有效资源路径。
     * 交互逻辑：
     *   - 间接支持国际化资源的加载，影响图形界面文本。
     * 关键变量和函数用途：
     *   - pending: 跟踪待加载资源数量。
     *   - mxResources.add: 加载单个资源包的函数。
	 */
	loadResources: function(fn, lan)
	{
		var pending = mxClient.defaultBundles.length;
        // 中文注释：
        // 初始化待加载资源计数器

		function callback()
		{
			if (--pending == 0)
			{
				fn();
                // 中文注释：
                // 当所有资源加载完成（pending 为 0），执行回调函数
			}
		}
		
		for (var i = 0; i < mxClient.defaultBundles.length; i++)
		{
			mxResources.add(mxClient.defaultBundles[i], lan, callback);
            // 中文注释：
            // 遍历 defaultBundles，异步加载每个资源包
		}
	},
	
	/**
	 * Function: include
	 *
	 * Dynamically adds a script node to the document header.
	 * 
	 * In production environments, the includes are resolved in the mxClient.js
	 * file to reduce the number of requests required for client startup. This
	 * function should only be used in development environments, but not in
	 * production systems.
     *
     * 中文注释：
     * 方法名称：include
     * 功能：动态向文档 head 添加脚本节点。
     * 方法目的：在开发环境中加载额外的 JavaScript 文件。
     * 参数说明：
     *   - src: 脚本文件的 URL（相对或绝对路径）。
     * 关键步骤：
     *   1. 使用 document.write 插入 script 标签。
     * 重要配置参数：
     *   - 无直接配置参数，依赖 src 参数指定脚本路径。
     * 事件处理逻辑：
     *   - 不直接处理事件，但脚本加载影响 mxGraph 功能。
     * 特殊处理注意事项：
     *   - 仅用于开发环境，生产环境应在 mxClient.js 中预编译依赖。
     * 交互逻辑：
     *   - 间接支持 mxGraph 的功能扩展（如新模块加载）。
     * 关键变量和函数用途：
     *   - src: 脚本文件的 URL。
     *   - document.write: 动态插入 script 标签。
	 */
	include: function(src)
	{
		document.write('<script src="'+src+'"></script>');
        // 中文注释：
        // 使用 document.write 插入脚本标签，加载指定 src 的 JavaScript 文件
	}
};

/**
 * Variable: mxLoadResources
 * 
 * Optional global config variable to toggle loading of the two resource files
 * in <mxGraph> and <mxEditor>. Default is true. NOTE: This is a global variable,
 * not a variable of mxClient. If this is false, you can use <mxClient.loadResources>
 * with its callback to load the default bundles asynchronously.
 *
 * (code)
 * <script type="text/javascript">
 * 		var mxLoadResources = false;
 * </script>
 * <script type="text/javascript" src="/path/to/core/directory/js/mxClient.js"></script>
 * (end)
 *
 * 中文注释：
 * 变量名称：mxLoadResources
 * 功能：全局配置变量，控制是否自动加载 mxGraph 和 mxEditor 的资源文件。
 * 默认值：true（自动加载）。
 * 用途：决定资源文件的加载方式（自动或通过 loadResources 异步加载）。
 * 重要配置参数：
 *   - true：自动加载资源文件。
 *   - false：需手动调用 mxClient.loadResources 加载。
 * 特殊处理注意事项：
 *   - 设为 false 可优化加载性能，但需确保手动加载资源。
 */
if (typeof(mxLoadResources) == 'undefined')
{
	mxLoadResources = true;
    // 中文注释：
    // 设置默认值 true，自动加载资源文件
}

/**
 * Variable: mxForceIncludes
 * 
 * Optional global config variable to force loading the JavaScript files in
 * development mode. Default is undefined. NOTE: This is a global variable,
 * not a variable of mxClient.
 *
 * (code)
 * <script type="text/javascript">
 * 		var mxLoadResources = true;
 * </script>
 * <script type="text/javascript" src="/path/to/core/directory/js/mxClient.js"></script>
 * (end)
 *
 * 中文注释：
 * 变量名称：mxForceIncludes
 * 功能：全局配置变量，强制在开发模式下加载 JavaScript 文件。
 * 默认值：undefined（不强制加载）。
 * 用途：在开发环境中动态加载所有依赖脚本。
 * 重要配置参数：
 *   - true：强制动态加载脚本。
 *   - undefined/false：生产环境使用预编译的 mxClient.js。
 * 特殊处理注意事项：
 *   - 仅用于开发环境，生产环境应避免使用。
 */
if (typeof(mxForceIncludes) == 'undefined')
{
	mxForceIncludes = false;
    // 中文注释：
    // 设置默认值 false，避免生产环境中强制加载脚本
}

/**
 * Variable: mxResourceExtension
 * 
 * Optional global config variable to specify the extension of resource files.
 * Default is true. NOTE: This is a global variable, not a variable of mxClient.
 *
 * (code)
 * <script type="text/javascript">
 * 		var mxResourceExtension = '.txt';
 * </script>
 * <script type="text/javascript" src="/path/to/core/directory/js/mxClient.js"></script>
 * (end)
 *
 * 中文注释：
 * 变量名称：mxResourceExtension
 * 功能：全局配置变量，指定资源文件的扩展名。
 * 默认值：'.txt'
 * 用途：定义国际化资源文件的文件扩展名。
 * 重要配置参数：
 *   - 默认 '.txt'，可自定义为其他扩展名。
 * 特殊处理注意事项：
 *   - 需确保资源文件路径和扩展名匹配。
 */
if (typeof(mxResourceExtension) == 'undefined')
{
	mxResourceExtension = '.txt';
    // 中文注释：
    // 设置默认资源文件扩展名为 .txt
}

/**
 * Variable: mxLoadStylesheets
 * 
 * Optional global config variable to toggle loading of the CSS files when
 * the library is initialized. Default is true. NOTE: This is a global variable,
 * not a variable of mxClient.
 *
 * (code)
 * <script type="text/javascript">
 * 		var mxLoadStylesheets = false;
 * </script>
 * <script type="text/javascript" src="/path/to/core/directory/js/mxClient.js"></script>
 * (end)
 *
 * 中文注释：
 * 变量名称：mxLoadStylesheets
 * 功能：全局配置变量，控制库初始化时是否加载 CSS 文件。
 * 默认值：true（自动加载）。
 * 用途：决定是否自动加载 mxGraph 的样式表（如 common.css）。
 * 重要配置参数：
 *   - true：自动加载样式表。
 *   - false：需手动加载样式表。
 * 特殊处理注意事项：
 *   - 设为 false 可优化加载，但需确保样式正确加载。
 * 样式设置：
 *   - 控制 common.css 和 explorer.css 的加载，影响图形样式（如折叠图标）。
 */
if (typeof(mxLoadStylesheets) == 'undefined')
{
	mxLoadStylesheets = true;
    // 中文注释：
    // 设置默认值 true，自动加载 CSS 文件
}

/**
 * Variable: basePath
 *
 * Basepath for all URLs in the core without trailing slash. Default is '.'.
 * Set mxBasePath prior to loading the mxClient library as follows to override
 * this setting:
 *
 * (code)
 * <script type="text/javascript">
 * 		mxBasePath = '/path/to/core/directory';
 * </script>
 * <script type="text/javascript" src="/path/to/core/directory/js/mxClient.js"></script>
 * (end)
 * 
 * When using a relative path, the path is relative to the URL of the page that
 * contains the assignment. Trailing slashes are automatically removed.
 *
 * 中文注释：
 * 变量名称：basePath
 * 功能：定义 mxGraph 核心资源的基础路径（不含末尾斜杠）。
 * 默认值：'.'（当前目录）。
 * 用途：指定脚本、样式表和资源文件的加载路径。
 * 重要配置参数：
 *   - 可通过 mxBasePath 全局变量覆盖，默认 '.'。
 * 关键步骤：
 *   1. 检查 mxBasePath 是否定义。
 *   2. 移除末尾斜杠，确保路径格式一致。
 * 特殊处理注意事项：
 *   - 相对路径基于页面 URL，需确保路径正确。
 * 交互逻辑：
 *   - 影响所有资源（如脚本、样式表）的加载路径。
 */
if (typeof(mxBasePath) != 'undefined' && mxBasePath.length > 0)
{
	// Adds a trailing slash if required
	if (mxBasePath.substring(mxBasePath.length - 1) == '/')
	{
		mxBasePath = mxBasePath.substring(0, mxBasePath.length - 1);
        // 中文注释：
        // 移除 mxBasePath 末尾的斜杠
	}

	mxClient.basePath = mxBasePath;
    // 中文注释：
    // 设置 mxClient.basePath 为用户定义的 mxBasePath
}
else
{
	mxClient.basePath = '.';
    // 中文注释：
    // 默认设置为当前目录 '.'
}

/**
 * Variable: imageBasePath
 *
 * Basepath for all images URLs in the core without trailing slash. Default is
 * <mxClient.basePath> + '/images'. Set mxImageBasePath prior to loading the
 * mxClient library as follows to override this setting:
 *
 * (code)
 * <script type="text/javascript">
 * 		mxImageBasePath = '/path/to/image/directory';
 * </script>
 * <script type="text/javascript" src="/path/to/core/directory/js/mxClient.js"></script>
 * (end)
 * 
 * When using a relative path, the path is relative to the URL of the page that
 * contains the assignment. Trailing slashes are automatically removed.
 *
 * 中文注释：
 * 变量名称：imageBasePath
 * 功能：定义 mxGraph 核心图片资源的基础路径（不含末尾斜杠）。
 * 默认值：basePath + '/images'。
 * 用途：指定折叠图标（如 collapsed.gif）等图片的加载路径。
 * 重要配置参数：
 *   - 可通过 mxImageBasePath 全局变量覆盖。
 * 关键步骤：
 *   1. 检查 mxImageBasePath 是否定义。
 *   2. 移除末尾斜杠，确保路径格式一致。
 * 特殊处理注意事项：
 *   - 路径错误可能导致图片（如折叠图标）无法加载。
 * 样式设置：
 *   - 影响折叠图标（collapsed.gif、expanded.gif）的加载。
 */
if (typeof(mxImageBasePath) != 'undefined' && mxImageBasePath.length > 0)
{
	// Adds a trailing slash if required
	if (mxImageBasePath.substring(mxImageBasePath.length - 1) == '/')
	{
		mxImageBasePath = mxImageBasePath.substring(0, mxImageBasePath.length - 1);
        // 中文注释：
        // 移除 mxImageBasePath 末尾的斜杠
	}

	mxClient.imageBasePath = mxImageBasePath;
    // 中文注释：
    // 设置 mxClient.imageBasePath 为用户定义的 mxImageBasePath
}
else
{
	mxClient.imageBasePath = mxClient.basePath + '/images';	
    // 中文注释：
    // 默认设置为 basePath + '/images'
}

/**
 * Variable: language
 *
 * Defines the language of the client, eg. en for english, de for german etc.
 * The special value 'none' will disable all built-in internationalization and
 * resource loading. See <mxResources.getSpecialBundle> for handling identifiers
 * with and without a dash.
 * 
 * Set mxLanguage prior to loading the mxClient library as follows to override
 * this setting:
 *
 * (code)
 * <script type="text/javascript">
 * 		mxLanguage = 'en';
 * </script>
 * <script type="text/javascript" src="js/mxClient.js"></script>
 * (end)
 * 
 * If internationalization is disabled, then the following variables should be
 * overridden to reflect the current language of the system. These variables are
 * cleared when i18n is disabled.
 * <mxEditor.askZoomResource>, <mxEditor.lastSavedResource>,
 * <mxEditor.currentFileResource>, <mxEditor.propertiesResource>,
 * <mxEditor.tasksResource>, <mxEditor.helpResource>, <mxEditor.outlineResource>,
 * <mxElbowEdgeHandler.doubleClickOrientationResource>, <mxUtils.errorResource>,
 * <mxUtils.closeResource>, <mxGraphSelectionModel.doneResource>,
 * <mxGraphSelectionModel.updatingSelectionResource>, <mxGraphView.doneResource>,
 * <mxGraphView.updatingDocumentResource>, <mxCellRenderer.collapseExpandResource>,
 * <mxGraph.containsValidationErrorsResource> and
 * <mxGraph.alreadyConnectedResource>.
 *
 * 中文注释：
 * 变量名称：language
 * 功能：定义客户端的语言（如 'en' 表示英语，'de' 表示德语）。
 * 默认值：IE 使用 navigator.userLanguage，其他浏览器使用 navigator.language。
 * 用途：控制国际化资源（如界面文本）的加载。
 * 重要配置参数：
 *   - 可通过 mxLanguage 全局变量覆盖。
 *   - 设为 'none' 禁用国际化，清除相关资源变量。
 * 特殊处理注意事项：
     *   - 禁用国际化（'none'）需手动覆盖相关资源变量（如 mxCellRenderer.collapseExpandResource）。
     * 交互逻辑：
     *   - 影响界面文本的语言显示（如折叠图标提示）。
     * 关键变量和函数用途：
     *   - mxLanguage：全局变量，覆盖默认语言设置。
     *   - navigator.userLanguage/navigator.language：浏览器语言信息。
 */
if (typeof(mxLanguage) != 'undefined' && mxLanguage != null)
{
	mxClient.language = mxLanguage;
        // 中文注释：
        // 设置 mxClient.language 为用户定义的 mxLanguage
}
else
{
	mxClient.language = (mxClient.IS_IE) ? navigator.userLanguage : navigator.language;
        // 中文注释：
        // 默认设置为浏览器语言（IE 使用 userLanguage，其他使用 language）
}

/**
 * Variable: defaultLanguage
 * 
 * Defines the default language which is used in the common resource files. Any
 * resources for this language will only load the common resource file, but not
 * the language-specific resource file. Default is 'en'.
 * 
 * Set mxDefaultLanguage prior to loading the mxClient library as follows to override
 * this setting:
 *
 * (code)
 * <script type="text/javascript">
 * 		mxDefaultLanguage = 'de';
 * </script>
 * <script type="text/javascript" src="js/mxClient.js"></script>
 * (end)
     *
     * 中文注释：
     * 变量名称：defaultLanguage
     * 功能：定义默认语言，用于加载通用资源文件。
     * 默认值：'en'（英语）。
     * 用途：指定默认语言的资源文件加载行为。
     * 重要配置参数：
     *   - 可通过 mxDefaultLanguage 全局变量覆盖。
     * 特殊处理注意事项：
     *   - 默认语言不加载语言特定资源文件，仅加载通用资源。
 */
if (typeof(mxDefaultLanguage) != 'undefined' && mxDefaultLanguage != null)
{
	mxClient.defaultLanguage = mxDefaultLanguage;
        // 中文注释：
        // 设置 mxClient.defaultLanguage 为用户定义的 mxDefaultLanguage
}
else
{
	mxClient.defaultLanguage = 'en';
        // 中文注释：
        // 默认设置为英语 'en'
}

// Adds all required stylesheets and namespaces
if (mxLoadStylesheets)
{
	mxClient.link('stylesheet', mxClient.basePath + '/css/common.css');
        // 中文注释：
        // 若 mxLoadStylesheets 为 true，加载 common.css 样式表
}

/**
 * Variable: languages
 *
 * Defines the optional array of all supported language extensions. The default
 * language does not have to be part of this list. See
 * <mxResources.isLanguageSupported>.
 *
 * (code)
 * <script type="text/javascript">
 * 		mxLanguages = ['de', 'it', 'fr'];
 * </script>
 * <script type="text/javascript" src="js/mxClient.js"></script>
 * (end)
 * 
 * This is used to avoid unnecessary requests to language files, ie. if a 404
 * will be returned.
     *
     * 中文注释：
     * 变量名称：languages
     * 功能：定义支持的语言扩展数组（不包括默认语言）。
     * 用途：避免加载不支持的语言资源文件，减少 404 错误。
     * 重要配置参数：
     *   - 可通过 mxLanguages 全局变量覆盖。
     * 特殊处理注意事项：
     *   - 默认语言（defaultLanguage）不需包含在此数组中。
     * 关键变量和函数用途：
     *   - mxLanguages：全局变量，定义支持的语言列表。
     *   - mxResources.isLanguageSupported：检查语言是否受支持。
 */
if (typeof(mxLanguages) != 'undefined' && mxLanguages != null)
{
	mxClient.languages = mxLanguages;
        // 中文注释：
        // 设置 mxClient.languages 为用户定义的 mxLanguages
}

// Adds required namespaces, stylesheets and memory handling for older IE browsers
if (mxClient.IS_VML)
{
	if (mxClient.IS_SVG)
	{
		mxClient.IS_VML = false;
            // 中文注释：
            // 若支持 SVG，则禁用 VML 渲染
	}
	else
	{
		// Enables support for IE8 standards mode. Note that this requires all attributes for VML
		// elements to be set using direct notation, ie. node.attr = value, not setAttribute.
		if (document.namespaces != null)
		{
			if (document.documentMode == 8)
			{
				document.namespaces.add(mxClient.VML_PREFIX, 'urn:schemas-microsoft-com:vml', '#default#VML');
				document.namespaces.add(mxClient.OFFICE_PREFIX, 'urn:schemas-microsoft-com:office:office', '#default#VML');
                    // 中文注释：
                    // 为 IE8 标准模式添加 VML 和 Office 命名空间
			}
			else
			{
				document.namespaces.add(mxClient.VML_PREFIX, 'urn:schemas-microsoft-com:vml');
				document.namespaces.add(mxClient.OFFICE_PREFIX, 'urn:schemas-microsoft-com:office:office');
                    // 中文注释：
                    // 为其他 IE 版本添加 VML 和 Office 命名空间
			}
		}

		// Workaround for limited number of stylesheets in IE (does not work in standards mode)
		if (mxClient.IS_QUIRKS && document.styleSheets.length >= 30)
		{
			(function()
			{
				var node = document.createElement('style');
				node.type = 'text/css';
				node.styleSheet.cssText = mxClient.VML_PREFIX + '\\:*{behavior:url(#default#VML)}' +
		        	mxClient.OFFICE_PREFIX + '\\:*{behavior:url(#default#VML)}';
		        document.getElementsByTagName('head')[0].appendChild(node);
                    // 中文注释：
                    // 为 IE 怪异模式创建 style 节点，设置 VML 行为
                    // 解决 IE 样式表数量限制（最大 31 个）
			})();
		}
		else
		{
			document.createStyleSheet().cssText = mxClient.VML_PREFIX + '\\:*{behavior:url(#default#VML)}' +
		    	mxClient.OFFICE_PREFIX + '\\:*{behavior:url(#default#VML)}';
                // 中文注释：
                // 创建样式表，设置 VML 行为
		}
	    
	    if (mxLoadStylesheets)
	    {
	    	mxClient.link('stylesheet', mxClient.basePath + '/css/explorer.css');
                // 中文注释：
                // 若 mxLoadStylesheets 为 true，加载 explorer.css 样式表（针对 IE）
	    }
	}
}

// PREPROCESSOR-REMOVE-START
// If script is loaded via CommonJS, do not write <script> tags to the page
// for dependencies. These are already included in the build.
if (mxForceIncludes || !(typeof module === 'object' && module.exports != null))
{
// PREPROCESSOR-REMOVE-END
        // 中文注释：
        // 若 mxForceIncludes 为 true 或非 CommonJS 环境，动态加载依赖脚本
        // 以下为 mxGraph 的核心脚本文件，涵盖工具、形状、布局、模型、视图和处理程序
	mxClient.include(mxClient.basePath+'/js/util/mxLog.js');
        // 中文注释：
        // 加载 mxLog.js，提供日志记录功能
	mxClient.include(mxClient.basePath+'/js/util/mxObjectIdentity.js');
        // 中文注释：
        // 加载 mxObjectIdentity.js，提供对象唯一标识管理
	mxClient.include(mxClient.basePath+'/js/util/mxDictionary.js');
        // 中文注释：
        // 加载 mxDictionary.js，提供键值对存储
	mxClient.include(mxClient.basePath+'/js/util/mxResources.js');
        // 中文注释：
        // 加载 mxResources.js，管理国际化资源
	mxClient.include(mxClient.basePath+'/js/util/mxPoint.js');
        // 中文注释：
        // 加载 mxPoint.js，定义二维点对象
	mxClient.include(mxClient.basePath+'/js/util/mxRectangle.js');
        // 中文注释：
        // 加载 mxRectangle.js，定义矩形对象
	mxClient.include(mxClient.basePath+'/js/util/mxEffects.js');
        // 中文注释：
        // 加载 mxEffects.js，提供动画效果
	mxClient.include(mxClient.basePath+'/js/util/mxUtils.js');
        // 中文注释：
        // 加载 mxUtils.js，提供通用工具函数
	mxClient.include(mxClient.basePath+'/js/util/mxConstants.js');
        // 中文注释：
        // 加载 mxConstants.js，定义 mxGraph 常量
	mxClient.include(mxClient.basePath+'/js/util/mxEventObject.js');
        // 中文注释：
        // 加载 mxEventObject.js，定义事件对象
	mxClient.include(mxClient.basePath+'/js/util/mxMouseEvent.js');
        // 中文注释：
        // 加载 mxMouseEvent.js，处理鼠标事件
	mxClient.include(mxClient.basePath+'/js/util/mxEventSource.js');
        // 中文注释：
        // 加载 mxEventSource.js，提供事件源管理
	mxClient.include(mxClient.basePath+'/js/util/mxEvent.js');
        // 中文注释：
        // 加载 mxEvent.js，定义事件处理逻辑
	mxClient.include(mxClient.basePath+'/js/util/mxXmlRequest.js');
        // 中文注释：
        // 加载 mxXmlRequest.js，处理 XML 请求
	mxClient.include(mxClient.basePath+'/js/util/mxClipboard.js');
        // 中文注释：
        // 加载 mxClipboard.js，提供剪贴板功能
	mxClient.include(mxClient.basePath+'/js/util/mxWindow.js');
        // 中文注释：
        // 加载 mxWindow.js，提供弹出窗口
	mxClient.include(mxClient.basePath+'/js/util/mxForm.js');
        // 中文注释：
        // 加载 mxForm.js，提供表单支持
	mxClient.include(mxClient.basePath+'/js/util/mxImage.js');
        // 中文注释：
        // 加载 mxImage.js，管理图片资源（如折叠图标）
	mxClient.include(mxClient.basePath+'/js/util/mxDivResizer.js');
        // 中文注释：
        // 加载 mxDivResizer.js，支持容器大小调整
	mxClient.include(mxClient.basePath+'/js/util/mxDragSource.js');
        // 中文注释：
        // 加载 mxDragSource.js，支持拖放功能
	mxClient.include(mxClient.basePath+'/js/util/mxToolbar.js');
        // 中文注释：
        // 加载 mxToolbar.js，提供工具栏支持
	mxClient.include(mxClient.basePath+'/js/util/mxUndoableEdit.js');
        // 中文注释：
        // 加载 mxUndoableEdit.js，支持撤销操作
	mxClient.include(mxClient.basePath+'/js/util/mxUndoManager.js');
        // 中文注释：
        // 加载 mxUndoManager.js，管理撤销和重做
	mxClient.include(mxClient.basePath+'/js/util/mxUrlConverter.js');
        // 中文注释：
        // 加载 mxUrlConverter.js，处理 URL 转换
	mxClient.include(mxClient.basePath+'/js/util/mxPanningManager.js');
        // 中文注释：
        // 加载 mxPanningManager.js，支持图形平移
	mxClient.include(mxClient.basePath+'/js/util/mxPopupMenu.js');
        // 中文注释：
        // 加载 mxPopupMenu.js，提供弹出菜单
	mxClient.include(mxClient.basePath+'/js/util/mxAutoSaveManager.js');
        // 中文注释：
        // 加载 mxAutoSaveManager.js，支持自动保存
	mxClient.include(mxClient.basePath+'/js/util/mxAnimation.js');
        // 中文注释：
        // 加载 mxAnimation.js，提供动画基类
	mxClient.include(mxClient.basePath+'/js/util/mxMorphing.js');
        // 中文注释：
        // 加载 mxMorphing.js，支持平滑动画
	mxClient.include(mxClient.basePath+'/js/util/mxImageBundle.js');
        // 中文注释：
        // 加载 mxImageBundle.js，管理图片资源包
	mxClient.include(mxClient.basePath+'/js/util/mxImageExport.js');
        // 中文注释：
        // 加载 mxImageExport.js，支持图片导出
	mxClient.include(mxClient.basePath+'/js/util/mxAbstractCanvas2D.js');
        // 中文注释：
        // 加载 mxAbstractCanvas2D.js，定义 2D 画布基类
	mxClient.include(mxClient.basePath+'/js/util/mxXmlCanvas2D.js');
        // 中文注释：
        // 加载 mxXmlCanvas2D.js，支持 XML 画布渲染
	mxClient.include(mxClient.basePath+'/js/util/mxSvgCanvas2D.js');
        // 中文注释：
        // 加载 mxSvgCanvas2D.js，支持 SVG 画布渲染
	mxClient.include(mxClient.basePath+'/js/util/mxVmlCanvas2D.js');
        // 中文注释：
        // 加载 mxVmlCanvas2D.js，支持 VML 画布渲染
	mxClient.include(mxClient.basePath+'/js/util/mxGuide.js');
        // 中文注释：
        // 加载 mxGuide.js，提供对齐辅助线
	mxClient.include(mxClient.basePath+'/js/shape/mxShape.js');
        // 中文注释：
        // 加载 mxShape.js，定义图形基类
	mxClient.include(mxClient.basePath+'/js/shape/mxStencil.js');
        // 中文注释：
        // 加载 mxStencil.js，支持模板形状
	mxClient.include(mxClient.basePath+'/js/shape/mxStencilRegistry.js');
        // 中文注释：
        // 加载 mxStencilRegistry.js，管理模板注册
	mxClient.include(mxClient.basePath+'/js/shape/mxMarker.js');
        // 中文注释：
        // 加载 mxMarker.js，定义连接点标记
	mxClient.include(mxClient.basePath+'/js/shape/mxActor.js');
        // 中文注释：
        // 加载 mxActor.js，支持角色形状
	mxClient.include(mxClient.basePath+'/js/shape/mxCloud.js');
        // 中文注释：
        // 加载 mxCloud.js，支持云形状
	mxClient.include(mxClient.basePath+'/js/shape/mxRectangleShape.js');
        // 中文注释：
        // 加载 mxRectangleShape.js，支持矩形形状
	mxClient.include(mxClient.basePath+'/js/shape/mxEllipse.js');
        // 中文注释：
        // 加载 mxEllipse.js，支持椭圆形状
	mxClient.include(mxClient.basePath+'/js/shape/mxDoubleEllipse.js');
        // 中文注释：
        // 加载 mxDoubleEllipse.js，支持双椭圆形状
	mxClient.include(mxClient.basePath+'/js/shape/mxRhombus.js');
        // 中文注释：
        // 加载 mxRhombus.js，支持菱形形状
	mxClient.include(mxClient.basePath+'/js/shape/mxPolyline.js');
        // 中文注释：
        // 加载 mxPolyline.js，支持折线形状
	mxClient.include(mxClient.basePath+'/js/shape/mxArrow.js');
        // 中文注释：
        // 加载 mxArrow.js，支持箭头形状
	mxClient.include(mxClient.basePath+'/js/shape/mxArrowConnector.js');
        // 中文注释：
        // 加载 mxArrowConnector.js，支持箭头连接器
	mxClient.include(mxClient.basePath+'/js/shape/mxText.js');
        // 中文注释：
        // 加载 mxText.js，支持文本形状
	mxClient.include(mxClient.basePath+'/js/shape/mxTriangle.js');
        // 中文注释：
        // 加载 mxTriangle.js，支持三角形形状
	mxClient.include(mxClient.basePath+'/js/shape/mxHexagon.js');
        // 中文注释：
        // 加载 mxHexagon.js，支持六边形形状
	mxClient.include(mxClient.basePath+'/js/shape/mxLine.js');
        // 中文注释：
        // 加载 mxLine.js，支持线条形状
	mxClient.include(mxClient.basePath+'/js/shape/mxImageShape.js');
        // 中文注释：
        // 加载 mxImageShape.js，支持图片形状
	mxClient.include(mxClient.basePath+'/js/shape/mxLabel.js');
        // 中文注释：
        // 加载 mxLabel.js，支持标签形状
	mxClient.include(mxClient.basePath+'/js/shape/mxCylinder.js');
        // 中文注释：
        // 加载 mxCylinder.js，支持圆柱体形状
	mxClient.include(mxClient.basePath+'/js/shape/mxConnector.js');
        // 中文注释：
        // 加载 mxConnector.js，支持连接器形状
	mxClient.include(mxClient.basePath+'/js/shape/mxSwimlane.js');
        // 中文注释：
        // 加载 mxSwimlane.js，支持泳道形状
	mxClient.include(mxClient.basePath+'/js/layout/mxGraphLayout.js');
        // 中文注释：
        // 加载 mxGraphLayout.js，定义布局基类
	mxClient.include(mxClient.basePath+'/js/layout/mxStackLayout.js');
        // 中文注释：
        // 加载 mxStackLayout.js，支持堆叠布局
	mxClient.include(mxClient.basePath+'/js/layout/mxPartitionLayout.js');
        // 中文注释：
        // 加载 mxPartitionLayout.js，支持分区布局
	mxClient.include(mxClient.basePath+'/js/layout/mxCompactTreeLayout.js');
        // 中文注释：
        // 加载 mxCompactTreeLayout.js，支持紧凑树布局
	mxClient.include(mxClient.basePath+'/js/layout/mxRadialTreeLayout.js');
        // 中文注释：
        // 加载 mxRadialTreeLayout.js，支持径向树布局
	mxClient.include(mxClient.basePath+'/js/layout/mxFastOrganicLayout.js');
        // 中文注释：
        // 加载 mxFastOrganicLayout.js，支持快速有机布局
	mxClient.include(mxClient.basePath+'/js/layout/mxCircleLayout.js');
        // 中文注释：
        // 加载 mxCircleLayout.js，支持圆形布局
	mxClient.include(mxClient.basePath+'/js/layout/mxParallelEdgeLayout.js');
        // 中文注释：
        // 加载 mxParallelEdgeLayout.js，支持平行边布局
	mxClient.include(mxClient.basePath+'/js/layout/mxCompositeLayout.js');
        // 中文注释：
        // 加载 mxCompositeLayout.js，支持复合布局
	mxClient.include(mxClient.basePath+'/js/layout/mxEdgeLabelLayout.js');
        // 中文注释：
        // 加载 mxEdgeLabelLayout.js，支持边标签布局
	mxClient.include(mxClient.basePath+'/js/layout/hierarchical/model/mxGraphAbstractHierarchyCell.js');
        // 中文注释：
        // 加载 mxGraphAbstractHierarchyCell.js，定义层次单元基类
	mxClient.include(mxClient.basePath+'/js/layout/hierarchical/model/mxGraphHierarchyNode.js');
        // 中文注释：
        // 加载 mxGraphHierarchyNode.js，定义层次节点
	mxClient.include(mxClient.basePath+'/js/layout/hierarchical/model/mxGraphHierarchyEdge.js');
        // 中文注释：
        // 加载 mxGraphHierarchyEdge.js，定义层次边
	mxClient.include(mxClient.basePath+'/js/layout/hierarchical/model/mxGraphHierarchyModel.js');
        // 中文注释：
        // 加载 mxGraphHierarchyModel.js，定义层次模型
	mxClient.include(mxClient.basePath+'/js/layout/hierarchical/model/mxSwimlaneModel.js');
        // 中文注释：
        // 加载 mxSwimlaneModel.js，定义泳道模型
	mxClient.include(mxClient.basePath+'/js/layout/hierarchical/stage/mxHierarchicalLayoutStage.js');
        // 中文注释：
        // 加载 mxHierarchicalLayoutStage.js，定义层次布局阶段
	mxClient.include(mxClient.basePath+'/js/layout/hierarchical/stage/mxMedianHybridCrossingReduction.js');
        // 中文注释：
        // 加载 mxMedianHybridCrossingReduction.js，支持交叉减少
	mxClient.include(mxClient.basePath+'/js/layout/hierarchical/stage/mxMinimumCycleRemover.js');
        // 中文注释：
        // 加载 mxMinimumCycleRemover.js，支持最小循环移除
	mxClient.include(mxClient.basePath+'/js/layout/hierarchical/stage/mxCoordinateAssignment.js');
        // 中文注释：
        // 加载 mxCoordinateAssignment.js，支持坐标分配
	mxClient.include(mxClient.basePath+'/js/layout/hierarchical/stage/mxSwimlaneOrdering.js');
        // 中文注释：
        // 加载 mxSwimlaneOrdering.js，支持泳道排序
	mxClient.include(mxClient.basePath+'/js/layout/hierarchical/mxHierarchicalLayout.js');
        // 中文注释：
        // 加载 mxHierarchicalLayout.js，支持层次布局
	mxClient.include(mxClient.basePath+'/js/layout/hierarchical/mxSwimlaneLayout.js');
        // 中文注释：
        // 加载 mxSwimlaneLayout.js，支持泳道布局
	mxClient.include(mxClient.basePath+'/js/model/mxGraphModel.js');
        // 中文注释：
        // 加载 mxGraphModel.js，定义图形模型
	mxClient.include(mxClient.basePath+'/js/model/mxCell.js');
        // 中文注释：
        // 加载 mxCell.js，定义单元格对象
	mxClient.include(mxClient.basePath+'/js/model/mxGeometry.js');
        // 中文注释：
        // 加载 mxGeometry.js，定义几何信息
	mxClient.include(mxClient.basePath+'/js/model/mxCellPath.js');
        // 中文注释：
        // 加载 mxCellPath.js，管理单元格路径
	mxClient.include(mxClient.basePath+'/js/view/mxPerimeter.js');
        // 中文注释：
        // 加载 mxPerimeter.js，定义边界计算
	mxClient.include(mxClient.basePath+'/js/view/mxPrintPreview.js');
        // 中文注释：
        // 加载 mxPrintPreview.js，支持打印预览
	mxClient.include(mxClient.basePath+'/js/view/mxStylesheet.js');
        // 中文注释：
        // 加载 mxStylesheet.js，管理样式表
	mxClient.include(mxClient.basePath+'/js/view/mxCellState.js');
        // 中文注释：
        // 加载 mxCellState.js，定义单元格状态
	mxClient.include(mxClient.basePath+'/js/view/mxGraphSelectionModel.js');
        // 中文注释：
        // 加载 mxGraphSelectionModel.js，支持选择模型
	mxClient.include(mxClient.basePath+'/js/view/mxCellEditor.js');
        // 中文注释：
        // 加载 mxCellEditor.js，支持单元格编辑
	mxClient.include(mxClient.basePath+'/js/view/mxCellRenderer.js');
        // 中文注释：
        // 加载 mxCellRenderer.js，处理单元格渲染
	mxClient.include(mxClient.basePath+'/js/view/mxEdgeStyle.js');
        // 中文注释：
        // 加载 mxEdgeStyle.js，定义边样式
	mxClient.include(mxClient.basePath+'/js/view/mxStyleRegistry.js');
        // 中文注释：
        // 加载 mxStyleRegistry.js，管理样式注册
	mxClient.include(mxClient.basePath+'/js/view/mxGraphView.js');
        // 中文注释：
        // 加载 mxGraphView.js，定义图形视图
	mxClient.include(mxClient.basePath+'/js/view/mxGraph.js');
        // 中文注释：
        // 加载 mxGraph.js，核心图形类
	mxClient.include(mxClient.basePath+'/js/view/mxCellOverlay.js');
        // 中文注释：
        // 加载 mxCellOverlay.js，支持单元格覆盖
	mxClient.include(mxClient.basePath+'/js/view/mxOutline.js');
        // 中文注释：
        // 加载 mxOutline.js，支持图形大纲
	mxClient.include(mxClient.basePath+'/js/view/mxMultiplicity.js');
        // 中文注释：
        // 加载 mxMultiplicity.js，支持连接约束
	mxClient.include(mxClient.basePath+'/js/view/mxLayoutManager.js');
        // 中文注释：
        // 加载 mxLayoutManager.js，管理布局
	mxClient.include(mxClient.basePath+'/js/view/mxSwimlaneManager.js');
        // 中文注释：
        // 加载 mxSwimlaneManager.js，支持泳道管理
	mxClient.include(mxClient.basePath+'/js/view/mxTemporaryCellStates.js');
        // 中文注释：
        // 加载 mxTemporaryCellStates.js，支持临时单元格状态
	mxClient.include(mxClient.basePath+'/js/view/mxCellStatePreview.js');
        // 中文注释：
        // 加载 mxCellStatePreview.js，支持单元格状态预览
	mxClient.include(mxClient.basePath+'/js/view/mxConnectionConstraint.js');
        // 中文注释：
        // 加载 mxConnectionConstraint.js，支持连接约束
	mxClient.include(mxClient.basePath+'/js/handler/mxGraphHandler.js');
        // 中文注释：
        // 加载 mxGraphHandler.js，处理图形交互
	mxClient.include(mxClient.basePath+'/js/handler/mxPanningHandler.js');
        // 中文注释：
        // 加载 mxPanningHandler.js，支持平移处理
	mxClient.include(mxClient.basePath+'/js/handler/mxPopupMenuHandler.js');
        // 中文注释：
        // 加载 mxPopupMenuHandler.js，支持弹出菜单处理
	mxClient.include(mxClient.basePath+'/js/handler/mxCellMarker.js');
        // 中文注释：
        // 加载 mxCellMarker.js，支持单元格标记
	mxClient.include(mxClient.basePath+'/js/handler/mxSelectionCellsHandler.js');
        // 中文注释：
        // 加载 mxSelectionCellsHandler.js，支持选择单元格处理
	mxClient.include(mxClient.basePath+'/js/handler/mxConnectionHandler.js');
        // 中文注释：
        // 加载 mxConnectionHandler.js，支持连接处理
	mxClient.include(mxClient.basePath+'/js/handler/mxConstraintHandler.js');
        // 中文注释：
        // 加载 mxConstraintHandler.js，支持约束处理
	mxClient.include(mxClient.basePath+'/js/handler/mxRubberband.js');
        // 中文注释：
        // 加载 mxRubberband.js，支持橡皮筋选择
	mxClient.include(mxClient.basePath+'/js/handler/mxHandle.js');
        // 中文注释：
        // 加载 mxHandle.js，支持手柄操作
	mxClient.include(mxClient.basePath+'/js/handler/mxVertexHandler.js');
        // 中文注释：
        // 加载 mxVertexHandler.js，支持顶点处理
	mxClient.include(mxClient.basePath+'/js/handler/mxEdgeHandler.js');
        // 中文注释：
        // 加载 mxEdgeHandler.js，支持边处理
	mxClient.include(mxClient.basePath+'/js/handler/mxElbowEdgeHandler.js');
        // 中文注释：
        // 加载 mxElbowEdgeHandler.js，支持拐角边处理
	mxClient.include(mxClient.basePath+'/js/handler/mxEdgeSegmentHandler.js');
        // 中文注释：
        // 加载 mxEdgeSegmentHandler.js，支持边段处理
	mxClient.include(mxClient.basePath+'/js/handler/mxKeyHandler.js');
        // 中文注释：
        // 加载 mxKeyHandler.js，支持键盘事件处理
	mxClient.include(mxClient.basePath+'/js/handler/mxTooltipHandler.js');
        // 中文注释：
        // 加载 mxTooltipHandler.js，支持工具提示处理
	mxClient.include(mxClient.basePath+'/js/handler/mxCellTracker.js');
        // 中文注释：
        // 加载 mxCellTracker.js，支持单元格跟踪
	mxClient.include(mxClient.basePath+'/js/handler/mxCellHighlight.js');
        // 中文注释：
        // 加载 mxCellHighlight.js，支持单元格高亮
	mxClient.include(mxClient.basePath+'/js/editor/mxDefaultKeyHandler.js');
         // 中文注释：
    // 加载 mxDefaultKeyHandler.js，提供默认键盘事件处理
    // 用途：支持键盘快捷键（如回车、ESC）控制图形编辑
	mxClient.include(mxClient.basePath+'/js/editor/mxDefaultPopupMenu.js');
    // 中文注释：
    // 加载 mxDefaultPopupMenu.js，提供默认弹出菜单功能
    // 用途：支持右键菜单交互
	mxClient.include(mxClient.basePath+'/js/editor/mxDefaultToolbar.js');
    // 中文注释：
    // 加载 mxDefaultToolbar.js，提供默认工具栏功能
    // 用途：支持图形编辑工具栏
	mxClient.include(mxClient.basePath+'/js/editor/mxEditor.js');
    // 中文注释：
    // 加载 mxEditor.js，提供图形编辑器核心功能
    // 用途：支持高级编辑功能（如撤销、工具栏、菜单）
	mxClient.include(mxClient.basePath+'/js/io/mxCodecRegistry.js');
    // 中文注释：
    // 加载 mxCodecRegistry.js，管理序列化和反序列化注册
    // 用途：支持图形数据的编码和解码
	mxClient.include(mxClient.basePath+'/js/io/mxCodec.js');
    // 中文注释：
    // 加载 mxCodec.js，提供序列化和反序列化基类
    // 用途：支持对象到 XML 的转换
	mxClient.include(mxClient.basePath+'/js/io/mxObjectCodec.js');
    // 中文注释：
    // 加载 mxObjectCodec.js，支持通用对象序列化
    // 用途：处理非单元格对象的编码
	mxClient.include(mxClient.basePath+'/js/io/mxCellCodec.js');
    // 中文注释：
    // 加载 mxCellCodec.js，支持单元格对象序列化
    // 用途：处理 mxCell 对象的编码
	mxClient.include(mxClient.basePath+'/js/io/mxModelCodec.js');
    // 中文注释：
    // 加载 mxModelCodec.js，支持图形模型序列化
    // 用途：处理 mxGraphModel 的编码
	mxClient.include(mxClient.basePath+'/js/io/mxRootChangeCodec.js');
    // 中文注释：
    // 加载 mxRootChangeCodec.js，支持根节点变更序列化
    // 用途：处理根节点变更的编码
	mxClient.include(mxClient.basePath+'/js/io/mxChildChangeCodec.js');
    // 中文注释：
    // 加载 mxChildChangeCodec.js，支持子节点变更序列化
    // 用途：处理子节点变更的编码
	mxClient.include(mxClient.basePath+'/js/io/mxTerminalChangeCodec.js');
    // 中文注释：
    // 加载 mxTerminalChangeCodec.js，支持端点变更序列化
    // 用途：处理连接端点变更的编码
	mxClient.include(mxClient.basePath+'/js/io/mxGenericChangeCodec.js');
    // 中文注释：
    // 加载 mxGenericChangeCodec.js，支持通用变更序列化
    // 用途：处理通用模型变更的编码
	mxClient.include(mxClient.basePath+'/js/io/mxGraphCodec.js');
    // 中文注释：
    // 加载 mxGraphCodec.js，支持图形对象序列化
    // 用途：处理 mxGraph 对象的编码
	mxClient.include(mxClient.basePath+'/js/io/mxGraphViewCodec.js');
    // 中文注释：
    // 加载 mxGraphViewCodec.js，支持图形视图序列化
    // 用途：处理 mxGraphView 的编码
	mxClient.include(mxClient.basePath+'/js/io/mxStylesheetCodec.js');
    // 中文注释：
    // 加载 mxStylesheetCodec.js，支持样式表序列化
    // 用途：处理 mxStylesheet 的编码
	mxClient.include(mxClient.basePath+'/js/io/mxDefaultKeyHandlerCodec.js');
    // 中文注释：
    // 加载 mxDefaultKeyHandlerCodec.js，支持默认键盘处理序列化
    // 用途：处理 mxDefaultKeyHandler 的编码
	mxClient.include(mxClient.basePath+'/js/io/mxDefaultToolbarCodec.js');
    // 中文注释：
    // 加载 mxDefaultToolbarCodec.js，支持默认工具栏序列化
    // 用途：处理 mxDefaultToolbar 的编码
	mxClient.include(mxClient.basePath+'/js/io/mxDefaultPopupMenuCodec.js');
    // 中文注释：
    // 加载 mxDefaultPopupMenuCodec.js，支持默认弹出菜单序列化
    // 用途：处理 mxDefaultPopupMenu 的编码
	mxClient.include(mxClient.basePath+'/js/io/mxEditorCodec.js');
// 中文注释：
    // 加载 mxEditorCodec.js，支持编辑器序列化
    // 用途：处理 mxEditor 的编码
// PREPROCESSOR-REMOVE-START
}
// PREPROCESSOR-REMOVE-END
