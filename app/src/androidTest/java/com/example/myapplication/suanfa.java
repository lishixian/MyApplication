package com.example.myapplication;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * 排序算法的一些例子
 */
public class suanfa {
    public static void main(String args[]) {
        int[] a = {3, 5, 9, 3, 6, 4, 8, 5, 1, 4, 9, 3, 5, 7, 2, 8, 45, 4, 93, 54, 678, 5};
        //System.out.println("---");
        //testQ();
        //testM();
        //testX();
        //testC(a);
        sort(a);
        System.out.println(Arrays.toString(a));
        //testBianli();
        //testStack();
        //testQueue();
        //teststack();
        //testTwoSum();
        //testnm();
    }


    /**
     *0,1,...,n-1这n个数字排成一个圆圈，从数字0开始每次从这个圆圈里删除第m个数字。
     * 求这个圆圈里剩下的最后一个数字。
     *
     */
    public static void testnm(){
        String s;
        int n = 5;
        int m = 3;
        int j = testnm2(n,m);
        System.out.println(j);
        System.out.println(lastRemaining(n,m));
    }

    public static int testnm2(int n,int m){
        if(n < 1 || m < 1){
            return -1;
        }
        if(n == 1){
            return 0;
        }
        return (testnm2(n-1,m) + m)%n;
    }

    public static int lastRemaining(int n, int m){
        if(n < 1 || m < 1){
            return -1;
        }
        int last = 0;
        for(int i = 2; i <= n; i++){
            last = (last + m) % i;
        }
        return last;
    }

/**
     * 输入数字numbers= { 2，7，11，15 }，目标= 9输出：index1 = 1，index2= 2
     */
    public static void testTwoSum() {
        int[] arr = {2, 7, 11, 15};
        int target = 17;
        testts(arr,target);
    }

