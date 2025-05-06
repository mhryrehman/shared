package com.org.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaIntegratorApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaIntegratorApiApplication.class, args);
		twoSum(new int[]{3, 3}, 6);
	}
	public static int[] twoSum(int[] nums, int target) {

		int[] result = new int[2];

		for(int i=0; i<nums.length; i++){

			for(int j=1; j<nums.length; j++){
				if(nums[i] + nums[j] == target){
					result[0]=i;
					result[1]=j;
					break;
				}
			}

		}
		return result;
	}
}
