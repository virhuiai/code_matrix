/*
 * Copyright (c) 2001-2013 JGoodies Software GmbH. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JGoodies Software GmbH nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jgoodies.looks;

import javax.swing.UIDefaults;


/**
 * 查找并返回微布局的接口。
 * 定义了如何获取组件的内边距、外边距和间距设置。
 *
 * @author  Karsten Lentzsch
 * @version $Revision: 1.6 $
 *
 * @see MicroLayout
 * @see MicroLayoutPolicies
 *
 * @since 2.1
 */
public interface MicroLayoutPolicy {

    /**
     * 查找并返回内边距、外边距和间距，这些将被外观和感觉用来设置组件的内边距、外边距和间距。<p>
     *
     * 此方法在外观组件初始化期间被调用。
     * 调用者会传递用于定义组件设置的UIDefaults对象。
     * 因此，UIDefaults对象可以用来查找由父外观初始化的值。
     * 例如，JGoodies Windows外观可以使用由父外观（Sun Windows外观）设置的默认值。
     *
     * @param lafName 请求字体的外观和感觉名称
     * @param table 可以用来查找父外观字体的UIDefaults表
     *
     * @return 用作组件默认设置的微布局
     */
    MicroLayout getMicroLayout(String lafName, UIDefaults table);

}