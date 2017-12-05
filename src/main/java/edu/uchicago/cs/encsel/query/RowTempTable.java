/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Contributors:
 *     Hao Jiang - initial API and implementation
 */

package edu.uchicago.cs.encsel.query;

import org.apache.parquet.io.api.Converter;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.Type;

import java.util.*;
import java.util.stream.Collectors;

public class RowTempTable extends GroupConverter implements TempTable {

    protected Row current;
    private Converter[] converters;

    private Map<ColumnKey, Integer> index = new HashMap<>();
    private List<Row> records = new ArrayList<>();

    public RowTempTable(MessageType schema) {
        converters = new Converter[schema.getFieldCount()];

        for (int i = 0; i < converters.length; i++) {
            final Type type = schema.getType(i);
            if (type.isPrimitive()) {
                converters[i] = new RowFieldPrimitiveConverter(this, i, schema.getType(i).asPrimitiveType());
                index.put(new ColumnKey(schema.getColumns().get(i).getPath()), i);
            }
        }
    }

    @Override
    public void start() {
        current = new Row(converters.length);
        records.add(current);
    }

    @Override
    public Converter getConverter(int fieldIndex) {
        return converters[fieldIndex];
    }

    public Converter getConverter(String[] path) {
        return converters[index.get(new ColumnKey(path))];
    }

    @Override
    public void end() {
    }

    public Row getCurrentRecord() {
        return current;
    }

    public void setCurrentRecord(int index) {
        current = records.get(index);
    }

    public List<Row> getRecords() {
        return records;
    }
}
