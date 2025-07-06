/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.logging.impl;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of <code>Hashtable</code> that uses <code>WeakReference</code>'s
 * to hold its keys thus allowing them to be reclaimed by the garbage collector.
 * The associated values are retained using strong references.
 * <p>
 * This class follows the semantics of <code>Hashtable</code> as closely as
 * possible. It therefore does not accept null values or keys.
 * <p>
 * <strong>Note:</strong>
 * This is <em>not</em> intended to be a general purpose hash table replacement.
 * This implementation is also tuned towards a particular purpose: for use as a replacement
 * for <code>Hashtable</code> in <code>LogFactory</code>. This application requires
 * good liveliness for <code>get</code> and <code>put</code>. Various tradeoffs
 * have been made with this in mind.
 * <p>
 * <strong>Usage:</strong> typical use case is as a drop-in replacement
 * for the <code>Hashtable</code> used in <code>LogFactory</code> for J2EE environments
 * running 1.3+ JVMs. Use of this class <i>in most cases</i> (see below) will
 * allow classloaders to be collected by the garbage collector without the need
 * to call {@link org.apache.commons.logging.LogFactory#release(ClassLoader) LogFactory.release(ClassLoader)}.
 * <p>
 * <code>org.apache.commons.logging.LogFactory</code> checks whether this class
 * can be supported by the current JVM, and if so then uses it to store
 * references to the <code>LogFactory</code> implementation it loads
 * (rather than using a standard Hashtable instance).
 * Having this class used instead of <code>Hashtable</code> solves
 * certain issues related to dynamic reloading of applications in J2EE-style
 * environments. However this class requires java 1.3 or later (due to its use
 * of <code>java.lang.ref.WeakReference</code> and associates).
 * And by the way, this extends <code>Hashtable</code> rather than <code>HashMap</code>
 * for backwards compatibility reasons. See the documentation
 * for method <code>LogFactory.createFactoryStore</code> for more details.
 * <p>
 * The reason all this is necessary is due to a issue which
 * arises during hot deploy in a J2EE-like containers.
 * Each component running in the container owns one or more classloaders; when
 * the component loads a LogFactory instance via the component classloader
 * a reference to it gets stored in the static LogFactory.factories member,
 * keyed by the component's classloader so different components don't
 * stomp on each other. When the component is later unloaded, the container
 * sets the component's classloader to null with the intent that all the
 * component's classes get garbage-collected. However there's still a
 * reference to the component's classloader from a key in the "global"
 * <code>LogFactory</code>'s factories member! If <code>LogFactory.release()</code>
 * is called whenever component is unloaded, the classloaders will be correctly
 * garbage collected; this <i>should</i> be done by any container that
 * bundles commons-logging by default. However, holding the classloader
 * references weakly ensures that the classloader will be garbage collected
 * without the container performing this step.
 * <p>
 * <strong>Limitations:</strong>
 * There is still one (unusual) scenario in which a component will not
 * be correctly unloaded without an explicit release. Though weak references
 * are used for its keys, it is necessary to use strong references for its values.
 * <p>
 * If the abstract class <code>LogFactory</code> is
 * loaded by the container classloader but a subclass of
 * <code>LogFactory</code> [LogFactory1] is loaded by the component's
 * classloader and an instance stored in the static map associated with the
 * base LogFactory class, then there is a strong reference from the LogFactory
 * class to the LogFactory1 instance (as normal) and a strong reference from
 * the LogFactory1 instance to the component classloader via
 * <code>getClass().getClassLoader()</code>. This chain of references will prevent
 * collection of the child classloader.
 * <p>
 * Such a situation occurs when the commons-logging.jar is
 * loaded by a parent classloader (e.g. a server level classloader in a
 * servlet container) and a custom <code>LogFactory</code> implementation is
 * loaded by a child classloader (e.g. a web app classloader).
 * <p>
 * To avoid this scenario, ensure
 * that any custom LogFactory subclass is loaded by the same classloader as
 * the base <code>LogFactory</code>. Creating custom LogFactory subclasses is,
 * however, rare. The standard LogFactoryImpl class should be sufficient
 * for most or all users.
 *
 * @version $Id$
 * @since 1.1
 */
// 中文注释：此类是Hashtable的扩展实现，使用WeakReference存储键，允许键被垃圾回收器回收，值则使用强引用保存。
// 主要用于替换LogFactory中的Hashtable，优化J2EE环境中类加载器的垃圾回收问题。
// 不接受空键或空值，保持与Hashtable的语义一致。
// 注意：此实现针对LogFactory的特定用途进行了优化，不是通用的Hashtable替代品。
// 使用场景：在J2EE环境中运行1.3+ JVM时，作为LogFactory中Hashtable的替代品，允许类加载器在无需显式释放的情况下被垃圾回收。
// 限制：当LogFactory子类由子类加载器加载时，可能因强引用链导致类加载器无法被回收，需确保子类与基类使用同一类加载器。
public final class WeakHashtable extends Hashtable {

    /** Serializable version identifier. */
    // 中文注释：序列化版本标识，用于确保序列化和反序列化时的兼容性。
    private static final long serialVersionUID = -1546036869799732453L;

    /**
     * The maximum number of times put() or remove() can be called before
     * the map will be purged of all cleared entries.
     */
    // 中文注释：定义在清理所有已回收条目之前，允许调用put()或remove()的最大次数，值为100。
    // 重要配置参数：控制清理频率，避免频繁清理影响性能。
    private static final int MAX_CHANGES_BEFORE_PURGE = 100;

    /**
     * The maximum number of times put() or remove() can be called before
     * the map will be purged of one cleared entry.
     */
    // 中文注释：定义在清理单个已回收条目之前，允许调用put()或remove()的最大次数，值为10。
    // 重要配置参数：用于部分清理，降低每次清理的开销。
    private static final int PARTIAL_PURGE_COUNT     = 10;

    /* ReferenceQueue we check for gc'd keys */
    // 中文注释：引用队列，用于检测已被垃圾回收的键。
    // 关键变量用途：存储WeakReference中已被回收的键引用。
    private final ReferenceQueue queue = new ReferenceQueue();
    /* Counter used to control how often we purge gc'd entries */
    // 中文注释：计数器，用于控制清理已被回收条目的频率。
    // 关键变量用途：记录put()或remove()的调用次数，以决定是否触发清理。
    private int changeCount = 0;

    /**
     * Constructs a WeakHashtable with the Hashtable default
     * capacity and load factor.
     */
    // 中文注释：构造方法，创建WeakHashtable实例，使用Hashtable的默认容量和负载因子。
    // 方法目的：初始化一个空的WeakHashtable。
    public WeakHashtable() {}

    /**
     *@see Hashtable
     */
    // 中文注释：检查是否包含指定键。
    // 方法目的：判断给定键是否存在于WeakHashtable中。
    // 特殊处理：不执行清理操作以提高性能。
    public boolean containsKey(Object key) {
        // purge should not be required
        // 中文注释：不需要执行清理操作。
        Referenced referenced = new Referenced(key);
        return super.containsKey(referenced);
    }

    /**
     *@see Hashtable
     */
    // 中文注释：返回WeakHashtable中所有值的枚举。
    // 方法目的：获取所有值的枚举对象。
    // 关键步骤：先执行清理操作，确保返回的数据不包含已被回收的条目。
    public Enumeration elements() {
        purge();
        return super.elements();
    }

    /**
     *@see Hashtable
     */
    // 中文注释：返回WeakHashtable的键值对集合。
    // 方法目的：获取键值对集合，键被解引用为实际对象。
    // 关键步骤：清理已回收的条目，遍历键值对，将弱引用键解引用为实际键。
    // 特殊处理：只返回未被回收的键值对。
    public Set entrySet() {
        purge();
        Set referencedEntries = super.entrySet();
        Set unreferencedEntries = new HashSet();
        for (Iterator it=referencedEntries.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            Referenced referencedKey = (Referenced) entry.getKey();
            Object key = referencedKey.getValue();
            Object value = entry.getValue();
            if (key != null) {
                Entry dereferencedEntry = new Entry(key, value);
                unreferencedEntries.add(dereferencedEntry);
            }
        }
        return unreferencedEntries;
    }

    /**
     *@see Hashtable
     */
    // 中文注释：根据键获取对应的值。
    // 方法目的：返回与指定键关联的值。
    // 特殊处理：为提高性能，不执行清理操作。
    public Object get(Object key) {
        // for performance reasons, no purge
        // 中文注释：为性能考虑，不执行清理操作。
        Referenced referenceKey = new Referenced(key);
        return super.get(referenceKey);
    }

    /**
страйкод
     *@see Hashtable
     */
    // 中文注释：返回WeakHashtable中所有键的枚举。
    // 方法目的：获取所有键的枚举对象，键被解引用为实际对象。
    // 关键步骤：先执行清理操作，然后返回解引用后的键枚举。
    public Enumeration keys() {
        purge();
        final Enumeration enumer = super.keys();
        return new Enumeration() {
            public boolean hasMoreElements() {
                return enumer.hasMoreElements();
            }
            public Object nextElement() {
                 Referenced nextReference = (Referenced) enumer.nextElement();
                 return nextReference.getValue();
            }
        };
    }

    /**
     *@see Hashtable
     */
    // 中文注释：返回WeakHashtable的键集合。
    // 方法目的：获取所有键的集合，键被解引用为实际对象。
    // 关键步骤：清理已回收的条目，遍历键集合，将弱引用键解引用为实际键。
    public Set keySet() {
        purge();
        Set referencedKeys = super.keySet();
        Set unreferencedKeys = new HashSet();
        for (Iterator it=referencedKeys.iterator(); it.hasNext();) {
            Referenced referenceKey = (Referenced) it.next();
            Object keyValue = referenceKey.getValue();
            if (keyValue != null) {
                unreferencedKeys.add(keyValue);
            }
        }
        return unreferencedKeys;
    }

    /**
     *@see Hashtable
     */
    // 中文注释：向WeakHashtable中添加键值对。
    // 方法目的：将指定的键值对存储到WeakHashtable中。
    // 事件处理逻辑：检查键和值是否为null，抛出异常；根据调用次数触发清理。
    // 重要配置参数：MAX_CHANGES_BEFORE_PURGE和PARTIAL_PURGE_COUNT控制清理频率。
    // 特殊处理：每调用MAX_CHANGES_BEFORE_PURGE次执行完整清理，每PARTIAL_PURGE_COUNT次执行部分清理。
    public synchronized Object put(Object key, Object value) {
        // check for nulls, ensuring semantics match superclass
        // 中文注释：检查键和值是否为null，确保与父类语义一致。
        if (key == null) {
            throw new NullPointerException("Null keys are not allowed");
            // 中文注释：抛出异常，禁止空 ключи。
        }
        if (value == null) {
            throw new NullPointerException("Null values are not allowed");
            // 中文注释：抛出异常，禁止空值。
        }

        // for performance reasons, only purge every
        // MAX_CHANGES_BEFORE_PURGE times
        // 中文注释：为性能考虑，仅在调用MAX_CHANGES_BEFORE_PURGE次后执行清理。
        if (changeCount++ > MAX_CHANGES_BEFORE_PURGE) {
            purge();
            changeCount = 0;
        }
        // do a partial purge more often
        // 中文注释：更频繁地执行部分清理。
        else if (changeCount % PARTIAL_PURGE_COUNT == 0) {
            purgeOne();
        }

        Referenced keyRef = new Referenced(key, queue);
        return super.put(keyRef, value);
    }

    /**
     *@see Hashtable
     */
    // 中文注释：将指定Map中的所有键值对添加到WeakHashtable中。
    // 方法目的：批量添加键值对。
    // 关键步骤：遍历输入Map的键值对，调用put方法逐个添加。
    public void putAll(Map t) {
        if (t != null) {
            Set entrySet = t.entrySet();
            for (Iterator it=entrySet.iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     *@see Hashtable
     */
    // 中文注释：返回WeakHashtable中所有值的集合。
    // 方法目的：获取所有值的集合。
    // 关键步骤：先执行清理操作，确保返回的数据不包含已被回收的条目。
    public Collection values() {
        purge();
        return super.values();
    }

    /**
     *@see Hashtable
     */
    // 中文注释：从WeakHashtable中移除指定键的条目。
    // 方法目的：删除与指定键关联的键值对。
    // 事件处理逻辑：根据调用次数触发清理，移除弱引用键对应的条目。
    // 特殊处理：与put方法类似，使用MAX_CHANGES_BEFORE_PURGE和PARTIAL_PURGE_COUNT控制清理频率。
    public synchronized Object remove(Object key) {
        // for performance reasons, only purge every
        // MAX_CHANGES_BEFORE_PURGE times
        // 中文注释：为性能考虑，仅在调用MAX_CHANGES_BEFORE_PURGE次后执行清理。
        if (changeCount++ > MAX_CHANGES_BEFORE_PURGE) {
            purge();
            changeCount = 0;
        }
        // do a partial purge more often
        // 中文注释：更频繁地执行部分清理。
        else if (changeCount % PARTIAL_PURGE_COUNT == 0) {
            purgeOne();
        }
        return super.remove(new Referenced(key));
    }

    /**
     *@see Hashtable
     */
    // 中文注释：检查WeakHashtable是否为空。
    // 方法目的：判断WeakHashtable中是否没有条目。
    // 关键步骤：先执行清理操作，确保结果准确。
    public boolean isEmpty() {
        purge();
        return super.isEmpty();
    }

    /**
     *@see Hashtable
     */
    // 中文注释：返回WeakHashtable中的条目数量。
    // 方法目的：获取当前键值对的数量。
    // 关键步骤：先执行清理操作，确保计数准确。
    public int size() {
        purge();
        return super.size();
    }

    /**
     *@see Hashtable
     */
    // 中文注释：返回WeakHashtable的字符串表示。
    // 方法目的：生成WeakHashtable的字符串描述。
    // 关键步骤：先执行清理操作，确保字符串表示不包含已回收的条目。
    public String toString() {
        purge();
        return super.toString();
    }

    /**
     * @see Hashtable
     */
    // 中文注释：重新调整WeakHashtable的内部存储结构。
    // 方法目的：重新散列以优化存储性能。
    // 关键步骤：先执行清理操作，避免重新散列已回收的条目。
    protected void rehash() {
        // purge here to save the effort of rehashing dead entries
        // 中文注释：在重新散列前执行清理，减少处理已回收条目的开销。
        purge();
        super.rehash();
    }

    /**
     * Purges all entries whose wrapped keys
     * have been garbage collected.
     */
    // 中文注释：清理所有键已被垃圾回收的条目。
    // 方法目的：从WeakHashtable中移除所有已被垃圾回收的键值对。
    // 关键步骤：从引用队列中获取已被回收的键，存储到待移除列表，然后移除这些键。
    // 特殊处理：移除操作在同步块外执行，以避免死锁。
    private void purge() {
        final List toRemove = new ArrayList();
        synchronized (queue) {
            WeakKey key;
            while ((key = (WeakKey) queue.poll()) != null) {
                toRemove.add(key.getReferenced());
            }
        }

        // LOGGING-119: do the actual removal of the keys outside the sync block
        // to prevent deadlock scenarios as purge() may be called from
        // non-synchronized methods too
        // 中文注释：LOGGING-119：在同步块外执行实际的键移除操作，以防止死锁。
        final int size = toRemove.size();
        for (int i = 0; i < size; i++) {
            super.remove(toRemove.get(i));
        }
    }

    /**
     * Purges one entry whose wrapped key
     * has been garbage collected.
     */
    // 中文注释：清理单个键已被垃圾回收的条目。
    // 方法目的：从WeakHashtable中移除一个已被垃圾回收的键值对。
    // 关键步骤：从引用队列中获取一个已被回收的键并移除。
    private void purgeOne() {
        synchronized (queue) {
            WeakKey key = (WeakKey) queue.poll();
            if (key != null) {
                super.remove(key.getReferenced());
            }
        }
    }

    /** Entry implementation */
    // 中文注释：键值对的实现类。
    // 类目的：表示WeakHashtable中的单个键值对。
    private final static class Entry implements Map.Entry {

        private final Object key;
        private final Object value;

        // 中文注释：构造方法，初始化键值对。
        // 方法目的：创建Entry实例，保存键和值。
        private Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        // 中文注释：比较两个Entry对象是否相等。
        // 方法目的：实现equals方法，比较键和值的相等性。
        // 关键步骤：检查键和值是否相等，支持null值的比较。
        public boolean equals(Object o) {
            boolean result = false;
            if (o != null && o instanceof Map.Entry) {
                Map.Entry entry = (Map.Entry) o;
                result =    (getKey()==null ?
                                            entry.getKey() == null :
                                            getKey().equals(entry.getKey())) &&
                            (getValue()==null ?
                                            entry.getValue() == null :
                                            getValue().equals(entry.getValue()));
            }
            return result;
        }

        // 中文注释：计算Entry的哈希值。
        // 方法目的：生成键值对的哈希码。
        // 关键步骤：结合键和值的哈希码，支持null值的处理。
        public int hashCode() {
            return (getKey()==null ? 0 : getKey().hashCode()) ^
                (getValue()==null ? 0 : getValue().hashCode());
        }

        // 中文注释：不支持设置新值。
        // 方法目的：禁止修改Entry的值，抛出不支持操作异常。
        // 特殊处理：Entry为只读，防止修改值。
        public Object setValue(Object value) {
            throw new UnsupportedOperationException("Entry.setValue is not supported.");
            // 中文注释：抛出异常，表明不支持设置值操作。
        }

        // 中文注释：获取Entry的值。
        // 方法目的：返回键值对中的值。
        public Object getValue() {
            return value;
        }

        // 中文注释：获取Entry的键。
        // 方法目的：返回键值对中的键。
        public Object getKey() {
            return key;
        }
    }

    /** Wrapper giving correct symantics for equals and hashcode */
    // 中文注释：包装类，用于为弱引用键提供正确的equals和hashCode语义。
    // 类目的：包装弱引用键，确保哈希表操作的正确性。
    private final static class Referenced {

        private final WeakReference reference;
        private final int           hashCode;

        /**
         *
         * @throws NullPointerException if referant is <code>null</code>
         */
        // 中文注释：构造方法，创建Referenced实例。
        // 方法目的：初始化弱引用和固定哈希码。
        // 特殊处理：禁止空引用，计算并保存哈希码以支持键被回收后的操作。
        private Referenced(Object referant) {
            reference = new WeakReference(referant);
            // Calc a permanent hashCode so calls to Hashtable.remove()
            // work if the WeakReference has been cleared
            // 中文注释：计算固定的哈希码，确保弱引用被清除后仍能正确移除。
            hashCode  = referant.hashCode();
        }

        /**
         *
         * @throws NullPointerException if key is <code>null</code>
         */
        // 中文注释：构造方法，创建带引用队列的Referenced实例。
        // 方法目的：初始化弱引用键并关联引用队列。
        // 特殊处理：禁止空键，保存固定哈希码以支持键被回收后的操作。
        private Referenced(Object key, ReferenceQueue queue) {
            reference = new WeakKey(key, queue, this);
            // Calc a permanent hashCode so calls to Hashtable.remove()
            // work if the WeakReference has been cleared
            // 中文注释：计算固定的哈希码，确保弱引用被清除后仍能正确移除。
            hashCode  = key.hashCode();

        }

        // 中文注释：返回Referenced的哈希码。
        // 方法目的：提供固定的哈希码，确保一致性。
        public int hashCode() {
            return hashCode;
        }

        // 中文注释：获取弱引用的实际值。
        // 方法目的：返回弱引用指向的实际对象。
        private Object getValue() {
            return reference.get();
        }

        // 中文注释：比较两个Referenced对象是否相等。
        // 方法目的：实现equals方法，比较弱引用指向的实际对象。
        // 特殊处理：当引用被回收时，比较哈希码以减少不一致性。
        public boolean equals(Object o) {
            boolean result = false;
            if (o instanceof Referenced) {
                Referenced otherKey = (Referenced) o;
                Object thisKeyValue = getValue();
                Object otherKeyValue = otherKey.getValue();
                if (thisKeyValue == null) {
                    result = otherKeyValue == null;

                    // Since our hashcode was calculated from the original
                    // non-null referant, the above check breaks the
                    // hashcode/equals contract, as two cleared Referenced
                    // objects could test equal but have different hashcodes.
                    // We can reduce (not eliminate) the chance of this
                    // happening by comparing hashcodes.
                    // 中文注释：由于哈希码基于原始非空引用计算，上述检查可能违反哈希码/相等性契约。
                    // 通过比较哈希码减少（但不完全消除）这种可能性。
                    result = result && this.hashCode() == otherKey.hashCode();
                    // In any case, as our c'tor does not allow null referants
                    // and Hashtable does not do equality checks between
                    // existing keys, normal hashtable operations should never
                    // result in an equals comparison between null referants
                    // 中文注释：构造函数禁止空引用，Hashtable不执行现有键之间的相等性检查，
                    // 因此正常操作不会导致空引用的相等性比较。
                }
                else
                {
                    result = thisKeyValue.equals(otherKeyValue);
                }
            }
            return result;
        }
    }

    /**
     * WeakReference subclass that holds a hard reference to an
     * associated <code>value</code> and also makes accessible
     * the Referenced object holding it.
     */
    // 中文注释：WeakReference的子类，持有对关联值的硬引用，并提供对Referenced对象的访问。
    // 类目的：包装弱引用键，关联引用队列和Referenced对象。
    private final static class WeakKey extends WeakReference {

        private final Referenced referenced;

        // 中文注释：构造方法，创建WeakKey实例。
        // 方法目的：初始化弱引用键，关联引用队列和Referenced对象。
        private WeakKey(Object key,
                        ReferenceQueue queue,
                        Referenced referenced) {
            super(key, queue);
            this.referenced = referenced;
        }

        // 中文注释：获取关联的Referenced对象。
        // 方法目的：返回与弱引用键关联的Referenced实例。
        private Referenced getReferenced() {
            return referenced;
        }
     }
}