    public static void testts(int[] arr, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < arr.length; i++) {
            if (map.containsKey(arr[i])) {
                System.out.println("index1 = " + (map.get(arr[i]) + 1) + ",index2=" + (i + 1));
            } else {
                map.put(target - arr[i], i);
            }
        }
    }

    public static void teststack() {
        MyStack stack1 = new MyStack();
        stack1.push(3);
        System.out.println(stack1.getMin());
        stack1.push(4);
        System.out.println(stack1.getMin());
        stack1.push(1);
        System.out.println(stack1.getMin());
        System.out.println(stack1.pop());
        System.out.println(stack1.getMin());
        System.out.println(stack1.pop());
        System.out.println(stack1.getMin());


        System.out.println("=============");
    }

    /**
     * 实现一个特殊的栈，在实现栈的基本功能的基础上，在实现返回栈中最小元素的操作。
     * pop、push、getMin操作的时间复杂度都是O(1)
     * 设计的栈类型可以使用现成的栈结构
     */
    public static class MyStack {
        private Stack<Integer> stack;
        private Stack<Integer> minStack;

        public MyStack() {
            stack = new Stack<Integer>();
            minStack = new Stack<Integer>();
        }

        public Integer pop() {
            int i = stack.pop();
            if (i == getMin()) {
                return minStack.pop();
            }
            return i;
        }

        public void push(int i) {
            stack.push(i);
            if (minStack.isEmpty()) {
                minStack.push(i);
            } else {
                int min = getMin();
                if (i < min) {
                    minStack.push(i);
                }
            }
        }

        public Integer getMin() {
            if (minStack.isEmpty()) {
                throw new RuntimeException("minStack is null");
            }
            return minStack.peek();
        }
    }

    public static void testQueue() {
        myQueue mQueue = new myQueue();
        mQueue.add(1);
        mQueue.add(2);
        mQueue.add(3);
        System.out.println(mQueue.peek());
        System.out.println(mQueue.poll());
        System.out.println(mQueue.peek());
        System.out.println(mQueue.poll());
        System.out.println(mQueue.peek());
        System.out.println(mQueue.poll());
    }

    /**
     * 编写一个类，用两个栈实现队列，支持队列的基本操作(add、poll、peek)。
     *
     * @author dream
     */
    public static class myQueue {

        private Stack<Integer> stack1;
        private Stack<Integer> stack2;

        public myQueue() {
            stack1 = new Stack<Integer>();
            stack2 = new Stack<Integer>();
        }

        /**
         * add只负责往stack1里面添加数据
         *
         * @param newNum
         */
        public void add(Integer newNum) {
            stack1.push(newNum);
        }

        /**
         * 这里要注意两点：
         * 1.stack1要一次性压入stack2
         * 2.stack2不为空，stack1绝不能向stack2压入数据
         *
         * @return
         */
        public Integer poll() {
            if (stack1.isEmpty() && stack2.isEmpty()) {
                throw new RuntimeException("Queue is Empty");
            } else if (stack2.isEmpty()) {
                while (!stack1.isEmpty()) {
                    stack2.push(stack1.pop());
                }
            }
            return stack2.pop();
        }

        public Integer peek() {
            if (stack1.isEmpty() && stack2.isEmpty()) {
                throw new RuntimeException("Queue is Empty");
            } else if (stack2.isEmpty()) {
                while (!stack1.isEmpty()) {
                    stack2.push(stack1.pop());
                }
            }
            return stack2.peek();
        }
    }

    /**
     * 仅用递归函数和栈操作逆序一个栈
     */
    public static void testStack() {
        Stack<Integer> test = new Stack<Integer>();
        test.push(1);
        test.push(2);
        test.push(3);
        test.push(4);
        test.push(5);
        reverse(test);
        while (!test.isEmpty()) {
            System.out.println(test.pop());
        }
    }

    public static void reverse(Stack<Integer> stack) {
        if (stack.isEmpty()) {
            return;
        }
        int i = stack.pop();
        reverse(stack);
        stack.push(i);
    }


    /**
     * 构建二叉树
     * 前序遍历序列 {1,2,4,7,3,5,6,8}和中序遍历序列{4,7,2,1,5,3,8,6}重建出二叉 树
     */
    public static void testBianli() {
        int[] preorder = {1, 2, 4, 7, 3, 5, 6, 8};
        int[] inorder = {4, 7, 2, 1, 5, 3, 8, 6};
        try {
            constructCore(preorder, inorder);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static BinaryTreeNode constructCore(int[] preorder, int[] inorder) throws Exception {
        if (preorder == null || inorder == null) {
            return null;
        }
        if (preorder.length != inorder.length) {
            throw new Exception("长度不一样，非法的输入");
        }
        BinaryTreeNode root = new BinaryTreeNode();
        int length = inorder.length;
        for (int i = 0; i < length; i++) {
            if (inorder[i] == preorder[0]) {
                root.value = inorder[i];
                System.out.println(root.value);
                root.left = constructCore(Arrays.copyOfRange(preorder, 1, i + 1),
                        Arrays.copyOfRange(inorder, 0, i));
                root.right = constructCore(Arrays.copyOfRange(preorder, i + 1, preorder.length),
                        Arrays.copyOfRange(inorder, i + 1, inorder.length));
            }
        }
        return root;
    }

    public static class BinaryTreeNode {
        public static int value;
        public BinaryTreeNode left;
        public BinaryTreeNode right;
    }

    /**
     * 二叉树遍历
     * @param node
     */
    public static void preOrder(BinaryTreeNode node){
        if(null != node){
            System.out.println(node.value);
            preOrder(node.left);
            preOrder(node.right);
        }
    }

    /**
     * 非递归方式遍历
     * @param node
     */
    public static void preOrder2(BinaryTreeNode node){
        Stack<BinaryTreeNode> stack=new Stack<BinaryTreeNode>();
        while(node != null || !stack.isEmpty()){
            while(node != null){
                System.out.println(node.value);
                stack.push(node);
                node = node.left;
            }
            if(!stack.isEmpty()){
                node = stack.pop().right;
            }
        }
    }




    /**
     * 插入排序：
     * 一个个来，小的往前放，大的往后放
     */
    public static void testC(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            for (int j = i; j > 0; j--) {
                if (arr[j] < arr[j - 1]) {
                    swap(arr, j, j - 1);
                }
            }
        }
    }

    /**
     * 选择排序：
     * 两两比较，找到最小的数的下标，与开头的数交换，放到开头
     */
    public static void testX() {
        int[] a = {3, 5, 9, 3, 6, 4, 8, 5, 1, 4, 9, 3, 5, 7, 2, 8, 45, 4, 93, 54, 678, 5};
        for (int i = 0, min = 0; i < a.length; i++, min = i) {
            for (int j = i + 1; j < a.length; j++) {
                if (a[j] < a[min]) {
                    min = j;
                }
            }
            swap(a, i, min);
        }
        System.out.println(Arrays.toString(a));
    }


    /**
     * 测试冒泡排序：
     * 两两比较，找到最大的数，排到最后
     */
    public static void testM() {
        int[] a = {3, 5, 9, 3, 6, 4, 8, 5, 1, 4, 9, 3, 5, 7, 2, 8, 45, 4, 93, 54, 678, 5};
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length - i - 1; j++) {
                if (a[j] > a[j + 1]) {
                    swap(a, j, j + 1);
                }
            }

        }
        System.out.println(Arrays.toString(a));
    }

    public static void swap(int[] a, int i, int j) {
        int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }


    /**
     * 测试快速排序
     */
    public static void testQ() {
        int a[] = {3, 5, 9, 3, 6, 4, 8, 5, 1, 4, 9, 3, 5, 7, 2, 8, 45, 4, 93, 54, 678, 5};
        quick(a, 0, a.length - 1);
        System.out.println(Arrays.toString(a));
    }


    public static void quick(int[] array, int low, int hight) {
        int i = low;
        int j = hight;
        if (i >= j) {
            System.out.println("------return--------");
            return;
        }
        int key = array[low];

        while (i < j) {
            /*按j--方向遍历目标数组，直到比key小的值为止*/
            while (i < j && array[j] >= key) {
                j--;
            }
            if (i < j) {
                /*targetArr[i]已经保存在key中，可将后面的数填入*/
                array[i] = array[j];
                i++;
            }

            /*按i++方向遍历目标数组，直到比key大的值为止*/
            while (i < j && array[i] <= key) {
                i++;
            }
            if (i < j) {
                /*targetArr[j]已保存在targetArr[i]中，可将前面的值填入*/
                array[j] = array[i];
                j--;
            }
        }

        array[i] = key;
        System.out.println("--------------i=" + i + ",j=" + j);
        //System.out.println(Arrays.toString(array));
        quick(array, low, i - 1);
        quick(array, j + 1, hight);

    }

    /**
     * 归并排序
     * @param arr
     */
    public static void  sort(int []arr){
        int []temp = new int[arr.length];//在排序前，先建好一个长度等于原数组长度的临时数组，避免递归中频繁开辟空间
        sort(arr,0,arr.length-1,temp);
    }
    private static void sort(int[] arr,int left,int right,int []temp){
        if(left<right){
            int mid = (left+right)/2;
            sort(arr,left,mid,temp);//左边归并排序，使得左子序列有序
            sort(arr,mid+1,right,temp);//右边归并排序，使得右子序列有序
            merge(arr,left,mid,right,temp);//将两个有序子数组合并操作
        }
    }
    private static void merge(int[] arr,int left,int mid,int right,int[] temp){
        int i = left;//左序列指针
        int j = mid+1;//右序列指针
        int t = 0;//临时数组指针
        while (i<=mid && j<=right){
            if(arr[i]<=arr[j]){
                temp[t++] = arr[i++];
            }else {
                temp[t++] = arr[j++];
            }
        }
        while(i<=mid){//将左边剩余元素填充进temp中
            temp[t++] = arr[i++];
        }
        while(j<=right){//将右序列剩余元素填充进temp中
            temp[t++] = arr[j++];
        }
        t = 0;
        //将temp中的元素全部拷贝到原数组中
        while(left <= right){
            arr[left++] = temp[t++];
        }
    }
}
