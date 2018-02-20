/**
 * This content is released under the MIT License (MIT)
 *
 * Copyright (c) 2018, canchito-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 * @author 		José Carlos Mendoza Prego
 * @copyright	Copyright (c) 2018, canchito-dev (http://www.canchito-dev.com)
 * @license		http://opensource.org/licenses/MIT	MIT License
 * @link		https://github.com/canchito-dev/thread-pool-with-task-queue-in-database
 **/
package com.canchitodev.example.utils.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum BehaviorTaskStatus {
	undefined(0),
	NEW(1),
    WAITING(2),
    EXECUTING(3),
    DONE(4),
    ERROR(5);

    private static final Map<Integer,BehaviorTaskStatus> lookup 
         = new HashMap<Integer, BehaviorTaskStatus>();

    static {
         for(BehaviorTaskStatus s : EnumSet.allOf(BehaviorTaskStatus.class))
              lookup.put(s.getStatus(), s);
    }

    private int status;

    private BehaviorTaskStatus(int status) {
         this.status = status;
    }

    public int getStatus() { return status; }

    public static BehaviorTaskStatus get(int status) { 
         return lookup.getOrDefault(status, BehaviorTaskStatus.undefined); 
    }
}
