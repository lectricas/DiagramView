package com.polusov;

import java.util.Stack;
import org.junit.*;

public class LeetcodeTest {

    @Test
    public void testLeet() {

        String a = "(((";

        Stack<Character> stack = new Stack<>();

        char[] arr = a.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == '{' || arr[i] == '[' || arr[i] == '(') {
                stack.push(arr[i]);
            } else if (arr[i] == ']') {
                if (!stack.empty() && stack.peek() == '[') {
                    stack.pop();
                } else {
                    throw new IllegalArgumentException("incorrect");
                }
            } else if (arr[i] == ')') {
                if (!stack.empty() && stack.peek() == '(') {
                    stack.pop();
                } else {
                    throw new IllegalArgumentException("incorrect");
                }
            } else if (!stack.empty() && arr[i] == '}') {
                if (stack.peek() == '{') {
                    stack.pop();
                } else {
                    throw new IllegalArgumentException("incorrect");
                }
            } else {
                throw new IllegalArgumentException("wrong character");
            }
        }

        System.out.println("Correct");
    }
}
