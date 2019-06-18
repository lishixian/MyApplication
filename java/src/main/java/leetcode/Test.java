package leetcode;

import java.util.Arrays;
import java.util.HashMap;

public class Test {

    public static void main(String[] args) {
        twoSum();
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
