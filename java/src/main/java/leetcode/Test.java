package leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Test {

    public static void main(String[] args) {
        //twoSum();
        //lengthOfLongestSubstring();
        //ThreeThread();
        //addTwoNumbers();
        //longestPalindrome();
        //convert("sdfsdfasdf",2);
        //testNode();
        //testF(6);
    }


    /**
     * 二叉树的后序遍历 - 非递归版java实现
     * @param root
     * @return
     */
    public ArrayList<Integer> postorderTraversal(TreeNode root) {
        Stack<TreeNode> stack = new Stack<TreeNode>();
        TreeNode cur = root;
        TreeNode prior = null;
        ArrayList<Integer> ans = new ArrayList<Integer>(20);
        while (cur != null || !stack.isEmpty()) {
            if (cur != null) {
                stack.push(cur);
                cur = cur.left;
            } else {
                cur = stack.pop();
                if (cur.right == null || cur.right == prior) {
                    ans.add(cur.val);
                    prior = cur;
                    cur = null;
                }
                else {
                    stack.push(cur);
                    cur = cur.right;
                    stack.push(cur);
                    cur = cur.left;
                }
            }
        }
        return ans;
    }

    /**
     * 二叉树的中序遍历 - 非递归版java实现
     * @param root
     * @return
     */
    public ArrayList<Integer> postorderTraversal2(TreeNode root) {
        Stack<TreeNode> stack = new Stack<TreeNode>();
        TreeNode cur = root;
        ArrayList<Integer> ans = new ArrayList<Integer>(20);
        while (cur != null || !stack.isEmpty()) {
            if (cur != null) {
                stack.push(cur);//前序
                cur = cur.left;
            } else {
                cur = stack.pop();
                ans.add(cur.val);// 中序
                cur = cur.right;
            }
        }
        return ans;
    }




    /**
     * 斐波那契数列 1, 1, 2, 3, 5
     * @param n
     * @return
     */
    public static int testF(int n){
        if (n<=2){
            return 1;
        }
        return testF(n-1) + testF(n-2);
    }

    public static void testNode(){
        ListNode node = new ListNode(0);
        ListNode node1 = new ListNode(1);
        ListNode node2 = new ListNode(2);
        ListNode node3 = new ListNode(3);
        node.next = node1;
        node1.next = node2;
        node2.next = node3;
        printNode(node);
        printNode(reverse3(node));
    }


    public static ListNode reverse3(ListNode node) {
        if (node == null || node.next == null) return node;
        ListNode temp = node.next;
        ListNode newHead = reverse(node.next);
        temp.next = node;
        node.next = null;
        return newHead;
    }

    public static ListNode reverse2(ListNode node) {
        if (node == null) return null;
        ListNode pre = null;
        ListNode next = null;
        while (node != null) {
            next = node.next;
            node.next = pre;
            pre = node;
            node = next;
        }
        return pre;
    }

    public static ListNode reverse(ListNode node){
        Stack stack = new Stack();
        ListNode prenode = node;
        while(node != null){
            stack.push(node.val);
            node = node.next;
        }
        ListNode n = new ListNode();
        ListNode result = n;
        while(!stack.isEmpty()){
            n.val = (int)stack.pop();
            ListNode newNode = new ListNode();
            n.next = newNode;
            n = newNode;
        }
        return result;
    }

    public static void printNode(ListNode node){
        if(node == null){
            return;
        }
        System.out.print("node{");
        while(node != null){
            System.out.print(node.val + ",");
            node = node.next;
        }
        System.out.print("}");
    }




    /**
     * N字形展示字符串
     *
     * @param s
     * @param numRows
     * @return
     */
    public static String convert(String s, int numRows) {
        if(numRows==1) return s;
        List<StringBuilder> rows= new ArrayList<>();
        for(int i =0;i<numRows;i++){
            rows.add(new StringBuilder());// 有rows列
        }
        int len = s.length();
        int start = 0,num=0,add = 1;
        while(start<len){
            rows.get(num).append(s.charAt(start++));
            if(num==numRows-1){
                add=-1;
            }else if(num==0){
                add = 1;
            }
            num +=add;
        }
        StringBuilder ret = new StringBuilder();
        for(StringBuilder row : rows){
            ret.append(row);
        }
        return ret.toString();
    }

    /** 找到最长的对称字符串
     * Given a string s, find the longest palindromic substring in s. You may assume that the maximum length of s is 1000.
     *
     * Example 1:
     *
     * Input: "babad"
     * Output: "bab"
     * Note: "aba" is also a valid answer.
     * Example 2:
     *
     * Input: "cbbd"
     * Output: "bb"
     */
    public static void longestPalindrome() {
        String str = "afsdfasdfdsamasdfadsfadfa";
        System.out.println("" + longestPalindrome(str));
    }

    public static String longestPalindrome(String s) {
        if(s==null||s.length()<1) return "";
        int len = s.length(),start=0,end=0;
        String str = "";
        for(int i = 0;i<len;i++){
            int len1 = findMaxLen(s,i,i);
            int len2 = findMaxLen(s,i,i+1);
            int maxLen = Math.max(len1,len2);
            if(maxLen > end-start){
                start = i-(maxLen-1)/2;//这里无论奇数偶数都适用
                end = i+maxLen/2;
            }
        }
        return  s.substring(start,end+1);
    }

    public static int findMaxLen(String s,int left,int right){
        while(left >=0&&right<s.length()&&s.charAt(left) == s.charAt(right)){
            left--;
            right++;
        }
        return right-left-1;// 这里由于都向外扩展了，所以要-1
    }

    /**
     * Example:
     *
     * Input: (2 -> 4 -> 3) + (5 -> 6 -> 4)
     * Output: 7 -> 0 -> 8
     * Explanation: 342 + 465 = 807.
     */
    public static void addTwoNumbers() {

    }
    public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dum = new ListNode(0);
        ListNode p = l1,q=l2,result = dum;
        int sum = 0,remainder=0,carry = 0;
        while(p != null && q != null){
            int x =  p.val;
            int y =  q.val;
            sum = x+y+carry;
            carry = sum / 10;
            result.next = new ListNode(sum % 10);
            result = result.next;
            p = p.next;
            q = q.next;
        }
        while(p!=null){
            sum = p.val + carry;
            result.next = new ListNode(sum % 10);
            carry = sum/10;
            result = result.next;
            p = p.next;
        }
        while(q!=null){
            sum = q.val + carry;
            result.next = new ListNode(sum % 10);
            carry = sum/10;
            result = result.next;
            q = q.next;
        }
        if(carry > 0){
            result.next = new ListNode(carry);
        }
        return dum.next;
    }

    /**
     * 三个线程依次打印 1a，2b，3c，1d。。。
     *
     */
    public static void ThreeThread() {

        MyThread t1 = new MyThread(1);
        MyThread t2 = new MyThread(2);
        MyThread t3 = new MyThread(3);
        t1.start();
        t2.start();
        t3.start();
    }

    private static class MyThread extends Thread {
        private int index;

        private static Object object = new Object();
        private static int count = 0;

        private static String str = "abcdefghijklmn";
        private static char[] chars = str.toCharArray();

        MyThread(int index) {
            this.index = index;
        }

        public void run() {
            synchronized (object) {
                while (count < chars.length) {
                    object.notifyAll();// 让多个线程竞争锁，符合条件的会打印，不符合的释放锁再去竞争
                    if (count % 3 == index - 1) {
                        System.out.println("Thread " + index + "  :" + index + chars[count]);
                        count += 1;
                    }
                    try {
                        object.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.exit(0);
            }
        }
    }


    /**
     * Given a string, find the length of the longest substring without repeating characters.
     * <p>
     * Example 1:
     * <p>
     * Input: "abcabcbb"
     * Output: 3
     * Explanation: The answer is "abc", with the length of 3.
     * Example 2:
     * <p>
     * Input: "bbbbb"
     * Output: 1
     * Explanation: The answer is "b", with the length of 1.
     * Example 3:
     * <p>
     * Input: "pwwkew"
     * Output: 3
     * Explanation: The answer is "wke", with the length of 3.
     * Note that the answer must be a substring, "pwke" is a subsequence and not a substring.
     */
    public static void lengthOfLongestSubstring() {
        String str = "abcabcbb";
        lengthOfLongestSubstring(str);
    }

    public static int lengthOfLongestSubstring(String s) {
        int start = 0, max = 0;
        String str = "";
        Map<Character, Integer> map = new HashMap<>();

        for (int i = 0, length = s.length(); i < length; i++) {
            char ch = s.charAt(i);
            if (map.containsKey(ch)) {
                start = Math.max(map.get(ch) + 1, start);
            }
            if (max < i - start + 1) {
                max = i - start + 1;
                str = s.substring(start, i + 1);
            }
            map.put(ch, i);
        }
        System.out.println("max:" + max + ",str:" + str);
        return max;
    }

    /**
     * Given an array of integers, return indices of the two numbers such that they add up to a specific target.
     * You may assume that each input would have exactly one solution, and you may not use the same element twice.
     * Example:
     * Given nums = [2, 7, 11, 15], target = 9,
     * Because nums[0] + nums[1] = 2 + 7 = 9,
     * return [0, 1].
     */
    public static void twoSum() {
        int[] nums = {2, 7, 11, 15};
        int target = 9;
        int[] result = twoSum(nums, target);
        System.out.println(Arrays.toString(result));
    }

    public static int[] twoSum(int[] nums, int target) {
        int[] result = {0, 0};
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0, length = nums.length; i < length; i++) {
            int value = nums[i];
            if (map.containsKey(target - value)) {
                result[0] = map.get(target - value);
                result[1] = i;
                return result;
            }
            map.put(value, i);
        }
        return result;
    }

}
