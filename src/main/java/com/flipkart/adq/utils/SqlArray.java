package com.flipkart.adq.utils;


import com.google.common.collect.Iterables;

import java.util.Arrays;
import java.util.Collection;

public class SqlArray<T>
    {
        private final Object[] elements;
        private final Class<T> type;

        public SqlArray(Class<T> type, Collection<T> elements) {
            this.elements = Iterables.toArray(elements, Object.class);
            this.type = type;
        }

        public static <T> SqlArray<T> arrayOf(Class<T> type, T... elements) {
            return new SqlArray<T>(type, Arrays.asList(elements));
        }


        public Object[] getElements()
        {
            return elements;
        }

        public Class<T> getType()
        {
            return type;
        }
    }

