package leetcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Test {

    public static void main(String[] args) {
        //twoSum();
        lengthOfLongestSubstring();
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
