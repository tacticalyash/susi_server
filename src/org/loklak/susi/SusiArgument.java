/**
 *  SusiThoughts
 *  Copyright 30.06.2016 by Michael Peter Christen, @0rb1t3r
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *  
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program in the file lgpl21.txt
 *  If not, see <http://www.gnu.org/licenses/>.
 */

package org.loklak.susi;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An Argument is a series of thoughts, also known as a 'proof' in automated reasoning
 */
public class SusiArgument {

    private final ArrayList<SusiThought> recall;
    
    /**
     * create an empty argument
     */
    public SusiArgument() {
        this.recall = new ArrayList<>();
    }
    
    /**
     * get an impression of time which elapsed since the start of reasoning in this argument
     * @return the number of thoughts
     */
    public int times() {
        return this.recall.size();
    }
    
    /**
     * The mindstate is the current state of an argument
     * @return the latest thought in a series of proof steps
     */
    public SusiThought mindstate() {
        return remember(0);
    }
    
    /**
     * Remembering the thoughts is essential to recall which thoughts leads to the current mindstate
     * @param timesBack the number of thoughts backwards from the current mindstate
     * @return the thought in the past according to the elapsed time of the thoughts
     */
    public SusiThought remember(int timesBack) {
        int state = this.recall.size() - timesBack - 1;
        if (state < 0) return new SusiThought(); // empty mind!
        return this.recall.get(state);
    }
 
    /**
     * Thinking is a series of thoughts, every new thought appends another thought to the argument
     * @param thought the next thought
     * @return self, the current mindstate
     */
    public SusiArgument think(SusiThought thought) {
        this.recall.add(thought);
        return this;
    }
    
    /**
     * Unification applies a piece of memory wihtin the current argument to a statement
     * which creates an instantiated statement
     * @param statement
     * @return the instantiated statement with elements of the argument applied
     */
    public String unify(String statement) {
        JSONArray table = this.mindstate().getData();
        if (table != null && table.length() > 0) {
            JSONObject row = table.getJSONObject(0);
            for (String key: row.keySet()) {
                int i = statement.indexOf("$" + key + "$");
                if (i >= 0) {
                    statement = statement.substring(0, i) + row.get(key).toString() + statement.substring(i + key.length() + 2);
                }
            }
        }
        return statement;
    }

    /**
     * To perform a proof we must apply inference steps.
     * @param inference the inference to apply on this argument
     * @return self, the current mindstate
     */
    public SusiArgument deducewith(SusiInference inference) {
        return this.think(inference.applyon(this));
    }
}
