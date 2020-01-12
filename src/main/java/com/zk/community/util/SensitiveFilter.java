package com.zk.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用前缀树实现敏感词过滤
 */
@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT = "***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init() {
        try (
                InputStream in = this.getClass().getClassLoader().getResourceAsStream("sensitive-word.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in))
        ) {
            String keyword;
            while ((keyword = bufferedReader.readLine()) != null) {
                //添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败：" + e.getMessage());
        }

    }

    /**
     * 将敏感词添加到前缀树中
     * @param keyword
     */
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null) {
                //初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            //指向子节点，开始下一次循环
            tempNode = subNode;

            //设置结束标识
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text 待过滤的文本
     * @return  过滤后的文本
     */
    public String filterSensitiveWord(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        //过滤算法使用的指针
        //指针1
        int begin = 0;
        //指针2
        int end = 0;
        //指针3
        TrieNode tempNode = rootNode;
        //结果
        StringBuilder res = new StringBuilder();

        while (begin < text.length()) {
            Character c = text.charAt(end);

            //跳过特殊字符
            if (isSymbol(c)) {
               //如果指针3指向根节点，将符号加入到文本中
                if (tempNode == rootNode) {
                    res.append(c);
                    ++begin;
                }
                //无论符号在开头还是在中间，end都向下走一步
                end++;
                continue;
            }

            //检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                //以begin开头的字符串不是敏感词
                res.append(text.charAt(begin));
                //重新指向根节点
                tempNode = rootNode;
                //进入下一个位置
                end = ++begin;
            }else if (tempNode.isKeywordEnd()) {
                //以begin开头的字符串是敏感词
                res.append(REPLACEMENT);
                //指向根节点
                tempNode = rootNode;
                //进入下一个位置
                begin = ++end;
            }else {
                //指针2指向下一个字符
                end++;
            }
        }
        return res.toString();
    }

    //判断是否为符号
    private boolean isSymbol(Character c) {
        //0x2E80~0x9FFF为东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    /**
     * 前缀树
     */
    private class TrieNode {

        //关键词结束标识
        private boolean isKeywordEnd = false;

        //子节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        // 添加子节点
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }
    }
}
