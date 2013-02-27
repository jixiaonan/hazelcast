/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.concurrent.atomiclong;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Operation;

import java.io.IOException;

// author: sancar - 24.12.2012
public class CompareAndSetOperation extends AtomicLongBackupAwareOperation {

    private long expect;
    private long update;
    private boolean returnValue = false;

    public CompareAndSetOperation() {
        super();
    }

    public CompareAndSetOperation(String name, long expect, long update) {
        super(name);
        this.expect = expect;
        this.update = update;
    }

    @Override
    public void run() throws Exception {
        returnValue = getNumber().compareAndSet(expect, update);
        shouldBackup = !returnValue;
    }

    @Override
    public Object getResponse() {
        return returnValue;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(expect);
        out.writeLong(update);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        expect = in.readLong();
        update = in.readLong();
    }

    public Operation getBackupOperation() {
        return new SetBackupOperation(name, update);
    }
}
