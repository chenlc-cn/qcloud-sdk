/*
 * Copyright 2017-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.chenlc.qcloud.sdk.vod.vo;

import com.alibaba.fastjson.JSON;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 分类信息树描述对象
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/17
 */
public class VodClassTreeMap {

    private Map<String, TreeNode> nodes;

    public VodClassTreeMap() {
        this.nodes = new LinkedHashMap<>();
    }

    public void addNode(String name, TreeNode node) {
        this.nodes.put(name, node);
    }

    public TreeNode getNode(String name) {
        return nodes.get(name);
    }

    public boolean hasNode(String name) {
        return nodes.containsKey(name);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(nodes.values());
    }

    public static class TreeNode {
        private VodClassInfo nodeInfo;
        private Map<String, TreeNode> subClasses;

        public TreeNode(VodClassInfo nodeInfo) {
            this.nodeInfo = nodeInfo;
            this.subClasses = new LinkedHashMap<>();
        }

        public VodClassInfo getNodeInfo() {
            return nodeInfo;
        }

        public void addNode(String name, TreeNode node) {
            this.subClasses.put(name, node);
        }

        public TreeNode getNode(String name) {
            return subClasses.get(name);
        }

        public boolean hasNode(String name) {
            return subClasses.containsKey(name);
        }

        @Override
        public String toString() {
            return "{\"info\": " + JSON.toJSONString(nodeInfo) + ", \"subClasses\": " + JSON.toJSONString(subClasses.values()) + "}";
        }
    }

}
